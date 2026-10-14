/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.utils;

import org.cmdbuild.email.template.EmailUtils;
import com.google.common.base.Strings;
import static com.google.common.base.Strings.nullToEmpty;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeUtility;
import org.apache.tika.Tika;
import org.cmdbuild.email.EmailAttachment;
import static org.cmdbuild.email.EmailContentUtils.enrichedTextToHtml;
import org.cmdbuild.email.EmailException;
import org.cmdbuild.email.beans.EmailAttachmentImpl;
import org.cmdbuild.email.beans.EmailImpl;
import static org.cmdbuild.email.utils.EmailMtaUtils.fixEmailInlineAttachmentsWithCmdbuildCidUrl;
import static org.cmdbuild.utils.html.HtmlSanitizerUtils.sanitizeHtmlForEmail;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.setCharsetInContentType;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmPreconditions;
import static org.cmdbuild.utils.lang.CmStringUtils.normalize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailPartsParser {

    private static final Tika TIKA = new Tika();

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final List<EmailAttachment> attachments = CmCollectionUtils.list();
    private final List<EmailAttachment> inline = CmCollectionUtils.list();
    private final List<EmailAttachment> parts = CmCollectionUtils.list();
    private Optional<EmailAttachment> htmlPart;
    private Optional<EmailAttachment> plaintextPart;
    private Optional<EmailAttachment> textEnrichedPart;

    public void loadEmailContentParts(Multipart part) throws MessagingException, IOException {
        doLoadEmailContentParts(part);
        doLoadElements();
    }

    public void loadEmailContentParts(Part part) throws MessagingException, IOException {
        doLoadEmailContentParts(part);
        doLoadElements();
    }

    public List<EmailAttachment> getAttachments() {
        return attachments;
    }

    public Consumer<EmailImpl.EmailImplBuilder> loadEmailContent() {
        return loadEmailContent(htmlPart, plaintextPart, textEnrichedPart, list(inline).with(parts));
    }

    private void doLoadElements() { 
        List<EmailAttachment> elementParts = list(parts).with(inline);
        htmlPart = elementParts.stream().filter(p -> p.isOfType("text/html")).findFirst();
        plaintextPart = elementParts.stream().filter(p -> p.isOfType("text/plain")).findFirst();
        textEnrichedPart = elementParts.stream().filter(p -> p.isOfType("text/enriched")).findFirst();
        htmlPart.ifPresent(elementParts::remove);
        plaintextPart.ifPresent(elementParts::remove);
        textEnrichedPart.ifPresent(elementParts::remove);
        attachments.addAll(elementParts);
    }

    private void doLoadEmailContentParts(Part part) throws MessagingException, IOException {
        logger.debug("loading email part with content type =< {} >", normalize(part.getContentType()));
        try {
            if (part.isMimeType("multipart/*")) {
                if (part instanceof Multipart) {
                    doLoadEmailContentParts((Multipart) part);
                } else {
                    doLoadEmailContentParts(new MimeMultipart(CmIoUtils.toDataSource(part.getDataHandler())));
                }
            } else {
                byte[] data = CmIoUtils.toByteArray(part.getDataHandler());
                String contentType = CmPreconditions.firstNotBlank(part.getContentType(), TIKA.detect(data));
                String contentId = Optional.ofNullable(part.getHeader("Content-ID")).map(cid -> EmailUtils.parseEmailHeaderToken(cid[0])).orElse(null);
                logger.trace("loaded email part with content type =< {} > disposition =< {} > contentId =< {} >", normalize(contentType), nullToEmpty(part.getDisposition()), nullToEmpty(contentId));
                String attachmentContentDisposition = Strings.nullToEmpty(part.getDisposition()).trim().toLowerCase();
                EmailAttachment element = EmailAttachmentImpl.builder()
                        .withData(data)
                        .withContentDisposition(attachmentContentDisposition)
                        .withFileName(MimeUtility.decodeText(Strings.nullToEmpty(part.getFileName())))
                        .withContentType(contentType)
                        .withContentId(contentId).build();
                switch (attachmentContentDisposition) {
                    case "":
                        parts.add(element);
                        break;
                    case Part.INLINE:
                        inline.add(element);
                        break;
                    case Part.ATTACHMENT:
                    default:
                        // as per rfc, unidentified disposition should be threated as `attachment`
                        attachments.add(element);
                }
            }
        } catch (Exception ex) {
            throw new EmailException(ex, "error loading email part with content type =< %s > disposition =< %s >", part.getContentType(), Strings.nullToEmpty(part.getDisposition()));
        }
    }

    private void doLoadEmailContentParts(Multipart multipart) throws MessagingException, IOException {
        for (int i = 0; i < multipart.getCount(); i++) {
            doLoadEmailContentParts(multipart.getBodyPart(i));
        }
    }

    private static Consumer<EmailImpl.EmailImplBuilder> loadEmailContent(Optional<EmailAttachment> htmlPartParam, Optional<EmailAttachment> plaintextPart, Optional<EmailAttachment> textEnrichedPart, List<EmailAttachment> attachmentsForInline) {
        return (b) -> {
            try {
                Optional<EmailAttachment> htmlPart = htmlPartParam;
                if (textEnrichedPart.isPresent() && !htmlPart.isPresent()) {
                    LOGGER.debug("load html part from text/enriched part");
                    htmlPart = Optional.of(EmailAttachmentImpl.build(enrichedTextToHtml(textEnrichedPart.get().getDataAsString()).getBytes(UTF_8), "text/html;charset=UTF-8"));//TODO handle charset in html header
                }

                if (htmlPart.isPresent()) {
                    LOGGER.debug("process html part, add inline images and sanitize html");
                    String html = htmlPart.get().getDataAsString();
//                    html = embedEmailInlineAttachmentsAsBase64(html, attachmentsForInline);
                    html = fixEmailInlineAttachmentsWithCmdbuildCidUrl(html, attachmentsForInline);
                    html = sanitizeHtmlForEmail(html);
                    htmlPart = Optional.of(EmailAttachmentImpl.build(html.getBytes(UTF_8), "text/html;charset=UTF-8"));//TODO handle charset in html header
                }

                if (plaintextPart.isPresent()) {
                    b.withContent(plaintextPart.get().getDataAsString()).withContentType(setCharsetInContentType(plaintextPart.get().getContentType(), UTF_8));
                } else if (htmlPart.isPresent()) {
                    b.withContent(htmlPart.get().getDataAsString()).withContentType(setCharsetInContentType(htmlPart.get().getContentType(), UTF_8));
                }

                if (htmlPart.isPresent() && plaintextPart.isPresent()) {
                    MimeMultipart mimeMultipart = new MimeMultipart("alternative");
                    MimeBodyPart plaintext = new MimeBodyPart();
                    plaintext.setContent(plaintextPart.get().getDataAsString(), plaintextPart.get().getContentType());
                    plaintext.setHeader("Content-Type", plaintextPart.get().getContentType());
                    mimeMultipart.addBodyPart(plaintext);
                    MimeBodyPart html = new MimeBodyPart();
                    html.setContent(htmlPart.get().getDataAsString(), htmlPart.get().getContentType());
                    html.setHeader("Content-Type", htmlPart.get().getContentType());
                    mimeMultipart.addBodyPart(html);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    mimeMultipart.writeTo(out);
                    b.withMultipartContent(out.toByteArray()).withMultipartContentType(mimeMultipart.getContentType());
                } else {
                    b.withMultipartContent(null).withMultipartContentType(null);
                }
            } catch (Exception ex) {
                throw new EmailException(ex, "error processing email content parts");
            }
        };
    }

}
