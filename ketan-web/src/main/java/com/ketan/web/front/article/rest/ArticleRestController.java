package com.ketan.web.front.article.rest;

import com.ketan.api.model.context.ReqInfoContext;
import com.ketan.api.model.enums.DocumentTypeEnum;
import com.ketan.api.model.enums.OperateTypeEnum;
import com.ketan.api.model.vo.PageParam;
import com.ketan.api.model.vo.PageVo;
import com.ketan.api.model.vo.ResVo;
import com.ketan.api.model.vo.article.ArticlePostReq;
import com.ketan.api.model.vo.article.ContentPostReq;
import com.ketan.api.model.vo.article.dto.TagDTO;
import com.ketan.api.model.vo.constants.StatusEnum;
import com.ketan.core.mdc.MdcDot;
import com.ketan.core.permission.Permission;
import com.ketan.core.permission.UserRole;
import com.ketan.service.article.repository.entity.ArticleDO;
import com.ketan.service.article.service.ArticleReadService;
import com.ketan.service.article.service.ArticleWriteService;
import com.ketan.service.article.service.TagService;
import com.ketan.service.notify.service.RabbitmqService;
import com.ketan.service.user.service.UserFootService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        OperateTypeEnum operate = OperateTypeEnum.fromCode(type);
        if (operate == OperateTypeEnum.EMPTY) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, type + "非法");
        }

        // 要求文章必须存在
        ArticleDO article = articleReadService.queryBasicArticle(articleId);
        if (article == null) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "文章不存在!");
        }

        // 更新用户与文章的点赞/收藏状态
        userFootService.favorArticleComment(DocumentTypeEnum.ARTICLE, articleId, article.getUserId(),
                ReqInfoContext.getReqInfo().getUserId(),
                operate);
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
