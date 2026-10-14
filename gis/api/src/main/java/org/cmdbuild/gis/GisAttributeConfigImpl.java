package org.cmdbuild.gis;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import org.cmdbuild.gis.GisAttributeConfigImpl.GisAttributeConfigImplBuilder;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

@JsonDeserialize(builder = GisAttributeConfigImplBuilder.class)
public class GisAttributeConfigImpl implements GisAttributeConfig {

    private final boolean infoWindowEnabled;
    private final String infoWindowContent;
    private final String infoWindowImage;

    private GisAttributeConfigImpl(GisAttributeConfigImplBuilder builder) {
        this.infoWindowEnabled = firstNotNull(builder.infoWindowEnabled, false);
        this.infoWindowContent = builder.infoWindowContent;
        this.infoWindowImage = builder.infoWindowImage;
    }

    @Override
    public boolean getInfoWindowEnabled() {
        return infoWindowEnabled;
    }

    @Override
    @Nullable
    public String getInfoWindowContent() {
        return infoWindowContent;
    }

    @Override
    @Nullable
    public String getInfoWindowImage() {
        return infoWindowImage;
    }

    public static GisAttributeConfigImplBuilder builder() {
        return new GisAttributeConfigImplBuilder();
    }

    public static GisAttributeConfigImplBuilder copyOf(GisAttributeConfig source) {
        return new GisAttributeConfigImplBuilder()
                .withInfoWindowEnabled(source.getInfoWindowEnabled())
                .withInfoWindowContent(source.getInfoWindowContent())
                .withInfoWindowImage(source.getInfoWindowImage());
    }

    public static class GisAttributeConfigImplBuilder implements Builder<GisAttributeConfigImpl, GisAttributeConfigImplBuilder> {

        private Boolean infoWindowEnabled;
        private String infoWindowContent;
        private String infoWindowImage;

        public GisAttributeConfigImplBuilder withInfoWindowEnabled(boolean infoWindowEnabled) {
            this.infoWindowEnabled = infoWindowEnabled;
            return this;
        }

        public GisAttributeConfigImplBuilder withInfoWindowContent(String infoWindowContent) {
            this.infoWindowContent = infoWindowContent;
            return this;
        }

        public GisAttributeConfigImplBuilder withInfoWindowImage(String infoWindowImage) {
            this.infoWindowImage = infoWindowImage;
            return this;
        }

        @Override
        public GisAttributeConfigImpl build() {
            return new GisAttributeConfigImpl(this);
        }

    }
}
