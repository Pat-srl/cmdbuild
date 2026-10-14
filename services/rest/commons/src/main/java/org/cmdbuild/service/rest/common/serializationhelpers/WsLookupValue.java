/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import java.util.Collection;
import org.cmdbuild.lookup.DmsAttachmentCountCheck;
import org.cmdbuild.lookup.LookupValueImpl;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class WsLookupValue {

    private final Long parentId;
    private final Integer index;
    private final Boolean isDefault, active, isHideText;
    private final String code, description, iconType, iconImage, iconFont, iconColor, textColor, notes, modelClass;
    private final Collection<String> allowedExtensions;
    private final DmsAttachmentCountCheck checkCount;
    private final Integer checkCountNumber, maxFileSize;

    public WsLookupValue(
            @JsonProperty("parent_id") Long parentId,
            @JsonProperty("index") Integer index,
            @JsonProperty("default") Boolean isDefault,
            @JsonProperty("active") Boolean active,
            @JsonProperty("code") String code,
            @JsonProperty("description") String description,
            @JsonProperty("icon_type") String iconType,
            @JsonProperty("icon_image") String iconImage,
            @JsonProperty("icon_font") String iconFont,
            @JsonProperty("icon_color") String iconColor,
            @JsonProperty("text_color") String textColor,
            @JsonProperty("modelClass") String modelClass,
            @JsonProperty("allowedExtensions") String allowedExtensions,
            @JsonProperty("checkCount") String checkCount,
            @JsonProperty("checkCountNumber") Integer checkCountNumber,
            @JsonProperty("maxFileSize") Integer maxFileSize,
            @JsonProperty("note") String notes,
            @JsonProperty("hide_text") Boolean isHideText) {
        this.parentId = parentId;
        this.index = index;
        this.isDefault = isDefault;
        this.code = checkNotBlank(code);
        this.description = description;
        this.iconType = iconType;
        this.iconImage = iconImage;
        this.iconFont = iconFont;
        this.iconColor = iconColor;
        this.textColor = textColor;
        this.notes = notes;
        this.active = active;
        this.modelClass = modelClass;
        this.allowedExtensions = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(Strings.nullToEmpty(allowedExtensions));
        this.maxFileSize = maxFileSize;
        this.checkCount = parseEnumOrNull(checkCount, DmsAttachmentCountCheck.class);
        this.checkCountNumber = checkCountNumber;
        this.isHideText = isHideText;
    }

    public LookupValueImpl.LookupBuilder buildLookup() {
        return LookupValueImpl.builder()
                .withCode(code)
                .withConfig(b -> b
                    .withIconColor(iconColor)
                    .withTextColor(textColor)
                    .withDefault(isDefault)
                    .withIconFont(iconFont)
                    .withIconImage(iconImage)
                    .withIconType(iconType)
                    .withAllowedExtensions(allowedExtensions)
                    .withCountCheck(checkCount)
                    .withCountCheckNumber(checkCountNumber)
                    .withMaxFileSize(maxFileSize)
                    .withDmsModel(modelClass)
                    .withHideText(isHideText))
                .withDescription(description)
                .withNotes(notes)
                .withIndex(index)
                .withActive(active)
                .withParentId(parentId);
    }

}
