package com.ketan.service.config.repository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import com.ketan.api.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@TableName("global_conf")
public class GlobalConfigDO extends BaseDO {
    private static final long serialVersionUID = -6122208316544171301L;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // 配置项名称
    @TableField("`key`")
    private String key;
    // 配置项值
    private String value;
    // 备注
    private String comment;
    // 删除
    private Integer deleted;
}
