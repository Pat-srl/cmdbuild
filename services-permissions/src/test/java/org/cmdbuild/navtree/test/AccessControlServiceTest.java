/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.navtree.test;

import org.cmdbuild.navtree.NavTree;
import org.cmdbuild.security.Securable;
import org.cmdbuild.security.authorization.AccessControlService;
import org.cmdbuild.security.authorization.AccessControlServiceImpl;
import org.cmdbuild.security.authorization.AuthorizationService;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author ataboga
 */
public class AccessControlServiceTest {

    @Test
    public void navTreeTest() {
        AuthorizationService authorizationService = mock(AuthorizationService.class);
        when(authorizationService.canRead(any())).thenReturn(true);
        AccessControlService accessControlService = new AccessControlServiceImpl(authorizationService);
        NavTree navTree = mock(NavTree.class);
        NavTree secureNavTree = accessControlService.secure(navTree);
        assertTrue(((Securable) secureNavTree).isReadable());
    }
}
