package com.ketan.core.autoconf;


import com.ketan.api.model.event.ConfigRefreshEvent;
import com.ketan.core.autoconf.property.SpringValueRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

/**
 * 配置刷新事件监听
 */
@Service
public class ConfigRefreshEventListener implements ApplicationListener<ConfigRefreshEvent> {
    @Autowired
    private DynamicConfigContainer dynamicConfigContainer;

    /**
     * 监听配置变更事件
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(ConfigRefreshEvent event) {
        dynamicConfigContainer.reloadConfig();
        SpringValueRegistry.updateValue(event.getKey());
    }
}
