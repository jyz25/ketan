package com.ketan.service.config.repository.dao;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ketan.api.model.enums.PushStatusEnum;
import com.ketan.api.model.enums.YesOrNoEnum;
import com.ketan.api.model.vo.banner.dto.ConfigDTO;
import com.ketan.service.config.converter.ConfigConverter;
import com.ketan.service.config.repository.entity.ConfigDO;
import com.ketan.service.config.repository.entity.GlobalConfigDO;
import com.ketan.service.config.repository.mapper.ConfigMapper;
import com.ketan.service.config.repository.mapper.GlobalConfigMapper;
import com.ketan.service.config.repository.params.SearchGlobalConfigParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Repository
public class ConfigDao extends ServiceImpl<ConfigMapper, ConfigDO> {

    @Resource
    private GlobalConfigMapper globalConfigMapper;

    public Long countGlobalConfig(SearchGlobalConfigParams params) {
        return globalConfigMapper.selectCount(buildQuery(params));
    }

    /**
     * 根据key查询全局配置
     *
     * @param key
     * @return
     */
    public GlobalConfigDO getGlobalConfigByKey(String key) {
        // 查询的时候 deleted 为 0
        LambdaQueryWrapper<GlobalConfigDO> query = Wrappers.lambdaQuery();
        query.select(GlobalConfigDO::getId, GlobalConfigDO::getKey, GlobalConfigDO::getValue, GlobalConfigDO::getComment)
                .eq(GlobalConfigDO::getKey, key)
                .eq(GlobalConfigDO::getDeleted, YesOrNoEnum.NO.getCode());
        return globalConfigMapper.selectOne(query);
    }

    public List<GlobalConfigDO> listGlobalConfig(SearchGlobalConfigParams params) {
        // 构建查询条件
        LambdaQueryWrapper<GlobalConfigDO> query = buildQuery(params);

        // 指定查询的字段
        query.select(GlobalConfigDO::getId,
                GlobalConfigDO::getKey,
                GlobalConfigDO::getValue,
                GlobalConfigDO::getComment);

        // 执行查询并返回结果
        return globalConfigMapper.selectList(query);
    }

    public void save(GlobalConfigDO globalConfigDO) {
        globalConfigMapper.insert(globalConfigDO);
    }

    public void updateById(GlobalConfigDO globalConfigDO) {
        globalConfigDO.setUpdateTime(new Date());
        globalConfigMapper.updateById(globalConfigDO);
    }

    /**
     * 根据id查询全局配置
     *
     * @param id
     * @return
     */
    public GlobalConfigDO getGlobalConfigById(Long id) {
        // 查询的时候 deleted 为 0
        LambdaQueryWrapper<GlobalConfigDO> query = Wrappers.lambdaQuery();
        query.select(GlobalConfigDO::getId, GlobalConfigDO::getKey, GlobalConfigDO::getValue, GlobalConfigDO::getComment)
                .eq(GlobalConfigDO::getId, id)
                .eq(GlobalConfigDO::getDeleted, YesOrNoEnum.NO.getCode());
        return globalConfigMapper.selectOne(query);
    }

    public void delete(GlobalConfigDO globalConfigDO) {
        globalConfigDO.setDeleted(YesOrNoEnum.YES.getCode());
        globalConfigMapper.updateById(globalConfigDO);
    }

    private LambdaQueryWrapper<GlobalConfigDO> buildQuery(SearchGlobalConfigParams params) {
        // 创建查询条件对象
        LambdaQueryWrapper<GlobalConfigDO> query = Wrappers.lambdaQuery();

        // 动态添加查询条件。如果参数值不为空，则添加对应的查询条件。
        query.and(!StringUtils.isEmpty(params.getKey()),
                        k -> k.like(GlobalConfigDO::getKey, params.getKey()))
                .and(!StringUtils.isEmpty(params.getValue()),
                        v -> v.like(GlobalConfigDO::getValue, params.getValue()))
                .and(!StringUtils.isEmpty(params.getComment()),
                        c -> c.like(GlobalConfigDO::getComment, params.getComment()))
                .eq(GlobalConfigDO::getDeleted, YesOrNoEnum.NO.getCode())
                .orderByDesc(GlobalConfigDO::getUpdateTime);

        return query;
    }



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

    /**
     * 更新阅读相关计数
     */
    public void updatePdfConfigVisitNum(long configId, String extra) {
        lambdaUpdate().set(ConfigDO::getExtra, extra)
                .eq(ConfigDO::getId, configId)
                .update();
    }
}
