/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report.test;

import java.text.DecimalFormatSymbols;
import java.util.Locale; 
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class LocaleTest {

    @Test
    public void testLocaleDecimalFormat() {
        assertEquals(',', DecimalFormatSymbols.getInstance(Locale.forLanguageTag("it")).getDecimalSeparator());
        assertEquals('.', DecimalFormatSymbols.getInstance(Locale.forLanguageTag("it")).getGroupingSeparator());
        assertEquals('.', DecimalFormatSymbols.getInstance(Locale.forLanguageTag("en")).getDecimalSeparator());
        assertEquals(',', DecimalFormatSymbols.getInstance(Locale.forLanguageTag("en")).getGroupingSeparator());
    }

}
