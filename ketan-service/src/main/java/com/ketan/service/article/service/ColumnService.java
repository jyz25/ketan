package com.ketan.service.article.service;

import com.ketan.api.model.vo.PageListVo;
import com.ketan.api.model.vo.PageParam;
import com.ketan.api.model.vo.article.dto.ColumnDTO;
import com.ketan.api.model.vo.article.dto.SimpleArticleDTO;
import com.ketan.service.article.repository.entity.ColumnArticleDO;

import java.util.List;

public interface ColumnService {


    /**
     * 专栏 + 文章列表详情
     *
     * @param columnId
     * @return
     */
    List<SimpleArticleDTO> queryColumnArticles(long columnId);

    /**
     * 专栏列表
     *
     * @param pageParam
     * @return
     */
    PageListVo<ColumnDTO> listColumn(PageParam pageParam);

    /**
     * 专栏详情
     *
     * @param columnId
     * @return
     */
    ColumnDTO queryColumnInfo(Long columnId);


    /**
     * 获取专栏中的第N篇文章
     *
     * @param columnId
     * @param section
     * @return
     */
    ColumnArticleDO queryColumnArticle(long columnId, Integer section);

    /**
     * 只查询基本的专栏信息，不需要统计、作者等信息
     *
     * @param columnId
     * @return
     */
    ColumnDTO queryBasicColumnInfo(Long columnId);


    /**
     * 根据文章id，构建对应的专栏详情地址
     *
     * @param articleId 文章主键
     * @return 专栏详情页
     */
    ColumnArticleDO getColumnArticleRelation(Long articleId);

}
