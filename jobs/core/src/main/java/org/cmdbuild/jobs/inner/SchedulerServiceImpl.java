package org.cmdbuild.jobs.inner;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Maps.uniqueIndex;
import com.google.common.eventbus.Subscribe;
import static java.lang.String.format;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import org.cmdbuild.cluster.ClusterService;
import org.cmdbuild.config.ClusterBalancingStrategy;
import org.cmdbuild.config.JobsConfiguration;
import org.cmdbuild.config.SchedulerConfiguration;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.jobs.JobData;
import static org.cmdbuild.jobs.JobData.JOB_CONFIG_CLUSTER_BALANCING_STRATEGY;
import static org.cmdbuild.jobs.JobData.JOB_CONFIG_WORKGROUP;
import org.cmdbuild.jobs.JobRunService;
import org.cmdbuild.minions.MinionComponent;
import org.cmdbuild.minions.MinionHandler;
import org.cmdbuild.minions.MinionHandlerExt;
import org.cmdbuild.minions.MinionHandlerImpl;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_ERROR;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_NOTRUNNING;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_READY;
import org.cmdbuild.requestcontext.RequestContextService;
import org.cmdbuild.scheduler.JobSource;
import org.cmdbuild.scheduler.JobUpdatedEvent;
import org.cmdbuild.scheduler.ScheduledJobInfo;
import org.cmdbuild.scheduler.ScheduledJobInfoImpl;
import org.cmdbuild.scheduler.SchedulerService;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeUtc;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.cmdbuild.utils.sked.Sked;
import static org.cmdbuild.utils.sked.Sked.newSkedService;
import org.cmdbuild.utils.sked.SkedEnv;
import org.cmdbuild.utils.sked.SkedJob;
import org.cmdbuild.utils.sked.SkedJob.SkedJobTriggerType;
import static org.cmdbuild.utils.sked.SkedJob.SkedJobTriggerType.ST_CRON;
import static org.cmdbuild.utils.sked.SkedJob.SkedJobTriggerType.ST_TIMESTAMP;
import org.cmdbuild.utils.sked.SkedJobImpl;
import org.cmdbuild.utils.sked.SkedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class SchedulerServiceImpl implements SchedulerService, MinionComponent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ClusterService clusterService;
    private final SchedulerConfiguration schedulerConfiguration;
    private final Map<String, JobSource> jobSources;
    private final JobRunService runService;
    private final RequestContextService contextService;

    private final MinionHandlerExt minionHandler;
    private SkedService scheduler;

    public SchedulerServiceImpl(JobRunService runService, List<JobSource> jobSources, ClusterService clusterService, SchedulerConfiguration schedulerConfiguration, RequestContextService contextService, EventBusService busService) {
        this.schedulerConfiguration = checkNotNull(schedulerConfiguration);
        this.runService = checkNotNull(runService);
        this.clusterService = checkNotNull(clusterService);
        this.contextService = checkNotNull(contextService);
        this.jobSources = uniqueIndex(jobSources, JobSource::getJobSourceName);
        this.minionHandler = MinionHandlerImpl.builder()
                .withName("Scheduler Service (core)")
                .withConfigEnabler("org.cmdbuild.scheduler.enabled")
                .withEnabledChecker(schedulerConfiguration::isEnabled)
                .reloadOnConfigs(SchedulerConfiguration.class, JobsConfiguration.class)
                .withOrder(10)
                .build();
        busService.getDaoEventBus().register(new Object() {
            @Subscribe
            public void handleJobUpdateEvent(JobUpdatedEvent event) {
                try {
                    if (isReady() && isEnabled()) {
                        doReconfigureJobs();//TODO standard minion job reload !!
                    }
                } catch (Exception ex) {
                    logger.error("error processing job update event = {}", event, ex);
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
        logger.info("start system scheduler service");
        doReconfigureJobs();
    }

    @Override
    public void stop() {
        destroySchedulerSafe();
    }

    @Override
    public List<ScheduledJobInfo> getConfiguredJobs() {
        List<ScheduledJobInfo> list = list();
        if (isReady()) {
            scheduler.getJobInfos().stream().map(j -> ScheduledJobInfoImpl.builder()
                    .withCode(j.getCode())
                    .withTrigger(j.getJob().getTrigger())
                    .withRunning(j.isRunning())
                    .withLastRun(j.getLastRun())
                    .withClusterMode(j.getClusterMode())
                    .build()).forEach(list::add);
        }
        return list;
    }

    @Override
    public void runJob(String code) {
        if (isReady()) {
            scheduler.runJobNow(code);
        } else {
            try (SkedService sked = newSkedService()) {
                sked.addJobs(buildSkedJobList()).runJobNow(code);
            }
        }
    }

    @Override
    public void runJobsForTimeRange(ZonedDateTime startInclusive, ZonedDateTime endInclusive) {
        logger.info("run all jobs from time =< {} > to time =< {} >", toIsoDateTime(startInclusive), toIsoDateTime(endInclusive));
        try (SkedService sked = newSkedService()) {
            sked.addJobs(buildSkedJobList()).runJobsInclusive(startInclusive, endInclusive);
        }
    }

    private synchronized void doReconfigureJobs() {
        destroySchedulerSafe();
        logger.info("start scheduler service");
        try {
            scheduler = Sked.startSkedService(new ClusteredSkedEnv()).withJobs(buildSkedJobList());
            minionHandler.setStatus(MRS_READY);
        } catch (Exception ex) {
            minionHandler.setStatus(MRS_ERROR);
            throw runtime(ex);
        }
    }

    private synchronized void destroySchedulerSafe() {
        if (scheduler != null) {
            logger.info("stop scheduler service");
            try {
                if (scheduler.isRunning()) {
                    scheduler.stop();
                }
                checkArgument(scheduler.isShutdown(), "scheduler failed to stop");
            } catch (Exception ex) {
                logger.warn(marker(), "error stopping scheduler", ex);
            }
            scheduler = null;
            logger.info("system scheduler service stopped");
        }
        minionHandler.setStatus(MRS_NOTRUNNING);
    }

    private List<SkedJob> buildSkedJobList() {
        return jobSources.values().stream().flatMap((js) -> js.getJobs().stream().map(j -> buildJob(js, j))).collect(toImmutableList());
    }

    private SkedJob buildJob(JobSource jobSource, JobData job) {
        checkNotNull(jobSource);
        checkNotNull(job);
        String jobCode = format("%s.%s", jobSource.getJobSourceName(), job.getCode());
        String trigger;
        SkedJobTriggerType triggerType;
        if (job.isRunOnce()) {
            trigger = toIsoDateTimeUtc(job.getRunOnceTimestamp());
            triggerType = ST_TIMESTAMP;
        } else {
            trigger = checkNotBlank(job.getCronExpression(), "missing cron expression");
            if (!job.cronExpressionHasSeconds()) {
                trigger = "0 " + trigger;
            }
            triggerType = ST_CRON;
        }
        logger.debug("prepare job runner for source = {} job = {}", jobSource, jobCode);
        return SkedJobImpl.builder()
                .withCode(jobCode)
                .withTrigger(trigger)
                .withTriggerType(triggerType)
                .withClusterMode(job.getClusterMode())
                .withConfig(job.getConfig())
                .withRunnable(() -> {
                    MDC.put("cm_type", "job");
                    MDC.put("cm_id", "sked");
                    contextService.initCurrentRequestContext("sked job service");
                    logger.debug("start job =< {} >", jobCode);
                    try {
                        runService.runJobSafe(job);
                        logger.debug("completed job =< {} >", jobCode);
                    } catch (Exception ex) {
                        logger.error("error running job =< {} >", jobCode, ex);
                    } finally {
                        contextService.destroyCurrentRequestContext();
                        MDC.clear();
                    }
                })
                .build();
    }

    private class ClusteredSkedEnv implements SkedEnv {

        @Override
        public boolean isMasterNode() {
            return clusterService.isSingleActiveNode() || clusterService.isFirstNode();
        }

        @Override
        public boolean isMasterNodeForJob(SkedJob job, ZonedDateTime fireTimestamp) {
            ClusterBalancingStrategy clusterBalancingStrategy = parseEnumOrDefault(job.getConfig().get(JOB_CONFIG_CLUSTER_BALANCING_STRATEGY), schedulerConfiguration.getClusterBalancingStrategy());
            String key = switch (clusterBalancingStrategy) {
                case CB_AUTO ->
                    key(job.getCode(), fireTimestamp.toEpochSecond());
                case CB_PIN_JOB_TO_NODE ->
                    job.getCode();
            };
            return clusterService.isActiveNodeForKey(key, job.getConfig().get(JOB_CONFIG_WORKGROUP));
        }

    }

}
