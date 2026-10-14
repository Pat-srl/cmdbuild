/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.xpdl;

import static com.google.common.base.Objects.equal;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.SCRIPT_LANGUAGE_BEANSHELL;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.SCRIPT_LANGUAGE_GROOVY;

public class XpdlUtils {

    public static String parseScriptType(@Nullable String scriptType) {
        return parseScriptType(scriptType, null);
    }

    public static String parseScriptType(@Nullable String scriptType, @Nullable String defaultType) {
        if (isBlank(scriptType) || scriptType.toLowerCase().contains("java")) {
            if (!equal(scriptType, defaultType) && isNotBlank(defaultType)) {
                return parseScriptType(defaultType);
            } else {
                return SCRIPT_LANGUAGE_BEANSHELL;
            }
        } else if (scriptType.toLowerCase().contains(SCRIPT_LANGUAGE_BEANSHELL)) {
            return SCRIPT_LANGUAGE_BEANSHELL;
        } else if (scriptType.toLowerCase().contains(SCRIPT_LANGUAGE_GROOVY)) {
            return SCRIPT_LANGUAGE_GROOVY;
        } else {
            throw unsupported("unsupported script type =< %s >", scriptType);
        }
    }

    public static String buildStepIdPrefixFromParentActivityId(String parentActivityId) {
        return format("%s_", checkNotBlank(parentActivityId));
    }

    public static String buildStepIdFromParentActivityIdAndActivityId(String parentActivityId, String activityId) {
        return buildStepIdPrefixFromParentActivityId(parentActivityId) + checkNotBlank(activityId);
    }

}
