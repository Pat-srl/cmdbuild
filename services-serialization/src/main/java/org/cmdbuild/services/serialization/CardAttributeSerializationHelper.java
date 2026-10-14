/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/License0s/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.services.serialization;

import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

import static java.lang.String.format;

/**
 *
 * @author afelice
 */
public interface CardAttributeSerializationHelper<T> {

    CmMapUtils.FluentMap<String, Object> serialize(T data);

    default String prefix(String prefix, String suffix) {
        return format("%s_%s", prefix, suffix);
    }

    /**
     * Prefix all keys with <code><prefix>_</code>
     *
     * @param prefix
     * @param serialization
     * @return
     */
    default FluentMap<String, Object> prefixKeysWith(String prefix, FluentMap<String, Object> serialization) {
        return serialization.mapKeys(k -> prefix(prefix, k));
    }

    default String underscorePrefix(String attributeName, String suffix) {
        return format("_%s_%s", attributeName, suffix);
    }

    /**
     * Prefix all keys with <code>_&lt;attributeName&gt;_</code>
     *
     * @param attributeName
     * @param serialization
     * @return
     */
    default FluentMap<String, Object> underscorePrefixKeysWith(String attributeName, FluentMap<String, Object> serialization) {
        return serialization.mapKeys(k -> underscorePrefix(attributeName, k));
    }

    default String mapRepr(String attributeName, String suffix) {
        return format("%s.%s", attributeName, suffix);
    }

    /**
     * All values as map (<code>&lt;attributeName>&gt;.&lt;key&gt;</code>,
     * <code>&lt;value&gt;</code>)
     *
     * @param attributeName
     * @param serialization
     * @return
     */
    default CmMapUtils.FluentMap<String, Object> asMapStr(String attributeName, FluentMap<String, Object> serialization) {
        return serialization.mapKeys(k -> mapRepr(attributeName, k));
    }

}
