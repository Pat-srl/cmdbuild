/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.serialization;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 *
 * @author ldare
 */
public class LookupValueSerializationHelper {

	// method returns FluentMap<Object, Object> cause accept prevents type inference
	public static FluentMap<Object, Object> serializeLookupValue(LookupValue lookup, ObjectTranslationService translationService, LookupService lookupService) {
		return map(
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
				"hide_text", lookup.isHideText(),
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

    public static List<FluentMap<Object, Object>> serializePagedLookupValues(PagedElements<LookupValue> lookups, ObjectTranslationService objectTranslationService, LookupService lookupService) {
        return lookups.stream().map(l -> serializeLookupValue(l, objectTranslationService, lookupService)).collect(toList());
    }
}
