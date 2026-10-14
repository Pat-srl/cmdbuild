package org.cmdbuild.utils.ipaddress;

import static org.cmdbuild.utils.ipaddress.CmIpAddressUtils.isIpAddress;
import static org.cmdbuild.utils.ipaddress.CmIpAddressUtils.toIpAddress;
import static org.junit.Assert.*;
import org.junit.Test;

public class CmIpAddressUtilsTest {

    @Test
    public void testIsIpv4Address() {
        assertTrue(isIpAddress("192.168.1.1"));
        assertTrue(isIpAddress("192.168.1.2/31"));
        assertTrue(isIpAddress("192.168.1.1/32"));
        assertTrue(isIpAddress("127.0.0.1/5"));

        assertFalse(isIpAddress(null));
        assertFalse(isIpAddress(""));
        assertFalse(isIpAddress("192.168.1.1.2"));
        assertFalse(isIpAddress("192.168.1.1/33"));
        assertFalse(isIpAddress("192.168.1.1/323"));
        assertFalse(isIpAddress("127.0.0.1/-2"));
    }

    @Test
    public void testIsIpv6Address() {
        assertTrue(isIpAddress("2001:db8::1"));
        assertTrue(isIpAddress("2001:db8::1/64"));
        assertTrue(isIpAddress("::1/128"));
        assertTrue(isIpAddress("fe80::1/10"));

        assertFalse(isIpAddress(null));
        assertFalse(isIpAddress(""));
        assertFalse(isIpAddress("2001:db8::1::2"));
        assertFalse(isIpAddress("2001:db8::1/129"));
        assertFalse(isIpAddress("2001:db8::1/999"));
        assertFalse(isIpAddress("::1/-2"));
    }

    @Test
    public void testToIpv4Address() {
        assertEquals("192.168.1.1", toIpAddress("192.168.1.1"));
        assertEquals("192.168.1.1", toIpAddress("192.168.1.1/32"));
        assertEquals("192.168.1.1/31", toIpAddress("192.168.1.1/31"));
        assertEquals("127.0.0.1/1", toIpAddress("127.0.0.1/1"));
        assertEquals("127.0.0.1/0", toIpAddress("127.0.0.1/0"));

        assertNotEquals(null, toIpAddress("192.168.1.1"));
        assertNotEquals(null, toIpAddress("192.168.1.1/32"));
        assertNotEquals("192.168.1.1/32", toIpAddress("192.168.1.1/32"));
        assertNotEquals(null, toIpAddress("192.168.1.1/1"));
        assertNotEquals("192.168.1.1", toIpAddress("192.168.1.1/1"));
    }

    @Test
    public void testToIpv6Address() {
        assertEquals("2001:db8::1", toIpAddress("2001:db8::1"));
        assertEquals("2001:db8::1", toIpAddress("2001:db8::1/128"));
        assertEquals("2001:db8::1/127", toIpAddress("2001:db8::1/127"));
        assertEquals("2001:db8::1/32", toIpAddress("2001:db8::1/32"));
        assertEquals("::1/1", toIpAddress("::1/1"));
        assertEquals("::1/0", toIpAddress("::1/0"));

        assertNotEquals(null, toIpAddress("2001:db8::1"));
        assertNotEquals(null, toIpAddress("2001:db8::1/128"));
        assertNotEquals("2001:db8::1/128", toIpAddress("2001:db8::1/128"));
        assertNotEquals(null, toIpAddress("2001:db8::1/1"));
        assertNotEquals("2001:db8::1", toIpAddress("2001:db8::1/1"));
    }
}
