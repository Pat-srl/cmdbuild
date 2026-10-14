/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dataset.data;

import java.util.concurrent.Flow;

/**
 * Represents a dataset, for data only, for a {@link Process} and filters to be
 * applied to obtain relevant {@link Flow}.
 *
 * <p>
 * Always in <i>read only</i> mode.
 *
 * @author afelice
 */
public class ProcessDataset extends ClasseDataset {

    public ProcessDataset() {
        mode = "read";
    }

    @Override
    public void setMode(String mode) {
        // Do nothing, is always in read-only mode.
    }

}
