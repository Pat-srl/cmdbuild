/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.exec;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.transform;
import com.google.common.collect.Ordering;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import static java.util.stream.Collectors.joining;
import jakarta.annotation.Nullable;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.apache.commons.io.IOUtils;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExecutorUtils.namedThreadFactory;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmProcessUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String executeProcess(Object... params) {
        return executeProcess(list(params), null);
    }

    public static String executeProcess(List<?> params) {
        return executeProcess(params, null);
    }

    public static String executeProcess(List<?> params, @Nullable Long timeout) {
        ExecutorService executorService = Executors.newSingleThreadExecutor(namedThreadFactory(CmProcessUtils.class));
        try {
            String cliStr = Joiner.on(" ").join(params);
            LOGGER.debug("exec command = '{}'", cliStr);
            ProcessBuilder processBuilder = new ProcessBuilder(transform(params, CmStringUtils::toStringOrEmpty).toArray(String[]::new));
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            AtomicReference<String> output = new AtomicReference<>("<output unavailable>");
            executorService.submit(() -> {
                try {
                    output.set(IOUtils.toString(process.getInputStream(), Charset.defaultCharset()));
                } catch (Exception ex) {
                    LOGGER.warn("error processing stream output from command = '{}'", ex, cliStr);
                }
            });
            Integer res = null;
            Boolean waitForTimeout = null;
            if (timeout == null) {
                res = process.waitFor();
            } else {
                waitForTimeout = process.waitFor(timeout, TimeUnit.SECONDS);
            }
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.SECONDS);
            if (timeout == null && res != null) {
                LOGGER.debug("command = '{}' returned with code = {} and output = \n\n{}\n", cliStr, res, output.get());
                checkArgument(res == 0, "error executing command = '%s' : %s", cliStr, abbreviate(output.get()));
            } else if (waitForTimeout != null) {
                checkArgument(waitForTimeout, "error executing command '%s', command timed out", cliStr);
            }
            return output.get();
        } catch (IOException | InterruptedException ex) {
            throw runtime(ex);
        } finally {
            shutdownQuietly(executorService);
        }
    }

    public static String executeBashScript(String bashScriptContent, Object... params) {
        return executeBashScript(bashScriptContent, list(params));
    }

    public static String executeBashScript(String bashScriptContent, List<Object> params) {
        File tempFile = tempFile(null, ".sh");
        try {
            writeToFile(bashScriptContent, tempFile);
//            return executeProcess(listOf(Object.class).with("/bin/bash", "-l", tempFile.getAbsolutePath()).with(params));
            return executeProcess(listOf(Object.class).with("/bin/bash", tempFile.getAbsolutePath()).with(params), null);
        } finally {
            deleteQuietly(tempFile);
        }
    }

    public static String getThreadDump() {
        ThreadMXBean service = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfo = service.dumpAllThreads(true, true);
//        return list(threadInfo).stream().map(t -> format("thread name =< %s > id = %s status = %s\n%s", t.getThreadName(), t.getThreadId(), t.getThreadState(), list(t.getStackTrace()).stream().map(e -> format("        %s", e.toString())).collect(joining("\n")))).collect(joining("\n\n"));
        return list(threadInfo).stream().sorted(Ordering.natural().onResultOf(ThreadInfo::getThreadName)).map(ThreadInfo::toString).collect(joining());
    }
}
