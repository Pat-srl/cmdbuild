/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.jobs;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.Collection;
import static java.util.Collections.emptyList;
import org.cmdbuild.config.WaterwayConfig;
import org.cmdbuild.etl.config.WaterwayConfigReloadEvent;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import org.cmdbuild.etl.config.WaterwayItem;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_TRIGGER;
import org.cmdbuild.etl.waterway.WaterwayService;
import static org.cmdbuild.etl.waterway.WaterwayService.WATERWAY_SERVICE_MINION;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.jobs.JobData;
import static org.cmdbuild.jobs.JobData.JOB_CONFIG_CRON_EXPR;
import static org.cmdbuild.jobs.JobData.JOB_CONFIG_WORKGROUP;
import org.cmdbuild.jobs.JobRunContext;
import org.cmdbuild.jobs.JobRunner;
import org.cmdbuild.jobs.beans.JobDataImpl;
import org.cmdbuild.minions.MinionComponent;
import org.cmdbuild.minions.MinionHandler;
import org.cmdbuild.minions.MinionHandlerExt;
import org.cmdbuild.minions.MinionHandlerImpl;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_ERROR;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_NOTRUNNING;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_READY;
import org.cmdbuild.scheduler.JobSource;
import org.cmdbuild.scheduler.JobUpdatedEvent;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WaterwayJobTriggerService implements JobSource, JobRunner, MinionComponent {

    private final static String WATERWAY_JOB_SOURCE_NAME = "waterway",
            WATERWAY_JOB_TRIGGER_TYPE = "trigger",
            WATERWAY_JOB_TRIGGER_TARGET_PARAM = "target";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WaterwayDescriptorService configService;
    private final WaterwayService service;

    private final MinionHandlerExt minionHandler;
    private final EventBus eventBus;

    public WaterwayJobTriggerService(WaterwayConfig configuration, WaterwayDescriptorService configService, WaterwayService service, EventBusService busService) {
        this.configService = checkNotNull(configService);
        this.service = checkNotNull(service);
        this.minionHandler = MinionHandlerImpl.builder()
                .withName("Waterway_ Jobs")
                .withDescription("Waterway Jobs")
                .withConfigEnabler("org.cmdbuild.waterway.jobs.enabled")
                .withEnabledChecker(configuration::isTriggersEnabled)
                .withRequires(WATERWAY_SERVICE_MINION)
                .reloadOnConfigs(WaterwayConfig.class)
                .build();
        eventBus = busService.getDaoEventBus();
        eventBus.register(new Object() {

            @Subscribe
            public void handleWaterwayConfigReloadEvent(WaterwayConfigReloadEvent event) {
                if (configuration.isTriggersEnabled()) {
                    start(); //TODO improve this !! minion reload
                }
            }
        });
    }

    @Override
    public MinionHandler getMinionHandler() {
        return minionHandler;
    }

    @Override
    public void start() {
        minionHandler.setStatus(MRS_READY);
        try {
            getJobs();
        } catch (Exception ex) {
            minionHandler.setStatus(MRS_ERROR);
            throw ex;
        }
        eventBus.post(JobUpdatedEvent.INSTANCE);
    }

    @Override
    public void stop() {
        minionHandler.setStatus(MRS_NOTRUNNING);
        eventBus.post(JobUpdatedEvent.INSTANCE);
    }

    @Override
    public String getJobSourceName() {
        return WATERWAY_JOB_SOURCE_NAME;
    }

    @Override
    public String getJobRunnerName() {
        return WATERWAY_JOB_TRIGGER_TYPE;
    }

    @Override
    public Collection<JobData> getJobs() {
        if (minionHandler.isReady()) {
            return configService.getAllItemsEnabled(WYCIT_TRIGGER).stream().filter(i -> i.hasConfigNotBlank(JOB_CONFIG_CRON_EXPR)).map(this::itemToJob).collect(toImmutableList());
        } else {
            logger.debug("waterway service inactive and/or triggers disabled: no waterway jobs to load");
            return emptyList();
        }
    }

    @Override
    public void runJob(JobData jobData, JobRunContext jobContext) {
        checkArgument(jobData.isOfType(WATERWAY_JOB_TRIGGER_TYPE));
        String gate = jobData.getConfigNotBlank(WATERWAY_JOB_TRIGGER_TARGET_PARAM);
        logger.debug("execute waterway trigger for gate =< {} > job = {}", gate, jobData);
        service.newRequest(gate).withMeta(map("wy_trigger_job", jobData.getCode(), "wy_trigger_job_run", jobContext.getJobRunId())).submit();
    }

    @Override
    public void vaildateJob(JobData jobData) {
        jobData.getConfigNotBlank(WATERWAY_JOB_TRIGGER_TARGET_PARAM);
    }

    private JobData itemToJob(WaterwayItem item) {
        return JobDataImpl.builder()
                .withConfig(map(item.getConfig()).with(JOB_CONFIG_WORKGROUP, item.getConfig("workgroup")))
                .withEnabled(item.isEnabled())//note: not used, always true here
                .withCode(item.getCode())
                .withDescription(item.getDescription())
                .withType(WATERWAY_JOB_TRIGGER_TYPE)
                //                .withConfig(JOB_MODULE, normalizeId(item.getConfigFileCode()).toLowerCase())//TODO improve this
                .build();
    }

}
