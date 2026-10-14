/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.template;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.common.beans.LookupValue;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.translation.TranslationUtils.lookupDescriptionTranslationCode;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDate;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoTime;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This code was in <code>EmailTemplateProcessor.cardAttrToJsContext()</code>
 *
 * @author afelice
 */
public class JsContextBuilder {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final ContextBuilder_Timestamp CONTEXT_BUILDER_TIMESTAMP = new ContextBuilder_Timestamp();
    private static final ContextBuilder_Time CONTEXT_BUILDER_TIME = new ContextBuilder_Time();
    private static final ContextBuilder_Date CONTEXT_BUILDER_DATE = new ContextBuilder_Date();
    private static final ContextBuilder_Reference CONTEXT_BUILDER_REFERENCE = new ContextBuilder_Reference();

    @Nullable
    private final String language;

    FluentMap<AttributeTypeName, ContextBuilder> builders = map(AttributeTypeName.REFERENCE, CONTEXT_BUILDER_REFERENCE,
            AttributeTypeName.FOREIGNKEY, CONTEXT_BUILDER_REFERENCE,
            AttributeTypeName.DATE, CONTEXT_BUILDER_DATE,
            AttributeTypeName.TIME, CONTEXT_BUILDER_TIME,
            AttributeTypeName.TIMESTAMP, CONTEXT_BUILDER_TIMESTAMP);

    /**
     * To be used when language not detected yet.
     *
     * @param translationService
     */
    public JsContextBuilder(ObjectTranslationService translationService) {
        this(translationService, null);
    }

    /**
     * To be used when language already detected.
     *
     * <p>
     * Note: {@link ContextBuilder_Lookup_WithLanguage} and
     * {@link ContextBuilder_LookupArray_WithLanguage} has to be used, with
     * given language.
     *
     * @param translationService
     * @param language
     */
    public JsContextBuilder(ObjectTranslationService translationService,
            @Nullable String language) {
        checkNotNull(translationService);

        this.language = language;
        if (this.language != null) {
           builders.with(
                    AttributeTypeName.LOOKUP, new ContextBuilder_Lookup_WithLanguage(translationService, language),
                    AttributeTypeName.LOOKUPARRAY, new ContextBuilder_LookupArray_WithLanguage(translationService, language)
            );
        } else {
            builders.with(
                    AttributeTypeName.LOOKUP, new ContextBuilder_Lookup(translationService),
                    AttributeTypeName.LOOKUPARRAY, new ContextBuilder_LookupArray(translationService)
            );
        }
    }

    /**
     * This feature was in <code>EmailTemplateProcessorServiceImpl.buildJsContext(Card.String)</code>
     *
     * @param clientCard
     * @param serverCard
     *
     * @return
     */
    public String buildContext(@Nullable Card clientCard, @Nullable Card serverCard) {
        return toJson(
                map(
                        "client", buildContext(clientCard),
                        "server", buildContext(serverCard))
        );
    }

    /**
     * This feature was in
     * <code>EmailTemplateProcessorServiceImpl.cardToJsContext(Card,String)</code>
       *
     * @param card
     * @return
     */
    private Object buildContext(@Nullable Card card) {
        if (card == null) {
            return emptyMap();
        }

        return map(card.getAllValuesAsMap()).mapValues((k, v) -> build(card.getType().getAttributeOrNull(k), v));
    }

    /**
     * This feature was in <code>EmailTemplateProcessorServiceImpl.cardAttrToJsContext(Attribute,Object,String)</code>
     *
     * @param attr
     * @param value
     * @return
     */
    private Object build(@Nullable Attribute attr, @Nullable Object value) {
        if (value == null) {
            return null;
        }

        if (attr == null) {
            return value;
        }

        if (builders.containsKey(attr.getType().getName())) {
            return builders.get(attr.getType().getName()).build(attr, value);
        }

        logger.trace("process attr =< {} > [{}]: no builder", attr.getName(), attr.getType().getName());
        return value;
    }
} // end JsContextBuilder class

interface ContextBuilder {

    Object build(Attribute attr, Object value);
}

class ContextBuilder_Reference implements ContextBuilder {

    @Override
    public Map<String, String> build(Attribute attr, Object value) {
        IdAndDescription reference = rawToSystem(attr, value);
        FluentMap<String, String> result = map();
        return result.with("Id", reference.getId(),
                "_id", reference.getId()).skipNullValues()
                .with(
                        "Code", reference.getCode(), // For legacy
                        "code", reference.getCode(),
                        "Description", reference.getDescription(), // For legacy
                        "description", reference.getDescription());
    }
} // end ContextBuilder_Reference class

class ContextBuilder_Lookup implements ContextBuilder {

    protected final ObjectTranslationService translationService;

    ContextBuilder_Lookup(ObjectTranslationService translationService) {
        this.translationService = checkNotNull(translationService);
    }

