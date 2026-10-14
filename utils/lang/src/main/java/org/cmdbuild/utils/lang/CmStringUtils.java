/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.io.CharStreams;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import jakarta.annotation.Nullable;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import static java.lang.Integer.max;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import static java.util.Arrays.asList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trim;
import org.apache.commons.text.StringEscapeUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmStringUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final static int DEFAULT_MAX = 500;

    @Nullable
    public static String escapeGroovyTripleSingleQuoteString(@Nullable String value) {
        if (isBlank(value)) {
            return value;
        } else {
            return value.replace("\\", "\\\\").replace("'", "\\'");
        }
    }

    @Nullable
    public static String toUpperCaseOrNull(@Nullable String value) {
        return isBlank(value) ? value : value.toUpperCase();
    }

    @Nullable
    public static String toLowerCaseOrNull(@Nullable String value) {
        return isBlank(value) ? value : value.toLowerCase();
    }

    @Nullable
    public static String toUpperCaseOrNull(@Nullable Object value) {
        String stringValue = toStringOrNull(value);
        return isBlank(stringValue) ? stringValue : stringValue.toUpperCase();
    }

    @Nullable
    public static String toLowerCaseOrNull(@Nullable Object value) {
        String stringValue = toStringOrNull(value);
        return isBlank(stringValue) ? stringValue : stringValue.toLowerCase();
    }

    @Nullable
    public static String toStringOrNull(@Nullable Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Number number && number.doubleValue() % 1 == 0) {
            return Long.toString(number.longValue());
        } else {
            return value.toString();
        }
    }

    @Nullable
    public static String toStringOrNullSafe(@Nullable Object value) {
        try {
            return toStringOrNull(value);
        } catch (Exception ex) {
            LOGGER.debug("failed to string", ex);
            return null;
        }
    }

    public static String toStringOrDefault(@Nullable Object value, String def) {
        return firstNonNull(toStringOrNull(value), def);
    }

    public static String toStringNotBlank(Object value) {
        return checkNotBlank(toStringOrNull(value));
    }

    public static String toStringNotBlank(Object value, String message, Object... args) {
        return checkNotBlank(toStringOrNull(value), message, args);
    }

    public static String nullToEmpty(@Nullable String value) {
        return value == null ? "" : value;
    }

    public static String emptyToNull(@Nullable String value) {
        return "".equals(value) ? null : value;
    }

    public static String toStringOrEmpty(@Nullable Object value) {
        return nullToEmpty(toStringOrNull(value));
    }

    public static String toStringSafe(Object value) {
        try {
            return toStringNotBlank(value);
        } catch (Exception ex) {
            LOGGER.error(marker(), "error retrieving string value", ex);
            return format("error retrieving string value: %s", ex.toString());
        }
    }

    @Nullable
    public static String abbreviate(@Nullable Object value) {
        return abbreviate(toStringOrNull(value));
    }

    @Nullable
    public static String abbreviate(@Nullable String value) {
        return abbreviate(value, DEFAULT_MAX);
    }

    @Nullable
    public static String truncate(@Nullable String value, int len) {
        if (value == null || value.length() <= len) {
            return value;
        } else {
            return value.substring(0, len);
        }
    }

    @Nullable
    public static String truncateStartAndEnd(@Nullable String value, int len) {
        if (value == null || value.length() / 2 <= len) {
            return value;
        } else {
            return format("%s ...\n\n...\n\n... %s", value.substring(0, len / 2), value.substring(value.length() - len / 2, value.length()));
        }
    }

    @Nullable
    public static String addLineNumbers(@Nullable String value) {
        if (value == null) {
            return null;
        } else {
            List<String> list = list(Splitter.onPattern("\\R").splitToList(value));
            for (int i = 0; i < list.size(); i++) {
                list.set(i, format("%4s: %s", i + 1, list.get(i)));
            }
            return Joiner.on("\n").join(list);
        }
    }

    @Nullable
    public static String getLine(int number, @Nullable String value) {
        List<String> list = list(Splitter.onPattern("\\R").splitToList(nullToEmpty(value)));
        return list.get(number);
    }

    @Nullable
    public static String normalize(@Nullable String value) {
        if (value == null) {
            return value;
        } else {
            return value.replaceAll("\\R+", " ").replaceAll("[ \t]+", " ");
        }
    }

    @Nullable
    public static String normalizeNewlines(@Nullable String value) {
        if (value == null) {
            return value;
        } else {
            return value.replaceAll("\\R", "\n");
        }
    }

    @Nullable
    public static String normalizeId(@Nullable String value) {
        if (value == null) {
            return value;
        } else {
            return value.replaceAll("[^a-zA-Z0-9]", "");
        }
    }

    @Nullable
    public static String abbreviate(@Nullable String value, int max) {
        if (value == null) {
            return value;
        } else {
            value = normalize(value);
            if (value.length() <= max) {
                return value;
            } else if (max < 16) {
                return StringUtils.abbreviate(value, max);
            } else {
                String count = format(" (%s chars)", value.length());
                return StringUtils.abbreviate(value, max - count.length()) + count;
            }
        }
    }

    @Nullable
    public static String multilineWithOffset(@Nullable String value, int max, int offset) {
        if (isBlank(value) || value.length() < max) {
            return value;
        } else {
            List<String> list = list(Splitter.fixedLength(max).splitToList(value));//TODO break on words
            String padding = StringUtils.leftPad("", offset);
            for (int i = 1; i < list.size(); i++) {
                list.set(i, padding + list.get(i));
            }
            return Joiner.on("\n").join(list);
        }
    }

    @Nullable
    public static String multilineWithOffset(@Nullable String value, int offset) {
        if (isBlank(value)) {
            return value;
        } else {
            List<String> list = readLines(value);
            String padding = StringUtils.leftPad("", offset);
            return list.stream().map(l -> padding + l).collect(joining("\n"));
        }
    }

    @Nullable
    public static String multiline(@Nullable String value, int max) {
        return multilineWithOffset(value, max, 0);
    }

    public static List<String> readLines(String value) {
        try {
            return CharStreams.readLines(new StringReader(value));
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static String mapToLoggableString(Properties properties) {
        return mapToLoggableString(map(properties));
    }

    public static String mapToLoggableString(Map<?, ?> map) {
        return mapToLoggableString(null, map);
    }

    public static String mapToLoggableString(@Nullable String prefix, Map<?, ?> map) {
        if (map == null) {
            return "<null>";
        } else if (map.isEmpty()) {
            return firstNotNull(prefix, "") + "<empty map>";
        } else {
            int len = max(20, map.keySet().stream().map(k -> toStringOrEmpty(k)).map(String::length).collect(Collectors.maxBy(Comparator.naturalOrder())).get() + 5);
            return Joiner.on("\n").join(map.entrySet().stream()
                    .sorted(Comparator.comparing(e -> toStringOrNull(e.getKey())))
                    .map((entry) -> format("%s%-" + len + "s %50s = %s", firstNotNull(prefix, "\t\t"), entry.getKey(), "(" + classNameOrVoid(entry.getValue()) + ")", abbreviate(entry.getValue())))
                    .collect(toList()));
        }
    }

    public static String mapToLoggableStringInline(Map<?, ?> map) {
        return normalize(mapToLoggableString(map).replaceAll("\n", ", "));
    }

    public static String mapDifferencesToLoggableString(Map<String, ?> one, Map<String, ?> two) {
        Set<String> differences = set(one.keySet()).with(two.keySet()).without(k -> Objects.equals(one.get(k), two.get(k)));
        int len = max(20, set(one.keySet()).with(two.keySet()).stream().map(String::length).collect(Collectors.maxBy(Comparator.naturalOrder())).get() + 5);
        if (!differences.isEmpty()) {
            return differences.stream().sorted(Comparator.naturalOrder()).map(k -> format("\t\t%-" + len + "s %50s = %s -> %50s = %s", k, "(" + classNameOrVoid(one.get(k)) + ")", abbreviate(one.get(k)), "(" + classNameOrVoid(two.get(k)) + ")", abbreviate(two.get(k))))
                    .collect(joining("\n"));
        } else {
            return "<no changes>";
        }
    }

    public static Object mapDifferencesToLoggableStringLazy(Map<String, ?> one, Map<String, ?> two) {
        return lazyString(() -> mapDifferencesToLoggableString(one, two));
    }

    public static Object mapToLoggableStringLazy(Map<? extends String, ?> map) {
        return lazyString(() -> mapToLoggableString(map));
    }

    public static Object mapToLoggableStringInlineLazy(Map<?, ?> map) {
        return lazyString(() -> mapToLoggableStringInline(map));
    }

    public static String htmlToString(@Nullable String value) {
        if (isBlank(value)) {
            return "";
        } else {
            value = nullToEmpty(value)
                    .replaceAll("\\R", "\n")
                    .replaceAll("(?m)^[ \t]+", "")
                    .replaceAll("(?s)[<][!]--.*?--[>]", "")
                    .replaceAll("(?i)[<] *br */?[>]", "\n")
                    .replaceAll("(?i)[<] *div *[>]", "\n")
                    .replaceAll("[<][^>]+[>]", "")//TODO improve this
                    .replaceAll("\n\n+", "\n\n");
            value = StringEscapeUtils.unescapeHtml4(value);
            value = trim(value);
            return value;
        }
    }

    public static String htmlRecursiveToString(String url) {
        if (url.contains("%") || url.contains("+")) {
            try {
                return (htmlRecursiveToString(java.net.URLDecoder.decode(url, "UTF-8")));
            } catch (UnsupportedEncodingException ex) {
                throw runtime(ex);
            }
        }
        return url;
    }

    public static String markdownToString(@Nullable String value) {
        if (isBlank(value)) {
            return "";
        } else {
            MutableDataSet options = new MutableDataSet();
            com.vladsch.flexmark.util.ast.Document document = Parser.builder(options).build().parse(value);
            return htmlToString(HtmlRenderer.builder(options).build().render(document));
        }
    }

    public static String markdownToHtml(@Nullable String value) {
        if (isBlank(value)) {
            return value;
        } else {
            MutableDataSet options = new MutableDataSet();
            com.vladsch.flexmark.util.ast.Document document = Parser.builder(options).build().parse(value);
            return HtmlRenderer.builder(options).build().render(document);
        }
    }

    public static String classNameOrVoid(@Nullable Object value) {
        Class theClass = classOrVoid(value);
        return theClass.getName().startsWith("java.lang") ? theClass.getSimpleName() : theClass.getName();
    }

    public static Class classOrVoid(@Nullable Object value) {
        if (value == null) {
            return Void.class;
        } else {
            return value.getClass();
        }
    }

    public static String cmformat(String format, Object... params) {
        Matcher matcher = Pattern.compile("(%s|\\{\\})").matcher(checkNotBlank(format));
        StringBuilder builder = new StringBuilder();
        Iterator iterator = asList(checkNotNull(params)).iterator();
        while (matcher.find()) {
            checkArgument(iterator.hasNext(), "cmformat processing error: more params were expected");
            String value = String.valueOf(iterator.next());
            matcher.appendReplacement(builder, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(builder);
        checkArgument(!iterator.hasNext(), "cmformat processing error: too many params");
        return builder.toString();
    }

    public static String removeQuotes(String value) {
        Matcher matcher = Pattern.compile("^['\"](.*)['\"]$").matcher(value);
        checkArgument(matcher.find());
        return matcher.group(1);
    }

    public static boolean isBetweenQuotes(String value) {
        return value.matches("^(['].*[']|[\"].*[\"])$");
    }

}
