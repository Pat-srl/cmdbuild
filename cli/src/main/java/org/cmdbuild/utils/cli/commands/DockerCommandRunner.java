/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import java.util.Iterator;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.cmdbuild.utils.cli.utils.CliAction;
import org.cmdbuild.utils.cli.utils.CliCommandParser;
import static org.cmdbuild.utils.cli.utils.CliCommandUtils.executeAction;

public class DockerCommandRunner extends AbstractCommandRunner {

    private final Map<String, CliAction> actions;

    public DockerCommandRunner() {
        super("docker", "docker utils");
        actions = new CliCommandParser().parseActions(this);
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        return options;
    }

    @Override
    protected void printAdditionalHelp() {
        super.printAdditionalHelp();
        System.out.println("\navailable docker utils:");
        actions.values().stream().distinct().forEach((action -> {
            System.out.printf("\t%-32s\t%s\n", action.getHelpAliases(), action.getHelpParameters());
        }));
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        Iterator<String> iterator = cmd.getArgList().iterator();
        if (!iterator.hasNext()) {
            System.out.println("no method selected, doing nothing...");
        } else {
            executeAction(actions, iterator);
        }
    }
}
