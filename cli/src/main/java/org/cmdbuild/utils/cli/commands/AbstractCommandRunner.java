/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import static java.util.Collections.singletonList;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfig;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfigImpl;
import org.cmdbuild.utils.cli.CliCommandRunner;
import static org.cmdbuild.utils.cli.Main.getCliHome;
import static org.cmdbuild.utils.cli.Main.getExecNameForHelpMessage;
import static org.cmdbuild.utils.cli.Main.isRunningFromWebappDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCommandRunner implements CliCommandRunner {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final String description;
    private final List<String> names;

    public AbstractCommandRunner(String name, String description) {
        this(singletonList(name), description);
    }

    public AbstractCommandRunner(Collection<String> names, String description) {
        this.names = ImmutableList.copyOf(names);
        this.description = checkNotNull(trimToNull(description));
        checkArgument(!names.isEmpty() && !names.stream().anyMatch(StringUtils::isBlank));
    }

    @Override
    public String getName() {
        return names.iterator().next();
    }

    @Override
    public List<String> getNames() {
        return names;
    }

    @Override
    public String getDescription() {
        return description;
    }

    protected Options buildOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "print help");
        return options;
    }

    @Override
    public void exec(String[] args) throws Exception {
        try {
            Options options = buildOptions();
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(getExecNameForHelpMessage() + " " + getName(), options, true);
                printAdditionalHelp();
            } else {
                exec(cmd);
            }
        } catch (ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    protected void printAdditionalHelp() {

    }

    protected abstract void exec(CommandLine cmd) throws Exception;

    protected File getFile(CommandLine cmd, String key, boolean shouldExist, String errorMessage) throws Exception {
        String value = cmd.getOptionValue(key);
        checkArgument(!isBlank(value), errorMessage);
        File file = new File(value);
        if (shouldExist) {
            checkArgument(file.isFile(), errorMessage);
        } else {
            checkArgument(file.getParentFile().isDirectory(), errorMessage);
        }
        return file;
    }

    protected File getConfigFile(CommandLine cmd) throws Exception {
        File configFile;
        if (isRunningFromWebappDir()) {
            configFile = new File(new File(new File(getCliHome(), "../../conf"), getCliHome().getName()), "database.conf");
        } else {
            configFile = new File("conf/cmdbuild/database.conf");
        }
        if (!configFile.isFile() || cmd.hasOption("configfile")) {
            configFile = getFile(cmd, "configfile", true, "must set valid 'configfile'");
        }
        configFile = configFile.getCanonicalFile();
        logger.debug("using config file = {}", configFile.getAbsolutePath());
        return configFile;
    }

    protected DatabaseCreatorConfig getDbConfig(CommandLine cmd) throws Exception {
        return DatabaseCreatorConfigImpl.builder().withConfig(new FileInputStream(getConfigFile(cmd))).build();
    }
}
