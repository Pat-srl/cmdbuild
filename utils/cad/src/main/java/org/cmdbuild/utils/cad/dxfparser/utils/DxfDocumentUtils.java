/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class DxfDocumentUtils {

    public static Map<String, String> parseMetadata(@Nullable String payload) {
        if (isBlank(payload)) {
            return emptyMap();
        } else {
            return mapOf(String.class, String.class).accept(m -> {
                Matcher matcher = Pattern.compile("([a-zA-Z0-9_.-]+)\\s*=\\s*([^\n\r]+)").matcher(payload.replaceAll(Pattern.quote("^M^J"), "\n")); //TODO check this, processing of ^M^J parts
                while (matcher.find()) {
                    String keyword = checkNotBlank(matcher.group(1)),
                            value = checkNotBlank(matcher.group(2));
                    checkArgument(m.put(keyword, value) == null, "duplicate metadata found for keyword =< %s >", keyword);
                }
            });
        }
    }

}
