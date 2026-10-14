/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation;

import java.util.Map;
import jakarta.annotation.Nullable;

public interface ExportRecord {

    String getCode();

    @Nullable
    String getDefault();

    Map<String, String> getTranslationsByLanguage();

}
