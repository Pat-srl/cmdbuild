/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import java.util.Map;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.modeldiff.diff.AbstractCmModelNodeDiffer;
import org.cmdbuild.modeldiff.diff.CmModelNode;
import org.cmdbuild.modeldiff.diff.CmModelNodeDiffer;
import org.cmdbuild.modeldiff.diff.DiffEquals;
import org.cmdbuild.modeldiff.diff.patch.AbstractCmDelta;
import org.cmdbuild.modeldiff.diff.patch.CmDeltaList;
import org.cmdbuild.modeldiff.diff.patch.CmEqualDelta;
import org.cmdbuild.modeldiff.diff.patch.CmInsertDelta;
import org.cmdbuild.modeldiff.diff.patch.CmRemoveDelta;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * The visitor to calculate equal/changed (a.k.a. the "differ", who calculates
 * the diff) on a {@link CmSchemaItemDataNode} containing data (<b>only
 * <code>name</code> and <code>container type</code></b>) (a
 * {@link CmSchemaItemData}) for:
 * <ul>
 * <li>{@link Classe};
 * <li>{@link Process};
 * <li>{@link Domain};
 * <li>{@link LookupType};
 * <li><i>Dms model</i>;
 * <li><i>Dms category</i>.
 * </ul>
 *
 * <p>
 * All is based on a <i>distinguishing name</i> (see
 * {@link CmSchemaItemDataNode}) to calculate if insert/remove/equal, that is
 * the presence/absence of related <code>name</code> in a list of
 * {@link CmSchemaItemDataNode}.
 *
 * @author afelice
 */
public class CmSchemaItemDataDiffer extends AbstractCmModelNodeDiffer implements CmModelNodeDiffer<CmSchemaItemDataNode, CmSchemaItemData> {
    
    /**
     *
     * @param <T> Model Node
     * @param <U> Model class (a <i>schema item</i> with
     *
     * @param curLeft
     * @param curRight
     * @return
     */
    @Override
    public <T extends CmModelNode<T, U>, U> CmSchemaItemDataDeltaList diff(T curLeft, T curRight) {
        if (curLeft.getDistinguishingName().equals(curRight.getDistinguishingName())) {
            // May be a schema item or one of its attributes

            CmSchemaItemDataDeltaList diffList = new CmSchemaItemDataDeltaList();

            if (curLeft instanceof CmSchemaItemDataNode curLeftItemNode
                    && curRight instanceof CmSchemaItemDataNode curRightItemNode) {
                // Schema item: add item diff as component of outer diff

                // Compare item properties
                CmSchemaItemDataDeltaList itemDiffList = diffMap(
                        toItemAttributeNode(curLeftItemNode),
                        toItemAttributeNode(curRightItemNode)
                );

                if (!itemDiffList.hasRootDiffs()) {
                    // Item properties not changed
                    itemDiffList.add(buildEqual(toItemAttributeNode(curLeftItemNode)));
                }

                // Compare contained attributes...
                CmDeltaList composedDelta = diffComposed(curLeftItemNode.getComponents(), curRightItemNode.getComponents());
                // ...and add attributes diff as components of item diff
                itemDiffList.addAllAsCompoundComponent(composedDelta);

                // add as componet of components
                diffList.addAsCompoundComponent(itemDiffList);
            } else if (curLeft instanceof CmSchemaItemAttributesDataNode curLeftItemAttributeNode
                    && curRight instanceof CmSchemaItemAttributesDataNode curRightItemAttributeNode) {
                // Same schema item attribute
                // Compare attribute versions
                diffList.addAll(diffMap(curLeftItemAttributeNode, curRightItemAttributeNode));
            } else {
                throw unsupported("trying to compare a =< %s > with a =< %s > for item with distinguishing name =< %s >".formatted(curLeft.getClass().getName(), curRight.getClass().getName(), curLeft.getDistinguishingName()));
            }

            if (diffList.hasChanges()) {
                // Return the diff
                return diffList;
            }

            // Return an Equal
            final CmDeltaList equalDelta = equal(curLeft.getDistinguishingName(), toItemAttributeNode(curLeft));
            return CmSchemaItemDataDeltaList.from(equalDelta);
        } else {
            throw unsupported("two items can't be diffed if their id/name is different");
        }

        // inserted and removed are handled in lists of nodes, see AbstractCmModelNodeDiffer.diffComposed()
    }

