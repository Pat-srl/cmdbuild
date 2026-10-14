/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.ecql.utils;

import jakarta.annotation.Nullable;
import org.cmdbuild.dao.entrytype.AbstractMetadata;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.easytemplate.EasytemplateProcessor;
import org.cmdbuild.easytemplate.EasytemplateProcessorImpl;
import org.cmdbuild.ecql.*;
import org.cmdbuild.ecql.inner.EcqlBindingInfoImpl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class EcqlUtils {

    public final static String ECQL_WIDGET_FROM_CLASS = EcqlSourceUtils.ECQL_WIDGET_FROM_CLASS;
    public final static String UNIQUE_CLASS_TOKEN_DELIMITER = EcqlSourceUtils.UNIQUE_CLASS_TOKEN_DELIMITER;

    public static String buildEcqlId(EcqlSource source, Object... sourceId) {
        return EcqlSourceUtils.buildEcqlId(source, sourceId);
    }

    public static String buildAttrEcqlId(Attribute attribute) {
        return EcqlSourceUtils.buildAttrEcqlId(attribute);
    }

    public static String buildDomainEcqlId(Domain domain, String filterSide) {
        return EcqlSourceUtils.buildDomainEcqlId(domain, filterSide);
    }

    public static String buildDomainMasterDetailEcqlId(Domain domain) {
        return EcqlSourceUtils.buildDomainMasterDetailEcqlId(domain);
    }

    public static String buildReportAttrEcqlId(Attribute attribute) {
        return EcqlSourceUtils.buildReportAttrEcqlId(attribute);
    }

    public static String buildTemplateEcqlId(String code) {
        return EcqlSourceUtils.buildTemplateEcqlId(code);
    }

    @Deprecated
    public static String buildEmbeddedEcqlId(String cqlExpr) {
        return EcqlSourceUtils.buildEmbeddedEcqlId(cqlExpr);
    }

    public static String buildNavTreeEcqlId(String navTreeCode, String nodeId) {
        return EcqlSourceUtils.buildNavTreeEcqlId(navTreeCode, nodeId);        
    }

    public static String buildDashboardEcqlId(String dashboardCode, int index) {
        return EcqlSourceUtils.buildDashboardEcqlId(dashboardCode, index);        
    }

    public static String buildHomepageEcqlId(String homepageCode, int index) {
        return EcqlSourceUtils.buildHomepageEcqlId(homepageCode, index);
    }

    public static String buildWidgetEcqlId(String classId, String widgetId, String widgetAttr, Map<String, Object> xaContext) {
        return EcqlSourceUtils.buildWidgetEcqlId(classId, widgetId, widgetAttr, xaContext);        
    }

    public static String buildWidgetEcqlId(String processIdOrClassId, @Nullable String taskDefinitionIdOrNull, String widgetId, String widgetAttr, Map<String, Object> xaContext) {
        return EcqlSourceUtils.buildWidgetEcqlId(processIdOrClassId, taskDefinitionIdOrNull, widgetId, widgetAttr, xaContext);        
    }

    public static String buildEcqlId(EcqlSource source, String... sourceId) {
        return EcqlSourceUtils.buildEcqlId(source, sourceId);
    }

    public static EcqlId parseEcqlId(String encodedId) {
        return EcqlSourceUtils.parseEcqlId(encodedId);
    }

    public static EcqlBindingInfo getEcqlBindingInfoForExpr(EcqlExpression expression) {
        return getEcqlBindingInfoForExpr(expression.getEcql(), expression.getContext());
    }

    public static String resolveEcqlXa(String ecql, Map<String, Object> xaContext) {
        return EasytemplateProcessorImpl.builder()
                .withResolver("xa", xaContext::get)
                .build().processExpression(ecql);
    }

    public static EcqlBindingInfo getEcqlBindingInfoForExpr(String ecql, Map<String, Object> context) {
        checkNotBlank(ecql, "ecql expression is null or blank");

        List<String> clientBindings = list(),
                serverBindings = list(),
                xaBindings = list();

        AtomicReference<EasytemplateProcessor> processor = new AtomicReference<>();

        processor.set(EasytemplateProcessorImpl.builder()
                .withResolver("client", (key) -> clientBindings.add(key))
                .withResolver("server", (key) -> serverBindings.add(key))
                .withResolver("xa", (key) -> xaBindings.add(key))
                .withResolver("js", (key) -> processor.get().processExpression(getJsExprFromContext(key, context)))
                .build());

        processor.get().processExpression(ecql);

        return EcqlBindingInfoImpl.builder()
                .withClientBindings(clientBindings)
                .withServerBindings(serverBindings)
                .withXaBindings(xaBindings)
                .build();
    }

    public static String getJsExprFromContext(String key, Map<String, Object> context) {
        return EcqlSourceUtils.getJsExprFromContext(key, context);
    }

    public static EcqlExpression getEcqlExpressionFromAttributeFilter(Attribute attribute) {
        return EcqlSourceUtils.getEcqlExpressionFromAttributeFilter(attribute);
    }

    public static EcqlExpression getEcqlExpressionFromDomainClassFilter(Domain domain, String filterSide) {
        return EcqlSourceUtils.getEcqlExpressionFromDomainClassFilter(domain, filterSide);
    }

    public static EcqlExpression getEcqlExpressionFromDomainClassMasterDetailFilter(Domain domain) {
        return EcqlSourceUtils.getEcqlExpressionFromDomainClassMasterDetailFilter(domain);
    }

    public static EcqlExpression getEcqlExpression(AbstractMetadata metadata, String filter) {
        return EcqlSourceUtils.getEcqlExpression(metadata, filter);
    }

}
