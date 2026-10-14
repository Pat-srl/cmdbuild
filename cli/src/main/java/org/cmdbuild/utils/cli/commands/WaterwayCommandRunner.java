/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.DiffException;
import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.getOnlyElement;
import com.google.common.collect.Multimap;
import static com.google.common.collect.Multimaps.index;
import com.google.common.collect.Ordering;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import static java.lang.String.format;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.joining;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.apache.commons.io.FilenameUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.client.rest.api.EtlApi;
import org.cmdbuild.dao.config.inner.DatabaseCreator;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfig;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import org.cmdbuild.etl.config.WaterwayDescriptorInfoExt;
import org.cmdbuild.etl.config.WaterwayDescriptorMetaImpl;
import org.cmdbuild.etl.config.WaterwayItemInfo;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecord;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecordImpl;
import org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.buildDescriptorDataAndParams;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.buildDescriptorFilename;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.checkDescriptorData;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.fixDescriptorDataForEdit;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.fixDescriptorDataForEditAndAddParams;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.getDescriptorCodeFromFilename;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.getDescriptorTemplate;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.hasDescriptorDataAndParams;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.isDescriptorFilename;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.prepareRecord;
import org.cmdbuild.etl.loader.EtlProcessingResult;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.utils.cli.utils.CliCommand;
import static org.cmdbuild.utils.date.CmDateUtils.getReadableTimezoneOffset;
import static org.cmdbuild.utils.date.CmDateUtils.toUserReadableDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toUserReadableDateTimeWithTimezone;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackIfPacked;
import static org.cmdbuild.utils.gui.GuiFileEditor.editFile;
import static org.cmdbuild.utils.io.CmIoUtils.copy;
import static org.cmdbuild.utils.io.CmIoUtils.emptyDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.readLines;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import org.springframework.jdbc.core.JdbcTemplate;

public class WaterwayCommandRunner extends AbstractRestCommandRunner {

    private boolean includeparams, dbaccess, deleteMissing;
    private DatabaseCreatorConfig config;

    public WaterwayCommandRunner() {
        super(list("waterway", "wy", "w"), "waterway utils");
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        options.addOption("includeparams", false, "include bus descriptor params in checkout");
        options.addOption("db", false, "direct db access mode: read/write from db, not ws");
        options.addOption("configfile", true, "cmdbuild database config file (es: database.conf); default to conf/<webapp>/database.conf");
        options.addOption("deletemissing", false, "delete missing descriptors (on checkin)");
        return options;
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        includeparams = cmd.hasOption("includeparams");
        deleteMissing = cmd.hasOption("deletemissing");
        dbaccess = cmd.hasOption("db");
        if (dbaccess) {
            logger.info("using direct db access");
            config = getDbConfig(cmd);
        }
        super.exec(cmd);
    }

    private void checkNoDbAccess() {
        checkArgument(!dbaccess, "this command cannot be executed via db access");
    }

    @CliCommand("post")
    protected void postToGate(String gateId, String file) {
        checkNoDbAccess();
        System.out.printf("upload file = %s to gate =< %s >\n", file, gateId);
        doRunGate(gateId, newDataSource(new File(file)));
    }

    @CliCommand("post")
    protected void runGate(String gateId) {
        checkNoDbAccess();
        System.out.printf("execute gate =< %s >\n", gateId);
        doRunGate(gateId, emptyDataSource());
    }

    @CliCommand(alias = {"list", "descriptors"})
    protected void listDescriptors() {
        checkNoDbAccess();
        EtlApi etl = login().etl();
        List<WaterwayDescriptorRecord> modules = etl.getAllDescriptors();
        Multimap<String, WaterwayItemInfo> items = index(etl.getAllItems(), WaterwayItemInfo::getDescriptorKey);
        if (modules.isEmpty()) {
            System.out.println("no bus descriptor found");
        } else {
            modules.stream().sorted(Ordering.natural().onResultOf(WaterwayDescriptorInfoExt::getCode)).forEach(c -> {
                System.out.printf("%-20s\t%s\t%10s\t%s\n", c.getKey(), c.isEnabled() ? "ready   " : "disabled", c.getTag(), c.getDescription());
                items.get(c.getKey()).stream().sorted(Ordering.natural().onResultOf(WaterwayItemInfo::getCode))
                        .forEach(i -> System.out.printf("\t%-10s\t%-20s\t%s\n", serializeEnum(i.getType()), i.getCode(), i.getDescription()));
                System.out.println();
            });
        }
    }

