package com.ketan.service.article.conveter;

import com.ketan.api.model.vo.article.dto.CategoryDTO;
import com.ketan.service.article.repository.entity.CategoryDO;

/**
 * @author Kindow
 * @date 2024/12/29
 * @description 文章转换 从 do 转化为 dto
 */
public class ArticleConveter {

    public static CategoryDTO toDto(CategoryDO category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategory(category.getCategoryName());
        dto.setCategoryId(category.getId());
        dto.setRank(category.getRank());
        dto.setStatus(category.getStatus());
        dto.setSelected(false);
    }
}
