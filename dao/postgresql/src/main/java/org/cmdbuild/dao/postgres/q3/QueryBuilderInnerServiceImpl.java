/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.MoreCollectors.toOptional;
import jakarta.inject.Provider;
import static java.lang.String.format;
import java.util.Collection;
import static java.util.Collections.singletonList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.cql.compiler.impl.ClassDeclarationImpl;
import org.cmdbuild.cql.compiler.impl.CqlQueryImpl;
import static org.cmdbuild.dao.DaoConst.DOMAIN_PREFIX;
import org.cmdbuild.dao.DaoException;
import static org.cmdbuild.dao.beans.CMRelation.ATTR_CANREAD1;
import static org.cmdbuild.dao.beans.CMRelation.ATTR_CANREAD2;
import static org.cmdbuild.dao.beans.CMRelation.ATTR_CODE1;
import static org.cmdbuild.dao.beans.CMRelation.ATTR_CODE2;
import static org.cmdbuild.dao.beans.CMRelation.ATTR_DESCRIPTION1;
import static org.cmdbuild.dao.beans.CMRelation.ATTR_DESCRIPTION2;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ2;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS_A;
import static org.cmdbuild.dao.core.q3.DaoService.ATTRS_ALL;
import static org.cmdbuild.dao.core.q3.DaoService.COUNT;
import static org.cmdbuild.dao.core.q3.DaoService.ROW_NUMBER;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.core.q3.WhereOperator;
import static org.cmdbuild.dao.core.q3.WhereOperator.ISNOTNULL;
import static org.cmdbuild.dao.core.q3.WhereOperator.ISNULL;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.repository.ClasseReadonlyRepository;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FOREIGNKEY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUP;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.graph.ClasseHierarchyService;
import org.cmdbuild.dao.orm.CardMapper;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderConfiguration.SqlQueryReferenceProcessingStrategy.RPS_IGNORETENANT;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.JOIN_ID_DEFAULT;
import org.cmdbuild.dao.postgres.q3.beans.JoinQueryArgs;
import org.cmdbuild.dao.postgres.q3.beans.PreparedQueryExt;
import org.cmdbuild.dao.postgres.q3.beans.QueryMode;
import static org.cmdbuild.dao.postgres.q3.beans.QueryMode.QM_COUNT;
import static org.cmdbuild.dao.postgres.q3.beans.QueryMode.QM_DELETE;
import static org.cmdbuild.dao.postgres.q3.beans.QueryMode.QM_GROUP;
import static org.cmdbuild.dao.postgres.q3.beans.QueryMode.QM_ROWNUMBER;
import static org.cmdbuild.dao.postgres.q3.beans.QueryMode.QM_SIMPLE;
import static org.cmdbuild.dao.postgres.q3.beans.QueryMode.QM_UPDATE;
import org.cmdbuild.dao.postgres.q3.beans.SelectArg;
import org.cmdbuild.dao.postgres.q3.beans.SelectElement;
import org.cmdbuild.dao.postgres.q3.beans.SelectHolder;
import org.cmdbuild.dao.postgres.q3.beans.WhereArg;
import org.cmdbuild.dao.postgres.q3.beans.WhereElement;
import org.cmdbuild.dao.postgres.services.EntryUpdateHelperService;
import static org.cmdbuild.dao.postgres.utils.QueryBuilderUtils.buildOffsetLimitExprOrBlank;
import static org.cmdbuild.dao.postgres.utils.QueryBuilderUtils.compileCql;
import static org.cmdbuild.dao.postgres.utils.QueryBuilderUtils.getReservedAttrs;
import static org.cmdbuild.dao.postgres.utils.QueryBuilderUtils.smartExprReplace;
import static org.cmdbuild.dao.postgres.utils.QueryBuilderUtils.whereElementsToWhereExprBlankIfEmpty;
import org.cmdbuild.dao.postgres.utils.RelationDirectionQueryHelper;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.Q3_MASTER;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildCodeAttrName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildDescAttrName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildInfoAttrName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildLookupInfoExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildReferenceAttrName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildReferenceCodeExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildReferenceDescExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildReferenceExistsExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildTypeAttrName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.sqlTableToDomainName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.wrapExprWithBrackets;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.CqlFilter;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import org.cmdbuild.data.filter.beans.CqlFilterImpl;
import org.cmdbuild.ecql.EcqlService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QueryBuilderInnerServiceImpl implements QueryBuilderInnerService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final QueryBuilderConfiguration sqlConfiguration;
    private final ClasseReadonlyRepository classeRepository;
    private final ClasseHierarchyService hierarchyService;
    private final DomainRepository domainRepository;
    private final PreparedQueryHelperService executorService;
    private final RefAttrHelperService refAttrHelperService;
    private final EcqlService ecqlService;
    private final QueryBuilderUtilsService utils;
    private final EntryUpdateHelperService updateHelper;
    private final Provider<OperationUserSupplier> operationUserSupplier;//TODO: refactor and remove this; user supplier should not be accessed from dao

    public QueryBuilderInnerServiceImpl(QueryBuilderConfiguration sqlConfiguration, Provider<OperationUserSupplier> operationUserSupplier, ClasseReadonlyRepository classeRepository, ClasseHierarchyService hierarchyService, DomainRepository domainRepository, PreparedQueryHelperService executorService, RefAttrHelperService refAttrHelperService, EcqlService ecqlService, QueryBuilderUtilsService utils, EntryUpdateHelperService updateHelper) {
        this.classeRepository = checkNotNull(classeRepository);
        this.hierarchyService = checkNotNull(hierarchyService);
        this.domainRepository = checkNotNull(domainRepository);
        this.executorService = checkNotNull(executorService);
        this.refAttrHelperService = checkNotNull(refAttrHelperService);
        this.ecqlService = checkNotNull(ecqlService);
        this.utils = checkNotNull(utils);
        this.updateHelper = checkNotNull(updateHelper);
        this.operationUserSupplier = checkNotNull(operationUserSupplier);
        this.sqlConfiguration = checkNotNull(sqlConfiguration);
    }

    @Override
    public PreparedQueryExt buildPreparedQuery(QueryBuilderParams source) {
        return new QueryBuilderProcessor(source).doBuild();
    }

    private class JoinQueryElement {

        private final String joinId;
        private final EntryType from;
        private final List<WhereElement> on = list();
        private final String alias;

        public JoinQueryElement(String joinId, EntryType from, String alias) {
            this.joinId = checkNotBlank(joinId);
            this.from = checkNotNull(from);
            this.alias = checkNotBlank(alias);
        }

    }

    private class QueryBuilderProcessor {

        private final AliasBuilder aliasBuilder = new AliasBuilder();
        private final SelectHolder select = new SelectHolder();
        private final List<JoinQueryElement> joinElements = list();
        private EntryType from;
        private final String fromAlias, rowNumberSubExprAlias = aliasBuilder.buildAlias("x");
        private final List<WhereElement> where = list();
        private final Set<String> groupBy;
        private final CmdbSorter sorter;
        private final Boolean activeCardsOnly, hasJoin;
        private final CardMapper cardMapper;
        private final Map<String, CqlQueryImpl> processedCqlFilters = map();
        private final QueryMode queryMode;
        private final QueryBuilderHelper helper;
        private final DaoQueryOptions queryOptions;
        private final Map<String, Object> updateValues;
        private final QueryBuilderOptionsCommons builderOptions;

        private QueryBuilderProcessor(QueryBuilderParams source) {
            builderOptions = checkNotNull(source);
            from = source.getFrom();
            queryOptions = source.getQueryOptions();
            sorter = source.getQueryOptions().getSorter();
            activeCardsOnly = source.getActiveCardsOnly();
            cardMapper = source.getCardMapper();

            source.getSelectArgs().stream().filter(SelectArg::hasAlias).forEach(a -> aliasBuilder.addAlias(a.getAlias()));

            groupBy = source.getSelectArgs().stream().filter(SelectArg::getGroupBy).map(SelectArg::getName).collect(toImmutableSet());
            boolean count = source.getSelectCount(), selectRowNumber = source.getSelectRowNumber(), selectGroupBy = !groupBy.isEmpty();

            updateValues = source.getValues();

            checkArgument(!((count && selectRowNumber) || (selectGroupBy && selectRowNumber)), "cannot mix `count`, `select row number` and `select distinct/group by` within the same query");
            //TODO add check for update, delete
            if (source.isUpdate()) {
                queryMode = QM_UPDATE;
            } else if (source.isDelete()) {
                queryMode = QM_DELETE;
            } else if (selectGroupBy) {
                queryMode = QM_GROUP;
            } else if (count) {
                queryMode = QM_COUNT;
            } else if (selectRowNumber) {
                queryMode = QM_ROWNUMBER;
            } else {
                queryMode = QM_SIMPLE;
            }

            List<CmdbFilter> filters = list(source.getQueryOptions().getFilter());//TODO code cleanup
//                    source.filters;

            filters = preProcessCqlFilters(filters);//TODO code cleanup

            checkNotNull(from, "query from param is null");
            fromAlias = aliasBuilder.buildAlias(from.getName());

            filters = fixAliasesInFilters(filters);//TODO code cleanup

            List<SelectArg> selectArgs = list(source.getSelectArgs());
            if (filters.stream().anyMatch(f -> f.hasFulltextFilter())) {
                selectArgs.add(SelectArg.build(ATTRS_ALL, ATTRS_ALL));//add all attrs for fulltext filter join count; TODO: improve this!! add only joins, ref/lookup description etc
            }
            selectArgs = prepareSelectArgs(from, selectArgs, true);
            selectArgs = selectAttrsForSorter(selectArgs);//TODO also add attrs from filters !!

            hasJoin = !source.getJoinArgs().isEmpty() || (builderOptions.enableRefLookupJoin() && selectArgs.stream().map(SelectArg::getName).map(from::getAttributeOrNull).filter(Objects::nonNull).anyMatch(a -> a.isOfType(REFERENCE, FOREIGNKEY, LOOKUP)));

            helper = utils.helper()
                    .withAddFromToIdentifiers(hasJoin)
                    .withFrom(from)
                    .withFromAlias(fromAlias)
                    .withAliasBuilder(aliasBuilder)
                    .withJoinElementAliasSupplier(this::getAliasForJoinElement)
                    .build();

            selectArgs.stream().map(helper::processSelectArg).forEach(select::add);

            selectExtendedAttrs();

            processJoinArgs(source.getJoinArgs());

//            List<WhereArg> whereArgs = preProcessWhereArgs(source.getWhere());
//            List<WhereArg> whereArgs = source.getWhere();
            processWhereArgs(source.getWhere());
            processFilters(filters);//TODO check
            addStandardWhereArgs();
        }

        private List<CmdbFilter> fixAliasesInFilters(List<CmdbFilter> filters) {
            Map<String, String> uiAliasToAttrMap = from.getAliasToAttributeMap();
            return filters.stream().map(f -> f.mapNames(uiAliasToAttrMap)).collect(toList());
        }

        private List<SelectArg> prepareSelectArgs(EntryType entryType, List<SelectArg> selectArgs, boolean includeReservedAttrs) {
            Collection<String> reservedAttrs = getReservedAttrs(entryType);
            selectArgs = list(selectArgs);
            boolean selectAll = selectArgs.removeIf((s) -> equal(s.getName(), ATTRS_ALL));
            if (selectAll) {
                includeReservedAttrs = true;
            }
            if (isQueryMode(QM_COUNT)) {
                if (selectArgs.isEmpty()) {
                    if (entryType.isClasse() || entryType.isDomain()) {
                        selectArgs.add(SelectArg.build(ATTR_ID, quoteSqlIdentifier(ATTR_ID)));
                    } else {
                        selectAll = true;
                    }
                }
            }
            if (includeReservedAttrs) {
                list(reservedAttrs).filter(not(transform(selectArgs, SelectArg::getName)::contains)).map(a -> SelectArg.build(a, quoteSqlIdentifier(a))).forEach(selectArgs::add);
            }
            if (selectAll && entryType.isDomain()) {//TODO improve this
                Domain domain = (Domain) from;
                //TODO fix this, change to join; add options to filter for join
                selectExpr(ATTR_DESCRIPTION1, buildReferenceDescExpr(domain.getSourceClass(), fromAlias, quoteSqlIdentifier(ATTR_IDOBJ1)));
                selectExpr(ATTR_DESCRIPTION2, buildReferenceDescExpr(domain.getTargetClass(), fromAlias, quoteSqlIdentifier(ATTR_IDOBJ2)));
                selectExpr(ATTR_CODE1, buildReferenceCodeExpr(domain.getSourceClass(), fromAlias, quoteSqlIdentifier(ATTR_IDOBJ1)));
                selectExpr(ATTR_CODE2, buildReferenceCodeExpr(domain.getTargetClass(), fromAlias, quoteSqlIdentifier(ATTR_IDOBJ2)));
                selectExpr(ATTR_CANREAD1, buildReferenceExistsExpr(domain.getSourceClass(), fromAlias, quoteSqlIdentifier(ATTR_IDOBJ1)));
                selectExpr(ATTR_CANREAD2, buildReferenceExistsExpr(domain.getTargetClass(), fromAlias, quoteSqlIdentifier(ATTR_IDOBJ2)));
            }
            if (selectAll) {
                list(entryType.getCoreAttributes()).filter(Attribute::isActive).filter(not(Attribute::isVirtual)).map(Attribute::getName).filter(not(reservedAttrs::contains)).filter(not(transform(selectArgs, SelectArg::getName)::contains)).sorted().map(a -> SelectArg.build(a, quoteSqlIdentifier(a))).forEach(selectArgs::add);
            }
            return selectArgs;
        }

        private boolean isQueryMode(QueryMode mode) {
            return equal(this.queryMode, mode);
        }

        private List<CmdbFilter> preProcessCqlFilters(List<CmdbFilter> filters) {
            filters = filters.stream().map(this::preProcessCqlFilter).collect(toImmutableList());
            Set<EntryType> fromsFromCql = set();
            AtomicBoolean cqlFromMismatch = new AtomicBoolean(false);

            processedCqlFilters.values().forEach((cql) -> {
                ClassDeclarationImpl mainClass = cql.getFrom().mainClass();
                EntryType cqlFrom;
                if (mainClass.getId() > 0) {
                    cqlFrom = classeRepository.getClasse(mainClass.getId());
                } else if (mainClass.getName().startsWith(DOMAIN_PREFIX)) {//TODO improve this
                    cqlFrom = domainRepository.getDomain(sqlTableToDomainName(mainClass.getName()));
                } else {
                    cqlFrom = classeRepository.getClasse(mainClass.getName());
                }
                if (!equal(cqlFrom, from)) {
                    if (from == null || (from.isClasse() && cqlFrom.isClasse() && from.asClasse().isAncestorOf(cqlFrom.asClasse()))) {
                        fromsFromCql.add(cqlFrom);
                    } else if (from.isClasse() && cqlFrom.isClasse() && cqlFrom.asClasse().isAncestorOf(from.asClasse())) {
                        //nothing to do
                    } else {
                        logger.debug("cql from = {} it is not consistent with this query entry type = {}: add FALSE select arg", fromsFromCql, from);//TODO improve this
                        cqlFromMismatch.set(true);
                    }
                }
            });
            if (cqlFromMismatch.get()) {
//                return singletonList(AttributeFilterConditionImpl.eq(ATTR_ID, -1).toAttributeFilter().toCmdbFilters());//TODO improve this, make FALSE filter
                return singletonList(CmdbFilterImpl.falseFilter());
            } else {
                checkArgument(fromsFromCql.size() <= 1, "error processing cql filter: multiple cql filters with incompatible FROM class expr = %s", fromsFromCql);
                if (!fromsFromCql.isEmpty()) {
                    from = getOnlyElement(fromsFromCql);
                }
                return filters;
            }
        }

        private CmdbFilter preProcessCqlFilter(CmdbFilter filter) {
            if (filter.hasCompositeFilter()) {
                filter = CmdbFilterImpl.copyOf(filter).withCompositeFilter(filter.getCompositeFilter().mapElements(this::preProcessCqlFilter)).build();
            }
            if (filter.hasEcqlFilter()) {
                String cqlExpr = ecqlService.prepareCqlExpression(filter.getEcqlFilter().getEcqlId(), filter.getEcqlFilter().getJsContext());
                filter = CmdbFilterImpl.copyOf(filter).withEcqlFilter(null).withCqlFilter(new CqlFilterImpl(cqlExpr)).build();
            }
            if (filter.hasCqlFilter()) {
                processedCqlFilters.put(cqlFilterKey(filter.getCqlFilter()), compileCql(filter.getCqlFilter()));
            }
            return filter;
        }

        private QueryBuilderHelper getHelper() {
            return checkNotNull(helper, "helper not ready");
        }

        private void selectExpr(String name, String expr) {
            select.add(SelectElement.build(name, expr, aliasBuilder.buildAlias(name)));
        }

        private List<SelectArg> selectAttrsForSorter(List<SelectArg> args) {
            List<SelectArg> res = list(args);
            sorter.getElements().forEach((s) -> {
                if (!res.stream().anyMatch((a) -> a.getName().equals(s.getProperty()))) {
                    res.add(SelectArg.build(s.getProperty(), quoteSqlIdentifier(s.getProperty())));
                }
            });
            return res;
        }

        private void selectExtendedAttrs() {
            if (builderOptions.enableRefLookupJoin()) {
                list(select.getElements()).stream().filter(a -> from.hasAttribute(a.getName())).forEach((a) -> selectRefLookupJoinExtendedAttrs(a, from.getAttribute(a.getName())));
            }
        }

        private void selectRefLookupJoinExtendedAttrs(SelectElement select, Attribute a) {
            switch (a.getType().getName()) {
                case REFERENCE, FOREIGNKEY -> {
                    Classe targetClass = refAttrHelperService.getTargetClassForAttribute(a);
                    boolean useTenantRlsOverrideFunctions = sqlConfiguration.hasReferenceProcessingStrategy(RPS_IGNORETENANT)
                            && !operationUserSupplier.get().getUser().getUserTenantContext().ignoreTenantPolicies()
                            && (targetClass.isSuperclass() ? hierarchyService.getClasseHierarchy(targetClass).getDescendants().stream().anyMatch(Classe::hasMultitenantEnabled) : targetClass.hasMultitenantEnabled());
                    if (useTenantRlsOverrideFunctions) {
                        JoinQueryElement join = new JoinQueryElement(randomId(), targetClass, aliasBuilder.buildAlias(a.getName()));
                        join.on.add(WhereElement.build(format("%s.\"Id\" = %s.%s", join.alias, getAliasForJoinElement(JOIN_ID_DEFAULT), quoteSqlIdentifier(a.getName()))));
                        if (targetClass.hasHistory() && activeCardsOnly) {
                            join.on.add(WhereElement.build(format("%s.\"Status\" = 'A'", join.alias)));
                        }
                        joinElements.add(join);
                        selectExtendedAttr(select, buildTypeAttrName(a.getName()), format("COALESCE(%s.\"IdClass\", _cm3_card_type_get(%s, %s.%s, %s))", join.alias, systemToSqlExpr(targetClass), getAliasForJoinElement(JOIN_ID_DEFAULT), quoteSqlIdentifier(a.getName()), !activeCardsOnly));
                        if (targetClass.hasAttribute(ATTR_CODE)) {
                            selectExtendedAttr(select, buildCodeAttrName(a.getName()), format("COALESCE(%s.\"Code\", _cm3_card_code_get(%s, %s.%s, %s))", join.alias, systemToSqlExpr(targetClass), getAliasForJoinElement(JOIN_ID_DEFAULT), quoteSqlIdentifier(a.getName()), !activeCardsOnly));
                        }
                        if (targetClass.hasAttribute(ATTR_DESCRIPTION)) {
                            selectExtendedAttr(select, buildDescAttrName(a.getName()), format("COALESCE(%s.\"Description\", _cm3_card_description_get(%s, %s.%s, %s))", join.alias, systemToSqlExpr(targetClass), getAliasForJoinElement(JOIN_ID_DEFAULT), quoteSqlIdentifier(a.getName()), !activeCardsOnly));
                        }
                    } else {
                        JoinQueryElement join = new JoinQueryElement(randomId(), targetClass, aliasBuilder.buildAlias(a.getName()));
                        join.on.add(WhereElement.build(format("%s.\"Id\" = %s.%s", join.alias, getAliasForJoinElement(JOIN_ID_DEFAULT), quoteSqlIdentifier(a.getName()))));
                        if (targetClass.hasHistory() && activeCardsOnly) {
                            join.on.add(WhereElement.build(format("%s.\"Status\" = 'A'", join.alias)));
                        }
                        joinElements.add(join);
                        selectExtendedAttr(select, buildTypeAttrName(a.getName()), format("%s.\"IdClass\"::regclass", join.alias));
                        if (targetClass.hasAttribute(ATTR_CODE)) {
                            selectExtendedAttr(select, buildCodeAttrName(a.getName()), format("%s.\"Code\"", join.alias));
                        }
                        if (targetClass.hasAttribute(ATTR_DESCRIPTION)) {
                            selectExtendedAttr(select, buildDescAttrName(a.getName()), format("%s.\"Description\"", join.alias));
                        }
                    }
                    if (a.isOfType(REFERENCE) && !domainRepository.getDomain(a.getType().as(ReferenceAttributeType.class).getDomainName()).getActiveServiceAttributes().isEmpty()) {
                        Domain domain = domainRepository.getDomain(a.getType().as(ReferenceAttributeType.class).getDomainName());
                        RelationDirectionQueryHelper dirHelper = RelationDirectionQueryHelper.forDirection(a.getType().as(ReferenceAttributeType.class).getDirection());
                        JoinQueryElement join = new JoinQueryElement(randomId(), domain, aliasBuilder.buildAlias(a.getName()));
                        join.on.add(WhereElement.build(format("%s.%s = %s.%s", join.alias, dirHelper.getSourceCardIdExpr(), getAliasForJoinElement(JOIN_ID_DEFAULT), quoteSqlIdentifier(ATTR_ID))));
                        join.on.add(WhereElement.build(format("%s.%s = %s.%s", join.alias, dirHelper.getTargetCardIdExpr(), getAliasForJoinElement(JOIN_ID_DEFAULT), quoteSqlIdentifier(a.getName()))));
//                        if (activeCardsOnly) { TODO improve this
                        join.on.add(WhereElement.build(format("%s.\"Status\" = 'A'", join.alias)));
//                        }
                        joinElements.add(join);
                        domain.getActiveServiceAttributes().stream().filter(e -> !e.isVirtual()).forEach(domainAttr -> {
                            selectExtendedAttr(select, buildReferenceAttrName(a.getName(), domainAttr.getName()), format("%s.%s", join.alias, quoteSqlIdentifier(domainAttr.getName())));
                        });
                    }
                }
                case LOOKUP -> {
                    JoinQueryElement join = new JoinQueryElement(randomId(), classeRepository.getClasse("LookUp"), aliasBuilder.buildAlias(a.getName()));
                    join.on.add(WhereElement.build(format("%s.\"Id\" = %s.%s", join.alias, getAliasForJoinElement(JOIN_ID_DEFAULT), quoteSqlIdentifier(a.getName()))));
                    if (activeCardsOnly) {
                        join.on.add(WhereElement.build(format("%s.\"Status\" = 'A'", join.alias)));
                    }
                    joinElements.add(join);
                    selectExtendedAttr(select, buildCodeAttrName(a.getName()), format("%s.\"Code\"", join.alias));
                    selectExtendedAttr(select, buildDescAttrName(a.getName()), format("%s.\"Description\"", join.alias));
                }
                case LOOKUPARRAY ->
                    selectExtendedAttr(select, buildInfoAttrName(a.getName()), buildLookupInfoExpr(getAliasForJoinElement(JOIN_ID_DEFAULT), quoteSqlIdentifier(a.getName())));
            }
        }

        private void selectExtendedAttr(SelectElement parentSelect, String name, String expr) {
            SelectElement selectElement = buildExtendedAttr(name, expr);
            if (parentSelect.getGroupBy()) {
                selectElement = SelectElement.copyOf(selectElement).withGroupBy(true).build();
            }
            select.add(selectElement);
        }

        private SelectElement buildExtendedAttr(String name, String expr) {
            return SelectElement.build(name, expr, aliasBuilder.buildAlias(name));
        }

        private void processWhereArgs(List<WhereArg> list) {
            list.stream().map(this::whereArgToWhereElement).forEach(where::add);
        }

        private void addStandardWhereArgs() {
            if (activeCardsOnly && from.hasHistory()) {
                where.add(whereArgToWhereElement(WhereArg.build(ATTR_STATUS, EQ, ATTR_STATUS_A)));
            }
        }

        private WhereElement whereArgToWhereElement(WhereArg arg) {
            return WhereElement.builder().withTarget(arg.getTarget()).withExpr(whereArgToWhereElementExpr(arg)).build();
        }

        private String whereArgToWhereElementExpr(WhereArg arg) {
            return switch (arg.getType()) {
                case WA_OPERATOR -> {
                    SelectElement attr = getAttrWithAliasForWhere(arg.getExpr(), arg.getJoinFrom());
                    Function<SelectElement, String> attrExprExtractor;
                    if (arg.requireWithExpr()) {
                        attrExprExtractor = SelectElement::getAlias;
                    } else if (arg.isForRowNumber()) {
                        attrExprExtractor = (a) -> format("%s.%s", rowNumberSubExprAlias, a.getAlias());//TODO check this when enableWithSubexpr is true
                    } else {
                        attrExprExtractor = SelectElement::getExpr;
                    }
                    yield buildWhereExpr(attr, attrExprExtractor, arg);
                }
                case WA_EXPR -> {
                    try {
                        String expr = arg.getExpr();
                        {
                            Matcher matcher = Pattern.compile(Pattern.quote("?") + "+").matcher(expr);
                            Iterator params = arg.getParams().iterator();
                            StringBuffer sb = new StringBuffer();
                            while (matcher.find()) {
                                if (equal(matcher.group(), "?")) {
                                    matcher.appendReplacement(sb, Matcher.quoteReplacement(systemToSqlExpr(params.next())));
                                } else if (equal(matcher.group(), "??")) {
                                    matcher.appendReplacement(sb, Matcher.quoteReplacement("?"));
                                } else {
                                    throw new DaoException("invalid part =< %s > found in expr =< %s >", matcher.group(), expr);
                                }
                            }
                            matcher.appendTail(sb);
                            checkArgument(!params.hasNext(), "found more params than replacement expressions ('?')");
                            expr = sb.toString();
                        }
                        if (arg.enableExprMarkerProcessing()) {
                            logger.trace("executing explicit alias processing for expr =< {} >", expr);
                            expr = expr.replace(Q3_MASTER, fromAlias);
                            logger.trace("executed explicit alias processing for expr, output =< {} >", expr);
                        }
                        if (hasJoin && arg.enableSmartAliasProcessing()) {//TODO duplicate code, improve this
                            expr = smartExprReplace(expr, getAliasForJoinElement(arg.getJoinTo()));
                        }
                        yield expr;
                    } catch (Exception ex) {
                        throw new DaoException(ex, "error processing sql expr = %s with params = %s", arg.getExpr(), arg.getParams());
                    }
                }
                case WA_COMPOSITE -> {
                    List<WhereElement> inners = arg.getInners().stream().map(this::whereArgToWhereElement).collect(toList());
                    yield switch (arg.getCompositeOperatorNotNull()) {
                        case AND ->
                            inners.stream().map(WhereElement::getExpr).collect(joining(" AND "));
                        case OR ->
                            wrapExprWithBrackets(inners.stream().map(WhereElement::getExpr).collect(joining(" OR ")));
                        case NOT ->
                            wrapExprWithBrackets(format("NOT %s", getOnlyElement(inners).getExpr()));
                        default ->
                            throw new DaoException("unsupported composite operator = %s", arg.getOperator());
                    };
                }
                default ->
                    throw new IllegalArgumentException("unsupported where arg type = " + arg.getType());
            };
        }

        private void processFilters(List<CmdbFilter> list) {
            list.forEach(filter -> {
                getHelper().buildWheresForFilter(filter, select).forEach(where::add);
            });
        }

        private SelectElement getAttrWithAliasForWhere(String attr, String joindId) {
            SelectElement attrWithAlias = select.stream().filter((a) -> a.getName().equals(attr) && a.getJoinFrom().equals(joindId)).collect(toOptional()).orElse(null);
            if (attrWithAlias == null) {
                String expr = getHelper().attrNameToSqlIdentifierExpr(attr, joindId);
                attrWithAlias = SelectElement.build(attr, expr, expr);
            }
            if (from.hasAttribute(attr) && from.getAttribute(attr).isOfType(AttributeTypeName.REGCLASS)) {
                attrWithAlias = SelectElement.build(attr, format("_cm3_utils_regclass_to_name(%s)", quoteSqlIdentifier(attr)), quoteSqlIdentifier(attr));//TODO improve this
            }
            return attrWithAlias;
        }

        private String buildWhereExpr(SelectElement attr, Function<SelectElement, String> attrExprExtractor, WhereArg whereArg) {
            List<Object> params = whereArg.getParams();
            String attrExpr = attrExprExtractor.apply(attr);
            Supplier<String> onlyElementExprSupplier = () -> systemToSqlExpr(getOnlyElement(params), attr.getSqlTypeHint());
            if (whereArg.hasJoinTo()) {
                String alias = getAliasForJoinElement(whereArg.getJoinTo());
                onlyElementExprSupplier = () -> format("%s.%s", alias, getOnlyElement(params));
            }
            WhereOperator operator = whereArg.getOperatorNotNull();
            return switch (operator) {
                case ISNULL -> {
                    checkArgument(params.isEmpty());
                    yield format("%s IS NULL", attrExpr);
                }
                case ISNOTNULL -> {
                    checkArgument(params.isEmpty());
                    yield format("%s IS NOT NULL", attrExpr);
                }
                case EQ ->
                    equal(onlyElementExprSupplier.get(), "NULL") ? format("%s IS NULL", attrExpr) : format("%s = %s", attrExpr, onlyElementExprSupplier.get());
                case EQ_CASE_INSENSITIVE ->
                    equal(onlyElementExprSupplier.get(), "NULL") ? format("%s IS NULL", attrExpr) : format("LOWER(%s) = LOWER(%s)", attrExpr, onlyElementExprSupplier.get());
                case NOTEQ ->
                    equal(onlyElementExprSupplier.get(), "NULL") ? format("%s IS NOT NULL", attrExpr) : format("%s <> %s", attrExpr, onlyElementExprSupplier.get());
                case LT ->
                    format("%s < %s", attrExpr, onlyElementExprSupplier.get());
                case GT ->
                    format("%s > %s", attrExpr, onlyElementExprSupplier.get());
                case LTEQ ->
                    format("%s <= %s", attrExpr, onlyElementExprSupplier.get());
                case GTEQ ->
                    format("%s >= %s", attrExpr, onlyElementExprSupplier.get());
                case IN ->
                    format("%s = ANY (%s)", attrExpr, systemToSqlExpr(set((Iterable) getOnlyElement(params)), attr.getSqlTypeHint()));
                case NOTIN ->
                    format("%s <> ALL (%s)", attrExpr, systemToSqlExpr(set((Iterable) getOnlyElement(params)), attr.getSqlTypeHint()));
                case INTERSECTS ->
                    format("%s && %s", attrExpr, systemToSqlExpr(set((Iterable) getOnlyElement(params)), attr.getSqlTypeHint()));
                case LIKE ->
                    format("%s LIKE %s", attrExpr, onlyElementExprSupplier.get());
                case MATCHES_REGEXP ->
                    format("%s ~ %s", attrExpr, onlyElementExprSupplier.get());
                case NOT_MATCHES_REGEXP ->
                    format("%s !~ %s", attrExpr, onlyElementExprSupplier.get());
                case BETWEEN -> {
                    checkArgument(params.size() == 2);
                    yield format("%s BETWEEN %s AND %s", attrExpr, systemToSqlExpr(params.get(0), attr.getSqlTypeHint()), systemToSqlExpr(params.get(1), attr.getSqlTypeHint()));
                }
                default ->
                    throw unsupported("unsupported operator = %s", whereArg.getOperatorNotNull());
            };
        }

        private void processJoinArgs(List<JoinQueryArgs> joinArgs) {
            joinArgs.forEach(this::processJoinArg);
        }

        private void processJoinArg(JoinQueryArgs arg) {
            JoinQueryElement element = new JoinQueryElement(arg.getJoinId(), arg.getFrom(), aliasBuilder.buildAlias(arg.getFrom().getName()));
            joinElements.add(element);
            List<SelectArg> selectArgs = prepareSelectArgs(arg.getFrom(), arg.getSelect(), false);
            selectArgs.stream().map(sa -> SelectArg.copyOf(sa).withJoinFrom(element.joinId).build()).map(getHelper()::processSelectArg).forEach(select::add);
            arg.getOnExprs().stream().map(this::whereArgToWhereElement).forEach(element.on::add);
            if (element.from.hasHistory() && activeCardsOnly) {
                element.on.add(WhereElement.build(format("%s.\"Status\" = 'A'", element.alias)));
            }
            arg.getWhere().stream().map(wa -> WhereArg.copyOf(wa).withJoinTo(arg.getJoinId()).build()).map(this::whereArgToWhereElement).forEach(where::add);//TODO check this
            checkArgument(arg.getFilters().isEmpty(), "join filters are not supported yet");//TODO
        }

        private String getAliasForJoinElement(String joinId) {
            if (equal(joinId, JOIN_ID_DEFAULT)) {
                return fromAlias;
            } else {
                return joinElements.stream().filter(j -> equal(j.joinId, joinId)).map(j -> j.alias).collect(onlyElement("join element not found for joinId =< %s >", joinId));
            }
        }

        public PreparedQueryExt doBuild() {
            checkNotNull(from);

            String query = format("SELECT %s FROM %s %s", select.stream().map(getHelper()::selectToExpr).collect(joining(", ")), entryTypeToSqlExpr(from), fromAlias);

            query += joinElements.stream().map(j -> {
                String joinQuery = format(" LEFT JOIN %s %s", entryTypeToSqlExpr(j.from), j.alias);
                if (!j.on.isEmpty()) {
                    joinQuery += format(" ON %s", j.on.stream().map(WhereElement::getExpr).distinct().collect(joining(" AND ")));
                }
                return joinQuery;
            }).collect(joining());

            List<WhereElement> whereElementsRequiringWithExpr = where.stream().filter(WhereElement::requireWithExpr).collect(toList());
            List<WhereElement> whereElementsNotForRowNumber = where.stream().filter(not(WhereElement::requireWithExpr)).filter(not(WhereElement::forRowNumber)).collect(toList());
            List<WhereElement> whereElementsFowRowNumber = where.stream().filter(not(WhereElement::requireWithExpr)).filter(WhereElement::forRowNumber).collect(toList());

            query += whereElementsToWhereExprBlankIfEmpty(whereElementsNotForRowNumber);

            if (!whereElementsRequiringWithExpr.isEmpty()) {
                String alias = aliasBuilder.buildAlias("subquery");
                query = format("WITH %s AS ( %s ) SELECT * FROM %s", alias, query, alias);
                query += whereElementsToWhereExprBlankIfEmpty(whereElementsRequiringWithExpr);
            }

            if (isQueryMode(QM_GROUP)) {
                SelectElement selectCount = buildCountSelectElement();
                String selectDistinctAlias = aliasBuilder.buildAlias("y"),
                        selectDistinctExpr = select.stream().filter(compose(getGroupByNames()::contains, SelectElement::getName)).map(SelectElement::getAlias).collect(joining(", "));
                select.add(selectCount);
                query = format("SELECT %s, %s %s FROM ( %s ) %s GROUP BY %s", selectDistinctExpr, selectCount.getExpr(), selectCount.getAlias(), query, selectDistinctAlias, selectDistinctExpr);
            }

            query += helper.buildOrderByExprOrBlank(sorter, select);

            String rowNumberAlias = null;
            if (isQueryMode(QM_ROWNUMBER)) {
                rowNumberAlias = aliasBuilder.buildAlias(ROW_NUMBER);
                query = format("SELECT *, ROW_NUMBER() OVER () AS %s FROM ( %s ) _rownumber_subquery", rowNumberAlias, query);
                query = format("SELECT * FROM ( %s ) %s %s", query, rowNumberSubExprAlias, whereElementsToWhereExprBlankIfEmpty(whereElementsFowRowNumber));
            }

            query += buildOffsetLimitExprOrBlank(queryOptions);

            List<SelectElement> preparedQuerySelect = switch (queryMode) {
                case QM_UPDATE -> {
                    String x = aliasBuilder.buildAlias("x"),
                            q = aliasBuilder.buildAlias("q");
                    query = format("WITH %s AS ( %s ) UPDATE %s %s SET %s FROM %s WHERE %s.\"Id\" = %s.%s RETURNING %s.*", q, query, entryTypeToSqlExpr(from), x, updateHelper.buildUpdateExpr(from, updateValues), q, x, q, select.getByName(ATTR_ID).getAlias(), q);
                    yield select.getElements();
                }
                case QM_DELETE -> {
                    String x = aliasBuilder.buildAlias("x"),
                            q = aliasBuilder.buildAlias("q");
                    if (from.hasHistory()) {
                        query = format("WITH %s AS ( %s ) UPDATE %s %s SET \"Status\" = 'N' FROM %s WHERE %s.\"Id\" = %s.%s RETURNING %s.*", q, query, entryTypeToSqlExpr(from), x, q, x, q, select.getByName(ATTR_ID).getAlias(), q);
                    } else {
                        query = format("WITH %s AS ( %s ) DELETE FROM %s %s USING %s WHERE %s.\"Id\" = %s.%s RETURNING %s.*", q, query, entryTypeToSqlExpr(from), x, q, x, q, select.getByName(ATTR_ID).getAlias(), q);
                    }
                    yield select.getElements();
                }
                case QM_COUNT -> {
                    SelectElement countAttr = buildCountSelectElement();
                    query = format("SELECT %s %s FROM ( %s ) %s", countAttr.getExpr(), countAttr.getAlias(), query, aliasBuilder.buildAlias("subquery"));
                    yield singletonList(countAttr);
                }
                case QM_ROWNUMBER ->
                    list(select.getElements()).with(SelectElement.build(ROW_NUMBER, ROW_NUMBER, checkNotBlank(rowNumberAlias)));
                case QM_GROUP ->
                    select.stream().filter(compose(set(getGroupByNames()).with(COUNT)::contains, SelectElement::getName)).collect(toList());
                case QM_SIMPLE ->
                    select.getElements();
            };

            return executorService.prepareQuery(query, preparedQuerySelect, where, from, cardMapper);
        }

        private Set<String> getGroupByNames() {
            return groupBy.stream().flatMap(g -> list(g, buildDescAttrName(g), buildCodeAttrName(g), buildInfoAttrName(g)).stream()).collect(toImmutableSet());
        }

        private SelectElement buildCountSelectElement() {
            return SelectElement.build(COUNT, "COUNT(*)", aliasBuilder.buildAlias(COUNT));
        }

    }

    private static String cqlFilterKey(CqlFilter filter) {
        return checkNotBlank(filter.getCqlExpression());
    }

}
