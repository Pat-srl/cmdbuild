/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cardfilter;

import jakarta.annotation.Nullable;
import org.cmdbuild.auth.grant.PrivilegeSubject;
import static org.cmdbuild.auth.grant.PrivilegeSubject.privilegeId;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrLtEqZero;

public interface StoredFilter extends PrivilegeSubject {

    @Nullable
    Long getId();

    String getName();

    String getDescription();

    String getOwnerName();
    
    StoredFilterOwnerType getOwnerType();

    String getConfiguration();

    boolean isShared();

    boolean isActive();

    @Nullable
    Long getUserId();

    @Override
    default String getPrivilegeId() {
        return privilegeId(PS_FILTER, getId());
    }

    default boolean isNew() {
        return isNullOrLtEqZero(getId());
    }
}
