/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.providers;


import org.cmdbuild.common.utils.FilteringOptions;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessage;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessageData;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationData;
import org.cmdbuild.plugin.notification.mobileapp.MobileAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;

/**
 * @author ldare
 */
public class DummyMobileAppService implements MobileAppService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String getName() {
        return "dummy-mobile-service";
    }

    @Override
    public PagedElements<MobileAppMessage> getMessagesForCurrentUser(FilteringOptions options) {
        return throwError("trying to get mobile app notification msg, but mobile is disabled");
    }

    @Override
    public MobileAppMessage sendMessage(MobileAppNotificationData notification) {
        return throwError("trying to send mobile app notification msg, but mobile is disabled");
    }

    @Override
    public MobileAppMessage sendMessage(MobileAppMessageData message) {
        return throwError("trying to send mobile app notification msg, but mobile is disabled");
    }

    @Override
    public boolean releaseSender(MobileAppNotificationData mobileAppNotificationData) {
        return throwError("trying to release mobile app notification sender, but mobile is disabled");
    }

    @Override
    public void archiveMessagesForCurrentUser(List<Long> recordIds) {
        throwError("trying to archive mobile app notification message, but mobile is disabled");
    }

    @Override
    public void deleteMessagesForCurrentUser(List<Long> recordIds) {
        throwError("trying to delete mobile app notification message, but mobile is disabled");
    }

    private <T> T throwError(String errMsg) throws UnsupportedOperationException {
        logger.warn(marker(), errMsg);
        throw new UnsupportedOperationException(errMsg);
    }
}
