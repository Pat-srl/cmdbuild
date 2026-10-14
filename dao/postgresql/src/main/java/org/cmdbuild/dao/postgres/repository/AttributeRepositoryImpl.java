/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.repository;

import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.sql.ResultSet;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static java.util.function.Predicate.isEqual;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.beans.AttributeMetadataImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.SYSTEM_ATTRIBUTE_ALIASES;
import org.cmdbuild.dao.driver.repository.ClassStructureChangedEvent;
import org.cmdbuild.dao.driver.repository.DomainStructureChangedEvent;
import org.cmdbuild.dao.driver.repository.InnerAttributeRepository;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.AttributeMetadata;
import static org.cmdbuild.dao.entrytype.AttributeMetadata.LENGTH;
import static org.cmdbuild.dao.entrytype.AttributeMetadata.MULTILINE;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwnerImpl;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_MANY;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.event.AttributeModifiedEventImpl;
import org.cmdbuild.dao.event.AttributeStructureChangedEvent;
import org.cmdbuild.dao.event.AttributesCreatedEventImpl;
import static org.cmdbuild.dao.postgres.utils.CommentUtils.ATTRIBUTE_COMMENT_TO_METADATA_MAPPING;
import static org.cmdbuild.dao.postgres.utils.CommentUtils.parseCommentFromFeatures;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.createAttributeType;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.getSqlTypeString;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.parseSqlType;
import org.cmdbuild.dao.postgres.utils.SqlType;
import org.cmdbuild.eventbus.EventBusService;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.getOnlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmMapUtils.toMultimap;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Primary
public class AttributeRepositoryImpl implements InnerAttributeRepository {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final EventBus eventBus;
    private final JdbcTemplate jdbcTemplate;
    private final CmCache<List<AttributeWithoutOwner>> attributesByOwner;

    public AttributeRepositoryImpl(CacheService cacheService, EventBusService eventBusService, @Qualifier(SYSTEM_LEVEL_ONE) JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = checkNotNull(jdbcTemplate);
        attributesByOwner = cacheService.newCache("entry_type_attributes_by_owner", CacheConfig.SYSTEM_OBJECTS);
        eventBus = eventBusService.getDaoEventBus();
        eventBus.register(new Object() {
            @Subscribe
            public void handleClassStructureChangedEvent(ClassStructureChangedEvent event) {
                if (event.impactAllClasses()) {
                    invalidateWholeCache();
                } else {
                    invalidateCache(event.getAffectedClassOids());
                }
            }

            @Subscribe
            public void handleClassStructureChangedEvent(DomainStructureChangedEvent event) {
                if (event.impactAllDomains()) {
                    invalidateWholeCache();
                } else {
                    invalidateCache(event.getAffectedDomainOids());
                }
            }
        });
    }

    private void invalidateWholeCache() {
        attributesByOwner.invalidateAll();
    }

    private void invalidateCache(Iterable<Long> affectedClassOids) {
        affectedClassOids.forEach(attributesByOwner::invalidate);
    }

    @Override
    public Attribute createAttribute(Attribute definition) {
        logger.info("creating attribute =< {} > owner = {} type = {}", definition.getName(), definition.getOwner(), definition.getType());
        AttributeWithoutOwner attribute = doCreateAttribute(definition);
        eventBus.post(new AttributesCreatedEventImpl(definition.getOwner()));//TODO move this in repo service
        return AttributeImpl.copyOf(attribute).withOwner(definition.getOwner()).build();//TODO refresh owner after attr update and cache cleanup
    }

    @Override
    public List<Attribute> updateAttributes(List<Attribute> definitions) {
        if (isNullOrEmpty(definitions)) {
            return emptyList();
        } else {
            EntryType owner = definitions.stream().map(Attribute::getOwner).distinct().collect(onlyElement());
            List<AttributeWithoutOwner> attributes = doUpdateAttributes(owner, definitions);
            eventBus.post(new AttributeModifiedEventImpl(owner));//TODO move this in repo service
            return attributes.stream().map((a) -> AttributeImpl.copyOf(a).withOwner(owner).build()).collect(toList());//TODO refresh owner after attr update and cache cleanup
        }
    }

    @Override
    public void deleteAttribute(Attribute attribute) {
        logger.info("deleting attribute = {}", attribute.getName());
        doDeleteAttribute(attribute);
        eventBus.post(new AttributeModifiedEventImpl(attribute.getOwner()));//TODO move this in repo service
    }

