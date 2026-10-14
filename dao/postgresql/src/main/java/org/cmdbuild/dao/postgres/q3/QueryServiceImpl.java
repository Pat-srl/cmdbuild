/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import static com.google.common.base.Preconditions.checkNotNull;
import java.sql.ResultSet;
import java.util.List;
import java.util.function.Function;
import org.cmdbuild.dao.postgres.services.PostgresDatabaseAdapterService;
import org.cmdbuild.dao.postgres.services.QueryService;
import org.springframework.stereotype.Component;

@Component
public class QueryServiceImpl implements QueryService {

	private final PostgresDatabaseAdapterService databaseAdapterService;

	public QueryServiceImpl(PostgresDatabaseAdapterService databaseAdapterService) {
		this.databaseAdapterService = checkNotNull(databaseAdapterService);
	}

	@Override
	public <T> List<T> query(String query, Function<ResultSet, T> rowHandler) {
		return databaseAdapterService.getJdbcTemplate().query(query, (rs, i) -> rowHandler.apply(rs));
	}

}
