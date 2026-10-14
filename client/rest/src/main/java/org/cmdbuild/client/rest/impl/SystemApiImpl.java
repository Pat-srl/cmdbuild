/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.google.common.collect.Ordering;
import com.google.common.collect.Streams;
import com.google.common.net.UrlEscapers;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.CloseReason;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.String.format;
import java.net.URI;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.time.ZonedDateTime;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;
import org.apache.commons.codec.binary.Hex;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import static org.apache.http.entity.ContentType.APPLICATION_FORM_URLENCODED;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.cmdbuild.auth.multitenant.config.MultitenantMode;
import static org.cmdbuild.auth.multitenant.config.MultitenantMode.MTM_CMDBUILD_CLASS;
import static org.cmdbuild.auth.multitenant.config.MultitenantMode.MTM_DB_FUNCTION;
import org.cmdbuild.cache.CmCacheStats;
import org.cmdbuild.cache.CmCacheStatsImpl;
import org.cmdbuild.client.rest.api.NodeStatus;
import org.cmdbuild.client.rest.api.SystemApi;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.client.rest.model.ClusterStatus;
import org.cmdbuild.client.rest.model.LogMessage;
import org.cmdbuild.client.rest.model.LoggerInfo;
import org.cmdbuild.client.rest.model.LoggerInfoImpl;
import org.cmdbuild.client.rest.model.PatchInfo;
import org.cmdbuild.cluster.ClusterNode;
import org.cmdbuild.cluster.ClusterNodeImpl;
import static org.cmdbuild.common.http.HttpConst.CMDBUILD_AUTHORIZATION_HEADER;
import org.cmdbuild.config.api.ConfigCategory;
import org.cmdbuild.config.api.ConfigDefinition;
import static org.cmdbuild.config.api.ConfigDefinition.ModularConfigDefinition.MCD_NONE;
import org.cmdbuild.config.api.ConfigDefinitionImpl;
import org.cmdbuild.config.api.ConfigLocation;
import org.cmdbuild.dao.MyPooledDataSource.ConnectionInfo;
import org.cmdbuild.dao.MyPooledDataSource.ConnectionStatus;
import org.cmdbuild.dao.datasource.utils.ConnectionInfoImpl;
import org.cmdbuild.debuginfo.BugReportInfo;
import org.cmdbuild.debuginfo.DebugInfoImpl;
import org.cmdbuild.etl.config.WaterwayDescriptorInfoExt;
import org.cmdbuild.etl.config.WaterwayDescriptorInfoImpl;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecord;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecordImpl;
import org.cmdbuild.fault.FaultEventImpl;
import org.cmdbuild.fault.FaultEventsData;
import org.cmdbuild.fault.FaultLevel;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRun;
import org.cmdbuild.jobs.JobRunStatus;
import org.cmdbuild.jobs.beans.JobDataImpl;
import org.cmdbuild.jobs.beans.JobRunImpl;
import org.cmdbuild.log.LogService.LogLevel;
import org.cmdbuild.minions.MinionStatus;
import org.cmdbuild.minions.MinionStatusInfo;
import org.cmdbuild.minions.SystemStatus;
import org.cmdbuild.scheduler.ScheduledJobInfo;
import org.cmdbuild.scheduler.ScheduledJobInfoImpl;
import org.cmdbuild.sysmon.SystemErrorInfo;
import org.cmdbuild.sysmon.SystemErrorInfoImpl;
import org.cmdbuild.systemplugin.SystemPlugin;
import org.cmdbuild.systemplugin.SystemPluginImpl;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeHex;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.stream;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toListOfStrings;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.emptyToNull;
import static org.cmdbuild.utils.lang.CmStringUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.utils.sked.SkedJobClusterMode;

public class SystemApiImpl extends AbstractServiceClientImpl implements SystemApi {

    public SystemApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    private Boolean requireSession;

    @Override
    protected boolean isSessionTokenRequired() {
        return firstNonNull(requireSession, true);
    }

