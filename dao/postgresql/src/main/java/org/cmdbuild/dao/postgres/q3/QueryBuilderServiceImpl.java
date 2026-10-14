/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import org.cmdbuild.dao.postgres.q3.beans.JoinQueryArgs;
import org.cmdbuild.dao.postgres.q3.beans.InnerPreparedQuery;
import org.cmdbuild.dao.postgres.q3.beans.WhereArg;
import org.cmdbuild.dao.postgres.q3.beans.SelectArg;
import org.cmdbuild.dao.core.q3.WhereOperator;
import org.cmdbuild.dao.core.q3.ResultRow;
import org.cmdbuild.dao.core.q3.PreparedQuery;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import org.cmdbuild.dao.core.q3.QueryBuilderService;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.core.q3.CommonQueryBuilderMethods;
import org.cmdbuild.dao.core.q3.CompositeWhereHelper;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.orm.CardMapper;
import org.cmdbuild.dao.orm.CardMapperService;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import static org.cmdbuild.dao.utils.CmSorterUtils.noopSorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import org.cmdbuild.dao.core.q3.CompositeWhereOperator;
import org.cmdbuild.dao.core.q3.JoinQueryBuilder;
import org.cmdbuild.dao.core.q3.QueryBuilderOptions;
import org.cmdbuild.dao.core.q3.RowNumberQueryBuilder;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.driver.repository.ClasseReadonlyRepository;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.function.StoredFunction;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.dao.postgres.q3.beans.WhereArgType.WA_EXPR;
import static org.cmdbuild.dao.postgres.q3.beans.WhereTarget.WT_JOINON;
import static org.cmdbuild.dao.postgres.q3.beans.WhereTarget.WT_ROWNUMBER;
import static org.cmdbuild.dao.postgres.utils.QueryBuilderUtils.buildSelectArgForExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.exprContainsQ3Markers;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.wrapExprWithBrackets;
import static org.cmdbuild.data.filter.beans.CmdbFilterImpl.noopFilter;
import static org.cmdbuild.utils.lang.CmMapUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.cmdbuild.dao.driver.repository.StoredFunctionRepository;

@Component
public class QueryBuilderServiceImpl implements QueryBuilderService {

    public final static String JOIN_ID_DEFAULT = "DEFAULT";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ClasseReadonlyRepository classeRepository;
    private final StoredFunctionRepository functionRepository;
    private final DomainRepository domainRepository;
    private final CardMapperService mapper;
    private final QueryBuilderInnerService processorService;

    public QueryBuilderServiceImpl(ClasseReadonlyRepository classeRepository, StoredFunctionRepository functionRepository, DomainRepository domainRepository, CardMapperService mapper, QueryBuilderInnerService processorService) {
        this.classeRepository = checkNotNull(classeRepository);
        this.functionRepository = checkNotNull(functionRepository);
        this.domainRepository = checkNotNull(domainRepository);
        this.mapper = checkNotNull(mapper);
        this.processorService = checkNotNull(processorService);
    }

    @Override
    public QueryBuilder query() {
        return new QueryBuilderImpl();
    }

    protected abstract class QueryBuilderCommons<T extends CommonQueryBuilderMethods> implements CommonQueryBuilderMethods<T> {

        protected final List<SelectArg> select = list();
        protected final List<WhereArg> where = list();
        protected final List<CmdbFilter> filters = list();

        @Override
        public T select(Collection<String> attrs) {
            attrs.stream().map((a) -> SelectArg.build(a, quoteSqlIdentifier(a))).forEach(this::addSelect);
            return (T) this;
        }

        @Override
        public T selectExpr(String name, String expr) {
            addSelect(buildSelectArgForExpr(name, expr));
            return (T) this;
        }

        @Override
        public T selectMatchFilter(String name, CmdbFilter filter) {
            addSelect(SelectArg.build(name, filter));
            return (T) this;
        }

        @Override
        public T where(String attr, WhereOperator operator, Object... params) {
            where.add(WhereArg.build(attr, operator, params));
            return (T) this;
        }

        @Override
        public T where(CompositeWhereOperator operator, Consumer<CompositeWhereHelper> consumer) {
            CompositeWhereHelperImpl helper = new CompositeWhereHelperImpl();
            consumer.accept(helper);
            where.add(WhereArg.build(operator, helper.inners));
            return (T) this;
        }

        @Override
        public T where(CmdbFilter filter) {
            filters.add(checkNotNull(filter));
            return (T) this;
        }

