/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.Maps;
import static com.google.common.collect.Maps.filterKeys;
import static com.google.common.io.Files.toByteArray;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringReader;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import java.nio.charset.StandardCharsets;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.time.Duration;
import java.time.ZonedDateTime;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import org.apache.commons.codec.binary.Base64;
import static org.apache.commons.codec.binary.Base64.isBase64;
import static org.apache.commons.collections.MapUtils.toProperties;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import org.apache.commons.io.IOUtils;
import static org.apache.commons.io.IOUtils.copyLarge;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.audit.RequestData;
import org.cmdbuild.audit.RequestInfo;
import org.cmdbuild.auth.multitenant.config.MultitenantMode;
import org.cmdbuild.client.rest.api.AttachmentApi.AttachmentData;
import org.cmdbuild.client.rest.api.AttachmentApi.AttachmentPreview;
import org.cmdbuild.client.rest.api.ClassApi;
import org.cmdbuild.client.rest.api.LookupApi;
import org.cmdbuild.client.rest.api.NodeStatus;
import org.cmdbuild.client.rest.api.SessionApi.SessionInfo;
import org.cmdbuild.client.rest.api.SystemApi;
import org.cmdbuild.client.rest.api.WokflowApi;
import org.cmdbuild.client.rest.api.WokflowApi.FlowDataAndStatus;
import org.cmdbuild.client.rest.api.WokflowApi.PlanVersionInfo;
import org.cmdbuild.client.rest.api.WokflowApi.TaskDetail;
import org.cmdbuild.client.rest.model.Attachment;
import org.cmdbuild.client.rest.model.AttributeData;
import org.cmdbuild.client.rest.model.AttributeRequestData;
import org.cmdbuild.client.rest.model.AttributeRequestDataImpl;
import org.cmdbuild.client.rest.model.Card;
import org.cmdbuild.client.rest.model.ClassData;
import org.cmdbuild.client.rest.model.ClassDataImpl;
import org.cmdbuild.client.rest.model.CustomComponentInfo;
import org.cmdbuild.client.rest.model.FlowDataImpl;
import org.cmdbuild.client.rest.model.LoggerInfo;
import org.cmdbuild.client.rest.model.RestApiCardImpl;
import static org.cmdbuild.common.Constants.BASE_CLASS_NAME;
import org.cmdbuild.config.api.ConfigDefinition;
import static org.cmdbuild.config.utils.ConfigDefinitionUtils.parseConfigPropLine;
import org.cmdbuild.dao.MyPooledDataSource;
import org.cmdbuild.dao.MyPooledDataSource.ConnectionInfo;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_NAMESPACE;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_URL;
import org.cmdbuild.dao.entrytype.AttributePermissionMode;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import org.cmdbuild.debuginfo.BugReportInfo;
import org.cmdbuild.debuginfo.BuildInfo;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccount;
import static org.cmdbuild.email.job.EmailJobConfig.EMAIL_JOB_CONFIG_EMAIL_SOURCE_PARAM;
import static org.cmdbuild.email.job.EmailJobConfig.JobConfigEmailSource.JCES_DB;
import org.cmdbuild.email.mta.EmailImapSessionProvider;
import static org.cmdbuild.email.mta.EmailMtaServiceImpl.EMAIL_HEADER_MESSAGE_ID;
import org.cmdbuild.email.mta.EmailProviderStrategy;
import static org.cmdbuild.email.template.EmailUtils.parseEmailHeaderToken;
import static org.cmdbuild.email.utils.EmailMtaUtils.emailToMessageData;
import static org.cmdbuild.email.utils.EmailMtaUtils.getMessageHeader;
import static org.cmdbuild.email.utils.EmailMtaUtils.parseEmail;
import org.cmdbuild.jobs.JobRun;
import static org.cmdbuild.jobs.JobRunStatusImpl.serializeJobRunStatus;
import static org.cmdbuild.minions.MinionUtils.buildServicesStatusInfoMessage;
import org.cmdbuild.minions.SystemStatus;
import static org.cmdbuild.minions.SystemStatus.SYST_READY;
import static org.cmdbuild.minions.SystemStatus.SYST_READY_RESTART_REQUIRED;
import static org.cmdbuild.minions.SystemStatusUtils.serializeSystemStatus;
import static org.cmdbuild.platform.UpgradeUtils.validateWarData;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.ReportInfo;
import org.cmdbuild.report.ReportInfoImpl;
import org.cmdbuild.sysmon.SystemErrorInfo;
import org.cmdbuild.systemplugin.SystemPlugin;
import static org.cmdbuild.systemplugin.SystemPluginUtils.buildPluginStatusInfoMessage;
import org.cmdbuild.translation.dao.Translation;
import static org.cmdbuild.utils.cli.Main.getWarFile;
import org.cmdbuild.utils.cli.utils.CliCommand;
import org.cmdbuild.utils.cli.utils.CliParameter;
import static org.cmdbuild.utils.cli.utils.CliUtils.buildProgressListener;
import static org.cmdbuild.utils.cli.utils.CliUtils.getDbdumpFile;
import static org.cmdbuild.utils.cli.utils.CliUtils.hasInteractiveConsole;
import static org.cmdbuild.utils.cli.utils.CliUtils.prepareDumpFile;
import org.cmdbuild.utils.cli.utils.ConsoleClientHelper;
import static org.cmdbuild.utils.date.CmDateUtils.dateTimeFileSuffix;
import static org.cmdbuild.utils.date.CmDateUtils.getReadableTimezoneOffset;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toDuration;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeLocal;
import static org.cmdbuild.utils.date.CmDateUtils.toUserDuration;
import static org.cmdbuild.utils.date.CmDateUtils.toUserReadableDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toUserReadableDateTimeWithTimezone;
import org.cmdbuild.utils.encode.CmPackUtils;
import static org.cmdbuild.utils.encode.CmPackUtils.isPacked;
import static org.cmdbuild.utils.encode.CmPackUtils.unpack;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackBytes;
import static org.cmdbuild.utils.gui.GuiFileEditor.editFile;
import org.cmdbuild.utils.io.BigByteArray;
import org.cmdbuild.utils.io.BigByteArrayInputStream;
import static org.cmdbuild.utils.io.CmIoUtils.isPlaintext;
import static org.cmdbuild.utils.io.CmIoUtils.javaTmpDir;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import org.cmdbuild.utils.io.CmZipUtils;
import static org.cmdbuild.utils.io.CmZipUtils.dirToZip;
import static org.cmdbuild.utils.io.CmZipUtils.unzipToDir;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmExecutorUtils.sleepSafe;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.multilineWithOffset;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import static org.cmdbuild.utils.sked.SkedJobClusterMode.RUN_ON_ALL_NODES;
import static org.cmdbuild.utils.url.CmUrlUtils.decodeUrlParams;
import org.json.JSONArray;
import org.json.JSONObject;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class RestCommandRunner extends AbstractRestCommandRunner {

    private final static EmailProviderStrategy emailProviderStrategy = new EmailProviderStrategy();

    public RestCommandRunner() {
        super(list("restws", "r"), "test cmdbuild rest ws");
    }

    protected RestCommandRunner(List<String> names, String description) {
        super(names, description);
    }

    @CliCommand
    protected void curl() {
        System.out.print(buildCurlCli(login().getSessionToken(), "services/rest/v4/"));
    }

    @CliCommand
    protected void status() {
        SystemStatus systemStatus = client.system().getStatus();
        System.out.println("system status: " + serializeSystemStatus(systemStatus));
        if (list(SYST_READY, SYST_READY_RESTART_REQUIRED).contains(systemStatus)) {
            System.out.println();
            SystemApi system = login().system();
            List<NodeStatus> nodes = system.getAllNodeStatus();

            System.out.println(buildServicesStatusInfoMessage(nodes.get(0).getServicesStatus()));
            System.out.println();
            System.out.print(buildPluginStatusInfoMessage(system.getSystemPlugins()));

            nodes.forEach(n -> {
                Map<String, String> systemInfo = n.getSystemInfo();
                if (nodes.size() > 1) {
                    System.out.printf("node:      %-10s    %20s  %s\n", n.getNodeInfo().getNodeId(), n.getNodeInfo().getAddress(), n.getNodeInfo().isThisNode() ? "(this node)" : "");
                }
                if (!n.getNodeInfo().getWorkgroups().isEmpty()) {
                    System.out.printf("workgroup: %s\n", Joiner.on(",").join(n.getNodeInfo().getWorkgroups()));
                }
                System.out.printf("version:   %s\nbuild:     %s\nruntime:   %s\n", systemInfo.get("version_full"), systemInfo.get("build_info"), systemInfo.get("runtime"));
                System.out.printf("uptime:    %s %s\n", systemInfo.get("hostname"), toUserDuration(systemInfo.get("uptime")));
                System.out.printf("memory:    %,d MB used, %,d MB total, %,d MB max (java heap+metaspace memory)\n", toIntegerOrNull(systemInfo.get("java_memory_used")), toIntegerOrNull(systemInfo.get("java_memory_total")), toIntegerOrNull(systemInfo.get("java_memory_max")));
                System.out.printf("process:   pid %s, %,d MB total (process memory)\n", systemInfo.get("java_pid"), toIntegerOrNull(systemInfo.get("process_memory_used")));
                System.out.printf("db pool:   %s active, %s idle, %s max\n\n", systemInfo.get("datasource_active_connections"), systemInfo.get("datasource_idle_connections"), systemInfo.get("datasource_max_active_connections"));
            });

            List<SystemErrorInfo> events = system.getEvents().stream().filter(e -> e.getTimestamp().isAfter(now().minusHours(1))).collect(toImmutableList());
            if (!events.isEmpty()) {
                System.out.printf("\nevents:\n");
                events.forEach(e -> System.out.printf("%30s %10s %10s %20s %s\n", toIsoDateTimeLocal(e.getTimestamp()), e.getLevel().name(), e.getCategory(), e.getSource(), e.getMessage()));
            }
        }
    }

    @CliCommand
    protected void poolStatus() {
        List<MyPooledDataSource.ConnectionInfo> connections = login().system().getPoolStatus();

        long active = connections.stream().filter(ConnectionInfo::isActive).count(), idle = connections.size() - active;

        System.out.printf("active connections:  %s\nidle connections  :  %s\n\n", active, idle);

        if (active > 0) {
            if (connections.stream().filter(ConnectionInfo::isActive).allMatch(c -> isBlank(c.getTrace()))) {
                System.out.print("WARNING: active connections trace not available; enable pool debug (and restart) to get connection trace.\n\n");
            } else {
                System.out.print("active connections detail:\n\n");
                connections.stream().filter(ConnectionInfo::isActive).forEach(e -> {
                    AtomicInteger i = new AtomicInteger(0);
                    System.out.printf("  connection #%2s %s", i.getAndIncrement(), serializeEnum(e.getStatus()).toUpperCase());
                    if (isNotBlank(e.getTrace())) {
                        System.out.printf(":\n\n%s\n", multilineWithOffset(e.getTrace(), 4));
                    } else {
                        System.out.println();
                    }
                    System.out.println();
                });
            }
        }
    }

    @CliCommand("threads")
    protected void threadDump() {
        System.out.println(login().system().dumpThreads());
    }

    @CliCommand
    protected void dump() throws IOException {
        BigByteArray data = login().system().dumpDatabase();
        if (hasInteractiveConsole()) {
            File file = new File(format("cmdbuild_%s.dump", dateTimeFileSuffix()));
            writeToFile(file, data);
            System.out.printf("stored dump to file = %s\n (%s)", file.getAbsoluteFile(), byteCountToDisplaySize(data.length()));
        } else {
            copyLarge(new BigByteArrayInputStream(data), System.out);
        }
    }

    @CliCommand("reconfigure")
    protected void reconfigureDatabase(String newDatabaseUrl) throws IOException {
        System.out.printf("reconfigure database with new url = %s\n", newDatabaseUrl);
        login().system().reconfigureDatabase(map(DATABASE_CONFIG_URL, checkNotBlank(newDatabaseUrl)));
    }

    @CliCommand(alias = {"debug", "downloadDebugInfo", "downloadBugreport", "getbugreport"})
    protected void debugInfo() throws IOException {
        if (hasInteractiveConsole()) {
            String brPassword = new String(System.console().readPassword("bug report password (optional): "));
            System.out.print("download bug report... ");
            BigByteArray data = login().system().downloadDebugInfo(brPassword);
            File outputFile = new File(format("debug_%s.zip", dateTimeFileSuffix()));
            if (isNotBlank(brPassword)) {
                outputFile = new File(outputFile.getName() + ".secure");
            }
            writeToFile(outputFile, data);
            System.out.printf("done\noutput written to %s %s\n", outputFile.getAbsolutePath(), FileUtils.byteCountToDisplaySize(outputFile.length()));
        } else {
            BigByteArray data = login().system().downloadDebugInfo();
            copyLarge(new BigByteArrayInputStream(data), System.out);
        }
    }

    @CliCommand("sendbugreport")
    protected void bugreport() throws IOException {
        String message = System.console().readLine("bug report message (optional): ");
        String brPassword = new String(System.console().readPassword("bug report password (optional): "));
        System.out.println("sending bug report");
        BugReportInfo debugInfo = login().system().sendBugReport(message, brPassword);
        System.out.printf("bug report sent, file name = %s\n", debugInfo.getFileName());
    }

    @CliCommand
    protected void eval(String script) {
        eval(script, null);
    }

    @CliCommand("evalg")
    protected void evalGroovy(String script) {
        eval(script, "groovy");
    }

    @CliCommand("evalp")
    protected void evalPython(String script) {
        eval(script, "python");
    }

    @CliCommand
    protected void dumpPrivileges(String groupName) {
        dumpPrivileges(groupName, null);
    }

    @CliCommand
    protected void dumpPrivileges(String groupName, @Nullable String filter) {
        evalGroovy("cmdb.system().getService(\"privilegeDebugHelperService\").dumpDebugInfoForGroup(\"%s\",\"%s\")".formatted(checkNotBlank(groupName), nullToEmpty(filter)));
    }

    private void eval(String script, @Nullable String language) {
        if (new File(script).isFile()) {
            script = readToString(new File(script));
        }
        Object output = login().system().eval(script, language);
        System.out.printf("output: %s\n", output);
    }

    @CliCommand
    protected void querySql() {
        querySql(readToString(System.in));
    }

    @CliCommand
    protected void querySql(String script) {
        Object output = login().system().querySql(script);
        System.out.printf("output: %s\n", output);
    }

    @CliCommand
    protected void test() {
        boolean ok = true;
        try {
            SessionInfo sessionInfo = login().session().getSessionInfo();
            logger.info("current session info = {}", sessionInfo);
        } catch (Exception ex) {
            logger.error("error", ex);
            ok = false;
        }
        System.out.println("test " + (ok ? "OK" : "ERROR"));
    }

    @CliCommand
    protected void getSessionToken() {
        SessionInfo sessionInfo = login().session().getSessionInfo();
        System.out.println(sessionInfo.getSessionToken());
    }

    @CliCommand("sessions")
    protected void getSessions() {
        List<SessionInfo> sessions = login().session().getAllSessionsInfo();
        System.out.printf("active session count = %s\n\n", sessions.size());
        sessions.forEach(s -> System.out.printf("    %s   %-16s    last active %s ago ( %s )\n", s.getSessionToken(), s.getUsername(), toUserDuration(Duration.between(s.getLastActive(), now())), toIsoDateTimeLocal(s.getLastActive())));
    }

    @CliCommand(alias = {"lookupValues", "lookup", "getlookup"})
    protected void getLookupValues(String lookupTypeId) {
        List<LookupApi.LookupValue> values = login().lookup().getValues(lookupTypeId);
        System.out.println("received lookup values for type: " + lookupTypeId);
        values.forEach((value) -> {
            System.out.format("\t%-16s\t%-16s\t%s\n", value.getId(), value.getCode(), value.getDescription());
        });
    }

    @CliCommand
    protected void configureMultitenant(String tenantMode) {
        configureMultitenant(tenantMode, null);
    }

    @CliCommand
    protected void configureMultitenant(String tenantMode, @Nullable String className) {
        switch (tenantMode) {
            case "class" ->
                login().system().configureMultitenant(MultitenantMode.MTM_CMDBUILD_CLASS, className);
            case "function" ->
                login().system().configureMultitenant(MultitenantMode.MTM_DB_FUNCTION, null);
            default ->
                System.out.printf(format("Cannot configure tenant, unknown tenant mode ' %s ', valid modes are 'class' and 'function'\n", tenantMode));
        }
        System.out.printf("Multitenant enabled, reloading system\n");
        login().system().reload();
    }

    @CliCommand("class")
    protected void getClass(String classId) {
        ClassApi api = login().classe();
        ClassData classeData = api.getById(classId);
        System.out.println("received class for id: " + classeData.getId() + "\n");
        printClass(classeData);
        List<AttributeData> attributes = api.getAttributes(classId);
        System.out.println("\nclass attributes: \n");
        attributes.forEach((attr) -> {
            System.out.printf("\t%-16s\t%s\n", attr.getName(), attr.getType());
        });
    }

    @CliCommand
    protected void editClass(String classId) {
        ClassApi classApi = login().classe();
        String classJsonData = classApi.getRawJsonById(classId);
        String modifiedData = editFile(classJsonData, classId);
        if (!Objects.equals(classJsonData, modifiedData)) {
            ClassData classData = classApi.update(classId, modifiedData).getClasse();
            System.out.println("updated classe for id: " + classData.getId());
        }
    }

    @CliCommand("classes")
    protected void getClasses() {
        List<ClassData> classes = login().classe().getAll();
        System.out.println("received class data for " + classes.size() + " classes:");
        classes.forEach((classeData) -> {
            System.out.println();
            printClass(classeData);
        });
    }

    @CliCommand
    protected void createClass(String classId, Map<String, String> data) {
        ClassData classData = cliToClassData(classId, data);
        classData = login().classe().create(classData);
        System.out.println("created classe for id: " + classData.getId());
        printClass(classData);
    }

    @CliCommand
    protected void updateClass(String classId, Map<String, String> data) {
        ClassData classData = cliToClassData(classId, data);
        classData = login().classe().update(classData).getClasse();
        System.out.println("updated classe for id: " + classData.getId());
        printClass(classData);
    }

    @CliCommand
    protected void deleteClass(String classId) {
        login().classe().deleteById(classId);
        System.out.println("deleted class for id: " + classId);
    }

    @CliCommand(alias = {"getAttr", "readAttr", "readAttribute"})
    protected void getAttribute(String classId, String attrId) {
        AttributeData attributeData = login().classe().getAttr(classId, attrId);
        System.out.println("get attr for id: " + attributeData.getName());
        printAttribute(attributeData);
    }

    @CliCommand("createAttr")
    protected void createAttribute(String classId, Map<String, String> data) {
        AttributeRequestData requestData = paramToAttrData(data.get("name"), data);
        AttributeData attributeData = login().classe().createAttr(classId, requestData).getAttr();
        System.out.println("created attr for id: " + attributeData.getName());
        printAttribute(attributeData);
    }

    @CliCommand("updateAttr")
    protected void updateAttribute(String classId, String attrId, Map<String, String> data) {
        AttributeRequestData requestData = paramToAttrData(attrId, data);
        AttributeData attributeData = login().classe().updateAttr(classId, requestData).getAttr();
        System.out.println("updated attr for id: " + attributeData.getName());
        printAttribute(attributeData);
    }

    @CliCommand("deleteAttr")
    protected void deleteAttribute(String classId, String attrId) {
        login().classe().deleteAttr(classId, attrId);
        System.out.println("deleted attr for id: " + attrId);
    }

    private void printAttribute(AttributeData attr) {
        System.out.printf("\t%-16s\t%s\n", attr.getName(), attr.getType());//TODO
//		{"success":true,"data":{"type":"string","name":"Description","description":"Description","displayableInList":true,"domainName":null,"unique":true,"mandatory":true,"inherited":true,"active":true,"index":2,"defaultValue":null,"group":"","precision":null,"scale":null,"targetClass":null,"targetType":null,"length":250,"editorType":null,"filter":null,"values":[],"writable":true,"hidden":false,"metadata":{},"classOrder":null,"ipType":null,"lookupType":null,"_id":"Description"}}

    }

    private AttributeRequestData paramToAttrData(String attrId, Map<String, String> data) {
        return AttributeRequestDataImpl.builder()
                .withActive(toBooleanOrDefault(data.get("active"), true))
                .withDescription(firstNotNull(data.get("description"), attrId))
                .withName(attrId)
                .withMode(firstNotBlank(data.get("mode"), AttributePermissionMode.APM_WRITE.name()))
                .withType(firstNotNull(trimToNull(data.get("type")), "string"))
                .withShowInGrid(toBooleanOrDefault(data.get("showInGrid"), true))
                .withUnique(toBooleanOrDefault(data.get("unique"), false))
                .withRequired(toBooleanOrDefault(data.get("required"), false))
                //TODO handle all data
                .build();
    }

    private ClassData cliToClassData(String classId, Map<String, String> data) {
        return ClassDataImpl.builder()
                .withActive(toBooleanOrDefault(data.get("active"), true))
                .withName(classId)
                .withDescription(firstNotNull(trimToNull(data.get("description")), classId))
                .withParentId(emptyToNull(data.get("parent")))
                .withSuperclass(toBooleanOrDefault(data.get("prototype"), false))
                .withType(firstNotNull(trimToNull(data.get("type")), "standard"))
                .build();
    }

    private void printClass(ClassData classeData) {
        map("name", classeData.getName(),
                "description", classeData.getDescription(),
                "type", classeData.getType(),
                "parent", classeData.getParentId(),
                "superclass", classeData.isSuperclass(),
                "active", classeData.isActive()).entrySet().forEach((entry) -> {
            System.out.format("%-16s\t%-32s\n", entry.getKey(), entry.getValue());
        });
    }

    @CliCommand
    protected void getCard(String classId, String cardId) {
        Card card = login().card().getCard(classId, cardId);
        System.out.println("received card for id: " + card.getId());
        card.getAttributes().entrySet().forEach((entry) -> {
            System.out.format("\t%-32s\t%-32s\n", entry.getKey(), entry.getValue());
        });
    }

    @CliCommand("cards")
    protected void getCards(String classeId) {
        List<Card> cards = login().card().getCards(classeId);
        System.out.println("received card values for classe: " + classeId);
        cards.forEach((card) -> {
            System.out.format("\t%-10s\t%s\n", card.getId(), card.getDescription());
        });
    }

    @CliCommand("query")
    protected void queryCards(String filter, String sort, String offset, String limit) {
        List<Card> cards = login().card().queryCards()
                .filter(filter)
                .sort(sort)
                .limit(toIntegerOrNull(limit))
                .offset(toIntegerOrNull(offset))
                .getCards();
        System.out.println("received card values for query");
        cards.forEach((card) -> {
            System.out.format("\t%-10s\t%s\n", card.getId(), card.getDescription());
        });
    }

    @CliCommand
    protected void deleteCard(String classId, String cardId) {
        login().card().deleteCard(classId, cardId);
        System.out.println("deleted card for id: " + cardId);
    }

    @CliCommand
    protected void createCard(String classId, Map<String, String> data) {
        Card card = login().card().createCard(classId, new RestApiCardImpl(data));
        System.out.println("created card for id: " + card.getId());
    }

    @CliCommand
    protected void getAttachments(String classId, String cardId) {
        List<Attachment> attachments = login().attachment().getCardAttachments(classId, cardId);
        attachments.forEach((attachment) -> {
            System.out.printf("%-32s\t%-32s\t%-32s\n", attachment.getId(), attachment.getFileName(), attachment.getVersion());
        });
    }

    @CliCommand
    protected void getAttachmentHistory(String classId, String cardId, String attachmentId) {
        List<Attachment> attachments = login().attachment().getAttachmentHistory(classId, cardId, attachmentId);
        attachments.forEach((attachment) -> {
            System.out.printf("%-32s\t%-32s\n", attachment.getFileName(), attachment.getVersion());
        });
    }

    @CliCommand
    protected void createAttachment(String classId, String cardId, String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        checkArgument(file.isFile(), "file %s is not a valid file", file);
        Attachment attachment = login().attachment().createCardAttachment(classId, cardId, file.getName(), new FileInputStream(file)).getAttachment();
        System.out.printf("created attachment: %s\n", attachment);
    }

    @CliCommand
    protected void updateAttachment(String classId, String cardId, String attachmentId, String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        checkArgument(file.isFile(), "file %s is not a valid file", file);
        Attachment attachment = login().attachment().updateCardAttachment(classId, cardId, attachmentId, file.getName(), new FileInputStream(file)).getAttachment();
        System.out.printf("updated attachment: %s\n", attachment);
    }

    @CliCommand
    protected void deleteattachment(String classId, String cardId, String attachmentId) {
        login().attachment().deleteCardAttachment(classId, cardId, attachmentId);
        System.out.printf("OK\n");
    }

    @CliCommand
    protected void getAttachment(String classId, String cardId, String attachmentId) throws IOException {
        AttachmentData data = login().attachment().download(classId, cardId, attachmentId).getData();
        IOUtils.write(data.toByteArray(), System.out);
    }

    @CliCommand
    protected void getAttachmentPreview(String classId, String cardId, String attachmentId) throws IOException {
        AttachmentPreview data = login().attachment().preview(classId, cardId, attachmentId).getPreview();
        if (data.hasPreview()) {
            IOUtils.write(data.toByteArray(), System.out);
        } else {
            System.err.println("NO PREVIEW AVAILABLE");
            System.exit(1);//TOOD set return value, and do clean shutdown
        }
    }

    @CliCommand
    protected void exportAttachments() throws IOException {
        BigByteArray data = login().attachment().exportAllDocumentsToZipFile();
        if (hasInteractiveConsole()) {
            File outputFile = new File(format("dms_export_%s.zip", dateTimeFileSuffix()));
            writeToFile(outputFile, data);
            System.out.printf("output written to %s %s\n", outputFile.getAbsolutePath(), FileUtils.byteCountToDisplaySize(outputFile.length()));
        } else {
            copyLarge(data.toInputStream(), System.out);
        }
    }

    @CliCommand
    protected void importFromDms() {
        login().system().importFromDms();
        System.out.println("OK");
    }

    @CliCommand
    protected void syncDms() {
        login().system().eval("cmdb.system().getService(\"dmsMetadataSyncHelper\").run()", "groovy");
        System.out.println("OK");
    }

    @CliCommand
    protected void syncDmsDocumentId() {
        login().system().eval("cmdb.system().getService(\"dmsMetadataSyncHelper\").syncDocumentId(false)", "groovy");
        System.out.println("OK");
    }

    @CliCommand
    protected void syncDmsDocumentIdWithHash() {
        login().system().eval("cmdb.system().getService(\"dmsMetadataSyncHelper\").syncDocumentId(true)", "groovy");
        System.out.println("OK");
    }

    @CliCommand
    protected void getProcesses() {
        login().workflow().getPlans().forEach((plan) -> {
            System.out.format("%-32s\t%-6s\t%-32s\n", plan.getId(), nullToEmpty(plan.getProvider()), plan.getDescription());
        });
    }

    @CliCommand
    protected void getProcess(String processId) {
        WokflowApi workflow = login().workflow();
        WokflowApi.PlanInfo plan = workflow.getPlan(processId);
        List<WokflowApi.PlanVersionInfo> planVersions = workflow.getPlanVersions(processId);
        System.out.printf("plan %s (%s):\n", plan.getId(), plan.getDescription());
        planVersions.forEach((version) -> {
            System.out.printf("\t%1s  %-16s\t%-16s\t%s\n", version.isDefault() ? "*" : "", version.getProvider(), version.getVersion(), version.getPlanId());
        });
    }

    @CliCommand
    protected void getProcessTemplate(String processId) {
        String xpdlTemplate = login().workflow().getXpdlTemplate(processId);
        System.out.println(xpdlTemplate);
    }

    @CliCommand(alias = {"uploadProcessXpdl", "setProcessXpdl", "setxpdl"})
    protected void uploadProcess(String processId, String fileName) throws FileNotFoundException {
        PlanVersionInfo versionInfo = doUploadProcess(processId, fileName, false);
        System.out.printf("created plan: %s\n", versionInfo);
    }

    @CliCommand(alias = {"replaceProcessXpdl", "replacexpdl"})
    protected void replaceProcess(String processId, String fileName) throws FileNotFoundException {
        PlanVersionInfo versionInfo = doUploadProcess(processId, fileName, true);
        System.out.printf("upgraded plan: %s\n", versionInfo);
    }

    private PlanVersionInfo doUploadProcess(String processId, String fileName, boolean replace) throws FileNotFoundException {
        File file = new File(fileName);
        checkArgument(file.isFile(), "file %s is not a valid file", file);
        WokflowApi workflow = login().workflow();
        if (replace) {
            return workflow.replacePlanVersion(processId, new FileInputStream(file)).getPlanVersionInfo();
        } else {
            return workflow.uploadPlanVersion(processId, new FileInputStream(file)).getPlanVersionInfo();
        }
    }

    @CliCommand
    protected void downloadProcess(String classId, String planId) {
        System.out.println(login().workflow().downloadPlanVersion(classId, planId));
    }

    @CliCommand(alias = {"getProcessXpdl", "getxpdl"})
    protected void downloadProcessXpdl(String classId) {
        WokflowApi workflow = login().workflow();
        List<WokflowApi.PlanVersionInfo> versions = workflow.getPlanVersions(classId);
        System.out.println(workflow.downloadPlanVersion(classId, versions.iterator().next().getPlanId()));
    }

    @CliCommand(alias = {"flowgraph", "fg"})
    protected void getFlowGraph(String classId, String cardId) throws IOException {
        byte[] data = login().workflow().downloadFlowGraph(classId, toLong(cardId));
        File file = new File(tempDir(), "file.png");
        FileUtils.writeByteArrayToFile(file, data);
        Desktop.getDesktop().open(file);
    }

    @CliCommand(alias = {"simplifiedflowgraph", "sfg"})
    protected void getSimplifiedFlowGraph(String classId, String cardId) throws IOException {
        byte[] data = login().workflow().downloadSimplifiedFlowGraph(classId, toLong(cardId));
        File file = new File(tempDir(), "file.png");
        FileUtils.writeByteArrayToFile(file, data);
        Desktop.getDesktop().open(file);
    }

    @CliCommand
    protected void databaseDiagram(String classesParam) throws IOException {
        List<String> classes = Splitter.on(",").splitToList(checkNotBlank(classesParam));
        DataSource data = login().system().downloadSystemDiagram(classes);
        File file = new File(tempDir(), data.getName());
        FileUtils.copyInputStreamToFile(data.getInputStream(), file);
        System.out.printf("open file = %s\n", file.getAbsolutePath());
        Desktop.getDesktop().open(file);
    }

    @CliCommand
    protected void uploadCustomPageFile(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        checkArgument(file.isFile(), "file %s is not a valid file", file);
        uploadCustomPage(new FileInputStream(file), "", null);
    }

    @CliCommand
    protected void uploadCustomPageDir(String dirName) throws FileNotFoundException {
        File dir = new File(dirName);
        checkArgument(dir.isDirectory(), "dir %s is not a valid directory", dir);
        byte[] data = dirToZip(dir);
        uploadCustomPage(new ByteArrayInputStream(data), "", null);
    }

    @CliCommand
    protected void uploadCustomPageDir(String dirName, String description) throws FileNotFoundException {
        File dir = new File(dirName);
        checkArgument(dir.isDirectory(), "dir %s is not a valid directory", dir);
        byte[] data = dirToZip(dir);
        uploadCustomPage(new ByteArrayInputStream(data), description, null);
    }

    @CliCommand
    protected void uploadCustomPageDir(String dirName, String description, String targetDevice) {
        File dir = new File(dirName);
        checkArgument(dir.isDirectory(), "dir %s is not a valid directory", dir);
        byte[] data = dirToZip(dir);
        uploadCustomPage(new ByteArrayInputStream(data), description, targetDevice);
    }

    @CliCommand
    protected void uploadContextMenu(String dirName) throws FileNotFoundException {
        File dir = new File(dirName);
        checkArgument(dir.isDirectory(), "dir %s is not a valid directory", dir);
        byte[] data = dirToZip(dir);
        uploadContextMenu(new ByteArrayInputStream(data), dir.getName(), null);
    }

    @CliCommand
    protected void uploadContextMenu(String dirName, String description, String targetDevice) {
        File dir = new File(dirName);
        checkArgument(dir.isDirectory(), "dir %s is not a valid directory", dir);
        byte[] data = dirToZip(dir);
        uploadContextMenu(new ByteArrayInputStream(data), description, targetDevice);
    }

    @CliCommand
    protected void uploadCustomWidget(String dirName) throws FileNotFoundException {
        File dir = new File(dirName);
        checkArgument(dir.isDirectory(), "dir %s is not a valid directory", dir);
        byte[] data = dirToZip(dir);
        uploadCustomWidget(new ByteArrayInputStream(data), dir.getName(), null);
    }

    @CliCommand
    protected void uploadCustomWidget(String dirName, String description, String targetDevice) throws FileNotFoundException {
        File dir = new File(dirName);
        checkArgument(dir.isDirectory(), "dir %s is not a valid directory", dir);
        byte[] data = dirToZip(dir);
        uploadCustomWidget(new ByteArrayInputStream(data), description, targetDevice);
    }

    @CliCommand
    protected void uploadContextMenuForClass(String dirName, String description, String className) {
        uploadContextMenuForClass(dirName, description, className, null, true);
    }

    @CliCommand
    protected void uploadContextMenuForProcess(String dirName, String description, String className) {
        uploadContextMenuForClass(dirName, description, className, null, false);
    }

    @CliCommand
    protected void uploadContextMenuForClass(String dirName, String description, String className, String applicability) {
        uploadContextMenuForClass(dirName, description, className, applicability, true);
    }

    @CliCommand
    protected void uploadContextMenuForProcess(String dirName, String description, String className, String applicability) {
        uploadContextMenuForClass(dirName, description, className, applicability, false);
    }

    @CliCommand
    protected void uploadContextMenuForClass(String dirName, String description, String className, @Nullable String applicability, boolean isClass) {
        String jsFileContent = readToString(new File(dirName));
        String rawClassJson;
        if (isClass) {
            rawClassJson = client.doLoginWithAnyGroup(username, password).classe().getRawJsonById(className);
        } else {
            rawClassJson = client.doLoginWithAnyGroup(username, password).classe().getProcessRawJsonById(className);
        }
        JSONObject classJson = new JSONObject(rawClassJson);
        Pair<Integer, JSONObject> oldContextMenu = getContextMenuToUpdateOrNull(classJson.getJSONArray("contextMenuItems"), description);
        if (oldContextMenu == null) {
            System.out.println(format("Adding new contextmenu with label < %s >", description));
            JSONObject contextMenuJson = new JSONObject().put("label", description)
                    .put("type", "custom")
                    .put("active", true)
                    .put("visibility", applicability)
                    .put("componentId", "")
                    .put("script", jsFileContent);
            classJson.getJSONArray("contextMenuItems").put(contextMenuJson);
        } else {
            System.out.println(format("Context menu with label < %s > already exists, updating it", description));
            classJson.getJSONArray("contextMenuItems").remove(oldContextMenu.getLeft());
            JSONObject updatedContextMenu = oldContextMenu.getRight().put("script", jsFileContent);
            classJson.getJSONArray("contextMenuItems").put(updatedContextMenu);
        }
        client.doLoginWithAnyGroup(username, password).classe().update(className, classJson.toString());
        System.out.println("Upload completed");
    }

    private Pair<Integer, JSONObject> getContextMenuToUpdateOrNull(JSONArray contextMenus, String label) {
        for (int i = 0; i < contextMenus.length(); i++) {
            if (((JSONObject) contextMenus.get(i)).get("label").equals(label)) {
                Pair<Integer, JSONObject> newPair = Pair.of(i, (JSONObject) contextMenus.get(i));
                return newPair;
            }
        }
        return null;
    }

    private void uploadCustomPage(InputStream in, String description, @Nullable String targetDevice) {
        CustomComponentInfo customPageInfo = login().customComponent().uploadCustomPage(in, description, targetDevice).getCustomComponentInfo();
        System.out.printf("uploaded custom page: %s (%s)\n", customPageInfo.getName(), customPageInfo.getDescription());
    }

    private void uploadContextMenu(InputStream in, String description, @Nullable String targetDevice) {
        CustomComponentInfo contextMenuInfo = login().customComponent().uploadCustomContextMenu(in, description, targetDevice).getCustomComponentInfo();
        System.out.printf("uploaded context menu: %s (%s)\n", contextMenuInfo.getName(), contextMenuInfo.getDescription());
    }

    private void uploadCustomWidget(InputStream in, String description, @Nullable String targetDevice) {
        CustomComponentInfo customWidget = login().customComponent().uploadCustomWidget(in, description, targetDevice).getCustomComponentInfo();
        System.out.printf("uploaded custom widget: %s (%s)\n", customWidget.getName(), customWidget.getDescription());
    }

    @CliCommand(alias = {"startprocess"})
    protected void startFlow(String processId, Map<String, Object> data) {
        FlowDataAndStatus flow = login().workflow().start(processId, FlowDataImpl.builder().withAttributes(data).build()).getFlowData();
        printFlowActionOutput("started", flow);
    }

    @CliCommand
    protected void completeTask(String processId, String instanceId, String taskId, Map<String, Object> data) {
        FlowDataAndStatus flow = login().workflow().advance(processId, instanceId, taskId, FlowDataImpl.builder().withAttributes(data).build()).getFlowData();
        printFlowActionOutput("advanced", flow);
    }

    private void printFlowActionOutput(String action, FlowDataAndStatus flow) {
        System.out.printf("%s process instance with id: %s\n\tflow status is: %s\n\ttasklist size: %s\n", action, flow.getFlowCardId(), flow.getFlowStatus(), flow.getTaskList().size());
        flow.getTaskList().forEach((task) -> {
            System.out.println();
            printTaskDetail(task);
        });
    }

    @CliCommand(alias = {"getprocessinstance", "flow"})
    protected void getFlow(String processId, String instanceId) {
        WokflowApi.FlowData walk = login().workflow().get(processId, instanceId);
        System.out.println("received process instance for id: " + walk.getFlowId());
        System.out.printf("status is: %s\n", walk.getStatus());
        walk.getAttributes().entrySet().forEach((entry) -> {
            System.out.format("\t%-32s\t%-32s\n", entry.getKey(), entry.getValue());
        });
    }

    @CliCommand(alias = {"tasks", "tasklist"})
    protected void getTaskList(String processId, String instanceId) {
        List<WokflowApi.TaskInfo> list = login().workflow().getTaskList(processId, instanceId);
        System.out.println("received process instance activities for id: " + instanceId);
        list.forEach((activity) -> {
            System.out.format("\t%-48s\t%-32s\n", activity.getId(), activity.getDescription());
        });
    }

    @CliCommand(alias = {"task"})
    protected void getTask(String processId, String instanceId, String taskId) {
        WokflowApi.TaskDetail task = login().workflow().getTask(processId, instanceId, taskId);
        printTaskDetail(task);
    }

    @CliCommand(alias = {"getstarttask"})
    protected void getStartProcessTask(String processId) {
        WokflowApi.TaskDetail task = login().workflow().getStartProcessTask(processId);
        printTaskDetail(task);
    }

    private void printTaskDetail(TaskDetail task) {
//		System.out.format("received task detail for task %s (%s)\n", taskId, task.getDescription());
        System.out.format("task detail for task %s (%s)\n", task.getId(), task.getDescription());
        task.getParams().forEach((param) -> {
            System.out.format("\t%-32s\trequired = %-5s\twritable = %-5s\taction = %-5s\ttype = %-10s\t%-32s", param.getName(), param.isRequired(), param.isWritable(), param.isAction(), param.getDetail().getType(), param.getDetail().targetInfoToString());
            if (param.getDetail().hasFilter()) {
                System.out.printf("\tfilter = %s", param.getDetail().getFilter());
            }
            System.out.println();
        });

    }

    @CliCommand(alias = {"loggers"})
    protected void getLoggers() {
        List<LoggerInfo> loggers = login().system().getLoggers();
        loggers.forEach((loggerInfo) -> {
            System.out.format("%-32s\t%-32s\n", loggerInfo.getCategory(), loggerInfo.getLevel());
        });
    }

    @CliCommand
    protected void setLogger(String loggerCategory, String loggerLevel) {
        login().system().setLogger(loggerCategory, loggerLevel);
        System.out.println("set logger " + loggerCategory + " to level " + loggerLevel);
    }

    @CliCommand
    protected void deleteLogger(String loggerCategory) {
        login().system().deleteLogger(loggerCategory);
        System.out.println("removed logger " + loggerCategory);
    }

    @CliCommand("tail")
    protected void streamLogMessages() throws InterruptedException, ExecutionException {
        login().system().streamLogMessages(x -> {
            System.out.println(x.getLine());
        }).get();
    }

    @CliCommand(alias = {"configs"})
    protected void getConfigs() {
        Map<String, String> config = login().system().getConfig();
        config.entrySet().forEach((entry) -> {
            System.out.format("%-70s\t%s\n", abbreviate(entry.getKey(), 70), abbreviate(entry.getValue(), 100));
        });
    }

    @CliCommand(alias = {"config"})
    protected void getConfig(String key) {
        String value = login().system().getConfig(key);
        System.out.println(value);
    }

    @CliCommand(alias = {"configinfo", "configdefinition", "configdesc", "configinfos", "configdefinitions", "configdescs", "getconfiginfos", "getconfigdefinitions", "getconfigdescs", "configsinfo", "configsdefinition", "configsdesc"})
    protected void getConfigDesc() {
        Map<String, ConfigDefinition> configDefinitions = login().system().getConfigDefinitions();
        int size = configDefinitions.keySet().stream().mapToInt(String::length).max().getAsInt();
        System.out.format("%-" + size + "s%-10s%-10s   %-30s   %s\n\n", "key", "category", "location", "default", "description");
        configDefinitions.values().forEach((def) -> {
            String key = def.getKey();
            if (def.isModule()) {
//                Matcher matcher = Pattern.compile("^(.+)[.]([^.]+)$").matcher(checkNotBlank(def.getModuleNamespace()));
//                checkArgument(matcher.matches());
//                key = format("%s.[%s].%s", checkNotBlank(matcher.group(1)), checkNotBlank(matcher.group(2)), key.substring(def.getModuleNamespace().length() + 1));
                key = format("%s.[%s].%s", def.getModulePrefix(), def.getModuleType(), def.getModuleSuffix());
            }
            System.out.format("%-" + size + "s%-10s%-10s   %-30s   %s\n", key, serializeEnum(def.getCategory()), serializeEnum(def.getLocation()), abbreviate(def.getDefaultValue(), 30), multilineWithOffset(def.getDescription(), 60, 126));
        });
    }

    @CliCommand(alias = {"configinfo", "configdefinition", "configdesc", "configinfos", "getconfiginfos", "configsinfo", "configsdesc"})
    protected void getConfigDesc(String key) {
        Map<String, ConfigDefinition> configDefinitions = login().system().getConfigDefinitions();
        Map<String, String> mapToPrint = map();
        configDefinitions.values().stream().filter(def -> def.getKey().equals(key)).forEach(def -> {
            String defKey = def.getKey();
            if (def.isModule()) {
                defKey = format("%s.[%s].%s", def.getModulePrefix(), def.getModuleType(), def.getModuleSuffix());
            }
            mapToPrint.put("key", defKey);
            mapToPrint.put("description", def.getDescription());
            mapToPrint.put("default", def.getDefaultValue());
            if (isNotEmpty(def.getEnumValues())) {
                mapToPrint.put("one of", def.getEnumValues());
            }
            mapToPrint.put("category", serializeEnum(def.getCategory()));
            mapToPrint.put("location", serializeEnum(def.getLocation()));
        });
        mapToPrint.forEach((k, v) -> System.out.format("\t\t%-12s: %s\n", k, v));
    }

    @CliCommand(alias = {"exportconfig", "exportconfigs"})
    protected void getConfigProperties() throws IOException {
        Map<String, String> config = login().system().getConfig();
        Properties properties = new Properties();
        properties.putAll(Maps.filterEntries(config, (entry) -> entry.getValue() != null));
        properties.store(System.out, null);//TODO sort by key
    }

    @CliCommand("exportconfigsql")
    protected void getConfigSql() throws IOException {
        Map<String, String> config = filterKeys(login().system().getConfig(), k -> !k.startsWith(DATABASE_CONFIG_NAMESPACE));
        config.forEach((k, v) -> System.out.printf("SELECT _cm3_system_config_set('%s','%s');\n", systemToSqlExpr(k), systemToSqlExpr(v)));
    }

    @CliCommand(alias = {"importconfig", "importconfigs"})
    protected void setConfigProperties(String propertyFileOrUrlParams) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(propertyFileOrUrlParams));
        System.out.printf("import config from file = %s :\n", propertyFileOrUrlParams);
        doImportProperties(properties);
    }

    @CliCommand
    protected void setConfigs(String configs) throws IOException {
        doImportProperties(toProperties(decodeUrlParams(configs)));
    }

    @CliCommand(alias = {"importconfig", "importconfigs"})
    protected void setConfigProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(System.in);
        System.out.printf("import config from stdin\n");
        doImportProperties(properties);
    }

    private void doImportProperties(Properties properties) {
        properties.forEach((key, value) -> {
            System.out.printf("cmdbuild r setconfig %s '%s'\n", key, value);
        });
        login().system().setConfigs(map(properties));
    }

    @CliCommand
    protected void setConfig(String keyValue) {
        Pair<String, String> config = parseConfigPropLine(keyValue);
        setConfig(config.getKey(), config.getValue());
    }

    @CliCommand
    protected void setConfig(String key, String value) {
        login().system().setConfig(key, value);
        System.out.printf("cmdbuild r setconfig %s '%s'\n", key, value);
    }

    @CliCommand
    protected void deleteConfig(String key) {
        login().system().deleteConfig(key);
        System.out.printf("delete config = %s\n", key);
    }

    @CliCommand(alias = {"reloadconfig"})
    protected void reloadConfig() {
        login().system().reloadConfig();
        System.out.println("OK");
    }

    @CliCommand
    protected void editConfig() throws IOException { //TODO merge code with getConfigProperties()
        SystemApi system = login().system();
        Map<String, String> config = system.getConfig();
        Map<String, ConfigDefinition> defs = system.getConfigDefinitions();
        StringBuilder editableConfig = new StringBuilder();
        String[] curPrefix = {""};
        defs.forEach((key, def) -> {
            String prefix = key.replaceFirst("org.cmdbuild.([^.]+).*", "$1");
            if (!Objects.equals(prefix, curPrefix[0])) {
                editableConfig.append(format("\n# === %s ===\n\n", prefix.toUpperCase()));
                curPrefix[0] = prefix;
            }
            if (def.hasDescription()) {
                editableConfig.append("#\n# ").append(def.getDescription()).append(":\n");
            }
            if (config.containsKey(key)) {
                editableConfig.append(format("%s=%s", key, config.get(key))).append("\n");
            } else {
                editableConfig.append(format("#%s=%s", key, nullToEmpty(def.getDefaultValue()))).append("\n");
            }
            if (def.hasDescription()) {
                editableConfig.append("#\n");
            }
        });

        editableConfig.append(format("\n# === %s ===\n\n", "OTHER"));
        config.keySet().stream().filter(not(defs::containsKey)).sorted().forEach((key) -> {
            editableConfig.append(format("%s=%s", key, config.get(key))).append("\n");
        });

        editableConfig.append("\n\n");
        String editedConfig = editFile(editableConfig.toString(), "system config");
        Properties properties = new Properties();
        properties.load(new StringReader(editedConfig));
        Map<String, String> toSet = map();
        properties.forEach((key, value) -> {
            if (!Objects.equals(value, config.get((String) key))) {
                System.out.printf("cmdbuild r setconfig %s '%s'\n", key, value);
                toSet.put((String) key, (String) value);
            }
        });
        system = login().system();
        if (!toSet.isEmpty()) {
            system.setConfigs(toSet);
        }
        config.keySet().stream().filter(not(properties::containsKey)).map((key) -> {
            System.out.printf("cmdbuild r deleteconfig %s\n", key);
            return key;
        }).forEach(system::deleteConfig);
    }

    @CliCommand
    protected void editConfig(String key) throws IOException {
        String value = login().system().getConfig(checkNotBlank(key));
        boolean isPacked = isPacked(value);
        if (isPacked) {
            value = unpack(value);
        }
        String oldValue = value;
        editFile(oldValue, "value of config " + key, (modified) -> {
            if (!Objects.equals(modified, oldValue)) {
                if (isPacked) {
                    modified = CmPackUtils.pack(modified);
                }
                System.out.printf("cmdbuild r setconfig %s '%s'\n", key, modified);
                login().system().setConfig(key, modified);
            }
        });
    }

    @CliCommand(description = "get plugin configs")
    protected void getPluginConfigs(@CliParameter("plugin") String plugin) {
        List<Map<String, String>> configs = login().system().getPluginConfigs(plugin);
        configs.forEach(config -> {
            System.out.format("%-70s (%s)\t%s\n", (String) config.get("key"), config.get("access"), abbreviate(config.get("value"), 100));
        });
    }

    @CliCommand(description = "set plugin config")
    protected void getPluginConfig(@CliParameter("plugin") String plugin, @CliParameter("key") String key) {
        Map<String, String> config = login().system().getPluginConfig(plugin, key);
        System.out.format("%-70s (%s)\t%s\n", (String) config.get("key"), config.get("access"), abbreviate(config.get("value"), 100));
    }

    @CliCommand(description = "set plugin config")
    protected void setPluginConfig(@CliParameter("plugin") String plugin, @CliParameter("key") String key, @CliParameter("value") String value, @CliParameter("access") String access) {
        login().system().setPluginConfig(plugin, key, value, access);
        System.out.printf("cmdbuild r setpluginconfig %s %s '%s' %s\n", plugin, key, value, access);
    }

    @CliCommand(description = "delete plugin config")
    protected void deletePluginConfig(@CliParameter("plugin") String plugin, @CliParameter("key") String key) {
        login().system().deletePluginConfig(plugin, key);
        System.out.printf("cmdbuild r deletepluginconfig %s %s\n", plugin, key);
    }

    @CliCommand
    protected void createEvents(String triggerIdOrCode) {
        createEvents(triggerIdOrCode, null);
    }

    @CliCommand
    protected void createEvents(String triggerIdOrCode, @Nullable String filter) {
        if (isPacked(filter)) {
            filter = unpack(filter);
        }
        System.out.printf("Generating events for trigger = %s with the filter = %s\n", triggerIdOrCode, filter);
        boolean generated = login().system().createEvents(triggerIdOrCode, filter);
        if (generated) {
            System.out.printf("Generated events for trigger = %s with the filter = %s\n", triggerIdOrCode, filter);
        } else {
            System.out.printf("There has been an issue when generating the events\n");
        }
    }

    @CliCommand
    protected void mark() {
        String mark = login().audit().mark();
        System.out.printf("audit mark: %s\n", mark);
    }

    @CliCommand(alias = {"lastrequests", "glr"})
    protected void getLastRequests() {
        getLastRequests(25);
    }

    @CliCommand(alias = {"lastrequests", "glr"})
    protected void getLastRequests(int limit) {
        List<RequestInfo> requests = login().audit().getLastRequests(limit);
        printRequests(requests);
    }

    @CliCommand(alias = {"getRequests", "requests", "gr"})
    protected void getRequestsSince(String mark) {
        checkNotBlank(mark, "missing mark");
        List<RequestInfo> requests = login().audit().getRequestsSince(mark);
        printRequests(requests);
    }

    @CliCommand(alias = {"lasterrors", "gle"})
    protected void getLastErrors() {
        getLastErrors(25);
    }

    @CliCommand(alias = {"lasterrors", "gle"})
    protected void getLastErrors(int limit) {
        List<RequestInfo> requests = login().audit().getLastErrors(limit);
        printRequests(requests);
    }

    private void printRequests(List<RequestInfo> requests) {
        System.out.printf("timestamp (%6s)   requestId                   actionId                    sessionId                   user        elap  method    response path                                               query    \n", getReadableTimezoneOffset());
        requests.stream().filter((request) -> !request.getActionId().matches("cli_restws_(get)?requests|cli_restws_mark")).forEach((request) -> {
            System.out.printf("%-18s %-24s %-32s %-24s %-16s  %6s %-6s %9s %-50s %s %s\n",
                    toUserReadableDateTime(request.getTimestamp()),
                    abbreviate(request.getRequestId(), 24),
                    abbreviate(request.getActionId(), 32),
                    nullToEmpty(request.getSessionId()),
                    nullToEmpty(request.getUser()),
                    request.isCompleted() ? (request.getElapsedTimeMillis() + "ms") : "",
                    request.getMethod(),
                    responseCode(request),
                    request.getPath(),
                    request.getQuery(),
                    request.isSoap() ? nullToEmpty(request.getSoapActionOrMethod()) : "");
        });
    }

    @CliCommand(alias = {"request", "gr"})
    protected void getRequest(String requestId) {
        getRequest(requestId, false);
    }

    @CliCommand(alias = {"requestExtended", "grx"})
    protected void getRequestExtended(String requestId) {
        getRequest(requestId, true);
    }

    protected void getRequest(String requestId, boolean extended) {
        RequestData request = login().audit().getRequestData(requestId.replaceAll("[.]", ""));
        System.out.printf("timestamp  : %s\n", toUserReadableDateTimeWithTimezone(request.getTimestamp()));
        System.out.printf("actionId   : %s\n", request.getActionId());
        System.out.printf("requestId  : %s\n", request.getRequestId());
        System.out.printf("sessionId  : %s (%s)\n\n", request.getSessionId(), request.getUser());

        System.out.printf("node       : %s\n", request.getNodeId());
        System.out.printf("client     : %s (%s)\n\n", request.getClient(), request.getUserAgent());

        System.out.printf("request    : %s %s\n", request.getMethod(), request.getPathWithQuery());
        System.out.printf("response   : %s (%s)\n\n", responseCode(request), request.isCompleted() ? format("elapsed %s ms", request.getElapsedTimeMillis()) : "ongoing");

        if (isNotBlank(request.getQuery())) {
            try {
                decodeUrlParams(request.getQuery()).forEach((k, v) -> System.out.printf("query param: %10s = %s\n", k, v));
            } catch (Exception ex) {
                System.out.printf("query params: %s\n", request.getQuery());
            }
            System.out.println();
        }

        System.out.printf("=== payload (%s %s) ===\n%s\n", nullToEmpty(request.getPayloadContentType()), byteCountToDisplaySize(request.getPayloadSizeOrZero()), request.getBestPlaintextPayload());
        System.out.printf("=== response (%s %s) ===\n%s\n===         end         ===\n", nullToEmpty(request.getResponseContentType()), byteCountToDisplaySize(request.getResponseSizeOrZero()), request.getBestPlaintextResponse());

        if (extended) {

            if (!request.getFaultEvents().isEmpty()) {
                System.out.printf("\n\n=== errors and messages ===\n\n");
                request.getFaultEvents().forEach((e) -> {
                    System.out.printf("level   :   %s\n", e.getLevel());
                    System.out.printf("message :   %s\n\n", e.getMessage());
                    System.out.printf("stacktrace :   %s\n", e.getStacktrace());
                });
                System.out.printf("===        end          ===\n");
            }

            if (request.hasLogs()) {
                System.out.printf("\n\n===      logs        ===\n%s\n===         end logs    ===\n", request.getLogs());
            }

            if (request.hasTcpDump()) {
                if (isPlaintext(request.getTcpDumpBytes())) {
                    System.out.printf("\n\n===      tcp dump        ===\n%s\n===         end tcp dump    ===\n", new String(request.getTcpDumpBytes(), StandardCharsets.UTF_8));
                } else {
                    File tempFile = new File(javaTmpDir(), format("%s.tcpdump", randomId(6)));
                    writeToFile(tempFile, request.getBinaryPayload());
                    System.out.printf("tcp dump: \n", tempFile.getAbsolutePath());
                }
            }
        }

        System.out.println();

        String payloadForCurl;
        if (request.isBinaryPayload() || request.getPayloadSize() > 250) {
            File tempFile = new File(javaTmpDir(), format("%s.file", randomId(6)));
            writeToFile(tempFile, request.getBinaryPayload());
            payloadForCurl = format("@%s", tempFile.getAbsolutePath());
            if (request.getPayloadSize() < 10000) {
                System.out.printf("echo '%s' | base64 -d > '%s'\n\n", Base64.encodeBase64String(request.getBinaryPayload()), tempFile.getAbsolutePath());
            }
        } else {
            payloadForCurl = request.getPayloadText();
        }

        System.out.println(buildCurlCli(request.getSessionId(), request.getPathWithQuery(), request.getMethod(), payloadForCurl, request.getPayloadContentType(), !request.isSoap(), false));
    }

    @CliCommand(alias = {"jobs"})
    protected void getJobs() {
        System.out.println(" enabled       id                     code                                            type           mode     cron expr");
        login().system().getJobs().forEach(j -> {
            System.out.printf("   %s        %8s    %-50s  %-20s %-10s %s\n", j.isEnabled() ? "X" : " ", j.getId(), abbreviate(j.getCode(), 50), j.getType(), serializeEnum(j.getMode()), nullToEmpty(j.getCronExpression()));
        });
    }

    @CliCommand(alias = {"sysjobs"})
    protected void getSystemJobs() {
        System.out.println("  running   cluster   trigger               last run                            key                                 ");
        login().system().getSysJobs().forEach(j -> {
            System.out.printf("    %s       %s      %-20s %-36s %s\n", j.isRunning() ? "X" : " ", j.hasClusterMode(RUN_ON_ALL_NODES) ? "A" : "N", j.getTrigger(), nullToEmpty(toIsoDateTimeLocal(j.getLastRun())), j.getCode());
        });
    }

    @CliCommand(alias = {"runsysjob"})
    protected void runSystemJob(String key) {
        System.out.printf("trigger sys job %s ... ", key);
        login().system().runSysJob(key);
        System.out.println("DONE");
    }

    @CliCommand
    protected void runJob(String jobId) {
        JobRun run = login().system().runJob(checkNotBlank(jobId));
        printDetailedRun(run);
    }

    @CliCommand(alias = {"lastjoberrors", "lje"})
    protected void getLastJobErrors() {
        getLastJobErrors(25);
    }

    @CliCommand(alias = {"lastjoberrors", "lje"})
    protected void getLastJobErrors(int limit
    ) {
        List<JobRun> runs = login().system().getLastJobErrors(limit);
        printRuns(runs);
    }

    @CliCommand(alias = {"lastjobruns", "ljr"})
    protected void getLastJobRuns() {
        getLastJobRuns(25);
    }

    @CliCommand(alias = {"lastjobruns", "ljr"})
    protected void getLastJobRuns(int limit) {
        List<JobRun> runs = login().system().getLastJobRuns(limit);
        printRuns(runs);
    }

    private void printRuns(List<JobRun> runs) {
        System.out.printf("timestamp (%6s)   runId   job                                                              status       elap\n\n", getReadableTimezoneOffset());
        runs.stream().forEach((run) -> {
            System.out.printf("%-18s %12s %-64s %-12s %ss\n",
                    toUserReadableDateTime(run.getTimestamp()),
                    run.getId(),
                    run.getJobCode(),
                    serializeJobRunStatus(run.getJobStatus()),
                    Optional.ofNullable(run.getElapsedTime()).map(t -> (Object) (t / 1000d)).orElse(""));
        });
    }

    @CliCommand(alias = {"jobrun", "jr"})
    protected void getJobRun(String id) {
        JobRun run = login().system().getJobRun(parseLong(id));
        printDetailedRun(run);
    }

    private void printDetailedRun(JobRun run) {
        System.out.printf("timestamp   : %s\n", toUserReadableDateTime(run.getTimestamp()));
        System.out.printf("runId  : %s\n", run.getId());
        System.out.printf("job : %s\n", run.getJobCode());
        System.out.printf("status  : %s\n", serializeJobRunStatus(run.getJobStatus()));

        if (!run.getErrorOrWarningEvents().isEmpty()) {
            System.out.printf("\n\n=== errors and messages ===\n\n");
            run.getErrorOrWarningEvents().forEach((e) -> {
                System.out.printf("level   :   %s\n", e.getLevel());
                System.out.printf("message :   %s\n\n", e.getMessage());
                System.out.printf("stacktrace :   %s\n", e.getStacktrace());
            });
            System.out.printf("===        end          ===\n");
        }

        if (run.hasLogs()) {
            System.out.printf("\n\n=== logs ===\n%s\n===        end          ===\n", run.getLogs());
        }
    }

    private String responseCode(RequestInfo request) {
        return format("%s%s", request.hasError() ? "ERROR " : "", request.getStatusCode()).trim();
    }

    @CliCommand("patches")
    protected void getPatches() {
        client.system().getPatches().forEach((patch) -> {
            System.out.printf("%-24s %-24s %s\n", patch.getCategory(), patch.getName(), nullToEmpty(patch.getDescription()));
        });
    }

    @CliCommand("patch")
    protected void applyPatches() {
        client.system().applyPatches();
        System.out.println("OK");
    }

    @CliCommand
    protected void interrupt(String requestId) {
        login().system().interrupt(requestId);
        System.out.println("OK");
    }

    @CliCommand
    protected void reload() {
        login().system().reload();
        System.out.println("OK");
    }

    @CliCommand
    protected void rollback(String timestamp) {
        ZonedDateTime dateTime = toDateTime(checkNotBlank(timestamp));
        System.out.printf("rollback data to timestamp = %s ... ", toIsoDateTimeLocal(dateTime));
        login().system().rollback(dateTime);
        System.out.println("OK");
    }

    @CliCommand
    protected void dropAllCaches() {
        login().system().dropAllCaches();
        System.out.println("OK");
    }

    @CliCommand
    protected void dropCache(String cacheId) {
        login().system().dropCache(cacheId);
        System.out.println("OK");
    }

    @CliCommand("caches")
    protected void cacheStats() {
        System.out.println("               cache                       size         memory usage");
        login().system().getCacheStats().forEach((k, c) -> System.out.printf("%-40s %10s %10s\n", c.getName(), c.getSize(), byteCountToDisplaySize(c.getEstimateMemSize())));
    }

    @CliCommand
    protected void upload(String targetPathAndFileName, String fileNameOrBase64Content) throws FileNotFoundException, IOException {
        byte[] data;
        File file = new File(fileNameOrBase64Content);
        if (!file.exists() && isPacked(fileNameOrBase64Content)) {
            data = unpackBytes(fileNameOrBase64Content);
        } else if (!file.exists() && isBase64(fileNameOrBase64Content)) {
            data = Base64.decodeBase64(fileNameOrBase64Content);
        } else {
            checkArgument(file.isFile(), "file %s is not a valid file", file);
            data = toByteArray(file);
        }
        login().withUploadProgressListener(buildProgressListener("upload")).uploads().upload(targetPathAndFileName, data);
        System.out.printf("completed upload of file = %s\n", targetPathAndFileName);
    }

    @CliCommand
    protected void upload(String zipFileNameOrDir) throws FileNotFoundException, IOException {
        File file = new File(zipFileNameOrDir);
        byte[] data;
        if (file.isDirectory()) {
            System.out.printf("upload files from dir = %s\n", file.getAbsolutePath());
            data = CmZipUtils.dirToZip(file);
        } else {
            checkArgument(zipFileNameOrDir.endsWith(".zip"), "file %s is not a valid zip file", file);
            System.out.printf("upload files from zip = %s\n", file.getAbsolutePath());
            data = toByteArray(file);
        }
        login().withUploadProgressListener(buildProgressListener("upload")).uploads().uploadMany(data);
    }

    @CliCommand
    protected void download(String path) throws IOException {
        byte[] data = login().uploads().download(path).toByteArray();
        IOUtils.write(data, System.out);
    }

    @CliCommand("exportUploads")
    protected void downloadAll() throws IOException {
        byte[] data = login().uploads().downloadAll().toByteArray();
        if (hasInteractiveConsole()) {
            File outputFile = new File(format("uploads_export_%s.zip", dateTimeFileSuffix()));
            writeToFile(outputFile, data);
            System.out.printf("output written to %s %s\n", outputFile.getAbsolutePath(), FileUtils.byteCountToDisplaySize(outputFile.length()));
        } else {
            System.out.write(data);
        }
    }

    @CliCommand("exportUploadsToDir")
    protected void downloadAllToDir(String dir) throws IOException {
        File target = new File(checkNotBlank(dir));
        if (!target.exists()) {
            target.mkdirs();
        }
        checkArgument(target.isDirectory());
        byte[] data = login().uploads().downloadAll().toByteArray();
        unzipToDir(data, target);
        System.out.printf("exported uploads to dir = %s\n", target.getAbsolutePath());
    }

    @CliCommand
    protected void printReport(String reportId, String ext, Map<String, String> params) throws IOException {
        byte[] data = login().report().executeAndDownload(reportId, ReportFormat.valueOf(ext.toUpperCase()), (Map) params).toByteArray();
        IOUtils.write(data, System.out);
    }

    @CliCommand
    protected void downloadReport(String reportId) throws IOException {
        printReport(reportId, "zip", emptyMap());
    }

    @CliCommand
    protected void createReport(String code, String reportTemplateDir) throws IOException {
        createReport(code, reportTemplateDir, "realtime");
    }

    @CliCommand
    protected void createOrUpdateReport(String code, String reportTemplateDir) throws IOException {
        createOrUpdateReport(code, reportTemplateDir, "realtime");
    }

    @CliCommand
    protected void createReport(String code, String reportTemplateDir, String processing) throws IOException {
        checkArgument(new File(reportTemplateDir).isDirectory(), "file %s is not a directory", reportTemplateDir);
        checkArgument(set(serializeEnum(ReportInfo.ReportProcessingMode.RPM_REALTIME), serializeEnum(ReportInfo.ReportProcessingMode.RPM_BATCH)).contains(processing), "processing %s is not a valid: use 'realtime' or 'batch'", reportTemplateDir);
        List<File> files = list(new File(reportTemplateDir).listFiles()).withOnly(File::isFile);
        System.out.printf("create report = %s files = %s\n", code, files.stream().map(File::getName).collect(joining(",")));
        ReportInfo info = ReportInfoImpl.builder()
                .withActive(true)
                .withCode(code)
                .withDescription(code)
                .withConfig(ReportInfo.REPORT_CONFIG_PROCESSING_MODE, processing)
                .build();
        ReportInfo reportInfo = login().report().createReport(info, files);
        System.out.printf("created report = %s %s\n", reportInfo.getId(), reportInfo.getCode());
    }

    @CliCommand
    protected void createOrUpdateReport(String code, String reportTemplateDir, String processing) throws IOException {
        if (!login().report().reportExists(code)) { // if not exists, create report
            createReport(code, reportTemplateDir, processing);
        } else {
            checkArgument(new File(reportTemplateDir).isDirectory(), "file %s is not a directory", reportTemplateDir);
            List<File> files = list(new File(reportTemplateDir).listFiles()).withOnly(File::isFile);
            System.out.printf("report = %s already exists, updating with files = %s\n", code, files.stream().map(File::getName).collect(joining(",")));
            login().report().uploadReportTemplate(code, files);
            System.out.printf("Updated report = %s \n", code);
        }
    }

    @CliCommand
    protected void reportExists(String reportCode) {
        if (login().report().reportExists(reportCode)) {
            System.out.printf("the report with code %s exists\n", reportCode);
        } else {
            System.out.printf("the report with code %s doesn't exist\n", reportCode);
        }
    }

    @CliCommand
    protected void uploadReport(String reportId, String reportTemplateDir) throws IOException {
        List<File> files = list(new File(reportTemplateDir).listFiles()).withOnly(File::isFile);
        System.out.printf("upload report template for report = %s files = %s\n", reportId, files.stream().map(File::getName).collect(joining(",")));
        login().report().uploadReportTemplate(reportId, files);
    }

    @CliCommand
    protected void uploadGeoserverLayer(String classId, Long cardId, String layerId, String pathToZip) throws IOException {
        File zipFile = new File(pathToZip);
        System.out.println("uploading geoserver layer");
        login().geoserverLayer().uploadGeoserverLayer(classId, cardId, layerId, new ByteArrayInputStream(toByteArray(zipFile)));
    }

    @CliCommand("upgrade")
    protected void upgradeWebapp(String fileName) throws FileNotFoundException, IOException {
        System.out.println("preparing upgrade, check war file");
        File file = new File(fileName);
        checkArgument(file.isFile(), "file %s is not a valid file", file);
        logger.debug("load war data from file = {}", file.getAbsolutePath());
        byte[] data = toByteArray(file);
        BuildInfo buildInfo = validateWarData(data);
        System.out.printf("upgrade cmdbuild webapp, load war file = %s rev %s\n", file.getAbsolutePath(), buildInfo.getCommitInfo());
        ZonedDateTime begin = now();
        login().withUploadProgressListener(buildProgressListener("upload")).system().upgradeWebapp(new ByteArrayInputStream(data));
        System.out.println("execute upgrade, wait for restart");
        Stopwatch stopwatch = Stopwatch.createStarted();
        while (true) {
            try {
                sleepSafe(1000);
                SystemApi system = login().system();
                if (Objects.equals(system.getStatus(), SYST_READY)//TODO handle patch required
                        && Objects.equals(buildInfo.getCommitInfo(), system.getSystemInfo().get("build_info"))
                        && now().minus(toDuration(system.getSystemInfo().get("uptime"))).isAfter(begin)) {
                    status();
                    return;
                } else {
                    logger.debug("system not ready");
                }
                checkArgument(stopwatch.elapsed(TimeUnit.SECONDS) < 60, "startup timeout: system failed to restart/upgrade in 60 seconds");
            } catch (Exception ex) {
                logger.debug("system not ready, error = {}", ex.toString());
            }
        }
    }

    @CliCommand("upgrade")
    protected void upgradeWebapp() throws FileNotFoundException, IOException {
        upgradeWebapp(getWarFile().getAbsolutePath());
    }

    @CliCommand(alias = {"recreate", "importDatabase", "importDb"})
    protected void recreateDatabase(String fileName) throws FileNotFoundException, IOException {
        doImportDb(fileName, false);
    }

    @CliCommand(alias = {"importDb_freezeSessions", "importDb_fs", "importDbfs"})
    protected void importDbFreezeSessions(String fileName) throws FileNotFoundException, IOException {
        doImportDb(fileName, true);
    }

    private void doImportDb(String fileName, boolean freezesessions) throws FileNotFoundException, IOException {
        File file = getDbdumpFile(fileName);
        System.out.printf("recreate cmdbuild database, import db from file = %s (freeze sessions = %s)\n", file.getAbsolutePath(), freezesessions);
        file = prepareDumpFile(file);
        login().withUploadProgressListener(buildProgressListener("upload")).system().recreateDatabase(new FileInputStream(file), freezesessions);
        System.out.println("done");
    }