    @Override
    public Map<String, String> build(Attribute attr, Object value) {
        LookupValue lookup = rawToSystem(attr, value);

        Long lookupId = lookup.getId();
        String lookupCode = lookup.getCode();
        String lookupDescription = buildLookupDescription(lookup);
        String lookupDescriptionTranslation = buildLookupDescriptionTranslation(lookup);

        return buildLookupContextMap(lookupId, lookupCode, lookupDescription, lookupDescriptionTranslation);
    }

    protected String buildLookupDescription(LookupValue lookup) {
        return lookup.getDescription();
    }

    /**
     *
     * @param lookupId Long for LOOKUP; String with each LookupValue for
     * LOOKUPARRAY
     * @param lookupCode
     * @param lookupDescription
     * @param lookupDescriptionTranslation
     * @return
     */
    protected FluentMap<String, String> buildLookupContextMap(Object lookupId, String lookupCode, String lookupDescription, String lookupDescriptionTranslation) {
        FluentMap<String, String> result = map();

        return result.with("Id", lookupId,
                "_id", lookupId).skipNullValues()
                .with(
                        "Code", lookupCode,
                        "code", lookupCode,
                        "Description", lookupDescription,
                        "description", lookupDescription,
                        "_description_translation", lookupDescriptionTranslation
                );
    }

    protected String buildLookupDescriptionTranslation(LookupValue lookup) {
        if (lookup.getLookupType() != null && lookup.getCode() != null) {
            // Translation available
            return translationService.translateLookupDescription(lookup.getLookupType(), lookup.getCode(), lookup.getDescription());
        }

        return lookup.getDescription();
    }
} // end ContextBuilder_Lookup class

class ContextBuilder_Lookup_WithLanguage extends ContextBuilder_Lookup {

    private final String language;

    ContextBuilder_Lookup_WithLanguage(ObjectTranslationService translationService, String language) {
        super(translationService);
        this.language = checkNotNull(language);
    }

    @Override
    protected String buildLookupDescription(LookupValue lookup) {
        if (lookup.getLookupType() != null && lookup.getCode() != null) {
            // Translation available
            return translationService.translateByLangAndCode(language, lookupDescriptionTranslationCode(lookup.getLookupType(), lookup.getCode()), lookup.getDescription());
        }

        return lookup.getDescription();
    }
} // end ContextBuilder_Lookup_WithLanguage class

class ContextBuilder_LookupArray extends ContextBuilder_Lookup {

    private static final String LOOKUPARRAY_CONTEXT_REPRESENTATION_SEPARATOR = ", ";

    public ContextBuilder_LookupArray(ObjectTranslationService translationService) {
        super(translationService);
    }

    @Override
    public Map<String, String> build(Attribute attr, Object value) {
        List<LookupValue> lookupArray = rawToSystem(attr, value);

        String lookupId = toFormattedArrayValues(lookupArray.stream().map(l -> l.getId().toString()).collect(toList()));
        String lookupCode = toFormattedArrayValues(lookupArray.stream().map(LookupValue::getCode).collect(toList()));
        String lookupDescription = toFormattedArrayValues(lookupArray.stream().map(l -> buildLookupDescription(l)).collect(toList()));
        String lookupDescriptionTranslation = toFormattedArrayValues(lookupArray.stream().map(l -> buildLookupDescriptionTranslation(l)).collect(toList()));

        return buildLookupContextMap(lookupId, lookupCode, lookupDescription, lookupDescriptionTranslation);
    }

    private String toFormattedArrayValues(List values) {
        return String.join(LOOKUPARRAY_CONTEXT_REPRESENTATION_SEPARATOR, values);
    }
} // end ContextBuilder_LookupArray class

class ContextBuilder_LookupArray_WithLanguage extends ContextBuilder_LookupArray {

    private final String language;

    ContextBuilder_LookupArray_WithLanguage(ObjectTranslationService translationService, String language) {
        super(translationService);
        this.language = checkNotNull(language);
    }

    /**
     * Same as in
     * {@link ContextBuilder_Lookup_WithLanguage#buildLookupDescription(org.cmdbuild.common.beans.LookupValue)}
     *
     * @param lookup
     * @return
     */
    @Override
    protected String buildLookupDescription(LookupValue lookup) {
        if (lookup.getLookupType() != null && lookup.getCode() != null) {
            // Translation available
            return translationService.translateByLangAndCode(language, lookupDescriptionTranslationCode(lookup.getLookupType(), lookup.getCode()), lookup.getDescription());
        }

        return lookup.getDescription();
    }
} // end ContextBuilder_LookupArray_WithLanguage class

class ContextBuilder_Date implements ContextBuilder {

    @Override
    public String build(Attribute attr, Object value) {
        return toIsoDate(value);
    }
} // end ContextBuilder_Date class

class ContextBuilder_Time implements ContextBuilder {

    @Override
    public String build(Attribute attr, Object value) {
        return toIsoTime(value);
    }
} // end ContextBuilder_Time class

class ContextBuilder_Timestamp implements ContextBuilder {

    @Override
    public Object build(Attribute attr, Object value) {
        return toIsoDateTime(value);
    }
} // end ContextBuilder_Timestamp class
