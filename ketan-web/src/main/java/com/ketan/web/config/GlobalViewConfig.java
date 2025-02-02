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

    private String websiteRecord;

    /**
     * oss的地址
     */
    private String oss;


    private String contactMeWxQrCode;


    private String contactMeStarQrCode;

    /**
     * 健康知识的跳转链接
     */
    private String zsxqUrl;

    /**
     * 知识海报的一个展示的图片地址
     */
    private String zsxqImgUrl;

    private String host;

    // 知识星球文章可阅读数
    private String zsxqArticleReadCount;

    // 需要登录文章可阅读数
    private String needLoginArticleReadCount;

}
