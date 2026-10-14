/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.repository;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import com.google.common.eventbus.Subscribe;
import java.util.Map;
import static java.util.function.Function.identity;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.dao.driver.repository.ClassStructureChangedEvent;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.graph.ClasseHierarchy;
import static org.cmdbuild.dao.graph.ClasseHierarchyUtils.buildClassHierarchy;
import org.cmdbuild.dao.graph.ClasseHierarchyService;
import static org.cmdbuild.utils.lang.CmMapUtils.toImmutableMap;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.event.AttributesModifiedEvent;
import org.cmdbuild.eventbus.EventBusService;
import org.springframework.context.annotation.Primary;

@Primary
@Component
public class ClasseHierarchyServiceImpl implements ClasseHierarchyService {

    private final ClasseRepository classeRepository;
    private final Holder<Map<String, ClasseHierarchy>> allHierarchyCache;

    public ClasseHierarchyServiceImpl(ClasseRepository classeRepository, EventBusService eventService, CacheService cacheService) {
        this.classeRepository = checkNotNull(classeRepository);
        allHierarchyCache = cacheService.newHolder("org.cmdbuild.database.classes.hierarchy.all", CacheConfig.SYSTEM_OBJECTS);
        eventService.getDaoEventBus().register(new Object() {
            @Subscribe
            public void handleClassStructureChangedEvent(ClassStructureChangedEvent event) {
                invalidateOurCache();
            }

            @Subscribe
            public void handleAttributeModifiedEvent(AttributesModifiedEvent event) {
                if (event.getOwner() instanceof Classe) {
                    eventService.getDaoEventBus().post(ClassStructureChangedEvent.affecting(transform(getClasseHierarchy((Classe) event.getOwner()).getDescendantsAndSelf(), Classe::getOid)));
                }
            }

        });
    }

    private void invalidateOurCache() {
        allHierarchyCache.invalidate();
    }

    @Override
    public ClasseHierarchy getClasseHierarchy(String classe) {
        return checkNotNull(allHierarchyCache.get(this::doGetClasseHierarchy).get(classe), "class hierarchy not found for class = %s", classe);
    }

    private Map<String, ClasseHierarchy> doGetClasseHierarchy() {
        return buildClassHierarchy(classeRepository.getAllClasses()).stream().collect(toImmutableMap(h -> h.getClasse().getName(), identity()));
    }

}
