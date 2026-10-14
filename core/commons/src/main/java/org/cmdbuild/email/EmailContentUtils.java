/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.html.HtmlEscapers;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.annotation.Nullable;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeMultipart;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import org.apache.commons.lang3.StringEscapeUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.io.CmIoUtils.getContentType;
import static org.cmdbuild.utils.io.CmIoUtils.isContentType;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;

public class EmailContentUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String getContentTypeOrAutoDetect(@Nullable String contentType, String content) {
        if (isBlank(contentType) || isContentType(contentType, "application/octet-stream")) {
            if (isBlank(content)) {
                contentType = "text/plain";
            } else {
                contentType = getContentType(nullToEmpty(content).getBytes());
                if (isContentType(contentType, "application/octet-stream")) {
                    LOGGER.warn("unable to detect content type for email payload =< {} >", abbreviate(content));
                    contentType = "text/plain";
                }
            }
        }
        return contentType;
    }

    public static String getContentPlaintext(Email email) {
        try {
            LOGGER.trace("get plaintext content from email = {}", email);
            if (!email.hasAnyContent()) {
                return "";
            } else if (isContentType(email.getContentType(), "text/plain") && email.hasContent()) {
                return email.getContent();
            } else if (email.hasMultipartContent()) {
                Optional<Part> part = getMultiParts(email).stream().filter(rethrowPredicate(p -> p.isMimeType("text/plain"))).findFirst();
                if (part.isPresent()) {
                    return readToString(part.get().getDataHandler());
                }
            }
            Optional<String> html = getContentHtmlIfExists(email);
            if (html.isPresent()) {
                LOGGER.debug("only html content found, converting to plaintext");
                return htmlToPlainText(html.get());
            }
            throw new EmailException("plaintext content not found");
        } catch (Exception ex) {
            LOGGER.warn(marker(), "unable to get plaintext content for email = {} (will return raw content of type =< {} >)", email, email.getContentType(), ex);
            return email.getContent();
        }
    }

    private static List<Part> getMultiParts(Email email) throws MessagingException {
        MimeMultipart mimeMultipart = new MimeMultipart(newDataSource(email.getMultipartContent(), email.getMultipartContentType()));
        List<Part> parts = getParts(mimeMultipart);
        checkArgument(!parts.isEmpty(), "multipart content is empty");
        return parts;
    }

    public static String getContentHtmlOrRawPlaintext(Email email) {
        return EmailContentUtils.getContentHtml(email, false);
    }

    public static String getContentHtml(Email email) {
        return EmailContentUtils.getContentHtml(email, true);
    }

    private static String getContentHtml(Email email, boolean convertPlaintextFallback) {
        return getContentHtmlIfExists(email).orElseGet(() -> {
            String plaintext = getContentPlaintext(email);
            if (convertPlaintextFallback) {
                plaintext = plaintextToHtml(plaintext);
            }
            return plaintext;
        });
    }

    public static Optional<String> getContentHtmlIfExists(Email email) {
        try {
            LOGGER.trace("get html content from email = {}", email);
            if (!email.hasAnyContent()) {
                return Optional.of("");
            } else if (isContentType(email.getContentType(), "text/html") && email.hasContent()) {
                return Optional.of(readToString(newDataHandler(email.getContent(), email.getContentType())));
            } else if (email.hasMultipartContent()) {
                Optional<Part> htmlPart = getMultiParts(email).stream().filter(rethrowPredicate(p -> p.isMimeType("text/html"))).findFirst();
                if (htmlPart.isPresent()) {
                    return Optional.of(readToString(htmlPart.get().getDataHandler()));
                }
                Optional<Part> textEnrichedPart = getMultiParts(email).stream().filter(rethrowPredicate(p -> p.isMimeType("text/enriched"))).findFirst();
                if (textEnrichedPart.isPresent()) {
                    return Optional.of(enrichedTextToHtml(readToString(textEnrichedPart.get().getDataHandler())));
                }
            }
        } catch (Exception ex) {
            LOGGER.error(marker(), "unable to get html content for email = {}", email, ex);
        }
        return Optional.empty();
    }

    public static String enrichedTextToHtml(String value) {
        value = value
                .replaceAll(Pattern.quote("<<"), Matcher.quoteReplacement("&lt;"))
                .replaceAll("(?i)[<]param[>].*?[<][/]param[>]", "");
        {
            Matcher matcher = Pattern.compile("([<][/]?)([^/>]+)([>])").matcher(value);
            StringBuffer stringBuilder = new StringBuffer();
            while (matcher.find()) {
                String replacement;
                replacement = switch (matcher.group(2).trim().toLowerCase()) {
                    case "bold" ->
                        matcher.group(1) + "b" + matcher.group(3);
                    case "italic" ->
                        matcher.group(1) + "i" + matcher.group(3);
                    default ->
                        "";
                };
                matcher.appendReplacement(stringBuilder, Matcher.quoteReplacement(replacement));
            }
            matcher.appendTail(stringBuilder);
            value = stringBuilder.toString();
        }
        {
            Matcher matcher = Pattern.compile("\r\n((\r\n)*)").matcher(value);
            StringBuffer stringBuilder = new StringBuffer();
            while (matcher.find()) {
                String replacement = " " + nullToEmpty(matcher.group(1)).replaceAll("\r\n", "<br>\n");
                matcher.appendReplacement(stringBuilder, Matcher.quoteReplacement(replacement));
            }
            matcher.appendTail(stringBuilder);
            value = stringBuilder.toString();
        }
        return value;
    }

    private static List<Part> getParts(Multipart multipart) throws MessagingException {
        List<Part> parts = list();
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart part = multipart.getBodyPart(i);
            LOGGER.trace("found email part = {} {}", byteCountToDisplaySize(part.getSize()), part.getContentType());
            if (part.isMimeType("multipart/*")) {
                parts.addAll(getParts(new MimeMultipart(toDataSource(part.getDataHandler()))));//TODO improve this conversion
            } else {
                parts.add(part);
            }
        }
        return parts;
    }

    public static String plaintextToHtml(String plaintext) {
        plaintext = HtmlEscapers.htmlEscaper().escape(plaintext);
        plaintext = plaintext.replaceAll("\\R", "<br>\n");
        return plaintext;
    }

    public static String htmlToPlainText(String content) {
        content = nullToEmpty(content)
                .replaceAll("\\R", "\n")
                .replaceAll("(?m)^[ \t]+", "")
                .replaceAll("(?s)[<][!]--.*?--[>]", "")
                .replaceAll("(?i)[<] *br */?[>]", "\n")
                .replaceAll("[<][^>]+[>]", "")//TODO improve this
                .replaceAll("\n\n+", "\n\n");
        content = StringEscapeUtils.unescapeHtml4(content);
        content = trim(content);
        return content;
    }

    public static boolean hasContentHtml(Email email) {
        return getContentHtmlIfExists(email).isPresent();
    }
}
