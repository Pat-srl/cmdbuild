/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.String.format;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import static java.util.Collections.singletonList;
import static java.util.Collections.synchronizedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.SimpleJasperReportsContext;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.auth.login.PasswordAlgo;
import static org.cmdbuild.auth.login.PasswordAlgo.PA_CM3;
import static org.cmdbuild.auth.utils.CmPasswordUtils.decryptPasswordIfPossible;
import static org.cmdbuild.auth.utils.CmPasswordUtils.detectPasswordAlgo;
import static org.cmdbuild.auth.utils.CmPasswordUtils.encryptPassword;
import org.cmdbuild.dao.config.inner.DatabaseCreator;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfig;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSql;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlTypeName._bytea;
import static org.cmdbuild.dao.postgres.utils.SqlTypeName._varchar;
import static org.cmdbuild.dao.postgres.utils.SqlTypeName.bytea;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.utils.EmailMtaUtils;
import org.cmdbuild.report.dao.ReportDataImpl;
import org.cmdbuild.report.inner.ReportDataExt;
import static org.cmdbuild.report.inner.utils.ReportUtils.jasperReportToJasperDesign;
import static org.cmdbuild.report.inner.utils.ReportUtils.loadReport;
import static org.cmdbuild.report.inner.utils.ReportUtils.toByteArray;
import static org.cmdbuild.report.inner.utils.ReportUtils.toJasperDesign;
import static org.cmdbuild.report.inner.utils.ReportUtils.updateReportData;
import org.cmdbuild.utils.cli.utils.CliAction;
import org.cmdbuild.utils.cli.utils.CliCommand;
import org.cmdbuild.utils.cli.utils.CliCommandParser;
import static org.cmdbuild.utils.cli.utils.CliCommandParser.printActionHelp;
import static org.cmdbuild.utils.cli.utils.CliCommandUtils.prepareAction;
import org.cmdbuild.utils.cli.utils.CliParameter;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.deserializeObject;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmMultipartUtils.isMultipart;
import static org.cmdbuild.utils.io.CmStreamProgressUtils.buildProgressListener;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmExecutorUtils.safe;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import org.cmdbuild.utils.postgres.PostgresUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.supercsv.io.CsvListWriter;
import static org.supercsv.prefs.CsvPreference.STANDARD_PREFERENCE;

public class DbUtilsCommandRunner extends AbstractCommandRunner {

    private final Map<String, CliAction> actions;
    private DatabaseCreatorConfig config;

    public DbUtilsCommandRunner() {
        super(list("dbutils", "dbu", "du", "dbtools", "dt"), "access cmdbuild database");
        actions = new CliCommandParser().parseActions(this);
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        options.addOption("configfile", true, "cmdbuild database config file (es: database.conf); default to conf/<webapp>/database.conf");
        return options;
    }

    @Override
    protected void printAdditionalHelp() {
        System.out.println("\navailable rest methods:");
        printActionHelp(actions);
        System.err.println("\nconfig file example:\n");
        System.err.println(readToString(getClass().getResourceAsStream("/database.conf_cli_example")));
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        config = getDbConfig(cmd);
        prepareAction(actions, cmd.getArgList().iterator()).execute();
    }

    @CliCommand
    protected void getUserPassword(String username) {
        String rawPassword = PostgresUtils.newHelper(config.getHost(), config.getPort(), config.getCmdbuildUser(), config.getCmdbuildPassword()).withDatabase(config.getDatabaseName()).buildHelper()
                .executeQuery("SELECT \"Password\" FROM \"User\" WHERE \"Username\" = '%s' AND \"Status\" = 'A'", username);
        String clearPassword = decryptPasswordIfPossible(rawPassword);
        System.out.printf("username             : %s\npassword (encrypted) : %s\npassword (clear)     : %s\n", username, firstNotBlank(rawPassword, "<password not found>"), firstNotBlank(clearPassword, "<unable to decrypt>"));
    }

    @CliCommand
    protected void setUserPassword(String username, String password) {
        PostgresUtils.newHelper(config.getHost(), config.getPort(), config.getCmdbuildUser(), config.getCmdbuildPassword()).withDatabase(config.getDatabaseName()).buildHelper()
                .executeUpdate("UPDATE \"User\" SET \"Password\" = '%s', \"Active\" = true WHERE \"Username\" = '%s' AND \"Status\" = 'A'", encryptPassword(checkNotBlank(password), PA_CM3), checkNotBlank(username));
        System.out.println("password set");
    }

