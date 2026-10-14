package org.cmdbuild.common.java.sql;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.google.common.collect.ForwardingObject;

public abstract class ForwardingDataSource extends ForwardingObject implements DataSource {

    protected ForwardingDataSource() {
    }

    @Override
    protected abstract DataSource delegate();

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return delegate().getLogWriter();
    }

    @Override
    public void setLogWriter(final PrintWriter out) throws SQLException {
        delegate().setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(final int seconds) throws SQLException {
        delegate().setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return delegate().getLoginTimeout();
    }

    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return delegate().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return delegate().isWrapperFor(iface);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return delegate().getConnection();
    }

    @Override
    public Connection getConnection(final String username, final String password) throws SQLException {
        return delegate().getConnection(username, password);
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return delegate().getParentLogger();
    }

}
