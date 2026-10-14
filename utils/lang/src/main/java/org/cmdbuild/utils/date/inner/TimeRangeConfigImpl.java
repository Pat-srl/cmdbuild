package org.cmdbuild.utils.date.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import static java.util.function.Function.identity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cmdbuild.utils.date.TimeRangeConfig;
import static org.cmdbuild.utils.date.TimeRangeHelper.TIMERANGE_DEFAULT;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import org.cmdbuild.utils.lang.CmCollectionUtils.FluentList;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public class TimeRangeConfigImpl implements TimeRangeConfig {

    private final ZoneId zoneId;
    private final Map<String, TimeRangeSet> timeRangeSets;

    public TimeRangeConfigImpl(ZoneId zoneId, List<TimeRangeSet> timeRangeSets) {
        this.zoneId = checkNotNull(zoneId);
        this.timeRangeSets = map(timeRangeSets, TimeRangeSet::getName, identity()).immutable();
    }

    @Override
    public ZoneId getTimezoneId() {
        return zoneId;
    }

    @Override
    public Map<String, TimeRangeSet> getTimeRangeSets() {
        return timeRangeSets;
    }

    public static TimeRangeConfig parse(String config) {
        Matcher matcher = Pattern.compile("(([^|]+)[|]\\s*)?(((([0-9:]+)\\s*-\\s*([0-9:]+))(\\s*,\\s*)?)+)").matcher(trimAndCheckNotBlank(config));
        if (matcher.matches()) {
            ZoneId timezoneId;
            String expr;
            if (isBlank(matcher.group(2))) {
                timezoneId = ZoneId.systemDefault();
                expr = config;
            } else {
                timezoneId = ZoneId.of(matcher.group(2));
                expr = checkNotBlank(matcher.group(3));
            }
            return new TimeRangeConfigImpl(timezoneId, list(new TimeRangeSetImpl(TIMERANGE_DEFAULT, listOf(TimeRangeEntry.class).accept(list -> {
                Matcher m = Pattern.compile("(([0-9:]+) *- *([0-9:]+))").matcher(trimAndCheckNotBlank(expr));
                while (m.find()) {
                    list.add(new TimeRangeEntryImpl(LocalTime.parse(checkNotBlank(m.group(2))), LocalTime.parse(checkNotBlank(m.group(3)))));
                }
            }))));
        } else {
            Map<String, Object> map = fromJson(config, MAP_OF_OBJECTS);
            ZoneId zoneId = ZoneId.of(firstNotBlank(toStringOrNull(map.get("timezone")), ZoneId.systemDefault().toString()));
            Object timerange = map.get("range");
            List<TimeRangeSet> timeRangeSets = ((FluentList<Map>) (timerange instanceof Collection ? list((Collection) timerange) : list(timerange != null ? timerange : map))).map(r -> new TimeRangeSetImpl(firstNotBlank(toStringOrNull(r.get("name")), TIMERANGE_DEFAULT),
                    ((FluentList<Map>) (r.containsKey("entries") ? list((Collection) r.get("entries")) : list(r))).map((Map e) -> new TimeRangeEntryImpl(LocalTime.parse(toStringNotBlank(e.get("from"))), LocalTime.parse(toStringNotBlank(e.get("to")))))));
            return new TimeRangeConfigImpl(zoneId, timeRangeSets);
        }
    }

    private static class TimeRangeSetImpl implements TimeRangeSet {

        private final String name;
        private final List<TimeRangeEntry> entries;

        public TimeRangeSetImpl(String name, List<TimeRangeEntry> entries) {
            this.name = checkNotBlank(name);
            this.entries = ImmutableList.copyOf(entries);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<TimeRangeEntry> getEntries() {
            return entries;
        }

    }

    private static class TimeRangeEntryImpl implements TimeRangeEntry {

        private final LocalTime from, to;

        public TimeRangeEntryImpl(LocalTime from, LocalTime to) {
            this.from = checkNotNull(from);
            this.to = checkNotNull(to);
        }

        @Override
        public LocalTime getFrom() {
            return from;
        }

        @Override
        public LocalTime getTo() {
            return to;
        }

    }

}
