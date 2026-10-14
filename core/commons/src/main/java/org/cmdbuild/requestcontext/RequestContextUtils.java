/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.requestcontext;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.MDC;

public class RequestContextUtils {

    private final static Cache<String, String> interrupts = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();

    public static boolean isInterrupted() {
        String current = MDC.get("cm_id");//TODO improve this (const?)
        return isNotBlank(current) && interrupts.asMap().keySet().stream().anyMatch(s -> Pattern.compile(s).matcher(current).find());
    }

    public static void checkNotInterrupted() {
        checkArgument(!isInterrupted(), "current request processing thread has been interrupted!");//TODO pass on reason (?)
    }

    public static void interruptRequest(String key) {
        checkNotBlank(key);
        interrupts.put(key, key);
    }

}
