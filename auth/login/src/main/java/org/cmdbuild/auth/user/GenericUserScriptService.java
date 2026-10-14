/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.auth.user;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import org.cmdbuild.api.CmApiService;
import org.cmdbuild.auth.login.AuthenticationException;
import org.cmdbuild.auth.login.LoginModuleConfiguration;
import org.cmdbuild.auth.login.LoginUserIdentity;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import org.cmdbuild.utils.script.groovy.GroovyScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
public class GenericUserScriptService implements UserScriptService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final GroovyScriptService scriptService;
    private final CmApiService apiService;

    public GenericUserScriptService(GroovyScriptService scriptService, CmApiService apiService) {
        this.scriptService = checkNotNull(scriptService);
        this.apiService = checkNotNull(apiService);
    }

    @Override
    public LoginUserIdentity getLoginFromScript(LoginModuleConfiguration authConf, Logger customLogger, Object loginData) {
        try {
            logger.debug("module = {} type = {}, executing script before login =< {} >", authConf.getCode(), authConf.getType(), authConf.getLoginHandlerScript());
            Map<String, Object> out = scriptService.executeScript(authConf.getLoginHandlerScript(), map(
                    "cmdb", apiService.getCmApi(),
                    "logger", customLogger,
                    "username", (String) loginData,
                    "password", null,
                    "login", null
            ));
            return LoginUserIdentity.build(toStringNotBlank(out.get("login"), "invalid login output from handler script"));
        } catch (Exception ex) {
            throw new AuthenticationException(ex, "unable to authenticate user");
        }
    }
}
