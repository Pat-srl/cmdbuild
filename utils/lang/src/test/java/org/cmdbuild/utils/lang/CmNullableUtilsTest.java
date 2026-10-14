/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import static org.cmdbuild.utils.lang.CmNullableUtils.nullIf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

public class CmNullableUtilsTest {

    @Test
    public void testNullIf() {
        assertNull(nullIf("asd", "asd"));
        assertNull(nullIf(null, "asd"));
        assertEquals("asda", nullIf("asda", "asd"));

        Object one = new Object(), two = new Object();

        assertNull(nullIf(one, one));
        assertNull(nullIf(null, one));
        assertEquals(one, nullIf(one, two));
    }

}
