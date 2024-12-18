package com.ketan.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @auther Kindow
 * @date 2024/12/11
 * @project ketan
 * @description 全局配置类
 */

@Component
@Data
@ConfigurationProperties(prefix = "view.site")
public class GlobalViewConfig {

    private String websiteName;

    /**
     * oss的地址
     */
    private String oss;


    private String contactMeWxQrCode;


    private String contactMeStarQrCode;
}
