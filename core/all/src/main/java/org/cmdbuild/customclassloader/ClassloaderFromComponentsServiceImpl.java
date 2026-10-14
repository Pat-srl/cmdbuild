package org.cmdbuild.customclassloader;

import org.cmdbuild.utils.classpath.SingleClassLoader;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.capitalize;
import org.cmdbuild.corecomponents.CoreComponent;
import org.cmdbuild.corecomponents.CoreComponentService;
import org.cmdbuild.utils.script.groovy.GroovyScriptExecutorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ClassloaderFromComponentsServiceImpl implements ClassloaderFromComponentsService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CoreComponentService componentService;

    public ClassloaderFromComponentsServiceImpl(CoreComponentService componentService) {
        this.componentService = checkNotNull(componentService);
    }

    @Override
    @Nullable
    public ClassLoaderHelper getClassLoaderFromComponentOrNull(String code) {
        CoreComponent component = componentService.getComponentOrNull(code);
        if (component == null) {
            return null;
        } else {
            logger.debug("get classloader from script component = {}", component);
            return new ClassLoaderHelperImpl(component);
        }
    }

    private class ClassLoaderHelperImpl implements ClassLoaderHelper {

        private final CoreComponent component;
        private final String scriptContent, className;

        public ClassLoaderHelperImpl(CoreComponent component) {
            this.component = checkNotNull(component);
            scriptContent = component.getData();
            className = format("org.cmdbuild.core.component.script.%s", capitalize(component.getCode()));
        }

        @Override
        public ClassLoader getClassLoader(ClassLoader parent) {
            Class<?> groovyClass = new GroovyScriptExecutorImpl(className, scriptContent, parent).getGroovyClass();
            logger.debug("build classloader from script component = {} with class =< {} >", component, groovyClass.getName());
            return new SingleClassLoader(groovyClass, parent);
        }

    }

}
