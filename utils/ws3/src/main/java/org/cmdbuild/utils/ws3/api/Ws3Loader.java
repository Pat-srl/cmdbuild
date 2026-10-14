/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.api;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import jakarta.ws.rs.Path;
import org.cmdbuild.utils.ws3.inner.WsResourceBeanWithInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

@Component
public class Ws3Loader implements WsResourceRepository {//TODO improve this; load beans using spring 5 index

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ApplicationContext applicationContext;

    public Ws3Loader(ApplicationContext applicationContext) {
        this.applicationContext = checkNotNull(applicationContext);
    }

    @Override
    public Iterable<WsResourceBeanWithInterface> getResources() {
        try (ScanResult scanResult = new ClassGraph()
                .acceptPackages("org.cmdbuild.service.rest.v3.endpoint")
                .enableAnnotationInfo().scan()) {
            return scanResult
                    .getClassesWithAnnotation(Path.class)
                    .stream()
                    .map(t -> new WsResourceBeanWithInterface(t.loadClass(), applicationContext.getAutowireCapableBeanFactory().createBean(t.loadClass())))
                    .collect(toList());
        }
    }
}
