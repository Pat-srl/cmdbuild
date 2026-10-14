package org.cmdbuild.config;

import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.bugreport")
public class BugreportConfigurationImpl implements BugreportConfiguration {

    @ConfigValue(key = "url", description = "bugreport endpoint url", defaultValue = "http://team.cmdbuild.org/bugreportcollector/bugreport")
    private String url;

    @Override
    public String getBugreportEndpoint() {
        return url;
    }
}
