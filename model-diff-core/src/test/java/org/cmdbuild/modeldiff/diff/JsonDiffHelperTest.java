/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author afelice
 */
public class JsonDiffHelperTest {

    public JsonDiffHelperTest() {
    }

    /**
     * Test of handleIdDiff method, of class CmCardAttributesDeltaBuilder.
     */
    @Test
    public void testNormalizeDiff_Schema() {
        System.out.println("normalizeDiff_Schema");

        //arrange:
        Map<String, Object> deserializedValue = new HashMap();
        deserializedValue.put("attribute", "canDo");
        deserializedValue.put("values", asList(101, 102, 103));

        Map<String, Object> leftSerialization = map(
                "lookupValues", asList(deserializedValue)
        );

        Map<String, Object> rightSerialization_1 = map(
                "lookupValues", list(
                        map(
                                "attribute", "canDo",
                                "values", list(101L, 102L, 103L)
                        )
                )
        );
        MapDifference<String, Object> differingAttribValues_1 = Maps.difference(leftSerialization, rightSerialization_1);

        Map<String, Object> rightSerialization_2 = map(
                "lookupValues", list(
                        map(
                                "attribute", "canDo",
                                "values", list(101, 10555, 103)
                        )
                )
        );
        MapDifference<String, Object> differingAttribValues_2 = Maps.difference(leftSerialization, rightSerialization_2);

        //act:
        Map<String, MapDifference.ValueDifference<Object>> result_1 = JsonDiffHelper.normalizeDiff_Schema(differingAttribValues_1.entriesDiffering());
        Map<String, MapDifference.ValueDifference<Object>> result_2 = JsonDiffHelper.normalizeDiff_Schema(differingAttribValues_2.entriesDiffering());

        //assert:
        assertTrue(result_1.isEmpty());
        assertTrue(result_2.isEmpty());
    }
}
