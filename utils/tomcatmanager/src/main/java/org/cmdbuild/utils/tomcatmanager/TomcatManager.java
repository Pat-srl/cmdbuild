/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.tomcatmanager;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import java.io.File;
import java.io.IOException;
import static java.lang.String.format;
import java.util.UUID;
import java.util.logging.Level;
import jakarta.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.auth.login.file.FileAuthUtils;
import org.cmdbuild.auth.login.file.FileAuthUtils.AuthFile;
import org.cmdbuild.client.rest.RestClientImpl;
import org.cmdbuild.minions.SystemStatus;
import static org.cmdbuild.utils.tomcatmanager.TomcatManagerUtils.execSafe;
import static org.cmdbuild.utils.tomcatmanager.TomcatManagerUtils.sleepSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmNetUtils.isPortAvailable;
import static org.cmdbuild.utils.io.CmPlatformUtils.getOsName;
import static org.cmdbuild.utils.io.CmPlatformUtils.isLinux;
import static org.cmdbuild.utils.io.CmPlatformUtils.isWindows;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.auth.AuthConst.SYSTEM_USER;

/**
 * TomcatManager wraps a tomcat instance, and offers methods to start/stop this
 * instance (and a few utility method that uses {@link TomcatBuilder} to build
 * and destroy the tomcat installation).
 * <br><br>
 * Uses {@link TomcatConfig} as the source of all tomcat configuration.
 * <br><br>
 * sample usage:
 * <pre>{@code
 *
 * TomcatManager tomcatManager = new TomcatManager(TomcatConfig.newBuilder().buildConfig()); //pass here optional config params
 * tomcatManager.buildAndStart();
 * // do stuff
 * tomcatManager.stopAndCleanup();
 * }</pre>
 *
 * @author davide
 */
