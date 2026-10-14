/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.cmdbuild.utils.html.HtmlSanitizerUtils.sanitizeHtmlForEmail;

public class HtmlSanitizerUtilsTest {

    @Test
    public void testHtmlInEmail() {
        assertEquals("something <img src=\"http://lalalal.jpg\" /> else", sanitizeHtmlForEmail("something <img src=\"http://lalalal.jpg\" /> else"));
        assertEquals("something <img src=\"data:image/png;base64,iVBORw0KGgoAAAANS\" /> else", sanitizeHtmlForEmail("something <img src=\"data:image/png;base64,iVBORw0KGgoAAAANS\" /> else"));
    }

}
