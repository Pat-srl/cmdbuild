/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import com.microsoft.graph.models.MailFolder;
import com.microsoft.graph.models.MailFolderCollectionResponse;
import com.microsoft.graph.models.MessageCollectionResponse;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.InputStream;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.joining;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailException;
import org.cmdbuild.email.data.EmailRepository;
import static org.cmdbuild.email.mta.EmailMoverMSGraph.MS_WELL_KNOWN_FOLDER_NAMES;
import org.cmdbuild.lock.LockService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowSupplier;
import org.slf4j.LoggerFactory;

/**
 *
 * @author afelice
 */
public class EmailReceiverMSGraph extends BaseEmailReceiverProvider {

    protected final EmailMSGraphClientProvider emailMsGraphClientProvider;

    public EmailReceiverMSGraph(EmailAccount emailAccount, EmailMSGraphClientProvider emailMsGraphClientProvider, LockService lockService, EmailRepository repository) {
        super(emailAccount, lockService, repository, LoggerFactory.getLogger(EmailReceiverMSGraph.class));
        this.emailMsGraphClientProvider = emailMsGraphClientProvider;

        this.logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public void receiveMails(EmailReceiveConfig receiveConfig) throws MessagingException {
        final String sendingAddress = emailAccount.getAddress();

        GraphServiceClient msGraphClient = this.emailMsGraphClientProvider.create();
        try (this.emailMsGraphClientProvider; EmailReaderLocker readerLocker = new EmailReaderLocker(emailAccount, lockService);) {
            Map<String, String> foldersByName = map();

            MS_WELL_KNOWN_FOLDER_NAMES.forEach(f -> {
                MailFolder mailFolder = msGraphClient.users().byUserId(sendingAddress)
                        .mailFolders().byMailFolderId(f).get();
                foldersByName.put(mailFolder.getDisplayName(), mailFolder.getId());
                foldersByName.put(f, mailFolder.getId());
            });
            MailFolderCollectionResponse folderPage = msGraphClient.users().byUserId(sendingAddress).mailFolders().get();
            if (folderPage != null && folderPage.getValue() != null) {
                folderPage.getValue().forEach(f -> foldersByName.put(f.getDisplayName(), f.getId()));
            }
            String incomingFolder = receiveConfig.getIncomingFolder();
            logger.debug("open incoming folder = {}", incomingFolder);
            checkArgument(foldersByName.containsKey(incomingFolder), "incoming folder not found for name = %s; available folders = %s", incomingFolder, lazyString(rethrowSupplier(() -> list(foldersByName.keySet()).collect(joining(", ")))));

            String folderId = foldersByName.get(incomingFolder);
            MessageCollectionResponse messagesPage = msGraphClient.users().byUserId(sendingAddress).messages()
                    .get(requestConfig -> {
                        requestConfig.queryParameters.filter = format("parentFolderId eq '%s'", folderId);
                        requestConfig.queryParameters.select = new String[]{"parentFolderId", "id"};
                        requestConfig.headers.add("Prefer", "outlook.body-content-type=\"text\"");
                    });

            while (messagesPage != null) {
                List<com.microsoft.graph.models.Message> messages = messagesPage.getValue();

                if (messages == null || messages.isEmpty()) {
                    logger.debug("no message received for account = {} folder = {}", emailAccount, incomingFolder);
                    break;
                } else {
                    logger.debug("processing {} incoming messages for account = {} folder = {}", messages.size(), emailAccount, incomingFolder);
//                    Collections.shuffle(messages);
                    messages.forEach(m -> processMessageSafe(m, msGraphClient, receiveConfig));

                    String nextLink = messagesPage.getOdataNextLink();
                    if (nextLink == null) {
                        break;
                    } else {
                        messagesPage = msGraphClient.users().byUserId(sendingAddress).messages()
                                .withUrl(nextLink)
                                .get(requestConfig
                                        -> requestConfig.headers.add("Prefer", "outlook.body-content-type=\"text\"")
                                ); // Re-add the header to subsequent requests
                    }
                    logger.info("processed {} incoming messages for account = {} folder = {}", messages.size(), emailAccount, incomingFolder);
                }
            } // end while
        }
    } // end receiveMails() method

    @Override
    public void testConnection() {
        emailMsGraphClientProvider.testConnection();
    }

    private void processMessageSafe(com.microsoft.graph.models.Message messageMSGraph, GraphServiceClient msGraphClient, EmailReceiveConfig receiveConfig) {
        Message message = convertMimeToMessage(msGraphClient.users().byUserId(emailAccount.getAddress()).messages().byMessageId(messageMSGraph.getId()).content().get());

        EmailMoverMSGraph emailMover = new EmailMoverMSGraph(emailAccount, msGraphClient, messageMSGraph, logger);
        processMessageSafe(message, emailMover, receiveConfig);
    }

    private static Message convertMimeToMessage(InputStream mimeMessage) {
        try {
            return new MimeMessage(null, mimeMessage);
        } catch (MessagingException ex) {
            throw new EmailException(ex, "error loading mime message from ms graph");
        }
    }

}
