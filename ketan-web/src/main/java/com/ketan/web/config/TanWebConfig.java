package com.ketan.web.config;

import com.ketan.core.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 注册xml解析器
 */
@Slf4j
@Configuration
public class TanWebConfig implements WebMvcConfigurer {


    /**
     * 配置序列化方式
     *
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2XmlHttpMessageConverter());
        converters.forEach(s -> {
            if (s instanceof MappingJackson2HttpMessageConverter) {
                // 长整型序列化返回时，更新为string，避免前端js精度丢失
                // 注意这个仅适用于json数据格式的返回，对于Thymeleaf的模板渲染依然会出现精度问题
                ((MappingJackson2HttpMessageConverter) s).getObjectMapper().registerModule(JsonUtil.bigIntToStrsimpleModule());
            }
        });
    }


}
