package org.cmdbuild.dao.postgres.repository;

import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.Nullable;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.auth.multitenant.config.MultitenantConfiguration;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.cache.Holder;
import static org.cmdbuild.common.Constants.BASE_CLASS_NAME;
import org.cmdbuild.common.beans.TypeAndCode;
import static org.cmdbuild.dao.DaoConst.COMMENT_ACTIVE;
import static org.cmdbuild.dao.DaoConst.COMMENT_DESCR;
import static org.cmdbuild.dao.DaoConst.COMMENT_FLOW_SAVE_BUTTON_ENABLED;
import static org.cmdbuild.dao.DaoConst.COMMENT_FLOW_STATUS_ATTR;
import static org.cmdbuild.dao.DaoConst.COMMENT_MODE;
import static org.cmdbuild.dao.DaoConst.COMMENT_MULTITENANT_MODE;
import static org.cmdbuild.dao.DaoConst.COMMENT_SUPERCLASS;
import static org.cmdbuild.dao.DaoConst.COMMENT_TYPE;
import static org.cmdbuild.dao.DaoConst.COMMENT_TYPE_CLASS;
import static org.cmdbuild.dao.DaoConst.COMMENT_TYPE_SIMPLECLASS;
import static org.cmdbuild.dao.DaoConst.COMMENT_USERSTOPPABLE;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.beans.ClassMetadataImpl;
import org.cmdbuild.dao.driver.repository.AttributeGroupRepository;
import org.cmdbuild.dao.driver.repository.ClassDeletedEvent;
import org.cmdbuild.dao.driver.repository.ClassStructureChangedEvent;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.driver.repository.InnerAttributeRepository;
import org.cmdbuild.dao.entrytype.AttributeGroupData;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.ClassDefinition;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.ClassMetadata;
import static org.cmdbuild.dao.entrytype.ClassMetadata.ATTACHMENTS_INLINE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.ATTACHMENTS_INLINE_CLOSED;
import static org.cmdbuild.dao.entrytype.ClassMetadata.CLASS_SPECIALITY;
import static org.cmdbuild.dao.entrytype.ClassMetadata.CLASS_TYPE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.DEFAULT_EXPORT_TEMPLATE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.DEFAULT_FILTER;
import static org.cmdbuild.dao.entrytype.ClassMetadata.DEFAULT_IMPORT_TEMPLATE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.DMS_CATEGORY;
import static org.cmdbuild.dao.entrytype.ClassMetadata.DOMAIN_ORDER;
import static org.cmdbuild.dao.entrytype.ClassMetadata.NOTE_INGRID;
import static org.cmdbuild.dao.entrytype.ClassMetadata.NOTE_INLINE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.NOTE_INLINE_CLOSED;
import static org.cmdbuild.dao.entrytype.ClassMetadata.VALIDATION_RULE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.WORKFLOW_PROVIDER;
import static org.cmdbuild.dao.entrytype.ClassMultitenantMode.CMM_NEVER;
import static org.cmdbuild.dao.entrytype.ClassMultitenantModeUtils.serializeClassMultitenantMode;
import org.cmdbuild.dao.entrytype.ClassType;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.serializeClassPermissionMode;
import static org.cmdbuild.dao.entrytype.EntryTypeMetadata.VIRTUAL_ATTRIBUTES_PREFIX;
import static org.cmdbuild.dao.entrytype.EntryTypeType.ET_CLASS;
import org.cmdbuild.dao.event.AttributeGroupModifiedEvent;
import org.cmdbuild.dao.event.ClassCreatedEvent;
import static org.cmdbuild.dao.postgres.utils.CommentUtils.CLASS_COMMENT_TO_METADATA_MAPPING;
import static org.cmdbuild.dao.postgres.utils.CommentUtils.parseCommentFromFeatures;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import org.cmdbuild.eventbus.EventBusService;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Primary
public class ClasseRepositoryImpl implements ClasseRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final JdbcTemplate jdbcTemplate;
    private final EventBus eventBus;
    private final InnerAttributeRepository attributesRepository;
    private final MultitenantConfiguration multitenantConfiguration;
    private final AttributeGroupRepository attributeGroupRepository;

    private final Holder<List<Classe>> allClassesCache;
    private final CmCache<Optional<Classe>> classesCacheByOid;
    private final CmCache< Optional<Classe>> classesCacheByName;

    public ClasseRepositoryImpl(AttributeGroupRepository attributeGroupRepository, MultitenantConfiguration multitenantConfiguration, InnerAttributeRepository attributesRepository, CacheService cacheService, @Qualifier(SYSTEM_LEVEL_ONE) JdbcTemplate jdbcTemplate, AttributeGroupRepository groupRepository, EventBusService eventBusService) {
        this.jdbcTemplate = checkNotNull(jdbcTemplate);
        this.attributesRepository = checkNotNull(attributesRepository);
        this.multitenantConfiguration = checkNotNull(multitenantConfiguration);
        this.attributeGroupRepository = checkNotNull(attributeGroupRepository);
        allClassesCache = cacheService.newHolder("org.cmdbuild.database.classes.all", CacheConfig.SYSTEM_OBJECTS);
        classesCacheByOid = cacheService.newCache("org.cmdbuild.database.classes.by_oid", CacheConfig.SYSTEM_OBJECTS);
        classesCacheByName = cacheService.newCache("org.cmdbuild.database.classes.by_name", CacheConfig.SYSTEM_OBJECTS);
        eventBus = eventBusService.getDaoEventBus();
        eventBus.register(new Object() {
            @Subscribe
            public void handleClassStructureChangedEvent(ClassStructureChangedEvent event) {
                invalidateOurCache();
            }

            @Subscribe
            public void handleAttributeGroupModifiedEvent(AttributeGroupModifiedEvent event) {
                eventBus.post(ClassStructureChangedEvent.affectingAll());//TODO invalidate only affected classes (check attr group scope)
            }
        });
    }

    private void invalidateOurCache() {
        logger.debug("drop class cache");
        allClassesCache.invalidate();
        classesCacheByOid.invalidateAll();
        classesCacheByName.invalidateAll();
    }

    private void sendClassStructureChangedEvent(long affectedClassOid) {
        eventBus.post(ClassStructureChangedEvent.affecting(affectedClassOid));
    }

    private void sendClassDeletedEvent(long affectedClassOid) {
        eventBus.post(ClassDeletedEvent.affecting(affectedClassOid));
    }

    @Override
    public List<Classe> getAllClasses() {
        return allClassesCache.get(this::doGetAllClasses);
    }

    @Override
    public Classe getClasseOrNull(String name) {
        checkNotBlank(name, "name param is null");
        return classesCacheByName.get(name, () -> {
            return getAllClasses().stream().filter((classe) -> equal(classe.getName(), name)).findAny();
        }).orElse(null);
    }

    @Override
    @Nullable
    public Classe getClasseOrNull(long oid) {
        return classesCacheByOid.get(String.valueOf(oid), () -> {
            return getAllClasses().stream().filter((classe) -> classe.getOid() == oid).findAny();
        }).orElse(null);
    }

    @Override
    public Classe createClass(ClassDefinition definition) {
        checkNotNull(definition, "class definition is null");
        logger.info("create class =< {} >", definition.getName());
        checkClassDefinition(definition);
        try {
            String parentName = definition.getParentOrNull();
            Classe parent = isBlank(parentName) ? null : getClasse(parentName);
            checkArgument(parent == null || parent.isSuperclass(), "cannot extend parent class = %s, it is not a valid superclass/prototype!", parent);
            if (definition.getMetadata().holdsHistory() && parent == null) {
                parent = getRootClass();
            }
            String classComment = buildFeaturesJsonString(definition);
            String name = definition.getName();
            logger.info("create class = {}", definition);
            long oid = jdbcTemplate.queryForObject("SELECT x::oid FROM _cm3_class_create(?, ?::regclass, ?::jsonb) x", Long.class, name, parent == null ? null : quoteSqlIdentifier(parent.getName()), classComment);
            sendClassStructureChangedEvent(oid);
            Classe classe = getClasse(oid);
            eventBus.post(new ClassCreatedEventImpl(classe));
            return classe;
        } catch (Exception ex) {
            throw new DaoException(ex, "error while creating new class from definition = %s", definition);
        }
    }

    @Override
    public Classe updateClass(ClassDefinition definition) {
        checkNotNull(definition, "class definition is null");
        logger.info("updating class = {}", definition.getName());
        checkClassDefinition(definition);
        String comment = buildFeaturesJsonString(definition);
        logger.info("update class = {}", definition);
        jdbcTemplate.queryForObject("SELECT _cm3_class_modify(?::regclass, ?::jsonb)", Object.class, quoteSqlIdentifier(definition.getName()), comment);
        sendClassStructureChangedEvent(definition.getOid());
        return getClasse(definition.getName());
    }

    @Override
    public ClassDefinition retrieveClassOid(ClassDefinition definition) {
        checkNotNull(definition, "class definition is null");
        if (definition.getOid() != null) {
            return definition;
        }

        logger.info("retrieving class oid for class = {}", definition.getName());
        String name = definition.getName();
        Long oid = jdbcTemplate.queryForObject("SELECT _cm3_utils_name_to_regclass(?)::oid", Long.class, name);
        ClassDefinition result = ClassDefinitionImpl.copyOf(definition).withOid(oid).build();
        return result;
    }

    @Override
    public void deleteClass(Classe classe) {
        checkNotNull(classe, "class is null");
        logger.info("deleting class = {}", classe.getName());
        jdbcTemplate.queryForObject("SELECT * FROM _cm3_class_delete(?::regclass)", Object.class, quoteSqlIdentifier(classe.getName()));
        sendClassDeletedEvent(classe.getOid());
    }

    private List<Classe> doGetAllClasses() {
        logger.debug("findAllClasses");
        List<Classe> list = jdbcTemplate.query("SELECT * FROM _cm3_class_list_detailed()",
                (rs, i) -> {

                    Long id = null;
                    String name = "<unknown>";
                    try {
                        id = checkNotNullAndGtZero(rs.getLong("table_id"));
                        name = checkNotBlank(rs.getString("table_name"));
                        String featuresStr = rs.getString("features");
                        Map<String, String> features = fromJson(featuresStr, MAP_OF_STRINGS);
                        ClassMetadata meta = new ClassMetadataImpl(map(features)
                                .withoutKeys(CLASS_COMMENT_TO_METADATA_MAPPING.keySet())
                                .withoutKeys(CLASS_COMMENT_TO_METADATA_MAPPING.values())
                                .with(parseCommentFromFeatures(features, CLASS_COMMENT_TO_METADATA_MAPPING)));
                        List<String> ancestors;
                        if (meta.isSimpleClass() || equal(name, BASE_CLASS_NAME)) {
                            ancestors = emptyList();
                        } else {
                            ancestors = convert(rs.getArray("ancestor_names"), List.class);
                        }
                        List<AttributeGroupData> attributeGroups = attributeGroupRepository.getAttributeGroupsForEntryType(name, ET_CLASS);
                        Collection<AttributeWithoutOwner> attributes = list(attributesRepository.getNonReservedEntryTypeAttributesForType(id)).with(meta.getVirtualAttributes());
                        return (Classe) ClasseImpl.builder()
                                .withName(name)
                                .withId(id)
                                .withMetadata(meta)
                                .withAttributes(attributes)
                                .withAttributeGroups(attributeGroups)
                                .withAncestors(ancestors)
                                .build();
                    } catch (Exception ex) {
                        throw new DaoException(ex, "error processing class oid = %s name =< %s >", id, name);

                    }
                }).stream().sorted(Ordering.natural().onResultOf(Classe::getName)).collect(toImmutableList());
        logger.debug("loaded {} classes from db", list.size());
        logger.trace("loaded classes from db = {}", lazyString(() -> list.toString()));
        return list;
    }

    private void checkClassDefinition(ClassDefinition classe) {
        checkArgument(multitenantConfiguration.isMultitenantEnabled() || equal(classe.getMetadata().getMultitenantMode(), CMM_NEVER), "cannot set class multitenant mode = %s, multitenant is disabled", classe.getMetadata().getMultitenantMode());
    }

    private String buildFeaturesJsonString(ClassDefinition definition) {
        ClassMetadata metadata = definition.getMetadata();
        return toJson(map(metadata.getCustomMetadata()).with(map(metadata.getAll()).filterKeys(k -> k.startsWith(VIRTUAL_ATTRIBUTES_PREFIX))).with(
                COMMENT_DESCR, metadata.getDescription(),
                COMMENT_MODE, serializeClassPermissionMode(metadata.getMode()),
                COMMENT_SUPERCLASS, metadata.isSuperclass(),
                COMMENT_TYPE, checkNotNull(map(ClassType.CT_SIMPLE, COMMENT_TYPE_SIMPLECLASS, ClassType.CT_STANDARD, COMMENT_TYPE_CLASS).get(metadata.getClassType()))
        ).skipNullValues().with(
                DMS_CATEGORY, emptyToNull(metadata.getDmsCategoryOrNull()),
                COMMENT_MULTITENANT_MODE, equal(metadata.getMultitenantMode(), CMM_NEVER) ? null : serializeClassMultitenantMode(metadata.getMultitenantMode()),
                DEFAULT_FILTER, metadata.getDefaultFilterOrNull(),
                DEFAULT_IMPORT_TEMPLATE, Optional.ofNullable(metadata.getDefaultImportTemplateOrNull()).map(TypeAndCode::serialize).orElse(null),
                DEFAULT_EXPORT_TEMPLATE, Optional.ofNullable(metadata.getDefaultExportTemplateOrNull()).map(TypeAndCode::serialize).orElse(null),
                NOTE_INLINE, metadata.getNoteInline(),
                NOTE_INLINE_CLOSED, metadata.getNoteInlineClosed(),
                NOTE_INGRID, metadata.getNoteInGrid(),
                ATTACHMENTS_INLINE, metadata.getAttachmentsInline(),
                ATTACHMENTS_INLINE_CLOSED, metadata.getAttachmentsInlineClosed(),
                VALIDATION_RULE, metadata.getValidationRuleOrNull(),
                DOMAIN_ORDER, emptyToNull(Joiner.on(",").join(metadata.getDomainOrder()))
        ).accept((m) -> {
            if (definition.hasParent() && getClasse(definition.getParentOrNull()).isProcess()) {
                m.skipNullValues().with(
                        COMMENT_FLOW_STATUS_ATTR, emptyToNull(metadata.getFlowStatusAttr()),
                        COMMENT_FLOW_SAVE_BUTTON_ENABLED, metadata.isFlowSaveButtonEnabled(),
                        COMMENT_USERSTOPPABLE, metadata.isWfUserStoppable() ? true : null,//TODO improve default value handling
                        WORKFLOW_PROVIDER, emptyToNull(metadata.getFlowProviderOrNull())
                );
            }
            if (!metadata.isActive()) {
                m.put(COMMENT_ACTIVE, Boolean.FALSE.toString());
            }
        }).withoutKeys(CLASS_SPECIALITY, CLASS_TYPE));
    }

    private static class ClassCreatedEventImpl implements ClassCreatedEvent {

        private final Classe classe;

        public ClassCreatedEventImpl(Classe classe) {
            this.classe = checkNotNull(classe);
        }

//        @Override
//        public EntryTypeOrAttribute getCreatedItem() {
//            return classe;
//        }
    }
}
