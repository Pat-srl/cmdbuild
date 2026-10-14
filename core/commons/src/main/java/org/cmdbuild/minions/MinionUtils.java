/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.minions;

import com.google.common.base.Supplier;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_ERROR;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_NOTRUNNING;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_READY;
import static org.cmdbuild.minions.MinionStatus.MS_ERROR;
import static org.cmdbuild.minions.MinionStatus.MS_READY;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.normalizeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static boolean checkStatusOk(Runnable check) {
        try {
            check.run();
            return true;
        } catch (Exception ex) {
            LOGGER.debug("status check failed", ex);
            return false;
        }
    }

    public static Supplier<Boolean> buildStatusOkChecker(Runnable check) {
        return () -> checkStatusOk(check);
    }

    public static Supplier<MinionRuntimeStatus> buildMinionRuntimeStatusChecker(Supplier<Boolean> isStarted, Runnable checkOk) {
        return buildMinionRuntimeStatusChecker(isStarted, buildStatusOkChecker(checkOk));
    }

    public static Supplier<MinionRuntimeStatus> buildMinionRuntimeStatusChecker(Supplier<Boolean> isStarted, Supplier<Boolean> isOk) {
        return () -> {
            if (isStarted.get()) {
                return isOk.get() ? MRS_READY : MRS_ERROR;
            } else {
                return MRS_NOTRUNNING;
            }
        };
    }

    public static String normalizeMinionId(String name) {
        return checkNotBlank(normalizeId(name).toLowerCase());
    }

    public static String buildServicesStatusInfoMessage(Collection<? extends MinionStatusInfo> minions) {
        return minions.stream().sorted(comparing(MinionStatusInfo::getName))
                .map(s -> format("%-24s    %s   %s", s.getName(), map(MS_READY, "*", MS_ERROR, "E").getOrDefault(s.getStatus(), " "), serializeEnum(s.getStatus()))).collect(joining("\n"));
    }
}
