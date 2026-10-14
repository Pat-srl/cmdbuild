/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.utils;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.getOnlyElement;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import java.util.List;
import static java.util.function.Predicate.not;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.cql.CqlUtils.compileAndCheck;
import org.cmdbuild.cql.compiler.impl.CqlQueryImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_FLOW_DATA;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDTENANT;
import static org.cmdbuild.dao.constants.SystemAttributes.DOMAIN_RESERVED_ATTRIBUTES;
import static org.cmdbuild.dao.constants.SystemAttributes.PROCESS_CLASS_RESERVED_ATTRIBUTES;
import static org.cmdbuild.dao.constants.SystemAttributes.SIMPLE_CLASS_RESERVED_ATTRIBUTES;
import static org.cmdbuild.dao.constants.SystemAttributes.STANDARD_CLASS_RESERVED_ATTRIBUTES;
import static org.cmdbuild.dao.core.q3.DaoService.ATTRS_ALL;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.postgres.q3.beans.SelectArg;
import org.cmdbuild.dao.postgres.q3.beans.WhereElement;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.exprContainsQ3Markers;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.wrapExprWithBrackets;
import org.cmdbuild.data.filter.CqlFilter;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryBuilderUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static WhereElement compactWhereElements(List<WhereElement> list) {
        return compactWhereElements(list, true);
    }

    public static WhereElement compactWhereElements(List<WhereElement> list, boolean wrapWithBrackets) {
        return compactWhereElements(list, "AND", wrapWithBrackets);
    }

    public static WhereElement compactWhereElements(List<WhereElement> list, String operator) {
        return compactWhereElements(list, operator, true);
    }

    public static WhereElement compactWhereElements(List<WhereElement> list, String operator, boolean wrapWithBrackets) {
        checkArgument(!list.isEmpty(), "unable to compact empty where element list");
        if (list.size() == 1) {
            return getOnlyElement(list);
        } else if (list.stream().anyMatch(WhereElement::isFalse)) {
            return WhereElement.whereFalse();
        } else {
            if (wrapWithBrackets) {
                return WhereElement.build(wrapExprWithBrackets(list.stream().map(WhereElement::getExpr).distinct().map(SqlQueryUtils::wrapExprWithBrackets).collect(joining(" " + operator + " "))));
            } else {
                return WhereElement.build(list.stream().distinct().map(WhereElement::getExpr).collect(joining(" " + operator + " ")));
            }
        }
    }

    public static CqlQueryImpl compileCql(CqlFilter filter) {
        LOGGER.debug("processing cql filter = '{}'", abbreviate(filter.getCqlExpression()));
        return compileAndCheck(filter.getCqlExpression());
    }

    public static Collection<String> getReservedAttrs(EntryType entryType) {
        Collection<String> reservedAttrs;
        if (entryType.isClasse()) {
            if (((Classe) entryType).isStandardClass()) {
                if (((Classe) entryType).isProcess()) {
                    reservedAttrs = PROCESS_CLASS_RESERVED_ATTRIBUTES;//TODO: fix this, standardize and move ws customization back to flow/commons
                } else {
                    reservedAttrs = STANDARD_CLASS_RESERVED_ATTRIBUTES;
                }
            } else {
                reservedAttrs = SIMPLE_CLASS_RESERVED_ATTRIBUTES;
            }
            if (((Classe) entryType).hasMultitenantEnabled()) {
                reservedAttrs = set(reservedAttrs).with(ATTR_IDTENANT);
            }
        } else if (entryType.isDomain()) {
            reservedAttrs = DOMAIN_RESERVED_ATTRIBUTES;
        } else if (entryType.isFunction()) {
            reservedAttrs = emptyList();
        } else {
            throw unsupported("unsuppoted 'from' type = %s", entryType);
        }
        return reservedAttrs;
    }

    public static Collection<Attribute> getActiveAttributesFromQueryOptions(Classe classe, DaoQueryOptions query) {
        checkArgument(!query.hasAttrs() || !query.getOnlyGridAttrs(), "use attrs or onlyGridAttrs, cannot be used at the same time");
        Collection<Attribute> attributes = list(classe.getActiveServiceAttributes()).accept(c -> {
            if (classe.isProcess()) {
                c.add(classe.getAttribute(ATTR_FLOW_DATA)); // special attribute used by process
            }
        });
        if (query.hasAttrs()) {
            attributes.removeIf(not(a -> query.getAttrs().contains(a.getName())));
        } else if (query.getOnlyGridAttrs()) {
            attributes.removeIf(Attribute::isHiddenInGrid);
        }
        return attributes;
    }

    public static Collection<String> getQueryActiveAttributes(Classe classe, DaoQueryOptions query) {
        Collection<Attribute> attributes = getActiveAttributesFromQueryOptions(classe, query);
        if (classe.getActiveServiceAttributes().size() == attributes.size()) {
            return singletonList(ATTRS_ALL); // return ATTRS_ALL, to select * attributes
        }
        return attributes.stream().filter(not(Attribute::isVirtual)).map(Attribute::getName).collect(toSet());
    }

    public static String buildOffsetLimitExprOrBlank(DaoQueryOptions options) {
        String expr = "";
        if (options.hasOffset()) {
            expr += format(" OFFSET %s", options.getOffset());
        }
        if (options.hasLimit()) {
            expr += format(" LIMIT %s", options.getLimit());
        }
        return expr;
    }

    public static SelectArg buildSelectArgForExpr(String name, String expr) {
        return SelectArg.builder().withName(name).withExpr(expr).accept(b -> {
            if (exprContainsQ3Markers(expr)) {
                b.enableExprMarkerProcessing(true);
            } else {
                b.enableSmartAliasProcessing(true);
            }
        }).build();
    }

    public static String smartExprReplace(String expr, String joinAlias) {
        LOGGER.trace("executing smart alias processing for expr =< {} >", expr);
        //smart alias processing of expr; note: kinda weak
        Matcher matcher = Pattern.compile("(?!')\"([^\"']+)\"(?!')").matcher(expr);
        StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(joinAlias + ".") + "$0");
        }
        matcher.appendTail(stringBuffer);
        expr = stringBuffer.toString();
        LOGGER.trace("executed smart alias processing for expr, output =< {} >", expr);
        return expr;
    }

    public static List<String> getFulltextQueryParts(@Nullable String query) {
        if (isBlank(query)) {
            return emptyList();
        } else {
            Matcher matcher = Pattern.compile("([^\"]*)([\"]([^\"]+)[\"])?([^\"]*)").matcher(query);
            String remainingWords = "";
            List<String> list = list();
            while (matcher.find()) {
                remainingWords += " " + nullToEmpty(matcher.group(1)) + " " + nullToEmpty(matcher.group(4));
                if (isNotBlank(matcher.group(3))) {
                    list.add(matcher.group(3));
                }
            }
            // handling "" string present in input query -> don't like it
            // TODO handle "" using REGEX
            if (query.contains("\"\"")) list.add("\"\"");
            Splitter.onPattern("\\s+").trimResults().omitEmptyStrings().split(remainingWords).forEach(list::add);
            return list;
        }
    }

    public static String whereElementsToWhereExprBlankIfEmpty(List<WhereElement> whereElements) {
        if (whereElements.isEmpty()) {
            return "";
        } else {
            return " WHERE " + compactWhereElements(whereElements, false).getExpr();
        }
    }
}
