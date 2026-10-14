/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.auth.user;

import org.cmdbuild.auth.login.LoginModuleConfiguration;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.slf4j.Logger;

/**
 *
 * @author ataboga
 */
public interface UserScriptService {

    public LoginUserIdentity getLoginFromScript(LoginModuleConfiguration authConf, Logger customLogger, Object loginData);

}
