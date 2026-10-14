/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session.data.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;
import java.util.List;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.auth.login.AuthenticationStore;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.login.LoginType;
import org.cmdbuild.auth.user.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.session.model.SessionImpl;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationStoreWithCurrentSessionBackend implements AuthenticationStore {

    private static final String AUTH_TYPE_KEY = "authType";
    private static final String LOGIN_KEY = "login";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SessionService sessionService;

    public AuthenticationStoreWithCurrentSessionBackend(SessionService sessionService) {
        this.sessionService = checkNotNull(sessionService);
    }

    @Override
    public UserType getType() {
        String typeString = trimToNull(sessionService.getCurrentSessionDataSafe().get(AUTH_TYPE_KEY));
        UserType type = null;
        if (typeString != null) {
            try {
                type = UserType.valueOf(typeString);
            } catch (Exception ex) {
                logger.warn("error parsing user type = '" + typeString + "'", ex);
            }
        }
        if (type == null) {
            type = UserType.APPLICATION;
            setType(type);
        }
        return type;
    }

    @Override
    public void setType(UserType type) {
        sessionService.updateCurrentSession((s) -> SessionImpl.copyOf(s).addSessionData(map(AUTH_TYPE_KEY, type.name())).build());
    }

    @Override
    public LoginUserIdentity getLogin() {
        List<String> loginData = (List<String>) sessionService.getCurrentSessionDataSafe().get(LOGIN_KEY);
        LoginUserIdentity login = null;
        if (loginData != null) {
            try {
                login = LoginUserIdentity.builder().withType(LoginType.valueOf(loginData.get(0))).withValue(loginData.get(1)).build();
            } catch (Exception ex) {
                logger.error("error parsing login from data = " + loginData, ex);
            }
        }
        if (login == null) {
            login = LoginUserIdentity.builder().withValue(EMPTY).build();
            setLogin(login);
        }
        return login;
    }

    @Override
    public void setLogin(LoginUserIdentity login) {
        sessionService.updateCurrentSession((s) -> SessionImpl.copyOf(s).addSessionData(map(s.getSessionData()).with(LOGIN_KEY, asList(login.getType().name(), login.getValue()))).build());
    }

}
