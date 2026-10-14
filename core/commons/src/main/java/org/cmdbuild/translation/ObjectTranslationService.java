/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation;

import jakarta.annotation.Nullable;
import org.cmdbuild.dao.entrytype.*;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Strings.nullToEmpty;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.cmdbuild.translation.TranslationUtils.*;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;

public interface ObjectTranslationService {

    @Nullable
    String translateByCode(String code);

    @Nullable
    String translateByLangAndCode(String language, String code);

    String translateAttributeDescription(Attribute attribute);

    String translateAttributeGroupDescription(EntryType entryType, AttributeGroupInfo attributeGroup);

    default String translateViewAttributeGroupDescription(AttributeGroupData attributeGroup) {
        return translateViewAttributeGroupDescription(attributeGroup.getOwnerName(), attributeGroup.getName(), attributeGroup.getDescription());
    }

    default String translateViewAttributeDescription(String view, String code, String defaultValue) {
        return translateByCode(viewAttributeDescriptionTranslationCode(view, code), defaultValue);
    }

    default String translateViewAttributeGroupDescription(String view, String code, String defaultValue) {
        return translateByCode(viewAttributeGroupDescriptionTranslationCode(view, code), defaultValue);
    }

    default String translateAttributeFkMasterDetailDescription(Attribute attribute, String defaultValue) {
        return translateByCode(attributeFkMasterDetailDescriptionTranslationCode(attribute), defaultValue);
    }

    default String translateClassDescription(Classe classe) {
        return translateByCode(classDescriptionTranslationCode(classe), classe.getDescription());
    }

    default String translateClassDescriptionPlural(Classe classe) {
        return translateByCode(classDescriptionPluralTranslationCode(classe), translateClassDescription(classe));
    }

    default String translateClassHelp(Classe classe) {
        return translateByCode(classHelpTranslationCode(classe), classe.getMetadata().getHelpMessage());
    }

    default String translateDomainDirectDescription(String domainName, String defaultValue) {
        return translateByCode(domainDirectDescriptionTranslationCode(domainName), defaultValue);
    }

    default String translateDomainInverseDescription(String domainName, String defaultValue) {
        return translateByCode(domainInverseDescriptionTranslationCode(domainName), defaultValue);
    }

    default String translateDomainMasterDetailDescription(String domainName, String defaultValue) {
        return translateByCode(domainMasterDetailDescriptionTranslationCode(domainName), defaultValue);
    }

    default String translateFilterDescription(String className, String filterCode, String defaultValue) {
        return translateByCode(filterDescriptionTranslationCode(className, filterCode), defaultValue);
    }

    default String translateFunctionAttributeDescription(String functionName, String attributeName, String defaultValue) {
        return translateByCode(functionDescriptionTranslationCode(functionName, attributeName), defaultValue);
    }

    default String translateLookupDescriptionSafe(String lookupType, String lookupCode, String defaultValue) {
        try {
            return translateLookupDescription(lookupType, lookupCode, defaultValue);
        } catch (Exception ex) {
            LoggerFactory.getLogger(getClass()).warn(marker(), "unable to translate lookup description for lookup type = {} code = {}", lookupType, lookupCode, ex);
            return defaultValue;
        }
    }

    default String translateLookupDescription(String lookupType, String lookupCode, String defaultValue) {
        return translateByCode(lookupDescriptionTranslationCode(lookupType, lookupCode), defaultValue);
    }

    default String translateClassWidgetDescription(String owner, String widgetId, String defaultValue) {
        return translateByCode(classWidgetDescriptionTranslationCode(owner, widgetId), defaultValue);
    }

    default String translateWorkflowWidgetDescription(String processId, String taskId, String widgetId, String defaultValue) {
        return translateByCode(workflowWidgetDescriptionTranslationCode(processId, taskId, widgetId), defaultValue);
    }

    default String translateTaskDescription(String processId, String taskId, String defaultValue) {
        return translateByCode(taskDescriptionTranslationCode(processId, taskId), defaultValue);
    }

    default String translateTaskInstructions(String processId, String taskId, String defaultValue) {
        return translateByCode(taskInstructionsTranslationCode(processId, taskId), defaultValue);
    }

    default String translateRoleDescription(String roleCode, String defaultValue) {
        return translateByCode(roleDescriptionTranslationCode(roleCode), defaultValue);
    }

    default String translateMenuitemDescription(String code, String menuCode, String defaultValue) {
        return translateByCode(menuitemDescriptionTranslationCode(code, menuCode), defaultValue);
    }

    default String translateNavtreeDescription(String code, String defaultValue) {
        return translateByCode(navtreeDescriptionTranslationCode(code), defaultValue);
    }

    default String translateNavtreeItemDescription(String code, String item, String defaultValue) {
        return translateByCode(navtreeItemDescriptionTranslationCode(code, item), defaultValue);
    }

    default String translateNavtreeItemSubclassDescription(String code, String item, String subclass, String defaultValue) {
        return translateByCode(navtreeItemSubclassDescriptionTranslationCode(code, item, subclass), defaultValue);
    }

    default String translateDependencytreeDescription(String code, String defaultValue) {
        return translateByCode(dependencytreeDescriptionTranslationCode(code), defaultValue);
    }

    default String translateViewDescription(String viewCode, String defaultValue) {
        return translateByCode(viewDescriptionTranslationCode(viewCode), defaultValue);
    }

