/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader;

import java.util.Collection;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;

public interface EtlNotification {

    String getTemplate();

    @Nullable
    String getAccount();

    EtlNotificationEvent getEvent();

    default boolean hasEvent(Collection<EtlNotificationEvent> events) {
        return events.contains(getEvent());
    }

    default boolean hasAccount() {
        return isNotBlank(getAccount());
    }

    enum EtlNotificationEvent {
        EN_ALWAYS, EN_SUCCESS, EN_ERROR
    }

}
