/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.Ordering;
import static com.google.common.io.Files.copy;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.apache.commons.lang3.RandomStringUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.dao.config.inner.ConfigImportStrategy.CIS_DATA_ONLY;
import static org.cmdbuild.dao.config.inner.ConfigImportStrategy.CIS_RESTORE_BACKUP;
import org.cmdbuild.dao.config.inner.DatabaseCreator;
import static org.cmdbuild.dao.config.inner.DatabaseCreator.EMBEDDED_DATABASES;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfig;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfigImpl;
import org.cmdbuild.dao.config.inner.PatchService;
import static org.cmdbuild.dao.config.utils.PatchManagerUtils.getFunctionCodeRepository;
import org.cmdbuild.dao.sql.utils.SqlFunction;
import org.cmdbuild.utils.cli.utils.CliAction;
import org.cmdbuild.utils.cli.utils.CliCommand;
import org.cmdbuild.utils.cli.utils.CliCommandParser;
import static org.cmdbuild.utils.cli.utils.CliCommandParser.printActionHelp;
import static org.cmdbuild.utils.cli.utils.CliCommandUtils.prepareAction;
import org.cmdbuild.utils.cli.utils.CliParameter;
import static org.cmdbuild.utils.cli.utils.CliUtils.extractDatabaseFromZipFile;
import static org.cmdbuild.utils.cli.utils.CliUtils.getDbdumpFileOrNull;
import static org.cmdbuild.utils.cli.utils.CliUtils.hasInteractiveConsole;
import org.cmdbuild.utils.cli.utils.DatabaseUtils;
import static org.cmdbuild.utils.cli.utils.DatabaseUtils.buildDatabaseCreator;
import static org.cmdbuild.utils.cli.utils.DatabaseUtils.dropDatabase;
import static org.cmdbuild.utils.cli.utils.DatabaseUtils.getSqlDir;
import org.cmdbuild.utils.crypto.CmDataCryptoUtils;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.io.CmIoUtils.javaTmpDir;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import static org.cmdbuild.utils.io.CmPropertyUtils.loadProperties;
import static org.cmdbuild.utils.io.CmPropertyUtils.writeProperties;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;
import org.cmdbuild.utils.postgres.PostgresUtils;

public class DbconfigCommandRunner extends AbstractCommandRunner {

    private final Map<String, CliAction> actions;
    private File configFile;
    private boolean skippatches, backuprestore, dataonly, keepconfigs, xzip, singlecore, excludelogs, freezesessions, interactive;

    public DbconfigCommandRunner() {
        super(list("dbconfig", "db", "d"), "configure cmdbuild database");
        actions = new CliCommandParser().parseActions(this);
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        options.addOption("configfile", true, "cmdbuild database config file (es: database.conf); default to conf/<webapp>/database.conf");
        options.addOption("skippatches", false, "skip patches (do not apply patches)");
        options.addOption("backuprestore", false, "backup-restore import mode (restore all configs from dump)");
        options.addOption("dataonly", false, "data-only import mode (restore no configs from dump - except those strongly coupled with data, such as multitenant mode)");
        options.addOption("keepconfigs", false, "keep local configs for those categories that are excluded from config import");
        options.addOption("xzip", false, "compress dump with xzip (best compression, slow)");
        options.addOption("singlecore", false, "use one core to restore db (useful when concurrency is a problem)");
        options.addOption("excludelogs", false, "avoid dumping table _Request, _JobRun and _EventLog");
        options.addOption("freezesessions", false, "freeze all existing sessions in db (so that they won't expire); this is useful when importing a bug report");
        options.addOption("interactive", false, "force interactive cmd");
        return options;
    }

    @Override
    protected void printAdditionalHelp() {
        System.out.println("\navailable dbconfig methods:");
        printActionHelp(actions);
        System.out.println("\nconfig file example:\n");
        System.out.println(readToString(getClass().getResourceAsStream("/database.conf_cli_example")));
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        configFile = getConfigFile(cmd);
        skippatches = cmd.hasOption("skippatches");
        backuprestore = cmd.hasOption("backuprestore");
        dataonly = cmd.hasOption("dataonly");
        keepconfigs = cmd.hasOption("keepconfigs");
        xzip = cmd.hasOption("xzip");
        singlecore = cmd.hasOption("singlecore");
        excludelogs = cmd.hasOption("excludelogs");
        freezesessions = cmd.hasOption("freezesessions");
        interactive = cmd.hasOption("interactive");
        prepareAction(actions, cmd.getArgList().iterator()).execute();
    }

