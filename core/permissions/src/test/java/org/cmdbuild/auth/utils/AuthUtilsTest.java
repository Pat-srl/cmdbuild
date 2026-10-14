/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.utils;

import static org.cmdbuild.auth.utils.AuthUtils.getUsernameFromHistoryUser;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class AuthUtilsTest {

    @Test
    public void testGetUsernameFromHistoryUser() {
        assertEquals(null, getUsernameFromHistoryUser(null));
        assertEquals("", getUsernameFromHistoryUser(""));
        assertEquals(" ", getUsernameFromHistoryUser(" "));
        assertEquals("MyUser", getUsernameFromHistoryUser("MyUser"));
        assertEquals("MyUser", getUsernameFromHistoryUser("system / MyUser"));
    }

}
