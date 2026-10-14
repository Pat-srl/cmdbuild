package org.cmdbuild.workflow.model;

import org.cmdbuild.utils.lang.CmException;

public class WorkflowException extends CmException {

    public WorkflowException(Throwable nativeException) {
        super(nativeException);
    }

    public WorkflowException(String message) {
        super(message);
    }

    public WorkflowException(Throwable nativeException, String format, Object... params) {
        super(nativeException, format, params);
    }

    public WorkflowException(String format, Object... params) {
        super(format, params);
    }

}
