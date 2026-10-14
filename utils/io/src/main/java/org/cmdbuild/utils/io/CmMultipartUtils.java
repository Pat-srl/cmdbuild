/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io;

import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.getOnlyElement;
import jakarta.activation.DataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMultipart;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.io.CmIoUtils.isContentType;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.slf4j.LoggerFactory;

public class CmMultipartUtils {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static boolean isMultipartWithOnlyPlaintextParts(DataSource dataSource) {
        if (isMultipart(dataSource)) {
            try {
                MimeMultipart mimeMultipart = new MimeMultipart(dataSource);
                return isMultipartWithOnlyPlaintextParts(mimeMultipart);
            } catch (Exception ex) {
                LOGGER.warn("error checking multipart content", ex);
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean isMultipart(DataSource dataSource) {
        return isMultipart(dataSource.getContentType());
    }

    public static boolean isMultipart(@Nullable String contentType) {
        return isNotBlank(contentType) && isContentType(contentType, "multipart/*");
    }

    public static boolean isMultipartMixed(@Nullable String contentType) {
        return isNotBlank(contentType) && isContentType(contentType, "multipart/mixed");
    }

    public static boolean isPlaintext(@Nullable String contentType) {
        return nullToEmpty(contentType).toLowerCase().matches("^(text/.*|application/(json|javascript|x-www-form-urlencoded|(xop\\+)?xml))(;.*)?$");
    }

    @Nullable
    public static String getPartName(@Nullable String contentDisposition) {
        Matcher matcher = Pattern.compile("name=\"([^\"]+)\"").matcher(nullToEmpty(contentDisposition));
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    private static boolean isMultipartWithOnlyPlaintextParts(MimeMultipart multipart) throws MessagingException {
        LOGGER.info("test multipart content = {} type =< {} >", multipart, multipart.getContentType());
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart part = multipart.getBodyPart(i);
            LOGGER.info("test multipart content = {} part = {} {} type =< {} >", multipart, i, part, part.getContentType());
            if (part.isMimeType("multipart/*")) {
                LOGGER.info("part is multipart, test content");
                if (!isMultipartWithOnlyPlaintextParts(new MimeMultipart(toDataSource(part.getDataHandler())))) {
                    LOGGER.info("inner part is not plain text, return false from isMultipartWithOnlyPlaintextParts()");
                    return false;
                }
            } else if (!isPlaintext(part.getContentType())) {
                LOGGER.info("part not plain text ( type =< {} > ), return false from isMultipartWithOnlyPlaintextParts()", part.getContentType());
                return false;
            }
        }
        LOGGER.info("all parts are plain text, return true from isMultipartWithOnlyPlaintextParts()");
        return true;
    }

    public static boolean hasSinglePlaintextPart(DataSource dataSource) {
        return getSinglePlaintextPartOptional(dataSource).isPresent();
    }

    public static String getSinglePlaintextPart(DataSource dataSource) {
        return getSinglePlaintextPartOptional(dataSource).get();
    }

    private static Optional<String> getSinglePlaintextPartOptional(DataSource dataSource) {
        if (isMultipart(dataSource.getContentType())) {
            try {
                MimeMultipart mimeMultipart = new MimeMultipart(dataSource);
                Collection<String> parts = getPlaintextParts(mimeMultipart);
                return parts.size() == 1 ? Optional.of(getOnlyElement(parts)) : Optional.empty();
            } catch (Exception ex) {
                LOGGER.warn("error processing multipart content", ex);
            }
        }
        return Optional.empty();
    }

    private static Collection<String> getPlaintextParts(MimeMultipart multipart) throws MessagingException {
        Collection<String> parts = list();
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart part = multipart.getBodyPart(i);
            if (part.isMimeType("multipart/*")) {
                parts.addAll(getPlaintextParts(new MimeMultipart(toDataSource(part.getDataHandler()))));
            } else if (isPlaintext(part.getContentType())) {
                parts.add(readToString(part.getDataHandler()));
            }
        }
        return parts;
    }
}
