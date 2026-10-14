/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import com.google.common.base.Splitter;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.gis.GisService;
import org.cmdbuild.gis.stylerules.*;
import org.cmdbuild.service.rest.v4.model.WsRulesetData;
import org.cmdbuild.utils.lang.CmConvertUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.gis.stylerules.GisStyleRulesUtils.parseRules;
import static org.cmdbuild.gis.stylerules.GisStyleRulesetAccessType.AT_PRIVATE;
import static org.cmdbuild.gis.stylerules.GisStyleRulesetAccessType.AT_PUBLIC;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.emptyToNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;

/**
 * @author ldare
 */
@Component
public class GeoStyleRulesWsCommand {

    private final GisStyleRulesService gisStyleRulesService;
    private final GisService gisService;
    private final UserClassService userClassService;

    public GeoStyleRulesWsCommand(GisStyleRulesService gisStyleRulesService, GisService gisService, UserClassService userClassService) {
        this.gisStyleRulesService = checkNotNull(gisStyleRulesService);
        this.gisService = checkNotNull(gisService);
        this.userClassService = checkNotNull(userClassService);
    }

    public List<GisStyleRuleset> doReadAll(String classId, String filterStr) {
        List<GisStyleRuleset> list;
        if (equal(classId, "_ANY")) {
            list = gisStyleRulesService.getAll().stream().filter(r -> userClassService.getUserClass(r.getOwnerClassName()).hasGisAttributeReadPermission(r.getGisAttribute().getLayerName())).collect(toList());
        } else {
            list = gisStyleRulesService.getForClass(classId).stream().filter(r -> userClassService.getUserClass(classId).hasGisAttributeReadPermission(r.getGisAttribute().getLayerName())).collect(toList());
        }
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        filter.checkHasOnlySupportedFilterTypes(FilterType.FULLTEXT);
        if (filter.hasFulltextFilter()) {
            list = list.stream().filter(r -> nullToEmpty(r.getDescription()).toLowerCase().contains(filter.getFulltextFilter().getQuery().toLowerCase())
                    || r.getCode().toLowerCase().contains(filter.getFulltextFilter().getQuery().toLowerCase())).collect(toList());//TODO improve this
        }
        return list;
    }

    public GisStyleRuleset doRead(String classId, Long rulesetId) {
        GisStyleRuleset rulesetById = gisStyleRulesService.getById(rulesetId);
        checkArgument(userClassService.getUserClass(classId).hasGisAttributeReadPermission(rulesetById.getGisAttribute().getLayerName()), "User not allowed to read the related geo attribute");
        return rulesetById;
    }

    public GisStyleRuleset doCreate(WsRulesetData data) {
        return gisStyleRulesService.create(dataToRules(data).build());
    }

    public GisStyleRuleset doUpdate(Long rulesetId, WsRulesetData data) {
        return gisStyleRulesService.update(dataToRules(data).withId(rulesetId).build());
    }

    public void doDelete(Long rulesetId) {
        gisStyleRulesService.delete(rulesetId);
    }

    public Map<Long, Boolean> doUpdateVisibility(String classId, Map<Long, Boolean> rulesets) {
        checkArgument(equal(classId, "_ANY"), "cannot perform update on single class");
        rulesets.forEach((k, v) -> {
            GisStyleRuleset rulesetById = gisStyleRulesService.getById(k);
            if (equal(rulesetById.getAccessType(), AT_PRIVATE) && v) {
                gisStyleRulesService.update(GisStyleRulesetImpl.copyOf(rulesetById).withParams(GisStyleRulesetParamsImpl.copyOf(rulesetById.getParams()).withAccessType(AT_PUBLIC).build()).build());
            } else if (equal(rulesetById.getAccessType(), AT_PUBLIC) && !v) {
                gisStyleRulesService.update(GisStyleRulesetImpl.copyOf(rulesetById).withParams(GisStyleRulesetParamsImpl.copyOf(rulesetById.getParams()).withAccessType(AT_PRIVATE).build()).build());
            }
        });
        return rulesets;
    }

    public Map<Long, Map<String, Object>> doApplyRules(String cards, Long rulesetId) {
        Set<Long> cardIds = isBlank(cards) ? null : emptyToNull(Splitter.on(",").splitToList(cards).stream().map(CmConvertUtils::toLong).collect(toSet()));
        return gisStyleRulesService.applyRulesOnCards(rulesetId, cardIds);
    }

    public Map<Long, Map<String, Object>> doTestRules(String cards, WsRulesetData data) {
        Set<Long> cardIds = isBlank(cards) ? null : emptyToNull(Splitter.on(",").splitToList(cards).stream().map(CmConvertUtils::toLong).collect(toSet()));
        GisStyleRuleset ruleset = dataToRules(data).build();
        return gisStyleRulesService.applyRulesOnCards(ruleset, cardIds);
    }


    public GisStyleRulesetImpl.GisStyleRulesetImplBuilder dataToRules(WsRulesetData data) {
        return GisStyleRulesetImpl.builder()
                .withCode(data.getName())
                .withDescription(data.getDescription())
                .withFunction(data.getFunction())
                .withGisAttribute(gisService.getGisAttributeIncludeInherited(data.getOwner(), data.getAttribute()))
                .withParams(b -> b.withSegments(data.getSegments()).withAnalysisType(parseEnumOrNull(data.getAnalysistype(), GisStyleRulesetAnalysisType.class)).withClassAttribute(data.getClassattribute()).withAccessType(parseEnumOrNull(data.getAccessType(), GisStyleRulesetAccessType.class)))
                .withRules(parseRules(toJson(data.getRules())));
    }
}