    @CliCommand(description = "drop database")
    protected void drop() throws Exception {
        dropDatabase(configFile);
    }

    @CliCommand(description = "create database")
    protected void create(@CliParameter("database_type|dump_to_import") String dump) throws Exception {
        doWithDbFile(trimAndCheckNotBlank(dump, "must set non-null 'dbtype' (es: 'empty','demo',...)"), (databaseType) -> {

            DatabaseCreatorConfig config = DatabaseCreatorConfigImpl.builder().accept(b -> {
                if (backuprestore) {
                    b.withConfigImportStrategy(CIS_RESTORE_BACKUP);
                } else if (dataonly) {
                    b.withConfigImportStrategy(CIS_DATA_ONLY);
                }
                if (keepconfigs) {
                    b.withKeepLocalConfig(true);
                }
                if (singlecore) {
                    b.withSingleCore(true);
                }
            }).withSource(databaseType).withConfig(configFile).build();

            DatabaseUtils.createDatabase(config, !skippatches);

            if (freezesessions) {
                System.out.println("freezing sessions");
                new DatabaseCreator(config).freezeSessions();
            }

        });
    }

    @CliCommand(description = "drop database, then create database", alias = {"importdb"})
    protected void recreate(@CliParameter("database_type|dump_to_import") String dump) throws Exception {
        drop();
        create(dump);
    }

    @CliCommand(description = "dump database to file")
    protected void dump(@CliParameter("target_file") String filename) throws Exception {
        DatabaseCreatorConfig config = DatabaseCreatorConfigImpl.builder().withConfig(configFile).build();
        boolean xzCompression = xzip;
        boolean lightMode = excludelogs;
        File file;
        if (isNotBlank(filename)) {
            file = new File(filename);
            if (file.getName().endsWith(".xz")) {
                xzCompression = true;
            }
        } else {
            file = new File(javaTmpDir(), format("cmdbuild_%s_%s.%s", config.getDatabaseName(), CmDateUtils.dateTimeFileSuffix(), xzCompression ? "dump.xz" : "dump"));
        }
        if (hasInteractiveConsole()) {
            System.out.printf("dump database = %s to file = %s\n", config.getDatabaseUrl(), file.getAbsolutePath());
        }
        PostgresUtils.newHelper(
                config.getHost(),
                config.getPort(),
                config.getAdminUser(),
                config.getAdminPassword())
                .withDatabase(config.getDatabaseName())
                .withXzCompression(xzCompression)
                .withLightMode(lightMode)
                .buildHelper()
                .dumpDatabaseToFile(file);
        if (hasInteractiveConsole()) {
            System.out.printf("dump OK to %s %s\n", file.getAbsolutePath(), FileUtils.byteCountToDisplaySize(file.length()));
        } else if (interactive) {
            // do nothing, skip redirect to sysout and delete file
        } else {
            copy(file, System.out);
            deleteQuietly(file);
        }
    }

    @CliCommand(description = "check dump file", alias = {"check"})
    protected void checkDump(@CliParameter("database_type|dump_to_import") String dump) throws Exception {
        doWithDbFile(dump, (databaseType) -> {
            File file = new File(databaseType);
            try {
                PostgresUtils.checkDumpFile(file);
                System.err.printf("dump OK = %s (%s)\n", file.getAbsolutePath(), byteCountToDisplaySize(file.length()));
            } catch (Exception ex) {
                System.err.printf("\ndump ERROR = %s (%s) : %s\n\n", file.getAbsolutePath(), byteCountToDisplaySize(file.length()), ex.toString());
                throw ex;
            }
        });
    }

    @CliCommand(description = "anonymize db, the anonymization affects only attribute anonymizable", alias = {"anonymize"})
    protected void anonymizeDb(@CliParameter("database_anonymized_dump") String anonymizedDumpName) throws Exception {
        anonymizeDb(RandomStringUtils.secure().nextAlphabetic(10).toLowerCase(), anonymizedDumpName);
    }

