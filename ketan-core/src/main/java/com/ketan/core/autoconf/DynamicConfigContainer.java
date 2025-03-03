package com.ketan.core.autoconf;

import com.google.common.collect.Maps;
import com.ketan.core.util.JsonUtil;
import com.ketan.core.util.SpringUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * 自定义的配置工厂类，专门用于 ConfDot 属性配置文件的配置加载，支持从自定义的配置源获取
 */
@Slf4j
@Component
public class DynamicConfigContainer implements EnvironmentAware, ApplicationContextAware, CommandLineRunner {
    private ConfigurableEnvironment environment;
    private ApplicationContext applicationContext;
    /**
     * 存储db中的全局配置，优先级最高
     */
    @Getter
    public Map<String, Object> cache;

    private DynamicConfigBinder binder;

    /**
     * 配置变更的回调任务
     */
    @Getter
    private Map<Class, Runnable> refreshCallback = Maps.newHashMap();

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    // 在该类实例化后，自动执行该方法
    @PostConstruct
    public void init() {
        // 1、为成员变量cache创建实例
        cache = Maps.newHashMap();
        // 2、将cache与系统配置连接起来，作为动态配置的数据来源
        bindBeansFromLocalCache("dbConfig", cache);
    }

    /**
     * 从db中获取全量的配置信息
     *
     * @return true 表示有信息变更; false 表示无信息变更
     */
    private boolean loadAllConfigFromDb() {
        List<Map<String, Object>> list = SpringUtil.getBean(JdbcTemplate.class).queryForList("select `key`, `value` from global_conf where deleted = 0");
        // 避免内存浪费，避免重新分配内存时的性能消耗
        Map<String, Object> val = Maps.newHashMapWithExpectedSize(list.size());
        for (Map<String, Object> conf : list) {
            val.put(conf.get("key").toString(), conf.get("value").toString());
        }
        // 比较的是值，而不是引用
        if (val.equals(cache)) {
            return false;
        }
        cache.clear();
        cache.putAll(val);
        return true;
    }

    // 配置绑定：将 cache 注册为一个配置源
    private void bindBeansFromLocalCache(String namespace, Map<String, Object> cache) {
        // 将内存的配置信息设置为最高优先级
        // 1、从cache键值对中读取配置信息，然后包装成MapPropertySource
        MapPropertySource propertySource = new MapPropertySource(namespace, cache);
        // 2、将该配置信息（MapPropertySource）设置为最高优先级，并添加到Spring应用环境中
        environment.getPropertySources().addFirst(propertySource);
        // 3、新建一个自定义绑定器
        this.binder = new DynamicConfigBinder(this.applicationContext, environment.getPropertySources());
    }

    /**
     * 配置绑定
     *
     * @param bindable
     */
    public void bind(Bindable bindable) {
        binder.bind(bindable);
    }


    /**
     * 监听配置的变更
     */
    public void reloadConfig() {
        // 转化为JSON字符串
        String before = JsonUtil.toStr(cache);
        // 1、判断数据库中的配置是否发生改变，如果改变了，就加载到cache中，并返回true
        boolean toRefresh = loadAllConfigFromDb();
        if (toRefresh) {
            // 2、cache发生改变，系统配置动态改变
            refreshConfig();
            log.info("配置刷新! 旧:{}, 新:{}", before, JsonUtil.toStr(cache));
        }
    }

    /**
     * 强制刷新缓存配置
     */
    public void forceRefresh() {
        loadAllConfigFromDb();
        refreshConfig();
        log.info("db配置强制刷新! {}", JsonUtil.toStr(cache));
    }

    /**
     * 支持配置的动态刷新
     * 仅仅将cache改变是不行的，我们需要把cache中的真实映射到环境中
     */
    private void refreshConfig() {
        applicationContext.getBeansWithAnnotation(ConfigurationProperties.class).values().forEach(bean -> {
            // 1、获取被@ConfigurationProperties注解的所有bean
            Bindable<?> target = Bindable.ofInstance(bean).withAnnotations(AnnotationUtils.findAnnotation(bean.getClass(), ConfigurationProperties.class));
            // 2、配置绑定
            bind(target);
            // 3、如果配置信息在其他地方被使用，则开始应用新的值
            if (refreshCallback.containsKey(bean.getClass())) {
                refreshCallback.get(bean.getClass()).run();
            }
        });
    }

    /**
     * 注册db的动态配置变更
     */
    @Scheduled(fixedRate = 300000) // 5 minutes expressed in milliseconds
    private void registerConfRefreshTask() {
        try {
            log.debug("5分钟，自动更新db配置信息!");
            reloadConfig();
        } catch (Exception e) {
            log.warn("自动更新db配置信息异常!", e);
        }
    }

    /**
     * 注册配置变更的回调任务
     *
     * @param bean
     * @param run
     */
    public void registerRefreshCallback(Object bean, Runnable run) {
        refreshCallback.put(bean.getClass(), run);
    }


    /**
     * 应用启动之后，执行的动态配置初始化
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        reloadConfig();
        registerConfRefreshTask();
    }
}
