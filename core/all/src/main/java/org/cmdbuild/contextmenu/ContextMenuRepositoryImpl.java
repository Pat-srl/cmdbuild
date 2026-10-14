/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.contextmenu;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Component
public class ContextMenuRepositoryImpl implements ContextMenuRepository {

    private final DaoService dao;

    public ContextMenuRepositoryImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Override
    public List<ContextMenuItemData> getContextMenuItems(String className, ContextMenuOwnerType ownerType) {
        return dao.selectAll().from(ContextMenuItemDataImpl.class)
                .where("Owner", EQ, checkNotBlank(className))
                .where("OwnerType", EQ, checkNotBlank(ownerType)).asList();
    }

    @Override
    public void updateContextMenuItems(String ownerName, List<ContextMenuItemData> items, ContextMenuOwnerType ownerType) {
        //TODO currently implemented as delete-then-create, refactor as update
        deleteContextMenuItems(ownerName, ownerType);
        items.forEach(dao::create);
    }

    @Override
    public void deleteContextMenuItems(String ownerName, ContextMenuOwnerType ownerType) {
        getContextMenuItems(ownerName, ownerType).forEach(dao::delete);
    }

}
