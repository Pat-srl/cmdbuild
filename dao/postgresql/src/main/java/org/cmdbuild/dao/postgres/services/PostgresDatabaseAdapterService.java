/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.services;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public interface PostgresDatabaseAdapterService {

    final String PG_OPERATION_USER = "cmdbuild.operation_user",
            PG_OPERATION_ROLE = "cmdbuild.operation_role",
            PG_OPERATION_SESSION = "cmdbuild.operation_session",
            PG_OPERATION_SCOPE = "cmdbuild.operation_scope",
            PG_USER_TENANTS = "cmdbuild.user_tenants",
            PG_IGNORE_TENANT_POLICIES = "cmdbuild.ignore_tenant_policies",
            PG_LANG = "cmdbuild.lang";

    DataSource getDataSource();

    JdbcTemplate getJdbcTemplate();
}
