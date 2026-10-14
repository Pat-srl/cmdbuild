/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import java.lang.reflect.Method;

public abstract class ProxyWrapper {

    public void beforeMethodInvocation(Method method, Object[] params) {

    }

    public Object afterSuccessfullMethodInvocation(Method method, Object[] params, Object response) {
        return response;
    }

    public void afterFailedMethodInvocation(Method method, Object[] params, Throwable error) {

    }

    public void afterMethodInvocation(Method method, Object[] params) {

    }
}
