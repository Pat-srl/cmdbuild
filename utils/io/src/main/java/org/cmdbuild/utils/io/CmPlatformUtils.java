package org.cmdbuild.utils.io;

import java.io.File;
import static java.lang.Math.toIntExact;
import java.lang.invoke.MethodHandles;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.io.FileUtils.readLines;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmPlatformUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public final static String OS_WINDOWS = "windows", OS_LINUX = "linux";

    public static boolean isWindows() {
        return getOsName().toLowerCase().contains("windows");
    }

    public static boolean isLinux() {
        return getOsName().equalsIgnoreCase("linux");
    }

    public static String getOsType() {
        if (isLinux()) {
            return OS_LINUX;
        } else if (isWindows()) {
            return OS_WINDOWS;
        } else {
            return getOsName();
        }
    }

    public static String getOsName() {
        return System.getProperty("os.name", "unknown");
    }

    public static int getProcessMemoryMegs() {
        if (isLinux()) {
            try {
                for (String line : readLines(new File("/proc/self/status"))) {
                    Matcher matcher = Pattern.compile("^VmRSS:\\s*([0-9]+)\\s*kB").matcher(line);
                    if (matcher.find()) {
                        return toIntExact(toLong(matcher.group(1)) * 1024 / 1000000);
                    }
                }
            } catch (Exception ex) {
                LOGGER.debug("error reading process memory info", ex);
            }
        }
        return toIntExact(Runtime.getRuntime().totalMemory() * 12 / 10000000);//fallback estimate, total mem + 20%
    }
}
