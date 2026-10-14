/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.modeldiff.schema.ProcessConfiguration;

/**
 * <b>Concrete placeholder</b> for visitable (to engage polymorphism and choice
 * of visitor in related repository), wrap for a real <i>schema</i>
 * {@link Process} stuff, composed of {@link Attribute} items.
 *
 * @author afelice
 */
public class CmProcessSchemaNode extends CmClasseSchemaNode {

    public CmProcessSchemaNode(CmSchemaItemAttributesData itemData) {
        super(itemData);
    }

    /**
     * Node used for <i>topological sorting</i>.
     *
     * @param process
     * @return
     */
    static public CmProcessSchemaNode buildProcessSchemaNode(ProcessConfiguration process) {
        CmProcessSchemaNode result = new CmProcessSchemaNode(
                CmSchemaItemAttributesData.from(process.name,
                        process.getCmdbSerialization() // Process properties
                )
        );

        process.attributes.forEach(attribConfiguration
                -> result.addComponent(
                        new CmSchemaItemAttributesDataNode(
                                CmSchemaItemAttributesData.from(attribConfiguration.getName(), attribConfiguration.getCmdbSerialization()) // Attribute properties
                        ))
        );

        return result;
    }
}