//    @CliCommand()
//    protected void dbPoolDebug() { TODO
//        login().system().downloadDbPoolDebugInfo();
//    }
    @CliCommand()
    protected void dbPoolReload() {
        System.out.print("reload db pool... ");
        login().system().reloadDatabasePool();
        System.out.println("done");
    }

    @CliCommand("stop")
    protected void stopTomcat() {
        login().system().stop();
    }

    @CliCommand("restart")
    protected void restartTomcat() {
        login().system().restart();
    }

    @CliCommand("broadcast")
    protected void sendBroadcastMessage() throws IOException {
        System.out.print("message: ");
        String message = new BufferedReader(new InputStreamReader(System.in)).readLine();
        login().system().sendBroadcast(message);
        System.out.println("message sent");
    }

    @CliCommand("broadcast")
    protected void sendBroadcastMessage(String message) {
        System.out.printf("send broadcast message: %s\n", checkNotBlank(message));
        login().system().sendBroadcast(message);
        System.out.println("message sent");
    }

    @CliCommand("emailAccount")
    protected void getEmailAccount(String emailAccount) {
        EmailAccount account = login().email().getEmailAccount(emailAccount);
        System.out.printf("account: %s %s < %s >\n", account.getId(), account.getName(), account.getAddress());
        System.out.printf("username: %s\npassword: %s\n", account.getUsername(), account.getPassword());
        if (account.isSmtpConfigured()) {
            System.out.printf("smtp: %s:%s ssl=%s startTls=%s\n", account.getSmtpServer(), account.getSmtpPort(), account.getSmtpSsl(), account.getSmtpStartTls());
        }
        if (account.isImapConfigured()) {
            System.out.printf("imap: %s:%s ssl=%s startTls=%s (outbox =< %s >)\n", account.getImapServer(), account.getImapPort(), account.getImapSsl(), account.getImapStartTls(), account.getSentEmailFolder());
        }
    }

    @CliCommand()
    protected void grabEmail(String emailAccount, String messageId) throws MessagingException, IOException {
        grabEmail(emailAccount, "INBOX", messageId);
    }

    @CliCommand()
    protected void grabEmail(String emailAccount, String inbox, String messageId) throws MessagingException, IOException {
        checkNotBlank(inbox);
        checkNotBlank(messageId);
        EmailAccount account = login().email().getEmailAccount(emailAccount);
        try (EmailImapSessionProvider imapSessionProvider = emailProviderStrategy.buildImapSessionProvider(
                account, logger)) {
            Folder folder = imapSessionProvider.getStore().getFolder(inbox);
            folder.open(Folder.READ_ONLY);
            Message message = list(folder.getMessages()).stream().filter(m -> Objects.equals(parseEmailHeaderToken(getMessageHeader(m, EMAIL_HEADER_MESSAGE_ID)), parseEmailHeaderToken(messageId))).collect(onlyElement("message not found for message id =< %s >", messageId));
            Email email = parseEmail(message);
            File file = new File(javaTmpDir(), "email_" + randomId(6) + ".raw");
            try (PrintStream out = new PrintStream(new FileOutputStream(file))) {
                out.println(email.getHeaders());
                out.println();
                out.println();
                out.write(checkNotNull(email.getMultipartContent()));
            }
            System.out.printf("\n%s\n\n\n%s\n\nRAW CONTENT : %s\n", email.getHeaders(), new String(email.getMultipartContent()), file.getAbsolutePath());
        }
    }

