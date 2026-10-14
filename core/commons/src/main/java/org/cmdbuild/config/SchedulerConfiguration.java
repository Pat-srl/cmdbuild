package org.cmdbuild.config;

import java.time.Duration;
import jakarta.annotation.Nullable;

public interface SchedulerConfiguration {

    boolean isEnabled();

    String getSelectableClasses();

    ClusterBalancingStrategy getClusterBalancingStrategy();

    boolean isTimeoutEnabled();

    @Nullable
    Duration getSoftTimeout();

    @Nullable
    Duration getHardTimeout();

}
