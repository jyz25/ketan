package com.ketan.service.user.service.userfoot;

import com.ketan.api.model.enums.DocumentTypeEnum;
import com.ketan.api.model.enums.NotifyTypeEnum;
import com.ketan.api.model.enums.OperateTypeEnum;
import com.ketan.api.model.vo.PageParam;
import com.ketan.api.model.vo.user.dto.SimpleUserInfoDTO;
import com.ketan.core.common.CommonConstants;
import com.ketan.core.util.JsonUtil;
import com.ketan.service.comment.repository.entity.CommentDO;
import com.ketan.service.notify.help.MsgNotifyHelper;
import com.ketan.service.notify.service.RabbitmqService;
import com.ketan.service.user.repository.dao.UserFootDao;
import com.ketan.service.user.repository.entity.UserFootDO;
import com.ketan.service.user.service.UserFootService;
import com.rabbitmq.client.BuiltinExchangeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
@Slf4j
public class UserFootServiceImpl implements UserFootService {

    private final UserFootDao userFootDao;

    @Autowired
    private RabbitmqService rabbitmqService;


    public UserFootServiceImpl(UserFootDao userFootDao) {
        this.userFootDao = userFootDao;
    }

    @Override
    public List<SimpleUserInfoDTO> queryArticlePraisedUsers(Long articleId) {
        return userFootDao.listDocumentPraisedUsers(articleId, DocumentTypeEnum.ARTICLE.getCode(), 10);
    }

    @Override
    public UserFootDO queryUserFoot(Long documentId, Integer type, Long userId) {
        return userFootDao.getByDocumentAndUserId(documentId, type, userId);
    }


    /**
     * 保存或更新状态信息
     *
     * @param documentType    文档类型：博文 + 评论
     * @param documentId      文档id
     * @param authorId        作者
     * @param userId          操作人
     * @param operateTypeEnum 操作类型：点赞，评论，收藏等
     */
    @Override
    public UserFootDO saveOrUpdateUserFoot(DocumentTypeEnum documentType, Long documentId, Long authorId, Long userId, OperateTypeEnum operateTypeEnum) {
        // 查询是否有该足迹；有则更新，没有则插入
        UserFootDO readUserFootDO = userFootDao.getByDocumentAndUserId(documentId, documentType.getCode(), userId);
        if (readUserFootDO == null) {
            readUserFootDO = new UserFootDO();
            readUserFootDO.setUserId(userId);
            readUserFootDO.setDocumentId(documentId);
            readUserFootDO.setDocumentType(documentType.getCode());
            readUserFootDO.setDocumentUserId(authorId);
            setUserFootStat(readUserFootDO, operateTypeEnum);
            userFootDao.save(readUserFootDO);
        } else if (setUserFootStat(readUserFootDO, operateTypeEnum)) {
            readUserFootDO.setUpdateTime(new Date());
            userFootDao.updateById(readUserFootDO);
        }
        return readUserFootDO;
    }


    @Override
    public List<Long> queryUserReadArticleList(Long userId, PageParam pageParam) {
        return userFootDao.listReadArticleByUserId(userId, pageParam);
    }

    @Override
    public List<Long> queryUserCollectionArticleList(Long userId, PageParam pageParam) {
        return userFootDao.listCollectedArticlesByUserId(userId, pageParam);
    }

