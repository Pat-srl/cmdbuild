/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.test;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.input.BOMInputStream;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

public class CharsetProcessingTest {

    @Test
    public void testCharsetProcessing() throws IOException {
        try (CsvListReader reader = new CsvListReader(new InputStreamReader(new BOMInputStream(getClass().getResourceAsStream("/org/cmdbuild/etl/test/file_utf8_bom.csv")), StandardCharsets.US_ASCII), CsvPreference.EXCEL_PREFERENCE)) {
            assertEquals("Host", reader.read().get(0));
        }
        try (CsvListReader reader = new CsvListReader(new InputStreamReader(new BOMInputStream(getClass().getResourceAsStream("/org/cmdbuild/etl/test/file_utf8_bom.csv")), StandardCharsets.ISO_8859_1), CsvPreference.EXCEL_PREFERENCE)) {
            assertEquals("Host", reader.read().get(0));
        }
        try (CsvListReader reader = new CsvListReader(new InputStreamReader(new BOMInputStream(getClass().getResourceAsStream("/org/cmdbuild/etl/test/file_utf8_bom.csv")), StandardCharsets.UTF_8), CsvPreference.EXCEL_PREFERENCE)) {
            assertEquals("Host", reader.read().get(0));
        }
        try (CsvListReader reader = new CsvListReader(new InputStreamReader(new BOMInputStream(getClass().getResourceAsStream("/org/cmdbuild/etl/test/file_utf8_bom.csv"))), CsvPreference.EXCEL_PREFERENCE)) {
            assertEquals("Host", reader.read().get(0));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCharsetProcessing1() throws IOException {
        try (CsvListReader reader = new CsvListReader(new InputStreamReader(getClass().getResourceAsStream("/org/cmdbuild/etl/test/file_utf8_bom.csv"), StandardCharsets.UTF_8), CsvPreference.EXCEL_PREFERENCE)) {
            checkArgument(equal("Host", reader.read().get(0)));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCharsetProcessing2() throws IOException {
        try (CsvListReader reader = new CsvListReader(new InputStreamReader(getClass().getResourceAsStream("/org/cmdbuild/etl/test/file_utf8_bom.csv"), StandardCharsets.ISO_8859_1), CsvPreference.EXCEL_PREFERENCE)) {
            checkArgument(equal("Host", reader.read().get(0)));
        }
    }

}
