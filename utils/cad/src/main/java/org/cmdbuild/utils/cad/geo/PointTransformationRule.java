/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.geo;

import jakarta.annotation.Nullable;
import org.cmdbuild.utils.cad.model.CadPoint;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;

public interface PointTransformationRule {

    @Nullable
    CadPoint getTranslation();

    @Nullable
    Double getRotationAngle();

    @Nullable
    CadPoint getRotationCenter();

    @Nullable
    CadPoint getScaling();

    @Nullable
    CadPoint getScalingCenter();

    @Nullable
    String getSourceCoordinateReferenceSystem();

    @Nullable
    String getTargetCoordinateReferenceSystem();

    default boolean isTranslation() {
        return getTranslation() != null;
    }

    default boolean isRotation() {
        return getRotationAngle() != null && getRotationAngle() != 0;
    }

    default boolean isScaling() {
        return getScaling() != null && !getScaling().isOne();
    }

    default boolean isCrs() {
        return isNotBlank(getSourceCoordinateReferenceSystem());
    }

}
