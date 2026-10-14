/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.authorization;

import com.google.common.base.Preconditions;
import static org.cmdbuild.auth.grant.PrivilegeSubject.privilegeId;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;
import org.cmdbuild.cardfilter.StoredFilter;

public class CardFilterAsPrivilegeSubject implements PrivilegeSubjectWithInfo {

    private final StoredFilter filter;

    public CardFilterAsPrivilegeSubject(StoredFilter filter) {
        this.filter = Preconditions.checkNotNull(filter);
    }

    @Override
    public Long getId() {
        return filter.getId();
    }

    @Override
    public String getName() {
        return filter.getName();
    }

    @Override
    public String getDescription() {
        return filter.getDescription();
    }

    @Override
    public String getPrivilegeId() {
        return privilegeId(PS_FILTER, getId());
    }

}
