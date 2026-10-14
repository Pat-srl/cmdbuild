/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.etl.config.itemkey;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.nullToEmpty;
import static java.lang.String.format;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;

/**
 *
 * @author afelice
 */
public class ItemKeyUtils {

    public static String configItemKey(String descriptorKey, String code) {
        return format("%s#%s", checkNotBlank(descriptorKey), checkNotBlank(code));
    }

    public static ItemKey parseItemKey(String key) {
        return new ItemKeyImpl(key);
    }

    public static String getItemCodeFromKeyOrCode(String keyOrCode) {
        checkNotBlank(keyOrCode);
        return isItemKey(keyOrCode) ? parseItemKey(keyOrCode).getCode() : keyOrCode;
    }

    public static boolean isItemKey(@Nullable String keyOrCode) {
        return nullToEmpty(keyOrCode).matches("(.+)#(.+)");
    }

    public static String buildDescriptorKey(String code, int version) {
        return format("%s_v%s", checkNotBlank(code), checkNotNullAndGtZero(version));
    }

    public static String getDescriptorCodeFromKey(String key) {
        Matcher matcher = Pattern.compile("^(.+)_v[0-9]+$").matcher(checkNotBlank(key));
        checkArgument(matcher.matches(), "invalid config file key =< %s >", key);
        return checkNotBlank(matcher.group(1));
    }

    public static String checkIsValidItemCode(String code) {
        checkArgument(nullToEmpty(code).matches("[a-zA-Z0-9_-]+"), "invalid component code =< %s >", code);
        return code;
    }

    private static class ItemKeyImpl implements ItemKey {

        private final String code, descriptorKey;

        public ItemKeyImpl(String key) {
            Matcher matcher = Pattern.compile("(.+)#(.+)").matcher(checkNotBlank(key));
            checkArgument(matcher.matches(), "invalid item key =< %s >", key);
            this.descriptorKey = checkNotBlank(matcher.group(1));
            this.code = checkIsValidItemCode(matcher.group(2));
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getDescriptorKey() {
            return descriptorKey;
        }

    }

}
