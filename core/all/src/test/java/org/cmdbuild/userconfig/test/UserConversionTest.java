package org.cmdbuild.userconfig.test;

import java.time.format.DateTimeFormatter;
import org.cmdbuild.userconfig.UserPrefHelper;
import org.cmdbuild.userconfig.DateAndFormatPreferences;
import org.cmdbuild.userconfig.DateAndFormatPreferencesImpl;
import org.cmdbuild.userconfig.UserPrefHelperImpl;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeUtc;
import static org.cmdbuild.utils.lang.CmConvertUtils.toDouble;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class UserConversionTest {

    @Test
    public void testUserPrefHelper() {
        DateAndFormatPreferences config = DateAndFormatPreferencesImpl.builder()
                .withTimezone("UTC")
                .withDateFormat("ISO_DATE", DateTimeFormatter.ISO_DATE)
                .withTimeFormat("ISO_TIME", DateTimeFormatter.ISO_TIME)
                .withDateTimeFormat("ISO_DATE_TIME", DateTimeFormatter.ISO_DATE_TIME)
                .withDecimalSeparator(".")
                .withNumberGroupingSeparator("")
                .build();

        UserPrefHelper helper = new UserPrefHelperImpl(config);

        assertEquals("1988-12-10T00:00:00Z", toIsoDateTimeUtc(helper.parseDateTime("1988-12-10T00:00:00")));

        config = DateAndFormatPreferencesImpl.copyOf(config).withTimezone("Europe/Rome").build();
        helper = new UserPrefHelperImpl(config);

        assertEquals("1988-12-09T23:00:00Z", toIsoDateTimeUtc(helper.parseDateTime("1988-12-10T00:00:00")));

        config = DateAndFormatPreferencesImpl.copyOf(config).withTimezone("America/New_York").build();
        helper = new UserPrefHelperImpl(config);

        assertEquals("1988-12-10T05:00:00Z", toIsoDateTimeUtc(helper.parseDateTime("1988-12-10T00:00:00")));
    }

    @Test
    public void testUserPrefHelper2() {
        DateAndFormatPreferences config = DateAndFormatPreferencesImpl.builder()
                .withTimezone("UTC")
                .withDateFormat("ISO_DATE", DateTimeFormatter.ISO_DATE)
                .withTimeFormat("ISO_TIME", DateTimeFormatter.ISO_TIME)
                .withDateTimeFormat("ISO_DATE_TIME", DateTimeFormatter.ISO_DATE_TIME)
                .withDecimalSeparator(".")
                .withNumberGroupingSeparator("")
                .build();

        assertEquals(123.456d, toDouble(new UserPrefHelperImpl(config).parseNumber("123.456")), 0.0d);

        assertEquals(123d, toDouble(new UserPrefHelperImpl(config).parseNumber("123")), 0.0d);

        assertEquals(0.002d, toDouble(new UserPrefHelperImpl(config).parseNumber("0.002")), 0.0d);

        config = DateAndFormatPreferencesImpl.copyOf(config)
                .withDecimalSeparator(",")
                .withNumberGroupingSeparator(".")
                .build();

        assertEquals(9123.456d, toDouble(new UserPrefHelperImpl(config).parseNumber("9.123,456")), 0.0d);

        assertEquals(123d, toDouble(new UserPrefHelperImpl(config).parseNumber("123")), 0.0d);

        assertEquals(0.002d, toDouble(new UserPrefHelperImpl(config).parseNumber("0,002")), 0.0d);

    }
}