    @Override
    public List<SystemPlugin> getSystemPlugins() {
        return list(get("administration/system/plugins").asJackson().get("data").elements()).map(e -> SystemPluginImpl.builder()
                .withName(e.get("_id").textValue())
                .withDescription(e.get("description").textValue())
                .withService(Optional.ofNullable(e.get("service")).map(JsonNode::textValue).orElse(null))
                .withVersion(Optional.ofNullable(e.get("version")).map(JsonNode::textValue).orElse(null))
                .withRequiredCoreVersion(e.get("requiredCoreVersion").textValue())
                .withChecksum(e.get("checksum").textValue())
                .withRequiredLibs(list(e.get("requiredLibs").elements()).map(l -> l.get("fileName").asText()))
                .withHealthCheck(list(e.get("healthCheck").elements()).map(f -> new FaultEventImpl(parseEnum(f.get("level").asText(), FaultLevel.class), f.get("message").asText())))
                .build());
    }

    @Override
    public void deploySystemPlugins(List<File> files) {
        MultipartEntityBuilder data = MultipartEntityBuilder.create();
        files.forEach(f -> data.addBinaryBody(f.getName(), f));
        post("system/plugins", data.build());
    }

    @Override
    public DataSource downloadSystemDiagram(Collection<String> classes) {
        return getDataSource(format("system/database/diagram?classes=%s", encodeUrlQuery(Joiner.on(",").join(Set.copyOf(classes)))));
    }

    @Override
    public boolean createEvents(String triggerId, @Nullable String filter) {
        Response response;
        if (filter != null) {
            response = post(format("calendar/triggers/%s/create-events?filter=%s", triggerId, encodeUrlPath(filter)), "");
        } else {
            response = post(format("calendar/triggers/%s/create-events", triggerId), "");
        }
        return response.asJackson().get("success").asBoolean();
    }

    @Override
    public List<JobData> getJobs() {
        return Streams.stream(get("administration/jobs?detailed=true").asJson().getAsJsonObject().getAsJsonArray("data")).map(JsonElement::getAsJsonObject)
                .map(this::parseJobData).collect(toList());
    }

    @Override
    public List<SystemErrorInfo> getEvents() {
        return Streams.stream(get("system/events").asJson().getAsJsonObject().getAsJsonArray("data")).map(JsonElement::getAsJsonObject)
                .map(data -> SystemErrorInfoImpl.builder()
                .withTimestamp(toDateTime(data.get("timestamp")))
                .withCategory(toString(data.get("category")))
                .withSource(toString(data.get("source")))
                .withLevel(parseEnum(toString(data.get("level")), FaultLevel.class))
                .withMessage(toString(data.get("message")))
                .build()).collect(toList());
    }

    @Override
    public List<ScheduledJobInfo> getSysJobs() {
        return Streams.stream(get("system/scheduler/jobs").asJson().getAsJsonObject().getAsJsonArray("data")).map(JsonElement::getAsJsonObject).map(data -> ScheduledJobInfoImpl.builder()
                .withCode(toString(data.get("_id")))
                .withTrigger(toString(data.get("trigger")))
                .withRunning(toBoolean(data.get("isRunning")))
                .withLastRun(toDateTime(data.get("lastRun")))
                .withClusterMode(parseEnum(toString(data.get("clusterMode")), SkedJobClusterMode.class)).build()).collect(toList());
    }

    @Override
    public JobData getJob(String jobId) {
        return parseJobData(get(format("administration/jobs/%s", encodeUrlPath(jobId))).asJson().getAsJsonObject().getAsJsonObject("data"));
    }

    @Override
    public void runSysJob(String key) {
        post(format("system/scheduler/jobs/%s/trigger", encodeHex(checkNotBlank(key))), "");
    }

    @Override
    public void reloadDatabasePool() {
        post("system/database/pool/reload", "");
    }

    @Override
    public JobData updateJob(JobData jobData) {
        return parseJobData(put(format("administration/jobs/%s", encodeUrlPath(jobData.getCode())), map(
                "_id", jobData.getId(),
                "code", jobData.getCode(),
                "description", jobData.getDescription(),
                "type", jobData.getType(),
                "enabled", jobData.isEnabled(),
                "config", map(jobData.getConfig())
        )).asJson().getAsJsonObject().getAsJsonObject("data"));
    }

