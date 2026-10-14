/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.security;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * @author ataboga
 */
public class SecurityHandler implements InvocationHandler {

    private final Object target;
    private final boolean canRead, canUpdate;

    public SecurityHandler(Object target, boolean canUpdate) {
        this.target = target;
        this.canRead = this.canUpdate = canUpdate;
    }

    public SecurityHandler(Object target, boolean canRead, boolean canUpdate) {
        this.target = target;
        this.canRead = canRead;
        this.canUpdate = canUpdate;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (method.getName()) {
            case "isReadable" -> {
                return canRead;
            }
            case "isUpdatable" -> {
                return canUpdate;
            }
        }
        return method.invoke(target, args);
    }
}
