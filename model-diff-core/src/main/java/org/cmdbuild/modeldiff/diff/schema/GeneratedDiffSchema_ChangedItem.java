/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import org.cmdbuild.modeldiff.core.CmSerializationHelper;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * Represents a <i>diff</i>, a changed <i>schema item</i>, on modified
 * <i>schema</i>.
 *
 * @author afelice
 */
public class GeneratedDiffSchema_ChangedItem {

    protected GeneratedDiffSchema_ChangedItemProps itemProps;

    protected List<Map<String, Object>> insertedAttributes = list();

    protected List<Map<String, Object>> removedAttributes = list();

    protected List<GeneratedDiffSchema_ChangedItemAttributes> changedAttributes = list();

    /**
     * Override in your class to use your tag.
     *
     * @return
     */
    @JsonProperty("itemProps")
    public GeneratedDiffSchema_ChangedItemProps getItemProps() {
        return itemProps;
    }

    /**
     * Override in your class to use your tag.
     *
     * @param itemProps
     */
    @JsonProperty("itemProps")
    public void setItemProps(GeneratedDiffSchema_ChangedItemProps itemProps) {
        this.itemProps = itemProps;
    }

    public boolean hasChangedProps() {
        return getItemProps().hasChangedProps();
    }

    /**
     * Override in your class to use your tag.
     *
     * @return
     */
    @JsonProperty("insertedAttributes")
    public List<Map<String, Object>> getInsertedAttributes() {
        return insertedAttributes;
    }

    /**
     * Override in your class to use your tag.
     *
     * @param insertedAttributes
     */
    @JsonProperty("insertedAttributes")
    public void setInsertedAttributes(List<Map<String, Object>> insertedAttributes) {
        this.insertedAttributes = insertedAttributes;
    }

    /**
     * Override in your class to use your tag.
     *
     * @return
     */
    @JsonProperty("removedAttributes")
    public List<Map<String, Object>> getRemovedAttributes() {
        return removedAttributes;
    }

    /**
     * Override in your class to use your tag.
     *
     * @param removedAttributes
     */
    @JsonProperty("removedAttributes")
    public void setRemovedAttributes(List<Map<String, Object>> removedAttributes) {
        this.removedAttributes = removedAttributes;
    }

    /**
     * Override in your class to use your tag.
     *
     * @return
     */
    @JsonProperty("changedAttributes")
    public List<GeneratedDiffSchema_ChangedItemAttributes> getChangedAttributes() {
        return changedAttributes;
    }

    /**
     * Override in your class to use your tag.
     *
     * @param changedAttributes
     */
    @JsonProperty("changedAttributes")
    public void setChangedAttributes(List<GeneratedDiffSchema_ChangedItemAttributes> changedAttributes) {
        this.changedAttributes = changedAttributes;
    }

    /**
     * Applies cumulative properties changes (inserted, changed) to given
     * <code>origProps</code>,
       *
     * @param origProps
     * @return
     */
    public Map<String, Object> calculatePropChanges(Map<String, Object> origProps) {
        return map(origProps).with(itemProps.getAllNewAttributeValues());
    }

    @JsonIgnore
    public String getName() {
        return itemProps.getName();
    }

    @JsonIgnore
    public boolean isActive() {
        final Map<String, Object> cumulativeValues = getItemProps().getCumulativeAttributeValues();
        return !cumulativeValues.containsKey(CmSerializationHelper.ATTR_ACTIVE_SERIALIZATION) || // "active" property not changed, so nothing to do on Classe/domain activation/deactivation
                CmSchemaHelper.isActive(cumulativeValues);
    }

    // @todo AFE fix this
//    public Map<String, Object> getNewAttributeValues() {
//        // Leads to NullPointerException for null newValue
////        return attribs.stream()
////                .collect(Collectors.toMap(
////                        GeneratedDiffData_CardAttribute::getAttribName,
////                        GeneratedDiffData_CardAttribute::getNewValue));
//        Map<String, Object> result = map();
//        changedAttributes.forEach(a -> {
//            result.put(a.attribName, a.newValue);
//        });
//
//        return result;
//    }

    /**
     * Returns changed plus added attributes.
     *
     * @return
     */
    // @todo AFE fix this
//    public Map<String, Object> getAllNewAttributeValues() {
//        CmMapUtils.FluentMap<String, Object> result = map();
//        return result.with(getNewAttributeValues()).with(insertedAttributes);
//    }
    // @todo AFE fix this
//    public Map<String, Object> getOldAttributeValues() {
//        Map<String, Object> result = map();
//        changedAttributes.forEach(a -> {
//            result.put(a.attribName, a.oldValue);
//        });
//
//        return result;
//    }

    /**
     * Returns changed plus removed attributes.
     *
     * @return
     */
    // @todo AFE fix this
//    public Map<String, Object> getAllOldAttributeValues() {
//        CmMapUtils.FluentMap<String, Object> result = map();
//        return result.with(getOldAttributeValues()).with(removedAttributes);
//    }

    private CmMapUtils.FluentMap<String, Object> notNull(Map<String, Object> origMap) {
        return map(origMap).withoutValues(v -> v == null);
    }

}
