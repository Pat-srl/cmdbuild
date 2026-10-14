/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.common.utils;

import static com.google.common.base.Strings.nullToEmpty;
import jakarta.annotation.Nullable;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_ADMIN;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_SYSTEM;

public class WsRequestUtils {

    public static boolean isAdminViewMode(@Nullable String viewModeParam) {
        return nullToEmpty(viewModeParam).equalsIgnoreCase(VIEW_MODE_ADMIN);
    }

    public static boolean isSystemViewMode(@Nullable String viewModeParam) {
        return nullToEmpty(viewModeParam).equalsIgnoreCase(VIEW_MODE_SYSTEM);
    }

}
