package com.ketan.web.global.vo;

import com.ketan.api.model.vo.seo.SeoTagVo;
import com.ketan.api.model.vo.user.dto.BaseUserInfoDTO;
import com.ketan.service.sitemap.model.SiteCntVo;
import com.ketan.web.config.GlobalViewConfig;
import lombok.Data;

import java.util.List;

/**
 * @auther Kindow
 * @date 2024/12/18
 * @project ketan
 * @description
 */

@Data
public class GlobalVo {

    /**
     * 网站相关配置
     */
    private GlobalViewConfig siteInfo;

    /**
     * 今日的站点统计详细信息
     */
    private SiteCntVo todaySiteStatisticInfo;

    /**
     * 在线用户人数
     */
    private Integer onlineCnt;

    /**
     * 登录用户信息
     */
    private BaseUserInfoDTO user;

    /**
     * 消息通知数量
     */
    private Integer msgNum;

    /**
     * 环境
     */
    private String env;

    /**
     * 是否已登录
     */
    private Boolean isLogin;


    /**
     * SEO是英文Search Engine Optimization的缩写，中文翻译为“搜索引擎优化”。
     * 它是指在了解搜索引擎自然排名机制的基础上，对网站进行内部及外部的调整优化，以改进网站在搜索引擎中的关键词自然排名，
     * 从而获得更多流量，最终达成品牌建设或者产品销售的目的。
     */
    private List<SeoTagVo> ogp;

    private String jsonLd;

    private String currentDomain;

    /**
     * 站点统计信息
     */
    private SiteCntVo siteStatisticInfo;
}
