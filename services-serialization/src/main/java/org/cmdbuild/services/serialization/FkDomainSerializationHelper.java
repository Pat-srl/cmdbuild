/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.serialization;


import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.entrytype.FkDomain;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

import java.util.Optional;

import static java.lang.String.format;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_CREATE;
import static org.cmdbuild.dao.utils.DomainUtils.serializeDomainCardinality;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
public class FkDomainSerializationHelper {

    public static FluentMap<String, Object> serializeFkDomain(FkDomain input, ObjectTranslationService translationService, UserClassService classService) {
        return map(
                "_id", format("%s_%s", input.getSourceClass().getName(), input.getSourceAttr().getName()),
                "source", input.getSourceClass().getName(),
                "sourceProcess", input.getSourceClass().isProcess(),
                "destination", input.getTargetClass().getName(),
                "destinationProcess", input.getTargetClass().isProcess(),
                "cardinality", serializeDomainCardinality(input.getCardinality()),
                "cascadeAction", serializeEnum(input.getCascadeAction()),
                //                "descriptionDirect", input.getDirectDescription(),
                //                "_descriptionDirect_translation", translationService.translateDomainDirectDescription(input.getName(), input.getDirectDescription()),
                //                "descriptionInverse", input.getInverseDescription(),
                //                "_descriptionInverse_translation", translationService.translateDomainInverseDescription(input.getName(), input.getInverseDescription()),
                //                "indexDirect", input.getIndexForSource(),
                //                "indexInverse", input.getIndexForTarget(),
                "descriptionMasterDetail", input.getMasterDetailDescription(),
                "_descriptionMasterDetail_translation", translationService.translateAttributeFkMasterDetailDescription(input.getSourceAttr(), input.getMasterDetailDescription()),
                //                "_descriptionMasterDetail_translation", translationService.translateDomainMasterDetailDescription(input.getName(), input.getMasterDetailDescription()),
                //                "filterMasterDetail", input.getMasterDetailFilter(),
                "isMasterDetail", input.isMasterDetail(),
                "fk_attribute_name", input.getSourceAttr().getName(),
                "fk_attribute_direction", serializeEnum(input.getDirection()),
                //                "inline", input.getMetadata().isInline(),
                //                "defaultClosed", input.getMetadata().isDefaultClosed(),
                //                "active", input.isActive(),
                //                "disabledSourceDescendants", CmCollectionUtils.toList(input.getDisabledSourceDescendants()),
                //                "disabledDestinationDescendants", CmCollectionUtils.toList(input.getDisabledTargetDescendants())
                "_can_create", Optional.ofNullable(classService.getUserClassOrNull(input.getSourceClass().getName())).map(c -> c.hasUiPermission(CP_CREATE)).orElse(false)
        );
    }
}
