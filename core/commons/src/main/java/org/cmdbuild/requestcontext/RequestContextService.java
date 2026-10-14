/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.requestcontext;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import static java.util.Collections.emptyMap;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public interface RequestContextService extends RequestContextActiveService {

    final String REQUEST_CONTEXT_SESSION_ID_KEY = "org.cmdbuild.auth.session.REQUEST_CONTEXT_SESSION_ID_KEY";

    <T> RequestContextHolder<T> createRequestContextHolder(Supplier<T> initialValueSupplier);

    <T> RequestContextHolder<T> createRequestContextHolder(String key, Supplier<T> initialValueSupplier);

    RequestContext getRequestContext();

    void initCurrentRequestContext(String identifier, Map<String, Object> data);

    void destroyCurrentRequestContext();

    boolean hasRequestContext();

    default void initCurrentRequestContext(String identifier, RequestContext copyOf) {
        initCurrentRequestContext(identifier, copyOf.getData());
    }

    default void initCurrentRequestContext(String identifier, Object... data) {
        initCurrentRequestContext(identifier, map(data));
    }

    default void initCurrentRequestContext(String identifier) {
        initCurrentRequestContext(identifier, emptyMap());
    }

    @Nullable
    default <T> T get(String key) {
        return getRequestContext().get(key);
    }

    default void set(String key, Object value) {
        getRequestContext().set(key, value);
    }

    default <T> RequestContextHolder<T> createRequestContextHolder() {
        return createRequestContextHolder(Suppliers.ofInstance(null));
    }

    default <T> RequestContextHolder<T> createRequestContextHolder(String key) {
        return createRequestContextHolder(key, Suppliers.ofInstance(null));
    }

    default String getRequestContextId() {
        return getRequestContext().getId();
    }

    @Nullable
    default String getRequestContextIdOrNull() {
        return hasRequestContext() ? getRequestContextId() : null;
    }

}
