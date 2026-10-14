/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.lookup;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import jakarta.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static org.cmdbuild.lookup.LegacyIconMapping.LEGACY_ICON_MAP;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrNull;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public class LookupConfigImpl implements LookupConfig {

    private final Map<String, String> config;

    private final String textColor, iconColor, iconImage, iconFont;
    private final IconType iconType;
    private final boolean isDefault, isHideText;
    private final String dmsModel;
    private final Set<String> allowedExtensions;
    private final DmsAttachmentCountCheck countCheck;
    private final Integer maxFileSize;
    private final Integer countCheckNumber;

    public LookupConfigImpl(Map<String, String> config) {
        this.config = ImmutableMap.copyOf(map(config).withoutValues(Objects::isNull));
        this.allowedExtensions = ImmutableSet.copyOf(Splitter.on(",").trimResults().omitEmptyStrings().splitToList(nullToEmpty(config.get(DMS_ALLOWED_EXTENSIONS))));
        this.countCheck = parseEnumOrNull(config.get(DMS_CHECK_COUNT), DmsAttachmentCountCheck.class);
        this.countCheckNumber = toIntegerOrNull(config.get(DMS_CHECK_COUNT_NUMBER));
        this.dmsModel = config.get(DMS_MODEL_CLASS);
        this.maxFileSize = toIntegerOrNull(config.get(DMS_MAX_FILE_SIZE));
        this.isDefault = toBooleanOrDefault(config.get(LOOKUP_CONFIG_IS_DEFAULT), false);
        this.textColor = emptyToNull(config.get(LOOKUP_CONFIG_TEXT_COLOR));
        this.iconColor = emptyToNull(config.get(LOOKUP_CONFIG_ICON_COLOR));
        this.iconType = parseEnumOrDefault(config.get(LOOKUP_CONFIG_ICON_TYPE), IconType.NONE);
        this.isHideText = toBooleanOrDefault(config.get(LOOKUP_CONFIG_HIDE_TEXT), false);
        switch (iconType) {
            case FONT -> {
                String iconFont = checkNotBlank(config.get(LOOKUP_CONFIG_ICON_FONT));
                this.iconFont = LEGACY_ICON_MAP.getOrDefault(iconFont, iconFont);
                this.iconImage = null;
            }
            case IMAGE -> {
                this.iconFont = null;
                this.iconImage = checkNotBlank(config.get(LOOKUP_CONFIG_ICON_IMAGE));
            }
            case NONE -> {
                this.iconFont = null;
                this.iconImage = null;
            }
            default ->
                throw new IllegalArgumentException("unsupported icon type = " + iconType);
        }
    }

    @Override
    public Set<String> getDmsAllowedExtensions() {
        return allowedExtensions;
    }

    @Override
    public Integer getMaxFileSize() {
        return maxFileSize;
    }

    @Override
    @Nullable
    public DmsAttachmentCountCheck getDmsCheckCount() {
        return countCheck;
    }

    @Override
    @Nullable
    public Integer getDmsCheckCountNumber() {
        return countCheckNumber;
    }

    @Override
    @Nullable
    public String getDmsModelClass() {
        return dmsModel;
    }

    @Override
    @Nullable
    public String getTextColor() {
        return textColor;
    }

    @Override
    @Nullable
    public String getIconColor() {
        return iconColor;
    }

    @Override
    @Nullable
    public String getIconImage() {
        return iconImage;
    }

    @Override
    @Nullable
    public String getIconFont() {
        return iconFont;
    }

    @Override
    public IconType getIconType() {
        return iconType;
    }

    @Override
    @Nullable
    public boolean isHideText() { return isHideText;}

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public Map<String, String> asMap() {
        return config;
    }

    public static LookupConfigImplBuilder builder() {
        return new LookupConfigImplBuilder();
    }

    public static LookupConfigImplBuilder copyOf(LookupConfigImpl source) {
        return new LookupConfigImplBuilder()
                .withConfig(source.asMap());
    }

    public static class LookupConfigImplBuilder implements Builder<LookupConfigImpl, LookupConfigImplBuilder> {

        private final Map<String, String> config = map();

        public LookupConfigImplBuilder withConfig(Map<String, String> config) {
            this.config.putAll(config);
            return this;
        }

        public LookupConfigImplBuilder withAllowedExtensions(@Nullable Iterable<String> allowedExtensions) {
            config.put(DMS_ALLOWED_EXTENSIONS, allowedExtensions == null ? null : Joiner.on(",").join(set(allowedExtensions)));
            return this;
        }

        public LookupConfigImplBuilder withCountCheck(@Nullable DmsAttachmentCountCheck countCheck) {
            config.put(DMS_CHECK_COUNT, serializeEnum(countCheck));
            return this;
        }

        public LookupConfigImplBuilder withCountCheckNumber(@Nullable Integer countCheckNumber) {
            config.put(DMS_CHECK_COUNT_NUMBER, toStringOrNull(countCheckNumber));
            return this;
        }

        public LookupConfigImplBuilder withMaxFileSize(@Nullable Integer maxFileSize) {
            config.put(DMS_MAX_FILE_SIZE, toStringOrNull(maxFileSize));
            return this;
        }

        public LookupConfigImplBuilder withDmsModel(String dmsModel) {
            config.put(DMS_MODEL_CLASS, dmsModel);
            return this;
        }

        public LookupConfigImplBuilder withIconType(String iconType) {
            config.put(LOOKUP_CONFIG_ICON_TYPE, iconType);
            return this;
        }

        public LookupConfigImplBuilder withIconType(IconType iconType) {
            config.put(LOOKUP_CONFIG_ICON_TYPE, serializeEnum(iconType));
            return this;
        }

        public LookupConfigImplBuilder withIconImage(String iconImage) {
            config.put(LOOKUP_CONFIG_ICON_IMAGE, iconImage);
            return this;
        }

        public LookupConfigImplBuilder withIconFont(String iconFont) {
            config.put(LOOKUP_CONFIG_ICON_FONT, iconFont);
            return this;
        }

        public LookupConfigImplBuilder withIconColor(String iconColor) {
            config.put(LOOKUP_CONFIG_ICON_COLOR, iconColor);
            return this;
        }

        public LookupConfigImplBuilder withTextColor(String textColor) {
            config.put(LOOKUP_CONFIG_TEXT_COLOR, textColor);
            return this;
        }

        public LookupConfigImplBuilder withDefault(Boolean isDefault) {
            config.put(LOOKUP_CONFIG_IS_DEFAULT, toStringOrNull(isDefault));
            return this;
        }

        public LookupConfigImplBuilder withHideText(Boolean isHideText) {
            config.put(LOOKUP_CONFIG_HIDE_TEXT, toStringOrNull(isHideText));
            return this;
        }

        @Override
        public LookupConfigImpl build() {
            return new LookupConfigImpl(config);
        }

    }
}
