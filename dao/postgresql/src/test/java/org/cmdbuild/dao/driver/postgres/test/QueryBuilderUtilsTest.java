/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.postgres.test;

import static java.util.Collections.emptyList;
import static org.cmdbuild.dao.postgres.utils.QueryBuilderUtils.getFulltextQueryParts;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class QueryBuilderUtilsTest {

    @Test
    public void testFulltextParts1() {
        assertEquals(emptyList(), getFulltextQueryParts(null));
        assertEquals(emptyList(), getFulltextQueryParts(""));
        assertEquals(emptyList(), getFulltextQueryParts("        "));
    }

    @Test
    public void testFulltextParts2() {
        assertEquals(list("ciao", "come", "va"), getFulltextQueryParts("ciao come va"));
        assertEquals(list("ciao", "come", "va"), getFulltextQueryParts("  ciao   come va"));
        assertEquals(list("ciao", "come", "va"), getFulltextQueryParts("ciao      come va  "));
        assertEquals(list("ciao", "come", "va"), getFulltextQueryParts("  ciao      come va   "));
    }

    @Test
    public void testFulltextParts3() {
        assertEquals(list("spe cial", "ciao", "come", "va"), getFulltextQueryParts("ciao \"spe cial\" come va"));
        assertEquals(list("spe cial", "ciao", "come", "va"), getFulltextQueryParts("ciao\"spe cial\" come va"));
        assertEquals(list("spe cial", "ciao", "come", "va"), getFulltextQueryParts("ciao \"spe cial\" come va\""));
        assertEquals(list("spe cial", "ciao", "come", "va"), getFulltextQueryParts("ciao \"spe cial\"come va"));
        assertEquals(list("spe cial", "ciao", "come", "va"), getFulltextQueryParts("  \"spe cial\"ciao come va"));
    }

    @Test
    public void testFulltextParts4() {
        assertEquals(list("\"\""), getFulltextQueryParts("\"\""));
        assertEquals(list("\"\""), getFulltextQueryParts("     \"\"\""));
        assertEquals(list("\"\"", "ciao", "mondo"), getFulltextQueryParts("     \"\" ciao mondo"));
    }

}
