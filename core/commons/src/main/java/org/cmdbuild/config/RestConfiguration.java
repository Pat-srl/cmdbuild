package org.cmdbuild.config;

/**
 *
 * @author ataboga
 */
public interface RestConfiguration {

    final static String REST_CONFIG_NAMESPACE = "org.cmdbuild.services.rest",
            REST_V2_CONFIG_ENABLED = "v2.enabled";

    boolean isRestV2Enabled();

}
