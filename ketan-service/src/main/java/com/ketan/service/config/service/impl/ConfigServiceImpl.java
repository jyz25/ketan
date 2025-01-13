package com.ketan.service.config.service.impl;

import com.ketan.api.model.enums.ConfigTypeEnum;
import com.ketan.api.model.vo.banner.dto.ConfigDTO;
import com.ketan.service.config.repository.dao.ConfigDao;
import com.ketan.service.config.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private ConfigDao configDao;

    @Override
    public List<ConfigDTO> getConfigList(ConfigTypeEnum configTypeEnum) {
        return configDao.listConfigByType(configTypeEnum.getCode());
    }
}
