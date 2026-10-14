package org.cmdbuild.auth.login;

import org.cmdbuild.utils.lang.CmException;

public class AuthenticationException extends CmException {

    public AuthenticationException(Throwable nativeException) {
        super(nativeException);
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(Throwable nativeException, String message) {
        super(nativeException, message);
    }

    public AuthenticationException(Throwable nativeException, String format, Object... params) {
        super(nativeException, format, params);
    }

    public AuthenticationException(String format, Object... params) {
        super(format, params);
    }

}
