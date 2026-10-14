/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation.file;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import java.io.File;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.translation.TranslationObject;
import org.cmdbuild.translation.TranslationObjectsService;
import org.cmdbuild.translation.dao.Translation;
import org.cmdbuild.translation.dao.TranslationSource;
import static org.cmdbuild.translation.file.TranslationFromFileUtils.loadTranslations;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.serializeListOfStrings;
import org.cmdbuild.minions.PostStartup;

@Component
public class TranslationSourceFromFileImpl implements TranslationSource {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DirectoryService directoryService;

    private final List<Translation> translations = list();
    private final Multimap<String, Translation> translationsByCode = HashMultimap.create();
    private final Map<String, Translation> translationsByCodeAndLang = new ConcurrentHashMap<>();

    public TranslationSourceFromFileImpl(DirectoryService directoryService) {
        this.directoryService = checkNotNull(directoryService);
    }

    @PostStartup
    public void loadStaticTranslationsFromFile() {
        if (!directoryService.hasWebappDirectory()) {
            logger.warn("cannot load static translations from file: webapp folder not available");
        } else {
            File localesDir = new File(directoryService.getWebappDirectory(), "ui/app/locales");
//            FileUtils.listFiles(localesDir, new String[]{"js"}, true).stream().filter(f -> f.getName().matches("(Locales|LocalesAdministration).js")).forEach(f -> {
            FileUtils.listFiles(localesDir, new String[]{"js"}, true).stream().filter(f -> f.getName().matches("Locales.js")).sorted().forEach(f -> {
                logger.debug("load translations from file =< {} >", f.getAbsolutePath());
                loadTranslations(readToString(f, UTF_8)).forEach(TranslationSourceFromFileImpl.this::load);
            });
        }
    }

    @Override
    public List<Translation> getTranslations(String code) {
        return ImmutableList.copyOf(translationsByCode.get(checkNotBlank(code)));
    }

    @Override
    @Nullable
    public String getTranslationOrNull(String code, String lang) {
        return Optional.ofNullable(translationsByCodeAndLang.get(key(checkNotBlank(code), checkNotBlank(lang)))).map(Translation::getValue).orElse(null);
    }

    private void load(Translation translation) {
        logger.debug("load translation code =< {} > lang =< {} > value =< {} >", translation.getCode(), translation.getLang(), translation.getValue());
        translationsByCode.put(translation.getCode(), translation);
        translationsByCodeAndLang.put(key(translation.getCode(), translation.getLang()), translation);
        translations.add(translation);
    }

    @Override
    public List<Translation> getTranslations() {
        return Collections.unmodifiableList(translations);
    }

    @Override
    public void loadTranslationsForLangs(Collection<String> languages, List<TranslationObject> translatableObjects) {

    }

}