    @CliCommand(description = "anonymize db, the anonymization affects only attribute anonymizable", alias = {"anonymize"})
    protected void anonymizeDb(@CliParameter("temporary_database_name") String tempDatabaseName, @CliParameter("database_anonymized_dump") String anonymizedDumpName) throws Exception {
        File dumpPath = tempFile();
        File tempFile = tempFile();
        try {
            dump(dumpPath.getAbsolutePath());

            DatabaseCreatorConfig databaseConfig = DatabaseCreatorConfigImpl.builder().withConfig(configFile).build();
            Map<String, String> configs = loadProperties(configFile);
            configs.computeIfPresent("db.url", (k, v) -> v.replace(databaseConfig.getDatabaseName(), tempDatabaseName));
            writeProperties(tempFile, configs);
            configFile = tempFile;
            create(dumpPath.getAbsolutePath());
            dumpPath.delete();

            buildDatabaseCreator(DatabaseCreatorConfigImpl.builder().withConfig(configFile).build()).anonymize();
            excludelogs = true;
            dump(anonymizedDumpName);
            tempFile.delete();
        } finally {
            FileUtils.deleteQuietly(dumpPath);
            FileUtils.deleteQuietly(tempFile);
        }
    }

    @CliCommand(description = "apply patches to existing database")
    protected void patch() throws Exception {
        DatabaseCreator databaseCreator = buildDatabaseCreator(DatabaseCreatorConfigImpl.builder().withConfig(configFile).build());
        System.out.println("apply patches");
        databaseCreator.applyPatches();
        System.out.print("done");
    }

    @CliCommand(description = "apply patches to existing database, up to <lastpatch>")
    protected void patch(@CliParameter("name_of_last_patch") String lastPatch) throws Exception {
        DatabaseCreator databaseCreator = buildDatabaseCreator(DatabaseCreatorConfigImpl.builder().withConfig(configFile).build());
        System.out.printf("apply patches up to =< %s >\n", lastPatch);
        databaseCreator.applyPatchesUpTo(lastPatch);
        System.out.print("done");
    }

    @CliCommand(description = "list available patches")
    protected void listPatches() throws Exception {
        PatchService patchManager = buildDatabaseCreator(DatabaseCreatorConfigImpl.builder().withConfig(configFile).build()).getPatchManager();
        System.out.printf("\nlast patch on db is =< %s >\n\n", patchManager.getLastPatchOnDbKeyOrNull());
        System.out.printf("found %s available patches:\n", patchManager.getAvailableCorePatches().size());
        patchManager.getAvailableCorePatches().forEach(p -> System.out.printf("\t%s %-30s %s\n", p.getCategory(), p.getVersion(), p.getDescription()));
    }

    @CliCommand(description = "list available functions")
    protected void listFunctions() throws Exception {
        List<SqlFunction> functions = getFunctionCodeRepository(getSqlDir()).getAvailableFunctions();
        System.out.printf("found %s available functions:\n", functions.size());
        functions.stream().sorted(Ordering.natural().onResultOf(SqlFunction::getSignature)).forEach(f -> System.out.printf("\t%-80s %s\n", f.getSignature(), f.getRequiredPatchVersion()));
    }

    @CliCommand(description = "compact database, removing unuseful data")
    protected void compactDb() throws Exception {
        new DatabaseCreator(DatabaseCreatorConfigImpl.build(readToString(configFile))).compactDb();
    }

    @CliCommand(description = "rebuild mismatch patches hash")
    protected void rebuildPatchesHash() throws Exception {
        PatchService patchManager = buildDatabaseCreator(DatabaseCreatorConfigImpl.builder().withConfig(configFile).build()).getPatchManager();
        patchManager.rebuildPatchesHash();
    }

    private void doWithDbFile(String databaseType, Consumer<String> consumer) throws IOException {
        List<File> toDelete = list();

        if (!EMBEDDED_DATABASES.contains(databaseType)) { //TODO improve this
            File file = getDbdumpFileOrNull(databaseType);
            if (file != null) {
                databaseType = file.getAbsolutePath();
            }
        }

        if (databaseType.endsWith(".secure")) {
            File secure = new File(databaseType), file = new File(databaseType.replaceFirst(".secure$", ""));
            try (FileInputStream in = new FileInputStream(secure); FileOutputStream out = new FileOutputStream(file)) {
                CmDataCryptoUtils.withPassword(new String(System.console().readPassword("password required for file < %s >: ", secure))).decrypt(in, out);
            }
            databaseType = file.getAbsolutePath();
            toDelete.add(file);
        }

        if (databaseType.endsWith(".zip")) {
            File file = new File(databaseType);
            checkArgument(file.isFile(), "invalid zip file = %s", databaseType);
            databaseType = extractDatabaseFromZipFile(file).getAbsolutePath();
            toDelete.add(new File(databaseType));
        }

        try {
            consumer.accept(databaseType);
        } finally {
            toDelete.forEach(FileUtils::deleteQuietly);
        }
    }

}
