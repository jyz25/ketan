package com.ketan.service.statistics.service.impl;

import com.ketan.service.statistics.repository.entity.RequestCountDO;
import com.ketan.service.statistics.service.RequestCountService;
import com.ketan.service.statistics.service.StatisticsSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StatisticsSettingServiceImpl implements StatisticsSettingService {

    @Autowired
    private RequestCountService requestCountService;

    @Override
    public void saveRequestCount(String host) {
        RequestCountDO requestCountDO = requestCountService.getRequestCount(host);
        if (requestCountDO == null) {
            requestCountService.insert(host);
        } else {
            // 改为数据库直接更新
            requestCountService.incrementCount(requestCountDO.getId());
        }
    }
}
