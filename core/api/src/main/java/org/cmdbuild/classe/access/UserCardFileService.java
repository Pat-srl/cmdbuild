package org.cmdbuild.classe.access;

import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.EntryType;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FILE;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public interface UserCardFileService {

    Object prepareFileAttribute(Attribute attribute, Map<String, Object> values, String ownerName, @Nullable Card oldCard, long cardId);

    void clearDeletedAttachments(Card oldCard, Card newCard);

    default Map<String, Object> prepareFileAttributes(EntryType entryType, Map<String, Object> values, @Nullable Card oldCard, long cardId) {
        return map(entryType.getAllAttributesAsMap()).filterValues(a -> a.isActive() && a.isOfType(FILE) && values.containsKey(a.getName())).mapValues((k, a) -> prepareFileAttribute(a, values, entryType.getName(), oldCard, cardId));
    }
}
