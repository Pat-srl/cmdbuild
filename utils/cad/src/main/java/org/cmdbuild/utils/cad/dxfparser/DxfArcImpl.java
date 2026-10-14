/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Double.min;
import static java.lang.Math.PI;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.cad.CadGeometryUtils.normalizeAngle;
import org.cmdbuild.utils.cad.dxfparser.model.DxfArc;
import org.cmdbuild.utils.cad.dxfparser.model.DxfExtendedData;
import org.cmdbuild.utils.cad.model.CadPoint;
import org.cmdbuild.utils.lang.Builder;

public class DxfArcImpl implements DxfArc {

    private final CadPoint center;
    private final double startAngle;
    private final double endAngle;
    private final double radius;
    private final String layer;
    private final DxfExtendedData xdata;

    public DxfArcImpl(CadPoint center, Double startAngle, Double endAngle, Double radius, @Nullable String layer, DxfExtendedData xdata) {
        this.center = checkNotNull(center);
        this.startAngle = normalizeAngle(startAngle);
        this.endAngle = normalizeAngle(endAngle);
        this.radius = radius;
        this.layer = layer;
        this.xdata = new DxfExtendedDataImpl(xdata);
    }

    @Override
    public CadPoint getCenter() {
        return center;
    }

    @Override
    public double getStartAngle() {
        return startAngle;
    }

    @Override
    public double getEndAngle() {
        return endAngle;
    }

    @Override
    public double getRadius() {
        return radius;
    }

    @Nullable
    @Override
    public String getLayer() {
        return layer;
    }

    @Override
    public String getType() {
        return "ARC";
    }

    @Override
    public DxfExtendedData getXdata() {
        return xdata;
    }

    @Override
    public String toString() {
        return "DxfArc{" + "center=" + center + ", startAngle=" + startAngle + ", endAngle=" + endAngle + ", radius=" + radius + '}';
    }

    public static DxfArcImplBuilder builder() {
        return new DxfArcImplBuilder();
    }

    public static DxfArcImplBuilder copyOf(DxfArc source) {
        return new DxfArcImplBuilder()
                .withLayer(source.getLayer())
                .withCenter(source.getCenter())
                .withStartAngle(source.getStartAngle())
                .withEndAngle(source.getEndAngle())
                .withRadius(source.getRadius())
                .withXdata(new DxfExtendedDataImpl(source.getXdata()));
    }

    public static class DxfArcImplBuilder implements Builder<DxfArcImpl, DxfArcImplBuilder> {

        private CadPoint center = new CadPoint(0, 0);
        private double startAngle = 0d;
        private double endAngle = 0d;
        private double radius = 0d;
        private String layer;
        private DxfExtendedData xdata;
        private CadPoint offset = new CadPoint(0, 0);
        private Double rotationAngle = 0d;
        private double scaleX = 1, scaleY = 1;

        public DxfArcImplBuilder withCenter(CadPoint center) {
            this.center = center;
            return this;
        }

        public DxfArcImplBuilder withStartAngle(double startAngle) {
            this.startAngle = startAngle;
            return this;
        }

        public DxfArcImplBuilder withEndAngle(double endAngle) {
            this.endAngle = endAngle;
            return this;
        }

        public DxfArcImplBuilder withRadius(double radius) {
            this.radius = radius;
            return this;
        }

        public DxfArcImplBuilder withLayer(String layer) {
            this.layer = layer;
            return this;
        }

        public DxfArcImplBuilder withXdata(DxfExtendedDataImpl xdata) {
            this.xdata = xdata;
            return this;
        }

        public DxfArcImplBuilder withOffset(CadPoint offset) {
            this.offset = offset;
            return this;
        }

        public DxfArcImplBuilder withRotation(Double rotationAngle) {
            this.rotationAngle = rotationAngle;
            return this;
        }

        public DxfArcImplBuilder withScale(double scaleX, double scaleY) {
            this.scaleX = scaleX;
            this.scaleY = scaleY;
            return this;
        }

        @Override
        public DxfArcImpl build() {
            // this is the correct order to modify the point (scale, rotation, offset)
            if (scaleX != 1 || scaleY != 1) {
                radius = radius * min(scaleX, scaleY);
            }
            if (rotationAngle != 0) {
                startAngle = startAngle - (rotationAngle / PI * 180);
                endAngle = endAngle - (rotationAngle / PI * 180);
            }
            center = center.scale(scaleX, scaleY).rotate(rotationAngle).addOffset(offset);

            return new DxfArcImpl(center, startAngle, endAngle, radius, layer, xdata);
        }

    }

}
