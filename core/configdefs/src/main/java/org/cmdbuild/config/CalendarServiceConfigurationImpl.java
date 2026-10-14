package org.cmdbuild.config;

import static org.cmdbuild.config.CalendarServiceConfigurationImpl.CALENDAR_CONFIG_NAMESPACE;
import org.cmdbuild.config.api.ConfigComponent;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.TRUE;

@Component
@ConfigComponent(CALENDAR_CONFIG_NAMESPACE)
public final class CalendarServiceConfigurationImpl implements CalendarServiceConfiguration {

    public static final String CALENDAR_CONFIG_NAMESPACE = "org.cmdbuild.calendar";

    @ConfigValue(key = CALENDAR_CONFIG_NAMESPACE + ".enabled", description = "enable calendar service", defaultValue = TRUE)
    private boolean isEnabled;

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

}
