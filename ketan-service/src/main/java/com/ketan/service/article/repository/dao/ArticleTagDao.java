package com.ketan.service.article.repository.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ketan.api.model.vo.article.dto.TagDTO;
import com.ketan.service.article.repository.entity.ArticleTagDO;
import com.ketan.service.article.repository.mapper.ArticleTagMapper;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class ArticleTagDao extends ServiceImpl<ArticleTagMapper, ArticleTagDO> {
    public List<TagDTO> queryArticleTagDetails(Long articleId) {
        return baseMapper.listArticleTagDetails(articleId);
    }
}
