/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.template;

import static java.lang.String.format;
import org.cmdbuild.api.CmApiService;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.session.OperatorService;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.common.localization.LanguageService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.constants.SystemAttributes;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.ClassType;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.easytemplate.EasytemplateProcessorImpl;
import org.cmdbuild.easytemplate.EasytemplateService;
import org.cmdbuild.easytemplate.FtlTemplateService;
import org.cmdbuild.easytemplate.store.EasytemplateRepository;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author afelice
 */
public class TemplateProcessorHandlerTest {

    private static final String A_KNOWN_EXPRESSION = "[#etl]this is an easy template with ${email.from} interpolation";

    private static final String A_KNOWN_LANG = "en";

    private final LanguageService languageService = mock(LanguageService.class);
    private final ObjectTranslationService translationService = mock(ObjectTranslationService.class);
    private final FtlTemplateService ftlTemplateService = mock(FtlTemplateService.class);
    private final EasytemplateService easytemplateService = mock(EasytemplateService.class);
    private final EasytemplateRepository easytemplateRepository = mock(EasytemplateRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final OperationUserSupplier userSupplier = mock(OperationUserSupplier.class);
    private final RoleRepository roleRepository = mock(RoleRepository.class);
    private final DaoService dao = mock(DaoService.class);
    private final CmApiService apiService = mock(CmApiService.class);

    private final Logger clientLogger = LoggerFactory.getLogger(getClass());
    private final OperatorService operatorService = mock(OperatorService.class);

    Classe aClasse = mockClasse("Test");

    TemplateProcessorHandler instance = new TemplateProcessorHandler(languageService, translationService,
            ftlTemplateService, easytemplateService, easytemplateRepository,
            userRepository, userSupplier, roleRepository, dao,
            apiService,
            clientLogger, operatorService);

    @Before
    public void setUp() {
        when(easytemplateService.getDefaultProcessorWithJsContext(anyString())).thenReturn(EasytemplateProcessorImpl.builder().build());
    }

    /**
     * Test of fetchLanguage method, of class TemplateProcessorHandler.
     */
    @Test
    public void testFetchLanguage() {
        System.out.println("fetchLanguage()");

        //arrange:
        final String lang = "en";
        EmailTemplate aTemplateWithLang = buildFakeEmailTemplate(1, lang);

        //act:
        String result = instance.fetchLanguage(ExpressionInputData
                .builder()
                .withTemplate(aTemplateWithLang)
                .build());

        //assert:
        assertEquals(lang, result);
    }

    /**
     * Test of initWith method, of class TemplateProcessorHandler.
     */
    @Test
    public void testInitWith() {
        System.out.println("initWith()");

        //arrange:
        final String lang = A_KNOWN_LANG;
        EmailTemplate aTemplateWithLang = buildFakeEmailTemplate(1, lang);
        final long aCardId = 3L;
        final String aCardDescr = "card 3";
        ExpressionProcessor expressionProcessor = mock(ExpressionProcessor.class);
        when(expressionProcessor.processExpression(lang)).thenReturn(lang);
        TemplateProcessorHandler theInstance = new TemplateProcessorHandler(languageService, translationService,
                ftlTemplateService, easytemplateService, easytemplateRepository,
                userRepository, userSupplier, roleRepository, dao,
                apiService,
                clientLogger, operatorService) {
            @Override
            protected ExpressionProcessor buildExpressionProcessor(TemplateExpressionInputData expressionInputData, String jsContext) {
                return expressionProcessor;
            }
        };

        //act:
        theInstance.initWith(ExpressionInputData
                .builder()
                .withClientCard(buildCard(aCardId, aCardDescr))
                .withTemplate(aTemplateWithLang)
                .build());

        //assert:
        assertEquals(lang, theInstance.getLanguageToUse());
        assertEquals(lang, theInstance.ftlTemplateData.get("lang"));
        assertEquals(map().with("Id", aCardId,
                "Description", aCardDescr),
                theInstance.ftlTemplateData.get("_client")
        );
    }

    /**
     * Test of initWith method, forced language overrides template lang, of
     * class TemplateProcessorHandler.
     */
    @Test
    public void testInitWith_ForcedLanguageOverridesTemplateLang() {
        System.out.println("initWith_ForcedLanguageOverridesTemplateLang()");

        //arrange:
        final String forcedLang = "fr";
        final String lang = A_KNOWN_LANG;
        EmailTemplate aTemplateWithLang = buildFakeEmailTemplate(1, lang);
        final long aCardId = 3L;
        final String aCardDescr = "card 3";
        ExpressionProcessor expressionProcessor = mock(ExpressionProcessor.class);
        when(expressionProcessor.getLanguage()).thenReturn(forcedLang);
        TemplateProcessorHandler theInstance = new TemplateProcessorHandler(languageService, translationService,
                ftlTemplateService, easytemplateService, easytemplateRepository,
                userRepository, userSupplier, roleRepository, dao,
                apiService,
                clientLogger, operatorService) {
            @Override
            protected ExpressionProcessor buildExpressionProcessor(TemplateExpressionInputData expressionInputData, String jsContext) {
                when(expressionProcessor.getLanguage()).thenReturn(languageToUse);
                return expressionProcessor;
            }
        };

        //act:
        theInstance.initWith(ExpressionInputData.builder()
                .withClientCard(buildCard(aCardId, aCardDescr))
                .withTemplate(aTemplateWithLang)
                .withForcedLanguage(forcedLang)
                .build());

        //assert:
        assertEquals(forcedLang, theInstance.getLanguageToUse());
        assertEquals(forcedLang, theInstance.ftlTemplateData.get("lang"));
        assertEquals(map().with("Id", aCardId,
                "Description", aCardDescr),
                theInstance.ftlTemplateData.get("_client")
        );
    }

    /**
     * Test of initWith method, forced language, of class
     * TemplateProcessorHandler.
     */
    @Test
    public void testInitWith_ForcedLanguageNoTemplateLang() {
        System.out.println("initWith_ForcedLanguageNoTemplateLang()");

        //arrange:
        final String forcedLang = "fr";
        final String lang = A_KNOWN_LANG;
        EmailTemplate aTemplateNoLang = buildFakeEmailTemplate(1);
        final long aCardId = 3L;
        final String aCardDescr = "card 3";
        ExpressionProcessor expressionProcessor = mock(ExpressionProcessor.class);
        when(expressionProcessor.getLanguage()).thenReturn(forcedLang);
        TemplateProcessorHandler theInstance = new TemplateProcessorHandler(languageService, translationService,
                ftlTemplateService, easytemplateService, easytemplateRepository,
                userRepository, userSupplier, roleRepository, dao,
                apiService,
                clientLogger, operatorService) {
            @Override
            protected ExpressionProcessor buildExpressionProcessor(TemplateExpressionInputData expressionInputData, String jsContext) {
                when(expressionProcessor.getLanguage()).thenReturn(languageToUse);
                return expressionProcessor;
            }
        };

        //act:
        theInstance.initWith(ExpressionInputData.builder()
                .withClientCard(buildCard(aCardId, aCardDescr))
                .withTemplate(aTemplateNoLang)
                .withForcedLanguage(forcedLang)
                .build());

        //assert:
        assertEquals(forcedLang, theInstance.getLanguageToUse());
        assertEquals(forcedLang, theInstance.ftlTemplateData.get("lang"));
        assertEquals(map().with("Id", aCardId,
                "Description", aCardDescr),
                theInstance.ftlTemplateData.get("_client")
        );
    }

    /**
     * Test of initWith method, forced language, of class
     * TemplateProcessorHandler.
     */
    @Test
    public void testInitWith_None() {
        System.out.println("initWith_None()");

        //arrange:
        EmailTemplate aTemplateNoLang = buildFakeEmailTemplate(1);
        final long aCardId = 3L;
        final String aCardDescr = "card 3";
        ExpressionProcessor expressionProcessor = mock(ExpressionProcessor.class);
        TemplateProcessorHandler theInstance = new TemplateProcessorHandler(languageService, translationService,
                ftlTemplateService, easytemplateService, easytemplateRepository,
                userRepository, userSupplier, roleRepository, dao,
                apiService,
                clientLogger, operatorService) {
            @Override
            protected ExpressionProcessor buildExpressionProcessor(TemplateExpressionInputData expressionInputData, String jsContext) {
                when(expressionProcessor.getLanguage()).thenReturn(languageToUse);
                return expressionProcessor;
            }
        };

        //act:
        theInstance.initWith(ExpressionInputData
                .builder()
                .withClientCard(buildCard(aCardId, aCardDescr))
                .withTemplate(aTemplateNoLang)
                .build());

        //assert:
        assertNull(theInstance.getLanguageToUse());
        assertNull(theInstance.ftlTemplateData.get("lang"));
        assertEquals(map().with("Id", aCardId,
                "Description", aCardDescr),
                theInstance.ftlTemplateData.get("_client")
        );
    }

    private static EmailTemplate buildFakeEmailTemplate(long id) {
        EmailTemplate template = mock(EmailTemplate.class);
        when(template.getId()).thenReturn(id);
        when(template.getCode()).thenReturn(format("<ATemplate_%s>", 1));
        when(template.getId()).thenReturn(id);
        when(template.getContentType()).thenReturn("text/plain");
        when(template.getContent()).thenReturn(A_KNOWN_EXPRESSION);

        return template;
    }

    private static EmailTemplate buildFakeEmailTemplate(long id, String language) {
        EmailTemplate template = mock(EmailTemplate.class);
        when(template.getId()).thenReturn(id);
        when(template.getCode()).thenReturn(format("<ATemplate_%s>", 1));
        when(template.getId()).thenReturn(id);
        when(template.getContentType()).thenReturn("text/plain");
        when(template.getContent()).thenReturn(A_KNOWN_EXPRESSION);
        when(template.hasLangExpr()).thenReturn(true);
        when(template.getLangExpr()).thenReturn(language);

        return template;
    }

    private static Classe mockClasse(String classeName) {
        Classe mock = mock(Classe.class);
        when(mock.getName()).thenReturn(classeName);
        when(mock.getClassType()).thenReturn(ClassType.CT_SIMPLE);

        return mock;
    }

    private Card buildCard(long id, String description) {
        return CardImpl.builder()
                .withType(aClasse)
                .withId(id)
                .withAttribute(SystemAttributes.ATTR_DESCRIPTION, description)
                .build();
    }

}
