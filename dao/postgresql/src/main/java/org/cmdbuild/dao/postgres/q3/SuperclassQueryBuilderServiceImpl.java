/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import static java.lang.String.format;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS;
import static org.cmdbuild.dao.core.q3.DaoService.COUNT;
import static org.cmdbuild.dao.core.q3.DaoService.ROW_NUMBER;
import org.cmdbuild.dao.core.q3.PreparedQuery;
import org.cmdbuild.dao.core.q3.QueryBuilderOptions;
import static org.cmdbuild.dao.core.q3.QueryBuilderOptions.QO_NO_ALIAS_MAPPING;
import org.cmdbuild.dao.core.q3.ResultRow;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.driver.repository.ClasseReadonlyRepository;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.graph.ClasseHierarchyService;
import org.cmdbuild.dao.orm.CardMapperService;
import org.cmdbuild.dao.postgres.q3.SuperclassQueryBuilderService.SuperclassSubclassQuery;
import org.cmdbuild.dao.postgres.q3.beans.AbstractResultRow;
import org.cmdbuild.dao.postgres.q3.beans.PreparedQueryExt;
import org.cmdbuild.dao.postgres.q3.beans.SelectArg;
import org.cmdbuild.dao.postgres.q3.beans.SelectElement;
import org.cmdbuild.dao.postgres.q3.beans.SelectHolder;
import static org.cmdbuild.dao.postgres.q3.beans.SelectType.ST_FILTER;
import org.cmdbuild.dao.postgres.q3.beans.SuperclassSubclassQueryImpl;
import org.cmdbuild.dao.postgres.q3.beans.WhereArg;
import org.cmdbuild.dao.postgres.services.QueryService;
import static org.cmdbuild.dao.postgres.utils.QueryBuilderUtils.buildOffsetLimitExprOrBlank;
import static org.cmdbuild.dao.postgres.utils.QueryBuilderUtils.getReservedAttrs;
import org.cmdbuild.dao.postgres.utils.SqlQueryUtils;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildCodeAttrName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildDescAttrName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildInfoAttrName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildTypeAttrName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.parseEntryTypeQueryResponseData;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.beans.AttributeFilterConditionImpl;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.alwaysTrue;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SuperclassQueryBuilderServiceImpl implements SuperclassQueryBuilderService {

    private final static String IDCLASS_DESCRIPTION_TRANSLATION = "_IdClass_dt";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ClasseReadonlyRepository classeRepository;
    private final QueryService queryService;
    private final CardMapperService mapper;
    private final QueryBuilderUtilsService utils;
    private final QueryBuilderInnerService helperService;
    private final ClasseHierarchyService classHierarchyService;

    public SuperclassQueryBuilderServiceImpl(
            ClasseReadonlyRepository classeRepository,
            QueryService queryService,
            CardMapperService mapper,
            QueryBuilderUtilsService utils,
            QueryBuilderInnerService helperService,
            ClasseHierarchyService classHierarchyService) {
        this.classeRepository = checkNotNull(classeRepository);
        this.queryService = checkNotNull(queryService);
        this.mapper = checkNotNull(mapper);
        this.utils = checkNotNull(utils);
        this.helperService = checkNotNull(helperService);
        this.classHierarchyService = checkNotNull(classHierarchyService);
    }

    @Override
    public PreparedQuery buildQuery(SuperclassQuery query) {
        return new SuperclassQueryHelper(query).buildQuery();
    }

    private class SuperclassQueryHelper {

        private final AliasBuilder aliasBuilder = new AliasBuilder();

        private final SuperclassQuery options;

        private int subclassQueryIndexSequence = 0;

        public SuperclassQueryHelper(SuperclassQuery query) {
            this.options = checkNotNull(query);
        }

        public PreparedQuery buildQuery() {
            Classe superclass = options.getSuperclass();
            DaoQueryOptions queryOptions = options.getOptions();
            String superclassAlias = aliasBuilder.buildAlias(superclass.getName());
            List<SuperclassSubclassQuery> subclassQueries = list();

            SelectHolder select = new SelectHolder();

            QueryBuilderHelper helper = utils.helper()
                    .withAddFromToIdentifiers(false)
                    .withAliasBuilder(aliasBuilder)
                    .withFrom(superclass)
                    .withFromAlias(superclassAlias)
                    .withBuilderOptions(options)
                    .build();

            CmdbSorter sorter = helper.fixAliasesInSorter(queryOptions.getSorter()).mapAttributeNames(map(ATTR_IDCLASS, IDCLASS_DESCRIPTION_TRANSLATION));
            List<SelectArg> selectArgs = list();

            Collection<String> reservedAttrs = getReservedAttrs(superclass);
            reservedAttrs.stream().map(a -> SelectArg.build(a, quoteSqlIdentifier(a))).forEach(selectArgs::add);
            superclass.getCoreAttributes().stream()
                    .filter(Attribute::isActive)
                    .filter(not(Attribute::isVirtual))
                    .filter(queryOptions.getOnlyGridAttrs() ? not(Attribute::isHiddenInGrid) : alwaysTrue())
                    .map(Attribute::getName)
                    .filter(not(reservedAttrs::contains))
                    .filter(a -> (!queryOptions.hasAttrs() && !options.isCount()) || queryOptions.getAttrs().contains(a) || sorter.hasAttr(a))//TODO fix this, also add attrs from WHERE (?)
                    .sorted(Comparator.naturalOrder()).map(a -> SelectArg.build(a, quoteSqlIdentifier(a))).forEach(selectArgs::add);

//            SelectArg typeSelectArg = SelectArg.builder().withName(IDCLASS_DESCRIPTION_TRANSLATION).withAlias(aliasBuilder.buildAlias(IDCLASS_DESCRIPTION_TRANSLATION)).withExpr("_cm3_class_description_get(\"IdClass\")").enableSmartAliasProcessing(true).build();
//            selectArgs.add(typeSelectArg);
            List<SelectArg> customSelectArgs = options.getCustomSelectArgs().stream().map(s -> SelectArg.copyOf(s).withAlias(s.hasAlias() ? s.getAlias() : aliasBuilder.buildAlias(s.getName())).build()).collect(toList());
            if (sorter.hasAttr(IDCLASS_DESCRIPTION_TRANSLATION)) {
                customSelectArgs.add(SelectArg.builder().withName(IDCLASS_DESCRIPTION_TRANSLATION).withAlias(aliasBuilder.buildAlias(IDCLASS_DESCRIPTION_TRANSLATION)).withExpr("_cm3_translation_class_get(\"IdClass\")").enableSmartAliasProcessing(true).build());
            }
            selectArgs.addAll(customSelectArgs);

            selectArgs.stream().map(helper::processSelectArg).forEach(select::add);

            List<SelectArg> subclassSelectArgs = select.stream().filter(e -> options.getSuperclass().hasAttribute(e.getName())).map(SelectElement::toSelectArg).collect(toList());
            subclassSelectArgs.addAll(customSelectArgs);
            if (options.processAllSubclasses() && options.getSubclassQueries().isEmpty()) {
                classHierarchyService.getClasseHierarchy(superclass).getDescendants().forEach(c -> subclassQueries.add(new SuperclassSubclassQueryImpl(c, queryOptions.getFilter(), emptyList())));
            } else {
                subclassQueries.addAll(options.getSubclassQueries());
            }

            List<SubclassQueryElementWithMetadata<SuperclassSubclassQuery>> preprocessedSubclassQueries = listOf(SuperclassSubclassQuery.class).accept(l -> {
                Collection<String> subclassesWithoutFilter = subclassQueries.stream()
                        .filter(not(SuperclassSubclassQuery::hasFiltersOrWhereArgs))
                        .map(SuperclassSubclassQuery::getSubclass)
                        .map(Classe::getName).collect(toSet());
//                boolean useSubclassAggregateQuery = !subclassesWithoutFilter.isEmpty() && !sorter.hasAttr(IDCLASS_DESCRIPTION_TRANSLATION);
                boolean useSubclassAggregateQuery = !subclassesWithoutFilter.isEmpty();
                if (useSubclassAggregateQuery) {
                    l.add(new SuperclassSubclassQueryImpl(superclass, AttributeFilterConditionImpl.in(ATTR_IDCLASS, subclassesWithoutFilter).toAttributeFilter().toCmdbFilters(), emptyList(), options.getWhereArgs()));
                    subclassQueries.stream().filter(SuperclassSubclassQuery::hasFiltersOrWhereArgs).forEach(l::add);
                } else {
                    l.addAll(subclassQueries);
                }
            }).stream().map(s -> {
                List<SelectArg> extraSelectArgs = s.getMatchFilters().stream().map(m -> SelectArg.builder().withName(m.getName()).withFilter(m.getFilter()).withType(ST_FILTER).withAlias(aliasBuilder.buildAlias(m.getName())).build()).collect(toUnmodifiableList());
                logger.debug("extra select args = {}", extraSelectArgs);
                return new SubclassQueryElementWithMetadata<>(subclassQueryIndexSequence++, s, extraSelectArgs);
            }).collect(toUnmodifiableList());

            List<SubclassQueryElementWithMetadata<PreparedQueryExt>> subqueries = preprocessedSubclassQueries.stream().map(p -> {
                logger.debug("prepare subquery = {} with extra select args = {}", p.getElement().getSubclass().getName(), p.getExtraSelectArgs());
//                return new SubclassQueryElementWithMetadata<>(p.getIndex(), buildSubquery(p.getElement(), list(subclassSelectArgs).with(p.getExtraSelectArgs()).with(SelectArg.copyOf(typeSelectArg).withExpr(subclassDescrExpr(p.getElement().getSubclass())).build())), p.getExtraSelectArgs());
//                return new SubclassQueryElementWithMetadata<>(p.getIndex(), buildSubquery(p.getElement(), list(subclassSelectArgs).with(p.getExtraSelectArgs()).with(typeSelectArg)), p.getExtraSelectArgs());
                return new SubclassQueryElementWithMetadata<>(p.getIndex(), buildSubquery(p.getElement(), list(subclassSelectArgs).with(p.getExtraSelectArgs())), p.getExtraSelectArgs());
            }).collect(toList());

            subqueries.removeIf(s -> s.getElement().hasFalseFilter());//TODO do not remove all elements!!
            if (subqueries.isEmpty()) {
                return () -> emptyList();
            }//TODO handle single subquery case (short circuit)

//            List<SelectArg> extraSelectArgs = list();
//            List<PreparedQueryExt> subqueries = options.getSubclassQueries().stream().map(s -> {
//                List<SelectArg> thisSubclassSelectArgs = list(subclassSelectArgs).accept(l -> {
//                    s.getMatchFilters().stream().map(m -> new SelectArg(m.getName(), m.getFilter()).witAlias(aliasBuilder.buildAlias(m.getName()))).forEach(sa -> {
//                        l.add(sa);
//                        extraSelectArgs.add(sa);
//                    });
//                });
//                return buildSubquery(s, thisSubclassSelectArgs);
//            }).collect(toImmutableList());
            //TODO improve this, avoid duplicate code etc
            if (options.enableRefLookupJoin()) {
                list(subclassSelectArgs).stream().filter(e -> options.getSuperclass().hasAttribute(e.getName())).forEach(s -> {
                    switch (options.getSuperclass().getAttribute(s.getName()).getType().getName()) {
                        case LOOKUP -> {
                            select.add(SelectElement.build(buildCodeAttrName(s.getName()), "_", aliasBuilder.buildAlias(buildCodeAttrName(s.getName()))));
                            select.add(SelectElement.build(buildDescAttrName(s.getName()), "_", aliasBuilder.buildAlias(buildDescAttrName(s.getName()))));
                        }
                        case REFERENCE, FOREIGNKEY -> {
                            select.add(SelectElement.build(buildCodeAttrName(s.getName()), "_", aliasBuilder.buildAlias(buildCodeAttrName(s.getName()))));
                            select.add(SelectElement.build(buildDescAttrName(s.getName()), "_", aliasBuilder.buildAlias(buildDescAttrName(s.getName()))));
                            select.add(SelectElement.build(buildTypeAttrName(s.getName()), "_", aliasBuilder.buildAlias(buildTypeAttrName(s.getName()))));
                        }
                        case LOOKUPARRAY ->
                            select.add(SelectElement.build(buildInfoAttrName(s.getName()), "_", aliasBuilder.buildAlias(buildInfoAttrName(s.getName()))));
                    }
                });
            }

            subqueries.stream().flatMap(sq -> sq.getExtraSelectArgs().stream()).forEach(sa -> {
                select.add(SelectElement.build(sa.getName(), "_", sa.getAlias()));
            });

//            subclassSelectArgs.add(typeSelectArg);
            String query = subqueries.stream().map(thisSq -> {
                PreparedQueryExt q = thisSq.getElement();
                String subquery = q.getQuery();
                if (subqueries.stream().anyMatch(s -> !s.getExtraSelectArgs().isEmpty()) || !Objects.equals(list(q.getSelect()).map(SelectElement::getAlias), list(subclassSelectArgs).map(SelectArg::getAlias))) {
                    List<String> selectExprs = subclassSelectArgs.stream().map(s -> {  //TODO improve this
                        String otherAlias = q.getSelectForAttr(s.getName()).getAlias();
                        if (Objects.equals(s.getAlias(), otherAlias)) {
                            return quoteSqlIdentifier(otherAlias);
                        } else {
                            return format("%s %s", quoteSqlIdentifier(otherAlias), quoteSqlIdentifier(s.getAlias()));
                        }
                    }).map(SqlQueryUtils::quoteSqlIdentifier).collect(toList());
                    subclassSelectArgs.stream().flatMap(a -> list(buildTypeAttrName(a.getName()), buildDescAttrName(a.getName()), buildCodeAttrName(a.getName()), buildInfoAttrName(a.getName())).stream()).map(q::getSelectForAttrOrNull).filter(Objects::nonNull).map(s -> {//TODO improve this
                        String otherAlias = select.getByName(s.getName()).getAlias();
                        if (Objects.equals(s.getAlias(), otherAlias)) {
                            return quoteSqlIdentifier(otherAlias);
                        } else {
                            return format("%s %s", quoteSqlIdentifier(s.getAlias()), quoteSqlIdentifier(otherAlias));
                        }
                    }).forEach(selectExprs::add);
                    subqueries.stream().forEach(anysq -> {
                        if (anysq.getIndex() == thisSq.getIndex()) {
                            anysq.getExtraSelectArgs().stream().map(SelectArg::getAlias).map(SqlQueryUtils::quoteSqlIdentifier).forEach(selectExprs::add);
                        } else {
                            anysq.getExtraSelectArgs().stream().map(sa -> format("NULL%s %s", sa.getName().startsWith("cm_filter_mark_") ? "::boolean" : "", quoteSqlIdentifier(sa.getAlias()))).forEach(selectExprs::add);//TODO improve type handling !!
                        }
                    });
                    subquery = format("SELECT %s FROM (%s) %s", selectExprs.stream().collect(joining(", ")), subquery, aliasBuilder.buildAlias("x"));
                }
                return subquery;
            }).collect(joining(" UNION ALL "));

            switch (options.getQueryMode()) {
                case QM_ROWNUMBER -> {
                    checkArgument(options.getOptions().hasPositionOf());
                    String rowNumberAlias = aliasBuilder.buildAlias(ROW_NUMBER),
                            attrIdAlias = select.getByName(ATTR_ID).getAlias();
                    query += helper.buildOrderByExprOrBlank(sorter, select);
                    query = format("SELECT * FROM (SELECT %s, ROW_NUMBER() OVER () AS %s FROM (%s) %s) %s WHERE %s = %s",
                            attrIdAlias, rowNumberAlias, query, aliasBuilder.buildAlias("rd_inner"), aliasBuilder.buildAlias("rd_outher"), attrIdAlias, options.getOptions().getPositionOf());
                    return new PreparedQueryImpl(query, singletonList(SelectElement.build(ROW_NUMBER, "_", rowNumberAlias)));
                }
                case QM_COUNT -> {
                    query = format("SELECT COUNT(*) _count FROM (%s) %s", query, aliasBuilder.buildAlias("x"));
                    return new PreparedQueryImpl(query, singletonList(SelectElement.build(COUNT, "_", "_count")));
                }
                case QM_SIMPLE -> {
                    query += helper.buildOrderByExprOrBlank(sorter, select);
                    query += buildOffsetLimitExprOrBlank(options.getOptions());
                    return new PreparedQueryImpl(query, select.getElements());
                }
                default ->
                    throw unsupported("unsupported query mode = %s", options.getQueryMode());
            }
        }

        private PreparedQueryExt buildSubquery(SuperclassSubclassQuery subclassOptions, List<SelectArg> selectArgs) {
            Classe subclass = subclassOptions.getSubclass();
            DaoQueryOptions queryOptions = DaoQueryOptionsImpl.builder().withFilter(options.getOptions().getFilter().and(subclassOptions.getFilter())).build();
            return helperService.buildPreparedQuery(new QueryBuilderParams() {
                @Override
                public EntryType getFrom() {
                    return subclass;
                }

                @Override
                public DaoQueryOptions getQueryOptions() {
                    return queryOptions;
                }

                @Override
                public List<SelectArg> getSelectArgs() {
                    return selectArgs;
                }

                @Override
                public boolean getJoinForRefCodeDescription() {
                    return true;
                }

                @Override
                public List<WhereArg> getWhere() {
                    return list(options.getWhereArgs()).with(subclassOptions.getWhereArgs());
                }

                @Override
                public Set<QueryBuilderOptions> getBuilderOptions() {
                    return set(options.getBuilderOptions()).without(QO_NO_ALIAS_MAPPING);
                }

            });
        }

        private class PreparedQueryImpl implements PreparedQueryExt {

            private final Classe superclass;
            private final String query;
            private final List<SelectElement> select;

            public PreparedQueryImpl(String preparedQuery, List<SelectElement> select) {
                this.query = checkNotBlank(preparedQuery);
                this.superclass = SuperclassQueryHelper.this.options.getSuperclass();
                this.select = List.copyOf(select);
            }

            @Override
            public List<ResultRow> run() {
                logger.trace("execute query:\n\tquery = {} ", query);
                try {
                    return queryService.query(query, rethrowFunction(rs -> {
                        Map<String, Object> data = parseEntryTypeQueryResponseData(superclass, select, SelectElement::getName, rethrowFunction(s -> rs.getObject(s.getAlias())));
                        String classId = toStringOrNull(data.get(ATTR_IDCLASS));
                        Classe classe = isBlank(classId) ? superclass : classeRepository.getClasse(classId);
                        return new ResultRowImpl(classe, data);
                    }));
                } catch (Exception ex) {
                    throw new DaoException(ex, "error executing query = '%s'", query);
                }
            }

            @Override
            public String getQuery() {
                return query;
            }

            @Override
            public List<SelectElement> getSelect() {
                return select;
            }
        }
    }

//    private  String subclassDescrExpr(Classe classe) {
//            return "_cm3_class_description_get(\"IdClass\")";
//            if(classe.isSuperclass()){
//            return "_cm3_utils_first_not_blank(_cm3_class_features_get(\"IdClass\")->>'DESCR',_cm3_utils_regclass_to_name(\"IdClass\"))";
//            return "_cm3_class_description_get(\"IdClass\")";
//            //TODO improve subclass list
//            return format("CASE \"IdClass\" %s ELSE ''::varchar END", hierarchyService.getClasseHierarchy(classe).getLeaves().stream().map(c->format("WHEN %s THEN %s::varchar",systemToSqlExpr(c), systemToSqlExpr(c.getDescription()))).collect(joining(" ")));
//        }else{
//            return format("%s::varchar", systemToSqlExpr(classe.getDescription()));
//        }
//    }
    private static class SubclassQueryElementWithMetadata<T> {

        private final int index;
        private final T element;
        private final List<SelectArg> extraSelectArgs;

        public SubclassQueryElementWithMetadata(int index, T element, List<SelectArg> extraSelectArgs) {
            this.index = index;
            this.element = checkNotNull(element);
            this.extraSelectArgs = List.copyOf(extraSelectArgs);
        }

        public int getIndex() {
            return index;
        }

        public T getElement() {
            return element;
        }

        public List<SelectArg> getExtraSelectArgs() {
            return extraSelectArgs;
        }

    }

    private class ResultRowImpl extends AbstractResultRow implements ResultRow {

        private final Classe classe;
        private final Map<String, Object> data;

        public ResultRowImpl(Classe classe, Map<String, Object> data) {
            this.classe = checkNotNull(classe);
            this.data = checkNotNull(data);
        }

        @Override
        public <T> T toModel(Class<T> type) {
            return (T) mapper.getMapperForModelOrBuilder(type).cardToObject(toCard());
        }

        @Override
        public Map<String, Object> asMap() {
            return data;
        }

        @Override
        public <T> T toModel() {
            return (T) mapper.cardToObject(toCard());
        }

        @Override
        public Card toCard() {
            return CardImpl.buildCard(classe, data);
        }

        @Override
        public CMRelation toRelation() {
            throw new UnsupportedOperationException();
        }
    }
}
