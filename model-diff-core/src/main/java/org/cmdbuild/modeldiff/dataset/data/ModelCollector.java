/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dataset.data;

import org.cmdbuild.modeldiff.data.ModelConfiguration;

/**
 * From a dataset (definition), build a configuration (of real data to handle)
 * 
 * @author afelice
 */
public interface ModelCollector {
    
    ModelConfiguration collectModel(DataDataset dataset, boolean detailed);
}
