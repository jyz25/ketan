package com.ketan.core.util;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

// 使用@Component注解将此工具类注册为Spring容器中的一个Bean
@Component
public class SpringUtil implements ApplicationContextAware, EnvironmentAware {
    // 使用volatile关键字确保多线程环境下的可见性和有序性
    private volatile static ApplicationContext context;
    private volatile static Environment environment;

    // 静态Binder对象，用于配置绑定
    private static Binder binder;

    // 实现ApplicationContextAware接口的方法，Spring容器启动时会调用此方法注入ApplicationContext
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.context = applicationContext;
    }

    // 实现EnvironmentAware接口的方法，Spring容器启动时会调用此方法注入Environment
    @Override
    public void setEnvironment(Environment environment) {
        SpringUtil.environment = environment;
        // 使用传入的Environment初始化Binder对象
        binder = Binder.get(environment);
    }

    // 获取ApplicationContext实例
    public static ApplicationContext getContext() {
        return context;
    }

    /**
     * 通过类型获取Bean
     *
     * @param bean 类的Class对象
     * @param <T>  泛型，指定Bean的类型
     * @return 返回指定类型的Bean实例
     */
    public static <T> T getBean(Class<T> bean) {
        return context.getBean(bean);
    }

    // 通过类型获取Bean，如果Bean不存在则返回null
    public static <T> T getBeanOrNull(Class<T> bean) {
        try {
            return context.getBean(bean);
        } catch (Exception e) {
            return null;
        }
    }

    // 通过Bean名称获取Bean
    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    // 通过Bean名称获取Bean，如果Bean不存在则返回null
    public static Object getBeanOrNull(String beanName) {
        try {
            return context.getBean(beanName);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 通过键名获取配置值
     *
     * @param key 配置项的键名
     * @return 返回配置项的值
     */
    public static String getConfig(String key) {
        return environment.getProperty(key);
    }

    // 获取配置项的值，如果主键不存在则尝试获取备键的值
    public static String getConfigOrElse(String mainKey, String slaveKey) {
        String ans = environment.getProperty(mainKey);
        if (ans == null) {
            return environment.getProperty(slaveKey);
        }
        return ans;
    }

    /**
     * 通过键名获取配置值，如果配置项不存在则返回默认值
     *
     * @param key 配置项的键名
     * @param val 配置项不存在的默认值
     * @return 返回配置项的值或默认值
     */
    public static String getConfig(String key, String val) {
        return environment.getProperty(key, val);
    }

    /**
     * 发布事件消息
     *
     * @param event 要发布的事件对象
     */
    public static void publishEvent(ApplicationEvent event) {
        context.publishEvent(event);
    }

    /**
     * 获取配置绑定器对象
     *
     * @return 返回Binder对象
     */
    public static Binder getBinder() {
        return binder;
    }
}
