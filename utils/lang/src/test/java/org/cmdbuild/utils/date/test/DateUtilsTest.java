/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.date.test;

import static com.google.common.base.Objects.equal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.date.CmDateUtils.toDate;
import static org.cmdbuild.utils.date.CmDateUtils.toDateAtTimeZone;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDate;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDuration;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoInterval;
import static org.cmdbuild.utils.date.CmDateUtils.toJavaDate;
import static org.cmdbuild.utils.date.CmDateUtils.toTime;
import static org.cmdbuild.utils.date.ExtjsDateUtils.extjsDateTimeFormatToJavaDateTimeFormat;
import org.joda.time.Period;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.cmdbuild.utils.date.CmDateUtils.checkThatZoneIdIsRegion;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeUtc;

public class DateUtilsTest {

    @Test
    public void testSqlTimestampPrecisionConversion() {
        Timestamp timestamp = new java.sql.Timestamp(120, 0, 21, 9, 12, 58, 96520000);
        assertEquals(toIsoDateTimeUtc(toDateTime("2020-01-21T09:12:58.09652Z").withZoneSameLocal(ZoneId.systemDefault())), toIsoDateTimeUtc(timestamp));
    }

    @Test
    public void testSqlTimestampPrecisionConversion1() {
        assertEquals("2020-01-21T09:12:58.09652Z", toIsoDateTime(toDateTime("2020-01-21T09:12:58.09652Z")));
    }

