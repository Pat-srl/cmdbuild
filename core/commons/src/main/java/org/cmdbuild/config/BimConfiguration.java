package org.cmdbuild.config;

import org.cmdbuild.utils.http.ExtServiceConfiguration;

public interface BimConfiguration extends ExtServiceConfiguration {

    public static final String BIM_CONFIG_NAMESPACE = "org.cmdbuild.bim",
            BIM_CONFIG_ENABLED = "enabled";

    boolean isEnabled();
}
