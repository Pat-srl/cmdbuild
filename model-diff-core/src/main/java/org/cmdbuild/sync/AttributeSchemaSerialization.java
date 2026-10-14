/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.modeldiff.diff.schema.CmSchemaHelper;
import org.cmdbuild.modeldiff.diff.schema.CmClasseSchemaNode;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.WsAttributeData;
import org.cmdbuild.utils.json.CmJsonUtils;
import org.cmdbuild.utils.lang.CmMapUtils;

/**
 *
 * @author afelice
 */
public class AttributeSchemaSerialization {

    protected static final ObjectMapper OBJECT_MAPPER = CmJsonUtils.getObjectMapper();

    protected final AttributeTypeConversionService attributeSerializer;

    public AttributeSchemaSerialization(AttributeTypeConversionService attributeSerializer) {
        this.attributeSerializer = checkNotNull(attributeSerializer);
    }        
    
    /**
     * 
     * @param attribName
     * @param attribCmdbSerialization
     * @param owner {@link Classe} or {@link Domain}
     * @return 
     */
    public Attribute build(String attribName, Map<String, Object> attribCmdbSerialization, EntryType owner) {
        WsAttributeData attribData = buildAttributeData(attribCmdbSerialization);

        return attribData.toAttrDefinition(owner);
    }

    /**
     * 
     * @param attribName
     * @param attribCmdbSerialization
     * @param owner {@link Classe} or {@link Domain}
     * @return 
     */
    public Attribute build_toDeactivated(String attribName, Map<String, Object> attribCmdbSerialization, EntryType owner) {
        Map<String, Object> deactivatedAttribCmdbSerialization = CmSchemaHelper.toDeactivated(attribCmdbSerialization);
        WsAttributeData deactivatedAttribData = buildAttributeData(deactivatedAttribCmdbSerialization);

        return deactivatedAttribData.toAttrDefinition(owner);
    }

    public CmMapUtils.FluentMap<String, Object> serializeAttributeProps(Attribute curAttrib) {
        return attributeSerializer.serializeAttributeType(curAttrib, false);
    }

    /**
     * {@link Attribute} <i>metadata</i> is is exploded in CMDBuild
     * serialization and needs to be build again before <i>comparing</i>.
     *
     * @param attribCmdbSerialization
     * @return
     */
    public Map<String, String> buildMetadata(Map<String, Object> attribCmdbSerialization) {
        WsAttributeData attribData = buildAttributeData(attribCmdbSerialization);

        return attribData.toAttrDefinition().getMetadata().getAll();
    }

    /**
     * {@link Attribute} <i>metadata</i> is exploded in CMDBuild serialization
     * and needs to be build again before <i>adding</i>/<i>updating</i>.
     *
     * @param attribCmdbSerialization
     * @return
     */
    public WsAttributeData buildAttributeData(Map<String, Object> attribCmdbSerialization) {
        // Reconstructs metadata from Attribute serialization        
        return getSystemObjectMapper().convertValue(attribCmdbSerialization, WsAttributeData.class);
    }    

    protected ObjectMapper getSystemObjectMapper() {
        return OBJECT_MAPPER;
    }
    
    /**
     * As done in {@link ClassAttributeWs}.
     *
     * @param attribCmdbSerialization
     * @param ownerEntryType
     * @return
     */
    // @todo AFE TBC
    private Attribute buildAttributeFor(Map<String, Object> attribCmdbSerialization, EntryType ownerEntryType) {
        WsAttributeData attribData = buildAttributeData(attribCmdbSerialization);

        return attribData.toAttrDefinition(ownerEntryType);
    }
    
}