    @CliCommand
    protected void enable(String code) {
        checkNoDbAccess();
        login().etl().enableDescriptorOrItem(code);
    }

    @CliCommand
    protected void disable(String code) {
        checkNoDbAccess();
        login().etl().disableDescriptorOrItem(code);
    }

    @CliCommand
    protected void edit(String code) {
        checkNoDbAccess();
        doEditConfigFile(code, prepareWyConfigForEdit(login().etl().getDescriptor(code)));
    }

    @CliCommand
    protected void delete(String codeOrFile) {
        File file = new File(isDescriptorFilename(codeOrFile) ? codeOrFile : buildDescriptorFilename(codeOrFile));
        String code = getDescriptorCodeFromFilename(file.getName());
        System.out.printf("delete bus descriptor: %s with file: %s\n", code, file.getAbsolutePath());
        deleteDescriptor(code);
        deleteQuietly(file);
    }

    @CliCommand
    protected void create(String codeOrFile) {
        File file = new File(isDescriptorFilename(codeOrFile) ? codeOrFile : buildDescriptorFilename(codeOrFile));
        checkArgument(!file.isFile(), "error: bus descriptor already exists, found file = %s", file.getAbsolutePath());
        String data = includeparams ? buildDescriptorDataAndParams(getDescriptorTemplate(codeOrFile), WaterwayDescriptorMetaImpl.empty()) : getDescriptorTemplate(codeOrFile);
        writeToFile(file, data);
        System.out.printf("create bus descriptor file from template: %s\n", file.getAbsolutePath());
        checkin(file.getAbsolutePath());
    }

    @CliCommand("co")
    protected void checkout(@Nullable String path, String code) {
        doCheckout(getFolder(path), getDescriptor(getDescriptorCodeFromFilename(code)));
    }

    @CliCommand("co")
    protected void checkout(@Nullable String path) {
        if (isNotBlank(path) && isDescriptorFilename(path)) {
            checkout(null, path);
        } else {
            File folder = getFolder(path);
            if (folder.listFiles((f) -> isDescriptorFilename(f.getName())).length > 0) {
                System.err.printf("folder %s is not empty, all existing files will be overwritten: continue?\n", folder.getAbsolutePath());
                System.console().readLine();
                System.out.printf("cleanup folder: %s\n", folder.getAbsolutePath());
                list(folder.listFiles((f) -> isDescriptorFilename(f.getName()))).forEach(File::delete);
            }
            System.out.printf("checkout bus descriptors to folder: %s\n", folder.getAbsolutePath());
            getAllDescriptors().forEach(record -> doCheckout(folder, record));
            System.out.println("OK");
        }
    }

    @CliCommand("ci")
    protected void checkin(@Nullable String path) {
        if (isNotBlank(path) && isDescriptorFilename(path)) {
            doCheckin(new File(path));
        } else {
            File folder = getFolder(path);
            System.out.printf("checkin bus descriptors from folder: %s\n", folder.getAbsolutePath());
            if (deleteMissing) {
                list(getAllDescriptors()).map(WaterwayDescriptorInfoExt::getFileName).filter(f -> !new File(folder, f).exists()).forEach(this::delete);
            }
            list(folder.listFiles(f -> isDescriptorFilename(f.getName()))).forEach(this::doCheckin);
        }
    }

