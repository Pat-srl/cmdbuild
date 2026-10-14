package org.cmdbuild.auth;

import org.cmdbuild.utils.lang.CmException;

public class UnauthorizedAccessException extends CmException {

    public UnauthorizedAccessException(Throwable nativeException) {
        super(nativeException);
    }

    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccessException(Throwable nativeException, String message) {
        super(nativeException, message);
    }

    public UnauthorizedAccessException(Throwable nativeException, String format, Object... params) {
        super(nativeException, format, params);
    }

    public UnauthorizedAccessException(String format, Object... params) {
        super(format, params);
    }

}
