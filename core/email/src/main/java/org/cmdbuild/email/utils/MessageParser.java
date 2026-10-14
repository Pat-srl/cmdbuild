/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.utils;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import static java.lang.String.format;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Consumer;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.email.EmailException;
import org.cmdbuild.email.EmailStatus;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.mta.EmailMtaServiceImpl;
import static org.cmdbuild.email.template.EmailUtils.parseEmailHeaderToken;
import static org.cmdbuild.email.template.EmailUtils.parseEmailReferencesHeader;
import static org.cmdbuild.email.utils.EmailMtaUtils.getMessageInfoSafe;
import static org.cmdbuild.email.utils.EmailMtaUtils.serializeMessageHeaders;
import static org.cmdbuild.email.utils.EmailMtaUtils.writeMessageAsString;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.safeSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author afelice
 */
public class MessageParser {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Message message;
    private final EmailPartsParser partsParser = new EmailPartsParser();

    public MessageParser(byte[] message) {
        logger.trace("build email parser from raw email data = \n\n{}\n", lazyString(safeSupplier(() -> new String(message, StandardCharsets.US_ASCII))));
        try {
            this.message = new MimeMessage(null, new ByteArrayInputStream(checkNotNull(message)));
        } catch (MessagingException ex) {
            throw new EmailException(ex, "error loading mime message from raw data");
        }
    }

    public MessageParser(Message message) {
        this.message = checkNotNull(message);
    }

    public Consumer<EmailImpl.EmailImplBuilder> acquireEmail() {
        return builder -> {
            try {
                try {
                    builder.accept(parseCommonEmailStuff());
                } catch (Exception ex) {
                    logger.warn(marker(), "error processing email metadata", ex);
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                message.writeTo(out);
                builder.withMultipartContent(out.toByteArray()).withMultipartContentType("message/rfc822").withStatus(EmailStatus.ES_ACQUIRED);
            } catch (IOException | MessagingException ex) {
                throw new EmailException(ex, "error acquiring email message = %s", getMessageInfoSafe(message));
            }
        };
    }

    public Consumer<EmailImpl.EmailImplBuilder> parseEmail() {
        return builder -> {
            try {
                logger.debug("parse email =< {} >", getMessageInfoSafe(message));
                logger.trace("parse raw message = \n\n{}\n", writeMessageAsString(message));
                loadEmailContentParts();
                builder.accept(parseCommonEmailStuff()).withStatus(EmailStatus.ES_RECEIVED).withAttachments(partsParser.getAttachments()).accept(partsParser.loadEmailContent());
            } catch (IOException | MessagingException ex) {
                throw new EmailException(ex, "error parsing email message = %s", getMessageInfoSafe(message));
            }
        };
    }

    private Consumer<EmailImpl.EmailImplBuilder> parseCommonEmailStuff() {
        return builder -> {
            try {
                builder
                        .withSentOrReceivedDate(firstNotNull(toDateTime(message.getReceivedDate()), now()))
                        .withMessageId(parseEmailHeaderToken(EmailMtaUtils.getMessageHeader(message, EmailMtaServiceImpl.EMAIL_HEADER_MESSAGE_ID)))
                        .withSubject(message.getSubject())
                        .withFrom(parseAddresses(message.getFrom()))
                        .withTo(parseAddresses(message.getRecipients(Message.RecipientType.TO)))
                        .withCc(parseAddresses(message.getRecipients(Message.RecipientType.CC)))
                        .withBcc(parseAddresses(message.getRecipients(Message.RecipientType.BCC)))
                        .withReplyTo(parseAddresses(message.getReplyTo()))
                        .withInReplyTo(parseEmailHeaderToken(EmailMtaUtils.getMessageHeader(message, EmailMtaServiceImpl.EMAIL_HEADER_IN_REPLY_TO)))
                        .withReferences(parseEmailReferencesHeader(EmailMtaUtils.getMessageHeader(message, EmailMtaServiceImpl.EMAIL_HEADER_REFERENCES)))
                        .withHeaders(serializeMessageHeaders(message));
            } catch (MessagingException ex) {
                throw new EmailException(ex, "error parsing email message = %s", getMessageInfoSafe(message));
            }
        };
    }

    private String parseAddresses(@Nullable Address[] list) {
        if (list == null) {
            return "";
        } else {
            return Arrays.stream(list).map(this::parseAddress).collect(joining(","));
        }
    }

    private String parseAddress(Address address) {
        if (address instanceof InternetAddress internetAddress) {
            String personal = internetAddress.getPersonal();
            String email = internetAddress.getAddress();
            if (isNotBlank(personal) && isNotBlank(email)) {
                return format("%s <%s>", personal, email);
            }
        }
        return address.toString();
    }

    private void loadEmailContentParts() throws MessagingException, IOException {
        logger.trace("raw email body = \n\n{}\n", lazyString(safeSupplier(() -> readToString(message.getDataHandler()))));
        partsParser.loadEmailContentParts(message);
    }

} // end MessageParser class
