/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.template;

import com.google.common.annotations.VisibleForTesting;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.api.CmApiService;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.session.OperatorService;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.common.beans.LookupValue;
import org.cmdbuild.common.localization.LanguageService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.CardImpl.CardImplBuilder;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FILE;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FOREIGNKEY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUP;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUPARRAY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import org.cmdbuild.dao.postgres.q3.RefAttrHelperServiceExt;
import org.cmdbuild.easytemplate.EasytemplateService;
import org.cmdbuild.easytemplate.FtlTemplateService;
import org.cmdbuild.easytemplate.store.EasytemplateRepository;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAttachment;
import org.cmdbuild.email.EmailException;
import org.cmdbuild.email.EmailSignatureService;
import static org.cmdbuild.email.EmailStatus.ES_DRAFT;
import org.cmdbuild.email.beans.EmailAttachmentImpl;
import org.cmdbuild.email.beans.EmailImpl;
import static org.cmdbuild.email.template.EmailUtils.handleEmailSignatureForTemplate;
import static org.cmdbuild.email.utils.EmailMtaUtils.renameDuplicates;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.report.ReportConfig;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.services.serialization.SerializationPrefixMode;
import org.cmdbuild.services.serialization.attribute.file.CardAttributeFileBasicSerializer;
import org.cmdbuild.services.serialization.attribute.file.CardAttributeFileHelper;
import org.cmdbuild.services.serialization.attribute.file.CardAttributeFileSerializationData;
import org.cmdbuild.template.ExpressionInputData;
import org.cmdbuild.template.SimpleExpressionInputData;
import org.cmdbuild.template.TemplateBindings;
import org.cmdbuild.template.TemplateProcessorHandler;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.translation.TranslationUtils;
import static org.cmdbuild.utils.io.CmIoUtils.isContentType;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import org.cmdbuild.utils.lang.CmNullableUtils;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringInline;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * <ul>
 * <li>initializes the {@link TemplateProcessorHandler} with language detection
 * from email {@link EmailTemplate};
 * <li>fetches values from an email template with
 * {@link #fetchTemplateBindings(org.cmdbuild.email.EmailTemplate)}
 * <li>processes an email applying its {@link EmailTemplate} for each email's
 * parameter with
 * {@link #processEmail(org.cmdbuild.email.Email, org.cmdbuild.template.ExpressionInputData)};
 * <li>processes and expression with
 * {@link #processExpression(org.cmdbuild.email.Email, org.cmdbuild.template.SimpleExpressionInputData)}
 * and multiple expressions with
 * {@link #processMultipleExpressions(org.cmdbuild.email.Email, org.cmdbuild.template.SimpleExpressionInputData)}.
 * </ul>
 *
 * @author afelice
 */
@Component
public class EmailTemplateProcessorServiceImpl implements EmailTemplateProcessorService {

    private static final String LOOKUPARRAY_CONTEXT_REPRESENTATION_SEPARATOR = ", ";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EasytemplateRepository easytemplateRepository;
    private final EasytemplateService easytemplateService;
    private final UserRepository userRepository;
    private final OperationUserSupplier userSupplier;
    private final RoleRepository roleRepository;
    private final DaoService dao;
    private final FtlTemplateService ftlTemplateService;
    private final CmApiService apiService;
    private final RefAttrHelperServiceExt refAttrHelperService;
    private final LookupService lookupService;
    private final ReportService reportService;
    private final ObjectTranslationService translationService;
    private final EmailSignatureService signatureService;
    private final LanguageService languageService;
    private final DmsAttachmentDownloader dmsAttachmentDownloader;
    private final CardAttributeFileHelper cardAttributeFileHelper;
    private final OperatorService operatorService;

    public EmailTemplateProcessorServiceImpl(EasytemplateRepository easytemplateRepository, EasytemplateService easytemplateService,
            UserRepository userRepository, OperationUserSupplier userSupplier, RoleRepository roleRepository,
            DaoService dao,
            FtlTemplateService ftlTemplateService, CmApiService apiService, RefAttrHelperServiceExt refAttrHelperService, LookupService lookupService,
            ReportService reportService, ObjectTranslationService translationService, EmailSignatureService signatureService, LanguageService languageService,
            DmsAttachmentDownloader dmsAttachmentDownloader,
            CardAttributeFileHelper cardAttributeFileHelper, OperatorService operatorService) {
        this.easytemplateRepository = checkNotNull(easytemplateRepository);
        this.easytemplateService = checkNotNull(easytemplateService);
        this.userRepository = checkNotNull(userRepository);
        this.userSupplier = checkNotNull(userSupplier);
        this.roleRepository = checkNotNull(roleRepository);
        this.dao = checkNotNull(dao);
        this.ftlTemplateService = checkNotNull(ftlTemplateService);
        this.apiService = checkNotNull(apiService);
        this.refAttrHelperService = checkNotNull(refAttrHelperService);
        this.lookupService = checkNotNull(lookupService);
        this.reportService = checkNotNull(reportService);
        this.translationService = checkNotNull(translationService);
        this.signatureService = checkNotNull(signatureService);
        this.languageService = checkNotNull(languageService);
        this.dmsAttachmentDownloader = checkNotNull(dmsAttachmentDownloader);
        this.cardAttributeFileHelper = checkNotNull(cardAttributeFileHelper);
        this.operatorService = checkNotNull(operatorService);
    }

    /**
     * Language will be (eventually) detected from expression language in
     * template.
     *
     * @param template
     * @return
     */
    @Override
    public TemplateBindings fetchTemplateBindings(EmailTemplate template) {
        checkNotNull(template);
        return buildProcessor(ExpressionInputData.builder()
                .withTemplate(template)
                // with unforced language: language will be (eventually) detected from expression language in template
                .build())
                .fetchTemplateBindings();
    }

    /**
     * Builds an undetected language template processor. Invoke
     * {@link TemplateProcessorHandler#initWith(org.cmdbuild.template.ExpressionInputData)}
     * to detect language from given input (that may contain a decided elsewhere
     * language).
     *
     * <p>
     * Handler for test: processor mocking
     *
     * @return
     */
    TemplateProcessorHandler buildTemplateProcessor() {
        return new TemplateProcessorHandler(languageService, translationService, ftlTemplateService,
                easytemplateService, easytemplateRepository,
                userRepository, userSupplier, roleRepository, dao,
                apiService, logger, operatorService);
    }

    /**
     * Parse an Email/Email template.
     *
     * @param email if <code>null</code>, builds a <i>draft email</i>.
     * @param expressionInputData
     * @return
     */
    @Override
    public Email processEmail(@Nullable Email email, ExpressionInputData expressionInputData) {
        return buildProcessor(expressionInputData)
                .processEmail(email, expressionInputData);
    }

    /**
     * Process expression to obtain a value.
     *
     * @param simpleExprInputData
     * @return bound value; empty string if given expression is
     * <code>null</code> or empty.
     */
    @Override
    public String processExpression(SimpleExpressionInputData simpleExprInputData) {
        if (isBlank(simpleExprInputData.getExpression())) {
            return "";
        }

        EmailTemplateProcessor templateProcessor = buildProcessor(simpleExprInputData);
        return templateProcessor.processExpression(simpleExprInputData.getExpression());
    }

    /**
     * Process multiple expressions to obtain a set of values.
     *
     * @param simpleExprInputData
     * @return bound values; an empty string if given expression is
     * <code>null</code> or empty.
     */
    @Override
    public Map<String, Object> processMultipleExpressions(SimpleExpressionInputData simpleExprInputData) {
        checkNotNull(simpleExprInputData.getExpressions(), "processing multiple expressions, but invalid: found null");

        EmailTemplateProcessor templateProcessor = buildProcessor(simpleExprInputData);
        return templateProcessor.processMultipleExpressions(simpleExprInputData.getExpressions());
    }

    /**
     * <p>
     * Placeholder for tests
     *
     * @param expressionInputData
     * @return
     */
    protected EmailTemplateProcessor buildProcessor(ExpressionInputData expressionInputData) {
        return new EmailTemplateProcessor(expressionInputData);
    }

    @VisibleForTesting
    protected Card loadClientCardValues(Card clientCard, Card serverCard) {
        logger.debug("load card data: client= <{}>, server= <{}> ", clientCard, serverCard);

        if (clientCard == null) {
            return null;
        }

        Card result = clientCard;
        if (clientCard != serverCard) {
            // Client Card has different data from persisted one: recalculate

            logger.debug("load client card data (lookup/references id and descriptions)");
            Classe type = clientCard.getType();
            CardImplBuilder clientCardBuilder = CardImpl.copyOf(clientCard);

            Map<String, Object> otherAttrValues = clientCard.getAllValuesAsMap().entrySet().stream().map(e -> { //TODO move this to another service
                Object value = e.getValue();
                String attrKey = e.getKey();
                if (isReference(type, attrKey) && hasId(value)) {
                    Attribute attribute = type.getAttribute(attrKey);
                    value = (IdAndDescription) switch (attribute.getType().getName()) {
                        case REFERENCE, FOREIGNKEY ->
                            refAttrHelperService.getReferencedCard(attribute, value);
                        case LOOKUP ->
                            lookupService.getLookup(((IdAndDescription) value).getId());
                        default ->
                            value;
                    };
                }
                return Pair.of(e.getKey(), value);
            }).collect(toMap(Pair::getKey, Pair::getValue));

            clientCardBuilder.withAttributes(otherAttrValues);

            result = clientCardBuilder.build();
        } // end if

        Classe type = clientCard.getType();

        // Add other (complex) reference data types
        Map<String, String> aliasMap = type.getAttributeToAliasMap();
        logger.debug("load client card values - alias names: {}", aliasMap);
        Map<String, Object> allAttrValues = clientCard.getAllValuesAsMap();
        // Handle LookupArray
        FluentMap<String, Object> clientLookupArrValues = handleLookupArrayAttrValues(type, aliasMap, allAttrValues);
        // Handle FILE
        FluentMap<String, Object> clientFileValues = handleFileAttrValues(type, aliasMap, clientCard);

        Card resultCard = CardImpl.copyOf(result).withAttributes(clientLookupArrValues.with(clientFileValues)).build();
        logger.debug("client card obtained (loaded and calculated) values: {}", resultCard.getAllValuesAsMap());
        return resultCard;
    }

    private FluentMap<String, Object> handleLookupArrayAttrValues(Classe type, Map<String, String> aliasMap, Map<String, Object> allAttrValues) {
        FluentMap<String, Object> clientLookupArrValues = map();
        type.getAllAttributes().stream().filter(a -> a.isOfType(LOOKUPARRAY) && CmNullableUtils.isNotBlank(allAttrValues.get(a.getName()))).forEach(a -> {
            // Mimic .mapKeys(type.getAliasToAttributeMap()::get) and type.getAttributeOrNull(key) done in CardImpl constructor
            String name = firstNotNull(aliasMap.get(a.getName()), a.getName());
            logger.debug("load client card values - found lookupArray attribute name= <{}>", name);
            List<LookupValue> toUseValues = (List<LookupValue>) allAttrValues.get(name);
            logger.debug("load client card values - found lookupArray attribute value= <{}>", toUseValues);

            // Load full Lookup value
            List<LookupValue> toUseLookups = toUseValues.stream().map(v -> lookupService.getLookup(v.getId())).collect(toList());
            String idArrayRepresentation = toFormattedArrayValues(toUseLookups.stream().map(l -> l.getId().toString()).collect(toList()));
            logger.debug("load client card values - calculated lookupArray attribute id representation= <{}>", idArrayRepresentation);
            clientLookupArrValues.put(format("%s", name), idArrayRepresentation); // default representation is list of ids
            clientLookupArrValues.put(format("%s.id", name), idArrayRepresentation);
            clientLookupArrValues.put(format("%s.Id", name), idArrayRepresentation); // For legacy
            final String codeArrayRepresentation = toFormattedArrayValues(toUseLookups.stream().map(LookupValue::getCode).collect(toList()));
            logger.debug("load client card values - calculated lookupArray attribute code representation= <{}>", codeArrayRepresentation);
            clientLookupArrValues.put(format("%s.code", name), codeArrayRepresentation);
            clientLookupArrValues.put(format("%s.Code", name), codeArrayRepresentation); // For legacy
            final String descriptionArrayRepresentation = toFormattedArrayValues(toUseLookups.stream().map(LookupValue::getDescription).collect(toList()));
            logger.debug("load client card values - calculated lookupArray attribute description representation= <{}>", codeArrayRepresentation);
            clientLookupArrValues.put(format("%s.description", name), descriptionArrayRepresentation);
            clientLookupArrValues.put(format("%s.Description", name), descriptionArrayRepresentation); // For legacy
            final String descriptionTranslationArrayRepresentation = toFormattedArrayValues(toUseLookups.stream().map( l ->
                    translationService.translateLookupDescription(l.getLookupType(), l.getCode(), l.getDescription())
            ).collect(toList()));
            clientLookupArrValues.put(format("%s._description_translation", name), descriptionTranslationArrayRepresentation);

            logger.debug("load client card values - LOOKUPARRAY {} - added values: {}", name, clientLookupArrValues);
        });
        logger.debug("load client card values - added values: {}", clientLookupArrValues);
        return clientLookupArrValues;
    }

    private FluentMap<String, Object> handleFileAttrValues(Classe type, Map<String, String> aliasMap, Card clientCard) {
        FluentMap<String, Object> clientFileValues = map();
        type.getAllAttributes().stream().filter(a -> a.isOfType(FILE) && CmNullableUtils.isNotBlank(clientCard.get(a.getName()))).forEach(a -> {
            // Mimic .mapKeys(type.getAliasToAttributeMap()::get) and type.getAttributeOrNull(key) done in CardImpl constructor
            String name = firstNotNull(aliasMap.get(a.getName()), a.getName());

            // Fetch data
            CardAttributeFileSerializationData serializationData = cardAttributeFileHelper.fetchDocument(name, a.getOwnerName(), clientCard);
            cardAttributeFileHelper.fetchCategory(serializationData);
            // Serialize as (<FileAttributeName>.<propertyName>, String)
            serializationData.setPrefixMode(SerializationPrefixMode.SPM_JSON);
            clientFileValues.putAll(cardAttributeFileHelper.serialize(serializationData, new CardAttributeFileBasicSerializer()));

            logger.debug("load client card values - FILE {} - added values: {}", name, clientFileValues);
        });
        logger.debug("load client card values - added values: {}", clientFileValues);

        return clientFileValues;
    }

    private boolean hasId(Object value) {
        if (value != null && value instanceof IdAndDescription idAndDescription) {
            return idAndDescription.hasId();
        } else {
            return false;
        }
    }

    private boolean isReference(Classe type, String attrKey) {
        return type.hasAttribute(attrKey) && type.getAttribute(attrKey).isOfType(REFERENCE, FOREIGNKEY, LOOKUP, LOOKUPARRAY, FILE);
    }

    private String toFormattedArrayValues(List values) {
        return String.join(LOOKUPARRAY_CONTEXT_REPRESENTATION_SEPARATOR, values);
    }

    /**
     * <ol>
     * <li>initializes the {@link TemplateProcessorHandler} with language
     * detection from email {@link EmailTemplate};
     * <li>process and email applying its {@link EmailTemplate} for each email's
     * parameter.
     * </ol>
     *
     * <p>
     * Note: it's an inner class to wrap:
     * <ol>
     * <li>loading client card values from repository;
     * <li>the creation and use of a {@link TemplateProcessorHandler};
     * <li>handling of email attachments.
     * </ol>
     */
    @VisibleForTesting
    class EmailTemplateProcessor {

        private final ExpressionInputData expressionInputData;
        private final TemplateProcessorHandler templateProcessorHandler;

        /**
         *
         * @param expressionInputData
         */
        EmailTemplateProcessor(ExpressionInputData expressionInputData) {
            Card valorizedClientCard = loadClientCardValues(expressionInputData.getClientCard(), expressionInputData.getServerCard());
            if (valorizedClientCard != null) {
                this.expressionInputData = ExpressionInputData.copyOf(expressionInputData)
                        .withClientCard(valorizedClientCard)
                        .build();
            } else {
                this.expressionInputData = expressionInputData;
            }

            logger.trace("client (new) card data for notification template = \n\n{}\n", mapToLoggableStringLazy(this.expressionInputData.getClientData()));
            logger.trace("server (current) card data for notification template = \n\n{}\n", mapToLoggableStringLazy(this.expressionInputData.getServerData()));
            logger.trace("other data for notification template = \n\n{}\n", mapToLoggableStringLazy(this.expressionInputData.getOtherData()));

            // Detect language from input data
            templateProcessorHandler = buildTemplateProcessor(this.expressionInputData);
        }

        /**
         * Builds a (detected/Fetched) language template processor.
         *
         * <p>
         * Handler for test: processor mocking
         *
         * @return
         */
        TemplateProcessorHandler buildTemplateProcessor(ExpressionInputData expressionInputData) {
            // Undetected language template processor
            TemplateProcessorHandler curTemplateProcessorHandler = EmailTemplateProcessorServiceImpl.this.buildTemplateProcessor();

            // Detect language from template/forced language
            curTemplateProcessorHandler.initWith(expressionInputData);

            return curTemplateProcessorHandler;
        }

        TemplateBindings fetchTemplateBindings() {
            return templateProcessorHandler.fetchTemplateBindings();
        }

        /**
         *
         * @param email
         * @param expressionInputData
         * @return build email using template/card/data bindings; if given
         * <code>email</code> is <code>null</code>, returns a <i>draft
         * email</i>.
         */
        Email processEmail(@Nullable Email email, ExpressionInputData expressionInputData) {
            checkNotNull(expressionInputData.getTemplate(), "processing an email template, but missing the template");

            if (email == null) {
                email = EmailImpl.builder().withStatus(ES_DRAFT).build();
            }

            // process template to create the email to send
            // language will be determined by template
            final EmailTemplate template = expressionInputData.getTemplate();
            logger.debug("processing email = {} with template = {}", email, template);

            // Create email to send with processed values
            final Email toSendEmail = EmailImpl.copyOf(email)
                    .accept((builder) -> {
                        processEmailTemplate(builder::withFrom, template.getFrom());
                        processEmailTemplate(builder::withBcc, template.getBcc());
                        processEmailTemplate(b -> {
                            if (template.hasSignature() && isContentType(template.getContentType(), "text/html")) {
                                b = handleEmailSignatureForTemplate(b, signatureService.getSignatureHtmlForCurrentUser(template.getSignature()));
                            }
                            builder.withContent(b);
                        }, translateBodyTemplateValue(template.getContent()), template.getContentType());
                        processEmailTemplate(builder::withCc, template.getCc());
                        processEmailTemplate(builder::withSubject, translateSubjectTemplateValue(template.getSubject()));
                        processEmailTemplate(builder::withTo, template.getTo());
                    })
                    .withAccount(template.getAccount())
                    .withContentType(template.getContentType())
                    .withDelay(template.getDelay())
                    .withKeepSynchronization(template.getKeepSynchronization())
                    .withPromptSynchronization(template.getPromptSynchronization())
                    .withSignature(template.getSignature())
                    .withNotificationProvider(template.getNotificationProvider())
                    .withTemplate(template.getId())
                    .accept(e -> {
                        Map<String, String> meta = map(template.getMeta());
                        meta.computeIfPresent("actionLabel", (k, v) -> translateActionLabelTemplateValue(v));
                        e.withMeta(mapOf(String.class, String.class).with(processMultipleExpressions(meta)));
                        if (template.hasReports()) {
                            e.addAttachments(executeReportsToAttachments());
                        }
                        if (template.hasUploadAttachments()) {
                            logger.debug("adding upload attachments for filter =< {} > defined in template =< {} >", template.getUploadAttachmentsFilter(), template);
                            final List<EmailAttachment> toUploadAttachments = executeUploadAttachmentsToAttachments();
                            e.addAttachments(toUploadAttachments);
                            logger.debug("added {} to upload attachments", toUploadAttachments.size());
                        }
                    }).build();

            return toSendEmail;
        }

        /**
         *
         * @param expression
         * @return bound value, empty string if <code>expression</code> is
         * blank.
         */
        String processExpression(String expression) {
            return templateProcessorHandler.processExpression(expression);
        }

        /**
         *
         * @param exprs
         * @return bound value, empty string for blank expressions.
         */
        Map<String, Object> processMultipleExpressions(Map<String, ?> exprs) {
            return templateProcessorHandler.processMultipleExpressions(exprs);
        }

        /**
         *
         * @param value
         *
         * @return subject translation for given value; empty string if
         * <code>value</code> is blank.
         */
        private String translateSubjectTemplateValue(String value) {
            return translateTemplateValue(value, TranslationUtils::notificationTemplateSubjectTranslationCode);
        }

        /**
         *
         * @param value
         * @return body translation for given value; empty string if
         * <code>value</code> is blank.
         */
        private String translateBodyTemplateValue(String value) {
            return translateTemplateValue(value, TranslationUtils::notificationTemplateBodyTranslationCode);
        }

        /**
         *
         * @param value
         * @return actionLabel translation for given value; empty string if
         * <code>value</code> is blank.
         */
        private String translateActionLabelTemplateValue(String value) {
            return translateTemplateValue(value, TranslationUtils::notificationTemplateActionLabelTranslationCode);
        }

        /**
         *
         * @param value
         * @return translation for given value using translationCode
         */
        private String translateTemplateValue(String value, Function<String, String> fun) {
            if (isNotBlank(value)) {
                return templateProcessorHandler.translateValue(expressionInputData.getTemplate(), value, fun.apply(expressionInputData.getTemplate().getCode()));
            }
            return EMPTY;
        }

        /**
         * Applies bound value from expression, if given <code>expression</code>
         * is not blank (not null nor empty).
         *
         * @param setter
         * @param expression
         */
        private void processEmailTemplate(Consumer<String> setter, @Nullable String expression) {
            processEmailTemplate(setter, expression, null);
        }

        /**
         * Applies bound value from expression, if given <code>expression</code>
         * is not blank (not null nor empty).
         *
         * @param setter
         * @param expression
         * @param contentType
         */
        private void processEmailTemplate(Consumer<String> setter, @Nullable String expression, @Nullable String contentType) {
            if (isBlank(expression)) {
                // do nothing
            } else {
                logger.debug("processing email template expr =< {} >", expression);
                // returns empty string if given expression is blank (null or empty)
                String value = templateProcessorHandler.processTemplateValue(expression, contentType);
                setter.accept(value);
            }
        }

        private List<EmailAttachment> executeReportsToAttachments() {
            List<EmailAttachment> result = expressionInputData.getTemplate().getReports().stream().map(this::executeReportToAttachment).collect(toUnmodifiableList());

            // Different attachments have to have different unique names
            return renameDuplicates(result);
        }

        private EmailAttachment executeReportToAttachment(ReportConfig config) {
            try {
                logger.debug("execute report = {} template for email attachment", config);
                Map<String, Object> params = templateProcessorHandler.processMultipleExpressions(config.getParams());
                logger.debug("build report = {} with template params = {}", config, mapToLoggableStringInline(params));

                DataHandler dataHandler = reportService.executeReportAndDownload(config.getCode(), config.getFormat(), params);
                EmailAttachment attachment = buildEmailAttachment(dataHandler);
                logger.debug("build email attachment = {}", attachment);
                return attachment;
            } catch (Exception ex) {
                throw new EmailException(ex, "error processing report = %s for email attachment", config);
            }
        }

        private EmailAttachmentImpl buildEmailAttachment(DataHandler dataHandler) {
            return EmailAttachmentImpl.build(toDataSource(dataHandler));
        }

        private List<EmailAttachment> executeUploadAttachmentsToAttachments() {
            return list(dmsAttachmentDownloader.downloadAttachments(
                    expressionInputData.getClientCard(),
                    expressionInputData.getTemplate().getUploadAttachmentsFilter()))
                    .map(this::buildEmailAttachment);
        }

    } // end EmailTemplateProcessor inner class

} // end EmailTemplateProcessorServiceImpl class
