/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import org.cmdbuild.modeldiff.data.ModelConfiguration;

/**
 * @author ldare
 */
public class WsModelConfiguration extends ModelConfiguration {
    public String masterClass;

    public WsModelConfiguration(ModelConfiguration modelConfiguration) {
        super(modelConfiguration.id, modelConfiguration.name);
        classes = modelConfiguration.classes;
        processes = modelConfiguration.processes;
        views = modelConfiguration.views;
        lookups = modelConfiguration.lookups;
        dmsModels = modelConfiguration.dmsModels;
        dmsCategoryLookups = modelConfiguration.dmsCategoryLookups;
    }

    public void setMasterClass(String masterClass) {
        this.masterClass = masterClass;
    }
}
