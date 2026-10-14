/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.html;

import jakarta.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Objects;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import org.owasp.html.AttributePolicy;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlSanitizerUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /* taken and modified from org.owasp.html.Sanitizers */
    private static final AttributePolicy INTEGER = (String elementName, String attributeName, String value) -> {
        if (value.isEmpty()) {
            return null;
        }
        for (int i = 0; i < value.length(); ++i) {
            char ch = value.charAt(i);
            if (ch == '.') {
                if (i == 0) {
                    return null;
                }
                return value.substring(0, i);  // truncate to integer.
            } else if (!('0' <= ch && ch <= '9')) {
                return null;
            }
        }
        return value;
    };

    private final static PolicyFactory DEFAULT_POLICY_FACTORY = Sanitizers.FORMATTING.and(Sanitizers.STYLES).and(Sanitizers.BLOCKS).and(Sanitizers.LINKS).and(Sanitizers.TABLES),
            EMAIL_POLICY_FACTORY = Sanitizers.FORMATTING.and(Sanitizers.STYLES).and(Sanitizers.BLOCKS).and(
                    /* taken and modified from org.owasp.html.Sanitizers */
                    new HtmlPolicyBuilder()
                            .allowUrlProtocols("http", "https", "data", "cid").allowElements("img")
                            .allowAttributes("alt", "src").onElements("img")
                            .allowAttributes("border", "height", "width").matching(INTEGER)
                            .onElements("img")
                            .toFactory()
            ).and(Sanitizers.LINKS).and(Sanitizers.TABLES),
            LINK_POLICY_FACTORY = new HtmlPolicyBuilder().allowStandardUrlProtocols().allowUrlProtocols("ftp", "ftps", "ws", "wss").allowElements("a").allowAttributes("href", "target").onElements("a").toFactory();

    @Nullable
    public static String sanitizeHtmlForEmail(@Nullable String str) {
        return sanitizeHtml(str, EMAIL_POLICY_FACTORY);
    }

    @Nullable
    public static String sanitizeHtml(@Nullable String str) {
        return sanitizeHtml(str, DEFAULT_POLICY_FACTORY);
    }

    @Nullable
    public static String sanitizeHtmlForLink(@Nullable String str) {
        return sanitizeHtml(str, LINK_POLICY_FACTORY);
    }

    public static boolean isSafeHtml(@Nullable String html) {
        return isBlank(html) || Objects.equals(html, LINK_POLICY_FACTORY.sanitize(html));
    }

    @Nullable
    private static String sanitizeHtml(@Nullable String str, PolicyFactory policy) {
        if (isBlank(str)) {
            return str;
        } else {
            String sanitized = policy.sanitize(str);
            if (!Objects.equals(sanitized, str)) {
                LOGGER.trace("sanitized html\n=== source html BEGIN ===\n{}\n== source html END; sanitized html BEGIN ===\n{}\n===sanitized html END ===", str, sanitized);
            }
            return sanitized;
        }
    }

    public static <K, V> Map<K, V> sanitizeHtmlInMapValues(Map<K, V> map) {
        return map(map).mapValues(v -> v instanceof String string ? (V) sanitizeHtml(string) : v);
    }

}
