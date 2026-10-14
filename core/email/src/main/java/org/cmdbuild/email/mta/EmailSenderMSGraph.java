/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.getLast;
import com.microsoft.graph.core.exceptions.ClientException;
import com.microsoft.graph.core.models.IProgressCallback;
import com.microsoft.graph.core.requests.GraphClientFactory;
import com.microsoft.graph.core.tasks.LargeFileUploadTask;
import com.microsoft.graph.models.*;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.item.messages.item.attachments.createuploadsession.CreateUploadSessionPostRequestBody;
import com.microsoft.kiota.ApiException;
import com.microsoft.kiota.RequestAdapter;
import com.microsoft.kiota.authentication.AnonymousAuthenticationProvider;
import com.microsoft.kiota.http.OkHttpRequestAdapter;
import jakarta.mail.MessagingException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.String.format;
import java.lang.reflect.InvocationTargetException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import okhttp3.OkHttpClient;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.debuginfo.InstanceInfoService;
import org.cmdbuild.email.*;
import static org.cmdbuild.email.template.EmailUtils.parseEmailHeaderToken;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.io.CmStreamProgressUtils.progressDescription;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmConcurrentUtils.CmLazyInitializer;
import static org.cmdbuild.utils.lang.CmConcurrentUtils.lazyInitializer;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import org.slf4j.LoggerFactory;

/**
 *
 * @author afelice
 */
public class EmailSenderMSGraph extends BaseEmailSenderProvider {

    protected static final int SEND_MSG_TIMEOUT_SECONDS = 30;

    protected final EmailMSGraphClientProvider emailMsGraphClientProvider;
    protected final CmLazyInitializer<GraphServiceClient> lazyMsGraphClient;

