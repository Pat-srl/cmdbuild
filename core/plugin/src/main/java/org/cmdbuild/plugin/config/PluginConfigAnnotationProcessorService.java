/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.plugin.config;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.cmdbuild.plugin.config.api.PluginConfigComponent;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
public class PluginConfigAnnotationProcessorService implements BeanPostProcessor {

    private final List<Object> beans = new CopyOnWriteArrayList<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(PluginConfigComponent.class)) {
            addBean(bean);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public synchronized void addBean(Object bean) {
        beans.add(bean);
    }

    public List<Object> getPluginConfigBeans() {
        return beans;
    }
}
