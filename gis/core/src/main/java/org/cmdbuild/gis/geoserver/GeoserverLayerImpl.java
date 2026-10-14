/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.geoserver;

import static com.google.common.base.Preconditions.checkArgument;
import java.time.ZonedDateTime;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.gis.GeoserverLayer;
import org.cmdbuild.gis.GisAttribute;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.gis.GisConst.GIS_GEOSERVER_LAYER_TABLE_NAME;
import org.cmdbuild.gis.model.PointImpl;
import org.cmdbuild.utils.cad.model.CadPoint;

@CardMapping(GIS_GEOSERVER_LAYER_TABLE_NAME)
public class GeoserverLayerImpl implements GeoserverLayer {

    private final Long id;
    private final String attributeName, geoserverStore, geoserverLayer, ownerClass;
    private final long ownerCard;
    private final ZonedDateTime beginDate;
    private final boolean isActive;
    private final CadPoint center;

    private GeoserverLayerImpl(GeoserverLayerImplBuilder builder) {
        this.id = builder.id;
        this.attributeName = checkNotBlank(builder.attributeName);
        this.ownerClass = checkNotBlank(builder.ownerClass);
        this.geoserverStore = checkNotBlank(builder.geoserverStore);
        this.geoserverLayer = checkNotBlank(builder.geoserverLayer);
        this.ownerCard = builder.cardId;
        this.beginDate = firstNotNull(builder.beginDate, now());
        this.isActive = firstNotNull(builder.isActive, true);
        this.center = firstNotNull(builder.center, new CadPoint(1, 1));
    }

    @Override
    @Nullable
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @CardAttr(GEOSERVER_LAYER_ATTR_OWNER_CLASS)
    @Override
    public String getOwnerClass() {
        return ownerClass;
    }

    @CardAttr(GEOSERVER_LAYER_ATTR_ATTRIBUTE_NAME)
    @Override
    public String getAttributeName() {
        return attributeName;
    }

    @CardAttr(GEOSERVER_LAYER_ATTR_OWNER_CARD)
    @Override
    public long getOwnerCard() {
        return ownerCard;
    }

    @CardAttr
    @Override
    public String getGeoserverStore() {
        return geoserverStore;
    }

    @CardAttr
    @Override
    public String getGeoserverLayer() {
        return geoserverLayer;
    }

    @CardAttr(value = ATTR_BEGINDATE, writeToDb = false)
    @Override
    public ZonedDateTime getBeginDate() {
        return beginDate;
    }

    @Override
    public CadPoint getCenter() {
        return center;
    }

    @CardAttr
    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public String toString() {
        return "GeoserverLayer{" + "id=" + id + ", attributeName=" + attributeName + ", geoserverStore=" + geoserverStore + ", geoserverLayer=" + geoserverLayer + ", ownerClass=" + ownerClass + ", ownerCard=" + ownerCard + '}';
    }

    public static GeoserverLayerImplBuilder builder() {
        return new GeoserverLayerImplBuilder();
    }

    public static GeoserverLayerImplBuilder copyOf(GeoserverLayer source) {
        return new GeoserverLayerImplBuilder()
                .withId(source.getId())
                .withAttributeName(source.getAttributeName())
                .withGeoserverStore(source.getGeoserverStore())
                .withGeoserverLayer(source.getGeoserverLayer())
                .withOwnerClass(source.getOwnerClass())
                .withOwnerCard(source.getOwnerCard())
                .withBeginDate(source.getBeginDate())
                .withActive(source.isActive())
                .withCenter(source.getCenter());
    }

    public static class GeoserverLayerImplBuilder implements Builder<GeoserverLayerImpl, GeoserverLayerImplBuilder> {

        private Long id, cardId;
        private String attributeName, geoserverStore, geoserverLayer, ownerClass;
        private ZonedDateTime beginDate;
        private Boolean isActive;
        private CadPoint center;

        public GeoserverLayerImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public GeoserverLayerImplBuilder withActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public GeoserverLayerImplBuilder withAttributeName(String attributeName) {
            this.attributeName = attributeName;
            return this;
        }

        public GeoserverLayerImplBuilder withAttribute(GisAttribute attribute) {
            checkArgument(attribute.isGeoserver(), "invalid gis attribute = %s : not a geoserver attribute", attribute);
            return this.withAttributeName(attribute.getLayerName());
        }

        public GeoserverLayerImplBuilder withGeoserverStore(String geoserverStore) {
            this.geoserverStore = geoserverStore;
            return this;
        }

        public GeoserverLayerImplBuilder withGeoserverLayer(String geoserverLayer) {
            this.geoserverLayer = geoserverLayer;
            return this;
        }

        public GeoserverLayerImplBuilder withOwnerCard(long cardId) {
            this.cardId = cardId;
            return this;
        }

        public GeoserverLayerImplBuilder withOwnerClass(String ownerClass) {
            this.ownerClass = ownerClass;
            return this;
        }

        public GeoserverLayerImplBuilder withCenter(CadPoint center) {
            this.center = center;
            return this;
        }

        public GeoserverLayerImplBuilder withBeginDate(ZonedDateTime beginDate) {
            this.beginDate = beginDate;
            return this;
        }

        @Override
        public GeoserverLayerImpl build() {
            return new GeoserverLayerImpl(this);
        }

    }
}
