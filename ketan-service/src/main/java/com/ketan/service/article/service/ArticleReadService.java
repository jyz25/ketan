package com.ketan.service.article.service;

import com.ketan.api.model.vo.PageListVo;
import com.ketan.api.model.vo.PageParam;
import com.ketan.api.model.vo.article.dto.ArticleDTO;
import com.ketan.api.model.vo.article.dto.SimpleArticleDTO;
import com.ketan.service.article.repository.entity.ArticleDO;

import java.util.List;
import java.util.Map;

public interface ArticleReadService {

    /**
     * 查询基础的文章信息
     *
     * @param articleId
     * @return
     */
    ArticleDO queryBasicArticle(Long articleId);


    /**
     * 查询文章详情，包括正文内容，分类、标签等信息
     *
     * @param articleId
     * @return
     */
    ArticleDTO queryDetailArticleInfo(Long articleId);


    /**
     * 查询文章所有的关联信息，正文，分类，标签，阅读计数+1，当前登录用户是否点赞、评论过
     *
     * @param articleId   文章id
     * @param currentUser 当前查看的用户ID
     * @return
     */
    ArticleDTO queryFullArticleInfo(Long articleId, Long currentUser);

    // 根据分类统计文章数
    Map<Long, Long> queryArticleCountsByCategory();

    /**
     * 查询某个分类下的文章，支持翻页
     */
    PageListVo<ArticleDTO> queryArticlesByCategory(Long categoryId, PageParam page);


    /**
     * 文章实体补齐统计、作者、分类标签等信息
     *
     * @param records
     * @param pageSize
     * @return
     */
    PageListVo<ArticleDTO> buildArticleListVo(List<ArticleDO> records, long pageSize);


    /**
     * 获取 Top 文章
     *
     * @param categoryId
     * @return
     */
    List<ArticleDTO> queryTopArticlesByCategory(Long categoryId);

    /**
     * 查询热门文章
     *
     * @param pageParam
     * @return
     */
    PageListVo<SimpleArticleDTO> queryHotArticlesForRecommend(PageParam pageParam);



}
