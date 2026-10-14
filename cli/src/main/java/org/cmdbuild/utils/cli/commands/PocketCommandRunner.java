/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import static org.cmdbuild.config.CoreConfiguration.CORE_LOGGER_AUTOCONFIGURE_PROPERTY;
import org.cmdbuild.pocket.PocketHelper;
import org.cmdbuild.pocket.PocketUtils;
import static org.cmdbuild.utils.cli.Main.getWarFile;
import org.cmdbuild.utils.cli.utils.CliAction;
import org.cmdbuild.utils.cli.utils.CliCommand;
import org.cmdbuild.utils.cli.utils.CliCommandParser;
import org.cmdbuild.utils.cli.utils.CliCommandUtils;
import static org.cmdbuild.utils.cli.utils.CliCommandUtils.prepareAction;
import static org.cmdbuild.utils.cli.utils.CliCommandParser.printActionHelp;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class PocketCommandRunner extends AbstractCommandRunner {

    private final Map<String, CliAction> actions;

    public PocketCommandRunner() {
        super("pocket", "pocket cmdbuild tools (run cmdbuild with embedded tomcat)");
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
    protected void run(String database) throws URISyntaxException, IOException {
        System.out.printf("start pocket cmdbuild\n  war file = %s\n  database source = %s\n", getWarFile(), database);
        PocketHelper pocket = PocketUtils.pocket(getWarFile())
                .withConfig(CORE_LOGGER_AUTOCONFIGURE_PROPERTY, false)
                .withDbSource(checkNotBlank(database));
        pocket.start().waitUntilReady();
        String url = pocket.getBaseUrl();
        System.out.printf("cmdbuild ready with url = %s\n", url);
//        if (Desktop.isDesktopSupported()) {
//            Desktop.getDesktop().browse(new URI(url));
//        } 
        System.out.println("shutdown?");
        new BufferedReader(new InputStreamReader(System.in)).readLine();//TODO improve this

        System.out.print("stopping pocket cmdbuild ... ");
        pocket.cleanup();
        System.out.println("done");
    }

}
