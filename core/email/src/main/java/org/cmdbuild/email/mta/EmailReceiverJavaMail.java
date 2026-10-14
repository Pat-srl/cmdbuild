/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Store;
import java.util.Collections;
import java.util.List;
import static java.util.stream.Collectors.joining;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailException;
import org.cmdbuild.email.data.EmailRepository;
import org.cmdbuild.lock.LockService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author afelice
 */
public class EmailReceiverJavaMail extends BaseEmailReceiverProvider {

    public EmailReceiverJavaMail(EmailAccount emailAccount, LockService lockService, EmailRepository repository) {
        super(emailAccount, lockService, repository, LoggerFactory.getLogger(EmailReceiverJavaMail.class));
        this.logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public void receiveMails(EmailReceiveConfig receiveConfig) throws MessagingException {
        try (EmailReaderLocker readerLocker = new EmailReaderLocker(emailAccount, lockService); EmailImapSessionProvider imapSession = buildEmailImapSessionProvider(emailAccount, logger)) {
            String incomingFolder = receiveConfig.getIncomingFolder();
            logger.debug("open incoming folder = {}", incomingFolder);
            try (Folder folder = imapSession.getStore().getFolder(checkNotBlank(incomingFolder))) {
                checkArgument(folder.exists(), "incoming folder not found for name = %s; available folders = %s", incomingFolder, lazyString(rethrowSupplier(() -> list(imapSession.getStore().getDefaultFolder().list()).stream().map(Folder::getName).collect(joining(", ")))));
                folder.open(Folder.READ_WRITE);
                List<Message> messages = list(folder.getMessages());
                if (messages.isEmpty()) {
                    logger.debug("no message received for account = {} folder = {}", emailAccount, folder);
                } else {
                    logger.debug("processing {} incoming messages for account = {} folder = {}", messages.size(), emailAccount, folder);
                    Collections.shuffle(messages);//process messages in random order to avoid a single problematic message to block all the others
                    EmailMoverProvider emailMover = new EmailMoverJavaMail(
                            emailAccount, logger);
                    messages.forEach((message) -> this.processMessageSafe(message, emailMover, receiveConfig));
                    logger.info("processed {} incoming messages for account = {} folder = {}", messages.size(), emailAccount, folder);
                }
            }
        }
    }

    @Override
    public void testConnection() {
        logger.info("test imap connection for account = {}", emailAccount);

        try (EmailImapSessionProvider imapSessionProvider = buildEmailImapSessionProvider(emailAccount, logger)) {
            Store store = imapSessionProvider.getStore();
            checkArgument(store.isConnected(), "imap is not connected");
            Folder folder = store.getDefaultFolder();
            folder.list();
            logger.info("imap connection ok");
        } catch (MessagingException ex) {
            throw new EmailException(ex);
        }
    }

    protected EmailImapSessionProvider buildEmailImapSessionProvider(EmailAccount emailAccount, Logger logger) {
        return new EmailImapSessionJavaMailProvider(emailAccount, logger);
    }

}
