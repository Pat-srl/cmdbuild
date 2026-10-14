/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.contextmenu;

import java.util.List;

public interface ContextMenuRepository {

    List<ContextMenuItemData> getContextMenuItems(String className, ContextMenuOwnerType ownerType);

    void updateContextMenuItems(String ownerName, List<ContextMenuItemData> items, ContextMenuOwnerType ownerType);

    void deleteContextMenuItems(String ownerName, ContextMenuOwnerType ownerType);

}
