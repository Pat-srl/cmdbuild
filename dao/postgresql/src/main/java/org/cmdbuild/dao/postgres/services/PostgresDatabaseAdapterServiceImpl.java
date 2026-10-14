/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.services;

import org.cmdbuild.dao.postgres.utils.ConnectionWrapperDataSource;
import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static java.lang.String.format;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import static java.util.Collections.emptySet;
import java.util.Set;
import javax.sql.DataSource;
import org.cmdbuild.dao.driver.DatabaseAccessConfig;
import org.cmdbuild.dao.driver.DatabaseAccessUserContext;
import org.cmdbuild.dao.postgres.utils.MyJdbcTemplate;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowBiConsumer;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.cmdbuild.dao.postgres.services.PostgresDatabaseAdapterService.PG_OPERATION_ROLE;
import static org.cmdbuild.dao.postgres.services.PostgresDatabaseAdapterService.PG_OPERATION_SESSION;
import static org.cmdbuild.dao.postgres.services.PostgresDatabaseAdapterService.PG_OPERATION_SCOPE;

public class PostgresDatabaseAdapterServiceImpl implements PostgresDatabaseAdapterService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final DatabaseAccessConfig databaseAccessConfig;
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public PostgresDatabaseAdapterServiceImpl(DataSource innerDataSource, DatabaseAccessConfig databaseAccessConfig) {
        this.databaseAccessConfig = checkNotNull(databaseAccessConfig);
        this.dataSource = new TenantAwareDataSource(checkNotNull(innerDataSource));
        this.jdbcTemplate = new MyJdbcTemplate(this.dataSource);
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    private class TenantAwareDataSource extends ConnectionWrapperDataSource {

        public TenantAwareDataSource(DataSource dataSource) {
            super(dataSource);
        }

        @Override
        protected void prepareConnection(Connection connection) throws SQLException {
            boolean ignoreTenantPolicies;
            Set<Long> tenantIds;
            DatabaseAccessUserContext userContext = databaseAccessConfig.getUserContext();
            if (databaseAccessConfig.getMultitenantConfiguration().isMultitenantDisabled()) {
                ignoreTenantPolicies = true;
                tenantIds = emptySet();
            } else {
                ignoreTenantPolicies = userContext.ignoreTenantPolicies();
                tenantIds = ignoreTenantPolicies ? emptySet() : userContext.getTenantIds();
            }
            logger.trace("ignoreTenantPolicies = {} tenantIds = {}", ignoreTenantPolicies, tenantIds);
            map(PG_OPERATION_USER, nullToEmpty(userContext.getUsername()),
                    PG_OPERATION_ROLE, nullToEmpty(userContext.getRolename()),
                    PG_OPERATION_SESSION, nullToEmpty(userContext.getSessionId()),
                    PG_OPERATION_SCOPE, serializeEnum(userContext.getScope()),
                    PG_LANG, firstNotBlank(userContext.getLanguage(), "default"),
                    PG_IGNORE_TENANT_POLICIES, Boolean.toString(ignoreTenantPolicies),
                    PG_USER_TENANTS, "{" + Joiner.on(",").join(tenantIds) + "}"
            ).forEach(rethrowBiConsumer((k, v) -> {
                logger.trace("set {} session variable to value = {}", k, v);
                try (Statement statement = connection.createStatement()) {
                    statement.execute(format("SET SESSION %s = %s", k, systemToSqlExpr(v)));//TODO escape value
                }
            }));
        }

        @Override
        protected void releaseConnection(Connection connection) throws SQLException {
            list(PG_OPERATION_USER, PG_OPERATION_ROLE, PG_OPERATION_SESSION, PG_OPERATION_SCOPE, PG_LANG, PG_IGNORE_TENANT_POLICIES, PG_USER_TENANTS).forEach(rethrowConsumer(key -> {
                logger.trace("reset {} session variable", key);
                try (Statement statement = connection.createStatement()) {
                    statement.execute(format("RESET %s", key));
                }
            }));
        }

    }
}
