/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import java.io.File;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.utils.postgres.PostgresUtils;

public class PostgresCommandRunner extends AbstractCommandRunner {

    public PostgresCommandRunner() {
        super("postgres", "run database operations with postgres binary client (dump, restore, etc)");
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        options.addOption("dburl", true, "postgres database url");
        options.addOption("database", true, "postgres database");
        options.addOption("file", true, "file for import or export");
        options.addOption("dump", "dump database");
        options.addOption("restore", "restore database");
        options.addOption("schema", true, "comma separated list of schemas to restore (default = all)");
        options.addOption("createschemas", "try to create schemas before import (default=false)");
        options.addOption("checkserver", "check server version");
        return options;
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        checkArgument(cmd.hasOption("checkserver") ^ cmd.hasOption("dump") ^ cmd.hasOption("restore"), "must choose ONE of `checkserver`, `dump`, `restore`");
        if (cmd.hasOption("checkserver")) {
            System.out.println("server version: " + PostgresUtils.newHelper().withUrl(cmd.getOptionValue("dburl")).buildHelper().getServerVersion());
        } else if (cmd.hasOption("dump")) {
            String database = emptyToNull(cmd.getOptionValue("database"));
            checkNotNull(database, "must set valid 'database'");
            File targetFile = getFile(cmd, "file", false, "must set valid 'file'");
            checkArgument(!isBlank(targetFile.getName()) && targetFile.getParentFile().isDirectory(), "must set valid 'file'");
            PostgresUtils.newHelper("localhost", 5432, "postgres", "postgres").withDatabase(database)
                    .withSchemas(Splitter.on(",").omitEmptyStrings().splitToList(nullToEmpty(cmd.getOptionValue("schema"))))
                    .buildHelper().dumpDatabaseToFile(targetFile);
            System.out.println("dumped database " + database + " to file " + targetFile + " " + FileUtils.byteCountToDisplaySize(targetFile.length()));
        } else if (cmd.hasOption("restore")) {
            String database = emptyToNull(cmd.getOptionValue("database"));
            checkNotNull(database, "must set valid 'database'");
            File sourceFile = getFile(cmd, "file", true, "must set valid 'file'");
            sourceFile = PostgresUtils.getDumpFromFileExtractIfNecessary(sourceFile);
            List<String> schemasToRestore = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(nullToEmpty(cmd.getOptionValue("schema")));
            PostgresUtils.newHelper("localhost", 5432, "postgres", "postgres")
                    .withDatabase(database)
                    .withCreateSchema(cmd.hasOption("createschemas"))
                    .withSchemas(schemasToRestore)
                    .buildHelper().restoreDumpFromFile(sourceFile);
            System.out.println("restored database " + database + " from file " + sourceFile + " " + FileUtils.byteCountToDisplaySize(sourceFile.length()));
        } else {
            checkArgument(false, "this code is supposed to be be unreacheable");
        }
    }

}
