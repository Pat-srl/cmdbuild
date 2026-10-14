/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff;

import java.util.List;
import java.util.Map;
import static org.cmdbuild.modeldiff.diff.TestDeltaHelper.checkChanged;
import static org.cmdbuild.modeldiff.diff.TestDeltaHelper.checkEqual;
import static org.cmdbuild.modeldiff.diff.TestDeltaHelper.checkInserted;
import static org.cmdbuild.modeldiff.diff.TestDeltaHelper.checkRemoved;
import org.cmdbuild.modeldiff.diff.patch.CmDeltaList;
import org.cmdbuild.modeldiff.diff.stub.KnownModelAggregatedItemNode;
import org.cmdbuild.modeldiff.diff.stub.KnownModelRootNode;
import org.cmdbuild.modeldiff.stub.KnownModelItem;
import org.cmdbuild.modeldiff.stub.KnownModelRoot;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author afelice
 */
public class AbstractCmModelNodeDifferTest {

    /**
     * Test of visitComposed method, of class AbstractCmModelNodeDiffer.
     *
     * <dl>
     * <dt>root
     * <dd>changed name
     * <dt>items
     * <dd>left: 1 2 4 6 (added 4)
     * <dd>right; 1 2 3 5 6* 7 (removed 3, 5, 7; changed 6)
     * </dl>
     */
    @Test
    public void testVisitComposed() {
        System.out.println("visitComposed");

        //arrange:
        // ** Model and data **
        KnownModelItem item_1 = new KnownModelItem("1", "one", "1_1");
        KnownModelItem item_2 = new KnownModelItem("2", "two", "1_2");
        KnownModelItem item_3 = new KnownModelItem("3", "three", "2_3");
        KnownModelItem item_4 = new KnownModelItem("4", "four", "1_4");
        KnownModelItem item_5 = new KnownModelItem("5", "five", "2_5");
        KnownModelItem item_6_left = new KnownModelItem("6", "six", "1_6");
        KnownModelItem item_6_right = new KnownModelItem("6", "six", "2_6");
        KnownModelItem item_7 = new KnownModelItem("7", "seven", "2_7");

        // left: root A (description origDescr), items: 1 2 4 6 (removed 4)
        KnownModelRoot root_left = new KnownModelRoot("A");
        root_left.setDescription("origDescr");
        KnownModelRootNode root_left_node = new KnownModelRootNode(root_left);
        root_left_node.addComponent(new KnownModelAggregatedItemNode(item_1));
        root_left_node.addComponent(new KnownModelAggregatedItemNode(item_2));
        root_left_node.addComponent(new KnownModelAggregatedItemNode(item_4));
        root_left_node.addComponent(new KnownModelAggregatedItemNode(item_6_left));

        // right: root B (description newDescr), items: 1 2 3 5 6* 7 (added 3, 5, 7; changed 6)
        KnownModelRoot root_right = new KnownModelRoot("A");
        root_right.setDescription("a description");
        KnownModelRootNode root_right_node = new KnownModelRootNode(root_right);
        root_right_node.addComponent(new KnownModelAggregatedItemNode(item_1));
        root_right_node.addComponent(new KnownModelAggregatedItemNode(item_2));
        root_right_node.addComponent(new KnownModelAggregatedItemNode(item_3));
        root_right_node.addComponent(new KnownModelAggregatedItemNode(item_5));
        root_right_node.addComponent(new KnownModelAggregatedItemNode(item_6_right));
        root_right_node.addComponent(new KnownModelAggregatedItemNode(item_7));

        // ** Diffs ** (based on model nodes structure)
        CmDifferRepository differRepository = new CmDifferRepository();
        final KnownModelRootDiffer rootModelVisitor = new KnownModelRootDiffer();
        final KnownModelAggregatedItemDiffer modelItemVisitor = new KnownModelAggregatedItemDiffer();
        differRepository.add(rootModelVisitor);
        differRepository.add(modelItemVisitor);

        //act:
        CmDeltaList diff = root_left_node.calculateDiff(differRepository, root_right_node);

        //assert:
        assertTrue(diff.hasChanges());
        // Root diff
        assertEquals(1, diff.rootDiffSize());
        checkChanged(0, KnownModelRootNode.class, root_left.getName(), root_left, root_right, diff);
        // Components diff
        assertEquals(7, diff.componentsDiffSize());
        CmDeltaList componentsDiff = diff.getComponentsDiff();
        checkEqual(0, KnownModelAggregatedItemNode.class, item_1.getId(), item_1, componentsDiff);
        checkEqual(1, KnownModelAggregatedItemNode.class, item_2.getId(), item_2, componentsDiff);
        checkInserted(2, KnownModelAggregatedItemNode.class, item_4.getId(), item_4, componentsDiff);
        checkRemoved(3, KnownModelAggregatedItemNode.class, item_3.getId(), item_3, componentsDiff);
        checkChanged(4, KnownModelAggregatedItemNode.class, item_6_left.getId(), item_6_left, item_6_right, componentsDiff);
        checkRemoved(5, KnownModelAggregatedItemNode.class, item_5.getId(), item_5, componentsDiff);
        checkRemoved(6, KnownModelAggregatedItemNode.class, item_7.getId(), item_7, componentsDiff);
    }

