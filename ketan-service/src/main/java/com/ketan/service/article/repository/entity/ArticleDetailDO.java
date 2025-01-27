package com.ketan.service.article.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ketan.api.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章详情
 * <p>
 * DO 对应数据库实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article_detail")
public class ArticleDetailDO extends BaseDO {

    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 版本号
     */
    private Long version;

    /**
     * 文章内容
     */
    private String content;

    private Integer deleted;
}
