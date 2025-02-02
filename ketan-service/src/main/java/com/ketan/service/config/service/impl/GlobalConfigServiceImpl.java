package com.ketan.service.config.service.impl;


import com.ketan.api.model.event.ConfigRefreshEvent;
import com.ketan.api.model.exception.ExceptionUtil;
import com.ketan.api.model.vo.PageVo;
import com.ketan.api.model.vo.config.GlobalConfigReq;
import com.ketan.api.model.vo.config.SearchGlobalConfigReq;
import com.ketan.api.model.vo.config.dto.GlobalConfigDTO;
import com.ketan.api.model.vo.constants.StatusEnum;
import com.ketan.core.senstive.SensitiveProperty;
import com.ketan.core.senstive.SensitiveService;
import com.ketan.core.util.NumUtil;
import com.ketan.core.util.SpringUtil;
import com.ketan.service.config.converter.ConfigStructMapper;
import com.ketan.service.config.repository.dao.ConfigDao;
import com.ketan.service.config.repository.entity.GlobalConfigDO;
import com.ketan.service.config.repository.params.SearchGlobalConfigParams;
import com.ketan.service.config.service.GlobalConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GlobalConfigServiceImpl implements GlobalConfigService {
    @Autowired
    private ConfigDao configDao;

    @Override
    public PageVo<GlobalConfigDTO> getList(SearchGlobalConfigReq req) {
        ConfigStructMapper mapper = ConfigStructMapper.INSTANCE;
        // 转换
        SearchGlobalConfigParams params = mapper.toSearchGlobalParams(req);
        // 查询
        List<GlobalConfigDO> list = configDao.listGlobalConfig(params);
        // 总数
        Long total = configDao.countGlobalConfig(params);

        return PageVo.build(mapper.toGlobalDTOS(list), params.getPageSize(), params.getPageNum(), total);
    }

    @Override
    public void save(GlobalConfigReq req) {
        GlobalConfigDO globalConfigDO = ConfigStructMapper.INSTANCE.toGlobalDO(req);
        // id 不为空
        if (NumUtil.nullOrZero(globalConfigDO.getId())) {
            configDao.save(globalConfigDO);
        } else {
            configDao.updateById(globalConfigDO);
        }

        // 配置更新之后，主动触发配置的动态加载
        SpringUtil.publishEvent(new ConfigRefreshEvent(this, req.getKeywords(), req.getValue()));
    }

    @Override
    public void delete(Long id) {
        GlobalConfigDO globalConfigDO = configDao.getGlobalConfigById(id);
        if (globalConfigDO != null) {
            configDao.delete(globalConfigDO);
        } else {
            throw ExceptionUtil.of(StatusEnum.RECORDS_NOT_EXISTS, "记录不存在");
        }
    }

    /**
     * 添加敏感词白名单
     *
     * @param word
     */
    @Override
    public void addSensitiveWhiteWord(String word) {
        String key = SensitiveProperty.SENSITIVE_KEY_PREFIX + ".allow";
        GlobalConfigReq req = new GlobalConfigReq();
        req.setKeywords(key);

        GlobalConfigDO config = configDao.getGlobalConfigByKey(key);
        if (config == null) {
            req.setValue(word);
            req.setComment("敏感词白名单");
        } else {
            req.setValue(config.getValue() + "," + word);
            req.setComment(config.getComment());
            req.setId(config.getId());
        }
        // 更新敏感词白名单
        save(req);

        // 移除敏感词记录
        SpringUtil.getBean(SensitiveService.class).removeSensitiveWord(word);
    }
}
