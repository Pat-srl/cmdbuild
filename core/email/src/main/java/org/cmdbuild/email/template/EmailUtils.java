/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.template;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailProcessingUtils;
import org.cmdbuild.email.job.MapperConfig;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmMapUtils.toImmutableMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class EmailUtils {

    private final static String WORKFLOW_MAPPING_PARAM_SEPARATOR = "&#124;";

    public static Map<String, String> parseWorkflowMappingParam(@Nullable String value) {
        if (isBlank(value)) {
            return emptyMap();
        } else {
            return Splitter.on(WORKFLOW_MAPPING_PARAM_SEPARATOR).splitToList(value).stream().map(l -> {
                Matcher matcher = Pattern.compile("\\s*([^=]+?)\\s*=\\s*(.*?)\\s*").matcher(l);
                checkArgument(matcher.matches(), "invalid workflow mapping param syntax for part = %s value = %s", l, value);
                return Pair.of(checkNotBlank(matcher.group(1)), matcher.group(2));
            }).collect(toImmutableMap(Pair::getKey, Pair::getValue));
        }
    }

    @Nullable
    public static Long parseCardIdFromEmailSubject(@Nullable String subject) {
        Matcher matcher = Pattern.compile("\\[\\s*([0-9]+)\\s*\\]").matcher(nullToEmpty(subject));
        if (matcher.find()) {
            return Long.valueOf(matcher.group(1));
        } else {
            return null;
        }
    }

    public static String parseSubjectDescrFromEmailSubject(@Nullable String subject) {
        Matcher matcher = Pattern.compile("\\[\\s*[0-9]+\\s*\\]\\s*(.*)").matcher(nullToEmpty(subject));
        if (matcher.find()) {
            return nullToEmpty(matcher.group(1));
        } else {
            return nullToEmpty(subject);
        }
    }

    public static String buildSubjectWithIdForEmail(Email email) {
        if (email.getNoSubjectPrefix()) {
            return nullToEmpty(email.getSubject());
        } else {
            return format("[%s] %s", checkNotNull(email.getId()), nullToEmpty(email.getSubject()));
        }
    }

    public static List<String> parseEmailReferencesHeader(@Nullable String value) {
        Matcher matcher = Pattern.compile(".*?([<]([^>]+)[>])", Pattern.DOTALL).matcher(nullToEmpty(value));
        return listOf(String.class).accept(l -> {
            while (matcher.find()) {
                l.add(checkNotBlank(matcher.group(2)));
            }
        });
    }

    public static String parseEmailHeaderToken(@Nullable String rawToken) {
        return nullToEmpty(rawToken).replaceFirst("\\s*[<](.+)[>]\\s*", "$1");
    }

    public static String formatEmailHeaderToken(String value) {
        return format("<%s>", checkNotBlank(value));
    }

    public static String processMapperExpr(MapperConfig config, String body, String expr) {
        return EmailProcessingUtils.processMapperExpr(config, body, expr);
    }

    public static String handleEmailSignatureForTemplate(String body, @Nullable String signature) {
        if (isNotBlank(signature)) {
            signature = format("<div data-type=\"signature\">%s</div>", signature);
            Matcher matcher = Pattern.compile("(?is)\\<div\\s+data-type\\s*=\\s*\"signature\"\\s*\\>(\\s*\\</div\\>)?").matcher(nullToEmpty(body));
            if (matcher.find()) {
                if (isBlank(matcher.group(1))) {
                    //nothing to do
                } else {
                    body = matcher.replaceFirst(signature);
                }
            } else {
                body += "\n\n" + signature;
            }
        }
        return body;
    }
}
