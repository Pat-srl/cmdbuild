/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.system;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.dao.event.DaoEvent;
import org.cmdbuild.eventbus.EventBusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReloadServiceImpl implements ReloadService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final GlobalConfigService configService;
    private final CacheService cacheService;
    private final JdbcTemplate jdbcTemplate;
    private final EventBus eventBus;

    public ReloadServiceImpl(GlobalConfigService configService, CacheService cacheService, JdbcTemplate jdbcTemplate, EventBusService eventBusService) {
        this.configService = checkNotNull(configService);
        this.cacheService = checkNotNull(cacheService);
        this.jdbcTemplate = checkNotNull(jdbcTemplate);
        eventBus = eventBusService.getDaoEventBus();
        eventBus.register(new Object() {
            @Subscribe
            public void handleDaoEvent(DaoEvent event) {
                refreshStructureCache();
            }
        });
    }

    @Override
    public void reload() {
        logger.debug("reload system");
        configService.reload();
        cacheService.invalidateAll();
    }

    @Override
    public void refreshAndReload() {
        logger.debug("refresch and reload system");
        refreshStructureCache();
        reload();
    }

    private void refreshStructureCache() {
        //logger.debug("refresh structure cache");
        //jdbcTemplate.execute("SELECT _cm3_structure_cache_refresh();");
    }
}
