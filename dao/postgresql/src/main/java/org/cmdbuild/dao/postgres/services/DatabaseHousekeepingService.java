/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.services;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.scheduler.ScheduledJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.sked.SkedJobClusterMode.RUN_ON_SINGLE_NODE;
import org.cmdbuild.minions.PostStartup;

@Component
public class DatabaseHousekeepingService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CoreConfiguration config;
    private final DaoService dao;

    public DatabaseHousekeepingService(CoreConfiguration coreConfiguration, DaoService dao) {
        this.dao = checkNotNull(dao);
        this.config = checkNotNull(coreConfiguration);
    }

    @PostStartup
    public void runDatabaseHousekeepingAtStartup() {
        if (config.runDatabaseHousekeepingFunctionAtStartup()) {
            doRunDatabaseHousekeeping();
        }
    }

    @ScheduledJob(value = "0 0 4 * * ?", clusterMode = RUN_ON_SINGLE_NODE) //run every day at 4 am
    public void runDatabaseHousekeepingJob() {
        if (config.runDatabaseHousekeepingFunctionDaily()) {
            doRunDatabaseHousekeeping();
        } else {
            logger.debug("daily database housekeeping disabled, skipping");
        }
    }

    private synchronized void doRunDatabaseHousekeeping() {
        logger.info("execute database housekeeping function");
        try {
            dao.getJdbcTemplate().queryForObject("SELECT _cm3_system_housekeeping()", Object.class);
            logger.debug("database housekeeping completed");
        } catch (Exception ex) {
            logger.error("database housekeeping error", ex);
        }
    }

}
