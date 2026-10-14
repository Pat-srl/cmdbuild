/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import org.cmdbuild.modeldiff.diff.CmDifferRepository;
import org.cmdbuild.modeldiff.diff.CmModelNode;
import org.cmdbuild.modeldiff.diff.patch.CmDeltaList;

/**
 * Visitable, wrap for a real model stuff, composed of other model items.
 *
 * @author afelice
 */
public class CmSchemaItemAttributesDataNode implements CmModelNode<CmSchemaItemAttributesDataNode, CmSchemaItemAttributesData>, CmSchemaItemNode {

    /**
     * Aggregation information, to aggregate all <i>diff delta</i> regarding a
     * <i>schema item</i>.
     */
    private final CmSchemaItemAttributesData schemaItemAttributesData;

    public CmSchemaItemAttributesDataNode(CmSchemaItemAttributesData schemaAttributesData) {
        this.schemaItemAttributesData = schemaAttributesData;
    }

    @Override
    public CmDeltaList calculateDiff(CmDifferRepository differRepository, CmSchemaItemAttributesDataNode rightModelNode) {
        CmDeltaList deltaList = new CmDeltaList();
        deltaList.addAll(differRepository.diff(this, rightModelNode));

//        // Note: visit for diff ignores list of FirstLevel objects contained in Root model

        return deltaList;
    }

    @Override
    public CmSchemaItemAttributesData getModelObj() {
        return schemaItemAttributesData;
    }

    @Override
    public String toString() {
        return "%s{model =< %s >=: %s }".formatted(getClass().getName(), schemaItemAttributesData.getName(), schemaItemAttributesData.getAttributesSerialization());
    }

    @Override
    public String getDistinguishingName() {
        return getModelObj().getName();
    }

    @Override
    public void overwriteDistinguishingName(String fakeDistinguishingName) {
        // Do nothing
    }

}
