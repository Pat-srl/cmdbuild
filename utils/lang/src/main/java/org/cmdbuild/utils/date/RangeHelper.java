package org.cmdbuild.utils.date;

import java.time.ZonedDateTime;

public interface RangeHelper {

    boolean includes(ZonedDateTime timestamp);

    default boolean isActive() {
        return includes(CmDateUtils.now());
    }

    default boolean match() {
        return isActive();
    }

}