    default String translateViewDescriptionPlural(String viewCode, String defaultValue) {
        return translateByCode(viewDescriptionPluralTranslationCode(viewCode), translateViewDescription(viewCode, defaultValue));
    }

    default String translateReportDescription(String reportCode, String defaultValue) {
        return translateByCode(reportDescriptionTranslationCode(reportCode), defaultValue);
    }

    default String translateReportAttributeDescription(String reportCode, String attrCode, String defaultValue) {
        return translateByCode(reportAttributeDescriptionTranslationCode(reportCode, attrCode), defaultValue);
    }

    default String translateCustomPageDesciption(String customPageCode, String defaultValue) {
        return translateByCode(customPageDescriptionTranslationCode(customPageCode), defaultValue);
    }

    default String translateDashboardDescription(String dashboardCode, String defaultValue) {
        return translateByCode(dashboardDescriptionTranslationCode(dashboardCode), defaultValue);
    }

    default String translateDashboardComponentDescription(String dashboardCode, String componentId, String defaultValue) {
        return translateByCode(dashboardComponentDescriptionTranslationCode(dashboardCode, componentId), defaultValue);
    }

    default String translateDashboardComponentValueAxisLabel(String dashboardCode, String componentId, String defaultValue) {
        return translateByCode(dashboardComponentValueAxisLabelTranslationCode(dashboardCode, componentId), defaultValue);
    }

    default String translateDashboardComponentCategoryAxisLabel(String dashboardCode, String componentId, String defaultValue) {
        return translateByCode(dashboardComponentCategoryAxisLabelTranslationCode(dashboardCode, componentId), defaultValue);
    }

    default String translateDashboardComponentLabelField(String dashboardCode, String componentId, String defaultValue) {
        return translateByCode(dashboardComponentCategoryLabelFieldTranslationCode(dashboardCode, componentId), defaultValue);
    }

    default String translateDashboardComponentDataSourceParameter(String dashboardCode, String componentId, String index, String defaultValue) {
        return translateByCode(dashboardComponentDataSourceParameterTranslationCode(dashboardCode, componentId, index), defaultValue);
    }

    default String translateDashboardParametersDescription(String dashboardCode, String parameterId, String defaultValue) {
        return translateByCode(dashboardParametersDescriptionTranslationCode(dashboardCode, parameterId), defaultValue);
    }

    default String translateDashboardParametersEmptyText(String dashboardCode, String parameterId, String defaultValue) {
        return translateByCode(dashboardParametersEmptyTextTranslationCode(dashboardCode, parameterId), defaultValue);
    }

    default String translateHomepageDescription(String homepageCode, String defaultValue) {
        return translateByCode(homepageDescriptionTranslationCode(homepageCode), defaultValue);
    }

    default String translateHomepageComponentDescription(String homepageCode, String componentId, String defaultValue) {
        return translateByCode(homepageComponentDescriptionTranslationCode(homepageCode, componentId), defaultValue);
    }

    default String translateHomepageComponentValueAxisLabel(String homepageCode, String componentId, String defaultValue) {
        return translateByCode(homepageComponentValueAxisLabelTranslationCode(homepageCode, componentId), defaultValue);
    }

    default String translateHomepageComponentCategoryAxisLabel(String homepageCode, String componentId, String defaultValue) {
        return translateByCode(homepageComponentCategoryAxisLabelTranslationCode(homepageCode, componentId), defaultValue);
    }

    default String translateHomepageComponentLabelField(String homepageCode, String componentId, String defaultValue) {
        return translateByCode(homepageComponentCategoryLabelFieldTranslationCode(homepageCode, componentId), defaultValue);
    }

    default String translateHomepageComponentDataSourceParameter(String homepageCode, String componentId, String index, String defaultValue) {
        return translateByCode(homepageComponentDataSourceParameterTranslationCode(homepageCode, componentId, index), defaultValue);
    }

    default String translateCalendarTriggerDescription(String triggerCode, String defaultValue) {
        return translateByCode(calendarTriggerDescriptionTranslationCode(triggerCode), defaultValue);
    }

    default String translateCalendarTriggerContent(String triggerCode, String defaultValue) {
        return translateByCode(calendarTriggerContentTranslationCode(triggerCode), defaultValue);
    }

    default String translateEmailSignatureDescription(String code, String defaultValue) {
        return translateByCode(emailSignatureDescriptionTranslationCode(code), defaultValue);
    }

    default String translateEmailSignatureContenthtml(String code, String defaultValue) {
        return translateByCode(emailSignatureContentHtmlTranslationCode(code), defaultValue);
    }

    default String translateEmailTemplateDescription(String code, String defaultValue) {
        return translateByCode(notificationTemplateDescriptionTranslationCode(code), defaultValue);
    }

    default String translateOfflineDescription(String code, String defaultValue) {
        return translateByCode(offlineDescriptionTranslationCode(code), defaultValue);
    }

    default String translateByCode(String code, String defaultValue) {
        return firstNonNull(translateByCode(code), nullToEmpty(defaultValue));
    }

    default String translateByLangAndCode(String language, String code, String defaultValue) {
        return firstNonNull(translateByLangAndCode(language, code), nullToEmpty(defaultValue));
    }

    default String translateContextMenuLabel(String ownerName, String contextMenuLabel, String defaultValue) {
        return firstNonNull(translateByCode(contextMenuLabelTranslationCode(ownerName, contextMenuLabel)), nullToEmpty(defaultValue));
    }

}
