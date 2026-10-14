/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.modeldiff.core.CmSerializationHelper;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_NAME_SERIALIZATION;
import static org.cmdbuild.modeldiff.diff.schema.CmSchemaHelper.fetchDefaultOrder;
import static org.cmdbuild.modeldiff.diff.schema.CmSchemaHelper.hasDefaultOrder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;

/**
 * Represents a <i>diff</i> on modified <i>schema</i>.
 *
 * @author afelice
 */
public class GeneratedDiffSchema {

    public String name;
    public String description;

    public List<GeneratedDiffSchema_Classe> insertedClasses = list();
    public List<GeneratedDiffSchema_Classe> removedClasses = list();
    public List<GeneratedDiffSchema_ChangedClasse> changedClasses = list();

    public List<GeneratedDiffSchema_Process> insertedProcesses = list();
    public List<GeneratedDiffSchema_Process> removedProcesses = list();
    public List<GeneratedDiffSchema_ChangedProcess> changedProcesses = list();

    public List<GeneratedDiffSchema_Domain> insertedDomains = list();
    public List<GeneratedDiffSchema_Domain> removedDomains = list();
    public List<GeneratedDiffSchema_ChangedDomain> changedDomains = list();

    public List<GeneratedDiffSchema_Lookup> insertedLookups = list();
    public List<GeneratedDiffSchema_Lookup> removedLookups = list();
    public List<GeneratedDiffSchema_ChangedLookup> changedLookups = list();

    public List<GeneratedDiffSchema_DmsModel> insertedDmsModels = list();
    public List<GeneratedDiffSchema_DmsModel> removedDmsModels = list();
    public List<GeneratedDiffSchema_ChangedDmsModel> changedDmsModels = list();

    public List<GeneratedDiffSchema_Lookup> insertedDmsCategories = list();
    public List<GeneratedDiffSchema_Lookup> removedDmsCategories = list();
    public List<GeneratedDiffSchema_ChangedLookup> changedDmsCategories = list();

    @JsonProperty("contextMenuItems")
    public GeneratedDiffSchema_ContextMenuItems changedContextMenuItems = new GeneratedDiffSchema_ContextMenuItems();

    public String xpdlZipFile = null;

    /**
     * Used internally by <i>apply schema diff</i> on {@link Classe} algorithm.
     */
    @JsonIgnore
    public List<String> classeInsertOrder = list();

    /**
     * Used internally by <i>apply schema diff</i> on {@link Process} algorithm.
     */
    @JsonIgnore
    public List<String> processInsertOrder = list();

    public boolean hasInsertedClasse(String curClasseName) {
        return findInserted(insertedClasses, curClasseName).isPresent();
    }

    public GeneratedDiffSchema_Classe getInsertedClasse(String curClasseName) {
        return findInserted(insertedClasses, curClasseName).get();
    }

    public synchronized boolean hasChangedClasse(String curClasseName) {
        return findChanged(changedClasses, curClasseName).isPresent();
    }

    public synchronized boolean hasChangedDomain(String curDomainName) {
        return findChanged(changedDomains, curDomainName).isPresent();
    }

    public synchronized GeneratedDiffSchema_ChangedClasse getChangedClasse(String curClasseName) {
        return findChanged(changedClasses, curClasseName).get();
    }

    public synchronized GeneratedDiffSchema_ChangedDomain getChangedDomain(String curDomainName) {
        return findChanged(changedDomains, curDomainName).get();
    }

    public GeneratedDiffSchema_Lookup getInsertedLookupType(String curLookupTypeName) {
        return findInserted(insertedLookups, curLookupTypeName).get();
    }

    public synchronized boolean hasChangedLookup(String curLookupTypeName) {
        return findChanged(changedLookups, curLookupTypeName).isPresent();
    }

    public synchronized GeneratedDiffSchema_ChangedLookup getChangedLookup(String curLookupTypeName) {
        return findChanged(changedLookups, curLookupTypeName).get();
    }

    public synchronized GeneratedDiffSchema_ChangedItemAttributes getChangedJSComponent(String curComponentId) {
        return changedContextMenuItems.components.getChangedAttributes().stream().filter(c -> curComponentId.equals(c.name)).findAny().get();
    }

    private <T extends GeneratedDiffSchema_SchemaItem> Optional<T> findInserted(List<T> insertedItems, String curItemName) {
        return insertedItems.stream().filter(c -> curItemName.equals(fetchName(c.getItemProperties()))).findAny();
    }