    /**
     * 文章/评论点赞、取消点赞、收藏、取消收藏
     *
     * @param documentType    文档类型：博文 + 评论
     * @param documentId      文档id
     * @param authorId        作者
     * @param userId          操作人
     * @param operateTypeEnum 操作类型：点赞，评论，收藏等
     */
    @Override
    @Transactional
    public void favorArticleComment(DocumentTypeEnum documentType, Long documentId, Long authorId, Long userId, OperateTypeEnum operateTypeEnum) {
        // fixme 这里没有做并发控制，在大并发场景下，可能出现查询出来的数据，与db中数据不一致的场景
        // fixme 解决方案：自旋等待的分布式锁 or 事务 + 悲观锁
        // fixme 考虑到这个足迹的准确性影响并不大，留待有缘人进行修正

        // 查询是否有该足迹；有则更新，没有则插入
        log.info("事务开始，尝试获取锁: {}, userId: {}", documentId, userId);
        UserFootDO readUserFootDO = userFootDao.getByDocumentAndUserIdForUpdate(documentId, documentType.getCode(), userId);
        log.info("本事务已经获取到锁: {}, userId: {}", documentId, userId);
        boolean dbChanged = false;
        if (readUserFootDO == null) {
            readUserFootDO = new UserFootDO();
            readUserFootDO.setUserId(userId);
            readUserFootDO.setDocumentId(documentId);
            readUserFootDO.setDocumentType(documentType.getCode());
            readUserFootDO.setDocumentUserId(authorId);
            setUserFootStat(readUserFootDO, operateTypeEnum);
            userFootDao.save(readUserFootDO);
            dbChanged = true;
        } else if (setUserFootStat(readUserFootDO, operateTypeEnum)) {
            readUserFootDO.setUpdateTime(new Date());
            userFootDao.updateById(readUserFootDO);
            dbChanged = true;
        }


        if (!dbChanged) {
            log.info("事务完成，释放锁: {}, userId: {}", documentId, userId);
            // 幂等，直接返回
            return;
        }


        // 点赞、收藏两种操作时，需要发送异步消息，用于生成消息通知、更新文章/评论的相关计数统计、更新用户的活跃积分
        NotifyTypeEnum notifyType = OperateTypeEnum.getNotifyType(operateTypeEnum);
        if (notifyType == null) {
            // 不需要发送通知的场景，直接返回
            return;
        }

        // 点赞消息走 RabbitMQ，其它走 Java 内置消息机制
        if (notifyType.equals(NotifyTypeEnum.PRAISE) && rabbitmqService.enabled()) {
            rabbitmqService.publishMsg(
                    CommonConstants.EXCHANGE_NAME_DIRECT,
                    BuiltinExchangeType.DIRECT,
                    CommonConstants.QUERE_KEY_PRAISE,
                    JsonUtil.toStr(readUserFootDO));
        } else {
            MsgNotifyHelper.publish(notifyType, readUserFootDO);
        }
    }


    @Override
    public void saveCommentFoot(CommentDO comment, Long articleAuthor, Long parentCommentAuthor) {
        // 保存文章对应的评论足迹
        saveOrUpdateUserFoot(DocumentTypeEnum.ARTICLE, comment.getArticleId(), articleAuthor, comment.getUserId(), OperateTypeEnum.COMMENT);
        // 如果是子评论，则找到父评论的记录，然后设置为已评
        if (comment.getParentCommentId() != null && comment.getParentCommentId() != 0) {
            // 如果需要展示父评论的子评论数量，authorId 需要传父评论的 userId
            saveOrUpdateUserFoot(DocumentTypeEnum.COMMENT, comment.getParentCommentId(), parentCommentAuthor, comment.getUserId(), OperateTypeEnum.COMMENT);
        }
    }

    @Override
    public void removeCommentFoot(CommentDO comment, Long articleAuthor, Long parentCommentAuthor) {
        saveOrUpdateUserFoot(DocumentTypeEnum.ARTICLE, comment.getArticleId(), articleAuthor, comment.getUserId(), OperateTypeEnum.DELETE_COMMENT);
        if (comment.getParentCommentId() != null) {
            // 如果需要展示父评论的子评论数量，authorId 需要传父评论的 userId
            saveOrUpdateUserFoot(DocumentTypeEnum.COMMENT, comment.getParentCommentId(), parentCommentAuthor, comment.getUserId(), OperateTypeEnum.DELETE_COMMENT);
        }
    }


    private boolean setUserFootStat(UserFootDO userFootDO, OperateTypeEnum operate) {
        switch (operate) {
            case READ:
                // 设置为已读
                userFootDO.setReadStat(1);
                // 需要更新时间，用于浏览记录
                return true;
            case PRAISE:
            case CANCEL_PRAISE:
                return compareAndUpdate(userFootDO::getPraiseStat, userFootDO::setPraiseStat, operate.getDbStatCode());
            case COLLECTION:
            case CANCEL_COLLECTION:
                return compareAndUpdate(userFootDO::getCollectionStat, userFootDO::setCollectionStat, operate.getDbStatCode());
            case COMMENT:
            case DELETE_COMMENT:
                return compareAndUpdate(userFootDO::getCommentStat, userFootDO::setCommentStat, operate.getDbStatCode());
            default:
                return false;
        }
    }

    /**
     * 相同则直接返回false不用更新；不同则更新,返回true
     *
     * @param supplier
     * @param consumer
     * @param input
     * @param <T>
     * @return
     */
    private <T> boolean compareAndUpdate(Supplier<T> supplier, Consumer<T> consumer, T input) {
        if (Objects.equals(supplier.get(), input)) {
            return false;
        }
        consumer.accept(input);
        return true;
    }


}
