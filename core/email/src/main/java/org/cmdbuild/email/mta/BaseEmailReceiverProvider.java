/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import com.google.common.base.Joiner;
import static com.google.common.base.MoreObjects.toStringHelper;
import jakarta.mail.Flags;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import static java.lang.String.format;
import java.util.function.Consumer;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.data.EmailRepository;
import static org.cmdbuild.email.mta.EmailMtaServiceImpl.EMAIL_HEADER_MESSAGE_ID;
import static org.cmdbuild.email.template.EmailUtils.parseEmailHeaderToken;
import org.cmdbuild.email.utils.EmailMtaUtils;
import org.cmdbuild.email.utils.MessageParser;
import org.cmdbuild.lock.LockService;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmStringUtils.nullToEmpty;
import org.slf4j.Logger;

/**
 *
 * @author afelice
 */
public abstract class BaseEmailReceiverProvider extends BaseEmailProvider implements EmailReceiverProvider {

    protected Logger logger;

    protected final LockService lockService;
    protected final EmailRepository repository;

    public BaseEmailReceiverProvider(EmailAccount emailAccount, LockService lockService, EmailRepository repository, Logger logger) {
        super(emailAccount);
        this.lockService = lockService;
        this.repository = checkNotNull(repository);

        this.logger = logger;
    }

    protected void processMessageSafe(Message message, EmailMoverProvider emailMover, EmailReceiveConfig receiveConfig) {
        Email email = null;
        try {
            //            logger.debug("download message = {}", getMessageInfoSafe(message));//TODO prefetch message info attrs (??)
            //            BigByteArray data = toBigByteArray(message.getDataHandler());
            //            logger.debug("acquired message data = {}", byteCountToDisplaySize(data.length()));
            //            Message offlineMessage = new MimeMessage(session, data.toInputStream());
            //            Message offlineMessage =message;
            logger.debug("preprocess message = {}", getMessageInfoSafe(message));
            email = EmailImpl.builder().accept(emailAcquirer(message)).withAccount(emailAccount.getId()).build();
            logger.debug("store raw email = {}", email);
            email = repository.create(email);
            logger.debug("processing email = {}", email);
            email = EmailImpl.copyOf(email).accept(emailParser(message)).build();
            EmailProcessedAction action = checkNotNull(receiveConfig.getCallback().apply(email));
            // (eventually) move to received folder; or delete it
            postProcessActionSafe(action, email, message, emailMover, receiveConfig);
        } catch (Exception ex) {
            logger.warn(marker(), "error processing message = {} email = {}", getMessageInfoSafe(message), email, ex);
            if (receiveConfig.hasRejectedFolder()) {
                postProcessActionSafe(EmailProcessedAction.EPA_MOVE_TO_REJECTED, email, message, emailMover, receiveConfig);
            }
        }
    }

    private void postProcessActionSafe(EmailProcessedAction action, Email email, Message message, EmailMoverProvider emailMover, EmailReceiveConfig receiveConfig) {
        try {
            postProcessAction(action, email, message, emailMover, receiveConfig);
        } catch (Exception ex) {
            logger.warn("failed to apply action = {} to message = {}, will retry later", action, email, ex);
        }
    }

    private void postProcessAction(EmailProcessedAction action, Email email, Message message, EmailMoverProvider emailMover, EmailReceiveConfig receiveConfig) throws Exception {
        logger.debug("execute post process action = {} with default action = {} for email = {}", serializeEnum(action), serializeEnum(receiveConfig.getReceivedEmailAction()), email);
        switch (action) {
            case EPA_DEFAULT -> {
                // Default: pick action from receive configuration
                switch (receiveConfig.getReceivedEmailAction()) {
                    case ERA_DELETE ->
                        postProcessAction(EmailProcessedAction.EPA_DELETE, email, message, emailMover, receiveConfig);
                    case ERA_MOVE_TO_RECEIVED ->
                        postProcessAction(EmailProcessedAction.EPA_MOVE_TO_PROCESSED, email, message, emailMover, receiveConfig);
                    case ERA_DO_NOTHING ->
                        postProcessAction(EmailProcessedAction.EPA_DO_NOTHING, email, message, emailMover, receiveConfig);
                    default ->
                        postProcessAction(EmailProcessedAction.EPA_DO_NOTHING, email, message, emailMover, receiveConfig);
                }
            }
            case EPA_DELETE -> {
                logger.debug("delete message = {}", getMessageInfoSafe(message));
                message.setFlag(Flags.Flag.DELETED, true);
                message.getFolder().expunge();
            }
            case EPA_DO_NOTHING ->
                logger.debug("leave message = {} (note: may be processed again)", getMessageInfoSafe(message));
            case EPA_MOVE_TO_PROCESSED ->
                emailMover.moveToFolder(message, checkNotBlank(receiveConfig.getReceivedFolder()));
            case EPA_MOVE_TO_REJECTED ->
                emailMover.moveToFolder(message, checkNotBlank(receiveConfig.getRejectedFolder()));
            default ->
                throw new IllegalArgumentException(format("unsupported epa action =< %s >", action));
        }
    }

    private Consumer<EmailImpl.EmailImplBuilder> emailAcquirer(Message message) {
        return new MessageParser(message).acquireEmail();
    }

    private Consumer<EmailImpl.EmailImplBuilder> emailParser(Message message) {
        return new MessageParser(message).parseEmail();
    }

    private String getMessageInfoSafe(Message message) {
        try {
            return toStringHelper(message)
                    .add("id", parseEmailHeaderToken(EmailMtaUtils.getMessageHeader(message, EMAIL_HEADER_MESSAGE_ID)))
                    .add("subject", nullToEmpty(message.getSubject()))
                    .add("from", message.getFrom() == null ? "" : Joiner.on(",").join(message.getFrom()))
                    .add("to", message.getAllRecipients() == null ? "" : Joiner.on(",").join(message.getAllRecipients()))
                    .toString();
        } catch (MessagingException ex) {
            logger.warn("unable to get message infos for log message", ex);
            return message.toString();
        }
    }

} // end BaseEmailReceiverProvider class
