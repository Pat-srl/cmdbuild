package org.cmdbuild.bim;

import static org.cmdbuild.bim.utils.BimConfigUtils.BIM_CONFIG_NAMESPACE;
import org.cmdbuild.config.BimConfiguration;
import static org.cmdbuild.config.api.ConfigCategory.CC_ENV;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link BimConfiguration} for managing BIM-related
 * configuration properties.
 * <p>
 * This component provides access to BIM feature flags, viewer selection, and
 * IFC-to-XKT conversion service settings. Configuration values are injected via
 * {@link ConfigValue} annotations and can be customized through environment
 * variables or configuration files.
 * </p>
 *
 * <p>
 * Main configuration properties:
 * <ul>
 * <li><b>isEnabled</b>: Whether BIM features are enabled</li>
 * <li><b>viewer</b>: The BIM viewer to use (e.g., ifc2xkt)</li>
 * <li><b>ifc2xktTimeout</b>: Timeout for IFC-to-XKT conversion (in
 * seconds)</li>
 * <li><b>ifc2xktUrl</b>: URL of the IFC-to-XKT conversion service</li>
 * <li><b>ifc2xktUsername</b>: Username for the conversion service</li>
 * <li><b>ifc2xktPassword</b>: Password for the conversion service</li>
 * </ul>
 * </p>
 *
 * <p>
 * This class is registered as a Spring component and a configuration component
 * for the BIM namespace.
 * </p>
 */
@Component
@ConfigComponent(BIM_CONFIG_NAMESPACE)
public final class BimConfigurationImpl implements BimConfiguration {

    /**
     * Indicates whether BIM features are enabled.
     */
    @ConfigValue(key = BIM_CONFIG_ENABLED, description = "bim enabled", defaultValue = FALSE)
    private boolean isEnabled;

    /**
     * Timeout for IFC-to-XKT conversion, expressed in seconds.
     */
    @ConfigValue(key = "ifc2xkt.timeout", description = "timeout for ifc to xkt conversion expressed in seconds", defaultValue = "600")
    private Long ifc2xktTimeout;

    /**
     * URL of the IFC-to-XKT conversion service.
     */
    @ConfigValue(key = "ifc2xkt.url", defaultValue = "http://localhost:8080/ifc2xkt/api/v1", category = CC_ENV)
    private String ifc2xktUrl;

    /**
     * Username for the IFC-to-XKT conversion service.
     */
    @ConfigValue(key = "ifc2xkt.username", defaultValue = "admin", category = CC_ENV)
    private String ifc2xktUsername;

    /**
     * Password for the IFC-to-XKT conversion service.
     */
    @ConfigValue(key = "ifc2xkt.password", defaultValue = "password", category = CC_ENV)
    private String ifc2xktPassword;

    /**
     * @return Returns a boolean indicating whether BIM features are enabled.
     */
    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * @return Returns the timeout for IFC-to-XKT conversion (in seconds).
     */
    @Override
    public Long getTimeout() {
        return ifc2xktTimeout;
    }

    /**
     * @return Returns the URL of the IFC-to-XKT conversion service.
     */
    @Override
    public String getUrl() {
        return ifc2xktUrl;
    }

    /**
     * @return Returns the username for the IFC-to-XKT conversion service.
     */
    @Override
    public String getUsername() {
        return ifc2xktUsername;
    }

    /**
     * @return Returns the password for the IFC-to-XKT conversion service.
     */
    @Override
    public String getPassword() {
        return ifc2xktPassword;
    }

}
