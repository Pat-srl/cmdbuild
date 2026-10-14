package org.cmdbuild.menu;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.ui.TargetDevice;

public interface MenuService {

    Menu getMenuByIdOrNull(long menuId);

    Menu getGisMenu();

    Menu create(String groupId, MenuTreeNode menu, TargetDevice targetDevice, MenuType menuType);

    Menu update(long menuId, MenuTreeNode menu, TargetDevice targetDevice);

    void delete(long menuId);

    Menu getMenuForCurrentUser();

    Menu getGisMenuForUserAndClass(String forClass);

    List<MenuInfo> getAllMenuInfos();

    default Menu getMenuById(long menuId) {
        return checkNotNull(getMenuByIdOrNull(menuId));
    }

    default List<Menu> getAllMenus() {
        return getAllMenuInfos().stream().map(m -> getMenuById(m.getId())).collect(toList());
    }

}
