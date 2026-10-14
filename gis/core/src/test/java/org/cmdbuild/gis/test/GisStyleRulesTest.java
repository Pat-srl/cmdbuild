/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.test;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.beans.AttributeFilterConditionImpl;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import static org.cmdbuild.gis.stylerules.GisStyleRulesUtils.parseRules;
import static org.cmdbuild.gis.stylerules.GisStyleRulesUtils.serializeRules;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GisStyleRulesTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testGisStyleRulesSerialization() {
        String value = serializeRules((List) list(
                Pair.of(AttributeFilterConditionImpl.eq("Code", "one").toAttributeFilter().toCmdbFilters(), map("color", "red")),
                Pair.of(AttributeFilterConditionImpl.eq("Code", "two").toAttributeFilter().toCmdbFilters(), map("color", "blue")),
                Pair.of(CmdbFilterImpl.noopFilter(), map("color", "green"))
        ));

        logger.info("res = {}", value);

        List<Pair<CmdbFilter, Map<String, Object>>> rules = parseRules(value);

        assertEquals(3, rules.size());
        assertEquals("one", rules.get(0).getLeft().getAttributeFilter().getCondition().getSingleValue());
        assertEquals("blue", rules.get(1).getRight().get("color"));

        String value2 = serializeRules(rules);

        assertEquals(value, value2);

    }

}
