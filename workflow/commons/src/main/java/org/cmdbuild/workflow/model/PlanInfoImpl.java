package org.cmdbuild.workflow.model;

import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.workflow.utils.PlanIdUtils;

public class PlanInfoImpl implements PlanInfo {

    private final String packageId, version, definitionId, planId;

    public PlanInfoImpl(String packageId, String version, String definitionId) {
        this.packageId = checkNotBlank(packageId);
        this.version = checkNotBlank(version);
        this.definitionId = checkNotBlank(definitionId);
        planId = PlanIdUtils.buildPlanId(packageId, version, definitionId);
    }

    @Override
    public final String getPackageId() {
        return packageId;
    }

    @Override
    public final String getVersion() {
        return version;
    }

    @Override
    public final String getDefinitionId() {
        return definitionId;
    }

    @Override
    public String getPlanId() {
        return planId;
    }

    @Override
    public String toString() {
        return serialize();
    }

    @Nullable
    public static PlanInfo deserializeNullable(@Nullable String serializedPlanInfo) {
        if (isBlank(serializedPlanInfo)) {
            return null;
        } else {
            return deserialize(serializedPlanInfo);
        }
    }

    public static PlanInfo deserialize(String serializedPlanInfo) {
        PlanIdUtils.PackageIdAndVersionAndDefinitionId packageIdAndProcessIdAndVersion = PlanIdUtils.readPlanId(serializedPlanInfo);
        return new PlanInfoImpl(packageIdAndProcessIdAndVersion.getPackageId(), packageIdAndProcessIdAndVersion.getVersion(), packageIdAndProcessIdAndVersion.getDefinitionId());
    }

}
