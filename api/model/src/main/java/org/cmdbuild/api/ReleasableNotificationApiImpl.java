/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.api;

import jakarta.annotation.Nullable;
import org.cmdbuild.plugin.notification.mobileapp.MobileAppNotificationSender;
import org.springframework.stereotype.Component;

/**
 * Methods on notifications
 *
 * @author afelice
 */
@Component
public class ReleasableNotificationApiImpl implements ReleasableNotificationApi {

    private final MobileAppNotificationSender mobileAppNotificationSender;

    public ReleasableNotificationApiImpl(@Nullable MobileAppNotificationSender mobileAppNotificationSender) {
        this.mobileAppNotificationSender = mobileAppNotificationSender;
    }

    @Override
    public void releaseMobileAppResources() {
        if (mobileAppNotificationSender != null) {
            mobileAppNotificationSender.releaseAll();
        }
    }
}
