package com.ketan.service.statistics.service;

import com.ketan.api.model.vo.user.dto.ArticleFootCountDTO;

public interface CountService {


    /**
     * 查询文章相关的统计信息
     *
     * @param articleId
     * @return 返回文章的 收藏、点赞、评论、阅读数
     */
    ArticleFootCountDTO queryArticleStatisticInfo(Long articleId);

}
