/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.utils;

import org.cmdbuild.utils.lang.CmException;

/**
 * exception form ws inner errors, opposed to Ws3Exception which is for generic/filter errors
*/
public class Ws3FrameworkException extends CmException {

    public Ws3FrameworkException(Throwable nativeException) {
        super(nativeException);
    }

    public Ws3FrameworkException(String message) {
        super(message);
    }

    public Ws3FrameworkException(Throwable nativeException, String message) {
        super(nativeException, message);
    }

    public Ws3FrameworkException(Throwable nativeException, String format, Object... params) {
        super(nativeException, format, params);
    }

    public Ws3FrameworkException(String format, Object... params) {
        super(format, params);
    }

}
