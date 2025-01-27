package com.ketan.web.front.comment.rest;

import com.ketan.api.model.context.ReqInfoContext;
import com.ketan.api.model.enums.DocumentTypeEnum;
import com.ketan.api.model.enums.NotifyTypeEnum;
import com.ketan.api.model.enums.OperateTypeEnum;
import com.ketan.api.model.vo.PageParam;
import com.ketan.api.model.vo.ResVo;
import com.ketan.api.model.vo.comment.CommentSaveReq;
import com.ketan.api.model.vo.comment.dto.TopCommentDTO;
import com.ketan.api.model.vo.constants.StatusEnum;
import com.ketan.api.model.vo.notify.NotifyMsgEvent;
import com.ketan.core.permission.Permission;
import com.ketan.core.permission.UserRole;
import com.ketan.core.util.NumUtil;
import com.ketan.core.util.SpringUtil;
import com.ketan.service.article.conveter.ArticleConverter;
import com.ketan.service.article.repository.entity.ArticleDO;
import com.ketan.service.article.service.ArticleReadService;
import com.ketan.service.comment.repository.entity.CommentDO;
import com.ketan.service.comment.service.CommentReadService;
import com.ketan.service.comment.service.CommentWriteService;
import com.ketan.service.user.repository.entity.UserFootDO;
import com.ketan.service.user.service.UserFootService;
import com.ketan.web.component.TemplateEngineHelper;
import com.ketan.web.front.article.vo.ArticleDetailVo;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 评论
 *
 * @author louzai
 * @date : 2022/4/22 10:56
 **/
@RestController
@RequestMapping(path = "comment/api")
public class CommentRestController {
    @Autowired
    private ArticleReadService articleReadService;

    @Autowired
    private CommentReadService commentReadService;

    @Autowired
    private CommentWriteService commentWriteService;

    @Autowired
    private UserFootService userFootService;

    @Autowired
    private TemplateEngineHelper templateEngineHelper;


    /**
     * 保存评论
     *
     * @param req
     * @return
     */
    @Permission(role = UserRole.LOGIN)
    @PostMapping(path = "post")
    @ResponseBody
    public ResVo<String> save(@RequestBody CommentSaveReq req) {
        if (req.getArticleId() == null) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "文章id为空");
        }
        ArticleDO article = articleReadService.queryBasicArticle(req.getArticleId());
        if (article == null) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "文章不存在!");
        }

        // 保存评论
        req.setUserId(ReqInfoContext.getReqInfo().getUserId());
        /**
         *
         *StringEscapeUtils.escapeHtml3() 是 Apache Commons Lang 库中的一个工具方法，
         *用于将字符串中的 HTML 特殊字符转换为 HTML 实体编码。
         *这主要用于防止 HTML 注入攻击，确保用户输入的内容在网页上安全地显示。
         */
        req.setCommentContent(StringEscapeUtils.escapeHtml3(req.getCommentContent()));
        commentWriteService.saveComment(req);

        // 返回新的评论信息，用于实时更新详情也的评论列表
        ArticleDetailVo vo = new ArticleDetailVo();
        vo.setArticle(ArticleConverter.toDto(article));
        // 评论信息
        List<TopCommentDTO> comments = commentReadService.getArticleComments(req.getArticleId(), PageParam.newPageInstance());
        vo.setComments(comments);

        // 热门评论
        TopCommentDTO hotComment = commentReadService.queryHotComment(req.getArticleId());
        vo.setHotComment(hotComment);
        String content = templateEngineHelper.render("views/article-detail/comment/index", vo);
        return ResVo.ok(content);
    }


    /**
     * 收藏、点赞等相关操作
     *
     * @param commendId
     * @param type      取值来自于 OperateTypeEnum#code
     * @return
     */
    @Permission(role = UserRole.LOGIN)
    @GetMapping(path = "favor")
    public ResVo<Boolean> favor(@RequestParam(name = "commentId") Long commendId,
                                @RequestParam(name = "type") Integer type) {
        OperateTypeEnum operate = OperateTypeEnum.fromCode(type);
        if (operate == OperateTypeEnum.EMPTY) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, type + "非法");
        }

        // 要求文章必须存在
        CommentDO comment = commentReadService.queryComment(commendId);
        if (comment == null) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "评论不存在!");
        }

        UserFootDO foot = userFootService.saveOrUpdateUserFoot(DocumentTypeEnum.COMMENT,
                commendId,
                comment.getUserId(),
                ReqInfoContext.getReqInfo().getUserId(),
                operate);
        // 点赞、收藏消息
        NotifyTypeEnum notifyType = OperateTypeEnum.getNotifyType(operate);
        Optional.ofNullable(notifyType).ifPresent(notify -> SpringUtil.publishEvent(new NotifyMsgEvent<>(this, notify, foot)));
        return ResVo.ok(true);
    }

}
