/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import java.util.function.Consumer;
import javax.sql.DataSource;
import org.cmdbuild.common.java.sql.ForwardingDataSource;
import org.cmdbuild.dao.ConfigurableDataSource;
import org.cmdbuild.dao.MyPooledDataSource;
import org.cmdbuild.dao.config.inner.DatabaseCreator;
import static org.cmdbuild.utils.lang.EventBusUtils.logExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyConfigurableDataSource extends ForwardingDataSource implements ConfigurableDataSource {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DatabaseCreator databaseCreator;

    private final EventBus eventBus = new EventBus(logExceptions(logger));

    public DummyConfigurableDataSource(DatabaseCreator databaseCreator) {
        this.databaseCreator = checkNotNull(databaseCreator);
    }

    @Override
    public String getDatabaseUrl() {
        return databaseCreator.getDatabaseUrl();
    }

    @Override
    protected DataSource delegate() {
        return databaseCreator.getCmdbuildDataSource();
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public MyPooledDataSource getInner() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void closeInner() {
        //do nothing
    }

    @Override
    public void reloadInner() {
        //do nothing
    }

    @Override
    public boolean hasAdminDataSource() {
        return databaseCreator.getConfig().hasAdminUser();
    }

    @Override
    public String getDatabaseUser() {
        return databaseCreator.getConfig().getCmdbuildUser();
    }

    @Override
    public void withAdminDataSource(Consumer<DataSource> consumer) {
        DataSource adminDataSource = databaseCreator.getAdminDataSource();
        consumer.accept(adminDataSource);
    }

}
