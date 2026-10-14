/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.scheduler;

import static com.google.common.base.Objects.equal;
import java.time.ZonedDateTime;
import org.cmdbuild.utils.sked.SkedJobClusterMode;
import org.springframework.lang.Nullable;

public interface ScheduledJobInfo {

    String getCode();

    String getTrigger();

    boolean isRunning();

    @Nullable
    ZonedDateTime getLastRun();

    SkedJobClusterMode getClusterMode();

    default boolean hasClusterMode(SkedJobClusterMode mode) {
        return equal(mode, getClusterMode());
    }
}
