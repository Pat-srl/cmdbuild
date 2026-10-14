/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.command;

import com.google.common.base.Supplier;
import java.util.List;
import java.util.function.Function;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.navtree.NavTree;
import org.cmdbuild.navtree.NavTreeService;
import org.cmdbuild.service.rest.v4.model.WsTreeData;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.springframework.stereotype.Component;

/**
 *
 * @author schursin
 */
@Component
public class NavTreeWsCommand {

    private final NavTreeService navTreeService;

    public NavTreeWsCommand(NavTreeService navTreeService) {
        this.navTreeService = checkNotNull(navTreeService);
    }

    public List<NavTree> doReadAll(Supplier<List<NavTree>> function, String filterStr) {
        List<NavTree> navTreeList = function.get();
        return filterNavTreeList(navTreeList, filterStr);
    }

    public NavTree doRead(Function<String, NavTree> function, String id) {
        return function.apply(id);
    }

    public NavTree doCreate(WsTreeData data) {
        NavTree tree = data.toTreeNode().build();
        return navTreeService.create(tree);
    }

    public NavTree doUpdate(String id, WsTreeData data) {
        NavTree tree = data.toTreeNode().withName(id).build();
        return navTreeService.update(tree);
    }

    public void doDelete(String id) {
        navTreeService.removeTree(id);
    }

    public void doFixNavTreeDirections(String id) {
        navTreeService.fixDirections(id);
    }

    private List<NavTree> filterNavTreeList(List<NavTree> navTreeList, String filterStr) {
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        if (filter.hasFilter()) {
            filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
            navTreeList = AttributeFilterProcessor.<NavTree>builder()
                    .withKeyToValueFunction((key, tree) -> {
                        return switch (checkNotBlank(key)) {
                            case "targetClass" ->
                                tree.getData().getTargetClassName();
                            case "type" ->
                                serializeEnum(tree.getType());
                            default ->
                                throw new IllegalArgumentException("invalid attribute filter key = " + key);
                        };
                    })
                    .withFilter(filter.getAttributeFilter()).build().filter(navTreeList);
        }
        return navTreeList;
    }
}
