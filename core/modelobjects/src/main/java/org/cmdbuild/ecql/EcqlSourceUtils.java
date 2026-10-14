/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.ecql;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.cql.EcqlException;
import org.cmdbuild.dao.entrytype.AbstractMetadata;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.ecql.inner.EcqlExpressionImpl;
import org.cmdbuild.utils.lang.CmStringUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.regex.Pattern.quote;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.dao.entrytype.Domain.DOMAIN_SOURCE_FILTER_SIDE;
import static org.cmdbuild.dao.entrytype.Domain.DOMAIN_TARGET_FILTER_SIDE;
import static org.cmdbuild.ecql.EcqlSource.SYSPARAM;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeString;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeString;
import static org.cmdbuild.utils.json.CmJsonUtils.*;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrLtEqZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;

public class EcqlSourceUtils {

    public final static String ECQL_WIDGET_FROM_CLASS = "CLASS";
    public final static String UNIQUE_CLASS_TOKEN_DELIMITER = "::";

    private final static BiMap<EcqlSource, String> MINIFIED_ECQL_SOURCE_KEY_MAP = ImmutableBiMap.copyOf(map(
            EcqlSource.CLASS_ATTRIBUTE, "C",
            EcqlSource.DOMAIN, "O",
            EcqlSource.DOMAIN_MD, "M",
            EcqlSource.DOMAIN_ATTRIBUTE, "A",
            EcqlSource.REPORT_ATTRIBUTE, "R",
            EcqlSource.EMBEDDED, "E",
            EcqlSource.NAVTREE_NODE, "N",
            EcqlSource.EASYTEMPLATE, "T",
            EcqlSource.DASHBOARD_ITEM, "D",
            EcqlSource.HOMEPAGE_ITEM, "H",
            EcqlSource.WIDGET, "W",
            SYSPARAM, "P"
    ));

    public static String buildEcqlId(EcqlSource source, Object... sourceId) {
        List<String> keys = asList(checkNotNull(sourceId)).stream().map(CmStringUtils::toStringOrNull).collect(toList());
        return buildEcqlId(source, keys.toArray(String[]::new));
    }

    public static String buildAttrEcqlId(Attribute attribute) {
        if (attribute.getOwner().isReport()) { // Specific case of isClass()
            return buildReportAttrEcqlId(attribute);
        } else if (attribute.getOwner().isClasse()) {
            return buildEcqlId(EcqlSource.CLASS_ATTRIBUTE, attribute.getOwner().getName(), attribute.getName());
        } else if (attribute.getOwner().isDomain()) {
            return buildEcqlId(EcqlSource.DOMAIN_ATTRIBUTE, attribute.getOwner().getName(), attribute.getName());
        } else if (isNullOrLtEqZero(attribute.getOwner().getId())) {
            return buildEmbeddedEcqlId(attribute.getFilter());
        }
        throw new IllegalArgumentException(format("while handling ecqlId, unhandled attribute type %s", attribute.getOwner().getEtType()));
    }

//    public static String buildDomainEcqlId(Domain domain, Attribute attribute) {
//        return buildEcqlId(EcqlSource.DOMAIN, domain.getName(), domain.getReferencedClassToken(attribute));
//    }
    public static String buildDomainEcqlId(Domain domain, String filterSide) {
        return buildEcqlId(EcqlSource.DOMAIN, domain.getName(), filterSide);
    }

    public static String buildDomainMasterDetailEcqlId(Domain domain) {
        return buildEcqlId(EcqlSource.DOMAIN_MD, domain.getName());
    }

    public static String buildReportAttrEcqlId(Attribute attribute) {
        return buildEcqlId(EcqlSource.REPORT_ATTRIBUTE, attribute.getOwner().getId(), attribute.getName());
    }

    public static String buildTemplateEcqlId(String code) {
        return buildEcqlId(EcqlSource.EASYTEMPLATE, checkNotBlank(code));
    }

    @Deprecated
    public static String buildEmbeddedEcqlId(String cqlExpr) {
        return buildEcqlId(EcqlSource.EMBEDDED, checkNotBlank(cqlExpr));
    }

    public static String buildNavTreeEcqlId(String navTreeCode, String nodeId) {
        return buildEcqlId(EcqlSource.NAVTREE_NODE, checkNotBlank(navTreeCode), checkNotBlank(nodeId));
    }

    public static String buildDashboardEcqlId(String dashboardCode, int index) {
        return buildEcqlId(EcqlSource.DASHBOARD_ITEM, checkNotBlank(dashboardCode), toStringNotBlank(index));
    }

    public static String buildHomepageEcqlId(String homepageCode, int index) {
        return buildEcqlId(EcqlSource.HOMEPAGE_ITEM, checkNotBlank(homepageCode), toStringNotBlank(index));
    }

