/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation.test;

import jakarta.inject.Provider;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.common.localization.LanguageService;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.translation.TranslationService;
import org.cmdbuild.translation.TranslationServiceImpl;
import org.cmdbuild.translation.dao.TranslationRepository;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NewTranslationServiceTest {

    private TranslationService translationService;
    private LanguageService languageService;

    @Before
    public void init() {
        TranslationRepository translationRepository = mock(TranslationRepository.class);
        when(translationRepository.getTranslationOrNull("my.key", "it")).thenReturn("My Content");
        languageService = mock(LanguageService.class);
        when(languageService.getContextLanguage()).thenReturn("it");
        translationService = new TranslationServiceImpl(mock(ClasseRepository.class), translationRepository, languageService, mock(Provider.class), mock(CacheService.class));
    }

    @Test
    public void testProcessing() {
        String res = translationService.translateExpr("something with {translate:my.key}.");
        assertEquals("something with My Content.", res);
    }

    @Test
    public void testProcessingWithDefaultNotUsed() {
        String res = translationService.translateExpr("something with {translate:my.key:My Default}.");
        assertEquals("something with My Content.", res);
    }

    @Test
    public void testProcessingWithDefaultUsed() {
        String res = translationService.translateExpr("something with {translate:my.key.not.found:My Default}.");
        assertEquals("something with My Default.", res);
    }

    @Test
    public void testProcessingWithEmptyDefaultUsed() {
        String res = translationService.translateExpr("something with {translate:my.key.not.found:}.");
        assertEquals("something with .", res);
    }

    @Test
    public void testProcessingWithNoDefaultAndNotFoundError() {
        String res = translationService.translateExpr("something with {translate:my.key.not.found}.");
        assertEquals("something with missing_translation('my.key.not.found').", res);
    }

    @Test(expected = RuntimeException.class)
    public void testProcessingWithInvalidEmptyKey() {
        String res = translationService.translateExpr("something with {translate:}.");
    }

    @Test(expected = RuntimeException.class)
    public void testProcessingWithInvalidBlankKey() {
        String res = translationService.translateExpr("something with {translate: }.");
    }

    @Test(expected = RuntimeException.class)
    public void testProcessingWithInvalidEmptyKeyAndDefault() {
        String res = translationService.translateExpr("something with {translate::something}.");
    }

    @Test(expected = RuntimeException.class)
    public void testProcessingWithInvalidBlankKeyAndDefault() {
        String res = translationService.translateExpr("something with {translate: :something}.");
    }

}
