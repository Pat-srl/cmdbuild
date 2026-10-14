/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.services;

import javax.sql.DataSource;
import org.cmdbuild.dao.driver.DatabaseAccessConfig;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Qualifier(SYSTEM_LEVEL_ONE)
public class NoTenantPostgresDatabaseAdapterService extends PostgresDatabaseAdapterServiceImpl {

	public NoTenantPostgresDatabaseAdapterService(@Qualifier(SYSTEM_LEVEL_ONE) DataSource innerDataSource, @Qualifier(SYSTEM_LEVEL_ONE) DatabaseAccessConfig databaseAccessConfig) {
		super(innerDataSource, databaseAccessConfig);
	}

	@Override
	@Bean("jdbcTemplate_level_one")
	@Qualifier(SYSTEM_LEVEL_ONE)
	public JdbcTemplate getJdbcTemplate() {
		return super.getJdbcTemplate();
	}

}
