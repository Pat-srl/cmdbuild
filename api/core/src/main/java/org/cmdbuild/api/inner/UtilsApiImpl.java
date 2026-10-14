/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import java.util.Map;
import org.cmdbuild.api.ApiConverterService;
import org.cmdbuild.api.UtilsApi;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.email.template.EmailTemplateProcessorService;
import org.cmdbuild.email.template.EmailTemplateService;
import org.cmdbuild.template.SimpleExpressionInputData;
import org.springframework.stereotype.Component;

@Component
public class UtilsApiImpl implements UtilsApi {

    private final EmailTemplateProcessorService service;
    private final EmailTemplateService emailTemplateService;
    private final ApiConverterService cardHelper;

    public UtilsApiImpl(EmailTemplateProcessorService service, EmailTemplateService emailTemplateService, ApiConverterService cardHelper) {
        this.service = checkNotNull(service);
        this.emailTemplateService = checkNotNull(emailTemplateService);
        this.cardHelper = checkNotNull(cardHelper);
    }

    @Override
    public String applyTemplate(String template, org.cmdbuild.api.fluent.Card card) {
        return service.processExpression(
                SimpleExpressionInputData
                        .extendedBuilder()
                        .withExpression(nullToEmpty(template))
                        .withClientCard(cardHelper.apiCardToDaoCard(card))
                        .build());
    }

    /**
     * Done for #6029
     *
     * <p>
     * Was in <code>EmailTemplateProcessorService#applyEmailTemplateExpr(expr, data)
     *
     * @param template
     * @param data
     * @return
     */
    @Override
    public String applyTemplate(String template, Map<String, ?> data) {
        return service.processExpression(
                SimpleExpressionInputData
                        .extendedBuilder()
                        .withExpression(nullToEmpty(template))
                        .withClientCard(buildSynthesizedCardFrom(data))
                        .build());
    }

    @Override
    public EmailTemplate getTemplateOrNull(String templateCode) {
        return emailTemplateService.getByNameOrNull(templateCode);
    }

    /**
     * Done for #6029
     *
     * <p>
     * Was in <code>EmailTemplateProcessorService#applyEmailTemplateExpr(expr, data)
     *
     * @param data
     * @return
     */
    private static Card buildSynthesizedCardFrom(Map<String, ?> data) {
        return CardImpl.buildCard(ClasseImpl.builder().withName("DUMMY").build(), (Map) data);
    }

//    private final FtlTemplateService templateService;
//    private final CmApiService apiService;
//
//    public UtilsApiImpl(FtlTemplateService templateService, CmApiService apiService) {
//        this.templateService = checkNotNull(templateService);
//        this.apiService = checkNotNull(apiService);
//    }
//
//    @Override
//    public String applyTemplate(String template, Map<String, ?> data) {//TODO improve this
//        return templateService.executeFtlTemplate(nullToEmpty(template), map(data).with("cmdb", apiService.getCmApi(), "logger", LoggerFactory.getLogger(format("%s.TEMPLATE", getClass().getName()))));
//    }
}
