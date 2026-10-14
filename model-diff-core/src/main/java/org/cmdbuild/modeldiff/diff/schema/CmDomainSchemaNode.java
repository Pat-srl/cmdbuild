/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.lookup.LookupType;
import static org.cmdbuild.modeldiff.diff.schema.CmSchemaHelper.isLookup;
import static org.cmdbuild.modeldiff.diff.schema.CmSchemaItemDeltaBuilder.buildSchemaItemAttributesDataNode_Unique;
import org.cmdbuild.modeldiff.schema.DomainConfiguration;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.sync.DomainSync;
import org.cmdbuild.sync.LookupHandler;

/**
 * <b>Concrete placeholder</b> for visitable (to engage polymorphism and choice
 * of visitor in related repository), wrap for a real <i>schema</i>
 * {@link Domain} stuff, composed of {@link Attribute} items.
 *
 * <p>
 * Knows how to build a <i>schema node</i> for a {@link Domain} and its
 * contained {@link Attribute}s.
 * 
 * @author afelice
 */
public class CmDomainSchemaNode extends CmSchemaItemDataNode {

    public CmDomainSchemaNode(CmSchemaItemAttributesData itemData) {
        super(itemData);
    }
    
    /**
     * Adds to the node:
     * <ol>
     * <li>the serialization for the {@link Domain};
     * <li>for each contained {@link Attribute}, a
     * {@link CmSchemaItemAttributesDataNode} with attribute's serialization;
     * </ol>
     *
     * @param aDomain
     * @param lookupDataHandler
     * @param domainSync
     * @param attributeSerializer
     * @return
     */
    static public CmDomainSchemaNode from(Domain aDomain,
            LookupHandler lookupDataHandler, 
            DomainSync domainSync, AttributeTypeConversionService attributeSerializer) {

        Map<String, Object> domainSerialization = domainSync.serializeDomainProps(aDomain);

        // Store Domain serialization in a CmSchemaItemAttributesDataNode
        CmDomainSchemaNode result = new CmDomainSchemaNode(new CmSchemaItemAttributesData(aDomain.getName(), domainSerialization));

        // Add attributes as components
        aDomain.getAllAttributes().stream().
                map(curAttrib -> buildSchemaItemAttributesDataNode_Unique(aDomain.getName(), curAttrib.getName(), attributeSerializer.serializeAttributeType(curAttrib, true)))
                .forEach(curAttribNode -> result.addComponent(curAttribNode));

        // @todo AFE add lookups
        return result;
    }

    /**
     * Node used for <i>topological sorting</i>.
     *
     * @param domain
     * @return
     */
    static public CmDomainSchemaNode buildDomainSchemaNode(DomainConfiguration domain) {
        CmDomainSchemaNode result = new CmDomainSchemaNode(
                CmSchemaItemAttributesData.from(domain.name,
                        domain.getCmdbSerialization() // Domain properties
                )
        );

        domain.attributes.forEach(attribConfiguration
                -> result.addComponent(
                        new CmSchemaItemAttributesDataNode(
                                CmSchemaItemAttributesData.from(attribConfiguration.getName(), attribConfiguration.getCmdbSerialization()) // Attribute properties
                        ))
        );

        return result;
    }
    
    /**
     * Used when <i>removing</i> a {@link LookupType}: all related
     * <i>lookup attributes</i> has to be deactivated.
     *
     * @param lookupTypeNames
     * @return
     */
    public List<CmSchemaItemAttributesDataNode> getLookups(Set<String> lookupTypeNames) {
        return getComponents().stream().filter(attribDataNode -> isLookup(attribDataNode, lookupTypeNames)).collect(toList());
    }    
    
}
