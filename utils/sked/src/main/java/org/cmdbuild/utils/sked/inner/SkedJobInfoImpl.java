/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.sked.inner;

import jakarta.annotation.Nullable;
import java.time.ZonedDateTime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.cmdbuild.utils.sked.SkedJob;
import org.cmdbuild.utils.sked.SkedJobInfo;

public class SkedJobInfoImpl implements SkedJobInfo {

    private final SkedJob job;
    private final boolean isRunning;
    private final ZonedDateTime lastRun;

    public SkedJobInfoImpl(SkedJob job, boolean isRunning, ZonedDateTime lastRun) {
        this.job = checkNotNull(job);
        this.isRunning = isRunning;
        this.lastRun = lastRun;
    }

    @Override
    public SkedJob getJob() {
        return job;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    @Nullable
    public ZonedDateTime getLastRun() {
        return lastRun;
    }

}
