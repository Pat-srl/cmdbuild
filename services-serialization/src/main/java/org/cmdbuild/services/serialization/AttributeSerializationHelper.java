/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.services.serialization;

import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.utils.date.CmDateUtils;

import java.util.Collection;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

/**
 *
 * @author afelice
 */
public class AttributeSerializationHelper {

    /**
     * TBC: will be used when a <code>services-serialization::Card_Helper</code>
     * will be developed to refactor <code>CardWsSerializationHelperv3</code>
     * <p>
     * Now is in WsAttributeconverterUtilsv3
     *
     * @param attributeType
     * @param value
     * @return
     */
    public static Object toClient(CardAttributeType<?> attributeType, Object value) {
        return switch (attributeType.getName()) {
            case DATE -> CmDateUtils.toIsoDate(value);
            case TIME -> CmDateUtils.toIsoTime(value);
            case TIMESTAMP -> CmDateUtils.toIsoDateTime(value);
            case REFERENCE, FOREIGNKEY, LOOKUP ->
                    Optional.ofNullable((IdAndDescription) rawToSystem(attributeType, value)).map(IdAndDescription::getId).orElse(null);
            case LOOKUPARRAY ->
                    list((Collection<IdAndDescription>) firstNotNull(rawToSystem(attributeType, value), emptyList())).map(IdAndDescription::getId);
            default -> value;
        };
    }
}
