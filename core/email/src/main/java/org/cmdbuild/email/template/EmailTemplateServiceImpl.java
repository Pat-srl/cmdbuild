/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.template;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.function.Function.identity;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.cache.Holder;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.email.EmailSignature;
import org.cmdbuild.email.EmailSignatureService;
import org.cmdbuild.email.beans.EmailTemplateImpl;
import org.cmdbuild.etl.config.WaterwayConfigReloadEvent;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import org.cmdbuild.etl.config.WaterwayItem;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_NOTIFICATION;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.template.TemplateBindings;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.toListOfStrings;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrNull;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private final EmailAccountService accountService;
    private final EmailSignatureService signatureService;
    private final EmailSysTemplateRepository sysTemplateRepository;
    private final EmailTemplateProcessorService processorService;
    private final DaoService dao;
    private final WaterwayDescriptorService configService;

    private final Holder<List<EmailTemplate>> templates;
    private final CmCache<Optional<EmailTemplate>> templatesByIdOrCode;

    public EmailTemplateServiceImpl(CacheService cache, EventBusService busService, EmailAccountService accountService, EmailSignatureService signatureService, EmailSysTemplateRepository sysTemplateRepository, EmailTemplateProcessorService processorService, DaoService dao, WaterwayDescriptorService configService) {
        this.accountService = checkNotNull(accountService);
        this.signatureService = checkNotNull(signatureService);
        this.sysTemplateRepository = checkNotNull(sysTemplateRepository);
        this.processorService = checkNotNull(processorService);
        this.dao = checkNotNull(dao);
        this.configService = checkNotNull(configService);

        templates = cache.newHolder("email_templates_all");
        templatesByIdOrCode = cache.newCache("email_templates_by_id_or_code");

        busService.getDaoEventBus().register(new Object() {

            @Subscribe
            public void handleWaterwayConfigReloadEvent(WaterwayConfigReloadEvent event) {
                invalidateCache();
            }
        });
    }

    private void invalidateCache() {
        templates.invalidate();
        templatesByIdOrCode.invalidateAll();
    }

    @Override
    public List<EmailTemplate> getAll() {
        return templates.get(() -> {
            Map<String, EmailTemplate> templatesFromDb = uniqueIndex(dao.selectAll().from(EmailTemplate.class).orderBy(ATTR_CODE).asList(EmailTemplate.class), EmailTemplate::getCode),
                    templatesFromConfig = configService.getAllItems(WYCIT_NOTIFICATION).stream().map(this::templateFromConfigItem).collect(toMap(EmailTemplate::getCode, identity()));
            checkArgument(list(templatesFromDb.keySet()).with(templatesFromConfig.keySet()).duplicates().isEmpty(), "invalid email template configs, found duplicate keys = %s", list(templatesFromDb.keySet()).with(templatesFromConfig.keySet()).duplicates());
            return list(templatesFromDb.values()).with(templatesFromConfig.values()).sorted(EmailTemplate::getCode);
        });
    }

    private EmailTemplate templateFromConfigItem(WaterwayItem item) {
        return EmailTemplateImpl.builder()
                .withCode(item.getCode())
                .withTo(item.getConfig("to"))
                .withFrom(item.getConfig("from"))
                .withBcc(item.getConfig("bcc"))
                .withCc(item.getConfig("cc"))
                .withSubject(item.getConfig("subject"))
                .withContent(item.getConfig("content"))
                .withContentType(firstNotBlank(item.getConfig("contentType"), "application/octet-stream"))
                .withDelay(toLongOrNull(item.getConfig("delay")))
                .withDescription(item.getDescription())
                .withMeta(map(unflattenMap(item.getConfig(), "meta")))
                .withNotificationProvider(item.getConfig("provider"))
                .withReportCodes(toListOfStrings(item.getConfig("report")))
                .withAccount(Optional.ofNullable(item.getConfig("account")).map(StringUtils::trimToNull).map(accountService::getAccountByIdOrCode).map(EmailAccount::getId).orElse(null))
                .withSignature(Optional.ofNullable(item.getConfig("signature")).map(StringUtils::trimToNull).map(signatureService::getOne).map(EmailSignature::getId).orElse(null))
                .build();
    }

    @Override
    @Nullable
    public EmailTemplate getByIdOrNull(long id) {
        return templatesByIdOrCode.get(id, () -> getAll().stream().filter(t -> equal(id, t.getId())).collect(toOptional())).orElse(null);
    }

    @Override
    @Nullable
    public EmailTemplate getByNameOrNull(String name) {
        return templatesByIdOrCode.get(checkNotBlank(name), () -> getAll().stream().filter(t -> equal(name, t.getCode())).collect(toOptional())).orElse(null);
    }

    @Override
    public EmailTemplate createEmailTemplate(EmailTemplate emailTemplate) {
        checkArgument(getByNameOrNull(emailTemplate.getCode()) == null, "duplicate email template name =< %s >", emailTemplate.getCode());
        EmailTemplate createdEmailTemplate = dao.create(emailTemplate);
        invalidateCache();
        return createdEmailTemplate;
    }

    @Override
    public EmailTemplate updateEmailTemplate(EmailTemplate emailTemplate) {
        emailTemplate = dao.update(emailTemplate);
        invalidateCache();
        return emailTemplate;
    }

    @Override
    public void deleteEmailTemplate(long id) {
        dao.delete(EmailTemplate.class, id);
        invalidateCache();
    }

    @Override
    public TemplateBindings fetchTemplateBindings(EmailTemplate emailTemplate) {
        return processorService.fetchTemplateBindings(emailTemplate);
    }

    @Override
    public EmailTemplate getSystemTemplateOrNull(String sysTemplateId) {
        return sysTemplateRepository.getSystemTemplateOrNull(sysTemplateId);
    }

    @Override
    public EmailTemplate getTemplate(EmailTemplateInlineConfig config) {
        return EmailTemplateImpl.copyOf(getByName(config.getTemplate())).withId(null).accept(t -> {
            if (config.getDelay() != null) {
                t.withDelay(config.getDelay());
            }
            if (isNotBlank(config.getContent())) {
                t.withContent(config.getContent());
            }
            if (!config.getReportList().isEmpty()) {
                t.withReports(config.getReportList());//TODO validate reports (??)
            }
        }).build();
    }

}
