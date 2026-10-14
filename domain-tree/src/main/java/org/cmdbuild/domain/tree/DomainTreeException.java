package org.cmdbuild.domain.tree;

import static java.lang.String.format;
import org.cmdbuild.utils.lang.CmException;

public class DomainTreeException extends CmException {

    public DomainTreeException(Throwable nativeException) {
        super(nativeException);
    }

    public DomainTreeException(String message) {
        super(message);
    }

    public DomainTreeException(Throwable nativeException, String message) {
        super(message, nativeException);
    }

    public DomainTreeException(Throwable nativeException, String format, Object... params) {
        super(format(format, params), nativeException);
    }

    public DomainTreeException(String format, Object... params) {
        super(format(format, params));
    }
}
