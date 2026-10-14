/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.stylerules;

import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.dao.utils.CmFilterUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class GisStyleRulesUtils {

    public static String serializeRules(List<Pair<CmdbFilter, Map<String, Object>>> rules) {
        return toJson(rules.stream().map(r -> map("condition", fromJson(CmFilterUtils.serializeFilter(r.getLeft()), JsonNode.class),
                "style", r.getRight()
        )).collect(toList()));
    }

    public static List<Pair<CmdbFilter, Map<String, Object>>> parseRules(String value) {
        return list(fromJson(value, JsonNode.class).elements()).stream().map(r -> {
            CmdbFilter filter = CmFilterUtils.parseFilter(toJson(r.get("condition")));
            Map<String, Object> style = fromJson(toJson(r.get("style")), MAP_OF_OBJECTS);
            return Pair.of(filter, style);
        }).collect(toImmutableList());
    }
}
