/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser;

import org.cmdbuild.utils.cad.dxfparser.model.DxfVertex;
import org.cmdbuild.utils.cad.model.CadPoint;
import org.cmdbuild.utils.lang.Builder;

public class DxfVertexImpl implements DxfVertex {

    private double x;
    private double y;
    private double z;

    public DxfVertexImpl() {
    }

    public DxfVertexImpl(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }

    public DxfVertexImpl(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "DxfVertex{" + "x=" + x + ", y=" + y + '}';
    }

    public static DxfVertexImplBuilder builder() {
        return new DxfVertexImplBuilder();
    }

    public static DxfVertexImplBuilder copyOf(DxfVertex source) {
        return new DxfVertexImplBuilder()
                .withX(source.getX())
                .withY(source.getY())
                .withZ(source.getZ());
    }

    public static class DxfVertexImplBuilder implements Builder<DxfVertexImpl, DxfVertexImplBuilder> {

        private double x = 0;
        private double y = 0;
        private double z = 0;
        private CadPoint offset = new CadPoint(0, 0);
        private Double rotationAngle = 0.0;
        private double scaleX = 1, scaleY = 1;

        public DxfVertexImplBuilder withX(double x) {
            this.x = x;
            return this;
        }

        public DxfVertexImplBuilder withY(double y) {
            this.y = y;
            return this;
        }

        public DxfVertexImplBuilder withZ(double z) {
            this.z = z;
            return this;
        }

        public DxfVertexImplBuilder withOffset(CadPoint offset) {
            this.offset = offset;
            return this;
        }

        public DxfVertexImplBuilder withRotation(Double rotationAngle) {
            this.rotationAngle = rotationAngle;
            return this;
        }

        public DxfVertexImplBuilder withScale(double scaleX, double scaleY) {
            this.scaleX = scaleX;
            this.scaleY = scaleY;
            return this;
        }

        @Override
        public DxfVertexImpl build() {
            // this is the correct order to modify the point (scale, rotation, offset)
            CadPoint vertexPoint = new CadPoint(x, y).scale(scaleX, scaleY).rotate(rotationAngle).addOffset(offset);

            return new DxfVertexImpl(vertexPoint.getX(), vertexPoint.getY(), z);
        }

    }
}
