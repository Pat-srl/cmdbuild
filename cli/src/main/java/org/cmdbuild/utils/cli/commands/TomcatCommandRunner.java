/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import static org.cmdbuild.utils.lang.CmExecutorUtils.sleepSafe;
import org.cmdbuild.utils.tomcatmanager.TomcatConfig;
import org.cmdbuild.utils.tomcatmanager.TomcatManager;

public class TomcatCommandRunner extends AbstractCommandRunner {

    public TomcatCommandRunner() {
        super("tomcat", "manage tomcat instance (build, destroy, etc");
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        //TODO this command does not have any otion for now, it is just a stub
        return options;
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        //TODO do something with cmd
        TomcatConfig tomcatConfig = TomcatConfig.builder().build();

        TomcatManager tomcatManager = new TomcatManager(tomcatConfig);
        tomcatManager.buildAndStart();

        sleepSafe(10000);

        tomcatManager.stopAndCleanup();
    }

}
