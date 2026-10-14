/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.user;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.isNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.Iterables.any;
import java.util.ArrayDeque;
import static java.util.Collections.singletonList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cmdbuild.auth.grant.UserPrivilegesForObject;
import org.cmdbuild.auth.role.RolePrivilege;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 *
 */
public class OperationUserStackImpl implements OperationUserStack {

    private final Deque<OperationUser> operationUsers;

    /**
     * note: you cannot use an instance of OperationUserStack as constructor
     * param (nesting of OperationUserStack instances is not allowed!); if
     * you're not sure, use static method {@link #wrapOrCast(org.cmdbuild.auth.user.OperationUser)
     * }
     *
     * @param operationUser
     */
    public OperationUserStackImpl(OperationUser operationUser) {
        this(singletonList(checkNotNull(operationUser)));
    }

    /**
     * note: operationUsers cannot contain instances of OperationUserStack
     * (nesting of OperationUserStack instances is not allowed!)
     *
     * @param operationUsers
     */
    public OperationUserStackImpl(List<OperationUser> operationUsers) {
        checkNotNull(operationUsers);
        checkArgument(!operationUsers.isEmpty(), "cannot create an operation user stack with an empty stack");
        checkArgument(!any(operationUsers, isNull()));
        checkArgument(!any(operationUsers, (item) -> item instanceof OperationUserStack),
                "cannot use an instance of OperationUserStack as operationUser param while building a new instance of OperationUserStack (nesting of OperationUserStack instances is not allowed!); params = %s", operationUsers);
        this.operationUsers = new ArrayDeque<>(operationUsers);
    }

    @Override
    public Map<String, UserPrivilegesForObject> getAllPrivileges() {
        return getCurrentOperationUser().getAllPrivileges();
    }

    @Override
    public OperationUser getCurrentOperationUser() {
        return operationUsers.getLast();
    }

    @Override
    public Set<RolePrivilege> getRolePrivileges() {
        return getCurrentOperationUser().getRolePrivileges();
    }

    @Override
    public OperationUser getRootOperationUser() {
        return operationUsers.getFirst();
    }

    @Override
    public List<OperationUser> getOperationUserStack() {
        return ImmutableList.copyOf(operationUsers);
    }

    @Override
    public int getOperationUserStackSize() {
        return operationUsers.size();
    }

    @Override
    public OperationUserStack push(OperationUser user) {
        return new OperationUserStackImpl(list(operationUsers).with(user));
    }

    @Override
    public OperationUserStack pop() {
        checkArgument(operationUsers.size() > 1, "cannot pop operation user: stack size is = 1");
        return new OperationUserStackImpl(list(operationUsers).withoutLast());
    }
    /**
     * if operationUser is an instance of OperationUserStack, return
     * operationUser; otherwise, build and return a new instance of
     * OperationUserStack that wrap operationUser
     *
     * @param operationUser
     * @return operationUserStack
     */
    public static OperationUserStack wrapOrCast(OperationUser operationUser) {
        if (operationUser instanceof OperationUserStack) {
            return (OperationUserStack) operationUser;
        } else {
            return new OperationUserStackImpl(operationUser);
        }
    }

    /**
     * if operationUser is an instance of operationuserstack, unwrap the current
     * user; otherwise, return operationUser
     *
     * @param operationUser
     * @return
     */
    public static OperationUser toSimpleOperationUser(OperationUser operationUser) {
        if (operationUser instanceof OperationUserStack) {
            OperationUserStack operationUserStack = (OperationUserStack) operationUser;
            return operationUserStack.getCurrentOperationUser();
        } else {
            return operationUser;
        }
    }

    @Override
    public String toString() {
        return "OperationUserStackImpl{" + "operationUsers=" + operationUsers + '}';
    }

}