        @Override
        public T where(PreparedQuery query) {
            InnerPreparedQuery src = (InnerPreparedQuery) query;
//            src.getFilters().forEach((w) -> where.add(new WhereArg(w.getExpr(), SpecialOperators.EXPR, w.getMeta(), emptyList())));//TODO improve this
//            src.getFilters().forEach((w) -> where.add(new WhereArg(w.getExpr(), SpecialOperators.EXPR, emptyMap(), emptyList())));//TODO improve this; copy meta
            src.getFilters().forEach((w) -> where.add(WhereArg.build(w.getExpr())));//TODO improve this; copy meta
            return (T) this;
        }

        @Override
        public T whereExpr(String expr, Object... params) {
            return whereExpr(expr, list(params));
        }

        @Override
        public T whereExpr(String expr, Collection params) {
            where.add(WhereArg.builder().withType(WA_EXPR).withExpr(wrapExprWithBrackets(expr)).accept(b -> {
                if (exprContainsQ3Markers(expr)) {//TODO duplicate code from selectExpr, improve this
                    b.enableExprMarkerProcessing(true);
                } else {
                    b.enableSmartAliasProcessing(true);
                }
            }).withParams(CmCollectionUtils.toList(params)).build());
            return (T) this;
        }

        protected void addSelect(SelectArg selectArg) {
            logger.trace("add select = {}", selectArg);
            select.add(selectArg);
        }

    }

    protected class QueryBuilderImpl extends QueryBuilderCommons<QueryBuilder> implements QueryBuilder, QueryBuilderParams {

        protected EntryType from;
        protected Long offset, limit;
        protected CmdbSorter sorter = noopSorter();
        protected boolean selectRowNumber = false, activeCardsOnly = true, count = false, bulkDelete = false, bulkUpdate = false;
        protected CardMapper cardMapper;
        protected final List<JoinQueryArgs> joinArgs = list();
        protected Map<String, Object> values;
        private final Set<QueryBuilderOptions> builderOptions = EnumSet.noneOf(QueryBuilderOptions.class);

        @Override
        public QueryBuilder selectCount() {
            count = true;
            return this;
        }

        @Override
        public QueryBuilder delete() {
            bulkDelete = true;
            return this;
        }

        @Override
        public QueryBuilder update(Map<String, Object> values) {
            this.values = checkNotNull(values);
            bulkUpdate = true;
            return this;
        }

        @Override
        public RowNumberQueryBuilder selectRowNumber() {
            selectRowNumber = true;
            return new RowNumberQueryBuilderImpl();
        }

        @Override
        public QueryBuilder groupBy(String attr) {
            addSelect(SelectArg.builder(attr, quoteSqlIdentifier(attr)).withGroupBy(true).build());
            return this;
        }

        @Override
        public QueryBuilder groupByExpr(String name, String expr) {
            addSelect(SelectArg.copyOf(buildSelectArgForExpr(name, expr)).withGroupBy(true).build());
            return this;
        }

        @Override
        public QueryBuilder from(Class model) {
            cardMapper = mapper.getMapperForModel(model);
            return from(cardMapper.getClassId());
        }

        @Override
        public QueryBuilder from(String classe) {
            return from(classeRepository.getClasse(classe));
        }

        @Override
        public QueryBuilder fromDomain(String domain) {
            return from(domainRepository.getDomain(domain));
        }

        @Override
        public QueryBuilder fromFunction(String function) {
            return from(functionRepository.getFunctionByName(function));
        }

        @Override
        public QueryBuilder from(Classe classe) {
            return doFrom(classe);
        }

        @Override
        public QueryBuilder from(StoredFunction function) {
            return doFrom(function);
        }

        @Override
        public QueryBuilder from(Domain domain) {
            return doFrom(domain);
        }

        @Override
        public QueryBuilder includeHistory() {
            activeCardsOnly = false;
            return this;
        }

        @Override
        public QueryBuilder orderBy(CmdbSorter sort) {
            sorter = checkNotNull(sort);
            return this;
        }

        @Override
        public QueryBuilder offset(@Nullable Long offset) {
            this.offset = offset;
            return this;
        }

        @Override
        public QueryBuilder limit(@Nullable Long limit) {
            this.limit = limit;
            return this;
        }

        @Override
        public PreparedQuery build() {
            return processorService.buildPreparedQuery(this);
        }

        @Override
        public List<ResultRow> run() {
            return build().run();
        }

        private QueryBuilder doFrom(EntryType entryType) {
            checkNotNull(entryType);
            checkArgument(entryType.isClasse() || entryType.isDomain() || entryType.isFunction(), "invalid entry type for query = %s", entryType);
            from = entryType;
            return this;
        }

