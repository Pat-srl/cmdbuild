/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.lock;

import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;

public class LockScopeUtils {

    public static LockScope parseLockScope(String scope) {
        return parseEnum(scope, LockScope.class);
    }

    public static String serializeLockScope(LockScope scope) {
        return serializeEnum(scope);
    }

}
