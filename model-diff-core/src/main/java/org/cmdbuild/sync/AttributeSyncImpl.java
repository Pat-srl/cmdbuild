/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import java.util.Map;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.utils.lang.CmMapUtils;

/**
 *
 * @author afelice
 */
public abstract class AttributeSyncImpl implements AttributeSync {
    
    private final AttributeSchemaSerialization attributeSchemaSerialization;

    public AttributeSyncImpl(AttributeTypeConversionService attributeSerializer) {
        this.attributeSchemaSerialization = new AttributeSchemaSerialization(attributeSerializer);
    }    
    
    @Override
    public CmMapUtils.FluentMap<String, Object> serializeAttributeProps(Attribute curAttrib) {
        return attributeSchemaSerialization.serializeAttributeProps(curAttrib);
    }    
    
    @Override
    public Attribute build(String attribName, Map<String, Object> attribCmdbSerialization, EntryType owner) {
        return attributeSchemaSerialization.build(attribName, attribCmdbSerialization, owner);
    }
    
    /**
     * {@link Attribute} <i>metadata</i> is is exploded in CMDBuild
     * serialization and needs to be build again before <i>comparing</i>.
     *
     * @param attribCmdbSerialization
     * @return
     */
    @Override
    public Map<String, String> buildMetadata(Map<String, Object> attribCmdbSerialization) {
        return attributeSchemaSerialization.buildMetadata(attribCmdbSerialization);
    }    
    
    @Override
    public Attribute build_toDeactivated(String attribName, Map<String, Object> attribCmdbSerialization, EntryType ownerEntryType) {
        return attributeSchemaSerialization.build_toDeactivated(attribName, attribCmdbSerialization, ownerEntryType);
    }    
}