    private JobData parseJobData(JsonObject data) {
        return JobDataImpl.builder()
                .withId(toLong(data.get("_id")))
                .withCode(toString(data.get("code")))
                .withDescription(toString(data.get("description")))
                .withType(toString(data.get("type")))
                .withEnabled(toBoolean(data.get("enabled")))
                .withConfig(data.get("config").getAsJsonObject().entrySet().stream().collect(toMap(Entry::getKey, e -> toString(e.getValue()))))
                .build();
    }

    @Override
    public JobRun runJob(String jobId, Map<String, String> configOverride) {
        return parseJobRun(post(format("administration/jobs/%s/run", encodeUrlPath(jobId)), map("config", configOverride)).asJson().getAsJsonObject().getAsJsonObject("data"));
    }

    @Override
    public JsonObject configureMultitenant(MultitenantMode multitenantMode, @Nullable String classCode) {
        return post("administration/tenants/configure", map().accept((t) -> {
            if (multitenantMode.equals(MTM_CMDBUILD_CLASS)) {
                t.with("org.cmdbuild.multitenant.mode", "CMDBUILD_CLASS");
                t.with("org.cmdbuild.multitenant.tenantClass", classCode);
            } else if (multitenantMode.equals(MTM_DB_FUNCTION)) {
                t.with("org.cmdbuild.multitenant.mode", "DB_FUNCTION");
            }
        })).asJson().getAsJsonObject().getAsJsonObject("data");
    }

    @Override
    public void interrupt(String requestId) {
        delete(format("system/requests/%s", encodeUrlPath(checkNotBlank(requestId))));
    }

    @Override
    public JobRun getJobRun(String jobId, long runId) {
        return parseJobRun(get(format("administration/jobs/%s/runs/%s", encodeUrlPath(jobId), runId)).asJson().getAsJsonObject().getAsJsonObject("data"));
    }

    private JobRun parseJobRun(JsonObject data) {
        return JobRunImpl.builder()
                .withId(toLong(data.get("_id")))
                .withJobCode(toString(data.get("jobCode")))
                .withJobStatus(parseEnum(toString(data.get("status")), JobRunStatus.class))
                .withCompleted(toBoolean(data.get("completed")))
                .withTimestamp(toDateTime(data.get("timestamp")))
                .withElapsedTime(toLong(data.get("elapsedMillis")))
                .withErrorMessageData(new FaultEventsData(stream(data.get("errors").getAsJsonArray()).map(JsonElement::getAsJsonObject) //TODO duplicate code, fix
                        .map((e) -> new FaultEventImpl(parseEnum(toString(e.get("level")), FaultLevel.class), toString(e.get("message")), toString(e.get("exception")))).collect(toList())))
                .withLogs(toString(data.get("logs")))
                .build();
    }

    @Override
    public List<JobRun> getLastJobRuns(String jobId, long limit) {
        return parseJobRuns(get(format("administration/jobs/%s/runs?limit=%s", encodeUrlPath(jobId), limit)).asJson());
    }

    @Override
    public List<JobRun> getLastJobErrors(String jobId, long limit) {
        return parseJobRuns(get(format("administration/jobs/%s/errors?limit=%s", encodeUrlPath(jobId), limit)).asJson());
    }

    @Override
    public List<JobRun> getLastJobRuns(long limit) {
        return parseJobRuns(get(format("administration/jobs/_ANY/runs?limit=%s", limit)).asJson());
    }

    @Override
    public List<JobRun> getLastJobErrors(long limit) {
        return parseJobRuns(get(format("administration/jobs/_ANY/errors?limit=%s", limit)).asJson());
    }

