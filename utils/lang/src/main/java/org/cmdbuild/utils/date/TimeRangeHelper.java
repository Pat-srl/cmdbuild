package org.cmdbuild.utils.date;

import java.time.ZonedDateTime;

public interface TimeRangeHelper extends RangeHelper {

    final String TIMERANGE_DEFAULT = "default";

    RangeHelper range(String name);

    RangeHelper range();

    @Override
    default boolean includes(ZonedDateTime timestamp) {
        return range().includes(timestamp);
    }

}