    @CliCommand
    protected void diff(@Nullable String folderName) throws DiffException {
        if (isDescriptorFilename(folderName)) {
            diff(FilenameUtils.getFullPath(folderName), FilenameUtils.getName(folderName));
        } else {
            File folder = getFolder(folderName);
            System.out.printf("check bus descriptors from folder: %s\n", folder.getAbsolutePath());
            Map<String, String> local = list(folder.listFiles(f -> isDescriptorFilename(f.getName()))).collect(toMap(File::getName, f -> readToString(f))),
                    remote = list(getAllDescriptors()).collect(toMap(WaterwayDescriptorInfoExt::getFileName, r -> hasDescriptorDataAndParams(local.get(r.getFileName())) ? fixDescriptorDataForEditAndAddParams(r) : fixDescriptorDataForEdit(r)));
            Set<String> remoteOnly = set(remote.keySet()).without(local.keySet()),
                    localOnly = set(local.keySet()).without(remote.keySet()),
                    changed = set(remote.keySet()).withOnly(local.keySet()).withOnly(k -> !equal(remote.get(k), local.get(k)));
            if (!remoteOnly.isEmpty()) {
                System.out.printf("only on cmdbuild:\n%s\n", list(remoteOnly).sorted().map(c -> format("\t%s", c)).collect(joining("\n")));
            }
            if (!localOnly.isEmpty()) {
                System.out.printf("only local:\n%s\n", list(localOnly).sorted().map(c -> format("\t%s", c)).collect(joining("\n")));
            }
            if (!changed.isEmpty()) {
                System.out.printf("modified files:\n%s\n", list(changed).sorted().map(c -> format("\t%s", c)).collect(joining("\n")));
            }
            System.out.printf("%s not modified, %s to commit\n", remote.size() - changed.size(), localOnly.size() + changed.size());
        }
    }

    @CliCommand
    protected void diff(@Nullable String folderName, String fileNameOrCode) throws DiffException {
        if (!isDescriptorFilename(fileNameOrCode)) {
            fileNameOrCode = buildDescriptorFilename(fileNameOrCode);
        }
        File folder = getFolder(folderName), file = new File(folder, fileNameOrCode);
        System.out.printf("check bus descriptor file: %s\n", file.getAbsolutePath());
        String local = readToString(file);
        WaterwayDescriptorRecord record = getDescriptorOrNull(getDescriptorCodeFromFilename(file.getName()));
        if (record == null) {
            System.out.println("bus descriptor not found on remote, only local");
        } else {
            System.out.printf("remote: %s\n\n", record.getKey());//TODO add last mod timestamp
            String remote = hasDescriptorDataAndParams(local) ? fixDescriptorDataForEditAndAddParams(record) : fixDescriptorDataForEdit(record);
            List<String> diff = UnifiedDiffUtils.generateUnifiedDiff("(remote)", "(local)", readLines(remote), DiffUtils.diff(readLines(remote), readLines(local)), 4);
            if (diff.isEmpty()) {
                System.out.println("no changes detected");
            } else {
                System.out.println(Joiner.on("\n").join(diff));
            }
        }
    }

    @CliCommand("wa")
    protected void watch(@Nullable String folderName) throws Exception {
        checkNoDbAccess();
        File folder = getFolder(folderName);
        login();
        System.out.printf("watch bus descriptors from folder: %s\n", folder);
        WatchService watcher = FileSystems.getDefault().newWatchService();
        folder.toPath().register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
        while (true) {
            WatchKey watchKey = watcher.take();
            watchKey.pollEvents().forEach(e -> {
                logger.debug("processing event = {}", e);
                if (e.kind() != OVERFLOW) {
                    File file = folder.toPath().resolve(((Path) e.context())).toFile();
                    logger.debug("event file =< {} >", file.getAbsolutePath());
                    if (file.isFile() && isDescriptorFilename(file.getName())) {
                        logger.debug("processing file: {}", file.getAbsolutePath());
                        try {
                            client.login().checkLogin(() -> client.doLoginWithAnyGroup(username, regenPassword()));
                            doCheckin(file);
                        } catch (Exception ex) {
                            logger.warn("error processing file =< {} >", file.getAbsolutePath(), ex);
                        }
                    }
                }
            });
            watchKey.reset();
        }
    }

    @CliCommand(alias = {"messages", "lastmessages", "glm"})
    protected void getLastMessages() {
        checkNoDbAccess();
        getLastMessages(20);
    }

    @CliCommand(alias = {"messages", "lastmessages", "glm"})
    protected void getLastMessages(long count) {
        checkNoDbAccess();
        System.out.printf("timestamp (%6s)    messageId                           queue             node             status\n", getReadableTimezoneOffset());
        login().etl().getLastMessages(count).forEach(m -> System.out.printf("%-18s    %-24s    %24s    %-16s    %10s\n", toUserReadableDateTime(m.getTimestamp()), m.getMessageId(), m.getQueueCode(), m.getNodeId(), serializeEnum(m.getStatus())));
    }

