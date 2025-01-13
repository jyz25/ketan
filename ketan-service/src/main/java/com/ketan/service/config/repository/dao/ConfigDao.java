package com.ketan.service.config.repository.dao;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ketan.api.model.enums.PushStatusEnum;
import com.ketan.api.model.enums.YesOrNoEnum;
import com.ketan.api.model.vo.banner.dto.ConfigDTO;
import com.ketan.service.config.converter.ConfigConverter;
import com.ketan.service.config.repository.entity.ConfigDO;
import com.ketan.service.config.repository.mapper.ConfigMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConfigDao extends ServiceImpl<ConfigMapper, ConfigDO> {


    /**
     * 根据类型获取配置列表（无需分页）
     *
     * @param type
     * @return
     */
    public List<ConfigDTO> listConfigByType(Integer type) {
        List<ConfigDO> configDOS = lambdaQuery()
                .eq(ConfigDO::getType, type)
                .eq(ConfigDO::getStatus, PushStatusEnum.ONLINE.getCode())
                .eq(ConfigDO::getDeleted, YesOrNoEnum.NO.getCode())
                .orderByAsc(ConfigDO::getRank)
                .list();
        return ConfigConverter.toDTOS(configDOS);
    }
}
