/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import java.util.List;
import org.cmdbuild.modeldiff.diff.CmDifferRepository;
import org.cmdbuild.modeldiff.diff.CmModelNode;
import org.cmdbuild.modeldiff.diff.patch.CmDeltaList;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 * Visitable, wrap for a real <i>schema</i> model stuff, composed of other model
 * items.
 *
 * @author afelice
 */
public class CmSchemaItemDataNode implements CmModelNode<CmSchemaItemDataNode, CmSchemaItemData>, CmSchemaItemNode {

    /**
     * Item name and properties.
     */
    protected final CmSchemaItemAttributesData itemData;

    /**
     * Item attributes, each with a name and properties.
     */
    private final List<CmSchemaItemAttributesDataNode> aggregatedComponents = list();

    public CmSchemaItemDataNode(CmSchemaItemAttributesData itemData) {
        this.itemData = itemData;
    }

    @Override
    public CmDeltaList calculateDiff(CmDifferRepository differRepository, CmSchemaItemDataNode rightModelNode) {
        CmDeltaList deltaList = new CmDeltaList();
        deltaList.addAll(differRepository.diff(this, rightModelNode));

        // Note: visit for diff ignores list of FirstLevel objects contained in Root model, instead this
        // ModelNode permits to aggregate CmSchemaItemDataNode inherited object (through their respective nodes)
        // Application of visitor to respective (aggregated by node) components
        deltaList.addAllAsComponentsDiff(differRepository.diffComposed(aggregatedComponents, rightModelNode.aggregatedComponents));

        return deltaList;
    }

    @Override
    public CmSchemaItemAttributesData getModelObj() {
        return itemData;
    }

    public void addComponent(CmSchemaItemAttributesDataNode component) {
        aggregatedComponents.add(component);
    }

    public List<CmSchemaItemAttributesDataNode> getComponents() {
        return aggregatedComponents;
    }

    @Override
    public String toString() {
        return "%s{model =< %s >= ([%d]props, [%d] aggregated components)}".formatted(getClass().getSimpleName(), getDistinguishingName(), itemData.getAttributesSerialization().size(), getComponents().size());
    }

    @Override
    public String getDistinguishingName() {
        return getModelObj().getName();
    }

    @Override
    public void overwriteDistinguishingName(String fakeDistinguishingName) {
        // Do nothing
    }
    
    public CmSchemaItemAttributesDataNode getAttributeNode(String attribName) {
        return getComponents().stream().filter(aNode -> aNode.getModelObj().getName().equals(attribName)).findFirst().get();
    }
            
    public boolean hasAttributeNode(String attribName) {
        return getComponents().stream().filter(aNode -> aNode.getModelObj().getName().equals(attribName)).findAny().isPresent();
    }      
        
}
