/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.mobile;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import static org.cmdbuild.auth.AuthConst.SYSTEM_USER;
import static org.cmdbuild.auth.login.GodUserUtils.isGodUser;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.common.utils.FilteringOptions;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.config.MobileConfiguration;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.mobile.MobileAppMessageImpl.MobileAppMessageImplBuilder;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessage;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessageData;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessageDataImpl;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessageSourceType;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppMessageSourceType.MAMST_SYSTEM;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppMessageSourceType.MAMST_USER;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppMessageStatus.MAMS_ARCHIVED;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppMessageStatus.MAMS_ERROR;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppMessageStatus.MAMS_NEW;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppMessageStatus.MAMS_OUTGOING;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppMessageStatus.toMessageStatus;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationData;
import org.cmdbuild.plugin.notification.mobileapp.MobileAppNotificationSender;
import org.cmdbuild.plugin.notification.mobileapp.MobileAppService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MobileAppServiceImpl implements MobileAppService {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final Map<String, MobileAppNotificationSender> mobileAppNotificationSenders;
    private final MobileConfiguration mobileConfiguration;
    private final MobileAppMessageRepository repository;
    private final OperationUserSupplier operationUser;
    
    public MobileAppServiceImpl(List<MobileAppNotificationSender> mobileNotificationSenders, MobileConfiguration mobileConfiguration, MobileAppMessageRepository repository, OperationUserSupplier operationUser) {
        this.mobileAppNotificationSenders = Maps.uniqueIndex(checkNotNull(mobileNotificationSenders), MobileAppNotificationSender::getSenderName);
        this.mobileConfiguration = checkNotNull(mobileConfiguration);
        this.repository = checkNotNull(repository);
        this.operationUser = checkNotNull(operationUser);
    }
    
    @Override
    public String getName() {
        return "MobileApp-Service";
    }
    
    public MobileAppNotificationSender getSenderService() {
        return mobileAppNotificationSenders.get("Firebase");
        // @todo handle configuration #7648
        // return checkNotNull(mobileAppNotificationSenders.get(config.getService()), "mobileApp notification sender not found for name = %s", config.getService());
    }
    
    @Override
    public PagedElements<MobileAppMessage> getMessagesForCurrentUser(FilteringOptions options) {
        String username = operationUser.getUsername();
        return repository.getMessagesForUser(username, (DaoQueryOptions) options);
    }
    
    @Override
    public MobileAppMessage sendMessage(MobileAppNotificationData notification) {
        return sendMessage(MobileAppMessageImpl.copyMessageDataOf(notification).withSourceType(MAMST_SYSTEM).withSourceName(SYSTEM_USER).build()); //setting default sender user
    }
    
    @Override
    public MobileAppMessage sendMessage(MobileAppMessageData message) {
        MobileAppMessage messageWithSender = buildToSendMessage(MobileAppMessageImpl.copyOf(message)).build();
        boolean allMessagesSent = MobileAppNotificationData.parseTopicListAsStrings(message.getTarget()).stream().map(m -> {
            // fetch source name and source description from operation user
            MobileAppMessage outMessage = MobileAppMessageImpl.copyOf(messageWithSender).withTarget(m).build(); //load sender user from session
            if (!mobileConfiguration.isMobileEnabled()) {
                logger.warn("trying to send mobile app notification msg, but mobile is disabled");
                outMessage = MobileAppMessageImpl.copyOf(outMessage).withStatus(MAMS_ERROR).build();
                if (!outMessage.isVolatile()) {
                    outMessage = repository.createMessage(outMessage);
                }
                return outMessage;
            }

            // setting message to outgoing status
            outMessage = MobileAppMessageImpl.copyOf(outMessage).withStatus(MAMS_OUTGOING).build();
            // store in repository
            if (!outMessage.isVolatile()) {
                outMessage = repository.createMessage(outMessage);
                logger.debug("created outgoing mobilApp message = {}", outMessage);
            } else {
                logger.debug("built outgoing message (volatile) = {}", outMessage);
            }

            // add authInfo & Send notification
            String authInfo = mobileConfiguration.getMobileNotificationAuthInfo();
            checkNotNull(authInfo, "unable to retrieve mobile authentication info");
            MobileAppNotificationData mobileAppNotificationData = MobileAppMessageDataImpl.copyNotificationDataOf(outMessage).withAuthInfo(authInfo).withMeta(map("messageId", outMessage.getMessageId())).build();
            MobileAppNotificationData resultData = getSenderService().sendNotification(mobileAppNotificationData);

            // Adjust result status based on firebase response
            outMessage = MobileAppMessageImpl.copyOf(outMessage).withStatus(toMessageStatus(resultData.getStatus())).build();
            if (!outMessage.isVolatile()) {
                outMessage = repository.updateMessage(outMessage);
            }
            
            return outMessage.getStatus();
        }).allMatch(s -> equal(s, MAMS_NEW));
        return MobileAppMessageImpl.copyOf(messageWithSender).withStatus(allMessagesSent ? MAMS_NEW : MAMS_ERROR).build();
    }
    
    @Override
    public void archiveMessagesForCurrentUser(List<Long> recordIds) {
        list(recordIds).map(repository::getMessageByRecordId).forEach(m -> {
            checkCurrentUserHasWriteAccess(m);
            repository.updateMessage(MobileAppMessageImpl.copyOf(m).withStatus(MAMS_ARCHIVED).build());
        });
    }
    
    @Override
    public void deleteMessagesForCurrentUser(List<Long> recordIds) {
        list(recordIds).map(repository::getMessageByRecordId).forEach(m -> {
            checkCurrentUserHasWriteAccess(m);
            repository.deleteMessage(m);
        });
    }
    
    @Override
    public boolean releaseSender(MobileAppNotificationData mobileAppNotificationData) {
        return getSenderService().release(mobileAppNotificationData);
    }
    
    private void checkCurrentUserHasWriteAccess(MobileAppMessage message) {
        checkArgument(message.getTarget().contains(operationUser.getUsername()) && equal(message.getSourceType(), MAMST_USER),
                "current user does not have access to mobileApp message = %s", message);
    }
    
    private MobileAppMessageImplBuilder buildToSendMessage(MobileAppMessageImplBuilder messageBuilder) {
        // Calculate additional data to be stored
        // Similar to what done in ChatServiceImpl.sendMessage() and sendMessageAs()
        MobileAppMessageSourceType sourceType = (isGodUser(operationUser.getUsername()) || operationUser.getUser().getLoginUser().isService()) ? MAMST_SYSTEM : MAMST_USER;
        String sourceName = operationUser.getUsername();
        String sourceDescription = operationUser.getUser().getLoginUser().getDescription();
        
        messageBuilder.withSourceType(sourceType).withSourceName(sourceName).withSourceDescription(sourceDescription).withStatus(MAMS_OUTGOING);
        
        return messageBuilder;
    }
    
}
