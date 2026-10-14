/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.userconfig;

import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.eventbus.EventBus;
import jakarta.annotation.Nullable;
import java.util.Map;
import static org.apache.commons.io.FileUtils.ONE_KB;
import static org.apache.commons.io.FileUtils.ONE_MB;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_USERS_MODIFY;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.eventbus.EventBusService;
import static org.cmdbuild.utils.io.CmImageUtils.checkIsImage;
import static org.cmdbuild.utils.lang.CmExceptionUtils.illegalArgument;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrNull;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.url.CmUrlUtils.isDataUrl;
import static org.cmdbuild.utils.url.CmUrlUtils.urlToByteArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserConfigServiceImpl implements UserConfigService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserConfigRepository configRepository;
    private final OperationUserSupplier operationUser;
    private final EventBus eventBus;

    public UserConfigServiceImpl(UserConfigRepository configRepository, OperationUserSupplier operationUser, EventBusService eventBusService) {
        this.configRepository = checkNotNull(configRepository);
        this.operationUser = checkNotNull(operationUser);
        eventBus = eventBusService.getContextEventBus();
    }

    @Override
    public Map<String, String> getByUsername(String username) {
        return configRepository.getByUsername(username);
    }

    @Override
    public void setByUsername(String username, Map<String, String> data) {
        validateConfig(data, operationUser.hasPrivileges(p -> p.hasPrivileges(RP_ADMIN_USERS_MODIFY)));
        Map<String, String> newConfigs = map(getByUsername(username)).with(data);
        setUserConfigRepository(username, newConfigs);
    }

    @Override
    public void setByUsername(String username, String key, @Nullable String value) {
        logger.info("set config by usename = {} key = {} value = {}", username, key, value);
        setByUsername(username, map(key, value));
    }

    @Override
    @Nullable
    public Optional<String> getByUsername(String username, String key) {
        logger.debug("get config by usename = {} key = {}", username, key);
        Map<String, String> config = getByUsername(username);
        return applyOrNull(config, c -> Optional.fromNullable(c.get(key)));
    }

    @Override
    public void deleteByUsername(String username, String key) {
        logger.info("delete config by usename = {} key = {}", username, key);
        Map<String, String> newConfigs = map(getByUsername(username)).withoutKey(key);
        setUserConfigRepository(username, newConfigs);
    }

    @Override
    @Nullable
    public String getForCurrentUsernameOrNull(String key) {
        return getByUsernameOrNull(operationUser.getUsername(), key);
    }

    @Override
    public Map<String, String> getForCurrentUsername() {
        return getByUsername(operationUser.getUsername());
    }

    @Override
    public void setForCurrent(String key, String value) {
        setByUsername(operationUser.getUsername(), key, value);
    }

    private void setUserConfigRepository(String username, Map<String, String> data) {
        configRepository.setByUsername(username, data);
        eventBus.post(UserConfigChangedEvent.INSTANCE);
    }

    private static void validateConfig(final Map<String, String> data, boolean hasUsersModifyPrivilege) {
        checkArgument(data.size() < 1000, "CMO 150: too many user config values");
        data.forEach((k, v) -> {
            try {
                checkArgument(nullToEmpty(k).length() < ONE_KB, "CMO 150: invalid config key");
                checkArgument(nullToEmpty(v).length() < ONE_MB, "CMO 150: config value too big");
                checkArgument(!k.startsWith("cm_") || hasUsersModifyPrivilege || ALLOWLIST_CONFIG.contains(k), "CMO 150: cannot set system preference =< %s >", k);
                switch (k) {
                    case "icon" -> {
                        if (!isBlank(v)) {
                            checkArgument(isDataUrl(v), "CMO 150: invalid value format");
                            byte[] bytes = urlToByteArray(v);
                            checkIsImage(bytes);
                        }
                    }
                }
            } catch (Exception ex) {
                throw illegalArgument(ex, "invalid user config for key =< %s >", abbreviate(k));
            }
        });
    }
}
