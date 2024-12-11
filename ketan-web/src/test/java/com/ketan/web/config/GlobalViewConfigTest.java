package com.ketan.web.config;

/**
 * @auther Kindow
 * @date 2024/12/11
 * @project ketan
 * @description 全局配置类
 */

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@RunWith(SpringRunner.class)
public class GlobalViewConfigTest {

    @Autowired
    private GlobalViewConfig globalViewConfig;

    @Test
    public void testGlobalViewConfigLoading() {
        // 验证配置类是否正确加载
        assertThat(globalViewConfig).isNotNull();

        // 验证配置项是否注入
        assertThat(globalViewConfig.getWebsiteName()).isEqualTo("科探");
    }
}