    @Test
    public void testZoneIdCheck1() {
        checkThatZoneIdIsRegion(ZoneId.of("Europe/Rome"));
        checkThatZoneIdIsRegion(ZoneId.of("America/New_York"));
        checkThatZoneIdIsRegion(ZoneId.of("UTC"));
        checkThatZoneIdIsRegion(ZoneId.of("GMT"));
        checkThatZoneIdIsRegion(ZoneId.of("America/Argentina/Rio_Gallegos"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZoneIdCheck2() {
        checkThatZoneIdIsRegion(ZoneId.of("+01:00"));
    }

    @Test
    public void testLocalTimeToIsoTime1() {
        LocalTime localTime = LocalTime.of(12, 13, 14);

        String stringTime = CmDateUtils.toIsoTime(localTime);

        assertEquals("12:13:14", stringTime);
    }

    @Test
    public void testLocalTimeToIsoTime2() {
        LocalTime localTime = LocalTime.of(00, 13, 14);

        String stringTime = CmDateUtils.toIsoTime(localTime);

        assertEquals("00:13:14", stringTime);
    }

    @Test
    public void testLocalTimeToIsoTime3() {
        LocalTime localTime = LocalTime.of(23, 59, 59);

        String stringTime = CmDateUtils.toIsoTime(localTime);

        assertEquals("23:59:59", stringTime);
    }

    @Test
    public void testLocalTimeToIsoTime4() {
        LocalTime localTime = LocalTime.of(0, 0, 0);

        String stringTime = CmDateUtils.toIsoTime(localTime);

        assertEquals("00:00:00", stringTime);
    }

    @Test
    public void testLocalDateToIsoDate() {
        LocalDate localDate = LocalDate.of(2018, 9, 12);

        String stringDate = CmDateUtils.toIsoDate(localDate);

        assertEquals("2018-09-12", stringDate);
    }

    @Test
    public void testLocalDateConversion1() {
        LocalDate localDate = CmDateUtils.toDate("2018-09-12");

        String stringDate = CmDateUtils.toIsoDate(localDate);

        assertEquals("2018-09-12", stringDate);
    }

    @Test
    public void testLocalDateConversion2() {
        java.sql.Date sqlDate = new java.sql.Date(2018 - 1900, 8, 12);

        LocalDate localDate = CmDateUtils.toDate(sqlDate);
        assertEquals(2018, localDate.getYear());
        assertEquals(9, localDate.getMonthValue());
        assertEquals(12, localDate.getDayOfMonth());

        String stringDate = CmDateUtils.toIsoDate(localDate);

        assertEquals("2018-09-12", stringDate);
    }

//    @Test
//    public void testDateTimeParsing() {
//        ZonedDateTime value = CmDateUtils.toDateTime("2019-09-03 12:37:35 UTC");
//
//        assertEquals("2019-09-03T12:37:35Z", toIsoDateTimeUtc(value));
//    }
    @Test
    public void testLocalDateConversionWithTimeZone() {
        Date date = toJavaDate(toDateTime("2014-09-30T22:00:00Z"));
        assertEquals("2014-09-30", toIsoDate(toDate(date)));
        assertEquals("2014-10-01", toIsoDate(toDateAtTimeZone(date, TimeZone.getTimeZone("Europe/Rome"))));
    }

    @Test
    public void testLocalDateParsinge() {
        LocalDate localDate = CmDateUtils.toDate("26/04/2018");

        String stringDate = CmDateUtils.toIsoDate(localDate);

        assertEquals("2018-04-26", stringDate);
    }

    @Test
    public void testLocalDateToJavaDate1() {
        LocalDate localDate = LocalDate.of(2018, 9, 12);

        Date date = CmDateUtils.toJavaDate(localDate);

        assertEquals(2018 - 1900, date.getYear());
        assertEquals(8, date.getMonth());
//        assertEquals(12, date.getDate());

        assertEquals("2018-09-12T00:00:00Z", CmDateUtils.toIsoDateTimeUtc(date));
    }

    @Test
    public void testLocalDateToJavaDate2() {
        LocalDate localDate = LocalDate.of(2018, 9, 12);

        Date date = CmDateUtils.toJavaDate(localDate);

        localDate = CmDateUtils.toDate(date);

        String stringDate = CmDateUtils.toIsoDate(localDate);

        assertEquals("2018-09-12", stringDate);
    }

    @Test
    public void testLocalDateToJavaDate3() {
        LocalDate localDate = LocalDate.of(2009, 3, 30);
        Date date = CmDateUtils.toJavaDate(localDate);

        assertEquals(localDate, CmDateUtils.toDate(date));
        assertEquals("2009-03-30", CmDateUtils.toIsoDate(date));
        assertEquals("2009-03-30T00:00:00Z", CmDateUtils.toIsoDateTimeUtc(date));
    }

    @Test
    public void testStringToJavaDate1() {
        String in = "2018-04-26T11:23:57.589Z";

        Date javaDate = toJavaDate(in);

        assertEquals(1524741837589l, javaDate.getTime());
    }

    @Test
    public void testStringToJavaDate2() {
        String in = "2018-04-26T12:28:52.291Z";

        Date javaDate = toJavaDate(in);

        assertEquals(1524745732291l, javaDate.getTime());
    }

    @Test
    public void testStringToJavaDate3() {
        String in = "2018-04-26";

        Date javaDate = toJavaDate(in);

        assertEquals(1524700800000l, javaDate.getTime());
    }

    @Test
    public void testLocalTimeToJavaDate() {
        LocalTime localTime = LocalTime.of(12, 13, 14);

        Date javaDate = toJavaDate(localTime);

        assertEquals(43994000l, javaDate.getTime());

        localTime = CmDateUtils.toTime(javaDate);

        assertEquals(LocalTime.of(12, 13, 14), localTime);

        String string = CmDateUtils.toIsoTime(localTime);

        assertEquals("12:13:14", string);

        localTime = CmDateUtils.toTime(string);

        assertEquals(LocalTime.of(12, 13, 14), localTime);
    }

    @Test
    public void testGregorianCalendarToZonedDateTime() {
        Calendar source = GregorianCalendar.getInstance();
        source.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
        source.set(Calendar.YEAR, 1980);
        source.set(Calendar.MONTH, 1);
        source.set(Calendar.DAY_OF_MONTH, 24);
        source.set(Calendar.HOUR_OF_DAY, 14);
        source.set(Calendar.MINUTE, 37);
        source.set(Calendar.SECOND, 44);

        ZonedDateTime dateTime = toDateTime(source).withZoneSameInstant(ZoneOffset.UTC);

        assertEquals(1980, dateTime.getYear());
        assertEquals(2, dateTime.getMonth().getValue());
        assertEquals(24, dateTime.getDayOfMonth());
        assertEquals(14, dateTime.getHour());
        assertEquals(37, dateTime.getMinute());
        assertEquals(44, dateTime.getSecond());
    }

    @Test
    public void testGregorianCalendarToZonedDateTimeWithZone() {
        Calendar source = GregorianCalendar.getInstance();
        source.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
        source.set(Calendar.YEAR, 1980);
        source.set(Calendar.MONTH, 1);
        source.set(Calendar.DAY_OF_MONTH, 24);
        source.set(Calendar.HOUR_OF_DAY, 14);
        source.set(Calendar.MINUTE, 37);
        source.set(Calendar.SECOND, 44);

        ZonedDateTime dateTime = toDateTime(source).withZoneSameInstant(ZoneId.of("Europe/Rome"));

        assertEquals(1980, dateTime.getYear());
        assertEquals(2, dateTime.getMonth().getValue());
        assertEquals(24, dateTime.getDayOfMonth());
        assertEquals(14, dateTime.getHour());
        assertEquals(37, dateTime.getMinute());
        assertEquals(44, dateTime.getSecond());
    }

    @Test
    public void testExtjsDateTimeFormatToJavaDateTimeFormat() {
        assertEquals("dd/MM/yyyy HH:mm:ss", extjsDateTimeFormatToJavaDateTimeFormat("d/m/Y H:i:s"));
        assertEquals("MM/dd/yyyy hh:mm:ss a", extjsDateTimeFormatToJavaDateTimeFormat("m/d/Y h:i:s A"));
    }

    @Test
    public void testEqualTimes1() {
        assertTrue(equal(toTime("11:13:18"), toTime("11:13:18")));
        assertTrue(equal(toTime("15:00:00"), toTime("15:00:00")));
        assertTrue(equal(toTime("15:00:00"), toTime("15:00")));
        assertFalse(equal(toTime("15:00:01"), toTime("15:00")));
    }

    @Test
    public void testDurationParsing1() {
        assertEquals("PT2H1M3S", toIsoDuration("02:01:03"));
        assertEquals("PT241H", toIsoDuration("10 days 01:00:00"));
        assertEquals("PT2H30M", toIsoDuration("0 years 0 mons 0 days 2 hours 30 mins 0.00 secs"));
    }

    @Test(expected = Exception.class)
    public void testDurationParsing2() {
        toIsoDuration("1 year 01:00:00");
    }

    @Test
    public void testIntervalParsing() {
        assertEquals("PT2H1M3S", toIsoInterval("02:01:03"));
        assertEquals("PT2H1M3S", toIsoInterval("PT2H1M3S"));
        assertEquals("P1Y2M10DT2H30M", toIsoInterval("P1Y2M10DT2H30M"));
        assertEquals("P-1Y-2M10DT2H30M", toIsoInterval("P-1Y-2M10DT2H30M"));
        assertEquals("PT2H30M", toIsoInterval("0 years 0 mons 0 days 2 hours 30 mins 0.00 secs"));
        assertEquals("P1Y2M3DT2H30M", toIsoInterval("1 years 2 mons 3 days 2 hours 30 mins 0.00 secs"));
        assertEquals("P-1Y-2M-3DT2H30M", toIsoInterval("-1 years -2 mons -3 days 2 hours 30 mins 0.00 secs"));
        assertEquals("P1Y2M-6DT4H30M8S", toIsoInterval("1 years 2 mons -6 days 4 hours 30 mins 8.00 secs"));
        assertEquals("P0D", toIsoInterval(Period.ZERO));
        assertEquals("P0D", toIsoInterval(Duration.ZERO));
    }
}
