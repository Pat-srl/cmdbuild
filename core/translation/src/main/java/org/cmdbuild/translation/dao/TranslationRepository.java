/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation.dao;

import java.util.Collection;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.common.utils.PagedElements;

public interface TranslationRepository extends TranslationSource {

    PagedElements<Translation> getTranslations(@Nullable String filter, @Nullable Integer offset, @Nullable Integer limit);

    Translation setTranslation(String code, String lang, String value);

    void deleteTranslationIfExists(String code, String lang);

    void deleteTranslations(String code);

    List<Translation> getAllForLanguages(Collection<String> languages);

    @Override
    public default List<Translation> getTranslations() {
        return getTranslations(null, null, null).elements();
    }

}
