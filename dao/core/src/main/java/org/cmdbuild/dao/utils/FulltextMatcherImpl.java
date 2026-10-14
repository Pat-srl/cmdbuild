/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.utils;

import com.google.common.base.Splitter;
import java.util.Collection;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;

public class FulltextMatcherImpl<T> implements FulltextMatcher<T> {

    private final List<String> elements;

    public FulltextMatcherImpl(String query) {
        elements = list(Splitter.on(" ").omitEmptyStrings().trimResults().splitToList(checkNotBlank(query))).map(String::toLowerCase);
    }

    public static FulltextMatcher fulltextMatcher(String query) {
        return new FulltextMatcherImpl(query);
    }

    public static FulltextMatcher build(String query) {
        return new FulltextMatcherImpl(query);
    }

    @Override
    public boolean matches(@Nullable String value) {
        return isNotBlank(value) && elements.stream().allMatch(e -> value.toLowerCase().contains(e));
    }

    @Override
    public boolean matchesAny(@Nullable Collection<T> values) {
        List<String> list = list(CmCollectionUtils.nullToEmpty(values)).map(v -> toStringOrEmpty(v).toLowerCase());
        return elements.stream().allMatch(e -> list.stream().anyMatch(v -> v.contains(e)));
    }

}
