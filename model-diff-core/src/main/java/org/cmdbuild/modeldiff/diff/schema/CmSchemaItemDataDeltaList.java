/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.modeldiff.diff.patch.AbstractCmDelta;
import org.cmdbuild.modeldiff.diff.patch.CmDeltaList;
import org.cmdbuild.modeldiff.diff.patch.CmInsertDelta;
import org.cmdbuild.modeldiff.diff.patch.CmRemoveDelta;
import org.cmdbuild.utils.lang.CmCollectionUtils.FluentList;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * Handles <b>two level of changes</b> for <i>schema item</i>, with changes in:
 * <ol>
 * <li>root for <i>schema item</i> properties changes;
 * <li>list of changes for component attributes.
 * </ol>
 *
 * @author afelice
 */
public class CmSchemaItemDataDeltaList extends CmDeltaList {

    private int nestingLevel = 0;

    /**
     * Two level diff
     */
    private final List<CmSchemaItemDataDeltaList> compoundComponentsDiff = list();

    public CmSchemaItemDataDeltaList() {
        super();
    }

    private CmSchemaItemDataDeltaList(CmDeltaList origDiff) {
        super(origDiff);
    }

    public CmSchemaItemDataDeltaList(AbstractCmDelta diff) {
        super(diff);
    }

    /**
     * Add all deltas as root diff and component diffs. If it's a
     * {@link CmSchemaItemDataDeltaList}, add even compound components diffs.
     *
     * @param cmDeltaList
     */
    @Override
    public void addAll(CmDeltaList cmDeltaList) {
        super.addAll(cmDeltaList);

        if (cmDeltaList instanceof CmSchemaItemDataDeltaList cmSchemaItemDeltaList) {
            compoundComponentsDiff.addAll(cmSchemaItemDeltaList.compoundComponentsDiff);
        }
    }

    /**
     * Add all deltas as components diff.
     *
     * <p>
     * Handles two levels of component diffs, for <i>schema item</i> and for
     * <i>schema item attributes</i>. In super implementation all root diff are
     * added as components (only one level of diffs is handled)
     *
     * @param cmDeltaList with components diff while calculating a <i>schema
     * item</i> diff.
     */
    @Override
    public synchronized void addAllAsComponentsDiff(CmDeltaList cmDeltaList) {
        if (cmDeltaList.hasComponentsDiffs()) {
            // usupported three levels of diffs
            throw unsupported("unsupported three levels of diffs for =< %s >=".formatted(cmDeltaList.getComponentsDiff().get(0).getSourceModelNode().getDistinguishingName()));
        }

        // Add all (other) delta root deltas as component diff
        if (componentsDiff == null) {
            componentsDiff = new CmDeltaList();
        }
        cmDeltaList.getRootDiffs().forEach(dl -> componentsDiff.add(dl));
    }

    /**
     * Handles compound component diffs, that is, for each item, a
     * {@link CmDeltaList} containing:
     * <ol>
     * <li>the diff on <i>schema item</i>;
     * <li>a list of diffs (a {@link CmDeltaList}) for each of its attributes (a
     * component).
     * </ol>
     *
     * @param itemDiffList
     */
    public void addAsCompoundComponent(CmSchemaItemDataDeltaList itemDiffList) {
        itemDiffList.incrementNestingLevel();
        compoundComponentsDiff.add(itemDiffList);
    }

    /**
     * Handles component diffs, that is, for each component diff, a
     * {@link CmDeltaList} containing:
     * <ol>
     * <li>the diff on <i>schema item</i>;
     * <li>a list of diffs (a {@link CmDeltaList}) for each of its attributes (a
     * component).
     * </ol>
     *
     * @param componentDiffList
     */
    public void addAllAsCompoundComponent(CmDeltaList componentDiffList) {
        componentDiffList.getRootDiffs().forEach(compDff -> {
            CmSchemaItemDataDeltaList secondLevelCompDiff = new CmSchemaItemDataDeltaList(compDff);
            compoundComponentsDiff.add(secondLevelCompDiff);
        });
    }

    public List<CmSchemaItemDataDeltaList> getCompoundComponentsDiff() {
        return compoundComponentsDiff;
    }

    @Override
    public boolean hasChanges() {
        return super.hasChanges()
                || compoundComponentsDiff.stream().anyMatch(d -> d.hasChanges());
    }

    /**
     *
     * @param modelNodeClass
     * @param distinguishingName
     * @return
     */
    public boolean hasAlreadyComponent(Class<?> modelNodeClass, String distinguishingName) {
        if (componentsDiff == null) {
            return false;
        }

        return componentsDiff.hasAlready(modelNodeClass, distinguishingName);
    }

