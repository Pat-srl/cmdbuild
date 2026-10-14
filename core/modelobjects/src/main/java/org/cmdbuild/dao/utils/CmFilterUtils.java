/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.utils;

import org.cmdbuild.data.filter.EcqlFilter;
import org.cmdbuild.data.filter.AttributeFilter;
import org.cmdbuild.data.filter.RelationFilterCardInfo;
import org.cmdbuild.data.filter.AttributeFilterCondition;
import org.cmdbuild.data.filter.AttributeFilterConditionOperator;
import org.cmdbuild.data.filter.AttachmentFilter;
import org.cmdbuild.data.filter.RelationFilterRule;
import org.cmdbuild.data.filter.ContextFilter;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.RelationFilter;
import org.cmdbuild.data.filter.FunctionFilterEntry;
import org.cmdbuild.data.filter.CompositeFilter;
import org.cmdbuild.data.filter.FulltextFilter;
import org.cmdbuild.data.filter.FunctionFilter;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import jakarta.annotation.Nullable;
import java.io.IOException;
import static java.lang.String.format;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.data.filter.AttributeFilter.AttributeFilterMode;
import static org.cmdbuild.data.filter.AttributeFilterConditionOperator.FULLTEXT;
import org.cmdbuild.data.filter.CompositeFilter.CompositeFilterMode;
import static org.cmdbuild.data.filter.FilterType.ATTRIBUTE;
import static org.cmdbuild.data.filter.FilterType.COMPOSITE;
import static org.cmdbuild.data.filter.FilterType.CONTEXT;
import org.cmdbuild.data.filter.RelationFilterRule.RelationFilterRuleType;
import org.cmdbuild.data.filter.beans.AttachmentFilterImpl;
import org.cmdbuild.data.filter.beans.AttributeFilterConditionImpl;
import org.cmdbuild.data.filter.beans.AttributeFilterImpl;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl.CmdbFilterBuilder;
import org.cmdbuild.data.filter.beans.CompositeFilterImpl;
import org.cmdbuild.data.filter.beans.ContextFilterImpl;
import org.cmdbuild.data.filter.beans.CqlFilterImpl;
import org.cmdbuild.data.filter.beans.EcqlFilterImpl;
import org.cmdbuild.data.filter.beans.FulltextFilterImpl;
import org.cmdbuild.data.filter.beans.FunctionFilterEntryImpl;
import org.cmdbuild.data.filter.beans.FunctionFilterImpl;
import org.cmdbuild.data.filter.beans.RelationFilterCardInfoImpl;
import org.cmdbuild.data.filter.beans.RelationFilterImpl;
import org.cmdbuild.data.filter.beans.RelationFilterRuleImpl;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.AND_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.ATTACHMENT_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.ATTRIBUTE_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.CLASSNAME_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.COMPOSITE_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.CQL_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.ECQL_CONTEXT_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.ECQL_ID_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.ECQL_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.ELEMENTS_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.FULL_TEXT_QUERY_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.FUNCTION_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.FUNCTION_NAME_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.MODE_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.NOT_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.OPERATOR_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.OR_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.RELATION_CARDS_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.RELATION_CARD_CLASSNAME_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.RELATION_CARD_ID_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.RELATION_DOMAIN_DIRECTION;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.RELATION_DOMAIN_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.RELATION_FILTER;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.RELATION_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.RELATION_TYPE_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.SIMPLE_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.VALUE_KEY;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.illegalArgument;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.json.JSONObject;

public class CmFilterUtils {

