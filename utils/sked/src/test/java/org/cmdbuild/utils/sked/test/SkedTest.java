/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.sked.test;

import static java.lang.Math.abs;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeLocal;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeUtc;
import static org.cmdbuild.utils.date.CmDateUtils.toJavaDate;
import static org.cmdbuild.utils.lang.CmExecutorUtils.sleepSafe;
import static org.cmdbuild.utils.lang.CmExecutorUtils.waitUntil;
import org.cmdbuild.utils.sked.Sked;
import org.cmdbuild.utils.sked.SkedJobImpl;
import org.cmdbuild.utils.sked.SkedService;
import org.cmdbuild.utils.testutils.IgnoreSlowTestRule;
import org.cmdbuild.utils.testutils.Slow;
import static org.hamcrest.Matchers.lessThan;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import org.junit.Rule;
import org.junit.Test;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import org.quartz.spi.MutableTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkedTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Rule
    public IgnoreSlowTestRule rule = new IgnoreSlowTestRule();

    private SkedService service;

    @After
    public void cleanup() {
        if (service != null) {
            service.stop();
            service = null;
        }
    }

    @Test
    @Slow
    public void testSkedJobs() {
        List<ZonedDateTime> runs = new CopyOnWriteArrayList<>();

        service = Sked.startSkedService().withJobs(SkedJobImpl.build("test", "*/5 * * * * ?", (Runnable) () -> {
            logger.info("job run");
            runs.add(now());
        }));

        waitUntil(() -> !runs.isEmpty());
        logger.info("await next run in 5 sec");
        waitUntil(() -> runs.size() > 1);
        service.stop();

        assertEquals(2, runs.size());
        long precision = abs(runs.get(1).toInstant().toEpochMilli() - runs.get(0).toInstant().toEpochMilli() - 5000);
        logger.info("scheduler precision = {} (execution at date = {} and = {} )", precision, toIsoDateTimeLocal(runs.get(0)), toIsoDateTimeLocal(runs.get(1)));
        assertThat("timestamp error", precision, lessThan(500l));

    }

    @Test
    public void testManualTrigger() {
        AtomicInteger runs = new AtomicInteger();

        service = Sked.startSkedService().pause().withJobs(SkedJobImpl.build("test", "0 */30 * * * ?", (Runnable) () -> {
            logger.info("job run");
            runs.incrementAndGet();
        }));

        assertEquals(0, runs.get());

        service.runJobNow("test");

        waitUntil(() -> runs.get() > 0, 500);
        assertEquals(1, runs.get());
    }

    @Test
    public void testManualTriggerOfMany() {

        AtomicInteger runs = new AtomicInteger();

        service = Sked.startSkedService().pause().withJobs(SkedJobImpl.build("test", "0 */30 * * * ?", (Runnable) () -> {
            logger.info("job run");
            runs.incrementAndGet();
        }));

        assertEquals(0, runs.get());

        service.runJobs(toDateTime("2020-01-10T15:10:00Z"), toDateTime("2020-01-10T15:25:00Z"));

        assertEquals(0, runs.get());

        service.runJobs(toDateTime("2020-01-10T15:10:00Z"), toDateTime("2020-01-10T15:35:00Z"));

        assertEquals(1, runs.get());

        service.runJobs(toDateTime("2020-01-10T15:10:00Z"), toDateTime("2020-01-10T16:05:00Z"));

        assertEquals(3, runs.get());

        service.runJobs(toDateTime("2020-01-10T15:00:00Z"), toDateTime("2020-01-10T15:25:00Z"));

        assertEquals(3, runs.get());

        service.runJobs(toDateTime("2020-01-10T15:00:00Z"), toDateTime("2020-01-10T15:30:00Z"));

        assertEquals(3, runs.get());

        service.runJobs(toDateTime("2020-01-10T15:00:00Z"), toDateTime("2020-01-10T15:35:00Z"));

        assertEquals(4, runs.get());

        service.runJobs(toDateTime("2020-01-10T14:59:00Z"), toDateTime("2020-01-10T15:35:00Z"));

        assertEquals(6, runs.get());

        service.runJobsInclusive(toDateTime("2020-01-10T15:00:00Z"), toDateTime("2020-01-10T15:30:00Z"));

        assertEquals(8, runs.get());
    }

    @Test
    public void testNextFireTime() {
        MutableTrigger cron = cronSchedule("*/10 * * * * ?").build();
        cron.setStartTime(toJavaDate("2020-01-10T10:00:00Z"));//must set start time before first time
        assertEquals("2020-01-10T15:10:10Z", toIsoDateTimeUtc(cron.getFireTimeAfter(toJavaDate("2020-01-10T15:10:00Z"))));
        assertEquals("2020-01-10T15:10:20Z", toIsoDateTimeUtc(cron.getFireTimeAfter(toJavaDate("2020-01-10T15:10:10Z"))));
    }

    @Test
    @Slow
    public void testJobsDoesNotOverlap1() {
        AtomicBoolean isRunning = new AtomicBoolean(false),
                failed = new AtomicBoolean(false);

        AtomicInteger runs = new AtomicInteger();

        service = Sked.startSkedService().withJobs(SkedJobImpl.build("test", "*/1 * * * * ?", (Runnable) () -> {
            logger.info("job run");
            int r = runs.incrementAndGet();
            if (isRunning.getAndSet(true)) {
                logger.error("failure");
                failed.set(true);
            }
            for (int i = 0; i < 50; i++) {
                sleepSafe(100);
                if (r != runs.get()) {
                    logger.error("failure");
                    failed.set(true);
                }
            }
            isRunning.set(false);
        }));

        sleepSafe(3000);

        assertFalse(failed.get());
        assertEquals(1, runs.get());

        sleepSafe(4000);

        assertFalse(failed.get());
        assertEquals(2, runs.get());
    }

    @Test
    @Slow
    public void testJobsDoesNotOverlap2() {
        AtomicBoolean isRunning = new AtomicBoolean(false),
                failed = new AtomicBoolean(false);

        AtomicInteger runs = new AtomicInteger();

        service = Sked.startSkedService().withJobs(SkedJobImpl.build("test", "*/1 * * * * ?", (Runnable) () -> {
            logger.info("job run");
            int r = runs.incrementAndGet();
            if (isRunning.getAndSet(true)) {
                logger.error("failure");
                failed.set(true);
            }
            for (int i = 0; i < 50; i++) {
                sleepSafe(100);
                if (r != runs.get()) {
                    logger.error("failure");
                    failed.set(true);
                }
            }
            isRunning.set(false);
        }));

        sleepSafe(2000);

        assertFalse(failed.get());
        assertEquals(1, runs.get());

        service.addJobs(SkedJobImpl.build("test2", "0 0 0 * * ?", (Runnable) () -> {
        }));//reload jobs

        sleepSafe(2000);

        assertFalse(failed.get());
        assertEquals(1, runs.get());

        sleepSafe(3000);

        assertFalse(failed.get());
        assertEquals(2, runs.get());
    }

}
