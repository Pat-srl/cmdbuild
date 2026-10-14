/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.datasource.utils;

import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.List;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObjectInfo;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.cmdbuild.dao.MyPooledDataSource;
import static org.cmdbuild.dao.MyPooledDataSource.ConnectionStatus.CS_ACTIVE;
import static org.cmdbuild.dao.MyPooledDataSource.ConnectionStatus.CS_IDLE;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyPooledDataSourceImpl extends BasicDataSource implements MyPooledDataSource {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<ConnectionInfo> getStackTraceForBorrowedConnections() {
        GenericObjectPool<PoolableConnection> pool = getConnectionPool();
        return pool.listAllObjects().stream()
                .map(i -> new ConnectionInfoImpl(i.getLastBorrowTime() > i.getLastReturnTime() ? CS_ACTIVE : CS_IDLE, nullToEmpty(i.getLastBorrowTrace()).replaceFirst(" has not been returned to the pool", "")))//TODO improve this, status
                .collect(toImmutableList());
    }

    @Override
    public void hardReset() {
        try {
            GenericObjectPool<PoolableConnection> pool = getConnectionPool();
            close();
            if (pool != null) {
                pool.close();
                pool.clear();
                List<DefaultPooledObjectInfo> pooledObjects = list(pool.listAllObjects());
                if (!pooledObjects.isEmpty()) {
                    logger.info("detected {} busy connections, force terminate (this will cause exception termination of ongoing jobs)", pooledObjects.size());
                    pooledObjects.forEach(rethrowConsumer(o -> {
                        Field privateField = o.getClass().getDeclaredField("pooledObject");
                        privateField.setAccessible(true);
                        String borrowTrace = nullToEmpty(o.getLastBorrowTrace());
                        try (Connection connection = ((DefaultPooledObject<PoolableConnection>) privateField.get(o)).getObject().getDelegate()) {
                            logger.info("close connection = {} borrowed by = {}", connection, firstNotBlank(borrowTrace, "<borrow trace not available>"));
                        }
                    }));
                }
            }
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

}
