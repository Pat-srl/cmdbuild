package org.cmdbuild.lookup;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.MoreCollectors.toOptional;
import static java.lang.String.format;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.dao.core.q3.QueryBuilder.NOTEQ;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUP;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUPARRAY;
import org.cmdbuild.dao.orm.CardMapperService;
import org.cmdbuild.dao.postgres.q3.AliasBuilder;
import org.cmdbuild.dao.postgres.services.LookupDescriptionService;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import org.cmdbuild.data.filter.CmdbFilter;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LookupRepositoryImpl implements LookupRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static String LOOKUP_TYPE_CODE = "org.cmdbuild.LOOKUPTYPE";

    private final LookupDescriptionService descriptionService;

    private final DaoService dao;

    private final CmCache<Optional<LookupValue>> lookupCacheById;
    private final CmCache<List<LookupValue>> lookupCacheByType;
    private final Holder<List<LookupValue>> allLookups;
    private final Holder<List<LookupType>> allLookupTypes;
    private final CmCache<Optional<LookupType>> typesByName;
    private final CmCache<LookupType> typesById;

    public LookupRepositoryImpl(CardMapperService mapper, DaoService dataView, CacheService cacheService, DaoService dao, LookupDescriptionService descriptionService) {
        this.dao = checkNotNull(dao);
        this.descriptionService = checkNotNull(descriptionService);
        lookupCacheById = cacheService.newCache("lookup_store_cache_by_id", CacheConfig.SYSTEM_OBJECTS);
        lookupCacheByType = cacheService.newCache("lookup_store_cache_by_type", CacheConfig.SYSTEM_OBJECTS);
        allLookups = cacheService.newHolder("lookup_store_all_lookups", CacheConfig.SYSTEM_OBJECTS);
        allLookupTypes = cacheService.newHolder("lookup_store_all_lookup_types", CacheConfig.SYSTEM_OBJECTS);
        typesByName = cacheService.newCache("lookup_store_lookup_types_by_name", CacheConfig.SYSTEM_OBJECTS);
        typesById = cacheService.newCache("lookup_store_lookup_types_by_id", CacheConfig.SYSTEM_OBJECTS);
    }

    private void invalidateCache() {
        lookupCacheById.invalidateAll();
        lookupCacheByType.invalidateAll();
        allLookups.invalidate();
        allLookupTypes.invalidate();
        descriptionService.invalidateCache();//TODO use event bus for this
        typesByName.invalidateAll();
        typesById.invalidateAll();
    }

    @Override
    @Nullable
    public LookupValue getByIdOrNull(long lookupId) {
        return lookupCacheById.get(lookupId, () -> doGetById(lookupId)).orElse(null);
    }

    @Override
    public List<LookupValue> getAll() {
        return allLookups.get(this::doGetAll);
    }

    @Override
    public List<LookupValue> getByType(String type, CmdbFilter filter) {
        getTypeByName(type);
        checkNotNull(filter);
        if (filter.isNoop()) {
            return lookupCacheByType.get(type, () -> doReadAll(type));
        } else {
            return dao.select(ATTR_ID).from("LookUp").where("Type", EQ, type).where(ATTR_CODE, NOTEQ, LOOKUP_TYPE_CODE).where(filter).getCards().stream().map(Card::getId).map(this::getById).collect(toImmutableList());
        }
    }

    @Override
    public List<LookupType> getAllTypes() {
        return allLookupTypes.get(this::doGetAllTypes);
    }

    @Override
    public List<LookupValue> getAllByTypeClassAttr(String type, String forClass, String forAttr) {
        Classe lookupClass = dao.getClasse(forClass);
        Attribute lookupAttr = lookupClass.getAttribute(forAttr);
        checkArgument(lookupAttr.isOfType(LOOKUP, LOOKUPARRAY), "attribute type not allowed");
        AliasBuilder aliasBuilder = new AliasBuilder();
        String subquerySelect;
        return (List<LookupValue>) dao.getJdbcTemplate().queryForObject(format("SELECT %s FROM (SELECT %s FROM %s WHERE \"Status\" = 'A') %s",
                switch (lookupAttr.getType().getName()) {
            case LOOKUP -> {
                subquerySelect = quoteSqlIdentifier(forAttr);
                yield format("array_agg(DISTINCT %s) %s", quoteSqlIdentifier(forAttr), aliasBuilder.buildAliasAndStore(forAttr, forAttr));
            }
            case LOOKUPARRAY -> {
                subquerySelect = format("unnest(CASE WHEN %s <> '{}' THEN %s ELSE '{NULL}' END) %s",
                        quoteSqlIdentifier(forAttr), quoteSqlIdentifier(forAttr), aliasBuilder.buildAliasAndStore(forAttr, "unnested_array", forAttr));
                yield format("array_agg(DISTINCT %s) %s", aliasBuilder.getAlias("unnested_array", forAttr), aliasBuilder.buildAliasAndStore(forAttr, forAttr));
            }
            default ->
                throw new IllegalArgumentException();
        }, subquerySelect, entryTypeToSqlExpr(lookupClass), aliasBuilder.buildAlias(forClass)),
                (r, i) -> list(convert(r.getArray(aliasBuilder.getAlias(forAttr)), List.class)).filter(Objects::nonNull).map(l -> lookupCacheById.get(toLong(l), () -> doGetById(toLong(l))).orElse(null)).filter(Objects::nonNull));
    }

    @Nullable
    @Override
    public LookupType getTypeByNameOrNull(String lookupTypeName) {
        return typesByName.get(checkNotBlank(lookupTypeName), () -> getAllTypes().stream().filter((l) -> equal(l.getName(), lookupTypeName)).collect(toOptional())).orElse(null);
    }

    @Override
    public LookupType getTypeById(long typeId) {
        return typesById.get(typeId, () -> getAllTypes().stream().filter((l) -> equal(l.getId(), typeId)).collect(onlyElement("lookup type not found for id = %s", typeId)));
    }

    @Override
    public LookupType createLookupType(LookupType lookupType) {
        checkAccessDefault(lookupType);

        logger.debug("persisting lookupType {}", lookupType);
        lookupType = dao.create(lookupType);
        invalidateCache();
        return lookupType;
    }
    
    /**
     * Used by <code>model-diff-schema</code> to handle creation of
     * <code>protected</code> {@link LookupType}s.
     *
     * @param lookupType
     * @return
     */
    @Override
    public LookupType createLookupType_Protected(LookupType lookupType) {
        logger.debug("persisting lookupType {}", lookupType);
        lookupType = dao.create(lookupType);
        invalidateCache();
        return lookupType;
    }    

    @Override
    @Transactional //TODO check that annotation works!
    public void deleteLookupType(String lookupTypeId) {
        LookupType type = getTypeByName(lookupTypeId);
        checkAccessDefault(type);
        //TODO check lookup reference in class attributes, etc
        logger.info("delete lookup type for id = {}", lookupTypeId);
        getAllByType(type.getName()).stream().map(LookupValue::getId).forEach(this::deleteLookupValue);
        dao.delete(type);
        invalidateCache();
    }

    @Override
    @Nullable
    public LookupValue getOneByTypeAndCodeOrNull(String type, String code) {
        return getAllByType(type).stream().filter((l) -> equal(l.getCode(), code)).collect(toOptional()).orElse(null);
    }

    @Override
    public LookupValue getOneByTypeAndDescriptionOrNull(String type, String description) {
        List<LookupValue> list = getAllByType(type).stream().filter((l) -> equal(l.getDescription(), description)).collect(toList());
        if (list.isEmpty()) {
            return null;
        } else if (list.size() == 1) {
            return getOnlyElement(list);
        } else {
            logger.warn("found more than one lookup for type = %s description = %s", type, description);
            return null;
        }
    }

    @Override
    public LookupValue createOrUpdate(LookupValue lookup) {
        LookupType type = getTypeByName(lookup.getTypeName());
        if (lookup.hasParent()) {
            LookupValue parent = getById(lookup.getParentId());
            checkArgument(equal(parent.getType().getId(), getTypeByName(lookup.getTypeName()).getParent()), "parenty type mismatch: update not allowed");
            lookup = LookupValueImpl.copyOf(lookup).withParent(parent).build();
        }
        LookupValueData lookupData;
        if (lookup.hasId()) {
            LookupValue current = getById(lookup.getId());
            boolean active;
            if (getTypeByName(current.getType().getName()).isSystem()) {
                active = true;
            } else {
                active = lookup.isActive();
            }
            lookupData = dao.update(LookupValueDataImpl.copyOf(lookup).withId(current.getId()).withCode(current.getCode()).withTypeId(current.getTypeId()).withActive(active).build());
        } else {
            checkAccessNotSystem(type);
            lookupData = dao.create(LookupValueDataImpl.copyOf(lookup).withType(type).build());
        }
        logger.info("create/update lookup record = {}", lookup);
        invalidateCache();
        return getById(lookupData.getId());
    }

    @Override
    public void deleteLookupValue(long lookupValueId) {
        LookupValue lookupValue = getById(lookupValueId);
        checkAccessNotSystem(lookupValue.getType());
        if (getAllByType(lookupValue.getType()).size() <= 1) {
            checkAccessDefault(lookupValue.getType());
        }
        dao.delete(LookupValueData.class, lookupValue.getId());
        invalidateCache();
    }

    private Optional<LookupValue> doGetById(long lookupId) {
        return getAll().stream().filter((l) -> equal(l.getId(), lookupId)).findFirst();
    }

    private List<LookupValue> doGetAll() {
        return dao.selectAll().from(LookupValueDataImpl.class).asList(LookupValueData.class).stream().map((LookupValueData l) -> LookupValueImpl.copyOf(l).withType(checkNotNull(getTypeById(l.getTypeId()))).build()).collect(toImmutableList());
    }

    private List<LookupValue> doReadAll(String type) {
        return getAll().stream().filter((l) -> equal(l.getType().getName(), type)).collect(toList());
    }

    private List<LookupType> doGetAllTypes() {
        return ImmutableList.copyOf(dao.selectAll().from(LookupTypeImpl.class).orderBy(ATTR_CODE).asList());
    }

    private void checkAccessDefault(LookupType lookupType) {
        checkArgument(lookupType.isAccessDefault(), "cannot modify system lookup =< %s >", lookupType.getName());
    }

    private void checkAccessNotSystem(LookupType lookupType) {
        checkArgument(!lookupType.isSystem(), "cannot create/delete system/protected lookup =< %s >", lookupType.getName());
    }

}
