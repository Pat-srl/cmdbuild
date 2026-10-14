/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation.dao;

import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.WhereOperator.EQ;
import static org.cmdbuild.dao.core.q3.WhereOperator.IN;
import static org.cmdbuild.dao.core.q3.WhereOperator.LIKE;
import static org.cmdbuild.data.filter.SorterElementDirection.ASC;
import org.cmdbuild.translation.TranslationObject;
import org.cmdbuild.translation.file.TranslationSourceFromFileImpl;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.equal;
import static org.cmdbuild.utils.lang.CmStringUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.springframework.stereotype.Component;

@Component
public class TranslationRepositoryImpl implements TranslationRepository {

    private final DaoService dao;
    private final TranslationSource translationFromFile;
    private final CmCache<Optional<String>> translationByCodeAndLang;
    private final CmCache<List<Translation>> translationsByFilter;

    public TranslationRepositoryImpl(DaoService dao, CacheService cacheService, TranslationSourceFromFileImpl translationFromFile) {
        this.dao = checkNotNull(dao);
        this.translationFromFile = checkNotNull(translationFromFile);
        translationByCodeAndLang = cacheService.newCache("translation_by_code_and_lang", CacheConfig.SYSTEM_OBJECTS);
        translationsByFilter = cacheService.newCache("translation_by_filter", CacheConfig.SYSTEM_OBJECTS);
    }

    @Override
    public List<Translation> getTranslations(String code) {
        checkNotBlank(code, "translation code cannot be null");
        Map<String, Translation> map = map(translationFromFile.getTranslations(code), Translation::getLang)
                .accept(m -> dao.selectAll().from(TranslationImpl.class).where(ATTR_CODE, EQ, code).asList(Translation.class).forEach(t -> m.put(t.getLang(), t)));
        return list(map.values()).sorted(Translation::getLang);
    }

    @Override
    @Nullable
    public String getTranslationOrNull(String code, String lang) {
        return translationByCodeAndLang.get(key(code, lang), () -> Optional.ofNullable(doGetTranslationOrNull(code, lang))).orElse(null);
    }

    @Override
    public void loadTranslationsForLangs(Collection<String> languages, List<TranslationObject> translatableObjects) {
        doGetTranslationsForLangs(languages).forEach((k, v) -> {
            translationByCodeAndLang.put(k, Optional.ofNullable(v.getValue()));
        });
        languages.forEach(l -> {
            translatableObjects.forEach(e -> {
                translationByCodeAndLang.get(key(e.getCode(), l), Optional::empty);
            });
        });
    }

    @Nullable
    private String doGetTranslationOrNull(String code, String lang) {
        return Optional.ofNullable(getTranslationRecordOrNull(code, lang)).map(Translation::getValue).orElseGet(() -> translationFromFile.getTranslationOrNull(code, lang));
    }

    @Override
    public PagedElements<Translation> getTranslations(@Nullable String filter, @Nullable Integer offset, @Nullable Integer limit) {
        return paged(getTranslationsWithFilter(nullToEmpty(filter)), offset, limit);
    }

    @Override
    public Translation setTranslation(String code, String lang, String value) {
        TranslationImpl record = getTranslationRecordOrNull(code, lang);
        if (record == null) {
            record = dao.create(TranslationImpl.builder().withCode(code).withLang(lang).withValue(value).build());
        } else {
            record = dao.update(TranslationImpl.copyOf(record).withValue(value).build());
        }
        translationByCodeAndLang.invalidate(key(code, lang));
        translationsByFilter.invalidateAll();
        return record;
    }

    @Override
    public void deleteTranslationIfExists(String code, String lang) {
        Optional.ofNullable(getTranslationRecordOrNull(code, lang)).ifPresent(this::deleteTranslation);
    }

    @Override
    public void deleteTranslations(String code) {
        getTranslations(code).stream().filter(t -> t instanceof TranslationImpl translationImpl && translationImpl.hasId()).forEach(this::deleteTranslation);
    }

    private void deleteTranslation(Translation record) {
        dao.delete(record);
        translationByCodeAndLang.invalidate(key(record.getCode(), record.getLang()));
        translationsByFilter.invalidateAll();
    }

    @Nullable
    private TranslationImpl getTranslationRecordOrNull(String code, String lang) {
        return dao.selectAll().from(TranslationImpl.class)
                .where(ATTR_CODE, EQ, checkNotBlank(code))
                .where("Lang", EQ, checkNotBlank(lang))
                .getOneOrNull();
    }

    @Override
    public List<Translation> getAllForLanguages(Collection<String> languages) {
        return dao.selectAll().from(Translation.class).where("Lang", IN, set(languages)).orderBy("Lang", ASC, ATTR_CODE, ASC).asList();
    }

    @Nullable
    private Map<String, Translation> doGetTranslationsForLangs(Collection<String> languages) {
        Map<String, Translation> translations = map();
        dao.selectAll().from(TranslationImpl.class)
                .where("Lang", IN, set(languages))
                .asList(Translation.class).forEach(t -> translations.put(key(t.getCode(), t.getLang()), t));
        languages.forEach(l -> {
            translationFromFile.getTranslations().stream().filter(equal(Translation::getLang, l)).forEach(e -> translations.put(key(e.getCode(), e.getLang()), e));
        });
        return translations;
    }

    private List<Translation> getTranslationsWithFilter(String filter) {
        return translationsByFilter.get(filter, () -> List.copyOf(doGetTranslationsWithFilter(filter)));
    }

    private List<Translation> doGetTranslationsWithFilter(String filter) {
        Map<String, Translation> translations = map(translationFromFile.getTranslations().stream().filter(t -> t.getCode().contains(nullToEmpty(filter))).collect(toUnmodifiableList()), t -> key(t.getCode(), t.getLang()));
        dao.selectAll()
                .from(TranslationImpl.class)
                .orderBy(ATTR_CODE, ASC, "Lang", ASC)
                .accept((q) -> {
                    if (isNotBlank(filter)) {
                        q.where(ATTR_CODE, LIKE, format("%%%s%%", filter));
                    }
                })
                .asList(Translation.class).forEach(t -> translations.put(key(t.getCode(), t.getLang()), t));
        List<Translation> list = list(translations.values());
        list.sort(Comparator.comparing(Translation::getCode).thenComparing(Translation::getLang));
        return list;
    }

}