    @Override
    public List<AttributeWithoutOwner> getNonReservedEntryTypeAttributesForType(long entryTypeId) {
        return firstNonNull(attributesByOwner.get(String.valueOf(entryTypeId), () -> {
            synchronized (AttributeRepositoryImpl.this) {
                if (attributesByOwner.asMap().isEmpty()) {
                    Map<Long, List<AttributeWithoutOwner>> map = doGetNonReservedEntryTypeAttributesByOwner();
                    map(map).filterKeys(not(isEqual(entryTypeId))).forEach((k, v) -> attributesByOwner.put(String.valueOf(k), v));
                    return map.getOrDefault(entryTypeId, emptyList());
                } else {
                    return doGetNonReservedEntryTypeAttributesByOwner(entryTypeId);
                }
            }
        }), emptyList());
    }

    private List<AttributeWithoutOwner> doGetNonReservedEntryTypeAttributesByOwner(long ownerOid) {
        return jdbcTemplate.query("select owner::oid::int _classid, * FROM _cm3_attribute_list_detailed(?::oid::regclass)", (ResultSet rs, int rowNum) -> parseAttributesRecord(rs), ownerOid)
                .stream().map(Pair::getValue).collect(toImmutableList());
    }

    private Map<Long, List<AttributeWithoutOwner>> doGetNonReservedEntryTypeAttributesByOwner() {
        return map(jdbcTemplate.query("select owner::oid::int _classid, * FROM _cm3_attribute_list_detailed()", (ResultSet rs, int rowNum) -> parseAttributesRecord(rs))
                .stream().collect(toMultimap(Pair::getKey, Pair::getValue)).asMap()).mapValues(ImmutableList::copyOf);
    }

    private AttributeWithoutOwner doGetNonReservedEntryTypeAttribute(long ownerOid, String attrName) {
        return jdbcTemplate.query("select owner::oid::int _classid, * FROM _cm3_attribute_list_detailed(?::oid::regclass) WHERE name = ?", (ResultSet rs, int rowNum) -> parseAttributesRecord(rs), ownerOid, attrName)
                .stream().map(Pair::getValue).collect(onlyElement("attribute not found for owner oid = %s name = %s", ownerOid, attrName));
    }

    private Pair<Long, AttributeWithoutOwner> parseAttributesRecord(ResultSet rs) {
        String name = "<unknown>", ownerTable = "<unknown>";
        Long classId = null;
        try {
            ownerTable = rs.getString("owner");
            classId = checkNotNullAndGtZero(rs.getLong("_classid"));
            name = rs.getString("name");
            Map<String, String> metadata = fromJson(rs.getString("metadata"), MAP_OF_STRINGS),
                    comment = fromJson(rs.getString("comment"), MAP_OF_STRINGS),
                    features = map(metadata).with(comment);
            AttributeMetadata meta = new AttributeMetadataImpl(mapOf(String.class, String.class).accept(rethrowConsumer(m -> {
                SqlType sqlType = parseSqlType(rs.getString("sql_type"));
                switch (sqlType.getType()) {//TODO improve this, load sqltype and meta together
                    case varchar, bpchar -> {
                        m.put(MULTILINE, FALSE);
                        if (sqlType.hasParams()) {
                            int length = toInt(getOnlyElement(sqlType.getParams()));
                            m.put(LENGTH, Integer.toString(length));
                            checkArgument(isBlank(comment.get(checkNotBlank(ATTRIBUTE_COMMENT_TO_METADATA_MAPPING.inverse().get(LENGTH))))//TODO improve this
                                    || toInt(comment.get(checkNotBlank(ATTRIBUTE_COMMENT_TO_METADATA_MAPPING.inverse().get(LENGTH)))) <= length, "invalid lenght metadata config, sql col lenght =< {} > and config metadata length =< {} >", length, comment.get(checkNotBlank(ATTRIBUTE_COMMENT_TO_METADATA_MAPPING.inverse().get(LENGTH))));
                        }
                    }
                    case text ->
                        m.put(MULTILINE, TRUE);
                }
                //TODO improve this, load sqltype and meta together
            })).with(map(features).withoutKeys(ATTRIBUTE_COMMENT_TO_METADATA_MAPPING.keySet()).withoutKeys(ATTRIBUTE_COMMENT_TO_METADATA_MAPPING.values()))
                    .with(parseCommentFromFeatures(features, ATTRIBUTE_COMMENT_TO_METADATA_MAPPING))
                    .skipNullValues().with(
                            AttributeMetadata.INHERITED, Boolean.toString(rs.getBoolean("inherited")),
                            AttributeMetadata.DEFAULT, rs.getString("default_value"),
                            AttributeMetadata.MANDATORY, Boolean.toString(rs.getBoolean("not_null_constraint")),
                            AttributeMetadata.UNIQUE, Boolean.toString(rs.getBoolean("unique_constraint")),
                            AttributeMetadata.UI_ALIAS, SYSTEM_ATTRIBUTE_ALIASES.get(name)
                    ));
            CardAttributeType<?> type = createAttributeType(rs.getString("sql_type"), meta);
            return Pair.of(classId, (AttributeWithoutOwner) AttributeWithoutOwnerImpl.builder()
                    .withName(name)
                    .withType(type)
                    .withMeta(meta)
                    .build());
        } catch (Exception ex) {
            throw new DaoException(ex, "error processing attribute = \"%s\" with owner_oid = %s owher_table = %s", name, classId, ownerTable);
        }
    }