public class TomcatManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final TomcatConfig tomcatConfig;
    private LogFollow catalinaLog, cmdbuildLog;
    private final PlatformHelper platform;

    public TomcatManager(TomcatConfig tomcatConfig) {
        checkNotNull(tomcatConfig);
        this.tomcatConfig = tomcatConfig;
        if (isWindows()) {
            platform = new WindowsPlatformHelper();
        } else if (isLinux()) {
            platform = new UnixPlatformHelper();
        } else {
            throw runtime("unsupported platform =< %s >", getOsName());
        }
    }

    public void buildAndStart() {
        buildAndStart(true);
    }

    public void buildAndStart(boolean waitForReady) {
        build();
        startTomcat();
        if (waitForReady) {
            waitForTomcatAndCmdbuildStartup();
        }
    }

    public void build() {
        new TomcatBuilder(tomcatConfig).buildTomcat();
    }

    public void stopAndCleanup() {
        if (isRunning()) {
            stopTomcat();
        }
        new TomcatBuilder(tomcatConfig).cleanup();
    }

    public TomcatManager startTomcat() {
        logger.info("starting tomcat");
        checkArgument(!isRunning(), "tomcat already running!");
        Pair<Integer, String> res = platform.startTomcat();
        catalinaLog = new LogFollow(new File(tomcatConfig.getInstallDir(), "logs/catalina.out"));
        catalinaLog.startFollowingLog();
        cmdbuildLog = new LogFollow(new File(tomcatConfig.getInstallDir(), "logs/cmdbuild.log"));//TODO cmdbuild log file
        cmdbuildLog.startFollowingLog();
        logger.info("startup output = \n{}", res.getRight());
        checkArgument(res.getLeft() == 0, "error starting tomcat!");
        checkArgument(isRunning(), "error starting tomcat!");
        logger.info("tomcat is starting...");
        return this;
    }

    public void stopTomcat() {
        logger.info("stopping tomcat");
        checkArgument(isRunning(), "tomcat is not running!");
        Pair<Integer, String> res = platform.stopTomcat();
        logger.info("shutdown output = \n{}", res.getRight());
        for (int i = 0; i < 6 && isRunning(); i++) {
            logger.debug("waiting shutdown...");
            sleepSafe(500);
        }
        if (isRunning()) {
            execSafe("kill", getTomcatPid().toString());
        }
        for (int i = 0; i < 10 && isRunning(); i++) {
            logger.debug("waiting shutdown...");
            sleepSafe(1000);
        }
        if (isRunning()) {
            execSafe("kill", "-9", getTomcatPid().toString());
        }
        sleepSafe(500);
        checkArgument(!isRunning(), "unable to stop tomcat !!");
        stopFollowingLogs();
        FileUtils.deleteQuietly(tomcatConfig.getCatalinaPidFile());
        logger.info("tomcat stopped");
    }

    public void stopFollowingLogs() {
        if (catalinaLog != null) {
            catalinaLog.stopFollowingLog();
            catalinaLog = null;
        }
        if (cmdbuildLog != null) {
            cmdbuildLog.stopFollowingLog();
            cmdbuildLog = null;
        }
    }

    private Pair<Integer, String> execWithLog(@Nullable String logFileName, String... cmd) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            processBuilder.directory(tomcatConfig.getInstallDir());
            File logFile;
            boolean deleteLogFile = logFileName == null;
            if (logFileName != null) {
                logFile = new File(tomcatConfig.getInstallDir(), "logs/" + logFileName);
            } else {
                logFile = new File(tomcatConfig.getInstallDir(), "logs/exec_" + UUID.randomUUID().toString().substring(0, 6) + ".log");
            }
            processBuilder.redirectErrorStream(true);
            FileUtils.deleteQuietly(logFile);
            processBuilder.redirectOutput(logFile);
            logger.info("exec {}", Joiner.on(" ").join(cmd));
            Process process = processBuilder.start();
            int res = process.waitFor();
            String logMessages = FileUtils.readFileToString(logFile, Charsets.UTF_8);
            if (deleteLogFile) {
                FileUtils.deleteQuietly(logFile);
            }
            return Pair.of(res, logMessages);
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean tomcatHasStarted() {
        return platform.tomcatHasStarted();
    }

    public boolean cmdbuildHasStarted() {
        AuthFile authFile = buildAuthFile();
        try {
            SystemStatus status = RestClientImpl.build(format("http://localhost:%s/cmdbuild/", tomcatConfig.getHttpPort()))
                    .doLogin(SYSTEM_USER, authFile.getPassword())
                    .system().getStatus();
            return equal(status, SystemStatus.SYST_READY);
        } catch (Exception ex) {
            logger.error("error checking cmdbuild status", ex);
            return false;
        } finally {
            deleteQuietly(authFile.getFile());
        }
    }

    public AuthFile buildAuthFile() {
        return FileAuthUtils.buildAuthFile(new File(tomcatConfig.getInstallDir(), "temp"));
    }

    public boolean tomcatAndCmdbuildHaveStarted() {
        return tomcatHasStarted() && cmdbuildHasStarted();
    }

    private boolean isRunning() {
        return platform.isRunning();
    }

    @Nullable
    private Integer getTomcatPid() {
        try {
            File pidFile = tomcatConfig.getCatalinaPidFile();
            if (!pidFile.exists()) {
                return null;
            } else {
                Integer pid = Integer.valueOf(StringUtils.trim(FileUtils.readFileToString(pidFile, Charsets.UTF_8)));
                return pid;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void waitForTomcatAndCmdbuildStartup() {
        waitForStartup(() -> tomcatAndCmdbuildHaveStarted());
    }

    public void waitForTomcatStartup() {
        waitForStartup(() -> tomcatHasStarted());
    }

    private void waitForStartup(Supplier<Boolean> isStartedStatusSupplier) {
        final int waitTimeout = 500, waitCount = 240;
        for (int i = 0; i < waitCount && !isStartedStatusSupplier.get(); i++) {
            logger.debug("waiting for tomcat to start...");
            sleepSafe(waitTimeout);
        }
        checkArgument((boolean) isStartedStatusSupplier.get(), "tomcat failed to start in %s seconds", waitCount * waitTimeout / 1000);
        logger.info("tomcat {} is running", tomcatConfig.getInstallDir().getAbsolutePath());

    }

    public TomcatConfig getConfig() {
        return tomcatConfig;
    }

//    public String getBaseUrl(String webapp) {
//        return format("http://%s:%s/%s/", getHostname(), getPort(),checkNotBlank(webapp));
//    }
////	public LogManager getLogManager() {
//		return logFollow;
//	}
    public void flushLogs() {
        if (catalinaLog != null) {
            catalinaLog.flushLogs();
        }
        if (cmdbuildLog != null) {
            cmdbuildLog.flushLogs();
        }
    }

    private interface PlatformHelper {

        Pair<Integer, String> startTomcat();

        Pair<Integer, String> stopTomcat();

        boolean isRunning();

        boolean tomcatHasStarted();
    }

    private class WindowsPlatformHelper implements PlatformHelper {

        @Override
        public Pair<Integer, String> startTomcat() {
            return execWithLog("startup.log", tomcatConfig.getInstallDir() + "\\bin\\startup.bat");
        }

        @Override
        public Pair<Integer, String> stopTomcat() {
            throw new UnsupportedOperationException("operation not supported on this platform");//TODO
        }

        @Override
        public boolean isRunning() {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(TomcatManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            return !isPortAvailable(tomcatConfig.getHttpPort());
        }

        @Override
        public boolean tomcatHasStarted() {
            File[] listFiles = new File(tomcatConfig.getInstallDir() + "/logs").listFiles();
            String fileName = "";
            for (File listFile : listFiles) {
                if (listFile.isFile()) {
                    if (listFile.getName().startsWith("catalina")) {
                        fileName = listFile.getName();
                    }
                }
            }
            return readToString(new File(tomcatConfig.getInstallDir(), "logs/" + fileName)).contains("Server startup");
        }

    }

    private class UnixPlatformHelper implements PlatformHelper {

        @Override
        public Pair<Integer, String> startTomcat() {
            return execWithLog("startup.log", "/bin/bash", "-c", ":> ./logs/catalina.out; ./bin/startup.sh");
        }

        @Override
        public Pair<Integer, String> stopTomcat() {
            return execWithLog("shutdown.log", "/bin/bash", "-c", "./bin/shutdown.sh 30 -force");
        }

        @Override
        public boolean isRunning() {
            Integer pid = getTomcatPid();
            if (pid == null) {
                return false;
            }
            return isPidRunning(pid);
        }

        @Override
        public boolean tomcatHasStarted() {
            return execSafe(tomcatConfig.getInstallDir(), "/bin/bash", "-c", "grep -q 'Server startup' ./logs/catalina.out") == 0;
        }

        private boolean isPidRunning(int pid) {
            return execSafe(new String[]{"kill", "-0", Integer.toString(pid)}) == 0;
        }
    }

}
