/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation;

import jakarta.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.translation.dao.Translation;

public interface TranslationService extends ObjectTranslationService_WithGis {

    @Nullable
    String translateExpr(@Nullable String source);

    @Nullable
    String getTranslationForCodeAndCurrentUser(String code);

    @Nullable
    Map<String, String> getTranslationsBySectionAndCurrentUser(TranslationSection section);

    Map<String, String> getTranslationValueMapByLangForCode(String code);

    String getTranslationValueForCodeAndLang(String code, String lang);

    PagedElements<Translation> getTranslations(@Nullable String filter, @Nullable Integer offset, @Nullable Integer limit);

    Translation setTranslation(String code, String lang, String value);

    void deleteTranslationIfExists(String code, String lang);

    void deleteTranslations(String code);

    TranslationExportHelper exportHelper();

    TranslationImportHelper importHelper();

    void loadTranslationsForLanguages(Collection<String> languages);

}
