/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.contextmenu;

import java.util.Map;
import jakarta.annotation.Nullable;

public interface ContextMenuItemData {

    @Nullable
    Long getId();

    String getLabel();

    String getType();

    @Nullable
    String getComponentId();

    @Nullable
    String getJsScript();

    @Nullable
    Map<String, Object> getConfig();

    boolean isActive();

    String getVisibility();

    int getIndex();

    String getClassId();
    
    ContextMenuOwnerType getOwnerType();
}
