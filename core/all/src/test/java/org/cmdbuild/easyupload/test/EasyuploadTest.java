/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easyupload.test;

import static org.cmdbuild.easyupload.EasyuploadUtils.normalizePath;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class EasyuploadTest {

    @Test
    public void testPathNormalization() {
        assertEquals("/", normalizePath("/"));
        assertEquals("/", normalizePath("./"));
        
        assertEquals("asd", normalizePath("asd"));
        assertEquals("asd", normalizePath("/asd"));
        assertEquals("asd", normalizePath("./asd"));
        assertEquals("asd", normalizePath("asd/"));
        assertEquals("asd", normalizePath("asd/."));
        
        assertEquals("asd", normalizePath("asd/dsa/.."));
        assertEquals("asd", normalizePath("/asd/qwe/vsfd/../.."));
        assertEquals("asd", normalizePath("./asda/../asdqewd/ace/../../asd/")); 
    }

}
