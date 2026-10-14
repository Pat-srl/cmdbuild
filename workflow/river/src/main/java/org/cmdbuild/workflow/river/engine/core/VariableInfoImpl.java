/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.core;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.workflow.river.engine.RiverVariableInfo;

public class VariableInfoImpl<V extends Serializable> implements RiverVariableInfo<V> {

    private final Optional<V> defaultValue;
    private final String key;
    private final Class<V> javaType;
    private final Map<String, String> attributes;

    public VariableInfoImpl(String key, Class<V> javaType, Optional<V> defaultValue, Map<String, String> attributes) {
        this.key = checkNotBlank(key);
        this.javaType = checkNotNull(javaType);
        this.defaultValue = defaultValue.map(v -> convert(v, javaType));
        this.attributes = ImmutableMap.copyOf(attributes);
    }

    public VariableInfoImpl(String key, Class<V> javaType, Optional<V> defaultValue) {
        this(key, javaType, defaultValue, emptyMap());
    }

    public static <V extends Serializable> RiverVariableInfo<V> variableInfo(String key, Class<V> javaType) {
        return variableInfo(key, javaType, null);
    }

    public static <V extends Serializable> RiverVariableInfo<V> variableInfo(String key, Class<V> javaType, @Nullable V defaultValue) {
        return new VariableInfoImpl<>(key, javaType, Optional.ofNullable(defaultValue));
    }

    @Override
    public Optional<V> getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public boolean isBasicType() {
        return true;
    }

    @Override
    public Class<V> getJavaType() {
        return javaType;
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "VariableInfoImpl{" + "defaultValue=" + defaultValue + ", key=" + key + ", javaType=" + javaType + '}';
    }

}
