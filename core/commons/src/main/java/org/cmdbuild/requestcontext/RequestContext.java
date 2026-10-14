/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.requestcontext;

import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface RequestContext {

    static final String REQUEST_ID = "org.cmdbuild.requestcontext.REQUEST_ID",
            CONTEXT_SCOPE = "cm_context_scope", CONTEXT_SCOPE_DEFAULT = "default";

    Map<String, Object> getData();

    @Nullable
    <T> T get(String key);

    void set(String key, Object value);

    String getId();

    default <T> T get(String key, Supplier<T> loader) {
        checkNotNull(loader);
        T value = get(key);
        if (value == null) {
            value = loader.get();
            set(key, value);
        }
        return value;
    }

    default boolean has(String key) {
        return get(key) != null;
    }

    default String getContextScope() {
        return Iterables.getLast(getContextScopeStack());
    }

    default List<String> getContextScopeStack() {
        String value = get(CONTEXT_SCOPE);
        return isBlank(value) ? singletonList(CONTEXT_SCOPE_DEFAULT) : Splitter.on(",").splitToList(value);
    }

    default void pushContextScope(String value) {
        set(CONTEXT_SCOPE, Joiner.on(",").join(list(getContextScopeStack()).with(checkNotBlank(value))));
    }

    default void popContextScope() {
        List<String> stack = getContextScopeStack();
        checkArgument(stack.size() > 1);
        set(CONTEXT_SCOPE, Joiner.on(",").join(stack.subList(0, stack.size() - 1)));
    }

    default boolean hasContextScope(String scope) {
        return equal(getContextScope(), scope);
    }

    default boolean hasContextScopeDefault() {
        return hasContextScope(CONTEXT_SCOPE_DEFAULT);
    }

    @Nullable
    default String getRequestId() {
        return get(REQUEST_ID);
    }
}
