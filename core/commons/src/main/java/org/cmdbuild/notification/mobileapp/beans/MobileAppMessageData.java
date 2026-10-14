/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.notification.mobileapp.beans;

import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface MobileAppMessageData {

    final String MOBILE_APP_MESSAGE_ATTR_TARGET = "Target",
            MOBILE_APP_MESSAGE_ATTR_SOURCE_NAME = "SourceName",
            MOBILE_APP_MESSAGE_ATTR_SOURCE_TYPE = "SourceType",
            MOBILE_APP_MESSAGE_ATTR_TIMESTAMP = "Timestamp",
            MOBILE_APP_MESSAGE_ATTR_STATUS = "MessageStatus",
            MOBILE_APP_MESSAGE_META_VOLATILE = "mam_message_volatile";

    String getTarget();

    @Nullable
    String getSubject();

    @Nullable
    String getContent();

    Map<String, String> getMeta();

    @Nullable
    default String getMeta(String key) {
        return getMeta().get(checkNotBlank(key));
    }

    default boolean isVolatile() {
        return toBooleanOrDefault(getMeta(MOBILE_APP_MESSAGE_META_VOLATILE), false);
    }

}
