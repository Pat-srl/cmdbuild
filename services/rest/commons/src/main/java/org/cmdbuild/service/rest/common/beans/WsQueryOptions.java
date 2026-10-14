/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.common.beans;

import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.ImmutableList.toImmutableList;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ONLY_GRID_ATTRS;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.POSITION_OF;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.POSITION_OF_GOTOPAGE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SORT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;

public class WsQueryOptions extends WsQueryOptionsParams {

    private final DaoQueryOptions query;
    private final boolean detailed;

    public WsQueryOptions(
            @QueryParam("attrs") List<String> attrs,
            @QueryParam(ONLY_GRID_ATTRS) @DefaultValue(FALSE) Boolean onlyGridAttrs,
            @QueryParam(FILTER) String filterStr,
            @QueryParam("query") String query,
            @QueryParam(SORT) String sort,
            @QueryParam(LIMIT) Long limit,
            @QueryParam(START) Long offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed,
            @QueryParam(POSITION_OF) Long positionOf,
            @QueryParam(POSITION_OF_GOTOPAGE) @DefaultValue(TRUE) Boolean goToPage) {
        this.query = DaoQueryOptionsImpl.builder()
                .withFilter(filterStr)
                .withSorter(sort)
                .withPaging(offset, limit)
                .withPositionOf(positionOf, goToPage)
                .withAttrs(CmCollectionUtils.nullToEmpty(attrs).stream().flatMap(a -> Splitter.on(",").omitEmptyStrings().trimResults().splitToList(nullToEmpty(a)).stream()).distinct().collect(toImmutableList()))
                .withOnlyGridAttrs(onlyGridAttrs)
                .accept(b -> {
                    if (isNotBlank(query)) {
                        b.and(CmdbFilterImpl.builder().withFulltextFilter(query).build());
                    }
                }).build();
        this.detailed = detailed;
    }

    public DaoQueryOptions getQuery() {
        return query;
    }

    public boolean isDetailed() {
        return detailed;
    }

    public long getOffset() {
        return query.getOffset();
    }

    public long getLimit() {
        if (query.getLimit() != null) {
            return query.getLimit();
        } else {
            return 0;
        }
    }

    public boolean isPaged() {
        return getQuery().isPaged();
    }

}