    private List<JobRun> parseJobRuns(JsonElement reqResponse) {
        JsonArray requests = reqResponse.getAsJsonObject().getAsJsonArray("data");
        return Streams.stream(requests).map((JsonElement::getAsJsonObject)).map((data) -> {
            return JobRunImpl.builder()
                    .withId(toLong(data.get("_id")))
                    .withJobCode(toString(data.get("jobCode")))
                    .withJobStatus(parseEnum(toString(data.get("status")), JobRunStatus.class))
                    .withCompleted(toBoolean(data.get("completed")))
                    .withTimestamp(toDateTime(data.get("timestamp")))
                    .withElapsedTime(toLong(data.get("elapsedMillis")))
                    .withErrorMessages(emptyList())
                    .build();

        }).collect(toList());
    }

    @Override
    public List<LoggerInfo> getLoggers() {
        logger.debug("getLoggers");
        return stream(get("system/loggers").asJson().getAsJsonObject().getAsJsonArray("data"))
                .map(JsonElement::getAsJsonObject)
                .map((record) -> new LoggerInfoImpl(record.getAsJsonPrimitive("category").getAsString(), record.getAsJsonPrimitive("level").getAsString()))
                .collect(toList());
    }

    @Override
    public void setLogger(LoggerInfo loggerToUpdate) {
        logger.debug("updateLogger = {}", loggerToUpdate);
        post("system/loggers/" + trimAndCheckNotBlank(loggerToUpdate.getCategory()), trimAndCheckNotBlank(loggerToUpdate.getLevel()));
    }

    @Override
    public void deleteLogger(String category) {
        logger.debug("deleteLogger = {}", category);
        delete("system/loggers/" + trimAndCheckNotBlank(category));
    }

