package org.cmdbuild.workflow.model;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.common.beans.LookupValue;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_FLOW_STATUS;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import org.cmdbuild.workflow.utils.FlowStatusUtils;
import static org.cmdbuild.workflow.utils.FlowStatusUtils.toFlowStatus;

public interface Flow extends Card {

    Long getCardId();

    String getFlowId();

    @Override
    Process getType();

    FlowStatus getStatus();

    @Nullable
    PlanInfo getPlanInfoOrNull();

    default Map<String, Object> getWidgetData() {
        return emptyMap();
    }

    default PlanInfo getPlanInfo() {
        return checkNotNull(getPlanInfoOrNull(), "plan info not available for flow = %s", this);
    }

    default String getPlanId() {
        return getPlanInfo().getDefinitionId();
    }

    default List<FlowActivity> getFlowActivities() {
        throw new UnsupportedOperationException("TODO");
    }

    default List<String> getPreviousExecutors() {
        throw new UnsupportedOperationException("TODO");
    }

    default FlowActivity getFlowActivityByDefinitionId(String definitionId) {
        return getFlowActivities().stream().filter(t -> equal(t.getDefinitionId(), definitionId)).collect(onlyElement("task not found for definitionId = %s", definitionId));
    }

    default FlowActivity getFlowActivityByInstanceId(String instanceId) {
        return getFlowActivities().stream().filter(t -> equal(t.getInstanceId(), instanceId)).collect(onlyElement("task not found for instanceId = %s", instanceId));
    }

    default boolean isCompleted() {
        return FlowStatusUtils.isCompleted(getStatus());
    }

    default LookupValue getFlowStatusLookup() {
        LookupValue value = get(ATTR_FLOW_STATUS, LookupValue.class);
        checkArgument(value != null && equal(toFlowStatus(value), getStatus()), "invalid flow status lookup value = %s", value);
        return value;
    }

    default long getFlowStatusId() {
        return getFlowStatusLookup().getId();
    }

    default String getFlowStatusDescription() {
        return getFlowStatusLookup().getDescription();
    }

}
