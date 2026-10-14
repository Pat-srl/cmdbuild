/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.userconfig;

import com.google.common.base.Optional;
import java.util.Map;
import java.util.Set;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import org.cmdbuild.utils.lang.CmMapUtils;

public interface UserConfigService {

    static final String USER_TIME_ZONE = "cm_ui_timezone";

    static final Set<String> ALLOWLIST_CONFIG = set(
            "cm_ui_preferredFileCharset",
            "cm_ui_startingClass",
            "cm_ui_preferredOfficeSuite",
            "cm_user_language",
            "cm_ui_dateFormat",
            "cm_ui_timezone",
            "cm_ui_decimalsSeparator",
            "cm_ui_timeFormat",
            "cm_ui_thousandsSeparator",
            "cm_ui_gridsconfig",
            "cm_ui_preferredCsvSeparator",
            "cm_ui_startDay",
            "cm_ui_notifications_sound_enabled",
            "cm_ui_notifications_sound_volume",
            "cm_ui_email_groupByStatus",
            "cm_ui_email_defaultDelay",
            "cm_ui_preferredMenu",
            "cm_ui_preferredMapLayer",
            "cm_ui_preferredMapLabelSize"
    );

    Map<String, String> getByUsername(String username);

    void setByUsername(String username, Map<String, String> data);

    void setByUsername(String username, String key, @Nullable String value);

    @Nullable
    Optional<String> getByUsername(String username, String key);

    void deleteByUsername(String username, String key);

    @Nullable
    String getForCurrentUsernameOrNull(String key);

    Map<String, String> getForCurrentUsername();

    void setForCurrent(String key, String value);

    default void setForCurrent(String... values) {
        setForCurrent(CmMapUtils.<String, String, String>map(values));
    }

    default void setForCurrent(Map<String, String> values) {
        values.forEach(this::setForCurrent);
    }

    default void setByUsernameDeleteIfNull(String username, String key, @Nullable String value) {
        if (value == null) {
            deleteByUsername(username, key);
        } else {
            setByUsername(username, key, value);
        }
    }

    @Nullable
    default String getByUsernameOrNull(String username, String key) {
        Optional<String> optional = getByUsername(username, key);
        return optional == null ? null : optional.orNull();
    }

    default void updateByUsername(String username, Map<String, String> data) {
        setByUsername(username, data);
    }

    enum UserConfigChangedEvent {
        INSTANCE;
    }

}
