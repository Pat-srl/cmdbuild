/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.services;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import com.google.common.collect.Streams;
import static java.lang.String.format;
import java.util.Collections;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang3.tuple.Pair;
import static org.cmdbuild.cache.CacheConfig.DEFAULT;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.common.beans.LookupValue;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.common.beans.IdAndDescriptionImpl;
import org.cmdbuild.dao.beans.LookupValueImpl;
import org.cmdbuild.dao.core.q3.CachedPreparedQuery;
import static org.cmdbuild.dao.core.q3.CachedPreparedQuery.cached;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.core.q3.PreparedQuery;
import org.cmdbuild.dao.core.q3.ResultRow;
import org.cmdbuild.dao.driver.PostgresService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.orm.CardMapperService;
import org.cmdbuild.dao.postgres.q3.AliasBuilder;
import org.cmdbuild.dao.postgres.q3.beans.AbstractResultRow;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildCodeAttrName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildDescAttrName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildLookupCodeExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildLookupDescExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildReferenceCodeExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildReferenceDescExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildReferenceTypeExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildTypeAttrName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.sqlTableToClassNameOrNull;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import org.cmdbuild.dao.function.StoredFunctionService;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringInline;
import org.springframework.context.annotation.Primary;

