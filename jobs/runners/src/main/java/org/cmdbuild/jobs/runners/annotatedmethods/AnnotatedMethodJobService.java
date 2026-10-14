/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.runners.annotatedmethods;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.cmdbuild.config.api.ConfigDefinitionImpl;
import org.cmdbuild.config.api.ConfigDefinitionRepository;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.cmdbuild.jobs.JobData;
import static org.cmdbuild.jobs.JobData.JOB_CONFIG_CRON_EXPR;
import static org.cmdbuild.jobs.JobData.JOB_CONFIG_CRON_EXPR_HAS_SECONDS;
import static org.cmdbuild.jobs.JobData.JOB_CONFIG_PERSIST_RUN;
import static org.cmdbuild.jobs.runners.annotatedmethods.AnnotatedMethodJobSource.SYSJOB_CONFIG_NAMESPACE;
import static org.cmdbuild.jobs.runners.annotatedmethods.AnnotatedMethodJobSource.buildJob;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AnnotatedMethodJobService implements AnnotatedMethodJobStore, AnnotatedMethodJobSupplier {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ConfigDefinitionRepository configDefinitionRepository;

    private final Map<String, AnnotatedMethodJob> jobs = new ConcurrentHashMap<>();

    public AnnotatedMethodJobService(ConfigDefinitionRepository configDefinitionRepository) {
        this.configDefinitionRepository = checkNotNull(configDefinitionRepository);
    }

    @Override
    public List<AnnotatedMethodJob> getAnnotatedMethodJobs() {
        return ImmutableList.copyOf(jobs.values());
    }

    @Override
    public synchronized void addJob(AnnotatedMethodJob job) {
        logger.debug("add job = {}", job);
        jobs.put(job.getCode(), job);
        addConfigDefinitions(buildJob(job));
    }

    @Override
    public AnnotatedMethodJob getAnnotatedMethodJob(String code) {
        return checkNotNull(jobs.get(checkNotBlank(code)), "job not found for code =< %s >", code);
    }

    private void addConfigDefinitions(JobData job) {
        String prefix = format("%s.%s.", SYSJOB_CONFIG_NAMESPACE, job.getCode());
        configDefinitionRepository.put(ConfigDefinitionImpl.builder().withKey(prefix + "enabled").withDefaultValue(TRUE).withDescription("enable job").build());
        configDefinitionRepository.put(ConfigDefinitionImpl.builder().withKey(prefix + JOB_CONFIG_CRON_EXPR).withDefaultValue(job.getCronExpression()).withDescription("cron expression").build());
        configDefinitionRepository.put(ConfigDefinitionImpl.builder().withKey(prefix + JOB_CONFIG_CRON_EXPR_HAS_SECONDS).withDefaultValue(toStringNotBlank(job.cronExpressionHasSeconds())).withDescription("cron expression has seconds").build());
        configDefinitionRepository.put(ConfigDefinitionImpl.builder().withKey(prefix + JOB_CONFIG_PERSIST_RUN).withDefaultValue(toStringNotBlank(job.persistJobRun())).withDescription("persist job run").build());
    }

}
