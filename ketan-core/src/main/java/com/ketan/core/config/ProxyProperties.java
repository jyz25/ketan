package com.ketan.core.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.Proxy;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "net")
public class ProxyProperties {
    private List<ProxyType> proxy;

    @Data
    @Accessors(chain = true)
    public static class ProxyType {
        /**
         * 代理类型
         */
        private Proxy.Type type;
        /**
         * 代理ip
         */
        private String ip;
        /**
         * 代理端口
         */
        private Integer port;
    }
}
