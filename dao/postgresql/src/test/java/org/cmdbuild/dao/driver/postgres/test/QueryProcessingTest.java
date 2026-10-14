/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.postgres.test;

import static org.cmdbuild.dao.postgres.utils.QueryBuilderUtils.smartExprReplace;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class QueryProcessingTest {

    @Test
    public void testRegexReplace() {
        assertEquals("a b c", "a \"e\" c".replaceFirst("\"e\"", "b"));

        assertEquals("a b c", "a \"e\" c".replaceFirst("(?!')\"e\"(?!')", "b"));
        assertEquals("b c", "\"e\" c".replaceFirst("(?!')\"e\"(?!')", "b"));
        assertEquals("a b", "a \"e\"".replaceFirst("(?!')\"e\"(?!')", "b"));
        assertEquals("a '\"e\"' c", "a '\"e\"' c".replaceFirst("(?!')\"e\"(?!')", "b"));

        assertEquals("(( _assetmgtci.\"IdClass1\" = '\"AssetMgt\"'::regclass AND _assetmgtci.\"IdObj1\" = 737902 ) OR ( _assetmgtci.\"IdClass2\" = '\"AssetMgt\"'::regclass AND _assetmgtci.\"IdObj2\" = 737902 ))",
                smartExprReplace("(( \"IdClass1\" = '\"AssetMgt\"'::regclass AND \"IdObj1\" = 737902 ) OR ( \"IdClass2\" = '\"AssetMgt\"'::regclass AND \"IdObj2\" = 737902 ))", "_assetmgtci"));

        assertEquals("'\"MyClass\"'::regclass = _assetmgtci.\"IdClass\"", smartExprReplace("'\"MyClass\"'::regclass = \"IdClass\"", "_assetmgtci"));
        assertEquals("_assetmgtci.\"IdClass\" = '\"MyClass\"'::regclass", smartExprReplace("\"IdClass\" = '\"MyClass\"'::regclass", "_assetmgtci"));
    }

}
