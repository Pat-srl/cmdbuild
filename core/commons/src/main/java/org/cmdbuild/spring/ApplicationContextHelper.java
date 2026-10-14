/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.spring;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.Iterables;
import static com.google.common.collect.Lists.newArrayList;
import java.util.List;
import java.util.UUID;
import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 *
 */
public class ApplicationContextHelper {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final List<Object> config = newArrayList();
    private AnnotationConfigApplicationContext applicationContext;

    public ApplicationContextHelper() {
    }

    public ApplicationContextHelper(Object config) {
        addConfig(config);
    }

    public ApplicationContextHelper(Object... config) {
        addConfig(list(config));
    }

    public final void addConfig(Object config) {
        if (config instanceof Iterable iterable) {
            Iterables.addAll(this.config, iterable);
        } else {
            this.config.add(config);
        }
    }

    public final void init() {
        logger.info("init BEGIN");
        try {
            doInit();
        } catch (Exception ex) {
            throw runtime(ex);
        }
        logger.info("init END");
    }

    protected void doInit() throws Exception {
        checkArgument(applicationContext == null);

        List<Class> customClasses = newArrayList();
        List<Object> customBeans = newArrayList();

        ApplicationContext parent = null;

        for (Object object : config) {
            if (object instanceof ApplicationContext appContext) {
                parent = appContext;
            } else if (object instanceof Class aClass) {
                customClasses.add(aClass);
            } else {
                customBeans.add(object);
            }
        }

        checkArgument(!customClasses.isEmpty());

        DefaultListableBeanFactory parentBeanFactory = new DefaultListableBeanFactory();
        for (Object bean : customBeans) {
            parentBeanFactory.registerSingleton(uncapitalize(bean.getClass().getName()) + "_" + UUID.randomUUID().toString().substring(0, 4), bean);
        }
        GenericApplicationContext genericApplicationContext = new GenericApplicationContext(parentBeanFactory);
        if (parent != null) {
            genericApplicationContext.setParent(parent);
        }
        genericApplicationContext.refresh();

        applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(customClasses.toArray(Class[]::new));
        applicationContext.setParent(genericApplicationContext);

        applicationContext.refresh();
    }

    public final void cleanup() {
        logger.info("cleanup BEGIN");
        if (applicationContext != null) {
            doCleanup();
            applicationContext = null;
        }
        logger.info("cleanup END");
    }

    protected void doCleanup() {
        logger.info("close application context");
        applicationContext.close();
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public <T> T getBean(Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }

}
