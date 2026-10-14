/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.inner;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Stopwatch;
import com.google.common.eventbus.EventBus;
import jakarta.annotation.Nullable;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Provider;
import static java.lang.String.format;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.cluster.NodeIdProvider;
import org.cmdbuild.config.SchedulerConfiguration;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.fault.FaultEventCollector;
import org.cmdbuild.fault.FaultEventCollectorService;
import org.cmdbuild.jobs.HardJobTimeoutEvent;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobException;
import static org.cmdbuild.jobs.JobExecutorService.JOBUSER_SYSTEM;
import org.cmdbuild.jobs.JobRun;
import org.cmdbuild.jobs.JobRunContext;
import org.cmdbuild.jobs.JobRunRepository;
import org.cmdbuild.jobs.JobRunService;
import static org.cmdbuild.jobs.JobRunStatus.JRS_COMPLETED;
import static org.cmdbuild.jobs.JobRunStatus.JRS_FAILED;
import static org.cmdbuild.jobs.JobRunStatus.JRS_RUNNING;
import org.cmdbuild.jobs.JobRunnerRepository;
import org.cmdbuild.jobs.JobSessionService;
import org.cmdbuild.jobs.JobSessionService.JobSessionContext;
import org.cmdbuild.jobs.JobTimeoutEvent;
import org.cmdbuild.jobs.SoftJobTimeoutEvent;
import org.cmdbuild.jobs.beans.JobDataImpl;
import org.cmdbuild.jobs.beans.JobRunImpl;
import org.cmdbuild.lock.AutoCloseableItemLock;
import static org.cmdbuild.lock.LockScope.LS_REQUEST;
import org.cmdbuild.lock.LockService;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.date.CmDateUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.date.CmDateUtils.toDuration;
import static org.cmdbuild.utils.date.CmDateUtils.toUserDuration;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.printStackTrace;
import static org.cmdbuild.utils.lang.CmExecutorUtils.awaitCompletionIgnoreInterrupt;
import static org.cmdbuild.utils.lang.CmExecutorUtils.executorService;
import static org.cmdbuild.utils.lang.CmExecutorUtils.safe;
import static org.cmdbuild.utils.lang.CmExecutorUtils.scheduledExecutorService;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNullOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.EventBusUtils.logExceptions;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import static org.cmdbuild.utils.sked.SkedJobClusterMode.RUN_ON_SINGLE_NODE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class JobRunServiceImpl implements JobRunService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final JobRunRepository jobRunRepository;
    private final FaultEventCollectorService errorAndWarningCollectorService;
    private final JobSessionService jobSessionService;
    private final Provider<JobRunnerRepository> jobRunnerRepository;
    private final NodeIdProvider nodeIdProvider;
    private final LockService lockService;
    private final SessionService sessionService;
    private final SchedulerConfiguration config;
    private final EventBus eventBus;

    private final ExecutorService executorService;
    private final ScheduledExecutorService monitorExecutorService;

    public JobRunServiceImpl(EventBusService eventBusService, SchedulerConfiguration config, SessionService sessionService, RequestContextService contextService, JobRunRepository jobRunRepository, FaultEventCollectorService errorAndWarningCollectorService, JobSessionService jobSessionService, Provider<JobRunnerRepository> jobRunnerRepository, NodeIdProvider nodeIdProvider, LockService lockService) {
        this.jobRunRepository = checkNotNull(jobRunRepository);
        this.errorAndWarningCollectorService = checkNotNull(errorAndWarningCollectorService);
        this.jobSessionService = checkNotNull(jobSessionService);
        this.jobRunnerRepository = checkNotNull(jobRunnerRepository);
        this.nodeIdProvider = checkNotNull(nodeIdProvider);
        this.lockService = checkNotNull(lockService);
        this.sessionService = checkNotNull(sessionService);
        this.config = checkNotNull(config);
        this.eventBus = eventBusService.getJobRunEventBus();
        checkNotNull(contextService);
        executorService = executorService("job_run_service", () -> contextService.initCurrentRequestContext("job run service"), contextService::destroyCurrentRequestContext);
        monitorExecutorService = scheduledExecutorService("job_run_monitor", () -> contextService.initCurrentRequestContext("job run monitor"), contextService::destroyCurrentRequestContext);
    }

    @PreDestroy
    public void cleanup() {
        shutdownQuietly(monitorExecutorService);
        shutdownQuietly(executorService);
    }

    @Override
    public JobRun runJobSafe(JobData data) {
        return doRunJob(data, true, null);
    }

    @Override
    public JobRun runJob(JobData data) {
        return doRunJob(data, false, null);
    }

    @Override
    public Future<JobRun> runJobLater(JobData job, @Nullable Object listener) {
        logger.debug("submit batch job = {}", job);
        JobData actual = JobDataImpl.copyOf(job).accept(j -> {
            if (job.useCurrentSessionContext()) {
                j.withSessionId(sessionService.getCurrentSessionId()).withUseCurrentSessionContext(false);
            }
        }).build();
        return executorService.submit(() -> doRunJob(actual, true, listener));
    }

    public JobRun doRunJob(JobData data, boolean safe, @Nullable Object listener) {
        logger.debug("preparing execution for job = {}", data);
        JobRunner jobRunner = new JobRunner(data);
        if (listener != null) {
            jobRunner.getThisEventBus().register(listener);
        }
        try {
            jobRunner.prepareAndRunJob();
        } catch (Exception ex) {
            if (safe) {
                logger.error("error executing job = {}", data, ex);
            } else {
                throw new JobException(ex, "error executing job = %s", data);
            }
        }
        return checkNotNull(jobRunner.getJobRun(), "job run failed");
    }

    private static class JobRunCompletedEventImpl implements JobRunCompletedEvent {

        final JobData job;
        final JobRun jobRun;

        public JobRunCompletedEventImpl(JobData job, JobRun jobRun) {
            this.job = checkNotNull(job);
            this.jobRun = checkNotNull(jobRun);
        }

        @Override
        public JobData getJob() {
            return job;
        }

        @Override
        public JobRun getJobRun() {
            return jobRun;
        }

        @Override
        public String toString() {
            return "JobRunCompletedEvent{" + "job=" + job + ", jobRun=" + jobRun + '}';
        }

    }

    private class JobRunner {

        private final EventBus thisEventBus = new EventBus(logExceptions(logger));

        private final JobData job;
        private FaultEventCollector eventCollector;
        private JobRun jobRun;
        private Stopwatch stopwatch;
        private boolean success;
        private final Map<String, String> output = map();

        private JobTimeoutMonitor timeoutMonitor;
        private Thread jobThread;

        public JobRunner(JobData job) {
            this.job = checkNotNull(job);
        }

        public EventBus getThisEventBus() {
            return thisEventBus;
        }

        public JobRun prepareAndRunJob() throws Exception {
            prepareRun();
            try {
                if (job.useCurrentSessionContext()) {
                    executeJob();
                } else {
                    awaitCompletionIgnoreInterrupt(executorService.submit(this::setContextAndExecuteJob));
                }
                success = true;
            } catch (Exception ex) {
                success = false;
                eventCollector.addError(ex);
                throw ex;
            } finally {
                saveRunExitStatusAndErrors();
                postEvent(new JobRunCompletedEventImpl(job, jobRun));
            }
            return jobRun;
        }

        @Nullable
        public JobRun getJobRun() {
            return jobRun;
        }

        private boolean requireLock() {
            return equal(job.getClusterMode(), RUN_ON_SINGLE_NODE);
        }

        private void prepareRun() {
            jobRun = JobRunImpl.builder()
                    .withNodeId(nodeIdProvider.getNodeId())
                    .withJobCode(job.getCode())
                    .withJobStatus(JRS_RUNNING)
                    .withTimestamp(now())
                    .withCompleted(false)
                    .build();
            if (job.persistJobRun()) {
                jobRun = jobRunRepository.create(jobRun);
            }
            eventCollector = errorAndWarningCollectorService.newEventCollector();
            stopwatch = Stopwatch.createStarted();
            if (config.isTimeoutEnabled()) {
                timeoutMonitor = new JobTimeoutMonitor();
                timeoutMonitor.startMonitor();
            }
        }

        private void saveRunExitStatusAndErrors() {
            timeoutMonitor.stopMonitor();
            long elapsedTimeMillis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            jobRun = JobRunImpl.copyOf(jobRun)
                    .withCompleted(true)
                    .withElapsedTime(elapsedTimeMillis)
                    .withJobStatus(success ? JRS_COMPLETED : JRS_FAILED)
                    .withErrorMessages(eventCollector.getCollectedEvents())
                    .withLogs(eventCollector.getLogs()) //TODO make this configurable
                    .withMetadata(output)
                    .build();
            if (job.persistJobRun()) {
                jobRun = jobRunRepository.update(jobRun);
            } else if (jobRun.hasErrors()) {
                jobRun = jobRunRepository.create(jobRun);
            }
        }

        private void setContextAndExecuteJob() {
            String ctx = toStringNotBlank(firstNotNull(jobRun.getId(), randomId(8)));
            if (job.hasModule()) {
                MDC.put("cm_type", "mod");
                MDC.put("cm_mod", checkNotBlank(job.getModule()));
            } else {
                MDC.put("cm_type", "job");
            }
            MDC.put("cm_id", format("jobrun:%s", ctx));
            JobSessionContext helper = job.hasSessionId()
                    ? jobSessionService.createJobSessionContextWithExistingSession(job.getSessionId(), "jobrun_%s", ctx)
                    : jobSessionService.createJobSessionContextWithUser(firstNotBlank(job.getSessionUser(), JOBUSER_SYSTEM), "jobrun_%s", ctx);
            errorAndWarningCollectorService.getCurrentRequestEventCollector().enableFullLogCollection();//TODO make this configurable 
            try {
                executeJob();
            } finally {
                eventCollector.copyErrorsAndLogsFrom(errorAndWarningCollectorService.getCurrentRequestEventCollector());
                helper.destroyJobSessionContext();
                MDC.clear();
            }
        }

        private void executeJob() {
            if (requireLock()) {
                try (AutoCloseableItemLock lock = lockService.aquireLockOrFail(key(job.getType(), job.getCode()), LS_REQUEST)) {
                    doExecuteJob();
                }
            } else {
                doExecuteJob();
            }
        }

        private void doExecuteJob() {
            jobThread = Thread.currentThread();
            logger.debug("executing job type = {} id = {} code = {}", job.getType(), job.getId(), job.getCode());
            output.putAll(checkNotNull(jobRunnerRepository.get().getJobRunner(job.getType()).runJobWithOutput(new MyJobRunContext(job, jobRun))));
        }

        private void postEvent(Object event) {
            thisEventBus.post(event);
            eventBus.post(event);
        }

        private class JobTimeoutMonitor {

            private final Duration softTimeout, hardTimeout;
            private final List<Future> monitors = list();

            public JobTimeoutMonitor() {
                this.softTimeout = firstNotNullOrNull(toDuration(job.getConfig("softTimeout")), config.getSoftTimeout());
                this.hardTimeout = firstNotNullOrNull(toDuration(job.getConfig("hardTimeout")), config.getHardTimeout());
            }

            public void startMonitor() {
                if (isNotNullAndGtZero(softTimeout)) {
                    logger.debug("set soft job timeout = {} for job run = {}", toUserDuration(softTimeout), jobRun);
                    monitors.add(monitorExecutorService.schedule(safe(this::softFail), softTimeout.toMillis(), TimeUnit.MILLISECONDS));
                }
                if (isNotNullAndGtZero(hardTimeout)) {
                    logger.debug("set hard job timeout = {} for job run = {}", toUserDuration(hardTimeout), jobRun);
                    monitors.add(monitorExecutorService.schedule(safe(this::hardFail), hardTimeout.toMillis(), TimeUnit.MILLISECONDS));
                }
            }

            public void stopMonitor() {
                monitors.forEach(f -> f.cancel(true));
                monitors.clear();
            }

            private void softFail() {
                logger.warn(marker(), "job run = {} on thread = {} is still running after {} (soft timeout)", jobRun, jobThread, toUserDuration(stopwatch.elapsed()));
                logger.warn("thread stack trace = {}", printStackTrace(jobThread));
                postEvent(new SoftJobTimeoutEventImpl(job, jobRun, jobThread.getName(), printStackTrace(jobThread)));
            }

            private void hardFail() {
                logger.error(marker(), "job run = {} on thread = {} is still running after {} (hard timeout)", jobRun, jobThread, toUserDuration(stopwatch.elapsed()));
                logger.error("thread stack trace = {}", printStackTrace(jobThread));
                postEvent(new HardJobTimeoutEventImpl(job, jobRun, jobThread.getName(), printStackTrace(jobThread)));
            }

        }

    }

    private class MyJobRunContext implements JobRunContext {

        private final JobData job;
        private final JobRun jobRun;

        public MyJobRunContext(JobData job, JobRun jobRun) {
            this.job = checkNotNull(job);
            this.jobRun = checkNotNull(jobRun);
        }

        @Override
        public JobData getJob() {
            return job;
        }

        @Override
        @Nullable
        public Long getJobRunId() {
            return jobRun.getId();
        }

        @Override
        public String getRunId() {
            return jobRun.getRunId();
        }

        @Override
        public String toString() {
            return "JobRunContext{" + "job=" + job + ", runId=" + getRunId() + '}';
        }

    }

    private static class SoftJobTimeoutEventImpl extends JobTimeoutEventImpl implements SoftJobTimeoutEvent {

        public SoftJobTimeoutEventImpl(JobData job, JobRun run, String thread, String stacktrace) {
            super(job, run, thread, stacktrace);
        }

    }

    private static class HardJobTimeoutEventImpl extends JobTimeoutEventImpl implements HardJobTimeoutEvent {

        public HardJobTimeoutEventImpl(JobData job, JobRun run, String thread, String stacktrace) {
            super(job, run, thread, stacktrace);
        }

    }

    private static abstract class JobTimeoutEventImpl implements JobTimeoutEvent {

        private final JobData job;
        private final JobRun run;
        private final String thread, stacktrace;

        public JobTimeoutEventImpl(JobData job, JobRun run, String thread, String stacktrace) {
            this.job = checkNotNull(job);
            this.run = checkNotNull(run);
            this.thread = checkNotBlank(thread);
            this.stacktrace = checkNotBlank(stacktrace);
        }

        @Override
        public JobData getJob() {
            return job;
        }

        @Override
        public JobRun getRun() {
            return run;
        }

        @Override
        public String getThread() {
            return thread;
        }

        @Override
        public String getStackTrace() {
            return stacktrace;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" + "job=" + job + ", run=" + run + '}';
        }

    }

}
