package com.ketan.service.notify.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ketan.api.model.context.ReqInfoContext;
import com.ketan.api.model.enums.NotifyStatEnum;
import com.ketan.api.model.enums.NotifyTypeEnum;
import com.ketan.api.model.vo.PageListVo;
import com.ketan.api.model.vo.PageParam;
import com.ketan.api.model.vo.notify.dto.NotifyMsgDTO;
import com.ketan.core.util.NumUtil;
import com.ketan.core.ws.WebSocketResponseUtil;
import com.ketan.service.notify.repository.dao.NotifyMsgDao;
import com.ketan.service.notify.repository.entity.NotifyMsgDO;
import com.ketan.service.notify.service.NotifyService;
import com.ketan.service.user.repository.entity.UserFootDO;
import com.ketan.service.user.service.UserRelationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service
public class NotifyServiceImpl implements NotifyService {

    public static String NOTIFY_TOPIC = "/msg";

    @Autowired
    private NotifyMsgDao notifyMsgDao;

    @Resource
    private UserRelationService userRelationService;

    /**
     * 记录用户与对应的jwt token之间的缓存关系；用于websocket的广播通知
     * key: 用户ID（对应数据库中唯一标识） value:存放用户ID对应的 session(此处是jwt)
     */
    private LoadingCache<Long, Set<String>> wsUserSessionCache;

    @PostConstruct
    public void init() {
        wsUserSessionCache = CacheBuilder.newBuilder()
                .maximumSize(500)
                .expireAfterAccess(1, TimeUnit.HOURS)
                .build(new CacheLoader<Long, Set<String>>() {
                    @Override
                    public Set<String> load(Long aLong) throws Exception {
                        return new HashSet<>();
                    }
                });
    }

    @Override
    public int queryUserNotifyMsgCount(Long userId) {
        return notifyMsgDao.countByUserIdAndStat(userId, NotifyStatEnum.UNREAD.getStat());
    }


    @Override
    public void saveArticleNotify(UserFootDO foot, NotifyTypeEnum notifyTypeEnum) {
        NotifyMsgDO msg = new NotifyMsgDO().setRelatedId(foot.getDocumentId())
                .setNotifyUserId(foot.getDocumentUserId())
                .setOperateUserId(foot.getUserId())
                .setType(notifyTypeEnum.getType() )
                .setState(NotifyStatEnum.UNREAD.getStat())
                .setMsg("");
        NotifyMsgDO record = notifyMsgDao.getByUserIdRelatedIdAndType(msg);
        if (record == null) {
            // 若之前已经有对应的通知，则不重复记录；因为一个用户对一篇文章，可以重复的点赞、取消点赞，但是最终我们只通知一次
            notifyMsgDao.save(msg);
        }
    }

    /**
     * 查询消息通知列表
     *
     * @return
     */
    @Override
    public PageListVo<NotifyMsgDTO> queryUserNotices(Long userId, NotifyTypeEnum type, PageParam page) {
        List<NotifyMsgDTO> list = notifyMsgDao.listNotifyMsgByUserIdAndType(userId, type, page);
        if (CollectionUtils.isEmpty(list)) {
            return PageListVo.emptyVo();
        }

        // 设置消息为已读状态
        notifyMsgDao.updateNotifyMsgToRead(list);
        // 更新全局总的消息数
        ReqInfoContext.getReqInfo().setMsgNum(queryUserNotifyMsgCount(userId));
        // 更新当前登录用户对粉丝的关注状态
        updateFollowStatus(userId, list);
        return PageListVo.newVo(list, page.getPageSize());
    }

    private void updateFollowStatus(Long userId, List<NotifyMsgDTO> list) {
        List<Long> targetUserIds = list.stream().filter(s -> s.getType() == NotifyTypeEnum.FOLLOW.getType()).map(NotifyMsgDTO::getOperateUserId).collect(Collectors.toList());
        if (targetUserIds.isEmpty()) {
            return;
        }

        // 查询userId已经关注过的用户列表；并将对应的msg设置为true，表示已经关注过了；不需要再关注
        Set<Long> followedUserIds = userRelationService.getFollowedUserId(targetUserIds, userId);
        list.forEach(notify -> {
            if (followedUserIds.contains(notify.getOperateUserId())) {
                notify.setMsg("true");
            } else {
                notify.setMsg("false");
            }
        });
    }



    @Override
    public Map<String, Integer> queryUnreadCounts(long userId) {
        Map<Integer, Integer> map = Collections.emptyMap();
        if (ReqInfoContext.getReqInfo() != null && NumUtil.upZero(ReqInfoContext.getReqInfo().getMsgNum())) {
            map = notifyMsgDao.groupCountByUserIdAndStat(userId, NotifyStatEnum.UNREAD.getStat());
        }
        // 指定先后顺序
        Map<String, Integer> ans = new LinkedHashMap<>();
        initCnt(NotifyTypeEnum.COMMENT, map, ans);
        initCnt(NotifyTypeEnum.REPLY, map, ans);
        initCnt(NotifyTypeEnum.PRAISE, map, ans);
        initCnt(NotifyTypeEnum.COLLECT, map, ans);
        initCnt(NotifyTypeEnum.FOLLOW, map, ans);
        initCnt(NotifyTypeEnum.SYSTEM, map, ans);
        return ans;
    }

    private void initCnt(NotifyTypeEnum type, Map<Integer, Integer> map, Map<String, Integer> result) {
        result.put(type.name().toLowerCase(), map.getOrDefault(type.getType(), 0));
    }

    // -------------------------------------------- 下面是与用户的websocket长连接维护相关实现 -------------------------

    /**
     * x用户发送
     * @param userId 用户id
     * @param msg 通知内容
     */
    @Override
    public void notifyToUser(Long userId, String msg) {
        wsUserSessionCache.getUnchecked(userId).forEach(s -> {
            WebSocketResponseUtil.sendMsgToUser(s, NOTIFY_TOPIC, msg);
        });
    }

    /**
     * WebSocket通道管理
     *
     * @param accessor
     */
    @Override
    public void notifyChannelMaintain(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (StringUtils.isBlank(destination) || accessor.getCommand() == null) {
            return;
        }


        // 全局私信、通知长连接入口
        ReqInfoContext.ReqInfo user = (ReqInfoContext.ReqInfo) accessor.getUser();
        if (user == null) {
            log.info("websocket用户未登录! {}", accessor);
            return;
        }
        switch (accessor.getCommand()) {
            case SUBSCRIBE:
                // 建立用户通信通道
                addUserToken(user.getUserId(), user.getSession());
                break;
            case DISCONNECT:
                // 中断链接，去掉用户的长连接会话
                releaseUserToken(user.getUserId(), user.getSession());
                break;
        }
    }

    /**
     * 用户建立连接时，添加用户信息
     *
     * @param userId  用户id
     * @param session jwt token
     */
    private void addUserToken(Long userId, String session) {
        wsUserSessionCache.getUnchecked(userId).add(session);
    }


    /**
     * 断开连接时，移除用户信息
     *
     * @param userId  用户id
     * @param session jwt token
     */
    private void releaseUserToken(Long userId, String session) {
        wsUserSessionCache.getUnchecked(userId).remove(session);
    }
}
