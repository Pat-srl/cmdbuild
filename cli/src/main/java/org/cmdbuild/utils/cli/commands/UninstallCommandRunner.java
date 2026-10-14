/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.utils.cli.utils.DatabaseUtils.dropDatabase;
import org.cmdbuild.utils.console.CmConsole;
import static org.cmdbuild.utils.console.CmConsole.defaultConsole;

public class UninstallCommandRunner extends AbstractCommandRunner {

    public UninstallCommandRunner() {
        super("uninstall", "remove existing cmdbuild+tomcat instance");
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        options.addOption("d", "dir", true, "tomcat install dir");
        return options;
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        String locationString = trimToNull(cmd.hasOption("d") ? cmd.getOptionValue("d") : (cmd.getArgList().isEmpty() ? null : cmd.getArgList().get(0)));
        checkNotNull(locationString, "must set location of tomcat instance to remove");
        File tomcatLocation = new File(locationString);
        checkArgument(tomcatLocation.isDirectory() && new File(tomcatLocation, "conf/server.xml").isFile(), "cannot find existing tomcat at location %s", locationString);
        System.out.println("\n destroy tomcat instance at " + tomcatLocation.getAbsolutePath() + "; are you sure?");
        CmConsole console = defaultConsole();
        console.readLine();
        System.out.println("stop tomcat (if running)");
        Runtime.getRuntime().exec(new String[]{tomcatLocation.getAbsolutePath() + "/bin/shutdown.sh", "-force"}).waitFor();
        dropDatabase(findDatabaseConfigFile(tomcatLocation));
        System.out.println("delete tomcat directory " + tomcatLocation.getAbsolutePath());
        FileUtils.deleteDirectory(tomcatLocation);
        System.out.println("done!");
    }

    private File findDatabaseConfigFile(File tomcatLocation) {
        if (new File(tomcatLocation, "webapps/cmdbuild/WEB-INF/conf/database.conf").isFile()) {
            return new File(tomcatLocation, "webapps/cmdbuild/WEB-INF/conf/database.conf");
        } else {
            return new File(tomcatLocation, "conf/cmdbuild/database.conf");
        }
    }

}
