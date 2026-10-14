package org.cmdbuild.service.rest.common.serializationhelpers;

import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.Optional;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

public class WsAttributeConverterUtilsv3 {

    public static Object toClient(CardAttributeType<?> attributeType, Object value) {
        return switch (attributeType.getName()) {
            case DATE ->
                CmDateUtils.toIsoDate(value);
            case TIME ->
                CmDateUtils.toIsoTime(value);
            case TIMESTAMP ->
                CmDateUtils.toIsoDateTime(value);
            case REFERENCE, FOREIGNKEY, LOOKUP ->
                Optional.ofNullable((IdAndDescription) rawToSystem(attributeType, value)).map(IdAndDescription::getId).orElse(null);
            case LOOKUPARRAY ->
                list((Collection<IdAndDescription>) firstNotNull(rawToSystem(attributeType, value), emptyList())).map(IdAndDescription::getId);
            default ->
                value;
        };
    }
}
