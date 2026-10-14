/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dataset.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cmdbuild.view.View;

/**
 * Represents a dataset, for data only, for a {@link View}.
 *
 * <p>
 * Always in <i>read only</i> mode.
 *
 * @author afelice
 */
public class ViewDataset extends ClasseDataset {

    public ViewDataset() {
        mode = "read";
    }

    @Override
    @JsonIgnore
    public String getFilter() {
        return "";
    }

    @Override
    public void setMode(String mode) {
        // Do nothing, is always in read-only mode.
    }

}
