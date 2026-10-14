/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.bim;

import java.time.ZonedDateTime;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;

public interface BimProject {

    @Nullable
    Long getId();

    @Nullable
    Long getParentId();

    String getProjectId();

    String getName();

    String getDescription();

    @Nullable
    String getImportMapping();

    @Nullable
    String getIfcFormat();//TODO handle get ifc format from bimserver (??)

    @Nullable
    byte[] getXktFile();

    boolean isActive();

    @Nullable
    ZonedDateTime getLastCheckin();

    default boolean hasParent() {
        return isNotNullAndGtZero(getParentId());
    }

}
