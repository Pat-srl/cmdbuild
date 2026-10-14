/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io.test;

import static org.cmdbuild.utils.xml.CmXmlUtils.prettifyXml;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class XmlUtilsTest {

    @Test
    public void testPrettyXml() {
        assertEquals("<a>\n    <b>hello</b>\n</a>\n", prettifyXml("<a><b>hello</b></a>"));
        assertEquals("<a>\n    <b>hello</b>\n</a>\n", prettifyXml("<a>   <b>hello</b>  </a>"));
    }

}