    @Override
    public <T extends CmModelNode<T, U>, U> CmDeltaList inserted(String distinguishingName, T modelNode) {
        if (modelNode instanceof CmSchemaItemDataNode curLeftItemNode) {
            // Schema item: add item inserted, and its components too. Will be added to outer diff
            CmSchemaItemDataDeltaList itemInsertedList = new CmSchemaItemDataDeltaList();

            // the item
            AbstractCmDelta insertedItem = new CmInsertDelta(curLeftItemNode.getClass(), distinguishingName, toItemAttributeNode(curLeftItemNode));
            itemInsertedList.add(insertedItem);

            // the components
            CmDeltaList composedDelta = new CmDeltaList();
            curLeftItemNode.getComponents()
                    .forEach(compNode -> {
                        AbstractCmDelta insertedItemComp = new CmInsertDelta(compNode.getClass(), compNode.getDistinguishingName(), compNode);
                        composedDelta.add(insertedItemComp);
                    });
            // ...and add attributes insertion as components of item inserted
            itemInsertedList.addAllAsCompoundComponent(composedDelta);

            CmSchemaItemDataDeltaList outerDeltaList = new CmSchemaItemDataDeltaList();
            outerDeltaList.addAsCompoundComponent(itemInsertedList);

            return outerDeltaList;
        }

        // Schema item attribute
        return super.inserted(distinguishingName, toItemAttributeNode(modelNode));
    }

    @Override
    public <T extends CmModelNode<T, U>, U> CmDeltaList removed(String distinguishingName, T modelNode) {
        if (modelNode instanceof CmSchemaItemDataNode curRightItemNode) {
            // Schema item: add item removed, and its components too. Will be added to outer diff
            CmSchemaItemDataDeltaList itemRemovedList = new CmSchemaItemDataDeltaList();

            // the item
            AbstractCmDelta removedItem = new CmRemoveDelta(curRightItemNode.getClass(), distinguishingName, toItemAttributeNode(curRightItemNode));
            itemRemovedList.add(removedItem);

            // the components
            CmDeltaList composedDelta = new CmDeltaList();
            curRightItemNode.getComponents()
                    .forEach(compNode -> {
                        AbstractCmDelta removedItemComp = new CmRemoveDelta(compNode.getClass(), compNode.getDistinguishingName(), compNode);
                        composedDelta.add(removedItemComp);
                    });
            // ...and add attributes removing as components of item removed
            itemRemovedList.addAllAsCompoundComponent(composedDelta);

            CmSchemaItemDataDeltaList outerDeltaList = new CmSchemaItemDataDeltaList();
            outerDeltaList.addAsCompoundComponent(itemRemovedList);

            return outerDeltaList;
        }

        // Schema item attribute
        return super.removed(distinguishingName, toItemAttributeNode(modelNode));
    }

    @Override
    public Class<CmSchemaItemDataNode> getTarget() {
        return CmSchemaItemDataNode.class;
    }

