/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;

/**
 * Represents a <i>diff</i>, a changed {@link Classe}, on modified
 * <i>schema</i>.
 *
 * @author afelice
 */
public class GeneratedDiffSchema_ChangedItemProps {

    public String name;
    public String description;

    @JsonProperty("added")
    public Map<String, Object> addedProps = map();

    @JsonProperty("removed")
    public Map<String, Object> removedProps = map();

    @JsonProperty("changed")
    public List<GeneratedDiffSchema_ChangedAttributeValue> changedProps = list();

    public boolean hasChangedProps() {
        return !addedProps.isEmpty()
                || !removedProps.isEmpty()
                || !changedProps.isEmpty();
    }

    @JsonIgnore
    public Map<String, Object> getNewAttributeValues() {
        // Leads to NullPointerException for null newValue
//        return attribs.stream()
//                .collect(Collectors.toMap(
//                        GeneratedDiffData_CardAttribute::getAttribName,
//                        GeneratedDiffData_CardAttribute::getNewValue));
        Map<String, Object> result = map();
        changedProps.forEach(a -> {
            result.put(a.attribName, a.newValue);
        });

        return result;
    }

    /**
     * Returns changed plus added attributes.
     *
     * @return
     */
    @JsonIgnore
    public Map<String, Object> getAllNewAttributeValues() {
        FluentMap<String, Object> result = map();
        return result.with(getNewAttributeValues()).with(addedProps);
    }

    @JsonIgnore
    public Map<String, Object> getOldAttributeValues() {
        Map<String, Object> result = map();
        changedProps.forEach(a -> {
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
        return result.with(getOldAttributeValues()).with(removedProps);
    }

    @JsonIgnore
    public Map<String, Object> getCumulativeAttributeValues() {
        return mapOf(String.class, Object.class)
                .with(notNull(getOldAttributeValues()))
                .with(notNull(getNewAttributeValues()))
                .with(notNull(addedProps));
    }

    public String getName() {
        return name;
    }

    private FluentMap<String, Object> notNull(Map<String, Object> origMap) {
        return map(origMap).withoutValues(v -> v == null);
    }
}
