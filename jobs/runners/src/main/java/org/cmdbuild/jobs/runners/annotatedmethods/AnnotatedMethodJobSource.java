/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.runners.annotatedmethods;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.eventbus.EventBus;
import static java.lang.String.format;
import java.lang.reflect.Method;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import java.util.Map;
import org.cmdbuild.config.api.ConfigListener;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.config.api.NamespacedConfigService;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.jobs.JobData;
import static org.cmdbuild.jobs.JobData.JOB_CONFIG_CRON_EXPR;
import static org.cmdbuild.jobs.JobData.JOB_CONFIG_CRON_EXPR_HAS_SECONDS;
import static org.cmdbuild.jobs.JobData.JOB_CONFIG_PERSIST_RUN;
import static org.cmdbuild.jobs.JobData.JOB_CONFIG_SESSION_USER;
import org.cmdbuild.jobs.JobException;
import org.cmdbuild.jobs.JobRunContext;
import org.cmdbuild.jobs.JobRunner;
import org.cmdbuild.jobs.beans.JobDataImpl;
import static org.cmdbuild.jobs.runners.annotatedmethods.AnnotatedMethodJob.ANNOTATED_METHOD_JOB_TYPE;
import org.cmdbuild.scheduler.JobSource;
import org.cmdbuild.scheduler.JobUpdatedEvent;
import org.cmdbuild.scheduler.ScheduledJob;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElementOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class AnnotatedMethodJobSource implements JobSource, JobRunner {

    public static final String SYSJOB_CONFIG_NAMESPACE = "org.cmdbuild.sysjob";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AnnotatedMethodJobSupplier annotatedMethodJobSupplier;
    private final ApplicationContext applicationContext;
    private final NamespacedConfigService configService;
    private final EventBus eventBus;

    public AnnotatedMethodJobSource(AnnotatedMethodJobSupplier annotatedMethodJobSupplier, ApplicationContext applicationContext, GlobalConfigService configService, EventBusService busService) {
        this.annotatedMethodJobSupplier = checkNotNull(annotatedMethodJobSupplier);
        this.applicationContext = checkNotNull(applicationContext);
        this.configService = configService.getConfig(SYSJOB_CONFIG_NAMESPACE);
        eventBus = busService.getDaoEventBus();
    }

    @Override
    public String getJobSourceName() {
        return "core";
    }

    @Override
    public String getJobRunnerName() {
        return ANNOTATED_METHOD_JOB_TYPE;
    }

    @ConfigListener(configNamespaces = {SYSJOB_CONFIG_NAMESPACE})
    public synchronized void loadCustomConfig() {
        logger.debug("reload sys job config");
        eventBus.post(JobUpdatedEvent.INSTANCE);
    }

    @Override
    public Collection<JobData> getJobs() {
        return annotatedMethodJobSupplier.getAnnotatedMethodJobs().stream().map(j -> {
            Map<String, String> config = map(configService.getAsMap()).filterMapKeys(format("%s.", j.getCode()));
            if (!config.isEmpty()) {
                logger.debug("custom sys job config for job =< {} > config =\n\n{}\n", j.getCode(), mapToLoggableString(config));
            }
            return buildJob(config, j);
        }).filter(JobData::isEnabled).collect(toImmutableList());
    }

    @Override
    public void runJob(JobData jobData, JobRunContext jobContext) {
        AnnotatedMethodJob job = annotatedMethodJobSupplier.getAnnotatedMethodJob(jobData.getCode());
        String beanName = job.getBeanName(), methodName = job.getMethodName();
        Object bean;
        Method method;
        try {
            logger.trace("get job bean for name = {}", beanName);
            bean = applicationContext.getBean(beanName);//TODO load bean when building job detail, below
            logger.trace("get job method for bean name = {} method name = {}", beanName, methodName);
            method = list(bean.getClass().getMethods()).filter(m -> m.isAnnotationPresent(ScheduledJob.class) && m.getAnnotation(ScheduledJob.class).alias().equals(methodName)).collect(onlyElementOrNull());
            if (method == null) {
                method = bean.getClass().getMethod(methodName);
            }
            checkArgument(method.isAnnotationPresent(ScheduledJob.class), "this method is not annotated with ScheduledJob"); // throw error if the bean is not annotated as a scheduled job
        } catch (Exception ex) {
            throw new JobException(ex, "error loading job for bean =< %s > method =< %s >", beanName, methodName);
        }
        try {
            logger.debug("invoke bean method = {}.{}", beanName, methodName);
            method.invoke(bean);
            logger.debug("job execution completed for bean name = {} method name = {}", beanName, methodName);
        } catch (Exception ex) {
            throw new JobException(ex, "error invoking job method for bean =< %s > method =< %s >", beanName, methodName);
        }
    }

    public static JobData buildJob(AnnotatedMethodJob j) {
        return buildJob(emptyMap(), j);
    }

    private static JobData buildJob(Map<String, String> config, AnnotatedMethodJob j) {
        return JobDataImpl.builder()
                .withType(ANNOTATED_METHOD_JOB_TYPE)
                .withPersistRun(toBooleanOrDefault(config.get(JOB_CONFIG_PERSIST_RUN), j.persistRun()))
                .withEnabled(toBooleanOrDefault(config.get("enabled"), true))
                .withCronExpressionHasSeconds(toBooleanOrDefault(config.get(JOB_CONFIG_CRON_EXPR_HAS_SECONDS), true))
                .withCode(j.getCode())
                .withClusterMode(j.getClusterMode())
                .withCronExpression(firstNotBlank(config.get(JOB_CONFIG_CRON_EXPR), j.getCronExpression()))
                .withConfig(JOB_CONFIG_SESSION_USER, j.getUser())
                .build();
    }

}
