/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.utils;

import java.util.Collection;
import java.util.function.Predicate;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public interface FulltextMatcher<T extends Object> extends Predicate<T> {

    boolean matches(@Nullable String value);

    boolean matchesAny(Collection<T> values);

    @Override
    default boolean test(T t) {
        return matches(toStringOrNull(t));
    }

    default boolean matchesAny(String... values) {
        return matchesAny((Collection) list(values));
    }

}
