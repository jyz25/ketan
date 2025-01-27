package com.ketan.web.front.article.view;

import com.ketan.api.model.context.ReqInfoContext;
import com.ketan.api.model.vo.PageParam;
import com.ketan.api.model.vo.article.dto.ArticleDTO;
import com.ketan.api.model.vo.article.dto.ArticleOtherDTO;
import com.ketan.api.model.vo.comment.dto.TopCommentDTO;
import com.ketan.api.model.vo.recommend.SideBarDTO;
import com.ketan.api.model.vo.user.dto.UserStatisticInfoDTO;
import com.ketan.core.util.MarkdownConverter;
import com.ketan.core.util.SpringUtil;
import com.ketan.service.article.repository.entity.ColumnArticleDO;
import com.ketan.service.article.service.ArticleReadService;
import com.ketan.service.article.service.ColumnService;
import com.ketan.service.comment.service.CommentReadService;
import com.ketan.service.sidebar.service.SidebarService;
import com.ketan.service.user.service.UserService;
import com.ketan.web.front.article.vo.ArticleDetailVo;
import com.ketan.web.global.BaseViewController;
import com.ketan.web.global.SeoInjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

/**
 * 文章
 * todo: 所有的入口都放在一个Controller，会导致功能划分非常混乱
 * ： 文章列表
 * ： 文章编辑
 * ： 文章详情
 * ---
 *  - 返回视图 view
 *  - 返回json数据
 *
 * @author yihui
 */
@Controller
@RequestMapping(path = "article")
public class ArticleViewController extends BaseViewController {


    @Autowired
    private ArticleReadService articleService;

    @Autowired
    private SidebarService sidebarService;

    @Autowired
    private ColumnService columnService;

    @Autowired
    private CommentReadService commentService;

    @Autowired
    private UserService userService;

    /**
     * 文章详情页
     * - 参数解析知识点
     * - fixme * [1.Get请求参数解析姿势汇总 | 一灰灰Learning](https://hhui.top/spring-web/01.request/01.190824-springboot%E7%B3%BB%E5%88%97%E6%95%99%E7%A8%8Bweb%E7%AF%87%E4%B9%8Bget%E8%AF%B7%E6%B1%82%E5%8F%82%E6%95%B0%E8%A7%A3%E6%9E%90%E5%A7%BF%E5%8A%BF%E6%B1%87%E6%80%BB/)
     *
     * @param articleId
     * @return
     */
    @GetMapping("detail/{articleId}")
    public String detail(@PathVariable(name = "articleId") Long articleId, Model model) throws IOException {
        // 针对专栏文章，做一个重定向
        ColumnArticleDO columnArticle = columnService.getColumnArticleRelation(articleId);
        if (columnArticle != null) {
            return String.format("redirect:/column/%d/%d", columnArticle.getColumnId(), columnArticle.getSection());
        }

        ArticleDetailVo vo = new ArticleDetailVo();
        // 文章相关信息
        ArticleDTO articleDTO = articleService.queryFullArticleInfo(articleId, ReqInfoContext.getReqInfo().getUserId());
        // 返回给前端页面时，转换为html格式
        articleDTO.setContent(MarkdownConverter.markdownToHtml(articleDTO.getContent()));
        vo.setArticle(articleDTO);

        // 评论信息
        List<TopCommentDTO> comments = commentService.getArticleComments(articleId, PageParam.newPageInstance(1L, 10L));
        vo.setComments(comments);

        // 热门评论
        TopCommentDTO hotComment = commentService.queryHotComment(articleId);
        vo.setHotComment(hotComment);

        // 其他信息封装
        ArticleOtherDTO other = new ArticleOtherDTO();
        // 作者信息
        UserStatisticInfoDTO user = userService.queryUserInfoWithStatistic(articleDTO.getAuthor());
        articleDTO.setAuthorName(user.getUserName());
        articleDTO.setAuthorAvatar(user.getPhoto());
        vo.setAuthor(user);

        vo.setOther(other);

        // 详情页的侧边推荐信息
        List<SideBarDTO> sideBars = sidebarService.queryArticleDetailSidebarList(articleDTO.getAuthor(), articleDTO.getArticleId());
        vo.setSideBarItems(sideBars);
        model.addAttribute("vo", vo);

        SpringUtil.getBean(SeoInjectService.class).initColumnSeo(vo);
        return "views/article-detail/index";
    }

}
