/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.modeldiff.diff.patch.CmChangeDelta;
import org.cmdbuild.modeldiff.diff.patch.CmDeltaList;
import org.cmdbuild.modeldiff.diff.patch.CmEqualDelta;
import org.cmdbuild.modeldiff.diff.patch.CmInsertDelta;
import org.cmdbuild.modeldiff.diff.patch.CmRemoveDelta;
import static org.cmdbuild.modeldiff.diff.schema.CmSchemaHelper.toMapByName;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;

/**
 *
 * @author afelice
 */
public abstract class AbstractCmModelNodeDiffer implements BaseCmModelNodeDiffer {

    @Override
    public <T extends CmModelNode> CmDeltaList diffComposed(List<T> left, List<T> right) {
        CmDeltaList diffList = buildOutputList();

        List<T> leftComponents = list(left);
        // (Eventually) fill blank distniguishing name for newly added components
        distinguishingNameFill(leftComponents);
        Map<String, T> leftItems = componentsToMap(leftComponents);
        leftComponents.sort(Comparator.comparing(t -> t.getDistinguishingName()));

        List<T> rightComponents = list(right);
        // (Eventually) fill blank distniguishing name for newly added components
        distinguishingNameFill(rightComponents);
        Map<String, T> rightItems = componentsToMap(rightComponents);
        rightComponents.sort(Comparator.comparing(t -> t.getDistinguishingName()));

        boolean first = true;
        T curLeft = null;
        T curRight = null;
        Set<String> processed = set();
        // O(2n) complexity; exit when both lists are processed
        for (Iterator<T> itLeft = leftComponents.iterator(), itRight = rightComponents.iterator();;) {
            if (first) {
                curLeft = getNext(itLeft);
                curRight = getNext(itRight);
                first = false;
            }

            if (curLeft == null && curRight == null) {
                // No more elements to process
                break;
            }

            if (curLeft != null && curRight != null) {
                // Something to compare in both lists
                if (curLeft.getDistinguishingName().equals(curRight.getDistinguishingName())) {
                    // (Potentially) changed element
                    diffList.addAll(diff(curLeft, curRight)); // Makes diff, invoking equal() or changed()
                    processed.add(curLeft.getDistinguishingName());

                    // Prepare for next iteration
                    curLeft = getNext(itLeft);
                    curRight = getNext(itRight);
                    continue;
                }
            }

            if (curLeft != null) {
                if (processed.contains(curLeft.getDistinguishingName())) {
                    // Already processed. Nothing to do
                } else if (rightItems.containsKey(curLeft.getDistinguishingName())) {
                    // (Potentially) changed element
                    diffList.addAll(diff(curLeft, rightItems.get(curLeft.getDistinguishingName()))); // Makes diff, invoking equal() or changed()
                    processed.add(curLeft.getDistinguishingName());
                } else {
                    // Inserted to left
                    diffList.addAll(inserted(curLeft.getDistinguishingName(), curLeft));
                }

                // Prepare for next iteration
                curLeft = getNext(itLeft);
            }

            if (curRight != null) {
                if (processed.contains(curRight.getDistinguishingName())) {
                    // Already processed. Nothing to do
                } else if (leftItems.containsKey(curRight.getDistinguishingName())) {
                    // (Potentially) changed element
                    diffList.addAll(diff(leftItems.get(curRight.getDistinguishingName()), curRight)); // Makes diff, invoking equal() or changed()
                    processed.add(curRight.getDistinguishingName());
                } else {
                    // Removed from left
                    diffList.addAll(removed(curRight.getDistinguishingName(), curRight));
                }

                // Prepare for next iteration
                curRight = getNext(itRight);
            }
        } // end for

        return diffList;
    } // end visitComposed method

    public <T extends CmModelNode<T, U>, U> Map<String, T> componentsToMap(List<T> dataList) {
        return toMapByName(dataList.stream(), T::getDistinguishingName);
    }

    protected CmDeltaList buildOutputList() {
        return new CmDeltaList();
    }

    /**
     * Newly added model schema/data can have it's distinguishing name empty:
     * fill it with unique (incremental) value
     *
     * @param <T> Model node
     * @param origList
     * @return
     */
    protected <T extends CmModelNode> void distinguishingNameFill(List<T> origList) {
        List<T> emptyDistinguishingNameColl = filterBlankDistinguishingName(origList);

        int inc = 1;
        for (Iterator<T> it = emptyDistinguishingNameColl.iterator(); it.hasNext(); inc++) {
            T curNode = it.next();
            curNode.overwriteDistinguishingName("<new_%d>".formatted(inc));
        }
    }

    protected <T extends CmModelNode> List<T> filterBlankDistinguishingName(List<T> origList) {
        return origList.stream().filter(d -> d.getDistinguishingName().isBlank() || d.getDistinguishingName().equals("0")).collect(toList());
    }

    protected <T extends CmModelNode> T getNext(Iterator<T> it) {
        T curIt = null;
        if (it.hasNext()) {
            curIt = it.next();
        }
        return curIt;
    }

    @Override
    public <T extends CmModelNode<T, U>, U> CmDeltaList equal(String distinguishingName, T modelNode) {
        return new CmDeltaList(new CmEqualDelta(modelNode.getClass(), distinguishingName, modelNode));
    }

    @Override
    public <T extends CmModelNode<T, U>, U> CmDeltaList inserted(String distinguishingName, T modelNode) {
        return new CmDeltaList(new CmInsertDelta(modelNode.getClass(), distinguishingName, modelNode));
    }

    @Override
    public <T extends CmModelNode<T, U>, U> CmDeltaList removed(String distinguishingName, T modelNode) {
        return new CmDeltaList(new CmRemoveDelta(modelNode.getClass(), distinguishingName, modelNode));
    }

    @Override
    public <T extends CmModelNode<T, U>, U> CmDeltaList changed(String distinguishingName, T leftModelNode, T rightModelNode) {
        return new CmDeltaList(new CmChangeDelta(leftModelNode.getClass(), distinguishingName, leftModelNode, rightModelNode));
    }

}
