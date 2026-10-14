/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.tuple.Pair;
import static org.cmdbuild.activitylog.ActivityLogAction.ALA_CARDS_LIST;
import static org.cmdbuild.activitylog.ActivityLogAction.ALA_CARD_READ;
import org.cmdbuild.activitylog.ActivityLogInfoImpl;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_BULK_DELETE;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_BULK_UPDATE;
import static org.cmdbuild.auth.grant.GrantUtils.mergePrivilegeGroups;
import org.cmdbuild.auth.grant.GroupOfPrivileges;
import org.cmdbuild.auth.grant.UserPrivilegesForObject;
import org.cmdbuild.auth.grant.UserRoleGrantPrivilegeUpdateEvent;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RolePrivilege;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import static org.cmdbuild.classe.access.UserCardAccessUtils.buildFilterMarkName;
import static org.cmdbuild.classe.access.UserClassUtils.applyPrivilegesToClass;
import org.cmdbuild.classe.cache.CardCacheService;
import static org.cmdbuild.common.Constants.BASE_CLASS_NAME;
import static org.cmdbuild.common.Constants.DMS_MODEL_PARENT_CLASS;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.common.utils.PositionOf;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.beans.RelationImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDTENANT;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_NEXT_EXECUTOR;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_PREV_EXECUTORS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS_A;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS_D;
import org.cmdbuild.dao.core.q3.BasicWhereMethods;
import org.cmdbuild.dao.core.q3.CompositeWhereHelper;
import static org.cmdbuild.dao.core.q3.CompositeWhereOperator.OR;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import org.cmdbuild.dao.core.q3.SelectExprBuilder;
import org.cmdbuild.dao.core.q3.SelectMatchFilterBuilder;
import org.cmdbuild.dao.core.q3.SuperclassQueryService.SuperclassQueryBuilderHelper;
import static org.cmdbuild.dao.core.q3.WhereOperator.EQ;
import static org.cmdbuild.dao.core.q3.WhereOperator.IN;
import static org.cmdbuild.dao.core.q3.WhereOperator.INTERSECTS;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.driver.postgres.q3.stats.AggregateQuery;
import org.cmdbuild.dao.driver.postgres.q3.stats.DaoStatsQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.stats.StatsQueryResponse;
import org.cmdbuild.dao.driver.postgres.q3.stats.StatsQueryResponseImpl;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_CREATE;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_UPDATE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_CREATE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_DELETE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_READ;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_UPDATE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_WF_BASIC;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_WF_READTOUCHED;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.DECIMAL;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.DOUBLE;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FILE;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FLOAT;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.INTEGER;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LONG;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.event.DaoEvent;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.postgres.q3.AliasBuilder;
import org.cmdbuild.dao.postgres.q3.beans.PreparedQueryExt;
import static org.cmdbuild.dao.postgres.utils.QueryBuilderUtils.getActiveAttributesFromQueryOptions;
import static org.cmdbuild.dao.postgres.utils.QueryBuilderUtils.getQueryActiveAttributes;
import org.cmdbuild.dao.postgres.utils.RelationDirectionQueryHelper;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildReferenceAttrName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import org.cmdbuild.dao.user.UserDaoHelperService;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import static org.cmdbuild.dao.utils.CmFilterUtils.merge;
import static org.cmdbuild.dao.utils.CmFilterUtils.noopFilter;
import static org.cmdbuild.dao.utils.CmFilterUtils.serializeFilter;
import static org.cmdbuild.dao.utils.PositionOfUtils.buildPositionOf;
import org.cmdbuild.dao.virtual.VirtualAttributeService;
import org.cmdbuild.data.filter.AttachmentFilter;
import org.cmdbuild.data.filter.AttributeFilterConditionOperator;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.beans.AttributeFilterConditionImpl;
import org.cmdbuild.data.filter.beans.AttributeFilterImpl;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import org.cmdbuild.data.filter.beans.CompositeFilterImpl;
import org.cmdbuild.dms.DmsService;
import static org.cmdbuild.dms.DmsService.DOCUMENT_ATTR_CARD;
import static org.cmdbuild.dms.DmsService.DOCUMENT_ATTR_DOCUMENTID;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.lookup.LookupService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.getOnlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.getOnlyElementOrNull;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBoolean;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltEqZeroToNull;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrDefault;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.equal;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserCardServiceImpl implements UserCardService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserDaoHelperService user;
    private final DmsService dmsService;
    private final UserClassService classService;
    private final ClasseRepository classeRepository;
    private final DaoService dao;
    private final CardCacheService cardCache;
    private final EventBusService eventBusService;
    private final CoreConfiguration coreConfiguration;
    private final UserCardHelperService helper;
    private final LookupService lookupService;
    private final VirtualAttributeService virtualAttributeService;
    private final UserCardFileServiceImpl userCardFileService;
    private final UserFilterAccessService userFilterAccessService;
    private final OperationUserSupplier operationUser;
    private final RoleRepository roleRepository;

    private final CmCache<UserCardAccess> userCardAccessCache;
    private final int MAX_ATTACHMENTS_MATCHING_FILTER = 10000;

    public UserCardServiceImpl(UserFilterAccessService userFilterAccessService, UserCardHelperService helper, VirtualAttributeService virtualAttributeService, CoreConfiguration coreConfiguration, UserDaoHelperService user, UserClassService classService, ClasseRepository classeRepository, DaoService dao, CardCacheService cardCache, CacheService cacheService, EventBusService eventBusService, DmsService service, LookupService lookupService, UserCardFileServiceImpl userCardFileService, OperationUserSupplier operationUser, RoleRepository roleRepository) {
        this.user = checkNotNull(user);
        this.virtualAttributeService = checkNotNull(virtualAttributeService);
        this.userFilterAccessService = checkNotNull(userFilterAccessService);
        this.classService = checkNotNull(classService);
        this.dao = checkNotNull(dao);
        this.cardCache = checkNotNull(cardCache);
        this.coreConfiguration = checkNotNull(coreConfiguration);
        this.userCardAccessCache = cacheService.newCache("user_card_access_by_user_and_class");
        this.eventBusService = checkNotNull(eventBusService);
        this.dmsService = checkNotNull(service);
        this.helper = checkNotNull(helper);
        this.lookupService = checkNotNull(lookupService);
        this.classeRepository = checkNotNull(classeRepository);
        this.userCardFileService = checkNotNull(userCardFileService);
        this.operationUser = checkNotNull(operationUser);
        this.roleRepository = checkNotNull(roleRepository);
        eventBusService.getGrantEventBus().register(new Object() {
            @Subscribe
            public void handleGrantDataUpdatedEvent(UserRoleGrantPrivilegeUpdateEvent event) {
                invalidateCache();
            }
        });
        eventBusService.getDaoEventBus().register(new Object() {
            @Subscribe
            public void handleDaoEvent(DaoEvent event) {
                invalidateCache();
            }
        });
    }

    private void invalidateCache() {
        userCardAccessCache.invalidateAll();
    }

    @Override
    public Card getUserCard(String classId, long cardId) {
        eventBusService.getActivityLogEventBus().post(ActivityLogInfoImpl.builder()
                .withCode(ALA_CARD_READ)
                .withEntryTypeName(classId)
                .withCardId(cardId)
                .build());
        return cardCache.getCard(cardId, () -> doGetUserCard(classId, cardId));
    }

    @Override
    public Card getUserCardInfo(String classId, long cardId) {
        if (!user.getUser().getUserTenantContext().ignoreTenantPolicies() && dao.getClasseHierarchy(classId).getDescendantsAndSelf().stream().anyMatch(Classe::hasMultitenantEnabled)) {//TODO improve this
            return dao.getJdbcTemplate().queryForObject(format("SELECT _cm3_utils_regclass_to_name(\"IdClass\") \"IdClass\", \"Code\", \"Description\" FROM _cm3_card_info_get(%s, %s)", systemToSqlExpr(dao.getClasse(classId)), cardId), (r, i) -> {
                Classe classe = classService.getUserClass(r.getString(ATTR_IDCLASS));
                classe.checkServicePermission(CP_READ);
                return CardImpl.buildCard(ClasseImpl.copyOf(classe).withAttributes(list(classe.getAllAttributes()).withOnly(a -> set(ATTR_ID, ATTR_CODE, ATTR_DESCRIPTION).contains(a.getName()))).build(), ATTR_ID, cardId, ATTR_CODE, r.getString(ATTR_CODE), ATTR_DESCRIPTION, r.getString(ATTR_DESCRIPTION));
            });
        } else {
            return dao.select(ATTR_CODE, ATTR_DESCRIPTION).from(dao.getClasse(classId)).where(ATTR_ID, EQ, cardId).getCard();
        }
    }

    @Override
    public boolean userCanReadCard(String classId, long cardId) {
        return doGetUserCardWithAccess(classId, cardId).hasAccess();
    }

    @Override
    public Card createCard(String classId, Map<String, Object> values) {
        return new ClassHelper(classId, values).createCard();
    }

    @Override
    public Card updateCard(String classId, long cardId, Map<String, Object> values) {
        return new ClassHelper(classId, cardId, values).updateCard();
    }

    @Override
    public void deleteCard(String classId, long cardId) {
        Card card = getUserCard(classId, cardId);
        checkArgument(card.getType().hasServicePermission(CP_DELETE), "CMO 303: permission denied: cannot delete card = %s", card);
        dao.delete(card);
        cardCache.deleteCard(card);
    }

    @Override
    public void deleteCards(String classId, CmdbFilter filter) {
        userFilterAccessService.checkUserFilterAccess(filter);
        Classe classe = classService.getUserClass(classId);
        Role currentUserRole = roleRepository.getByNameOrIdOrNull(operationUser.getCurrentGroup());
        user.checkPrivileges(p -> toBooleanOrDefault(p.getPrivilegesForObject(classe).getMaxPrivilegesForSomeRecords().getCustomPrivileges().get(GDCP_BULK_DELETE), (coreConfiguration.isBulkDeleteEnabledDefault() || (currentUserRole != null && toBooleanOrDefault(currentUserRole.getConfig().getBulkDelete(), false))) && classe.hasServicePermission(CP_DELETE)), "CMO 305: user not allowed to execute bulk delete on class =< %s >", classId);
        dao.deleteCards(classe, filter.and(user.getPrivilegesForObject(classe).getMinPrivilegesForAllRecords().getFilter()));
    }

    @Override
    public void updateCards(String classId, CmdbFilter filter, Map<String, Object> values) {
        userFilterAccessService.checkUserFilterAccess(filter);
        Classe classe = classService.getUserClass(classId);
        Role currentUserRole = roleRepository.getByNameOrIdOrNull(operationUser.getCurrentGroup());
        user.checkPrivileges(p -> toBooleanOrDefault(p.getPrivilegesForObject(classe).getMaxPrivilegesForSomeRecords().getCustomPrivileges().get(GDCP_BULK_UPDATE), (coreConfiguration.isBulkUpdateEnabledDefault() || (currentUserRole != null && toBooleanOrDefault(currentUserRole.getConfig().getBulkUpdate(), false))) && classe.hasServicePermission(CP_UPDATE)), "CMO 304: user not allowed to execute bulk update on class =< %s >", classId);
        if (classe.getActiveServiceAttributes().stream().anyMatch(a -> a.isOfType(FILE) && values.containsKey(a.getName()))) {
            getUserCards(classId, UserCardQueryOptionsImpl.builder().withQueryOptions(b -> b.withFilter(filter)).build()).forEach(c -> new ClassHelper(c, values).updateCard());
        } else {
            dao.updateCards(classe, filter.and(user.getPrivilegesForObject(classe).getMinPrivilegesForAllRecords().getFilter()), new ClassHelper(classe, values).prepareCardValuesForUpdate());//TODO check classe access control, values filtering
        }
    }

    @Override
    public UserCardAccess getUserCardAccess(String classId) {
        return userCardAccessCache.get(key(user.getUserPrivilegesChecksum(), checkNotBlank(classId)), () -> doGetUserCardAccess(classId));
    }

    @Override
    public SuperclassUserQueryHelper getSuperclassQueryHelper(Classe superClass) {
        return new SuperclassUserQueryHelperImpl(superClass);
    }

    private class SuperclassUserQueryHelperImpl implements SuperclassUserQueryHelper {

        private final Map<String, UserCardAccess> subclassesCardAccess;

        public SuperclassUserQueryHelperImpl(Classe classe) {
            subclassesCardAccess = dao.getClasseHierarchy(classe).getLeaves().stream()
                    .filter(c -> classe.isDmsModel() == c.isDmsModel())
                    .filter(c -> classService.isActiveAndUserCanRead(c.getName()))
                    .collect(toMap(Classe::getName, c -> getUserCardAccess(c.getName())));

            checkArgument(!subclassesCardAccess.isEmpty(), "no subclass is accessible for superclass = %s", classe);
        }

        @Override
        public Map<String, UserCardAccess> getSubclassesUserCardAccess() {
            return subclassesCardAccess;
        }

        @Override
        public Consumer<SuperclassQueryBuilderHelper> addSubclassesFiltersAndMarks(Function<Classe, Consumer<BasicWhereMethods>> where) {
            return b -> {
                subclassesCardAccess.forEach((subclass, subclassAccess) -> {
                    CmdbFilter subclassFilter = subclassAccess.getWholeClassFilter();
                    logger.debug("add query filters/marks for subclass =< {} > with access filter =< {} > and {} subset filters", subclass, subclassFilter, subclassAccess.getSubsetFiltersByName().size());
                    b.withSubclass(subclass)
                            .accept(subclassAccess.addSubsetFilterMarkersToQueryVisitor()::accept)
                            .accept(where.apply(subclassAccess.getUserClass())::accept)
                            .where(subclassFilter);
                });
            };
        }

        public Card addCardAccessPermissionsFromSubfilterMark(Card c) {
            return subclassesCardAccess.get(c.getType().getName()).addCardAccessPermissionsFromSubfilterMark(c);
        }

    }

    @Override
    public PagedElements<Card> getUserCards(String classId, UserCardQueryOptions options) {
        Pair<String, Function<Classe, Consumer<BasicWhereMethods>>> where = getUserCardAccess(classId).getUserClass().isProcess() ? Pair.of("wfexfilter_" + user.getActiveGroupNames().stream().collect(joining("_")), this::addUserExecutorFilter) : emptyWhere();
        return getUserCards(classId, options, where);
    }

    @Override
    public PagedElements<Card> getUserCards(String classId, UserCardQueryOptions options, Pair<String, Function<Classe, Consumer<BasicWhereMethods>>> where) {
        logger.trace("getUserCards() get card from class =< {} > with query options =< {} >", classId, options.getQueryOptions());

        eventBusService.getActivityLogEventBus().post(ActivityLogInfoImpl.build(ALA_CARDS_LIST, classId));

        UserCardAccess cardAccess = getUserCardAccess(classId);
        Classe classe = cardAccess.getUserClass();
        DaoQueryOptions queryOptions = options.getQueryOptions().mapAttrNames(dao.getClasse(classId).getAliasToAttributeMap()).expandFulltextFilter(classe);
        CmdbFilter myFilter = queryOptions.getFilter();
        userFilterAccessService.checkUserFilterAccess(myFilter);

        if (options.hasForDomain()) {
            checkArgument(dao.getDomain(options.getForDomain().getDomainName()).getThisDomainWithDirection(options.getForDomain().getDirection()).isDomainForTargetClasse(dao.getClasse(classId)),
                    "invalid forDomain = %s for this class = %s with direction = %s", options.getForDomain().getDomainName(), classId, options.getForDomain().getDirection());
        }

        if (myFilter.hasAttachmentFilter() && dmsService.isEnabled()) {
            List<Long> cardIds = getCardIdsMatchingAttachmentFilter(classId, queryOptions);
            CmdbFilter attachAsAttrFilter = CmdbFilterImpl.builder().withAttributeFilter(AttributeFilterImpl.simple(AttributeFilterConditionImpl.builder()
                    .withOperator(AttributeFilterConditionOperator.IN)
                    .withKey(ATTR_ID)
                    .withValues(cardIds).build())).build();
            myFilter = merge(CmdbFilterImpl.copyOf(myFilter).withAttachmentFilter(null).build(), attachAsAttrFilter);
        }

        if (classe.isSuperclass()) {
            SuperclassUserQueryHelper superclassHelper = new SuperclassUserQueryHelperImpl(classe);

//<<<<<<< HEAD
            //TODO improve checksum (users with same access should have same checksum)
            String accessChecksum = superclassHelper.getSubclassesUserCardAccess().entrySet().stream()
                    .map(sub -> key(sub.getKey(),
                    serializeFilter(sub.getValue().getWholeClassFilter()),
                    serializeSubsetFilters(sub.getValue().getSubsetFiltersByName()),
                    user.getActiveTenantIds())).collect(joining(" "));
            CmdbFilter fullFilter = myFilter;
            return cardCache.getCards(classe, queryOptions, accessChecksum, () -> {

                DaoQueryOptions query = DaoQueryOptionsImpl.copyOf(queryOptions).withFilter(fullFilter).build();//options.getQueryOptions();
                PositionOf positionOf = null;
                //TODO select function value
                if (query.hasPositionOf()) {
                    Long rowNumber = dao.queryFromSuperclass(classe).withOptions(query).accept(superclassHelper.addSubclassesFiltersAndMarks(where.getRight())).getRowNumber();
                    positionOf = buildPositionOf(rowNumber, query);
                    query = query.withOffset(positionOf.getActualOffset());
                }

                List<Card> cards;
                cards = dao.queryFromSuperclass(classe).withOptions(query)
                        .accept(addForDomainSelect(options.getForDomain())::accept)
                        .accept(superclassHelper.addSubclassesFiltersAndMarks(where.getRight())).getCards();

                cards = addVirtualAttributesAndAccessPermissions(classe, cards, query);

                long total;
                if (query.isPagedAndHasFullPage(cards.size())) {
                    total = dao.queryFromSuperclass(classe).withOptions(DaoQueryOptionsImpl.build(query.getFilter())).accept(superclassHelper.addSubclassesFiltersAndMarks(where.getRight())).count();
                } else {
                    total = cards.size() + query.getOffset();
                }
                return new PagedElements<>(cards, total, positionOf);
            });
        } else {
            CmdbFilter fullFilter = cardAccess.getWholeClassFilter().and(myFilter);

            //TODO improve checksum (users with same access should have same checksum)
            String accessChecksum = key(serializeFilter(fullFilter), cardAccess.getSubsetFiltersByName().entrySet().stream().map(e -> serializeSubsetFilter(e)).collect(joining("||")), user.getActiveTenantIds(), where.getLeft());

            return cardCache.getCards(cardAccess.getUserClass(), queryOptions, accessChecksum, () -> {
                DaoQueryOptions query = queryOptions;
                CmdbFilter filter = fullFilter;

                PositionOf positionOf = null;
                final UserCardQueryForDomain forDomain = options.getForDomain();
                if (query.hasPositionOf()) {
                    Long rowNumber = dao.selectRowNumber().where(ATTR_ID, EQ, query.getPositionOf()).then()
                            .from(classId)
                            .orderBy(query.getSorter())
                            .where(filter)
                            .accept(addForDomaiDisabledClassesFilter(forDomain))
                            .accept(where.getRight().apply(classe)::accept)
                            .build().getRowNumberOrNull();
                    positionOf = buildPositionOf(rowNumber, query);
                    query = query.withOffset(positionOf.getActualOffset());
                }

                Collection<String> attributes = getQueryActiveAttributes(classe, query);
                List<Card> cards = dao.select(attributes)
                        .from(classId)
                        .orderBy(query.getSorter())
                        .where(filter)
                        .accept(cardAccess.addSubsetFilterMarkersToQueryVisitor()::accept)
                        .accept(addSelectFunctionValue(options.getFunctionValue()))
                        .accept(addForDomainSelect(options.getForDomain())::accept)
                        .accept(addForDomaiDisabledClassesFilter(options.getForDomain()))
                        .accept(where.getRight().apply(classe)::accept)
                        //<<<<<<< HEAD
                        .paginate(query.getOffset(), query.getLimit())
                        .getCards();

                cards = addVirtualAttributesAndAccessPermissions(classe, cards, query);

                logger.trace("getUserCards() found cards =< {} > for class =< {} >", UserCardService.toLog(cards), classe.getName());

                long total;
                if (query.isPagedAndHasFullPage(cards.size())) {
                    total = dao.selectCount()
                            .from(classId)
                            .where(filter)
                            .accept(addForDomaiDisabledClassesFilter(options.getForDomain()))
                            .accept(where.getRight().apply(classe)::accept)
                            .getCount();
                } else {
                    total = cards.size() + query.getOffset();
                }
                return new PagedElements<>(cards, total, positionOf);
            });
        }

    } // end getUserCards method

    @Override
    public boolean isRelatedInDomain(Long originCardId, String classId, RelationDirection origDirection, Domain domain) {
        logger.debug("isRelatedInDomain() originCardId=< {} > for classId=< {} >, domain=< {} > and direction=< {} >", originCardId, classId, domain.getName(), origDirection);

        Classe classe = classService.getUserClass(classId);
        UserCardQueryForDomainImpl forDomain = UserCardQueryForDomainImpl.builder()
                .withDomainName(domain.getName())
                .withDirection(origDirection)
                .withOriginId(originCardId)
                .build();
        List<Card> cards;
        DaoQueryOptions queryOptions = DaoQueryOptionsImpl.emptyOptions();
        Pair<String, Function<Classe, Consumer<BasicWhereMethods>>> where = Pair.of("", c -> w -> w.where(ATTR_ID, EQ, originCardId));

        String relatedClassName = classe.getName();
        if (classe.isSuperclass()) {
            logger.debug("isRelatedInDomain() for Superclass =< {} > ", relatedClassName);

            // Superclass cards
            SuperclassUserQueryHelperImpl superclassHelper = new SuperclassUserQueryHelperImpl(classe);

            //TODO improve checksum (users with same access should have same checksum)
            String accessChecksum = superclassHelper.getSubclassesUserCardAccess().entrySet().stream()
                    .map(sub -> key(sub.getKey(),
                    serializeFilter(sub.getValue().getWholeClassFilter()),
                    serializeSubsetFilters(sub.getValue().getSubsetFiltersByName()),
                    user.getActiveTenantIds())).collect(joining(" "));

            cards = cardCache.getCards(classe, queryOptions, accessChecksum,
                    () -> {
                        List<Card> foundCards = dao.queryFromSuperclass(classe).withOptions(DaoQueryOptionsImpl.builder().withAttrs(ATTR_ID, ATTR_DESCRIPTION).build())
                                .accept(addForDomainSelect(forDomain)::accept)
                                .accept(superclassHelper.addSubclassesFiltersAndMarks(where.getRight()))
                                .getCards();
                        return new PagedElements<>(foundCards);
                    }).elements();
        } else {
            // standard class cards
            logger.debug("isRelatedInDomain() for standard class =< {} > ", relatedClassName);

            UserCardAccess cardAccess = getUserCardAccess(relatedClassName);
            CmdbFilter fullFilter = cardAccess.getWholeClassFilter();

            //TODO improve checksum (users with same access should have same checksum)
            String accessChecksum = key(serializeFilter(fullFilter),
                    serializeSubsetFilters(cardAccess.getSubsetFiltersByName()),
                    user.getActiveTenantIds(), where.getLeft());
            cards = cardCache.getCards(cardAccess.getUserClass(), queryOptions, accessChecksum,
                    () -> {
                        List<Card> foundCards = dao.select(ATTR_ID, ATTR_DESCRIPTION)
                                .from(classId)
                                .where(fullFilter)
                                .accept(cardAccess.addSubsetFilterMarkersToQueryVisitor()::accept)
                                .accept(addForDomainSelect(forDomain)::accept)
                                .accept(addForDomaiDisabledClassesFilter(forDomain))
                                .accept(where.getRight().apply(classe)::accept)
                                .getCards();
                        logger.debug("isRelatedInDomain() all found cards=< {} > for class=< {} >", UserCardService.toLog(foundCards), relatedClassName);
                        return new PagedElements<>(foundCards);
                    }).elements();
        }
        logger.debug("isRelatedInDomain() found cards=< {} > for class=< {} >", UserCardService.toLog(cards), relatedClassName);
        return cards.stream().anyMatch(c -> c.get(FOR_DOMAIN_HAS_ANY_RELATION, Boolean.class));
    } // end isRelatedInDomain method

    @Override
    public StatsQueryResponse getStats(String classId, DaoQueryOptions options, DaoStatsQueryOptions query) {
        userFilterAccessService.checkUserFilterAccess(options);
        Classe classe = classService.getUserClass(classId);
        checkArgument(!query.getAggregateQueries().isEmpty(), "aggregate query is empty");
        AliasBuilder aliasBuilder = new AliasBuilder();
        List<Pair<AggregateQuery, String>> queriesWithAlias = query.getAggregateQueries().stream().map(q -> Pair.of(q, aliasBuilder.buildAlias(format("%s_%s", q.getAttribute(), serializeEnum(q.getOperation()))))).collect(toUnmodifiableList());
        //TODO check superclass support!!
        PreparedQueryExt preparedQuery = (PreparedQueryExt) dao.selectAll().from(classService.getUserClass(classId)).where(getUserCardAccess(classId).getWholeClassFilter().and(options.getFilter())).build();
        return dao.getJdbcTemplate().queryForObject(format("SELECT %s FROM ( %s ) x", queriesWithAlias.stream().map(q -> {
            Attribute attribute = classe.getAttribute(q.getLeft().getAttribute());
            return switch (q.getLeft().getOperation()) {
                case SUM -> {
                    checkArgument(attribute.isOfType(DECIMAL, DOUBLE, INTEGER, LONG, FLOAT), "SUM is not a valid operation for attribute = %s", attribute);
                    yield format("COALESCE(SUM(%s),0) %s", preparedQuery.getSelectForAttr(attribute.getName()).getAlias(), q.getRight());
                }
                default ->
                    throw unsupported("unsupported operation = %s", q.getLeft().getOperation());
            };
        }).collect(joining(", ")), preparedQuery.getQuery()), (r, i) -> StatsQueryResponseImpl.builder().accept(rethrowConsumer(b -> {
            queriesWithAlias.forEach(rethrowConsumer(q -> {
                b.withAggregateResult(q.getLeft(), rawToSystem(classe.getAttribute(q.getLeft().getAttribute()), r.getObject(q.getRight())));
            }));
        })).build());
    }

    private List<Card> addVirtualAttributesAndAccessPermissions(Classe classe, List<Card> cards, DaoQueryOptions query) {
        List<Card> modifiedCards = List.copyOf(cards);
        Collection<Attribute> attributes = getActiveAttributesFromQueryOptions(classe, query);
        modifiedCards = list(modifiedCards).map(c -> virtualAttributeService.loadVirtualAttributes(c, attributes));
        if (classe.isSuperclass()) {
            SuperclassUserQueryHelperImpl superclassHelper = new SuperclassUserQueryHelperImpl(classe);
            modifiedCards = list(modifiedCards).map(superclassHelper::addCardAccessPermissionsFromSubfilterMark);
        } else {
            UserCardAccess cardAccess = getUserCardAccess(classe.getName());
            modifiedCards = list(modifiedCards).map(cardAccess::addCardAccessPermissionsFromSubfilterMark);
        }
        return modifiedCards;
    }

    private Consumer<BasicWhereMethods> addUserExecutorFilter(Classe classe) {
        return (BasicWhereMethods q) -> {
            if (classe.hasServicePermission(CP_READ)) {
                logger.debug("skip flow executor filter for process = {} : current user {}[{}] has full read access on this process", classe.getName(), user.getUsername(), user.getActiveGroupNames().stream().collect(joining(",")));
            } else {
                logger.debug("add flow executor filter for process = {}", classe.getName());
                classe.checkServicePermission(CP_WF_BASIC);
                if (classe.hasServicePermission(CP_WF_READTOUCHED)) {
                    q.where(OR, (Consumer<CompositeWhereHelper>) (b) -> b.where(ATTR_NEXT_EXECUTOR, INTERSECTS, user.getActiveGroupNames()).where(ATTR_PREV_EXECUTORS, INTERSECTS, user.getActiveGroupNames()));
                } else {
                    q.where(ATTR_NEXT_EXECUTOR, INTERSECTS, user.getActiveGroupNames());
                }
            }
        };
    }

    private UserCardAccess doGetUserCardAccess(String classId) {
        Classe classe = classService.getUserClass(classId);
        checkArgument(classe.hasServicePermission(CP_READ) || (classe.isProcess() && classe.hasServicePermission(CP_WF_BASIC)), "permission denied: cannot read cards of class = %s", classe);//TODO throw specific access denied ex
        UserPrivilegesForObject privilegeGroups = user.getPrivilegesForObject(classe);
        Set<RolePrivilege> rolePrivileges = user.getRolePrivileges();
        CmdbFilter wholeClassFilter;
        List<UserCardAccessWithFilterImpl> subsetFilters;
        GroupOfPrivileges minPrivilegesForAllRecords = privilegeGroups.getMinPrivilegesForAllRecords();
        if (privilegeGroups.hasPrivilegesWithFilter()) {
            boolean canReadAllRecords = applyPrivilegesToClass(rolePrivileges, minPrivilegesForAllRecords, classe).hasServicePermission(CP_READ);
            if (privilegeGroups.getPrivilegeGroupsWithFilter().size() == 1 && !canReadAllRecords) {
                subsetFilters = emptyList();
                wholeClassFilter = getOnlyElement(privilegeGroups.getPrivilegeGroupsWithFilter()).getFilter();
                minPrivilegesForAllRecords = getOnlyElement(privilegeGroups.getPrivilegeGroupsWithFilter());
            } else {
                subsetFilters = privilegeGroups.getPrivilegeGroupsWithFilter().stream().map((p) -> {
                    CmdbFilter filter = p.getFilter(); //TODO check filter conversion
                    return new UserCardAccessWithFilterImpl(format("%s_%s", classId, p.getSource()), filter, p); //TODO check that p.source is unique
                }).collect(toList());
                if (canReadAllRecords) {
                    wholeClassFilter = noopFilter();
                } else {
                    wholeClassFilter = CompositeFilterImpl.or(list(subsetFilters).map(UserCardAccessWithFilter::getFilter));
                }
            }
        } else {
            wholeClassFilter = noopFilter();
            subsetFilters = emptyList();
        }
        return new UserCardAccessImpl(classe, rolePrivileges, minPrivilegesForAllRecords, wholeClassFilter, subsetFilters);
    }

    private Consumer<SelectExprBuilder> addForDomainSelect(@Nullable UserCardQueryForDomain forDomain) {
        return q -> {
            logger.debug("addForDomainSelect() with forDomain {}", applyOrDefault(forDomain, fd -> format("name =< %s > direction =< %s > originalCardId =< %s >", fd.getDomainName(), fd.getDirection(), fd.getOriginId()), "null"));
            if (forDomain != null) {
                Domain domain = dao.getDomain(forDomain.getDomainName());
                RelationDirectionQueryHelper helperDomain = RelationDirectionQueryHelper.forDirection(forDomain.getDirection());
                q.selectExpr(FOR_DOMAIN_HAS_THIS_RELATION, "EXISTS (SELECT 1 FROM %s WHERE %s = Q3_MASTER.\"Id\" AND %s = %s AND \"Status\" = 'A')", entryTypeToSqlExpr(domain), helperDomain.getTargetCardIdExpr(), helperDomain.getSourceCardIdExpr(), forDomain.getOriginId());
                q.selectExpr(FOR_DOMAIN_HAS_ANY_RELATION, "EXISTS (SELECT 1 FROM %s WHERE %s = Q3_MASTER.\"Id\" AND \"Status\" = 'A')", entryTypeToSqlExpr(domain), helperDomain.getTargetCardIdExpr());
            }
        };
    }

    private Consumer<QueryBuilder> addForDomaiDisabledClassesFilter(@Nullable UserCardQueryForDomain forDomain) {
        return q -> {
            if (forDomain != null) {
                Set<String> disabledTargetDescendants = set(dao.getDomain(forDomain.getDomainName()).getThisDomainWithDirection(forDomain.getDirection()).getDisabledTargetDescendants());
                if (!disabledTargetDescendants.isEmpty()) {
                    q.whereExpr("_cm3_utils_regclass_to_name(Q3_MASTER.\"IdClass\") <> ALL (?)", (Object) disabledTargetDescendants);
                }
            }
        };
    }

    private Card doGetUserCard(String classId, long cardId) {//TODO fix this, apply column permission rules (??)cp
        CardWithAccess cardWithAccess = doGetUserCardWithAccess(classId, cardId);
        checkArgument(cardWithAccess.hasCard() && cardWithAccess.hasAccess(), "card not existing or user not authorized to access card with type = %s id = %s", classId, cardId);
        return cardWithAccess.getCard();
    }

    private CardWithAccess doGetUserCardWithAccess(String classId, long cardId) {//TODO fix this, apply column permission rules (??)cp
        Classe classe = classService.getUserClass(dao.getTypeName(classId, cardId));
        UserCardAccess cardAccess = getUserCardAccess(classe.getName());
        Card card = dao.selectAll().from(classe)
                .accept(cardAccess.addSubsetFilterMarkersToQueryVisitor()::accept)
                .where(cardAccess.getWholeClassFilter())
                .where(ATTR_ID, EQ, checkNotNull(cardId))
                .getCardOrNull();
        if (card != null) {
            card = virtualAttributeService.loadVirtualAttributes(card);
            card = cardAccess.addCardAccessPermissionsFromSubfilterMark(card);
        }
        return CardWithAccessImpl.builder().withCard(card).withHasAccess(card != null ? (classe.isProcess() || card.getType().hasServiceReadPermission()) : false).build();
    }

    private Consumer<QueryBuilder> addSelectFunctionValue(@Nullable String selectFunctionValue) {
        return q -> {
            if (isNotBlank(selectFunctionValue)) {
                StoredFunction storedFunction = getSelectFunctionValueStoredFunction(selectFunctionValue);
                q.selectExpr(storedFunction.getOnlyOutputParameter().getName(), format("%s(\"Id\")", quoteSqlIdentifier(storedFunction.getName()))); //TODO move this to dao module !!
            }
        };
    }

    private StoredFunction getSelectFunctionValueStoredFunction(String selectFunctionValue) {
        StoredFunction storedFunction = dao.getFunctionByName(selectFunctionValue);//TODO check fun permission
        checkArgument(storedFunction.hasOnlyOneOutputParameter());//TODO
        return storedFunction;

    }

    private boolean hasSpecialAccessPermission(Classe classe, String attr) {
        return Objects.equals(attr, ATTR_IDTENANT) && classe.hasMultitenantEnabled();
    }

    private Card checkSpecialWriteConstraintAndNormalize(Card card) {
        Long tenantId = ltEqZeroToNull(card.getTenantId());
        switch (card.getType().getMultitenantMode()) {
            case CMM_ALWAYS -> {
                if (tenantId == null) {
                    tenantId = user.getDefaultTenantId();
                }
                checkNotNull(tenantId, "missing tenant id");
            }
            case CMM_MIXED ->
                checkArgument(tenantId == null || user.canAccessTenant(tenantId), "permission denied: user is not authorized to access tenant = %s", tenantId);
        }
        return CardImpl.copyOf(card).withAttribute(ATTR_IDTENANT, tenantId).build();
    }

    private Card updateRelationAttrs(Card card, Map<String, Object> values) {
        card.getType().getActiveServiceAttributes().stream().filter(a -> a.isOfType(REFERENCE)).forEach(a -> {
            Domain domain = dao.getDomain(a.getType().as(ReferenceAttributeType.class).getDomainName());
            if (domain.getActiveServiceAttributes().stream().anyMatch(Attribute::showInGrid)) {
                Long targetId = card.get(a.getName(), Long.class);
                if (isNotNullAndGtZero(targetId)) {
                    CMRelation relation = dao.getRelation(domain.getThisDomainWithDirection(a.getType().as(ReferenceAttributeType.class).getDirection()), card.getId(), targetId);
                    CMRelation newRelation = RelationImpl.copyOf(relation).accept(r -> {
                        domain.getActiveServiceAttributes().forEach(ra -> {
                            String key = buildReferenceAttrName(a.getName(), ra.getName());
                            if (values.containsKey(key)) {
                                r.withAttribute(ra.getName(), values.get(key));
                            }
                        });
                    }).build();
                    if (newRelation.allValuesEqualTo(relation)) {
                        logger.info(marker(), "CM: skip relation update, new relation is equal to current relation");
                    } else {
                        dao.update(newRelation);
                    }
                }
            }
        });
        return doGetUserCard(card.getClassName(), card.getId());
    }

    private List<Long> getCardIdsMatchingAttachmentFilter(String classId, DaoQueryOptions queryOptions) {
        AttachmentFilter attachmentFilter = queryOptions.getFilter().getAttachmentFilter();
        List<String> documentIdsMatchingContentOnDms = list();
        List<String> documentIdsMatchingFilter = list();
        if (attachmentFilter.hasFulltextFilter()) {
            documentIdsMatchingContentOnDms = dmsService.getService().queryDocumentsForClass(attachmentFilter.getInnerFilter().getFulltextFilter().getQuery(), classId);
        }
        Pair<String, Boolean> commonDmsModel = getCommonDmsModel(classId);
        DaoQueryOptions withoutPaging = DaoQueryOptionsImpl.copyOf(queryOptions)
                .withFilter(queryOptions.getFilter().getAttachmentFilter().getInnerFilter())
                .withSorter(CmdbSorter.class.cast(null))
                .build().withoutPaging();
        List<Card> cards;
        if (commonDmsModel.getRight()) {
            cards = dao.queryFromSuperclass(commonDmsModel.getLeft()).enableSmartSubclassFilterProcessing().enableAllSubclassesProcessing()
                    .withOptions(withoutPaging).selectExpr("_cardexists", format("EXISTS ( SELECT _cm3_utils_regclass_to_name(\"IdClass\") FROM \"%s\" WHERE \"Id\" = Q3_MASTER.\"Card\" )", classId))
                    .getCards();
        } else {
            cards = dao.select(DOCUMENT_ATTR_DOCUMENTID).from(commonDmsModel.getLeft())
                    .withOptions(withoutPaging).selectExpr("_cardexists", format("EXISTS ( SELECT _cm3_utils_regclass_to_name(\"IdClass\") FROM \"%s\" WHERE \"Id\" = Q3_MASTER.\"Card\" )", classId))
                    .getCards();
        }
        cards.stream().filter(c -> c.getBoolean("_cardexists")).forEach(c -> documentIdsMatchingFilter.add(c.getString(DOCUMENT_ATTR_DOCUMENTID)));
        Set<String> documentIds = set();
        documentIds.addAll(documentIdsMatchingContentOnDms);
        documentIds.addAll(documentIdsMatchingFilter);
        if (documentIds.size() > MAX_ATTACHMENTS_MATCHING_FILTER) {
            logger.info("Too many attachments match the filter, limiting the query to the first < {} > results, create a more strict filter", MAX_ATTACHMENTS_MATCHING_FILTER);
            documentIds = documentIds.stream().limit(MAX_ATTACHMENTS_MATCHING_FILTER).collect(toSet());
        }

        return documentIds.isEmpty()
                ? emptyList()
                : dao.select(DOCUMENT_ATTR_CARD).from(DMS_MODEL_PARENT_CLASS).where(DOCUMENT_ATTR_DOCUMENTID, IN, documentIds)
                        .getCards().stream().map(c -> c.getLong("Card")).collect(toList());
    }

    //Duplicated function
    private Pair<String, Boolean> getCommonDmsModel(String className) {
        Classe userClass = classService.getUserClass(className);
        if (userClass.hasDmsCategory()) {
            List<String> dmsModels = list();
            lookupService.getAllLookup(userClass.getDmsCategory()).forEach(v -> dmsModels.add(v.getDmsModelClass()));
            if (!dmsModels.isEmpty() && Collections.frequency(dmsModels, dmsModels.get(0)) == dmsModels.size()) {
                return Pair.of(dmsModels.get(0), false);
            }
            Set<String> commonAncestor = set();
            dmsModels.forEach(m -> {
                commonAncestor.addAll(classeRepository.getClasse(m).getAncestors().stream().filter(not(equal(identity(), BASE_CLASS_NAME))).collect(toSet()));
            });
            if (!commonAncestor.isEmpty()) {
                return Pair.of(commonAncestor.iterator().next(), true);
            }
        }
        return Pair.of(DMS_MODEL_PARENT_CLASS, true);
    }

    private String serializeSubsetFilters(Map<String, UserCardAccessWithFilter> subMap) {
        return subMap.entrySet().stream()
                .map(this::serializeSubsetFilter)
                .collect(joining("||"));
    }

    private String serializeSubsetFilter(Map.Entry<String, UserCardAccessWithFilter> e) {
        return format("%s:%s", e.getKey(), serializeFilter(e.getValue().getFilter()));
    }

    private class ClassHelper {

        private final Classe classe;
        private final Map<String, Object> values;
        private Long cardId;
        private Card oldCard;

        public ClassHelper(String classId, Map<String, Object> values) {
            this(classService.getUserClass(classId), values);
        }

        public ClassHelper(String classId, long cardId, Map<String, Object> values) {
            this(getUserCard(classId, cardId), values);
        }

        private ClassHelper(Card oldCard, Map<String, Object> values) {
            this.oldCard = checkNotNull(oldCard);
            this.cardId = oldCard.getId();
            this.classe = oldCard.getType();
            this.values = checkNotNull(handleFileMetadata(map(values), oldCard));
        }

        private ClassHelper(Classe classe, Map<String, Object> values) {
            this.values = checkNotNull(values);
            this.classe = checkNotNull(classe);
        }

        public Card updateCard() {
            checkArgument(classe.hasServicePermission(CP_UPDATE), "CMO 302: permission denied: cannot update card = %s", oldCard);
            Card newCard = CardImpl.copyOf(oldCard).withAttributes(prepareCardValuesForUpdate()).build();
            newCard = checkSpecialWriteConstraintAndNormalize(newCard);
            if (newCard.allValuesEqualTo(oldCard)) {
                logger.info(marker(), "CM: skip card update, new card is equal to current card");
            } else {
                newCard = dao.update(newCard);
            }
            newCard = updateRelationAttrs(newCard, values);
            userCardFileService.clearDeletedAttachments(oldCard, newCard);
            cardCache.updateCard(newCard);
            return newCard;
        }

        public Card createCard() {
            checkArgument(classe.hasServicePermission(CP_CREATE), "CMO 301: permission denied: cannot create card of class = %s", classe);
            Card card = CardImpl.buildCard(classe, map(values).mapKeys(classe.getAliasToAttributeMap()::get).filterKeys(classe::hasAttribute).filterKeys((s)
                    -> (classe.getAttribute(s).hasServicePermission(AP_CREATE) || hasSpecialAccessPermission(classe, s)) && !classe.getAttribute(s).isOfType(FILE)));

            card = checkSpecialWriteConstraintAndNormalize(card);
            card = helper.sanitizeValues(card);
            if (classe.getActiveServiceAttributes().stream().anyMatch(a -> a.isOfType(FILE) && a.isMandatory())) {
                card = dao.create(CardImpl.copyOf(card).withAttribute(ATTR_STATUS, ATTR_STATUS_D).build());
            } else {
                card = dao.create(card);
            }
            cardId = card.getId();
            Map<String, Object> fileAttributes = prepareCardFileAttributes();
            if (fileAttributes.values().stream().anyMatch(Objects::nonNull)) {
                logger.debug("update new card with file attributes = \n\n{}\n", mapToLoggableStringLazy(fileAttributes));
                card = dao.update(CardImpl.copyOf(card).withAttributes(fileAttributes).withAttribute(ATTR_STATUS, ATTR_STATUS_A).build());
            }
            card = updateRelationAttrs(card, values);
            cardCache.createCard(card);
            return card;
        }

        public Map<String, Object> prepareCardValuesForUpdate() {
            Map<String, Object> map = map(values).mapKeys(classe.getAliasToAttributeMap()::get).filterKeys(classe::hasAttribute).filterKeys(s -> classe.getAttribute(s).hasServicePermission(AP_UPDATE) || hasSpecialAccessPermission(classe, s)).with(prepareCardFileAttributes());
            map = helper.sanitizeValues(classe, map);
            return map;
        }

        private Map<String, Object> prepareCardFileAttributes() {
            return list(values.keySet()).filter(classe::hasAttribute).map(classe::getAttribute).filter(a -> a.isOfType(FILE) && a.hasServicePermission(AP_UPDATE)).collect(toMap(Attribute::getName, this::prepareFileAttribute));
        }

        @Nullable
        private Object prepareFileAttribute(Attribute attribute) {
            return userCardFileService.prepareFileAttribute(attribute, values, classe.getName(), oldCard, cardId);
        }

        private Map<String, Object> handleFileMetadata(Map<String, Object> newValues, Card oldValues) {
            list(newValues.keySet())
                    .map(attr -> getOnlyElementOrNull(list(oldValues.getType().getActiveServiceAttributes())
                    .filter(classeAttr -> classeAttr.isOfType(FILE) && attr.startsWith(format("_%s_", classeAttr.getName())))
                    .map(Attribute::getName).distinct()))
                    .forEach(a -> {
                        if (!newValues.keySet().contains(a) && oldValues.get(a) != null) {
                            newValues.put(a, dmsService.getCardAttachmentByMetadataId(oldValues.getLong(a)).getDocumentId());  // getting attribute file from metadata (improve this?)
                        }
                    });
            return newValues;
        }
    }

    private static class UserCardAccessImpl implements UserCardAccess {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final Classe baseClass;
        private final Set<RolePrivilege> rolePrivileges;
        private final GroupOfPrivileges basePrivileges;
        private final CmdbFilter wholeClassFilter;
        private final Map<String, UserCardAccessWithFilterImpl> subsetFiltersByName;
        private final Cache<String, Classe> userClassCache = CacheBuilder.newBuilder().build();

        public UserCardAccessImpl(Classe baseClass, Set<RolePrivilege> rolePrivileges, GroupOfPrivileges basePrivileges, CmdbFilter wholeClassFilter, List<UserCardAccessWithFilterImpl> subsetFilters) {
            this.baseClass = checkNotNull(baseClass);
            this.rolePrivileges = checkNotNull(rolePrivileges);
            this.wholeClassFilter = checkNotNull(wholeClassFilter);
            this.basePrivileges = checkNotNull(basePrivileges);
            this.subsetFiltersByName = map(subsetFilters, UserCardAccessWithFilter::getName);
        }

        @Override
        public Classe getUserClass() {
            return baseClass;
        }

        @Override
        public CmdbFilter getWholeClassFilter() {
            return wholeClassFilter;
        }

        @Override
        public Map<String, UserCardAccessWithFilter> getSubsetFiltersByName() {
            return (Map) subsetFiltersByName;
        }

        @Override
        public Classe getUserClass(Set<String> activeFilters) {
            try {
                return userClassCache.get(key(activeFilters), () -> {
                    GroupOfPrivileges privileges = mergePrivilegeGroups(list(basePrivileges).with(map(subsetFiltersByName).filterKeys(activeFilters::contains).values().stream().map(UserCardAccessWithFilter::getPrivileges)))
                            .withSource("filters_" + activeFilters.stream().collect(joining("+"))).build();
                    return applyPrivilegesToClass(rolePrivileges, privileges, baseClass);
                });
            } catch (ExecutionException ex) {
                throw runtime(ex);
            }
        }

        @Override
        public Consumer<SelectMatchFilterBuilder> addSubsetFilterMarkersToQueryVisitor() {
            return (q) -> {
                getSubsetFiltersByName().forEach((key, a) -> {
                    String mark = buildFilterMarkName(key);
                    logger.debug("add query mark =< {} > for filter = {} {}", mark, a.getName(), a.getFilter());
                    logger.trace("privileges =\n{}", lazyString(a.getPrivileges()::getPrivilegesDetailedInfos));
                    q.selectMatchFilter(mark, a.getFilter());
                });
            };
        }

        @Override
        public Set<String> getActiveFilters(Function<String, Object> record) {
            return getSubsetFiltersByName().keySet().stream().filter((k) -> toBoolean(record.apply(buildFilterMarkName(k)))).collect(toSet());
        }

        @Override
        public Set<String> getFilterMarkNames() {
            return getSubsetFiltersByName().keySet().stream().map(UserCardAccessUtils::buildFilterMarkName).collect(toSet());
        }

        @Override
        public Card addCardAccessPermissionsFromSubfilterMark(Card card) {
            Set<String> activeFilters = getActiveFilters(card::get);
            Classe userClass = getUserClass(activeFilters);
            if (!Objects.equals(userClass.getName(), card.getType().getName())) {
                userClass = ClasseImpl.copyOf(card.getType())
                        .withAttributes(userClass.getAllAttributes())
                        .withPermissions(userClass)
                        .build();
            }
            return CardImpl.copyOf(card).withType(userClass).build();
        }

    }

    private static class UserCardAccessWithFilterImpl implements UserCardAccessWithFilter {

        final String name;
        final CmdbFilter filter;
        final GroupOfPrivileges privileges;

        public UserCardAccessWithFilterImpl(String name, CmdbFilter filter, GroupOfPrivileges privileges) {
            this.name = checkNotBlank(name);
            this.filter = checkNotNull(filter);
            this.privileges = checkNotNull(privileges);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public CmdbFilter getFilter() {
            return filter;
        }

        @Override
        public GroupOfPrivileges getPrivileges() {
            return privileges;
        }

    }

}
