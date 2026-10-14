/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.inner;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import java.io.IOException;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import org.apache.commons.codec.digest.DigestUtils;
import org.cmdbuild.common.beans.CardIdAndClassName;
import static org.cmdbuild.common.beans.CardIdAndClassNameImpl.card;
import org.cmdbuild.config.EmailConfiguration;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import static org.cmdbuild.dao.entrytype.TextContentSecurity.TCS_HTML_SAFE;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.dms.DocumentDataImpl;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.email.Email;
import static org.cmdbuild.email.Email.EMAIL_CLASS_NAME;
import static org.cmdbuild.email.Email.NOTIFICATION_PROVIDER_EMAIL;
import org.cmdbuild.email.EmailAttachment;
import org.cmdbuild.email.EmailService;
import static org.cmdbuild.email.EmailStatus.ES_DRAFT;
import static org.cmdbuild.email.EmailStatus.ES_OUTGOING;
import org.cmdbuild.email.beans.EmailAttachmentImpl;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.data.EmailRepository;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.email.template.EmailTemplateProcessorService;
import org.cmdbuild.email.template.EmailTemplateService;
import static org.cmdbuild.email.utils.EmailMtaUtils.buildCmdbuildContentId;
import static org.cmdbuild.email.utils.EmailMtaUtils.convertEmailInlineHtmlAttachmentsToEmailAttachments;
import static org.cmdbuild.email.utils.EmailMtaUtils.renameDuplicates;
import org.cmdbuild.notification.NotificationProvider;
import org.cmdbuild.template.ExpressionInputData;
import org.cmdbuild.template.SimpleExpressionInputData;
import static org.cmdbuild.utils.html.HtmlSanitizerUtils.sanitizeHtmlForEmail;
import static org.cmdbuild.utils.io.CmIoUtils.isContentType;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.EventBusUtils.logExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailServiceImpl implements EmailService, NotificationProvider<Email> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EventBus eventBus = new EventBus(logExceptions(logger));
    private final EmailConfiguration emailConfiguration;
    private final EmailRepository repository;
    private final EmailTemplateService templateRepository;
    private final EmailTemplateProcessorService templateProcessorService;
    private final DmsService dmsService;

    public EmailServiceImpl(EmailConfiguration emailConfiguration, EmailRepository repository, EmailTemplateService templateRepository, EmailTemplateProcessorService templateProcessorService, DmsService dmsService) {
        this.emailConfiguration = checkNotNull(emailConfiguration);
        this.repository = checkNotNull(repository);
        this.templateRepository = checkNotNull(templateRepository);
        this.templateProcessorService = checkNotNull(templateProcessorService);
        this.dmsService = checkNotNull(dmsService);
    }

    @Override
    public String getNotificationProviderName() {
        return NOTIFICATION_PROVIDER_EMAIL;
    }

    @Override
    public Email sendNotification(Email notificationData) {
        return create(notificationData);
    }

    @Override
    public Email getLastReceivedEmail() {
        return repository.getLastReceivedEmail();
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public List<Email> getAllForCard(long reference, DaoQueryOptions queryOptions) {
        return repository.getAllForCard(reference, queryOptions);
    }

    @Override
    @Nullable
    public Email getOneOrNull(long emailId) {
        return repository.getOneOrNull(emailId);
    }

    @Override
    public List<Email> getAllForTemplate(long templateId) {
        return repository.getAllForTemplate(templateId);
    }

    @Override
    public List<Email> getByMessageId(String messageId) {
        return repository.getByMessageId(messageId);
    }

    @Override
    @Nullable
    public Email getLastWithReferenceBySenderAndSubjectFuzzyMatchingOrNull(String from, String subject) {
        return repository.getLastWithReferenceBySenderAndSubjectFuzzyMatchingOrNull(from, subject);
    }

    @Override
    public Email create(Email email) {
        checkIsProviderEmail(email);
        logger.debug("create email = {}", email);
        email = sanitizeOutgoingEmail(email);
        return switch (email.getStatus()) {
            case ES_ACQUIRED ->
                repository.create(email);
            default -> {
                Email saved = repository.create(EmailImpl.copyOf(email).withStatus(ES_DRAFT).build());
                if (email.hasAttachments()) {
                    saved = EmailImpl.copyOf(saved).withAttachments(email.getAttachments()).build();
                    saveEmailAttachments(saved);
                }
                if (!equal(saved.getStatus(), email.getStatus())) {
                    saved = repository.update(EmailImpl.copyOf(email).withId(saved.getId()).build());
                    if (email.hasAttachments()) {
                        saved = EmailImpl.copyOf(saved).withAttachments(email.getAttachments()).build();
                    }
                    checkOutgoing(saved);
                }
                yield saved;
            }
        };
    }

    @Override
    public Email update(Email email) {
        checkIsProviderEmail(email);
        logger.debug("update email = {}", email);
        email = sanitizeOutgoingEmail(email);
        email = repository.update(email);
        saveEmailAttachments(email);
        checkOutgoing(email);
        return email;
    }

    @Override
    public void delete(Email email) {
        checkIsProviderEmail(email);
        logger.debug("delete email = {}", email);
        repository.delete(email);
    }

    @Override
    public Email applyTemplate(Email email, Card clientCard, Card serverCard) {
        return innerApplyEmailTemplate(email, clientCard, serverCard);
    }

    @Override
    public Email applyTemplate(Email email) {
        return innerApplyEmailTemplate(email, null, null);
    }

    @Override
    public String applyTemplateExpr(Long templateId, String expr, Card clientCard, Card serverCard) {
        EmailTemplate template = templateRepository.getById(checkNotNull(templateId, "email template id cannot be null"));
        return templateProcessorService.processExpression(SimpleExpressionInputData.extendedBuilder()
                .withTemplate(template)
                .withClientCard(clientCard)
                .withServerCard(serverCard)
                .withExpression(expr)
                .build());
    }

    @Override
    public Email applySysTemplate(Email email, String sysTemplateId) {
        EmailTemplate template = templateRepository.getSystemTemplate(sysTemplateId);
        return innerApplyWithTemplate(email, template, null);
    }

    @Override
    public Email applyTemplate(Email email, EmailTemplate template, Map<String, Object> data) {
        return innerApplyWithTemplate(email, template, data);
    }

    @Override
    public Email applyTemplate(Email email, String templateCode, Map<String, Object> data) {
        return applyTemplate(email, templateRepository.getByName(templateCode), data);
    }

    @Override
    public List<Email> getAllForOutgoingProcessing() {
        return repository.getAllForOutgoingProcessing();
    }

    @Override
    public List<Email> getAllForErrorProcessing() {
        return repository.getAllForErrorProcessing();
    }

    @Override
    public List<EmailAttachment> getAllEmailAttachments(Email email) {
        if (dmsService.isEnabled()) {
            return list(getDmsEmailAttachments(email.getId())).with(getEmbeddedEmailAttachments(email));
        } else {
            return emptyList();
        }
    }

    @Override
    public Email loadEmailAttachments(Email email) {
        if (email.getId() == null) {
            return email;
        } else {
            return EmailImpl.copyOf(email).withAttachments(getAllEmailAttachments(email)).build();
        }
    }

    @Override
    public void saveEmailAttachments(Email email) {
        saveEmailAttachments(email, false);
    }

    @Override
    public void saveEmailAttachments(Email email, boolean excludeAlreadyUploaded) {
        if (!email.getAttachments().isEmpty()) {
            logger.debug("save email attachments");
            checkArgument(dmsService.isEnabled(), "dms service not enabled, unable to process email attachments!");
            CardIdAndClassName card = card(EMAIL_CLASS_NAME, email.getId());
            List<DocumentInfoAndDetail> cardInfoAttachments = dmsService.getCardAttachments(card);
            logger.debug("[{}] email attachments", cardInfoAttachments.size());

            if (excludeAlreadyUploaded) {
                List<DataSource> emailAttachments = list(cardInfoAttachments).map(d -> dmsService.download(d.getDocumentId(), null).getDataSource());
                List<String> sha1HexAttachments = emailAttachments.stream().map(a -> {
                    try {
                        return DigestUtils.sha1Hex(a.getInputStream());
                    } catch (IOException ex) {
                        logger.error("error generating md5 for {}", a.getName());
                        return null;
                    }
                }).filter(Objects::nonNull).toList();
                email = EmailImpl.copyOf(email).withAttachments(email.getAttachments().stream().filter(a -> {
                    if (sha1HexAttachments.contains(DigestUtils.sha1Hex(a.getData()))) {
                        logger.debug("attachment {} is already uploaded", a.getFileName());
                        return false;
                    } else {
                        return true;
                    }
                }).toList()).build();
            }

            renameDuplicates(list(cardInfoAttachments).map(DocumentInfoAndDetail::getFileName), email.getAttachments()).forEach(a -> dmsService.create(card, DocumentDataImpl.builder().withFilename(a.getFileName()).withData(a.getData()).build()));//TODO merge/update attachments (?)
            logger.debug("email attachments saved");
        }
    }

    /**
     * Template to apply get from given email.
     *
     * @param email
     * @param clientCard
     * @param serverCard
     * @return
     */
    private Email innerApplyEmailTemplate(Email email,
            Card clientCard, Card serverCard) {
        logger.debug("apply template for email = {}", email);
        checkArgument(email.hasTemplate(), "unable to sync email without template");
        EmailTemplate template = templateRepository.getById(email.getTemplate());

        return templateProcessorService.processEmail(email,
                ExpressionInputData.builder()
                        .withClientCard(clientCard)
                        .withServerCard(serverCard)
                        .withTemplate(template)
                        .build());
    }

    /**
     * Template to apply given.
     *
     * @param email
     * @param template
     * @param data
     * @return
     */
    private Email innerApplyWithTemplate(Email email,
            EmailTemplate template, Map<String, Object> data) {
        logger.debug("apply template for email = {}", email);

        return templateProcessorService.processEmail(email,
                ExpressionInputData.builder()
                        .withTemplate(template)
                        .withOtherData(data)
                        .build());
    }

    private List<EmailAttachment> getDmsEmailAttachments(long emailId) {
        return list(dmsService.getCardAttachments(EMAIL_CLASS_NAME, emailId)).map(a -> {
            byte[] documentBytes = dmsService.getDocumentBytes(a.getDocumentId());
            return EmailAttachmentImpl.builder()
                    .withFileName(a.getFileName())
                    .withContentType(a.getMimeType())
                    .withData(documentBytes)
                    .withContentId(buildCmdbuildContentId(documentBytes))
                    .build();
        });
    }

    private List<EmailAttachment> getEmbeddedEmailAttachments(Email email) {
        List<EmailAttachment> embeddedAttachments = convertEmailInlineHtmlAttachmentsToEmailAttachments(email.getContent());
        saveEmailAttachments(EmailImpl.copyOf(email).withAttachments(embeddedAttachments).build());
        return embeddedAttachments;
    }

    private void checkIsProviderEmail(Email email) {
        checkArgument(email.hasNotificationProvider(NOTIFICATION_PROVIDER_EMAIL), "invalid notification provider for email = %s", email);
    }

    private void checkOutgoing(Email email) {
        if (equal(email.getStatus(), ES_OUTGOING)) {
            logger.debug("outgoing email processed, trigger email queue (email = {})", email);
            eventBus.post(NewOutgoingEmailEvent.INSTANCE);
        }
    }

    private Email sanitizeOutgoingEmail(Email email) {
        return switch (email.getStatus()) {
            case ES_OUTGOING, ES_DRAFT ->
                EmailImpl.copyOf(email).accept(b -> {
                    if (emailConfiguration.hasDefaultTextContentSecurity(TCS_HTML_SAFE) && isContentType(email.getContentType(), "text/html") && email.hasContent()) {//TODO check this
                        b.withContent(sanitizeHtmlForEmail(email.getContent()));
                    }
                }).build();
            default ->
                email;
        };
    }

}
