/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.role;

import jakarta.annotation.Nullable;
import org.cmdbuild.utils.json.JsonBean;

@JsonBean(GroupConfigImpl.class)
public interface GroupConfig {

    boolean getProcessWidgetAlwaysEnabled();

    @Nullable
    String getStartingClass();

    @Nullable
    Boolean getBulkUpdate();

    @Nullable
    Boolean getBulkDelete();

    @Nullable
    Boolean getBulkAbort();

    @Nullable
    Boolean getFullTextSearch();

}
