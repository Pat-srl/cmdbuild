/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.translation.TranslationObject;

public interface TranslationSource {

    List<Translation> getTranslations(String code);

    void loadTranslationsForLangs(Collection<String> languages, List<TranslationObject> translatableObjects);

    List<Translation> getTranslations();

    @Nullable
    String getTranslationOrNull(String code, String lang);

    default String getTranslation(String code, String lang) {
        return checkNotNull(getTranslationOrNull(code, lang), "translation not found for code =< %s > and lang =< %s >", code, lang);
    }
}
