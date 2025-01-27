package com.ketan.service.notify.config;


import com.ketan.core.async.AsyncUtil;
import com.ketan.core.config.RabbitmqProperties;
import com.ketan.core.rabbitmq.RabbitmqConnectionPool;
import com.ketan.service.notify.service.RabbitmqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author YiHui
 * @date 2023/6/9
 */
@Configuration
@ConditionalOnProperty(value = "rabbitmq.switchFlag")
@EnableConfigurationProperties(RabbitmqProperties.class)
public class RabbitMqAutoConfig implements ApplicationRunner {
    @Resource
    private RabbitmqService rabbitmqService;

    @Autowired
    private RabbitmqProperties rabbitmqProperties;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        String host = rabbitmqProperties.getHost();
        Integer port = rabbitmqProperties.getPort();
        String userName = rabbitmqProperties.getUsername();
        String password = rabbitmqProperties.getPassport();
        String virtualhost = rabbitmqProperties.getVirtualhost();
        Integer poolSize = rabbitmqProperties.getPoolSize();
        // 初始化RabbitMQ连接池
        RabbitmqConnectionPool.initRabbitmqConnectionPool(host, port, userName, password, virtualhost, poolSize);
        AsyncUtil.execute(() -> rabbitmqService.processConsumerMsg());
    }
}
