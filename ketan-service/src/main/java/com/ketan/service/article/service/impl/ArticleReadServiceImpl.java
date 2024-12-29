package com.ketan.service.article.service.impl;


import com.ketan.service.article.repository.dao.ArticleDao;
import com.ketan.service.article.service.ArticleReadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class ArticleReadServiceImpl implements ArticleReadService {

    @Autowired
    ArticleDao articleDao;

    @Override
    public Map<Long, Long> queryArticleCountsByCategory() {
        return articleDao.countArticleByCategoryId();
    }
}
