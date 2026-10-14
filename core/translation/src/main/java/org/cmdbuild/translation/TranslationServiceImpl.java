/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation;

import com.google.common.base.Joiner;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import jakarta.inject.Provider;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.common.localization.LanguageService;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeGroupInfo;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.translation.dao.Translation;
import org.cmdbuild.translation.dao.TranslationRepository;
import org.cmdbuild.utils.csv.CsvUtils;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.google.common.collect.Maps.uniqueIndex;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.translation.TranslationUtils.attributeDescriptionTranslationCode;
import static org.cmdbuild.translation.TranslationUtils.attributeGroupDescriptionTranslationCode;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.*;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.*;
import static org.cmdbuild.utils.lang.CmStringUtils.emptyToNull;
import static org.cmdbuild.utils.lang.CmStringUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;

@Component
public class TranslationServiceImpl implements TranslationService, ObjectTranslationService {

    public final static String LANG_DEFAULT = "default";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ClasseRepository classeRepository;
    private final TranslationRepository translationRepository;
    private final LanguageService languageService;
    private final Provider<TranslationObjectsService> objectsService; //TODO improve this
    private final CacheService cacheService;

    public TranslationServiceImpl(
            ClasseRepository classeRepository,
            TranslationRepository translationRepository,
            LanguageService languageService,
            Provider<TranslationObjectsService> objectsService,
            CacheService cacheService) {
        this.classeRepository = checkNotNull(classeRepository);
        this.translationRepository = checkNotNull(translationRepository);
        this.languageService = checkNotNull(languageService);
        this.objectsService = checkNotNull(objectsService);
        this.cacheService = checkNotNull(cacheService);
    }

    @Override
    public String translateAttributeDescription(Attribute attribute) {
        String value = translateByCode(attributeDescriptionTranslationCode(attribute));
        if (attribute.isInherited() && attribute.getOwner().isRegularClass()) {
            Classe classe = attribute.getOwner().asClasse();
            while (value == null && classe.hasParent()) {
                classe = classeRepository.getClasse(classe.getParent());
                Attribute inherited = classe.getAttributeOrNull(attribute.getName());
                if (inherited != null && Objects.equals(attribute.getDescription(), inherited.getDescription())) {
                    value = translateByCode(attributeDescriptionTranslationCode(inherited));
                } else {
                    break;
                }
            }
        }
        return firstNonNull(value, nullToEmpty(attribute.getDescription()));
    }

    @Override
    public String translateAttributeGroupDescription(EntryType owner, AttributeGroupInfo attributeGroup) {
        String value = translateByCode(attributeGroupDescriptionTranslationCode(owner, attributeGroup));
        String defaultValue = attributeGroup.getDescription();
        if (owner.isClasse()) {
            Classe classe = ((Classe) owner);
            while (value == null) {
                Classe parent = classe.hasParent() ? classeRepository.getClasse(classe.getParent()) : null;
                if (parent == null || !parent.hasAttributeGroup(attributeGroup.getName()) || !Objects.equals(parent.getAttributeGroup(attributeGroup.getName()).getDescription(), attributeGroup.getDescription())) {
                    break;
                } else {
                    classe = parent;
                    value = translateByCode(attributeGroupDescriptionTranslationCode(classe, attributeGroup));
                }
            }
        }
        return firstNonNull(value, nullToEmpty(defaultValue));
    }

