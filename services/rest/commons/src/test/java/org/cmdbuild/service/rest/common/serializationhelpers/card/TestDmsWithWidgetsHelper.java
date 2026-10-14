/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.common.serializationhelpers.card;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import java.util.Map;
import org.cmdbuild.dao.beans.AttributeMetadataImpl;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import org.cmdbuild.dao.core.q3.WhereOperator;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.AttributeMetadata;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.FileAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IntegerAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.dms.dao.DmsModelDocumentImpl;
import org.cmdbuild.dms.inner.DmsProviderService;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.dms.inner.DocumentInfoAndDetailImpl;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupTypeImpl;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.lookup.LookupValueImpl;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.widget.model.Widget;
import org.cmdbuild.widget.model.WidgetData;
import org.cmdbuild.widget.model.WidgetDataImpl;
import org.cmdbuild.widget.model.WidgetImpl;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author afelice
 */
public class TestDmsWithWidgetsHelper {

    static final long A_KNOWN_CATEGORY_LOOKUP_ID = 1328L;
    static final String A_KNOWN_CATEGORY_CODE = "aCategoryCode";
    static final String A_KNOWN_CATEGORY_DESCRIPTION = "aDmsCategory";
    static final long A_KNOWN_DOCUMENT_FULL_GENERATED_CARD_ID = 999L;
    static final String A_KNOWN_DOCUMENT_HASH = "a9ud20s21j18dl5kbby1xnzg";
    static final String A_KNOWN_FILENAME = "file.txt";
    static final String A_KNOWN_DOCUMENT_MIME_TYPE = "application/json";
    static final int A_KNOWN_DOCUMENT_FILE_SIZE = 221;
    static final String A_KNOWN_AUTHOR = "aAuthor";
    static final String A_KNOWN_DOCUMENT_ID = "7jjh657632mm50mn34thm7cm38nl6l7h6ln1m26j697476";
    public static final String A_KNOWN_DOCUMENT_DESCRIPTION = "aDescription";
    public static final String A_KNOWN_DOCUMENT_CREATION_DATE_STR = "2023-08-16T09:45:00Z";
    public static final String A_KNOWN_DOCUMENT_VERSION = "aVersion";
    public static final String A_KNOWN_DOCUMENT_MODIFICATION_DATE_STR = "2023-08-16T09:47:00Z";

    static Attribute mockBuildAttr_Integer(String name, Classe classe) {
        return AttributeImpl.builder().withName(name).withDescription(name).withType(new IntegerAttributeType()).withMeta(AttributeMetadataImpl.emptyAttributeMetadata()).withOwner(classe).build();
    }

    private static Attribute mockBuildAttr(String name, CardAttributeType type, Classe classe, String[] metaValues) {
        return AttributeImpl.builder().withName(name).withDescription(name).withType(type).withMeta(metaValues).withOwner(classe).build();
    }

    static Attribute mockBuildAttr_File(String name, Classe classe, String fileDmsCategory) {
        return mockBuildAttr(name, FileAttributeType.INSTANCE, classe, new String[]{AttributeMetadata.DMS_CATEGORY, fileDmsCategory});
    }

    static Attribute mockBuildAttr_String(String name, Classe classe) {
        return AttributeImpl.builder().withName(name).withDescription(name).withType(new StringAttributeType()).withMeta(AttributeMetadataImpl.emptyAttributeMetadata()).withOwner(classe).build();
    }

    static DocumentInfoAndDetailImpl.DocumentInfoAndDetailImplBuilder mockBuildDocumentInfoAndDetail() {
        return DocumentInfoAndDetailImpl.builder().withDocumentId(A_KNOWN_DOCUMENT_ID).withCategory(Long.toString(A_KNOWN_CATEGORY_LOOKUP_ID)).withFileName(A_KNOWN_FILENAME).withFileSize(A_KNOWN_DOCUMENT_FILE_SIZE).withHash(A_KNOWN_DOCUMENT_HASH).withDescription(A_KNOWN_DOCUMENT_DESCRIPTION)
                .withMimeType(A_KNOWN_DOCUMENT_MIME_TYPE)
                .withVersion(A_KNOWN_DOCUMENT_VERSION).withAuthor(A_KNOWN_AUTHOR)
                .withCreated(toDateTime(A_KNOWN_DOCUMENT_CREATION_DATE_STR)).withModified(toDateTime(A_KNOWN_DOCUMENT_MODIFICATION_DATE_STR));
    }

