package com.ketan.service.article.repository.params;

import com.ketan.api.model.vo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class SearchCategoryParams extends PageParam {
    // 类目名称
    private String category;
}
