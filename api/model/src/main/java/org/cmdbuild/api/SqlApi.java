/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Nullable;

public interface SqlApi {

    List<Map<String, Object>> query(String query);

    void execute(String query);

    default List<Map<String, Object>> query(String query, Object... params) {
        return query(format(query, params));
    }

    @Nullable
    default Map<String, Object> getRecord(String query, Object... params) {
        return getOnlyElement(query(query, params), null);
    }

    @Nullable
    default Object getValue(String query, Object... params) {
        return Optional.ofNullable(getOnlyElement(query(query, params), null)).map(m -> getOnlyElement(m.values(), null)).orElse(null);
    }

}
