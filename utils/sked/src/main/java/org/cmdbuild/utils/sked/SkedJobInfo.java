/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.sked;

import java.time.ZonedDateTime;
import jakarta.annotation.Nullable;

public interface SkedJobInfo {

    SkedJob getJob();

    boolean isRunning();

    @Nullable
    ZonedDateTime getLastRun();

    default String getCode() {
        return getJob().getCode();
    }

    default SkedJobClusterMode getClusterMode() {
        return getJob().getClusterMode();
    }
}
