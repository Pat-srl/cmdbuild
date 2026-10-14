/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Maps.uniqueIndex;
import static java.lang.String.format;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import org.cmdbuild.config.api.ConfigDefinitionRepository;
import org.cmdbuild.config.api.ConfigEntry;
import org.cmdbuild.config.api.ConfigEntryImpl;
import org.cmdbuild.config.api.ConfigUpdate;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConfigRepositoryImpl implements ConfigRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final JdbcTemplate jdbcTemplate;
    private final ConfigDefinitionRepository configDefinitionRepository;

    public DatabaseConfigRepositoryImpl(@Qualifier(SYSTEM_LEVEL_ONE) JdbcTemplate jdbcTemplate, ConfigDefinitionRepository configDefinitionRepository) {
        this.jdbcTemplate = checkNotNull(jdbcTemplate);
        this.configDefinitionRepository = checkNotNull(configDefinitionRepository);
    }

    @Override
    public List<ConfigEntry> getConfigEntries() {
        Map<String, String> config = fromJson(jdbcTemplate.queryForObject("SELECT _cm3_system_config_get()", String.class), MAP_OF_STRINGS);
        logger.trace("loaded raw config from db = \n\n{}\n", mapToLoggableStringLazy(config));
        return config.entrySet().stream().map(e -> {
            ConfigEntry configEntry = new ConfigEntryImpl(e.getKey(), Cm3EasyCryptoUtils.decryptValue(e.getValue()), e.getValue(), Cm3EasyCryptoUtils.isEncrypted(e.getValue()));
            if (!configDefinitionRepository.containsConfigOrModuleConfig(configEntry.getKey()) && configEntry.getKey().startsWith("org.cmdbuild") && !configEntry.getKey().equals(DATABASE_EXT_CONFIG)) {
                logger.warn("found unknown config key = {} from db", configEntry.getKey());
                Instant configTimestamp = jdbcTemplate.queryForObject("SELECT \"BeginDate\" FROM \"_SystemConfig\" WHERE \"Code\" = ? AND \"Status\" = 'A'", Instant.class, configEntry.getKey());
                long daysFromNow = ChronoUnit.DAYS.between(configTimestamp, Instant.now());
                if (daysFromNow > 30) {
                    logger.warn("delete unknown config key = {} from db, older than 30 days ( {} days )", configEntry.getKey(), daysFromNow);
                    jdbcTemplate.execute(format("SELECT _cm3_system_config_delete('%s')", configEntry.getKey()));
                }
            }
            return configEntry;
        }).collect(toImmutableList());
    }

    @Override
    public void updateConfigs(List<? extends ConfigUpdate> configs) {
        if (!configs.isEmpty()) {
            checkArgument(configs.stream().allMatch(configDefinitionRepository::isLocationDefault));
            logger.trace("update config on db = \n\n{}\n", mapToLoggableStringLazy(uniqueIndex(configs, ConfigUpdate::getKey)));
            configs.stream().filter(ConfigUpdate::isDelete).map(ConfigUpdate::getKey).forEach(k -> {
                jdbcTemplate.execute(format("SELECT _cm3_system_config_delete('%s')", k));
            });
            configs.stream().filter(ConfigUpdate::isUpdate).map(ConfigEntry.class::cast).forEach(e -> {
                jdbcTemplate.execute(format("SELECT _cm3_system_config_set('%s','%s')", e.getKey(), e.getValue()));
            });
        }
    }

}