    /**
     * Compact changes on a <i>schema item</i> if related to same
     * <code>distinguishingName</code>.
     */
    public void compactChanged() {
        if (diff.size() <= 1) {            
            return; // nothing to compact
        }
        
        FluentList<AbstractCmDelta> compactedDiff = list();

        Map<String, AbstractCmDelta> changedTempMap = map();
        for (AbstractCmDelta cmDelta : diff) {
            if (cmDelta.isChange()) {
                final String curDistinguishingName = cmDelta.getDistinguishingName();
                if (!changedTempMap.containsKey(curDistinguishingName)) {
                    changedTempMap.put(cmDelta.getDistinguishingName(), cmDelta);
                } else if (changedTempMap.get(curDistinguishingName) instanceof CmSchemaItemAttributesDataChangeDelta curCmChangeDelta) {
                    curCmChangeDelta.addAll((CmSchemaItemAttributesDataChangeDelta) cmDelta);
                }
            } else {
                compactedDiff.add(cmDelta);
            }
        }

        compactedDiff.addAll(changedTempMap.values());

        this.diff = compactedDiff;
    }

    /**
     * Handles two levels of diff:
     * <ol>
     * <li><i>schema item</i> diff in root diffs;
     * <li><i>schema item attributes</i> diffs in component diff.
     * </ol>
     *
     * @param cmDeltaList
     * @return
     */
    public static CmSchemaItemDataDeltaList from(CmDeltaList cmDeltaList) {
        CmSchemaItemDataDeltaList result = new CmSchemaItemDataDeltaList(cmDeltaList);

        // Convert component diffs, too
        if (cmDeltaList.hasComponentsDiffs()) {
            if (result.componentsDiff == null) {
                result.componentsDiff = new CmSchemaItemDataDeltaList(cmDeltaList.getComponentsDiff());
            } else {
                result.componentsDiff.addAll(cmDeltaList.getComponentsDiff());
            }
        }

        return result;
    }

    /**
     * Check only in first level if inserted <i>schema item</i>
     *
     * <p>
     * Note: all its <i>attributes</i> will be in inserted mode, too.
     *
     * @return
     */
    public List<CmSchemaItemDataDeltaList> getCompoundComponentDiffsInserted() {
        return filterRootCompoundComponentDiffs(AbstractCmDelta::isInsert); // Only first level
    }

    /**
     * Check only in first level if removed <i>schema item</i>
     *
     * <p>
     * Note: all its <i>attributes</i> will be in removed mode, too.
     *
     * @return
     */
    public List<CmSchemaItemDataDeltaList> getCompoundComponentDiffsRemoved() {
        return filterRootCompoundComponentDiffs(AbstractCmDelta::isRemove); // Only first level
    }

    /**
     * Check only in first level if changed <i>schema item</i>.
     * 
     * <p>handled change delta that is emptyed by some peculiar programmatic handling
     * (f.e. to handle NullNode in Json content VS "null" or null).
     *
     * @return
     */
    public List<CmSchemaItemDataDeltaList> getCompoundComponentDiffsChanged() {
        // Only first level, only really changed stuff
        return list(filterRootCompoundComponentDiffs(AbstractCmDelta::isChange)).withOnly(c -> notEquivalent(c.get(0))); 
    }

    /**
     * Check only in second level if inserted <i>schema item</i>
     *
     * @return
     */
    public List<CmSchemaItemDataDeltaList> getSecondLevelCompoundComponentDiffsInserted() {
        if (compoundComponentsDiff.isEmpty()) {
            return emptyList();
        }
        
        return filterDiffs(compoundComponentsDiff.get(0).getCompoundComponentsDiff(), AbstractCmDelta::isInsert); // Only first level
    }

    /**
     * Check only in second level if removed <i>schema item</i>
     *
     * @return
     */
    public List<CmSchemaItemDataDeltaList> getSecondLevelCompoundComponentDiffsRemoved() {
        if (compoundComponentsDiff.isEmpty()) {
            return emptyList();
        }
        
        return filterDiffs(compoundComponentsDiff.get(0).getCompoundComponentsDiff(), AbstractCmDelta::isRemove); // Only first level
    }

    /**
     * Check only in second level if changed <i>schema item</i>.
     *
     * @return
     */
    public List<CmSchemaItemDataDeltaList> getSecondLevelCompoundComponentDiffsChanged() {
        if (compoundComponentsDiff.isEmpty()) {
            return emptyList();
        }
        
        // Only second level, only really changed stuff
        return list(filterDiffs(compoundComponentsDiff.get(0).getCompoundComponentsDiff(), AbstractCmDelta::isChange)).withOnly(c -> notEquivalent(c.get(0))); 
    }    
    
    /**
     * Check if changed, in cascade mode:
     * <ol>
     * <li>in first level (<code>diff</code>) deltas;
     * <li>in second level (<code>compoundComponentsDiff</code>) deltas, if
     * change/insert/remove.
     * </ol>
     *
     * @return
     */
    public List<CmSchemaItemDataDeltaList> getCompoundComponentDiffsChanged_Cascade() {
        List<CmSchemaItemDataDeltaList> result = list();

        compoundComponentsDiff.forEach(comp -> {
            boolean isItemChange = hasRoot(comp, AbstractCmDelta::isChange);
            boolean hasComponentChanges = hasRoot(comp, AbstractCmDelta::isEqual)
                    && comp.compoundComponentsDiff.stream().filter(this::isModify).findAny().isPresent();
            if (isItemChange || hasComponentChanges) {
                result.add(onlyChangedComponents(comp));
            }
        });

        return result;
    }

