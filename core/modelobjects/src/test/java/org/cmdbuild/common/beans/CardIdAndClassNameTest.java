/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.beans;

import org.cmdbuild.common.beans.CardIdAndClassName;
import static org.cmdbuild.common.beans.CardIdAndClassNameUtils.parseCardIdAndClassName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;

public class CardIdAndClassNameTest {

    @Test
    public void testParseCardIdAndClassName1a() {
        CardIdAndClassName cardIdAndClassName = parseCardIdAndClassName("template:123");
        assertNotNull(cardIdAndClassName);
        assertEquals("template", cardIdAndClassName.getClassName());
        assertEquals(123l, (long) cardIdAndClassName.getId());
    }

    @Test
    public void testParseCardIdAndClassName1b() {
        CardIdAndClassName cardIdAndClassName = parseCardIdAndClassName("template[123]");
        assertNotNull(cardIdAndClassName);
        assertEquals("template", cardIdAndClassName.getClassName());
        assertEquals(123l, (long) cardIdAndClassName.getId());
    }

    @Test
    public void testParseCardIdAndClassName2() {
        CardIdAndClassName cardIdAndClassName = parseCardIdAndClassName(" ");
        assertNull(cardIdAndClassName);
    }

    @Test
    public void testParseCardIdAndClassName3() {
        CardIdAndClassName cardIdAndClassName = parseCardIdAndClassName("123", "default");
        assertNotNull(cardIdAndClassName);
        assertEquals("default", cardIdAndClassName.getClassName());
        assertEquals(123l, (long) cardIdAndClassName.getId());
    }

}