    /**
     * Diff for map of values (f.e. the data values of item attributes).
     *
     * @param leftSchemaItemNode a {@link CmSchemaItemDataNode} or a
     * {@link CmSchemaItemAttributesDataNode}.
     * @param rightSchemaItemNode a {@link CmSchemaItemDataNode} or a
     * {@link CmSchemaItemAttributesDataNode}.
     * @return
     */
    public CmSchemaItemDataDeltaList diffMap(CmSchemaItemNode leftSchemaItemNode, CmSchemaItemNode rightSchemaItemNode) {
        CmSchemaItemDataDeltaList diffList = new CmSchemaItemDataDeltaList();

        // Compare item serialization        
        Map<String, Object> leftSerialization = getItemSerialization(leftSchemaItemNode.getModelObj());
        Map<String, Object> rightSerialization = getItemSerialization(rightSchemaItemNode.getModelObj());

        MapDifference<String, Object> mapDifference = Maps.difference(leftSerialization, rightSerialization, DiffEquals.INSTANCE);

        // @todo AFE TBC: non dovrebbe servire, per come funziona dao.update(card): gli attributi non cambiati non vengono sovrascritti, si passa solo le cose aggiunte/cambiate
//        Map<String, Object> mapEqualities = mapDifference.entriesInCommon();
//        if (!mapEqualities.isEmpty()) {
//            diffList.add(buildChangeDelta_Unchanged(rightSchemaItemDataNode, leftSchemaItemDataNode, mapEqualities));
//        }
        // missing on left
        FluentMap<String, Object> removedItemValues = map(mapDifference.entriesOnlyOnRight());
        if (!removedItemValues.isEmpty()) {
            diffList.add(CmSchemaItemDeltaBuilder.buildChangeDelta_Removed_Attrib(toItemAttributeNode(rightSchemaItemNode), toItemAttributeNode(leftSchemaItemNode), removedItemValues));
        }

        // added to left        
        FluentMap<String, Object> addedItemValues = map(mapDifference.entriesOnlyOnLeft());
        if (!addedItemValues.isEmpty()) {
            // Adding is a change for card attributes map
            diffList.add(CmSchemaItemDeltaBuilder.buildChangeDelta_Added_Attrib(toItemAttributeNode(rightSchemaItemNode), toItemAttributeNode(leftSchemaItemNode), addedItemValues));
        }

        // different values
        AbstractCmDelta changeDelta = CmSchemaItemDeltaBuilder.buildChangeDelta_Attrib(toItemAttributeNode(leftSchemaItemNode), toItemAttributeNode(rightSchemaItemNode), mapDifference);
        if (changeDelta != null) {
            diffList.add(changeDelta);
        }

        if (diffList.hasChanges()) {
            diffList.compactChanged();
        }

        return diffList;
    }

    @Override
    protected CmDeltaList buildOutputList() {
        return new CmSchemaItemDataDeltaList();
    }

    private Map<String, Object> getItemSerialization(CmSchemaItemData schemaItemData) {
        if (schemaItemData instanceof CmSchemaItemAttributesData schemaItemAttributesData) {
            return schemaItemAttributesData.getAttributesSerialization();
        }

        return map();
    }

    /**
     * To calculate diff, only <i>item properties</i> are needed (so a {@link CmInsertDelta}, {@link CmRemoveDelta},
     * {@link CmSchemaItemAttributesDataChangeDelta} or {@link CmEqualDelta}
     * with a {@link CmSchemaItemAttributesDataNode} will be generated in
     * {@link AbstractCmModelNodeDiffer#diffMap() }, and so only a
     * {@link CmSchemaItemAttributesDataNode} will be used while serializing
     * diff, later on.
     *
     * @param itemDataNode
     * @return
     */
    private CmSchemaItemAttributesDataNode toItemAttributeNode(Object itemDataNode) {
        if (itemDataNode instanceof CmSchemaItemAttributesDataNode attribNode) {
            return attribNode;
        } else if (itemDataNode instanceof CmSchemaItemDataNode itemNode) {
            return new CmSchemaItemAttributesDataNode(itemNode.getModelObj());
        }

        throw new UnsupportedOperationException("unhandled case =< %s >, expected a =< %s > ".formatted(itemDataNode.getClass(), CmSchemaItemAttributesDataNode.class));
    }

    protected static <T extends CmModelNode<T, U>, U> CmEqualDelta buildEqual(T curLeft) {
        return new CmEqualDelta(curLeft.getClass(), curLeft.getDistinguishingName(), curLeft);
    }

}
