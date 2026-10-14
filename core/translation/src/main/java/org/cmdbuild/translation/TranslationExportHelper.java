/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation;

import java.util.Collection;
import java.util.List;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;

public interface TranslationExportHelper {

    TranslationExportHelper withLanguages(@Nullable Collection<String> languages);

    TranslationExportHelper withSeparator(@Nullable String separator);

    TranslationExportHelper withEmptyRecordsForAllObjects(boolean generateEmptyRecordsForAllObjects);

    TranslationExportHelper withIncludeRecordsWithoutDefault(boolean includeRecordsWithoutDefault);

    TranslationExportHelper withSection(TranslationSection section);

    DataHandler export();

    List<ExportRecord> exportRecords();

    List<String> getLanguages();

}