    public List<CmSchemaItemDataDeltaList> filterCompoundComponent(Predicate<AbstractCmDelta> filterPredicate) {
        // A compound component has a component in selected mode
        return compoundComponentsDiff.stream().filter(comp
                -> comp.compoundComponentsDiff.stream().filter(delta -> hasRoot(delta, filterPredicate)).findAny().isPresent()
        )
                .collect(toList());
    }

    protected List<CmSchemaItemDataDeltaList> filterCompoundComponentDiffs(List<CmSchemaItemDataDeltaList> compoundComponentsDiff, Predicate<AbstractCmDelta> filterPredicate) {
        // A compound component has a component in selected mode
        return compoundComponentsDiff.stream().filter(comp
                -> comp.compoundComponentsDiff.stream().filter(delta -> hasRoot(delta, filterPredicate)).findAny().isPresent()
        )
                .collect(toList());
    }    

    protected List<CmSchemaItemDataDeltaList> filterDiffs(List<CmSchemaItemDataDeltaList> aCompoundComponentsDiff, Predicate<AbstractCmDelta> filterPredicate) {
        // A compound component has a component in selected mode
        return aCompoundComponentsDiff.stream().filter(delta -> hasRoot(delta, filterPredicate))
                .collect(toList());
    }  
    
    private void incrementNestingLevel() {
        nestingLevel++;

        if (componentsDiff != null) {
            ((CmSchemaItemDataDeltaList) componentsDiff).incrementNestingLevel();
        }
    }

    protected CmSchemaItemDataDeltaList onlyChangedComponents(CmSchemaItemDataDeltaList origDeltaList) {
        CmSchemaItemDataDeltaList result = new CmSchemaItemDataDeltaList(origDeltaList.diff.get(0));

        origDeltaList.compoundComponentsDiff.forEach(comp -> {
            // get only compound diff in selected mode
            List<AbstractCmDelta> changedComps = comp.diff.stream().filter(this::isModify).collect(toList());

            if (!changedComps.isEmpty()) {
                if (notEquivalent(comp.diff.get(0))) {
                    CmSchemaItemDataDeltaList reducedChangedComp = new CmSchemaItemDataDeltaList(comp.diff.get(0));                
                    result.compoundComponentsDiff.add(reducedChangedComp);
                }
            }
        });

        return result;
    }

    private boolean notEquivalent(AbstractCmDelta cmDelta) {
        if (cmDelta instanceof CmInsertDelta || cmDelta instanceof CmRemoveDelta) {
            return true;
        } else if (cmDelta instanceof CmSchemaItemAttributesDataChangeDelta changeDelta) { 
            return changeDelta.isSomethingChanged();
        }
        
        return false;
    }
    
    protected boolean isModify(CmSchemaItemDataDeltaList deltaList) {
        return deltaList.diff.stream().filter(this::isModify).findAny().isPresent();
    }

    protected boolean isModify(AbstractCmDelta delta) {
        return delta.isInsert() || delta.isRemove() || delta.isChange();
    }

    /**
     * Check only on first level (<code>diff</code>) delta.
     *
     * @param filterPredicate
     * @return
     */
    protected List<CmSchemaItemDataDeltaList> filterRootCompoundComponentDiffs(Predicate<AbstractCmDelta> filterPredicate) {
        // A compound component has a diff in selected mode and a list of related CmDeltaList.
        return compoundComponentsDiff.stream().filter(comp -> hasRoot(comp, filterPredicate)).collect(toList());
    }

    /**
     * Check only on second level (<code>diff</code>) delta.
     *
     * @param filterPredicate
     * @return
     */
    protected List<CmSchemaItemDataDeltaList> filterSecondLevelCompoundComponentDiffs(Predicate<AbstractCmDelta> filterPredicate) {
        if (compoundComponentsDiff.isEmpty()) {
            return emptyList();
        }
        
        // Compound components second level has a diff in selected mode and a list of related CmDeltaList.
        return filterCompoundComponentDiffs(compoundComponentsDiff.get(0).compoundComponentsDiff, filterPredicate);
    }    
    
    protected boolean hasRoot(CmDeltaList compoundComponentDiff, Predicate<AbstractCmDelta> filterPredicate) {
        return !(compoundComponentDiff.filterRoot(filterPredicate)).isEmpty();
    }

    protected boolean hasCompoundComponent(CmSchemaItemDataDeltaList compoundComponentDiff, Predicate<AbstractCmDelta> filterPredicate) {
        return !(compoundComponentDiff.filterCompoundComponent(filterPredicate)).isEmpty();
    }

}
