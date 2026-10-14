/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.services;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.dao.DaoException;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.cmdbuild.cache.CmCache;

@Component
public class LookupDescriptionServiceImpl implements LookupDescriptionService {

    private final JdbcTemplate jdbcTemplate;
    private final CmCache<String> cache;

    public LookupDescriptionServiceImpl(@Qualifier(SYSTEM_LEVEL_ONE) JdbcTemplate jdbcTemplate, CacheService cacheService) {
        this.jdbcTemplate = checkNotNull(jdbcTemplate);
        cache = cacheService.newCache("lookup_description_by_id", CacheConfig.SYSTEM_OBJECTS);
    }

    @Override
    public String getDescription(long lookupId) {
        try {
            return cache.get(lookupId, () -> doGetDescription(lookupId));
        } catch (Exception ex) {
            throw new DaoException(ex, "error retrieving lookup description for id = %s", lookupId);
        }
    }

    private String doGetDescription(long lookupId) {
        return checkNotNull(jdbcTemplate.queryForObject("SELECT \"Description\" FROM \"LookUp\" WHERE \"Id\" = ? AND \"Status\" = 'A'", String.class, lookupId));
    }

    @Override
    public void invalidateCache() {
        cache.invalidateAll();
    }

}
