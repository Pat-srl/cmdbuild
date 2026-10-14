/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.notification;

import static com.google.common.base.Objects.equal;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface NotificationCommonData {

    @Nullable
    Long getId();    
    
    @Nullable
    String getTo();

    @Nullable
    String getSubject();

    @Nullable
    String getContent();

    String getContentType();

    String getNotificationProvider();

    Map<String, String> getMeta();

    @Nullable
    default String getMeta(String key) {
        return getMeta().get(checkNotBlank(key));
    }
    
    default boolean hasId() {
        return isNotNullAndGtZero(getId());
    }

    default boolean hasNotificationProvider(String provider) {
        return equal(checkNotBlank(provider), getNotificationProvider());
    }
}
