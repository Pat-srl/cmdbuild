/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.offline;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.Subscribe;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.cmdbuild.auth.grant.GrantDataUpdatedEvent;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.menu.MenuInfo;
import org.cmdbuild.ui.TargetDevice;
import org.springframework.stereotype.Component;

@Component
public class OfflineRepositoryImpl implements OfflineRepository {

    private final DaoService dao;
    private final CmCache<Map<TargetDevice, OfflineData>> modelElementsByGroup;
    private final Holder<List<MenuInfo>> modelInfos;

    public OfflineRepositoryImpl(DaoService dao, CacheService cacheService, EventBusService grantEventService) {
        this.dao = checkNotNull(dao);
        modelElementsByGroup = cacheService.newCache("model_offline_elements_by_group", CacheConfig.SYSTEM_OBJECTS);
        modelInfos = cacheService.newHolder("model_offline_infos", CacheConfig.SYSTEM_OBJECTS);
        grantEventService.getGrantEventBus().register(new Object() {
            @Subscribe
            public void handleGrantDataUpdatedEvent(GrantDataUpdatedEvent event) {
                invalidateCache();
            }
        });
    }

    private void invalidateCache() {
        modelElementsByGroup.invalidateAll();
        modelInfos.invalidate();
    }

    @Override
    @Nullable
    public List<OfflineData> getAllModelData() {
        return dao.selectAll().from(OfflineDataImpl.class).asList();
    }

    @Override
    @Nullable
    public OfflineData getModelDataByIdOrNull(long offlineId) {
        return dao.getByIdOrNull(OfflineDataImpl.class, offlineId);
    }

    @Override
    @Nullable
    public OfflineData getModelDataByCodeOrNull(String offlineCode) {
        return dao.getByCodeOrNull(OfflineDataImpl.class, offlineCode);
    }

    @Override
    public OfflineData updateModelData(OfflineData offlineData) {
        offlineData = dao.update(offlineData);
        invalidateCache();
        return offlineData;
    }

    @Override
    public OfflineData createModelData(OfflineData offlineData) {
        offlineData = dao.create(offlineData);
        invalidateCache();
        return offlineData;
    }

    @Override
    public void delete(long id) {
        dao.delete(OfflineData.class, id);
        invalidateCache();
    }
}
