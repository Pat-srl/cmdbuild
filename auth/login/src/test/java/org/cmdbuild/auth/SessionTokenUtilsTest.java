/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth;

import org.cmdbuild.auth.login.LoginData;
import static org.cmdbuild.auth.utils.SessionTokenUtils.basicAuthTokenToLoginData;
import static org.cmdbuild.auth.utils.SessionTokenUtils.buildBasicAuthToken;
import static org.cmdbuild.auth.utils.SessionTokenUtils.isBasicAuthToken;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class SessionTokenUtilsTest {

    @Test
    public void testBasicSessionToken() {
        String token = buildBasicAuthToken("user", "psw");
        assertEquals("basic75736572g707377", token);
        assertTrue(isBasicAuthToken(token));
        assertFalse(isBasicAuthToken("asd"));
        LoginData login = basicAuthTokenToLoginData(token);
        assertEquals("user", login.getLoginString());
        assertEquals("psw", login.getPassword());
        assertTrue(login.isPasswordRequired());
    }

}
