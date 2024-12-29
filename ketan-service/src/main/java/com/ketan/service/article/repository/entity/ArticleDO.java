package com.ketan.service.article.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ketan.api.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description 文章表
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article")
public class ArticleDO extends BaseDO {
    private static final long serialVersionUID = 1L; // 版本控制的标识符


}
