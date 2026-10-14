/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.postgres.utils.SqlQueryUtils;
import org.cmdbuild.data.filter.SorterElement;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.common.Constants.*;
import static org.cmdbuild.dao.constants.SystemAttributes.*;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_ONE;
import static org.cmdbuild.dao.entrytype.DomainCardinality.ONE_TO_MANY;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.pgObjectToString;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.notNull;
import static org.cmdbuild.utils.lang.CmStringUtils.toLowerCaseOrNull;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;

/**
 * @author ldare
 */
@Component
public class GlobalSearchWsCommand {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService daoService;
    private final UserClassService userClassService;
    private final UserDomainService userDomainService;

    private static final String EMAIL_CLASS = "Email";

    public GlobalSearchWsCommand(DaoService daoService, UserClassService userClassService, UserDomainService userDomainService) {
        this.daoService = checkNotNull(daoService);
        this.userClassService = checkNotNull(userClassService);
        this.userDomainService = checkNotNull(userDomainService);
    }

    public List<Card> doGlobalSearch(WsQueryOptions wsQueryOptions) {
        Stopwatch totalStopwatch = Stopwatch.createStarted();
        String query = wsQueryOptions.getQuery().getFilter().getFulltextFilter().getQuery();
        logger.trace("global search =< {} >", query);
        Stream<Domain> domains = userDomainService.getActiveUserDomains().stream().filter(d -> d.hasCardinality(ONE_TO_MANY) || d.hasCardinality(MANY_TO_ONE));

        Set<Classe> classesFromDomains = set();
        domains.forEach(d -> {
            classesFromDomains.addAll(userClassService.getActiveUserClasses().stream().filter(c -> c.equalToOrDescendantOf(d.getSourceClass())).collect(toList()));
            classesFromDomains.addAll(userClassService.getActiveUserClasses().stream().filter(c -> c.equalToOrDescendantOf(d.getSourceClass())).collect(toList()));
        });
        // adding and removing special classes
        classesFromDomains.addAll(list(userClassService.getUserClassOrNull(LOOKUP_CLASS_NAME), userClassService.getUserClassOrNull(USER_CLASS_NAME), userClassService.getUserClassOrNull(ROLE_CLASS_NAME)).filter(notNull()));
        classesFromDomains.remove(daoService.getClasse(BASE_CLASS_NAME));

        String sqlClass = format("SELECT %s FROM %s WHERE %s = 'A' AND %s IN (%s)",
                STANDARD_CLASS_INFO_ATTRIBUTES.stream().map(SqlQueryUtils::quoteSqlIdentifier).collect(joining(", ")),
                quoteSqlIdentifier(BASE_CLASS_NAME),
                quoteSqlIdentifier(ATTR_STATUS),
                quoteSqlIdentifier(ATTR_IDCLASS),
                classesFromDomains.stream().map(c -> format("'%s'::regclass", quoteSqlIdentifier(c.getName()))).collect(joining(", ")));

        Stopwatch codeDescrStopwatch = Stopwatch.createStarted();
        List<Map<String, Object>> codeDescrMatches = daoService.getJdbcTemplate().queryForList(sqlClass);
        logger.trace("executing global search query on =< Code, Description > on {}, elapsed: {}", classesFromDomains, codeDescrStopwatch);

        // remove system classes
        codeDescrMatches.removeIf(r -> pgObjectToString(r.get(ATTR_IDCLASS)).startsWith("_") || pgObjectToString(r.get(ATTR_IDCLASS)).equals(EMAIL_CLASS));

        // remove all records not containing the text searched
        String textToSearch = toLowerCaseOrNull(query);
        codeDescrMatches.removeIf(r -> {
            String code = toLowerCaseOrNull(r.get(ATTR_CODE));
            String description = toLowerCaseOrNull(r.get(ATTR_DESCRIPTION));
            return code == null || !code.contains(textToSearch) && description == null || !description.contains(textToSearch);
        });
        logger.trace("Code, Description matches on =< {} >", codeDescrMatches);

        Stream<Classe> classes = userClassService.getAllUserClasses().stream().filter(c -> !equal(c.getName(), BASE_CLASS_NAME) && !c.isSuperclass() && c.isStandardClass());

        List<Card> cards = list();
        classes.forEach(c -> {
            String attributesQuery = c.getActiveUiAttributes().stream().map(a -> {
                return switch (a.getType().getName()) {
                    case LOOKUP -> {
                        String lookupIds = codeDescrMatches.stream().filter(r -> pgObjectToString(r.get(ATTR_IDCLASS)).equals(LOOKUP_CLASS_NAME)).map(r -> toStringNotBlank(r.get(ATTR_ID))).collect(joining(", "));
                        yield isBlank(lookupIds) ? "" : format("%s IN (%s)", quoteSqlIdentifier(a.getName()), lookupIds);
                    }
                    case REFERENCE, FOREIGNKEY -> {
                        Classe relatedClasse = switch (a.getType().getName()) {
                            case REFERENCE ->
                                    userDomainService.getDomain(a.getMetadata().getDomain()).getReferencedClass(a);
                            case FOREIGNKEY -> userClassService.getUserClass(a.getForeignKeyDestinationClassName());
                            default -> throw unsupported("unsopported attribute type =< {} >", a.getType().getName());
                        };
                        String referenceIds = codeDescrMatches.stream().filter(r -> relatedClasse.equalToOrAncestorOf(daoService.getClasse(pgObjectToString(r.get(ATTR_IDCLASS)))) || daoService.getClasse(pgObjectToString(r.get(ATTR_IDCLASS))).equalToOrAncestorOf(relatedClasse)).map(r -> toStringNotBlank(r.get(ATTR_ID))).collect(joining(", "));
                        logger.trace("class {} is reference of attribute {} - ids =< {} >", relatedClasse.getName(), a.getName(), referenceIds);
                        yield isBlank(referenceIds) ? "" : format("%s IN (%s)", quoteSqlIdentifier(a.getName()), referenceIds);
                    }
                    case FORMULA -> "";
                    default -> format("%s::text ILIKE '%%%s%%'", quoteSqlIdentifier(a.getName()), query);
                };
            }).filter(StringUtils::isNotEmpty).collect(joining(" OR "));

            logger.trace("executing global search query for {} with where {}", c.getName(), attributesQuery);
            Stopwatch classStopwatch = Stopwatch.createStarted();
            daoService.select(set(STANDARD_CLASS_INFO_ATTRIBUTES).with(ATTR_USER, ATTR_BEGINDATE)).from(c).whereExpr(isBlank(attributesQuery) ? "" : format("%s", attributesQuery)).getCards().forEach(cards::add);
            logger.trace("executing global search query for {}, elapsed: {}", c.getName(), classStopwatch);
        });
        logger.debug("found {} elements that matches to {}, elapsed: {}", cards.size(), query, totalStopwatch);
        return cards;
    }

    public static Comparator reorderComparator(Comparator<Card> comparator, SorterElement sorter) {
        return switch (sorter.getDirection()) {
            case DESC -> comparator;
            case ASC -> comparator.reversed();
        };
    }
}