    static DocumentInfoAndDetail mockBuildDocumentInfoAndDetail(Card aRelatedCard) {
        Card generatedFullCard = null;
        if (aRelatedCard != null) {
            // Full (generated) card with generated infos
            generatedFullCard = CardImpl.copyOf(aRelatedCard).withType(aRelatedCard.getType()).withId(A_KNOWN_DOCUMENT_FULL_GENERATED_CARD_ID).build();
        }
        return mockBuildDocumentInfoAndDetail().withMetadata(generatedFullCard).build();
    }

    private static DmsModelDocumentImpl.PgDocumentImplBuilder mockBuildDmsModelDocument() {
        return DmsModelDocumentImpl.builder().withDocumentId(A_KNOWN_DOCUMENT_ID).withCategory(Long.toString(A_KNOWN_CATEGORY_LOOKUP_ID)).withFileName(A_KNOWN_FILENAME).withFileSize(A_KNOWN_DOCUMENT_FILE_SIZE).withHash(A_KNOWN_DOCUMENT_HASH).withDescription(A_KNOWN_DOCUMENT_DESCRIPTION)
                .withMimeType(A_KNOWN_DOCUMENT_MIME_TYPE)
                .withVersion(A_KNOWN_DOCUMENT_VERSION).withAuthor(A_KNOWN_AUTHOR)
                .withCreated(toDateTime(A_KNOWN_DOCUMENT_CREATION_DATE_STR)).withModified(toDateTime(A_KNOWN_DOCUMENT_MODIFICATION_DATE_STR));
    }

    static FluentMap<String, Object> mockDocumentAttributes() {
        CmMapUtils.FluentMap<String, Object> result = map();

        return result
                .with(DmsService.DOCUMENT_ATTR_DESCRIPTION, A_KNOWN_DOCUMENT_DESCRIPTION)
                .with(DmsService.DOCUMENT_ATTR_DOCUMENTID, A_KNOWN_DOCUMENT_ID)
                .with(DmsService.DOCUMENT_ATTR_FILENAME, A_KNOWN_FILENAME)
                .with(DmsService.DOCUMENT_ATTR_VERSION, A_KNOWN_DOCUMENT_VERSION)
                .with(DmsService.DOCUMENT_ATTR_MIMETYPE, A_KNOWN_DOCUMENT_MIME_TYPE)
                .with(DmsService.DOCUMENT_ATTR_CATEGORY, A_KNOWN_CATEGORY_LOOKUP_ID)
                .with(DmsService.DOCUMENT_ATTR_SIZE, A_KNOWN_DOCUMENT_FILE_SIZE)
                .with(DmsService.DOCUMENT_ATTR_HASH, A_KNOWN_DOCUMENT_HASH)
                .with(DmsService.DOCUMENT_ATTR_CREATED, A_KNOWN_DOCUMENT_CREATION_DATE_STR);
    }

    static DmsModelDocumentImpl mockBuildDmsModelDocument(Card aRelatedCard) {
        return mockBuildDmsModelDocument().withCardId(aRelatedCard.getId()).build();
    }

    static LookupValue mockCategory(boolean categoryActive) {
        LookupType aLookupType = LookupTypeImpl.builder().withName("aLookupType").build();
        return LookupValueImpl.builder().withId(A_KNOWN_CATEGORY_LOOKUP_ID).withType(aLookupType).withCode(A_KNOWN_CATEGORY_CODE).withDescription(A_KNOWN_CATEGORY_DESCRIPTION).withActive(categoryActive).build();
    }

    /**
     * <ol>
     * <li>coming from a SelectAll()
     * <li>append <code>when(mockQueryBuilder).getXyz(expResult))</code>
     * </ol>
     *
     * @param mockQueryBuilder(
     * @param relatedClass
     */
    static void mockQueryBuilder_where_Long(QueryBuilder mockQueryBuilder, String relatedClass) {
        when(mockQueryBuilder.from(relatedClass)).thenReturn(mockQueryBuilder);
        when(mockQueryBuilder.where(anyString(), any(WhereOperator.class), anyLong())).thenReturn(mockQueryBuilder);
    }

    /**
     * <ol>
     * <li>coming from a SelectAll()
     * <li>append <code>when(mockQueryBuilder).getXyz(expResult))</code>
     * </ol>
     *
     * @param mockQueryBuilder(
     * @param relatedClass
     */
    static void mockQueryBuilder_where_String(QueryBuilder mockQueryBuilder, String relatedClass) {
        when(mockQueryBuilder.from(relatedClass)).thenReturn(mockQueryBuilder);
        when(mockQueryBuilder.where(anyString(), any(WhereOperator.class), anyString())).thenReturn(mockQueryBuilder);
    }

