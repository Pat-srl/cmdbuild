/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.tomcatmanager;

import com.google.common.base.Joiner;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author davide
 */
public class TomcatManagerUtils {

    private final static Logger logger = LoggerFactory.getLogger(TomcatManagerUtils.class);

    public static void sleepSafe(int millis) {//TODO move this in cmdbuild common utils
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
        }
    }

    public static int execSafe(String... cmd) {
        return execSafe(null, cmd);
    }

    public static int execSafe(File workingDir, String... cmd) {
        try {
            logger.debug("exec {}", Joiner.on(" ").join(cmd));
            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            processBuilder.directory(workingDir);
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            return processBuilder.start().waitFor();
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
