/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import org.cmdbuild.api.fluent.MailApi;
import org.cmdbuild.api.fluent.NewMail;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.email.EmailSignatureService;
import static org.cmdbuild.email.EmailStatus.ES_DRAFT;
import static org.cmdbuild.email.EmailStatus.ES_OUTGOING;
import org.cmdbuild.email.beans.EmailAttachmentImpl;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.beans.EmailImpl.EmailImplBuilder;
import org.cmdbuild.notification.NotificationCommonData;
import org.cmdbuild.notification.NotificationService;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.urlToDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.toListOrEmpty;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltEqZeroToNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class LocalMailApiImpl implements MailApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final NotificationService notificationService;
    private final EmailService emailService;
    private final EmailAccountService accountService;
    private final EmailSignatureService signatureService;

    public LocalMailApiImpl(NotificationService notificationService, EmailService emailService, EmailAccountService accountService, EmailSignatureService signatureService) {
        this.notificationService = checkNotNull(notificationService);
        this.emailService = checkNotNull(emailService);
        this.accountService = checkNotNull(accountService);
        this.signatureService = checkNotNull(signatureService);
    }

    @Override
    public NewMail newMail() {
        return new SendableNewMailImpl();
    }

    private class SendableNewMailImpl implements NewMail {

        private EmailImplBuilder email = EmailImpl.builder();

        @Override
        public NewMail withProvider(String notificationProvider) {
            email.withNotificationProvider(notificationProvider);
            return this;
        }

        @Override
        public NewMail withMeta(String key, String value) {
            email.withMeta(map(key, value));
            return this;
        }

        @Override
        public NewMail withFrom(String from) {
            email.withFrom(from);
            return this;
        }

        @Override
        public NewMail withTo(String to) {
            email.addToAddress(to);
            return this;
        }

        @Override
        public NewMail withTo(String... tos) {
            email.addToAddresses(toListOrEmpty(tos));
            return this;
        }

        @Override
        public NewMail withTo(Iterable<String> tos) {
            email.addToAddresses(toListOrEmpty(tos));
            return this;
        }

        @Override
        public NewMail withCc(String cc) {
            email.addCcAddress(cc);
            return this;
        }

        @Override
        public NewMail withCc(String... ccs) {
            email.addCcAddresses(toListOrEmpty(ccs));
            return this;
        }

        @Override
        public NewMail withCc(Iterable<String> ccs) {
            email.addCcAddresses(toListOrEmpty(ccs));
            return this;
        }

        @Override
        public NewMail withBcc(String bcc) {
            email.addBccAddress(bcc);
            return this;
        }

        @Override
        public NewMail withBcc(String... bccs) {
            email.addBccAddresses(toListOrEmpty(bccs));
            return this;
        }

        @Override
        public NewMail withBcc(Iterable<String> bccs) {
            email.addBccAddresses(toListOrEmpty(bccs));
            return this;
        }

        @Override
        public NewMail withSubject(String subject) {
            email.withSubject(subject);
            return this;
        }

        @Override
        public NewMail withContent(String content) {
            email.withContent(content);
            return this;
        }

        @Override
        public NewMail withContentType(String contentType) {
            email.withContentType(contentType);
            return this;
        }

        @Override
        public NewMail withAttachment(URL url) {
            email.addAttachment(EmailAttachmentImpl.build(urlToDataSource(url)));
            return this;
        }

        @Override
        public NewMail withAttachment(URL url, String name) {
            email.addAttachment(EmailAttachmentImpl.copyOf(urlToDataSource(url)).withFileName(name).build());
            return this;
        }

        @Override
        public NewMail withAttachment(String url) {
            email.addAttachment(EmailAttachmentImpl.build(urlToDataSource(url)));
            return this;
        }

        @Override
        public NewMail withAttachment(String url, String name) {
            email.addAttachment(EmailAttachmentImpl.copyOf(urlToDataSource(url)).withFileName(name).build());
            return this;
        }

        @Override
        public NewMail withAttachment(DataHandler dataHandler) {
            email.addAttachment(EmailAttachmentImpl.build(toDataSource(dataHandler)));
            return this;
        }

        @Override
        public NewMail withAttachment(DataHandler dataHandler, String name) {
            email.addAttachment(EmailAttachmentImpl.copyOf(toDataSource(dataHandler)).withFileName(name).build());
            return this;
        }

        @Override
        public NewMail withAsynchronousSend(boolean asynchronous) {
            logger.warn("Usage of withAsynchronousSend is not required due to every mail being sent asyncronusly");
            return this;
        }

        @Override
        @Nullable
        public Long send() {
            return Optional.ofNullable(notificationService.sendNotification(email.withStatus(ES_OUTGOING).build())).map(NotificationCommonData::getId).orElse(null);
        }

        @Override
        public NewMail fromTemplate(String template, Map<String, ?> data) {
            email = EmailImpl.copyOf(emailService.applyTemplate(email.build(), template, (Map) data));
            return this;
        }

        @Override
        public long create() {
            return emailService.create(email.withStatus(ES_DRAFT).build()).getId(); //TODO: test this
        }

        @Override
        public NewMail withCard(@Nullable String className, @Nullable Long cardId) {
            email.withReference(ltEqZeroToNull(cardId));
            return this;
        }

        @Override
        public NewMail withAccount(String accountCode) {
            email.withAccount(accountService.getAccount(accountCode).getId());
            return this;
        }

        @Override
        public NewMail withSignature(String signatureCode) {
            email.withSignature(signatureService.getOneByCode(signatureCode).getId());
            return this;
        }

    }

}