    @CliCommand(alias = {"message", "gm"})
    protected void getMessage(String messageReference) {
        checkNoDbAccess();
        WaterwayMessage message = login().etl().getMessage(messageReference);
        System.out.printf("""
        timestamp: %s
        messageId: %s
        status:    %s
        queue:     %s
        storage:   %s
        node:      %s

        meta:
        %s
        """, toUserReadableDateTimeWithTimezone(message.getTimestamp()), message.getMessageId(), serializeEnum(message.getStatus()), message.getQueue(), message.getStorage(), message.getNodeId(), mapToLoggableString(message.getMeta()));
        message.getAttachments().forEach(a -> System.out.printf("""

        attachment: %s
        type:       %s
        storage:    %s
        content:    %s (%s)
        value:      %s

        meta:
        %s
        """, a.getName(), serializeEnum(a.getType()), firstNotBlank(a.getMeta("_restclient_storage"), serializeEnum(a.getStorage())), a.getByteSize() == null ? "n/a" : byteCountToDisplaySize(a.getByteSize()), firstNotBlank(a.getContentType(), "application/octet-stream"), a.getText(), mapToLoggableString(map(a.getMeta()).withoutKey("_restclient_storage"))));
        System.out.printf("\ntimestamp (%6s)    messageKey                   queue              storage           node             status\n", getReadableTimezoneOffset());
        message.getHistoryRecords().forEach(h -> System.out.printf("%-18s    %-27s    %-16s    %-16s    %-16s    %10s\n", toUserReadableDateTime(h.getTimestamp()), h.getMessageKey(), h.getQueueCode(), h.getStorageCode(), h.getNodeId(), serializeEnum(h.getStatus())));
    }

    @CliCommand(alias = {"messageattachment", "gma"})
    protected void getMessageAttachment(String messageReference, String attachmentName) throws IOException {
        checkNoDbAccess();
        DataSource dataSource = login().etl().getMessageAttachment(messageReference, attachmentName);
        copy(dataSource.getInputStream(), System.out);
    }

    @CliCommand(alias = {"messageattachment", "gma"})
    protected void getMessageAttachment(String messageReference) throws IOException {
        checkNoDbAccess();
        DataSource dataSource = login().etl().getMessageAttachment(messageReference, getOnlyElement(login().etl().getMessage(messageReference).getAttachmentMap().keySet()));
        copy(dataSource.getInputStream(), System.out);
    }

    @CliCommand(alias = {"schema", "gs"})
    protected void getSchema(String folderName, String fileName) throws IOException {
        folderName = FilenameUtils.normalizeNoEndSeparator(new File(folderName).getAbsolutePath());
        System.out.printf("\tdownload waterway schema %s/%s\n", folderName, fileName);
        writeToFile(WaterwayDescriptorUtils.class.getResourceAsStream("/org/cmdbuild/etl/config/schema.json").readAllBytes(), new File(folderName, fileName));
    }

    private void doCheckout(File folder, WaterwayDescriptorRecord record) {
        System.out.printf("\tcheckout bus descriptor: %s v%s\n", record.getFileName(), record.getVersion());
        writeToFile(prepareWyConfigForEdit(record), new File(folder, record.getFileName()));
    }

    private String prepareWyConfigForEdit(WaterwayDescriptorRecord record) {
        return includeparams ? fixDescriptorDataForEditAndAddParams(record) : fixDescriptorDataForEdit(record);
    }

    private void doCheckin(File file) {
        DataSource data = toDataSource(file);
        String code = getDescriptorCodeFromFilename(data);
        if (hasChanges(code, readToString(data))) {
            System.out.printf("update bus descriptor < %s > from file: %s\n", code, file.getAbsolutePath());
            createUpdateDescriptor(data);
        }
    }

    private boolean hasChanges(String code, String data) {
        WaterwayDescriptorRecord current = dbaccess ? getDescriptorOrNull(code) : login().etl().getDescriptorOrNull(code);
        if (current == null) {
            return true;
        } else {
            return hasDescriptorDataAndParams(data) ? !equal(fixDescriptorDataForEditAndAddParams(current), data) : !equal(fixDescriptorDataForEdit(current.getData()), data);
        }
    }

    private File getFolder(@Nullable String folderName) {
        File folder = new File(firstNotBlank(folderName, ".")).getAbsoluteFile();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        checkArgument(folder.isDirectory(), "invalid folder =< %s >", folder);
        return folder;
    }

