/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.stylerules;

import jakarta.annotation.Nullable;
import org.cmdbuild.utils.json.JsonBean;

@JsonBean(GisStyleRulesetParamsImpl.class)
public interface GisStyleRulesetParams {

    @Nullable
    GisStyleRulesetAnalysisType getAnalysisType();

    @Nullable
    Integer getSegments();

    @Nullable
    String getClassAttribute();

    @Nullable
    String getUserOwner();

    GisStyleRulesetAccessType getAccessType();
}
