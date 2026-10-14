package org.cmdbuild.config;

import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import org.springframework.stereotype.Component;

/**
 *
 * @author mbuzzulini
 */
@Component
@ConfigComponent("org.cmdbuild.subscription")
public class SubscriptionConfigurationImpl implements SubscriptionConfiguration {

    @ConfigValue(key = "customer.code", description = "customer code for subscription", defaultValue = "")
    private String customerCode;

    @Override
    public String getSubscriptionCustomerCode() { return customerCode; }

}