    public EmailSenderMSGraph(EmailAccount emailAccount, EmailMSGraphClientProvider emailMsGraphClientProvider, InstanceInfoService infoService, EmailSignatureService signatureService) {
        super(emailAccount, infoService, signatureService, LoggerFactory.getLogger(EmailSenderMSGraph.class));
        this.emailMsGraphClientProvider = emailMsGraphClientProvider;

        lazyMsGraphClient = lazyInitializer(() -> this.emailMsGraphClientProvider.create());

        logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public Email sendEmail(Email email) throws MessagingException {
        checkArgument(email.hasDestinationAddress(), "invalid email: no destination address found (TO, CC or BCC)");
        Email emailToSend = prepareEmail(email);

        GraphServiceClient msGraphClient = lazyMsGraphClient.get();
        try (this.emailMsGraphClientProvider) {
            logger.debug("send email = {}", emailToSend);
            com.microsoft.graph.models.Message messageMSGraph = convertMessageToMessageGraph(emailToSend, infoService);
            logger.debug("send message = {}", getMessageInfoSafe(messageMSGraph));

            logger.info("handling email attachments");
            final String sendingAddress = emailAccount.getAddress();

            messageMSGraph = handleAttachments(emailToSend, messageMSGraph, msGraphClient, sendingAddress); // throws ClientException

            logger.info("sending message");

            Message finalMessageMSGraph = messageMSGraph;
            msGraphClient.users()
                    .byUserId(sendingAddress)
                    .messages()
                    .byMessageId(finalMessageMSGraph.getId())
                    .send()
                    .post();

            logger.debug("sent message");

            Message messageSent = msGraphClient.users().byUserId(sendingAddress).messages().byMessageId(messageMSGraph.getId())
                    .get(requestConfig
                            -> requestConfig.queryParameters.select = new String[]{"id", "parentFolderId", "internetMessageId", "internetMessageHeaders"}
                    );

            String messageId = checkNotBlank(parseEmailHeaderToken(getMessageId(messageSent)), "error: message sent, but message id header is null");
            String rawHeaders = serializeMessageHeaders(emailToSend, messageSent);
            final ZonedDateTime sentDateTime = toDateTime(messageSent.getSentDateTime());

            return buildMessageSent(emailToSend, sentDateTime, messageId, rawHeaders);
        } catch (ClientException | ApiException ex) {
            throw new EmailException(ex, "error creating imap session for account = %s", emailAccount);
        }
    } // end sendMail method

    @Override
    public void testConnection() {
        emailMsGraphClientProvider.testConnection();
    }

    private Message handleAttachments(Email email, Message messageToSend, GraphServiceClient msGraphClient, final String sendingAddress) throws ClientException {
        Message draftMessage;
        if (email.hasAttachments() && email.getAttachments().stream().mapToLong(a -> a.getData().length).sum() < 3 * FileUtils.ONE_MB) {
            messageToSend.setAttachments(buildSmallAttachments(email.getAttachments()));
            draftMessage = draftMessage(msGraphClient, sendingAddress, messageToSend);
        } else if (email.hasAttachments()) {
            draftMessage = draftMessage(msGraphClient, sendingAddress, messageToSend); // Mandatory before uploading large attachments
            uploadLargeAttachments(msGraphClient, sendingAddress, email.getAttachments(), draftMessage.getId());
        } else {
            draftMessage = draftMessage(msGraphClient, sendingAddress, messageToSend);
        }
        return draftMessage;
    }

    private Message draftMessage(GraphServiceClient msGraphClient, final String sendingAddress, Message messageToSend) {
        return msGraphClient.users().byUserId(sendingAddress).messages().post(messageToSend);
    }

    private com.microsoft.graph.models.Message convertMessageToMessageGraph(Email email, InstanceInfoService infoService) throws MessagingException {
        try {
            com.microsoft.graph.models.Message messageMSGraph = new com.microsoft.graph.models.Message();

            // setting references and in-reply-to
            List<SingleValueLegacyExtendedProperty> extendedProperties = calculateReferencesReply(email);
            if (!extendedProperties.isEmpty()) {
                messageMSGraph.setSingleValueExtendedProperties(extendedProperties);
            }

            // setting headers
            messageMSGraph.setInternetMessageHeaders(buildHeaders(email, infoService));

            // setting body
            ItemBody body = new ItemBody();
            body.setContentType(fetchContentType(email.getContentType()));
            body.setContent(email.getContent());

            if (email.getFirstFromAddressOrNull() != null) {
                Recipient recipient = buildRecipient(email.getFirstFromAddressOrNull());
                messageMSGraph.setFrom(recipient);
            }
            addMailDestinations(messageMSGraph, email);
            messageMSGraph.setSubject(email.getSubject());
            messageMSGraph.setBody(body);

            return messageMSGraph;
        } catch (EmailException ex) {
            throw new EmailException(ex, "error converting message to ms graph");
        }
    }

    private List<InternetMessageHeader> buildHeaders(Email email, InstanceInfoService infoService) {
        List<InternetMessageHeader> headersList = list();
        headersList.add(buildHeader("X-Mailer", format("CMDBuild v%s", infoService.getVersion())));
        headersList.add(buildHeader("X-CMDBuild-Version", infoService.getVersion()));
        headersList.add(buildHeader("X-CMDBuild-Revision", infoService.getRevision()));
        headersList.add(buildHeader("X-CMDBuild-InstanceInfo", infoService.getInstanceInfo()));
        if (email.hasId()) {
            headersList.add(buildHeader("X-CMDBuild-EmailId", toStringNotBlank(email.getId())));
        }
        return headersList;
    }

    private InternetMessageHeader buildHeader(String name, String value) {
        InternetMessageHeader header = new InternetMessageHeader();
        header.setName(name);
        header.setValue(value);
        return header;
    }

    private void addMailDestinations(Message messageMSGraph, Email email) {
        messageMSGraph.setToRecipients(email.getToEmailAddressList().stream().map(this::buildRecipient).collect(toList()));
        messageMSGraph.setCcRecipients(email.getCcEmailAddressList().stream().map(this::buildRecipient).collect(toList()));
        messageMSGraph.setBccRecipients(email.getBccEmailAddressList().stream().map(this::buildRecipient).collect(toList()));
    }

    private Recipient buildRecipient(String e) {
        Recipient recipient = new Recipient();
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setAddress(checkNotBlank(e));
        recipient.setEmailAddress(emailAddress);
        return recipient;
    }

    private com.microsoft.graph.models.BodyType fetchContentType(String emailContentType) {
        return switch (emailContentType) {
            case "text/plain" ->
                BodyType.Text;
            case "text/html" ->
                BodyType.Html;
            default ->
                BodyType.Text;
        };
    }

    private String serializeMessageHeaders(Email email, com.microsoft.graph.models.Message messageMSGraph) {
        if (messageMSGraph.getInternetMessageHeaders() != null) {
            Map<String, String> mapiProperty = map("String 0x1039", "References", "String 0x1042", "In-Reply-To");
            return messageMSGraph.getInternetMessageHeaders().stream().map(h -> format("%s: %s", h.getName(), nullToEmpty(h.getValue()))).collect(joining("\n"))
                    .concat("\n")
                    .concat(calculateReferencesReply(email).stream().map(h -> format("%s: %s", mapiProperty.get(h.getId()), nullToEmpty(h.getValue()))).collect(joining("\n")));
        } else {
            return "";
        }
    }

    private List<SingleValueLegacyExtendedProperty> calculateReferencesReply(Email email) {
        List<SingleValueLegacyExtendedProperty> extendedProperties = list();
        String inReplyTo = email.getInReplyTo();
        List<String> references = email.getReferences();
        if (isNotBlank(inReplyTo) && (references.isEmpty() || !equal(getLast(references), inReplyTo))) {
            references = list(references).with(inReplyTo);
        }
        if (!references.isEmpty()) {
            SingleValueLegacyExtendedProperty header = new SingleValueLegacyExtendedProperty();
            header.setId("String 0x1039");
            header.setValue(references.stream().collect(joining(" ")));
            extendedProperties.add(header);
        }
        if (isNotBlank(inReplyTo)) {
            SingleValueLegacyExtendedProperty header = new SingleValueLegacyExtendedProperty();
            header.setId("String 0x1042");
            header.setValue(inReplyTo);
            extendedProperties.add(header);
        }
        return extendedProperties;
    }

    private String getMessageId(com.microsoft.graph.models.Message messageMSGraph) {
        return messageMSGraph.getInternetMessageId();
    }

    private String getMessageInfoSafe(com.microsoft.graph.models.Message messageMSGraph) {
        try {
            return toStringHelper(messageMSGraph)
                    .add("id", messageMSGraph.getInternetMessageId())
                    .add("subject", nullToEmpty(messageMSGraph.getSubject()))
                    .add("from", messageMSGraph.getFrom() == null ? "" : messageMSGraph.getFrom().getEmailAddress())
                    .add("to", messageMSGraph.getToRecipients() == null ? "" : messageMSGraph.getToRecipients().stream().map(to -> to.getEmailAddress().getAddress()).collect(joining(","))).toString();
        } catch (Exception ex) {
            logger.warn("unable to get message infos for log message", ex);
            return messageMSGraph.toString();
        }
    }

    /**
     * Add directly as file attachments to mail
     *
     * @param attachments
     * @return
     */
    private List<Attachment> buildSmallAttachments(List<EmailAttachment> attachments) {
        logger.debug("uploading small attachments");

        return attachments.stream().map(this::buildFileAttachment).collect(Collectors.toList());
    }

    private FileAttachment buildFileAttachment(EmailAttachment a) {
        FileAttachment fileAttachment = new FileAttachment();

        fileAttachment.setName(a.getFileName());
        fileAttachment.setContentId(a.getContentId());
        fileAttachment.setContentType(a.getContentType());
        fileAttachment.setContentBytes(a.getData());
        fileAttachment.setOdataType("#microsoft.graph.fileAttachment");
        fileAttachment.setIsInline(equal(a.getContentDisposition(), "inline"));
        return fileAttachment;
    }

    /**
     * Upload attachments, in chunk, to previously sent message
     *
     * @param msGraphClient
     * @param emailAddress
     * @param attachments
     * @param messageMSGraphId
     */
    private void uploadLargeAttachments(GraphServiceClient msGraphClient, String emailAddress, List<EmailAttachment> attachments, String messageMSGraphId) {
        logger.debug("uploading large attachments");
        attachments.forEach(a -> {
            try {
                AttachmentItem attachmentItem = new AttachmentItem();
                attachmentItem.setAttachmentType(AttachmentType.File);
                attachmentItem.setName(a.getFileName());
                attachmentItem.setSize((long) a.getData().length);
                attachmentItem.setContentType("application/octet-stream");

                CreateUploadSessionPostRequestBody uploadSessionRequest = new CreateUploadSessionPostRequestBody();
                uploadSessionRequest.setAttachmentItem(attachmentItem);

                UploadSession uploadSession = msGraphClient.users()
                        .byUserId(emailAddress)
                        .messages()
                        .byMessageId(messageMSGraphId)
                        .attachments()
                        .createUploadSession()
                        .post(uploadSessionRequest);

                try (InputStream inputStream = new ByteArrayInputStream(a.getData())) {

                    OkHttpClient anonymousHttpClient = GraphClientFactory.create().build();
                    RequestAdapter anonymousAdapter = new OkHttpRequestAdapter(new AnonymousAuthenticationProvider(), null, null, anonymousHttpClient);

                    LargeFileUploadTask<AttachmentItem> chunkedUploadProvider = new LargeFileUploadTask<>(
                            anonymousAdapter,
                            uploadSession,
                            inputStream,
                            attachmentItem.getSize(),
                            AttachmentItem::createFromDiscriminatorValue
                    );

                    IProgressCallback callback = (current, max) -> logger.debug("uploading file {}, {} of {}", attachmentItem.getName(), progressDescription(current, max), FileUtils.byteCountToDisplaySize(attachmentItem.getSize()));

                    chunkedUploadProvider.upload(10, callback);
                }
            } catch (IOException | InterruptedException | InvocationTargetException | IllegalAccessException | NoSuchMethodException ex) {
                throw new EmailException(ex, "error uploading attachment %s", a.getFileName());
            }
        });
    }

}