        @Override
        public JoinQueryBuilder join(EntryType entryType) {
            JoinQueryArgs join = new JoinQueryArgsImpl(this, JOIN_ID_DEFAULT, entryType);
            joinArgs.add(join);
            return join;
        }

        @Override
        public JoinQueryBuilder join(String classId) {
            return join(classeRepository.getClasse(classId));
        }

        @Override
        public JoinQueryBuilder joinDomain(String domainId) {
            return join(domainRepository.getDomain(domainId));
        }

        private class RowNumberQueryBuilderImpl implements RowNumberQueryBuilder {

            @Override
            public RowNumberQueryBuilder where(String attr, WhereOperator operator, Object... params) {
                where.add(WhereArg.builder(attr, operator, params).withTarget(WT_ROWNUMBER).build());
                return this;
            }

            @Override
            public QueryBuilder then() {
                return QueryBuilderImpl.this;
            }

        }

        @Override
        public EntryType getFrom() {
            return from;
        }

        @Override
        public DaoQueryOptions getQueryOptions() {
            return DaoQueryOptionsImpl.builder()
                    .withPaging(offset, limit)
                    .withSorter(sorter)
                    .withFilter(noopFilter().and(filters))
                    .build();
        }

        @Override
        public boolean getActiveCardsOnly() {
            return activeCardsOnly;
        }

        @Override
        @Nullable
        public CardMapper getCardMapper() {
            return cardMapper;
        }

        @Override
        public List<SelectArg> getSelectArgs() {
            return select;
        }

        @Override
        public boolean getSelectCount() {
            return count;
        }

        @Override
        public boolean getSelectRowNumber() {
            return selectRowNumber;
        }

        @Override
        public List<JoinQueryArgs> getJoinArgs() {
            return joinArgs;
        }

        @Override
        public List<WhereArg> getWhere() {
            return where;
        }

        @Override
        public boolean isDelete() {
            return bulkDelete;
        }

        @Override
        public boolean isUpdate() {
            return bulkUpdate;
        }

        @Override
        public Map<String, Object> getValues() {
            return nullToEmpty(values);
        }

        @Override
        public Set<QueryBuilderOptions> getBuilderOptions() {
            return builderOptions;
        }

    }

    protected class JoinQueryArgsImpl extends QueryBuilderCommons<JoinQueryBuilder> implements JoinQueryArgs {

        protected final String joinId = randomId();
        protected final List<WhereArg> onExprs = list();
        protected final EntryType from;
        protected final QueryBuilderImpl root;
        protected final String parentId;

        public JoinQueryArgsImpl(QueryBuilderImpl root, String parentId, EntryType from) {
            this.root = checkNotNull(root);
            this.parentId = checkNotBlank(parentId);
            this.from = checkNotNull(from);
        }

        @Override
        public JoinQueryBuilder on(String attr1, WhereOperator operator, String attr2) {
            onExprs.add(WhereArg.builder(attr1, operator, quoteSqlIdentifier(attr2)).withJoinTo(joinId).withTarget(WT_JOINON).build());
            return this;
        }

        @Override
        public JoinQueryBuilder join(EntryType entryType) {
            JoinQueryArgs join = new JoinQueryArgsImpl(root, joinId, entryType);
            root.joinArgs.add(join);
            return join;
        }

        @Override
        public JoinQueryBuilder join(String classId) {
            return join(classeRepository.getClasse(classId));
        }

        @Override
        public JoinQueryBuilder joinDomain(String domainId) {
            return join(domainRepository.getDomain(domainId));
        }

        @Override
        public QueryBuilder then() {
            return root;
        }

        @Override
        public List<ResultRow> run() {
            return then().build().run();
        }

        @Override
        public String getJoinId() {
            return joinId;
        }

        @Override
        public EntryType getFrom() {
            return from;
        }

        @Override
        public List<SelectArg> getSelect() {
            return select;
        }

        @Override
        public List<WhereArg> getOnExprs() {
            return onExprs;
        }

        @Override
        public List<WhereArg> getWhere() {
            return where;
        }

        @Override
        public List<CmdbFilter> getFilters() {
            return filters;
        }

    }

    public static class CompositeWhereHelperImpl implements CompositeWhereHelper {

        public final List<WhereArg> inners = list();

        @Override
        public CompositeWhereHelper where(String attr, WhereOperator operator, Object... params) {
            inners.add(WhereArg.build(attr, operator, params));//TODO duplicate code, merge with main
            return this;
        }
    }
}
