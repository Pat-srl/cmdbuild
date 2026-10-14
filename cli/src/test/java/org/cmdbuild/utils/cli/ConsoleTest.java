/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli;

import java.io.File;
import org.cmdbuild.utils.cli.commands.DbconfigCommandRunner;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.lang.CmExecutorUtils.sleepSafe;
import org.junit.Ignore;
import org.junit.Test;

public class ConsoleTest {

    @Test
    @Ignore("only useful for manual testing")
    public void testBackspace() {
        System.out.println("1 4 3");
        sleepSafe(1000);
        System.out.print("\033[1A");
        System.out.print("\033[2K");
        System.out.println("      ");
        sleepSafe(1000);
        System.out.print("\033[1A");
        System.out.print("\033[2K");
        System.out.println("1 2 3");
    }

//    @Test
//    @Ignore
//    public void testCompileReportsCommand() throws Exception {
//        String configFileContent
//                = "db.password=cmdbuild\n"
//                + "db.admin.password=postgres\n"
//                + "db.url=jdbc:postgresql://localhost:5433/cmdbuild_30\n"
//                + "db.username=cmdbuild\n"
//                + "db.admin.username=postgres";
//        DbconfigCommandRunner runner = new DbconfigCommandRunner();
//        File configFile = new File("/tmp/confExample.txt");
//        writeToFile(configFile, configFileContent.getBytes());
//        runner.compileReportXml(configFile);
//    }

}
