package com.ketan.service.config.repository.params;


import com.ketan.api.model.vo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SearchConfigParams extends PageParam {
    // 类型
    private Integer type;
    // 名称
    private String name;
}
