package com.ketan.api.model.vo.article.dto;

import lombok.Data;


@Data
public class ColumnArticleFlipDTO {
    String prevHref;
    Boolean prevShow;
    String nextHref;
    Boolean nextShow;
}
