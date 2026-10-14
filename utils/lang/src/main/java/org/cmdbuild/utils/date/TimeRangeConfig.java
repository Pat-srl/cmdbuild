package org.cmdbuild.utils.date;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

public interface TimeRangeConfig {

    ZoneId getTimezoneId();

    Map<String, TimeRangeSet> getTimeRangeSets();

    interface TimeRangeSet {

        String getName();

        List<TimeRangeEntry> getEntries();

    }

    interface TimeRangeEntry {

        LocalTime getFrom();

        LocalTime getTo();

    }

}
