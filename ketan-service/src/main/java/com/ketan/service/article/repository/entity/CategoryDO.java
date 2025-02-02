package com.ketan.service.article.repository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ketan.api.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Kindow
 * @date 2024/12/29
 * @description 类目表
 */

@Data
@EqualsAndHashCode(callSuper = true) // 确保父类字段参与比较
@TableName("category")
public class CategoryDO extends BaseDO {

    private static final long serialVersionUID = 1L;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    // 类目名称
    private String categoryName;

    // 状态： 0-未发布 1-已发布
    private Integer status;

    // 排序(注：rank是保留关键字，所以用``引用起来)
    @TableField("`rank`")
    private Integer rank;

    private Integer deleted;

}
