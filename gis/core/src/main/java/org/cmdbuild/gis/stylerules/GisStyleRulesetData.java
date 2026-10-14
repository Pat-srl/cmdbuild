/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.stylerules;

import jakarta.annotation.Nullable;

public interface GisStyleRulesetData {

    @Nullable
    Long getId();

    String getCode();

    String getOwner();

    @Nullable
    String getDescription();

    long getGisAttribute();

    @Nullable
    String getFunction();

    String getRules();

    GisStyleRulesetParams getParams();

}
