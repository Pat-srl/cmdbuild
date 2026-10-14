/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.postgres.q3.stats;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.invoke.MethodHandles;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.dao.driver.postgres.q3.stats.DaoStatsQueryOptionsImpl.emptyOptions;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.jsonValueToString;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaoStatsQueryOptionsUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static DaoStatsQueryOptions statsQueryOptionsFromJson(@Nullable String json) {
        try {
            if (isBlank(json)) {
                return emptyOptions();
            } else {
                return DaoStatsQueryOptionsImpl.builder().accept(b -> {
                    ObjectNode query = fromJson(json, ObjectNode.class);
                    if (query.has("aggregate")) {
                        query.get("aggregate").elements().forEachRemaining(a -> {
                            b.withAggregateQuery(jsonValueToString(a.get("attribute")), parseEnumOrDefault(jsonValueToString(a.get("operation")), AggregateOperation.SUM));
                        });
                    }
                }).build();
            }
        } catch (Exception ex) {
            throw runtime(ex, "error parsing stats query options, invalid value =< %s >", json);
        }
    }
}
