/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.email.template;

import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import org.cmdbuild.api.CmApiService;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.session.OperatorService;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.common.beans.IdAndDescriptionImpl;
import org.cmdbuild.common.localization.LanguageService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.beans.CardImpl.builder;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import org.cmdbuild.dao.core.q3.WhereOperator;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.ClassType;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.postgres.q3.RefAttrHelperServiceExt;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.dms.dao.DmsModelDocument;
import org.cmdbuild.dms.dao.DocumentInfoRepository;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.easytemplate.EasytemplateProcessorImpl;
import org.cmdbuild.easytemplate.EasytemplateService;
import org.cmdbuild.easytemplate.FtlTemplateService;
import org.cmdbuild.easytemplate.store.EasytemplateRepository;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAttachment;
import org.cmdbuild.email.EmailSignatureService;
import org.cmdbuild.email.EmailStatus;
import static org.cmdbuild.email.EmailStatus.ES_DRAFT;
import static org.cmdbuild.email.EmailStatus.ES_OUTGOING;
import org.cmdbuild.email.beans.EmailAttachmentImpl;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.beans.EmailTemplateImpl;
import org.cmdbuild.email.job.MapperConfig;
import org.cmdbuild.email.job.MapperConfigImpl;
import static org.cmdbuild.email.utils.EmailMtaUtils.renameDuplicates;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.services.permissions.DummyPermissionsHandler;
import org.cmdbuild.services.permissions.PermissionsHandlerProxy;
import org.cmdbuild.services.serialization.attribute.file.CardAttributeFileHelper;
import org.cmdbuild.template.ExpressionInputData;
import org.cmdbuild.template.SimpleExpressionInputData;
import org.cmdbuild.template.TemplateProcessorHandler;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.hamcrest.Matcher;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.endsWithIgnoringCase;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.AdditionalAnswers.returnsSecondArg;
import org.mockito.ArgumentMatcher;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 *
 * @author afelice
 */
public class EmailTemplateProcessorServiceImplTest {

    private static final String A_KNOWN_KEY = "aKey";
    private static final String A_KNOWN_EXPRESSION = "[#ftl]this is an easy template with ${email.from} interpolation";

    private static final String A_KNOWN_EXPRESSION_RESOLUTION = "this is an easy template with pippo@gmail.com interpolation";

    private static final String A_KNOWN_SENDER_EMAIL_EXPR = "${email.from}";
    private static final String A_KNOWN_DESTINATION_EMAIL_EXPR = "${email.to}";
    private static final String A_KNOWN_SENDER_EMAIL_EXPR_RESOLUTION = "my.sender@email.net";
    private static final String A_KNOWN_DESTINATION_EMAIL_EXPR_RESOLUTION = "my.dest@email.net";

    private final Classe classe = mock(Classe.class);

