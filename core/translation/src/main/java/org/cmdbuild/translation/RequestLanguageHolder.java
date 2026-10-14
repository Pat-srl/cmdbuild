/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation;

import jakarta.annotation.Nullable;

public interface RequestLanguageHolder {

    boolean hasRequestLanguage();

    void setRequestLanguage(String lang);

    String getRequestLanguage();

    @Nullable
    String getRequestLanguageOrNull();

    enum RequestLanguageSetEvent {
        INSTANCE;
    }

}