    /**
     * Test of visitComposed method, for anonymous (with empty distinguishing
     * name) components, of class AbstractCmModelNodeDiffer.
     *
     * <p>
     * Each new inserted node has to be something unique in the
     * <i>distinguishing name</i>
     *
     * <dl>
     * <dt>root
     * <dd>changed name
     * <dt>items
     * <dd>left: 1 2 (4) 6 (added 4)
     * <dd>right; 1 2 (3) (5) 6* (7) (removed 3, 5, 7; changed 6)
     * </dl>
     */
    @Test
    public void testVisit_MobileAnonymousCards() {
        System.out.println("visit_MobileAnonymousCards");

        //arrange:
        // ** Model and data **
        KnownModelItem item_1 = new KnownModelItem("1", "one", "1_1");
        KnownModelItem item_2 = new KnownModelItem("2", "two", "1_2");
        KnownModelItem item_3 = new KnownModelItem("", "", "2_3");
        KnownModelItem item_4 = new KnownModelItem("4", "four", "1_4");
        KnownModelItem item_5 = new KnownModelItem("", "", "2_5");
        KnownModelItem item_6_left = new KnownModelItem("6", "six", "1_6");
        KnownModelItem item_6_right = new KnownModelItem("6", "six", "2_6");
        KnownModelItem item_7 = new KnownModelItem("", "", "2_7");

        // left: root A (description origDescr), items: 1 2 4 6 (removed 4)
        KnownModelRoot root_left = new KnownModelRoot("A");
        root_left.setDescription("origDescr");
        KnownModelRootNode root_left_node = new KnownModelRootNode(root_left);
        root_left_node.addComponent(new KnownModelAggregatedItemNode(item_1));
        root_left_node.addComponent(new KnownModelAggregatedItemNode(item_2));
        root_left_node.addComponent(new KnownModelAggregatedItemNode(item_4));
        root_left_node.addComponent(new KnownModelAggregatedItemNode(item_6_left));

        // right: root B (description newDescr), items: 1 2 3 5 6* 7 (added 3, 5, 7; changed 6)
        KnownModelRoot root_right = new KnownModelRoot("A");
        root_left.setDescription("origDescr");
        root_right.setDescription("a description");
        KnownModelRootNode root_right_node = new KnownModelRootNode(root_right);
        root_right_node.addComponent(new KnownModelAggregatedItemNode(item_1));
        root_right_node.addComponent(new KnownModelAggregatedItemNode(item_2));
        root_right_node.addComponent(new KnownModelAggregatedItemNode(item_3));
        root_right_node.addComponent(new KnownModelAggregatedItemNode(item_5));
        root_right_node.addComponent(new KnownModelAggregatedItemNode(item_6_right));
        root_right_node.addComponent(new KnownModelAggregatedItemNode(item_7));

        // ** Diffs ** (based on model nodes structure)
        CmDifferRepository differRepository = new CmDifferRepository();
        final KnownModelRootDiffer rootModelVisitor = new KnownModelRootDiffer();
        final KnownModelAggregatedItemDiffer modelItemVisitor = new KnownModelAggregatedItemDiffer();
        differRepository.add(rootModelVisitor);
        differRepository.add(modelItemVisitor);

        //act:
        CmDeltaList diff = root_left_node.calculateDiff(differRepository, root_right_node);

        //assert:
        assertTrue(diff.hasChanges());
        // Root diff
        assertEquals(1, diff.rootDiffSize());
        checkChanged(0, KnownModelRootNode.class, root_left.getName(), root_left, root_right, diff);
        // Components diff
        assertEquals(7, diff.componentsDiffSize());
        CmDeltaList componentsDiff = diff.getComponentsDiff();
        checkEqual(0, KnownModelAggregatedItemNode.class, item_1.getId(), item_1, componentsDiff);
        checkEqual(1, KnownModelAggregatedItemNode.class, item_2.getId(), item_2, componentsDiff);
        checkInserted(2, KnownModelAggregatedItemNode.class, item_4.getId(), item_4, componentsDiff);
        checkChanged(3, KnownModelAggregatedItemNode.class, item_6_left.getId(), item_6_left, item_6_right, componentsDiff);
        checkRemoved(4, KnownModelAggregatedItemNode.class, "<new_1>", item_3, componentsDiff);
        checkRemoved(5, KnownModelAggregatedItemNode.class, "<new_2>", item_5, componentsDiff);
        checkRemoved(6, KnownModelAggregatedItemNode.class, "<new_3>", item_7, componentsDiff);
    }

