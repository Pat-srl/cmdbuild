/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.plugin.notification.mobileapp;

import java.util.List;
import org.cmdbuild.common.utils.FilteringOptions;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessage;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessageData;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationData;
import org.cmdbuild.plugin.PluginService;

public interface MobileAppService extends PluginService {

    final String MOBILE_APP_NOTIFICATION_SOURCE_TYPE = "mam_mobile_app_sourceType";

    PagedElements<MobileAppMessage> getMessagesForCurrentUser(FilteringOptions options);

    MobileAppMessage sendMessage(MobileAppNotificationData notification);

    MobileAppMessage sendMessage(MobileAppMessageData message);

    boolean releaseSender(MobileAppNotificationData mobileAppNotificationData);

    void archiveMessagesForCurrentUser(List<Long> recordIds);

    void deleteMessagesForCurrentUser(List<Long> recordIds);

}
