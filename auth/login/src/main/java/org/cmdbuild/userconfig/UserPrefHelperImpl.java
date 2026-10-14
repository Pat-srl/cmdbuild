/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.userconfig;

import static com.google.common.base.Preconditions.checkNotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.cmdbuild.utils.lang.CmNullableUtils;
import org.cmdbuild.utils.lang.CmStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserPrefHelperImpl implements UserPrefHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DateAndFormatPreferences userPreferences;

    public UserPrefHelperImpl(DateAndFormatPreferences userPreferences) {
        this.userPreferences = checkNotNull(userPreferences);
    }

    @Nullable
    @Override
    public String serializeDateTime(@Nullable ZonedDateTime dateTime) {
        return dateTime == null ? null : userPreferences.getDateTimeFormat().format(dateTime.withZoneSameInstant(userPreferences.getZoneId()).toLocalDateTime());
    }

    @Nullable
    @Override
    public String serializeDate(@Nullable LocalDate localDate) {
        return localDate == null ? null : userPreferences.getDateFormat().format(localDate);
    }

    @Nullable
    @Override
    public String serializeTime(@Nullable LocalTime localTime) {
        return localTime == null ? null : userPreferences.getTimeFormat().format(localTime);
    }

    @Nullable
    @Override
    public ZonedDateTime parseDateTime(@Nullable Object value) {
        if (CmNullableUtils.isNullOrBlank(value)) {
            return null;
        } else if (value instanceof String) {
            try {
                return LocalDateTime.parse(CmStringUtils.toStringNotBlank(value), userPreferences.getDateTimeFormat()).atZone(userPreferences.getZoneId());
            } catch (Exception ex) {
                throw runtime(ex, "error parsing dateTime value =< %s > with dateTime format =< %s >", value, userPreferences.getDateTimeFormatPattern());
            }
        } else {
            return CmDateUtils.toDateTime(value);
        }
    }

    @Nullable
    @Override
    public LocalDate parseDate(@Nullable Object value) {
        if (CmNullableUtils.isNullOrBlank(value)) {
            return null;
        } else if (value instanceof String) {
            try {
                return LocalDate.parse(CmStringUtils.toStringNotBlank(value), userPreferences.getDateFormat());
            } catch (Exception ex) {
                throw runtime(ex, "error parsing date value =< %s > with date format =< %s >", value, userPreferences.getDateFormatPattern());
            }
        } else {
            return CmDateUtils.toDateAtTimeZone(value, userPreferences.getTimezone());
        }
    }

    @Nullable
    @Override
    public LocalTime parseTime(@Nullable Object value) {
        if (CmNullableUtils.isNullOrBlank(value)) {
            return null;
        } else if (value instanceof String) {
            try {
                return LocalTime.parse(CmStringUtils.toStringNotBlank(value), userPreferences.getTimeFormat());
            } catch (Exception ex) {
                throw runtime(ex, "error parsing time value =< %s > with time format =< %s >", value, userPreferences.getTimeFormatPattern());
            }
        } else {
            return CmDateUtils.toTime(value);
        }
    }

    @Nullable
    @Override
    public String serializeNumber(@Nullable Number number) {
        return number == null ? null : userPreferences.getDecimalFormat().format(number);
    }

    @Nullable
    @Override
    public Number parseNumber(@Nullable String number) {
        try {
            if (StringUtils.isBlank(number)) {
                return null;
            } else {
                return userPreferences.getDecimalFormat().parse(number);
            }
        } catch (Exception ex) {
            throw runtime(ex, "error parsing number =< %s > with number format =< %s >", number, userPreferences.getDecimalFormatInfo());
        }
    }

}
