/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a <i>diff</i>, a changed <i>dms model</i> (a special type of
 * {@link Classe}), on modified
 * <i>schema</i>.
 *
 * @author afelice
 */
public class GeneratedDiffSchema_ChangedDmsModel extends GeneratedDiffSchema_ChangedItem {

    @JsonProperty("dmsModel")
    @Override
    public GeneratedDiffSchema_ChangedItemProps getItemProps() {
        return super.getItemProps();
    }

    @JsonProperty("dmsModel")
    @Override
    public void setItemProps(GeneratedDiffSchema_ChangedItemProps itemProps) {
        super.setItemProps(itemProps);
    }

}
