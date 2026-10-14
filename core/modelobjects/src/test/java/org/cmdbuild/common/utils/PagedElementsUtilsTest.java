/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.utils;

import static org.cmdbuild.common.utils.PagedElements.isPaged;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class PagedElementsUtilsTest {

    @Test
    public void testIsPaged() {
        assertFalse(isPaged(null, null));
        assertFalse(isPaged(0, null));
        assertFalse(isPaged(null, 0));
        assertFalse(isPaged(0, 0));

        assertTrue(isPaged(1, 0));
        assertTrue(isPaged(1, null));
        assertTrue(isPaged(null, 1));
        assertTrue(isPaged(0, 1));
    }

}
