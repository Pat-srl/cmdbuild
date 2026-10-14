/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.notification.mobileapp.beans;

import static com.google.common.base.Objects.equal;
import java.time.ZonedDateTime;
import jakarta.annotation.Nullable;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppMessageStatus.MAMS_ARCHIVED;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppMessageStatus.MAMS_NEW;

/**
 * Additional infos to be stored in repository
 *
 * @author afelice
 */
public interface MobileAppMessage extends MobileAppMessageData {

    @Nullable
    Long getId();

    String getMessageId();

    ZonedDateTime getTimestamp();

    MobileAppMessageSourceType getSourceType();

    String getSourceName();

    String getSourceDescription();

    MobileAppMessageStatus getStatus();

    default boolean isNewMessage() {
        return equal(getStatus(), MAMS_NEW);
    }

    default boolean isArchived() {
        return equal(getStatus(), MAMS_ARCHIVED);
    }
}
