/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import com.google.common.base.Splitter;
import jakarta.activation.DataHandler;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.preload.PreloadService;
import org.cmdbuild.translation.ExportRecord;
import org.cmdbuild.translation.TranslationExportHelper;
import org.cmdbuild.translation.TranslationService;
import org.cmdbuild.translation.dao.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Maps.filterKeys;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.translation.TranslationSection.TS_ALL;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
@Component
public class TranslationsWsCommand {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final TranslationService translationService;
    private final PreloadService preloadService;

    public TranslationsWsCommand(TranslationService translationService, PreloadService preloadService) {
        this.translationService = translationService;
        this.preloadService = preloadService;
    }

    public PagedElements<Translation> doGetAll(Integer limit, Integer offset, String filter) {
        return translationService.getTranslations(filter, offset, limit);
    }

    public void doLoadAllTranslationsForLanguages(String languages) {
        translationService.loadTranslationsForLanguages(Splitter.on(";").trimResults().omitEmptyStrings().splitToList(nullToEmpty(languages)));
    }

    public List<ExportRecord> doGetAllAggregateByCode(String section, String languages, Boolean includeRecordsWithoutTranslation, String filter) {
        TranslationExportHelper helper = translationService.exportHelper().withSection(parseEnumOrDefault(section, TS_ALL))
                .withLanguages(Splitter.on(",").trimResults().omitEmptyStrings().splitToList(nullToEmpty(languages)))
                .withEmptyRecordsForAllObjects(includeRecordsWithoutTranslation);
        Stream<ExportRecord> records = helper.exportRecords().stream();
        if (isNotBlank(filter)) {
            records = records.filter(r -> r.getCode().toLowerCase().contains(filter.toLowerCase().trim()));
        }
        return records.toList();
    }

    public Map<String, String> doGetTranslationForKeyAndLang(String code, String lang) {
        if (isNotBlank(lang)) {
            return map("value", translationService.getTranslationValueForCodeAndLang(code, lang));
        } else {
            return translationService.getTranslationValueMapByLangForCode(code);
        }
    }

    public Map<String, String> doSetTranslation(String code, Map<String, String> data) {
        filterKeys(data, not(equalTo("_id"))).forEach((k, v) -> {
            if (isNotBlank(v)) {
                translationService.setTranslation(code, k, v);
            } else {
                translationService.deleteTranslationIfExists(code, k);
            }
        });
        return translationService.getTranslationValueMapByLangForCode(code);
    }

    public void doDeleteTranslation(String code, String lang) {
        if (isBlank(lang)) {
            translationService.deleteTranslations(code);
        } else {
            translationService.deleteTranslationIfExists(code, lang);
        }
    }

    public DataHandler doExport(String languages, String format, String separator, String section, Boolean includeRecordsWithoutTranslation) {
        return translationService.exportHelper()
                .withLanguages(Splitter.on(",").trimResults().omitEmptyStrings().splitToList(nullToEmpty(languages)))
                .withSeparator(separator)
                .withSection(parseEnumOrDefault(section, TS_ALL))
                .withEmptyRecordsForAllObjects(includeRecordsWithoutTranslation)
                .withIncludeRecordsWithoutDefault(false)
                .export();
    }

    public void doImportTranslations(String separator, DataHandler dataHandler) {
        translationService.importHelper().withSeparator(separator).importTranslations(dataHandler);
        logger.info("Running system preload");
        preloadService.runPreload();
    }
}
