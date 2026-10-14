/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.contextmenu;

import java.util.List;
import org.cmdbuild.view.ViewBase;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.contextmenu.ContextMenuOwnerType.CMO_CLASS;
import static org.cmdbuild.contextmenu.ContextMenuOwnerType.CMO_VIEW;

public interface ContextMenuService {

    List<ContextMenuItem> getContextMenuItems(String ownerName, ContextMenuOwnerType ownerType);

    void updateContextMenuItems(String ownerName, List<ContextMenuItem> items, ContextMenuOwnerType ownerType);

    void deleteForOwner(String ownerName, ContextMenuOwnerType ownerType);

    default void updateContextMenuItemsForClass(Classe classe, List<ContextMenuItem> items) {
        updateContextMenuItems(classe.getName(), items, CMO_CLASS);
    }

    default void updateContextMenuItemsForView(ViewBase view, List<ContextMenuItem> items) {
        updateContextMenuItems(view.getName(), items, CMO_VIEW);
    }

    default List<ContextMenuItem> getContextMenuItemsForClass(Classe classe) {
        return getContextMenuItems(classe.getName(), CMO_CLASS);
    }

    default List<ContextMenuItem> getContextMenuItemsForView(ViewBase view) {
        return getContextMenuItems(view.getName(), CMO_VIEW);
    }

    default void deleteForClass(Classe classe) {
        deleteForOwner(classe.getName(), CMO_CLASS);
    }

    default void deleteForView(ViewBase view) {
        deleteForOwner(view.getName(), CMO_VIEW);
    }

}
