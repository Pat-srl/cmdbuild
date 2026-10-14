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
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_READ;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_MODIFY;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_LIST;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_WRITE;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_CORE;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_SERVICE;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_UI;

public interface AttributePermissions {

    Map<PermissionScope, Set<AttributePermission>> getPermissionMap();

    default Set<AttributePermission> getPermissionsForScope(PermissionScope scope) {
        return checkNotNull(getPermissionMap().get(scope), "permissions not found for scope = %s", scope);
    }

    default boolean hasPermission(PermissionScope scope, AttributePermission permission) {
        return getPermissionsForScope(scope).contains(permission);
    }

    default void checkPermission(PermissionScope scope, AttributePermission permission) {
        checkArgument(hasPermission(scope, permission), "forbidden operation: missing permission %s.%s", scope, permission);
    }

    default boolean hasCorePermission(AttributePermission permission) {
        return hasPermission(PS_CORE, permission);
    }

    default boolean hasServicePermission(AttributePermission permission) {
        return hasPermission(PS_SERVICE, permission);
    }

    default boolean hasUiPermission(AttributePermission permission) {
        return hasPermission(PS_UI, permission);
    }

    default boolean hasCoreListPermission() {
        return hasPermission(PS_CORE, AP_LIST);
    }

    default boolean hasServiceListPermission() {
        return hasPermission(PS_SERVICE, AP_LIST);
    }

    default boolean hasNotServiceListPermission() {
        return !hasServiceListPermission();
    }

    default boolean hasServiceReadPermission() {
        return hasPermission(PS_SERVICE, AP_READ);
    }

    default boolean hasServiceModifyPermission() {
        return hasPermission(PS_SERVICE, AP_MODIFY);
    }

    default boolean hasServiceWritePermission() {
        return hasPermission(PS_SERVICE, AP_WRITE);
    }

    default boolean hasUiReadPermission() {
        return hasPermission(PS_UI, AP_READ);
    }

    default boolean hasUiModifyPermission() {
        return hasPermission(PS_UI, AP_MODIFY);
    }

}
