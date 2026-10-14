/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

import static com.google.common.base.Strings.nullToEmpty;
import java.util.Map.Entry;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class ConfigEntryImpl implements ConfigEntry {

    private final String key, value, storedValue;
    private final boolean isEncrypted;

    public ConfigEntryImpl(Entry<String, String> entry) {
        this(entry.getKey(), entry.getValue());
    }

    public ConfigEntryImpl(String key, @Nullable String value) {
        this(key, value, value, false);
    }

    public ConfigEntryImpl(String key, @Nullable String value, @Nullable String storedValue, boolean isEncrypted) {
        this.key = checkNotBlank(key);
        this.value = nullToEmpty(value);
        this.storedValue = nullToEmpty(storedValue);
        this.isEncrypted = isEncrypted;
    }

    @Override
    @Nullable
    public String getStoredValue() {
        return storedValue;
    }

    @Override
    public boolean isEncrypted() {
        return isEncrypted;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    @Nullable
    public String getValue() {
        return value;
    }

    @Override
    public ConfigEntry withKey(String newKey) {
        return new ConfigEntryImpl(newKey, value, storedValue, isEncrypted);
    }

    @Override
    public String toString() {
        return "ConfigEntry{" + "key=" + key + ", value=" + value + '}';
    }

}