    @Override
    public String translateExpr(String source) {
        Matcher matcher = Pattern.compile("[{]translate:[^}]*[}]", Pattern.DOTALL).matcher(source);
        if (matcher.find()) {
            StringBuffer stringBuffer = new StringBuffer();
            matcher.reset();
            while (matcher.find()) {
                String from = matcher.group();
                Matcher blockMatcher = Pattern.compile("[{]translate:([^:}]*)(:[^}]*)?[}]").matcher(from);
                checkArgument(blockMatcher.find());
                String code = checkNotBlank(blockMatcher.group(1));
                String defaultValue = emptyToNull(blockMatcher.group(2));
                String value = translateByCode(code);
                if (value == null) {
                    if (defaultValue != null) {
                        value = defaultValue.replaceFirst(":", "");
                    } else {
                        logger.warn(marker(), "translation not found for code =< {} > and user language =< {} >", code, languageService.getContextLanguage());
                        value = format("missing_translation('%s')", code);
                    }
                }
                matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(value));
            }
            matcher.appendTail(stringBuffer);
            return stringBuffer.toString();
        } else {
            return source;
        }
    }

    @Nullable
    @Override
    public String translateByCode(String code) {
        checkNotBlank(code);
        String lang = languageService.getContextLanguage();
        String value = null;
        if (isNotBlank(lang)) {
            value = translationRepository.getTranslationOrNull(code, lang);
        }
        if (value == null && !Objects.equals(lang, LANG_DEFAULT)) {
            value = translationRepository.getTranslationOrNull(code, LANG_DEFAULT);
        }
        return value;
    }

    @Override
    public String translateByLangAndCode(String language, String code) {
        return translationRepository.getTranslationOrNull(checkNotBlank(code), checkNotBlank(language));
    }

    @Override
    public String getTranslationForCodeAndCurrentUser(String code) {
        return checkNotNull(translateByCode(code), format("unable to find translation for code =< %s > and user language =< %s >", code, languageService.getContextLanguage()));
    }

    @Override
    public Map<String, String> getTranslationsBySectionAndCurrentUser(TranslationSection section) {
        List<Translation> allForCurrentUser = translationRepository.getAllForLanguages(singletonList(languageService.getContextLanguage()));
        List<String> codes = filterCodesBySection(allForCurrentUser.stream().map(Translation::getCode), section).collect(toUnmodifiableList());
        return allForCurrentUser.stream().filter(t -> codes.contains(t.getCode())).collect(toMap(Translation::getCode, Translation::getValue));
    }

    @Override
    public String getTranslationValueForCodeAndLang(String code, String lang) {
        return translationRepository.getTranslation(code, lang);
    }

    @Override
    public Map<String, String> getTranslationValueMapByLangForCode(String code) {
        return translationRepository.getTranslations(code).stream().collect(toMap(Translation::getLang, Translation::getValue));
    }

    @Override
    public PagedElements<Translation> getTranslations(@Nullable String filter, @Nullable Integer offset, @Nullable Integer limit) {
        return translationRepository.getTranslations(filter, offset, limit);
    }

    @Override
    public Translation setTranslation(String code, String lang, String value) {
        return translationRepository.setTranslation(code, lang, value);
    }

    @Override
    public void deleteTranslationIfExists(String code, String lang) {
        translationRepository.deleteTranslationIfExists(code, lang);
    }

    @Override
    public void deleteTranslations(String code) {
        translationRepository.deleteTranslations(code);
    }

    @Override
    public TranslationExportHelper exportHelper() {
        return new TranslationExportHelperImpl();
    }

    @Override
    public TranslationImportHelper importHelper() {
        return new TranslationImportHelperImpl();
    }

    @Override
    public void loadTranslationsForLanguages(Collection<String> languages) {
        List<TranslationObject> candidateTranslations = objectsService.get().getAllTranslableObjectsAndDefaults();
        translationRepository.loadTranslationsForLangs(languages, candidateTranslations);
    }

    private Stream<String> filterCodesBySection(Stream<String> codes, TranslationSection section) {
        return switch (section) {
            case TS_ALL ->
                codes;
            case TS_LOOKUPTYPES ->
                codes.filter(c -> c.matches("^lookup[.].*"));
            case TS_CUSTOMCOMPONENTS ->
                codes.filter(c -> c.matches("^(customcomponent|custompage)[.].*"));
            case TS_MENU ->
                codes.filter(c -> c.matches("^menuitem[.].*"));
            case TS_VIEWS ->
                codes.filter(c -> c.matches("^view[.].*"));
            case TS_REPORTS ->
                codes.filter(c -> c.matches("^report[.].*"));
            case TS_SEARCHFILTERS ->
                codes.filter(c -> c.matches("^filter[.].*"));
            case TS_NOTIFICATIONS ->
                codes.filter(c -> c.matches("^notification[.].*"));
            case TS_DOMAINS ->
                codes.filter(c -> c.matches("^(domain|attributedomain|attributegroupdomain)[.].*"));
            case TS_CLASSES -> //Add widgets
                codes.filter(c -> {
                    Matcher matcher = Pattern.compile("^(class|attributegroupclass|attributeclass|contextmenu|widget)[.]([^.]+)[.].*").matcher(c);
                    if (matcher.find()) {
                        Classe classe = classeRepository.getClasseOrNull(matcher.group(2));
                        return classe != null && !classe.isProcess();
                    }
                    return false;
                });
            case TS_PROCESSES -> //Add widgets
                codes.filter(c -> {
                    Matcher matcher = Pattern.compile("^(class|attributegroupclass|attributeclass|activity|contextmenu|widget)[.]([^.]+)[.].*").matcher(c);
                    if (matcher.find()) {
                        Classe classe = classeRepository.getClasseOrNull(matcher.group(2));
                        return classe != null && classe.isProcess();
                    }
                    return false;
                });
            case TS_DASHBOARDS ->
                codes.filter(c -> c.matches("^dashboard[.].*"));
            case TS_HOMEPAGES ->
                codes.filter(c -> c.matches("^homepage[.].*"));
            case TS_GROUPS ->
                codes.filter(c -> c.matches("^role[.].*"));
            case TS_OFFLINE ->
                codes.filter(c -> c.matches("^offline[.].*"));
            case TS_UICUSTOM ->
                codes.filter(c -> c.matches("^uicustom[.].*"));
            default ->
                throw new IllegalArgumentException("invalid section = " + section);
        };
    }

    private class TranslationExportHelperImpl extends TranslationImportExportCommons<TranslationExportHelperImpl> implements TranslationExportHelper {

        private boolean generateEmptyRecordsForAllObjects = false, includeRecordsWithoutDefault = false;
        private TranslationSection section;
        private Set<String> languages;

        @Override
        public TranslationExportHelper withLanguages(@Nullable Collection<String> languages) {
            this.languages = CmCollectionUtils.emptyToNull(set(CmCollectionUtils.nullToEmpty(languages)));
            return this;
        }

        @Override
        public TranslationExportHelper withEmptyRecordsForAllObjects(boolean generateEmptyRecordsForAllObjects) {
            this.generateEmptyRecordsForAllObjects = generateEmptyRecordsForAllObjects;
            return this;
        }

        @Override
        public TranslationExportHelper withIncludeRecordsWithoutDefault(boolean includeRecordsWithoutDefault) {
            this.includeRecordsWithoutDefault = includeRecordsWithoutDefault;
            return this;
        }

        @Override
        public TranslationExportHelper withSection(TranslationSection section) {
            this.section = section;
            return this;
        }

        @Override
        public List<String> getLanguages() {
            if (isNullOrEmpty(languages)) {
                languages = set(languageService.getEnabledLanguages());
            }
            return list(languages);
        }

        @Override
        public List<ExportRecord> exportRecords() {
            List<Translation> currentTranslations = translationRepository.getAllForLanguages(getLanguages());
            List<TranslationObject> candidateTranslations = objectsService.get().getAllTranslableObjectsAndDefaults();
            Map<String, Translation> currentTranslationsByLangCode = uniqueIndex(currentTranslations, t -> key(t.getLang(), t.getCode()));
            Map<String, String> defaultValues = map(candidateTranslations, TranslationObject::getCode, TranslationObject::getDefaultValue);

            Stream<String> codes;
            if (generateEmptyRecordsForAllObjects) {
                codes = setOf(String.class).accept(s -> {
                    candidateTranslations.stream().map(TranslationObject::getCode).forEach(s::add);
                    currentTranslations.stream().map(Translation::getCode).forEach(s::add);
                }).stream().sorted();
            } else {
                codes = currentTranslations.stream().map(Translation::getCode).sorted().distinct();
            }

            codes = filterCodesBySection(codes, section);

            if (!includeRecordsWithoutDefault) {
                codes = codes.filter(defaultValues::containsKey);
            }

            return codes.map(code -> {
                String defaultValue = nullToEmpty(defaultValues.get(code));
                Map<String, String> translations = getLanguages().stream()
                        .collect(toMap(identity(), lang -> Optional.ofNullable(currentTranslationsByLangCode.get(key(lang, code)))
                        .map(Translation::getValue).orElse(null)));
                return new ExportRecordImpl(code, defaultValue, translations);
            }).collect(toList());
        }

        @Override
        public DataHandler export() {
            try {
                List<ExportRecord> records = exportRecords();

                CsvPreference csvPreference = buildCsvPreference(null);
                StringWriter writer = new StringWriter();
                try (CsvListWriter csv = new CsvListWriter(writer, csvPreference)) {
                    csv.write(list("identifier", "default").with(getLanguages()));
                    records.forEach(rethrowConsumer(r -> {
                        csv.write(list(r.getCode(), nullToEmpty(r.getDefault())).accept((line) -> getLanguages().stream().map(l -> nullToEmpty(r.getTranslationsByLanguage().get(l))).forEach(line::add)));
                    }));
                }
                return newDataHandler(writer.toString().getBytes(StandardCharsets.UTF_8), "text/csv", "translations.csv");
            } catch (IOException ex) {
                throw runtime(ex);
            }
        }

    }

    private static class ExportRecordImpl implements ExportRecord {

        private final String code;
        private final String defaultValue;
        private final Map<String, String> translations;

        public ExportRecordImpl(String code, @Nullable String defaultValue, Map<String, String> translations) {
            this.code = checkNotBlank(code);
            this.defaultValue = defaultValue;
            this.translations = map(translations).immutable();
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        @Nullable
        public String getDefault() {
            return defaultValue;
        }

        @Override
        public Map<String, String> getTranslationsByLanguage() {
            return translations;
        }

    }

    private class TranslationImportHelperImpl extends TranslationImportExportCommons<TranslationImportHelperImpl> implements TranslationImportHelper {

        @Override
        public void importTranslations(DataHandler data) {
            try {
                CsvPreference csvPreference = buildCsvPreference(data);
                try (CsvListReader reader = new CsvListReader(new InputStreamReader(data.getInputStream(), UTF_8), csvPreference)) {
                    List<String> header = reader.read();
                    List<String> row;
                    long processed = 0, updated = 0;
                    logger.info("loading translations for languages = {}", Joiner.on(", ").join(header.subList(2, header.size())));
                    while ((row = reader.read()) != null) {
                        String code = checkNotBlank(row.get(0));
                        logger.trace("processing translation row {} with key =< {} > values = {}", processed++, code, row.subList(2, row.size()));
                        for (int i = 2; i < header.size(); i++) {
                            String lang = checkNotBlank(header.get(i)), value = nullToEmpty(row.get(i));
                            if (!Objects.equals(nullToEmpty(translationRepository.getTranslationOrNull(code, lang)), value)) {
                                if (!isBlank(value)) {
                                    logger.debug("set translation record with key =< {} > lang = {} value = {}", code, lang, value);
                                    translationRepository.setTranslation(code, lang, value);
                                    updated++;
                                } else {
                                    logger.trace("skip translation record with key =< {} > lang = {} due to blank value", code, lang);
                                }
                            } else {
                                logger.trace("skip translation record with key =< {} > lang = {} value = {} (not changed)", code, lang, value);
                            }
                        }
                    }
                    logger.info(marker(), "processed {} lines ( {} records ), updated {} records", processed, processed * (header.size() - 2), updated);
                    logger.info(marker(), "clearing all caches");
                    cacheService.invalidateAll();
                }
            } catch (Exception ex) {
                throw runtime(ex, "error importing translations");
            }
        }

    }

    private abstract class TranslationImportExportCommons<T extends TranslationImportExportCommons> {

        private String separator;

        public T withSeparator(String separator) {
            this.separator = separator;
            return (T) this;
        }

        protected CsvPreference buildCsvPreference(@Nullable DataHandler data) {
            if (isBlank(separator)) {
                separator = firstNotBlank(data != null ? CsvUtils.detectCsvSeparatorOrNull(toDataSource(data)) : (String) null, ((Character) (char) CsvPreference.STANDARD_PREFERENCE.getDelimiterChar()).toString());
            }
            return new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE.getQuoteChar(), separator.charAt(0), CsvPreference.STANDARD_PREFERENCE.getEndOfLineSymbols()).build();
        }
    }
}
