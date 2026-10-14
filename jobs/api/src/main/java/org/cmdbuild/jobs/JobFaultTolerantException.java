/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs;

import org.cmdbuild.utils.lang.CmException;


/**
 * Exception used by connector plugins to signal an error that can doesn't lead
 * to a global task halt, is kind of tolerated and treated as a warning. 
 * 
 * @author afelice
 */
public class JobFaultTolerantException extends CmException {

    public JobFaultTolerantException(Throwable nativeException) {
        super(nativeException);
    }

    public JobFaultTolerantException(String message) {
        super(message);
    }

    public JobFaultTolerantException(Throwable nativeException, String message) {
        super(nativeException, message);
    }

    public JobFaultTolerantException(Throwable nativeException, String format, Object... params) {
        super(nativeException, format, params);
    }

    public JobFaultTolerantException(String format, Object... params) {
        super(format, params);
    }

}
