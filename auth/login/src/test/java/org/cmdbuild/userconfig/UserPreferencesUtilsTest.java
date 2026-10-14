/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.userconfig;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import static org.cmdbuild.userconfig.UserPreferencesUtils.buildDecimalFormat;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class UserPreferencesUtilsTest {

    @Test
    public void testBuildDecimalFormat() {
        DecimalFormat decimalFormat = buildDecimalFormat(".", null);

        assertEquals("0", decimalFormat.format(0));
        assertEquals("0.1", decimalFormat.format(0.1));
        assertEquals("0.01", decimalFormat.format(0.01));
        assertEquals("1234567", decimalFormat.format(1234567));
        assertEquals("1234.56", decimalFormat.format(1234.56));

        decimalFormat = buildDecimalFormat(",", null);

        assertEquals("0", decimalFormat.format(0));
        assertEquals("0,1", decimalFormat.format(0.1));
        assertEquals("0,01", decimalFormat.format(0.01));
        assertEquals("1234567", decimalFormat.format(1234567));
        assertEquals("1234,56", decimalFormat.format(1234.56));

        decimalFormat = buildDecimalFormat(",", ".");

        assertEquals("0", decimalFormat.format(0));
        assertEquals("0,1", decimalFormat.format(0.1));
        assertEquals("0,01", decimalFormat.format(0.01));
        assertEquals("1.234.567", decimalFormat.format(1234567));
        assertEquals("1.234,56", decimalFormat.format(1234.56));
    }

    @Test
    public void testLocalDateParsing() {
        assertEquals(LocalDate.parse("2020-08-11"), LocalDate.parse(CmStringUtils.toStringNotBlank("08/11/2020"), DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        assertEquals(LocalDate.parse("2020-09-24"), LocalDate.parse(CmStringUtils.toStringNotBlank("09/24/2020"), DateTimeFormatter.ofPattern("MM/dd/yyyy")));

        assertEquals(LocalDate.parse("2020-11-08"), LocalDate.parse(CmStringUtils.toStringNotBlank("08/11/2020"), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        assertEquals(LocalDate.parse("2020-09-24"), LocalDate.parse(CmStringUtils.toStringNotBlank("24/09/2020"), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    @Test
    public void testDateParsingFunction() {
        DateAndFormatPreferencesImpl preferences = DateAndFormatPreferencesImpl.builder()
                .withTimezone(TimeZone.getDefault())
                .withDateTimeFormat("MM/dd/yyyy HH:mm:ss")
                .withDateFormat("MM/dd/yyyy", null)
                .withTimeFormat("HH:mm:ss", null)
                .withDecimalSeparator(",").build();
        UserPrefHelperImpl userPrefsHelper = new UserPrefHelperImpl(preferences);

        assertEquals(LocalDate.parse("2020-08-27"), userPrefsHelper.parseDate("08/27/2020"));
        assertEquals(LocalDate.parse("2020-08-08"), userPrefsHelper.parseDate("08/08/2020"));
    }
}
