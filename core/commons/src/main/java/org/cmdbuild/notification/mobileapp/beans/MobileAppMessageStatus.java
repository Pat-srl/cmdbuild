/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.notification.mobileapp.beans;

import org.cmdbuild.notification.NotificationStatus;
import static org.cmdbuild.notification.NotificationStatus.NS_SENT;

public enum MobileAppMessageStatus {
    MAMS_OUTGOING, MAMS_NEW, MAMS_ARCHIVED, MAMS_ERROR;

    public NotificationStatus toNotificationStatus() {
        return switch (this) {
            case MAMS_ERROR ->
                NotificationStatus.NS_ERROR;
            case MAMS_OUTGOING ->
                NotificationStatus.NS_OUTGOING;
            case MAMS_NEW ->
                NotificationStatus.NS_SENT;
            case MAMS_ARCHIVED ->
                NotificationStatus.NS_ARCHIVED;
        };
    }

    public static MobileAppMessageStatus toMessageStatus(NotificationStatus notificationStatus) {
        return switch (notificationStatus) {
            case NS_ERROR ->
                MAMS_ERROR;
            case NS_OUTGOING ->
                MAMS_OUTGOING;
            case NS_SENT ->
                MAMS_NEW;
            case NS_ARCHIVED ->
                MAMS_ARCHIVED;
        };
    }
}
