package com.ketan.service.config.service;

import com.ketan.api.model.enums.ConfigTypeEnum;
import com.ketan.api.model.vo.banner.dto.ConfigDTO;

import java.util.List;

/**
 * Banner前台接口
 */
public interface ConfigService {
    /**
     * 获取 Banner 列表
     *
     * @param configTypeEnum
     * @return
     */
    List<ConfigDTO> getConfigList(ConfigTypeEnum configTypeEnum);
}