    static String buildProp(String attrName, String propName) {
        return format("%s.%s", attrName, propName);
    }

    static String buildWithUnderscoresProp(String attrName, String propName) {
        return format("_%s_%s", attrName, propName);
    }

    /**
     * <ol>
     * <li>coming from a SelectAll()
     * <li>append <code>when(mockQueryBuilder).getXyz(expResult))</code>
     * </ol>
     *
     * @param mockQueryBuilder
     * @param relatedClass
     */
    static void mockQueryBuilder_whereExpr(QueryBuilder mockQueryBuilder, String relatedClass) {
        when(mockQueryBuilder.from(relatedClass)).thenReturn(mockQueryBuilder);
        when(mockQueryBuilder.whereExpr(anyString(), any(Object[].class))).thenReturn(mockQueryBuilder);
    }

    static void mockDmsService(final DmsService dmsService, boolean isDmsServiceOk) {
        DmsProviderService providerService = mock(DmsProviderService.class);
        when(providerService.isServiceOk()).thenReturn(isDmsServiceOk);
        when(dmsService.getService()).thenReturn(providerService);
    }

    static QueryBuilder mockQueryBuilder_SelectAll(final DaoService dao) {
        QueryBuilder mockQueryBuilder = mock(QueryBuilder.class);
        when(dao.selectAll()).thenReturn(mockQueryBuilder);
        return mockQueryBuilder;
    }

    /**
     * If a value differs for a key in the two input maps, in the identifying
     * message for the {@link AssertionError} there will be a list of expResult
     * value (<i>left value</i>) and result value (<i>right value</i>).
     *
     * @param expResult
     * @param result
     */
    static void checkEquals_Map(Map<String, Object> expResult, Map<String, Object> result) {
        MapDifference<String, Object> mapDifference = Maps.difference(expResult, result);
        Map<String, Object> missing = mapDifference.entriesOnlyOnLeft();
        assertTrue(format("Expected but missing: %s", missing), missing.isEmpty());
        Map<String, Object> unexpected = mapDifference.entriesOnlyOnRight();
        assertTrue(format("Actual has unexpected: %s", unexpected), unexpected.isEmpty());
        Map<String, MapDifference.ValueDifference<Object>> differing = mapDifference.entriesDiffering();
        assertTrue(format("Expected and actual differs in: %s", differing), differing.isEmpty());
    }

    // ================ WIDGETS ===============
    static Widget mockBuildWidget(final String aId, final String aWidgetType, final String aWidgetLabel) {
        return mockBuildWidget(aId, aWidgetType, aWidgetLabel, map());
    }

    private static Widget mockBuildWidget(final String aId, final String aWidgetType, final String aWidgetLabel, CmMapUtils.FluentMap<String, Object> widgetExtension) {
        return WidgetImpl.builder().withType(aWidgetType).withLabel(aWidgetLabel).withId(aId).withActive(true).withData(widgetExtension).withContext(emptyMap()).build();
    }

    static WidgetData mockBuildWidgetData(final String aId, final String aWidgetType, final String aWidgetLabel) {
        return mockBuildWidgetData(aId, aWidgetType, aWidgetLabel, map());
    }

    private static WidgetData mockBuildWidgetData(final String aId, final String aWidgetType, final String aWidgetLabel, CmMapUtils.FluentMap<String, Object> widgetExtension) {
        return WidgetDataImpl.builder().withType(aWidgetType).withLabel(aWidgetLabel).withId(aId).withIsActive(true).withData(widgetExtension).build();
    }

    /**
     * As done in
     * {@link ClassSerializationHelper#serializeWidget(org.cmdbuild.widget.model.WidgetData, java.lang.String)}
     *
     * @param aWidget
     * @return
     */
    static CmMapUtils.FluentMap<Object, Object> mockSerializeWidget(Widget aWidget) {
        return map("_id", aWidget.getId(), "_label", aWidget.getLabel(), "_type", aWidget.getType(), "_active", aWidget.isActive(), "_required", aWidget.isRequired(), "_alwaysenabled", aWidget.isAlwaysEnabled(), "_hideincreation", aWidget.hideInCreation(), "_hideinedit", aWidget.hideInEdit(), "_inline", aWidget.isInline(), "_inlineclosed", aWidget.isInlineClosed(), "_inlinebefore", aWidget.getInlineBefore(), "_inlineafter", aWidget.getInlineAfter(), "_output", null, "_label_translation", aWidget.getLabel());
    }

}
