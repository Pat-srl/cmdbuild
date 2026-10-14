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
import org.cmdbuild.ecql.EcqlBindingInfo;
import org.cmdbuild.ecql.inner.EcqlExpressionImpl;
import org.cmdbuild.ecql.utils.EcqlUtils;
import org.cmdbuild.homepage.HomepageData;
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
import static org.cmdbuild.homepage.utils.HomepageUtils.getCqlExprsInOrder;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.stream;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class HomepageSerializationHelper {

    private final ObjectTranslationService objectTranslationService;

    public HomepageSerializationHelper(ObjectTranslationService objectTranslationService) {
        this.objectTranslationService = objectTranslationService;
    }

    public FluentMap<String, Object> serializeDetailedHomepageData(HomepageData homepageData) {
        return serializeBasicHomepageData(homepageData).with(
                "components", serializeComponents(homepageData),
                "layout", fromJson(homepageData.getConfig(), Map.class).get("layout"),//TODO improve this
                "parameters", fromJson(homepageData.getConfig(), Map.class).getOrDefault("parameters", emptyList())
        );
    }

    public FluentMap<String, Object> serializeBasicHomepageData(HomepageData homepageData) {
        return map(
                "_id", homepageData.getId(),
                "name", homepageData.getCode(),
                "description", homepageData.getDescription(),
                "_description_translation", objectTranslationService.translateHomepageDescription(homepageData.getCode(), homepageData.getDescription()),
                "active", homepageData.isActive(),
                "icon_font", homepageData.getIconFont(),
                "group", homepageData.getGroupName()
        );
    }

    public PagedElements applySerializationToListHomepageData(List<HomepageData> listHomepageData, Boolean detailed, Integer limit, Integer offset) {
        return paged(listHomepageData, offset, limit).map(detailed ? this::serializeDetailedHomepageData : this::serializeBasicHomepageData);
    }

    private JsonNode serializeComponents(HomepageData homepageData) {
        ListIterator<String> cqlExprsInOrder = getCqlExprsInOrder(homepageData).listIterator();
        JsonNode components = fromJson(homepageData.getConfig(), JsonNode.class).get("components");
        stream(components.elements()).map(ObjectNode.class::cast).forEach(component -> {
            if (component.hasNonNull("description")) {
                component.put("_description_translation",
                        objectTranslationService.translateHomepageComponentDescription(homepageData.getCode(), component.get("_id").asText(), component.get("description").asText()));
            }
            if (component.hasNonNull("valueAxisLabel")) {
                component.put("_valueAxisLabel_translation",
                        objectTranslationService.translateHomepageComponentValueAxisLabel(homepageData.getCode(), component.get("_id").asText(), component.get("valueAxisLabel").asText()));
            }
            if (component.hasNonNull("categoryAxisLabel")) {
                component.put("_categoryAxisLabel_translation",
                        objectTranslationService.translateHomepageComponentCategoryAxisLabel(homepageData.getCode(), component.get("_id").asText(), component.get("categoryAxisLabel").asText()));
            }
            if (component.hasNonNull("labelField")) {
                component.put("_labelField_translation",
                        objectTranslationService.translateHomepageComponentLabelField(homepageData.getCode(), component.get("_id").asText(), component.get("labelField").asText()));
            }
            if (component.hasNonNull("dataSourceFilter") && !component.get("dataSourceFilter").asText().isBlank()) {
                component.set("ecqlDataSourceFilter", new ObjectMapper().valueToTree(createEcqlFilterData(homepageData.getCode(), cqlExprsInOrder)));
            }
            if (component.hasNonNull("dataSourceParameters")) {
                ArrayNode dataSourceParameters = (ArrayNode) component.get("dataSourceParameters");
                int i = 0;
                for (JsonNode dataSourceParameter : dataSourceParameters) {
                    String name = Optional.ofNullable(dataSourceParameter.get("name")).map(JsonNode::textValue).orElse(null);
                    if (isNotBlank(name)) {
                        String description = Optional.ofNullable(dataSourceParameter.get("description")).map(JsonNode::textValue).map(CmStringUtils::emptyToNull).orElse(name);
                        ((ObjectNode) dataSourceParameter).put("_description_translation",
                                objectTranslationService.translateHomepageComponentDataSourceParameter(homepageData.getCode(), component.get("_id").asText(), Integer.toString(i++), description));
                    }
                    if (dataSourceParameter.hasNonNull("filter")) {
                        if (dataSourceParameter.get("filter").hasNonNull("expression")) {
                            if (!dataSourceParameter.get("filter").get("expression").asText().isBlank()) {
                                ObjectMapper mapper = new ObjectMapper();
                                ((ObjectNode) dataSourceParameter).set("ecqlFilter", mapper.valueToTree(createEcqlFilterData(homepageData.getCode(), cqlExprsInOrder)));
                            }
                        }
                    }
                }
            }
        });
        return components;
    }

    private Map<String, Object> createEcqlFilterData(String homepageCode, ListIterator<String> cqlExprsInOrder) {
        EcqlBindingInfo ecqlBindingInfo = EcqlUtils.getEcqlBindingInfoForExpr(new EcqlExpressionImpl(cqlExprsInOrder.next()));
        String ecqlId = EcqlUtils.buildHomepageEcqlId(homepageCode, cqlExprsInOrder.previousIndex());
        Map<String, Object> ecqlData = map("id", ecqlId, "bindings", map("server", ecqlBindingInfo.getServerBindings(), "client", ecqlBindingInfo.getClientBindings()));
        return ecqlData;
    }

}
