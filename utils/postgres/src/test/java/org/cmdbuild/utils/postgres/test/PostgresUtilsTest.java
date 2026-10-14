/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.postgres.test;

import java.io.IOException;
import java.net.URI;
import static org.cmdbuild.utils.postgres.PgVersionUtils.getPostgresServerVersionFromNumber;
import org.cmdbuild.utils.postgres.PostgresUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;
import static org.cmdbuild.utils.postgres.PgVersionUtils.getPostgresInfo;

public class PostgresUtilsTest {

    @Test
    public void testUriParsing() {
        URI uri = URI.create("x://10.0.0.173:5432/cmdbuild_30");
        assertEquals(5432, uri.getPort());
        assertEquals("10.0.0.173", uri.getHost());
        assertEquals("/cmdbuild_30", uri.getPath());
    }

    @Test
    @Ignore
    public void testPgHelper() {
        assertEquals("2", PostgresUtils.newHelper("localhost", 5432, "postgres", "postgres").buildHelper().executeQuery("SELECT 1 + 1"));
    }

    @Test
    public void testVersionNumberParsing() {
        assertEquals("9.5.23", getPostgresServerVersionFromNumber(90523));
        assertEquals("10.0", getPostgresServerVersionFromNumber(100000));
        assertEquals("10.18", getPostgresServerVersionFromNumber(100018));
        assertEquals("12.7", getPostgresServerVersionFromNumber(120007));
    }

    @Test
    public void testPostgresLatestVersion() throws IOException {
        assertEquals("12.22", getPostgresInfo("12").getCurrentMinor().getCanonical());
        assertFalse(getPostgresInfo("12").isSupported());
        assertTrue(getPostgresInfo("17").isSupported());
    }
}
