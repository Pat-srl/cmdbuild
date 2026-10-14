/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.stylerules;

import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Map;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.gis.GisAttribute;

public interface GisStyleRuleset extends GisStyleRulesetParams {

    @Nullable
    Long getId();

    String getCode();

    @Nullable
    String getDescription();

    GisAttribute getGisAttribute();

    @Nullable
    String getFunction();

    List<Pair<CmdbFilter, Map<String, Object>>> getRules();

    GisStyleRulesetParams getParams();

    default String getOwnerClassName() {
        return getGisAttribute().getOwnerClassName();
    }

    default boolean hasFunction() {
        return isNotBlank(getFunction());
    }

    @Override
    @Nullable
    default GisStyleRulesetAnalysisType getAnalysisType() {
        return getParams().getAnalysisType();
    }

    @Override
    @Nullable
    default Integer getSegments() {
        return getParams().getSegments();
    }

    @Override
    @Nullable
    default String getClassAttribute() {
        return getParams().getClassAttribute();
    }

    @Override
    default GisStyleRulesetAccessType getAccessType() {
        return getParams().getAccessType();
    }

    @Override
    @Nullable
    default String getUserOwner() {
        return getParams().getUserOwner();
    }

}
