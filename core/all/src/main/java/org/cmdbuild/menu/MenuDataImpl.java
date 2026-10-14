/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.menu;

import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.springframework.context.annotation.Primary;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import static org.cmdbuild.menu.MenuType.MT_NAVMENU;
import org.cmdbuild.ui.TargetDevice;
import static org.cmdbuild.ui.TargetDevice.TD_DEFAULT;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Primary
@CardMapping("_Menu")
public class MenuDataImpl implements MenuData {

    private final Long id;
    private final String groupName;
    private final MenuJsonRootNode menuRootNode;
    private final TargetDevice targetDevice;
    private final String code;
    private final MenuType type;

    private MenuDataImpl(MenuDataImplBuilder builder) {
        this.id = builder.id;
        this.groupName = checkNotBlank(builder.groupName);
        this.menuRootNode = checkNotNull(builder.menuRootNode);
        this.targetDevice = firstNotNull(builder.targetDevice, TD_DEFAULT);
        this.code = builder.code;
        this.type = firstNotNull(builder.type, MT_NAVMENU);
    }

    @Override
    @Nullable
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr("GroupName")
    public String getGroupName() {
        return groupName;
    }

    @Override
    @CardAttr("Data")
    public MenuJsonRootNode getMenuRootNode() {
        return menuRootNode;
    }

    @Override
    @CardAttr("TargetDevice")
    public TargetDevice getTargetDevice() {
        return targetDevice;
    }

    @Override
    @CardAttr("Code")
    public String getCode() {
        return code;
    }

    @Override
    @CardAttr("Type")
    public MenuType getType() {
        return type;
    }

    public static MenuDataImplBuilder builder() {
        return new MenuDataImplBuilder();
    }

    public static MenuDataImplBuilder copyOf(MenuData source) {
        return new MenuDataImplBuilder()
                .withId(source.getId())
                .withGroupName(source.getGroupName())
                .withMenuRootNode(source.getMenuRootNode())
                .withTargetDevice(source.getTargetDevice())
                .withCode(source.getCode())
                .withType(source.getType());
    }

    public static class MenuDataImplBuilder implements Builder<MenuDataImpl, MenuDataImplBuilder> {

        private Long id;
        private String groupName;
        private MenuJsonRootNode menuRootNode;
        private TargetDevice targetDevice;
        private String code;
        private MenuType type;

        public MenuDataImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public MenuDataImplBuilder withGroupName(String groupName) {
            this.groupName = groupName;
            return this;
        }

        public MenuDataImplBuilder withMenuRootNode(MenuJsonRootNode menuRootNode) {
            this.menuRootNode = menuRootNode;
            return this;
        }

        public MenuDataImplBuilder withTargetDevice(TargetDevice targetDevice) {
            this.targetDevice = targetDevice;
            return this;
        }

        public MenuDataImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public MenuDataImplBuilder withType(MenuType type) {
            this.type = type;
            return this;
        }

        @Override
        public MenuDataImpl build() {
            return new MenuDataImpl(this);
        }

    }
}