    private final DaoService dao = mock(DaoService.class);
    private final DmsService dmsService = mock(DmsService.class);
    private final ObjectTranslationService translationService = mock(ObjectTranslationService.class);
    private final DocumentInfoRepository repository = mock(DocumentInfoRepository.class);
    private final UserClassService userClassService = mock(UserClassService.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    private final EasytemplateRepository easytemplateRepository = mock(EasytemplateRepository.class);
    private final EasytemplateService easytemplateService = mock(EasytemplateService.class);
    private final OperationUserSupplier userSupplier = mock(OperationUserSupplier.class);
    private final RoleRepository roleRepository = mock(RoleRepository.class);
    private final FtlTemplateService ftlTemplateService = mock(FtlTemplateService.class);
    private final CmApiService apiService = mock(CmApiService.class);
    private final RefAttrHelperServiceExt refAttrHelperService = mock(RefAttrHelperServiceExt.class);
    private final LookupService lookupService = mock(LookupService.class);
    private final ReportService reportService = mock(ReportService.class);
    private final EmailSignatureService signatureService = mock(EmailSignatureService.class);
    private final LanguageService languageService = mock(LanguageService.class);
    private final DmsAttachmentDownloader dmsAttachmentDownloader = mock(DmsAttachmentDownloader.class);
    private final QueryBuilder queryBuilder = mock(QueryBuilder.class);

    private final OperatorService operatorService = mock(OperatorService.class);

    // Skip all permissions check
    PermissionsHandlerProxy permissionsHandler = new DummyPermissionsHandler();

    private final CardAttributeFileHelper serializationHelper = new CardAttributeFileHelper(dao, dmsService, repository, userClassService, translationService, userRepository,
            permissionsHandler);

    EmailTemplateProcessorServiceImpl instance;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() {
        when(classe.getName()).thenReturn("MyEmail"); // Used in CardAttributeFile_Helper.fillWithCardMetadata()
        when(classe.asClasse()).thenReturn(classe); // Used in CardImpl copy-contructor
        when(classe.getClassType()).thenReturn(ClassType.CT_SIMPLE); // Used in CardImpl contructor
        mockHistoryCount(1);

        instance = new EmailTemplateProcessorServiceImpl(easytemplateRepository, easytemplateService, userRepository, userSupplier, roleRepository,
                dao, ftlTemplateService, apiService, refAttrHelperService,
                lookupService, reportService, translationService,
                signatureService, languageService, dmsAttachmentDownloader,
                serializationHelper, operatorService);
    }

    /**
     * Test of loadClientCardValues method, for File attribute, <b>without a
     * DMS</b>, category active, of class EmailTemplateProcessorServiceImpl.
     */
    @Test
    public void testLoadClientCardValues_FileAttribute_NoDms_CategoryActive() {
        System.out.println("loadClientCardValues_FileAttribute_NoDms_CategoryActive");

        //arrange:
        final String fileAttrName = "FileAttribute";
        final Long cardId = 22L;

        // Attribute
        Attribute fileAttribute = TestDmsHelper.mockBuildAttr_File(fileAttrName, classe, TestDmsHelper.A_KNOWN_CATEGORY_DESCRIPTION);
        when(classe.getAllAttributes()).thenReturn(list(fileAttribute));
        // ...and related card
        Card aRelatedCard = CardImpl.builder()
                .withId(cardId).withType(classe)
                .withAttribute(fileAttrName, TestDmsHelper.A_KNOWN_DOCUMENT_ID)
                .build();

        DmsModelDocument aDocument = TestDmsHelper.mockBuildDmsModelDocument(aRelatedCard);

        // No DMS
        boolean isDmsServiceOk = false;
        TestDmsHelper.mockDmsService(dmsService, isDmsServiceOk);
        // Mock persistence
        QueryBuilder mockQueryBuilder = TestDmsHelper.mockQueryBuilder_SelectAll(dao);
        TestDmsHelper.mockQueryBuilder_where_String(mockQueryBuilder, "DmsModel");
        when(mockQueryBuilder.getCard()).thenReturn(aRelatedCard);
        TestDmsHelper.mockQueryBuilder_whereExpr(mockQueryBuilder, aRelatedCard.getTypeName());
        when(mockQueryBuilder.getCardOrNull()).thenReturn(aRelatedCard);

        when(repository.getById(eq(TestDmsHelper.A_KNOWN_DOCUMENT_ID))).thenReturn(aDocument);
        // Category
        boolean categoryActive = true;
        org.cmdbuild.lookup.LookupValue aCategory = TestDmsHelper.mockCategory(categoryActive);
        when(dmsService.getCategoryLookupForAttachment(any(), any())).thenReturn(aCategory);

        CmMapUtils.FluentMap<String, Object> expResult = map(
                ATTR_ID, cardId,
                // File attribute
                fileAttrName, TestDmsHelper.A_KNOWN_DOCUMENT_ID,
                TestDmsHelper.buildProp(fileAttrName, "name"), TestDmsHelper.A_KNOWN_FILENAME,
                TestDmsHelper.buildProp(fileAttrName, "category"), TestDmsHelper.A_KNOWN_CATEGORY_LOOKUP_ID,
                TestDmsHelper.buildProp(fileAttrName, "Category"), TestDmsHelper.A_KNOWN_CATEGORY_LOOKUP_ID, // for legacy
                TestDmsHelper.buildProp(fileAttrName, "description"), TestDmsHelper.A_KNOWN_DOCUMENT_DESCRIPTION,
                TestDmsHelper.buildProp(fileAttrName, "Description"), TestDmsHelper.A_KNOWN_DOCUMENT_DESCRIPTION, // for legacy
                TestDmsHelper.buildProp(fileAttrName, "version"), TestDmsHelper.A_KNOWN_DOCUMENT_VERSION,
                TestDmsHelper.buildProp(fileAttrName, "author"), TestDmsHelper.A_KNOWN_AUTHOR,
                TestDmsHelper.buildProp(fileAttrName, "created"), TestDmsHelper.A_KNOWN_DOCUMENT_CREATION_DATE_STR,
                TestDmsHelper.buildProp(fileAttrName, "modified"), TestDmsHelper.A_KNOWN_DOCUMENT_MODIFICATION_DATE_STR,
                TestDmsHelper.buildProp(fileAttrName, "isDmsServiceOk"), isDmsServiceOk);
        //act:
        Card cardWithValues = instance.loadClientCardValues(aRelatedCard, null);

        //assert:
        TestDmsHelper.checkEquals_Map(expResult, cardWithValues.getAllValuesAsMap());
    }

    /**
     * Test of loadClientCardValues method, for File attribute, <b>not set
     * documentId</b>, of class EmailTemplateProcessorServiceImpl.
     */
    @Test
    public void testLoadClientCardValues_FileAttribute_Dms_CategoryActive_EmptyCard() {
        System.out.println("loadClientCardValues_FileAttribute_Dms_CategoryActive_EmptyCard");

        //arrange:
        final String fileAttrName = "FileAttribute";
        final Long cardId = 22L;

        // Attribute
        Attribute fileAttribute = TestDmsHelper.mockBuildAttr_File(fileAttrName, classe, TestDmsHelper.A_KNOWN_CATEGORY_DESCRIPTION);
        when(classe.getAllAttributes()).thenReturn(list(fileAttribute));
        // ...and related card
        Card aRelatedCard = CardImpl.builder()
                .withId(cardId).withType(classe)
                // Not set documentId
                //.withAttribute(fileAttrName, TestDmsHelper.A_KNOWN_DOCUMENT_ID)
                .build();

        // DMS
        boolean isDmsServiceOk = true;
        TestDmsHelper.mockDmsService(dmsService, isDmsServiceOk);

        // Category
        boolean categoryActive = true;
        org.cmdbuild.lookup.LookupValue aCategory = TestDmsHelper.mockCategory(categoryActive);

        CmMapUtils.FluentMap<String, Object> expResult = map(
                ATTR_ID, cardId);

        //act:
        Card cardWithValues = instance.loadClientCardValues(aRelatedCard, null);

        //assert:
        verify(dmsService, never()).getCardAttachmentByMetadataId(anyLong());
        verify(dmsService, never()).getCategoryLookupForAttachment(any(), any());
        TestDmsHelper.checkEquals_Map(expResult, cardWithValues.getAllValuesAsMap());
    }

    /**
     * Test of loadClientCardValues method, for File attribute, given documentId
     * (as String), <b>with DMS</b>
     * enabled, category active, of class EmailTemplateProcessorServiceImpl.
     */
    @Test
    public void testLoadClientCardValues_FileAttribute_Dms_CategoryActive_DocumentId() {
        System.out.println("loadClientCardValues_FileAttribute_Dms_CategoryActive_DocumentId");

        //arrange:
        final String fileAttrName = "FileAttribute";
        final Long cardId = 22L;

        // Attribute
        Attribute fileAttribute = TestDmsHelper.mockBuildAttr_File(fileAttrName, classe, TestDmsHelper.A_KNOWN_CATEGORY_DESCRIPTION);
        when(classe.getAllAttributes()).thenReturn(list(fileAttribute));
        // ...and related card
        Card aRelatedCard = CardImpl.builder()
                .withId(cardId).withType(classe)
                .withAttribute(fileAttrName, TestDmsHelper.A_KNOWN_DOCUMENT_ID)
                .build();

        DocumentInfoAndDetail aDocument = TestDmsHelper.mockBuildDocumentInfoAndDetail(aRelatedCard);

        // DMS
        boolean isDmsServiceOk = true;
        TestDmsHelper.mockDmsService(dmsService, isDmsServiceOk);
        when(dmsService.getCardAttachmentById(eq(TestDmsHelper.A_KNOWN_DOCUMENT_ID))).thenReturn(aDocument);

        // Category
        boolean categoryActive = true;
        org.cmdbuild.lookup.LookupValue aCategory = TestDmsHelper.mockCategory(categoryActive);
        when(dmsService.getCategoryLookupForAttachment(any(), any())).thenReturn(aCategory);

        CmMapUtils.FluentMap<String, Object> expResult = map(
                ATTR_ID, cardId,
                // File attribute
                fileAttrName, TestDmsHelper.A_KNOWN_DOCUMENT_ID,
                TestDmsHelper.buildProp(fileAttrName, "name"), TestDmsHelper.A_KNOWN_FILENAME,
                TestDmsHelper.buildProp(fileAttrName, "category"), TestDmsHelper.A_KNOWN_CATEGORY_LOOKUP_ID,
                TestDmsHelper.buildProp(fileAttrName, "Category"), TestDmsHelper.A_KNOWN_CATEGORY_LOOKUP_ID, // for legacy
                TestDmsHelper.buildProp(fileAttrName, "description"), TestDmsHelper.A_KNOWN_DOCUMENT_DESCRIPTION,
                TestDmsHelper.buildProp(fileAttrName, "Description"), TestDmsHelper.A_KNOWN_DOCUMENT_DESCRIPTION, // for legacy
                TestDmsHelper.buildProp(fileAttrName, "version"), TestDmsHelper.A_KNOWN_DOCUMENT_VERSION,
                TestDmsHelper.buildProp(fileAttrName, "author"), TestDmsHelper.A_KNOWN_AUTHOR,
                TestDmsHelper.buildProp(fileAttrName, "created"), TestDmsHelper.A_KNOWN_DOCUMENT_CREATION_DATE_STR,
                TestDmsHelper.buildProp(fileAttrName, "modified"), TestDmsHelper.A_KNOWN_DOCUMENT_MODIFICATION_DATE_STR,
                TestDmsHelper.buildProp(fileAttrName, "isDmsServiceOk"), isDmsServiceOk);

        //act:
        Card cardWithValues = instance.loadClientCardValues(aRelatedCard, null);

        //assert:
        TestDmsHelper.checkEquals_Map(expResult, cardWithValues.getAllValuesAsMap());
    }

    /**
     * Test of loadClientCardValues method, for File attribute, given full
     * generated document cardId (as long), <b>with DMS</b>
     * enabled, category active, of class EmailTemplateProcessorServiceImpl.
     */
    @Test
    public void testLoadClientCardValues_FileAttribute_Dms_CategoryActive_DocumentCardId() {
        System.out.println("loadClientCardValues_FileAttribute_Dms_CategoryActive_DocumentCardId");

        //arrange:
        final String fileAttrName = "FileAttribute";
        final Long cardId = 22L;

        // Attribute
        Attribute fileAttribute = TestDmsHelper.mockBuildAttr_File(fileAttrName, classe, TestDmsHelper.A_KNOWN_CATEGORY_DESCRIPTION);
        when(classe.getAllAttributes()).thenReturn(list(fileAttribute));
        // ...and related card
        Card aRelatedCard = CardImpl.builder()
                .withId(cardId).withType(classe)
                .build();

        DocumentInfoAndDetail aDocument = TestDmsHelper.mockBuildDocumentInfoAndDetail(aRelatedCard);

        // In related card put the full (generated) card Id
        aRelatedCard = CardImpl.copyOf(aRelatedCard)
                .withAttribute(fileAttrName, TestDmsHelper.A_KNOWN_DOCUMENT_FULL_GENERATED_CARD_ID)
                .build();

        // DMS
        boolean isDmsServiceOk = true;
        TestDmsHelper.mockDmsService(dmsService, isDmsServiceOk);
        when(dmsService.getCardAttachmentByMetadataId(eq(TestDmsHelper.A_KNOWN_DOCUMENT_FULL_GENERATED_CARD_ID))).thenReturn(aDocument);

        // Category
        boolean categoryActive = true;
        org.cmdbuild.lookup.LookupValue aCategory = TestDmsHelper.mockCategory(categoryActive);
        when(dmsService.getCategoryLookupForAttachment(any(), any())).thenReturn(aCategory);

        CmMapUtils.FluentMap<String, Object> expResult = map(
                ATTR_ID, cardId,
                // File attribute
                fileAttrName, TestDmsHelper.A_KNOWN_DOCUMENT_ID,
                TestDmsHelper.buildProp(fileAttrName, "name"), TestDmsHelper.A_KNOWN_FILENAME,
                TestDmsHelper.buildProp(fileAttrName, "category"), TestDmsHelper.A_KNOWN_CATEGORY_LOOKUP_ID,
                TestDmsHelper.buildProp(fileAttrName, "Category"), TestDmsHelper.A_KNOWN_CATEGORY_LOOKUP_ID, // for legacy
                TestDmsHelper.buildProp(fileAttrName, "description"), TestDmsHelper.A_KNOWN_DOCUMENT_DESCRIPTION,
                TestDmsHelper.buildProp(fileAttrName, "Description"), TestDmsHelper.A_KNOWN_DOCUMENT_DESCRIPTION, // for legacy
                TestDmsHelper.buildProp(fileAttrName, "version"), TestDmsHelper.A_KNOWN_DOCUMENT_VERSION,
                TestDmsHelper.buildProp(fileAttrName, "author"), TestDmsHelper.A_KNOWN_AUTHOR,
                TestDmsHelper.buildProp(fileAttrName, "created"), TestDmsHelper.A_KNOWN_DOCUMENT_CREATION_DATE_STR,
                TestDmsHelper.buildProp(fileAttrName, "modified"), TestDmsHelper.A_KNOWN_DOCUMENT_MODIFICATION_DATE_STR,
                TestDmsHelper.buildProp(fileAttrName, "isDmsServiceOk"), isDmsServiceOk);

        //act:
        Card cardWithValues = instance.loadClientCardValues(aRelatedCard, null);

        //assert:
        TestDmsHelper.checkEquals_Map(expResult, cardWithValues.getAllValuesAsMap());
    }

    /**
     * Test of loadClientCardValues method, for File attribute, given full
     * generated document cardId (as IdAndDescription), <b>with DMS</b>
     * enabled, category active, of class EmailTemplateProcessorServiceImpl.
     */
    @Test
    public void testLoadClientCardValues_FileAttribute_Dms_CategoryActive_DocumentCardIdAndDescription() {
        System.out.println("loadClientCardValues_FileAttribute_Dms_CategoryActive");

        //arrange:
        final String fileAttrName = "FileAttribute";
        final Long cardId = 22L;

        // Attribute
        Attribute fileAttribute = TestDmsHelper.mockBuildAttr_File(fileAttrName, classe, TestDmsHelper.A_KNOWN_CATEGORY_DESCRIPTION);
        when(classe.getAllAttributes()).thenReturn(list(fileAttribute));
        // ...and related card
        Card aRelatedCard = CardImpl.builder()
                .withId(cardId).withType(classe)
                .build();

        DocumentInfoAndDetail aDocument = TestDmsHelper.mockBuildDocumentInfoAndDetail(aRelatedCard);

        // In related card put the full (generated) card Id (as IdAndDescription
        aRelatedCard = CardImpl.copyOf(aRelatedCard)
                .withAttribute(fileAttrName, new IdAndDescriptionImpl(TestDmsHelper.A_KNOWN_DOCUMENT_FULL_GENERATED_CARD_ID, "dummyDescription"))
                .build();

        // DMS
        boolean isDmsServiceOk = true;
        TestDmsHelper.mockDmsService(dmsService, isDmsServiceOk);
        when(dmsService.getCardAttachmentByMetadataId(eq(TestDmsHelper.A_KNOWN_DOCUMENT_FULL_GENERATED_CARD_ID))).thenReturn(aDocument);

        // Category
        boolean categoryActive = true;
        org.cmdbuild.lookup.LookupValue aCategory = TestDmsHelper.mockCategory(categoryActive);
        when(dmsService.getCategoryLookupForAttachment(any(), any())).thenReturn(aCategory);

        CmMapUtils.FluentMap<String, Object> expResult = map(
                ATTR_ID, cardId,
                // File attribute
                fileAttrName, TestDmsHelper.A_KNOWN_DOCUMENT_ID,
                TestDmsHelper.buildProp(fileAttrName, "name"), TestDmsHelper.A_KNOWN_FILENAME,
                TestDmsHelper.buildProp(fileAttrName, "category"), TestDmsHelper.A_KNOWN_CATEGORY_LOOKUP_ID,
                TestDmsHelper.buildProp(fileAttrName, "Category"), TestDmsHelper.A_KNOWN_CATEGORY_LOOKUP_ID, // for legacy
                TestDmsHelper.buildProp(fileAttrName, "description"), TestDmsHelper.A_KNOWN_DOCUMENT_DESCRIPTION,
                TestDmsHelper.buildProp(fileAttrName, "Description"), TestDmsHelper.A_KNOWN_DOCUMENT_DESCRIPTION, // for legacy
                TestDmsHelper.buildProp(fileAttrName, "version"), TestDmsHelper.A_KNOWN_DOCUMENT_VERSION,
                TestDmsHelper.buildProp(fileAttrName, "author"), TestDmsHelper.A_KNOWN_AUTHOR,
                TestDmsHelper.buildProp(fileAttrName, "created"), TestDmsHelper.A_KNOWN_DOCUMENT_CREATION_DATE_STR,
                TestDmsHelper.buildProp(fileAttrName, "modified"), TestDmsHelper.A_KNOWN_DOCUMENT_MODIFICATION_DATE_STR,
                TestDmsHelper.buildProp(fileAttrName, "isDmsServiceOk"), isDmsServiceOk);

        //act:
        Card cardWithValues = instance.loadClientCardValues(aRelatedCard, null);

        //assert:
        TestDmsHelper.checkEquals_Map(expResult, cardWithValues.getAllValuesAsMap());
    }

    /**
     * Test of renameDuplicates method, of class EmailMtaUtils.
     */
    @Test
    public void testEmailAttachments_RenameDuplicates() {
        System.out.println("emailAttachments_RenameDuplicates");

        //arrange:
        List<EmailAttachment> emailAttachments = list(mock_buildEmailAttachment_PDF("abc.pdf"),
                mock_buildEmailAttachment_PDF("abc.pdf")
        );

        //act:
        List<EmailAttachment> result = renameDuplicates(emailAttachments);

        //assert:
        assertThat(result, hasSize(2));
        assertFalse(result.get(0).getFileName().equals(result.get(1).getFileName()));
        final Matcher<EmailAttachment> matcherExtension = Matchers.hasProperty("fileName", endsWithIgnoringCase(".pdf"));
        assertThat(result, everyItem(matcherExtension));
    }

    /**
     * Test of process email, template with language only,
     * {@link TemplateProcessorHandler#initWith(org.cmdbuild.template.ExpressionInputData)}
     * must be invoked, of class EmailTemplateProcessorServiceImpl.
     *
     * <p>
     * Was createEmailFromTemplate(template, card, map) with card null and map
     * null
     */
    @Test
    public void testBuildProcessor() {
        System.out.println("buildProcessor");

        //arrange:
        EmailTemplate aTemplate = buildFakeEmailTemplate(1, A_KNOWN_EXPRESSION);
        TemplateProcessorHandler mockProcessor = mockTemplateProcessorHandler();

        EmailTemplateProcessorServiceImpl theInstance = new EmailTemplateProcessorServiceImpl(easytemplateRepository, easytemplateService, userRepository, userSupplier, roleRepository,
                dao, ftlTemplateService, apiService, refAttrHelperService,
                lookupService, reportService, translationService,
                signatureService, languageService, dmsAttachmentDownloader,
                serializationHelper, operatorService) {
            @Override
            protected TemplateProcessorHandler buildTemplateProcessor() {
                return mockProcessor;
            }
        };
        final ExpressionInputData exprInputData = ExpressionInputData
                .builder()
                .withTemplate(aTemplate)
                .build();

        //act:
        theInstance.buildProcessor(exprInputData);

        //assert:
        verify(mockProcessor, times(1)).initWith(exprInputData);
        verifyNoMoreInteractions(mockProcessor);
    }

    /**
     * Test of process email, error because no template given, of class
     * EmailTemplateProcessorServiceImpl.
     */
    @Test
    public void testProcessEmail_ErrorNoTemplate() {
        System.out.println("processEmail_ErrorNoTemplate");

        //arrange:
        when(easytemplateService.getDefaultProcessorWithJsContext(anyString())).thenReturn(EasytemplateProcessorImpl.builder().build());

        // act&assert:
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("processing an email template, but missing the template");
        instance.processEmail(null, ExpressionInputData.builder().build());
    }

    /**
     * Test of process email, template (with language) only, a <i>draft</i>
     * email created of class EmailTemplateProcessorServiceImpl.
     *
     */
    @Test
    public void testProcessEmail_TemplateOnly() {
        System.out.println("processEmail_TemplateOnly");

        //arrange:
        EmailTemplate aTemplate = buildFakeEmailTemplate(1, A_KNOWN_EXPRESSION);
        TemplateProcessorHandler mockProcessor = mockTemplateProcessorHandler();
        EmailTemplateProcessorServiceImpl theInstance = new EmailTemplateProcessorServiceImpl(easytemplateRepository, easytemplateService, userRepository, userSupplier, roleRepository,
                dao, ftlTemplateService, apiService, refAttrHelperService,
                lookupService, reportService, translationService,
                signatureService, languageService, dmsAttachmentDownloader,
                serializationHelper, operatorService) {
            @Override
            protected TemplateProcessorHandler buildTemplateProcessor() {
                return mockProcessor;
            }
        };
        ExpressionInputData exprInputData = ExpressionInputData.builder()
                .withTemplate(aTemplate)
                .build();
        mockBindingKnownExpression_onTranslateValue(mockProcessor, aTemplate);

        //act:
        Email result = theInstance.processEmail(null, exprInputData);

        //assert:
        verify(mockProcessor, times(1)).initWith(matchExpressionInputData(exprInputData));
        verify(mockProcessor, times(1)).translateValue(eq(aTemplate), eq(A_KNOWN_EXPRESSION), anyString());
        verify(mockProcessor, times(1)).processTemplateValue(eq(A_KNOWN_EXPRESSION_RESOLUTION), anyString());
        verify(mockProcessor, times(1)).processMultipleExpressions(anyMap());
        verifyNoMoreInteractions(mockProcessor);
        assertEquals(ES_DRAFT, result.getStatus()); // synthesized draft email
        assertEquals(A_KNOWN_EXPRESSION_RESOLUTION, result.getContent());
    }

    /**
     * Test of process email, template (with language) and map (with bingind), a
     * <i>draft</i>
     * email created of class EmailTemplateProcessorServiceImpl.
     *
     * <p>
     * Was createEmailFromTemplate(template, map)
     */
    @Test
    public void testProcessEmail_TemplateAndMap() {
        System.out.println("processEmail_TemplateAndMap");

        //arrange:
        EmailTemplate aTemplate = buildFakeEmailTemplate(1, A_KNOWN_EXPRESSION);
        FluentMap<String, Object> aMap = map();
        aMap.with(A_KNOWN_EXPRESSION, A_KNOWN_EXPRESSION_RESOLUTION);
        TemplateProcessorHandler mockProcessor = mockTemplateProcessorHandler();
        EmailTemplateProcessorServiceImpl theInstance = new EmailTemplateProcessorServiceImpl(easytemplateRepository, easytemplateService, userRepository, userSupplier, roleRepository,
                dao, ftlTemplateService, apiService, refAttrHelperService,
                lookupService, reportService, translationService,
                signatureService, languageService, dmsAttachmentDownloader,
                serializationHelper, operatorService) {
            @Override
            protected TemplateProcessorHandler buildTemplateProcessor() {
                return mockProcessor;
            }
        };
        ExpressionInputData exprInputData = ExpressionInputData.builder()
                .withTemplate(aTemplate)
                .withOtherData(aMap)
                .build();
        mockBindingKnownExpression_onProcessTemplateValue_ExpressionProcessor(mockProcessor, aTemplate,
                map(A_KNOWN_EXPRESSION, A_KNOWN_EXPRESSION_RESOLUTION)); // emulates binding on map

        //act:
        Email result = theInstance.processEmail(null, exprInputData);

        //assert:
        verify(mockProcessor, times(1)).initWith(matchExpressionInputData(exprInputData));
        verify(mockProcessor, times(1)).translateValue(eq(aTemplate), eq(A_KNOWN_EXPRESSION), anyString());
        verify(mockProcessor, times(1)).processTemplateValue(eq(A_KNOWN_EXPRESSION), anyString()); // Handles binding through map
        verify(mockProcessor, times(1)).processMultipleExpressions(anyMap());
        verifyNoMoreInteractions(mockProcessor);
        assertEquals(ES_DRAFT, result.getStatus()); // synthesized draft email
        assertEquals(A_KNOWN_EXPRESSION_RESOLUTION, result.getContent());
    }

    /**
     * Test of process email, template (with language) and (client) card (with
     * binding) and given email to send, of class
     * EmailTemplateProcessorServiceImpl.
     *
     * <p>
     * Was createEmailFromTemplate(template, card)
     */
    @Test
    public void testProcessEmail_EmailAndTemplateAndClientCard() {
        System.out.println("processEmail_EmailAndTemplateAndClientCard");

        //arrange:
        String lang = "en";
        Email aToProcessEmail = buildToProcessEmail("<nothing>");
        EmailTemplate aTemplate = buildFakeEmailTemplate(1, A_KNOWN_EXPRESSION, lang);
        Card aClientCard = buildCard(1L, A_KNOWN_EXPRESSION_RESOLUTION);
        TemplateProcessorHandler mockProcessor = mockTemplateProcessorHandler();
        EmailTemplateProcessorServiceImpl theInstance = new EmailTemplateProcessorServiceImpl(easytemplateRepository, easytemplateService, userRepository, userSupplier, roleRepository,
                dao, ftlTemplateService, apiService, refAttrHelperService,
                lookupService, reportService, translationService,
                signatureService, languageService, dmsAttachmentDownloader,
                serializationHelper, operatorService) {
            @Override
            protected TemplateProcessorHandler buildTemplateProcessor() {
                return mockProcessor;
            }
        };
        ExpressionInputData exprInputData = ExpressionInputData.builder()
                .withTemplate(aTemplate)
                .withClientCard(aClientCard)
                .build();
        mockBindingKnownExpression_onProcessTemplateValue_ExpressionProcessor(mockProcessor, aTemplate,
                map(A_KNOWN_EXPRESSION, A_KNOWN_EXPRESSION_RESOLUTION)); // emulates binding on card

        //act:
        Email result = theInstance.processEmail(aToProcessEmail, exprInputData);

        //assert:
        verify(mockProcessor, times(1)).initWith(matchExpressionInputData(exprInputData));
        verify(mockProcessor, times(1)).translateValue(eq(aTemplate), eq(A_KNOWN_EXPRESSION), anyString());
        verify(mockProcessor, times(1)).processTemplateValue(eq(A_KNOWN_EXPRESSION), anyString());
        verify(mockProcessor, times(1)).processMultipleExpressions(anyMap());
        verifyNoMoreInteractions(mockProcessor);
        assertEquals(ES_OUTGOING, result.getStatus());
        assertEquals(A_KNOWN_EXPRESSION_RESOLUTION, result.getContent());
        assertEquals(A_KNOWN_SENDER_EMAIL_EXPR_RESOLUTION, result.getFrom());
        assertEquals(A_KNOWN_DESTINATION_EMAIL_EXPR_RESOLUTION, result.getTo());
    }

    /**
     * Test of process email, template (with language) and given email to send,
     * of class EmailTemplateProcessorServiceImpl.
     *
     * <p>
     * Was applyEmailTemplate(email, template)
     */
    @Test
    public void testProcessEmail_EmailAndTemplate() {
        System.out.println("processEmail_EmailAndTemplate");

        //arrange:
        String lang = "en";
        Email aToProcessEmail = buildToProcessEmail("<nothing>");
        EmailTemplate aTemplate = buildFakeEmailTemplate(1, A_KNOWN_EXPRESSION, lang);
        TemplateProcessorHandler mockProcessor = mockTemplateProcessorHandler();
        EmailTemplateProcessorServiceImpl theInstance = new EmailTemplateProcessorServiceImpl(easytemplateRepository, easytemplateService, userRepository, userSupplier, roleRepository,
                dao, ftlTemplateService, apiService, refAttrHelperService,
                lookupService, reportService, translationService,
                signatureService, languageService, dmsAttachmentDownloader,
                serializationHelper, operatorService) {
            @Override
            protected TemplateProcessorHandler buildTemplateProcessor() {
                return mockProcessor;
            }
        };
        ExpressionInputData exprInputData = ExpressionInputData.builder()
                .withTemplate(aTemplate)
                .build();
        mockBindingKnownExpression_onProcessTemplateValue_ExpressionProcessor(mockProcessor, aTemplate,
                map(A_KNOWN_EXPRESSION, A_KNOWN_EXPRESSION_RESOLUTION)); // emulates binding on card

        //act:
        Email result = theInstance.processEmail(aToProcessEmail, exprInputData);

        //assert:
        verify(mockProcessor, times(1)).initWith(matchExpressionInputData(exprInputData));
        verify(mockProcessor, times(1)).translateValue(eq(aTemplate), eq(A_KNOWN_EXPRESSION), anyString());
        verify(mockProcessor, times(1)).processTemplateValue(eq(A_KNOWN_EXPRESSION), anyString());
        verify(mockProcessor, times(1)).processMultipleExpressions(anyMap());
        verifyNoMoreInteractions(mockProcessor);
        assertEquals(ES_OUTGOING, result.getStatus());
        assertEquals(A_KNOWN_EXPRESSION_RESOLUTION, result.getContent());
        assertEquals(A_KNOWN_SENDER_EMAIL_EXPR_RESOLUTION, result.getFrom());
        assertEquals(A_KNOWN_DESTINATION_EMAIL_EXPR_RESOLUTION, result.getTo());
    }

    /**
     * Test of process email, template (with language) and map (with binding)
     * and given email to send, of class EmailTemplateProcessorServiceImpl.
     *
     * <p>
     * Was applyEmailTemplate(email, template, data)
     */
    @Test
    public void tesProcessEmail_EmailAndTemplateAndMap() {
        System.out.println("processEmail_EmailAndTemplateAndMap");

        //arrange:
        String lang = "en";
        Email aToProcessEmail = buildToProcessEmail("<nothing>");
        EmailTemplate aTemplate = buildFakeEmailTemplate(1, A_KNOWN_EXPRESSION, lang);
        FluentMap<String, Object> aMap = map();
        aMap.with(A_KNOWN_EXPRESSION, A_KNOWN_EXPRESSION_RESOLUTION);
        TemplateProcessorHandler mockProcessor = mockTemplateProcessorHandler();
        EmailTemplateProcessorServiceImpl theInstance = new EmailTemplateProcessorServiceImpl(easytemplateRepository, easytemplateService, userRepository, userSupplier, roleRepository,
                dao, ftlTemplateService, apiService, refAttrHelperService,
                lookupService, reportService, translationService,
                signatureService, languageService, dmsAttachmentDownloader,
                serializationHelper, operatorService) {
            @Override
            protected TemplateProcessorHandler buildTemplateProcessor() {
                return mockProcessor;
            }
        };
        ExpressionInputData exprInputData = ExpressionInputData.builder()
                .withTemplate(aTemplate)
                .withOtherData(aMap)
                .build();
        mockBindingKnownExpression_onProcessTemplateValue_ExpressionProcessor(mockProcessor, aTemplate,
                map(A_KNOWN_EXPRESSION, A_KNOWN_EXPRESSION_RESOLUTION));

        //act:
        Email result = theInstance.processEmail(aToProcessEmail, exprInputData);

        //assert:
        verify(mockProcessor, times(1)).initWith(matchExpressionInputData(exprInputData));
        verify(mockProcessor, times(1)).translateValue(eq(aTemplate), eq(A_KNOWN_EXPRESSION), anyString());
        verify(mockProcessor, times(1)).processTemplateValue(eq(A_KNOWN_EXPRESSION), anyString());
        verify(mockProcessor, times(1)).processMultipleExpressions(anyMap());
        verifyNoMoreInteractions(mockProcessor);
        assertEquals(ES_OUTGOING, result.getStatus());
        assertEquals(A_KNOWN_EXPRESSION_RESOLUTION, result.getContent());
        assertEquals(A_KNOWN_SENDER_EMAIL_EXPR_RESOLUTION, result.getFrom());
        assertEquals(A_KNOWN_DESTINATION_EMAIL_EXPR_RESOLUTION, result.getTo());
    }

    /**
     * Test of process email, template (with language) and (client and server)
     * cards (with binding) and given email to send, of class
     * EmailTemplateProcessorServiceImpl.
     *
     * <p>
     * Was applyEmailTemplate(email, template, clientCard, serverCard)
     */
    @Test
    public void testProcessEmail_EmailAndTemplateAndCards() {
        System.out.println("processEmail_EmailAndTemplateAndCards");

        //arrange:
        String lang = "en";
        Email aToProcessEmail = buildToProcessEmail("<nothing>");
        EmailTemplate aTemplate = buildFakeEmailTemplate(1, A_KNOWN_EXPRESSION, lang);
        Card aClientCard = buildCard(1L);
        Card aServerCard = buildCard(1L, A_KNOWN_EXPRESSION_RESOLUTION);
        TemplateProcessorHandler mockProcessor = mockTemplateProcessorHandler();
        EmailTemplateProcessorServiceImpl theInstance = new EmailTemplateProcessorServiceImpl(easytemplateRepository, easytemplateService, userRepository, userSupplier, roleRepository,
                dao, ftlTemplateService, apiService, refAttrHelperService,
                lookupService, reportService, translationService,
                signatureService, languageService, dmsAttachmentDownloader,
                serializationHelper, operatorService) {
            @Override
            protected TemplateProcessorHandler buildTemplateProcessor() {
                return mockProcessor;
            }
        };
        ExpressionInputData exprInputData = ExpressionInputData.builder()
                .withTemplate(aTemplate)
                .withClientCard(aClientCard)
                .withServerCard(aServerCard)
                .build();
        mockBindingKnownExpression_onProcessTemplateValue_ExpressionProcessor(mockProcessor, aTemplate,
                map(A_KNOWN_EXPRESSION, A_KNOWN_EXPRESSION_RESOLUTION));

        //act:
        Email result = theInstance.processEmail(aToProcessEmail, exprInputData);

        //assert:
        verify(mockProcessor, times(1)).initWith(matchExpressionInputData(exprInputData));
        verify(mockProcessor, times(1)).translateValue(eq(aTemplate), eq(A_KNOWN_EXPRESSION), anyString());
        verify(mockProcessor, times(1)).processTemplateValue(eq(A_KNOWN_EXPRESSION), anyString());
        verify(mockProcessor, times(1)).processMultipleExpressions(anyMap());
        verifyNoMoreInteractions(mockProcessor);
        assertEquals(ES_OUTGOING, result.getStatus());
        assertEquals(A_KNOWN_EXPRESSION_RESOLUTION, result.getContent());
        assertEquals(A_KNOWN_SENDER_EMAIL_EXPR_RESOLUTION, result.getFrom());
        assertEquals(A_KNOWN_DESTINATION_EMAIL_EXPR_RESOLUTION, result.getTo());
    }

    /**
     * Test of process email, template (with language) and (client) card (with
     * binding) and received email (with other bindings) and given email to
     * send, of class EmailTemplateProcessorServiceImpl.
     *
     * <p>
     * Was applyEmailTemplate(email, template, card, receivedEmail)
     */
    @Test
    public void testProcessEmail_EmailAndTemplateAndClientCardAndReceivedEmail() {
        System.out.println("processEmail_EmailAndTemplateAndClientCardAndReceivedEmail");

        //arrange:
        String lang = "en";
        Email aToProcessEmail = buildToProcessEmail_NoAddresses("<nothing>");
        Email aReceivedEmail = buildReceivedEmail();
        EmailTemplate aTemplate = buildFakeEmailTemplate_WithAddresses(1, A_KNOWN_EXPRESSION, lang);
        Card aClientCard = buildCard(1L, A_KNOWN_EXPRESSION_RESOLUTION);
        TemplateProcessorHandler mockProcessor = mockTemplateProcessorHandler();
        EmailTemplateProcessorServiceImpl theInstance = new EmailTemplateProcessorServiceImpl(easytemplateRepository, easytemplateService, userRepository, userSupplier, roleRepository,
                dao, ftlTemplateService, apiService, refAttrHelperService,
                lookupService, reportService, translationService,
                signatureService, languageService, dmsAttachmentDownloader,
                serializationHelper, operatorService) {
            @Override
            protected TemplateProcessorHandler buildTemplateProcessor() {
                return mockProcessor;
            }
        };
        ExpressionInputData exprInputData = ExpressionInputData.builder()
                .withTemplate(aTemplate)
                .withClientCard(aClientCard)
                .withReceivedEmail(aReceivedEmail)
                .build();
        mockBindingKnownExpression_onProcessTemplateValue_ExpressionProcessor(mockProcessor, aTemplate,
                map(
                        A_KNOWN_EXPRESSION, A_KNOWN_EXPRESSION_RESOLUTION,
                        A_KNOWN_SENDER_EMAIL_EXPR, A_KNOWN_SENDER_EMAIL_EXPR_RESOLUTION,
                        A_KNOWN_DESTINATION_EMAIL_EXPR, A_KNOWN_DESTINATION_EMAIL_EXPR_RESOLUTION
                )
        );

        //act:
        Email result = theInstance.processEmail(aToProcessEmail, exprInputData);

        //assert:
        verify(mockProcessor, times(1)).initWith(matchExpressionInputData(exprInputData));
        verify(mockProcessor, times(1)).translateValue(eq(aTemplate), eq(A_KNOWN_EXPRESSION), anyString());
        verify(mockProcessor, times(1)).processTemplateValue(eq(A_KNOWN_EXPRESSION), anyString());
        verify(mockProcessor, times(1)).processTemplateValue(eq(A_KNOWN_SENDER_EMAIL_EXPR), anyString());
        verify(mockProcessor, times(1)).processTemplateValue(eq(A_KNOWN_DESTINATION_EMAIL_EXPR), anyString());
        verify(mockProcessor, times(1)).processMultipleExpressions(anyMap());
        verifyNoMoreInteractions(mockProcessor);
        assertEquals(ES_OUTGOING, result.getStatus());
        assertEquals(A_KNOWN_EXPRESSION_RESOLUTION, result.getContent());
        assertEquals(A_KNOWN_SENDER_EMAIL_EXPR_RESOLUTION, result.getFrom());
        assertEquals(A_KNOWN_DESTINATION_EMAIL_EXPR_RESOLUTION, result.getTo());
    }

    /**
     * Test of process expression, template (with language) and (client and
     * server) cards (with binding), of class EmailTemplateProcessorServiceImpl.
     *
     * <p>
     * Was applyEmailTemplateExpr(expr, template, clientCard, serverCard)
     */
    @Test
    public void testProcessExpression_TemplateAndCards() {
        System.out.println("processExpression_TemplateAndCards");

        //arrange:
        String lang = "en";
        String aToProcessExpr = A_KNOWN_EXPRESSION;
        EmailTemplate aTemplate = buildFakeEmailTemplate(1, A_KNOWN_EXPRESSION, lang);
        Card aClientCard = buildCard(1L);
        Card aServerCard = buildCard(1L, A_KNOWN_EXPRESSION_RESOLUTION);
        TemplateProcessorHandler mockProcessor = mockTemplateProcessorHandler();
        EmailTemplateProcessorServiceImpl theInstance = new EmailTemplateProcessorServiceImpl(easytemplateRepository, easytemplateService, userRepository, userSupplier, roleRepository,
                dao, ftlTemplateService, apiService, refAttrHelperService,
                lookupService, reportService, translationService,
                signatureService, languageService, dmsAttachmentDownloader,
                serializationHelper, operatorService) {
            @Override
            protected TemplateProcessorHandler buildTemplateProcessor() {
                return mockProcessor;
            }
        };
        when(mockProcessor.processExpression(eq(A_KNOWN_EXPRESSION))).thenReturn(A_KNOWN_EXPRESSION_RESOLUTION); // resolution
        SimpleExpressionInputData exprInputData = SimpleExpressionInputData.extendedBuilder()
                .withExpression(aToProcessExpr)
                .withTemplate(aTemplate)
                .withClientCard(aClientCard)
                .withServerCard(aServerCard)
                .build();

        //act:
        String result = theInstance.processExpression(exprInputData);

        //assert:
        verify(mockProcessor, times(1)).initWith(matchExpressionInputData(exprInputData));
        verify(mockProcessor, times(1)).processExpression(eq(A_KNOWN_EXPRESSION));
        verifyNoMoreInteractions(mockProcessor);
        assertEquals(A_KNOWN_EXPRESSION_RESOLUTION, result);
    }

    /**
     * Test of process expression, template (with language) and (client) card
     * (with binding), of class EmailTemplateProcessorServiceImpl
     *
     * <p>
     * Was default applyEmailTemplateExpr(expr, template, card)
     */
    @Test
    public void testBuilder_ProcessExpression_TemplateAndClientCard() {
        System.out.println("processExpression_TemplateAndClientCard");

        //arrange:
        String lang = "en";
        String aToProcessExpr = A_KNOWN_EXPRESSION;
        EmailTemplate aTemplate = buildFakeEmailTemplate(1, A_KNOWN_EXPRESSION, lang);
        Card aClientCard = buildCard(1L, A_KNOWN_EXPRESSION_RESOLUTION);
        TemplateProcessorHandler mockProcessor = mockTemplateProcessorHandler();
        EmailTemplateProcessorServiceImpl theInstance = new EmailTemplateProcessorServiceImpl(easytemplateRepository, easytemplateService, userRepository, userSupplier, roleRepository,
                dao, ftlTemplateService, apiService, refAttrHelperService,
                lookupService, reportService, translationService,
                signatureService, languageService, dmsAttachmentDownloader,
                serializationHelper, operatorService) {
            @Override
            protected TemplateProcessorHandler buildTemplateProcessor() {
                return mockProcessor;
            }
        };
        when(mockProcessor.processExpression(eq(A_KNOWN_EXPRESSION))).thenReturn(A_KNOWN_EXPRESSION_RESOLUTION); // resolution
        SimpleExpressionInputData exprInputData = SimpleExpressionInputData.extendedBuilder()
                .withExpression(aToProcessExpr)
                .withTemplate(aTemplate)
                .withClientCard(aClientCard)
                .build();

        //act:
        String result = theInstance.processExpression(exprInputData);

        //assert:
        verify(mockProcessor, times(1)).initWith(matchExpressionInputData(exprInputData));
        verify(mockProcessor, times(1)).processExpression(eq(A_KNOWN_EXPRESSION));
        verifyNoMoreInteractions(mockProcessor);
        assertEquals(A_KNOWN_EXPRESSION_RESOLUTION, result);
    }

    /**
     * Test of process expression, with (client) card (with binding), of class
     * EmailTemplateProcessorServiceImpl
     *
     * <p>
     * Was default applyEmailTemplateExpr(expr, card)
     */
    @Test
    public void testProcessExpression_ClientCard() {
        System.out.println("processExpression_ClientCard");

        //arrange:
        String aToProcessExpr = A_KNOWN_EXPRESSION;
        Card aClientCard = buildCard(1L, A_KNOWN_EXPRESSION_RESOLUTION);
        TemplateProcessorHandler mockProcessor = mockTemplateProcessorHandler();
        EmailTemplateProcessorServiceImpl theInstance = new EmailTemplateProcessorServiceImpl(easytemplateRepository, easytemplateService, userRepository, userSupplier, roleRepository,
                dao, ftlTemplateService, apiService, refAttrHelperService,
                lookupService, reportService, translationService,
                signatureService, languageService, dmsAttachmentDownloader,
                serializationHelper, operatorService) {
            @Override
            protected TemplateProcessorHandler buildTemplateProcessor() {
                return mockProcessor;
            }
        };
        when(mockProcessor.processExpression(eq(A_KNOWN_EXPRESSION))).thenReturn(A_KNOWN_EXPRESSION_RESOLUTION); // resolution
        SimpleExpressionInputData exprInputData = SimpleExpressionInputData.extendedBuilder()
                .withExpression(aToProcessExpr)
                .withClientCard(aClientCard)
                .build();

        //act:
        String result = theInstance.processExpression(exprInputData);

        //assert:
        verify(mockProcessor, times(1)).initWith(matchExpressionInputData(exprInputData));
        verify(mockProcessor, times(1)).processExpression(eq(A_KNOWN_EXPRESSION));
        verifyNoMoreInteractions(mockProcessor);
        assertEquals(A_KNOWN_EXPRESSION_RESOLUTION, result);
    }

    /**
     * Test of process expression, with given language and (client) card (with
     * binding), of class EmailTemplateProcessorServiceImpl
     *
     *
     * <p>
     * Was default applyEmailTemplateExpr(expr, card, language)
     */
    @Test
    public void testProcessExpression_ClientCardAndForcedLanguage() {
        System.out.println("processExpression_ClientCardAndForcedLanguage");

        //arrange:
        String aLanguage = "es";
        String aToProcessExpr = A_KNOWN_EXPRESSION;
        Card aClientCard = buildCard(1L, A_KNOWN_EXPRESSION_RESOLUTION);
        TemplateProcessorHandler mockProcessor = mockTemplateProcessorHandler();
        EmailTemplateProcessorServiceImpl theInstance = new EmailTemplateProcessorServiceImpl(easytemplateRepository, easytemplateService, userRepository, userSupplier, roleRepository,
                dao, ftlTemplateService, apiService, refAttrHelperService,
                lookupService, reportService, translationService,
                signatureService, languageService, dmsAttachmentDownloader,
                serializationHelper, operatorService) {
            @Override
            protected TemplateProcessorHandler buildTemplateProcessor() {
                return mockProcessor;
            }
        };
        when(mockProcessor.processExpression(eq(A_KNOWN_EXPRESSION))).thenReturn(A_KNOWN_EXPRESSION_RESOLUTION); // resolution
        SimpleExpressionInputData exprInputData = SimpleExpressionInputData.extendedBuilder()
                .withExpression(aToProcessExpr)
                .withClientCard(aClientCard)
                .withForcedLanguage(aLanguage)
                .build();

        //act:
        String result = theInstance.processExpression(exprInputData);

        //assert:
        verify(mockProcessor, times(1)).initWith(matchExpressionInputData(exprInputData));
        verify(mockProcessor, times(1)).processExpression(eq(A_KNOWN_EXPRESSION));
        verifyNoMoreInteractions(mockProcessor);
        assertEquals(A_KNOWN_EXPRESSION_RESOLUTION, result);
    }

    /**
     * Test of process multiple expressions, with received email and
     * mapperConfig (containing bindings), of class
     * EmailTemplateProcessorServiceImpl.
     *
     * <p>
     * Was applyEmailTemplate(expressions, receivedEmail, mapperConfig)
     */
    @Test
    public void testProcessMultipleExpressions_ReceivedEmailAndMapperConfig() {
        System.out.println("processMultipleExpressions_ReceivedEmailAndMapperConfig");

        //arrange:
        String aExpr = A_KNOWN_EXPRESSION;
        FluentMap<String, ?> expressions = buildMapObj(aExpr);
        Email aReceivedEmail = buildReceivedEmail();
        MapperConfig aMapperConfig = buildMapperConfig();
        TemplateProcessorHandler mockProcessor = mockTemplateProcessorHandler();
        EmailTemplateProcessorServiceImpl theInstance = new EmailTemplateProcessorServiceImpl(easytemplateRepository, easytemplateService, userRepository, userSupplier, roleRepository,
                dao, ftlTemplateService, apiService, refAttrHelperService,
                lookupService, reportService, translationService,
                signatureService, languageService, dmsAttachmentDownloader,
                serializationHelper, operatorService) {
            @Override
            protected TemplateProcessorHandler buildTemplateProcessor() {
                return mockProcessor;
            }
        };
        final Map<String, Object> exprResolutions = buildMapObj(A_KNOWN_EXPRESSION_RESOLUTION);
        when(mockProcessor.processMultipleExpressions(eq(expressions))).thenReturn(exprResolutions); // resolution
        SimpleExpressionInputData exprInputData = SimpleExpressionInputData.extendedBuilder()
                .withExpressions(expressions)
                .withMapperConfig(aMapperConfig)
                .withReceivedEmail(aReceivedEmail)
                .build();

        //act:
        Map<String, Object> result = theInstance.processMultipleExpressions(exprInputData);

        //assert:
        verify(mockProcessor, times(1)).initWith(matchExpressionInputData(exprInputData));
        verify(mockProcessor, times(1)).processMultipleExpressions(eq(expressions));
        verifyNoMoreInteractions(mockProcessor);
        checkEquals_Map(exprResolutions, result);
    }

    /**
     * Test of process multiple expressions, with (client) card (containing
     * bindings), of class EmailTemplateProcessorServiceImpl.
     *
     * <p>
     * Was applyEmailTemplateExprs(card, expressions)
     */
    @Test
    public void testProcessMultipleExpressions_ClientCard() {
        System.out.println("processMultipleExpressions_ClientCard");

        //arrange:
        String aExpr = A_KNOWN_EXPRESSION;
        FluentMap<String, ?> expressions = buildMapObj(aExpr);
        Card aClientCard = buildCard(1L, A_KNOWN_EXPRESSION_RESOLUTION);
        TemplateProcessorHandler mockProcessor = mockTemplateProcessorHandler();
        EmailTemplateProcessorServiceImpl theInstance = new EmailTemplateProcessorServiceImpl(easytemplateRepository, easytemplateService, userRepository, userSupplier, roleRepository,
                dao, ftlTemplateService, apiService, refAttrHelperService,
                lookupService, reportService, translationService,
                signatureService, languageService, dmsAttachmentDownloader,
                serializationHelper, operatorService) {
            @Override
            protected TemplateProcessorHandler buildTemplateProcessor() {
                return mockProcessor;
            }
        };
        final Map<String, Object> exprResolutions = buildMapObj(A_KNOWN_EXPRESSION_RESOLUTION);
        when(mockProcessor.processMultipleExpressions(eq(expressions))).thenReturn(exprResolutions); // resolution
        SimpleExpressionInputData exprInputData = SimpleExpressionInputData.extendedBuilder()
                .withExpressions(expressions)
                .withClientCard(aClientCard)
                .build();

        //act:
        Map<String, Object> result = theInstance.processMultipleExpressions(exprInputData);

        //assert:
        verify(mockProcessor, times(1)).initWith(matchExpressionInputData(exprInputData));
        verify(mockProcessor, times(1)).processMultipleExpressions(eq(expressions));
        verifyNoMoreInteractions(mockProcessor);
        checkEquals_Map(exprResolutions, result);
    }

    /**
     * Test of process multiple expressions, (client) card and language, of
     * class * EmailTemplateProcessorServiceImpl.
     *
     * <p>
     * Was applyEmailTemplateExprs(card, expressions, language)
     */
    @Test
    public void testprocessMultipleExpressions_ClientCardAndLanguage() {
        System.out.println("processMultipleExpressions_ClientCardAndLanguage");

        //arrange:
        String aLanguage = "es";
        String aExpr = A_KNOWN_EXPRESSION;
        FluentMap<String, ?> expressions = buildMapObj(aExpr);
        Card aClientCard = buildCard(1L, A_KNOWN_EXPRESSION_RESOLUTION);
        TemplateProcessorHandler mockProcessor = mockTemplateProcessorHandler();
        EmailTemplateProcessorServiceImpl theInstance = new EmailTemplateProcessorServiceImpl(easytemplateRepository, easytemplateService, userRepository, userSupplier, roleRepository,
                dao, ftlTemplateService, apiService, refAttrHelperService,
                lookupService, reportService, translationService,
                signatureService, languageService, dmsAttachmentDownloader,
                serializationHelper, operatorService) {
            @Override
            protected TemplateProcessorHandler buildTemplateProcessor() {
                return mockProcessor;
            }
        };
        final Map<String, Object> exprResolutions = buildMapObj(A_KNOWN_EXPRESSION_RESOLUTION);
        when(mockProcessor.processMultipleExpressions(eq(expressions))).thenReturn(exprResolutions); // resolution
        SimpleExpressionInputData exprInputData = SimpleExpressionInputData.extendedBuilder()
                .withExpressions(expressions)
                .withClientCard(aClientCard)
                .withForcedLanguage(aLanguage)
                .build();

        //act:
        Map<String, Object> result = theInstance.processMultipleExpressions(exprInputData);

        //assert:
        verify(mockProcessor, times(1)).initWith(matchExpressionInputData(exprInputData));
        verify(mockProcessor, times(1)).processMultipleExpressions(eq(expressions));
        verifyNoMoreInteractions(mockProcessor);
        checkEquals_Map(exprResolutions, result);
    }

    /**
     * Test of process multiple expressions, of class
     * EmailTemplateProcessorServiceImpl.
     *
     * <p>
     * Was default applyEmailTemplateExprs(expressions)
     */
    @Test
    public void testprocessMultipleExpressions() {
        System.out.println("processMultipleExpressions");

        //arrange:
        String aExpr = A_KNOWN_EXPRESSION;
        FluentMap<String, ?> expressions = buildMapObj(aExpr);
        TemplateProcessorHandler mockProcessor = mockTemplateProcessorHandler();
        EmailTemplateProcessorServiceImpl theInstance = new EmailTemplateProcessorServiceImpl(easytemplateRepository, easytemplateService, userRepository, userSupplier, roleRepository,
                dao, ftlTemplateService, apiService, refAttrHelperService,
                lookupService, reportService, translationService,
                signatureService, languageService, dmsAttachmentDownloader,
                serializationHelper, operatorService) {
            @Override
            protected TemplateProcessorHandler buildTemplateProcessor() {
                return mockProcessor;
            }
        };
        final Map<String, Object> exprResolutions = buildMapObj(A_KNOWN_EXPRESSION_RESOLUTION);
        when(mockProcessor.processMultipleExpressions(eq(expressions))).thenReturn(exprResolutions); // resolution
        SimpleExpressionInputData exprInputData = SimpleExpressionInputData.extendedBuilder()
                .withExpressions(expressions)
                .build();

        //act:
        Map<String, Object> result = theInstance.processMultipleExpressions(exprInputData);

        //assert:
        verify(mockProcessor, times(1)).initWith(matchExpressionInputData(exprInputData));
        verify(mockProcessor, times(1)).processMultipleExpressions(eq(expressions));
        verifyNoMoreInteractions(mockProcessor);
        checkEquals_Map(exprResolutions, result);
    }

    /**
     * Default behavior:
     * {@link TemplateProcessorHandler#translateValue(org.cmdbuild.email.template.EmailTemplate, java.lang.String, java.lang.String)}
     * and
     * {@link TemplateProcessorHandler#processTemplateValue(org.cmdbuild.template.ExpressionProcessor, java.lang.String, java.lang.String)}
     * returns the expression/value given
     *
     * @return
     */
    private TemplateProcessorHandler mockTemplateProcessorHandler() {
        TemplateProcessorHandler mockProcessor = mock(TemplateProcessorHandler.class);
        when(mockProcessor.processTemplateValue(anyString(), any())).then(i -> A_KNOWN_EXPRESSION.equals(i.getArgumentAt(0, String.class)) ? A_KNOWN_EXPRESSION_RESOLUTION : i.getArgumentAt(0, String.class));
        when(mockProcessor.translateValue(any(), anyString(), anyString())).then(returnsSecondArg());
        return mockProcessor;
    }

    private static EmailAttachmentImpl mock_buildEmailAttachment_PDF(final String fileName) {
        return EmailAttachmentImpl.builder().withFileName(fileName)
                .withData(new byte[0])
                .withContentType("application/pdf")
                .build();
    }

    private static EmailTemplateImpl buildFakeEmailTemplate(long id, final String content) {
        return EmailTemplateImpl.builder()
                .withId(id)
                .withCode(format("<ATemplate_%s>", 1))
                .withTextPlainContentType()
                .withContent(content)
                .build();
    }

    private static EmailTemplate buildFakeEmailTemplate(long id, final String content, String language) {
        EmailTemplate template = mock(EmailTemplate.class);
        when(template.getId()).thenReturn(id);
        when(template.getCode()).thenReturn(format("<ATemplate_%s>", 1));
        when(template.getContentType()).thenReturn("text/plain");
        when(template.getContent()).thenReturn(content);
        when(template.hasLangExpr()).thenReturn(true);
        when(template.getLangExpr()).thenReturn(language);

        return template;
    }

    private static EmailTemplate buildFakeEmailTemplate_WithAddresses(long id, final String content, String language) {
        EmailTemplate template = mock(EmailTemplate.class);
        when(template.getId()).thenReturn(id);
        when(template.getCode()).thenReturn(format("<ATemplate_%s>", 1));
        when(template.getContentType()).thenReturn("text/plain");
        when(template.getContent()).thenReturn(content);
        when(template.hasLangExpr()).thenReturn(true);
        when(template.getLangExpr()).thenReturn(language);

        when(template.getFrom()).thenReturn(A_KNOWN_SENDER_EMAIL_EXPR);
        when(template.getTo()).thenReturn(A_KNOWN_DESTINATION_EMAIL_EXPR);

        return template;
    }

    /**
     * Mock binding of {@link #A_KNOWN_EXPRESSION} to
     * {@link #A_KNOWN_EXPRESSION_RESOLUTION} when invoking
     * {@link TemplateProcessorHandler#translateValue(org.cmdbuild.email.template.EmailTemplate, java.lang.String, java.lang.String)}
     *
     * @param mockProcessor
     * @param aTemplate
     */
    private void mockBindingKnownExpression_onTranslateValue(TemplateProcessorHandler mockProcessor, EmailTemplate aTemplate) {
        when(mockProcessor.translateValue(eq(aTemplate), eq(A_KNOWN_EXPRESSION), any())).thenReturn(A_KNOWN_EXPRESSION_RESOLUTION);
        when(mockProcessor.processTemplateValue(any(), any())).then(returnsFirstArg());
    }

    /**
     * Mock binding of {@link #A_KNOWN_EXPRESSION} to
     * {@link #A_KNOWN_EXPRESSION_RESOLUTION} when invoking
     * {@link TemplateProcessorHandler#processTemplateValue(java.lang.String, java.lang.String)}
     * done in:
     * <ul>
     * <li>{@link ExpressionInputData#clientData};
     * <li>{@link ExpressionInputData#serverData};
     * <li>{@link ExpressionInputData#receivedEmail};
     * <li>{@link ExpressionInputData#otherData}.
     * </ul>
     *
     * @param mockProcessor
     * @param aTemplate
     * @param expr
     * @param exprResolution
     */
    private void mockBindingKnownExpression_onProcessTemplateValue_ExpressionProcessor(TemplateProcessorHandler mockProcessor, EmailTemplate aTemplate,
            Map<String, String> exprBindings) {
        exprBindings.forEach((k, v) -> {
            when(mockProcessor.translateValue(eq(aTemplate), eq(k), any())).thenReturn(k); // Identity
            when(mockProcessor.processTemplateValue(eq(k), any())).thenReturn(v); // resolution
        });
    }

    /**
     * Mockito <i>argument matcher</i> that accepts a <i>lambda expression</i>.
     *
     * <p>
     * <b>Usage:</b>
     * <p>
     * <code>verify(mockedFoo).doThing(argThat(matches( (Bar arg) -> arg.getI() == 5 )));</code>
     *
     * @see <a href="https://stackoverflow.com/a/31993440">Can Mockito verify an
     * argument has certain properties/fields?</a>
     * @param <T>
     * @param predicate
     * @return
     */
    private static <T> ArgumentMatcher<T> matches(Predicate<T> predicate) {
        return new ArgumentMatcher<T>() {

            @SuppressWarnings("unchecked")
            @Override
            public boolean matches(Object argument) {
                return predicate.test((T) argument);
            }
        };
    }

    private CardImpl buildCard(Classe type, Map<String, Object> attributes) {
        return builder().withType(type).withAttributes(attributes).build();
    }

    private CardImpl buildCard(long cardId) {
        return builder().withId(cardId).withType(classe).build();
    }

    private CardImpl buildCard(long cardId, String description) {
        return builder().withId(cardId).withAttribute(ATTR_DESCRIPTION, description).withType(classe).build();
    }

    private CardImpl buildSynthesizedCardFor(Map<String, Object> data) {
        return CardImpl.buildCard(ClasseImpl.builder().withName("DUMMY").build(), data);
    }

    private Email buildToProcessEmail(final String content) {
        return buildEmail("My Subject", content, ES_OUTGOING);
    }

    private Email buildToProcessEmail_NoAddresses(String content) {
        return EmailImpl.builder()
                .withStatus(ES_OUTGOING)
                .withSubject("My Subject")
                .withContent(content)
                .build();
    }

    private Email buildReceivedEmail() {
        return buildEmail("The (received) Subject", "The (received) dcontent", EmailStatus.ES_RECEIVED);
    }

    private Email buildEmail(String subject, String content, EmailStatus status) {
        return EmailImpl.builder()
                .withStatus(status)
                .withFrom(A_KNOWN_SENDER_EMAIL_EXPR_RESOLUTION).withTo(A_KNOWN_DESTINATION_EMAIL_EXPR_RESOLUTION)
                .withSubject(subject)
                .withContent(content)
                .build();
    }

    private FluentMap<String, String> buildMap(String aExpr) {
        FluentMap<String, String> expressions = map();
        expressions.with(A_KNOWN_KEY, aExpr
        );
        return expressions;
    }

    private FluentMap<String, Object> buildMapObj(String aExpr) {
        FluentMap<String, Object> expressions = map();
        expressions.with(A_KNOWN_KEY, aExpr
        );
        return expressions;
    }

    private MapperConfig buildMapperConfig() {
        FluentMap<String, String> aMap = map();
        aMap.with(A_KNOWN_EXPRESSION, A_KNOWN_EXPRESSION_RESOLUTION);
        return new MapperConfigImpl(aMap);
    }

    /**
     * If a value differs for a key in the two input maps, in the identifying
     * message for the {@link AssertionError} there will be a list of expResult
     * value (<i>left value</i>) and result value (<i>right value</i>).
     *
     * @param expResult
     * @param result
     */
    private void checkEquals_Map(Map<String, Object> expResult, Map<String, Object> result) {
        MapDifference<String, Object> mapDifference = Maps.difference(expResult, result);
        Map<String, Object> missing = mapDifference.entriesOnlyOnLeft();
        assertTrue(format("Expected but missing: %s", missing), missing.isEmpty());
        Map<String, Object> unexpected = mapDifference.entriesOnlyOnRight();
        assertTrue(format("Actual has unexpected: %s", unexpected), unexpected.isEmpty());
        Map<String, ValueDifference<Object>> differing = mapDifference.entriesDiffering();
        assertTrue(format("Expected and actual differs in: %s", differing),
                differing.isEmpty());
    }

    private void mockHistoryCount(int count) {
        when(dao.selectCount()).thenReturn(queryBuilder);
        when(dao.selectCount().from(anyString())).thenReturn(queryBuilder);
        when(dao.selectCount().from(anyString()).includeHistory()).thenReturn(queryBuilder);
        when(dao.selectCount().from(anyString()).where(anyString(), any(WhereOperator.class), anyList())).thenReturn(queryBuilder);

        when(dao.selectCount().from(anyString()).where(anyString(), any(WhereOperator.class), anyList()).getCount()).thenReturn(Long.valueOf(count));
        when(dao.selectCount().from(anyString()).includeHistory().where(anyString(), any(WhereOperator.class), anyList()).getCount()).thenReturn(Long.valueOf(count));
    }

    private static ExpressionInputData matchExpressionInputData(ExpressionInputData exprInputData) {
        return argThat(new ExpressionInputDataMatcher(exprInputData));
    }

}

class ExpressionInputDataMatcher extends ArgumentMatcher<ExpressionInputData> {

