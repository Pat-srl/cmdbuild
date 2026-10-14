/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import static com.google.common.base.Preconditions.checkArgument;
import java.io.File;
import static java.lang.String.format;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import static org.cmdbuild.utils.cli.Main.getWarFile;
import static org.cmdbuild.utils.date.CmDateUtils.dateTimeFileSuffix;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.console.CmConsole;
import static org.cmdbuild.utils.console.CmConsole.defaultConsole;
import static org.cmdbuild.utils.tomcatmanager.TomcatManagerUtils.execSafe;

public class UpgradeCommandRunner extends AbstractCommandRunner {

    public UpgradeCommandRunner() {
        super("upgrade", "upgrade cmdbuild webapp in existing tomcat instance");
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        return options;
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        String locationString = checkNotBlank(cmd.getArgList().isEmpty() ? System.getProperty("user.dir") : cmd.getArgList().get(0), "must set location of tomcat instance to upgrade");
        File tomcatLocation = new File(locationString), webappLocation = new File(tomcatLocation, "webapps/cmdbuild");//TODO auto detect webapp name
        checkArgument(tomcatLocation.isDirectory() && new File(tomcatLocation, "conf/server.xml").isFile() && webappLocation.isDirectory(), "cannot find existing tomcat/cmdbuild at location %s and %s", tomcatLocation, webappLocation);
        System.out.println("\n upgrade tomcat instance at " + webappLocation.getAbsolutePath() + "; are you sure?");
        CmConsole console = defaultConsole();
        console.readLine();
        File warFile = getWarFile();
        System.out.println("stop tomcat (if running)");
        Runtime.getRuntime().exec(new String[]{tomcatLocation.getAbsolutePath() + "/bin/shutdown.sh", "-force"}).waitFor();

        String backupFile = format("%s_backup_%s.tar.gz", webappLocation.getName(), dateTimeFileSuffix());
        System.out.printf("backup old cmdbuild to webapps/%s\n", backupFile);
        execSafe(new File(tomcatLocation, "webapps"), "/bin/bash", "-c", format("tar -czf '%s' '%s' && rm -rf '%s'", backupFile, webappLocation.getName(), webappLocation.getName()));

        System.out.printf("unpack new cmdbuild webapp from file = %s\n", warFile.getAbsolutePath());
        execSafe(new File(tomcatLocation, "webapps"), "/bin/bash", "-c", format("mkdir '%s' && unzip -q '%s' -d '%s'", webappLocation.getName(), warFile.getAbsolutePath(), webappLocation.getName()));

        System.out.println("done!");
    }

}
