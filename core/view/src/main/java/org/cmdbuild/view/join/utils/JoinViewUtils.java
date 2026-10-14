/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.view.join.utils;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.padStart;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.String.format;
import java.math.BigInteger;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import org.apache.commons.lang3.tuple.Pair;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class JoinViewUtils {

    private final static int PAD_SIZE = 20;

    public static BigInteger assembleJoinId(List<Long> ids) {
        String id = ids.stream().map(l -> padStart(l.toString(), PAD_SIZE, '0')).collect(joining(""));
        checkArgument(id.length() == ids.size() * PAD_SIZE);
        return new BigInteger(id);
    }

    public static List<Long> parseJoinId(BigInteger id) {
        String value = id.toString();
        value = padStart(value, (value.length() + PAD_SIZE - 1) / PAD_SIZE * PAD_SIZE, '0');
        return Splitter.fixedLength(PAD_SIZE).splitToList(value).stream().map(Long::valueOf).collect(toImmutableList());
    }

    public static List<Long> parseJoinId(String id) {
        return parseJoinId(new BigInteger(id));
    }

    public static String buildAttributeExpr(String tableAlias, String attributeName) {
        checkName(tableAlias);
        checkName(attributeName);
        return format("%s.%s", tableAlias, attributeName);
    }

    public static Pair<String, String> parseAttributeExpr(String expr) {
        Matcher matcher = Pattern.compile("^(.+)[.](.+)$").matcher(expr);
        checkArgument(matcher.matches(), "invalid join attr value pattern =< %s >", expr);
        String tableAlias = matcher.group(1), attributeName = matcher.group(2);
        return Pair.of(tableAlias, attributeName);
    }

    public static void checkName(String name) {
        checkNotBlank(name);
        checkArgument(!name.contains("."), "invalid name =< %s >", name);
    }

}
