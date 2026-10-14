package org.cmdbuild.dao.postgres.services;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import jakarta.annotation.Nullable;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.activitylog.ActivityLogAction.ALA_CARD_CREATE;
import static org.cmdbuild.activitylog.ActivityLogAction.ALA_CARD_DELETE;
import static org.cmdbuild.activitylog.ActivityLogAction.ALA_CARD_UPDATE;
import org.cmdbuild.activitylog.ActivityLogInfoImpl;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.ClassMetadataImpl;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.beans.DomainMetadataImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS;
import org.cmdbuild.dao.driver.PostgresService;
import org.cmdbuild.dao.driver.repository.AttributeGroupRepository;
import org.cmdbuild.dao.driver.repository.AttributeRepository;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.driver.repository.StoredFunctionRepository;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import static org.cmdbuild.dao.entrytype.AttributeMetadata.INDEX;
import static org.cmdbuild.dao.entrytype.AttributeMetadata.INHERITED;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.ClassDefinition;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainDefinition;
import org.cmdbuild.dao.entrytype.DomainDefinitionImpl;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.postgres.event.AfterCardCreateEventImpl;
import org.cmdbuild.dao.postgres.event.AfterCardDeleteEventImpl;
import org.cmdbuild.dao.postgres.event.AfterCardUpdateEventImpl;
import org.cmdbuild.dao.postgres.event.BeforeCardDeleteEventImpl;
import org.cmdbuild.dao.postgres.event.BeforeCardUpdateEventImpl;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import static org.cmdbuild.dao.utils.VirtualAttributeUtils.createVirtualAttribute;
import static org.cmdbuild.dao.utils.VirtualAttributeUtils.deleteVirtualAttribute;
import static org.cmdbuild.dao.utils.VirtualAttributeUtils.updateVirtualAttribute;
import org.cmdbuild.eventbus.EventBusService;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.INNER;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.SYSTEM_LEVEL_TWO;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * postgres driver factory; will cache stuff that does not depends on tenant (ie
 * operation user) at this level, while tenant dependant stuff will be included
 * within {@link PostgresServiceImpl} instances
 *
 */