@Component
@Primary
public class StoredFunctionServiceImpl implements StoredFunctionService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final PostgresService database;
    private final CardMapperService mapper;

    private final CmCache<CachedPreparedQuery> cache;

    public StoredFunctionServiceImpl(PostgresService database, CardMapperService mapper, CacheService cacheService) {
        this.database = checkNotNull(database);
        this.mapper = checkNotNull(mapper);
        this.cache = cacheService.newCache("dao_stored_function_response_records", DEFAULT);
    }

    @Override
    public List<StoredFunction> getAllFunctions() {
        return database.getAllFunctions();
    }

    @Override
    public StoredFunction getFunctionOrNull(String localname) {
        return database.getFunctionOrNull(localname);
    }

    @Override
    public Map<String, Object> callFunction(String functionId, Map<String, Object> params) {
        return callFunction(getFunctionByName(functionId), params);
    }

    @Override
    public Map<String, Object> callFunction(StoredFunction function, Map<String, Object> params) {
        logger.debug("call function = {} with params = {}", function, mapToLoggableStringInline(params));

        List<Object> input = function.getInputParameters().stream().map((p) -> rawToSystem(p.getType(), params.get(p.getName()))).collect(toList());

        List<ResultRow> result = selectFunction(function, input).run();

        Map<String, Object> res;

        if (result.isEmpty()) {
            res = emptyMap();
        } else {
            Map<String, Object> valueSet = getOnlyElement(result).asMap();
            res = function.getOutputParameters().stream().collect(toMap(Attribute::getName, (p) -> valueSet.get(p.getName())));
        }

        if (logger.isTraceEnabled()) {
            logger.trace("parsed function output = \n\n{}\n", mapToLoggableString(res));
        }

        return res;
    }

    @Override
    public PreparedQuery selectFunction(StoredFunction function, List<Object> input, List<Attribute> outputParamMapping) {
        FunctionPreparedQuery preparedQuery = prepareFunctionQuery(function, input, outputParamMapping);
        if (function.isCached()) {
            return cache.get(preparedQuery.query, () -> cached(preparedQuery));
        } else {
            return preparedQuery;
        }
    }

    private FunctionPreparedQuery prepareFunctionQuery(StoredFunction function, List<Object> input, List<Attribute> outputParamMapping) {
        logger.debug("call function = {} with args = {}", function, input);
        AliasBuilder aliasBuilder = new AliasBuilder();
        String functionAlias = aliasBuilder.buildAlias(function.getName());

        List<Pair<String, String>> outputExprAndAlias = list();
        List<Pair<String, Attribute>> outputAliasAndParam = list();
        checkArgument(outputParamMapping.size() == function.getOutputParameters().size());
        outputParamMapping.forEach((param) -> {
            String name = param.getName(),
                    expr = quoteSqlIdentifier(name),
                    alias = aliasBuilder.buildAlias(name);
            outputExprAndAlias.add(Pair.of(expr, alias));
            outputAliasAndParam.add(Pair.of(alias, param));
            CardAttributeType<?> type = param.getType();
            switch (type.getName()) {
                case LOOKUP:
                    outputExprAndAlias.add(Pair.of(buildLookupDescExpr(functionAlias, expr), buildDescAttrName(alias)));
                    outputExprAndAlias.add(Pair.of(buildLookupCodeExpr(functionAlias, expr), buildCodeAttrName(alias)));
                    break;
                case FOREIGNKEY:
                    Classe targetClass = database.getClasse(((ForeignKeyAttributeType) type).getForeignKeyDestinationClassName());
                    outputExprAndAlias.add(Pair.of(buildReferenceTypeExpr(targetClass, functionAlias, expr), buildTypeAttrName(alias)));
                    outputExprAndAlias.add(Pair.of(buildReferenceDescExpr(targetClass, functionAlias, expr), buildDescAttrName(alias)));
                    outputExprAndAlias.add(Pair.of(buildReferenceCodeExpr(targetClass, functionAlias, expr), buildCodeAttrName(alias)));
            }
        });

        checkArgument(input.size() == function.getInputParameters().size());
        List<Object> functionArgs = Streams.zip(function.getInputParameters().stream().map(Attribute::getType), input.stream(), (type, value) -> {
            value = rawToSystem(type, value);
            value = systemToSqlExpr(value, type);
            return value;
        }).collect(toList());

        String attrQuery = outputExprAndAlias.stream().map((p) -> format("%s AS %s", p.getLeft(), p.getRight())).collect(joining(", "));

        String query = format("SELECT %s FROM %s(%s) %s", attrQuery, quoteSqlIdentifier(function.getName()), Joiner.on(", ").join(functionArgs), functionAlias);

        return new FunctionPreparedQuery(query, outputAliasAndParam);
    }

    private class FunctionPreparedQuery implements PreparedQuery {

        private final String query;
        private final List<Pair<String, Attribute>> outputParametersWithAlias;

        public FunctionPreparedQuery(String query, List<Pair<String, Attribute>> outputParametersWithAlias) {
            this.query = checkNotBlank(query);
            this.outputParametersWithAlias = ImmutableList.copyOf(outputParametersWithAlias);
        }

        @Override
        public List<ResultRow> run() {
            return database.getJdbcTemplate().query(query, (rs, i) -> {
                Map<String, Object> map = map();
                for (Pair<String, Attribute> param : outputParametersWithAlias) {
                    String alias = param.getLeft();
                    Object value = rs.getObject(alias);
                    String name = param.getRight().getName();
                    CardAttributeType<?> attributeType = param.getRight().getType();
                    value = rawToSystem(attributeType, value);

                    switch (attributeType.getName()) {//TODO duplicate processing code, refactor and unify
                        case LOOKUP:
                            LookupValue lookupValue = (LookupValue) value;
                            if (lookupValue != null) {
                                String desc = rs.getString(buildDescAttrName(alias));
                                String code = rs.getString(buildCodeAttrName(alias));
                                value = LookupValueImpl.copyOf(lookupValue).withCode(code).withDescription(desc).build();
                            }
                            break;
                        case FOREIGNKEY:
                            IdAndDescription idAndDescription = (IdAndDescription) value;
                            if (idAndDescription != null) {
                                value = new IdAndDescriptionImpl(sqlTableToClassNameOrNull(rs.getString(buildTypeAttrName(alias))), idAndDescription.getId(), rs.getString(buildDescAttrName(alias)), rs.getString(buildCodeAttrName(alias)));
                            }
                    }

                    map.put(name, value);
                }
                logger.trace("received function query response = \n\n{}\n", mapToLoggableStringLazy(map));
                return new FunctionResultRow(map);
            });
        }

        private class FunctionResultRow extends AbstractResultRow {

            private final Map<String, Object> map;

            public FunctionResultRow(Map<String, Object> map) {
                this.map = Collections.unmodifiableMap(checkNotNull(map));
            }

            @Override
            public <T> T toModel(Class<T> type) {
                return (T) mapper.getMapperForModelOrBuilder(type).dataToObject(asMap()::get);
            }

            @Override
            public Map<String, Object> asMap() {
                return map;
            }

            @Override
            public <T> T toModel() {
                throw new UnsupportedOperationException("function result cannot be mapped to model without explicit model class param provided");
            }

            @Override
            public Card toCard() {
                throw new UnsupportedOperationException("function result cannot be mapped to card");
            }

            @Override
            public CMRelation toRelation() {
                throw new UnsupportedOperationException("function result cannot be mapped to relation");
            }

        }

    }
}
