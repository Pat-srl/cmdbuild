/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.test;

import org.apache.tika.Tika;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TikaTest {

    private final Tika tika = new Tika();

    @Test
    public void testTika1() {
        assertEquals("text/plain", tika.detect("something".getBytes()));
    }

    @Test
    public void testTika2() {
        assertEquals("text/html", tika.detect("<html> some html </html>".getBytes()));
    }

    @Test
    public void testTika3() {
        assertEquals("application/octet-stream", tika.detect("".getBytes()));
    }
}
