/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.sked.inner;

import jakarta.annotation.Nullable;
import static java.lang.Long.max;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang3.tuple.Pair;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeLocal;
import static org.cmdbuild.utils.date.CmDateUtils.toUserDuration;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExecutorUtils.awaitCompletionIgnoreInterrupt;
import static org.cmdbuild.utils.lang.CmExecutorUtils.namedThreadFactory;
import static org.cmdbuild.utils.lang.CmExecutorUtils.safe;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.cmdbuild.utils.sked.DefaultSkedEnv;
import org.cmdbuild.utils.sked.SkedEnv;
import org.cmdbuild.utils.sked.SkedJob;
import static org.cmdbuild.utils.sked.SkedJobClusterMode.RUN_ON_SINGLE_NODE;
import org.cmdbuild.utils.sked.SkedJobInfo;
import org.cmdbuild.utils.sked.SkedService;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import org.quartz.CronTrigger;
import org.quartz.spi.MutableTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkedServiceImpl implements SkedService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ScheduledExecutorService main = Executors.newSingleThreadScheduledExecutor(namedThreadFactory(SkedServiceImpl.class));
    private final ExecutorService worker = Executors.newCachedThreadPool(namedThreadFactory(SkedServiceImpl.class));
    private final List<SkedJob> jobs = new CopyOnWriteArrayList<>();
    private final List<SkedTrigger> triggers = new CopyOnWriteArrayList<>();
    private final Set<String> activeJobs = ConcurrentHashMap.newKeySet();
    private final Map<String, ZonedDateTime> lastJobRuns = new ConcurrentHashMap<>();

    private final SkedEnv env;

    private boolean isClosed = false, isPaused = true;

    public SkedServiceImpl() {
        this(new DefaultSkedEnv());
    }

    public SkedServiceImpl(SkedEnv env) {
        this.env = checkNotNull(env);
    }

    @Override
    public SkedService addJobs(Iterable<SkedJob> jobs) {
        checkNotClosed();
        checkNotNull(jobs);
        runOnMain(() -> {
            jobs.forEach(j -> {
                checkNotNull(j);
                this.jobs.stream().forEach(jj -> checkArgument(!Objects.equals(jj.getCode(), j.getCode()), "duplicated job code =< %s >", j.getCode()));
            });
            jobs.forEach(job -> {
                this.jobs.add(job);
            });
            if (!isPaused) {
                reloadTriggers();
            }
        });
        return this;
    }

    @Override
    public void removeJob(String jobCode) {
        checkNotClosed();
        checkNotBlank(jobCode);
        runOnMain(() -> {
            checkArgument(jobs.removeIf(j -> Objects.equals(j.getCode(), jobCode)), "job not found for code =< %s >", jobCode);
            if (!isPaused) {
                reloadTriggers();
            }
        });
    }

    @Override
    public List<SkedJob> getJobs() {
        return jobs;
    }

    @Override
    public SkedService pause() {
        checkNotClosed();
        logger.debug("pause scheduler");
        runOnMain(() -> {
            isPaused = true;
            clearTriggers();
        });
        return this;
    }

    @Override
    public SkedService start() {
        checkNotClosed();
        logger.debug("start scheduler");
        runOnMain(() -> {
            isPaused = false;
            reloadTriggers();
        });
        return this;
    }

    @Override
    public boolean isRunning() {
        return !isPaused && !isClosed;
    }

    @Override
    public void runJobs(ZonedDateTime from, ZonedDateTime to) {
        checkNotNull(from);
        checkNotNull(to);
        checkArgument(to.isAfter(from));
        logger.debug("trigger jobs from time =< {} > to time =< {} >", toIsoDateTimeLocal(from), toIsoDateTimeLocal(to));
        List<Pair<SkedTrigger, ZonedDateTime>> toTrigger = buildTriggers(from).stream()
                .map(t -> Pair.of(t, t.getNextFireTime(from)))
                .filter(p -> p.getRight() != null && p.getRight().isBefore(to))
                .sorted(Comparator.comparing(p -> p.getRight(), Comparator.naturalOrder()))
                .collect(toList());
        if (!toTrigger.isEmpty()) {
            ZonedDateTime triggerTime = toTrigger.get(0).getRight();
            toTrigger.removeIf(t -> !Objects.equals(t.getRight(), triggerTime));
            toTrigger.forEach(t -> {
                SkedTrigger trigger = t.getLeft();
                ZonedDateTime dateTime = t.getRight();
                logger.debug("found next job =< {} > at time =< {} >", trigger.getJob().getCode(), toIsoDateTimeLocal(dateTime));
                awaitCompletionIgnoreInterrupt(worker.submit(() -> executeJobSafe(trigger.getJob(), dateTime)));
            });
            runJobs(triggerTime, to);
        }
    }

    @Override
    public void close() {
        if (!isClosed) {
            logger.debug("close scheduler");
            runOnMain(() -> {
                isClosed = true;
                clearTriggers();
            });
            shutdownQuietly(main, worker);
        }
    }

    @Override
    public boolean isRunning(String jobCode) {
        return activeJobs.contains(checkNotBlank(jobCode));
    }

    @Override
    public List<SkedJobInfo> getJobInfos() {
        return list(jobs).map(j -> new SkedJobInfoImpl(j, isRunning(j.getCode()), lastJobRuns.get(j.getCode())));
    }

    @Override
    public void runJobNow(String code) {
        runOnMain(() -> {
            awaitCompletionIgnoreInterrupt(worker.submit(() -> {
                doExecuteJob(getJobInfo(code).getJob());
            }));
        });
    }

    private void runOnMain(Runnable runnable) {
        awaitCompletionIgnoreInterrupt(main.submit(runnable));
    }

    private void reloadTriggers() {
        clearTriggers();

        logger.debug("load triggers");
        triggers.addAll(buildTriggers());
        triggers.forEach(SkedTrigger::scheduleNextFireTime);
    }

    private List<SkedTrigger> buildTriggers() {
        return buildTriggers(now());
    }

    private List<SkedTrigger> buildTriggers(ZonedDateTime from) {
        return list(jobs).map(j -> {
            try {
                return switch (j.getTriggerType()) {
                    case ST_CRON -> {
                        MutableTrigger cron = cronSchedule(j.getTrigger()).build();
                        cron.setStartTime(new Date(from.toInstant().toEpochMilli() - 1000));//TODO check this
                        yield new CronSkedTrigger(j, (CronTrigger) cron);
                    }
                    case ST_TIMESTAMP ->
                        new TimestampSkedTrigger(j, toDateTime(j.getTrigger()));
                };
            } catch (Exception ex) {
                logger.error(marker(), "error processing trigger for job =< {} > (job will not run)", j, ex);
                return null;
            }
        }).filter(Objects::nonNull);
    }

    private void clearTriggers() {
        logger.debug("clear triggers");
        triggers.forEach(SkedTrigger::invalidate);
        triggers.clear();
    }

    private void checkNotClosed() {
        checkArgument(!isClosed, "sked service is closed");
    }

    private void executeJobSafe(SkedJob job, ZonedDateTime fireTimestamp) {
        logger.debug("processing job = {} with fire time =< {} >", job, toIsoDateTimeLocal(fireTimestamp));
        if (job.hasClusterMode(RUN_ON_SINGLE_NODE) && !env.isMasterNodeForJob(job, fireTimestamp)) {
            logger.debug("skip job =< {} > (running on non-master cluster node)", job.getCode());
        } else {
            try {
                doExecuteJob(job);
            } catch (Exception ex) {
                logger.error("error executing job =< {} > ", job.getCode(), ex);
            }
        }
    }

    private void doExecuteJob(SkedJob job) {
        logger.debug("execute job =< {} >", job.getCode());
        checkArgument(activeJobs.add(job.getCode()), "cannot execute job = %s : job is already running", job);
        lastJobRuns.put(job.getCode(), now());
        try {
            job.run();
            logger.debug("completed run for job =< {} >", job.getCode());
        } finally {
            activeJobs.remove(job.getCode());
        }
    }

    private abstract class SkedTrigger {

        protected final SkedJob job;
        protected boolean isValid = true;

        public SkedTrigger(SkedJob job) {
            this.job = checkNotNull(job);
        }

        public SkedJob getJob() {
            return job;
        }

        public void invalidate() {
            isValid = false;
        }

        public boolean isValid() {
            return isValid;
        }

        public void scheduleNextFireTime() {
            ZonedDateTime nextFireTime = getNextFireTimeOrNull();
            if (nextFireTime != null) {//TODO improve this, handle missing/invalid next fire time!
                long delayMillis = max(nextFireTime.toInstant().toEpochMilli() - System.currentTimeMillis(), 0);
                logger.debug("schedule next fire time for job =< {} > at date = {} ( {} away )", job.getCode(), toIsoDateTimeLocal(nextFireTime), toUserDuration(Duration.ofMillis(delayMillis)));
                main.schedule(safe(() -> {
                    if (isValid) {
                        worker.submit(safe(() -> {
                            if (isRunning(job.getCode())) {
                                logger.warn("job =< {} > is already running, skip run", job.getCode());
                            } else {
                                executeJobSafe(job, toDateTime(nextFireTime));
                            }
                            if (isValid) {
                                runOnMain(() -> {
                                    if (isValid) {
                                        scheduleNextFireTime();
                                    } else {
                                        logger.trace("processing expired trigger for job =< {} >, skipping", job.getCode());
                                    }
                                });
                            }
                        }));
                    } else {
                        logger.trace("processing expired trigger for job =< {} >, skipping", job.getCode());
                    }
                }), delayMillis, TimeUnit.MILLISECONDS);
            }
        }

        abstract ZonedDateTime getNextFireTime(ZonedDateTime from);

        abstract ZonedDateTime getNextFireTimeOrNull();

    }

    private class CronSkedTrigger extends SkedTrigger {

        private final CronTrigger cron;

        public CronSkedTrigger(SkedJob job, CronTrigger cron) {
            super(job);
            this.cron = checkNotNull(cron);
            logger.debug("build sked trigger with cron =< {} >", cron.getCronExpression());
        }

        @Nullable
        @Override
        public ZonedDateTime getNextFireTime(ZonedDateTime from) {
            return ZonedDateTime.ofInstant(cron.getFireTimeAfter(new Date(from.toInstant().toEpochMilli())).toInstant(), ZoneId.systemDefault());
        }

        @Nullable
        @Override
        public ZonedDateTime getNextFireTimeOrNull() {
            Date nextFireTime = checkNotNull(cron.getFireTimeAfter(new Date()));
            return toDateTime(nextFireTime);
        }

    }

    private class TimestampSkedTrigger extends SkedTrigger {

        private final ZonedDateTime timestamp;

        public TimestampSkedTrigger(SkedJob job, ZonedDateTime timestamp) {
            super(job);
            this.timestamp = checkNotNull(timestamp);
            logger.debug("build sked trigger with timestamp =< {} >", toIsoDateTimeLocal(timestamp));
        }

        @Nullable
        @Override
        public ZonedDateTime getNextFireTime(ZonedDateTime from) {
            return timestamp.isAfter(from) ? timestamp : null;//ZonedDateTime.ofInstant(cron.getFireTimeAfter(new Date(from.toInstant().toEpochMilli())).toInstant(), ZoneId.systemDefault());
        }

        @Nullable
        @Override
        public ZonedDateTime getNextFireTimeOrNull() {
            return getNextFireTime(now());
        }
    }
}
