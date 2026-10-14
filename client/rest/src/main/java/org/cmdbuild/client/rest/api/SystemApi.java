/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import static com.google.common.collect.Iterables.getOnlyElement;
import com.google.gson.JsonObject;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import java.io.File;
import java.io.InputStream;
import static java.lang.String.format;
import java.time.ZonedDateTime;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.auth.multitenant.config.MultitenantMode;
import org.cmdbuild.cache.CmCacheStats;
import org.cmdbuild.client.rest.core.RestServiceClient;
import org.cmdbuild.client.rest.model.ClusterStatus;
import org.cmdbuild.client.rest.model.LogMessage;
import org.cmdbuild.client.rest.model.LoggerInfo;
import org.cmdbuild.client.rest.model.LoggerInfoImpl;
import org.cmdbuild.client.rest.model.PatchInfo;
import org.cmdbuild.config.api.ConfigDefinition;
import org.cmdbuild.dao.MyPooledDataSource.ConnectionInfo;
import org.cmdbuild.debuginfo.BugReportInfo;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRun;
import org.cmdbuild.minions.MinionStatusInfo;
import org.cmdbuild.minions.SystemStatus;
import org.cmdbuild.scheduler.ScheduledJobInfo;
import org.cmdbuild.sysmon.SystemErrorInfo;
import org.cmdbuild.systemplugin.SystemPlugin;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.json.CmJsonUtils.LIST_OF_MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.cmformat;
import static org.cmdbuild.utils.lang.CmStringUtils.escapeGroovyTripleSingleQuoteString;

public interface SystemApi extends RestServiceClient {

    List<SystemPlugin> getSystemPlugins();

    void deploySystemPlugins(List<File> files);

    List<SystemErrorInfo> getEvents();

    List<JobData> getJobs();

    List<ScheduledJobInfo> getSysJobs();

    void runSysJob(String key);

    JobData getJob(String jobId);

    JobData updateJob(JobData jobData);

    JobRun runJob(String jobId, Map<String, String> configOverride);

    JobRun getJobRun(String jobId, long runId);

    List<JobRun> getLastJobRuns(String jobId, long limit);

    List<JobRun> getLastJobErrors(String jobId, long limit);

    List<JobRun> getLastJobRuns(long limit);

    List<JobRun> getLastJobErrors(long limit);

    SystemStatus getStatus();

    List<ConnectionInfo> getPoolStatus();

    Map<String, String> getSystemInfo();

    List<MinionStatusInfo> getServicesStatus();

    ClusterStatus getClusterStatus();

    List<NodeStatus> getAllNodeStatus();

    void sendBroadcast(String message);

    boolean createEvents(String triggerId, @Nullable String filter);

    DataSource downloadSystemDiagram(Collection<String> classes);

    List<LoggerInfo> getLoggers();

    List<PatchInfo> getPatches();

    void upgradeWebapp(InputStream warFileData);

    void recreateDatabase(InputStream dumpFileData, boolean freezesessions);

    void stop();

    void restart();

    void rollback(ZonedDateTime timestamp);

    void applyPatches();

    void importFromDms();

    void setLogger(LoggerInfo logger);

    void deleteLogger(String category);

    Future<Void> streamLogMessages(Consumer<LogMessage> listener);

    void reloadConfig();

    void dropAllCaches();

    void reload();

    void dropCache(String cacheId);

    Map<String, CmCacheStats> getCacheStats();

    BigByteArray dumpDatabase();

    void reconfigureDatabase(Map<String, String> config);

    BigByteArray downloadDebugInfo(@Nullable String password);

    BugReportInfo sendBugReport(@Nullable String message, @Nullable String password);

    @Nullable
    String eval(String script, @Nullable String language);

    Map<String, String> getConfig();

    String getConfig(String key);

    Map<String, ConfigDefinition> getConfigDefinitions();

    SystemApi setConfig(String key, String value);

    SystemApi setConfigs(Map<String, String> data);

    SystemApi deleteConfig(String key);

    List<Map<String, String>> getPluginConfigs(String plugin);

    Map<String, String> getPluginConfig(String plugin, String key);

    SystemApi setPluginConfig(String plugin, String key, String value, String access);

    SystemApi deletePluginConfig(String plugin, String key);

    JsonObject configureMultitenant(MultitenantMode multitenantMode, @Nullable String classCode);

    String dumpThreads();

    void interrupt(String requestId);

    void reloadDatabasePool();

    default Map<String, String> getConfigs(String namespace) {
        return map(getConfig()).filterMapKeys(checkNotBlank(namespace) + ".");
    }

    default JobRun runJob(String jobId) {
        return runJob(jobId, emptyMap());
    }

    default SystemApi deleteConfigs(String... keys) {
        list(keys).forEach(this::deleteConfig);
        return this;
    }

    default JobRun getJobRun(long runId) {
        return getJobRun("_ANY", runId);
    }

    default void recreateDatabase(InputStream dumpFileData) {
        recreateDatabase(dumpFileData, false);
    }

    default void setLogger(String category, String level) {
        setLogger(new LoggerInfoImpl(category, level));
    }

    default BigByteArray downloadDebugInfo() {
        return downloadDebugInfo(null);
    }

    default BugReportInfo sendBugReport() {
        return sendBugReport(null, null);
    }

    default BugReportInfo sendBugReport(@Nullable String message) {
        return sendBugReport(message, null);
    }

    @Nullable
    default String eval(String script) {
        return eval(script, null);
    }

    default void executeSql(String script) {
        eval(format("cmdb.system().sql().execute('''%s''')", escapeGroovyTripleSingleQuoteString(checkNotBlank(script))));
    }

    default void executeSql(String script, Object... params) {
        executeSql(cmformat(script, params));
    }

    @Nullable
    default List<Map<String, Object>> querySql(String script) {
        String output = eval(format("return cmdb.system().sql().query('''%s''')", escapeGroovyTripleSingleQuoteString(checkNotBlank(script))));
        return isBlank(output) ? null : fromJson(output, LIST_OF_MAP_OF_OBJECTS);
    }

    @Nullable
    default List<Map<String, Object>> querySql(String script, Object... params) {
        return querySql(format(script, params));
    }

    @Nullable
    default Object querySqlSingleValue(String script, Object... params) {
        return getOnlyElement(getOnlyElement(querySql(format(script, params)), emptyMap()).values(), null);
    }

    default SystemApi setConfigs(Object... items) {
        return setConfigs(map(items));
    }

}
