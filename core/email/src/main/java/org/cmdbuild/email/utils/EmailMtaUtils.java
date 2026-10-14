/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.utils;

import org.cmdbuild.email.template.EmailUtils;
import com.google.common.base.Joiner;
import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.transformValues;
import jakarta.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import jakarta.activation.DataSource;
import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.text.StringEscapeUtils;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAttachment;
import org.cmdbuild.email.EmailException;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.email.beans.EmailAttachmentImpl;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.beans.EmailImpl.EmailImplBuilder;
import static org.cmdbuild.email.mta.EmailMtaServiceImpl.EMAIL_HEADER_IN_REPLY_TO;
import static org.cmdbuild.email.mta.EmailMtaServiceImpl.EMAIL_HEADER_MESSAGE_ID;
import static org.cmdbuild.email.mta.EmailMtaServiceImpl.EMAIL_HEADER_REFERENCES;
import static org.cmdbuild.email.template.EmailUtils.formatEmailHeaderToken;
import static org.cmdbuild.email.template.EmailUtils.parseEmailHeaderToken;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.date.CmDateUtils.toJavaDate;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;
import static org.cmdbuild.utils.io.CmIoUtils.getCharsetFromContentType;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.setCharsetInContentType;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.io.CmMultipartUtils.isMultipart;
import static org.cmdbuild.utils.io.CmMultipartUtils.isMultipartMixed;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowBiConsumer;
import static org.cmdbuild.utils.url.CmUrlUtils.toDataUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailMtaUtils {

    private final static int MAX_EMBEDDED_IMAGE_SIZE = 10 * 1024 * 1024;//10M ; TODO: make this configurable!

    public static final String MAIL_IMAP_HOST = "mail.imap.host",
            MAIL_IMAP_PORT = "mail.imap.port",
            MAIL_IMAPS_HOST = "mail.imaps.host",
            MAIL_IMAP_SOCKET_FACTORY_CLASS = "mail.imap.socketFactory.class",
            MAIL_IMAPS_PORT = "mail.imaps.port",
            MAIL_IMAP_STARTTLS_ENABLE = "mail.imap.starttls.enable",
            MAIL_SMPT_SOCKET_FACTORY_CLASS = "mail.smpt.socketFactory.class",
            MAIL_SMPT_SOCKET_FACTORY_FALLBACK = "mail.smtp.socketFactory.fallback",
            MAIL_SMTP_AUTH = "mail.smtp.auth",
            MAIL_SMTP_HOST = "mail.smtp.host",
            MAIL_SMTP_PORT = "mail.smtp.port",
            MAIL_SMTPS_AUTH = "mail.smtps.auth",
            MAIL_SMTPS_HOST = "mail.smtps.host",
            MAIL_SMTPS_PORT = "mail.smtps.port",
            MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable",
            MAIL_STORE_PROTOCOL = "mail.store.protocol",
            MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol",
            JAVAX_NET_SSL_SSL_SOCKET_FACTORY = "javax.net.ssl.SSLSocketFactory";

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String serializeMessageHeaders(Message message) throws MessagingException {
        return list(message.getAllHeaders()).stream().map(h -> format("%s: %s", h.getName(), nullToEmpty(h.getValue()))).collect(joining("\n"));
    }

    public static String getMessageId(Message message) {
        return parseEmailHeaderToken(getMessageHeader(message, EMAIL_HEADER_MESSAGE_ID));
    }

    public static String getMessageHeader(Message message, String key) {
        try {
            String[] rawValue = message.getHeader(key);
            if (rawValue == null || rawValue.length == 0) {
                return "";
            } else {
                checkArgument(rawValue.length == 1);
                return nullToEmpty(rawValue[0]);
            }
        } catch (MessagingException ex) {
            throw new EmailException(ex, "error reading email header =< %s >", key);
        }
    }

    public static String getMessageInfoSafe(Message message) {
        try {
            return toStringHelper(message)
                    .add("id", parseEmailHeaderToken(getMessageHeader(message, EMAIL_HEADER_MESSAGE_ID)))
                    .add("subject", nullToEmpty(message.getSubject()))
                    .add("from", message.getFrom() == null ? "" : Joiner.on(",").join(message.getFrom()))
                    .add("to", message.getAllRecipients() == null ? "" : Joiner.on(",").join(message.getAllRecipients())).toString();
        } catch (MessagingException ex) {
            LOGGER.warn("unable to get message infos for log message", ex);
            return message.toString();
        }
    }

    public static Email parseAcquiredEmail(Email email) {
        try {
            checkArgument(email.isAcquired(), "invalid email status");
            return EmailImpl.copyOf(email).accept(new MessageParser(email.getMultipartContent()).parseEmail()).build();
        } catch (Exception ex) {
            throw new EmailException(ex, "error parsing acquired email = %s", email);
        }
    }

    public static Email parseEmail(String rawEmail) {
        return parseEmail(rawEmail.getBytes(StandardCharsets.ISO_8859_1));
    }

    public static Email parseEmail(byte[] message) {
        return EmailImpl.builder().accept(new MessageParser(message).parseEmail()).build();
    }

    public static Email parseEmail(Message message) {
        return EmailImpl.builder().accept(new MessageParser(message).parseEmail()).build();
    }

    public static Consumer<EmailImplBuilder> emailParser(Message message) {
        return new MessageParser(message).parseEmail();
    }

    public static Email acquireEmail(byte[] message) {
        return EmailImpl.builder().accept(new MessageParser(message).acquireEmail()).build();
    }

    public static Consumer<EmailImplBuilder> emailAcquirer(Message message) {
        return new MessageParser(message).acquireEmail();
    }

    /**
     * Will rename all duplicate filenames for attachments.
     * 
     * <b>Warning</b>: ensure to call this method <b>before</b> {@link EmailService#createEmail()} that will store in DMS that file.
     * 
     * @param attachments
     * @return 
     */
    public static List<EmailAttachment> renameDuplicates(List<EmailAttachment> attachments) {
        return CmCollectionUtils.renameDuplicates(emptyList(), attachments, EmailAttachment::getFileName, (a, n) -> EmailAttachmentImpl.copyOf(a).withFileName(n).build());
    }    
    
    /**
     * Will rename all duplicate filenames for attachments, <b>keeping unchanged</b> the <code>otherNames</code> ones.
     * 
     * <b>Warning</b>: ensure to call this method <b>before</b> {@link EmailService#createEmail()} that will store in DMS that file.
     * 
     * @param otherNames name of attachments that can't be changed (because given as input, like the ones  related to a Card or given as 
     * @param attachments
     * @return 
     */
    public static List<EmailAttachment> renameDuplicates(Collection<String> otherNames, List<EmailAttachment> attachments) {
        return CmCollectionUtils.renameDuplicates(otherNames, attachments, EmailAttachment::getFileName, (a, n) -> EmailAttachmentImpl.copyOf(a).withFileName(n).build());
    }

    public static String buildCmdbuildContentId(byte[] base64) {
        return format("cm_%s", hash(base64));
    }

    public static String buildCmdbuildContentId(EmailAttachment attachment) {
        return format("cm_%s", hash(attachment.getData()));
    }

    public static String fixEmailInlineAttachmentsWithCmdbuildCidUrl(String content, List<EmailAttachment> inlineAttachments) {
        if (isNotBlank(content)) {
            for (EmailAttachment attachment : inlineAttachments) {
                if (attachment.hasContentId()) {
                    String pattern = format("src=\"cid:%s\"", attachment.getContentId()), replacement = format("src=\"cid:%s\"", StringEscapeUtils.escapeHtml4(buildCmdbuildContentId(attachment)));
                    if (content.contains(pattern)) {
                        LOGGER.debug("process inline attachment pattern =< {} > , replace with pattern =< {} >", pattern, replacement);
                        content = content.replace(pattern, replacement);
                    }
                }
            }
        }
        return content;
    }

    public static String fixEmailInlineAttachmentsWithOutgoingCidUrl(String content, List<EmailAttachment> inlineAttachments) {
        if (isNotBlank(content)) {
            for (EmailAttachment attachment : inlineAttachments) {
                if (attachment.hasContentId()) {
                    String pattern = format("src=\"cid:%s\"", StringEscapeUtils.escapeHtml4(buildCmdbuildContentId(attachment))), replacement = format("src=\"cid:%s\"", StringEscapeUtils.escapeHtml4(attachment.getContentId()));
                    if (content.contains(pattern)) {
                        LOGGER.debug("process inline attachment pattern =< {} > , replace with pattern =< {} >", pattern, replacement);
                        content = content.replace(pattern, replacement);
                    }
                }
            }
        }
        return content;
    }

    public static String fixEmailInlineHtmlAttachmentsWithCidBase64(String content) {
        Pattern pattern = Pattern.compile("src=\"data:(image/[^;]+);base64,([^\"]+)\"");
        Pattern patternCid = Pattern.compile("src=\"(data:image/[^;]+;base64,[^\"]+)\"");
        Matcher matcher = pattern.matcher(content);
        Matcher matcherCid = patternCid.matcher(content);
        while (matcher.find()) {
            if (matcherCid.find()) {
                byte[] base64 = DatatypeConverter.parseBase64Binary(matcher.group(2));
                String cid = format("cid:%s", buildCmdbuildContentId(base64));
                content = content.replace(matcherCid.group(1), cid);
                LOGGER.debug("replacing html inline content with =< {} >", cid);
            }
        }
        return content;
    }

    public static String embedEmailInlineAttachmentsAsBase64(String content, DataSource... inlineAttachments) {
        return embedEmailInlineAttachmentsAsBase64(content, list(inlineAttachments).map(EmailAttachmentImpl::build));
    }

    public static String embedEmailInlineAttachmentsAsBase64(String content, List<EmailAttachment> inlineAttachments) {
        if (isNotBlank(content)) {
            for (EmailAttachment attachment : inlineAttachments.stream().flatMap(e -> listOf(EmailAttachment.class).accept(l -> {
                l.add(EmailAttachmentImpl.copyOf(e).withContentId(buildCmdbuildContentId(e)).build());
                if (e.hasContentId()) {
                    l.add(e);
                }
            }).stream()).collect(toList())) {
                String pattern = format("src=\"cid:%s\"", checkNotBlank(attachment.getContentId()));
                if (content.contains(pattern)) {
                    if (attachment.getData().length < MAX_EMBEDDED_IMAGE_SIZE) {
                        String replacement = format("src=\"%s\"", StringEscapeUtils.escapeHtml4(toDataUrl(toDataSource(attachment.getDataHandler()))));
                        content = content.replace(pattern, replacement);
                    } else {
                        LOGGER.warn(marker(), "will not embed image = {} in email content: image size too big for embedding", attachment);
                    }
                }
            }
        }
        return content;
    }

    public static List<EmailAttachment> convertEmailInlineHtmlAttachmentsToEmailAttachments(String content) {
        Pattern pattern = Pattern.compile("src=\"data:(image/[^;]+);base64,([^\"]+)\"");
        Matcher matcher = pattern.matcher(content);
        List<EmailAttachment> attachments = list();
        while (matcher.find()) {
            if (matcher.groupCount() == 2) {
                String contentType = matcher.group(1);
                byte[] base64 = DatatypeConverter.parseBase64Binary(matcher.group(2));
                EmailAttachment attachment = EmailAttachmentImpl.copyOf(newDataSource(base64)).withContentId(checkNotBlank(buildCmdbuildContentId(base64))).withContentType(contentType).build();
                attachments.add(attachment);
            }
        }
        LOGGER.debug("html inline attachments =< {} >", attachments);
        return attachments;
    }

    public static Consumer<EmailImplBuilder> loadEmailContent(Part content) {
        try {
            EmailPartsParser partsParser = new EmailPartsParser();
            partsParser.loadEmailContentParts(content);
            return partsParser.loadEmailContent();
        } catch (MessagingException | IOException ex) {
            throw new EmailException(ex, "error processing email content parts");
        }
    }

    public static Consumer<EmailImplBuilder> loadEmailContent(Multipart content) {
        try {
            EmailPartsParser partsParser = new EmailPartsParser();
            partsParser.loadEmailContentParts(content);
            return partsParser.loadEmailContent();
        } catch (MessagingException | IOException ex) {
            throw new EmailException(ex, "error processing email content parts");
        }
    }

    public static Address toAddress(String emailAddress) {
        try {
            return new InternetAddress(emailAddress);
        } catch (AddressException ex) {
            throw new EmailException(ex);
        }
    }

    public static Message emailToMessage(Email email) {
        try {
            Message message = new MimeMessage((Session) null);
            fillMessage(message, email);
            LOGGER.trace("built raw message from email = \n\n{}\n", writeMessageAsString(message));
            return message;
        } catch (MessagingException ex) {
            throw new EmailException(ex, "error building email message");
        }
    }

    public static String emailToMessageData(Email email) {
        return writeMessageAsString(emailToMessage(email));
    }

    public static String writeMessageAsString(Message message) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            message.writeTo(out);
            return out.toString(StandardCharsets.US_ASCII);
        } catch (IOException | MessagingException ex) {
            throw new EmailException(ex, "error printing email message");
        }
    }

    public static void fillMessage(Message message, Email email) throws MessagingException {
        message.addFrom(transform(email.getFromRawAddressList(), EmailMtaUtils::toAddress).toArray(new Address[]{}));

        if (isNotBlank(email.getReplyTo())) {
            message.setReplyTo(new Address[]{toAddress(email.getReplyTo())});
        }

        transformValues(CmMapUtils.<RecipientType, List<String>, Object>map(
                Message.RecipientType.TO, email.getToRawAddressList(),
                Message.RecipientType.CC, email.getCcRawAddressList(),
                Message.RecipientType.BCC, email.getBccRawAddressList()),
                (a) -> transform(a, EmailMtaUtils::toAddress)).forEach(rethrowBiConsumer((t, a) -> {
                    message.addRecipients(t, a.toArray(Address[]::new));
                }));

        message.setSubject(email.getSubject());
        message.setSentDate(toJavaDate(now()));

        String emailContent = nullToEmpty(email.getContent());
        String emailContentType = email.getContentType();
        Multipart contentPart;

        if (isMultipart(emailContentType)) {
            contentPart = new MimeMultipart(newDataSource(emailContent, emailContentType));
            emailContent = null;
        } else {
            contentPart = null;
            if (isBlank(getCharsetFromContentType(emailContentType))) {
                emailContentType = setCharsetInContentType(emailContentType, StandardCharsets.UTF_8.name());
            }
        }

        String inReplyTo = email.getInReplyTo();
        List<String> references = email.getReferences();
        if (isNotBlank(inReplyTo) && (references.isEmpty() || !equal(getLast(references), inReplyTo))) {
            references = list(references).with(inReplyTo);
        }
        if (!references.isEmpty()) {
            message.addHeader(EMAIL_HEADER_REFERENCES, references.stream().map(EmailUtils::formatEmailHeaderToken).collect(joining(" ")));
        }
        if (isNotBlank(inReplyTo)) {
            message.addHeader(EMAIL_HEADER_IN_REPLY_TO, formatEmailHeaderToken(inReplyTo));
        }

        if (email.getAttachments().isEmpty()) {
            if (contentPart == null) {
                message.setContent(checkNotNull(emailContent), emailContentType);
            } else {
                message.setContent(contentPart);
            }
        } else {
            Multipart multipart;
            if (contentPart != null && isMultipartMixed(contentPart.getContentType())) {
                multipart = contentPart;
            } else {
                multipart = new MimeMultipart("mixed");
                MimeBodyPart contentBodyPart = new MimeBodyPart();
                if (contentPart == null) {
                    contentBodyPart.setContent(checkNotNull(emailContent), emailContentType);
                } else {
                    contentBodyPart.setContent(contentPart);
                }
                multipart.addBodyPart(contentBodyPart);
            }
            for (EmailAttachment a : email.getAttachments()) {
                BodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.setDataHandler(newDataHandler(a.getData(), a.getContentType(), a.getFileName()));
                attachmentPart.setFileName(a.getFileName());//TODO is this required? test
                attachmentPart.setHeader("Content-ID", format("<%s>", a.getContentId()));
                multipart.addBodyPart(attachmentPart);
            }
            message.setContent(multipart);
        }
    }
}
