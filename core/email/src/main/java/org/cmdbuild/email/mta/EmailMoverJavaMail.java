/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Store;
import org.cmdbuild.debuginfo.InstanceInfoService;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailException;
import static org.cmdbuild.email.utils.EmailMtaUtils.getMessageInfoSafe;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.slf4j.Logger;

/**
 * Moves mail to another folder
 *
 * @author afelice
 */
public class EmailMoverJavaMail extends BaseEmailProvider implements EmailMoverProvider {

    private final Logger logger;
    private final static EmailProviderStrategy emailProviderStrategy = new EmailProviderStrategy();

    public EmailMoverJavaMail(EmailAccount emailAccount, Logger logger) {
        super(emailAccount);

        this.logger = logger;
    }

    void storeEmail(Email email, String storeToFolder, InstanceInfoService infoService) throws Exception {
        try (EmailImapSessionProvider imapSessionProvider = emailProviderStrategy.buildImapSessionProvider(emailAccount, logger)) {
            Message message = buildMessage(email, infoService, imapSessionProvider.getSession());
            logger.debug("open folder = {}", storeToFolder);
            try (Folder folder = imapSessionProvider.getStore().getFolder(checkNotBlank(storeToFolder))) {
                if (!folder.exists()) {
                    folder.create(Folder.HOLDS_MESSAGES);
                }
                folder.open(Folder.READ_WRITE);
                folder.appendMessages(new Message[]{message});
                message.setFlag(Flags.Flag.RECENT, true);
            }
        }
    }

    @Override
    public void moveToFolder(Message message, String targetFolderName) throws MessagingException {
        checkNotBlank(targetFolderName);
        try (EmailImapSessionProvider imapSessionProvider = emailProviderStrategy.buildImapSessionProvider(emailAccount, logger)) {
            logger.debug("moving message = {} from folder = {} to folder = {}", getMessageInfoSafe(message), message.getFolder(), targetFolderName);
            Folder source = checkNotNull(message.getFolder());
            try (Folder target = imapSessionProvider.getStore().getFolder(targetFolderName)) {
                if (!target.exists()) {
                    target.create(Folder.HOLDS_MESSAGES);
                }
                target.open(Folder.READ_WRITE);
                source.copyMessages(new Message[]{message}, target);
                source.setFlags(new Message[]{message}, new Flags(Flags.Flag.DELETED), true);
                source.expunge();
            }
        } catch (MessagingException ex) {
            throw new EmailException(ex, "error moving message = %s from folder = %s to folder = %s", getMessageInfoSafe(message), message.getFolder(), targetFolderName);
        }
    }

    @Override
    public void testConnection() {
        logger.debug("test imap connection");

        try (EmailImapSessionProvider imapSessionProvider = emailProviderStrategy.buildImapSessionProvider(emailAccount, logger)) {
            Store store = imapSessionProvider.getStore();
            checkArgument(store.isConnected(), "imap is not connected");
            Folder folder = store.getDefaultFolder();
            folder.list();
        } catch (MessagingException ex) {
            throw new EmailException(ex);
        }
        logger.debug("imap connection ok");
    }

}
