/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a <i>diff</i>, a changed {@link Domain}, on modified
 * <i>schema</i>.
 *
 * @author afelice
 */
public class GeneratedDiffSchema_ChangedDomain extends GeneratedDiffSchema_ChangedItem {

    @JsonProperty("domain")
    @Override
    public GeneratedDiffSchema_ChangedItemProps getItemProps() {
        return super.getItemProps();
    }

    @JsonProperty("domain")
    @Override
    public void setItemProps(GeneratedDiffSchema_ChangedItemProps itemProps) {
        super.setItemProps(itemProps);
    }

    public boolean hasChangedProps() {
        return getItemProps().hasChangedProps();
    }

}
