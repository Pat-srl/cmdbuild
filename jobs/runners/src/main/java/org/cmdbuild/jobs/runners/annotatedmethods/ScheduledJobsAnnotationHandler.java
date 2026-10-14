/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.runners.annotatedmethods;

import static com.google.common.base.Preconditions.checkNotNull;
import java.lang.reflect.Method;
import static org.apache.commons.lang3.StringUtils.firstNonBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.scheduler.ScheduledJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Component
public class ScheduledJobsAnnotationHandler implements BeanPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AnnotatedMethodJobStore annotatedMethodJobStore;

    public ScheduledJobsAnnotationHandler(AnnotatedMethodJobStore annotatedMethodJobStore) {
        this.annotatedMethodJobStore = checkNotNull(annotatedMethodJobStore);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean; // nothing to do
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithMethods(bean.getClass(), (Method method) -> {
            if (method.isAnnotationPresent(ScheduledJob.class)) {
                ScheduledJob annotation = method.getAnnotation(ScheduledJob.class);
                String cronExpression = checkNotNull(trimToNull(annotation.value()));
                logger.info("processing scheduled job from annotated method, bean = {} ({}) method = {} cronExpression =< {} >", bean.getClass().getSimpleName(), beanName, method.getName(), cronExpression);
                try {
                    annotatedMethodJobStore.addJob(new AnnotatedMethodJobImpl(beanName, firstNonBlank(annotation.alias(), method.getName()), cronExpression, annotation.clusterMode(), annotation.persistRun(), annotation.user()));
                } catch (Exception ex) {
                    logger.error("unable to configure scheduled job for spring method = " + beanName + " " + method, ex);
                }
            }
        });
        return bean;

    }

}
