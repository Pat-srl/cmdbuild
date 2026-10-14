/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import javax.annotation.Nullable;
import org.cmdbuild.lookup.IconType;
import org.cmdbuild.modeldiff.core.LookupValueConfiguration;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;

/**
 * See {@link LookupValueWsCommons#toResponse(LookupValue)}
 *
 * <p>
 * <b>Note</b>: in <i>schema diff</i> even <code>iconType</code> is
 * deserialized; standard <i>CMDBuild serialization</i> names are used (not so
 * in {@link LookupValueConfiguration}) for:
 * <ul>
 * <li><code>icon_type</code>;
 * <li><code>icon_image</code>;
 * <li><code>icon_font</code>;
 * <li><code>icon_color</code>;
 * <li><code>text_color</code>.
 * </ul>
 *
 * @author afelice
 */
public class SchemaLookupValueConfiguration extends LookupValueConfiguration {

    public SchemaLookupValueConfiguration(Long id, String lookupName,
            String code, String description) {
        super(id, lookupName, code, description);        
    }
    
    @JsonCreator
    public SchemaLookupValueConfiguration(@JsonProperty("_id") Long id, @JsonProperty("_type") String lookupName,
            @JsonProperty("code") String code, @JsonProperty("description") String description, @JsonProperty("_description_translation") String descriptionTranslation,
            @JsonProperty("notes") String notes, @JsonProperty("index") Integer index, @JsonProperty("active") boolean active,
            @JsonProperty("default") boolean isDefault, 
            @JsonProperty("icon_type") String icontTypeStr, @JsonProperty("icon_image") String iconImage, @JsonProperty("icon_font") String iconFont,
            @JsonProperty("icon_color") String iconColor, @JsonProperty("text_color") String textColor) {
        super(id, lookupName, code, description);
        
        if (isNotBlank(descriptionTranslation)) {
            setDescriptionTranslation(descriptionTranslation);
        }
        
        super.notes = notes;
        super.index = index;
        super.active = active;
        super.isDefault = isDefault;
        setIconTypeStr(icontTypeStr);
        super.iconImage = iconImage;
        super.iconFont = iconFont;
        super.iconColor = iconColor;
        super.textColor = textColor;
    }

    @JsonProperty("icon_type")
    public void setIconTypeStr(String iconTypeStr) {
        this.iconType = parseEnumOrDefault(iconTypeStr, IconType.NONE);
    }

    @Nullable
    @Override
    @JsonProperty("text_color")
    public String getTextColor() {
        return textColor;
    }

    @JsonProperty("icon_type")
    @Override
    public String getIconTypeStr() {
        return serializeEnum(iconType);
    } 
    
    @Nullable
    @Override
    @JsonProperty("icon_image")
    public String getIconImage() {
        return iconImage;
    }

    @Nullable
    @Override
    @JsonProperty("icon_font")
    public String getIconFont() {
        return iconFont;
    }

    @Nullable
    @Override
    @JsonProperty("icon_color")
    public String getIconColor() {
        return iconColor;
    }

    static public SchemaLookupValueConfiguration buildLookupValueConfiguration(Map<String, Object> lookupValueCmdbSerialization, ObjectMapper objectMapper) {
        SchemaLookupValueConfiguration valueConfig = objectMapper.convertValue(lookupValueCmdbSerialization, SchemaLookupValueConfiguration.class);
        return valueConfig;
    }    
    
}
