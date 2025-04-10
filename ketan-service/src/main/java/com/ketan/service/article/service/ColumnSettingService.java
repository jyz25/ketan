package com.ketan.service.article.service;


import com.ketan.api.model.vo.PageVo;
import com.ketan.api.model.vo.article.*;
import com.ketan.api.model.vo.article.dto.ColumnArticleDTO;
import com.ketan.api.model.vo.article.dto.ColumnDTO;
import com.ketan.api.model.vo.article.dto.SimpleColumnDTO;

import java.util.List;

/**
 * 专栏后台接口
 */
public interface ColumnSettingService {

    /**
     * 将文章保存到对应的专栏中
     *
     * @param articleId
     * @param columnId
     */
    void saveColumnArticle(Long articleId, Long columnId);

    /**
     * 保存专栏
     *
     * @param columnReq
     */
    void saveColumn(ColumnReq columnReq);

    /**
     * 保存专栏文章
     *
     * @param req
     */
    void saveColumnArticle(ColumnArticleReq req);

    /**
     * 删除专栏
     *
     * @param columnId
     */
    void deleteColumn(Long columnId);

    /**
     * 删除专栏文章
     *
     * @param id
     */
    void deleteColumnArticle(Long id);

    /**
     * 通过关键词，从标题中找出相似的进行推荐，只返回主键 + 标题
     *
     * @param key
     * @return
     */
    List<SimpleColumnDTO> listSimpleColumnBySearchKey(String key);

    PageVo<ColumnDTO> getColumnList(SearchColumnReq req);

    PageVo<ColumnArticleDTO> getColumnArticleList(SearchColumnArticleReq req);

    void sortColumnArticleApi(SortColumnArticleReq req);

    void sortColumnArticleByIDApi(SortColumnArticleByIDReq req);
}
