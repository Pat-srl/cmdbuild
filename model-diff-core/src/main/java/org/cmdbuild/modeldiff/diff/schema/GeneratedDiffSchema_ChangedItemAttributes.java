/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_DESCRIPTION_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_NAME_SERIALIZATION;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * Represents all changed {@link Attribute}s, on modified <i>schema item</i>.
 *
 * @author afelice
 */
public class GeneratedDiffSchema_ChangedItemAttributes {

    @JsonProperty(ATTR_NAME_SERIALIZATION)
    public String name;

    @JsonProperty(ATTR_DESCRIPTION_SERIALIZATION)
    public String description;

    @JsonProperty("added")
    public Map<String, Object> addedAttribs = map();

    @JsonProperty("removed")
    public Map<String, Object> removedAttribs = map();

    @JsonProperty("changed")
    public List<GeneratedDiffSchema_ChangedAttributeValue> changedAttribs = list();

    @JsonIgnore
    public Map<String, Object> getNewAttributeValues() {
        // Leads to NullPointerException for null newValue
//        return attribs.stream()
//                .collect(Collectors.toMap(
//                        GeneratedDiffData_CardAttribute::getAttribName,
//                        GeneratedDiffData_CardAttribute::getNewValue));
        Map<String, Object> result = map();
        changedAttribs.forEach(a -> {
            result.put(a.attribName, a.newValue);
        });

        return result;
    }

    /**
     * Returns changed plus added (synthesized) attributes.
     *
     * @return
     */
    @JsonIgnore
    public Map<String, Object> getAllNewAttributeValues() {
        FluentMap<String, Object> result = map();
        return result.with(getNewAttributeValues()).with(addedAttribs);
    }

    @JsonIgnore
    public Map<String, Object> getOldAttributeValues() {
        Map<String, Object> result = map();
        changedAttribs.forEach(a -> {
            result.put(a.attribName, a.oldValue);
        });

        return result;
    }

    /**
     * Returns changed plus removed (synthesized) attributes.
     *
     * @return
     */
    @JsonIgnore
    public Map<String, Object> getAllOldAttributeValues() {
        FluentMap<String, Object> result = map();
        return result.with(getOldAttributeValues()).with(removedAttribs);
    }

    @JsonIgnore
    public Map<String, Object> getCumulativeAttributeValues() {
        Map<String, Object> result = notNull(removedAttribs)
                .with(notNull(getNewAttributeValues()))
                .with(notNull(getOldAttributeValues()))
                .with(notNull(addedAttribs));

        result.put(ATTR_NAME_SERIALIZATION, name);

        return result;
    }

    /**
     * Applies cumulative attribute changes (inserted, changed) to the given
     * <code>origAttribValues</code>.
     *
     * @param origAttribValues
     * @return
     */
    public Map<String, Object> calculateAttribChanges(Map<String, Object> origAttribValues) {
        return map(origAttribValues).with(getAllNewAttributeValues());
    }

    private FluentMap<String, Object> notNull(Map<String, Object> origMap) {
        return map(origMap).withoutValues(v -> v == null);
    }

}
