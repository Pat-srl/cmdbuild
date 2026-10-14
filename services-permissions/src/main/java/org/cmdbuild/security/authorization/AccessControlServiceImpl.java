/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.security.authorization;

import java.lang.reflect.Proxy;
import org.cmdbuild.security.Securable;
import org.cmdbuild.security.SecurityHandler;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
public class AccessControlServiceImpl implements AccessControlService {

    private final AuthorizationService authorizationService;

    public AccessControlServiceImpl(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    public boolean canRead(Class<?> type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean canUpdate(Class<?> type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T secure(T object) {
        return (T) Proxy.newProxyInstance(object.getClass().getClassLoader(), list(object.getClass().getInterfaces()).with(Securable.class).toArray(Class<?>[]::new), new SecurityHandler(object, authorizationService.canRead(object)));
    }
}
