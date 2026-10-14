/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.log;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import jakarta.annotation.Nullable;

public class LogbackUtils {

    @Nullable
    public static Exception getExceptionFromLogbackIThrowableProxy(@Nullable IThrowableProxy proxy) {
        if (proxy == null || !(proxy instanceof ThrowableProxy)) {
            return null;
        } else {
            if (((ThrowableProxy) proxy).getThrowable() instanceof Exception) {
                return (Exception) ((ThrowableProxy) proxy).getThrowable();
            } else {
                return new RuntimeException(((ThrowableProxy) proxy).getThrowable());
            }
        }
    }
}
