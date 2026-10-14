package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.*;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;

@Path("system/")
@Tag(name = "System", description = "System related operations")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
public class SystemWs {

    private final org.cmdbuild.service.rest.v4.endpoint.SystemWs systemWs;

    public SystemWs(org.cmdbuild.service.rest.v4.endpoint.SystemWs systemWs) {
        this.systemWs = checkNotNull(systemWs);
    }

    @POST
    @Path("preload")
    @Operation(
            summary = "Preload system data",
            description = "Preload system data to improve performance on first access",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Preload completed successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "409", description = "Conflict"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object preload() {
        return systemWs.preload();
    }

    @GET
    @Path("status")
    @Operation(
            summary = "Get system status",
            description = "Get system status",
            responses = {
                    @ApiResponse(responseCode = "200", description = "System status"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public Object status() {
        return systemWs.status();
    }

    @GET
    @Path("threads")
    @Operation(
            summary = "Get thread dump",
            description = "Get thread dump",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Thread dump"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object threadDump() {
        return systemWs.threadDump();
    }

    @GET
    @Path("benchmark")
    @Operation(
            summary = "Execute system benchmark",
            description = "Execute system benchmark",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Benchmark results"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public synchronized Object executeSystemBenchmark() {
        return systemWs.executeSystemBenchmark();
    }

    @GET
    @Path("events")
    @Operation(
            summary = "Get system events",
            description = "Get system events",
            responses = {
                    @ApiResponse(responseCode = "200", description = "System events"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object events() {
        return systemWs.events();
    }

    @GET
    @Path("cluster/status")
    @Operation(
            summary = "Get cluster status",
            description = "Get cluster status",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cluster status"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getClusterStatus() {
        return systemWs.getClusterStatus();
    }

    @GET
    @Path("cluster/nodes")
    @Operation(
            summary = "Get cluster nodes",
            description = "Get cluster nodes",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cluster nodes"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getClusterNodes() {
        return systemWs.getClusterNodes();
    }

    @POST
    @Path("cluster/nodes/{nodeId}/invoke")
    @Operation(
            summary = "Invoke method on cluster node",
            description = "Invoke method on cluster node",
            parameters = {
                    @Parameter(name = "nodeId", in = ParameterIn.PATH, description = "Identifier of the cluster node to invoke the method on", required = true),
                    @Parameter(name = "payload", in = ParameterIn.QUERY, description = "JSON payload to pass to the method", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Method invocation result"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object invokeClusterNodeMethod(
            @PathParam("nodeId") String nodeId,
            String payload
    ) {
        return systemWs.invokeClusterNodeMethod(nodeId, payload);
    }

    @GET
    @Path("cluster/nodes/{hostname}/monitor")
    @Operation(
            summary = "Get system monitor data for a cluster node",
            description = "Get system monitor data for a cluster node",
            parameters = {
                    @Parameter(name = "hostname", in = ParameterIn.PATH, description = "Hostname of the cluster node", required = true),
                    @Parameter(name = "interval", in = ParameterIn.QUERY, description = "Interval for the monitor data (e.g., 01:00:00 for 1 hour)", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "System monitor data"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object systemMonitorInterval(
            @PathParam("hostname") String hostname,
            @PathParam("interval") @DefaultValue("01:00:00") String interval
    ) {
        return systemWs.systemMonitorInterval(hostname, interval);
    }

    @GET
    @Path("cluster/nodes/{hostname}/monitor/{hours}")
    @Operation(
            summary = "Get averaged system monitor data for a cluster node over specified hours",
            description = "Get averaged system monitor data for a cluster node over specified hours",
            parameters = {
                    @Parameter(name = "hostname", in = ParameterIn.PATH, description = "Hostname of the cluster node", required = true),
                    @Parameter(name = "hours", in = ParameterIn.PATH, description = "Number of hours to average over", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Averaged system monitor data"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    public Object systemMonitorHours(
            @PathParam("hostname") String hostname,
            @PathParam("hours") String hours
    ) {
        return systemWs.systemMonitorHours(hostname, hours);
    }

    @POST
    @Path("cache/drop")
    @Operation(
            summary = "Drop all system cache",
            description = "Drop all system cache",
            responses = {
                    @ApiResponse(responseCode = "200", description = "System cache dropped successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    public Object dropCacheAll() {
        return systemWs.dropCacheAll();
    }

    @POST
    @Path("cache/{cacheId}/drop")
    @Operation(
            summary = "Drop specific system cache",
            description = "Drop specific system cache",
            parameters = {
                    @Parameter(name = "cacheId", in = ParameterIn.PATH, description = "Identifier of the cache to drop", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Specific system cache dropped successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    public Object dropCacheById(
            @PathParam("cacheId") String cacheId
    ) {
        return systemWs.dropCacheById(cacheId);
    }

    @GET
    @Path("cache/stats")
    @Operation(
            summary = "Get system cache statistics",
            description = "Get system cache statistics",
            responses = {
                    @ApiResponse(responseCode = "200", description = "System cache statistics"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getCacheStats() {
        return systemWs.getCacheStats();
    }

    @DELETE
    @Path("requests/{requestId}")
    @Operation(
            summary = "Interrupt a running request",
            description = "Interrupt a running request",
            parameters = {@Parameter(name = "requestId", in = ParameterIn.PATH, description = "Identifier of the request to interrupt", required = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Request interrupted successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object interruptRequest(@PathParam("requestId") String requestId) {
        return systemWs.interruptRequest(requestId);
    }

    @POST
    @Path("stop")
    @Operation(
            summary = "Stop the system",
            description = "Stop the system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "System stopped successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    public Object stopSystem() {
        return systemWs.stopSystem();
    }

    @POST
    @Path("reload")
    @Operation(
            summary = "Reload the system",
            description = "Reload the system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "System reloaded successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    public Object reloadSystem() {
        return systemWs.reloadSystem();
    }

    @POST
    @Path("rollback")
    @Operation(
            summary = "Rollback all system data to a specific timestamp",
            description = "Rollback all system data to a specific timestamp",
            parameters = {@Parameter(name = "timestamp", in = ParameterIn.QUERY, description = "Timestamp to rollback to", required = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "System data rolled back successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    public Object rollbackSystemData(
            @QueryParam("timestamp") String timestampStr
    ) {
        return systemWs.rollbackSystemData(timestampStr);
    }

    @POST
    @Path("restart")
    @Operation(
            summary = "Restart the system",
            description = "Restart the system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "System restarted successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    public Object restartSystem() {
        return systemWs.restartSystem();
    }

    @POST
    @Path("upgrade")
    @Operation(
            summary = "Upgrade the system",
            description = "Upgrade the system",
            requestBody = @RequestBody(description = "System upgrade package", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "System upgraded successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    public Object upgradeSystem(
            @Multipart(FILE) DataHandler dataHandler
    ) {
        return systemWs.upgradeSystem(dataHandler);
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
                    @ApiResponse(responseCode = "200", description = "Audit data dropped successfully "),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    public void dropAudit() {
        systemWs.dropAudit();
    }

    @GET
    @Path("patches")
    @Operation(
            summary = "Get all system patches",
            description = "Get all system patches",
            responses = {
                    @ApiResponse(responseCode = "200", description = "System patches"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getAllPatches() {
        return systemWs.getAllPatches();
    }

    @GET
    @Path("tenants")
    @Operation(
            summary = "Get all active tenants",
            description = "Get all active tenants",
            responses = {
                    @ApiResponse(responseCode = "200", description = "System tenants"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public List<Object> getAllTenants() {
        return systemWs.getAllTenants();
    }

    @GET
    @Path("scheduler/jobs")
    @Operation(
            summary = "Get all configured scheduler jobs",
            description = "Get all configured scheduler jobs",
            responses = {
                    @ApiResponse(responseCode = "200", description = "System Scheduler Jobs"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getSchedulerJobs() {
        return systemWs.getSchedulerJobs();
    }

    @POST
    @Path("scheduler/jobs/{jobCode}/trigger")
    @Operation(
            summary = "Trigger a scheduler job immediately",
            description = "Trigger a scheduler job immediately",
            parameters = {@Parameter(name = "jobCode", in = ParameterIn.PATH, description = "Code of the scheduler job to trigger", required = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "System patches"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object triggerJobNow(
            @PathParam("jobCode") String jobCode
    ) {
        return systemWs.triggerJobNow(jobCode);
    }

    @GET
    @Path("loggers")
    @Operation(
            summary = "Get all logger configurations",
            description = "Get all logger configurations",
            parameters = {@Parameter(name = "includeLoggersWithoutLevel", in = ParameterIn.QUERY, description = "Whether to include loggers without a configured level in the response", required = false)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getAllLoggers(
            @QueryParam("includeLoggersWithoutLevel") @DefaultValue(FALSE) Boolean includeLoggersWithoutLevel
    ) {
        return systemWs.getAllLoggers(includeLoggersWithoutLevel);
    }

    @POST
    @Path("loggers/{key}")
    @Operation(
            summary = "Update logger level",
            description = "Update logger level",
            parameters = {@Parameter(name = "key", in = ParameterIn.PATH, description = "Logger category", required = true)},
            requestBody = @RequestBody(description = "Logger level", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(TEXT_PLAIN)
    public void updateLoggerLevel(
            @PathParam("key") String loggerCategory,
            String loggerLevel
    ) {
        systemWs.updateLoggerLevel(loggerCategory, loggerLevel);
    }

    @PUT
    @Path("loggers/{key}")
    @Operation(
            summary = "Add logger level",
            description = "Add logger level",
            parameters = {@Parameter(name = "key", in = ParameterIn.PATH, description = "Logger category", required = true)},
            requestBody = @RequestBody(description = "Logger level", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(TEXT_PLAIN)
    public void addLoggerLevel(
            @PathParam("key") String loggerCategory,
            String loggerLevel
    ) {
        systemWs.addLoggerLevel(loggerCategory, loggerLevel);
    }

    @DELETE
    @Path("loggers/{key}")
    @Operation(
            summary = "Delete logger level",
            description = "Delete logger level",
            parameters = {@Parameter(name = "key", in = ParameterIn.PATH, description = "Logger category", required = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public void deleteLoggerLevel(
            @PathParam("key") String loggerCategory
    ) {
        systemWs.deleteLoggerLevel(loggerCategory);
    }

    @POST
    @Consumes(WILDCARD)
    @Path("loggers/stream")
    @Operation(
            summary = "Start receiving log messages in real-time",
            description = "Start receiving log messages in real-time",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object receiveLogMessages() {
        return systemWs.receiveLogMessages();
    }

    @DELETE
    @Consumes(WILDCARD)
    @Path("loggers/stream")
    @Operation(
            summary = "Stop receiving log messages in real-time",
            description = "Stop receiving log messages in real-time",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object stopReceivingLogMessages() {
        return systemWs.stopReceivingLogMessages();
    }

    @GET
    @Path("log/files")
    @Operation(
            summary = "Get all log files",
            description = "Get all log files",
            parameters = {@Parameter(name = "includeArchives", in = ParameterIn.QUERY, description = "Whether to include archived log files in the response", required = false)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getAllLogFiles(
            @QueryParam("includeArchives") @DefaultValue(FALSE) boolean includeArchives
    ) {
        return systemWs.getAllLogFiles(includeArchives);
    }

    @GET
    @Path("log/files/{fileName}/download")
    @Operation(
            summary = "Download a log file",
            description = "Download a log file",
            parameters = {@Parameter(name = "fileName", in = ParameterIn.PATH, description = "Name of the log file to download", required = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public DataHandler downloadLogFile(
            @PathParam("fileName") String fileName
    ) {
        return systemWs.downloadLogFile(fileName);
    }

    @GET
    @Path("log/files/_ALL/download")
    @Operation(
            summary = "Download all log files",
            description = "Download all log files",
            parameters = {@Parameter(name = "includeArchives", in = ParameterIn.QUERY, description = "Whether to include archived log files in the response", required = false)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public DataHandler downloadAllLogFiles(
            @QueryParam("includeArchives") @DefaultValue(FALSE) boolean includeArchives
    ) {
        return systemWs.downloadAllLogFiles(includeArchives);
    }

    @POST
    @Path("eval")
    @Operation(
            summary = "Evaluate a script",
            description = "Evaluate a script",
            parameters = {
                    @Parameter(name = "script", in = ParameterIn.QUERY, description = "Script to evaluate", required = true),
                    @Parameter( name = "language", in = ParameterIn.QUERY, description = "Script language", required = true )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Object eval(
            @FormParam("script") String script,
            @QueryParam("language") String language
    ) {
        return systemWs.eval(script, language);
    }

    @GET
    @Path("database/dump")
    @Operation(
            summary = "Dump the database",
            description = "Dump the database",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler dumpDatabase() {
        return systemWs.dumpDatabase();
    }

    @POST
    @Path("database/reconfigure")
    @Operation(
            summary = "Reconfigure the database",
            description = "Reconfigure the database",
            requestBody = @RequestBody(description = "Database configuration", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object reconfigureDatabase(
            Map<String, String> dbConfig
    ) {
        return systemWs.reconfigureDatabase(dbConfig);
    }

    @POST
    @Path("database/import")
    @Operation(
            summary = "Import database from dump",
            description = "Import database from dump",
            parameters = {@Parameter( name = "freezeseessions", in = ParameterIn.QUERY, description = "Whether to freeze sessions during import", required = false)},
            requestBody = @RequestBody(description = "Database dump", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    public Object importDatabaseFromDump(
            @Multipart(FILE) DataHandler dataHandler,
            @QueryParam("freezesessions") @DefaultValue(FALSE) Boolean freezesessions
    ) {//TODO optional backup-before and restore-on-failure options; optional dump and restore config table
        return systemWs.importDatabaseFromDump(dataHandler, freezesessions);
    }

    @GET
    @Path("database/diagram")
    @Operation(
            summary = "Get database diagram",
            description = "Get database diagram",
            parameters = {@Parameter(name = "classes", in = ParameterIn.QUERY, description = "Comma-separated list of class names to include in the diagram (if not provided, all classes will be included)", required = false)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler getDatabaseDiagram(
            @QueryParam("classes") String classesParam
    ) {
        return systemWs.getDatabaseDiagram(classesParam);
    }

    @GET
    @Path("database/pool/debug")
    @Operation(
            summary = "Get database pool debug information",
            description = "Get database pool debug information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getDatabasePoolInfo() {
        return systemWs.getDatabasePoolInfo();
    }

    @POST
    @Path("database/pool/reload")
    @Operation(
            summary = "Reload all database pool connections",
            description = "Reload all database pool connections",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    public Object reloadAllDbPoolConnections() {
        return systemWs.reloadAllDbPoolConnections();
    }

    @GET
    @Path("debuginfo/download")
    @Operation(
            summary = "Generate system debug information",
            description = "Generate system debug information",
            parameters = {
                    @Parameter(name = "secure", in = ParameterIn.QUERY, description = "Whether to generate a secure debug information (requires a valid password)", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler generateDebugInfo(
            @QueryParam("secure") String secure
    ) {
        return systemWs.generateDebugInfo(secure);
    }

    @POST
    @Consumes(WILDCARD)
    @Path("debuginfo/send")
    @Operation(
            summary = "Send system bug report",
            description = "Send system bug report",
            parameters = {
                    @Parameter(name = "message", in = ParameterIn.QUERY, description = "Message to include in the bug report", required = false),
                    @Parameter(name = "secure", in = ParameterIn.QUERY, description = "Hex-encoded string to include in the bug report (will be decoded and included as plain text)", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object sendBugReport(
            @QueryParam("message") String message,
            @QueryParam("secure") String secure
    ) {
        return systemWs.sendBugReport(message, secure);
    }

    @POST
    @Consumes(WILDCARD)
    @Path("messages/broadcast")
    @Operation(
            summary = "Send a broadcast message to all users",
            description = "Send a broadcast message to all users",
            parameters = {@Parameter(name = "message", in = ParameterIn.QUERY, description = "Message to broadcast", required = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object sendBroadcastMessage(
            @QueryParam("message") String message
    ) {
        return systemWs.sendBroadcastMessage(message);
    }

    @GET
    @Path("dms/export")
    @Operation(
            summary = "Export all documents from the document management system",
            description = "Export all documents from the document management system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler exportAllDocuments() {
        return systemWs.exportAllDocuments();
    }

    @GET
    @Path("libs/jdbc")
    @Operation(
            summary = "Get available JDBC drivers",
            description = "Get available JDBC drivers",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed({SYSTEM_ACCESS_AUTHORITY, ADMIN_JOBS_VIEW_AUTHORITY})
    public Object getAvailableJdbcDrivers() {
        return systemWs.getAvailableJdbcDrivers();
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
