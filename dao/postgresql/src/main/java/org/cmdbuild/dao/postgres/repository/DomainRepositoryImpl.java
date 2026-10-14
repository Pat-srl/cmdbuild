/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.repository;

import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Maps.filterKeys;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.sql.ResultSet;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import org.cmdbuild.cache.CacheConfig;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.dao.DaoException;
import static org.cmdbuild.dao.DaoConst.DOMAIN_PREFIX;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.domainNameToSqlTable;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.DomainImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.DomainMetadata;
import org.cmdbuild.dao.event.AttributeGroupModifiedEvent;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainDefinition;
import org.cmdbuild.dao.driver.repository.InnerAttributeRepository;
import org.cmdbuild.dao.entrytype.ClassPermissionMode;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import org.cmdbuild.cache.CmCache;
import static org.cmdbuild.common.Constants.BASE_DOMAIN_NAME;
import org.cmdbuild.dao.beans.DomainMetadataImpl;
import org.cmdbuild.dao.driver.repository.DomainStructureChangedEvent;
import static org.cmdbuild.dao.DaoConst.COMMENT_ACTIVE;
import static org.cmdbuild.dao.DaoConst.COMMENT_MODE;
import static org.cmdbuild.dao.DaoConst.COMMENT_TYPE;
import org.cmdbuild.dao.driver.repository.ClassDeletedEvent;
import static org.cmdbuild.dao.postgres.utils.CommentUtils.DOMAIN_COMMENT_TO_METADATA_MAPPING;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import org.cmdbuild.dao.event.AttributesModifiedEvent;
import org.cmdbuild.dao.graph.ClasseHierarchyService;
import static org.cmdbuild.dao.utils.DomainUtils.serializeDomainCardinality;
import org.cmdbuild.eventbus.EventBusService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
@Primary
public class DomainRepositoryImpl implements DomainRepository {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Holder<List<Domain>> allDomainsCache;
    private final CmCache<Optional<Domain>> domainsCacheByName;
    private final CmCache<List<Domain>> domainsForClasse;

    private final InnerAttributeRepository attributesRepository;
    private final ClasseRepository classRepository;
    private final JdbcTemplate jdbcTemplate;
    private final EventBus eventBus;
    private final ClasseHierarchyService classeHierarchyService;

    public DomainRepositoryImpl(ClasseHierarchyService classeHierarchyService, CacheService cacheService, @Qualifier(SYSTEM_LEVEL_ONE) JdbcTemplate jdbcTemplate, InnerAttributeRepository attributesRepository, ClasseRepository classRepository, EventBusService eventBusService) {
        this.jdbcTemplate = checkNotNull(jdbcTemplate);
        this.attributesRepository = checkNotNull(attributesRepository);
        this.classRepository = checkNotNull(classRepository);
        this.classeHierarchyService = checkNotNull(classeHierarchyService);

        allDomainsCache = cacheService.newHolder("dao_all_domains", CacheConfig.SYSTEM_OBJECTS);
        domainsCacheByName = cacheService.newCache("dao_domains_by_name", CacheConfig.SYSTEM_OBJECTS);
        domainsForClasse = cacheService.newCache("dao_domains_for_classe", CacheConfig.SYSTEM_OBJECTS);

        eventBus = eventBusService.getDaoEventBus();
        eventBus.register(new Object() {
            @Subscribe
            public void handleAttributeModifiedEvent(AttributesModifiedEvent event) {
                if (event.getOwner().isDomain()) {
                    invalidateOurCache();
                    eventBus.post(DomainStructureChangedEvent.affecting(event.getOwner().getId()));
                }
            }

            @Subscribe
            public void handleAttributeGroupModifiedEvent(AttributeGroupModifiedEvent event) {
                invalidateOurCache();
                eventBus.post(DomainStructureChangedEvent.affectingAll());//TODO check this
            }

            @Subscribe
            public void handleClassStructureChangeEvent(ClassDeletedEvent event) {
                invalidateOurCache();
            }
        });
    }

