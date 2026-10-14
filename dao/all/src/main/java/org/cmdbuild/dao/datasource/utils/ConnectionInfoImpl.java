/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cmdbuild.dao.datasource.utils;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.cmdbuild.dao.MyPooledDataSource;

public class ConnectionInfoImpl implements MyPooledDataSource.ConnectionInfo {

    private final MyPooledDataSource.ConnectionStatus status;
    private final String trace;

    public ConnectionInfoImpl(MyPooledDataSource.ConnectionStatus status, String trace) {
        this.status = Preconditions.checkNotNull(status);
        this.trace = Strings.nullToEmpty(trace);
    }

    @Override
    public MyPooledDataSource.ConnectionStatus getStatus() {
        return status;
    }

    @Override
    public String getTrace() {
        return trace;
    }

}
