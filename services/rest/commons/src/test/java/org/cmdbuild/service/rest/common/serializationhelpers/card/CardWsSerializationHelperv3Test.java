/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.service.rest.common.serializationhelpers.card;

import static java.util.Arrays.asList;
import java.util.List;
import java.util.Map;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import org.cmdbuild.dao.core.q3.WhereOperator;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.ClassType;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dms.DmsService;
import static org.cmdbuild.dms.DmsService.DOCUMENT_ATTR_DOCUMENTID;
import org.cmdbuild.dms.dao.DmsModelDocument;
import org.cmdbuild.dms.dao.DocumentInfoRepository;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.services.permissions.DummyPermissionsHandler;
import org.cmdbuild.services.permissions.PermissionsHandlerProxy;
import org.cmdbuild.services.serialization.attribute.file.CardAttributeFileHelper;
import org.cmdbuild.services.serialization.widget.WidgetHelper;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.widget.WidgetService;
import org.cmdbuild.widget.model.Widget;
import org.cmdbuild.widget.model.WidgetData;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author afelice
 */
public class CardWsSerializationHelperv3Test {

    private CardWsSerializationHelperv3 instance;

    private final Classe classe = mock(Classe.class);
    private final Classe classeFile = mock(Classe.class);

    private final DaoService dao = mock(DaoService.class);
    private final DmsService dmsService = mock(DmsService.class);
    private final ObjectTranslationService translationService = mock(ObjectTranslationService.class);
    private final ClassSerializationHelper classSerializationHelper = mock(ClassSerializationHelper.class);
    private final AttributeTypeConversionService attributeTypeConversionService = mock(AttributeTypeConversionService.class);
    private final WidgetService widgetService = mock(WidgetService.class);
    private final UserClassService userClassService = mock(UserClassService.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final DocumentInfoRepository repository = mock(DocumentInfoRepository.class);
    private final QueryBuilder queryBuilder = mock(QueryBuilder.class);

    @Before
    public void setUp() {
        // Skip all permissions check
        PermissionsHandlerProxy permissionsHandler = new DummyPermissionsHandler();

        instance = new CardWsSerializationHelperv3(dao, translationService, classSerializationHelper,
                attributeTypeConversionService, widgetService, userRepository,
                new CardAttributeFileHelper(dao, dmsService, repository, userClassService, translationService, userRepository,
                        permissionsHandler),
                new WidgetHelper(widgetService, translationService, permissionsHandler),
                permissionsHandler);

        when(classe.getName()).thenReturn("Email"); // Used in serializeAttachment() to skip permission ckeck from userClassService; used in CardAttributeFile_Helper.fillWithCardMetadata()
        when(classe.getClassType()).thenReturn(ClassType.CT_SIMPLE); // Used in CardImpl contructor
        final List<Attribute> fileAttributeList = list(TestDmsWithWidgetsHelper.mockBuildAttr_String("Notes", classeFile),
                TestDmsWithWidgetsHelper.mockBuildAttr_Integer("Size", classeFile),
                TestDmsWithWidgetsHelper.mockBuildAttr_String("Hash", classeFile),
                TestDmsWithWidgetsHelper.mockBuildAttr_String("FileName", classeFile));

        // Used in CardAttributeFile_WsSerializer.serializeRelatedCard() when adding serialization for (synthesized) Card related to FILE Attribute (**recursively** calling addCardValuesAndDescriptionsAndExtras()
        when(classe.getServiceAttributes()).thenReturn(fileAttributeList);

        // used in CardAttributeFileHelper.loadDocument
        when(dao.selectCount()).thenReturn(queryBuilder);
        when(dao.selectCount().from(anyString())).thenReturn(queryBuilder);
        when(dao.selectCount().from(anyString()).includeHistory()).thenReturn(queryBuilder);
        when(dao.selectCount().from(anyString()).where(anyString(), any(WhereOperator.class), anyList())).thenReturn(queryBuilder);

        when(dao.selectCount().from(anyString()).where(anyString(), any(WhereOperator.class), anyList()).getCount()).thenReturn(Long.valueOf(1));
        when(dao.selectCount().from(anyString()).includeHistory().where(anyString(), any(WhereOperator.class), anyList()).getCount()).thenReturn(Long.valueOf(1));
    }

    /**
     * Test of serializeAttributeValue method, for File attribute, <b>without a
     * DMS</b>, category active, of class CardWsSerializationHelperv3.
     */ 
    @Test
    public void testSerializeAttributeValue_FileAttribute_NoDms_CategoryActive() {
        System.out.println("serializeAttributeValue_FileAttribute_NoDms_CategoryActive");

        //arrange:
        final String fileAttrName = "FileAttribute";
        final Long cardId = 22L;

        // Attribute
        Attribute fileAttribute = TestDmsWithWidgetsHelper.mockBuildAttr_File(fileAttrName, classe, TestDmsWithWidgetsHelper.A_KNOWN_CATEGORY_DESCRIPTION);
        // ...and related card
        Card aRelatedCard = CardImpl.builder()
                .withId(cardId).withType(classe)
                .withAttribute("MyFleAttr", TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_ID)
                .withAttribute(DOCUMENT_ATTR_DOCUMENTID, TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_ID)
                // Used in CardWsSerializationHelperv3.serializeCard() when adding serialization for (synthesized) Card related to FILE Attribute (**recursively** calling addCardValuesAndDescriptionsAndExtras()
                .withAttribute("Size", 211)
                .withAttribute("Hash", TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_HASH)
                .withAttribute("FileName", TestDmsWithWidgetsHelper.A_KNOWN_FILENAME)
                .build();
        FluentMap<String, Object> cardData = map(fileAttrName, cardId);

        DmsModelDocument aDocument = TestDmsWithWidgetsHelper.mockBuildDmsModelDocument(aRelatedCard);

        // No DMS
        boolean isDmsServiceOk = false;
        TestDmsWithWidgetsHelper.mockDmsService(dmsService, isDmsServiceOk);
        // Mock persistence
        QueryBuilder mockQueryBuilder = TestDmsWithWidgetsHelper.mockQueryBuilder_SelectAll(dao);

        TestDmsWithWidgetsHelper.mockQueryBuilder_where_Long(mockQueryBuilder, "DmsModel");
        when(mockQueryBuilder.getCard()).thenReturn(aRelatedCard);

        TestDmsWithWidgetsHelper.mockQueryBuilder_whereExpr(mockQueryBuilder, aRelatedCard.getTypeName());
        when(mockQueryBuilder.getCardOrNull()).thenReturn(aRelatedCard);

        when(repository.getById(eq(TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_ID))).thenReturn(aDocument);
        // Category
        boolean categoryActive = true;
        org.cmdbuild.lookup.LookupValue aCategory = TestDmsWithWidgetsHelper.mockCategory(categoryActive);
        when(dmsService.getCategoryLookupForAttachment(any(), any())).thenReturn(aCategory);

        FluentMap<String, Object> expResult = map(fileAttrName, TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_ID,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_category"), TestDmsWithWidgetsHelper.A_KNOWN_CATEGORY_LOOKUP_ID,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_category_name"), TestDmsWithWidgetsHelper.A_KNOWN_CATEGORY_CODE,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_category_description"), TestDmsWithWidgetsHelper.A_KNOWN_CATEGORY_DESCRIPTION,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_category_description_translation"), null,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_can_update"), true,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_can_delete"), true,
                // Card related to FileAttribute
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_card"), cardId,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "Notes"), null,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "Size"), 211,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "Hash"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_HASH,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "FileName"), TestDmsWithWidgetsHelper.A_KNOWN_FILENAME,
                // (end Card)
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "name"), TestDmsWithWidgetsHelper.A_KNOWN_FILENAME,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "category"), TestDmsWithWidgetsHelper.A_KNOWN_CATEGORY_LOOKUP_ID,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "Category"), TestDmsWithWidgetsHelper.A_KNOWN_CATEGORY_LOOKUP_ID, // for legacy
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "description"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_DESCRIPTION,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "Description"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_DESCRIPTION, // For legacy
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "version"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_VERSION,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "author"), TestDmsWithWidgetsHelper.A_KNOWN_AUTHOR,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_author_description"), TestDmsWithWidgetsHelper.A_KNOWN_AUTHOR,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "created"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_CREATION_DATE_STR,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "modified"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_MODIFICATION_DATE_STR,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "isDmsServiceOk"), isDmsServiceOk);

        //act:
        FluentMap<String, Object> result = instance.serializeAttributeValue(fileAttribute, cardData);

        //assert:
        TestDmsWithWidgetsHelper.checkEquals_Map(expResult, result);
    }

    /**
     * Test of serializeAttributeValue method, for File attribute, with a DMS
     * enabled, category active, of class CardWsSerializationHelperv3.
     */
    @Test
    public void testSerializeAttributeValue_FileAttribute_Dms_CategoryActive() {
        System.out.println("serializeAttributeValue_FileAttribute_Dms_CategoryActive");

        //arrange:
        final String fileAttrName = "FileAttribute";
        final Long cardId = 22L;

        // Attribute
        Attribute fileAttribute = TestDmsWithWidgetsHelper.mockBuildAttr_File(fileAttrName, classe, TestDmsWithWidgetsHelper.A_KNOWN_CATEGORY_DESCRIPTION);
        // ...and related card
        Card aRelatedCard = CardImpl.builder()
                .withId(cardId).withType(classe)
                .withAttribute("MyFleAttr", TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_ID)
                .build();

        DocumentInfoAndDetail aDocument = TestDmsWithWidgetsHelper.mockBuildDocumentInfoAndDetail(aRelatedCard);

        // DMS
        boolean isDmsServiceOk = true;
        TestDmsWithWidgetsHelper.mockDmsService(dmsService, isDmsServiceOk);
        FluentMap<String, Object> cardData = map(fileAttrName, TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_FULL_GENERATED_CARD_ID);
        when(dmsService.getCardAttachmentByMetadataId(TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_FULL_GENERATED_CARD_ID)).thenReturn(aDocument);
        // Category
        boolean categoryActive = true;
        org.cmdbuild.lookup.LookupValue aCategory = TestDmsWithWidgetsHelper.mockCategory(categoryActive);
        when(dmsService.getCategoryLookupForAttachment(any(), any())).thenReturn(aCategory);

        FluentMap<String, Object> expResult = map(fileAttrName, TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_ID,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_category"), TestDmsWithWidgetsHelper.A_KNOWN_CATEGORY_LOOKUP_ID,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_category_name"), TestDmsWithWidgetsHelper.A_KNOWN_CATEGORY_CODE,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_category_description"), TestDmsWithWidgetsHelper.A_KNOWN_CATEGORY_DESCRIPTION,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_category_description_translation"), null,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_can_update"), true,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_can_delete"), true,
                // Card related to FileAttribute
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_card"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_FULL_GENERATED_CARD_ID,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "Notes"), null,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "Size"), null,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "Hash"), null,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "FileName"), null,
                // (end Card)
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "name"), TestDmsWithWidgetsHelper.A_KNOWN_FILENAME,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "category"), TestDmsWithWidgetsHelper.A_KNOWN_CATEGORY_LOOKUP_ID,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "Category"), TestDmsWithWidgetsHelper.A_KNOWN_CATEGORY_LOOKUP_ID, // for legacy
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "description"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_DESCRIPTION,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "Description"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_DESCRIPTION, // For legacy
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "version"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_VERSION,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "author"), TestDmsWithWidgetsHelper.A_KNOWN_AUTHOR,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_author_description"), TestDmsWithWidgetsHelper.A_KNOWN_AUTHOR,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "created"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_CREATION_DATE_STR,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "modified"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_MODIFICATION_DATE_STR,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "isDmsServiceOk"), isDmsServiceOk);

        //act:
        FluentMap<String, Object> result = instance.serializeAttributeValue(fileAttribute, cardData);

        //assert:
        TestDmsWithWidgetsHelper.checkEquals_Map(expResult, result);
    }

    /**
     * Test of serializeAttributeValue method, for File attribute, with a DMS
     * enabled, category active, <b>not loaded related card</b> (as from {@link AttachmentWsHelper#readMany()) if no
     * <code>wsQueryOptions.isDetailed()</code> was set, of class
     * CardWsSerializationHelperv3.
     */
    @Test
    public void testSerializeAttributeValue_FileAttribute_Dms_CategoryActive_NotLoadedRelatedCard() {
        System.out.println("serializeAttributeValue_FileAttribute_Dms_CategoryActive_NotLoadedRelatedCard");

        //arrange:
        final String fileAttrName = "FileAttribute";

        // Attribute
        Attribute fileAttribute = TestDmsWithWidgetsHelper.mockBuildAttr_File(fileAttrName, classe, TestDmsWithWidgetsHelper.A_KNOWN_CATEGORY_DESCRIPTION);
        // Withoud related card

        DocumentInfoAndDetail aDocument = TestDmsWithWidgetsHelper.mockBuildDocumentInfoAndDetail(null);

        // DMS
        boolean isDmsServiceOk = true;
        TestDmsWithWidgetsHelper.mockDmsService(dmsService, isDmsServiceOk);
        FluentMap<String, Object> cardData = map(fileAttrName, TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_FULL_GENERATED_CARD_ID);
        when(dmsService.getCardAttachmentByMetadataId(TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_FULL_GENERATED_CARD_ID)).thenReturn(aDocument);
        // Category
        boolean categoryActive = true;
        org.cmdbuild.lookup.LookupValue aCategory = TestDmsWithWidgetsHelper.mockCategory(categoryActive);
        when(dmsService.getCategoryLookupForAttachment(any(), any())).thenReturn(aCategory);

        FluentMap<String, Object> expResult = map(fileAttrName, TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_ID,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_category"), TestDmsWithWidgetsHelper.A_KNOWN_CATEGORY_LOOKUP_ID,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_category_name"), TestDmsWithWidgetsHelper.A_KNOWN_CATEGORY_CODE,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_category_description"), TestDmsWithWidgetsHelper.A_KNOWN_CATEGORY_DESCRIPTION,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_category_description_translation"), null,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_can_update"), true,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_can_delete"), true,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "name"), TestDmsWithWidgetsHelper.A_KNOWN_FILENAME,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "category"), TestDmsWithWidgetsHelper.A_KNOWN_CATEGORY_LOOKUP_ID,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "Category"), TestDmsWithWidgetsHelper.A_KNOWN_CATEGORY_LOOKUP_ID, // for legacy
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "description"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_DESCRIPTION,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "Description"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_DESCRIPTION, // For legacy
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "version"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_VERSION,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "author"), TestDmsWithWidgetsHelper.A_KNOWN_AUTHOR,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_author_description"), TestDmsWithWidgetsHelper.A_KNOWN_AUTHOR,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "created"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_CREATION_DATE_STR,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "modified"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_MODIFICATION_DATE_STR,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "isDmsServiceOk"), isDmsServiceOk);

        //act:
        FluentMap<String, Object> result = instance.serializeAttributeValue(fileAttribute, cardData);

        //assert:
        TestDmsWithWidgetsHelper.checkEquals_Map(expResult, result);
    }

    /**
     * Test of serializeAttributeValue method, for File attribute, with a DMS
     * enabled, category <b>not active</b>, of class
     * CardWsSerializationHelperv3.
     */
    @Test
    public void testSerializeAttributeValue_FileAttribute_Dms_CategoryNotActive() {
        System.out.println("serializeAttributeValue_FileAttribute_Dms_CategoryNotActive");

        //arrange:
        final String fileAttrName = "FileAttribute";
        final Long cardId = 22L;

        // Attribute
        Attribute fileAttribute = TestDmsWithWidgetsHelper.mockBuildAttr_File(fileAttrName, classe, TestDmsWithWidgetsHelper.A_KNOWN_CATEGORY_DESCRIPTION);
        // ...and related card
        Card aRelatedCard = CardImpl.builder()
                .withId(cardId).withType(classe)
                .withAttribute("MyFleAttr", TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_ID)
                .build();

        DocumentInfoAndDetail aDocument = TestDmsWithWidgetsHelper.mockBuildDocumentInfoAndDetail(aRelatedCard);
        // DMS
        boolean isDmsServiceOk = true;
        TestDmsWithWidgetsHelper.mockDmsService(dmsService, isDmsServiceOk);
        FluentMap<String, Object> cardData = map(fileAttrName, TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_FULL_GENERATED_CARD_ID);
        when(dmsService.getCardAttachmentByMetadataId(TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_FULL_GENERATED_CARD_ID)).thenReturn(aDocument);

        // Category not active
        boolean categoryActive = false;
        org.cmdbuild.lookup.LookupValue aCategory = TestDmsWithWidgetsHelper.mockCategory(categoryActive);
        when(dmsService.getCategoryLookupForAttachment(any(), any())).thenReturn(aCategory);

        FluentMap<String, Object> expResult = map(fileAttrName, TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_ID,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_category"), null,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_category_name"), null,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_category_description"), null,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_category_description_translation"), null,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_can_update"), false,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_can_delete"), false,
                // Card related to FileAttribute
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_card"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_FULL_GENERATED_CARD_ID,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "Notes"), null,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "Size"), null,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "Hash"), null,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "FileName"), null,
                // (end Card)
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "category"), null,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "Category"), null, // for legacy
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "name"), TestDmsWithWidgetsHelper.A_KNOWN_FILENAME,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "description"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_DESCRIPTION,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "Description"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_DESCRIPTION, // For legacy
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "version"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_VERSION,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "author"), TestDmsWithWidgetsHelper.A_KNOWN_AUTHOR,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "_author_description"), TestDmsWithWidgetsHelper.A_KNOWN_AUTHOR,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "created"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_CREATION_DATE_STR,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "modified"), TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_MODIFICATION_DATE_STR,
                TestDmsWithWidgetsHelper.buildWithUnderscoresProp(fileAttrName, "isDmsServiceOk"), isDmsServiceOk);

        //act:
        FluentMap<String, Object> result = instance.serializeAttributeValue(fileAttribute, cardData);

        //assert:
        TestDmsWithWidgetsHelper.checkEquals_Map(expResult, result);
    }

    /**
     * Test of serializeAttachment method, for card with File attribute,
     * category <b>not active</b>, with
     * <code>includeWidgets && input.hasMetadata()</code>, of class
     * CardWsSerializationHelperv3.
     */
    @Test
    public void testSerializeAttachment_FileAttribute_Dms_CategoryNotActive_IncludeWidgets() {
        System.out.println("serializeAttachment_FileAttribute_Dms_CategoryNotActive_IncludeWidgets");

        //arrange:
        when(classe.getId()).thenReturn(11L);
        when(userClassService.getUserClass(anyString())).thenReturn(classe);
        // Card:
        final Long cardId = 22L;
        Card aRelatedCard = CardImpl.builder()
                .withId(cardId).withType(classe)
                .build();

        DocumentInfoAndDetail input = TestDmsWithWidgetsHelper.mockBuildDocumentInfoAndDetail()
                .withMetadata(aRelatedCard)
                .build();

        // No DMS
        boolean isDmsServiceOk = false;
        TestDmsWithWidgetsHelper.mockDmsService(dmsService, isDmsServiceOk);

        // Category not active
        boolean categoryActive = false;
        org.cmdbuild.lookup.LookupValue aCategory = TestDmsWithWidgetsHelper.mockCategory(categoryActive);
        when(dmsService.getCategoryLookupForAttachment(any(), any())).thenReturn(aCategory);
        when(dmsService.getDefaultDmsCategory()).thenReturn("defaultCategory");
        // Widget
        WidgetData aWidgetData = TestDmsWithWidgetsHelper.mockBuildWidgetData("wId", "aWidgetType", "aWidgetLabel");
        final List<WidgetData> classWidgets = asList(aWidgetData);
        when(widgetService.getAllWidgetsForClass(eq(aRelatedCard.getType()))).thenReturn(classWidgets);
        Widget aWidget = TestDmsWithWidgetsHelper.mockBuildWidget("wId", "aWidgetType", "aWidgetLabel");
        final List<Widget> expWidgets = asList(aWidget);
        when(widgetService.widgetDataToWidget(anyString(), anyString(), eq(classWidgets), any())).thenReturn(expWidgets);
        when(classSerializationHelper.serializeWidget(eq(aWidget), anyString())).thenReturn(TestDmsWithWidgetsHelper.mockSerializeWidget(aWidget));
        when(translationService.translateClassWidgetDescription(eq(classe.getName()), eq(aWidget.getId()), eq(aWidget.getLabel()))).thenReturn(aWidget.getLabel());

        FluentMap<String, Object> expResult = map("_category", null,
                "_category_name", null,
                "_category_description", null,
                "_category_description_translation", null,
                "_can_update", false,
                "_can_delete", false,
                // Card related to FileAttribute
                "Notes", null,
                "Size", null,
                "Hash", null,
                "FileName", null,
                // End Card
                "category", null,
                "Category", null, // for legacy
                "_card", cardId,
                "_id", TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_ID,
                "name", TestDmsWithWidgetsHelper.A_KNOWN_FILENAME,
                "description", TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_DESCRIPTION,
                "Description", TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_DESCRIPTION, // for legacy
                "version", TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_VERSION,
                "author", TestDmsWithWidgetsHelper.A_KNOWN_AUTHOR,
                "_author_description", TestDmsWithWidgetsHelper.A_KNOWN_AUTHOR,
                "created", TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_CREATION_DATE_STR,
                "modified", TestDmsWithWidgetsHelper.A_KNOWN_DOCUMENT_MODIFICATION_DATE_STR,
                "_widgets", asList(TestDmsWithWidgetsHelper.mockSerializeWidget(aWidget)));

        //act:
        Map<String, Object> result = instance.serializeAttachment_FullDetail(classe.getId().toString(), input, true);

        //assert:
        TestDmsWithWidgetsHelper.checkEquals_Map(expResult, result);
    }

}
