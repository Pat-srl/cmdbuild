/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.menu;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import static org.cmdbuild.menu.MenuType.MT_NAVMENU;
import org.cmdbuild.ui.TargetDevice;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class MenuImpl implements Menu {

    private final long id;
    private final MenuTreeNode rootNode;
    private final String groupName;
    private final TargetDevice device;
    private final String code;
    private final MenuType type;

    public MenuImpl(@Nullable Long id, String code, MenuTreeNode rootNode, String groupName, TargetDevice device, MenuType type) {
        this.id = id;
        this.rootNode = checkNotNull(rootNode);
        this.groupName = checkNotBlank(groupName);
        this.device = checkNotNull(device);
        this.code = code;
        this.type = firstNotNull(type, MT_NAVMENU);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getGroup() {
        return groupName;
    }

    @Override
    public MenuTreeNode getRootNode() {
        return rootNode;
    }

    @Override
    public TargetDevice getTargetDevice() {
        return device;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public MenuType getType() {
        return type;
    }

}
