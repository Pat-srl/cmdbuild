/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dashboard.DashboardData;
import org.cmdbuild.ecql.EcqlBindingInfo;
import org.cmdbuild.ecql.inner.EcqlExpressionImpl;
import org.cmdbuild.ecql.utils.EcqlUtils;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.cmdbuild.utils.lang.CmStringUtils;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.dashboard.utils.DashboardUtils.getCqlExprsInOrder;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.stream;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 *
 * @author ldare
 */
public class DashboardSerializationHelper {

    private final ObjectTranslationService objectTranslationService;

    public DashboardSerializationHelper(ObjectTranslationService objectTranslationService) {
        this.objectTranslationService = objectTranslationService;
    }

    public FluentMap<String, Object> serializeDetailedDashboardData(DashboardData dashboardData) {
        return serializeBasicDashboardData(dashboardData).with(
                "components", serializeComponents(dashboardData),
                "layout", fromJson(dashboardData.getConfig(), Map.class).get("layout"),//TODO improve this
                "parameters", serializeParameters(dashboardData)
        );
    }

    public FluentMap<String, Object> serializeBasicDashboardData(DashboardData dashboard) {
        return map(
                "_id", dashboard.getId(),
                "name", dashboard.getCode(),
                "description", dashboard.getDescription(),
                "_description_translation", objectTranslationService.translateDashboardDescription(dashboard.getCode(), dashboard.getDescription()),
                "active", dashboard.isActive(),
                "icon_font", dashboard.getIconFont()
        );
    }

    public PagedElements applySerializationToListDashboardData(List<DashboardData> listDashboardData, Boolean detailed, Integer limit, Integer offset) {
        return paged(listDashboardData, offset, limit).map(detailed ? this::serializeDetailedDashboardData : this::serializeBasicDashboardData);
    }

    private JsonNode serializeComponents(DashboardData dashboardData) { //note: this MUST match the order in DashboardUtils.getCqlExprsInOrder for ecql filter processing !!
        ListIterator<String> cqlExprsInOrder = getCqlExprsInOrder(dashboardData).listIterator();
        JsonNode components = fromJson(dashboardData.getConfig(), JsonNode.class).get("components");
        stream(components.elements()).map(ObjectNode.class::cast).forEach(component -> {
            if (component.hasNonNull("description")) {
                component.put("_description_translation",
                        objectTranslationService.translateDashboardComponentDescription(dashboardData.getCode(), component.get("_id").asText(), component.get("description").asText()));
            }
            if (component.hasNonNull("valueAxisLabel")) {
                component.put("_valueAxisLabel_translation",
                        objectTranslationService.translateDashboardComponentValueAxisLabel(dashboardData.getCode(), component.get("_id").asText(), component.get("valueAxisLabel").asText()));
            }
            if (component.hasNonNull("categoryAxisLabel")) {
                component.put("_categoryAxisLabel_translation",
                        objectTranslationService.translateDashboardComponentCategoryAxisLabel(dashboardData.getCode(), component.get("_id").asText(), component.get("categoryAxisLabel").asText()));
            }
            if (component.hasNonNull("labelField")) {
                component.put("_labelField_translation",
                        objectTranslationService.translateDashboardComponentLabelField(dashboardData.getCode(), component.get("_id").asText(), component.get("labelField").asText()));
            }
            if (component.hasNonNull("dataSourceFilter") && !component.get("dataSourceFilter").asText().isBlank()) {
                component.set("ecqlDataSourceFilter", new ObjectMapper().valueToTree(createEcqlFilterData(dashboardData.getCode(), cqlExprsInOrder)));
            }
            if (component.hasNonNull("dataSourceParameters")) {
                ArrayNode dataSourceParameters = (ArrayNode) component.get("dataSourceParameters");
                int i = 0;
                for (JsonNode dataSourceParameter : dataSourceParameters) {
                    String name = Optional.ofNullable(dataSourceParameter.get("name")).map(JsonNode::textValue).orElse(null);
                    if (isNotBlank(name)) {
                        String description = Optional.ofNullable(dataSourceParameter.get("description")).map(JsonNode::textValue).map(CmStringUtils::emptyToNull).orElse(name);
                        ((ObjectNode) dataSourceParameter).put("_description_translation",
                                objectTranslationService.translateDashboardComponentDataSourceParameter(dashboardData.getCode(), component.get("_id").asText(), Integer.toString(i++), description));
                    }
                    if (dataSourceParameter.hasNonNull("filter")) {
                        if (dataSourceParameter.get("filter").hasNonNull("expression")) {
                            if (!dataSourceParameter.get("filter").get("expression").asText().isBlank()) {
                                ObjectMapper mapper = new ObjectMapper();
                                ((ObjectNode) dataSourceParameter).set("ecqlFilter", mapper.valueToTree(createEcqlFilterData(dashboardData.getCode(), cqlExprsInOrder)));
                            }
                        }
                    }
                }
            }
        });
        return components;
    }

    private JsonNode serializeParameters(DashboardData dashboardData) { //note: this MUST match the order in DashboardUtils.getCqlExprsInOrder for ecql filter processing !!
        ListIterator<String> cqlExprsInOrder = getCqlExprsInOrder(dashboardData).listIterator();
        JsonNode parameters = fromJson(dashboardData.getConfig(), JsonNode.class).get("parameters");
        if(parameters == null) {
            return null;
        }
        stream(parameters.elements()).map(ObjectNode.class::cast).forEach(component -> {
            if (component.hasNonNull("description")) {
                component.put("_description_translation",
                        objectTranslationService.translateDashboardParametersDescription(dashboardData.getCode(), component.get("_id").asText(), component.get("description").asText()));
            }
            if (component.hasNonNull("emptyText")) {
                component.put("_emptyText_translation",
                        objectTranslationService.translateDashboardParametersEmptyText(dashboardData.getCode(), component.get("_id").asText(), component.get("emptyText").asText()));
            }
        });
        return parameters;
    }

    private Map<String, Object> createEcqlFilterData(String dashboardCode, ListIterator<String> cqlExprsInOrder) {
        EcqlBindingInfo ecqlBindingInfo = EcqlUtils.getEcqlBindingInfoForExpr(new EcqlExpressionImpl(cqlExprsInOrder.next()));
        String ecqlId = EcqlUtils.buildDashboardEcqlId(dashboardCode, cqlExprsInOrder.previousIndex());
        Map<String, Object> ecqlData = map("id", ecqlId, "bindings", map("server", ecqlBindingInfo.getServerBindings(), "client", ecqlBindingInfo.getClientBindings()));
        return ecqlData;
    }
}
