/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.serializationhelpers;


import org.cmdbuild.gis.stylerules.GisStyleRuleset;

import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.cmdbuild.gis.stylerules.GisStyleRulesUtils.serializeRules;
import static org.cmdbuild.gis.stylerules.GisStyleRulesetAccessType.AT_PRIVATE;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
public class GisStyleRulesetSerializer {

    public static Object serializeRuleset(GisStyleRuleset rules) {
        return map("_id", rules.getId(),
                "name", rules.getCode(),
                "description", rules.getDescription(),
                "owner", rules.getOwnerClassName(),
                "attribute", rules.getGisAttribute().getLayerName(),
                "type", rules.hasFunction() ? "function" : "table",
                "function", rules.getFunction(),
                "analysistype", serializeEnum(rules.getAnalysisType()),
                "segments", rules.getSegments(),
                "classattribute", rules.getClassAttribute(),
                "userOwner", rules.getUserOwner(),
                "accessType", serializeEnum(rules.getAccessType()),
                "_public", !rules.getAccessType().equals(AT_PRIVATE),
                "rules", serializeRules(rules.getRules())
        );
    }

    public static Object serializeRulesResult(Map<Long, Map<String, Object>> res) {
        return res.entrySet().stream().map(e -> map("_id", e.getKey(), "style", e.getValue())).collect(toList());
    }
}
