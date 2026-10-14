package org.cmdbuild.utils.date.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import org.cmdbuild.utils.date.RangeHelper;
import org.cmdbuild.utils.date.TimeRangeConfig;
import org.cmdbuild.utils.date.TimeRangeConfig.TimeRangeSet;
import org.cmdbuild.utils.date.TimeRangeHelper;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class TimeRangeHelperImpl implements TimeRangeHelper {

    private final TimeRangeConfig config;

    public TimeRangeHelperImpl(TimeRangeConfig config) {
        this.config = checkNotNull(config);
    }

    @Override
    public RangeHelper range(String name) {
        return new RangeHelperImpl(checkNotNull(config.getTimeRangeSets().get(checkNotBlank(name)), "range not found for name =< %s >", name));
    }

    @Override
    public RangeHelper range() {
        return config.getTimeRangeSets().size() == 1 ? new RangeHelperImpl(getOnlyElement(config.getTimeRangeSets().values())) : range(TIMERANGE_DEFAULT);
    }

    private class RangeHelperImpl implements RangeHelper {

        final TimeRangeSet range;

        public RangeHelperImpl(TimeRangeSet range) {
            this.range = checkNotNull(range);
        }

        @Override
        public boolean includes(ZonedDateTime timestamp) {
            LocalTime localTime = LocalTime.ofInstant(timestamp.toInstant(), config.getTimezoneId());
            return range.getEntries().stream().anyMatch(e -> !localTime.isBefore(e.getFrom()) && localTime.isBefore(e.getTo()));
        }

    }

}
