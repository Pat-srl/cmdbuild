/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.common.serializationhelpers;


import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.userconfig.UserConfigService;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;

/**
 * @author ldare
 */
@Component
public class UserConfigSerializationHelper {

    private final UserConfigService userPreferencesStore;
    private final CoreConfiguration coreConfiguration;

    public UserConfigSerializationHelper(UserConfigService userPreferencesStore, CoreConfiguration coreConfiguration) {
        this.userPreferencesStore = userPreferencesStore;
        this.coreConfiguration = coreConfiguration;
    }

    public Map<String, Object> getUserConfig(OperationUser operationUser) {
        Map<String, String> userConfig = userPreferencesStore.getByUsername(operationUser.getUsername());//TODO merge lang and starting class from here
        String initialPage = firstNotBlankOrNull(userConfig.get("cm_ui_startingClass"), userConfig.get("cm_user_initialPage"));
        if (isBlank(initialPage) && operationUser.hasDefaultGroup() && operationUser.getDefaultGroup().hasStartingClass()) {
            initialPage = operationUser.getDefaultGroup().getStartingClass();
        }
        if (isBlank(initialPage)) {
            initialPage = coreConfiguration.getStartingClassName();
        }
        return (Map) map()
                .with(userConfig)
                .accept((m) -> {
                    if (operationUser.hasDefaultGroup()) {
                        m.put(
                                //								"cm_ui_disabledModules", configuration.getDisabledModules(),
                                //								"cm_ui_disabledCardTabs", configuration.getDisabledCardTabs(),
                                //								"cm_ui_disabledProcessTabs", configuration.getDisabledProcessTabs(),
                                //								"cm_ui_hideSidePanel", configuration.isHideSidePanel(),
                                //								"cm_ui_fullScreenMode", configuration.isFullScreenMode(),
                                //								"cm_ui_simpleHistoryModeForCard", configuration.isSimpleHistoryModeForCard(),
                                //								"cm_ui_simpleHistoryModeForProcess", configuration.isSimpleHistoryModeForProcess(),
                                "cm_ui_processWidgetAlwaysEnabled", operationUser.getDefaultGroup().getConfig().getProcessWidgetAlwaysEnabled());
                    }
                })
                .with("_cm_ui_startingClass_actual", initialPage);
    }
}
