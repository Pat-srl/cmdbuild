/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.widget.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class WidgetValueUtils {

    public static String buildWidgetStringValue(String value) {
        return format("\"%s\"", checkNotNull(value));
    }

    @Nullable
    public static String parseWidgetStringValue(@Nullable String value) {
        if (isBlank(value)) {
            return null;
        } else {
            Matcher matcher = Pattern.compile("^\"(.+)\"$").matcher(value);
            checkArgument(matcher.matches(), "syntax error for output parameter =< %s >", value);
            return matcher.group(1);
        }
    }

    @Nullable
    public static String parseWidgetStringValueOrRawValue(@Nullable String value) {
        if (isBlank(value)) {
            return null;
        } else {
            Matcher matcher = Pattern.compile("^\"(.+)\"$").matcher(value);
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                return value;
            }
        }
    }
}
