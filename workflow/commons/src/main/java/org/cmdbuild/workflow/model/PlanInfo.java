package org.cmdbuild.workflow.model;

import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import static org.cmdbuild.workflow.utils.PlanIdUtils.buildPlanId;

public interface PlanInfo extends PlanPackageDefinitionInfo {

    String getDefinitionId();

    String getPlanId();

    default int getVersionInt() {
        return toInt(getVersion());
    }

    default String serialize() {
        return buildPlanId(getPackageId(), getVersion(), getDefinitionId());
    }
}
