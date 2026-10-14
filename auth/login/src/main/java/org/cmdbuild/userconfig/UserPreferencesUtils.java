/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.userconfig;

import static com.google.common.base.Preconditions.checkArgument;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class UserPreferencesUtils {

    public static DecimalFormat buildDecimalFormat(String decimalSeparator, @Nullable String thousandsSeparator) {
        DecimalFormat formatter = new DecimalFormat();
        DecimalFormatSymbols decimalFormatSymbols = formatter.getDecimalFormatSymbols();
        if (isNotEmpty(thousandsSeparator)) {
            checkArgument(thousandsSeparator.length() == 1, "invalid thousands separator = %s", thousandsSeparator);
            decimalFormatSymbols.setGroupingSeparator(thousandsSeparator.charAt(0));
            formatter.setGroupingSize(3);
            formatter.setGroupingUsed(true);
        } else {
            formatter.setGroupingUsed(false);
        }
        checkArgument(decimalSeparator.length() == 1, "invalid decimal separator = %s", decimalSeparator);
        decimalFormatSymbols.setDecimalSeparator(decimalSeparator.charAt(0));
        formatter.setMaximumFractionDigits(Integer.MAX_VALUE);
        formatter.setDecimalFormatSymbols(decimalFormatSymbols);
        return formatter;
    }

}
