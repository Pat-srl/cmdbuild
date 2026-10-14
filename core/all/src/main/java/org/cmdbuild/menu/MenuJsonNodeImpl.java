/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.menu;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.menu.MenuConstants.MENU_ITEM_TYPE_SERIALIZATION_MAP;
import static org.cmdbuild.menu.MenuItemType.*;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.toInverseMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

public class MenuJsonNodeImpl implements MenuJsonNode {

    private final MenuItemType menuType;
    private final String target, description, code;
    private final List<MenuJsonNode> children;

    @JsonCreator
    public MenuJsonNodeImpl(@JsonProperty("type") String menuType,
            @JsonProperty("target") String target,
            @JsonProperty("code") String code,
            @JsonProperty("description") String description,
            @JsonProperty("children") List<MenuJsonNodeImpl> children) {
        this(checkNotNull(toInverseMap(MENU_ITEM_TYPE_SERIALIZATION_MAP).get(menuType), "invalid menu type = '%s'", menuType), target, description, code, (List) children);
    }

    public MenuJsonNodeImpl(MenuItemType menuType, String target, String description, String code, List<MenuJsonNode> children) {
        this.menuType = checkNotNull(menuType);
        switch (menuType) {
            case CLASS, PROCESS, REPORT_CSV, REPORT_PDF, REPORT_ODT, REPORT_RTF, REPORT_XML, CUSTOM_PAGE, DASHBOARD, VIEW, NAVTREE, DEPENDENCYTREE, GEOATTRIBUTE, HOMEPAGE ->
                this.target = checkNotBlank(target, "must set valid target for menu item node of type = %s", menuType);
            case FOLDER, ROOT, SYSTEM_FOLDER ->
                this.target = null;
            default ->
                throw unsupported("unsupported menu item type = %s", menuType);
        }
        this.description = trimToNull(description);
        this.code = checkNotBlank(code);
        this.children = firstNotNull(children, emptyList()).stream().map(MenuJsonNode.class::cast).collect(toUnmodifiableList());
    }

    @JsonProperty("type")
    public String getMenuTypeAsJson() {
        return checkNotNull(MENU_ITEM_TYPE_SERIALIZATION_MAP.get(menuType));
    }

    @Override
    @JsonIgnore
    public MenuItemType getMenuType() {
        return menuType;
    }

    @Override
    @Nullable
    @JsonProperty("target")
    public String getTarget() {
        return target;
    }

    @Override
    @Nullable
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @Override
    @JsonProperty("children")
    public List<MenuJsonNode> getChildren() {
        return children;
    }

    @Override
    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "MenuJsonNodeImpl{" + "menuType=" + menuType + ", objectType=" + target + ", objectDescription=" + description + ", code=" + code + '}';
    }

}
