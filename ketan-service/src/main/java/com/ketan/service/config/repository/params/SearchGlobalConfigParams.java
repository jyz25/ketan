package com.ketan.service.config.repository.params;


import com.ketan.api.model.vo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class SearchGlobalConfigParams extends PageParam {
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // 配置项名称
    private String key;
    // 配置项值
    private String value;
    // 备注
    private String comment;
}
