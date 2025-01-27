package com.ketan.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Kindow
 * @date 2024/12/29
 * @description
 */

@Configuration
@ComponentScan("com.ketan.service")
@MapperScan(basePackages = {
        "com.ketan.service.article.repository.mapper",
        "com.ketan.service.user.repository.mapper",
        "com.ketan.service.config.repository.mapper",
        "com.ketan.service.notify.repository.mapper",
        "com.ketan.service.statistics.repository.mapper",
        "com.ketan.service.comment.repository.mapper"
})
public class ServiceAutoConfig {
}
