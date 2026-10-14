/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.utils;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Splitter;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class ConfigUtils {

    public static boolean hasNamespace(String namespace, String key) {
        return key.startsWith(namespace + ".");
    }

    public static boolean hasCmNamespace(String key) {
        return key.startsWith("org.cmdbuild.");
    }

    @Nullable
    public static String addNamespaceToKey(String namespace, @Nullable String key) {
        if (isBlank(key) || hasNamespace(namespace, key)) {
            return key;
        } else {
            return namespace + "." + key;
        }
    }

    public static String stripNamespaceFromKey(String namespace, String key) {
        checkArgument(hasNamespace(namespace, key));
        return key.substring(namespace.length() + 1);
    }

    public static String stripOptionalNamespaceFromKey(String namespace, String key) {
        if (hasNamespace(namespace, key)) {
            key = stripNamespaceFromKey(namespace, key);
        }
        return key;
    }

    public static String getNamespace(String key) {
        return Splitter.on(".").splitToList(key).stream().limit(3).collect(joining("."));
    }

    public static String getFilenameFromNamespace(String key) {
        return format("%s.conf", checkNotBlank(Splitter.on(".").splitToList(key).get(2)));
    }
}
