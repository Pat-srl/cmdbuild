/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.stub;

import org.cmdbuild.modeldiff.diff.CmDifferRepository;
import org.cmdbuild.modeldiff.diff.CmModelNode;
import org.cmdbuild.modeldiff.diff.CmModelNodeDiffer;
import org.cmdbuild.modeldiff.diff.patch.CmDeltaList;
import org.cmdbuild.modeldiff.stub.KnownModelItem;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

/**
 *
 * @author afelice
 */
public class KnownModelAggregatedItemNode implements CmModelNode<KnownModelAggregatedItemNode, KnownModelItem> {

    private final KnownModelItem item;

    protected String fakeDistinguishingName;

    public KnownModelAggregatedItemNode(KnownModelItem item) {
        this.item = item;
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
                item.getId()
        );
    }

    @Override
    public CmDeltaList calculateDiff(CmDifferRepository visitorRepository, KnownModelAggregatedItemNode rightModelNode) {
        CmModelNodeDiffer<KnownModelAggregatedItemNode, KnownModelItem> visitor = visitorRepository.get(this.getClass());

        return visitor.diff(this, rightModelNode);
    }

    @Override
    public KnownModelItem getModelObj() {
        return item;
    }

    @Override
    public String toString() {
        return "KnownModelAggregatedItemNode{item =< %s >}".formatted(getDistinguishingName());
    }

} // end KnownModelFirstLevelItemNode class
