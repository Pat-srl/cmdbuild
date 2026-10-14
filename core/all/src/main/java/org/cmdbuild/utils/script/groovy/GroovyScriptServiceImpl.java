/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.script.groovy;

import static java.lang.String.format;
import jakarta.annotation.Nullable;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.normalizeId;
import org.springframework.stereotype.Component;

@Component
public class GroovyScriptServiceImpl implements GroovyScriptService {

    private final CmCache<GroovyScriptExecutor> cache;

    public GroovyScriptServiceImpl(CacheService cacheService) {
        cache = cacheService.newCache("groovy_script_executors");
    }

    @Override
    public GroovyScriptExecutor getScriptExecutor(String scriptContent, @Nullable ClassLoader customClassLoader) {
        return doGetScriptExecutor(createClassname(hash(scriptContent, 8)), scriptContent, customClassLoader);
    }

    @Override
    public GroovyScriptExecutor getScriptExecutor(String className, String scriptContent, @Nullable ClassLoader customClassLoader) {
        return doGetScriptExecutor(format("%s_%s", checkNotBlank(className), hash(scriptContent, 4)), scriptContent, customClassLoader);
    }

    private GroovyScriptExecutor doGetScriptExecutor(String className, String scriptContent, @Nullable ClassLoader customClassLoader) {
        if (customClassLoader != null) {
            return new GroovyScriptExecutorImpl(className, scriptContent, customClassLoader);
        } else {
            return cache.get(className, () -> {
                return new GroovyScriptExecutorImpl(className, scriptContent, customClassLoader);
            });
        }
    }

    private static String createClassname(String key) {
        return format("org.cmdbuild.utils.groovy.GroovyScript_%s", normalizeId(key));
    }

}
