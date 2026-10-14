/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.security.utils;

import org.cmdbuild.security.Securable;

/**
 *
 * @author ataboga
 */
public class SecurableUtils {

    public static <T> boolean isReadable(T obj) {
        return Securable.class.cast(obj).isReadable();
    }

    public static <T> boolean isUpdatable(T obj) {
        return Securable.class.cast(obj).isUpdatable();
    }
}
