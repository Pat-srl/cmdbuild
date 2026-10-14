/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v4.utils;

import static java.util.Collections.emptyMap;
import java.util.Map;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.common.utils.PositionOf;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class PositionOfUtils {

    public static Map<String, Object> handlePositionOfAndGetMeta(DaoQueryOptions queryOptions, PagedElements paged) {
        long offset = queryOptions.getOffset();
        if (paged.hasPositionOf()) {
            PositionOf positionOf = paged.getPositionOf();
            Map positionMeta;
            if (positionOf.foundCard()) {
                offset = positionOf.getActualOffset();
                positionMeta = map("found", true,
                        "positionInPage", positionOf.getPositionInPage(),
                        "positionInTable", positionOf.getPositionInTable(),
                        "pageOffset", positionOf.getPageOffset());
            } else {
                positionMeta = map("found", false);
            }
            return map("positions", map(queryOptions.getPositionOf(), positionMeta), START, offset, LIMIT, queryOptions.getLimit());
        } else {
            return emptyMap();
        }
    }
}
