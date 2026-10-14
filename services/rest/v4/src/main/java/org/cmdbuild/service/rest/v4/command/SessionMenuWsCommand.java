/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.menu.Menu;
import org.cmdbuild.menu.MenuService;
import org.cmdbuild.service.rest.v4.wshelpers.SessionWsCommons;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class SessionMenuWsCommand extends SessionWsCommons {

    private final MenuService menuService;

    public SessionMenuWsCommand(MenuService menuService, SessionService sessionService) {
        super(sessionService);
        this.menuService = checkNotNull(menuService);
    }

    public Menu doRead(String sessionId) {
        checkIsCurrent(sessionId);
        return menuService.getMenuForCurrentUser();
    }
}
