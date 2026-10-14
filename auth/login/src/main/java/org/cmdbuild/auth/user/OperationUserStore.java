/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.user;

import java.util.function.Function;

public interface OperationUserStore extends OperationUserSupplier {

    void setUser(OperationUser user);

    default void setUser(Function<OperationUser, OperationUser> builder) {
        setUser(builder.apply(getUser()));
    }

    default void pushUser(Function<OperationUser, OperationUser> builder) {
        setUser((us) -> OperationUserStackImpl.wrapOrCast(us).push(builder::apply));
    }

    default void popUser() {
        setUser((us) -> OperationUserStackImpl.wrapOrCast(us).pop());
    }

    default void remove() {
        setUser((OperationUser) null);
    }
}