    @Override
    public Future<Void> streamLogMessages(Consumer<LogMessage> listener) {
        try {
            String sessionId = restClient().getSessionToken();
            ClientEndpointConfig clientEndpointConfig = ClientEndpointConfig.Builder.create().configurator(new ClientEndpointConfig.Configurator() {
                @Override
                public void beforeRequest(Map<String, List<String>> headers) {
                    headers.put(CMDBUILD_AUTHORIZATION_HEADER, singletonList(sessionId));
                }

            }).build();
            WebSocketContainer websocketClient = ContainerProvider.getWebSocketContainer();
            CompletableFuture connectionReadyFuture = new CompletableFuture(), connectionClosedFuture = new CompletableFuture();
            websocketClient.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    try {
                        logger.debug("bimserver session opened = {}", session.getId());
                    } catch (Exception ex) {
                        logger.error("error processing bimserver open session event", ex);
                    }

                    session.addMessageHandler((MessageHandler.Whole<String>) (String msg) -> {
                        Map<String, Object> payload = fromJson(msg, MAP_OF_OBJECTS);
                        switch (nullToEmpty(toStringOrNull(payload.get("_event")))) {
                            case "socket.session.ok" ->
                                connectionReadyFuture.complete(true);
                            case "log.message" -> {
                                LogMessage logMessage = new LogMessageImpl(payload);
                                listener.accept(logMessage);
                            }
                            case "socket.error" ->
                                ((CompletableFuture) connectionReadyFuture).completeExceptionally(runtime("error opening socket connection: %s", msg));
                            //TODO close connection (?)
                        }
                    });
                    try {
                        session.getBasicRemote().sendText(toJson(map("_action", "socket.session.login", "token", sessionId)));
                    } catch (IOException ex) {
                        throw runtime(ex);
                    }
                }

                @Override
                public void onClose(Session session, CloseReason closeReason) {
                    logger.debug("websocket session closed, session = {}, reason = {}", session.getId(), closeReason);
                    connectionClosedFuture.complete(true);
                }

            }, clientEndpointConfig, new URI(format("%s/services/websocket/v1/main", restClient().getServerUrl().replaceFirst("http", "ws"))));
            connectionReadyFuture.get(5, TimeUnit.SECONDS);
            post("system/loggers/stream", "");
            return connectionClosedFuture;
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    @Override
    public Map<String, String> getConfig() {
        logger.debug("getConfig");
        Map<String, String> map = map();
        get("administration/system/config").asJson().getAsJsonObject().getAsJsonObject("data").entrySet().forEach((entry) -> {
            map.put(entry.getKey(), toString(entry.getValue()));
        });
        return map;
    }

    @Override
    public Map<String, ConfigDefinition> getConfigDefinitions() {
        logger.debug("getConfigDefinitions");
        Map<String, ConfigDefinition> map = map();
        get("administration/system/config?detailed=true").asJson().getAsJsonObject().getAsJsonObject("data").entrySet().stream()
                .filter(e -> toBoolean(e.getValue().getAsJsonObject().get("hasDefinition")))
                .forEach((entry) -> map.put(entry.getKey(), ConfigDefinitionImpl.builder()
                .withKey(entry.getKey())
                .withDefaultValue(toString(entry.getValue().getAsJsonObject().get("default")))
                .withEnumValues(toString(entry.getValue().getAsJsonObject().get("oneof")))
                .withDescription(toString(entry.getValue().getAsJsonObject().get("description")))
                .withModuleNamespace(toString(entry.getValue().getAsJsonObject().get("module")))
                .withModular(parseEnumOrDefault(toString(entry.getValue().getAsJsonObject().get("modular")), MCD_NONE))
                .withCategory(parseEnum(toString(entry.getValue().getAsJsonObject().get("category")), ConfigCategory.class))
                .withLocation(parseEnum(toString(entry.getValue().getAsJsonObject().get("location")), ConfigLocation.class))
                .build()));
        return map;
    }

    @Override
    public void upgradeWebapp(InputStream warFileData) {
        checkNotNull(warFileData, "data param cannot be null");
        HttpEntity multipart = MultipartEntityBuilder.create().addBinaryBody("file", listenUpload("war file upload", warFileData), ContentType.APPLICATION_OCTET_STREAM, "file.war").build();
        post("system/upgrade", multipart);
    }

    @Override
    public void recreateDatabase(InputStream dumpFileData, boolean freezesessions) {
        checkNotNull(dumpFileData, "data param cannot be null");
        HttpEntity multipart = MultipartEntityBuilder.create().addBinaryBody("file", listenUpload("dump file upload", dumpFileData), ContentType.APPLICATION_OCTET_STREAM, "file.dump").build();
        post("system/database/import?freezesessions=" + freezesessions, multipart);
    }

    @Override
    public String getConfig(String key) {
        logger.debug("getConfig for key = {}", key);
        return toString(get("administration/system/config/" + trimAndCheckNotBlank(key)).asJson().getAsJsonObject().getAsJsonPrimitive("data"));
    }

    @Override
    @Nullable
    public String eval(String script, @Nullable String language) {
        return toString(post("system/eval" + (isBlank(language) ? "" : format("?language=%s", UrlEscapers.urlFormParameterEscaper().escape(language))), new StringEntity(format("script=%s", UrlEscapers.urlFormParameterEscaper().escape(script)), APPLICATION_FORM_URLENCODED))
                .asJson().getAsJsonObject().getAsJsonObject("data").get("output"));
    }

    @Override
    public SystemApi setConfig(String key, String value) {
        logger.debug("setConfig for key = {} to value = {}", key, value);
        put("administration/system/config/" + trimAndCheckNotBlank(key), value);
        return this;
    }

    @Override
    public SystemApi setConfigs(Map<String, String> data) {
        logger.debug("setConfig with data = {}", data);
        put("administration/system/config/_MANY", data);
        return this;
    }

    @Override
    public SystemApi deleteConfig(String key) {
        logger.debug("deleteConfig for key = {}", key);
        delete("administration/system/config/" + trimAndCheckNotBlank(key));
        return this;
    }

    @Override
    public List<Map<String, String>> getPluginConfigs(String plugin) {
        logger.debug("getPluginConfigs for plugin = {}", plugin);
        List<Map<String, String>> list = list();
        get(format("administration/plugin/%s/config", trimAndCheckNotBlank(plugin))).asJson().getAsJsonObject().getAsJsonArray("data").forEach((config) -> {
            list.add(map(config.getAsJsonObject().asMap()).mapValues(JsonElement::getAsString));
        });
        return list;
    }

    @Override
    public Map<String, String> getPluginConfig(String plugin, String key) {
        logger.debug("getConfig for plugin = {} key = {}", plugin, key);
        return map(get(format("administration/plugin/%s/config/%s", trimAndCheckNotBlank(plugin), trimAndCheckNotBlank(key))).asJson().getAsJsonObject().getAsJsonObject("data").asMap()).mapValues(JsonElement::getAsString);
    }

    @Override
    public SystemApi setPluginConfig(String plugin, String key, String value, String access) {
        logger.debug("setPluginConfig for plugin = {} key = {} to value = {}", plugin, key, value);
        put(format("administration/plugin/%s/config/%s", trimAndCheckNotBlank(plugin), trimAndCheckNotBlank(key)), map("access", access, "value", value));
        return this;
    }

    @Override
    public SystemApi deletePluginConfig(String plugin, String key) {
        logger.debug("setPluginConfig for plugin = {} key = {}", plugin, key);
        delete(format("administration/plugin/%s/config/%s", trimAndCheckNotBlank(plugin), trimAndCheckNotBlank(key)));
        return this;
    }

    @Override
    public void sendBroadcast(String message) {
        checkNotBlank(message);
        post("system/messages/broadcast?message=" + encodeUrlQuery(message), "");
    }

    @Override
    public List<PatchInfo> getPatches() {
        try {
            requireSession = false; //this is not great. TODO: refactor this with aspect or something
            logger.debug("get patches");
            return stream(get("boot/patches").asJson().getAsJsonObject().getAsJsonArray("data")).map(JsonElement::getAsJsonObject).map((patch) -> {
                return new PatchInfoImpl(toString(patch.get("name")), toString(patch.get("description")), toString(patch.get("category")));
            }).collect(toList());
        } finally {
            requireSession = null;
        }
    }

    @Override
    public List<MinionStatusInfo> getServicesStatus() {
        return stream(get("system_services").asJson().getAsJsonObject().getAsJsonArray("data")).map(JsonElement::getAsJsonObject).map((patch) -> {
            String name = checkNotBlank(toString(patch.get("name")));
            String description = checkNotBlank(toString(patch.get("description")));
            String configEnabler = checkNotBlank(toString(patch.get("configEnabler")));
            MinionStatus status = parseEnum(toString(patch.get("status")), MinionStatus.class);
            return new ServiceStatusInfoImpl(name, description, configEnabler, status);
        }).collect(toList());
    }

    private static WaterwayDescriptorInfoExt parseWaterwayConfigFileInfo(ObjectNode e) {
        return WaterwayDescriptorInfoImpl.builder()
                .withCode(e.get("_id").textValue())
                .withDescription(e.get("description").textValue())
                .withNotes(e.get("notes").textValue())
                .withVersion(e.get("version").intValue())
                .withTag(e.get("tag").textValue())
                .withEnabled(e.get("enabled").booleanValue())
                .withValid(e.get("valid").booleanValue())
                .withDisabledItems(toListOfStrings(e.get("disabled").textValue()))
                .withParams(fromJson(e.get("params"), MAP_OF_STRINGS))
                .build();
    }

    private static WaterwayDescriptorRecord parseWaterwayConfigFileRecord(ObjectNode e) {
        return WaterwayDescriptorRecordImpl.builder()
                .withCode(e.get("_id").textValue())
                .withDescription(e.get("description").textValue())
                .withNotes(e.get("notes").textValue())
                .withVersion(e.get("version").intValue())
                .withEnabled(e.get("enabled").booleanValue())
                .withValid(e.get("valid").booleanValue())
                .withDisabledItems(toListOfStrings(e.get("disabled").textValue()))
                .withParams(fromJson(e.get("params"), MAP_OF_STRINGS))
                .withData(e.get("data").textValue())
                .build();
    }

    private final class ServiceStatusInfoImpl implements MinionStatusInfo {

        private final String name, description, configEnabler;
        private final MinionStatus status;

        public ServiceStatusInfoImpl(String name, String description, String configEnabler, MinionStatus status) {
            this.name = checkNotBlank(name);
            this.description = checkNotBlank(description);
            this.configEnabler = configEnabler;
            this.status = checkNotNull(status);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String getConfigEnabler() {
            return configEnabler;
        }

        @Override
        public MinionStatus getStatus() {
            return status;
        }

    }

    @Override
    public SystemStatus getStatus() {
        try {
            requireSession = false; //this is not great. TODO: refactor this with aspect or something
            logger.debug("getStatus");
            JsonElement response = get("boot/status").asJson();
            String systemStatusValue = trimAndCheckNotBlank(response.getAsJsonObject().get("status").getAsString(), "cannot find status code in response");
            return parseEnum(systemStatusValue, SystemStatus.class);
        } finally {
            requireSession = null;
        }
    }

    @Override
    public Map<String, String> getSystemInfo() {
        return fromJson(get("system/status").asJackson().get("data"), MAP_OF_STRINGS);
    }

    @Override
    public List<ConnectionInfo> getPoolStatus() {
        return stream(get("system/database/pool/debug").asJackson().get("data").get("connections").elements())
                .map(e -> new ConnectionInfoImpl(parseEnum(e.get("status").asText(), ConnectionStatus.class), e.get("trace").asText())).collect(toUnmodifiableList());
    }

    @Override
    public ClusterStatus getClusterStatus() {
        JsonNode data = get("system/cluster/status").asJackson().get("data");
        boolean isRunning = data.get("running").asBoolean();
        List<ClusterNode> nodes;
        if (isRunning) {
            nodes = list(data.get("nodes").elements()).map((JsonNode n) -> new ClusterNodeImpl(n.get("nodeId").asText(),
                    n.get("address").asText(),
                    n.get("thisNode").asBoolean(),
                    fromJson(n.get("meta"), MAP_OF_STRINGS)));
        } else {
            nodes = emptyList();
        }
        return new ClusterStatus() {
            @Override
            public boolean isRunning() {
                return isRunning;
            }

            @Override
            public List<ClusterNode> getNodes() {
                return nodes;
            }
        };
    }

    @Override
    public List<NodeStatus> getAllNodeStatus() {//TODO improve this
        Map<String, Object[]> map = map();
        stream(post("system/cluster/nodes/_ALL/invoke", map("service", "system", "method", "status")).asJackson().get("data").elements()).forEach(n -> {
            map.put(n.get("cluster_node").get("nodeId").asText(), new Object[]{fromJson(n.get("data"), MAP_OF_STRINGS), new ClusterNodeImpl(n.get("cluster_node").get("nodeId").asText(),
                n.get("cluster_node").get("address").asText(),
                n.get("cluster_node").get("thisNode").asBoolean(),
                fromJson(n.get("cluster_node").get("meta"), MAP_OF_STRINGS)), null});
        });
        stream(post("system/cluster/nodes/_ALL/invoke", map("service", "minions", "method", "getAll")).asJackson().get("data").elements()).forEach(n -> {
            map.get(n.get("cluster_node").get("nodeId").asText())[2] = stream(n.get("data").elements()).map(s -> new ServiceStatusInfoImpl(s.get("name").asText(), s.get("description").asText(), "", parseEnum(s.get("status").asText(), MinionStatus.class))).collect(toUnmodifiableList());
        });
        return map.values().stream().map(v -> new NodeStatus() {
            @Override
            public Map<String, String> getSystemInfo() {
                return (Map<String, String>) v[0];
            }

            @Override
            public List<MinionStatusInfo> getServicesStatus() {
                return (List<MinionStatusInfo>) v[2];
            }

            @Override
            public ClusterNode getNodeInfo() {
                return (ClusterNode) v[1];
            }
        }).sorted(Ordering.natural().onResultOf(n -> n.getNodeInfo().getNodeId())).collect(toUnmodifiableList());
    }

    @Override
    public void applyPatches() {
        try {
            requireSession = false; //this is not great. TODO: refactor this with aspect or something
            logger.debug("apply patches");
            post("boot/patches/apply", "");
        } finally {
            requireSession = null;
        }
    }

    @Override
    public void importFromDms() {
        logger.debug("importFromDms");
        post("system/dms/import", "");
    }

    @Override
    public void reloadConfig() {
        logger.debug("reloadConfig");
        post("administration/system/config/reload", "");
    }

    @Override
    public void reload() {
        logger.debug("reload system");
        post("system/reload", "");
    }

    @Override
    public void stop() {
        post("system/stop", "");
    }

    @Override
    public void restart() {
        post("system/restart", "");
    }

    @Override
    public void rollback(ZonedDateTime timestamp) {
        post("system/rollback?timestamp=" + encodeUrlQuery(toIsoDateTime(checkNotNull(timestamp))), "");
    }

    @Override
    public void dropCache(String cacheId) {
        checkNotBlank(cacheId);
        logger.debug("drop cache = %s", cacheId);
        post(format("system/cache/%s/drop", encodeUrlPath(cacheId)), "");
    }

    @Override
    public void dropAllCaches() {
        logger.debug("drop all caches");
        post("system/cache/drop", "");
    }

    @Override
    public Map<String, CmCacheStats> getCacheStats() {
        return list(get("system/cache/stats").asJackson().get("data").elements())
                .map(e -> CmCacheStatsImpl.builder().withName(e.get("name").asText()).withSize(e.get("objectsCount").asLong()).withEstimateMemSize(e.get("objectsSize").asLong()).build())
                .collect(toMap(CmCacheStats::getName, identity()));
    }

    @Override
    public void reconfigureDatabase(Map<String, String> config) {
        //TODO validate config
        post("system/database/reconfigure", map(config));
    }

    @Override
    public BigByteArray dumpDatabase() {
        return getBigBytes("system/database/dump");
    }

    @Override
    public String dumpThreads() {
        return checkNotBlank(get("system/threads").asJackson().get("data").get("dump").asText());
    }

    @Override
    public BigByteArray downloadDebugInfo(@Nullable String password) {
        return getBigBytes("system/debuginfo/download?secure=" + Hex.encodeHexString(nullToEmpty(password).getBytes(UTF_8)));
    }

    @Override
    public BugReportInfo sendBugReport(@Nullable String message, @Nullable String password) {
        JsonObject data = post(format("system/debuginfo/send?message=%s&secure=%s", encodeUrlQuery(nullToEmpty(message)), Hex.encodeHexString(nullToEmpty(password).getBytes(UTF_8))), "").asJson().getAsJsonObject().getAsJsonObject("data");
        return new DebugInfoImpl(toString(data.get("fileName")));
    }

    protected static class LogMessageImpl implements LogMessage {

        private final LogLevel level;
        private final String message, stacktrace, line;
        private final ZonedDateTime timestamp;

        public LogMessageImpl(Map<String, Object> payload) {
            level = checkNotNull(convert(payload.get("level"), LogLevel.class));
            message = checkNotBlank(toStringOrNull(payload.get("message")));
            line = checkNotBlank(toStringOrNull(payload.get("line")));
            stacktrace = emptyToNull(toStringOrNull(payload.get("stacktrace")));
            timestamp = checkNotNull(CmDateUtils.toDateTime(payload.get("timestamp")));
        }

        @Override
        public LogLevel getLevel() {
            return level;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        @Nullable
        public String getStacktrace() {
            return stacktrace;
        }

        @Override
        public ZonedDateTime getTimestamp() {
            return timestamp;
        }

        @Override
        public String getLine() {
            return line;
        }

    }

    private static class PatchInfoImpl implements PatchInfo {

        private final String name, description, category;

        public PatchInfoImpl(String name, String description, String category) {
            this.name = trimAndCheckNotBlank(name);
            this.description = checkNotNull(description);
            this.category = trimAndCheckNotBlank(category);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String getCategory() {
            return category;
        }

        @Override
        public String toString() {
            return "SimplePatchInfo{" + "name=" + name + ", description=" + description + ", category=" + category + '}';
        }

    }
}
