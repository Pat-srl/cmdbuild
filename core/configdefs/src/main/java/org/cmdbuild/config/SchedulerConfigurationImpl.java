package org.cmdbuild.config;

import java.time.Duration;
import jakarta.annotation.Nullable;
import static org.cmdbuild.config.SchedulerConfigurationImpl.SCHEDULER_CONFIG_NAMESPACE;
import org.cmdbuild.config.api.ConfigComponent;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.TRUE;

@Component
@ConfigComponent(SCHEDULER_CONFIG_NAMESPACE)
public final class SchedulerConfigurationImpl implements SchedulerConfiguration {

    public static final String SCHEDULER_CONFIG_NAMESPACE = "org.cmdbuild.scheduler", SCHEDULER_CONFIG_ENABLED_KEY = "enabled", SCHEDULER_CONFIG_ENABLED_CLASSES = "selectableclasses";

    @ConfigValue(key = SCHEDULER_CONFIG_ENABLED_KEY, description = "enable scheduler service (run core scheduled jobs, and custom jobs, if they are enabled)", defaultValue = TRUE)
    private boolean isEnabled;

    @ConfigValue(key = SCHEDULER_CONFIG_ENABLED_CLASSES, defaultValue = "")
    private String selectableClasses;

    @ConfigValue(key = "cluster.balancingStrategy", description = "default is `pin_job_to_node`; this is less balanced, but avoid synchronization warnings when running overlapping (long running) jobs on cluster; use `auto` balancing to spread load", defaultValue = "pin_job_to_node")
    private ClusterBalancingStrategy clusterBalancingStrategy;

    @ConfigValue(key = "softTimeout", description = "soft job timeout (will print warning and send event if a job is still running after this time)", defaultValue = "PT40M")
    private Duration softTimeout;

    @ConfigValue(key = "hardTimeout", description = "hard job timeout (will print warning, send event and notification if a job is still running after this time)", defaultValue = "PT1H")
    private Duration hardTimeout;

    @ConfigValue(key = "timeout.enabled", description = "enable scheduler service timeout processing", defaultValue = TRUE)
    private boolean isTimeoutEnabled;

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String getSelectableClasses() {
        return selectableClasses;
    }

    @Override
    public ClusterBalancingStrategy getClusterBalancingStrategy() {
        return clusterBalancingStrategy;
    }

    @Override
    @Nullable
    public Duration getSoftTimeout() {
        return softTimeout;
    }

    @Override
    @Nullable
    public Duration getHardTimeout() {
        return hardTimeout;
    }

    @Override
    public boolean isTimeoutEnabled() {
        return isTimeoutEnabled;
    }

}
