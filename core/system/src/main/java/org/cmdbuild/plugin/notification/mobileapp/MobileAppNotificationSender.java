/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.plugin.notification.mobileapp;

import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationData;

/**
 *
 * @author afelice
 */
public interface MobileAppNotificationSender {

    String getSenderName();

    MobileAppNotificationData sendNotification(MobileAppNotificationData mobileAppNotificationData);

    /**
     * Releases Sender resources for auth info given
     *
     * @param mobileAppNotificationData
     * @return <code>true</code> if something released
     */
    boolean release(MobileAppNotificationData mobileAppNotificationData);

    /**
     * Releases all Sender resources
     */
    void releaseAll();

}