    @CliCommand
    protected void upgradePasswordStorage(String algo) {
        upgradePasswordStorage(parseEnum(algo, PasswordAlgo.class), false);
    }

    @CliCommand
    protected void upgradePasswordStorageSkipService(String algo) {
        upgradePasswordStorage(parseEnum(algo, PasswordAlgo.class), true);
    }

    @CliCommand
    protected void getSystemConfig() {
        System.out.println(mapToLoggableString(new DatabaseCreator(config).getSystemConfigsFromDb()));
    }

    @CliCommand
    protected void compactEmail() throws SQLException, InterruptedException {
        JdbcTemplate jdbc = getJdbcTemplate();
        jdbc.execute("ALTER TABLE \"Email\" DISABLE TRIGGER USER; ALTER TABLE \"Email_history\" DISABLE TRIGGER USER;");
        try {
            System.err.println("analyzing tables...");
            jdbc.execute("VACUUM FULL ANALYZE \"Email\"; VACUUM FULL ANALYZE \"Email_history\";");
            long count = jdbc.queryForObject("SELECT COUNT(*) FROM \"Email\"", Long.class),
                    tableSizeBefore = jdbc.queryForObject("select sum(total_size) from _cm3_utils_table_size('\"Email\"')", Long.class);
            AtomicLong processed = new AtomicLong(0), modified = new AtomicLong(0);
            List<Pair<Long, Exception>> errors = synchronizedList(new ArrayList<>());
            System.err.printf("processing %s email records, total size = %s\n", count, byteCountToDisplaySize(tableSizeBefore));
            Consumer<Long> progressListener = buildProgressListener(count, (e) -> System.err.printf("email compacting progress: %s modified, processed: %s\n", modified.get(), e.getProgressDescriptionDetailed()));
            BlockingQueue<Map<String, Object>> queue = new ArrayBlockingQueue<>(100);
            int threadCount = Runtime.getRuntime().availableProcessors();
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            for (int i = 0; i < threadCount; i++) {
                executorService.submit(safe(() -> {
                    while (processed.get() < count) {
                        try {
                            Map<String, Object> record = queue.take();
                            long id = (long) record.get(ATTR_ID);
                            try {
                                byte[] multipartBytes = CmIoUtils.emptyToNull((byte[]) record.get("Multipart"));
                                String content = Strings.nullToEmpty((String) record.get("Content")),
                                        contentType = Strings.emptyToNull((String) record.get("ContentType")),
                                        multipartType = Strings.emptyToNull((String) record.get("MultipartType"));
                                Multipart multipart;
                                if (multipartBytes != null && isMultipart(multipartType)) {
                                    multipart = new MimeMultipart(newDataSource(multipartBytes, multipartType));
                                } else if (isNotBlank(content) && isMultipart(contentType)) {
                                    multipart = new MimeMultipart(newDataSource(content, contentType));
                                } else {
                                    multipart = null;
                                }
                                if (multipart != null) {
                                    Email processedRecord = EmailImpl.builder().accept(EmailMtaUtils.loadEmailContent(multipart)).build();
                                    if (!equal(processedRecord.getContent(), content)
                                            || !equal(processedRecord.getContentType(), contentType)
                                            || !equal(CmIoUtils.emptyToNull(processedRecord.getMultipartContent()), multipartBytes)
                                            || !equal(processedRecord.getMultipartContentType(), multipartType)) {
                                        logger.debug("info record = {}", id);
                                        jdbc.update("UPDATE \"Email\" SET \"Multipart\" = ?, \"MultipartType\" = ?, \"Content\" = ?, \"ContentType\" = ? WHERE \"Id\" = ?",
                                                processedRecord.getMultipartContent(), processedRecord.getMultipartContentType(), processedRecord.getContent(), processedRecord.getContentType(), id);
                                        modified.incrementAndGet();
                                    }
                                }
                            } catch (Exception ex) {
                                logger.error("error processing email = {}", id, ex);
                                errors.add(Pair.of(id, ex));
                            } finally {
                                progressListener.accept(processed.incrementAndGet());
                            }
                        } catch (InterruptedException ex) {
                            return;
                        }
                    }
                }));
            }
            try (Connection connection = jdbc.getDataSource().getConnection()) {
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM \"Email\"");
                while (resultSet.next()) {
                    queue.put(map(
                            ATTR_ID, resultSet.getLong("Id"),
                            "Content", resultSet.getString("Content"),
                            "ContentType", resultSet.getString("ContentType"),
                            "Multipart", resultSet.getBytes("Multipart"),
                            "MultipartType", resultSet.getString("MultipartType")
                    ));
                }
            }
            shutdownQuietly(executorService);
            System.err.printf("processed %s email records, modified %s\nexecute vacuum analyze and check reclaimed space...\n", count, modified);
            jdbc.execute("VACUUM FULL ANALYZE \"Email\"; VACUUM FULL ANALYZE \"Email_history\";");
            long tableSizeAfter = jdbc.queryForObject("select sum(total_size) from _cm3_utils_table_size('\"Email\"')", Long.class);
            System.err.printf("cleanup completed, final table size = %s (reduced by %s%%)\n", byteCountToDisplaySize(tableSizeAfter), (tableSizeBefore - tableSizeAfter) * 100 / tableSizeBefore);
        } finally {
            jdbc.execute("ALTER TABLE \"Email\" ENABLE TRIGGER USER; ALTER TABLE \"Email_history\" ENABLE TRIGGER USER;");
        }
    }

    @CliCommand
    protected void compileReportXml(File configFile) throws Exception, JRException {
        JdbcTemplate jdbc = getJdbcTemplate();
        JasperReportsContext context = new SimpleJasperReportsContext();
        List<Pair<Long, List<String>>> queryForList = jdbc.query("SELECT \"Id\",\"Report\" FROM \"_Report\" WHERE \"Status\"='A'", (r, i) -> {
            List<String> reportsXml = list();
            ResultSet resultSet = r.getArray("Report").getResultSet();
            while (resultSet.next()) {
                reportsXml.add(resultSet.getString("VALUE"));
            }
            long id = r.getLong("Id");
            return Pair.of(id, reportsXml);
        });
        queryForList.forEach(rethrowConsumer(p -> {
            ImmutableList<String> reportList = p.getRight().stream().collect(toImmutableList());
            if (reportList.size() == 1) {
                byte[] reportData = toByteArray(toJasperDesign(reportList.get(0).getBytes(), context), context);
                String updateQuery = format("UPDATE \"_Report\" SET \"MainReport\" = %s WHERE \"Id\" = %s AND \"Status\"='A'", systemToSqlExpr(reportData, bytea), p.getLeft());
                jdbc.update(updateQuery);
            } else if (reportList.size() > 1) {
                byte[] mainReportData = toByteArray(toJasperDesign(reportList.get(0).getBytes(), context), context);
                List<byte[]> subReportsData = list();
                reportList.stream().skip(1).forEach(rethrowConsumer(s -> {
                    subReportsData.add(toByteArray(toJasperDesign(s.getBytes(), context), context));
                }));
                String updateQuery = format("UPDATE \"_Report\" SET \"MainReport\" = %s, \"SubReports\" = %s WHERE \"Id\" = %s AND \"Status\"='A'",
                        systemToSqlExpr(mainReportData, bytea), systemToSqlExpr(subReportsData, _bytea), p.getLeft());
                jdbc.update(updateQuery);
            }
        }));
    }

    @CliCommand
    protected void exportReportXml() throws Exception, JRException {
        JdbcTemplate jdbc = getJdbcTemplate();
        System.err.println("Getting reports from database");
        JasperReportsContext context = new SimpleJasperReportsContext();
        List<Pair<Long, List<byte[]>>> queryForList = jdbc.query("SELECT \"Id\",\"MainReport\",\"SubReports\" FROM \"_Report\" WHERE \"Status\"='A'", (r, i) -> {
            byte[] mainReport = r.getBytes("MainReport");
            List<byte[]> subreports = list();
            ResultSet resultSet = r.getArray("SubReports").getResultSet();
            while (resultSet.next()) {
                subreports.add(resultSet.getBytes("VALUE"));
            }
            long id = r.getLong("Id");
            return Pair.of(id, listOf(byte[].class).with(mainReport).with(subreports));
        });

        queryForList.forEach(rethrowConsumer(p -> {
            List<String> jrxmlReports = list();
            ImmutableList<JasperReport> reportList = p.getRight().stream()
                    .map((b) -> (JasperReport) deserializeObject(b))
                    .collect(toImmutableList());
            reportList.stream().forEach(rethrowConsumer(rep -> {
                JasperDesign jasperDesign = jasperReportToJasperDesign(rep, context);
                JasperReport compiled = JasperCompileManager.getInstance(context).compile(jasperDesign);
                String reportContent = JRXmlWriter.writeReport(compiled, "UTF-8");
                jrxmlReports.add(reportContent);
                String updateQuery = format("UPDATE \"_Report\" SET \"Report\" = $$%s$$ WHERE \"Id\" = %s AND \"Status\"='A'", systemToSql(_varchar, jrxmlReports), p.getLeft());
                jdbc.update(updateQuery);
            }));
        }));
    }

    @CliCommand(alias = {"createreport", "updatereport", "loadreport"})
    protected void createOrUpdateReport(String code, String reportTemplateDirOrFile) throws IOException {
        checkNotBlank(code);
        File file = new File(reportTemplateDirOrFile);
        List<File> files = file.isDirectory() ? list(new File(reportTemplateDirOrFile).listFiles()).withOnly(File::isFile) : singletonList(file);
        ReportDataExt report = loadReport(ReportDataImpl.builder().withCode(code).accept(updateReportData(map(files, File::getName, CmIoUtils::toByteArray))).build());
        boolean reportExists = getJdbcTemplate().queryForObject("SELECT EXISTS (SELECT * FROM \"_Report\" WHERE \"Code\" = ? AND \"Status\" = 'A')", Boolean.class, report.getCode());
        if (!reportExists) {
            System.out.printf("create report with code =< %s > from files = %s\n", report.getCode(), Joiner.on(", ").join(files));
            getJdbcTemplate().update(format("INSERT INTO \"_Report\" (\"Sources\",\"Images\",\"ImageNames\",\"Code\") VALUES (%s,%s,%s,%s)",
                    systemToSqlExpr(report.getSourceReports()), systemToSqlExpr(report.getImages()), systemToSqlExpr(report.getImageNames()), systemToSqlExpr(report.getCode())));
        } else {
            System.out.printf("update report with code =< %s > from files = %s\n", report.getCode(), Joiner.on(", ").join(files));
            getJdbcTemplate().update(format("UPDATE \"_Report\" SET \"Sources\" = %s, \"Images\" = %s, \"ImageNames\" = %s WHERE \"Code\" = %s AND \"Status\" = 'A'",
                    systemToSqlExpr(report.getSourceReports()), systemToSqlExpr(report.getImages()), systemToSqlExpr(report.getImageNames()), systemToSqlExpr(report.getCode())));
        }
    }

    @CliCommand(description = "export activity log into a csv file")
    protected void exportActivityLog(@CliParameter("export_path_file") String exportFile) throws IOException {
        File file = new File(exportFile);
        checkArgument(!file.isDirectory(), "file is directory!");
        System.out.println(format("starting export activity log =< %s >", file.getAbsoluteFile()));
        try (CsvListWriter csv = new CsvListWriter(new FileWriter(file), STANDARD_PREFERENCE)) {
            csv.write(list("Code", "Timestamp", "RequestId", "SessionId", "SessionUser", "Metadata"));
            List<List<String>> activityLog = getJdbcTemplate().query("SELECT \"Code\", \"Timestamp\", \"RequestId\", \"SessionId\", \"SessionUser\", \"Metadata\" FROM \"_ActivityLog\" ORDER BY \"BeginDate\" DESC", (r, i) -> {
                return list(r.getString("Code"), r.getString("Timestamp"), r.getString("RequestId"), r.getString("SessionId"), r.getString("SessionUser"), r.getString("Metadata"));
            });
            activityLog.forEach(rethrowConsumer(csv::write));
            System.out.println(format(" export activity log on file =< %s >", file.getAbsoluteFile()));
        } catch (Exception ex) {
            System.err.println(format("error exporting activity log on file =< %s >", file.getAbsoluteFile()));
        }
    }

    @CliCommand(description = "export activity and access log into a json file")
    protected void exportActivityAndAccessLog(@CliParameter("export_path_file") String exportFile) throws IOException {
        File file = new File(exportFile);
        checkArgument(!file.isDirectory(), "file is directory!");
        System.out.println(format("starting export activity log =< %s >", file.getAbsoluteFile()));
        try {
            List<Map<String, Object>> activityLog = getJdbcTemplate().query("SELECT \"Code\", \"Timestamp\", \"RequestId\", \"SessionId\", \"SessionUser\", \"Metadata\" FROM \"_ActivityLog\" ORDER BY \"BeginDate\" DESC", (r, i) -> {
                return map(
                        "timestamp", r.getString("Timestamp"),
                        "requestIdentifier", r.getString("RequestId"),
                        "actor", map("sessionId", r.getString("SessionId"), "username", r.getString("SessionUser")),
                        "event", r.getString("Code"),
                        "data", fromJson(r.getString("Metadata"), MAP_OF_OBJECTS),
                        "result", "success"
                );
            });
            List<Map<String, Object>> eventLog = getJdbcTemplate().query("SELECT \"Code\", \"Timestamp\", \"RequestId\", \"SessionId\", \"SessionUser\", \"Data\", \"Errors\" FROM \"_EventLog\" WHERE \"Code\" IN ('cm_session_create', 'cm_session_update', 'cm_auth_login_failed') AND (\"Data\" ->> 'login' IS NOT NULL OR \"Data\" ->> 'username' != 'system') ORDER BY \"BeginDate\" DESC", (r, i) -> {
                Map<String, Object> data = fromJson(r.getString("Data"), MAP_OF_OBJECTS);
                Map<String, Object> errors = fromJson(r.getString("Errors"), MAP_OF_OBJECTS);
                return map(
                        "timestamp", r.getString("Timestamp"),
                        "requestIdentifier", r.getString("RequestId"),
                        "actor", map("sessionId", data.get("sessionId"), "username", data.get("username")),
                        "event", r.getString("Code"),
                        "data", data,
                        "result", errors.entrySet().isEmpty() ? "success" : "error"
                );
            });
            System.out.println(format(" export activity log on file =< %s >", file.getAbsoluteFile()));
            FileUtils.writeByteArrayToFile(file, toJson(list(activityLog).with(eventLog)).getBytes());
        } catch (Exception ex) {
            logger.error("error exporting: ", ex);
            System.err.println(format("error exporting activity log on file =< %s >", file.getAbsoluteFile()));
        }
    }

    private void upgradePasswordStorage(PasswordAlgo targetAlgo, boolean skipService) {
        System.out.println("upgrade password to algo = " + serializeEnum(targetAlgo));
        AtomicInteger processed = new AtomicInteger(0), upgraded = new AtomicInteger(0);
        JdbcTemplate jdbc = getJdbcTemplate();
        list("Password", "RecoveryToken").forEach((field) -> {
            String query = format("SELECT \"Id\",\"%s\" FROM \"User\" WHERE \"%s\" IS NOT NULL", field, field);
            if (skipService) {
                query += " AND \"Service\" = FALSE";
            }
            jdbc.queryForList(query).stream().forEach(r -> {
                Long id = toLong(r.get("Id"));
                String rawPassword = toStringOrNull(r.get(field)),
                        clearPassword = decryptPasswordIfPossible(rawPassword);
                processed.incrementAndGet();
                if (isNotBlank(clearPassword) && !equal(detectPasswordAlgo(rawPassword), targetAlgo)) {
                    String encrypred = encryptPassword(clearPassword, targetAlgo);
                    logger.debug("upgrade record {}", id);
                    jdbc.execute(format("SELECT _cm3_class_triggers_disable('\"User\"'); UPDATE \"User\" SET \"%s\" = %s WHERE \"Id\" = %s; SELECT _cm3_class_triggers_enable('\"User\"')", field, systemToSqlExpr(encrypred), id));
                    upgraded.incrementAndGet();
                }
            });
        });
        System.out.printf("done (%s records processed, %s upgraded)\n", processed, upgraded);
    }

    private JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(new DatabaseCreator(config).getCmdbuildDataSource());
    }

}
