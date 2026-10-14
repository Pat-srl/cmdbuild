/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import org.apache.commons.codec.binary.Hex;
import static org.cmdbuild.utils.lang.CmExecutorUtils.sleepSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmRuntimeUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String getCurrentPidOrRuntimeId() {
        Matcher matcher = Pattern.compile("([^@]+)@.*").matcher(ManagementFactory.getRuntimeMXBean().getName());
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return Hex.encodeHexString(Runtime.getRuntime().toString().getBytes());
        }
    }

    public static boolean hasEnoughFreeMemory(long expectedUsageBytes) {
        long total = Runtime.getRuntime().totalMemory(), free = Runtime.getRuntime().freeMemory(), max = Runtime.getRuntime().maxMemory(),
                availableNow = (max - total) + free, availableAfter = availableNow - expectedUsageBytes, availableAfterPerc = availableAfter * 100 / max;
        return availableAfter > 500000000 && availableAfterPerc > 20;
    }

    public static boolean hasEnoughFreeMemoryGC(long expectedUsageBytes) {
        boolean hasEnoughFreeMemory = hasEnoughFreeMemory(expectedUsageBytes);
        if (!hasEnoughFreeMemory) {
            LOGGER.debug("low memory detected, trigger GC =< {} >", ManagementFactory.getGarbageCollectorMXBeans().stream().filter(g -> g.isValid()).map(GarbageCollectorMXBean::getName).collect(joining(", ")));
            System.gc();
            sleepSafe(500);
            return hasEnoughFreeMemory(expectedUsageBytes);
        }
        return hasEnoughFreeMemory;
    }

    public static String memBytesToDisplaySize(long count) {
        return format("%,d MB", count / 1000000);
    }

}
