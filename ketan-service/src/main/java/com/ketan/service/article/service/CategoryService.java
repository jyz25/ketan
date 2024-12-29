package com.ketan.service.article.service;

import com.ketan.api.model.vo.article.dto.CategoryDTO;

import java.util.List;

// 标签（文章分类）Service
public interface CategoryService {

    // 查询类目名
    String queryCategoryName(Long categoryId);

    // 查询所有的分类
    List<CategoryDTO> loadAllCategories();

    // 刷新缓存
    void refreshCache();


}
