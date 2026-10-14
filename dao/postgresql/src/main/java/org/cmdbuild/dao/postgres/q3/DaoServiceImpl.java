/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.sql.ResultSet;
import java.sql.Timestamp;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.common.beans.CardIdAndClassName;
import org.cmdbuild.common.beans.CardIdAndClassNameImpl;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.beans.CMRelation;
import static org.cmdbuild.dao.beans.CMRelation.ATTR_CODE2;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.beans.RelationImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ENDDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDDOMAIN;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS_A;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_USER;
import static org.cmdbuild.dao.constants.SystemAttributes.DOMAIN_RESERVED_ATTRIBUTES;
import static org.cmdbuild.dao.constants.SystemAttributes.STANDARD_CLASS_INFO_ATTRIBUTES;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.PreparedQuery;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.core.q3.QueryBuilderService;
import org.cmdbuild.dao.core.q3.SuperclassQueryService;
import org.cmdbuild.dao.driver.PostgresService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.ClassDefinition;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainDefinition;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FOREIGNKEY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUP;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.function.StoredFunctionService;
import org.cmdbuild.dao.graph.ClasseHierarchy;
import org.cmdbuild.dao.graph.ClasseHierarchyService;
import org.cmdbuild.dao.orm.CardMapperService;
import org.cmdbuild.dao.postgres.utils.RelationDirectionQueryHelper;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.attributeTypeToSqlCast;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildCodeAttrName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildDescAttrName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildInfoAttrName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildLookupCodeExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildLookupDescExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildLookupInfoExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildReferenceCodeExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildReferenceDescExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.classNameToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.parseEntryTypeQueryResponseData;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.sqlTableToClassName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.sqlTableToDomainName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import org.cmdbuild.dao.user.UserDaoHelperService;
import org.cmdbuild.dao.user.UserDaoService;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DaoServiceImpl implements DaoService, UserDaoService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final PostgresService database;
    private final StoredFunctionService storedFunctionService;
    private final CardMapperService mapper;
    private final QueryBuilderService queryBuilderService;
    private final UserDaoHelperService userDaoHelper;
    private final ClasseHierarchyService hierarchyService;
    private final SuperclassQueryService superclassQueryService;

    public DaoServiceImpl(StoredFunctionService functionCallService, PostgresService database, CardMapperService mapper, QueryBuilderService queryBuilderService, UserDaoHelperService userDaoHelper, ClasseHierarchyService hierarchyService, SuperclassQueryService superclassQueryService) {
        this.database = checkNotNull(database);
        this.mapper = checkNotNull(mapper);
        this.queryBuilderService = checkNotNull(queryBuilderService);
        this.userDaoHelper = checkNotNull(userDaoHelper);
        this.hierarchyService = checkNotNull(hierarchyService);
        this.superclassQueryService = checkNotNull(superclassQueryService);
        this.storedFunctionService = checkNotNull(functionCallService);
    }

    @Override
    public SuperclassQueryBuilderHelper queryFromSuperclass(Classe classe) {
        return superclassQueryService.queryFromSuperclass(classe);
    }

    @Override
    public SuperclassQueryBuilderHelper queryFromSuperclass(String classId) {
        return superclassQueryService.queryFromSuperclass(classId);
    }

    @Override
    public ClasseHierarchy getClasseHierarchy(String classe) {
        return hierarchyService.getClasseHierarchy(classe);
    }

    @Override
    public UserDaoHelperService getUserDaoHelper() {
        return userDaoHelper;
    }

    @Override
    public List<StoredFunction> getAllFunctions() {
        return database.getAllFunctions();
    }

    @Override
    public StoredFunction getFunctionOrNull(String name) {
        return database.getFunctionOrNull(name);
    }

    @Override
    public QueryBuilder query() {
        return queryBuilderService.query();
    }

    @Override
    public <T> T create(T model) {
        Card card = mapper.objectToCard(model);
        card = create(card);
        return mapper.cardToObject(card);
    }

    @Override
    public <T> long createOnly(T model) {
        Card card = mapper.objectToCard(model);
        return createOnly(card);
    }

    @Override
    public long createOnly(DatabaseRecord card) {
        return database.create(card);
    }

    @Override
    public List<Long> createBatch(List<Card> cards) {
        return database.createBatch((List<DatabaseRecord>) (List) cards);
    }

    @Override
    public <T> T update(T model) {
        Card card = mapper.objectToCard(model);
        card = update(card);
        return mapper.cardToObject(card);
    }

    @Override
    public <T> void updateOnly(T model) {
        Card card = mapper.objectToCard(model);
        updateOnly(card);
    }

    @Override
    public void updateOnly(Card card) {
        database.update(card);
    }

    @Override
    public void delete(Class model, long cardId) {
        delete(mapper.getClasseForModelOrBuilder(model), cardId);
    }

    @Override
    public void definitiveDelete(Class model, long cardId) {
        String query = format("DELETE FROM \"%s\" WHERE \"Id\" = %s", mapper.getClasseForModelOrBuilder(model).getName(), cardId);
        database.getJdbcTemplate().execute(query);
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return database.getJdbcTemplate();
    }

    @Override
    public Classe createClass(ClassDefinition definition) {
        return database.createClass(definition);
    }

    @Override
    public Classe updateClass(ClassDefinition definition) {
        return database.updateClass(definition);
    }

    @Override
    public ClassDefinition retrieveClassOid(ClassDefinition definition) {
        return database.retrieveClassOid(definition);
    }

    @Override
    public void deleteClass(Classe dbClass) {
        database.deleteClass(dbClass);
    }

    @Override
    public Card create(Card card) {
        long id = database.create(card);
        return getCard(card.getType(), id, firstNotBlank(card.getString(ATTR_STATUS), ATTR_STATUS_A));
    }

    @Override
    public Card update(Card card) {
        database.update(card);
        return getCard(card.getType(), card.getId());
    }

    @Override
    public List<Classe> getAllClasses() {
        return database.getAllClasses();
    }

    @Override
    public Classe getClasseOrNull(long oid) {
        return database.getClasseOrNull(oid);
    }

    @Override
    public Classe getClasseOrNull(String name) {
        return database.getClasseOrNull(name);
    }

    @Override
    public Domain getDomain(String domainId) {
        return database.getDomain(domainId);
    }

    @Override
    public Domain getDomainOrNull(Long id) {
        return database.getDomainOrNull(id);
    }

    @Override
    public Domain getDomainOrNull(String localname) {
        return database.getDomainOrNull(localname);
    }

    @Override
    public List<Domain> getAllDomains() {
        return database.getAllDomains();
    }

    @Override
    public Domain createDomain(DomainDefinition definition) {
        return database.createDomain(definition);
    }

    @Override
    public Domain updateDomain(DomainDefinition definition) {
        return database.updateDomain(definition);
    }

    @Override
    public void deleteDomain(Domain domain) {
        database.deleteDomain(domain);
    }

    @Override
    public List<Domain> getDomainsForClasse(Classe classe) {
        return database.getDomainsForClasse(classe);
    }

    @Override
    public void delete(DatabaseRecord card) {
        database.delete(card);
    }

    @Override
    public List<Card> deleteCards(Classe classe, CmdbFilter filter) {
        //TODO predelete, postdelete events (??)
        return queryBuilderService.delete().from(classe).where(filter).getCards();
    }

    @Override
    public List<Card> updateCards(Classe classe, CmdbFilter filter, Map<String, Object> values) {
        //TODO preupdate, postupdate events (??)
        return queryBuilderService.update(values).from(classe).where(filter).getCards();
    }

    @Override
    public void delete(Object model) {
        checkNotNull(model);
        try {
            long id = mapper.getCardId(model);
            delete(model.getClass(), id);
        } catch (Exception ex) {
            throw new DaoException(ex, "error deleting card for bean = %s", model);
        }
    }

    @Override
    public Attribute createAttribute(Attribute definition) {
        return database.createAttribute(definition);
    }

    @Override
    public List<Attribute> updateAttributes(List<Attribute> attributes) {
        return database.updateAttributes(attributes);
    }

    @Override
    public void deleteAttribute(Attribute dbAttribute) {
        database.deleteAttribute(dbAttribute);
    }

    @Override
    public Card getInfo(Classe classe, long cardId) {
        if (!userDaoHelper.getUser().getUserTenantContext().ignoreTenantPolicies() && getClasseHierarchy(classe).getDescendantsAndSelf().stream().anyMatch(Classe::hasMultitenantEnabled)) {//TODO improve this
            return getJdbcTemplate().queryForObject(format("SELECT _cm3_utils_regclass_to_name(\"IdClass\") \"IdClass\", \"Code\", \"Description\" FROM _cm3_card_info_get(%s, %s)", systemToSqlExpr(classe), cardId), (r, i) -> {
                return CardImpl.buildCard(ClasseImpl.copyOf(classe).withName(r.getString(ATTR_IDCLASS)).withAttributes(list(classe.getAllAttributes()).withOnly(a -> STANDARD_CLASS_INFO_ATTRIBUTES.contains(a.getName()))).build(), ATTR_ID, cardId, ATTR_CODE, r.getString(ATTR_CODE), ATTR_DESCRIPTION, r.getString(ATTR_DESCRIPTION));
            });
        } else {
            return select(STANDARD_CLASS_INFO_ATTRIBUTES).from(classe).where(ATTR_ID, EQ, cardId).getCard();
        }
    }

    @Override
    public List<CMRelation> getServiceRelationsForCard(CardIdAndClassName sourceCard, CmdbSorter sorter, boolean includeHistory) {
        Classe sourceClass = getClasse(sourceCard.getClassName());
        List<Domain> domains = database.getDomainsForClasse(sourceClass).stream()
                .filter(Domain::hasServiceReadPermission)
                .map((d) -> d.getThisDomainDirectAndOrReversedForClass(sourceClass))
                .flatMap(List::stream).collect(toList());

        AliasBuilder aliasBuilder = new AliasBuilder();
        String targetClassAlias = aliasBuilder.buildAlias("targetclass"),
                targetIdAlias = aliasBuilder.buildAlias("targetid"),
                targetDescAlias = aliasBuilder.buildAlias("targetdesc"),
                targetCodeAlias = aliasBuilder.buildAlias("targetcode"),
                directionAlias = aliasBuilder.buildAlias("direction");

        Map<String, String> customAttributeAliasesForAllDomains = domains.stream().flatMap(d -> {
            String direction = RelationDirectionQueryHelper.forDomain(d).name();
            return getDomainAttributesForRelationQuery(d).stream().filter(not(Attribute::isVirtual)).map(a -> {
                String alias = aliasBuilder.buildAliasAndStore(a.getName(), a.getOwner().getName(), direction, a.getName());
                String expr = format("NULL::%s %s", attributeTypeToSqlCast(a.getType()), alias);
                switch (a.getType().getName()) {
                    case FOREIGNKEY -> {
                        String codeAlias = aliasBuilder.buildAliasAndStore(buildCodeAttrName(a.getName()), a.getOwner().getName(), direction, buildCodeAttrName(a.getName())),
                                descAlias = aliasBuilder.buildAliasAndStore(buildDescAttrName(a.getName()), a.getOwner().getName(), direction, buildDescAttrName(a.getName()));
                        expr += format(", NULL::varchar %s, NULL::varchar %s", codeAlias, descAlias);
                    }
                    case LOOKUP -> {
                        String codeAlias = aliasBuilder.buildAliasAndStore(buildCodeAttrName(a.getName()), a.getOwner().getName(), direction, buildCodeAttrName(a.getName())),
                                descAlias = aliasBuilder.buildAliasAndStore(buildDescAttrName(a.getName()), a.getOwner().getName(), direction, buildDescAttrName(a.getName()));
                        expr += format(", NULL::varchar %s, NULL::varchar %s", codeAlias, descAlias);
                    }
                    case LOOKUPARRAY ->
                        expr += format(", NULL::varchar %s", aliasBuilder.buildAliasAndStore(buildInfoAttrName(a.getName()), a.getOwner().getName(), direction, buildInfoAttrName(a.getName())));
                }
                return Pair.of(alias, expr);
            });
        }).collect(toMap(Pair::getKey, Pair::getValue));

        List<String> domainQuery = domains.stream().map((d) -> {
            RelationDirectionQueryHelper direction = RelationDirectionQueryHelper.forDomain(d);
            String domainExpr = entryTypeToSqlExpr(d);
            String targetExpr = entryTypeToSqlExpr(d.getTargetClass());
            Map<String, String> customAttributeAliasesAndExprs = getDomainAttributesForRelationQuery(d).stream().filter(not(Attribute::isVirtual)).map(a -> {
                String alias = aliasBuilder.getAlias(a.getOwner().getName(), direction.name(), a.getName()),
                        attrExpr = quoteSqlIdentifier(a.getName()),
                        expr = format("%s.%s %s", domainExpr, attrExpr, alias);              //TODO proper attribute processing
                switch (a.getType().getName()) {
                    case FOREIGNKEY -> {
                        String codeAlias = aliasBuilder.getAlias(a.getOwner().getName(), direction.name(), buildCodeAttrName(a.getName())),
                                descAlias = aliasBuilder.getAlias(a.getOwner().getName(), direction.name(), buildDescAttrName(a.getName()));
                        expr += format(", %s %s, %s %s", buildReferenceCodeExpr(getClasse(a.getForeignKeyDestinationClassName()), domainExpr, attrExpr), codeAlias, buildReferenceDescExpr(getClasse(a.getForeignKeyDestinationClassName()), domainExpr, attrExpr), descAlias);
                    }
                    case LOOKUP -> {
                        String codeAlias = aliasBuilder.getAlias(a.getOwner().getName(), direction.name(), buildCodeAttrName(a.getName())),
                                descAlias = aliasBuilder.getAlias(a.getOwner().getName(), direction.name(), buildDescAttrName(a.getName()));
                        expr += format(", %s %s, %s %s", buildLookupCodeExpr(domainExpr, attrExpr), codeAlias, buildLookupDescExpr(domainExpr, attrExpr), descAlias);
                    }
                    case LOOKUPARRAY ->
                        expr += format(", %s %s", buildLookupInfoExpr(domainExpr, attrExpr), aliasBuilder.getAlias(a.getOwner().getName(), direction.name(), buildInfoAttrName(a.getName())));
                }
                return Pair.of(alias, expr);
            }).collect(toMap(Pair::getKey, Pair::getValue));
            String customAttrsQuery = customAttributeAliasesForAllDomains.keySet().stream().map(a -> {
                if (customAttributeAliasesAndExprs.containsKey(a)) {
                    return customAttributeAliasesAndExprs.get(a);
                } else {
                    return customAttributeAliasesForAllDomains.get(a);
                }
            }).collect(joining(", "));
            if (isNotBlank(customAttrsQuery)) {
                customAttrsQuery = ", " + customAttrsQuery;
            }
            String statusWhere = "TRUE";
            if (!includeHistory) {
                statusWhere = format("%s.\"Status\" = 'A'", domainExpr);
            }
            return String.format("SELECT '%s' %s, %s.\"Id\", %s.\"IdDomain\", %s.\"User\", %s.\"BeginDate\", %s.\"EndDate\", %s.\"Status\", %s.%s %s, %s.%s %s, "
                    + "%s.\"Description\" %s, %s.\"Code\" %s"//TODO replace with cross tenant function
                    + "%s "
                    + "FROM %s LEFT JOIN %s ON %s.%s = %s.\"IdClass\" AND %s.%s = %s.\"Id\" "
                    + "WHERE %s AND %s.\"Status\" = 'A' AND %s.%s = %s AND %s.%s = %s",
                    direction.name(), directionAlias, domainExpr, domainExpr, domainExpr, domainExpr, domainExpr, domainExpr, domainExpr, direction.getTargetClassIdExpr(), targetClassAlias, domainExpr, direction.getTargetCardIdExpr(), targetIdAlias,
                    targetExpr, targetDescAlias, targetExpr, targetCodeAlias,
                    customAttrsQuery,
                    domainExpr, targetExpr, domainExpr, direction.getTargetClassIdExpr(), targetExpr, domainExpr, direction.getTargetCardIdExpr(), targetExpr,
                    statusWhere, targetExpr, domainExpr, direction.getSourceClassIdExpr(), classNameToSqlExpr(sourceClass.getName()), domainExpr, direction.getSourceCardIdExpr(), sourceCard.getId());
        }).collect(toList());

        String query = Joiner.on(" UNION ALL ").join(domainQuery);
        if (!sorter.isNoop()) {
            sorter = sorter.mapAttributeNames(map(
                    "_destinationCode", "_targetcode",
                    "_destinationType", "_targetclass",
                    "_destinationDescription", "_targetdesc",
                    "_beginDate", "\"BeginDate\""));
            List<String> sortExprList = sorter.getElements().stream().map(s -> format("%s %s", quoteSqlIdentifier(s.getProperty()), s.getDirection().toSql())).collect(toList());
            query += format(" ORDER BY %s", Joiner.on(", ").join(sortExprList));
        }
        if (domains.isEmpty()) {
            return emptyList();
        } else {
            return database.getJdbcTemplate().query(query, (ResultSet rs, int rowNum) -> {
                Long relationId = checkNotNullAndGtZero(rs.getLong(ATTR_ID));
                String domainId = sqlTableToDomainName(checkNotBlank(rs.getString(ATTR_IDDOMAIN)));
                RelationDirectionQueryHelper direction = RelationDirectionQueryHelper.valueOf(rs.getString(directionAlias));
                String user = rs.getString(ATTR_USER);
                Timestamp beginDate = rs.getTimestamp(ATTR_BEGINDATE);
                Timestamp endDate = rs.getTimestamp(ATTR_ENDDATE);
                String targetDescription = rs.getString(targetDescAlias);
                String targetCode = rs.getString(targetCodeAlias);
                String status = rs.getString(ATTR_STATUS);
                String targetClassId = sqlTableToClassName(checkNotBlank(rs.getString(targetClassAlias)));
                Long targetCardId = checkNotNullAndGtZero(rs.getLong(targetIdAlias));
                Domain domain = database.getDomain(domainId);
                Classe targetClass = database.getClasse(targetClassId);
                return RelationImpl.builder()
                        .withDirection(direction.toRelationDirection())
                        .withType(domain)
                        .withId(relationId)
                        .withUser(user)
                        .withBeginDate(CmDateUtils.toDateTime(beginDate))
                        .withEndDate(CmDateUtils.toDateTime(endDate))
                        .withSourceCard(sourceCard)
                        .withStatus(parseEnumOrNull(status, Card.CardStatus.class))
                        .withTargetCard(CardIdAndClassNameImpl.card(targetClass.getName(), targetCardId))
                        .withTargetDescription(targetDescription)
                        .withAttribute(ATTR_CODE2, targetCode)
                        .addAttributes(parseEntryTypeQueryResponseData(domain, getDomainAttributesForRelationQuery(domain).stream().filter(not(Attribute::isVirtual)).flatMap(a -> {
                            return switch (a.getType().getName()) {
                                case LOOKUP, FOREIGNKEY ->
                                    list(a.getName(), buildCodeAttrName(a.getName()), buildDescAttrName(a.getName())).stream();
                                case LOOKUPARRAY ->
                                    list(a.getName(), buildInfoAttrName(a.getName())).stream();
                                default ->
                                    list(a.getName()).stream();
                            };
                        }).collect(toList()), identity(), rethrowFunction(a -> {
                            String alias = aliasBuilder.getAlias(domain.getName(), direction.name(), a);
                            return rs.getObject(alias);
                        })))
                        //TODO tenant id ??
                        .build();
            });
        }
    }

    @Override
    public PreparedQuery selectFunction(StoredFunction function, List<Object> input, List<Attribute> outputParamMapping) {
        return storedFunctionService.selectFunction(function, input, outputParamMapping);
    }

    @Override
    public Map<String, Object> callFunction(String functionId, Map<String, Object> functionParams) {
        return storedFunctionService.callFunction(functionId, functionParams);
    }

    @Override
    public Map<String, Object> callFunction(StoredFunction function, Map<String, Object> functionParams) {
        return storedFunctionService.callFunction(function, functionParams);
    }

    @Override
    public CMRelation create(CMRelation relation) {
        long id = database.create(relation);
        return getRelation(relation.getType(), id);
    }

    @Override
    public CMRelation update(CMRelation relation) {
        database.update(relation);
        return getRelation(relation.getType(), relation.getId());
    }

    @Override
    public void delete(CMRelation relation) {
        database.delete(relation);
    }

    private List<Attribute> getDomainAttributesForRelationQuery(Domain domain) {
        return domain.getCoreAttributes().stream().filter(a -> !DOMAIN_RESERVED_ATTRIBUTES.contains(a.getName())).collect(toList());
    }

}
