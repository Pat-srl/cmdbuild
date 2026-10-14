package org.cmdbuild.gis;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import java.time.ZonedDateTime;
import static java.time.ZonedDateTime.now;
import java.util.Map;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.gis.GisConst.ATTR_OWNER;
import static org.cmdbuild.gis.GisConst.GIS_ATTRIBUTE_TABLE_NAME;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.mapType;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping(GIS_ATTRIBUTE_TABLE_NAME)
public class GisAttributeImpl implements GisAttribute {

    private final String layerName, description, mapStyle, visibility;
    private final int index, minimumZoom, defaultZoom, maximumzoom;
    private final String ownerClass;
    private final boolean active;
    private final GisAttributeConfig config;
    private final Long id;
    private final ZonedDateTime beginDate;
    private final GisAttributeType type;

    private GisAttributeImpl(GisAttributeImplBuilder builder) {
        this.layerName = checkNotNull(builder.layerName, "layer name cannot be null");
        this.description = checkNotNull(builder.description, "description cannot be null");
        this.active = builder.active;
        this.mapStyle = checkNotNull(builder.mapStyle, "map style cannot be null");
        this.type = checkNotNull(builder.type, "attr type cannot be null");
        this.index = checkNotNull(builder.index, "index cannot be null");
        this.minimumZoom = checkNotNull(builder.minimumZoom, "min zoom cannot be null");
        this.defaultZoom = checkNotNull(builder.defaultZoom, "def zoom cannot be null");
        this.maximumzoom = checkNotNull(builder.maximumZoom, "max zoom cannot be null");
        this.visibility = checkNotNull(builder.visibility, "visibility cannot be null");
        this.ownerClass = checkNotBlank(builder.baseClassId, "base class id cannot be null");
        this.id = builder.id;
        this.beginDate = firstNotNull(builder.beginDate, now());
        this.config = firstNotNull(builder.config, GisAttributeConfigImpl.builder().build());
    }

    @Override
    @CardAttr(ATTR_ID)
    @Nullable
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr(value = ATTR_BEGINDATE, writeToDb = false)
    public ZonedDateTime getBeginDate() {
        return beginDate;
    }

    @Override
    @CardAttr(ATTR_CODE)
    public String getLayerName() {
        return layerName;
    }

    @Override
    @CardAttr(ATTR_OWNER)
    public String getOwnerClassName() {
        return ownerClass;
    }

    @Override
    @CardAttr("Active")
    public boolean isActive() {
        return active;
    }

    @Override
    @CardAttr("Config")
    public GisAttributeConfig getConfig() {
        return config;
    }

    @Override
    @CardAttr
    public String getDescription() {
        return description;
    }

    @Override
    @CardAttr
    public String getMapStyle() {
        return mapStyle;
    }

    @Override
    public Map<String, Object> getMapStyleMap() {
        return fromJson(getMapStyle(), MAP_OF_OBJECTS);
    }

    @Override
    @CardAttr
    public GisAttributeType getType() {
        return type;
    }

    @Override
    @CardAttr
    public int getIndex() {
        return index;
    }

    @Override
    @CardAttr
    public int getMinimumZoom() {
        return minimumZoom;
    }

    @Override
    @CardAttr
    public int getDefaultZoom() {
        return defaultZoom;
    }

    @Override
    @CardAttr
    public int getMaximumZoom() {
        return maximumzoom;
    }

    @Override
    @CardAttr
    public String getVisibility() {
        return visibility;
    }

    @Override
    public Map<String, Boolean> getVisibilityMap() {
        return fromJson(getVisibility(), mapType(String.class, Boolean.class));
    }

    @Override
    public String toString() {
        return "GisAttribute{id=" + id + ", name=" + layerName + ", owner=" + ownerClass + ", type=" + serializeEnum(type) + '}';
    }

    public static GisAttributeImplBuilder builder() {
        return new GisAttributeImplBuilder();
    }

    public static GisAttributeImplBuilder copyOf(GisAttribute source) {
        return new GisAttributeImplBuilder()
                .withId(source.getId())
                .withOwnerClassName(source.getOwnerClassName())
                .withActive(source.isActive())
                .withLayerName(source.getLayerName())
                .withDescription(source.getDescription())
                .withMapStyle(source.getMapStyle())
                .withType(source.getType())
                .withIndex(source.getIndex())
                .withMinimumZoom(source.getMinimumZoom())
                .withDefaultZoom(source.getDefaultZoom())
                .withMaximumZoom(source.getMaximumZoom())
                .withVisibility(source.getVisibility())
                .withBeginDate(source.getBeginDate())
                .withConfig(source.getConfig());
    }

    public static class GisAttributeImplBuilder implements Builder<GisAttributeImpl, GisAttributeImplBuilder> {

        private String layerName;
        private Long id;
        private boolean active;
        private String baseClassId, description;
        private String mapStyle;
        private GisAttributeType type;
        private Integer index;
        private Integer minimumZoom, defaultZoom;
        private Integer maximumZoom;
        private String visibility;
        private ZonedDateTime beginDate;
        private GisAttributeConfig config;

        public GisAttributeImplBuilder withLayerName(String name) {
            this.layerName = name;
            return this;
        }

        public GisAttributeImplBuilder withBeginDate(ZonedDateTime beginDate) {
            this.beginDate = beginDate;
            return this;
        }

        public GisAttributeImplBuilder withOwnerClassName(String classId) {
            this.baseClassId = classId;
            return this;
        }

        public GisAttributeImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public GisAttributeImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public GisAttributeImplBuilder withActive(boolean active) {
            this.active = active;
            return this;
        }

        public GisAttributeImplBuilder withMapStyle(String mapStyle) {
            this.mapStyle = mapStyle;
            return this;
        }

        public GisAttributeImplBuilder withMapStyle(Map<String, Object> mapStyleMap) {
            this.mapStyle = toJson(mapStyleMap);
            return this;
        }

        public GisAttributeImplBuilder withType(GisAttributeType type) {
            this.type = type;
            return this;
        }

        public GisAttributeImplBuilder withIndex(Integer index) {
            this.index = index;
            return this;
        }

        public GisAttributeImplBuilder withMinimumZoom(Integer minimumZoom) {
            this.minimumZoom = minimumZoom;
            return this;
        }

        public GisAttributeImplBuilder withDefaultZoom(Integer defaultZoom) {
            this.defaultZoom = defaultZoom;
            return this;
        }

        public GisAttributeImplBuilder withMaximumZoom(Integer maximumzoom) {
            this.maximumZoom = maximumzoom;
            return this;
        }

        public GisAttributeImplBuilder withVisibility(String visibility) {
            this.visibility = visibility;
            return this;
        }

        public GisAttributeImplBuilder withVisibility(Map<String, Boolean> visibilityMap) {
            this.visibility = toJson(visibilityMap);
            return this;
        }

        public GisAttributeImplBuilder withConfig(GisAttributeConfig config) {
            this.config = config;
            return this;
        }

        @Override
        public GisAttributeImpl build() {
            return new GisAttributeImpl(this);
        }

    }
}
