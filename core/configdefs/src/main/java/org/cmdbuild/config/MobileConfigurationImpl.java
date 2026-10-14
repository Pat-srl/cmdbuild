package org.cmdbuild.config;

import java.time.Duration;
import jakarta.annotation.Nullable;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
@ConfigComponent("org.cmdbuild.mobile")
public class MobileConfigurationImpl implements MobileConfiguration {

    @ConfigValue(key = "enabled", description = "disable mobile context", defaultValue = FALSE)
    private Boolean isMobileEnabled;

    @ConfigValue(key = "devicename.prefix", description = "set devicename prefix", defaultValue = "")
    private String deviceNamePrefix;

    @ConfigValue(key = "notification.authInfo", description = "Firebase service account private key (a json including a format PKCS#8 PEM, the one with BEGIN PRIVATE KEY and END PRIVATE KEY)", defaultValue = "pax021d2vca0gggg1299s95p0kvskj0hgslvt14qlegj6braignkl542s5245k4046hmktjrh8t939i4slrfhbrtbbkedd6jtm8ohccvctursfp7glssj6d05tefljka846q08s7jlpu8hd9r6me4hb70q43msov2enb1ub8b8md00cbmjb5jq0qjvitg5gbujpe16trkniheq3enqrtgrtgblfuarabcofngdupf7l97c2d5ctmc30e6b9hii91k4csmfpl528300djeorl1eq3ovjttbk97ejg3sh8a2gnp6dtcogm2cvt0am6so5nu7n2ul6m3kq2l0s9si27qkh8o6l9gk899e4qco924d227ckds8365gmu5lkl0l6c92q7bnlovoeecc23ki2kltfcvmm94o3cisu1fvrr4mt3lrh03jkmig4nq2v7v7cp4dcj436g5ighpj26id4fgu28vkunvlardfjns0hj6cv14xap")
    private String notificationAuthInfo;

    @ConfigValue(key = "archivedMessageTimeToLive", description = "retention time for archived messages", defaultValue = "P30D") // 30 days
    private Duration archivedMessageTimeToLive;

    @Override
    public Boolean isMobileEnabled() {
        return isMobileEnabled;
    }

    @Override
    public String getMobileDeviceNamePrefix() {
        return deviceNamePrefix;
    }

    @Override
    public String getMobileNotificationAuthInfo() {
        return notificationAuthInfo;
    }

    @Override
    @Nullable
    public Duration getArchivedMessageTimeToLive() {
        return archivedMessageTimeToLive;
    }

}
