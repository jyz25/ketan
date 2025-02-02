package com.ketan.web.front.article.vo;


import com.ketan.api.model.vo.article.dto.ArticleDTO;
import com.ketan.api.model.vo.article.dto.CategoryDTO;
import com.ketan.api.model.vo.article.dto.TagDTO;
import lombok.Data;

import java.util.List;


@Data
public class ArticleEditVo {

    private ArticleDTO article;

    private List<CategoryDTO> categories;

    private List<TagDTO> tags;

}
