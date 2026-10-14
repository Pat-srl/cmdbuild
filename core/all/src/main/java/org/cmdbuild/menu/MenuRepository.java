/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.menu;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.ui.TargetDevice;

public interface MenuRepository {

    @Nullable
    MenuData getMenuDataForGroupOrNull(String groupName, TargetDevice targetDevice);

    List<MenuInfo> getAllMenuInfos();

    MenuData getMenuDataByIdOrNull(long menuId);

    MenuData getMenuDataByCodeOrNull(String menuCode);

    MenuData updateMenuData(MenuData menuData);

    MenuData createMenuData(MenuData menuData);

    void delete(long id);

    default MenuData getMenuDataById(long menuId) {
        return checkNotNull(getMenuDataByIdOrNull(menuId));
    }
}
