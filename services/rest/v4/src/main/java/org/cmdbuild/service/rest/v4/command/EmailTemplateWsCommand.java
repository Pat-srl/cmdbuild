/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.command;

import com.google.common.base.Supplier;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.dao.utils.CmSorterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.email.beans.EmailTemplateImpl;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.email.template.EmailTemplateService;
import org.cmdbuild.report.ReportConfig;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.services.serialization.EmailTemplateSerializationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.dao.utils.SorterProcessor.sorted;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

/**
 *
 * @author schursin
 */
@Component
public class EmailTemplateWsCommand {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EmailTemplateSerializationHelper emailTemplateSerializationHelper;
    private final ReportService reportService;
    private final EmailTemplateService emailTemplateService;

    public EmailTemplateWsCommand(EmailTemplateSerializationHelper emailTemplateSerializationHelper, org.cmdbuild.report.ReportService reportService, EmailTemplateService emailTemplateService) {
        this.emailTemplateSerializationHelper = checkNotNull(emailTemplateSerializationHelper);
        this.reportService = checkNotNull(reportService);
        this.emailTemplateService = checkNotNull(emailTemplateService);
    }

    public List<EmailTemplate> doReadAllForClass(Supplier<List<EmailTemplate>> function, String filterStr, String sort, String classId) {
        List<EmailTemplate> emailTemplateList = function.get();
        return filterAndSort(emailTemplateList, filterStr, sort, classId);
    }

    public List<EmailTemplate> doReadAll(Supplier<List<EmailTemplate>> function, String filterStr, String sort) {
        List<EmailTemplate> emailTemplateList = function.get();
        return filterAndSort(emailTemplateList, filterStr, sort, null);
    }

    public EmailTemplate doRead(String id) {
        return emailTemplateService.getByNameOrId(id);
    }

    public List<EmailTemplate> filterAndSort(List<EmailTemplate> listEmailTemplate, String filterStr, String sort, String classId) {
        CmdbSorter sorter = CmSorterUtils.parseSorter(sort);
        if (!sorter.isNoop()) {
            listEmailTemplate = sorted(listEmailTemplate, sorter, (key, template) -> toStringOrNull(emailTemplateSerializationHelper.serializeBasicTemplate(template).get(key)));//TODO improve this
        }
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        if (filter.hasFilter()) {
            filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
            listEmailTemplate = AttributeFilterProcessor.<EmailTemplate>builder()
                    .withKeyToValueFunction((key, template) -> toStringOrNull(emailTemplateSerializationHelper.serializeBasicTemplate(template).get(key)))//TODO improve this
                    .withFilter(filter.getAttributeFilter()).build().filter(listEmailTemplate);
        }
        if (isNotBlank(classId)) {
            listEmailTemplate.removeIf(t -> !t.getShowOnClasses().isEmpty() && !t.getShowOnClasses().contains(classId));
        }
        return listEmailTemplate;
    }

    public EmailTemplate skipBatchReports(EmailTemplate emailTemplate) {
        List<ReportConfig> listReportConfig = list(emailTemplate.getReports()).without(r -> {
            boolean isBatch = reportService.getByCode(r.getCode()).isBatchReport();
            if (isBatch) {
                logger.warn("unable to use batch report =< {} > for email template =< {} >", r.getCode(), emailTemplate.getCode());
            }
            return isBatch;
        });
        return EmailTemplateImpl.copyOf(emailTemplate).withReports(listReportConfig).build();
    }
}
