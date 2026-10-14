/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.sked;

import org.cmdbuild.utils.lang.CmException;

public class SkedException extends CmException {

    public SkedException(Throwable nativeException) {
        super(nativeException);
    }

    public SkedException(String message) {
        super(message);
    }

    public SkedException(Throwable nativeException, String message) {
        super(nativeException, message);
    }

    public SkedException(Throwable nativeException, String format, Object... params) {
        super(nativeException, format, params);
    }

    public SkedException(String format, Object... params) {
        super(format, params);
    }

}
