package org.cmdbuild.utils.lang;

import static java.lang.String.format;

public class CmException extends RuntimeException {

    public CmException(Throwable nativeException) {
        super(nativeException);
    }

    public CmException(String message) {
        super(message);
    }

    public CmException(Throwable nativeException, String message) {
        super(message, nativeException);
    }

    public CmException(Throwable nativeException, String format, Object... params) {
        super(format(format, params), nativeException);
    }

    public CmException(String format, Object... params) {
        super(format(format, params));
    }
}
