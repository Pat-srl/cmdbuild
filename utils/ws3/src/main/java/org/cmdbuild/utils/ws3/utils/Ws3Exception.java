/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.utils;

import org.cmdbuild.utils.lang.CmException;

public class Ws3Exception extends CmException {

    public Ws3Exception(Throwable nativeException) {
        super(nativeException);
    }

    public Ws3Exception(String message) {
        super(message);
    }

    public Ws3Exception(Throwable nativeException, String message) {
        super(nativeException, message);
    }

    public Ws3Exception(Throwable nativeException, String format, Object... params) {
        super(nativeException, format, params);
    }

    public Ws3Exception(String format, Object... params) {
        super(format, params);
    }

}
