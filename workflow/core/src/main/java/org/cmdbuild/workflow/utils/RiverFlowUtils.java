/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.utils;

import static com.google.common.base.Predicates.in;
import java.util.Map;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_FLOW_DATA;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import org.cmdbuild.workflow.inner.FlowConversionMode;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.river.engine.RiverFlowStatus;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import static org.cmdbuild.workflow.utils.FlowDataSerializerUtils.RIVER_FLOW_ATTRS_FROM_FLOW_DATA;
import static org.cmdbuild.workflow.utils.FlowDataSerializerUtils.RIVER_FLOW_STATUS_ATTR;
import static org.cmdbuild.workflow.utils.FlowDataSerializerUtils.deserializeFlowData;

public class RiverFlowUtils {

    public static Map<String, Object> getRiverFlowData(RiverPlan riverPlan, Flow card, FlowConversionMode mode) {
        return switch (mode) {
            case CM_FULL ->
                deserializeFlowData(card.getString(ATTR_FLOW_DATA), riverPlan);
            case CM_LEAN ->
                deserializeFlowData(card.getString(ATTR_FLOW_DATA), riverPlan, in(RIVER_FLOW_ATTRS_FROM_FLOW_DATA));
            default ->
                throw new IllegalArgumentException("unsupported flow conversion mode = " + mode);
        };
    }

    public static RiverFlowStatus getRiverFlowStatus(Flow card) {
        return convert(deserializeFlowData(card.getString(ATTR_FLOW_DATA), RIVER_FLOW_STATUS_ATTR::equals).get(RIVER_FLOW_STATUS_ATTR), RiverFlowStatus.class);
    }
}