    /**
     * Used for <code>FORMULA</code> {@link Attribute} workaround.
     * 
     * @param domainName
     * @return 
     */
    public void invalidateCache_Domain(String domainName) {        
        // In getDomainOrnull(String) only domainsCacheByName is used
        domainsCacheByName.invalidate(domainName);
    }    

    private void invalidateOurCache() {
        allDomainsCache.invalidate();
        domainsCacheByName.invalidateAll();
        domainsForClasse.invalidateAll();
    }

    @Override
    @Nullable
    public Domain getDomainOrNull(Long id) {
        return getAllDomains().stream().filter((d) -> equal(d.getId(), id)).collect(toOptional()).orElse(null);
    }

    @Override
    @Nullable
    public Domain getDomainOrNull(@Nullable String domainId) {
        if (isBlank(domainId)) {
            return null;
        } else if (equal(domainId, BASE_DOMAIN_NAME)) {
            return DomainImpl.builder()//TODO root domain, check this
                    .withName(BASE_DOMAIN_NAME)
                    .withClass1(classRepository.getRootClass())//TODO check this
                    .withClass2(classRepository.getRootClass())
                    .withId(Long.MAX_VALUE)
                    .build();
        } else {
            return domainsCacheByName.get(domainId, () -> Optional.ofNullable(doFindDomain(domainId))).orElse(null);
        }
    }

    @Override
    public List<Domain> getAllDomains() {
        logger.trace("getting all domains");
        return allDomainsCache.get(this::doFindAllDomains);
    }

    @Override
    public Domain createDomain(DomainDefinition definition) {
        logger.info("creating domain = {}", definition.getName());
        doCreateDomain(definition);
        invalidateOurCache();
        Domain domain = getDomain(definition.getName());
        eventBus.post(new DomainCreatedEventImpl(domain));
        return domain;
    }

    @Override
    public Domain updateDomain(DomainDefinition definition) {
        logger.info("updating domain = {}", definition.getName());
        doUpdateDomain(definition);
        invalidateOurCache();
        Domain domain = getDomain(definition.getName());
        eventBus.post(DomainStructureChangedEvent.affecting(domain.getId()));
        return domain;
    }

    @Override
    public void deleteDomain(Domain domain) {
        logger.info("deleting domain = {}", domain.getName());
        doDeleteDomain(domain);//TODO remove cast
        invalidateOurCache();
        eventBus.post(DomainStructureChangedEvent.affecting(domain.getId()));//TODO check this
    }

    @Override
    public List<Domain> getDomainsForClasse(Classe classe) {
        checkNotNull(classe);
        return domainsForClasse.get(classe.getName(), () -> doGetDomainsForClasse(classe));
    }

    private List<Domain> doGetDomainsForClasse(Classe classe) {
        return getAllDomains().stream()
                //                .filter(Domain::isActive)
                .filter((d) -> d.isDomainForClasse(classe))
                .collect(toList());
    }

    private @Nullable
    Domain doFindDomain(String localname) {
        return getAllDomains().stream().filter((domain) -> equal(domain.getName(), localname)).findAny().orElse(null);
    }

    private List<Domain> doFindAllDomains() {
        return jdbcTemplate.query("WITH q AS (SELECT d::oid::int _id, _cm3_utils_regclass_to_name(d) _name, _cm3_class_features_get(d) _features FROM _cm3_domain_list() d) SELECT * FROM q ORDER BY _name", (rs, i) -> rowToDomain(rs));
    }

    private Domain rowToDomain(ResultSet rs) {
        Long id = null;
        String name = "<unknown_name>";
        try {
            id = rs.getLong("_id");
            name = tableNameToDomainName(rs.getString("_name"));

            Map<String, String> features = map((Map<String, String>) fromJson(rs.getString("_features"), MAP_OF_STRINGS));

            DOMAIN_COMMENT_TO_METADATA_MAPPING.forEach((k, v) -> features.put(v, features.remove(k)));

            DomainMetadata meta = new DomainMetadataImpl(features);
            Collection<AttributeWithoutOwner> attributes = list(attributesRepository.getNonReservedEntryTypeAttributesForType(id)).with(meta.getVirtualAttributes());

            return DomainImpl.builder()
                    .withName(name)
                    .withId(id)
                    .withMetadata(meta)
                    .withAllAttributes(attributes)
                    .withClass1(classeHierarchyService.getClasseHierarchy(meta.get(DomainMetadata.CLASS_1)))
                    .withClass2(classeHierarchyService.getClasseHierarchy(meta.get(DomainMetadata.CLASS_2)))
                    .build();
        } catch (Exception ex) {
            throw new DaoException(ex, "error processing domain = %s %s", firstNonNull(id, "<unknown oid>"), name);
        }
    }

