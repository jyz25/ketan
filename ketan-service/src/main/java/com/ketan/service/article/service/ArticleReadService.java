package com.ketan.service.article.service;

import com.ketan.api.model.vo.PageListVo;
import com.ketan.api.model.vo.PageParam;
import com.ketan.api.model.vo.article.dto.ArticleDTO;
import com.ketan.service.article.repository.entity.ArticleDO;

import java.util.List;
import java.util.Map;

public interface ArticleReadService {

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
}