    public static String buildWidgetEcqlId(String classId, String widgetId, String widgetAttr, Map<String, Object> xaContext) {
        return buildWidgetEcqlId(classId, null, widgetId, widgetAttr, xaContext);
    }

    public static String buildWidgetEcqlId(String processIdOrClassId, @Nullable String taskDefinitionIdOrNull, String widgetId, String widgetAttr, Map<String, Object> xaContext) {
        return buildEcqlId(EcqlSource.WIDGET, processIdOrClassId, firstNotBlank(taskDefinitionIdOrNull, ECQL_WIDGET_FROM_CLASS), widgetId, widgetAttr, toJson(xaContext));
    }

    public static String buildEcqlId(EcqlSource source, String... sourceId) {
        List<String> args = asList(checkNotNull(sourceId));
        checkArgument(args.stream().allMatch(StringUtils::isNotBlank));
        String ecqlRawId = format("%s%s",
                checkNotNull(MINIFIED_ECQL_SOURCE_KEY_MAP.get(source), format("missing MINIFIED_ECQL_SOURCE_KEY_MAP definition for %s", source)),
                args.size() == 1 ? getOnlyElement(args) : toJson(sourceId));
        String encodedId = encodeString(ecqlRawId);
        return encodedId;
    }

    public static EcqlId parseEcqlId(String encodedId) {
        try {
            String decodedId = decodeString(encodedId);
            Matcher matcher = Pattern.compile("(.)(.+)").matcher(decodedId);
            checkArgument(matcher.matches(), "invalid ecql format for value =< %s >", abbreviate(decodedId));
            String key = checkNotBlank(matcher.group(1)),
                    value = checkNotBlank(matcher.group(2));
            EcqlSource source = checkNotNull(MINIFIED_ECQL_SOURCE_KEY_MAP.inverse().get(key), "ecql source not found for key = '%s'", key);
            List<String> idList;
            if (value.matches("\\[.*\\]")) {
                idList = fromJson(value, LIST_OF_STRINGS);
            } else {
                idList = singletonList(value);
            }
            return new EcqlIdImpl(source, idList);
        } catch (Exception ex) {
            throw new EcqlException(ex, "error parsing ecqlId = '%s'", encodedId);
        }
    }

    public static String getJsExprFromContext(String key, Map<String, Object> context) {
        return checkNotBlank((String) context.get(key), "value not found in context for js key = '%s'", key);
    }

    public static EcqlExpression getEcqlExpressionFromAttributeFilter(Attribute attribute) {
        String filter = attribute.getFilter();
        checkNotBlank(filter);
        //TODO check syntax??

        return getEcqlExpression(attribute.getMetadata(), filter);
    }

    public static EcqlExpression getEcqlExpressionFromDomainClassFilter(Domain domain, String filterSide) {
        String filter = switch (filterSide) {
            case DOMAIN_SOURCE_FILTER_SIDE ->
                domain.getMetadata().getSourceFilter();
            case DOMAIN_TARGET_FILTER_SIDE ->
                domain.getMetadata().getTargetFilter();
            default ->
                throw unsupported("unsupported domain filter side %s", filterSide);
        };
        checkNotBlank(filter, format("%s filter < %s > not found", domain, filterSide));

        return getEcqlExpression(domain.getMetadata(), filter);
    }

    public static EcqlExpression getEcqlExpressionFromDomainClassMasterDetailFilter(Domain domain) {
        String filter = domain.getMasterDetailFilter();
        checkNotBlank(filter);

        return getEcqlExpression(domain.getMetadata(), filter);
    }

    public static EcqlExpression getEcqlExpression(AbstractMetadata metadata, String filter) {
        Map<String, Object> context = fetchTemplateValues(metadata);

        return new EcqlExpressionImpl(filter, context);
    }

    private static Map<String, Object> fetchTemplateValues(AbstractMetadata metadata) {
        Map<String, Object> context = map(metadata.getAll());
        metadata.getAll().entrySet().stream()
                .filter((e) -> e.getKey().startsWith("system.template."))
                .forEach((e) -> context.put(e.getKey().replaceFirst(quote("system.template."), ""), e.getValue()));

        return context;
    }

    private final static class EcqlIdImpl implements EcqlId {

        private final EcqlSource source;
        private final List<String> id;

        public EcqlIdImpl(EcqlSource source, List<String> id) {
            this.source = checkNotNull(source);
            this.id = copyOf(checkNotNull(id));
        }

        @Override
        public EcqlSource getSource() {
            return source;
        }

        @Override
        public List<String> getId() {
            return id;
        }

        @Override
        public String toString() {
            return "EcqlId{" + "source=" + source + ", id=" + id + '}';
        }

    }

}