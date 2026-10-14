/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.stylerules;

import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableList;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserRepository;
import static org.cmdbuild.cache.CacheConfig.SYSTEM_OBJECTS;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CURRENTID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_USER;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.ResultRow;
import static org.cmdbuild.dao.core.q3.WhereOperator.IN;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.function.StoredFunction;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.gis.GisAttributeRepository;
import static org.cmdbuild.gis.stylerules.GisStyleRulesUtils.parseRules;
import static org.cmdbuild.gis.stylerules.GisStyleRulesUtils.serializeRules;
import static org.cmdbuild.gis.stylerules.GisStyleRulesetAccessType.AT_PRIVATE;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrDefault;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.equal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GisStyleRulesServiceImpl implements GisStyleRulesService {

    public static final String FUNCTION_OUTPUT_KEYWORD = "OUTPUT";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final GisStyleRulesRepository repository;
    private final GisAttributeRepository gisAttributeRepository;
    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final Holder<List<GisStyleRuleset>> allRules;

    public GisStyleRulesServiceImpl(DaoService dao, GisAttributeRepository gisAttributeRepository, GisStyleRulesRepository repository, UserRepository userRepository, SessionService sessionService, CacheService cacheService) {
        this.dao = checkNotNull(dao);
        this.repository = checkNotNull(repository);
        this.gisAttributeRepository = checkNotNull(gisAttributeRepository);
        this.userRepository = checkNotNull(userRepository);
        this.sessionService = checkNotNull(sessionService);
        allRules = cacheService.newHolder("gis_style_rules_all", SYSTEM_OBJECTS);
    }

    @Override
    public GisStyleRuleset create(GisStyleRuleset rules) {
        rules = parseData(repository.create(serializeData(rules, true)));
        allRules.invalidate();
        return rules;
    }

    @Override
    public GisStyleRuleset update(GisStyleRuleset rules) {
        rules = parseData(repository.update(serializeData(rules)));
        allRules.invalidate();
        return rules;
    }

    @Override
    public List<GisStyleRuleset> getForClass(String classId) {
        return getAll().stream().filter(equal(GisStyleRuleset::getOwnerClassName, classId)).collect(toList());
    }

    @Override
    public GisStyleRuleset getById(long rulesetId) {
        return getAll().stream().filter(equal(GisStyleRuleset::getId, rulesetId)).collect(onlyElement("ruleset not found for id = %s", rulesetId));
    }

    @Override
    public void delete(long rulesetId) {
        repository.delete(rulesetId);
        allRules.invalidate();
    }

    @Override
    public List<GisStyleRuleset> getAll() {
        return allRules.get(() -> repository.getAll().stream()
                .map(this::parseData)
                .collect(toUnmodifiableList()))
                .stream().filter(r -> {
                    if (Objects.equals(r.getAccessType(), AT_PRIVATE)) {
                        return applyOrDefault(r.getUserOwner(), uo -> Objects.equals(uo, applyOrNull(sessionService.getCurrentSessionOrNull(), s -> s.getOperationUser().getUsername())), false);
                    }
                    return true;
                }).collect(toList());
    }

    @Override
    public Map<Long, Map<String, Object>> applyRulesOnCards(GisStyleRuleset ruleset, Set<Long> cardIds) {
        return new RulesHelper(ruleset).applyRulesOnCards(cardIds);
    }

    private class RulesHelper {

        private final GisStyleRuleset ruleset;
        private final StoredFunction storedFunction;
        private final Classe classe;

        public RulesHelper(GisStyleRuleset ruleset) {
            this.ruleset = checkNotNull(ruleset);
            classe = dao.getClasse(ruleset.getOwnerClassName());
            if (ruleset.hasFunction()) {
                storedFunction = dao.getFunctionByName(ruleset.getFunction());
            } else {
                storedFunction = null;
            }
        }

        public Map<Long, Map<String, Object>> applyRulesOnCards(Set<Long> cardIds) {
            List<Map<String, Object>> list;
            List<Pair<CmdbFilter, Map<String, Object>>> rules = ruleset.getRules();
            if (ruleset.hasFunction()) {
                String query = format("SELECT \"Id\" _id, %s(\"Id\") _val FROM %s WHERE \"Status\" = 'A'", quoteSqlIdentifier(storedFunction.getName()), entryTypeToSqlExpr(classe));
                if (cardIds != null) {
                    query += format(" AND \"Id\" IN (%s)", cardIds.stream().map(l -> l.toString()).collect(joining(",")));
                }
                list = dao.getJdbcTemplate().query(query, (r, i) -> map(ATTR_ID, r.getLong("_id"), storedFunction.getOnlyOutputParameter().getName(), r.getObject("_val")));
                rules = mapOutputKeywordToFunctionOutput(rules);
            } else {
                list = dao.selectAll().from(classe).accept(q -> {
                    if (cardIds != null) {
                        q.where(ATTR_ID, IN, cardIds);
                    }
                }).run().stream().map(ResultRow::asMap).collect(toList());
            }
            return applyRules(rules, list);
        }

        private List<Pair<CmdbFilter, Map<String, Object>>> mapOutputKeywordToFunctionOutput(List<Pair<CmdbFilter, Map<String, Object>>> rules) {
            String outputName = storedFunction.getOnlyOutputParameter().getName();
            return rules.stream().map(r -> Pair.of(r.getLeft().mapNames(FUNCTION_OUTPUT_KEYWORD, outputName), r.getRight())).collect(toList());
        }

        private Map<Long, Map<String, Object>> applyRules(List<Pair<CmdbFilter, Map<String, Object>>> rules, List<Map<String, Object>> cards) {
            return cards.stream().collect(toMap(r -> toLong(r.get("Id")), r -> {
                logger.trace("apply rules on record id = {}", r.get("Id"));
                for (Pair<CmdbFilter, Map<String, Object>> rule : rules) {
                    CmdbFilter filter = rule.getLeft();
                    if (matchRule(filter, r)) {
                        logger.trace("found match for rule = {} on record id = {}", filter, r.get("Id"));
                        return rule.getRight();
                    }
                }
                logger.trace("no rule match for record id = {}", r.get("Id"));
                return emptyMap();
            }));
        }

        private boolean matchRule(CmdbFilter filter, Map<String, Object> record) {
            logger.trace("test rule = {} on record id = {}", filter, record.get("Id"));
            if (filter.isNoop()) {
                return true;
            }
            filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
            if (filter.hasAttributeFilter()) {
                filter = filter.mapNames(classe.getAliasToAttributeMap());
                return AttributeFilterProcessor.<Map<String, Object>>builder().withKeyToValueFunction((k, c) -> {
                    checkArgument(
                            (storedFunction != null && Objects.equals(storedFunction.getOnlyOutputParameter().getName(), k))
                            || (classe.hasAttribute(k) && classe.getAttribute(k).hasServiceReadPermission()), "invalid attr =< %s > : attr not found or user not allowed to read", k);
                    return c.get(k);
                }).withAttributeTypeFunction((k) -> classe.hasAttribute((String) k) ? classe.getAttribute((String) k).getType() : (storedFunction != null && Objects.equals(storedFunction.getOnlyOutputParameter().getName(), k) ? storedFunction.getOnlyOutputParameter().getType() : null)).withFilter(filter.getAttributeFilter()).build().match(record);
            }
            return false;//TODO improve this
        }
    }

    private GisStyleRuleset parseData(GisStyleRulesetData data) {
        return GisStyleRulesetImpl.builder()
                .withCode(data.getCode())
                .withDescription(data.getDescription())
                .withFunction(data.getFunction())
                .withGisAttribute(gisAttributeRepository.getLayer(data.getGisAttribute()))
                .withId(data.getId())
                .withRules(parseRules(data.getRules()))
                .withParams(data.getParams())
                .build();
    }

    private GisStyleRulesetData serializeData(GisStyleRuleset rules) {
        return serializeData(rules, false);
    }

    private GisStyleRulesetData serializeData(GisStyleRuleset rules, boolean addUserOwner) {
        GisStyleRulesetParams params = GisStyleRulesetParamsImpl.copyOf(rules.getParams()).accept(p -> {
            if (addUserOwner) {
                p.withUserOwner(applyOrNull(sessionService.getCurrentSessionOrNull(), s -> s.getOperationUser().getUsername()));
            } else if (rules.getId() != null) {
                // get old user owner
                String userOwner = applyOrNull(dao.getByIdOrNull(GisStyleRulesetData.class, rules.getId()), r -> r.getParams().getUserOwner());
                // user owner not set, use creation user
                if (userOwner == null) {
                    userOwner = dao.getJdbcTemplate().queryForObject(format("SELECT %s FROM %s WHERE %s = %s ORDER BY %s ASC LIMIT 1", quoteSqlIdentifier(ATTR_USER), quoteSqlIdentifier("_GisStyleRules"), quoteSqlIdentifier(ATTR_CURRENTID), rules.getId(), quoteSqlIdentifier(ATTR_BEGINDATE)), String.class);
                }
                // if user is not active, use current user
                if (!applyOrDefault(userRepository.getUserDataByUsernameOrNull(userOwner), UserData::isActive, false)) {
                    userOwner = applyOrNull(sessionService.getCurrentSessionOrNull(), s -> s.getOperationUser().getUsername());
                }
                p.withUserOwner(userOwner);
            }
        }).build();
        return GisStyleRulesetDataImpl.builder()
                .withCode(rules.getCode())
                .withDescription(rules.getDescription())
                .withFunction(rules.getFunction())
                .withGisAttribute(rules.getGisAttribute().getId())
                .withId(rules.getId())
                .withOwner(rules.getGisAttribute().getOwnerClassName())
                .withParams(params)
                .withRules(serializeRules(rules.getRules()))
                .build();
    }
}
