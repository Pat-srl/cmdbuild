/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ldare
 */
public class WsRoleUsers {

    public final List<Long> listUsersToAdd, listUsersToRemove;

    public WsRoleUsers(@JsonProperty("add") List<Long> listUsersToAdd, @JsonProperty("remove") List<Long> listUsersToRemove) {
        this.listUsersToAdd = checkNotNull(listUsersToAdd);
        this.listUsersToRemove = checkNotNull(listUsersToRemove);
    }

    public List<Long> getListUsersToAdd() {
        return listUsersToAdd;
    }

    public List<Long> getListUsersToRemove() {
        return listUsersToRemove;
    }
}
