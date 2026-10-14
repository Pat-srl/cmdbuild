package org.cmdbuild.bim;

import org.cmdbuild.utils.lang.CmException;

public class BimException extends CmException {

    public BimException(Throwable e) {
        super(e);
    }

    public BimException(String message, Throwable e) {
        super(e, message);
    }

    public BimException(String message) {
        super(message);
    }

    public BimException(Throwable nativeException, String message) {
        super(nativeException, message);
    }

    public BimException(Throwable nativeException, String format, Object... params) {
        super(nativeException, format, params);
    }

    public BimException(String format, Object... params) {
        super(format, params);
    }

}
