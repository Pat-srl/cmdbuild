/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.sysmon;

import com.google.common.eventbus.Subscribe;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.time.ZonedDateTime;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static java.util.stream.Collectors.joining;
import jakarta.annotation.PreDestroy;
import org.cmdbuild.eventbus.EventBusService;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.io.CmPlatformUtils.getProcessMemoryMegs;
import static org.cmdbuild.utils.lang.CmExecutorUtils.namedThreadFactory;
import static org.cmdbuild.utils.lang.CmExecutorUtils.safe;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import static org.cmdbuild.utils.lang.CmExecutorUtils.sleepSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SystemEventProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ExecutorService executor = Executors.newSingleThreadExecutor(namedThreadFactory(getClass()));
    private ZonedDateTime lastGcEvent = now();

    public SystemEventProcessor(EventBusService eventService) {
        eventService.getSystemEventBus().register(new Object() {
            @Subscribe
            public void handleLowMemoryEvent(LowMemoryEvent event) {
                executor.submit(safe(SystemEventProcessor.this::handleLowMemoryEvent));
            }
        });
    }

    @PreDestroy
    public void cleanup() {
        shutdownQuietly(executor);
    }

    private void handleLowMemoryEvent() {
        logger.debug("received low memory event");
        ZonedDateTime currentTime = now();
        if (lastGcEvent.until(currentTime, SECONDS) > 10) {
            logger.info("low memory detected, trigger GC =< {} >", ManagementFactory.getGarbageCollectorMXBeans().stream().filter(g -> g.isValid()).map(GarbageCollectorMXBean::getName).collect(joining(", ")));
            long systemMemoryUsedBefore = getProcessMemoryMegs(), javaMemoryUsedBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            System.gc();
            lastGcEvent = currentTime;
            sleepSafe(500);
            long systemMemoryUsedAfter = getProcessMemoryMegs(), javaMemoryUsedAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(),
                    systemMemReleased = systemMemoryUsedBefore - systemMemoryUsedAfter, javaMemReleasedMegs = (javaMemoryUsedBefore - javaMemoryUsedAfter) / 1000000;
            logger.info("GC completed, released {} MB of java memory, {} MB of system memory{}", javaMemReleasedMegs, systemMemReleased, (javaMemReleasedMegs + systemMemReleased < 200) ? " (ineffective!)" : "");
        }
    }

}
