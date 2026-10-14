/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils;

import static org.cmdbuild.utils.io.CmMultipartUtils.isPlaintext;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class RequestDataUtilsTest {

    @Test
    public void testIsPlaintext() {
        assertTrue(isPlaintext("text/plain"));
        assertTrue(isPlaintext("text/xml"));
        assertTrue(isPlaintext("application/json"));
        assertTrue(isPlaintext("application/x-www-form-urlencoded"));
        assertTrue(isPlaintext("application/xop+xml; charset=UTF-8; type=\"text/xml\""));

        assertFalse(isPlaintext("application/pdf"));
        assertFalse(isPlaintext("application/octet-stream"));
    }

}
