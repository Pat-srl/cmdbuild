/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import com.microsoft.graph.models.MailFolder;
import com.microsoft.graph.models.MailFolderCollectionResponse;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.item.messages.item.move.MovePostRequestBody;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import org.slf4j.Logger;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.email.utils.EmailMtaUtils.getMessageInfoSafe;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toInverseMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 * Moves mail to another folder in a MS email account, using MSGraph library
 *
 * @author afelice
 */
public class EmailMoverMSGraph implements EmailMoverProvider {

    public static final CmCollectionUtils.FluentList<String> MS_WELL_KNOWN_FOLDER_NAMES = list("inbox", "drafts", "outbox");

    private final Logger logger;

    private final EmailAccount emailAccount;
    private final GraphServiceClient msGraphClient;
    private final com.microsoft.graph.models.Message messageMSGraph;

    public EmailMoverMSGraph(EmailAccount emailAccount, GraphServiceClient msGraphClient, com.microsoft.graph.models.Message messageMSGraph, Logger logger) {
        this.logger = logger;

        this.emailAccount = emailAccount;
        this.msGraphClient = msGraphClient;
        this.messageMSGraph = messageMSGraph;
    }

    @Override
    public void moveToFolder(Message message, String targetFolderName) throws MessagingException {
        checkNotBlank(targetFolderName);
        // @todo può essere unificato parzialmente con EmailReceiverMSGraph.receiveMails()
        Map<String, String> foldersById = map();
        MS_WELL_KNOWN_FOLDER_NAMES.forEach(f -> {
            MailFolder mailFolder = msGraphClient.users().byUserId(emailAccount.getAddress()).mailFolders().byMailFolderId(f).get();
            foldersById.put(mailFolder.getId(), mailFolder.getDisplayName());
        });
        MailFolderCollectionResponse folderPage = msGraphClient.users().byUserId(emailAccount.getAddress()).mailFolders().get();
        if (folderPage != null && folderPage.getValue() != null) {
            folderPage.getValue().forEach(f -> foldersById.put(f.getId(), f.getDisplayName()));
        }

        String source = checkNotNull(foldersById.get(messageMSGraph.getParentFolderId()));
        logger.debug("moving message = {} from folder = {} to folder = {}", getMessageInfoSafe(message), source, targetFolderName);
        String targetFolderNameId = toInverseMap(foldersById).get(checkNotBlank(targetFolderName));
        if (targetFolderNameId == null) {
            MailFolder mailFolder = new MailFolder();
            mailFolder.setDisplayName(targetFolderName);
            targetFolderNameId = msGraphClient.users().byUserId(emailAccount.getAddress()).mailFolders().post(mailFolder).getId();
        }

        // #7176 -- MS Graph - Read mail has to be marked as "read"
        setMessageAsRead(emailAccount, messageMSGraph.getId());

        msGraphClient.users().byUserId(emailAccount.getAddress()).messages().byMessageId(messageMSGraph.getId()).move().post(buildDestinationParameters(targetFolderNameId));
    }

    protected static MovePostRequestBody buildDestinationParameters(String targetFolderNameId) {
        MovePostRequestBody body = new MovePostRequestBody();
        body.setDestinationId(targetFolderNameId);
        return body;
    }

    private void setMessageAsRead(EmailAccount emailAccount, String messageId) {
        com.microsoft.graph.models.Message messageIsRead = new com.microsoft.graph.models.Message();
        messageIsRead.setIsRead(true);
        msGraphClient.users().byUserId(emailAccount.getAddress()).messages().byMessageId(messageId).patch(messageIsRead);
    }
}
