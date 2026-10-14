/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.service;

import java.io.InputStream;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.config.api.ConfigEntry;
import org.cmdbuild.config.api.ConfigEntryImpl;
import org.cmdbuild.config.api.ConfigUpdate;
import static org.cmdbuild.utils.io.CmPropertyUtils.loadProperties;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultConfigRepositoryImpl implements ConfigRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<ConfigEntry> getConfigEntries() {
        InputStream inputStream = getClass().getResourceAsStream("/org/cmdbuild/config/default_config.properties");
        if (inputStream != null) {
            Map<String, String> configs = loadProperties(inputStream);
            logger.debug("default configs = \n\n{}\n", mapToLoggableStringLazy(configs));
            return configs.entrySet().stream().map(ConfigEntryImpl::new).collect(toList());
        } else {
            logger.warn("default configs not found");
            return emptyList();
        }
    }

    @Override
    public void updateConfigs(List<? extends ConfigUpdate> configs) {
        throw new UnsupportedOperationException("default config update not supported");
    }

}