    private static String tableNameToDomainName(String tableName) {
        checkArgument(tableName.startsWith(DOMAIN_PREFIX), "invalid domain table name = %s", tableName);
        return tableName.substring(DOMAIN_PREFIX.length());
    }

    private void doCreateDomain(DomainDefinition definition) {
        String domainComment = buildCommentJsonString(definition);
        jdbcTemplate.queryForObject("SELECT _cm3_domain_create(?, ?::jsonb)", Long.class, definition.getName(), domainComment);
    }

    private void doUpdateDomain(DomainDefinition definition) {
        String domainComment = buildCommentJsonString(definition);
        jdbcTemplate.queryForObject("SELECT _cm3_domain_modify(?::regclass, ?::jsonb)", Object.class, quoteSqlIdentifier(domainNameToSqlTable(definition.getName())), domainComment);
    }

    private void doDeleteDomain(Domain domain) {
        jdbcTemplate.queryForObject("SELECT _cm3_domain_delete(?::regclass)", Object.class, quoteSqlIdentifier(domainNameToSqlTable(domain.getName())));
    }

    private String buildCommentJsonString(DomainDefinition definition) {
        return toJson(map(filterKeys(definition.getMetadata().getAll(), not(DOMAIN_COMMENT_TO_METADATA_MAPPING.inverse().keySet()::contains))).with(
                "LABEL", definition.getMetadata().getDescription(),
                "DESCRDIR", defaultIfBlank(definition.getMetadata().getDirectDescription(), EMPTY),
                "DESCRINV", defaultIfBlank(definition.getMetadata().getInverseDescription(), EMPTY),
                COMMENT_MODE, serializeEnum(ClassPermissionMode.CPM_DEFAULT),
                COMMENT_TYPE, "domain",
                "CLASS1", definition.getSourceClassName(),
                "CLASS2", definition.getTargetClassName(),
                "CARDIN", serializeDomainCardinality(definition.getMetadata().getCardinality()),
                "MASTERDETAIL", Boolean.toString(definition.getMetadata().isMasterDetail()),
                "MDLABEL", defaultIfBlank(definition.getMetadata().getMasterDetailDescription(), EMPTY),
                "MDFILTER", defaultIfBlank(definition.getMetadata().getMasterDetailFilter(), EMPTY),
                "DISABLED1", serializeParamList(definition.getMetadata().getDisabledSourceDescendants()),
                "DISABLED2", serializeParamList(definition.getMetadata().getDisabledTargetDescendants()),
                "INDEX1", Integer.toString(definition.getMetadata().getIndexForSource()),
                "INDEX2", Integer.toString(definition.getMetadata().getIndexForTarget()),
                "CASCADEDIRECT", serializeEnum(definition.getMetadata().getCascadeActionDirect()),
                "CASCADEINVERSE", serializeEnum(definition.getMetadata().getCascadeActionInverse())
        ).accept((m) -> {
            if (!definition.getMetadata().isActive()) {
                m.put(COMMENT_ACTIVE, Boolean.FALSE.toString());
            }
        }));
    }

    private static String serializeParamList(Iterable<String> values) {
        return Joiner.on(",").skipNulls().join(defaultIfNull(values, emptyList()));
    }

    private static class DomainCreatedEventImpl implements DomainStructureChangedEvent {

        private final Domain domain;

        public DomainCreatedEventImpl(Domain domain) {
            this.domain = checkNotNull(domain);
        }

        @Override
        public boolean impactAllDomains() {
            return false;
        }

        @Override
        public Set<Long> getAffectedDomainOids() {
            return singleton(domain.getId());
        }

    }

}
