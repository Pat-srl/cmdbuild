/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.patch;

import java.util.List;
import java.util.function.Predicate;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.utils.lang.CmCollectionUtils.FluentList;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 * List of {@link AbstractCmDelta}, organized as:
 * <dl>
 * <dt>root level <dd>list of diffs for the root level;
 * <dt>components level <dd>list of diffs for the components level;
 * </dl>
 *
 * @author afelice
 */
public class CmDeltaList {

    protected FluentList<AbstractCmDelta> diff;

    protected CmDeltaList componentsDiff;

    public CmDeltaList() {
        this.diff = list();
    }

    public CmDeltaList(AbstractCmDelta diff) {
        this.diff = list(diff);
    }

    /**
     * Copy constructor.
     *
     * @param origDiff
     */
    public CmDeltaList(CmDeltaList origDiff) {
        this.diff = list(origDiff.diff);
        if (origDiff.componentsDiff != null) {
            this.componentsDiff = new CmDeltaList(origDiff.componentsDiff);
        }
    }

    public boolean hasAlready(Class<?> modelNodeClass, String distinguishingName) {
        return diff.stream().anyMatch(d -> d.getModelNodeClass().equals(modelNodeClass) && d.isEqual() && d.getDistinguishingName().equals(distinguishingName));
    }

    public void add(AbstractCmDelta cmDelta) {
        if (hasAlready(cmDelta.getModelNodeClass(), cmDelta.getDistinguishingName())) {
            throw new UnsupportedOperationException("Already treaded diff for =< %s > with distinguishingName =< %s >".formatted(cmDelta.getModelNodeClass(), cmDelta.getDistinguishingName()));
        }
        diff.add(cmDelta);
    }

    /**
     * Add all deltas as root diff and component diffs.
     *
     * @param cmDeltaList
     */
    public void addAll(CmDeltaList cmDeltaList) {
        cmDeltaList.diff.forEach(this::add); // to make the uniqueness check

        if (cmDeltaList.hasComponentsDiffs()) {
            // Add all components diff as components.
            if (componentsDiff == null) {
                componentsDiff = new CmDeltaList();
            }
            componentsDiff.addAll(cmDeltaList.getComponentsDiff());
        }
    }

    /**
     * Add all deltas as components diff.
     *
     * @param cmDeltaList
     */
    public synchronized void addAllAsComponentsDiff(CmDeltaList cmDeltaList) {
        if (cmDeltaList.hasComponentsDiffs()) {
            // Nothing to do
            return;
        }

        // Add all root deltas as components diff
        if (componentsDiff == null) {
            componentsDiff = new CmDeltaList();
        }
        cmDeltaList.diff.forEach(componentsDiff::add);
        if (cmDeltaList.hasComponentsDiffs()) {
            throw new UnsupportedOperationException("unsupported multi-level components diffs, found [%d] components".formatted(cmDeltaList.componentsDiffSize()));
        }
    }

    public boolean hasChanges() {
        boolean rootChanges = diff.stream().anyMatch(d -> d.getDeltaType() != CmDeltaType.CM_DT_EQUAL);
        boolean componentsChanges = (componentsDiff == null) ? false : componentsDiff.hasChanges();

        return rootChanges || componentsChanges;
    }

    public boolean hasRootDiffs() {
        return !diff.isEmpty();
    }

    public int rootDiffSize() {
        return diff.size();
    }

    public boolean hasComponentsDiffs() {
        return componentsDiff != null && componentsDiff.hasRootDiffs();
    }

    public int componentsDiffSize() {
        return (componentsDiff == null) ? 0 : componentsDiff.rootDiffSize();
    }

    public AbstractCmDelta get(int aPos) {
        return diff.get(aPos);
    }

    public List<AbstractCmDelta> getRootDiffs() {
        return diff;
    }

    public List<AbstractCmDelta> getRootDiffsInserted() {
        return filterRoot(AbstractCmDelta::isInsert);
    }

    public List<AbstractCmDelta> getRootDiffsRemoved() {
        return filterRoot(AbstractCmDelta::isRemove);
    }

    public List<AbstractCmDelta> getRootDiffsChanged() {
        return filterRoot(AbstractCmDelta::isChange);
    }

    public CmDeltaList getComponentsDiff() {
        return (componentsDiff == null) ? new CmDeltaList() : new CmDeltaList(componentsDiff);
    }

    public CmDeltaList removeEquals() {
        diff.without(AbstractCmDelta::isEqual);
        return this;
    }

    public List<AbstractCmDelta> filterRoot(Predicate<AbstractCmDelta> filterPredicate) {
        return diff.stream().filter(filterPredicate).collect(toList());
    }

}
