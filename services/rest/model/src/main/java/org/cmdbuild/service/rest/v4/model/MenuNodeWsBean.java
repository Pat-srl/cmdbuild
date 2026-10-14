/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import static java.util.Collections.emptyList;
import java.util.List;
import static org.cmdbuild.menu.MenuConstants.MENU_ITEM_TYPE_SERIALIZATION_MAP;
import org.cmdbuild.menu.MenuItemType;
import static org.cmdbuild.utils.lang.CmMapUtils.toInverseMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmStringUtils.emptyToNull;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;

/**
 * @author ldare
 */
public class MenuNodeWsBean {

    public final MenuItemType menuType;
    public final String target, objectDescription, code;
    public final List<MenuNodeWsBean> children;

    public MenuNodeWsBean(
            @JsonProperty("menuType") String menuType,
            @JsonProperty("objectTypeName") String target,
            @JsonProperty("_id") String code,
            @JsonProperty("objectDescription") String objectDescription,
            @JsonProperty("children") List<MenuNodeWsBean> children) {
        this.menuType = checkNotNull(toInverseMap(MENU_ITEM_TYPE_SERIALIZATION_MAP).get(checkNotBlank(menuType)), "unknown menu type = '%s'", menuType);
        this.target = target;
        this.objectDescription = objectDescription;
        this.code = firstNotNull(emptyToNull(code), randomId());
        this.children = firstNotNull(children, emptyList());
    }
}
