/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.user.OperationUserStack;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.service.rest.v4.wshelpers.SessionWsCommons;
import org.cmdbuild.userconfig.UserConfigService;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class SessionPreferencesWsCommand extends SessionWsCommons {

    private final UserConfigService userPreferencesStore;

    public SessionPreferencesWsCommand(SessionService sessionService, UserConfigService userPreferencesStore, CoreConfiguration coreConfiguration) {
        super(sessionService);
        this.userPreferencesStore = checkNotNull(userPreferencesStore);
    }

    public OperationUserStack doGetOperationUserStack(String sessionId) {
        sessionId = sessionIdOrCurrent(sessionId);
        //TODO validate sessionId = current session id OR isAdmin()
        return sessionService.getSessionById(sessionId).getOperationUser();
    }

    public void doUpdateUserConfigValue(String sessionId, String key, String value) {
        OperationUserStack operationUserStack = doGetOperationUserStack(sessionId);
        userPreferencesStore.setByUsername(operationUserStack.getLoginUser().getUsername(), key, value);
    }

    public OperationUserStack doUpdateUserConfigValues(String sessionId, Map<String, String> data) {
        OperationUserStack operationUserStack = doGetOperationUserStack(sessionId);
        userPreferencesStore.updateByUsername(operationUserStack.getUsername(), data);
        return operationUserStack;
    }

    public void doDeleteSystemConfigValue(String sessionId, String key) {
        OperationUserStack operationUserStack = doGetOperationUserStack(sessionId);
        userPreferencesStore.deleteByUsername(operationUserStack.getLoginUser().getUsername(), key);
    }
}