    private AttributeWithoutOwner doCreateAttribute(Attribute attribute) {
        validateAttribute(attribute);
        EntryType owner = attribute.getOwner();
        if (attribute.getIndex() < 0) {
            attribute = AttributeImpl.copyOf(attribute).withIndex(owner.getAllAttributes().stream().map(Attribute::getIndex).reduce(Integer::max).orElse(-1) + 1).build();
        }
        jdbcTemplate.queryForObject("SELECT _cm3_attribute_create(?::regclass,?,?,?::jsonb)", Object.class, entryTypeToSqlExpr(owner), attribute.getName(), getSqlTypeString(attribute.getType()), toJson(buildFeaturesForAttribute(attribute)));
        eventBus.post(AttributeStructureChangedEventImpl.INSTANCE);
        return doGetNonReservedEntryTypeAttribute(owner.getId(), attribute.getName());
    }

    private List<AttributeWithoutOwner> doUpdateAttributes(EntryType owner, List<Attribute> definitions) {
        return definitions.stream().map((definition) -> {
            checkArgument(Objects.equals(definition.getOwner(), owner));
            return doUpdateAttribute(definition);
        }).collect(toList());
    }

    private AttributeWithoutOwner doUpdateAttribute(Attribute attribute) {
        validateAttribute(attribute);
        EntryType owner = attribute.getOwner();
        jdbcTemplate.queryForObject("SELECT _cm3_attribute_modify(?::regclass,?,?,?::jsonb)", Object.class, entryTypeToSqlExpr(owner), attribute.getName(), getSqlTypeString(attribute.getType()), toJson(buildFeaturesForAttribute(attribute)));
        eventBus.post(AttributeStructureChangedEventImpl.INSTANCE);
        return doGetNonReservedEntryTypeAttribute(owner.getId(), attribute.getName());
    }

    private void doDeleteAttribute(Attribute attribute) {
        checkArgument(!attribute.isVirtual());
        jdbcTemplate.queryForObject("SELECT _cm3_attribute_delete(?::regclass,?)", Object.class, entryTypeToSqlExpr(attribute.getOwner()), attribute.getName());
    }

    /**
     * validate attribute:
     * <ul>
     * <li>check if attribute is not virtual</li>
     * <li>if owner of attribute is a domain, domain key must be false when
     * cardinality is not N:N</li>
     * </ul>
     */
    private void validateAttribute(Attribute attribute) {
        checkArgument(!attribute.isVirtual(), "attribute %s is virtual", attribute.getName());
        checkArgument(!(attribute.getOwner().isDomain() && !attribute.getOwner().asDomain().hasCardinality(MANY_TO_MANY) && attribute.isDomainKey()), "domain attribute %s can't be a domain key", attribute.getName());
    }

    private Map<String, String> buildFeaturesForAttribute(Attribute attribute) {
        return map(attribute.getMetadata().getAll()).mapKeys(k -> firstNotNull(ATTRIBUTE_COMMENT_TO_METADATA_MAPPING.inverse().get(k), k));
    }

    private enum AttributeStructureChangedEventImpl implements AttributeStructureChangedEvent {
        INSTANCE;
    }
}
