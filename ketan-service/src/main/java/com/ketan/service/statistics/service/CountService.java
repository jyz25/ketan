package com.ketan.service.statistics.service;

import com.ketan.api.model.vo.user.dto.ArticleFootCountDTO;
import com.ketan.api.model.vo.user.dto.UserStatisticInfoDTO;

public interface CountService {


    /**
     * 查询文章相关的统计信息
     *
     * @param articleId
     * @return 返回文章的 收藏、点赞、评论、阅读数
     */
    ArticleFootCountDTO queryArticleStatisticInfo(Long articleId);


    /**
     * 查询用户的相关统计信息
     *
     * @param userId
     * @return 返回用户的 收藏、点赞、文章、粉丝、关注，总的文章阅读数
     */
    UserStatisticInfoDTO queryUserStatisticInfo(Long userId);
}
