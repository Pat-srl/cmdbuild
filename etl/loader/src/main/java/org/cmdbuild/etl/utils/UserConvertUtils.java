/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.utils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;

public class UserConvertUtils {

    @Nullable
    public static String timestampToDateStrWithUserFormat(@Nullable ZonedDateTime dateTime, TimeZone userTimeZone, DateTimeFormatter dateTimeFormat) {
        return dateTime == null ? null : dateTimeFormat.format(dateTime.withZoneSameInstant(userTimeZone.toZoneId()).toLocalDateTime());
    }

    @Nullable
    public static ZonedDateTime parseDateTime(@Nullable Object value, TimeZone userTimeZone, DateTimeFormatter dateTimeFormat) {
        if (isNullOrBlank(value)) {
            return null;
        } else if (value instanceof Date) {
            return toDateTime(value);
        } else {
            return LocalDateTime.parse(toStringNotBlank(value), dateTimeFormat).atZone(userTimeZone.toZoneId());
        }
    }

}
