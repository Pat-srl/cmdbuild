/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.utils;

import static com.google.common.base.Preconditions.checkNotNull;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.DelegatingConnection;
import org.cmdbuild.common.java.sql.ForwardingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConnectionWrapperDataSource extends ForwardingDataSource {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final DataSource dataSource;

    public ConnectionWrapperDataSource(DataSource dataSource) {
        checkNotNull(dataSource);
        this.dataSource = dataSource;
    }

    @Override
    protected DataSource delegate() {
        return dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return wrapConnection(super.getConnection());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return wrapConnection(super.getConnection(username, password));
    }

    private Connection wrapConnection(Connection connection) throws SQLException {
        logger.trace("wrapConnection BEGIN");
        logger.trace("prepareConnection BEGIN");
        prepareConnection(connection);
        logger.trace("prepareConnection END");
        Connection wrapper = new DelegatingConnection(connection) {
            @Override
            public void close() throws SQLException {
                try {
                    logger.trace("releaseConnection BEGIN");
                    releaseConnection(connection);
                    logger.trace("releaseConnection END");
                } catch (Exception ex) {
                    logger.error("error running relaseConnection hook", ex);
                }
                connection.close(); // should use super.close(), but it doesn't work
            }

        };
        logger.trace("wrapConnection END");
        return wrapper;
    }

    protected abstract void prepareConnection(Connection connection) throws SQLException;

    protected abstract void releaseConnection(Connection connection) throws SQLException;
}
