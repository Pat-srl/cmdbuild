/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import jakarta.ws.rs.core.Response;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.config.MobileConfiguration;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessage;
import org.cmdbuild.plugin.notification.mobileapp.MobileAppService;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.model.WsMobileAppMessageData;
import org.cmdbuild.service.rest.v4.providers.DummyMobileAppService;
import org.springframework.stereotype.Component;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppMessageData.*;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppMessageStatus.MAMS_ARCHIVED;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.failure;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
@Component
public class MobileAppMessageWsCommand {

    private final MobileConfiguration mobileConfiguration;
    private final MobileAppService mobileAppService;

    public MobileAppMessageWsCommand(MobileAppService mobileAppService, MobileConfiguration mobileConfiguration) {
        this.mobileAppService = checkNotNull(mobileAppService);
        this.mobileConfiguration = checkNotNull(mobileConfiguration);
    }

    public PagedElements<MobileAppMessage> doGetMessages(WsQueryOptions options) {
        return getService().getMessagesForCurrentUser(options.getQuery().mapAttrNames(map(
                "sourceType", MOBILE_APP_MESSAGE_ATTR_SOURCE_TYPE,
                "status", MOBILE_APP_MESSAGE_ATTR_STATUS,
                "timestamp", MOBILE_APP_MESSAGE_ATTR_TIMESTAMP,
                "sourceName", MOBILE_APP_MESSAGE_ATTR_SOURCE_NAME,
                "target", MOBILE_APP_MESSAGE_ATTR_TARGET
        )));
    }

    public MobileAppMessage doSendMessage(WsMobileAppMessageData message) {
        return getService().sendMessage(message.toMobileAppMessageData());
    }

    public void doUpdateMessage(Long recordId, WsMobileAppMessageData message) {
        checkArgument(equal(message.getStatus(), MAMS_ARCHIVED));
        getService().archiveMessagesForCurrentUser(list(recordId));
    }

    public void doDeleteMessage(Long recordId) {
        getService().deleteMessagesForCurrentUser(list(recordId));
    }

    public Response doFailureWith(String errMsg) {
        return Response.status(Response.Status.BAD_REQUEST).entity(failure().with("messages", errMsg)).build();
    }

    /**
     * This can't be done in class constructor since configuration is not
     * already correctly initialized. So a dynamic initialization, in each
     * endpoint, is done.
     */
    private MobileAppService getService() {
        if (mobileConfiguration.isMobileEnabled()) {
            return checkNotNull(mobileAppService);
        } else {
            return new DummyMobileAppService();
        }
    }
}
