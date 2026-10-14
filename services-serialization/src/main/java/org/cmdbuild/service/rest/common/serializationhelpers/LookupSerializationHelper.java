/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import com.google.common.base.Joiner;
import org.cmdbuild.common.beans.LookupValue;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.BiConsumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;

@Component
public class LookupSerializationHelper {

    private final ObjectTranslationService translationService;
    private final LookupService lookupService;

    public LookupSerializationHelper(ObjectTranslationService translationService, LookupService lookupService) {
        this.translationService = checkNotNull(translationService);
        this.lookupService = checkNotNull(lookupService);
    }

    public FluentMap<String, Object> serializeLookupValue(org.cmdbuild.lookup.LookupValue lookup) {
        return mapOf(String.class, Object.class).with(
                "_id", lookup.getId(),
                "_type", lookup.getType().getName(),
                "code", lookup.getCode(),
                "description", lookup.getDescription(),
                "_description_translation", translationService.translateLookupDescriptionSafe(lookup.getType().getName(), lookup.getCode(), lookup.getDescription()),
                "index", lookup.getIndex(),
                "active", lookup.isActive(),
                "parent_id", lookup.getParentId(),
                "parent_type", Optional.ofNullable(lookup.getParentTypeOrNull()).map(p -> lookupService.getLookupType(p).getName()).orElse(null),
                "default", lookup.isDefault(),
                "note", lookup.getNotes(),
                "text_color", lookup.getTextColor(),
                "icon_type", lookup.getIconType().name().toLowerCase(),
                "icon_image", lookup.getIconImage(),
                "icon_font", lookup.getIconFont(),
                "icon_color", lookup.getIconColor(),
                "accessType", serializeEnum(lookupService.getLookupType(lookup.getLookupType()).getAccessType())).accept(m -> {
            if (lookup.getType().isDmsCategorySpeciality()) {
                m.put(
                        "modelClass", lookup.getConfig().getDmsModelClass(),
                        "allowedExtensions", Joiner.on(",").join(nullToEmpty(lookup.getConfig().getDmsAllowedExtensions())),
                        "checkCount", serializeEnum(lookup.getConfig().getDmsCheckCount()),
                        "checkCountNumber", lookup.getConfig().getDmsCheckCountNumber(),
                        "maxFileSize", lookup.getConfig().getMaxFileSize());
            }
        });
    }

    /**
     *
     * @param name
     * @param value
     * @param adder
     */
    public void serializeLookupValueDetail(String name, LookupValue value, BiConsumer<String, Object> adder) {
        adder.accept(name, value.getCode());
        adder.accept(format("_%s_description", name), value.getDescription());
        adder.accept(format("_%s_description_translation", name), translationService.translateLookupDescriptionSafe(value.getLookupType(), value.getCode(), value.getDescription()));
    }

    /**
     *
     * @param <E>
     * @param name
     * @param value
     * @param adder
     */
    public <E extends Enum> void serializeLookupValueDetail(String name, E value, BiConsumer<String, Object> adder) {
        LookupSerializationHelper.this.serializeLookupValueDetail(name, lookupService.getLookup(value), adder);
    }

    public FluentMap<String, Object> serializeLookupType(LookupType lookupType) {
        return mapOf(String.class, Object.class).with(
                "_id", lookupType.getName(),
                "name", lookupType.getName(),
                "parent", Optional.ofNullable(lookupType.getParent()).map(l -> lookupService.getLookupType(l).getName()).orElse(null),
                "speciality", serializeEnum(lookupType.getSpeciality()),
                "accessType", serializeEnum(lookupType.getAccessType()));
    }
}
