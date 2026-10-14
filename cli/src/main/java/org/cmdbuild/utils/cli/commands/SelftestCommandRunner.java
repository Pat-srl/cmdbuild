/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import java.util.Iterator;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import static org.cmdbuild.config.CoreConfiguration.CORE_LOGGER_AUTOCONFIGURE_PROPERTY;
import static org.cmdbuild.dao.config.inner.DatabaseCreator.DEMO_DUMP;
import org.cmdbuild.pocket.PocketHelper;
import org.cmdbuild.pocket.PocketUtils;
import static org.cmdbuild.utils.cli.Main.getWarFile;
import org.cmdbuild.utils.cli.utils.CliAction;
import org.cmdbuild.utils.cli.utils.CliCommand;
import org.cmdbuild.utils.cli.utils.CliCommandParser;
import org.cmdbuild.utils.cli.utils.CliCommandUtils;
import static org.cmdbuild.utils.cli.utils.CliCommandUtils.prepareAction;
import static org.cmdbuild.utils.cli.utils.CliCommandParser.printActionHelp;

public class SelftestCommandRunner extends AbstractCommandRunner {

    private final Map<String, CliAction> actions;

    public SelftestCommandRunner() {
        super("selftest", "cli/war selftest tools");
        actions = new CliCommandParser().parseActions(this);
    }

    @Override
    protected void printAdditionalHelp() {
        System.out.println("\navailable methods:");
        printActionHelp(actions);
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        Iterator<String> iterator = cmd.getArgList().iterator();
        if (!iterator.hasNext()) {
            System.out.println("no rest call requested, doing nothing...");
        } else {
            CliCommandUtils.ExecutableAction action = prepareAction(actions, iterator);
            action.execute();
        }
    }

    @CliCommand
    protected void cli() {
        System.out.println("cli OK");//TODO add cli checksum check (?)
    }

    @CliCommand(alias = {"war", "webapp", "tomcat"})
    protected void pocket() {
        PocketHelper pocket = PocketUtils.pocket(getWarFile())
                .withConfig(CORE_LOGGER_AUTOCONFIGURE_PROPERTY, false)
                .withDbSource(DEMO_DUMP);
        System.out.print("test webapp with embedded tomcat ... ");
        try {
            pocket.start().waitUntilReady();
        } finally {
            pocket.cleanup();
        }
        System.out.println("webapp OK");
    }
}
