/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.model;

import org.cmdbuild.gis.GisValue;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.gis.CmGeometry;

public class GisValueImpl implements GisValue {

    private final String layerName, ownerClassId;
    private final long ownerCardId;
    private final CmGeometry geometry;
    private final String ownerCardDescription;

    private GisValueImpl(GisValueImplBuilder builder) {
        this.layerName = checkNotBlank(builder.layerName);
        this.ownerClassId = checkNotBlank(builder.ownerClassId);
        this.ownerCardId = builder.ownerCardId;
        this.geometry = checkNotNull(builder.geometry);
        this.ownerCardDescription = builder.ownerCardDescription;
    }

    @Override
    public String getLayerName() {
        return layerName;
    }

    @Override
    public String getOwnerClassId() {
        return ownerClassId;
    }

    @Override
    public long getOwnerCardId() {
        return ownerCardId;
    }

    @Override
    @Nullable
    public String getOwnerCardDescription() {
        return ownerCardDescription;
    }

    @Override
    public CmGeometry getGeometry() {
        return geometry;
    }

    @Override
    public String toString() {
        return "GisValue{" + "layerName=" + layerName + ", ownerClassId=" + ownerClassId + ", ownerCardId=" + ownerCardId + '}';
    }

    public static GisValueImplBuilder builder() {
        return new GisValueImplBuilder();
    }

    public static GisValueImplBuilder copyOf(GisValue source) {
        return new GisValueImplBuilder()
                .withLayerName(source.getLayerName())
                .withOwnerClassId(source.getOwnerClassId())
                .withOwnerCardId(source.getOwnerCardId())
                .withOwnerCardDescription(source.getOwnerCardDescription())
                .withGeometry(source.getGeometry());
    }

    public static class GisValueImplBuilder implements Builder<GisValueImpl, GisValueImplBuilder> {

        private String layerName;
        private String ownerClassId;
        private Long ownerCardId;
        private CmGeometry geometry;
        private String ownerCardDescription;

        public GisValueImplBuilder withLayerName(String layerName) {
            this.layerName = layerName;
            return this;
        }

        public GisValueImplBuilder withOwnerClassId(String ownerClassId) {
            this.ownerClassId = ownerClassId;
            return this;
        }

        public GisValueImplBuilder withOwnerCardId(Long ownerCardId) {
            this.ownerCardId = ownerCardId;
            return this;
        }

        public GisValueImplBuilder withOwnerCardDescription(String ownerCardDescription) {
            this.ownerCardDescription = ownerCardDescription;
            return this;
        }

        public GisValueImplBuilder withGeometry(CmGeometry geometry) {
            this.geometry = geometry;
            return this;
        }

        @Override
        public GisValueImpl build() {
            return new GisValueImpl(this);
        }

    }
}
