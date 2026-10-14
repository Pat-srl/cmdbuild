/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import com.google.common.collect.Ordering;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.config.api.GlobalConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;

/**
 * @author ldare
 */
@Component
public class SystemConfigWsCommand {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CoreConfiguration coreConfiguration;
    private final GlobalConfigService globalConfigService;

    public SystemConfigWsCommand(GlobalConfigService globalConfigService, CoreConfiguration coreConfiguration) {
        this.globalConfigService = checkNotNull(globalConfigService);
        this.coreConfiguration = checkNotNull(coreConfiguration);
    }

    public Set<String> doGetSystemConfigAll(Map<String, String> storedConfigs) {
        return set(globalConfigService.getConfigDefinitions().keySet()).with(storedConfigs.keySet()).stream().sorted(Ordering.natural()).collect(Collectors.toSet());
    }

    public String doGetSystemConfigValue(String key, Boolean includeDefault) {
        String value;
        if (includeDefault) {
            value = globalConfigService.getStringOrDefault(key);
        } else {
            value = globalConfigService.getString(key);
        }
        return value;
    }

    public void doUpdateSystemConfigValue(String key, String value, Boolean encrypt) {
        checkCanEdit();
        checkNotProtected(singleton(key));
        if (equal(value, "default")) {
            logger.info("delete system config by key = {}", key);
            globalConfigService.delete(key);
        } else {
            logger.info("update system config for key =< {} > value =< {} >", key, value);
            globalConfigService.putString(key, value, firstNonNull(encrypt, false));
        }
    }

    public void doDeleteSystemConfigValue(String key) {
        checkCanEdit();
        checkNotProtected(singleton(key));
        logger.info("delete system config by key = {}", key);
        globalConfigService.delete(key);
    }

    public void doReloadConfig() {
        logger.info("reload config");
        globalConfigService.reload();
    }

    public void doUpdateSystemConfigValues(Map<String, String> data) {
        checkCanEdit();
        checkNotProtected(data.keySet());
        logger.info("update system config with data = {}", data);
        globalConfigService.putStrings(data);
    }

    private void checkCanEdit() {
        checkArgument(coreConfiguration.allowConfigUpdateViaWs(), "CM_CUSTOM_EXCEPTION: system configuration update is disabled for this instance (demo mode)");//TODO check message
    }

    private void checkNotProtected(Set<String> keys) {
        keys = keys.stream().filter(globalConfigService::isProtected).collect(toSet());
        checkArgument(keys.isEmpty(), "you are not allowed to manually update these protected config params = %s : operation not allowed", keys);
    }
}
