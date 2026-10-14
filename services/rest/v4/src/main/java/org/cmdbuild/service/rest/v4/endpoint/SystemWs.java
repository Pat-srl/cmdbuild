package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.media.SchemaProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ComparisonChain;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import static com.google.common.collect.Maps.newLinkedHashMap;
import com.google.common.collect.Ordering;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import static jakarta.ws.rs.core.MediaType.*;
import java.io.File;
import static java.lang.String.format;
import java.lang.management.ManagementFactory;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.time.ZonedDateTime;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.audit.RequestTrackingService;
import org.cmdbuild.auth.multitenant.api.MultitenantService;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.*;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCacheStats;
import org.cmdbuild.cluster.ClusterNode;
import org.cmdbuild.cluster.ClusterRpcService;
import org.cmdbuild.cluster.ClusterService;
import org.cmdbuild.common.log.LoggerConfig;
import org.cmdbuild.common.log.LoggerConfigImpl;
import org.cmdbuild.common.log.LoggerConfigService;
import static org.cmdbuild.common.log.LoggerConfigService.LOGGER_LEVEL_DEFAULT;
import org.cmdbuild.config.CoreConfiguration;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.dao.ConfigurableDataSource;
import org.cmdbuild.dao.MyPooledDataSource;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_NAMESPACE;
import org.cmdbuild.dao.config.inner.*;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.*;
import org.cmdbuild.dao.postgres.services.DumpService;
import org.cmdbuild.dao.postgres.services.PostgresDateService;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import org.cmdbuild.debuginfo.BugReportInfo;
import org.cmdbuild.debuginfo.BugReportService;
import org.cmdbuild.debuginfo.BuildInfoService;
import org.cmdbuild.diagram.DiagramService;
import org.cmdbuild.dms.DmsService;
import static org.cmdbuild.etl.database.job.SqlDriverUtils.getInstalledJdbcDrivers;
import org.cmdbuild.event.EventService;
import org.cmdbuild.minions.MinionService;
import static org.cmdbuild.minions.SystemStatusUtils.serializeSystemStatus;
import org.cmdbuild.platform.PlatformService;
import org.cmdbuild.platform.UpgradeHelperService;
import org.cmdbuild.preload.PreloadService;
import org.cmdbuild.requestcontext.RequestContextUtils;
import org.cmdbuild.scheduler.ScheduledJobInfo;
import org.cmdbuild.scheduler.SchedulerService;
import org.cmdbuild.script.ScriptService;
import org.cmdbuild.script.ScriptType;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationUtils.serializePatchInfo;
import org.cmdbuild.service.rest.v4.wshelpers.LogMessageStreamHelper;
import org.cmdbuild.sysmon.SysmonRepository;
import org.cmdbuild.sysmon.SysmonService;
import org.cmdbuild.sysmon.SystemErrorsAggregatorService;
import org.cmdbuild.sysmon.SystemStatusLog;
import org.cmdbuild.system.ReloadService;
import org.cmdbuild.utils.benchmark.BenchmarkResults;
import org.cmdbuild.utils.benchmark.BenchmarkUtils;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.date.CmDateUtils.*;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeIfHex;
import static org.cmdbuild.utils.encode.CmPackUtils.pack;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackIfPacked;
import static org.cmdbuild.utils.exec.CmProcessUtils.getThreadDump;
import org.cmdbuild.utils.io.BigByteArrayDataSource;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.*;
import static org.cmdbuild.utils.io.CmNetUtils.getHostname;
import static org.cmdbuild.utils.io.CmNetUtils.getIpAddr;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.toReverse;
import static org.cmdbuild.utils.lang.CmConvertUtils.*;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Path("system/")
@Tag(name = "System", description = "System related operations")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
@Component
public class SystemWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final GlobalConfigService configService;
    private final CoreConfiguration coreConfig;
    private final LogMessageStreamHelper logMessageStreamHelper;
    private final ConfigurableDataSource dataSource;
    private final CacheService cacheService;
    private final RequestTrackingService requestTrackingService;
    private final PatchService patchManager;
    private final SchedulerService schedulerService;
    private final MultitenantService multitenantService;
    private final LoggerConfigService loggerConfigurationService;
    private final BugReportService bugreportService;
    private final DumpService dumpService;
    private final ClusterService clusteringService;
    private final ClusterRpcService clusterRpcService;
    private final PlatformService platformService;
    private final SysmonService sysmonService;
    private final EventService eventService;
    private final MinionService bootService;
    private final UpgradeHelperService upgradeService;
    private final BuildInfoService buildInfoService;
    private final PostgresDateService postgresDateService;
    private final DirectoryService directoryService;
    private final DmsService documentService;
    private final ScriptService scriptService;
    private final DiagramService diagramService;
    private final SystemErrorsAggregatorService systemEventsService;
    private final SysmonRepository sysmonRepository;
    private final ReloadService reloadService;
    private final PreloadService preloadService;

    public SystemWs(GlobalConfigService configService, CoreConfiguration coreConfig, LogMessageStreamHelper logMessageStreamHelper, ConfigurableDataSource dataSource, CacheService cacheService, RequestTrackingService requestTrackingService, PatchService patchManager, SchedulerService schedulerService, MultitenantService multitenantService, LoggerConfigService loggerConfigurationService, BugReportService bugreportService, DumpService dumpService, ClusterService clusteringService, ClusterRpcService clusterRpcService, PlatformService platformService, SysmonService sysmonService, EventService eventService, MinionService bootService, UpgradeHelperService upgradeService, BuildInfoService buildInfoService, PostgresDateService postgresDateService, DirectoryService directoryService, DmsService documentService, ScriptService scriptService, DiagramService diagramService, SystemErrorsAggregatorService systemEventsService, SysmonRepository sysmonRepository, ReloadService reloadService, PreloadService preloadService) {
        this.configService = checkNotNull(configService);
        this.coreConfig = checkNotNull(coreConfig);
        this.logMessageStreamHelper = checkNotNull(logMessageStreamHelper);
        this.dataSource = checkNotNull(dataSource);
        this.cacheService = checkNotNull(cacheService);
        this.requestTrackingService = checkNotNull(requestTrackingService);
        this.patchManager = checkNotNull(patchManager);
        this.schedulerService = checkNotNull(schedulerService);
        this.multitenantService = checkNotNull(multitenantService);
        this.loggerConfigurationService = checkNotNull(loggerConfigurationService);
        this.bugreportService = checkNotNull(bugreportService);
        this.dumpService = checkNotNull(dumpService);
        this.clusteringService = checkNotNull(clusteringService);
        this.clusterRpcService = checkNotNull(clusterRpcService);
        this.platformService = checkNotNull(platformService);
        this.sysmonService = checkNotNull(sysmonService);
        this.eventService = checkNotNull(eventService);
        this.bootService = checkNotNull(bootService);
        this.upgradeService = checkNotNull(upgradeService);
        this.buildInfoService = checkNotNull(buildInfoService);
        this.postgresDateService = checkNotNull(postgresDateService);
        this.directoryService = checkNotNull(directoryService);
        this.documentService = checkNotNull(documentService);
        this.scriptService = checkNotNull(scriptService);
        this.diagramService = checkNotNull(diagramService);
        this.systemEventsService = checkNotNull(systemEventsService);
        this.sysmonRepository = checkNotNull(sysmonRepository);
        this.reloadService = checkNotNull(reloadService);
        this.preloadService = checkNotNull(preloadService);
    }

    @POST
    @Path("preload")
    @Operation(
            summary = "Preload system data",
            description = "Preload system data to improve performance on first access",
            responses = {
                @ApiResponse(responseCode = "200", description = "Preload completed successfully"),
                @ApiResponse(responseCode = "500", description = "Preload failed")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object preload() {
        preloadService.runPreload();
        return response(success());
    }

    @GET
    @Path("status")
    @Operation(
            summary = "Get system status",
            description = "Get system status",
            responses = {
                @ApiResponse(responseCode = "200", description = "System status"),
                @ApiResponse(responseCode = "500", description = "System status retrieval failed")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public Object status() {
        SystemStatusLog runtimeStatus = sysmonService.getSystemRuntimeStatus();
        return response(map(
                "hostname", getHostname(),
                "hostaddress", getIpAddr(),
                "build_info", buildInfoService.getCommitInfo(),
                "version", buildInfoService.getVersionNumber(),
                "version_patch", buildInfoService.getPatchVersionNumber(),
                "version_full", buildInfoService.getCompleteVersionNumberWithVertName(),
                "runtime", sysmonService.getJavaRuntimeInfo(),
                "uptime", toIsoDuration(ManagementFactory.getRuntimeMXBean().getUptime()),
                "server_time", toIsoDateTime(systemDate()),
                "server_timezone", systemZoneId().toString(),
                "db_timezone", postgresDateService.getTimezone(),
                "db_timezone_offset", postgresDateService.getOffset(),
                "disk_used", runtimeStatus.getFilesystemMemoryUsed(),
                "disk_free", runtimeStatus.getFilesystemMemoryFree(),
                "disk_total", runtimeStatus.getFilesystemMemoryTotal(),
                "java_memory_used", runtimeStatus.getJavaMemoryUsed(),
                "process_memory_used", runtimeStatus.getProcessMemoryUsed(),
                "java_memory_free", runtimeStatus.getJavaMemoryFree(),
                "java_memory_total", runtimeStatus.getJavaMemoryTotal(),
                "java_memory_max", runtimeStatus.getJavaMemoryMax(),
                "java_pid", runtimeStatus.getJavaPid(),
                "system_memory_used", runtimeStatus.getSystemMemoryUsed(),
                "system_memory_free", runtimeStatus.getSystemMemoryFree(),
                "system_memory_total", runtimeStatus.getSystemMemoryTotal(),
                "system_load", runtimeStatus.getLoadAvg())
                .accept((m) -> {
                    if (runtimeStatus.hasWarnings()) {
                        m.put("warning", runtimeStatus.getWarnings());
                    }
                    try {
                        MyPooledDataSource basicDataSource = dataSource.getInner();
                        m.put("datasource_active_connections", String.valueOf(basicDataSource.getNumActive()));
                        m.put("datasource_idle_connections", String.valueOf(basicDataSource.getNumIdle()));
                        m.put("datasource_max_active_connections", String.valueOf(basicDataSource.getMaxTotal()));
                        m.put("datasource_max_idle_connections", String.valueOf(basicDataSource.getMaxIdle()));
                    } catch (Exception ex) {
                        logger.warn(marker(), "error retrieving datasource info", ex);
                    }
                })
        );
    }

    @GET
    @Path("threads")
    @Operation(
            summary = "Get thread dump",
            description = "Get thread dump",
            responses = {
                @ApiResponse(responseCode = "200", description = "Thread dump"),
                @ApiResponse(responseCode = "500", description = "Thread dump retrieval failed")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object threadDump() {
        return response(map("dump", getThreadDump()));
    }

    @GET
    @Path("benchmark")
    @Operation(
            summary = "Execute system benchmark",
            description = "Execute system benchmark",
            responses = {
                @ApiResponse(responseCode = "200", description = "Benchmark results"),
                @ApiResponse(responseCode = "500", description = "Benchmark execution failed")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public synchronized Object executeSystemBenchmark() {
        BenchmarkResults results = BenchmarkUtils.executeBenchmark();
        return response(map("score", results.getAverageScore(), "results", results.getResults().stream().map(r -> map(
                "category", r.getCategory(),
                "result", r.getResult(),
                "score", r.getScore(),
                "_has_error", r.hasError(),
                "error", r.hasError() ? r.getError().toString() : null
        )).collect(toList())));
    }

    @GET
    @Path("events")
    @Operation(
            summary = "Get system events",
            description = "Get system events",
            responses = {
                @ApiResponse(responseCode = "200", description = "System events"),
                @ApiResponse(responseCode = "500", description = "System events retrieval failed")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object events() {
        return response(systemEventsService.getEventsSince(now().minusHours(1)).stream().map(e -> map(//TODO configurable time window
                "timestamp", toIsoDateTime(e.getTimestamp()),
                "category", e.getCategory(),
                "source", e.getSource(),
                "level", e.getLevel().name(),
                "message", e.getMessage()
        )).collect(toList()));
    }

    @GET
    @Path("cluster/status")
    @Operation(
            summary = "Get cluster status",
            description = "Get cluster status",
            responses = {
                @ApiResponse(responseCode = "200", description = "Cluster status"),
                @ApiResponse(responseCode = "500", description = "Cluster status retrieval failed"),},
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getClusterStatus() {
        return response(map("running", clusteringService.isRunning(), "nodes", serializeClusterNodes()));
    }

    @GET
    @Path("cluster/nodes")
    @Operation(
            summary = "Get cluster nodes",
            description = "Get cluster nodes",
            responses = {
                @ApiResponse(responseCode = "200", description = "Cluster nodes"),
                @ApiResponse(responseCode = "500", description = "Cluster nodes retrieval failed"),},
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getClusterNodes() {
        return response(serializeClusterNodes());
    }

    @POST
    @Path("cluster/nodes/{nodeId}/invoke")
    @Operation(
            summary = "Invoke method on cluster node",
            description = "Invoke method on cluster node",
            responses = {
                @ApiResponse(responseCode = "200", description = "Method invocation result")},
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object invokeClusterNodeMethod(
            @PathParam("nodeId") @Parameter(description = "Node ID", required = true) String nodeId,
            @RequestBody(description = "JSON payload describing the method to invoke on the cluster node(s)", required = true) String payload
    ) {
        if (nodeId.matches("(?i)_?all")) {
            return response(clusterRpcService.invokeMethodOnAllNodes(payload).stream().map(r -> {
                ObjectNode json = fromJson(r.getOutput(), ObjectNode.class);
                json.putPOJO("cluster_node", serializeClusterNode(r.getNode()));
                return json;
            }).collect(toImmutableList()));
        } else {
            return clusterRpcService.invokeMethodOnNode(decodeIfHex(nodeId), payload);
        }
    }

    private Object serializeClusterNodes() {
        return clusteringService.isRunning() ? clusteringService.getClusterNodes().stream().map(SystemWs::serializeClusterNode).collect(toList()) : emptyList();
    }

    private static Object serializeClusterNode(ClusterNode n) {
        return map("address", n.getAddress(), "nodeId", n.getNodeId(), "thisNode", n.isThisNode(), "meta", n.getMeta());
    }

    @GET
    @Path("cluster/nodes/{hostname}/monitor")
    @Operation(
            summary = "Get system monitor data for a cluster node",
            description = "Get system monitor data for a cluster node",
            responses = {
                @ApiResponse(responseCode = "200", description = "System monitor data"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object systemMonitorInterval(
            @PathParam("hostname") @Parameter(description = "Node hostname", required = true) String hostname,
            @PathParam("interval") @Parameter(description = "Interval", required = true) @DefaultValue("01:00:00") String interval
    ) {
        return response(serializeSystemMonitor(sysmonRepository.getRecentRecords(toInterval(interval)), true).filter(hostname.matches("(?i)_?all") ? Predicates.alwaysTrue() : s -> s.get("hostname").equals(hostname)).collect(toReverse()));
    }

    @GET
    @Path("cluster/nodes/{hostname}/monitor/{hours}")
    @Operation(
            summary = "Get averaged system monitor data for a cluster node over specified hours",
            description = "Get averaged system monitor data for a cluster node over specified hours",
            responses = {
                @ApiResponse(responseCode = "200", description = "Averaged system monitor data"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    public Object systemMonitorHours(
            @PathParam("hostname") @Parameter(description = "Node hostname", required = true) String hostname,
            @PathParam("hours") @Parameter(description = "Number of hours", required = true) String hours
    ) {
        return success().with("data", list().accept(l -> {
            try {
                int multiplier = 60 / calculateIntervalSystemMonitor(hostname);
                List<Map<Object, Object>> tempAvgSysMonitor = list();
                int sizeAvg = 1;
                List<SystemStatusLog> recentRecords = switch (coreConfig.getSystemMonitorMode()) {
                    case SMM_RECENT ->
                        sysmonRepository.getRecentRecords(hostname, toInterval(format("%s:00:00", hours)));
                    case SMM_LAST ->
                        sysmonRepository.getLastNodeRecords(hostname, toInt(hours) * multiplier * 60);
                };

                for (Map<Object, Object> record : serializeSystemMonitor(recentRecords, false).toList()) {
                    tempAvgSysMonitor.add(record);
                    if (sizeAvg == toInt(hours) * multiplier) {
                        l.add(map(
                                "timestamp", toIsoDateTime(toLong(generateAvgStringSystemMonitor(tempAvgSysMonitor, "timestamp", LONG))),
                                "active_session", generateAvgStringSystemMonitor(tempAvgSysMonitor, "active_session", INTEGER),
                                "disk_used", generateAvgStringSystemMonitor(tempAvgSysMonitor, "disk_used", INTEGER),
                                "disk_free", generateAvgStringSystemMonitor(tempAvgSysMonitor, "disk_free", INTEGER),
                                "disk_total", generateAvgStringSystemMonitor(tempAvgSysMonitor, "disk_total", INTEGER),
                                "java_memory_used", generateAvgStringSystemMonitor(tempAvgSysMonitor, "java_memory_used", INTEGER),
                                "process_memory_used", generateAvgStringSystemMonitor(tempAvgSysMonitor, "process_memory_used", INTEGER),
                                "java_memory_free", generateAvgStringSystemMonitor(tempAvgSysMonitor, "java_memory_free", INTEGER),
                                "java_memory_total", generateAvgStringSystemMonitor(tempAvgSysMonitor, "java_memory_total", INTEGER),
                                "java_memory_max", generateAvgStringSystemMonitor(tempAvgSysMonitor, "java_memory_max", INTEGER),
                                "system_memory_used", generateAvgStringSystemMonitor(tempAvgSysMonitor, "system_memory_used", INTEGER),
                                "system_memory_free", generateAvgStringSystemMonitor(tempAvgSysMonitor, "system_memory_free", INTEGER),
                                "system_memory_total", generateAvgStringSystemMonitor(tempAvgSysMonitor, "system_memory_total", INTEGER),
                                "system_load", generateAvgStringSystemMonitor(tempAvgSysMonitor, "system_load", DOUBLE)
                        ));
                        tempAvgSysMonitor = list();
                        sizeAvg = 1;
                    } else {
                        sizeAvg = sizeAvg + 1;
                    }
                }
            } catch (Exception e) {
                logger.error("error generating system monitor");
            }
        }));
    }

    private Stream<Map<Object, Object>> serializeSystemMonitor(List<SystemStatusLog> systemStatusLog, boolean isoDateTime) {
        return systemStatusLog.stream().map(s -> map(
                "timestamp", isoDateTime ? toIsoDateTime(s.getBeginDate()) : s.getBeginDate().toInstant().toEpochMilli(),
                "hostname", s.getHostname(),
                "nodeId", s.getNodeId(),
                "active_session", s.getActiveSessionCount(),
                "disk_used", s.getFilesystemMemoryUsed(),
                "disk_free", s.getFilesystemMemoryFree(),
                "disk_total", s.getFilesystemMemoryTotal(),
                "java_memory_used", s.getJavaMemoryUsed(),
                "process_memory_used", s.getProcessMemoryUsed(),
                "java_memory_free", s.getJavaMemoryFree(),
                "java_memory_total", s.getJavaMemoryTotal(),
                "java_memory_max", s.getJavaMemoryMax(),
                "system_memory_used", s.getSystemMemoryUsed(),
                "system_memory_free", s.getSystemMemoryFree(),
                "system_memory_total", s.getSystemMemoryTotal(),
                "system_load", s.getLoadAvg()
        ));
    }

    private String generateAvgStringSystemMonitor(List<Map<Object, Object>> objAverage, String keyOfMap, AttributeTypeName type) {
        return switch (type) {
            case INTEGER ->
                format("%.0f", objAverage.stream().mapToInt(s -> toInt(s.get(keyOfMap))).average().getAsDouble());
            case LONG ->
                format("%.0f", objAverage.stream().mapToLong(s -> toLong(s.get(keyOfMap))).average().getAsDouble());
            case DOUBLE ->
                format("%.2f", objAverage.stream().mapToDouble(s -> toDouble(s.get(keyOfMap))).average().getAsDouble());
            default ->
                null;
        };
    }

    private int calculateIntervalSystemMonitor(String hostname) {
        if (coreConfig.isSystemMonitorAutoCalculateInterval()) {
            int secondInterval;
            List<Long> timestampInterval = sysmonRepository.getLastNodeRecords(hostname, 2).stream().map(s -> s.getBeginDate().toInstant().toEpochMilli()).toList();
            long firstTimestamp = toLong(timestampInterval.get(0)) / 1000, secondTimestamp = toLong(timestampInterval.get(1)) / 1000;
            if (firstTimestamp > secondTimestamp) {
                secondInterval = Math.round((firstTimestamp - secondTimestamp) / 10) * 10;
            } else {
                secondInterval = Math.round((secondTimestamp - firstTimestamp) / 10) * 10;
            }
            logger.debug("system monitor interval from first to second value is {} seconds", secondInterval);
            return secondInterval;
        } else {
            logger.debug("using default value for system monitor interval (60 seconds)");
            return 60;
        }
    }

    @POST
    @Path("cache/drop")
    @Operation(
            summary = "Drop all system cache",
            description = "Drop all system cache",
            responses = {
                @ApiResponse(responseCode = "200", description = "System cache dropped successfully"),
                @ApiResponse(responseCode = "500", description = "System cache drop failed"),},
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    public Object dropCacheAll() {
        logger.info("drop system cache");
        cacheService.invalidateAll();
        return success();
    }

    @POST
    @Path("cache/{cacheId}/drop")
    @Operation(
            summary = "Drop specific system cache",
            description = "Drop specific system cache",
            responses = {
                @ApiResponse(responseCode = "200", description = "Specific system cache dropped successfully"),
                @ApiResponse(responseCode = "500", description = "Specific system cache drop failed"),},
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    public Object dropCacheById(
            @PathParam("cacheId") @Parameter(description = "Cache ID", required = true) String cacheId
    ) {
        logger.info("drop cache = {}", cacheId);
        cacheService.invalidate(cacheId);
        return success();
    }

    @GET
    @Path("cache/stats")
    @Operation(
            summary = "Get system cache statistics",
            description = "Get system cache statistics",
            responses = {
                @ApiResponse(responseCode = "200", description = "System cache statistics")},
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getCacheStats() {
        Map<String, CmCacheStats> stats = cacheService.getStats();
        return success().with("data", list().accept((l) -> {
            stats.forEach((key, value) -> {
                l.add(map("name", key, "objectsCount", value.getSize(), "objectsSize", value.getEstimateMemSize(), "_objectsSize_description", FileUtils.byteCountToDisplaySize(value.getEstimateMemSize())));
            });
        }));
    }

    @DELETE
    @Path("requests/{requestId}")
    @Operation(
            summary = "Interrupt a running request",
            description = "Interrupt a running request",
            responses = {
                @ApiResponse(responseCode = "200", description = "Request interrupted successfully"),
                @ApiResponse(responseCode = "500", description = "Request interrupt failed"),},
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object interruptRequest(@PathParam("requestId") @Parameter(description = "Id of the request to interrupt") String requestId) {
        logger.info("interrupting request =< {} >", requestId);
        RequestContextUtils.interruptRequest(requestId);
        return success();
    }

    @POST
    @Path("stop")
    @Operation(
            summary = "Stop the system",
            description = "Stop the system",
            responses = {
                @ApiResponse(responseCode = "200", description = "System stopped successfully"),
                @ApiResponse(responseCode = "500", description = "System stop failed"),},
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    public Object stopSystem() {
        logger.info("stop cmdbuild");
        platformService.stopContainer();
        return success();
    }

    @POST
    @Path("reload")
    @Operation(
            summary = "Reload the system",
            description = "Reload the system",
            responses = {
                @ApiResponse(responseCode = "200", description = "System reloaded successfully"),
                @ApiResponse(responseCode = "500", description = "System reload failed"),},
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    public Object reloadSystem() {
        logger.info("reload cmdbuild");
        reloadService.refreshAndReload();
        return success();
    }

    @POST
    @Path("rollback")
    @Operation(
            summary = "Rollback all system data to a specific timestamp",
            description = "Rollback all system data to a specific timestamp",
            responses = {
                @ApiResponse(responseCode = "200", description = "System data rolled back successfully"),
                @ApiResponse(responseCode = "500", description = "System data rollback failed"),},
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    public Object rollbackSystemData(
            @QueryParam("timestamp") @Parameter(description = "Timestamp", required = true) String timestampStr
    ) {
        ZonedDateTime timestamp = toDateTime(checkNotBlank(timestampStr));
        logger.info("rollback all system data to timestamp = {}", toIsoDateTime(timestamp));
        dataSource.withAdminJdbcTemplate(j -> j.queryForObject(format("SELECT _cm3_system_rollback(%s)", systemToSqlExpr(timestamp)), Object.class));//TODO improve this, make it work without superuser privileges
        reloadService.reload();
        return success();
    }

    @POST
    @Path("restart")
    @Operation(
            summary = "Restart the system",
            description = "Restart the system",
            responses = {
                @ApiResponse(responseCode = "200", description = "System restarted successfully"),
                @ApiResponse(responseCode = "500", description = "System restart failed")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    public Object restartSystem() {
        logger.info("restart cmdbuild");
        platformService.restartCluster();
        return success();
    }

    @POST
    @Path("upgrade")
    @Operation(
            summary = "Upgrade the system",
            description = "Upgrade the system",
            requestBody = @RequestBody(description = "Multipart body: the upgrade package file", required = true, content = @Content(mediaType = MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = FILE, schema = @Schema(type = "string", format = "binary"))
            })),
            responses = {
                @ApiResponse(responseCode = "200", description = "System upgraded successfully"),
                @ApiResponse(responseCode = "500", description = "System upgrade failed")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    public Object upgradeSystem(@Multipart(FILE) DataHandler dataHandler) {
        logger.info("upgrade cmdbuild");
        upgradeService.upgradeWebapp(CmIoUtils.toByteArray(dataHandler));
        return success();
    }

    /**
     * drop all data collected by audit process (request tracking)
     * <p>
     * this is mostly useful for debug/devel, or to clear data after we've
     * disabled tracking
     *
     */
    @POST
    @Path("audit/drop")
    @Operation(
            summary = "Drop all audit data",
            description = "Drop all audit data",
            responses = {
                @ApiResponse(responseCode = "200", description = "Audit data dropped successfully ")},
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    public void dropAudit() {
        logger.info("drop audit data");
        requestTrackingService.dropAllData();
    }

    @GET
    @Path("patches")
    @Operation(
            summary = "Get all system patches",
            description = "Get all system patches",
            responses = {
                @ApiResponse(responseCode = "200", description = "System patches"),
                @ApiResponse(responseCode = "500", description = "Failed to get system patches")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getAllPatches() {
        return ImmutableMap.of("patches", patchManager.getAllPatches().stream()
                .sorted((a, b) -> ComparisonChain.start().compareFalseFirst(a.isApplied(), b.isApplied()).compare(firstNonNull(a.getApplyDate(), 0), firstNonNull(b.getApplyDate(), 0)).compare(b.getComparableVersion(), a.getComparableVersion()).result())
                .map((patch) -> serializePatchInfo(patch).accept((map) -> {
            map.put("applied", patch.isApplied());
            if (patch.isApplied()) {
                map.put("appliedOnDate", CmDateUtils.toIsoDateTime(patch.getApplyDate()));
            }
            if (!isBlank(patch.getHash())) {
                map.put("hash", patch.getHash());
            }
            List<String> warnings = Lists.newArrayList();
            if (patch.hashMismatch()) {
                warnings.add("hash mismatch: the hash on db does not match the hash on file");
            }
            if (patch.isApplied() && !patch.hasPatchOnFile()) {
                warnings.add("orphan patch: this patch does not exist on file");
            }
            if (!warnings.isEmpty()) {
                map.put("warning", warnings);
            }
        })).collect(toList()));
    }

    @GET
    @Path("tenants")
    @Operation(
            summary = "Get all active tenants",
            description = "Get all active tenants",
            responses = {
                @ApiResponse(responseCode = "200", description = "Active tenants"),
                @ApiResponse(responseCode = "500", description = "Failed to get active tenants")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public List<Object> getAllTenants() {
        return multitenantService.getAllActiveTenants().stream().map((tenant) -> {
            Map map = newLinkedHashMap();
            map.put("id", tenant.getId());
            map.put("description", tenant.getDescription());
            return map;
        }).collect(toList());
    }

    @GET
    @Path("scheduler/jobs")
    @Operation(
            summary = "Get all configured scheduler jobs",
            description = "Get all configured scheduler jobs",
            responses = {
                @ApiResponse(responseCode = "200", description = "Configured scheduler jobs"),
                @ApiResponse(responseCode = "500", description = "Failed to get configured scheduler jobs")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getSchedulerJobs() {
        return map("success", true, "data", schedulerService.getConfiguredJobs().stream()
                .sorted(Ordering.natural().onResultOf(ScheduledJobInfo::getCode))
                .map((job) -> map(
                "_id", job.getCode(),
                "trigger", job.getTrigger(),
                "isRunning", job.isRunning(),
                "lastRun", toIsoDateTime(job.getLastRun()),
                "clusterMode", serializeEnum(job.getClusterMode())
        )).collect(toList()));
    }

    @POST
    @Path("scheduler/jobs/{jobCode}/trigger")
    @Operation(
            summary = "Trigger a scheduler job immediately",
            description = "Trigger a scheduler job immediately",
            responses = {
                @ApiResponse(responseCode = "200", description = "Scheduler job triggered successfully"),
                @ApiResponse(responseCode = "500", description = "Failed to trigger scheduler job")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object triggerJobNow(
            @PathParam("jobCode") @Parameter(description = "Job code", required = true) String jobCode
    ) {
        schedulerService.runJob(decodeIfHex(jobCode));
        return success();
    }

    @GET
    @Path("loggers")
    @Operation(
            summary = "Get all logger configurations",
            description = "Get all logger configurations",
            responses = {
                @ApiResponse(responseCode = "200", description = "Logger configurations"),
                @ApiResponse(responseCode = "500", description = "Failed to get logger configurations")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getAllLoggers(
            @QueryParam("includeLoggersWithoutLevel") @Parameter(description = "Include loggers without level", required = false) @DefaultValue(FALSE) Boolean includeLoggersWithoutLevel
    ) {
        List<LoggerConfig> loggers = includeLoggersWithoutLevel ? loggerConfigurationService.getAllLoggerConfigIncludeUnconfigured() : loggerConfigurationService.getAllLoggerConfig();
        return response(loggers.stream()
                .sorted(Ordering.natural().onResultOf(LoggerConfig::getCategory))
                .map((item) -> map("_id", item.getCategory(), "category", item.getCategory(), "description", item.getDescription(), "level", item.getLevel())).collect(toList()));
    }

    @POST
    @Path("loggers/{key}")
    @Operation(
            summary = "Update logger level",
            description = "Update logger level",
            responses = {
                @ApiResponse(responseCode = "200", description = "Logger level updated successfully")},
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(TEXT_PLAIN)
    public void updateLoggerLevel(
            @PathParam("key") @Parameter(description = "Logger category") String loggerCategory,
            @RequestBody(description = "Logger level name (plain text, e.g. DEBUG, INFO, WARN, ERROR, or the default level to reset)", required = true) String loggerLevel
    ) {
        if (LOGGER_LEVEL_DEFAULT.equalsIgnoreCase(loggerLevel)) {
            loggerConfigurationService.removeLoggerConfig(loggerCategory);
        } else {
            loggerConfigurationService.setLoggerConfig(new LoggerConfigImpl(loggerCategory, loggerLevel));
        }
    }

    @PUT
    @Path("loggers/{key}")
    @Operation(
            summary = "Add logger level",
            description = "Add logger level",
            responses = {
                @ApiResponse(responseCode = "200", description = "Logger level added successfully"),
                @ApiResponse(responseCode = "400", description = "Logger level already exists"),
                @ApiResponse(responseCode = "500", description = "Failed to add logger level")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(TEXT_PLAIN)
    public void addLoggerLevel(
            @PathParam("key") @Parameter(description = "Logger category", required = true) String loggerCategory,
            @RequestBody(description = "Logger level name (plain text, e.g. DEBUG, INFO, WARN, ERROR)", required = true) String loggerLevel
    ) {
        updateLoggerLevel(loggerCategory, loggerLevel);
    }

    @DELETE
    @Path("loggers/{key}")
    @Operation(
            summary = "Delete logger level",
            description = "Delete logger level",
            responses = {
                @ApiResponse(responseCode = "200", description = "Logger level deleted successfully"),
                @ApiResponse(responseCode = "404", description = "Logger level not found"),
                @ApiResponse(responseCode = "500", description = "Failed to delete logger level")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public void deleteLoggerLevel(
            @PathParam("key") @Parameter(description = "Logger category", required = true) String loggerCategory
    ) {
        loggerConfigurationService.removeLoggerConfig(loggerCategory);
    }

    @POST
    @Consumes(WILDCARD)
    @Path("loggers/stream")
    @Operation(
            summary = "Start receiving log messages in real-time",
            description = "Start receiving log messages in real-time",
            responses = {
                @ApiResponse(responseCode = "200", description = "Started receiving log messages successfully"),
                @ApiResponse(responseCode = "500", description = "Failed to start receiving log messages")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object receiveLogMessages() {
        logMessageStreamHelper.startReceivingLogMessages();
        return success();
    }

    @DELETE
    @Consumes(WILDCARD)
    @Path("loggers/stream")
    @Operation(
            summary = "Stop receiving log messages in real-time",
            description = "Stop receiving log messages in real-time",
            responses = {
                @ApiResponse(responseCode = "200", description = "Stopped receiving log messages successfully"),
                @ApiResponse(responseCode = "500", description = "Failed to stop receiving log messages")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object stopReceivingLogMessages() {
        logMessageStreamHelper.stopReceivingLogMessages();
        return success();
    }

    @GET
    @Path("log/files")
    @Operation(
            summary = "Get all log files",
            description = "Get all log files",
            responses = {
                @ApiResponse(responseCode = "200", description = "Log files"),
                @ApiResponse(responseCode = "500", description = "Failed to get log files")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getAllLogFiles(
            @QueryParam("includeArchives") @Parameter(description = "Include archived log files", required = false) @DefaultValue(FALSE) boolean includeArchives
    ) {
        return response((includeArchives ? loggerConfigurationService.getAllLogFiles() : loggerConfigurationService.getActiveLogFiles()).stream()
                .sorted(Ordering.natural())
                .map((file) -> map("_id", pack(file.getAbsolutePath()), "file", file.getName(), "path", file.getAbsolutePath()))
                .collect(toList()));
    }

    @GET
    @Path("log/files/{fileName}/download")
    @Operation(
            summary = "Download a log file",
            description = "Download a log file",
            responses = {
                @ApiResponse(responseCode = "200", description = "Log file downloaded successfully")},
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public DataHandler downloadLogFile(@PathParam("fileName") @Parameter(description = "Name of the log file") String fileName) {
        return loggerConfigurationService.downloadLogFile(unpackIfPacked(fileName));
    }

    @GET
    @Path("log/files/_ALL/download")
    @Operation(
            summary = "Download all log files",
            description = "Download all log files",
            responses = {
                @ApiResponse(responseCode = "200", description = "All log files downloaded successfully"),
                @ApiResponse(responseCode = "500", description = "Failed to download all log files")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public DataHandler downloadAllLogFiles(
            @QueryParam("includeArchives") @Parameter(description = "Include archived log files", required = false) @DefaultValue(FALSE) boolean includeArchives
    ) {
        return includeArchives ? loggerConfigurationService.downloadAllLogFiles() : loggerConfigurationService.downloadActiveLogFiles();
    }

    @POST
    @Path("eval")
    @Operation(
            summary = "Evaluate a script",
            description = "Evaluate a script",
            responses = {
                @ApiResponse(responseCode = "200", description = "Script evaluated successfully"),
                @ApiResponse(responseCode = "500", description = "Script evaluation failed")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Object eval(
            @FormParam("script") @Parameter(description = "Script", required = true) String script,
            @QueryParam("language") @Parameter(description = "Language", required = true) String language
    ) {
        Object output = scriptService.helper(getClass()).withScript(script, parseEnumOrNull(language, ScriptType.class)).executeForOutput();
        return response(map("output", output));
    }

    @GET
    @Path("database/dump")
    @Operation(
            summary = "Dump the database",
            description = "Dump the database",
            responses = {
                @ApiResponse(responseCode = "200", description = "Database dump"),
                @ApiResponse(responseCode = "500", description = "Failed to dump the database")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler dumpDatabase() {
        File tempFile = tempFile();
        dumpService.dumpDatabaseToFile(tempFile);
        DataSource dumpFileDataSource;
        if (tempFile.length() < 1024L * 1024 * 1024 * 2) {
            dumpFileDataSource = new BigByteArrayDataSource(toBigByteArray(tempFile), APPLICATION_OCTET_STREAM, format("cmdbuild_%s.dump", dateTimeFileSuffix()));
            deleteQuietly(tempFile);
        } else {
            dumpFileDataSource = new FileDataSource(tempFile);
        }
        return new DataHandler(dumpFileDataSource);
    }

    @POST
    @Path("database/reconfigure")
    @Operation(
            summary = "Reconfigure the database",
            description = "Reconfigure the database",
            responses = {
                @ApiResponse(responseCode = "200", description = "Database reconfigured successfully"),
                @ApiResponse(responseCode = "500", description = "Failed to reconfigure the database")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object reconfigureDatabase(@RequestBody(description = "Map of database configuration key to value (only keys to change)", required = true) Map<String, String> dbConfig) {
        Map<String, String> currentConfig = configService.getConfig(DATABASE_CONFIG_NAMESPACE).getAsMap(), newConfig = map(currentConfig).with(dbConfig);
        if (equal(currentConfig, newConfig)) {
            logger.info(marker(), "database config already up to date, skip reconfigure");
        } else {
            DatabaseCreatorConfig config = DatabaseCreatorConfigImpl.builder().withConfig(newConfig).build();
            bootService.stopSystem();
            configService.putStrings(DATABASE_CONFIG_NAMESPACE, config.getCmdbuildDbConfig());
            cacheService.invalidateAll();
            bootService.startSystem();
        }
        return success().with("status", serializeSystemStatus(bootService.getSystemStatus()));
    }

    @POST
    @Path("database/import")
    @Operation(
            summary = "Import database from dump",
            description = "Import database from dump",
            requestBody = @RequestBody(description = "Multipart body: the database dump file to import", required = true, content = @Content(mediaType = MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = FILE, schema = @Schema(type = "string", format = "binary"))
            })),
            responses = {
                @ApiResponse(responseCode = "200", description = "Database imported successfully"),
                @ApiResponse(responseCode = "500", description = "Failed to import database")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    public Object importDatabaseFromDump(
            @Multipart(FILE) DataHandler dataHandler,
            @QueryParam("freezesessions") @Parameter(description = "Freeze sessions", required = false) @DefaultValue(FALSE) Boolean freezesessions
    ) {//TODO optional backup-before and restore-on-failure options; optional dump and restore config table
        logger.info("recreate cmdbuild database");
        File tempFile = tempFile(null, "dump");
        copy(dataHandler, tempFile);
        DatabaseCreator databaseCreator = new DatabaseCreator(DatabaseCreatorConfigImpl.builder()
                .withConfig(configService.getConfig(DATABASE_CONFIG_NAMESPACE).getAsMap())
                .withSqlPath(new File(directoryService.getWebappDirectory(), "WEB-INF/sql").getAbsolutePath())
                .withConfigImportStrategy(ConfigImportStrategy.CIS_DATA_ONLY)
                .withKeepLocalConfig(true)
                .withSource(tempFile.getAbsolutePath()).build());
        try {
            bootService.stopSystem();
            dataSource.closeInner();
            databaseCreator.dropDatabase();
        } catch (Exception ex) {
            logger.error("error dropping database; restarting system");
            dataSource.reloadInner();
            bootService.startSystem();
            throw ex;
        }
        try {
            databaseCreator.configureDatabase();
            try {
                databaseCreator.applyPatchesOrSkip();
            } finally {
                databaseCreator.adjustConfigs();
            }
            if (freezesessions) {
                databaseCreator.freezeSessions();
            }
        } finally {
            cacheService.invalidateAll();
            dataSource.reloadInner();
//            configService.reload(); TODO check this
            bootService.startSystem();
            deleteQuietly(tempFile);
        }
        return success();
    }

    @GET
    @Path("database/diagram")
    @Operation(
            summary = "Get database diagram",
            description = "Get database diagram",
            responses = {
                @ApiResponse(responseCode = "200", description = "Database diagram"),
                @ApiResponse(responseCode = "500", description = "Failed to get database diagram")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler getDatabaseDiagram(
            @QueryParam("classes") @Parameter(description = "Classes", required = false) String classesParam
    ) {
        List<String> classes = Splitter.on(",").splitToList(checkNotBlank(classesParam));
        return new DataHandler(diagramService.renderDatabaseDiagram(classes));
    }

    @GET
    @Path("database/pool/debug")
    @Operation(
            summary = "Get database pool debug information",
            description = "Get database pool debug information",
            responses = {
                @ApiResponse(responseCode = "200", description = "Database pool debug information"),
                @ApiResponse(responseCode = "500", description = "Failed to get database pool debug information")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getDatabasePoolInfo() {
        return response(map("connections", dataSource.getInner().getStackTraceForBorrowedConnections().stream().map(t -> map("status", serializeEnum(t.getStatus()), "trace", nullToEmpty(t.getTrace()))).collect(toImmutableList())));
    }

    @POST
    @Path("database/pool/reload")
    @Operation(
            summary = "Reload all database pool connections",
            description = "Reload all database pool connections",
            responses = {
                @ApiResponse(responseCode = "200", description = "Database pool connections reloaded successfully"),
                @ApiResponse(responseCode = "500", description = "Failed to reload all database pool connections")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    public Object reloadAllDbPoolConnections() {
        dataSource.reloadInner();
        return success();
    }

    @GET
    @Path("debuginfo/download")
    @Operation(
            summary = "Generate system debug information",
            description = "Generate system debug information",
            responses = {
                @ApiResponse(responseCode = "200", description = "System debug information"),
                @ApiResponse(responseCode = "500", description = "Failed to generate system debug information")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler generateDebugInfo(
            @QueryParam("secure") @Parameter(description = "Secure debug information", required = false) String secure
    ) {
        return new DataHandler(bugreportService.generateBugReport(decodePassword(secure)));
    }

    @POST
    @Consumes(WILDCARD)
    @Path("debuginfo/send")
    @Operation(
            summary = "Send system bug report",
            description = "Send system bug report",
            responses = {
                @ApiResponse(responseCode = "200", description = "System bug report sent successfully"),
                @ApiResponse(responseCode = "500", description = "Failed to send system bug report")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object sendBugReport(
            @QueryParam("message") @Parameter(description = "Bug report message", required = true) String message,
            @QueryParam("secure") @Parameter(description = "Secure debug information", required = false) String secure
    ) {
        BugReportInfo debugInfo = bugreportService.sendBugReport(message, decodePassword(secure));
        return response(map("fileName", debugInfo.getFileName()));
    }

    @POST
    @Consumes(WILDCARD)
    @Path("messages/broadcast")
    @Operation(
            summary = "Send a broadcast message to all users",
            description = "Send a broadcast message to all users",
            responses = {
                @ApiResponse(responseCode = "200", description = "Broadcast message sent successfully"),
                @ApiResponse(responseCode = "500", description = "Failed to send broadcast message")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object sendBroadcastMessage(
            @QueryParam("message") @Parameter(description = "Broadcast message", required = true) String message
    ) {
        eventService.sendBroadcastAlert(message);
        return success();
    }

    @GET
    @Path("dms/export")
    @Operation(
            summary = "Export all documents from the document management system",
            description = "Export all documents from the document management system",
            responses = {
                @ApiResponse(responseCode = "200", description = "All documents exported successfully"),
                @ApiResponse(responseCode = "500", description = "Failed to export all documents")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler exportAllDocuments() {
        return documentService.exportAllDocuments();
    }

    @GET
    @Path("libs/jdbc")
    @Operation(
            summary = "Get available JDBC drivers",
            description = "Get available JDBC drivers",
            responses = {
                @ApiResponse(responseCode = "200", description = "Available JDBC drivers"),
                @ApiResponse(responseCode = "500", description = "Failed to get available JDBC drivers")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed({SYSTEM_ACCESS_AUTHORITY, ADMIN_JOBS_VIEW_AUTHORITY})
    public Object getAvailableJdbcDrivers() {
        return response(list(getInstalledJdbcDrivers()).map(c -> map(
                "className", c.getName()
        )));
    }

    @Nullable
    private static String decodePassword(@Nullable String secure) {
        try {
            return isBlank(secure) ? null : new String(Hex.decodeHex(secure), UTF_8);
        } catch (DecoderException ex) {
            throw runtime(ex);
        }
    }
}
