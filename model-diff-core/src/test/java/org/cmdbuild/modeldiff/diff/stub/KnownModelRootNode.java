/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.stub;

import java.util.List;
import org.cmdbuild.modeldiff.diff.CmDifferRepository;
import org.cmdbuild.modeldiff.diff.CmModelNode;
import org.cmdbuild.modeldiff.diff.patch.CmDeltaList;
import org.cmdbuild.modeldiff.stub.KnownModelRoot;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

/**
 * Visitable, wrap for a real model stuff, composed of other model items.
 *
 * @author afelice
 */
public class KnownModelRootNode implements CmModelNode<KnownModelRootNode, KnownModelRoot> {

    private final KnownModelRoot model;

    private final List<KnownModelAggregatedItemNode> aggregatedComponents = list();

    protected String fakeDistinguishingName;

    public KnownModelRootNode(KnownModelRoot model) {
        this.model = model;
    }

    /**
     * To handle items (model data/schema) that hasn't a set distinguishing name
     * because newly inserted.
     *
     * <p>
     * Couldn't do this in a abstract intermediate class due to type erasure...
     *
     * @param fakeDistinguishingName
     */
    @Override
    public void overwriteDistinguishingName(String fakeDistinguishingName) {
        this.fakeDistinguishingName = fakeDistinguishingName;
    }

    @Override
    public String getDistinguishingName() {
        // Couldn't do this in a abstract intermediate class due to type erasure...
        return firstNotNull(fakeDistinguishingName,
                model.getName()
        );
    }

    @Override
    public CmDeltaList calculateDiff(CmDifferRepository differRepository, KnownModelRootNode rightModelNode) {
        CmDeltaList deltaList = new CmDeltaList();
        deltaList.addAll(differRepository.diff(this, rightModelNode));

        // Note: visit for diff ignores list of FirstLevel objects contained in Root model, instead this
        // ModelNode permits to aggregate KnownModelItem object (through their respective nodes)

        // Application of visitor to respective (aggregated by node) components
        deltaList.addAllAsComponentsDiff(differRepository.diffComposed(aggregatedComponents, rightModelNode.aggregatedComponents));

        return deltaList;
    }

    @Override
    public KnownModelRoot getModelObj() {
        return model;
    }

    public void addComponent(KnownModelAggregatedItemNode component) {
        aggregatedComponents.add(component);
    }

    public List<KnownModelAggregatedItemNode> getComponents() {
        return aggregatedComponents;
    }

    @Override
    public String toString() {
        return "KnownRootModelNode{model =< %s >= ([%d] aggregated components)}".formatted(getDistinguishingName(), getComponents().size());
    }

}
