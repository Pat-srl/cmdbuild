/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email;

import jakarta.annotation.Nullable;
import org.cmdbuild.notification.NotificationCommonData;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;

public interface EmailCommonData extends NotificationCommonData {

    @Nullable
    String getFrom();

    @Nullable
    String getCc();

    @Nullable
    String getBcc();

    @Nullable
    Long getAccount();

    boolean getKeepSynchronization();

    boolean getPromptSynchronization();

    @Nullable
    Long getDelay();

    @Nullable
    Long getSignature();

    default boolean hasNegativeDelay() {
        return getDelay() != null && getDelay() < 0;
    }

    default boolean hasSignature() {
        return isNotNullAndGtZero(getSignature());
    }

    default boolean hasAccount() {
        return getAccount() != null;
    }
}
