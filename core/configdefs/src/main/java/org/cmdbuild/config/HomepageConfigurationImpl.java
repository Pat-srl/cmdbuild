package org.cmdbuild.config;

import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import org.springframework.stereotype.Component;

import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.HomepageConfiguration.HP_NAMESPACE;

@Component
@ConfigComponent(HP_NAMESPACE)
public class HomepageConfigurationImpl implements HomepageConfiguration {

    @ConfigValue(key = HP_ENABLED, description = "disable homepage context", defaultValue = FALSE)
    private Boolean isEnabled;

    @ConfigValue(key = HP_TYPE, description = "homepage type", defaultValue = "")
    private String type;

    @ConfigValue(key = HP_SEARCH_CLASSES, description = "homepage search classes", defaultValue = "")
    private String searchClasses;

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getSearchClasses() {
        return searchClasses;
    }
}
