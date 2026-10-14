/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.chat;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static org.cmdbuild.auth.login.GodUserUtils.isGodUser;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.auth.user.OperationUserSupplier;
import static org.cmdbuild.chat.ChatMessageSourceType.CMST_SYSTEM;
import static org.cmdbuild.chat.ChatMessageSourceType.CMST_USER;
import static org.cmdbuild.chat.ChatMessageStatus.CMS_ARCHIVED;
import static org.cmdbuild.chat.ChatMessageStatus.CMS_NEW;
import static org.cmdbuild.chat.ChatMessageType.CMT_INCOMING;
import static org.cmdbuild.chat.ChatMessageType.CMT_OUTGOING;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.event.EventService;
import static org.cmdbuild.event.RawEvent.ALERT_LEVEL;
import static org.cmdbuild.event.RawEvent.ALERT_LEVEL_SYSTEM;
import static org.cmdbuild.event.RawEvent.ALERT_MESSAGE;
import static org.cmdbuild.event.RawEvent.ALERT_MESSAGE_SHOW_USER;
import static org.cmdbuild.event.RawEvent.EVENT_CODE_ALERT;
import static org.cmdbuild.event.RawEvent.EVENT_CODE_CHAT;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeLocal;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChatServiceImpl implements ChatService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ChatMessageRepository repository;
    private final OperationUserSupplier operationUser;
    private final EventService eventService;
    private final SessionService sessionService;

    public ChatServiceImpl(ChatMessageRepository repository, OperationUserSupplier operationUser, EventService eventService, SessionService sessionService) {
        this.repository = checkNotNull(repository);
        this.operationUser = checkNotNull(operationUser);
        this.eventService = checkNotNull(eventService);
        this.sessionService = checkNotNull(sessionService);
    }

    @Override
    public PagedElements<ChatMessage> getMessagesForCurrentUser(DaoQueryOptions options) {
        String username = operationUser.getUsername();
        return repository.getMessagesForUser(username, options);
    }

    @Override
    public ChatMessage sendMessage(ChatMessageData messageData) {
        return sendMessageAs(messageData, (isGodUser(operationUser.getUsername()) || operationUser.getUser().getLoginUser().isService()) ? CMST_SYSTEM : CMST_USER, operationUser.getUsername(), operationUser.getUser().getLoginUser().getDescription());
    }

    @Override
    public ChatMessage sendMessageAs(ChatMessageData messageData, ChatMessageSourceType sourceType, String sourceName, String sourceDescription) {
        return sendMessageAs(messageData, sourceType, sourceName, sourceDescription, sessionService.getAllSessions());
    }

    @Override
    public ChatMessage sendMessageAs(ChatMessageData messageData, ChatMessageSourceType sourceType, String sourceName, String sourceDescription, List<Session> sessions) {
        ChatMessage outMessage = ChatMessageImpl.copyOf(messageData)
                .withSourceType(sourceType).withSourceName(sourceName).withSourceDescription(sourceDescription)
                .withType(CMT_OUTGOING).withStatus(CMS_ARCHIVED).build();
        if (!outMessage.isVolatile()) {
            if (equal(sourceType, CMST_USER)) {
                outMessage = repository.createMessage(outMessage);
                logger.debug("created outgoing message = {}", outMessage);
            } else {
                logger.debug("built outgoing system message (outgoing system messages are not persisted) = {}", outMessage);
            }
        } else {
            logger.debug("built outgoing message (volatile) = {}", outMessage);
        }
        ChatMessage inMessage = ChatMessageImpl.copyOf(outMessage).withType(CMT_INCOMING).withStatus(CMS_NEW).build();
        if (!inMessage.isVolatile()) {
            inMessage = repository.createMessage(inMessage);
            logger.debug("created incoming message = {}", inMessage);
        } else {
            logger.debug("built incoming message (volatile) = {}", inMessage);
        }
        sendMessageNotification(inMessage, sessions);
        return outMessage;
    }

    @Override
    public void archiveMessagesForCurrentUser(List<Long> recordIds) {
        list(recordIds).map(repository::getMessageByRecordId).forEach(m -> {
            checkCurrentUserHasWriteAccess(m);
            repository.updateMessage(ChatMessageImpl.copyOf(m).withStatus(CMS_ARCHIVED).build());
        });
    }

    @Override
    public void deleteMessagesForCurrentUser(List<Long> recordIds) {
        list(recordIds).map(repository::getMessageByRecordId).forEach(m -> {
            checkCurrentUserHasWriteAccess(m);
            repository.deleteMessage(m);
        });
    }

    private void checkCurrentUserHasWriteAccess(ChatMessage message) {
        checkArgument((equal(message.getTarget(), operationUser.getUsername()) && equal(message.getType(), CMT_INCOMING))
                || (equal(message.getSourceName(), operationUser.getUsername()) && equal(message.getSourceType(), CMST_USER) && equal(message.getType(), CMT_OUTGOING)),
                "current user does not have access to message = %s", message);
    }

    private void sendMessageNotification(ChatMessage message, List<Session> sessions) {
        list(sessions).filter(s -> equal(s.getOperationUser().getUsername(), message.getTarget())).forEach(s -> {
            logger.debug("send message = {} to session =< {} >", message, s.getSessionId());
            eventService.sendEventMessage(s.getSessionId(), switch (message.getSourceType()) {
                case CMST_SYSTEM ->
                    EVENT_CODE_ALERT;
                case CMST_USER ->
                    EVENT_CODE_CHAT;
            }, map(
                    ALERT_LEVEL, ALERT_LEVEL_SYSTEM,
                    ALERT_MESSAGE, message.getContent(),
                    ALERT_MESSAGE_SHOW_USER, true,
                    "subject", message.getSubject(),
                    "content", message.getContent(),
                    "_id", message.getId(),
                    "messageId", message.getMessageId(),
                    "meta", message.getMeta(),
                    "subject", message.getSubject(),
                    "target", message.getTarget(),
                    "thread", message.getThread(),
                    "sourceType", serializeEnum(message.getSourceType()),
                    "sourceName", message.getSourceName(),
                    "sourceDescription", message.getSourceDescription(),
                    "timestamp", toIsoDateTimeLocal(message.getTimestamp()),
                    "type", serializeEnum(message.getType()),
                    "status", serializeEnum(message.getStatus()),
                    "_isNew", message.isNewMessage()
            ));
        });
    }

}
