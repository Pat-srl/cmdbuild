/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.menu;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.uniqueIndex;
import com.google.common.eventbus.Subscribe;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.grant.GrantDataUpdatedEvent;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.eventbus.EventBusService;

@Component
public class MenuRepositoryImpl implements MenuRepository {

    private final DaoService dao;
    private final CmCache<Map<TargetDevice, MenuData>> menuElementsByGroup;
    private final Holder<List<MenuInfo>> menuInfos;

    public MenuRepositoryImpl(DaoService dao, CacheService cacheService, EventBusService grantEventService) {
        this.dao = checkNotNull(dao);
        menuElementsByGroup = cacheService.newCache("menu_elements_by_group", CacheConfig.SYSTEM_OBJECTS);
        menuInfos = cacheService.newHolder("menu_infos", CacheConfig.SYSTEM_OBJECTS);
        grantEventService.getGrantEventBus().register(new Object() {
            @Subscribe
            public void handleGrantDataUpdatedEvent(GrantDataUpdatedEvent event) {
                invalidateCache();
            }
        });
    }

    private void invalidateCache() {
        menuElementsByGroup.invalidateAll();
        menuInfos.invalidate();
    }

    @Override
    @Nullable
    public MenuData getMenuDataForGroupOrNull(String groupName, TargetDevice targetDevice) {
        return menuElementsByGroup.get(groupName, () -> uniqueIndex(doGetMenuElementsForGroupOrNull(groupName), MenuData::getTargetDevice)).get(targetDevice);
    }

    @Override
    public List<MenuInfo> getAllMenuInfos() {
        return menuInfos.get(() -> doGetAllMenuInfos());
    }

    @Override
    @Nullable
    public MenuData getMenuDataByIdOrNull(long menuId) {
        return dao.getByIdOrNull(MenuDataImpl.class, menuId);
    }

    @Override
    @Nullable
    public MenuData getMenuDataByCodeOrNull(String menuCode) {
        return dao.getByCodeOrNull(MenuDataImpl.class, menuCode);
    }

    @Override
    public MenuData updateMenuData(MenuData menuData) {
        menuData = dao.update(menuData);
        invalidateCache();
        return menuData;
    }

    @Override
    public MenuData createMenuData(MenuData menuData) {
        menuData = dao.create(menuData);
        invalidateCache();
        return menuData;
    }

    @Override
    public void delete(long id) {
        dao.delete(MenuData.class, id);
        invalidateCache();
    }

    @Nullable
    private List<MenuData> doGetMenuElementsForGroupOrNull(String groupName) {
        return dao.selectAll().from(MenuDataImpl.class).where("GroupName", EQ, checkNotBlank(groupName)).where("Type", EQ, "navmenu").asList();
    }

    private List<MenuInfo> doGetAllMenuInfos() {
        return dao.selectAll().from(MenuInfoImpl.class).asList();
    }
}
