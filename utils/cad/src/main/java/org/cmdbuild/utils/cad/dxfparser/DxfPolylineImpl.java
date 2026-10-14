/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser;

import jakarta.annotation.Nullable;
import java.util.List;
import org.cmdbuild.utils.cad.dxfparser.model.DxfExtendedData;
import org.cmdbuild.utils.cad.dxfparser.model.DxfPolyline;
import org.cmdbuild.utils.cad.dxfparser.model.DxfVertex;
import org.cmdbuild.utils.cad.model.CadPoint;
import static org.cmdbuild.utils.cad.model.CadPoint.point;
import org.cmdbuild.utils.cad.model.CadPolyline;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class DxfPolylineImpl implements DxfPolyline {

    private boolean isClosedPerimeter = false;
    private String layer;
    private List<DxfVertex> vertexes = list();
    private final DxfExtendedDataImpl xdata;

    public DxfPolylineImpl() {
        xdata = new DxfExtendedDataImpl();
    }

    public DxfPolylineImpl(List<DxfVertex> vertexes, DxfExtendedData xdata, @Nullable String layer, boolean isClosedPerimeter) {
        this.layer = layer;
        this.vertexes = vertexes;
        this.xdata = new DxfExtendedDataImpl(xdata);
        this.isClosedPerimeter = isClosedPerimeter;
    }

    @Override
    public List<DxfVertex> getVertexes() {
        return vertexes;
    }

    public void addVertex(DxfVertex vertex) {
        vertexes.add(vertex);
    }

    @Override
    public CadPolyline getPolyline() {
        return new CadPolyline(list(vertexes).map(v -> point(v.getX(), v.getY())));
    }

    @Override
    public String getType() {
        return "POLYLINE";
    }

    @Nullable
    @Override
    public String getLayer() {
        return layer;
    }

    @Override
    public DxfExtendedDataImpl getXdata() {
        return xdata;
    }

    @Override
    public boolean isClosedPerimeter() {
        return isClosedPerimeter;
    }

    @Override
    public CadPolyline getPerimeter() {
        return getPolyline();
    }

    @Override
    public String toString() {
        return "DxfPolylineImpl{" + "closed=" + isClosedPerimeter + ", layer=" + layer + ", vertexes=" + vertexes.size() + '}';
    }

    public static DxfPolylineImplBuilder builder() {
        return new DxfPolylineImplBuilder();
    }

    public static DxfPolylineImplBuilder copyOf(DxfPolyline source) {
        return new DxfPolylineImplBuilder()
                .withLayer(source.getLayer())
                .withVertexes(source.getVertexes())
                .withXdata(new DxfExtendedDataImpl(source.getXdata()))
                .withClosedPerimeter(source.isClosedPerimeter());
    }

    public static class DxfPolylineImplBuilder implements Builder<DxfPolylineImpl, DxfPolylineImplBuilder> {

        private boolean closedPerimeter = false;
        private String layer;
        private List<DxfVertex> vertexes = list();
        private DxfExtendedDataImpl xdata;
        private CadPoint offset = new CadPoint(0, 0);
        private Double rotationAngle = 0.0;
        private double scaleX = 1, scaleY = 1;

        public DxfPolylineImplBuilder withClosedPerimeter(boolean closedPerimeter) {
            this.closedPerimeter = closedPerimeter;
            return this;
        }

        public DxfPolylineImplBuilder withLayer(String layer) {
            this.layer = layer;
            return this;
        }

        public DxfPolylineImplBuilder withVertexes(List<DxfVertex> vertexes) {
            this.vertexes = vertexes;
            return this;
        }

        public DxfPolylineImplBuilder withXdata(DxfExtendedDataImpl xdata) {
            this.xdata = xdata;
            return this;
        }

        public DxfPolylineImplBuilder withOffset(CadPoint offset) {
            this.offset = offset;
            return this;
        }

        public DxfPolylineImplBuilder withRotation(Double rotationAngle) {
            this.rotationAngle = rotationAngle;
            return this;
        }

        public DxfPolylineImplBuilder withScale(double scaleX, double scaleY) {
            this.scaleX = scaleX;
            this.scaleY = scaleY;
            return this;
        }

        @Override
        public DxfPolylineImpl build() {
            vertexes = list(vertexes).map(v -> DxfVertexImpl.copyOf(v).withOffset(offset).withRotation(rotationAngle).withScale(scaleX, scaleY).build());

            return new DxfPolylineImpl(vertexes, xdata, layer, closedPerimeter);
        }

    }
}
