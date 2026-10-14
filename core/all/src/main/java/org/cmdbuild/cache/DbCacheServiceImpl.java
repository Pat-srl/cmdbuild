/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cache;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import org.cmdbuild.scheduler.ScheduledJob;
import static org.cmdbuild.utils.sked.SkedJobClusterMode.RUN_ON_SINGLE_NODE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DbCacheServiceImpl {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;

    public DbCacheServiceImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @ScheduledJob(value = "0 */10 * * * ?", clusterMode = RUN_ON_SINGLE_NODE, persistRun = false) //run every 10 minutes
    public void removeExpiredRecords() {
        dao.getJdbcTemplate().update(format("DELETE FROM \"_Cache\" WHERE %s > \"BeginDate\" + format('%%s seconds', \"TimeToLive\")::interval", systemToSqlExpr(now())));
    }

}
