/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.role;

import org.cmdbuild.auth.role.RoleType;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;

public class RoleTypeUtils {

    public static String serializeRoleType(RoleType roleType) {
        return roleType.name().toLowerCase().replaceFirst("^rt_", "");
    }

    public static RoleType parseRoleType(String value) {
        return parseEnum(value, RoleType.class);
    }

}
