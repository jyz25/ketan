package com.ketan.service.article.service.impl;

import com.ketan.service.article.repository.dao.ColumnArticleDao;
import com.ketan.service.article.repository.entity.ColumnArticleDO;
import com.ketan.service.article.service.ColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColumnServiceImpl implements ColumnService {

    @Autowired
    private ColumnArticleDao columnArticleDao;

    @Override
    public ColumnArticleDO getColumnArticleRelation(Long articleId) {
        return columnArticleDao.selectColumnArticleByArticleId(articleId);
    }

}
