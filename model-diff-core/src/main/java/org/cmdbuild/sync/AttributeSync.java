/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import java.util.Map;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.utils.lang.CmMapUtils;

/**
 *
 * @author afelice
 */
public interface AttributeSync {

    Attribute read(String entryTypeId, String attrId);

//    AttributeImpl.AttributeImplBuilder buildAttribute(String name, CardAttributeType type, Classe owner);
    Attribute add(Attribute attribute);

    Attribute update(Attribute attribute);

    Attribute build(String attribName, Map<String, Object> attribCmdbSerialization, EntryType ownerEntryType);

    Attribute build_toDeactivated(String attribName, Map<String, Object> attribCmdbSerialization, EntryType ownerEntryType);

//    void remove(Attribute attribute);
    Map<String, String> buildMetadata(Map<String, Object> attribCmdbSerialization);

    CmMapUtils.FluentMap<String, Object> serializeAttributeProps(Attribute curAttrib);

}
