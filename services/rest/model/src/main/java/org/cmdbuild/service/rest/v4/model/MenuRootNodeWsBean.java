/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.menu.MenuType;
import org.cmdbuild.ui.TargetDevice;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.cmdbuild.menu.MenuType.MT_NAVMENU;
import static org.cmdbuild.ui.TargetDevice.TD_DEFAULT;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 * @author ldare
 */
public class MenuRootNodeWsBean {

    public final String groupName;
    public final List<MenuNodeWsBean> children;
    public final TargetDevice targetDevice;
    public final MenuType type;

    public MenuRootNodeWsBean(
            @JsonProperty("device") String targetDevice,
            @JsonProperty("group") String groupName,
            @JsonProperty("type") String menuType,
            @JsonProperty("children") List<MenuNodeWsBean> children) {
        this.groupName = checkNotBlank(groupName);
        this.children = firstNonNull(children, emptyList());
        this.type = parseEnumOrDefault(menuType, MT_NAVMENU);
        this.targetDevice = parseEnumOrDefault(targetDevice, TD_DEFAULT);
    }
}
