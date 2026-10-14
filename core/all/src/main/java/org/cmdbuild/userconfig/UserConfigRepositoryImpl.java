/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.userconfig;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collections;
import java.util.Map;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.cache.CmCache;

@Component
public class UserConfigRepositoryImpl implements UserConfigRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final CmCache<Map<String, String>> configByUsername;

    public UserConfigRepositoryImpl(DaoService dao, CacheService cacheService) {
        this.dao = checkNotNull(dao);
        configByUsername = cacheService.newCache("user_preferences_by_username", CacheConfig.SYSTEM_OBJECTS);
    }

    @Override
    public Map<String, String> getByUsername(String username) {
        checkNotBlank(username);
        return configByUsername.get(username, () -> doGetByUsername(username));
    }

    @Override
    public void setByUsername(String username, Map<String, String> data) {
        checkNotBlank(username);
        checkNotNull(data);
        dao.getJdbcTemplate().queryForObject("SELECT _cm3_user_config_set(?,?::jsonb)", Object.class, username, toJson(data));
        configByUsername.invalidate(username);
    }

    private Map<String, String> doGetByUsername(String username) {
        logger.debug("get config by usename = {}", username);
        return Collections.unmodifiableMap(fromJson(dao.getJdbcTemplate().queryForObject("SELECT _cm3_user_config_get(?)", String.class, username), MAP_OF_STRINGS));
    }

}
