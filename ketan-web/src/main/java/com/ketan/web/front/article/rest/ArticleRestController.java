package com.ketan.web.front.article.rest;

import com.ketan.api.model.context.ReqInfoContext;
import com.ketan.api.model.enums.DocumentTypeEnum;
import com.ketan.api.model.enums.NotifyTypeEnum;
import com.ketan.api.model.enums.OperateTypeEnum;
import com.ketan.api.model.vo.PageParam;
import com.ketan.api.model.vo.PageVo;
import com.ketan.api.model.vo.ResVo;
import com.ketan.api.model.vo.article.ArticlePostReq;
import com.ketan.api.model.vo.article.ContentPostReq;
import com.ketan.api.model.vo.article.dto.TagDTO;
import com.ketan.api.model.vo.constants.StatusEnum;
import com.ketan.api.model.vo.notify.NotifyMsgEvent;
import com.ketan.core.common.CommonConstants;
import com.ketan.core.mdc.MdcDot;
import com.ketan.core.permission.Permission;
import com.ketan.core.permission.UserRole;
import com.ketan.core.util.JsonUtil;
import com.ketan.core.util.SpringUtil;
import com.ketan.service.article.repository.entity.ArticleDO;
import com.ketan.service.article.service.ArticleReadService;
import com.ketan.service.article.service.ArticleWriteService;
import com.ketan.service.article.service.TagService;
import com.ketan.service.notify.service.RabbitmqService;
import com.ketan.service.user.repository.entity.UserFootDO;
import com.ketan.service.user.service.UserFootService;
import com.rabbitmq.client.BuiltinExchangeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

@Slf4j
@RequestMapping(path = "article/api")
@RestController
public class ArticleRestController {


    @Autowired
    private ArticleReadService articleReadService;

    @Autowired
    private ArticleReadService articleService;

    @Autowired
    private ArticleWriteService articleWriteService;

    @Autowired
    private UserFootService userFootService;

    @Autowired
    private RabbitmqService rabbitmqService;

    @Autowired
    private TagService tagService;

    /**
     * 收藏、点赞等相关操作
     *
     * @param articleId
     * @param type      取值来自于 OperateTypeEnum#code
     * @return
     */
    @Permission(role = UserRole.LOGIN)
    @GetMapping(path = "favor")
    @MdcDot(bizCode = "#articleId")
    public ResVo<Boolean> favor(@RequestParam(name = "articleId") Long articleId,
                                @RequestParam(name = "type") Integer type) throws IOException, TimeoutException {
        if (log.isDebugEnabled()) {
            log.debug("开始点赞: {}", type);
        }
        OperateTypeEnum operate = OperateTypeEnum.fromCode(type);
        if (operate == OperateTypeEnum.EMPTY) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, type + "非法");
        }

        // 要求文章必须存在
        ArticleDO article = articleReadService.queryBasicArticle(articleId);
        if (article == null) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "文章不存在!");
        }

        UserFootDO foot = userFootService.saveOrUpdateUserFoot(DocumentTypeEnum.ARTICLE, articleId, article.getUserId(),
                ReqInfoContext.getReqInfo().getUserId(),
                operate);
        // 点赞、收藏消息
        NotifyTypeEnum notifyType = OperateTypeEnum.getNotifyType(operate);

        // 点赞消息走 RabbitMQ，其它走 Java 内置消息机制
        if (notifyType.equals(NotifyTypeEnum.PRAISE) && rabbitmqService.enabled()) {
            rabbitmqService.publishMsg(
                    CommonConstants.EXCHANGE_NAME_DIRECT,
                    BuiltinExchangeType.DIRECT,
                    CommonConstants.QUERE_KEY_PRAISE,
                    JsonUtil.toStr(foot));
        } else {
            Optional.ofNullable(notifyType).ifPresent(notify -> SpringUtil.publishEvent(new NotifyMsgEvent<>(this, notify, foot)));
        }

        if (log.isDebugEnabled()) {
            log.info("点赞结束: {}", type);
        }
        return ResVo.ok(true);
    }

    /**
     * 查询所有的标签
     *
     * @return
     */
    @GetMapping(path = "tag/list")
    public ResVo<PageVo<TagDTO>> queryTags(@RequestParam(name = "key", required = false) String key,
                                           @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                           @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageVo<TagDTO> tagDTOPageVo = tagService.queryTags(key, PageParam.newPageInstance(pageNumber, pageSize));
        return ResVo.ok(tagDTOPageVo);
    }

    /**
     * 文章摘要生成
     *
     * @return
     */
    @PostMapping(path = "generateSummary")
    public ResVo<String> generateSummary(@RequestBody ContentPostReq req) {
        return ResVo.ok(articleService.generateSummary(req.getContent()));
    }

    /**
     * 发布文章，完成后跳转到详情页
     * - 这里有一个重定向的知识点
     *
     * @return
     */
    @Permission(role = UserRole.LOGIN)
    @PostMapping(path = "post")
    @MdcDot(bizCode = "#req.articleId")
    public ResVo<Long> post(@RequestBody ArticlePostReq req, HttpServletResponse response) throws IOException {
        Long id = articleWriteService.saveArticle(req, ReqInfoContext.getReqInfo().getUserId());
        // 如果使用后端重定向，可以使用下面两种策略
//        return "redirect:/article/detail/" + id;
//        response.sendRedirect("/article/detail/" + id);
        // 这里采用前端重定向策略
        return ResVo.ok(id);
    }


}
