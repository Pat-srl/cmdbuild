/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.date;

import jakarta.annotation.Nullable;
import static java.lang.Math.round;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import static java.time.ZoneOffset.UTC;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import java.time.temporal.Temporal;
import static java.util.Arrays.asList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.utils.date.inner.CmTicker;
import org.cmdbuild.utils.date.inner.TimeRangeConfigImpl;
import org.cmdbuild.utils.date.inner.TimeRangeHelperImpl;
import static org.cmdbuild.utils.lang.CmConvertUtils.toDoubleOrZero;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntOrZero;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrZero;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.joda.time.ReadableInstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmDateUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final CmTicker TICKER = CmTicker.getTicker();

    public static TimeRangeHelper timerange(TimeRangeConfig config) {
        return new TimeRangeHelperImpl(config);
    }

    public static TimeRangeHelper timerange(String config) {
        return timerange(TimeRangeConfigImpl.parse(config));
    }

    public static TimeZone systemTimeZone() {
        return TimeZone.getDefault();
    }

    public static ZoneId systemZoneId() {
        return ZoneId.systemDefault();
    }

    public static ZonedDateTime now() {
        return systemDate();
    }

    public static ZonedDateTime systemDate() {
        return TICKER.now().withZoneSameInstant(UTC);
    }

    public static String dateTimeFileSuffix() {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
    }

    public static ZonedDateTime min(ZonedDateTime one, ZonedDateTime two) {
        return one.isBefore(two) ? one : two;
    }

    public static ZonedDateTime max(ZonedDateTime one, ZonedDateTime two) {
        return one.isAfter(two) ? one : two;
    }

    @Nullable
    public static java.sql.Date toSqlDate(@Nullable Object dateTime) { //TODO check conversion and return value
        if (dateTime == null) {
            return null;
        } else if (dateTime instanceof java.sql.Date date) {
            return date;
        } else {
            Date javaDate = toJavaDate(dateTime);
            if (javaDate == null) {
                return null;
            } else {
                return new java.sql.Date(javaDate.getTime());
            }
        }
    }

    @Nullable
    public static Timestamp toSqlTimestamp(@Nullable Object dateTime) {
        if (dateTime == null) {
            return null;
        } else if (dateTime instanceof Timestamp timestamp) {
            return timestamp;
        } else if (dateTime instanceof ZonedDateTime zonedDateTime) {
            return new Timestamp(zonedDateTime.toInstant().toEpochMilli());
        } else {
            return toSqlTimestamp(toDateTime(dateTime));
        }
    }

    @Nullable
    public static Time toSqlTime(@Nullable Object localTime) {
        if (localTime == null) {
            return null;
        } else if (localTime instanceof Time time) {
            return time;
        } else if (localTime instanceof LocalTime) {
            return new Time(toJavaDate(localTime).getTime());//TODO test/check this
        } else {
            return toSqlTime(toTime(localTime));
        }
    }

    @Nullable
    public static String toIsoDateTime(@Nullable Object dateTime) {
        if (dateTime == null) {
            return null;
        } else {
            return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(toDateTime(dateTime));
        }
    }

    @Nullable
    public static String toIsoDateTimeUtc(@Nullable Object dateTime) {
        if (dateTime == null) {
            return null;
        } else {
            return DateTimeFormatter.ISO_INSTANT.format(toDateTime(dateTime).withZoneSameInstant(UTC));
        }
    }

    @Nullable
    public static String toIsoDateTimeLocal(@Nullable Object dateTime) {
        if (dateTime == null) {
            return null;
        } else {
            return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(toDateTime(dateTime).withZoneSameInstant(ZoneOffset.systemDefault()));
        }
    }

    private final static DateTimeFormatter READABLE_DATE_TIME = new DateTimeFormatterBuilder()
            .append(ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .toFormatter();

    private final static DateTimeFormatter READABLE_DATE_TIME_WITH_TIMEZONE = new DateTimeFormatterBuilder()
            .append(ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .appendLiteral(' ')
            .appendPattern("z")
            .toFormatter();

    @Nullable
    public static String toUserReadableDateTime(@Nullable Object dateTime) {
        if (dateTime == null) {
            return null;
        } else {
            return READABLE_DATE_TIME.format(toDateTime(dateTime).withZoneSameInstant(systemZoneId()));
        }
    }

    @Nullable
    public static String toUserReadableDateTimeWithTimezone(@Nullable Object dateTime) {
        if (dateTime == null) {
            return null;
        } else {
            return READABLE_DATE_TIME_WITH_TIMEZONE.format(toDateTime(dateTime).withZoneSameInstant(systemZoneId()));
        }
    }

    public static String getReadableTimezoneOffset() {
        return systemZoneId().getId();
    }

    @Nullable
    public static String toIsoDate(@Nullable Object date) {
        LocalDate localDate = toDate(date);
        if (localDate == null) {
            return null;
        } else {
            return localDate.toString();
        }
    }

    private final static DateTimeFormatter ISO_LOCAL_TIME_NO_MILLIS = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .toFormatter();

    @Nullable
    public static String toIsoTime(@Nullable Object value) {
        return toIsoTime(toTime(value));
    }

    @Nullable
    public static String toIsoTimeWithNanos(@Nullable Object value) {
        return CmDateUtils.toIsoTimeWithNanos(toTime(value));
    }

    @Nullable
    public static String toIsoTime(@Nullable LocalTime localTime) {
        if (localTime == null) {
            return null;
        } else {
            return ISO_LOCAL_TIME_NO_MILLIS.format(localTime);
        }
    }

    @Nullable
    public static String toIsoTimeWithNanos(@Nullable LocalTime localTime) {
        if (localTime == null) {
            return null;
        } else {
            return DateTimeFormatter.ISO_LOCAL_TIME.format(localTime);
        }
    }

    @Nullable
    public static LocalTime toTime(@Nullable Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof LocalTime localTime) {
            return localTime;
        } else if (value instanceof String string) {
            if (isBlank(string)) {
                return null;
            } else {
                return parseLocalTime(string);
            }
        } else if (value instanceof Number) {
            return toDateTime(value).toLocalTime();
        } else if (value instanceof Date date) {
            return toTime(date.getTime());
        } else if (value instanceof ReadableInstant readableInstant) {
            return toTime(readableInstant.getMillis());
        } else {
            throw new IllegalArgumentException(format("unable to convert value %s of type %s to TIME type", value, value.getClass()));
        }
    }
    private final static List<DateTimeFormatter> TIME_FORMATTERS = List.copyOf(asList(
            DateTimeFormatter.ISO_LOCAL_TIME.withResolverStyle(ResolverStyle.LENIENT),
            DateTimeFormatter.ISO_DATE_TIME));

    private static LocalTime parseLocalTime(String value) {
        for (DateTimeFormatter dateTimeFormatter : TIME_FORMATTERS) {
            try {
                return LocalTime.parse(value, dateTimeFormatter);
            } catch (IllegalArgumentException | DateTimeParseException e) {
                LOGGER.debug("unable to parse string value = '{}' with time format = {}", value, dateTimeFormatter);
                LOGGER.trace("date parsing error", e);
            }
        }
        throw new IllegalArgumentException(format("unsupported time format, unable to parse string = '%s'", value));
    }

    @Nullable
    public static LocalDate toDate(@Nullable Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof LocalDate localDate) {
            return localDate;
        } else if (value instanceof java.sql.Date date) {
            return date.toLocalDate();
        } else if (value instanceof String string) {
            if (isBlank(string)) {
                return null;
            } else {
                return parseLocalDate(string);
            }
        } else if (value instanceof Number number) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(number.longValue()), ZoneOffset.UTC).toLocalDate();
        } else if (value instanceof Date date) {
            return toDate(date.getTime());
        } else if (value instanceof ReadableInstant readableInstant) {
            return toDate(readableInstant.getMillis());
        } else if (value instanceof Temporal temporal) {
            return LocalDate.from(temporal);
        } else {
            throw new IllegalArgumentException(format("unable to convert value %s of type %s to DATE type", value, value.getClass()));
        }
    }

    @Nullable
    public static LocalDate toDateAtTimeZone(@Nullable Object value, TimeZone timezone) {
        checkNotNull(timezone, "timezone is null");
        ZonedDateTime dateTime = toDateTime(value);
        if (dateTime == null) {
            return null;
        } else {
            return dateTime.withZoneSameInstant(timezone.toZoneId()).toLocalDate();
        }
    }

    private final static List<java.time.format.DateTimeFormatter> DATE_FORMATTERS = List.copyOf(asList(
            java.time.format.DateTimeFormatter.ISO_DATE.withResolverStyle(ResolverStyle.LENIENT),
            java.time.format.DateTimeFormatter.ISO_DATE_TIME.withResolverStyle(ResolverStyle.LENIENT),
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy")));

    private static java.time.LocalDate parseLocalDate(String value) {
        for (java.time.format.DateTimeFormatter dateTimeFormatter : DATE_FORMATTERS) {
            try {
                return java.time.LocalDate.parse(value, dateTimeFormatter);
            } catch (IllegalArgumentException | DateTimeParseException e) {
                LOGGER.debug("unable to parse string value = '{}' with date format = {}", value, dateTimeFormatter);
                LOGGER.trace("date parsing error", e);
            }
        }
        throw new IllegalArgumentException(format("unsupported date format, unable to parse string = '%s'", value));
    }

    @Nullable
    public static ZonedDateTime toDateTime(@Nullable Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof ZonedDateTime zonedDateTime) {
            return zonedDateTime;
        } else if (value instanceof String string) {
            if (isBlank(string)) {
                return null;
            } else {
                return parseDateTime(string);
            }
        } else if (value instanceof Instant instant) {
            return ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
        } else if (value instanceof Number number) {
            return toDateTime(Instant.ofEpochMilli(number.longValue()));
        } else if (value instanceof java.sql.Timestamp timestamp) {
            return ZonedDateTime.ofInstant(timestamp.toInstant(), UTC);
        } else if (value instanceof Date date) {
            return toDateTime(date.getTime());
        } else if (value instanceof ReadableInstant readableInstant) {
            return toDateTime(readableInstant.getMillis());
        } else if (value instanceof Calendar calendar) {
            return toDateTime(calendar.toInstant());
        } else if (value instanceof LocalTime localTime) {
            return localTime.atDate(ZonedDateTime.now(systemZoneId()).toLocalDate()).atZone(systemZoneId());
        } else if (value instanceof OffsetDateTime offsetDateTime) {
            return offsetDateTime.toZonedDateTime();
        } else {
            throw new IllegalArgumentException(format("unable to convert value %s of type %s to java date type", value, value.getClass()));
        }
    }

    private final static List<DateTimeFormatter> DATE_TIME_PARSING_FORMATTERS = List.of(DateTimeFormatter.ISO_OFFSET_DATE_TIME.withResolverStyle(ResolverStyle.LENIENT),
            new DateTimeFormatterBuilder().parseCaseInsensitive().append(ISO_LOCAL_DATE_TIME).appendOffset("+HHmm", "Z").toFormatter().withResolverStyle(ResolverStyle.LENIENT),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.withResolverStyle(ResolverStyle.LENIENT).withZone(systemZoneId()));//TODO check user timezone offset (?)

    private static ZonedDateTime parseDateTime(String value) {
        for (DateTimeFormatter dateTimeFormatter : DATE_TIME_PARSING_FORMATTERS) {
            try {
                return ZonedDateTime.parse(value, dateTimeFormatter);
            } catch (IllegalArgumentException | DateTimeParseException e) {
                LOGGER.debug("unable to parse string value = '{}' with date format = {}", value, dateTimeFormatter);
                LOGGER.trace("date parsing error", e);
            }
        }
        try {
            LocalDate localDate = parseLocalDate(value);
            return ZonedDateTime.of(localDate.atStartOfDay(), ZoneOffset.UTC);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            LOGGER.debug("unable to parse string value = '{}' as local date", value);
            LOGGER.trace("date parsing error", e);
        }
        try {
            LocalTime localTime = parseLocalTime(value);
            return toDateTime(localTime);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            LOGGER.debug("unable to parse string value = '{}' as local time", value);
            LOGGER.trace("date parsing error", e);
        }
        throw new IllegalArgumentException(format("unsupported date/time format, unable to parse string = '%s'", value));
    }

    @Nullable
    public static Date toJavaDate(@Nullable Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Number number) {
            return new Date(number.longValue());
        } else if (value instanceof Date date) {
            return date;
        } else if (value instanceof ReadableInstant readableInstant) {
            return toJavaDate(readableInstant.getMillis());
        } else if (value instanceof ZonedDateTime zonedDateTime) {
            return toJavaDate(zonedDateTime.toInstant().toEpochMilli());
        } else if (value instanceof LocalDate localDate) {
            return toJavaDate(localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli());
        } else if (value instanceof LocalTime localTime) {
            return toJavaDate(localTime.atDate(LocalDate.of(1970, 1, 1)).toInstant(ZoneOffset.UTC).toEpochMilli());
        } else {
            ZonedDateTime dateTime;
            try {
                dateTime = toDateTime(value);
            } catch (Exception ex) {
                throw new IllegalArgumentException(format("unable to convert value %s of type %s to java date type", value, value.getClass()), ex);
            }
            if (dateTime == null) {
                return null;
            } else {
                return toJavaDate(dateTime);
            }
        }
    }

    public static String toIsoDuration(long millis) {
        return toIsoDuration(Duration.ofMillis(millis));
    }

    public static String toUserDuration(long millis) {
        return toUserDuration(Duration.ofMillis(millis));
    }

    @Nullable
    public static String toUserDuration(@Nullable Duration duration) {
        return duration == null ? null : duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }

    @Nullable
    public static String toUserDuration(@Nullable String duration) {
        return toUserDuration(toDuration(duration));
    }

    public static String toIsoDuration(Duration duration) {
        return duration.toString();
    }

    public static String toIsoDuration(Object duration) {
        return toIsoDuration(toDuration(duration));
    }

    public static String toIsoInterval(Interval interval) {
        return interval.toString();
    }

    public static String toIsoInterval(Object interval) {
        return toIsoInterval(toInterval(interval));
    }

    @Nullable
    public static Duration toDuration(@Nullable Object duration) {
        if (duration instanceof Duration) {
            return (Duration) duration;
        } else {
            return toDuration(toStringOrNull(duration));
        }
    }

    @Nullable
    public static Duration toDuration(@Nullable String duration) {
        try {
            Interval interval = toInterval(duration);
            if (interval == null) {
                return null;
            } else {
                return interval.toDuration();
            }
        } catch (Exception ex) {
            throw runtime(ex, "error parsign duration from string =< %s >", duration);
        }
    }

    public static boolean isNotNullAndGtZero(@Nullable Duration duration) {
        return duration != null && !duration.isZero() && !duration.isNegative();
    }

    @Nullable
    public static Interval toInterval(@Nullable Object interval) {
        if (interval instanceof Interval) {
            return (Interval) interval;
        } else {
            return toInterval(toStringOrNull(interval));
        }
    }

    @Nullable
    public static Interval toInterval(@Nullable String interval) {
        if (isBlank(interval)) {
            return null;
        } else {
            Matcher matcher = Pattern.compile("^([0-9]+):([0-9]+)(:([0-9.]+))?$").matcher(interval);
            if (matcher.find()) {
                return Interval.fromDuration(Duration.ofHours(toLongOrZero(matcher.group(1)))
                        .plusMinutes(toLongOrZero(matcher.group(2)))
                        .plusMillis(round(toDoubleOrZero(matcher.group(4)) * 1000)));
            }
            matcher = Pattern.compile("^( *([-]*[0-9]+) +years?)?( *([-]*[0-9]+) +mons?)?( *([-]*[0-9]+) +days?)?( +([0-9]+):([0-9]+)(:([0-9.]+))?)?$").matcher(interval);
            if (matcher.find()) {
                return Interval.fromPeriod(Period.of(toIntOrZero(matcher.group(2)), toIntOrZero(matcher.group(4)), toIntOrZero(matcher.group(6)))).withDuration(Duration
                        .ofHours(toLongOrZero(matcher.group(8)))
                        .plusMinutes(toLongOrZero(matcher.group(9)))
                        .plusMillis(round(toDoubleOrZero(matcher.group(11)) * 1000)));
            }
            matcher = Pattern.compile("^( *([-]*[0-9]+) +years?)?( *([-]*[0-9]+) +mons?)?( *([-]*[0-9]+) +days?)?( *([-]*[0-9]+) +hours?)?( *([-]*[0-9]+) +mins?)?( *([-]*[0-9.]+) +secs?)?$").matcher(interval);
            if (matcher.find()) {
                return Interval.fromPeriod(Period.of(toIntOrZero(matcher.group(2)), toIntOrZero(matcher.group(4)), toIntOrZero(matcher.group(6)))).withDuration(Duration
                        .ofHours(toLongOrZero(matcher.group(8)))
                        .plusMinutes(toLongOrZero(matcher.group(10)))
                        .plusMillis(round(toDoubleOrZero(matcher.group(12)) * 1000)));
            }
            return Interval.valueOf(interval);
        }
    }

    public static boolean isDate(@Nullable Object value) {
        return value != null && LocalDate.class.isAssignableFrom(value.getClass());
    }

    public static boolean isTime(@Nullable Object value) {
        return value != null && LocalTime.class.isAssignableFrom(value.getClass());
    }

    public static boolean isDateTime(@Nullable Object value) {
        return value != null && isDateTime(value.getClass());
    }

    public static boolean isDuration(@Nullable Object value) {
        return value != null && value instanceof Duration;
    }

    public static boolean isInterval(@Nullable Object value) {
        return value != null && value instanceof Interval || value instanceof Duration || value instanceof Period;
    }

    public static boolean isDateTime(Class classe) {
        return ZonedDateTime.class.isAssignableFrom(classe) || Instant.class.isAssignableFrom(classe) || Date.class.isAssignableFrom(classe) || ReadableInstant.class.isAssignableFrom(classe) || Calendar.class.isAssignableFrom(classe);
    }

    public static boolean isAnyDateType(@Nullable Object value) {
        return isDateTime(value) || isDate(value) || isTime(value);
    }

    public static boolean isBeforeNow(Object value) {
        checkNotNull(value);
        return toJavaDate(value).before(toJavaDate(now()));
    }

    public static boolean isAfterNow(Object value) {
        checkNotNull(value);
        return toJavaDate(value).after(toJavaDate(now()));
    }

    /**
     * note: restrict this to IANA time zones, or the intersection betweeb java
     * time zones and postgres time zones
     */
    public static boolean zoneIdIsRegion(ZoneId zoneId) {
        return zoneId.toString().matches("[a-zA-Z]+/.+|UTC|GMT");//TODO improve this
    }

    public static ZoneId checkThatZoneIdIsRegion(ZoneId zoneId) {
        checkArgument(zoneIdIsRegion(zoneId), "invalid zone id =< %s >: this zone id is not a region", zoneId);
        return zoneId;
    }

}
