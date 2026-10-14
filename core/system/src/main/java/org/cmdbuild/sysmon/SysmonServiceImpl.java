/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.sysmon;

import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.google.common.eventbus.EventBus;
import java.io.File;
import java.io.IOException;
import static java.lang.Math.toIntExact;
import static java.lang.String.format;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.cluster.NodeIdProvider;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.dao.ConfigurableDataSource;
import org.cmdbuild.dao.MyPooledDataSource;
import org.cmdbuild.debuginfo.BuildInfoService;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.minions.PostStartup;
import org.cmdbuild.scheduler.ScheduledJob;
import org.cmdbuild.sysmon.SystemStatusLogImpl.SystemStatusRecordImplBuilder;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmNetUtils.getHostname;
import static org.cmdbuild.utils.io.CmPlatformUtils.getProcessMemoryMegs;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SysmonServiceImpl implements SysmonService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CoreConfiguration coreConfiguration;
    private final SysmonRepository sysmonRepository;
    private final DirectoryService directoryService;
    private final NodeIdProvider nodeIdProvider;
    private final SessionService sessionService;
    private final BuildInfoService buildInfoService;
    private final ConfigurableDataSource dataSource;
    private final EventBus eventBus;

    public SysmonServiceImpl(EventBusService eventService, CoreConfiguration coreConfiguration, SysmonRepository sysmonRepository, DirectoryService directoryService, NodeIdProvider nodeIdProvider, SessionService sessionService, BuildInfoService buildInfoService, ConfigurableDataSource dataSource) {
        this.coreConfiguration = checkNotNull(coreConfiguration);
        this.sysmonRepository = checkNotNull(sysmonRepository);
        this.directoryService = checkNotNull(directoryService);
        this.nodeIdProvider = checkNotNull(nodeIdProvider);
        this.sessionService = checkNotNull(sessionService);
        this.buildInfoService = checkNotNull(buildInfoService);
        this.dataSource = checkNotNull(dataSource);
        this.eventBus = eventService.getSystemEventBus();
    }

    @ScheduledJob(value = "0 * * * * ?", persistRun = false)
    public void logSystemStatus() {
        sysmonRepository.store(getSystemRuntimeStatus());
    }

    @Override
    public SystemStatusLog getSystemRuntimeStatus() {
        return new SystemStatusChecker().gatherSystemStatus();
    }

    @PostStartup
    public void logSystemInfo() {
        logger.info("java runtime = {}", getJavaRuntimeInfo());
        logger.info("default charset = {}", Charset.defaultCharset().name());
    }

    private class SystemStatusChecker {

        private final SystemStatusRecordImplBuilder builder = SystemStatusLogImpl.builder();
        private final List<String> warnings = list();

        public SystemStatusLog gatherSystemStatus() {
            checkJavaMem();
            checkOsMemAndCpu();
            checkFilesystem();
            checkConnections();
            checkSessions();
            checkTest();
            attachWarnings();
            return builder.build();
        }

        private void checkJavaMem() {
            int heapMemMax = toIntExact((Runtime.getRuntime().maxMemory() / 1000 / 1000)),
                    heapMemTotal = toIntExact((Runtime.getRuntime().totalMemory() / 1000 / 1000)),
                    heapMemUsed = toIntExact(((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000 / 1000)),
                    metaspaceMemUsed = ManagementFactory.getMemoryPoolMXBeans().stream().filter(memoryMXBean -> equal("Metaspace", memoryMXBean.getName())).map(m -> toIntExact(m.getUsage().getUsed() / 1000 / 1000)).collect(toOptional()).orElse(0),
                    metaspaceMemTotal = ManagementFactory.getMemoryPoolMXBeans().stream().filter(memoryMXBean -> equal("Metaspace", memoryMXBean.getName())).map(m -> toIntExact(m.getUsage().getCommitted() / 1000 / 1000)).collect(toOptional()).orElse(0),
                    metaspaceMemMax = ManagementFactory.getMemoryPoolMXBeans().stream().filter(memoryMXBean -> equal("Metaspace", memoryMXBean.getName())).map(m -> m.getUsage().getMax() == Long.MAX_VALUE ? 0 : toIntExact(m.getUsage().getMax() / 1000 / 1000)).collect(toOptional()).orElse(0);

            int heapPercUsed = heapMemUsed * 100 / heapMemMax;
            logger.debug("java heap memory = {}% used ({}/{} GB)", heapPercUsed, heapMemUsed / 1000d, heapMemMax / 1000d);
            if (heapPercUsed > coreConfiguration.getGCHeapPercentage()) {
                addWarning("java heap memory almost exausted, %s%% used (%s/%s GB)", heapPercUsed, heapMemUsed / 1000d, heapMemMax / 1000d);
            }

            int metaspacePercUsed = metaspaceMemMax == 0 ? 0 : metaspaceMemUsed * 100 / metaspaceMemMax;
            logger.debug("java metaspace memory = {}% used ({}/{} GB)", metaspacePercUsed, metaspaceMemUsed / 1000d, metaspaceMemMax / 1000d);
            if (metaspacePercUsed > coreConfiguration.getGCMetaspacePercentage()) {
                addWarning("java metaspace memory almost exausted, %s%% used (%s/%s GB)", metaspacePercUsed, metaspaceMemUsed / 1000d, metaspaceMemMax / 1000d);
            }

            if (heapPercUsed > coreConfiguration.getGCHeapPercentage() || metaspacePercUsed > coreConfiguration.getGCMetaspacePercentage()) {
                eventBus.post(LowMemoryEvent.INSTANCE);
            }

            int javaMemUsed = heapMemUsed + metaspaceMemUsed,
                    javaMemTotal = heapMemTotal + metaspaceMemTotal,
                    javaMemMax = heapMemMax + metaspaceMemMax,
                    javaPercUsed = javaMemUsed * 100 / javaMemMax;

            logger.debug("java memory (heap+metaspace) = {}% used ({}/{} GB)", javaPercUsed, javaMemUsed / 1000d, javaMemMax / 1000d);

            long pid = ProcessHandle.current().pid();
            int procMemoryusedMegs = getProcessMemoryMegs();

            builder.withJavaMemoryUsed(javaMemUsed)
                    .withJavaMemoryTotal(javaMemTotal)
                    .withJavaMemoryMax(javaMemMax)
                    .withProcessMemoryUsed(procMemoryusedMegs)
                    .withJavaPid(toIntExact(pid))//TODO change to long
                    .withHostname(getHostname())
                    .withNodeId(nodeIdProvider.getNodeId());

            builder.withBuildInfo(buildInfoService.getBuildInfo().getCommitInfo());
        }

        private void checkOsMemAndCpu() {

            Integer systemMemAvailable = null, systemMemUsed = null;
            try {
                File file = new File("/proc/meminfo");
                if (file.canRead()) {
                    Matcher matcher = Pattern.compile("(?s).*?MemTotal:\\s*([0-9]+).*?MemFree:\\s*([0-9]+).*?Buffers:\\s*([0-9]+).*?Cached:\\s*([0-9]+).*").matcher(readToString(file));
                    checkArgument(matcher.find(), "invalid meminfo file format");
                    long memTotal = Long.parseLong(matcher.group(1)),
                            memFree = Long.parseLong(matcher.group(2)),
                            buffers = Long.parseLong(matcher.group(3)),
                            cache = Long.parseLong(matcher.group(4));
                    systemMemUsed = toIntExact((memTotal - memFree - cache - buffers) / 1000);
                    systemMemAvailable = toIntExact(memTotal / 1000);
                }
            } catch (Exception ex) {
                logger.debug("error reading system memory info", ex);
            }

            if (isNotNullAndGtZero(systemMemUsed) && isNotNullAndGtZero(systemMemAvailable)) {
                int percUsed = systemMemUsed * 100 / systemMemAvailable;
                logger.debug("system memory = {}% used ({}/{} GB)", percUsed, systemMemUsed / 1000d, systemMemAvailable / 1000d);
                if (percUsed > coreConfiguration.getGCSystemMemoryPercentage()) {
                    addWarning("system memory almost exausted, %s%% used (%s/%s GB)", percUsed, systemMemUsed / 1000d, systemMemAvailable / 1000d);
                    eventBus.post(LowMemoryEvent.INSTANCE);
                }
            }

            OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();

            double systemLoadAvg = operatingSystemMXBean.getSystemLoadAverage();
            int procNum = operatingSystemMXBean.getAvailableProcessors();

            logger.debug("system load avg = {} (on {} cpu)", systemLoadAvg, procNum);

            builder.withSystemMemoryTotal(systemMemAvailable)
                    .withSystemMemoryUsed(systemMemUsed)
                    .withLoadAvg(systemLoadAvg);

            double loadAvgPerCpu = systemLoadAvg / procNum;
            if (loadAvgPerCpu > 1) {
                addWarning("high cpu load, loadavg = %s, loadavg/cpu = %s", systemLoadAvg, loadAvgPerCpu);
            } else if (loadAvgPerCpu > 0.5 || systemLoadAvg > 1) {
                logger.debug("moderate cpu load detected, loadavg = {}, loadavg/cpu = {}", systemLoadAvg, loadAvgPerCpu);
            }
        }

        private void checkFilesystem() {
            logger.debug("check disk usage");
            if (directoryService.hasContainerDirectory()) {
                int filesystemMemTotal, filesystemMemFree, filesystemMemUsed;
                filesystemMemTotal = toIntExact((directoryService.getContainerDirectory().getTotalSpace() / 1000 / 1000));
                filesystemMemFree = toIntExact((directoryService.getContainerDirectory().getUsableSpace() / 1000 / 1000));
                filesystemMemUsed = filesystemMemTotal - filesystemMemFree;
                builder.withFilesystemMemoryTotal(filesystemMemTotal);
                builder.withFilesystemMemoryUsed(filesystemMemUsed);
            } else {
                builder.withFilesystemMemoryTotal(0);
                builder.withFilesystemMemoryUsed(0);
            }
            try {
                listOf(File.class).accept((list) -> {
                    if (directoryService.hasContainerDirectory()) {
                        list.add(directoryService.getContainerDirectory());
                    }
                    if (directoryService.hasConfigDirectory()) {
                        list.add(directoryService.getConfigDirectory());
                    }
                    if (directoryService.hasContainerLogDirectory()) {
                        list.add(directoryService.getContainerLogDirectory());
                    }
                    if (dataSource.hasAdminDataSource()) {
                        try {
                            dataSource.withAdminJdbcTemplate((j) -> {
                                String pgDataDir = j.queryForObject("SHOW data_directory;", String.class);//TODO check and improve this
                                if (isNotBlank(pgDataDir) && new File(pgDataDir).isDirectory()) {
                                    list.add(new File(pgDataDir));
                                }
                            });
                        } catch (Exception ex) {
                            logger.debug("unable to read postgres data dir", ex);
                        }
                    }
                }).distinct(rethrowFunction(f -> Files.getFileStore(f.toPath()).name())).forEach(file -> {
                    int filesystemMemTotal, filesystemMemFree, filesystemMemUsed, percUsed;
                    filesystemMemTotal = toIntExact((file.getTotalSpace() / 1000 / 1000));
                    filesystemMemFree = toIntExact((file.getUsableSpace() / 1000 / 1000));
                    filesystemMemUsed = filesystemMemTotal - filesystemMemFree;
                    percUsed = filesystemMemUsed * 100 / filesystemMemTotal;
                    logger.debug("check filesystem {}, {}% used ({}/{} GB)", file.getAbsolutePath(), percUsed, filesystemMemUsed / 1000d, filesystemMemTotal / 1000d);
                    if (percUsed > 95) {
                        addCritical("filesystem space almost exausted on %s, %s%% used (%s/%s GB)", file.getAbsolutePath(), percUsed, filesystemMemUsed / 1000d, filesystemMemTotal / 1000d);
                    } else if (percUsed > 85) {
                        addWarning("filesystem space almost exausted on %s, %s%% used (%s/%s GB)", file.getAbsolutePath(), percUsed, filesystemMemUsed / 1000d, filesystemMemTotal / 1000d);
                    }
                });
            } catch (IOException ex) {
                logger.error(marker(), "error executing filesystem check", ex);
            }
        }

        private void checkTest() {
            if (coreConfiguration.triggerSystemWarningForTest()) {
                addWarning("this is a system warning test");
            }
            if (coreConfiguration.triggerSystemCriticalForTest()) {
                addCritical("this is a system error test");
            }
        }

        private void checkSessions() {

            int activeSessions = sessionService.getActiveSessionCount();

            logger.debug("active session count = {}", activeSessions);

            builder.withActiveSessionCount(activeSessions);
        }

        private void checkConnections() {
            MyPooledDataSource basicDataSource = dataSource.getInner();
            int activeConnections = basicDataSource.getNumActive(),
                    idleConnections = basicDataSource.getNumIdle(),
                    maxConnections = basicDataSource.getMaxTotal(),
                    usedConnections = activeConnections + idleConnections,
                    percUsed = maxConnections <= 0 ? 0 : usedConnections * 100 / maxConnections,
                    availableConnections = maxConnections > 0 ? maxConnections - usedConnections : Integer.MAX_VALUE;

            logger.debug("connection count, active = {}, idle = {} (pool limit = {}); used {}%", activeConnections, idleConnections, maxConnections, percUsed);
            if (percUsed > 95 || (availableConnections <= 2)) {
                addCritical("connection pool %s%% used (%s/%s)", percUsed, usedConnections, maxConnections);
            } else if (percUsed > 85) {
                addWarning("connection pool %s%% used (%s/%s)", percUsed, usedConnections, maxConnections);
            }

        }

        private void attachWarnings() {
            if (!warnings.isEmpty()) {
                builder.withWarnings(Joiner.on("; ").join(warnings));
            }
        }

        private void addWarning(String format, Object... args) {
            String message = "CMO: WARNING: " + format(format, args);
            logger.warn(marker(), message);
            warnings.add(message);
        }

        private void addCritical(String format, Object... args) {
            String message = "CMO: CRITICAL: " + format(format, args);
            logger.error(marker(), message);
            warnings.add(message);
        }
    }

}
