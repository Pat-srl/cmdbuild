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

/**
 * Represents a <i>diff</i>, a changed {@link Classe}, on modified
 * <i>schema</i>.
 *
 * @author afelice
 */
public class GeneratedDiffSchema_ChangedJSComponents extends GeneratedDiffSchema_ChangedItem {

    public GeneratedDiffSchema_ChangedJSComponents() {
        setItemProps(new GeneratedDiffSchema_ChangedItemProps());
    }
    
    @JsonIgnore
    @Override
    public GeneratedDiffSchema_ChangedItemProps getItemProps() {
        return super.getItemProps();
    }

    @JsonIgnore
    @Override
    public void setItemProps(GeneratedDiffSchema_ChangedItemProps itemProps) {
        super.setItemProps(itemProps);
    }

    public boolean hasChangedProps() {
        return getItemProps().hasChangedProps();
    }

    /**
     * Override in your class to use your tag.
     *
     * @return
     */
    @JsonProperty("insertedComponents")
    @Override
    public List<Map<String, Object>> getInsertedAttributes() {
        return super.getInsertedAttributes();
    }

    /**
     * Override in your class to use your tag.
     *
     * @param insertedAttributes
     */
    @JsonProperty("insertedComponents")
    @Override
    public void setInsertedAttributes(List<Map<String, Object>> insertedAttributes) {
        super.setInsertedAttributes(insertedAttributes);
    }

    /**
     * Override in your class to use your tag.
     *
     * @return
     */
    @JsonProperty("removedComponents")
    @Override
    public List<Map<String, Object>> getRemovedAttributes() {
        return super.getRemovedAttributes();
    }

    /**
     * Override in your class to use your tag.
     *
     * @param removedAttributes
     */
    @JsonProperty("removedComponents")
    @Override
    public void setRemovedAttributes(List<Map<String, Object>> removedAttributes) {
        super.setRemovedAttributes(removedAttributes);
    }

    /**
     * Override in your class to use your tag.
     *
     * @return
     */
    @JsonProperty("changedComponents")
    @Override
    public List<GeneratedDiffSchema_ChangedItemAttributes> getChangedAttributes() {
        return super.getChangedAttributes();
    }

    /**
     * Override in your class to use your tag.
     *
     * @param changedAttributes
     */
    @JsonProperty("changedComponents")
    @Override
    public void setChangedAttributes(List<GeneratedDiffSchema_ChangedItemAttributes> changedAttributes) {
        super.setChangedAttributes(changedAttributes);
    }

}
