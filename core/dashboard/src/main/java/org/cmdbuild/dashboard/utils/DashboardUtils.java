package org.cmdbuild.dashboard.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.cmdbuild.dashboard.DashboardData;

import java.util.List;

import static com.google.common.collect.Streams.stream;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;

public class DashboardUtils {

    public static List<String> getCqlExprsInOrder(DashboardData dashboard) { //note: this MUST match the order in dashboard serialization (DashboardWs) for ecql filter processing !!
        return listOf(String.class).accept(l -> {
            stream(fromJson(dashboard.getConfig(), JsonNode.class).get("components").elements()).map(ObjectNode.class::cast).forEach(component -> {
                if (component.hasNonNull("dataSourceFilter") && !component.get("dataSourceFilter").asText().isBlank()) {
                    l.add(component.get("dataSourceFilter").asText());
                }
                if (component.hasNonNull("dataSourceParameters")) {
                    ArrayNode dataSourceParameters = (ArrayNode) component.get("dataSourceParameters");
                    for (JsonNode dataSourceParameter : dataSourceParameters) {
                        if (dataSourceParameter.hasNonNull("filter")) {
                            if (dataSourceParameter.get("filter").hasNonNull("expression")) {
                                if (!dataSourceParameter.get("filter").get("expression").asText().isBlank()) {
                                    l.add(dataSourceParameter.get("filter").get("expression").asText());
                                }
                            }
                        }
                    }
                }
            });
        });
    }
}
