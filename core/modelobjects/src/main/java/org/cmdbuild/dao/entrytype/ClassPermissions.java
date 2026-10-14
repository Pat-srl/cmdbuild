/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import java.util.Set;
import org.cmdbuild.auth.grant.GrantAttributePrivilege;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.GAP_DEFAULT;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.GAP_READ;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.GAP_WRITE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_WRITE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_READ;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_MODIFY;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_LIST;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_CORE;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_SERVICE;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_UI;

public interface ClassPermissions {

    Map<PermissionScope, Set<ClassPermission>> getPermissionsMap();

    Map<String, Object> getOtherPermissions();

    Map<String, Set<GrantAttributePrivilege>> getDmsPermissions();

    Map<String, Set<GrantAttributePrivilege>>   getGisPermissions();

    default Set<ClassPermission> getPermissionsForScope(PermissionScope scope) {
        return checkNotNull(getPermissionsMap().get(scope), "permissions not found for scope = %s", scope);
    }

    default boolean hasDmsPermission(String categoryValue, GrantAttributePrivilege permission) {
        return getDmsPermissions().get(categoryValue).contains(permission);
    }

    default boolean hasGisPermission(String gisAttributeName, GrantAttributePrivilege permission) {
        return getGisPermissions().get(gisAttributeName).contains(permission);
    }

    default boolean hasPermission(PermissionScope scope, ClassPermission permission) {
        return getPermissionsForScope(scope).contains(permission);
    }

    default void checkPermission(PermissionScope scope, ClassPermission permission) {
        checkArgument(hasPermission(scope, permission), "forbidden operation: missing permission %s.%s", scope, permission);
    }

    default void checkServicePermission(ClassPermission permission) {
        checkPermission(PS_SERVICE, permission);
    }

    default boolean hasCoreReadPermission() {
        return hasPermission(PS_CORE, CP_READ);
    }

    default boolean hasCoreWritePermission() {
        return hasPermission(PS_CORE, CP_WRITE);
    }

    default boolean hasServiceListPermission() {
        return hasPermission(PS_SERVICE, CP_LIST);
    }

    default boolean hasServicePermission(ClassPermission permission) {
        return hasPermission(PS_SERVICE, permission);
    }

    default boolean hasServiceReadPermission() {
        return hasPermission(PS_SERVICE, CP_READ);
    }

    default boolean hasServiceWritePermission() {
        return hasPermission(PS_SERVICE, CP_WRITE);
    }

    default boolean hasServiceModifyPermission() {
        return hasPermission(PS_SERVICE, CP_MODIFY);
    }

    default boolean hasUiModifyPermission() {
        return hasPermission(PS_UI, CP_MODIFY);
    }

    default boolean hasUiWritePermission() {
        return hasPermission(PS_UI, CP_WRITE);
    }

    default boolean hasUiPermission(ClassPermission permission) {
        return hasPermission(PS_UI, permission);
    }

    default boolean hasDmsCategoryWritePermission(String categoryValue) {
        if (getDmsPermissions().get(categoryValue) != null) {
            if (hasDmsPermission(categoryValue, GAP_DEFAULT)) {
                return hasServiceWritePermission();
            } else {
                return hasDmsPermission(categoryValue, GAP_WRITE);
            }
        } else {
            return hasServiceWritePermission();
        }
    }

    default boolean hasDmsCategoryReadPermission(String categoryValue) {
        if (getDmsPermissions().get(categoryValue) != null) {
            if (hasDmsPermission(categoryValue, GAP_DEFAULT)) {
                return hasServiceReadPermission();
            } else {
                return hasDmsPermission(categoryValue, GAP_READ);
            }
        } else {
            return hasServiceReadPermission();
        }
    }

    default boolean hasDmsCategoryDefaultPermission(String categoryValue) {
        return hasDmsPermission(categoryValue, GAP_DEFAULT);
    }

    default boolean hasGisAttributeWritePermission(String gisAttributeName) {
        if (getGisPermissions().get(gisAttributeName) != null) {
            if (hasGisPermission(gisAttributeName, GAP_DEFAULT)) {
                return hasServiceWritePermission();
            } else {
                return hasGisPermission(gisAttributeName, GAP_WRITE);
            }
        } else {
            return hasServiceWritePermission();
        }
    }

    default boolean hasGisAttributeReadPermission(String gisAttributeName) {
        if (getGisPermissions().get(gisAttributeName) != null) {
            if (hasGisPermission(gisAttributeName, GAP_DEFAULT)) {
                return hasServiceReadPermission();
            } else {
                return hasGisPermission(gisAttributeName, GAP_READ);
            }
        } else {
            return hasServiceReadPermission();
        }
    }

}
