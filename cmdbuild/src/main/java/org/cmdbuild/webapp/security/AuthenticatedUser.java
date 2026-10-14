/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.security;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cmdbuild.auth.grant.UserPrivilegesForObject;
import org.cmdbuild.auth.role.RolePrivilege;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.auth.user.OperationUserStack;
import static org.cmdbuild.auth.utils.UserPrivilegesUtils.getAutoritiesFromPrivileges;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class AuthenticatedUser implements Authentication, OperationUserStack {

    private final OperationUserStack operationUserStack;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthenticatedUser(OperationUserStack operationUserStack) {
        this.operationUserStack = checkNotNull(operationUserStack);
        this.authorities = getAutoritiesFromPrivileges(operationUserStack.getRolePrivileges());
    }

    // Authentication impl BEGIN
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getDetails() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return getName();
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new UnsupportedOperationException("this user is always authenticated");
    }

    @Override
    public String getName() {
        return operationUserStack.getLoginUser().getUsername();
    }

    // Authentication impl END
    @Override
    public String toString() {
        return "MyAuthenticatedUser{" + "username=" + getName() + '}';
    }

    // operation user stack BEGIN
    @Override
    public OperationUser getCurrentOperationUser() {
        return operationUserStack.getCurrentOperationUser();
    }

    @Override
    public OperationUser getRootOperationUser() {
        return operationUserStack.getRootOperationUser();
    }

    @Override
    public List<OperationUser> getOperationUserStack() {
        return operationUserStack.getOperationUserStack();
    }

    @Override
    public int getOperationUserStackSize() {
        return operationUserStack.getOperationUserStackSize();
    }

    @Override
    public Set<RolePrivilege> getRolePrivileges() {
        return operationUserStack.getRolePrivileges();
    }

    @Override
    public Map<String, UserPrivilegesForObject> getAllPrivileges() {
        return operationUserStack.getAllPrivileges();
    }

    // operation user stack END
    @Override
    public OperationUserStack push(OperationUser user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OperationUserStack pop() {
        throw new UnsupportedOperationException();
    }

}
