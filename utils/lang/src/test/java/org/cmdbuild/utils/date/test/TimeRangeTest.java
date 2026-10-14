package org.cmdbuild.utils.date.test;

import static org.cmdbuild.utils.date.CmDateUtils.timerange;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import org.cmdbuild.utils.date.TimeRangeHelper;
import org.cmdbuild.utils.date.RangeHelper;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class TimeRangeTest {

    @Test
    public void testTimeRangeUtils1() {
        RangeHelper helper = timerange("""
                                                                    {
                                                                        "timezone": "Europe/Rome",
                                                                        "from" : '10:00',
                                                                        "to": '11:00'
                                                                    }
                                                            """);

        assertTrue(helper.includes(toDateTime("2022-07-25T10:30:00+02:00")));
        assertTrue(helper.includes(toDateTime("2022-07-25T10:01:00+02:00")));
        assertTrue(helper.includes(toDateTime("2022-07-25T10:00:00+02:00")));
        assertTrue(helper.includes(toDateTime("2022-07-25T10:59:00+02:00")));

        assertFalse(helper.includes(toDateTime("2022-07-25T11:30:00+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T11:00:00+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T09:59:59+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T01:10:10+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T23:10:10+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T24:00:00+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T00:00:00+02:00")));
    }

    @Test
    public void testTimeRangeUtils2() {
        RangeHelper helper = timerange("""
                                                                    {
                                                                        "timezone": "Europe/Rome",
                                                                        "range": {
                                                                            "from" : '10:00',
                                                                            "to": '11:00'
                                                                         }
                                                                    }
                                                            """);

        assertTrue(helper.includes(toDateTime("2022-07-25T10:30:00+02:00")));
        assertTrue(helper.includes(toDateTime("2022-07-25T10:01:00+02:00")));
        assertTrue(helper.includes(toDateTime("2022-07-25T10:00:00+02:00")));
        assertTrue(helper.includes(toDateTime("2022-07-25T10:59:00+02:00")));

        assertFalse(helper.includes(toDateTime("2022-07-25T11:30:00+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T11:00:00+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T09:59:59+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T01:10:10+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T23:10:10+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T24:00:00+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T00:00:00+02:00")));
    }

    @Test
    public void testTimeRangeUtils3() {
        TimeRangeHelper helper = timerange("""
                                                                    {
                                                                        "timezone": "Europe/Rome",
                                                                        "entries": [{
                                                                            "from" : '10:00',
                                                                            "to": '11:00'
                                                                         }, {
                                                                            "from" : '14:00',
                                                                            "to": '17:00'
                                                                         }]
                                                                    }
                                                            """);

        assertTrue(helper.includes(toDateTime("2022-07-25T10:30:00+02:00")));
        assertTrue(helper.includes(toDateTime("2022-07-25T10:01:00+02:00")));
        assertTrue(helper.includes(toDateTime("2022-07-25T10:00:00+02:00")));
        assertTrue(helper.includes(toDateTime("2022-07-25T10:59:00+02:00")));

        assertTrue(helper.includes(toDateTime("2022-07-25T14:01:00+02:00")));
        assertTrue(helper.includes(toDateTime("2022-07-25T14:00:00+02:00")));
        assertTrue(helper.includes(toDateTime("2022-07-25T15:32:00+02:00")));
        assertTrue(helper.includes(toDateTime("2022-07-25T16:58:00+02:00")));

        assertFalse(helper.includes(toDateTime("2022-07-25T11:30:00+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T11:00:00+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T09:59:59+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T01:10:10+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T23:10:10+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T24:00:00+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T00:00:00+02:00")));
    }

    @Test
    public void testTimeRangeUtils4() {
        TimeRangeHelper helper = timerange("""
                                                                  {
                                                                      "timezone": "Europe/Rome",
                                                                      "range":[{
                                                                        "name":"one",
                                                                          "from" : '10:00',
                                                                          "to": '11:00'
                                                                      },{
                                                                        "name":"two",
                                                                          "from" : '14:00',
                                                                          "to": '17:00'
                                                                      }]
                                                                  }
                                                          """);

        assertTrue(helper.range("one").includes(toDateTime("2022-07-25T10:30:00+02:00")));
        assertTrue(helper.range("one").includes(toDateTime("2022-07-25T10:01:00+02:00")));
        assertTrue(helper.range("one").includes(toDateTime("2022-07-25T10:00:00+02:00")));
        assertTrue(helper.range("one").includes(toDateTime("2022-07-25T10:59:00+02:00")));

        assertFalse(helper.range("one").includes(toDateTime("2022-07-25T14:01:00+02:00")));
        assertFalse(helper.range("one").includes(toDateTime("2022-07-25T14:00:00+02:00")));
        assertFalse(helper.range("one").includes(toDateTime("2022-07-25T15:32:00+02:00")));
        assertFalse(helper.range("one").includes(toDateTime("2022-07-25T16:58:00+02:00")));

        assertTrue(helper.range("two").includes(toDateTime("2022-07-25T14:01:00+02:00")));
        assertTrue(helper.range("two").includes(toDateTime("2022-07-25T14:00:00+02:00")));
        assertTrue(helper.range("two").includes(toDateTime("2022-07-25T15:32:00+02:00")));
        assertTrue(helper.range("two").includes(toDateTime("2022-07-25T16:58:00+02:00")));

        assertFalse(helper.range("one").includes(toDateTime("2022-07-25T11:30:00+02:00")));
        assertFalse(helper.range("one").includes(toDateTime("2022-07-25T11:00:00+02:00")));
        assertFalse(helper.range("one").includes(toDateTime("2022-07-25T09:59:59+02:00")));
        assertFalse(helper.range("one").includes(toDateTime("2022-07-25T01:10:10+02:00")));
        assertFalse(helper.range("one").includes(toDateTime("2022-07-25T23:10:10+02:00")));
        assertFalse(helper.range("one").includes(toDateTime("2022-07-25T24:00:00+02:00")));
        assertFalse(helper.range("one").includes(toDateTime("2022-07-25T00:00:00+02:00")));
    }

    @Test
    public void testTimeRangeUtils5() {
        RangeHelper helper = timerange("Europe/Rome|10:00 - 11:00");

        assertTrue(helper.includes(toDateTime("2022-07-25T10:30:00+02:00")));
        assertTrue(helper.includes(toDateTime("2022-07-25T10:01:00+02:00")));
        assertTrue(helper.includes(toDateTime("2022-07-25T10:00:00+02:00")));
        assertTrue(helper.includes(toDateTime("2022-07-25T10:59:00+02:00")));

        assertFalse(helper.includes(toDateTime("2022-07-25T11:30:00+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T11:00:00+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T09:59:59+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T01:10:10+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T23:10:10+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T24:00:00+02:00")));
        assertFalse(helper.includes(toDateTime("2022-07-25T00:00:00+02:00")));
    }

    @Test
    public void testTimeRangeUtils6() {
        timerange("10:00 - 11:00").match();
        timerange("10:00 - 11:00, 14:00 - 17:00").match();
    }
}
