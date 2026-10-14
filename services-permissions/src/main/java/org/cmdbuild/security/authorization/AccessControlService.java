/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.security.authorization;

import org.cmdbuild.security.utils.SecurableUtils;

/**
 *
 * @author ataboga
 */
public interface AccessControlService {

    default boolean canCreate(Class<?> type) {
        return canUpdate(type);
    }

    boolean canRead(Class<?> type);

    boolean canUpdate(Class<?> type);

    default boolean canDelete(Class<?> type) {
        return canCreate(type);
    }

    default <T extends Object> boolean canRead(T object) {
        return SecurableUtils.isReadable(secure(object));
    }

    default <T extends Object> boolean canUpdate(T object) {
        return SecurableUtils.isUpdatable(secure(object));
    }

    <T extends Object> T secure(T object);
}
