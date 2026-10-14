/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api;

import jakarta.annotation.Nullable;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import java.util.Map;

public interface SystemApi {

    <T> T getService(String name);

    <T> T getService(Class<T> classe);

    @Nullable
    String getSystemConfig(String key);

    Map<String, String> getConfig();

    void setConfig(Map<String, String> config);

    SqlApi sql();

    LookupApi lookup();

    PluginApi plugin();

    /**
     * A <i>dummy implementation</i> (that throws
     * <code>UnsupportedOperationException</code> on all methods) if
     * schemaCollector module not available.
     *
     * @return
     */
    SchemaCollectorApi schemaCollector();

    void reload();

    void restart();

    void shutdown();

    void dropCache(String cache);

    Object eval(String script, Map<String, Object> data);

    default Object eval(String script) {
        return eval(script, emptyMap());
    }

    default void dropCache(String... cache) {
        asList(cache).forEach(this::dropCache);
    }

    @Nullable
    default String getConfig(String key) {
        return getSystemConfig(key);
    }

    default void setConfig(String key, String value) {
        setConfig(singletonMap(key, value));
    }

}
