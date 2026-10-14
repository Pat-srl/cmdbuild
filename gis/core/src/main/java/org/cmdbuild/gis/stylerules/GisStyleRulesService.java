/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.stylerules;

import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.annotation.Nullable;

public interface GisStyleRulesService {

    List<GisStyleRuleset> getAll();

    GisStyleRuleset create(GisStyleRuleset rules);

    GisStyleRuleset update(GisStyleRuleset rules);

    List<GisStyleRuleset> getForClass(String classId);

    GisStyleRuleset getById(long rulesetId);

    void delete(long rulesetId);

    Map<Long, Map<String, Object>> applyRulesOnCards(GisStyleRuleset ruleset, @Nullable Set<Long> cardIds);

    default Map<Long, Map<String, Object>> applyRulesOnCards(long rulesetId, @Nullable Set<Long> cardIds) {
        return applyRulesOnCards(getById(rulesetId), cardIds);
    }

    default Map<Long, Map<String, Object>> applyRulesOnCards(long rulesetId) {
        return applyRulesOnCards(rulesetId, null);
    }

}
