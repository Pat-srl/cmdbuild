package org.cmdbuild.auth.session;

import org.cmdbuild.auth.login.LoginUserIdentity;

import java.util.Map;

public interface OperatorService {

    Map<String, Object> getProperties(LoginUserIdentity loginUser);

    String getReasonForUnsuccessfulSearch();
}
