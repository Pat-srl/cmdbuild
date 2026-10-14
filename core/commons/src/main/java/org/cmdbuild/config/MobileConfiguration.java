package org.cmdbuild.config;

import java.time.Duration;
import jakarta.annotation.Nullable;

/**
 *
 * @author ataboga
 */
public interface MobileConfiguration {

    Boolean isMobileEnabled();

    String getMobileDeviceNamePrefix();

    String getMobileNotificationAuthInfo();

    @Nullable
    Duration getArchivedMessageTimeToLive();

}
