/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.RiverTask;
import org.cmdbuild.workflow.river.engine.task.ScriptTaskExtraAttr;
import static org.cmdbuild.workflow.river.engine.utils.PlanToDotGraphPlotter.planToDotGraph;
import org.cmdbuild.workflow.river.engine.xpdl.XpdlParser;
import org.cmdbuild.utils.cli.utils.CliAction;
import org.cmdbuild.utils.cli.utils.CliCommand;
import org.cmdbuild.utils.cli.utils.CliCommandParser;
import static org.cmdbuild.utils.cli.utils.CliCommandUtils.executeAction;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.exec.CmProcessUtils.executeProcess;
import static org.cmdbuild.workflow.river.engine.utils.PlanToStringPlotter.planToString;

public class RiverCommandRunner extends AbstractCommandRunner {

    private final Map<String, CliAction> actions;

    public RiverCommandRunner() {
        super("river", "river workflow utils");
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
        System.out.println("\navailable river utils:");
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

    @CliCommand(alias = {"plot"})
    protected void graph(String xpdlFile) {
        String graph = fileToDotGraph(xpdlFile);
        System.out.println(graph);
    }

    @CliCommand
    protected void dotty(String xpdlFile) {
        String graph = fileToDotGraph(xpdlFile);
        File temp = tempFile();
        try {
            writeToFile(temp, graph);
            executeProcess("dotty", temp.getAbsolutePath());
        } finally {
            FileUtils.deleteQuietly(temp);
        }
    }

    @CliCommand
    protected void plan(String xpdlFile) {
        String xpdlContent = readToString(new File(xpdlFile));
        RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(xpdlContent);
        System.out.println(planToString(plan));
    }

    @CliCommand
    protected void getScript(String xpdlFile, String stepId) {
        String xpdlContent = readToString(new File(xpdlFile));
        RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(xpdlContent);
        RiverTask task = plan.getStepById(stepId).getTask();
        ScriptTaskExtraAttr scriptAttr = (ScriptTaskExtraAttr) task.getTaskTypeData();
        System.out.printf("step id: %s\nscript type: %s\n\n%s\n", stepId, scriptAttr.getLanguage(), scriptAttr.getScript());
    }

    private String fileToDotGraph(String xpdlFile) {
        String xpdlContent = readToString(new File(xpdlFile));
        RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(xpdlContent);
        String graph = planToDotGraph(plan);
        return graph;
    }
}
