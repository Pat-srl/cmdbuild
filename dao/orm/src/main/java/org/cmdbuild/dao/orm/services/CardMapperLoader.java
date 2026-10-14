/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.orm.services;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import com.google.common.collect.Ordering;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.PostConstruct;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.cmdbuild.dao.orm.CardMapperConfigRepository;

@Component
public class CardMapperLoader {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CardMapperConfigRepository repository;

    public CardMapperLoader(CardMapperConfigRepository repository) {
        this.repository = checkNotNull(repository);
    }

    @PostConstruct
    public void scanClassesForHandlers() {
        logger.info("scanClassesForHandlers BEGIN");
        List<Class> classes = new MyClasspathScanner().get();
        scanClassesForHandlers(classes);
        logger.info("scanClassesForHandlers END - found {} classes", classes.size());
    }

    public void scanClassesForHandlers(List<Class> classes) {
        logger.debug("processing {} classes", classes.size());
        classes.stream().sorted(Ordering.natural().onResultOf(Class::getName)).forEach((thisClass) -> {
            logger.debug("processing class = {}", thisClass);
            repository.putConfig(new CardMapperConfigImpl(thisClass));
        });
    }

    private final class MyClasspathScanner extends ClassPathScanningCandidateComponentProvider implements Supplier<List<Class>> {

        public MyClasspathScanner() {
            super(false);
            addIncludeFilter(new AnnotationTypeFilter(CardMapping.class));
        }

        @Override
        public List<Class> get() {
            Set<BeanDefinition> beanDefinitions = this.findCandidateComponents("org.cmdbuild");
            List<Class> classes = beanDefinitions.stream().map((beanDefinition) -> {
                Class thisClass = ClassUtils.resolveClassName(beanDefinition.getBeanClassName(), ClassUtils.getDefaultClassLoader());
                return thisClass;
            }).collect(toList());
            return classes;
        }

    }

}
