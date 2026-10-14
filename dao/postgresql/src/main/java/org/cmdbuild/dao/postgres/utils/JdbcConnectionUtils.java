/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.utils;

import static com.google.common.base.Preconditions.checkNotNull;
import java.sql.Connection;
import org.postgresql.ds.PGSimpleDataSource;

public class JdbcConnectionUtils {

	public static void doTestConnection(String host, Integer port, String user, String password) throws Exception {
		PGSimpleDataSource testDataSource = new PGSimpleDataSource();
		testDataSource.setServerName(checkNotNull(host));
		testDataSource.setPortNumber(checkNotNull(port));
		testDataSource.setDatabaseName("postgres");
		testDataSource.setUser(checkNotNull(user));
		testDataSource.setPassword(checkNotNull(password));
		try (Connection connection = testDataSource.getConnection()) {
			//do nothing, then auto-close connection
		}
	}
}
