/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.command;

import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.dao.utils.CmSorterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.email.EmailSignature;
import org.cmdbuild.email.EmailSignatureService;
import org.cmdbuild.service.rest.v4.model.WsEmailSignatureData;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.cmdbuild.dao.utils.SorterProcessor.sorted;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author schursin
 */
@Component
public class EmailSignatureWsCommand {

    private final EmailSignatureService emailSignatureService;

    public EmailSignatureWsCommand(EmailSignatureService emailSignatureService) {
        this.emailSignatureService = checkNotNull(emailSignatureService);
    }

    public List<EmailSignature> doReadAll(String sort, Boolean detailed, String filterStr, Long limit, Long offset, Boolean onlyActive) {
        List<EmailSignature> listEmailSignature = emailSignatureService.getAll();
        if (onlyActive) {
            listEmailSignature = list(listEmailSignature).withOnly(EmailSignature::isActive);
        }
        return filterAndSort(listEmailSignature, sort, detailed, filterStr, limit, offset);
    }

    public EmailSignature doRead(String id) {
        return emailSignatureService.getOne(id);
    }

    public EmailSignature doCreate(WsEmailSignatureData data) {
        return emailSignatureService.create(data.toEmailSignature().build());
    }

    public EmailSignature doUpdate(Long signatureId, WsEmailSignatureData data) {
        return emailSignatureService.update(data.toEmailSignature().withId(signatureId).build());
    }

    public void doDelete(Long signatureId) {
        emailSignatureService.delete(signatureId);
    }

    List<EmailSignature> filterAndSort(List<EmailSignature> listEmailSignature, String sort, boolean detailed, String filterStr, Long limit, Long offset) {
        CmdbSorter sorter = CmSorterUtils.parseSorter(sort);
        if (!sorter.isNoop()) {
            listEmailSignature = sorted(listEmailSignature, sorter, (key, template) -> {
                switch (key) {
                    case "code":
                        return template.getCode();
                    case "description":
                        return template.getDescription();
                    default:
                        throw new IllegalArgumentException("unsupported filter key = " + key);
                }
            });
        }
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        if (filter.hasFilter()) {
            filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
            listEmailSignature = AttributeFilterProcessor.<EmailSignature>builder()
                    .withKeyToValueFunction((key, template) -> {
                        switch (checkNotBlank(key)) {
                            case "code":
                                return template.getCode();
                            case "description":
                                return template.getDescription();
                            default:
                                throw new IllegalArgumentException("invalid attribute filter key = " + key);
                        }
                    })
                    .withFilter(filter.getAttributeFilter()).build().filter(listEmailSignature);
        }
        return listEmailSignature;
    }


}
