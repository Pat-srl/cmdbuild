/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.test;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import static org.cmdbuild.etl.utils.UserConvertUtils.parseDateTime;
import static org.cmdbuild.etl.utils.UserConvertUtils.timestampToDateStrWithUserFormat;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeUtc;
import static org.cmdbuild.utils.date.ExtjsDateUtils.extjsDateTimeFormatToJavaDateTimeFormatter;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class UserConversionTest {

    @Test
    public void testDateTimeConversion1() {
        ZonedDateTime dateTime = toDateTime("2018-03-22T10:12:13Z");
        DateTimeFormatter formatter = extjsDateTimeFormatToJavaDateTimeFormatter("d/m/Y H:i:s");
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Rome");

        String value = timestampToDateStrWithUserFormat(dateTime, timeZone, formatter);
        assertEquals("22/03/2018 11:12:13", value);

        ZonedDateTime parsedDateTime = parseDateTime(value, timeZone, formatter);
        assertEquals(toIsoDateTimeUtc(dateTime), toIsoDateTimeUtc(parsedDateTime));
    }

    @Test
    public void testDateTimeConversion2() {
        ZonedDateTime dateTime = toDateTime("2018-03-22T10:12:13Z");
        DateTimeFormatter formatter = extjsDateTimeFormatToJavaDateTimeFormatter("m/d/Y H:i:s A");
        TimeZone timeZone = TimeZone.getTimeZone("America/New_York");

        String value = timestampToDateStrWithUserFormat(dateTime, timeZone, formatter);
        assertEquals("03/22/2018 06:12:13 AM", value);

        ZonedDateTime parsedDateTime = parseDateTime(value, timeZone, formatter);
        assertEquals(toIsoDateTimeUtc(dateTime), toIsoDateTimeUtc(parsedDateTime));
    }

}
