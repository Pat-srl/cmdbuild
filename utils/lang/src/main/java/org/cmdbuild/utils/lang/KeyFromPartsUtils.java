/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import com.google.common.base.Splitter;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class KeyFromPartsUtils {

    public static String key(Iterable parts) {
        return serializeListOfStrings("|", "\\", parts);
    }

    public static String key(Object... parts) {
        return serializeListOfStrings("|", "\\", list(parts));
    }

    public static String key(String... parts) {
        return serializeListOfStrings("|", "\\", list(parts));
    }

    public static List<String> unkey(@Nullable String key) {
        return parseListOfStrings("|", "\\", key);
    }

    public static String serializeListOfStrings(String separator, Iterable parts) {
        return serializeListOfStrings(separator, "\\", parts);
    }

    public static String serializeListOfStrings(String separator, String escape, Iterable parts) {
        checkNotBlank(separator);
        checkNotBlank(escape);
        checkArgument(!Objects.equals(separator, escape) && separator.length() == 1 && escape.length() == 1, "invalid separator/escape");
        return list((Iterable<Object>) parts).map(CmStringUtils::toStringOrEmpty)
                .map(s -> s.replaceAll(Pattern.quote(escape), Matcher.quoteReplacement(escape + escape)).replaceAll(Pattern.quote(separator), Matcher.quoteReplacement(escape + separator)))
                .collect(joining(separator));
    }

    public static List<String> parseListOfStrings(String separator, @Nullable String key) {
        return parseListOfStrings(separator, "\\", key);
    }

    public static List<String> parseListOfStrings(String separator, String escape, @Nullable String key) {
        checkNotBlank(separator);
        checkNotBlank(escape);
        checkArgument(!Objects.equals(separator, escape) && separator.length() == 1 && escape.length() == 1, "invalid separator/escape");
        return list(Splitter.on(separator).omitEmptyStrings().trimResults().splitToList(defaultString(key)
                .replaceAll(Pattern.quote(escape + escape), "KeyFromPartsUtils_ESCAPE_TOKEN_1")
                .replaceAll(Pattern.quote(escape + separator), "KeyFromPartsUtils_ESCAPE_TOKEN_2")))
                .map(s -> s.replaceAll("KeyFromPartsUtils_ESCAPE_TOKEN_2", Matcher.quoteReplacement(separator)).replaceAll("KeyFromPartsUtils_ESCAPE_TOKEN_1", Matcher.quoteReplacement(escape)))
                .immutableCopy();
    }

}