    private void doEditConfigFile(String code, String config) {
        editFile(checkDescriptorData(unpackIfPacked(nullToEmpty(config))), format("config %s", code), (content) -> {
            client.login().checkLogin(() -> client.doLoginWithAnyGroup(username, regenPassword())).etl().createUpdateDescriptor(newDataSource(content, null, buildDescriptorFilename(code)));
            System.out.printf("update content for bus descriptor file = %s\n", code);
        });
    }

    private void doRunGate(String gateId, DataSource payload) {//TODO duplicate code
        EtlProcessingResult result = login().etl().postToGate(gateId, payload);
        System.out.println("\nOK");
        if (result != null) {
            System.out.println();
            System.out.println(result.getResultDescriptionMultiline());
            System.out.println(result.getErrorsDescriptionMultiline());
        }
    }

    private WaterwayDescriptorRecord getDescriptor(String code) {
        return checkNotNull(getDescriptorOrNull(code), "bus descriptor not found for code =< %s >", code);
    }

    @Nullable
    private WaterwayDescriptorRecord getDescriptorOrNull(String code) {
        return dbaccess
                ? getOnlyElement(getJdbcTemplate().query("SELECT * FROM \"_EtlConfig\" WHERE \"Code\" = ? AND \"Status\" = 'A'", (r, i) -> parseRecord(r), checkNotBlank(code)), null)
                : login().etl().getDescriptorOrNull(code);
    }

    private List<WaterwayDescriptorRecord> getAllDescriptors() {
        return dbaccess
                ? getJdbcTemplate().query("SELECT * FROM \"_EtlConfig\" WHERE \"Status\" = 'A'", (r, i) -> parseRecord(r))
                : login().etl().getAllDescriptors();
    }

    private WaterwayDescriptorRecord parseRecord(ResultSet r) throws SQLException {
        return WaterwayDescriptorRecordImpl.builder()
                .withId(r.getLong(ATTR_ID))
                .withCode(r.getString(ATTR_CODE))
                .withData(r.getString("Data"))
                .withDisabledItems(convert(r.getObject("Disabled"), List.class))
                .withEnabled(r.getBoolean("Enabled"))
                .withVersion(r.getInt("Version"))
                .withParams(fromJson(r.getString("Params"), MAP_OF_STRINGS))
                .build();
    }

    private void deleteDescriptor(String code) {
        if (dbaccess) {
            getJdbcTemplate().execute("UPDATE \"_EtlConfig\" SET \"Status\" = 'N' WHERE \"Id\" = %s".formatted(getDescriptor(code).getId()));//this will check that module exists before delete
        } else {
            login().etl().deleteDescriptor(code);
        }
    }

    private void createUpdateDescriptor(DataSource data) {
        if (dbaccess) {
            WaterwayDescriptorRecord record = prepareRecord(data, null, this::getDescriptorOrNull, this::getAllDescriptors);
            getJdbcTemplate().execute("""
                                  INSERT INTO "_EtlConfig" ("Code","Disabled","Enabled","Description","Notes","Version","Data","Config","Params") VALUES (%s,%s::varchar[],%s,%s,%s,%s,%s,%s,%s)
                                  ON CONFLICT ("Code") WHERE "Status" = 'A' DO UPDATE SET
                                    "Code" = EXCLUDED."Code",
                                    "Disabled" = EXCLUDED."Disabled",
                                    "Enabled" = EXCLUDED."Enabled",
                                    "Description" = EXCLUDED."Description",
                                    "Notes" = EXCLUDED."Notes",
                                    "Version" = EXCLUDED."Version",
                                    "Data" = EXCLUDED."Data",
                                    "Config" = EXCLUDED."Config",
                                    "Params" = EXCLUDED."Params"
                                  """.formatted(systemToSqlExpr(record.getCode()),
                    systemToSqlExpr(record.getDisabledItems()),
                    systemToSqlExpr(record.isEnabled()),
                    systemToSqlExpr(record.getDescription()),
                    systemToSqlExpr(record.getNotes()),
                    systemToSqlExpr(record.getVersion()),
                    systemToSqlExpr(record.getData()),
                    systemToSqlExpr(((WaterwayDescriptorRecordImpl) record).getConfig()),
                    systemToSqlExpr(toJson(record.getParams()))));
        } else {
            login().etl().createUpdateDescriptor(data);
        }
    }

    private JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(new DatabaseCreator(config).getCmdbuildDataSource());
    }
}
