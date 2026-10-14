/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.geo;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import org.cmdbuild.utils.cad.model.CadPoint;
import static org.cmdbuild.utils.cad.model.CadPoint.point;

public class PointTransformationRuleImpl implements PointTransformationRule {

    private final CadPoint translation, rotationCenter, scaling, scalingCenter;
    private final Double rotationAngle;
    private final String sourceReferenceCoordinateSystem, targetReferenceCoordinateSystem;

    public PointTransformationRuleImpl(@Nullable CadPoint translation, @Nullable Double rotationAngle, @Nullable CadPoint rotationCenter, @Nullable CadPoint scaling, @Nullable CadPoint scalingCenter, @Nullable String sourceReferenceCoordinateSystem, @Nullable String targetReferenceCoordinateSystem) {
        this.translation = translation;
        this.rotationCenter = rotationCenter;
        this.scaling = scaling;
        this.scalingCenter = scalingCenter;
        this.rotationAngle = rotationAngle;
        this.sourceReferenceCoordinateSystem = sourceReferenceCoordinateSystem;
        this.targetReferenceCoordinateSystem = targetReferenceCoordinateSystem;
    }

    public static PointTransformationRule translation(double x, double y) {
        return translation(point(x, y));
    }

    public static PointTransformationRule translation(CadPoint point) {
        return new PointTransformationRuleImpl(checkNotNull(point), null, null, null, null, null, null);
    }

    public static PointTransformationRule rotation(double theta, double centerX, double centerY) {
        return new PointTransformationRuleImpl(null, theta, point(centerX, centerY), null, null, null, null);
    }

    public static PointTransformationRule scaling(double scaleX, double scaleY, double centerX, double centerY) {
        return new PointTransformationRuleImpl(null, null, null, point(scaleX, scaleY), point(centerX, centerY), null, null);
    }

    public static PointTransformationRule rcs(String sourceReferenceCoordinateSystem, String targetReferenceCoordinateSystem) {
        return new PointTransformationRuleImpl(null, null, null, null, null, sourceReferenceCoordinateSystem, targetReferenceCoordinateSystem);
    }

    @Override
    @Nullable
    public CadPoint getTranslation() {
        return translation;
    }

    @Override
    @Nullable
    public CadPoint getRotationCenter() {
        return rotationCenter;
    }

    @Override
    @Nullable
    public CadPoint getScaling() {
        return scaling;
    }

    @Override
    @Nullable
    public CadPoint getScalingCenter() {
        return scalingCenter;
    }

    @Override
    @Nullable
    public Double getRotationAngle() {
        return rotationAngle;
    }

    @Override
    @Nullable
    public String getSourceCoordinateReferenceSystem() {
        return sourceReferenceCoordinateSystem;
    }

    @Override
    @Nullable
    public String getTargetCoordinateReferenceSystem() {
        return targetReferenceCoordinateSystem;
    }

}
