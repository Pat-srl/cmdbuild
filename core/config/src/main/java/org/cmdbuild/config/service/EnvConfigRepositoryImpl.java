/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.service;

import static com.google.common.collect.Maps.filterKeys;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import org.cmdbuild.config.api.ConfigEntry;
import org.cmdbuild.config.api.ConfigEntryImpl;
import org.cmdbuild.config.api.ConfigUpdate;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.url.CmUrlUtils.decodeUrlParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EnvConfigRepositoryImpl implements ConfigRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<ConfigEntry> getConfigEntries() {
        Map<String, String> sysProp = map(System.getenv()).filterKeys(k -> k.startsWith("org.cmdbuild.")).accept(m -> {
            filterKeys(System.getenv(), k -> k.toUpperCase().startsWith("CMDBUILD_CONFIG")).forEach((k, v) -> {
                try {
                    decodeUrlParams(v).forEach(m::put);
                } catch (Exception ex) {
                    logger.warn(marker(), "unable to process system env config with key =< {} > and value =< {} >", k, v, ex);
                }
            });
        }), sysEnv = map(System.getProperties()).filterKeys(k -> k.startsWith("org.cmdbuild."));
        logger.debug("system env = \n\n{}\n", mapToLoggableStringLazy(sysEnv));
        logger.debug("system properties = \n\n{}\n", mapToLoggableStringLazy(sysProp));
        List<ConfigEntry> configs = map(sysEnv).with(sysProp).entrySet().stream().map(ConfigEntryImpl::new).collect(toList());
        configs.forEach(c -> {
            logger.info("load system env/prop config {} = {}", c.getKey(), c.getValue());
        });
        return configs;
    }

    @Override
    public void updateConfigs(List<? extends ConfigUpdate> configs) {
        throw new UnsupportedOperationException("env config update not supported");
    }

}