    private final ExpressionInputData left;

    ExpressionInputDataMatcher(ExpressionInputData left) {
        this.left = left;
    }

    @Override
    public boolean matches(Object obj) {
        ExpressionInputData right = (ExpressionInputData) obj;

        checkEquals_Map(left.getOtherData(), right.getOtherData());
        if (left.getTemplateContextData() instanceof Map && right.getTemplateContextData() instanceof Map) {
            checkEquals_Map((Map<String, Object>) left.getTemplateContextData(), (Map<String, Object>) right.getTemplateContextData());
        }
        return checkEquals_Card(left.getClientCard(), right.getClientCard())
                && checkEquals_Card(left.getServerCard(), right.getServerCard())
                && Objects.equals(left.getForcedLanguage(), right.getForcedLanguage())
                && Objects.equals(left.getMapperConfig(), right.getMapperConfig())
                && Objects.equals(left.getReceivedEmail(), right.getReceivedEmail())
                && Objects.equals(left.getTemplate(), right.getTemplate());
    }

    /**
     * If a value differs for a key in the two input maps, in the identifying
     * message for the {@link AssertionError} there will be a list of expResult
     * value (<i>left value</i>) and result value (<i>right value</i>).
     *
     * @param expResult
     * @param result
     */
    private void checkEquals_Map(Map<String, Object> expResult, Map<String, Object> result) {
        MapDifference<String, Object> mapDifference = Maps.difference(expResult, result);
        Map<String, Object> missing = mapDifference.entriesOnlyOnLeft();
        assertTrue(format("Expected but missing: %s", missing), missing.isEmpty());
        Map<String, Object> unexpected = mapDifference.entriesOnlyOnRight();
        assertTrue(format("Actual has unexpected: %s", unexpected), unexpected.isEmpty());
        Map<String, ValueDifference<Object>> differing = mapDifference.entriesDiffering();
        assertTrue(format("Expected and actual differs in: %s", differing),
                differing.isEmpty());
    }

    private boolean checkEquals_Card(Card left, Card right) {
        if (left == null && right == null) {
            return true;
        }

        return Objects.equals(left.getTypeName(), right.getTypeName())
                && Objects.equals(left.getCode(), right.getCode())
                && Objects.equals(left.getDescription(), right.getDescription());
    }

    private boolean checkEquals_MapperConfig(MapperConfig left, MapperConfig right) {
        if (left == null && right == null) {
            return true;
        }

        return Objects.equals(left.getKeyBegin(), right.getKeyBegin())
                && Objects.equals(left.getKeyEnd(), right.getKeyEnd())
                && Objects.equals(left.getValueBegin(), right.getValueBegin())
                && Objects.equals(left.getValueEnd(), right.getValueEnd());
    }
} // end NotificationDataMatcher class