@Component
@Qualifier(SYSTEM_LEVEL_TWO)
public class PostgresServiceImpl implements PostgresService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final PostgresDatabaseAdapterService databaseAdapterService;

    private final ClasseRepository classeRepository;
    private final DomainRepository domainRepository;
    private final EntryUpdateService entryUpdateService;
    private final StoredFunctionRepository functionRepository;
    private final EventBus cardEventBus;
    private final EventBus activityLogBus;
    private final AttributeRepository attributeRepository;

    public PostgresServiceImpl(EntryUpdateService entryUpdateService, AttributeRepository attributeRepository, @Qualifier(INNER) StoredFunctionRepository functionRepository, ClasseRepository classeRepository, DomainRepository domainRepository, PostgresDatabaseAdapterService databaseAdapterService, AttributeGroupRepository attributeGroupRepository, EventBusService eventBusService) {
        this.databaseAdapterService = checkNotNull(databaseAdapterService);
        this.cardEventBus = eventBusService.getCardEventBus();
        this.activityLogBus = eventBusService.getActivityLogEventBus();
        this.domainRepository = checkNotNull(domainRepository);
        this.classeRepository = checkNotNull(classeRepository);
        this.functionRepository = checkNotNull(functionRepository);
        this.attributeRepository = checkNotNull(attributeRepository);
        this.entryUpdateService = checkNotNull(entryUpdateService);
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return databaseAdapterService.getJdbcTemplate();
    }

    @Override
    public Classe getClasseOrNull(long id) {
        return classeRepository.getClasseOrNull(id);
    }

    @Override
    public Domain getDomainOrNull(Long id) {
        return domainRepository.getDomainOrNull(id);
    }

    @Override
    @Nullable
    public Domain getDomainOrNull(@Nullable String localname) {
        return domainRepository.getDomainOrNull(localname);
    }

    @Override
    public List<StoredFunction> getAllFunctions() {
        return functionRepository.getAllFunctions();
    }

    @Override
    @Nullable
    public StoredFunction getFunctionOrNull(@Nullable String name) {
        return functionRepository.getFunctionOrNull(name);
    }

    @Override
    public List<Classe> getAllClasses() {
        return classeRepository.getAllClasses();
    }

    @Override
    public Classe getClasseOrNull(String localname) {
        return classeRepository.getClasseOrNull(localname);
    }

    @Override
    public Classe createClass(ClassDefinition definition) {
        return classeRepository.createClass(definition);
    }

    @Override
    public Classe updateClass(ClassDefinition definition) {
        return classeRepository.updateClass(definition);
    }

    @Override
    public ClassDefinition retrieveClassOid(ClassDefinition definition) {
        return classeRepository.retrieveClassOid(definition);
    }

    @Override
    public void deleteClass(Classe classe) {
        classeRepository.deleteClass(classe);
    }

    @Override
    public Attribute createAttribute(Attribute attribute) {
        String attributeName = attribute.getName();
        checkArgument(!attribute.getOwner().hasAttribute(attributeName), "CM: cannot create attribute with name = %s: this attribute already exists", attributeName);
        checkArgument(getDescendantClasses(attribute.getOwner()).stream().filter(c -> c.hasAttribute(attributeName)).collect(toList()).isEmpty(), "CM: cannot create attribute with name = %s: this attribute already exists on %s", attributeName, getDescendantClasses(attribute.getOwner()).stream().filter(c -> c.hasAttribute(attributeName)).map(Classe::getName).collect(joining(", ")));
        if (attribute.isVirtual()) {
            expandVirtualAttributeToDescendantClasses(attribute, (meta, attr) -> createVirtualAttribute(meta, attr), true);
            attribute = AttributeImpl.copyOf(attribute).withMeta(INDEX, toStringOrNull(attribute.getOwner().getAllAttributes().stream().map(Attribute::getIndex).reduce(Integer::max).orElse(-1) + 1)).build();
            return updateEntryTypeMetadata(attribute.getOwner(), createVirtualAttribute(attribute.getOwner().getMetadata().getAll(), attribute))
                    .getAttribute(attribute.getName());
        } else {
            return attributeRepository.createAttribute(attribute);
        }
    }

    @Override
    public EntryType updateEntryTypeMetadata(EntryType owner, Map<String, String> entryTypeMetadata) {
        if (owner.isClasse()) {
            return updateClass(ClassDefinitionImpl.copyOf((Classe) owner).withMetadata(new ClassMetadataImpl(entryTypeMetadata)).build());
        } else if (owner.isDomain()) {
            return updateDomain(DomainDefinitionImpl.copyOf((Domain) owner).withMetadata(new DomainMetadataImpl(entryTypeMetadata)).build());
        } else {
            throw new UnsupportedOperationException("unsupported entry type = " + owner.getEtType());
        }
    }

    @Override
    public List<Attribute> updateAttributes(List<Attribute> attributes) {
        Map<String, Attribute> map = list(attributes.stream())
                .withOnly(Attribute::isVirtual)
                .map(attribute -> {
                    expandVirtualAttributeToDescendantClasses(attribute, (meta, attr) -> updateVirtualAttribute(meta, attr), false);
                    return updateEntryTypeMetadata(attribute.getOwner(), updateVirtualAttribute(attribute.getOwner().getMetadata().getAll(), attribute)).getAttribute(attribute.getName());
                }).collect(toMap(Attribute::getName, identity()));
        attributeRepository.updateAttributes(list(attributes).without(Attribute::isVirtual)).forEach(a -> map.put(a.getName(), a));
        return list(attributes).map(Attribute::getName).map(map::get).immutableCopy();
    }

    @Override
    public void deleteAttribute(Attribute attribute) {
        if (attribute.isVirtual()) {
            expandVirtualAttributeToDescendantClasses(attribute, (meta, attr) -> deleteVirtualAttribute(meta, attr), false);
            updateEntryTypeMetadata(attribute.getOwner(), deleteVirtualAttribute(attribute.getOwner().getMetadata().getAll(), attribute));
        } else {
            attributeRepository.deleteAttribute(attribute);
        }
    }

    private void expandVirtualAttributeToDescendantClasses(Attribute virtualAttribute, BiFunction<Map<String, String>, AttributeWithoutOwner, Map<String, String>> function, boolean setIndex) {
        getDescendantClasses(virtualAttribute.getOwner()).forEach(c -> {
            Attribute attribute = AttributeImpl.copyOf(virtualAttribute).withOwner(c).accept(a -> {
                if (setIndex) {
                    a.withMeta(INHERITED, Boolean.toString(TRUE), INDEX, toStringOrNull(c.getAllAttributes().stream().map(Attribute::getIndex).reduce(Integer::max).orElse(-1) + 1));
                }
            }).build();
            updateEntryTypeMetadata(attribute.getOwner(), function.apply(attribute.getOwner().getMetadata().getAll(), attribute));
        });
    }

    private List<Classe> getDescendantClasses(EntryType entryType) {
        if (entryType.isClasse() && entryType.asClasse().isSuperclass()) {
            return classeRepository.getAllClasses().stream()
                    .filter(c -> c.hasAncestor(entryType.asClasse()))
                    .filter(c -> !c.getName().equals(entryType.getName()))
                    .distinct().collect(toList());
        }
        return emptyList();
    }

    @Override
    public List<Domain> getAllDomains() {
        return domainRepository.getAllDomains();
    }

    @Override
    public Domain createDomain(DomainDefinition definition) {
        return domainRepository.createDomain(definition);
    }

    @Override
    public Domain updateDomain(DomainDefinition definition) {
        return domainRepository.updateDomain(definition);
    }

    @Override
    public void deleteDomain(Domain dbDomain) {
        domainRepository.deleteDomain(dbDomain);
    }

    @Override
    public List<Domain> getDomainsForClasse(Classe classe) {
        return domainRepository.getDomainsForClasse(classe);
    }

    @Override
    public Long create(DatabaseRecord entry) {
        logger.debug("create entry for type = {}", entry.getType());
        long id = entryUpdateService.executeInsertAndReturnKey(entry);
        postCreate(entry, id);
        return id;
    }

    @Override
    public List<Long> createBatch(List<DatabaseRecord> records) { //TODO improve this, real batch
        if (records.isEmpty()) {
            return emptyList();
        } else {
            checkArgument(records.stream().map(DatabaseRecord::getType).distinct().count() == 1, "all records in a batch must be of the same type");
            EntryType type = records.get(0).getType();
            logger.debug("create {} batch entries for type = {}", records.size(), type);
            List<Long> keys = entryUpdateService.executeBatchInsertAndReturnKeys(records);
            for (int i = 0; i < records.size(); i++) {
                postCreate(records.get(i), keys.get(i));
            }
            return keys;
        }
    }

    private void postCreate(DatabaseRecord entry, long id) {
        logger.debug("created entry for type = {} id = {}", entry.getType(), id);
        if (entry instanceof Card card) {
            activityLogBus.post(ActivityLogInfoImpl.build(ALA_CARD_CREATE, card.getClassName(), id));
            cardEventBus.post(new AfterCardCreateEventImpl(CardImpl.copyOf(card).withId(id).withAttribute(ATTR_IDCLASS, card.getTypeName()).build())); //NOTA: card non ricaricata !
        }
    }

    @Override
    public void update(DatabaseRecord entry) {
        logger.debug("updating entry with type = {} id = {}", entry.getType(), entry.getId());
        if (entry instanceof Card card) {
            activityLogBus.post(ActivityLogInfoImpl.build(ALA_CARD_UPDATE, card.getClassName(), card.getIdOrNull()));
            cardEventBus.post(new BeforeCardUpdateEventImpl(card, card)); //TODO prev/cur card
        }
        entryUpdateService.executeUpdate(entry);
        if (entry instanceof Card card) {
            cardEventBus.post(new AfterCardUpdateEventImpl(card, card)); //TODO prev/cur card
        }
    }

    @Override
    public void delete(DatabaseRecord record) {
        try {
            logger.debug("deleting record with id = {} for type = {}", record.getId(), record.getType());
            if (record instanceof Card card) {
                activityLogBus.post(ActivityLogInfoImpl.build(ALA_CARD_DELETE, card.getClassName(), card.getIdOrNull()));
                cardEventBus.post(new BeforeCardDeleteEventImpl(card));
            }
            databaseAdapterService.getJdbcTemplate().queryForObject(format("SELECT _cm3_card_delete(%s,%s)", systemToSqlExpr(record.getType()), checkNotNull(record.getId(), "unable to delete entry = %s, missing entry id", record)), Object.class);
            if (record instanceof Card card) {
                cardEventBus.post(new AfterCardDeleteEventImpl(card));
            }
        } catch (Exception ex) {
            throw new DaoException(ex, "error deleting record = %s", record);
        }
    }

    @Override
    public void truncate(EntryType type) {
        logger.info("clearing type = {}", type);
        // truncate all subclasses as well
        databaseAdapterService.getJdbcTemplate().execute(format("TRUNCATE TABLE %s CASCADE", entryTypeToSqlExpr(type)));
    }

}
