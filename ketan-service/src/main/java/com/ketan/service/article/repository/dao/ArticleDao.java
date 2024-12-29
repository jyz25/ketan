package com.ketan.service.article.repository.dao;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.ketan.api.model.enums.PushStatusEnum;
import com.ketan.api.model.enums.YesOrNoEnum;
import com.ketan.service.article.repository.entity.ArticleDO;
import com.ketan.service.article.repository.mapper.ArticleMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 文章相关 DB操作
 * 多表结构的操作封装，只与 DB操作相关
 */

@Repository
public class ArticleDao extends ServiceImpl<ArticleMapper, ArticleDO> {


    /**
     * 按照分类统计文章的数量
     *
     * @return key: categoryId value: count
     */
    public Map<Long, Long> countArticleByCategoryId() {
        QueryWrapper<ArticleDO> query = Wrappers.query();
        query.select("category_id, count(*) as cnt")
                .eq("deleted", YesOrNoEnum.NO.getCode())
                .eq("status", PushStatusEnum.ONLINE.getCode())
                .groupBy("category_id");
        // List:表示有很多条数据
        // Map<String, Object>: String: 字段名称 Object: 具体的字段值（此处为Integer）
        List<Map<String, Object>> mapList = baseMapper.selectMaps(query);
        Map<Long, Long> result = Maps.newHashMapWithExpectedSize(mapList.size());
        for (Map<String, Object> mp : mapList) {
            Long cnt = (Long) mp.get("cnt");
            if (cnt != null && cnt > 0) {
                result.put((Long) mp.get("category_id"), cnt);
            }
        }
        return result;
    }
}
