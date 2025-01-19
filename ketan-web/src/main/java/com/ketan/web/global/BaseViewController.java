package com.ketan.web.global;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 全局属性配置
 */
public class BaseViewController {

    @Autowired
    protected GlobalInitService globalInitService;


}
