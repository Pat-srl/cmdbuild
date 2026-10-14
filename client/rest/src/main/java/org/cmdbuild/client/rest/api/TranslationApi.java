/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import java.util.List;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import org.cmdbuild.translation.dao.Translation;

public interface TranslationApi {

    TranslationApi put(String code, String lang, String value);

    void importFromFile(DataSource payload, @Nullable String separator);

    TranslationApi delete(String code, String lang);

    List<Translation> getAll();

    List<Translation> getMany(String query);

    default void importFromFile(DataSource payload) {
        importFromFile(payload, null);
    }

}