    @Test
    public void testDistinguishingNameFiller() {
        System.out.println("distintuishingNameFiller");

        //arrange:
        final KnownModelAggregatedItemDiffer itemDiffer = new KnownModelAggregatedItemDiffer();
        KnownModelItem item_1 = new KnownModelItem("1", "one", "1_1");
        KnownModelItem item_2 = new KnownModelItem("2", "two", "1_2");
        KnownModelItem item_3 = new KnownModelItem("", "", "2_3");
        KnownModelItem item_4 = new KnownModelItem("4", "four", "1_4");
        KnownModelItem item_5 = new KnownModelItem(" ", "", "2_5");
        KnownModelItem item_6 = new KnownModelItem("6", "six", "1_6");
        KnownModelItem item_7 = new KnownModelItem(Integer.toString(0), "", "2_7");
        List<KnownModelAggregatedItemNode> items = list(
                new KnownModelAggregatedItemNode(item_1),
                new KnownModelAggregatedItemNode(item_2),
                new KnownModelAggregatedItemNode(item_3),
                new KnownModelAggregatedItemNode(item_4),
                new KnownModelAggregatedItemNode(item_5),
                new KnownModelAggregatedItemNode(item_6),
                new KnownModelAggregatedItemNode(item_7));
        assertEquals(3, itemDiffer.filterBlankDistinguishingName(items).size());

        //act:
        itemDiffer.distinguishingNameFill(items);
        assertEquals(0, itemDiffer.filterBlankDistinguishingName(items).size());
        Map<String, KnownModelAggregatedItemNode> result = itemDiffer.componentsToMap(items);

        //assert:
        assertEquals(7, result.keySet().size());
        assertTrue(result.keySet().contains("<new_1>"));
        assertTrue(result.keySet().contains("<new_2>"));
        assertTrue(result.keySet().contains("<new_3>"));
    }

} // end test class

/**
 * The visitor to calculate equal/changed (a.k.a. the "differ", who calculates
 * the diff) on a {@link KnownModelRootNode} * containing a {@link KnownModelRoot}.
 *
 * <p>
 * All is based on a <i>distinguishing name</i> (see {@link KnownModelRootNode})
 * to calculate if insert/remove/equal/changed.
 *
 * @author afelice
 */
class KnownModelRootDiffer extends AbstractCmModelNodeDiffer implements CmModelNodeDiffer<KnownModelRootNode, KnownModelRoot> {

    /**
     *
     * @param <T> Model
     * @param <U> Model node
     * @param left
     * @param right
     * @return
     */
    @Override
    public <T extends CmModelNode<T, U>, U> CmDeltaList diff(T left, T right) {
        final KnownModelRoot leftRoot = ((KnownModelRootNode) left).getModelObj();
        final KnownModelRoot rightRoot = ((KnownModelRootNode) right).getModelObj();

        if (leftRoot.getDescription().equals(rightRoot.getDescription())) {
            return equal(left.getDistinguishingName(), left);
        } else {
            return changed(left.getDistinguishingName(), left, right);
        }
        // inserted and removed are handled in lists of nodes
    }

    @Override
    public Class<KnownModelRootNode> getTarget() {
        return KnownModelRootNode.class;
    }
} // end KnownModelRootDiffer class

class KnownModelAggregatedItemDiffer extends AbstractCmModelNodeDiffer implements CmModelNodeDiffer<KnownModelAggregatedItemNode, KnownModelItem> {

    @Override
    public <T extends CmModelNode<T, U>, U> CmDeltaList diff(T left, T right) {
        final KnownModelItem leftItem = ((KnownModelAggregatedItemNode) left).getModelObj();
        final KnownModelItem rightItem = ((KnownModelAggregatedItemNode) right).getModelObj();

        if (leftItem.getName().equals(rightItem.getName())
                && leftItem.getValue().equals(rightItem.getValue())) {
            return equal(left.getDistinguishingName(), left);
        } else {
            return changed(left.getDistinguishingName(), left, right);
        }

        // insert and remove are handled in lists of nodes
    }

    @Override
    public Class<KnownModelAggregatedItemNode> getTarget() {
        return KnownModelAggregatedItemNode.class;
    }

} // end KnownModelAggregatedItemDiffer class
