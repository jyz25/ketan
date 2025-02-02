package com.ketan.service.article.service;


import com.ketan.api.model.vo.PageParam;
import com.ketan.api.model.vo.PageVo;
import com.ketan.api.model.vo.article.dto.TagDTO;

/**
 * 标签Service
 */
public interface TagService {

    PageVo<TagDTO> queryTags(String key, PageParam pageParam);

    Long queryTagId(String tag);
}
