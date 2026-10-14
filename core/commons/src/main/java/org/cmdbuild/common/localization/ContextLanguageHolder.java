/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.localization;

import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public interface ContextLanguageHolder {

    @Nullable
    String getContextLanguage();

    void setContextLanguage(@Nullable String language);

    default boolean hasContextLanguage() {
        return isNotBlank(getContextLanguage());
    }

}
