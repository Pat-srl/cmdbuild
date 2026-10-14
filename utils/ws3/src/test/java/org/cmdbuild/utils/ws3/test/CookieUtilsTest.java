/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.test;

import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import org.cmdbuild.utils.date.inner.CmTicker;
import static org.cmdbuild.utils.ws3.utils.WsCookieUtils.SameSiteMode.SS_LAX;
import static org.cmdbuild.utils.ws3.utils.WsCookieUtils.buildDeleteCookieHeader;
import static org.cmdbuild.utils.ws3.utils.WsCookieUtils.buildSetCookieHeader;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class CookieUtilsTest {

    @Before
    public void init() {
        CmTicker.getTicker().set(toDateTime("2020-02-10T11:02:40Z"));
    }

    @After
    public void cleanup() {
        CmTicker.getTicker().resume();
    }

    @Test
    public void testSetCookie() {
        assertEquals("CMDBuild-Authorization=de04v4od606uus0e1r0w3mh7; Max-Age=63072000; Expires=Wed, 09 Feb 2022 11:02:40 GMT; Path=/cmdbuild; SameSite=Lax",
                buildSetCookieHeader("CMDBuild-Authorization", "de04v4od606uus0e1r0w3mh7", 63072000, "/cmdbuild", false, false, SS_LAX));
        assertEquals("CMDBuild-Authorization=de04v4od606uus0e1r0w3mh7; Max-Age=63072000; Expires=Wed, 09 Feb 2022 11:02:40 GMT; Path=/cmdbuild; Secure; SameSite=Lax",
                buildSetCookieHeader("CMDBuild-Authorization", "de04v4od606uus0e1r0w3mh7", 63072000, "/cmdbuild", true, false, SS_LAX));
        assertEquals("CMDBuild-Authorization=de04v4od606uus0e1r0w3mh7; Max-Age=63072000; Expires=Wed, 09 Feb 2022 11:02:40 GMT; Path=/cmdbuild; HttpOnly; SameSite=Lax",
                buildSetCookieHeader("CMDBuild-Authorization", "de04v4od606uus0e1r0w3mh7", 63072000, "/cmdbuild", false, true, SS_LAX));
    }

    @Test
    public void testDeleteCookie() {
        assertEquals("CMDBuild-Authorization=0; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT; Path=/cmdbuild; SameSite=Lax", buildDeleteCookieHeader("CMDBuild-Authorization", "/cmdbuild", false, false, SS_LAX));
    }

}
