package com.ketan.service.statistics.service;

public interface StatisticsSettingService {
    /**
     * 保存计数
     *
     * @param host
     */
    void saveRequestCount(String host);

}