    protected final static ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        MAPPER.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        MAPPER.registerModule(new JsonOrgModule());
    }

    private final static CmdbFilter NOOP_FILTER = CmdbFilterImpl.builder().build();

    public static CmdbFilter noopFilter() {
        return NOOP_FILTER;
    }

    public static CmdbFilter mergeOrCompose(Collection<CmdbFilter> filters) {
        CmdbFilter filter = noopFilter();
        for (CmdbFilter f : filters) {
            filter = mergeOrCompose(f, filter);
        }
        return filter;
    }

    public static CmdbFilter mergeOrCompose(CmdbFilter one, CmdbFilter two) {
        if (canMerge(one, two)) {
            return merge(one, two);
        } else {
            return CompositeFilterImpl.and(one, two);
        }
    }

    public static boolean canMerge(CmdbFilter one, CmdbFilter two) {
        return (!(one.hasCqlFilter() && two.hasCqlFilter()))
                && (!(one.hasFulltextFilter() && two.hasFulltextFilter()))
                && (!(one.hasContextFilter() && two.hasContextFilter()))
                && (!(one.hasEcqlFilter() && two.hasEcqlFilter()))
                && (!(one.hasFunctionFilter() && two.hasFunctionFilter()))
                && (!(one.hasRelationFilter() && two.hasRelationFilter()))
                && !one.hasCompositeFilter() && !two.hasCompositeFilter();
    }

    public static CmdbFilter merge(CmdbFilter one, CmdbFilter two) {
        try {
            checkArgument(canMerge(one, two));
            CmdbFilterBuilder builder = CmdbFilterImpl.builder()
                    .withAttributeFilter(mergeAttributeFilter(one.hasAttributeFilter() ? one.getAttributeFilter() : null, two.hasAttributeFilter() ? two.getAttributeFilter() : null));
            if (one.hasCqlFilter()) {
                builder.withCqlFilter(one.getCqlFilter());
            } else if (two.hasCqlFilter()) {
                builder.withCqlFilter(two.getCqlFilter());
            }
            if (one.hasFulltextFilter()) {
                builder.withFulltextFilter(one.getFulltextFilter());
            } else if (two.hasFulltextFilter()) {
                builder.withFulltextFilter(two.getFulltextFilter());
            }
            if (one.hasEcqlFilter()) {
                builder.withEcqlFilter(one.getEcqlFilter());
            } else if (two.hasEcqlFilter()) {
                builder.withEcqlFilter(two.getEcqlFilter());
            }
            if (one.hasFunctionFilter()) {
                builder.withFunctionFilter(one.getFunctionFilter());
            } else if (two.hasFunctionFilter()) {
                builder.withFunctionFilter(two.getFunctionFilter());
            }
            if (one.hasRelationFilter()) {
                builder.withRelationFilter(one.getRelationFilter());
            } else if (two.hasRelationFilter()) {
                builder.withRelationFilter(two.getRelationFilter());
            }
            if (one.hasContextFilter()) {
                builder.withContextFilter(one.getContextFilter());
            } else if (two.hasContextFilter()) {
                builder.withContextFilter(two.getContextFilter());
            }
            return builder.build();
        } catch (Exception ex) {
            throw runtime(ex, "unable to merge card filters");
        }
    }

    public static CmdbFilter mapNamesInFilter(CmdbFilter source, Function<String, String> mapping) {
        if (source.hasAttributeFilter() || source.hasCompositeFilter()) {
            return CmdbFilterImpl.copyOf(source).accept(b -> {
                if (source.hasAttributeFilter()) {
                    b.withAttributeFilter(mapNamesInFilter(source.getAttributeFilter(), mapping));
                }
                if (source.hasCompositeFilter()) {
                    b.withCompositeFilter(new CompositeFilterImpl(source.getCompositeFilter().getMode(), source.getCompositeFilter().getElements().stream().map(e -> mapNamesInFilter(e, mapping)).collect(toImmutableList())));
                }
            }).build();
        } else {
            return source;
        }
    }

    public static CmdbFilter mapValuesInFilter(CmdbFilter source, Map<String, String> mapping) {
        if (source.hasFilterOfType(ATTRIBUTE, COMPOSITE, CONTEXT)) {
            return CmdbFilterImpl.copyOf(source).accept(b -> {
                if (source.hasAttributeFilter()) {
                    b.withAttributeFilter(mapValuesInFilter(source.getAttributeFilter(), mapping));
                }
                if (source.hasContextFilter()) {
                    b.withContextFilter(mapValuesInFilter(source.getContextFilter(), mapping));
                }
                if (source.hasCompositeFilter()) {
                    b.withCompositeFilter(new CompositeFilterImpl(source.getCompositeFilter().getMode(), source.getCompositeFilter().getElements().stream().map(e -> mapValuesInFilter(e, mapping)).collect(toImmutableList())));
                }
            }).build();
        } else {
            return source;
        }
    }

    public static CmdbFilter expandFulltextFilterForAttrs(CmdbFilter source, Collection<String> attrs) {
        if (!source.hasFulltextFilter()) {
            return source;
        } else {
            return CmdbFilterImpl.copyOf(source).withFulltextFilter((FulltextFilter) null).build()
                    .and(AttributeFilterImpl.or(attrs.stream().map(a -> AttributeFilterConditionImpl.builder().withKey(a).withOperator(FULLTEXT).withValues(source.getFulltextFilter().getQuery()).build().toAttributeFilter()).collect(toImmutableList())).toCmdbFilters());
        }
    }

    private static AttributeFilter mapNamesInFilter(AttributeFilter source, Function<String, String> mapping) {
        if (source.isSimple()) {
            AttributeFilterCondition condition = source.getCondition();
            return AttributeFilterImpl.simple(AttributeFilterConditionImpl.copyOf(condition).withKey(firstNotBlank(mapping.apply(condition.getKey()), condition.getKey())).build());
        } else {
            return AttributeFilterImpl.build(source.getMode(), source.getElements().stream().map(e -> mapNamesInFilter(e, mapping)).collect(toImmutableList()));
        }
    }

    private static AttributeFilter mapValuesInFilter(AttributeFilter source, Map<String, String> mapping) {
        if (source.isSimple()) {
            AttributeFilterCondition condition = source.getCondition();
            return AttributeFilterImpl.simple(AttributeFilterConditionImpl.copyOf(condition).withValues(condition.getValues().stream().map(v -> mapping.getOrDefault(v, v)).collect(toImmutableList())).build());
        } else {
            return AttributeFilterImpl.build(source.getMode(), source.getElements().stream().map(e -> mapValuesInFilter(e, mapping)).collect(toImmutableList()));
        }
    }

    private static ContextFilter mapValuesInFilter(ContextFilter source, Map<String, String> mapping) {
        return new ContextFilterImpl(source.getClassName(), mapping.getOrDefault(source.getRecordId(), source.getRecordId()));
    }

    @Nullable
    private static AttributeFilter mergeAttributeFilter(@Nullable AttributeFilter one, @Nullable AttributeFilter two) {
        if (one != null && one.isTrue()) {
            one = null;
        }
        if (two != null && two.isTrue()) {
            two = null;
        }
        if (one == null && two == null) {
            return null;
        } else if (one != null ^ two != null) {
            return firstNonNull(one, two);
        } else {
            return AttributeFilterImpl.and(one, two);
        }
    }

    public static String serializeFilter(@Nullable CmdbFilter filter) {
        try {
            Map map = filter == null ? emptyMap() : toJsonMap(filter);
            return MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException ex) {
            throw runtime(ex);
        }
    }

    public static List<String> getAttributeFilterAttributes(CmdbFilter filter) {
        if (filter.hasAttributeFilter()) {
            if (filter.getAttributeFilter().isSimple()) {
                return list(filter.getAttributeFilter().getCondition().getKey());
            } else {
                List<String> attributes = list();
                filter.getAttributeFilter().getElements().forEach(e -> attributes.add(e.getCondition().getKey()));
                return attributes;
            }
        }
        return list();
    }

    @Deprecated()//"for legacy support only"
    public static JSONObject toJsonObject(@Nullable CmdbFilter filter) {
        try {
            if (filter == null) {
                return new JSONObject();
            } else {
                return MAPPER.readValue(serializeFilter(filter), JSONObject.class);
            }
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    private static Map toJsonMap(CmdbFilter filter) {
        Map map = map();
        if (filter.hasAttributeFilter()) {
            map.put(ATTRIBUTE_KEY, toJsonMap(filter.getAttributeFilter()));
        }
        if (filter.hasCqlFilter()) {
            map.put(CQL_KEY, filter.getCqlFilter().getCqlExpression());
        }
        if (filter.hasFulltextFilter()) {
            map.put(FULL_TEXT_QUERY_KEY, filter.getFulltextFilter().getQuery());
        }
        if (filter.hasRelationFilter()) {
            map.put(RELATION_KEY, toJsonMap(filter.getRelationFilter()));
        }
        if (filter.hasFunctionFilter()) {
            map.put(FUNCTION_KEY, filter.getFunctionFilter().getFunctions().stream().map((f) -> map(FUNCTION_NAME_KEY, f.getName())).collect(toList()));
        }
        if (filter.hasEcqlFilter()) {
            map.put(ECQL_KEY, map(ECQL_ID_KEY, filter.getEcqlFilter().getEcqlId(), ECQL_CONTEXT_KEY, filter.getEcqlFilter().getJsContext()));
        }
        if (filter.hasCompositeFilter()) {
            map.put(COMPOSITE_KEY, map(MODE_KEY, filter.getCompositeFilter().getMode(), ELEMENTS_KEY, filter.getCompositeFilter().getElements().stream().map(e -> toJsonMap(e))));
        }
        return map;
    }

    private static Map toJsonMap(AttributeFilter attributeFilter) {
        Map attribute = map();
        if (attributeFilter.isSimple()) {
            AttributeFilterCondition condition = attributeFilter.getCondition();
            attribute.put(SIMPLE_KEY, map(
                    ATTRIBUTE_KEY, condition.getKey(),
                    OPERATOR_KEY, condition.getOperator().name().toLowerCase(),
                    VALUE_KEY, list(condition.getValues())));
        } else {
            attribute.put(attributeFilter.getMode().name().toLowerCase(), attributeFilter.getElements().stream().map(CmFilterUtils::toJsonMap).collect(toList()));
        }
        return attribute;
    }

    @Deprecated()//"for legacy support only"
    public static CmdbFilter fromJson(JSONObject jsonFilter) {
        try {
            String json = MAPPER.writeValueAsString(jsonFilter);
            return parseFilter(json);
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static CmdbFilter parseFilter(@Nullable String jsonFilter) {
        if (isBlank(jsonFilter)) {
            return NOOP_FILTER;
        } else {
            try {
                JsonNode jsonNode = MAPPER.readTree(jsonFilter);
                CmdbFilterBuilder builder = CmdbFilterImpl.builder();
                jsonNode.fieldNames().forEachRemaining(f -> {
                    switch (f.toLowerCase()) {
                        case ATTRIBUTE_KEY ->
                            builder.withAttributeFilter(parseAttributeFilter(jsonNode.get(f)));
                        case "cql" -> //note: CQL_KEY.toLowercase()
                            builder.withCqlFilter(new CqlFilterImpl(jsonNode.get(f).asText()));
                        case FULL_TEXT_QUERY_KEY -> {
                            if (isNotBlank(jsonNode.get(f).asText())) {
                                builder.withFulltextFilter(new FulltextFilterImpl(jsonNode.get(f).asText()));
                            }
                        }
                        case RELATION_KEY ->
                            builder.withRelationFilter(parseRelationFilter(jsonNode.get(f)));
                        case FUNCTION_KEY ->
                            builder.withFunctionFilter(parseFunctionFilter(jsonNode.get(f)));
                        case ECQL_KEY ->
                            builder.withEcqlFilter(parseEcqlFilter(jsonNode.get(f)));
                        case ATTACHMENT_KEY ->
                            builder.withAttachmentFilter(parseAttachmentFilter(jsonNode.get(f)));
                        case COMPOSITE_KEY ->
                            builder.withCompositeFilter(parseCompositeFilter(jsonNode.get(f)));
                        default ->
                            throw runtime("invalid json filter key =< %s >", f);
                    }
                });
                return builder.build();
            } catch (Exception ex) {
                throw illegalArgument(ex, "error deserializing json filter =< %s >", jsonFilter);
            }
        }
    }

    private static CompositeFilter parseCompositeFilter(JsonNode jsonNode) {
        List elements = list();
        JsonNode elementsNodes = jsonNode.get(ELEMENTS_KEY);
        elementsNodes.forEach(e -> {
            elements.add(parseFilter(e.toString()));
        });
        return new CompositeFilterImpl(parseEnum(jsonNode.get("mode").asText(), CompositeFilterMode.class), elements);
    }

    private static AttachmentFilter parseAttachmentFilter(JsonNode jsonNode) {
        return new AttachmentFilterImpl(parseFilter(jsonNode.toString()));
    }

    private static AttributeFilter parseAttributeFilterSimpleCondition(JsonNode simpleConditionNode) {
        String key = simpleConditionNode.get(ATTRIBUTE_KEY).asText();
        String className = simpleConditionNode.has(CLASSNAME_KEY) ? simpleConditionNode.get(CLASSNAME_KEY).asText() : null;
        AttributeFilterConditionOperator operator = parseEnum(simpleConditionNode.get(OPERATOR_KEY).asText(), AttributeFilterConditionOperator.class);
        List<String> values = list();
        JsonNode valuesNode = simpleConditionNode.get(VALUE_KEY);
        if (valuesNode != null && !valuesNode.isNull()) {
            if (valuesNode.isArray()) {
                for (int i = 0; i < valuesNode.size(); i++) {
                    values.add(valuesNode.get(i).asText());
                }
            } else {
                checkArgument(!valuesNode.isObject());
                values.add(valuesNode.asText());
            }
        }
        return AttributeFilterConditionImpl.builder()
                .withOperator(operator)
                .withValues(values)
                .withKey(key)
                .withClassName(className)
                .build().toAttributeFilter();
    }

    private static AttributeFilter parseAttributeFilter(JsonNode jsonNode) {
        if (jsonNode.has(SIMPLE_KEY)) {
            return parseAttributeFilterSimpleCondition(jsonNode.get(SIMPLE_KEY));
        } else if (jsonNode.has(NOT_KEY)) {
            JsonNode notElement = jsonNode.get(NOT_KEY);
            return AttributeFilterImpl.not(parseAttributeFilter(notElement));
        } else {
            JsonNode elementNodes;
            AttributeFilterMode mode;
            if (jsonNode.has(AND_KEY)) {
                elementNodes = jsonNode.get(AND_KEY);
                mode = AttributeFilterMode.AND;
            } else if (jsonNode.has(OR_KEY)) {
                elementNodes = jsonNode.get(OR_KEY);
                mode = AttributeFilterMode.OR;
            } else {
                throw new IllegalArgumentException(format("unable to parse illegal attribute filter = %s (invalid top-level key, must be one of 'and','or','simple','not')", abbreviate(toStringOrNull(jsonNode))));
            }
            List<AttributeFilter> elements = list();
            checkArgument(elementNodes.isArray());
            for (int i = 0; i < elementNodes.size(); i++) {
                elements.add(parseAttributeFilter(elementNodes.get(i)));
            }
            return AttributeFilterImpl.andOr(mode, elements);
        }
    }

    private static RelationFilter parseRelationFilter(JsonNode jsonNode) {
        checkArgument(jsonNode.isArray());
        List<RelationFilterRule> rules = list();
        for (int i = 0; i < jsonNode.size(); i++) {
            rules.add(parseRelationFilterRule(jsonNode.get(i)));
        }
        return new RelationFilterImpl(rules);
    }

    private static RelationFilterRule parseRelationFilterRule(JsonNode jsonNode) {
        try {
            List<RelationFilterCardInfo> cardInfos;
            if (jsonNode.has(RELATION_CARDS_KEY)) {
                cardInfos = list();
                JsonNode cards = jsonNode.get(RELATION_CARDS_KEY);
                for (int i = 0; i < cards.size(); i++) {
                    JsonNode card = cards.get(i);
                    String className = checkNotNull(card.get(RELATION_CARD_CLASSNAME_KEY), "must set 'className' field for 'cards' filter entry").asText();
                    long cardId = checkNotNull(card.get(RELATION_CARD_ID_KEY), "must set 'id' field for 'cards' filter entry").asLong();
                    cardInfos.add(new RelationFilterCardInfoImpl(className, cardId));
                }
            } else {
                cardInfos = null;
            }
            RelationFilterRuleType type = parseEnum(jsonNode.get(RELATION_TYPE_KEY).textValue(), RelationFilterRuleType.class);
            AttributeFilter filter = jsonNode.has(RELATION_FILTER) ? parseAttributeFilter(jsonNode.get(RELATION_FILTER)) : null;
            return RelationFilterRuleImpl.builder()
                    .withDirection(jsonNode.has(RELATION_DOMAIN_DIRECTION) ? RelationFilterRule.RelationFilterDirection.valueOf(jsonNode.get(RELATION_DOMAIN_DIRECTION).textValue()) : null)
                    .withDomain(jsonNode.get(RELATION_DOMAIN_KEY).textValue())
                    .withType(type)
                    .withCardInfos(cardInfos)
                    .withFilter(filter)
                    .build();
        } catch (Exception ex) {
            throw runtime(ex, "error parsing relation filter rule from node = %s", abbreviate(jsonNode));
        }
    }

    private static List toJsonMap(RelationFilter relationFilter) {
        return relationFilter.getRelationFilterRules().stream().map((r) -> toJsonMap(r)).collect(toList());
    }

    private static Map toJsonMap(RelationFilterRule rule) {
        Map map = map(
                RELATION_DOMAIN_DIRECTION, rule.getDirection().name(),
                RELATION_DOMAIN_KEY, rule.getDomain(),
                RELATION_TYPE_KEY, rule.getType().name().toLowerCase()
        );
        if (rule.isOneOf()) {
            map.put(RELATION_CARDS_KEY, rule.getCardInfos().stream().map((c) -> map(RELATION_CARD_ID_KEY, c.getId(), RELATION_CARD_CLASSNAME_KEY, c.getClassName())).collect(toList()));
        }
        if (!rule.getFilter().isTrue()) {
            map.put(RELATION_FILTER, toJsonMap(rule.getFilter()));
        }
        return map;
    }

    private static FunctionFilter parseFunctionFilter(JsonNode jsonNode) {
        checkArgument(jsonNode.isArray());
        List<FunctionFilterEntry> filters = list();
        for (int i = 0; i < jsonNode.size(); i++) {
            JsonNode entryNode = jsonNode.get(i);
            filters.add(new FunctionFilterEntryImpl(entryNode.get(FUNCTION_NAME_KEY).asText()));
        }
        return new FunctionFilterImpl(filters);
    }

    private static EcqlFilter parseEcqlFilter(JsonNode jsonNode) {
        return new EcqlFilterImpl(jsonNode.get(ECQL_ID_KEY).asText(), jsContextAsString(jsonNode));
    }

    private static String jsContextAsString(JsonNode jsonNode) {
        JsonNode context = jsonNode.get(ECQL_CONTEXT_KEY);
        if (context == null || context.isNull()) {
            return toJson(emptyMap());
        } else if (context.isObject()) {
            try {
                return MAPPER.writeValueAsString(context);
            } catch (JsonProcessingException ex) {
                throw runtime(ex);
            }
        } else {
            return context.asText();
        }
    }

    public static String checkFilterSyntax(String filter) {
        try {
            parseFilter(filter);
            return filter;
        } catch (Exception ex) {
            throw runtime(ex, "invalid filter syntax for filter =< %s >", abbreviate(filter));
        }
    }
}
