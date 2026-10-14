/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.utils;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import static com.google.common.base.Predicates.not;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import static com.google.common.collect.Maps.filterKeys;
import jakarta.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeUtc;
import static org.cmdbuild.utils.date.CmDateUtils.toJavaDate;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.isArrayOfPrimitiveOrWrapper;
import static org.cmdbuild.utils.lang.CmConvertUtils.isPrimitiveOrWrapper;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrNull;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.workflow.model.WorkflowException;
import org.cmdbuild.workflow.river.engine.RiverFlowStatus;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.RiverVariableInfo;
import org.cmdbuild.workflow.type.LookupType;
import org.cmdbuild.workflow.type.ReferenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowDataSerializerUtils {

    private static final String FLOW_DATA_TOKEN_ID = "i",
            FLOW_DATA_TOKEN_CLASS_TYPE = "T",
            FLOW_DATA_TOKEN_LOOKUP_TYPE = "t",
            FLOW_DATA_TOKEN_DESCRIPTION = "d",
            FLOW_DATA_TOKEN_CODE = "c";

    public static final String RIVER_FLOW_STATUS_ATTR = "RiverFlowStatus";

    public static final Set<String> RIVER_FLOW_ATTRS_FROM_FLOW_DATA = ImmutableSet.of(RIVER_FLOW_STATUS_ATTR);

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String serializeRiverFlowData(Map<String, Object> data, Classe classe, RiverPlan plan, RiverFlowStatus flowStatus) {

        data = filterOutAttributesStoredInCard(data, classe);

        data = filterOutAttributesNotInPlan(data, plan);

        data = filterOutValuesEqualToDefault(data, plan);

        data = map(data).with(RIVER_FLOW_STATUS_ATTR, flowStatus.name());

        return serializeFlowData(data);
    }

    public static String serializeFlowData(Map<String, Object> map) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("serialize flow data = \n\n{}\n", mapToLoggableString(map));
        }
        Map<String, Object> data = map.entrySet().stream().map((e) -> {
            try {
                return Pair.of(e.getKey(), serializeValue(e.getValue()));
            } catch (Exception ex) {
                throw new WorkflowException(ex, "error serializing flow value for key = %s value = %s (%s)", e.getKey(), e.getValue(), getClassOfNullable(e.getValue()));
            }
        }).collect(toMap(Pair::getKey, Pair::getValue));
        return toJson(data);
    }

    public static Map<String, Object> deserializeFlowData(@Nullable String data) {
        return deserializeFlowData(data, null, null);
    }

    public static Map<String, Object> deserializeFlowData(@Nullable String data, Predicate<String> keyFilter) {
        return deserializeFlowData(data, null, keyFilter);
    }

    public static Map<String, Object> deserializeFlowData(@Nullable String data, @Nullable RiverPlan riverPlan) {
        return deserializeFlowData(data, riverPlan, Predicates.alwaysTrue());
    }

    public static Map<String, Object> deserializeFlowData(@Nullable String data, @Nullable RiverPlan riverPlan, Predicate<String> keyFilter) {
        if (isBlank(data)) {
            return emptyMap();
        } else {
            Map<String, Object> rawData = filterKeys((Map) fromJson(data, MAP_OF_OBJECTS), keyFilter);
            if (riverPlan == null) {
                return rawData;
            } else {
                return riverPlan.getGlobalVariables().values().stream().collect(toMap(RiverVariableInfo::getKey, (variable) -> {
                    if (rawData.containsKey(variable.getKey())) {
                        return deserializeValue(rawData.get(variable.getKey()), variable);
                    } else {
                        return variable.getDefaultValue().orElse(null);
                    }
                })).with(filterKeys(rawData, not(riverPlan.getGlobalVariables()::containsKey)));
            }
        }
    }

    @Nullable
    public static Object serializeValue(@Nullable Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof ReferenceType referenceType) {
            return serializeReferenceType(referenceType);
        } else if (value instanceof ReferenceType[] referenceTypes) {
            return stream(referenceTypes).map(FlowDataSerializerUtils::serializeReferenceType).collect(toList());
        } else if (value instanceof LookupType lookupType) {
            return serializeLookupType(lookupType);
        } else if (value instanceof LookupType[] lookupTypes) {
            return stream(lookupTypes).map(FlowDataSerializerUtils::serializeLookupType).collect(toList());
        } else if (value instanceof Date) {
            return toIsoDateTimeUtc(toDateTime(value));
        } else {
            checkArgument(isPrimitiveOrWrapper(value) || (value instanceof String[]), "unable to serialize value = %s, unsupported class = %s", value, getClassOfNullable(value));
            return value;
        }
    }

    @Nullable
    public static Object deserializeValue(@Nullable Object value, RiverVariableInfo variable) {
        try {
            if (value == null) {
                return null;
            } else if (Date.class.equals(variable.getJavaType())) {
                return toJavaDate(value);
            } else if (isPrimitiveOrWrapper(variable.getJavaType()) || isArrayOfPrimitiveOrWrapper(variable.getJavaType())) {
                return convert(value, variable.getJavaType());
            } else if (ReferenceType.class.equals(variable.getJavaType())) {
                return deserializeReferenceType(value);
            } else if (ReferenceType[].class.equals(variable.getJavaType())) {
                return ((List) convert(value, List.class).stream().map(FlowDataSerializerUtils::deserializeReferenceType).collect(toList())).toArray(ReferenceType[]::new);
            } else if (LookupType.class.equals(variable.getJavaType())) {
                return deserializeLookupType(value);
            } else if (LookupType[].class.equals(variable.getJavaType())) {
                return ((List) convert(value, List.class).stream().map(FlowDataSerializerUtils::deserializeLookupType).collect(toList())).toArray(LookupType[]::new);
            } else {
                throw new WorkflowException("unable to deserialize variable of type = %s", variable);
            }
        } catch (Exception ex) {
            throw new WorkflowException(ex, "unable to deserialize value = '%s' (%s)", value, getClassOfNullable(value).getName());
        }
    }

    private static Map<String, Object> filterOutAttributesStoredInCard(Map<String, Object> data, Classe classe) {
        return Maps.filterKeys(data, not(classe::hasAttribute));
    }

    private static Map<String, Object> filterOutAttributesNotInPlan(Map<String, Object> data, RiverPlan plan) {
        return Maps.filterKeys(data, plan::hasGlobalVariable);
    }

    private static Map<String, Object> filterOutValuesEqualToDefault(Map<String, Object> data, RiverPlan plan) {
        return Maps.filterEntries(data, (e) -> {
            return !equal(e.getValue(), plan.getDefaultValueOrNull(e.getKey()));
        });
    }

    private static Object serializeReferenceType(ReferenceType referenceType) {
        return map(FLOW_DATA_TOKEN_ID, referenceType.getId(), FLOW_DATA_TOKEN_CLASS_TYPE, referenceType.getClassName(), FLOW_DATA_TOKEN_DESCRIPTION, referenceType.getDescription(), FLOW_DATA_TOKEN_CODE, referenceType.getCode());
    }

    private static Object serializeLookupType(LookupType lookupType) {
        return map(FLOW_DATA_TOKEN_ID, lookupType.getId(), FLOW_DATA_TOKEN_LOOKUP_TYPE, lookupType.getType(), FLOW_DATA_TOKEN_CODE, lookupType.getCode(), FLOW_DATA_TOKEN_DESCRIPTION, lookupType.getDescription());
    }

    @Nullable
    private static ReferenceType deserializeReferenceType(Object value) {
        Map map = (Map) value;
        Long id = toLongOrNull(map.get(FLOW_DATA_TOKEN_ID));
        if (isNotNullAndGtZero(id)) {
            return new ReferenceType(toStringOrNull(map.get(FLOW_DATA_TOKEN_CLASS_TYPE)), id, toStringOrNull(map.get(FLOW_DATA_TOKEN_DESCRIPTION)), toStringOrNull(map.get(FLOW_DATA_TOKEN_CODE)));
        } else {
            return null;
        }
    }

    @Nullable
    private static LookupType deserializeLookupType(Object value) {
        Map map = (Map) value;
        Long id = toLongOrNull(map.get(FLOW_DATA_TOKEN_ID));
        if (isNotNullAndGtZero(id)) {
            return new LookupType(id, toStringNotBlank(map.get(FLOW_DATA_TOKEN_LOOKUP_TYPE)), toStringOrNull(map.get(FLOW_DATA_TOKEN_DESCRIPTION)), toStringOrNull(map.get(FLOW_DATA_TOKEN_CODE)));
        } else {
            return null;
        }
    }

}
