/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.script;

import static com.google.common.base.Strings.nullToEmpty;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class CmScriptUtils {

    public static final String SCRIPT_OUTPUT_VAR = "output";

    public static String normalizeClassName(String key) {
        return checkNotBlank(nullToEmpty(key).replaceAll("[^a-zA-Z0-9]+", "_").replaceAll("(^_|_$)", ""));
    }
}
