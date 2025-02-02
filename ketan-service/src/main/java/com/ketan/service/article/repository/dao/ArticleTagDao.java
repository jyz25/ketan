package com.ketan.service.article.repository.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ketan.api.model.enums.YesOrNoEnum;
import com.ketan.api.model.vo.article.dto.TagDTO;
import com.ketan.service.article.repository.entity.ArticleTagDO;
import com.ketan.service.article.repository.mapper.ArticleTagMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;


@Repository
public class ArticleTagDao extends ServiceImpl<ArticleTagMapper, ArticleTagDO> {
    public List<TagDTO> queryArticleTagDetails(Long articleId) {
        return baseMapper.listArticleTagDetails(articleId);
    }

    /**
     * 批量保存
     *
     * @param articleId
     * @param tags
     */
    public void batchSave(Long articleId, Collection<Long> tags) {
        List<ArticleTagDO> insertList = new ArrayList<>(tags.size());
        tags.forEach(s -> {
            ArticleTagDO tag = new ArticleTagDO();
            tag.setTagId(s);
            tag.setArticleId(articleId);
            tag.setDeleted(YesOrNoEnum.NO.getCode());
            insertList.add(tag);
        });
        saveBatch(insertList);
    }

    /**
     * 更新文章标签
     * 1. 原来有，新的没有；则删除旧的
     * 2. 原来有，新的改变，则替换旧的
     * 3. 原来没有，新的有，则插入
     *
     * @param articleId
     * @param newTags
     */
    public void updateTags(Long articleId, Set<Long> newTags) {
        List<ArticleTagDO> dbTags = listArticleTags(articleId);
        // 在旧的里面，不在新的里面的标签，设置为删除
        List<Long> toDeleted = new ArrayList<>();
        dbTags.forEach(tag -> {
            if (!newTags.contains(tag.getTagId())) {
                toDeleted.add(tag.getId());
            } else {
                // 移除已经存在的记录
                newTags.remove(tag.getTagId());
            }
        });
        if (!toDeleted.isEmpty()) {
            baseMapper.deleteBatchIds(toDeleted);
        }

        if (!newTags.isEmpty()) {
            batchSave(articleId, newTags);
        }
    }

    public List<ArticleTagDO> listArticleTags(@Param("articleId") Long articleId) {
        return lambdaQuery().eq(ArticleTagDO::getArticleId, articleId).eq(ArticleTagDO::getDeleted, YesOrNoEnum.NO.getCode()).list();
    }

}
