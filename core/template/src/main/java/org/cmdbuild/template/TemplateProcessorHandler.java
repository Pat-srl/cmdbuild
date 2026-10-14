/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package org.cmdbuild.template;

import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.google.common.annotations.VisibleForTesting;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Maps.transformValues;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import static java.util.Objects.isNull;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.joining;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.apache.commons.lang3.math.NumberUtils;
import org.cmdbuild.api.CmApiService;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.session.OperatorService;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.common.localization.LanguageService;
import static org.cmdbuild.cql.CqlUtils.getCqlSelectElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.data.filter.beans.CqlFilterImpl;
import org.cmdbuild.easytemplate.EasytemplateProcessor;
import org.cmdbuild.easytemplate.EasytemplateProcessor.ExprProcessingMode;

import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.easytemplate.EasytemplateProcessor.ExprProcessingMode.EPM_DEFAULT;
import static org.cmdbuild.easytemplate.EasytemplateProcessor.ExprProcessingMode.EPM_JAVASCRIPT;
import org.cmdbuild.easytemplate.EasytemplateProcessorImpl;
import org.cmdbuild.easytemplate.EasytemplateService;
import org.cmdbuild.easytemplate.FtlTemplateService;
import org.cmdbuild.easytemplate.FtlTemplateService.FtlTemplateMode;
import static org.cmdbuild.easytemplate.FtlTemplateService.FtlTemplateMode.FTM_AUTO;
import static org.cmdbuild.easytemplate.FtlTemplateService.FtlTemplateMode.FTM_HTML;
import org.cmdbuild.easytemplate.FtlUtils;
import org.cmdbuild.easytemplate.TemplateResolver;
import org.cmdbuild.easytemplate.TemplateResolverImpl;
import org.cmdbuild.easytemplate.store.EasytemplateRepository;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailProcessingUtils;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toUserReadableDateTime;
import static org.cmdbuild.utils.io.CmIoUtils.isContentType;
import static org.cmdbuild.utils.json.CmJsonUtils.prettifyIfJsonLazy;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmMapUtils.emptyToNull;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>Warning</b>: <b>can't be used as a <code>@component</code></b> due to
 * {@link #initWith(org.cmdbuild.template.ExpressionInputData)} * and due to
 * {@link #clientlogger}:
 * <ol>
 * <li>an instance is created;
 * <li>the instance is initialized invoking
 * {@link #initWith(org.cmdbuild.template.ExpressionInputData)};
 * <li>if not forced in input data, the language to use is detected invoking
 * {@link #fetchLanguage(org.cmdbuild.template.ExpressionInputData)};
 * <li>now a template keys can be processed to obtain a related dynamic value,
 * invoking {@link #processTemplateValue(java.lang.String, java.lang.String)}.
 * </ol>
 *
 * @author afelice
 *
 *
 */
public class TemplateProcessorHandler {

    private final Logger clientLogger;

    private final LanguageService languageService;
    private final ObjectTranslationService translationService;
    private final FtlTemplateService ftlTemplateService;
    private final EasytemplateService easytemplateService;
    private final EasytemplateRepository easytemplateRepository;
    private final UserRepository userRepository;
    private final OperationUserSupplier userSupplier;
    private final RoleRepository roleRepository;
    private final DaoService dao;
    private final CmApiService apiService;
    private final OperatorService operatorService;

    private final JsContextBuilder jsContextBuilder_noLanguage;

    private ExpressionProcessor curExpressionProcessor;

    protected String languageToUse;

    /**
     * Initialized with all stuff:
     * <ul>
     * <li>clientCard data;
     * <li>serverCard data;
     * <li>serialization of Email data;
     * <li>other data;
     * <li>language;
     * <li>{@link UtilsCmApi};
     * <li>{@link Logger}.
     * </ul>
     */
    protected Map<String, Object> ftlTemplateData;

    /**
     * Processor to fetch language from an email template
     *
     * @param languageService
     * @param translationService
     * @param ftlTemplateService
     * @param easytemplateService
     * @param easytemplateRepository
     * @param userRepository
     * @param userSupplier
     * @param roleRepository
     * @param dao
     * @param apiService
     * @param clientLogger logger created by template processor client.
     */
    public TemplateProcessorHandler(LanguageService languageService, ObjectTranslationService translationService,
                                    FtlTemplateService ftlTemplateService, EasytemplateService easytemplateService, EasytemplateRepository easytemplateRepository,
                                    UserRepository userRepository, OperationUserSupplier userSupplier, RoleRepository roleRepository, DaoService dao,
                                    CmApiService apiService,
                                    Logger clientLogger, OperatorService operatorService) {
        this.languageService = checkNotNull(languageService);
        this.translationService = checkNotNull(translationService);
        this.ftlTemplateService = checkNotNull(ftlTemplateService);
        this.easytemplateService = checkNotNull(easytemplateService);
        this.easytemplateRepository = checkNotNull(easytemplateRepository);
        this.userRepository = checkNotNull(userRepository);
        this.userSupplier = checkNotNull(userSupplier);
        this.roleRepository = checkNotNull(roleRepository);
        this.dao = checkNotNull(dao);
        this.apiService = checkNotNull(apiService);
        this.clientLogger = checkNotNull(clientLogger);
        this.operatorService = operatorService;

        /**
         * Reused to fetch any language
         */
        this.jsContextBuilder_noLanguage = new JsContextBuilder(translationService);
    }

    /**
     * Example of how to initialize this {@link TemplateProcessorHandler}
     *
     * @param languageService
     * @param translationService
     * @param ftlTemplateService
     * @param easytemplateService
     * @param easytemplateRepository
     * @param userRepository
     * @param userSupplier
     * @param roleRepository
     * @param dao
     * @param apiService
     * @param clientLogger
     * @param expressionInputData
     * @return
     */
    static public TemplateProcessorHandler build(LanguageService languageService, ObjectTranslationService translationService,
            FtlTemplateService ftlTemplateService, EasytemplateService easytemplateService, EasytemplateRepository easytemplateRepository,
            UserRepository userRepository, OperationUserSupplier userSupplier, RoleRepository roleRepository, DaoService dao,
            CmApiService apiService,
            Logger clientLogger, OperatorService operatorService,
            ExpressionInputData expressionInputData) {
        TemplateProcessorHandler handler = new TemplateProcessorHandler(languageService, translationService,
                ftlTemplateService, easytemplateService, easytemplateRepository,
                userRepository, userSupplier, roleRepository, dao,
                apiService,
                clientLogger, operatorService);

        // Detect language from template/forced language
        handler.initWith(expressionInputData);

        return handler;
    }

    /**
     * Initializes the processor for an email template when language is known.
     *
     * <p>
     * Initializes FtlTemplateData used with all stuff:
     * <ul>
     * <li> clientCard data;
     * <li> serverCard data;
     * <li> serialization of Email data;
     * <li>other data;
     * <li>language;
     * <li>{@link UtilsCmApi};
     * <li>{@link Logger}.
     * </ul>
     *
     * @param expressionInputData <b>Attention</b>: modified by current method;
     * (eventually) contains language to use decided elsewhere.
     */
    public void initWith(ExpressionInputData expressionInputData) {
        // Detect language, if not forced in input data
        languageToUse = isNotBlank(expressionInputData.getForcedLanguage()) ? expressionInputData.getForcedLanguage() : fetchLanguage(expressionInputData);
        clientLogger.debug("template language =< {} >", languageToUse);

        clientLogger.trace("client (new) card data for notification template = \n\n{}\n", mapToLoggableStringLazy(expressionInputData.getClientData()));
        clientLogger.trace("server (current) card data for notification template = \n\n{}\n", mapToLoggableStringLazy(expressionInputData.getServerData()));
        clientLogger.trace("other data for notification template = \n\n{}\n", mapToLoggableStringLazy(expressionInputData.getOtherData()));

        JsContextBuilder jsContextBuilder = new JsContextBuilder(translationService, languageToUse);

        if (expressionInputData.getClientCard() != null && expressionInputData.getServerCard() == null) {
            // Is used if in template there is placeholder {server:Xyz}
            expressionInputData = ExpressionInputData.copyOf(expressionInputData)
                    .withServerCard(expressionInputData.getClientCard())
                    .build();
        }

        String jsContext = jsContextBuilder.buildContext(expressionInputData.getClientCard(), expressionInputData.getServerCard());

        clientLogger.trace("js context for notification template = \n\n{}\n", prettifyIfJsonLazy(jsContext));

        clientLogger.trace("template context data = \n\n{}\n", expressionInputData.getTemplateContextData());

        // Init FtlTemplateData with all stuff:
        // - clientCard data;
        // - serverCard data;
        // - serialization of Email data;
        // - other data;
        // - language;
        // - cmApi;
        // - logger.
        ftlTemplateData = buildFtlTemplateData(expressionInputData, languageToUse, apiService.getCmApi());

        this.curExpressionProcessor = buildExpressionProcessor(TemplateExpressionInputData.buildFrom(expressionInputData).withFtlTemplateData(ftlTemplateData).build(), jsContext);
    }

    /**
     * <p>
     * Handler for test: processor mocking
     *
     * @param expressionInputData
     * @param jsContext
     * @return
     */
    @VisibleForTesting
    ExpressionProcessor buildExpressionProcessor(TemplateExpressionInputData expressionInputData, String jsContext) {
        return new ExpressionProcessor(expressionInputData, jsContext, languageToUse,
                languageService, translationService, easytemplateService, easytemplateRepository,
                userRepository, userSupplier, roleRepository, dao, operatorService
        );
    }

    /**
     * Was in <code>EmailTemplateProcessor.getLanguage()</code>
     *
     * <p>
     * Detects language processing the language expression contained in
     * (eventually) given template (if any).
     *
     * @param expressionInputData with
     * <ul>
     * <li>template
     * <li>mapperConfig
     * <li>clientCard
     * <li>serverCard
     * </ul>
     * @return detected language from template language expression;
     * <code>null</code> if none detected.
     */
    public String fetchLanguage(ExpressionInputData expressionInputData) {

        String jsContext = jsContextBuilder_noLanguage.buildContext(expressionInputData.getClientCard(), expressionInputData.getServerCard());
        clientLogger.trace("js context for notification template = \n\n{}\n", prettifyIfJsonLazy(jsContext));

        ExpressionProcessor processor_noLanguage = buildExpressionProcessor(TemplateExpressionInputData.buildFrom(expressionInputData).build(), jsContext);

        if (expressionInputData.getTemplate() != null && expressionInputData.getTemplate().hasLangExpr()) { // Template may be null when processing expressions
            if (ftlTemplateData == null) { // build temporary ftlTemplateData if null, useful to calculate language
                ftlTemplateData = buildFtlTemplateData(expressionInputData, languageToUse, apiService.getCmApi());
            }
            return trimToNull(processTemplateValue(processor_noLanguage, expressionInputData.getTemplate().getLangExpr(), null));
        } else {
            return null;
        }
    }

    public String getLanguageToUse() {
        return languageToUse;
    }

    /**
     * Fetches bound values for given expressions.
     *
     * @param expressions
     * @return binds to an empty string for <code>expression</code> that are
     * blank.
     */
    public Map<String, Object> processMultipleExpressions(Map<String, ?> expressions) {
        if (isNull(expressions)) {
            return emptyMap();
        }

        return map(extractStrings(map(expressions))).mapValues(this::processExpression);
    }

    /**
     * Fetches bound value for given expression.
     *
     * @param expression
     * @return empty string if <code>expression</code> is blank.
     */
    public String processExpression(String expression) {
        if (isBlank(expression)) {
            return "";
        }

        clientLogger.debug("processing template expr =< {} >", expression);
        return processTemplateValue(expression, null);
    }

    /**
     * Handle Ftl template with <code>HTML</code> {@link FtlTemplateMode} from
     * contentType (if given) or with current expression processor (with
     * detected language).
     *
     * {@link #initWith(org.cmdbuild.template.ExpressionInputData)} must be
     * invoked before this, to:
     * <ol> <li> set input data to use for template binding;
     * <li> detect the language to use.
     * </ol>
     *
     * <p>
     *
     * Was in <code>EmailTemplateProcessor.processEmailTemplateValue()</code>,
     * for case with language already detected.
     *
     * @param expression
     * @param contentType
     * @return empty string if <code>expression</code> is blank
     * (<code>null</code> or empty), as done in
     * {@link FtlUtils#prepareFtlTemplateFixHeaderIfRequired()}
     */
    public String processTemplateValue(String expression, @Nullable String contentType) {
        if (isBlank(expression)) {
            return "";
        }

        checkNotNull(curExpressionProcessor, this.getClass().getName() + ".initWith() must be invoked before processTemplateValue(expression)");

        return processTemplateValue(curExpressionProcessor, expression, contentType);
    }

    /**
     * Was in <code>EmailTemplateProcessorServiceImpl.getBindings()</code>
     *
     * @return
     */
    public TemplateBindings fetchTemplateBindings() {
        checkNotNull(curExpressionProcessor, this.getClass().getName() + ".initWith() must be invoked before fetchBindings()");

        return curExpressionProcessor.fetchTemplateBindings();
    }

    /**
     *
     * @param template
     * @param value
     * @param code
     * @return empty string if <code>value</code> is blank.
     */
    @Nullable
    public String translateValue(EmailTemplate template, String value, String code) {
        if (isBlank(value)) {
            return EMPTY;
        }

        final String curLanguage = curExpressionProcessor.getLanguage();
        if (template != null && isNotBlank(curLanguage)) {
            value = translationService.translateByLangAndCode(curLanguage, code, value);
            clientLogger.trace("translate email {} as =< {} >", code, value);
        }
        return value;
    }

    /**
     * Invoked from constructor.
     *
     * <p>
     * Was in {@link EmailTemplateProcessor} constructor.
     *
     * <p>
     * initializes the ftl template data with
     * <ul>
     * <li>client card data;
     * <li>server card data;
     * <li>serialization of Email data;
     * <li>other data;
     * <li>language;
     * <li>{@link UtilsCmApi};
     * <li>{@link Logger}.
     * </ul>
     *
     * <b>Beware</b>: depends on {@link #clientCard} and {@link #serverCard}
     *
     * @param language detected language
     * @param extendedCmApi <b>Warning</b> this is coded as <code>Object</code>
     * as is in {@link CmApiService} to avoid dependence on middle level module
     * <code>api-model</code>, but its truly a <code>ExtendedApi</code>. To be
     * confirmed.
     * @return the builder
     */
    private Map<String, Object> buildFtlTemplateData(ExpressionInputData expressionInputData, String language, Object extendedCmApi) {
        Map<String, Object> allCardData = map();
        if (expressionInputData.getClientData() != null) {
            allCardData = firstNotNull(emptyToNull(expressionInputData.getServerData()), expressionInputData.getClientData());
        }
        allCardData = map(allCardData).with("_client", expressionInputData.getClientData(), "_new", expressionInputData.getClientData(), "_server", expressionInputData.getServerData(), "_old", expressionInputData.getServerData(), "_email", serializeEmailForTemplate(expressionInputData.getReceivedEmail()));
        Map<String, Object> allData = map(allCardData).with(expressionInputData.getOtherData());

        return map(allData)
                .with("card", allCardData,
                        "data", allData,
                        "email", serializeEmailForTemplate(expressionInputData.getReceivedEmail()),
                        "lang", language,
                        "cmdb", extendedCmApi,
                        "logger", LoggerFactory.getLogger(
                                format("%s.TEMPLATE.%s", getClass().getName(), Optional.ofNullable(expressionInputData.getTemplate()).map(EmailTemplate::getCode).orElse("NOTEMPLATE"))
                        )
                );
    }

    /**
     * Was in <code>EmailTemplateProcessor.processEmailTemplateValue()</code>.
     *
     * <p>
     * If language was detected, apply it in (a similar-session) in
     * {@link LanguageService}, then process:
     * <ul>
     * <li>a Ftl template;
     * <li> an expression.
     * </ol>
     *
     * @return empty string if <code>expression</code> is blank
     * (<code>null</code> or empty), as done in
     * {@link FtlUtils#prepareFtlTemplateFixHeaderIfRequired()}
     */
    private String processTemplateValue(ExpressionProcessor processor, String expression, @Nullable String contentType) {
        if (isBlank(expression)) {
            return "";
        }

        String language = null;
        if (processor.getLanguage() != null) {
            language = processor.getLanguage();
        }

        clientLogger.trace("process template (lang:{}) expr =< {} >", language, abbreviate(expression));
        String result;
        if (language != null) {
            languageService.setContextLanguage(language);
        }
        try {
            if (ftlTemplateService.isFtlTemplate(expression)) {
                FtlTemplateMode ftlTemplateMode = isContentType(contentType, "text/html") ? FTM_HTML : FTM_AUTO;//TODO improve this ??

                result = ftlTemplateService.executeFtlTemplate(expression, ftlTemplateMode, ftlTemplateData);
            } else {
                result = processor.processExpression(expression);
            }
        } finally {
            if (language != null) {
                languageService.resetContextLanguage();
            }
        }
        clientLogger.trace("processed template (lang:{}) expr =< {} >, output value =< {} >", language, abbreviate(expression), abbreviate(result));

        return result;
    }

    /**
     * Was in {@link EmailTemplateProcessor}
     *
     * @param email
     * @return
     */
    @Nullable
    private Map<String, Object> serializeEmailForTemplate(@Nullable Email email) {
        return email == null ? null : map(
                "_id", email.getId(),
                "from", email.getFrom(),
                "replyTo", email.getReplyTo(),
                "to", email.getTo(),
                "cc", email.getCc(),
                "bcc", email.getBcc(),
                "subject", email.getSubject(),
                "body", email.getContent(),
                "contentType", email.getContentType(),
                "_content_plain", email.getContentPlaintext(),
                "_content_html", email.getContentHtml(),
                "date", toIsoDateTime(email.getDate()),
                "card", email.getReference()
        );
    }

    private Map<String, String> extractStrings(Map<String, Object> orig) {
        return orig.entrySet().stream().filter(entry -> entry.getValue() instanceof String).collect(Collectors.toMap(Map.Entry::getKey, e -> (String) e.getValue()));
    }

} // end TemplateProcessorHandler class

/**
 * Wrapper for {@link EasytemplateProcessor} to handle:
 * <ul>
 * <li>translations;
 * <li>card, attribute and cql expression resolvers
 * </ol>
 *
 * @author afelice
 */
class ExpressionProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LanguageService languageService;
    private final ObjectTranslationService translationService;
    private final EasytemplateService easytemplateService;
    private final EasytemplateRepository easytemplateRepository;
    private final UserRepository userRepository;
    private final OperationUserSupplier userSupplier;
    private final RoleRepository roleRepository;
    private final DaoService dao;
    private final OperatorService operatorService;

    EasytemplateProcessor easyProcessor;

    TemplateExpressionInputData expressionInputData;
    String jsContext;
    String language;

    ExpressionProcessor(TemplateExpressionInputData expressionInputData, String jsContext, @Nullable String language,
            LanguageService languageService, ObjectTranslationService translationService, EasytemplateService easytemplateService, EasytemplateRepository easytemplateRepository,
            UserRepository userRepository, OperationUserSupplier userSupplier, RoleRepository roleRepository, DaoService dao, OperatorService operatorService) {
        this.expressionInputData = expressionInputData;
        this.jsContext = jsContext;
        this.language = language;

        this.languageService = checkNotNull(languageService);
        this.translationService = checkNotNull(translationService);
        this.easytemplateService = checkNotNull(easytemplateService);
        this.easytemplateRepository = checkNotNull(easytemplateRepository);
        this.userRepository = checkNotNull(userRepository);
        this.userSupplier = checkNotNull(userSupplier);
        this.roleRepository = checkNotNull(roleRepository);
        this.dao = checkNotNull(dao);
        this.operatorService = checkNotNull(operatorService);
        easyProcessor = buildEasytemplateProcessor(language, expressionInputData);
    }

    /**
     * (Possibly) detected language.
     *
     * @return <code>null</code> if no language detected.
     */
    @Nullable
    String getLanguage() {
        return language;
    }

    /**
     * Processes expression.
     *
     * @param expression
     * @return empty string if <code>expression</code> is blank.
     */
    String processExpression(String expression) {
        return processExpression(expression, EPM_DEFAULT);
    }

    /**
     * Processes expression, with given processing mode.
     *
     * @param expression
     * @param mode
     * @return empty string if <code>expression</code> is blank.
     */
    String processExpression(String expression, ExprProcessingMode mode) {
        if (isBlank(expression)) {
            return "";
        }

        return easyProcessor.processExpression(expression, mode);
    }

    /**
     * Processes template to extract all contained bindings
     *
     * <p>
     * Was in
     * <code>EmailTemplateProcessorServiceImpl.doProcessForBindings()</code>
     */
    TemplateBindings fetchTemplateBindings() {
        EmailTemplate template = expressionInputData.getTemplate();
        checkNotNull(template, "email template is null");

        List<String> clientBindings = list(), serverBindings = list();

        EasytemplateProcessor spyProcessor = EasytemplateProcessorImpl.copyOf(easyProcessor)
                .withResolver("client", clientBindings::add)
                .withResolver("server", serverBindings::add)
                .build();

        final Map<String, String> reportCustomParams = template.getBindingParams();
        list(
                template.getFrom(),
                template.getTo(),
                template.getCc(),
                template.getBcc(),
                template.getContent(),
                template.getSubject()
        ).with(
                reportCustomParams.values()
        //Prev version: template.getMeta().values() //note: process all data values because they _may_ contain bindings... this is quite rough and may/will produce a bunch of warnings
        ).stream().filter(StringUtils::isNotBlank).forEach(spyProcessor::processExpression);
        logger.trace("Report bindings found =< {} >", reportCustomParams);

        return new TemplateBindingsImpl(clientBindings, serverBindings);
    }

    /**
     * Builds resolver processors:
     * <ul>
     * <li>Javascript resolver;
     * <li>user expression resolver;
     * <li>group expression resolver;
     * <li>email expression resolver;
     * <li>card expression resolver;
     * <li>card attribute expression resolver;
     * <li>cql expression resolver;
     * <li>database template expression resolver;
     * <li>{@link MapperConfig} expression resolver.
     * </ul>
     *
     * @param language detected/forced language
     * @param expressionInputData
     * @return
     */
    private EasytemplateProcessor buildEasytemplateProcessor(String language, TemplateExpressionInputData expressionInputData) {
        Map<String, TemplateResolver> resolvers = buildProcessingResolvers(language, expressionInputData.getFtlTemplateData());
        return EasytemplateProcessorImpl.builder().withResolvers(resolvers).build();
    }

    /**
     * Builds all types of expression processors.
     *
     * <p>
     * Was in <code>EmailTemplateProcessor.processingResolvers(String)</code>
     *
     * @param jsContext
     * @return
     */
    private Map<String, TemplateResolver> buildProcessingResolvers(String language, Map<String, Object> ftlTemplateData) {
        Map<String, TemplateResolver> resolvers = EasytemplateProcessorImpl.copyOf(easytemplateService.getDefaultProcessorWithJsContext(jsContext))
                .withResolver("js", this::processJsExpr)
                .withResolver("user", this::processUserExpr)//TODO check this
                .withResolver("group", this::processGroupExpr)//TODO check this
                .withResolver("groupUsers", this::processGroupUsersExpr)
                .withResolver("email", this::processReceivedEmailExpr, false)
                .withResolver("card", expr -> processCardExpr(expr, language))
                .withResolver("", expr -> processCardExpr(expr, language))
                .withResolver("cql", this::processCqlExpr)
                .withResolver("dbtmpl", this::processDbTmplExpr)
                .withResolver("data", this::processOtherDataExpr)
                .withResolver("attrLabel", expr -> processAttrExpr(expr, ftlTemplateData))
                .accept((b) -> {
                    if (expressionInputData.getMapperConfig() != null) {
                        b.withResolver("mapper", this::processMapperExpr);
                    }
                })
                .build().getResolvers();

        resolvers = transformValues(resolvers, f -> {
            return new TemplateResolverImpl((x) -> {
                try {
                    return f.getFunction().apply(x);
                } catch (Exception ex) {
                    logger.warn(marker(), "CM: error processing email template expression =< {} > for template = {}", x, expressionInputData.getTemplate(), ex);
                    return "";
                }
            }, f.isRecursive());
        });

        return resolvers;
    }

    private String processJsExpr(String jsExpr) {
        checkNotBlank(jsExpr, "js expr is blank");
        if (expressionInputData.getTemplate() != null && isTemplateContextKey(jsExpr)) {
            jsExpr = getTemplateContextValueOrDbtemplateValue(jsExpr, EPM_JAVASCRIPT);
        }
        logger.trace("evaluate js expression =< {} >", jsExpr);
        return toStringOrNull(easytemplateService.evalJavascriptCode(jsExpr));
    }

    private String getTemplateContextValueOrDbtemplateValue(String key, EasytemplateProcessor.ExprProcessingMode mode) {
        String expr = null;
        if (expressionInputData.getTemplate() != null) {
            expr = expressionInputData.getTemplate().getMeta().get(key);
            logger.trace("trying to resolve key =< {} > with template context data, resolved to value =< {} >", key, expr);
//                easytemplateRepository.getTemplate
        }
        if (isBlank(expr)) {
            expr = easytemplateRepository.getTemplateOrNull(key);
            logger.trace("trying to resolve key =< {} > with dbtemplate data, resolved to value =< {} >", key, expr);
        }
        checkNotBlank(expr, "unable to resolve expr =< %s > for template context data or dbtemplate data", key);
        return processExpression(expr, mode);
    }

    private String processUserExpr(String username) {
        checkNotBlank(username, "username expr is blank");
        return switch (username) {
            case "id" ->
                toStringOrNull(userSupplier.getUser().getId());
            case "name" ->
                userSupplier.getUsername();
            case "lang" ->
                languageService.getContextLanguage();
            case "operator" ->
                operatorService.getProperties(LoginUserIdentity.build(userSupplier.getUsername())).getOrDefault(ATTR_ID, "").toString();
            default ->
                userRepository.getUserByIdOrUsername(username).getEmail();
        };
    }

    private String processGroupExpr(String group) {
        checkNotBlank(group, "group expr is blank");
        return switch (group) {
            case "id" ->
                userSupplier.getUser().hasDefaultGroup() ? toStringOrNull(userSupplier.getUser().getDefaultGroup().getId()) : null;
            case "name" ->
                userSupplier.getCurrentGroup();
            default ->
                roleRepository.getByNameOrId(group).getEmail();
        };
    }

    private String processGroupUsersExpr(String group) {
        checkNotBlank(group, "groupUsers expr is blank");
        long roleId;
        if (NumberUtils.isCreatable(group)) {
            roleId = toLong(group);
        } else {
            roleId = roleRepository.getGroupWithName(group).getId();
        }
        return userRepository.getAllWithRole(roleId).stream().map(UserData::getEmail).filter(StringUtils::isNotBlank).distinct().sorted().collect(joining(","));
    }

    private String processOtherDataExpr(String expr) {
        checkNotBlank(expr, "other data expr is blank");
        return toStringOrEmpty(expressionInputData.getOtherData().get(expr));
    }

    private String processReceivedEmailExpr(String expr) {
        final Email receivedEmail = expressionInputData.getReceivedEmail();
        checkNotNull(receivedEmail, "invalid email expr, no received email is available");
        checkNotBlank(expr, "email expr is blank");
        return switch (expr) {
            case "from" ->
                receivedEmail.getFrom();
            case "to" ->
                receivedEmail.getTo();
            case "cc" ->
                receivedEmail.getCc();
            case "bcc" ->
                receivedEmail.getBcc();
            case "date" ->
                toUserReadableDateTime(receivedEmail.getDate());
            case "subject" ->
                receivedEmail.getSubject();
            case "content" -> {
                if (hasHtmlTarget()) {
                    yield receivedEmail.getContentHtmlOrWrappedPlaintext();
                } else if (hasPlaintextTarget()) {
                    yield receivedEmail.getContentPlaintext();
                } else {
                    logger.warn(marker(), "evaluating email `context` expr, unable to detect target content type; returning fuzzy default. You should use `content_plain` or `content_html` here instead of `content`");
                    yield receivedEmail.getContentHtmlOrRawPlaintext();
                }
            }
            case "content_plain" ->
                receivedEmail.getContentPlaintext();
            case "content_html" ->
                receivedEmail.getContentHtmlOrWrappedPlaintext();
            default ->
                throw new IllegalArgumentException(format("unsupported email expr = %s", expr));
        };
    }

    private boolean hasPlaintextTarget() {
        return expressionInputData.getTemplate() != null && isContentType(expressionInputData.getTemplate().getContentType(), "text/plain");
    }

    private boolean hasHtmlTarget() {
        return expressionInputData.getTemplate() != null && isContentType(expressionInputData.getTemplate().getContentType(), "text/html");
    }

    @Nullable
    private String processCqlExpr(@Nullable String expr) {
        logger.trace("process cql expr =< {} >", expr);
        checkNotBlank(expr, "cql expr is blank");
        String field = null;
        Matcher matcher = Pattern.compile("^([^.]+)[.]([^.]+)$").matcher(expr);
        if (matcher.find()) {
            expr = checkNotBlank(matcher.group(1));
            field = checkNotBlank(matcher.group(2));
            expr = getTemplateContextValueOrDbtemplateValue(expr, EPM_DEFAULT);
        } else if (isTemplateContextKey(expr)) {
            expr = getTemplateContextValueOrDbtemplateValue(expr, EPM_DEFAULT);
        }
        logger.trace("execute cql query =< {} >", expr);
        Card card = dao.selectAll().where(CqlFilterImpl.build(expr).toCmdbFilter()).getCardOrNull();
        logger.trace("cql query output card = {}", card);
        String value;
        if (card == null) {
            value = null;
        } else {
            if (isBlank(field)) {
                field = getOnlyElement(getCqlSelectElements(expr));
            }
            value = card.getString(field);
        }
        logger.trace("cql query output value =< {} > for field =< {} > expr =< {} >", value, field, expr);
        return value;
    }

    private String processCardExpr(@Nullable String expr, String language) {
        logger.trace("process card expr =< {} >", expr);
        String value = switch (checkNotBlank(expr, "card expr is blank")) {
            case "CurrentRole" ->
                Optional.ofNullable(userSupplier.getUser().getDefaultGroupOrNull()).map(r -> toStringOrEmpty(r.getId())).orElse("");
            case "lang" ->
                nullToEmpty(language);
            default ->
                easyProcessor.processExpression(format("{client:%s}", expr));
        };
        logger.trace("processed card expr =< {} >, found value =< {} >", expr, value);
        return value;
    }

    private String processAttrExpr(String expr, Map<String, Object> ftlTemplateData) {
        logger.trace("process attr expr =< {} >", expr);
        checkNotBlank(expr);
        return translationService.translateAttributeDescription(dao.getClasse(((Map<String, Object>) ftlTemplateData.get("card")).get("IdClass").toString()).getAttribute(expr));
    }

    private String processMapperExpr(String expression) {
        checkNotNull(expressionInputData.getReceivedEmail(), "missing received email");
        checkNotNull(expressionInputData.getMapperConfig(), "missing mapper config");
        return EmailProcessingUtils.processMapperExpr(expressionInputData.getMapperConfig(), expressionInputData.getReceivedEmail().getContentPlaintext(), expression);
    }

    private String processDbTmplExpr(String expression) {
        return easyProcessor.processExpression(easytemplateRepository.getTemplateOrNull(checkNotBlank(expression, "db template expr is blank")));
    }

    private boolean isTemplateContextKey(String expr) {
        return expressionInputData.getTemplate() != null && expressionInputData.getTemplate().getMeta().containsKey(expr);
    }

} // end ExpressionProcessor class