    private <T extends GeneratedDiffSchema_ChangedItem> Optional<T> findChanged(List<T> changedItems, String curItemName) {
        return changedItems.stream().filter(c -> curItemName.equals(c.getItemProps().getName())).findAny();
    }

    /**
     * Used when updating a parent {@link Classe} before a derived one: once
     * done, remove it to avoid processing this change again later on.
     *
     * @param curClasseName
     * @return
     */
    public synchronized GeneratedDiffSchema_ChangedClasse popChangedClasse(String curClasseName) {
        // Remove and return (pop) from changed classes
        return popChangedItem(changedClasses, curClasseName);
    }

    /**
     * Used when updating a parent {@link LookupType} before a derived one: once
     * done, remove it to avoid processing this change again later on.
     *
     * @param curLookupTypeName
     * @return
     */
    public synchronized GeneratedDiffSchema_ChangedLookup popChangedLookup(String curLookupTypeName) {
        // Remove and return (pop) from changed lookups
        return popChangedItem(changedLookups, curLookupTypeName);
    }

    public boolean hasDiffProcess() {
        return !insertedProcesses.isEmpty() || !changedProcesses.isEmpty();
    }

    public GeneratedDiffSchema_Process getInsertedProcess(String curProcessName) {
        return findInserted(insertedProcesses, curProcessName).get();
    }

    public synchronized boolean hasChangedProcess(String curProcessName) {
        return findChanged(changedProcesses, curProcessName).isPresent();
    }

    public synchronized GeneratedDiffSchema_ChangedProcess getChangedProcess(String curProcessName) {
        return findChanged(changedProcesses, curProcessName).get();
    }

    public boolean hasModifiedProcesses() {
        return !insertedProcesses.isEmpty() || !changedProcesses.isEmpty();
    }

    /**
     * Used when updating a parent {@link Process} before a derived one: once
     * done, remove it to avoid processing this change again later on.
     *
     * @param curProcessName
     * @return
     */
    public synchronized GeneratedDiffSchema_ChangedProcess popChangedProcess(String curProcessName) {
        // Remove and return (pop) from changed processes
        return popChangedItem(changedProcesses, curProcessName);
    }

    /**
     *
     * @return a map with
     * <dl>
     * <dt>key</dt>
     * <dd>the {@link Classe} name.
     * <dt>value</dt>
     * <dd>the <code>defaultOrder</code>
     * </dl>
     */
    @JsonIgnore
    public Map<String, List> getChangedDefaultOrderColl() {
        Map<String, List> result = mapOf(String.class, List.class);

        result.putAll(insertedClasses.stream()
                .filter(c -> hasDefaultOrder(c.getItemProperties()) && !(fetchDefaultOrder(c.getItemProperties())).isEmpty()) // While inserting a new Classe, a defaultOrder of [] means "nothing to do"
                .collect(Collectors.toMap(
                        GeneratedDiffSchema_Classe::getName,
                        c -> fetchDefaultOrder(c.getItemProperties())
                )
                ));
        result.putAll(changedClasses.stream()
                .filter(c -> hasDefaultOrder(c.getItemProps()))
                .collect(Collectors.toMap(
                        GeneratedDiffSchema_ChangedClasse::getName,
                        c -> fetchDefaultOrder(c.getItemProps())
                )
                ));
        return result;
    }

    /**
     * Workaround to fix <code>defaultOrder</code> with duplicated pairs coming
     * from unit-tests.
     */
    public void distinctDefaultOrder() {
        insertedClasses.stream()
                .filter(c -> hasDefaultOrder(c.getItemProperties()))
                .forEach(c -> {
                    List<Map> foundDefaultOrder = fetchDefaultOrder(c.getItemProperties());
                    List<Map> distinctDefaultOrder = foundDefaultOrder.stream().distinct().collect(Collectors.toList());
                    c.getItemProperties().put(CmSerializationHelper.ATTR_CLASSE_DEFAULT_ORDER_SERIALIZATION, distinctDefaultOrder);
                });
    }

    public boolean hasInsertedDmsModel(String curDmsModelName) {
        return findInserted(insertedDmsModels, curDmsModelName).isPresent();
    }

    public GeneratedDiffSchema_DmsModel getInsertedDmsModel(String curDmsModelName) {
        return findInserted(insertedDmsModels, curDmsModelName).get();
    }

    public synchronized boolean hasChangedDmsModel(String curDmsModelName) {
        return findChanged(changedDmsModels, curDmsModelName).isPresent();
    }

    public synchronized GeneratedDiffSchema_ChangedDmsModel getChangedDmsModel(String curDmsModelName) {
        return findChanged(changedDmsModels, curDmsModelName).get();
    }

