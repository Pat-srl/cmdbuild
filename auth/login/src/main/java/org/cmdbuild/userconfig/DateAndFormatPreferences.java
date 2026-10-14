/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.userconfig;

import com.google.common.base.MoreObjects;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import org.cmdbuild.config.PreferencesConfiguration.PreferredOfficeSuite;

public interface DateAndFormatPreferences {

    DateTimeFormatter getDateTimeFormat();

    DateTimeFormatter getDateFormat();

    DateTimeFormatter getTimeFormat();

    String getDateTimeFormatPattern();

    String getDateFormatPattern();

    String getTimeFormatPattern();

    TimeZone getTimezone();

    String getDecimalSeparator();

    @Nullable
    String getNumberGroupingSeparator();

    DecimalFormat getDecimalFormat();

    PreferredOfficeSuite getPreferredOfficeSuite();

    String getPreferredFileCharset();
    
    String getPreferredCsvSeparator();

    default String getDecimalFormatInfo() {
        return MoreObjects.toStringHelper(DecimalFormat.class)
                .add("decimalSeparator", getDecimalFormat().getDecimalFormatSymbols().getDecimalSeparator())
                .add("groupingSeparator", getDecimalFormat().isGroupingUsed() ? getDecimalFormat().getDecimalFormatSymbols().getGroupingSeparator() : "disabled")
                .toString();
    }

    default boolean hasNumberGroupingSeparator() {
        return isNotEmpty(getNumberGroupingSeparator());
    }

    default ZoneId getZoneId() {
        return getTimezone().toZoneId();
    }

}
