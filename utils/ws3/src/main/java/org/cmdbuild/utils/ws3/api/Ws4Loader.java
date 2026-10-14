/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.api;

import static com.google.common.base.Preconditions.checkNotNull;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import jakarta.ws.rs.Path;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.utils.ws3.inner.WsResourceBeanWithInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class Ws4Loader implements WsResourceRepository {//TODO improve this; load beans using spring 5 index

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ApplicationContext applicationContext;

    public Ws4Loader(ApplicationContext applicationContext) {
        this.applicationContext = checkNotNull(applicationContext);
    }

    @Override
    public Iterable<WsResourceBeanWithInterface> getResources() {
        try (ScanResult scanResult = new ClassGraph()
                .acceptPackages("org.cmdbuild.service.rest.v4.endpoint", "org.cmdbuild.plugin.rest.v4.endpoint")
                .enableAnnotationInfo().scan()) {
            return scanResult
                    .getClassesWithAnnotation(Path.class)
                    .stream()
                    .map(t -> new WsResourceBeanWithInterface(t.loadClass(), applicationContext.getAutowireCapableBeanFactory().createBean(t.loadClass())))
                    .collect(toList());
        }
    }
}
