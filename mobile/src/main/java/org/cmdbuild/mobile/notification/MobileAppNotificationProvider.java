/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.mobile.notification;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.notification.NotificationProvider;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessageDataImpl;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationData;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationHelper;
import org.cmdbuild.plugin.notification.mobileapp.MobileAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author afelice
 */
@Component
public class MobileAppNotificationProvider implements NotificationProvider<MobileAppNotificationData> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MobileAppService mobileAppService;

    public MobileAppNotificationProvider(MobileAppService mobileAppService) {
        this.mobileAppService = checkNotNull(mobileAppService);
    }

    @Override
    public String getNotificationProviderName() {
        return MobileAppNotificationHelper.NOTIFICATION_PROVIDER_MOBILE_APP;
    }

    @Override
    public MobileAppNotificationData sendNotification(MobileAppNotificationData notificationData) {
        return MobileAppMessageDataImpl.copyNotificationDataOf(mobileAppService.sendMessage(notificationData)).build();
    }

    public boolean releaseSender(MobileAppNotificationData mobileAppNotificationData) {
        return mobileAppService.releaseSender(mobileAppNotificationData);
    }
}
