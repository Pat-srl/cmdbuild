package org.cmdbuild.client.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import org.cmdbuild.gis.GisAttributeType;
import static org.cmdbuild.gis.GisAttributeType.GAT_POINT;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

@JsonDeserialize(builder = GeoAttributeDataImpl.GeoAttributeDataBuilder.class)
public class GeoAttributeDataImpl implements GeoAttributeData {

    private final long id;
    private final String name;
    private final Long icon;
    private final String description;
    private final String type;
    private final GisAttributeType subType;
    private final boolean active;
    private final Integer index, zoomMin, zoomDef, zoomMax;
    private final Map<String, Boolean> visibility;
    private final Map<String, Object> style;
    private final boolean infoWindowEnabled;
    private final String infoWindowContent;
    private final String infoWindowImage;

    public GeoAttributeDataImpl(GeoAttributeDataBuilder builder) {
        this.id = builder.id;
        this.name = checkNotNull(builder.name);
        this.icon = builder.icon;
        this.description = builder.description;
        this.type = checkNotNull(builder.type);
        this.subType = builder.subType;
        this.active = firstNotNull(builder.active, true);
        this.index = builder.index;
        this.zoomMin = builder.zoomMin;
        this.zoomMax = builder.zoomMax;
        this.zoomDef = builder.zoomDef;
        this.visibility = firstNotNull(builder.visibility, map());
        this.style = firstNotNull(builder.style, map());
        this.infoWindowEnabled = firstNotNull(builder.infoWindowEnabled, false);
        this.infoWindowContent = builder.infoWindowContent;
        this.infoWindowImage = builder.infoWindowImage;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Long getIcon() {
        return icon;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public GisAttributeType getSubType() {
        return subType;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public Integer getIndex() {
        return index;
    }

    @Override
    public Integer getZoomMin() {
        return zoomMin;
    }

    @Override
    public Integer getZoomDef() {
        return zoomDef;
    }

    @Override
    public Integer getZoomMax() {
        return zoomMax;
    }

    @Override
    public Map<String, Boolean> getVisibility() {
        return visibility;
    }

    @Override
    public Map<String, Object> getStyle() {
        return style;
    }

    @Override
    public boolean isInfoWindowEnabled() {
        return infoWindowEnabled;
    }

    @Override
    public String getInfoWindowContent() {
        return infoWindowContent;
    }

    @Override
    public String getInfoWindowImage() {
        return infoWindowImage;
    }

    public static GeoAttributeDataBuilder copyOf(GeoAttributeDataImpl source) {
        return new GeoAttributeDataBuilder();
    }

    public static GeoAttributeDataImpl.GeoAttributeDataBuilder builder() {
        return new GeoAttributeDataImpl.GeoAttributeDataBuilder();
    }

    public static class GeoAttributeDataBuilder implements Builder<GeoAttributeDataImpl, GeoAttributeDataBuilder> {

        private long id;
        private String name;
        private Long icon;
        private String description;
        private String type;
        private GisAttributeType subType;
        private boolean active;
        private Integer index;
        private Integer zoomMin;
        private Integer zoomDef;
        private Integer zoomMax;
        private Map<String, Boolean> visibility;
        private Map<String, Object> style;
        private boolean infoWindowEnabled;
        private String infoWindowContent;
        private String infoWindowImage;

        public GeoAttributeDataBuilder withDefaults() {
            index = 0;
            zoomMin = 1;
            zoomDef = 13;
            zoomMax = 25;
            active = true;
            type = "geometry";
            subType = GAT_POINT;
            return this;
        }

        public GeoAttributeDataBuilder withName(String name) {
            this.name = name;
            return this;
        }

        @JsonProperty("_id")
        public GeoAttributeDataBuilder withId(long id) {
            this.id = id;
            return this;
        }

        public GeoAttributeDataBuilder withIcon(Long icon) {
            this.icon = icon;
            return this;
        }

        public GeoAttributeDataBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public GeoAttributeDataBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public GeoAttributeDataBuilder withSubtype(GisAttributeType subType) {
            this.subType = subType;
            return this;
        }

        public GeoAttributeDataBuilder withActive(boolean active) {
            this.active = active;
            return this;
        }

        public GeoAttributeDataBuilder withIndex(Integer index) {
            this.index = index;
            return this;
        }

        public GeoAttributeDataBuilder withZoomMin(Integer zoomMin) {
            this.zoomMin = zoomMin;
            return this;
        }

        public GeoAttributeDataBuilder withZoomDef(Integer zoomDef) {
            this.zoomDef = zoomDef;
            return this;
        }

        public GeoAttributeDataBuilder withZoomMax(Integer zoomMax) {
            this.zoomMax = zoomMax;
            return this;
        }

        public GeoAttributeDataBuilder withVisibility(Map<String, Boolean> visibility) {
            this.visibility = visibility;
            return this;
        }

        public GeoAttributeDataBuilder withStyle(Map<String, Object> style) {
            this.style = style;
            return this;
        }

        public GeoAttributeDataBuilder withInfoWindowEnabled(boolean infoWindowEnabled) {
            this.infoWindowEnabled = infoWindowEnabled;
            return this;
        }

        public GeoAttributeDataBuilder withInfoWindowContent(String infoWindowContent) {
            this.infoWindowContent = infoWindowContent;
            return this;
        }

        public GeoAttributeDataBuilder withInfoWindowImage(String infoWindowImage) {
            this.infoWindowImage = infoWindowImage;
            return this;
        }

        @Override
        public GeoAttributeDataImpl build() {
            return new GeoAttributeDataImpl(this);
        }
    }

}
