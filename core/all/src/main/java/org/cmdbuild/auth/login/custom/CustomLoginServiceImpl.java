/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login.custom;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import java.util.Optional;
import jakarta.inject.Provider;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.auth.login.AuthRequestInfo;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.session.SessionService;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackIfPackedOrBase64;
import org.cmdbuild.auth.login.LoginData;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.login.PasswordAuthenticator;
import org.cmdbuild.auth.login.PasswordCheckResult;
import org.cmdbuild.auth.login.PasswordCheckResultImpl;
import static org.cmdbuild.auth.login.PasswordCheckStatus.PCR_ACCESS_DENIED;
import static org.cmdbuild.auth.login.PasswordCheckStatus.PCR_HAS_VALID_PASSWORD;
import org.cmdbuild.script.ScriptService;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;
import static org.cmdbuild.utils.script.CmScriptUtils.SCRIPT_OUTPUT_VAR;

@Component
public class CustomLoginServiceImpl implements CustomLoginService, PasswordAuthenticator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Provider<SessionService> sessionService;
    private final CustomLoginConfiguration config;
    private final ScriptService scriptService;

    public CustomLoginServiceImpl(ScriptService scriptService, Provider<SessionService> sessionService, CustomLoginConfiguration config) {
        this.scriptService = checkNotNull(scriptService);
        this.sessionService = checkNotNull(sessionService);
        this.config = checkNotNull(config);
    }

    @Override
    public void handleCustomLoginRequestAndCreateAndSetSession(AuthRequestInfo authRequestInfo) {
        new CustomLoginServiceHelper().handleCustomLoginRequestAndCreateAndSetSession(authRequestInfo);
    }

    @Override
    public boolean isEnabled() {
        return config.isCustomLoginEnabled();
    }

    @Override
    public PasswordCheckResult checkPassword(LoginUserIdentity login, String password) {
        return new CustomLoginServiceHelper().checkPassword(login, password);
    }

    private class CustomLoginServiceHelper {

        private PasswordCheckResult loginResult;
        private LoginData loginData;

        public void handleCustomLoginRequestAndCreateAndSetSession(AuthRequestInfo authRequestInfo) {
            executeCustomLoginScript(map("request", authRequestInfo, "mode", "service"));
            checkArgument(equal(loginResult.getStatus(), PCR_HAS_VALID_PASSWORD), "invalid login");
            sessionService.get().createAndSet(LoginDataImpl.copyOf(loginData).withNoPasswordRequired().build());
        }

        public PasswordCheckResult checkPassword(LoginUserIdentity login, String password) {
            loginData = LoginDataImpl.builder().withLoginString(login.getValue()).build();
            executeCustomLoginScript(map("login", login.getValue(), "password", password, "mode", "login"));
            return loginResult;
        }

        private void executeCustomLoginScript(Map<String, Object> data) {

            checkArgument(config.isCustomLoginEnabled(), "custom login is not enabled");

            String script = checkNotBlank(unpackIfPackedOrBase64(config.getCustomLoginHandlerScript()), "custom login handler script config is null"),
                    classLoaderPath = config.getCustomLoginHandlerScriptClasspath();

            logger.trace("execute custom login handler script = \n\n{}\n", script);

            Map<String, Object> result = scriptService.helper(getClass(), script, config.getCustomLoginHandlerLanguage()).withClassLoader(classLoaderPath).execute(data);

            if (result.get(SCRIPT_OUTPUT_VAR) instanceof PasswordCheckResult r) {
                loginResult = r;
                loginData = LoginDataImpl.builder().withLoginString(r.getLogin().getValue()).build();
            } else {

                String username = toStringOrNull(firstNotBlankOrNull(result.get("username"), result.get(SCRIPT_OUTPUT_VAR))), group = toStringOrNull(result.get("group"));

                logger.debug("custom login handler returned username =< {} > group =< {} >", username, group);

                if (isBlank(username)) {
                    loginResult = new PasswordCheckResultImpl(PCR_ACCESS_DENIED, LoginUserIdentity.build(Optional.ofNullable(loginData).map(LoginData::getLoginString).orElse("unknown")));
                } else {
                    loginData = LoginDataImpl.builder().withLoginString(username).withGroupName(group).build();
                    loginResult = new PasswordCheckResultImpl(PCR_HAS_VALID_PASSWORD, LoginUserIdentity.build(loginData.getLoginString()));
                }

            }
        }

    }

}
