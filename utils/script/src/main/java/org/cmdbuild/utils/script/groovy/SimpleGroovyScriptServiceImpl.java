/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.script.groovy;

import static java.lang.String.format;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;

public class SimpleGroovyScriptServiceImpl implements GroovyScriptService {

    @Override
    public GroovyScriptExecutor getScriptExecutor(String scriptContent, @Nullable ClassLoader customClassLoader) {
        return getScriptExecutor(randomClassName(), scriptContent, customClassLoader);
    }

    @Override
    public GroovyScriptExecutor getScriptExecutor(String className, String scriptContent, @Nullable ClassLoader customClassLoader) {
        return new GroovyScriptExecutorImpl(className, scriptContent, customClassLoader);
    }

    public static String randomClassName() {
        return format("GroovyScript_%s", randomId(8));
    }

}
