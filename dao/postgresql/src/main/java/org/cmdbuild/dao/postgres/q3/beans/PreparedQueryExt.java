/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3.beans;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.MoreCollectors.toOptional;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.core.q3.PreparedQuery;

public interface PreparedQueryExt extends PreparedQuery {

    String getQuery();

    List<SelectElement> getSelect();

    default boolean hasSelectForAttr(String name) {
        return getSelectForAttrOrNull(name) != null;
    }

    default SelectElement getSelectForAttr(String name) {
        return checkNotNull(getSelectForAttrOrNull(name), "select not found for name =< %s >", name);
    }

    @Nullable
    default SelectElement getSelectForAttrOrNull(String name) {
        return getSelect().stream().filter(s -> equal(s.getName(), name)).collect(toOptional()).orElse(null);
    }
}
