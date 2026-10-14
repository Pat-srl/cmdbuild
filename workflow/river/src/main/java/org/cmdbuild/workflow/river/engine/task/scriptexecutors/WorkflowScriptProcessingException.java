/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.task.scriptexecutors;

import static java.lang.String.format;

public class WorkflowScriptProcessingException extends RuntimeException {

    public WorkflowScriptProcessingException() {
    }

    public WorkflowScriptProcessingException(String message) {
        super(message);
    }

    public WorkflowScriptProcessingException(Throwable cause) {
        super(cause);
    }

    public WorkflowScriptProcessingException(Throwable cause, String message, Object... args) {
        super(format(message, args), cause);
    }

}
