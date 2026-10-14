/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.core.q3;

import com.google.common.collect.ImmutableList;
import java.util.List;

public class CachedPreparedQuery implements PreparedQuery {

    private final List<ResultRow> list;

    public CachedPreparedQuery(PreparedQuery inner) {
        list = ImmutableList.copyOf(inner.run());
    }

    @Override
    public List<ResultRow> run() {
        return list;
    }

    public static CachedPreparedQuery cached(PreparedQuery query) {
        return new CachedPreparedQuery(query);
    }

}