//    @CliCommand()
//    protected void serveEmail(String emailAccount, String rawEmailFile) throws MessagingException, IOException, NoSuchProviderException {
//        EmailAccount account = login().withHeader(VIEW_MODE_HEADER_PARAM, VIEW_MODE_SYSTEM).email().getEmailAccount(emailAccount);
//        checkArgument(account.isImapConfigured());
//        byte[] emailData = toByteArray(new File(rawEmailFile));
//        Message message = new MimeMessage(Session.getDefaultInstance(new Properties()), new ByteArrayInputStream(emailData));
//        System.out.printf("start imap server on localhost:%s\n", account.getImapPort());
//        GreenMail greenMail = new GreenMail(new ServerSetup[]{new ServerSetup(account.getImapPort(), null, ServerSetup.PROTOCOL_IMAP)});
//        greenMail.setUser(account.getAddress(), account.getUsername(), account.getPassword());
//        greenMail.start();
//        System.out.printf("load email %s size = %s\n", getMessageInfoSafe(message), byteCountToDisplaySize(emailData.length));
//        try (IMAPStore store = greenMail.getImap().createStore()) {
//            store.connect(account.getUsername(), account.getPassword());
//            try (Folder folder = store.getFolder("INBOX")) {
//                folder.open(Folder.READ_WRITE);
//                folder.appendMessages(new Message[]{message});
//            }
//        }
//        System.out.printf("imap server and email ready\n");
//        System.console().readLine();
//    }
    @CliCommand()
    protected void loadEmail(long cardId, String content) {
        content = readToString(new File(content), StandardCharsets.ISO_8859_1);
        login().email().loadEmail(BASE_CLASS_NAME, cardId, content);
        System.out.println("OK");
    }

    @CliCommand()
    protected void acquireEmail(String fileName) throws IOException {
        login().email().acquireEmail(toByteArray(new File(fileName)));
    }

    @CliCommand("acquireEmailWithJob")
    protected void acquireEmail(String jobId, String fileName) throws IOException {
        acquireEmail(fileName);
        login().system().runJob(jobId, map(EMAIL_JOB_CONFIG_EMAIL_SOURCE_PARAM, serializeEnum(JCES_DB)));
    }

    @CliCommand
    protected void acquireEmailWithJob(String jobId) throws IOException {
        login().system().runJob(jobId, map(EMAIL_JOB_CONFIG_EMAIL_SOURCE_PARAM, serializeEnum(JCES_DB)));
    }

    @CliCommand
    protected void testEmailTemplate(long cardId, String templateId) throws IOException {
        Email email = login().email().testEmailTemplate(BASE_CLASS_NAME, cardId, templateId);
        String data = emailToMessageData(email);
        System.out.printf("=== SUBJECT ===\n\n%s\n\n=== BODY (%s) ===\n\n%s\n\n=== RAW DATA ===\n\n%s\n\n=== END ===\n", email.getSubject(), email.getContentType(), email.getContent(), data);
    }

    @CliCommand("console")
    protected void groovy() throws Exception {
        new ConsoleClientHelper().run(login().getSessionToken(), client.getBaseUrl());
    }

    @CliCommand("translations")
    protected void getTranslations() throws Exception {
        getTranslations("");
    }

    @CliCommand("translations")
    protected void getTranslations(@Nullable String query) throws Exception {
        List<Translation> translations = isBlank(query) ? login().translation().getAll() : login().translation().getMany(query);
        List<String> languages = translations.stream().map(Translation::getLang).distinct().sorted().collect(toImmutableList());
        Map<String, String> translationByCodeLang = map(translations, t -> key(t.getCode(), t.getLang()), Translation::getValue);
        try (CsvListWriter writer = new CsvListWriter(new OutputStreamWriter(System.out, UTF_8), CsvPreference.TAB_PREFERENCE)) {
            writer.write(list("key").with(languages));
            translations.stream().map(Translation::getCode).distinct().sorted().forEachOrdered(rethrowConsumer(k -> {
                writer.write(list(k).with(list(languages).map(l -> format("<%s>", nullToEmpty(translationByCodeLang.get(key(k, l)))))));
            }));
        }
    }

    @CliCommand("importTranslations")
    protected void setTranslations(String file) throws Exception {
        System.out.printf("import translations from file =< %s > ...", new File(file).getAbsolutePath());
        login().translation().importFromFile(toDataSource(new File(file)));
        System.out.println("OK");
    }

    @CliCommand(alias = {"clearsessions", "deleteallsessions"})
    protected void dropAllSessions() throws Exception {
        login().session().deleteAll();
    }

    @CliCommand(alias = {"systemplugins", "systemplugin", "plugins", "plugin"})
    protected void getSystemPlugins() {
        printSystemPlugins(login().system().getSystemPlugins());
    }

    public static void printSystemPlugins(List<SystemPlugin> plugins) {
        if (plugins.isEmpty()) {
            System.out.println("no plugin[s] found");
        } else {
            plugins.forEach(p -> System.out.printf("%s\n\tname: %s\n\tchecksum: %s\n\trequire core: %s\n\trequire libs: %s\n\tservice: %s\n\tstatus: %s\n\n",
                    p.getDescription(), p.getNameVersion(), p.getChecksum(), p.getRequiredCoreVersion(), Joiner.on(",").join(p.getRequiredLibFiles()), p.getService(), firstNotBlank(p.getHealthCheckMessage(), "OK")));
        }
    }

    @CliCommand(alias = {"deployplugin", "deployplugins", "deploysystemplugins"})
    protected void deploySystemPlugin(String file) {
        List<File> files = list(file.contains(",") ? Splitter.on(",").trimResults().omitEmptyStrings().splitToList(file) : singletonList(file)).flatMap(name -> new File(name).isDirectory() ? list(new File(name).listFiles()) : singletonList(new File(name)));
        System.out.printf("deploy system plugin files = %s ...", Joiner.on(",").join(files));
        login().system().deploySystemPlugins(files);
        System.out.println("OK");
    }

}
