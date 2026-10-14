/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import jakarta.annotation.Nullable;
import jakarta.inject.Provider;
import static java.lang.String.format;
import java.util.Collection;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.common.beans.LookupValue;
import org.cmdbuild.cql.compiler.impl.CqlQueryImpl;
import org.cmdbuild.cql.compiler.impl.FieldImpl;
import org.cmdbuild.cql.compiler.impl.GroupImpl;
import org.cmdbuild.cql.compiler.impl.WhereImpl;
import static org.cmdbuild.dao.DaoConst.NULL;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.beans.RelationDirection;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.dao.beans.RelationDirection.RD_INVERSE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.driver.repository.ClasseReadonlyRepository;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.driver.repository.StoredFunctionRepository;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FOREIGNKEY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUP;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUPARRAY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.function.StoredFunction;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.JOIN_ID_DEFAULT;
import org.cmdbuild.dao.postgres.q3.beans.SelectArg;
import org.cmdbuild.dao.postgres.q3.beans.SelectElement;
import org.cmdbuild.dao.postgres.q3.beans.SelectHolder;
import org.cmdbuild.dao.postgres.q3.beans.WhereElement;
import static org.cmdbuild.dao.postgres.utils.QueryBuilderUtils.compactWhereElements;
import static org.cmdbuild.dao.postgres.utils.QueryBuilderUtils.compileCql;
import static org.cmdbuild.dao.postgres.utils.QueryBuilderUtils.getFulltextQueryParts;
import static org.cmdbuild.dao.postgres.utils.QueryBuilderUtils.smartExprReplace;
import org.cmdbuild.dao.postgres.utils.RelationDirectionQueryHelper;
import org.cmdbuild.dao.postgres.utils.SqlQueryUtils;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.Q3_MASTER;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.addSqlCastIfRequired;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.attributeTypeToSqlType;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildDescAttrName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildFileNameExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildLookupArrayDescExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildLookupDescExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildReferenceDescExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.escapeLikeExpression;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.functionCallSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.wrapExprWithBrackets;
import org.cmdbuild.dao.postgres.utils.SqlType;
import org.cmdbuild.dao.postgres.utils.SqlTypeName;
import static org.cmdbuild.dao.postgres.utils.SqlTypeName._bytea;
import static org.cmdbuild.dao.postgres.utils.SqlTypeName._int4;
import static org.cmdbuild.dao.postgres.utils.SqlTypeName._int8;
import static org.cmdbuild.dao.postgres.utils.SqlTypeName._regclass;
import static org.cmdbuild.dao.postgres.utils.SqlTypeName._varchar;
import static org.cmdbuild.dao.utils.CmSorterUtils.serializeSorter;
import org.cmdbuild.data.filter.AttributeFilter;
import org.cmdbuild.data.filter.AttributeFilterCondition;
import org.cmdbuild.data.filter.AttributeFilterConditionOperator;
import static org.cmdbuild.data.filter.AttributeFilterConditionOperator.DESCRIPTION_BEGIN;
import static org.cmdbuild.data.filter.AttributeFilterConditionOperator.DESCRIPTION_CONTAINS;
import static org.cmdbuild.data.filter.AttributeFilterConditionOperator.DESCRIPTION_END;
import static org.cmdbuild.data.filter.AttributeFilterConditionOperator.DESCRIPTION_NOTBEGIN;
import static org.cmdbuild.data.filter.AttributeFilterConditionOperator.DESCRIPTION_NOTCONTAIN;
import static org.cmdbuild.data.filter.AttributeFilterConditionOperator.DESCRIPTION_NOTEND;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.CompositeFilter;
import static org.cmdbuild.data.filter.CompositeFilter.CompositeFilterMode.CFM_AND;
import static org.cmdbuild.data.filter.CompositeFilter.CompositeFilterMode.CFM_NOT;
import static org.cmdbuild.data.filter.CompositeFilter.CompositeFilterMode.CFM_OR;
import org.cmdbuild.data.filter.FulltextFilter;
import org.cmdbuild.data.filter.FunctionFilter;
import org.cmdbuild.data.filter.RelationFilter;
import org.cmdbuild.data.filter.RelationFilterCardInfo;
import org.cmdbuild.data.filter.RelationFilterRule;
import org.cmdbuild.data.filter.RelationFilterRule.RelationFilterDirection;
import org.cmdbuild.data.filter.SorterElementDirection;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import org.cmdbuild.data.filter.beans.CmdbSorterImpl;
import org.cmdbuild.lookup.LookupRepository;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotEmpty;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.notNull;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QueryBuilderUtilsServiceImpl implements QueryBuilderUtilsService {

    private final static String ATTRIBUTE_EXPR = "cm_query_builder_helper_attribute_expr";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final StoredFunctionRepository functionRepository;
    private final Provider<OperationUserSupplier> operationUserSupplier;//TODO: refactor and remove this; user supplier should not be accessed from dao
    private final RefAttrHelperService refAttrHelperService;
    private final ClasseReadonlyRepository classeRepository;
    private final DomainRepository domainRepository;
    private final Provider<LookupRepository> lookupRepository;//TODO improve this
    private final Provider<LookupService> lookupService;

    public QueryBuilderUtilsServiceImpl(
            Provider<LookupRepository> lookupRepository,
            DomainRepository domainRepository,
            ClasseReadonlyRepository classeRepository,
            Provider<OperationUserSupplier> operationUserSupplier,
            StoredFunctionRepository functionRepository,
            RefAttrHelperService refAttrHelperService,
            Provider<LookupService> lookupService) {
        this.operationUserSupplier = checkNotNull(operationUserSupplier);
        this.functionRepository = checkNotNull(functionRepository);
        this.refAttrHelperService = checkNotNull(refAttrHelperService);
        this.domainRepository = checkNotNull(domainRepository);
        this.classeRepository = checkNotNull(classeRepository);
        this.lookupRepository = checkNotNull(lookupRepository);
        this.lookupService = checkNotNull(lookupService);
    }

    @Override
    public QueryBuilderHelperBuilder helper() {
        return new QueryBuilderHelperBuilder();
    }

    protected class QueryBuilderHelperImpl implements QueryBuilderHelper {

        private final AliasBuilder aliasBuilder;
        private final boolean addFromToIdentifiers;
        private final EntryType from;
        private final String fromAlias;
        private final Function<String, String> joinElementAliasSupplier, joinIdByExprSupplier;
        private final Function<String, Attribute> attributeByExprSupplier;
        private final QueryBuilderOptionsCommons builderOptions;

        private QueryBuilderHelperImpl(QueryBuilderHelperBuilder builder) {
            this.addFromToIdentifiers = firstNotNull(builder.addFromToIdentifiers, false);
            this.fromAlias = checkNotBlank(builder.fromAlias);
            this.from = checkNotNull(builder.from);
            this.aliasBuilder = checkNotNull(builder.aliasBuilder);
            this.joinElementAliasSupplier = firstNotNull(builder.joinElementAliasSupplier, (x) -> null);
            this.joinIdByExprSupplier = firstNotNull(builder.joinIdByExprSupplier, (x) -> null);
            this.attributeByExprSupplier = firstNotNull(builder.attributeByExprSupplier, from::getAttributeOrNull);
            this.builderOptions = firstNotNull(builder.builderOptions, () -> emptySet());
        }

        @Override
        public SelectElement processSelectArg(SelectArg arg) {
            logger.trace("process select arg = {}", arg);
            switch (arg.getType()) {
                case ST_EXPR -> {
                    String expr = (String) arg.getExpr();
                    if (arg.enableExprMarkerProcessing()) {
                        logger.trace("executing explicit alias processing for expr =< {} >", expr);
                        expr = expr.replace(Q3_MASTER, fromAlias);
                        logger.trace("executed explicit alias processing for expr, output =< {} >", expr);
                    } else if (addFromToIdentifiers) {
                        if (arg.enableSmartAliasProcessing()) {
                            expr = smartExprReplace(expr, getAliasForJoinElement(arg.getJoinFrom()));
                        } else {
                            expr = format("%s.%s", getAliasForJoinElement(arg.getJoinFrom()), expr);
                        }
                    }
                    return SelectElement.builder()
                            .withAlias(arg.hasAlias() ? arg.getAlias() : (builderOptions.enableAliasMapping() ? aliasBuilder.buildAlias(arg.getName()) : aliasBuilder.addAlias(arg.getName())))
                            .withExpr(expr)
                            .withJoinFrom(arg.getJoinFrom())
                            .withName(arg.getName())
                            .withParams(arg.getParams())
                            .build();
                }
                case ST_FILTER -> {
                    logger.trace("processing select arg of type filter = {}", arg.getExpr());
                    List<WhereElement> wheres = buildWheresForFilter(arg.getFilter());
                    String expr;
                    if (wheres.isEmpty()) {
                        expr = "TRUE";
                    } else {
                        WhereElement whereElement = compactWhereElements(wheres);
                        expr = wrapExprWithBrackets(whereElement.getExpr());
                        expr = format("COALESCE(%s, FALSE)", expr);
                    }
                    logger.trace("using filter expr = {}", expr);

                    return SelectElement.builder()
                            .withName(arg.getName())
                            .withExpr(expr)
                            .withAlias(arg.hasAlias() ? arg.getAlias() : aliasBuilder.buildAlias(arg.getName()))
                            //							.withParams(whereElement.getParams())
                            .build();
                }
                default ->
                    throw new DaoException("unsupported select arg type = %s (%s)", arg.getType());
            }
        }

        @Override
        public List<WhereElement> buildWheresForFilter(CmdbFilter filter, @Nullable SelectHolder select) {
            if (filter.isFalse()) {
                return singletonList(WhereElement.whereFalse());
            } else {
                List<WhereElement> list = list();
                OperationUser user = operationUserSupplier.get().getUser();//TODO this is not great (user should not be accessed from here)
                filter = filter.mapValues(map(
                        "@MY_GROUP", toStringOrEmpty(user.hasDefaultGroup() ? user.getDefaultGroup().getId() : null),
                        "@MY_USER", toStringOrEmpty(user.getId())
                ));
                if (filter.hasAttributeFilter()) {
                    list.add(buildFilterExpr(filter.getAttributeFilter()));
                }
                if (filter.hasRelationFilter()) {
                    list.add(buildFilterWhereExpr(filter.getRelationFilter()));
                }
                if (filter.hasCqlFilter()) {
                    CqlQueryImpl cql = compileCql(filter.getCqlFilter());
                    Optional.ofNullable(new CqlExprBuilder(cql.getWhere()).buildWhereExprOrNull()).ifPresent(list::add);
                }
                if (filter.hasFulltextFilter()) {
                    list.add(buildFulltextFilterWhere(filter.getFulltextFilter(), select));
                }
                if (filter.hasCompositeFilter()) {
                    list.addAll(buildWheresForCompositeFilter(filter.getCompositeFilter()));
                }
                if (filter.hasFunctionFilter()) {
                    list.addAll(buildWheresForFunctionFilter(filter.getFunctionFilter()));
                }
                return list;
            }
        }

        @Override
        public String attrNameToSqlIdentifierExpr(String name, String joinId) {
            String expr = quoteSqlIdentifier(name);
            if (addFromToIdentifiers) {
                expr = format("%s.%s", getAliasForJoinElement(joinId), expr);
            }
            return expr;
        }

        @Override
        public String attrNameToSqlIdentifierExpr(String expr) {
            return attrNameToSqlIdentifierExpr(Optional.ofNullable(getAttributeOrNull(expr)).map(Attribute::getName).orElse(expr), firstNotBlank(joinIdByExprSupplier.apply(expr), JOIN_ID_DEFAULT));
        }

        @Override
        public String buildDescAttrExprForLookup(Attribute a) {
            return buildDescAttrExprForLookup(a, null);
        }

        @Override
        public String buildDescAttrExprForLookupArray(Attribute a, String value) {
            checkArgument(a.isOfType(AttributeTypeName.LOOKUPARRAY));
            return buildLookupArrayDescExpr(hasJoinId(a) ? getAliasForJoinElement(getJoinId(a)) : fromAlias, exprForAttribute(a), value);
        }

        @Override
        public String buildDescAttrExprForReference(Attribute a) {
            return buildDescAttrExprForReference(a, null);
        }

        @Override
        public String buildFileNameAttrExprForFile(Attribute a) {
            Classe targetClass = refAttrHelperService.getTargetClassForAttribute(a);
            return buildFileNameExpr(targetClass, hasJoinId(a) ? getAliasForJoinElement(getJoinId(a)) : fromAlias, exprForAttribute(a));//
        }

        @Override
        public String selectToExpr(SelectElement a) {
            String expr = a.getExpr();
            Attribute attr = getAttributeOrNull(a.getName());
            if (attr != null) {
                expr = addSqlCastIfRequired(attr.getType(), expr);
            }
            String aliasExpr = quoteSqlIdentifier(a.getAlias());
            return equal(expr, aliasExpr) ? expr : format("%s %s", expr, aliasExpr);
        }

        @Override
        public String buildOrderByExprOrBlank(CmdbSorter sorter, SelectHolder select) {
            try {
                if (sorter.isNoop()) {
                    return "";
                } else {
                    if (!sorter.hasAttr(ATTR_ID) && hasAttribute(ATTR_ID)) {
                        sorter = sorter.thenSortBy(CmdbSorterImpl.sorter(ATTR_ID, SorterElementDirection.DESC));
                    }
                    List<String> sortExprList = sorter.getElements().stream().map((s) -> {
                        String name = s.getProperty();
                        String direction = s.getDirection().toSql();
                        SelectElement attr = select.getByName(name);
                        if (hasAttribute(name)) {
                            Attribute a = getAttribute(name);
                            switch (a.getType().getName()) {
                                case REFERENCE, FOREIGNKEY ->
                                    attr = select.getByNameOrBuild(buildDescAttrName(name), () -> buildExtendedAttr(buildDescAttrName(name), buildDescAttrExprForReference(a)));
                                case LOOKUP ->
                                    attr = select.getByNameOrBuild(buildDescAttrName(name), () -> buildExtendedAttr(buildDescAttrName(name), buildDescAttrExprForLookup(a)));
                            }
                        }
                        return format("%s %s", quoteSqlIdentifier(attr.getAlias()), direction);
                    }).collect(toList());
                    return format(" ORDER BY %s", Joiner.on(", ").join(sortExprList));
                }
            } catch (Exception ex) {
                throw new DaoException(ex, "error processing query ordering = %s", serializeSorter(sorter));
            }
        }

        @Override
        public CmdbSorter fixAliasesInSorter(CmdbSorter s) {
            return s.isNoop() ? s : s.mapAttributeNames(from.getAliasToAttributeMap());
        }

        private String buildDescAttrExprForLookup(Attribute a, @Nullable SelectHolder select) {
            checkArgument(a.isOfType(AttributeTypeName.LOOKUP));
            SelectElement element = select == null ? null : select.getByNameAndJoinOrNull(getJoinId(a), buildDescAttrName(a.getName()));
            return element != null ? element.getExpr() : buildLookupDescExpr(hasJoinId(a) ? getAliasForJoinElement(getJoinId(a)) : fromAlias, exprForAttribute(a));
        }

        private String buildDescAttrExprForReference(Attribute a, @Nullable SelectHolder select) {
            Classe targetClass = refAttrHelperService.getTargetClassForAttribute(a);
            checkArgument(targetClass.hasAttribute(ATTR_DESCRIPTION), "cannot select description for referenced class = %s: this class does not have a description attr", targetClass);
            SelectElement element = select == null ? null : select.getByNameAndJoinOrNull(getJoinId(a), buildDescAttrName(a.getName()));
            return element != null ? element.getExpr() : buildReferenceDescExpr(targetClass, hasJoinId(a) ? getAliasForJoinElement(getJoinId(a)) : fromAlias, exprForAttribute(a));
        }

        private String getJoinId(Attribute a) {
            return firstNotBlank(joinIdByExprSupplier.apply(attributeExprOrName(a)), JOIN_ID_DEFAULT);
        }

        private boolean hasJoinId(Attribute a) {
            return !equal(getJoinId(a), JOIN_ID_DEFAULT);
        }

        @Nullable
        private Attribute getAttributeOrNull(String expr) {
            checkNotBlank(expr);
            return Optional.ofNullable(attributeByExprSupplier.apply(expr)).map(a -> AttributeImpl.copyOf(a).withMeta(ATTRIBUTE_EXPR, expr).build()).orElse(null);
        }

        private boolean hasAttribute(String name) {
            return getAttributeOrNull(name) != null;
        }

        private Attribute getAttribute(String name) {
            return checkNotNull(getAttributeOrNull(name), "attribute not found for name =< %s >", name);
        }

        private SelectElement buildExtendedAttr(String name, String expr) {
            return SelectElement.build(name, expr, aliasBuilder.buildAlias(name));
        }

        private String exprForAttribute(Attribute a) {
            return quoteSqlIdentifier(a.getName());
        }

        private WhereElement buildFilterExpr(AttributeFilter filter) {
            try {
                return switch (filter.getMode()) {
                    case AND ->
                        filter.hasOnlyElement() ? buildFilterExpr(filter.getOnlyElement()) : joinWhereElements(filter.getElements().stream().map(this::buildFilterExpr).collect(toList()), "AND");
                    case OR ->
                        filter.hasOnlyElement() ? buildFilterExpr(filter.getOnlyElement()) : joinWhereElements(filter.getElements().stream().map(this::buildFilterExpr).collect(toList()), "OR").mapExpr(SqlQueryUtils::wrapExprWithBrackets);
                    case SIMPLE ->
                        buildFilterExpr(filter.getCondition());
                    case NOT ->
                        buildFilterExpr(filter.getOnlyElement()).mapExpr((x) -> format("NOT %s", x));
                    default ->
                        throw new UnsupportedOperationException("unsupported filter mode = " + filter.getMode());
                };
            } catch (Exception ex) {
                throw new DaoException(ex, "error processing attribute filter = %s", filter);
            }
        }

        private WhereElement joinWhereElements(Collection<WhereElement> elements, String operator) {
            String expr = elements.stream().map(WhereElement::getExpr).map(SqlQueryUtils::wrapExprWithBrackets).collect(joining(format(" %s ", operator)));
            return WhereElement.build(expr);
        }

        public WhereElement buildFilterExpr(AttributeFilterCondition condition) {
            return switch (condition.getOperator()) {
                case FALSE ->
                    WhereElement.whereFalse();
                case TRUE ->
                    WhereElement.whereTrue();
                default ->
                    new ConditionExprBuilder(condition).build();
            };
        }

        private List<WhereElement> buildWheresForFunctionFilter(FunctionFilter functionFilter) {
            OperationUser user = operationUserSupplier.get().getUser();//TODO this is not great (user should not be accessed from here)
            Long userId = user.getId(),
                    groupId = user.hasDefaultGroup() ? user.getDefaultGroup().getId() : null;
            String className = from == null ? null : from.getName();
            return functionFilter.getFunctions().stream().map(f -> {
                String functionName = f.getName();
                logger.debug("processing function filter with name =< {} >", functionName);
                StoredFunction storedFunction = functionRepository.getFunctionByName(functionName);
                logger.debug("processing function filter with function = {}", storedFunction);
                return WhereElement.build(format("%s IN (SELECT %s)", attrNameToSqlIdentifierExpr(ATTR_ID), functionCallSqlExpr(storedFunction.getName(), userId, groupId, className)));//TODO auto set filter function params, improve filter function params
            }).collect(toList());
        }

        private List<WhereElement> buildWheresForCompositeFilter(CompositeFilter compositeFilter) {
            return switch (compositeFilter.getMode()) {
                case CFM_AND ->
                    compositeFilter.getElements().stream().map(this::buildWheresForFilter).flatMap(List::stream).collect(toList());
                case CFM_OR ->
                    singletonList(compactWhereElements(compositeFilter.getElements().stream().map(this::buildWheresForFilter).flatMap(List::stream).collect(toList()), "OR"));
                case CFM_NOT -> {
                    WhereElement element = compactWhereElements(buildWheresForFilter(compositeFilter.getElement()));
//                    return singletonList(WhereElement.build(format("NOT %s", element.getExpr()), element.getParams()));
                    yield singletonList(WhereElement.build(format("NOT %s", element.getExpr())));
                }
                default ->
                    throw unsupported("unsupported composite filter mode = %s", compositeFilter.getMode());
            };
        }

        private WhereElement buildFulltextFilterWhere(FulltextFilter fulltextFilter, @Nullable SelectHolder select) {
            return buildFulltextFilterWhere(fulltextFilter.getQuery(), select);
        }

        private WhereElement buildFulltextFilterWhere(String query, @Nullable SelectHolder select) {
            return WhereElement.build(buildFulltextFilterExpr(query, from.getServiceAttributes(), select));
        }

        private String buildFulltextFilterExpr(String query, Collection<Attribute> attrs, @Nullable SelectHolder select) {
            List<String> queryWords = checkNotEmpty(list(getFulltextQueryParts(checkNotBlank(query))).map(w -> format("%%%s%%", escapeLikeExpression(checkNotBlank(w)))));
            List<String> parts = attrs.stream().map((a) -> {
                List<String> list = queryWords.stream().map(queryVal -> {
                    return switch (a.getType().getName()) {
                        case STRING, TEXT, LINK ->
                            format("%s ILIKE %s", attrNameToSqlIdentifierExpr(a.getName()), systemToSqlExpr(queryVal));
                        case INET, DECIMAL, INTEGER, LONG, DOUBLE, FLOAT, DATE, TIMESTAMP, TIME ->
                            format("%s::varchar ILIKE %s", attrNameToSqlIdentifierExpr(a.getName()), systemToSqlExpr(queryVal));
                        case REFERENCE, FOREIGNKEY ->
                            format("%s ILIKE %s", buildDescAttrExprForReference(a, select), systemToSqlExpr(queryVal));
                        case FILE ->
                            format("%s ILIKE %s", buildFileNameAttrExprForFile(a), systemToSqlExpr(queryVal));
                        case LOOKUP ->
                            format("%s ILIKE %s", buildDescAttrExprForLookup(a, select), systemToSqlExpr(queryVal));
                        case LOOKUPARRAY ->
                            format("%s", buildDescAttrExprForLookupArray(a, systemToSqlExpr(queryVal)));
                        default ->
                            null;
                    };
                }).filter(notNull()).collect(toImmutableList());
                return list.isEmpty() ? null : list.size() == 1 ? getOnlyElement(list) : wrapExprWithBrackets(Joiner.on(" AND ").join(list));
            }).filter(notNull()).collect(toImmutableList());
            return parts.isEmpty() ? WhereElement.whereFalse().getExpr() : parts.size() == 1 ? getOnlyElement(parts) : wrapExprWithBrackets(Joiner.on(" OR ").join(parts));
        }

        private String getAliasForJoinElement(String joinId) {
            return checkNotBlank(joinElementAliasSupplier.apply(joinId), "join element alias not found for joinId =< %s >", joinId);
        }

        private WhereElement buildFilterWhereExpr(RelationFilter filter) {
            List<WhereElement> list = filter.getRelationFilterRules().stream().map(this::buildFilterExpr).collect(toList());
            if (list.size() == 1) {
                return getOnlyElement(list);
            } else {
                String expr = list.stream().map(WhereElement::getExpr).map(SqlQueryUtils::wrapExprWithBrackets).collect(joining(" AND "));
                return WhereElement.build(expr);
            }
        }

        private WhereElement buildFilterExpr(RelationFilterRule filter) {
            Domain domain = domainRepository.getDomain(filter.getDomain()).getThisDomainWithDirection(toRelationDirection(filter.getDirection()));
            RelationDirectionQueryHelper helper = RelationDirectionQueryHelper.forDirection(toRelationDirection(filter.getDirection()));
            checkArgument(domain.isDomainForSourceClasse((Classe) from), "CMO 999: invalid domain in relation filter, domain =< %s > is not valid for type =< %s > with direction =< %s >", domain.getName(), from.getName(), helper.toRelationDirection());
            String subqueryBase = format("SELECT 1 FROM %s WHERE %s = %s.\"Id\" AND %s = %s.\"IdClass\" AND \"Status\" = 'A'", entryTypeToSqlExpr(domain), helper.getSourceCardIdExpr(), fromAlias, helper.getSourceClassIdExpr(), fromAlias);
            return switch (filter.getType()) {
                case ANY ->
                    WhereElement.build(format("EXISTS %s", wrapExprWithBrackets(subqueryBase)));
                case NOONE ->
                    WhereElement.build(format("NOT EXISTS %s", wrapExprWithBrackets(subqueryBase)));
                case ONEOF -> {
                    List<String> exprs = list();
                    filter.getCardInfos().stream().map(RelationFilterCardInfo::getClassName).distinct().forEach((c) -> {
                        Classe classe = classeRepository.getClasse(c);
                        checkArgument(domain.getTargetClasses().contains(classe), "invalid target class =< %s > for domain =< %s > with direction = %s", classe.getName(), domain.getName(), filter.getDirection());
                        Set<Long> ids = filter.getCardInfos().stream().filter((ci) -> ci.getClassName().equals(c)).map(RelationFilterCardInfo::getId).collect(toSet());
                        String idExpr = ids.size() == 1 ? format("= %s", systemToSqlExpr(getOnlyElement(ids))) : format("= ANY (%s)", systemToSqlExpr(ids));
                        exprs.add(format("%s = %s AND %s %s", helper.getTargetClassIdExpr(), systemToSqlExpr(classe), helper.getTargetCardIdExpr(), idExpr));
                    });
                    String oneOfExpr = exprs.size() == 1 ? getOnlyElement(exprs) : wrapExprWithBrackets(exprs.stream().map(SqlQueryUtils::wrapExprWithBrackets).collect(joining(" OR ")));
                    yield WhereElement.build(format("EXISTS (%s AND %s)", subqueryBase, oneOfExpr));
                }
                case FROMFILTER -> {
                    //TODO improve this, use existing join and avoid ad-hoc subquery when possible (??)
                    CmdbFilter subFilter = CmdbFilterImpl.builder().withAttributeFilter(filter.getFilter()).build();
                    if (!subFilter.isNoop()) {
                        String targetAlias = aliasBuilder.buildAlias(domain.getTargetClass().getName()), domainAlias = aliasBuilder.buildAlias(domain.getName());
                        QueryBuilderHelper targetHelper = helper().withAliasBuilder(aliasBuilder).withFromAlias(targetAlias).withFrom(domain.getTargetClass()).build();
                        subqueryBase = format("SELECT 1 FROM %s %s JOIN %s %s ON %s.%s = %s.\"Id\" AND %s.%s = %s.\"IdClass\" WHERE %s.\"Status\" = 'A' AND  %s.\"Status\" = 'A' AND %s.%s = %s.\"Id\" AND %s.%s = %s.\"IdClass\" AND %s",
                                entryTypeToSqlExpr(domain), domainAlias, entryTypeToSqlExpr(domain.getTargetClass()), targetAlias, domainAlias, helper.getTargetCardIdExpr(), targetAlias, domainAlias, helper.getTargetClassIdExpr(), targetAlias,
                                domainAlias, targetAlias,
                                domainAlias, helper.getSourceCardIdExpr(), fromAlias, domainAlias, helper.getSourceClassIdExpr(), fromAlias,
                                checkNotBlank(targetHelper.buildWheresForFilter(subFilter).stream().map(w -> w.getExpr()).collect(joining(" AND ")))
                        );
                    }
                    yield WhereElement.build(format("EXISTS %s", wrapExprWithBrackets(subqueryBase)));
                }
                default ->
                    throw new UnsupportedOperationException("unsupported relation filter type = " + filter.getType());
            };
        }

        private class ConditionExprBuilder {

            private final AttributeFilterCondition condition;
            private final Attribute attribute;
            private final String attrExpr;
            private final SqlType sqlType;

            private final Set<AttributeFilterConditionOperator> descriptionOperators = EnumSet.of(
                    DESCRIPTION_BEGIN,
                    DESCRIPTION_END,
                    DESCRIPTION_CONTAINS,
                    DESCRIPTION_NOTBEGIN,
                    DESCRIPTION_NOTCONTAIN,
                    DESCRIPTION_NOTEND);

            public ConditionExprBuilder(AttributeFilterCondition condition) {
                this.condition = condition;
                attribute = getAttribute(condition.getKey());
                sqlType = attributeTypeToSqlType(attribute.getType());
//                String expr = attrNameToSqlIdentifierExpr(attribute.getName());
                attrExpr = attrNameToSqlIdentifierExpr(attributeExprOrName(attribute));

//                switch (sqlType.getType()) {
//                    case regclass:
//                        expr = format("_cm3_utils_regclass_to_name(%s)", expr);//TODO improve this
//                        break;
//                }
//                attrExpr = expr;
                //TODO validate operator for attribute type (es INET_* operators require Inet attr type)
            }

            public WhereElement build() {
                return WhereElement.build(buildConditionExpr());
            }

            private String buildConditionExpr() {
                if (attribute.isOfType(LOOKUP) && descriptionOperators.contains(condition.getOperator())) {
                    List<Long> lookupIds = handleLookupDescriptionFilter();
                    return format("%s = ANY (%s)", attrExpr, systemToSqlExpr(lookupIds, getArraySqlType()));
                } else if (attribute.isOfType(LOOKUPARRAY) && descriptionOperators.contains(condition.getOperator())) {
                    List<Long> lookupIds = handleLookupDescriptionFilter();
                    return format("%s && %s", attrExpr, systemToSqlExpr(lookupIds, getArraySqlType()));
                }
                return switch (condition.getOperator()) {
                    case ISNULL ->
                        switch (attribute.getType().getName()) {
                            case LOOKUPARRAY ->
                                format("( %s IS NULL OR cardinality(%s) = 0 )", attrExpr, attrExpr);
                            default ->
                                format("%s IS NULL", attrExpr);
                        };
                    case ISNOTNULL ->
                        switch (attribute.getType().getName()) {
                            case LOOKUPARRAY ->
                                format("( %s IS NOT NULL AND cardinality(%s) > 0 )", attrExpr, attrExpr);
                            default ->
                                format("%s IS NOT NULL", attrExpr);
                        };
                    case EQUAL ->
                        hasNullSingleValue() ? format("%s IS NULL", attrExpr) : format("%s = %s", attrExpr, getSingleValue());

                    case NOTEQUAL ->
                        hasNullSingleValue() ? format("%s IS NOT NULL", attrExpr) : format("%s IS DISTINCT FROM %s", attrExpr, getSingleValue()); //note: must use distinct from to handle NULL case
                    case IN ->
                        format("%s = ANY (%s)", attrExpr, systemToSqlExpr(condition.getValues(), getArraySqlType()));
                    case OVERLAP ->
                        format("%s && %s", attrExpr, systemToSqlExpr(condition.getValues(), getArraySqlType()));
                    case NOTOVERLAP ->
                        format("NOT ( %s && %s )", attrExpr, systemToSqlExpr(condition.getValues(), getArraySqlType()));
                    case BEGIN ->
                        format("%s ILIKE %s", attrExpr, getSingleValueForLike(null, "%"));
                    case DESCRIPTION_BEGIN -> {
                        switch (attribute.getType().getName()) {
                            case REFERENCE, FOREIGNKEY -> {
                                String aliasExpr = buildDescAttrExprForReference(attribute);
                                yield format("%s ILIKE %s", aliasExpr, getSingleValueForLike(null, "%"));
                            }
                            default ->
                                throw new IllegalArgumentException("invalid attribute type for `description_begin` operator, type = " + attribute.getType().getName());
                        }
                    }
                    case END ->
                        format("%s ILIKE %s", attrExpr, getSingleValueForLike("%", null));
                    case DESCRIPTION_END ->
                        switch (attribute.getType().getName()) {
                            case REFERENCE, FOREIGNKEY -> {
                                String aliasExpr = buildDescAttrExprForReference(attribute);
                                yield format("%s ILIKE %s", aliasExpr, getSingleValueForLike("%", null));
                            }
                            default ->
                                throw new IllegalArgumentException("invalid attribute type for `description_end` operator, type = " + attribute.getType().getName());
                        };
                    case CONTAIN ->
                        switch (attribute.getType().getName()) { //TODO improve this, duplicate code/pattern (see LIKE, BEGIN, END etc)
                            case REFERENCE, FOREIGNKEY -> {
                                String aliasExpr = buildDescAttrExprForReference(attribute);
                                yield format("%s ILIKE %s", aliasExpr, getSingleValueForLike("%", "%"));
                            }
                            default ->
                                format("%s::varchar ILIKE %s", attrExpr, getSingleValueForLike("%", "%")); //TODO improve this, check cast ::varchar
                        };
                    //TODO improve this, duplicate code/pattern (see LIKE, BEGIN, END etc)
                    case DESCRIPTION_CONTAINS -> {
                        switch (attribute.getType().getName()) {
                            case REFERENCE, FOREIGNKEY -> {
                                String aliasExpr = buildDescAttrExprForReference(attribute);
                                yield format("%s ILIKE %s", aliasExpr, getSingleValueForLike("%", "%"));
                            }
                            default ->
                                throw new IllegalArgumentException("invalid attribute type for `description_contains` operator, type = " + attribute.getType().getName());
                        }
                    }
                    case FULLTEXT ->
                        buildFulltextFilterExpr(condition.getSingleValue(), singleton(attribute), null);
                    case LIKE ->
                        format("%s ILIKE %s", attrExpr, getSingleValueForLike(null, null));
                    case NOTBEGIN ->
                        format("( %s IS NULL OR NOT %s ILIKE %s )", attrExpr, attrExpr, getSingleValueForLike(null, "%"));
                    case DESCRIPTION_NOTBEGIN ->
                        switch (attribute.getType().getName()) {
                            case REFERENCE, FOREIGNKEY -> {
                                String aliasExpr = buildDescAttrExprForReference(attribute);
                                yield format("( %s IS NULL OR NOT %s ILIKE %s )", aliasExpr, aliasExpr, getSingleValueForLike(null, "%"));
                            }
                            default ->
                                throw new IllegalArgumentException("invalid attribute type for `description_end` operator, type = " + attribute.getType().getName());
                        };
                    case NOTCONTAIN ->
                        switch (attribute.getType().getName()) {
                            case LOOKUPARRAY ->
                                format("( %s IS NULL OR NOT %s::varchar ILIKE %s )", attrExpr, attrExpr, getSingleValueForLike("%", "%"));
                            default ->
                                format("( %s IS NULL OR NOT %s ILIKE %s )", attrExpr, attrExpr, getSingleValueForLike("%", "%"));
                        };
                    case DESCRIPTION_NOTCONTAIN ->
                        switch (attribute.getType().getName()) {
                            case REFERENCE, FOREIGNKEY -> {
                                String aliasExpr = buildDescAttrExprForReference(attribute);
                                yield format("( %s IS NULL OR NOT %s ILIKE %s )", aliasExpr, aliasExpr, getSingleValueForLike("%", "%"));
                            }
                            default ->
                                throw new IllegalArgumentException("invalid attribute type for `description_end` operator, type = " + attribute.getType().getName());
                        };
                    case NOTEND ->
                        format("( %s IS NULL OR NOT %s ILIKE %s )", attrExpr, attrExpr, getSingleValueForLike("%", null));
                    case DESCRIPTION_NOTEND ->
                        switch (attribute.getType().getName()) {
                            case REFERENCE, FOREIGNKEY -> {
                                String aliasExpr = buildDescAttrExprForReference(attribute);
                                yield format("( %s IS NULL OR NOT %s ILIKE %s )", aliasExpr, aliasExpr, getSingleValueForLike("%", null));
                            }
                            default ->
                                throw new IllegalArgumentException("invalid attribute type for `description_end` operator, type = " + attribute.getType().getName());
                        };
                    case GREATER ->
                        format("%s > %s", attrExpr, getSingleValue());
                    case LESS ->
                        format("%s < %s", attrExpr, getSingleValue());
                    case BETWEEN -> {
                        checkArgument(condition.getValues().size() == 2, "between operator requires exactly two parameters");
                        yield format("%s BETWEEN %s AND %s", attrExpr, systemToSqlExpr(condition.getValues().get(0)), systemToSqlExpr(condition.getValues().get(1)));
                    }
                    case NET_CONTAINED ->
                        format("%s << %s", attrExpr, getSingleValue());
                    case NET_CONTAINEDOREQUAL ->
                        format("%s <<= %s", attrExpr, getSingleValue());
                    case NET_CONTAINS ->
                        format("%s >> %s", attrExpr, getSingleValue());
                    case NET_CONTAINSOREQUAL ->
                        format("%s >>= %s", attrExpr, getSingleValue());
                    case NET_RELATION -> {
                        String valueExpr = getSingleValue();
                        yield format("( %s <<= %s OR %s = %s OR %s >>= %s )", attrExpr, valueExpr, attrExpr, valueExpr, attrExpr, valueExpr);
                    }
                    default ->
                        throw new UnsupportedOperationException("unsupported condition operator = " + condition.getOperator());
                };
            }

            private List<Long> handleLookupDescriptionFilter() {
                List<LookupValue> lookupByType = lookupService.get().getAllTranslatedLookup(attribute.getType().as(LookupAttributeType.class).getLookupTypeName())
                        .stream().filter(l -> isNotBlank(l.getDescription())).collect(toList());
                String lowercaseValue = condition.getSingleValue().toLowerCase();
                return lookupByType.stream().filter(filterLookupDescription(lowercaseValue)).map(LookupValue::getId).collect(toList());
            }

            private Predicate<LookupValue> filterLookupDescription(String lowercaseValue) {
                return l -> {
                    String lookupDescrLowerCase = l.getDescription().toLowerCase();
                    return switch (condition.getOperator()) {
                        case DESCRIPTION_BEGIN ->
                            lookupDescrLowerCase.startsWith(lowercaseValue);
                        case DESCRIPTION_END ->
                            lookupDescrLowerCase.endsWith(lowercaseValue);
                        case DESCRIPTION_CONTAINS ->
                            lookupDescrLowerCase.contains(lowercaseValue);
                        case DESCRIPTION_NOTBEGIN ->
                            !lookupDescrLowerCase.startsWith(lowercaseValue);
                        case DESCRIPTION_NOTCONTAIN ->
                            !lookupDescrLowerCase.contains(lowercaseValue);
                        case DESCRIPTION_NOTEND ->
                            !lookupDescrLowerCase.endsWith(lowercaseValue);
                        default ->
                            throw unsupported("unsupported condition operator = %s", condition.getOperator());
                    };
                };
            }

            @Nullable
            private SqlTypeName getArraySqlType() {
                return switch (sqlType.getType()) {
                    case int4 ->
                        _int4;
                    case int8, _int8 ->
                        _int8;
                    case varchar ->
                        _varchar;
                    case bytea ->
                        _bytea;
                    case regclass ->
                        _regclass;
                    default ->
                        null;
                }; //TODO improve this;
            }

            private boolean hasNullSingleValue() {
                return equal(NULL, getSingleValue());
            }

            private String getSingleValue() {
                Object value = condition.getSingleValue();
                if (isNotBlank(value)) {
                    switch (attribute.getType().getName()) {
                        case LOOKUP ->
                            value = lookupRepository.get().getOneByTypeAndCodeOrId(attribute.getType().as(LookupAttributeType.class).getLookupTypeName(), value).getId();//TODO improve this, move conversion elsewhere
                    }
                }
                return systemToSqlExpr(value, attribute);
            }

            private String getSingleValueForLike(@Nullable String before, @Nullable String after) {
                if (condition.getSingleValue() != null) {
                    return systemToSqlExpr(nullToEmpty(before) + escapeLikeExpression(condition.getSingleValue()) + nullToEmpty(after));
                } else {
                    throw unsupported("unsupported null filter for attribute =< %s >", attribute.getName());
                }
            }

        }

        private class CqlExprBuilder {

            private final WhereImpl cqlWhere;

            public CqlExprBuilder(WhereImpl cqlWhere) {
                this.cqlWhere = checkNotNull(cqlWhere);
            }

            @Nullable
            public WhereElement buildWhereExprOrNull() {
                String whereExpr = buildWhereExprOrNull(cqlWhere);
                if (isBlank(whereExpr)) {
                    return null;
                } else {
                    return WhereElement.build(whereExpr);
                }
            }

            @Nullable
            private String buildWhereExprOrNull(org.cmdbuild.cql.compiler.where.WhereElement whereElement) {
                if (whereElement instanceof FieldImpl fieldImpl) {
                    return buildWhereExpr(fieldImpl);
                } else if (whereElement instanceof GroupImpl || whereElement instanceof WhereImpl) {
                    return whereElement.getElements().stream().map(this::buildWhereExprOrNull).collect(joining(" AND "));
                } else {
                    throw unsupported("unsupported cql where element = %s", whereElement);
                }
            }
//        private boolean selectHasAttrWithName(String name) {
//            return select.stream().map(SelectElement::getName).anyMatch(equalTo(name));
//        }

            private String buildWhereExpr(FieldImpl field) {
                String name = field.getId().getId();
                String attrExpr;
//                if (selectHasAttrWithName(name)) { TODO check this
//                    SelectElement attr = getAttrFromSelectByName(name);
//                    attrExpr = attr.getExpr();
//                } else {
                attrExpr = attrNameToSqlIdentifierExpr(name);
//                }
                Attribute attribute = getAttribute(name);
                return switch (field.getOperator()) { //TODO add support for NOT in the below cases like for EQ
                    case EQ -> {
                        Object value = getFieldValue(field);
                        boolean isNot = firstNotNull(field.isNot(), false);
                        String operator = isNot ? "<>" : "=";
                        if (value == null) {
                            yield format("%s IS NULL", attrExpr);
                        } else {
                            yield switch (getOnlyElement(field.getValues()).getType()) {
                                case NATIVE ->
                                    format("%s %s %s", attrExpr, operator, wrapExprWithBrackets(toStringNotBlank(value)));
                                default ->
                                    format("%s %s %s", attrExpr, operator, systemToSqlExpr(value, attribute));
                            };
                        }
                    }
                    case GT -> {
                        Object value = checkNotNull(getFieldValue(field));
                        yield switch (getOnlyElement(field.getValues()).getType()) {
                            case NATIVE ->
                                format("%s > %s", attrExpr, wrapExprWithBrackets(toStringNotBlank(value)));
                            default ->
                                format("%s > %s", attrExpr, systemToSqlExpr(value, attribute));
                        };
                    }
                    case LT -> {
                        Object value = checkNotNull(getFieldValue(field));
                        yield switch (getOnlyElement(field.getValues()).getType()) {
                            case NATIVE ->
                                format("%s < %s", attrExpr, wrapExprWithBrackets(toStringNotBlank(value)));
                            default ->
                                format("%s < %s", attrExpr, systemToSqlExpr(value, attribute));
                        };
                    }
                    case IN -> {
                        Object value = getFieldValue(field);
                        if (value == null) {
                            yield format("%s IS NULL", attrExpr);
                        } else {
                            yield switch (getOnlyElement(field.getValues()).getType()) {
                                case NATIVE ->
                                    format("%s IN %s", attrExpr, wrapExprWithBrackets(toStringNotBlank(value)));
                                default ->
                                    throw unsupported("unsupported cql field value type = %s", getOnlyElement(field.getValues()).getType());
                            };
                        }
                    }
                    default ->
                        throw unsupported("unsupported cql operator = %s", field.getOperator());
                };
            }

            @Nullable
            private Object getFieldValue(FieldImpl field) {
                return applyOrNull(getOnlyElement(field.getValues(), null), v -> v.getValue());
            }

        }
    }

    public class QueryBuilderHelperBuilder implements Builder<QueryBuilderHelper, QueryBuilderHelperBuilder> {

        private Boolean addFromToIdentifiers;
        private String fromAlias;
        private AliasBuilder aliasBuilder;
        private EntryType from;
        private Function<String, String> joinElementAliasSupplier, joinIdByExprSupplier;
        private Function<String, Attribute> attributeByExprSupplier;
        private QueryBuilderOptionsCommons builderOptions;

        public QueryBuilderHelperBuilder withAttributeByExprSupplier(Function<String, Attribute> attributeByExprSupplier) {
            this.attributeByExprSupplier = attributeByExprSupplier;
            return this;
        }

        public QueryBuilderHelperBuilder withJoinIdByExprSupplier(Function<String, String> joinIdByExprSupplier) {
            this.joinIdByExprSupplier = joinIdByExprSupplier;
            return this;
        }

        public QueryBuilderHelperBuilder withAddFromToIdentifiers(Boolean addFromToIdentifiers) {
            this.addFromToIdentifiers = addFromToIdentifiers;
            return this;
        }

        public QueryBuilderHelperBuilder withFromAlias(String fromAlias) {
            this.fromAlias = fromAlias;
            return this;
        }

        public QueryBuilderHelperBuilder withFrom(EntryType from) {
            this.from = from;
            return this;
        }

        public QueryBuilderHelperBuilder withAliasBuilder(AliasBuilder aliasBuilder) {
            this.aliasBuilder = aliasBuilder;
            return this;
        }

        public QueryBuilderHelperBuilder withJoinElementAliasSupplier(Function<String, String> joinElementAliasSupplier) {
            this.joinElementAliasSupplier = joinElementAliasSupplier;
            return this;
        }

        public QueryBuilderHelperBuilder withBuilderOptions(QueryBuilderOptionsCommons builderOptions) {
            this.builderOptions = builderOptions;
            return this;
        }

        @Override
        public QueryBuilderHelper build() {
            return new QueryBuilderHelperImpl(this);
        }

    }

    private static String attributeExprOrName(Attribute attribute) {
        return firstNotBlank(attribute.getMetadata().get(ATTRIBUTE_EXPR), attribute.getName());
    }

    private static RelationDirection toRelationDirection(RelationFilterDirection d) {
        return switch (d) {
            case _1 ->
                RD_DIRECT;
            case _2 ->
                RD_INVERSE;
            default ->
                throw new UnsupportedOperationException();
        };
    }

}
