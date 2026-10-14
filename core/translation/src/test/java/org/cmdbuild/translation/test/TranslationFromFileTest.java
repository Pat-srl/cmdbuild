/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation.test;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.cmdbuild.translation.dao.Translation;
import static org.cmdbuild.translation.file.TranslationFromFileUtils.loadTranslations;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.serializeListOfStrings;

public class TranslationFromFileTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testFileParsing1() {
        String fileContent = readToString(new File("../../ui/app/locales/Locales.js"));
        List<Translation> translations = loadTranslations(fileContent);
        assertFalse(translations.isEmpty());

        logger.info("translations = {}", translations);
        Map<String, String> byCodeLang = map(translations, t -> key(t.getCode(), t.getLang()), Translation::getValue);

        assertEquals("Default", byCodeLang.get(key("lookup.CalendarCategory.default.description", "default")));
        assertEquals("Daily", byCodeLang.get(key("lookup.CalendarFrequency.daily.description", "default")));
    }

    @Test
    public void testFileParsing2() {
        String fileContent = readToString(new File("../../ui/app/locales/it/Locales.js"));
        List<Translation> translations = loadTranslations(fileContent);
        assertFalse(translations.isEmpty());

        logger.info("translations = {}", translations);
        Map<String, String> byCodeLang = map(translations, t -> key(t.getCode(), t.getLang()), Translation::getValue);

        assertEquals("Default", byCodeLang.get(key("lookup.CalendarCategory.default.description", "it")));
        assertEquals("Giornaliera", byCodeLang.get(key("lookup.CalendarFrequency.daily.description", "it")));
    }

    @Test
    public void testFileParsing3() {
        String fileContent = readToString(new File("../../ui/app/locales/de/Locales.js"));
        List<Translation> translations = loadTranslations(fileContent);
        assertFalse(translations.isEmpty());
    }

}
