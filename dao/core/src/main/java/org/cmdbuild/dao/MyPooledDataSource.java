/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao;

import static com.google.common.base.Objects.equal;
import jakarta.annotation.Nullable;
import java.util.List;
import javax.management.MBeanRegistration;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSourceMXBean;
import static org.cmdbuild.dao.MyPooledDataSource.ConnectionStatus.CS_ACTIVE;

public interface MyPooledDataSource extends DataSource, BasicDataSourceMXBean, MBeanRegistration, AutoCloseable {

    List<ConnectionInfo> getStackTraceForBorrowedConnections();

    void hardReset();

    enum ConnectionStatus {
        CS_ACTIVE, CS_IDLE, CS_OTHER
    }

    interface ConnectionInfo {

        @Nullable
        String getTrace();

        ConnectionStatus getStatus();

        default boolean isActive() {
            return equal(CS_ACTIVE, getStatus());
        }

    }
}
