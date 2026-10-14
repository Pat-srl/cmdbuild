/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.services;

import javax.sql.DataSource;
import org.cmdbuild.dao.driver.DatabaseAccessConfig;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.SYSTEM_LEVEL_TWO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Primary
@Qualifier(SYSTEM_LEVEL_TWO)
public class TenantAwarePostgresDatabaseAdapterService extends PostgresDatabaseAdapterServiceImpl {

	public TenantAwarePostgresDatabaseAdapterService(@Qualifier(SYSTEM_LEVEL_ONE) DataSource innerDataSource, @Qualifier(SYSTEM_LEVEL_TWO) DatabaseAccessConfig databaseAccessConfig) {
		super(innerDataSource, databaseAccessConfig);
	}

	@Override
	@Bean("jdbcTemplate_level_two")
	@Qualifier(SYSTEM_LEVEL_TWO)
	@Primary
	public JdbcTemplate getJdbcTemplate() {
		return super.getJdbcTemplate();
	}

	@Override
	@Bean("mainDataSource")
	@Qualifier(SYSTEM_LEVEL_TWO)
	@Primary
	public DataSource getDataSource() {
		return super.getDataSource();
	}

}