    /**
     * Used when updating a parent <i>dms model</i> (a {@link Classe}) before a
     * derived one: once done, remove it to avoid processing this change again
     * later on.
     *
     * @param curDmsModelName
     * @return
     */
    public synchronized GeneratedDiffSchema_ChangedDmsModel popChangedDmsModel(String curDmsModelName) {
        // Remove and return (pop) from changed dms model
        return popChangedItem(changedDmsModels, curDmsModelName);
    }

    public boolean hasInsertedDmsCategoryType(String curDmsCategoryTypeName) {
        return findInserted(insertedDmsCategories, curDmsCategoryTypeName).isPresent();
    }

    public GeneratedDiffSchema_Lookup getInsertedDmsCategoryType(String curDmsCategoryTypeName) {
        return findInserted(insertedDmsCategories, curDmsCategoryTypeName).get();
    }

    public synchronized boolean hasChangedDmsCategoryType(String curDmsCategoryTypeName) {
        return findChanged(changedDmsCategories, curDmsCategoryTypeName).isPresent();
    }

    public synchronized GeneratedDiffSchema_ChangedLookup getChangedDmsCategoryType(String curDmsCategoryTypeName) {
        return findChanged(changedDmsCategories, curDmsCategoryTypeName).get();
    }

    /**
     * Used in <i>merge step</i>, when programmatically removing <i>Dms
     * categories</i> that refers to removed <i>Dms models</i>.
     *
     * @param dmsCategoryTypeName
     * @param toRemoveDmsCategories
     */
    public synchronized void addRemovedDmsCategories(String dmsCategoryTypeName, List<Map<String, Object>> toRemoveDmsCategories) {
        GeneratedDiffSchema_ChangedLookup changedDmsCategoryType;
        if (hasChangedDmsCategoryType(dmsCategoryTypeName)) {
            changedDmsCategoryType = getChangedDmsCategoryType(dmsCategoryTypeName);
        } else {
            changedDmsCategoryType = buildChangedDmsCategoryType(dmsCategoryTypeName);
            changedDmsCategories.add(changedDmsCategoryType);
        }

        if (changedDmsCategoryType.removedAttributes.isEmpty()) {
            changedDmsCategoryType.removedAttributes.addAll(toRemoveDmsCategories);
        } else {
            // Add only not already there to-remove Dms categories
            toRemoveDmsCategories.forEach(curToRem -> {
                String curToRemName = fetchName(curToRem);
                if (changedDmsCategoryType.removedAttributes.stream()
                        .map(m -> m.get(ATTR_NAME_SERIALIZATION))
                        .noneMatch(curToRemName::equals)) {
                    changedDmsCategoryType.removedAttributes.add(curToRem);
                }
            });
        }
    }

    /**
     * Used when updating a parent <i>dms category type</i> (a
     * {@link LookupType}) before a derived one: once done, remove it to avoid
     * processing this * change again later on.
     *
     * @param curDmsCategoryTypeName
     * @return
     */
    public synchronized GeneratedDiffSchema_ChangedLookup popChangedDmsCategoryType(String curDmsCategoryTypeName) {
        // Remove and return (pop) from changed dms categories
        return popChangedItem(changedDmsCategories, curDmsCategoryTypeName);
    }

    /**
     * Used when updating a parent {@link Classe} before a derived one: once
     * done, remove it to avoid processing this change again later on.
     *
     * @param curClasseName
     * @return
     */
    private synchronized <T extends GeneratedDiffSchema_ChangedItem> T popChangedItem(List<T> changedItems, String curItemName) {
        // Return removing from changed items
        T result = null;

        int curPos = 0;
        for (; curPos < changedItems.size(); curPos++) {
            if (changedItems.get(curPos).getName().equals(curItemName)) {
                result = changedItems.get(curPos);
                changedItems.remove(curPos);
                break; // Esci dal loop dopo la riomozione
            }
        }

        return result;
    }

    private String fetchName(Map<String, Object> props) {
        return (String) props.get(ATTR_NAME_SERIALIZATION);
    }

    private GeneratedDiffSchema_ChangedLookup buildChangedDmsCategoryType(String dmsCategoryTypeName) {
        GeneratedDiffSchema_ChangedLookup result = new GeneratedDiffSchema_ChangedLookup();
        GeneratedDiffSchema_ChangedItemProps lookupType = new GeneratedDiffSchema_ChangedItemProps();

        lookupType.name = dmsCategoryTypeName;
        lookupType.changedProps = list();
        result.setItemProps(lookupType);

        return result;
    }
}
