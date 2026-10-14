/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.scheduler;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

import java.time.ZonedDateTime;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import org.cmdbuild.utils.sked.SkedJobClusterMode;
import org.springframework.lang.Nullable;

public class ScheduledJobInfoImpl implements ScheduledJobInfo {

    private final String code, trigger;
    private final boolean running;
    private final ZonedDateTime lastRun;
    private final SkedJobClusterMode clusterMode;

    private ScheduledJobInfoImpl(ScheduledJobInfoImplBuilder builder) {
        this.code = checkNotBlank(builder.code);
        this.trigger = checkNotBlank(builder.trigger);
        this.running = firstNotNull(builder.running, false);
        this.lastRun = builder.lastRun;
        this.clusterMode = checkNotNull(builder.clusterMode);
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getTrigger() {
        return trigger;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    @Nullable
    public ZonedDateTime getLastRun() {
        return lastRun;
    }

    @Override
    public SkedJobClusterMode getClusterMode() {
        return clusterMode;
    }

    @Override
    public String toString() {
        return "ScheduledJobInfo{" + "code=" + code + '}';
    }

    public static ScheduledJobInfoImplBuilder builder() {
        return new ScheduledJobInfoImplBuilder();
    }

    public static ScheduledJobInfoImplBuilder copyOf(ScheduledJobInfo source) {
        return new ScheduledJobInfoImplBuilder()
                .withCode(source.getCode())
                .withTrigger(source.getTrigger())
                .withRunning(source.isRunning())
                .withLastRun(source.getLastRun())
                .withClusterMode(source.getClusterMode());
    }

    public static class ScheduledJobInfoImplBuilder implements Builder<ScheduledJobInfoImpl, ScheduledJobInfoImplBuilder> {

        private String code;
        private String trigger;
        private Boolean running;
        private ZonedDateTime lastRun;
        private SkedJobClusterMode clusterMode;

        public ScheduledJobInfoImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public ScheduledJobInfoImplBuilder withTrigger(String trigger) {
            this.trigger = trigger;
            return this;
        }

        public ScheduledJobInfoImplBuilder withRunning(Boolean running) {
            this.running = running;
            return this;
        }

        public ScheduledJobInfoImplBuilder withLastRun(ZonedDateTime lastRun) {
            this.lastRun = lastRun;
            return this;
        }

        public ScheduledJobInfoImplBuilder withClusterMode(SkedJobClusterMode clusterMode) {
            this.clusterMode = clusterMode;
            return this;
        }

        @Override
        public ScheduledJobInfoImpl build() {
            return new ScheduledJobInfoImpl(this);
        }

    }
}
