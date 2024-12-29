package com.ketan.service.article.service;

import java.util.Map;

public interface ArticleReadService {


    // 根据分类统计文章数
    Map<Long, Long> queryArticleCountsByCategory();
}
