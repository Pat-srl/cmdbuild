/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.auth;

import static org.cmdbuild.auth.utils.LdapUtils.encodeFilter;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author ataboga
 */
public class LdapTest {

    @Test
    public void ldapEncodeFilterTest() {
        String filter = encodeFilter("la12r3s*");
        assertEquals(filter, "la12r3s\\2a");
    }
}
