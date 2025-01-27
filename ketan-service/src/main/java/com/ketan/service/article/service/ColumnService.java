package com.ketan.service.article.service;

import com.ketan.service.article.repository.entity.ColumnArticleDO;

public interface ColumnService {

    /**
     * 根据文章id，构建对应的专栏详情地址
     *
     * @param articleId 文章主键
     * @return 专栏详情页
     */
    ColumnArticleDO getColumnArticleRelation(Long articleId);

}
