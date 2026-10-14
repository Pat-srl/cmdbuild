/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email;

import java.util.List;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import static org.cmdbuild.email.EmailAddressUtils.parseEmailAddress;
import static org.cmdbuild.email.EmailAddressUtils.parseEmailAddressListAsStrings;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class EmailAddressTest {

    @Test
    public void testAddress1() throws AddressException {
        InternetAddress address = new InternetAddress("name@provider.it");
        assertEquals("name@provider.it", address.getAddress());
    }

    @Test
    public void testAddress2() throws AddressException {
        InternetAddress address = new InternetAddress("my name <name@provider.it>");
        assertEquals("name@provider.it", address.getAddress());
        assertEquals("my name", address.getPersonal());
    }

    @Test
    public void testAddress3() {
        InternetAddress address = parseEmailAddress("name@provider.it");
        assertEquals("name@provider.it", address.getAddress());
    }

    @Test
    public void testAddress4() {
        InternetAddress address = parseEmailAddress("my name <name@provider.it>");
        assertEquals("name@provider.it", address.getAddress());
        assertEquals("my name", address.getPersonal());
    }

    @Test
    public void testAddressList1() {
        List<String> list = parseEmailAddressListAsStrings("\"Porter - OIT, Michael\" <michael.porter@state.co.us>");
        assertEquals(list("\"Porter - OIT, Michael\" <michael.porter@state.co.us>"), list);
    }

    @Test
    public void testAddressList2() {
        List<String> list = parseEmailAddressListAsStrings("\"Porter - OIT, Michael\" <michael.porter@state.co.us>, my name <name@provider.it>");
        assertEquals(list("\"Porter - OIT, Michael\" <michael.porter@state.co.us>", "my name <name@provider.it>"), list);
    }

    @Test
    public void testEmptyAddressList() {
        List<String> list = parseEmailAddressListAsStrings(null);
        assertEquals(0, list.size());

        list = parseEmailAddressListAsStrings("");
        assertEquals(0, list.size());

        list = parseEmailAddressListAsStrings(" ");
        assertEquals(0, list.size());
    }

}
