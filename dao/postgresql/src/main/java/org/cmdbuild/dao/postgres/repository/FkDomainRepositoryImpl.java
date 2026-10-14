/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.repository;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.Subscribe;
import java.util.List;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.dao.driver.repository.ClassStructureChangedEvent;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.driver.repository.FkDomainRepository;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.CascadeAction;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.DomainCardinality;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_ONE;
import static org.cmdbuild.dao.entrytype.DomainCardinality.ONE_TO_MANY;
import org.cmdbuild.dao.entrytype.FkDomain;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FOREIGNKEY;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.eventbus.EventBusService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.springframework.stereotype.Component;

@Component
public class FkDomainRepositoryImpl implements FkDomainRepository {

    private final ClasseRepository classeRepository;
    private final Holder<List<FkDomain>> fkdomains;
    private final CmCache<List<FkDomain>> cache;

    public FkDomainRepositoryImpl(ClasseRepository classeRepository, CacheService cacheService, EventBusService eventService) {
        this.classeRepository = checkNotNull(classeRepository);
        fkdomains = cacheService.newHolder("fk_domains_all", CacheConfig.SYSTEM_OBJECTS);
        cache = cacheService.newCache("fk_domains_by_class", CacheConfig.SYSTEM_OBJECTS);
        eventService.getDaoEventBus().register(new Object() {
            @Subscribe
            public void handleClassStructureChangedEvent(ClassStructureChangedEvent event) {
                invalidateOurCache();
            }

        });
    }

    private void invalidateOurCache() {
        fkdomains.invalidate();
        cache.invalidateAll();
    }

    @Override
    public List<FkDomain> getAllFkDomains() {
        return fkdomains.get(this::doGetAllFkDomains);
    }

    @Override
    public List<FkDomain> getFkDomainsForClass(String classId) {
        return cache.get(classId, () -> doGetFkDomainsForClass(classId));
    }

    private List<FkDomain> doGetAllFkDomains() {
        return classeRepository.getAllClasses().stream().flatMap(c -> c.getAllAttributes().stream()).filter(a -> a.isOfType(FOREIGNKEY)).map(a -> new FkDomainImpl(a)).collect(toImmutableList());
    }

    private List<FkDomain> doGetFkDomainsForClass(String classId) {
        Classe classe = classeRepository.getClasse(classId);
        return listOf(FkDomain.class).accept(l -> {
            getAllFkDomains().stream().filter(d -> equal(d.getSourceClass().getName(), classe.getName())).forEach(l::add);
            getAllFkDomains().stream().filter(d -> equal(d.getTargetClass().getName(), classe.getName())).map(d -> new FkDomainImpl(d)).forEach(l::add);
        }).stream().sorted(Ordering.natural().onResultOf(d -> d.getTargetClass().getName())).collect(toImmutableList());
    }

    private class FkDomainImpl implements FkDomain {

        private final Classe source, target;
        private final Attribute attribute;
        private final CascadeAction cascadeAction;
        private final boolean isMasterDetail;
        private final String masterDetailDescription;
        private final DomainCardinality cardinality;

        public FkDomainImpl(Attribute attribute) {
            this.attribute = checkNotNull(attribute);
            ForeignKeyAttributeType attributeType = attribute.getType().as(ForeignKeyAttributeType.class);
            this.source = attribute.getOwnerClass();
            this.target = classeRepository.getClasse(attributeType.getForeignKeyDestinationClassName());
            this.cascadeAction = attributeType.getForeignKeyCascadeAction();
            this.isMasterDetail = attribute.getMetadata().isMasterDetail();
            this.masterDetailDescription = firstNotBlank(attribute.getMetadata().getMasterDetailDescription(), target.getDescription());
            this.cardinality = MANY_TO_ONE;
        }

        public FkDomainImpl(FkDomain directDomainToReverse) {
            checkArgument(directDomainToReverse.isDirect());
            this.attribute = directDomainToReverse.getSourceAttr();
            this.source = directDomainToReverse.getTargetClass();
            this.target = directDomainToReverse.getSourceClass();
            this.cascadeAction = directDomainToReverse.getCascadeAction();
            this.isMasterDetail = directDomainToReverse.isMasterDetail();
            this.masterDetailDescription = firstNotBlank(attribute.getMetadata().getMasterDetailDescription(), target.getDescription());
            this.cardinality = ONE_TO_MANY;
        }

        @Override
        public Attribute getSourceAttr() {
            return attribute;
        }

        @Override
        public Classe getSourceClass() {
            return source;
        }

        @Override
        public Classe getTargetClass() {
            return target;
        }

        @Override
        public CascadeAction getCascadeAction() {
            return cascadeAction;
        }

        @Override
        public boolean isMasterDetail() {
            return isMasterDetail;
        }

        @Override
        public String getMasterDetailDescription() {
            return masterDetailDescription;
        }

        @Override
        public DomainCardinality getCardinality() {
            return cardinality;
        }

    }
}
