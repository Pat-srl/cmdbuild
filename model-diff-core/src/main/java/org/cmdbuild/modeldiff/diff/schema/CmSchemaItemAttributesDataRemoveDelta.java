/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import java.util.Map;
import org.cmdbuild.modeldiff.diff.patch.CmRemoveDelta;

/**
 *
 * @author afelice
 */
public class CmSchemaItemAttributesDataRemoveDelta extends CmRemoveDelta<CmSchemaItemAttributesDataNode, CmSchemaItemAttributesData> {

    private final Map<String, Object> removedAattributesSerialization;
    
    public CmSchemaItemAttributesDataRemoveDelta(Class modelNodeClass, String distinguishingName,
            CmSchemaItemAttributesDataNode sourceModelNode,
            Map<String, Object> removedAttributesSerialization) {
        super(modelNodeClass, distinguishingName, sourceModelNode);
        
        this.removedAattributesSerialization = removedAttributesSerialization;
    }

    public Map<String, Object> getRemovedAttributesSerialization() {
        return removedAattributesSerialization;
    }
}
