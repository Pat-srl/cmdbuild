/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff;

import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import jakarta.activation.DataHandler;
import java.io.File;
import java.io.IOException;
import static java.lang.String.format;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.cleanup.ViewType;
import static org.cmdbuild.cleanup.ViewType.VT_SQL;
import static org.cmdbuild.common.Constants.DMS_MODEL_DEFAULT_CLASS;
import static org.cmdbuild.common.Constants.DMS_MODEL_PARENT_CLASS;
import org.cmdbuild.dao.beans.AttributeMetadataImpl;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.ClassMetadataImpl;
import org.cmdbuild.dao.beans.ClassMetadataImpl.ClassMetadataImplBuilder;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.constants.SystemAttributes;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_FLOW_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_FLOW_STATUS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS;
import static org.cmdbuild.dao.entrytype.AbstractMetadata.ACTIVE;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import static org.cmdbuild.dao.entrytype.AttributeMetadata.INHERITED;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwnerImpl;
import static org.cmdbuild.dao.entrytype.ClassMetadata.CLASS_SPECIALITY;
import org.cmdbuild.dao.entrytype.ClassMetadata.ClassSpeciality;
import static org.cmdbuild.dao.entrytype.ClassMetadata.DMS_CATEGORY;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainCardinality;
import org.cmdbuild.dao.entrytype.DomainImpl;
import org.cmdbuild.dao.entrytype.attributetype.FileAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LongAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.dms.DmsService;
import static org.cmdbuild.dms.DmsService.DOCUMENT_ATTR_CARD;
import static org.cmdbuild.dms.DmsService.DOCUMENT_ATTR_CATEGORY;
import static org.cmdbuild.dms.DmsService.DOCUMENT_ATTR_DOCUMENTID;
import static org.cmdbuild.dms.DmsService.DOCUMENT_ATTR_FILENAME;
import static org.cmdbuild.dms.DmsService.DOCUMENT_ATTR_MIMETYPE;
import static org.cmdbuild.dms.DmsService.DOCUMENT_ATTR_SIZE;
import org.cmdbuild.dms.inner.DmsProviderService;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.dms.inner.DocumentInfoAndDetailImpl;
import org.cmdbuild.lookup.DmsAttachmentCountCheck;
import static org.cmdbuild.lookup.LookupConfig.LOOKUP_CONFIG_IS_DEFAULT;
import org.cmdbuild.lookup.LookupConfigImpl;
import org.cmdbuild.lookup.LookupSpeciality;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupTypeImpl;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.lookup.LookupValueImpl;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import org.cmdbuild.utils.random.CmRandomUtils;
import org.cmdbuild.view.View;
import org.cmdbuild.view.ViewImpl;
import org.cmdbuild.view.join.JoinAttributeImpl;
import org.cmdbuild.view.join.JoinViewConfig;
import org.cmdbuild.view.join.JoinViewConfigImpl;
import org.cmdbuild.workflow.dao.FlowCardRepositoryImpl;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.FlowImpl;
import org.cmdbuild.workflow.model.FlowStatus;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.ProcessImpl;
import static org.cmdbuild.workflow.utils.FlowStatusUtils.FLOW_STATUS_LOOKUP;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 *
 * @author afelice
 */
public class TestHelper_Model {

    public static final String A_KNOWN_DOCUMENT_HASH = "a9ud20s21j18dl5kbby1xnzg";
    public static final String A_KNOWN_AUTHOR = "aAuthor";
    public static final String A_KNOWN_DOCUMENT_MIME_TYPE = "application/json";
    public static final String A_KNOWN_DOCUMENT_DESCRIPTION = "aDescription";
    public static final String A_KNOWN_DOCUMENT_CREATION_DATE_STR = "2023-08-16T09:45:00Z";
    public static final String A_KNOWN_DOCUMENT_VERSION = "aVersion";
    public static final String A_KNOWN_DOCUMENT_MODIFICATION_DATE_STR = "2023-08-16T09:47:00Z";

    /**
     * Used in {@link Flow} serialization, invoking
     * {@link Flow#getFlowStatusLookup()}.
     *
     * For {@link LookupType} name, see
     * {@link FlowCardRepositoryImpl#queryOpenAndSuspended()}
     *
     * @return
     */
    public static final LookupType FLOW_STATUS_LOOKUP_TYPE = mockLookupType(FLOW_STATUS_LOOKUP);

    /**
     * Attribute with type String
     *
     * @param attributeName
     * @return
     */
    public static AttributeWithoutOwner mockBuildAttributeWithoutOwner(final String attributeName) {
        return AttributeWithoutOwnerImpl.builder()
                .withName(attributeName)
                .withType(new StringAttributeType())
                .build();
    }

    /**
     * Attribute with type String, required and unique.
     *
     * @param attributeName
     * @return
     */
    public static AttributeWithoutOwner mockBuildAttributeWithoutOwner_Unique(final String attributeName) {
        return AttributeWithoutOwnerImpl.builder()
                .withName(attributeName)
                .withType(new StringAttributeType())
                .withMeta(AttributeMetadataImpl.builder()
                        .withUnique(true)
                        .withRequired(true)
                        .build())
                .build();
    }

    /**
     * Inherited attribute with modified <i>metadata</i>, setting
     * <code>mandatory</code> to <code>true</code>.
     *
     * @param attribute
     * @return
     */
    public static AttributeWithoutOwner mockBuildAttributeWithoutOwner_Mandatory(final AttributeWithoutOwner attribute) {
        return AttributeWithoutOwnerImpl.copyOf(attribute)
                .withMeta(AttributeMetadataImpl.copyOf(attribute.getMetadata())
                        .withRequired(true)
                        .build())
                .build();
    }

    /**
     * Attribute Id (card id, serialized as <code>"_id"</code> later on)
     *
     * @return
     */
    public static AttributeWithoutOwner mockBuildAttributeId() {
        return AttributeWithoutOwnerImpl.builder()
                .withName(ATTR_ID)
                .withType(LongAttributeType.INSTANCE)
                .build();
    }

    /**
     * Attribute <code>IdClasse</code> (card type, serialized as
     * <code>"_type"</code> later on)
     *
     * @return
     */
    public static AttributeWithoutOwner mockBuildAttributeIdClasse() {
        return mockBuildAttributeWithoutOwner(ATTR_IDCLASS);
    }

    /**
     * Attribute with type Reference, to relate Cards.
     *
     * @param attributeName
     * @param domainName
     * @param direction
     * @return
     */
    public static AttributeWithoutOwner mockBuildAttributeReference(final String attributeName,
            final String domainName, final RelationDirection direction) {
        return AttributeWithoutOwnerImpl.builder()
                .withName(attributeName)
                .withType(new ReferenceAttributeType(domainName, direction))
                .build();
    }

    /**
     * Attribute with type ForeignKey, to relate single Cards (without need to
     * define a Domain).
     *
     * @param attributeName
     * @param destinationClasseName
     * @return
     */
    public static AttributeWithoutOwner mockBuildAttributeForeignKey(final String attributeName,
            final String destinationClasseName) {
        return AttributeWithoutOwnerImpl.builder()
                .withName(attributeName)
                .withType(new ForeignKeyAttributeType(destinationClasseName))
                .build();
    }

    /**
     * Attribute with type File, to relate to a DMS file.
     *
     * <p>
     * It's a reference to a {@link Card} for {@link Classe}
     * <code>DmsModel</code>, <code>BaseDocument</code> or some {@link Classe}
     * derived from <code>DmsModel</code> (a custom <i>document category</i>).
     *
     * @param attributeName
     * @param dmsCategoryLookupValue name of {@link LookupValue} to use as
     * <i>dms category</i>; it is related to a {@link LookupType}.
     * @return
     */
    public static AttributeWithoutOwner mockBuildAttributeFile(final String attributeName,
            final LookupValue dmsCategoryLookupValue) {
        return AttributeWithoutOwnerImpl.builder()
                .withName(attributeName)
                .withType(FileAttributeType.INSTANCE)
                .withMeta(
                        AttributeMetadataImpl.builder().withDmsCategory(dmsCategoryLookupValue.getCode()).build()
                )
                .build();
    }

    /**
     * Attribute of <i>super</i> {@link Classe} as seen in derived
     * {@link Classe}: <code>inherited</code> meta is <code>true</code>.
     *
     * @param attribute
     * @return
     */
    public static AttributeWithoutOwner toInherited(AttributeWithoutOwner attribute) {
        return AttributeWithoutOwnerImpl.copyOf(attribute)
                .withMeta(
                        AttributeMetadataImpl.copyOf(attribute.getMetadata()).withMetadata(INHERITED, "true").build()
                )
                .build();
    }

    /**
     * Attribute of {@link Classe} is active: <code>active</code> meta is
     * <code>true</code>.
     *
     * @param attribute
     * @return
     */
    public static AttributeWithoutOwner toDeactivated(AttributeWithoutOwner attribute) {
        return AttributeWithoutOwnerImpl.copyOf(attribute)
                .withMeta(
                        AttributeMetadataImpl.copyOf(attribute.getMetadata()).withMetadata(ACTIVE, "false").build()
                )
                .build();
    }

    /**
     *
     * @param fileAttribute
     * @return the name of {@link LookupType} related to FILE attribute <i>dms
     * category</i>.
     */
    public static String getDmsCategory(AttributeWithoutOwner fileAttribute) {
        return fileAttribute.getMetadata().getDmsCategory();
    }

    public static List<LookupValue> mockLookup(String lookupName, String lookupDescription, Long id,
            Map<String, String> codes, String defaultCode) {
        LookupType lookupType = mockLookupType(lookupName);
        return mockLookup(lookupType, lookupDescription, id, codes, defaultCode);
    }

    public static List<LookupValue> mockLookup(LookupType lookupType, String lookupDescription, Long id,
            Map<String, String> codes, String defaultCode) {
        IdWrapper idW = new IdWrapper(id);
        return codes.entrySet().stream().map(e
                -> mockLookupValue(lookupType, idW.getNext(), e.getKey(), e.getValue(), defaultCode.equals(e.getKey()))
        ).collect(toList());
    }

    public static LookupType mockLookupType(String lookupName) {
        return LookupTypeImpl.builder().withName(lookupName)
                .build();
    }

    public static LookupValue mockLookupValue(LookupType lookupType,
            Long id,
            String code, String description, boolean bDefault) {
        return mockLookupValue(lookupType, id, code, description, map(), bDefault);
    }

    public static LookupValue mockLookupValue(LookupType lookupType,
            Long id,
            String code, String description,
            Map<String, String> configs, boolean bDefault) {
        if (bDefault) {
            configs.put(LOOKUP_CONFIG_IS_DEFAULT, Boolean.TRUE.toString());
        }
        return LookupValueImpl.builder().withId(id)
                .withCode(code)
                .withDescription(description)
                .withType(lookupType)
                .withActive(true)
                .withConfigAsMap(configs)
                .build();
    }

    /**
     * Attribute with type Lookup.
     *
     * @param attributeName
     * @param lookupType
     * @return
     */
    public static AttributeWithoutOwner mockBuildAttributeLookup(final String attributeName,
            final LookupType lookupType) {
        return AttributeWithoutOwnerImpl.builder()
                .withName(attributeName)
                .withType(new LookupAttributeType(lookupType))
                .build();
    }

    /**
     * Create a simple classe.
     *
     * @param classeName
     * @return
     */
    public static Classe mockBuildClasse(String classeName) {
        final List<String> ancestors = emptyList();
        final List<AttributeWithoutOwner> attributes = emptyList();
        return mockBuildClasse(classeName, ancestors, attributes);
    }

    /**
     * Create a simple {@link Classe}, with given:
     * <ul>
     * <li>(possibly) classe ancestors;
     * <li>attributes.
     * </ul>
     *
     * @param classeName
     * @param ancestors
     * @param attributes
     * @return
     */
    public static Classe mockBuildClasse(String classeName, List<String> ancestors, List<AttributeWithoutOwner> attributes) {
        return mockBuildClasse_DmsCategory(classeName, ancestors, attributes, "");
    }

    /**
     * Create a simple {@link Classe}, with given attributes.
     *
     * @param classeName
     * @param attributes
     * @return
     */
    public static Classe mockBuildClasse(String classeName, List<AttributeWithoutOwner> attributes) {
        final List<String> ancestors = emptyList();
        return mockBuildClasse_DmsCategory(classeName, ancestors, attributes, "");
    }

    /**
     * Create a simple {@link Classe}, with given:
     * <ul>
     * <li>(possibly) classe ancestors;
     * <li>attributes;
     * <li>metadata.
     * </ul>
     *
     * @param classeName
     * @param ancestors
     * @param attributes
     * @param metadata
     * @return
     */
    public static Classe mockBuildClasse_WithMetadata(String classeName, List<String> ancestors, List<AttributeWithoutOwner> attributes,
            Map<String, String> metadata) {
        return mockBuildClasse_Metadata(classeName, ancestors, attributes, "", metadata);
    }

    /**
     * Create a simple {@link Classe}, with given:
     * <ul>
     * <li>(possibly) classe ancestors;
     * <li>attributes;
     * <li><i>dms category</i>;
     * <li>metadata.
     * </ul>
     *
     * @param classeName
     * @param ancestors
     * @param attributes
     * @param dmsCategoryType
     * @param metadata
     * @return
     */
    public static Classe mockBuildClasse_Metadata(String classeName, List<String> ancestors, List<AttributeWithoutOwner> attributes,
            String dmsCategoryType, Map<String, String> metadata) {
        ClasseImpl.ClasseBuilder builder = ClasseImpl.builder()
                .withName(classeName)
                .withId(Long.valueOf(UniqueTestIdUtils.tuid())) // Without this the attribute related filter is categorized as EMBEDDED instead of CLASS_ATTRIBUTE
                .withAttributes(attributes);

        if (!ancestors.isEmpty()) {
            builder.withAncestors(ancestors);
        }

        FluentMap<String, String> metadataToAdd = map(metadata);
        if (isNotBlank(dmsCategoryType)) {
            metadataToAdd.with(DMS_CATEGORY, dmsCategoryType);
        }

        if (!metadataToAdd.isEmpty()) {
            builder.withMetadata(new ClassMetadataImpl(metadataToAdd));
        }

        return builder.build();
    }

    /**
     * Create a simple {@link Classe}, with given:
     * <ul>
     * <li>attributes;
     * <li>{@link LookupType} name of related <i>dms category</i>.
     * </ul>
     *
     * @param classeName
     * @param attributes
     * @param dmsCategory
     * @return
     */
    public static Classe mockBuildClasse_DmsCategory(String classeName, List<AttributeWithoutOwner> attributes, String dmsCategory) {
        final List<String> ancestors = emptyList();
        return mockBuildClasse_DmsCategory(classeName, ancestors, attributes, dmsCategory);
    }

    /**
     * Create a Subclass, that is a {@link Classe} with ancestor(s).
     *
     * @param classeName
     * @param ancestors
     * @param attributes attributes of <code>Card</code>s are derived from this
     * ones.
     * @param dmsCategoryType the (name of the) {@link LookupType} for <i>dms
     * category</i>, if any.
     *
     * @return
     */
    public static Classe mockBuildClasse_DmsCategory(String classeName, List<String> ancestors, List<AttributeWithoutOwner> attributes, String dmsCategoryType) {
        ClasseImpl.ClasseBuilder builder = ClasseImpl.builder()
                .withName(classeName)
                .withId(Long.valueOf(UniqueTestIdUtils.tuid())) // Without this the attribute related filter is categorized as EMBEDDED instead of CLASS_ATTRIBUTE
                .withAttributes(attributes);

        if (!ancestors.isEmpty()) {
            builder.withAncestors(ancestors);
        }

        if (isNotBlank(dmsCategoryType)) {
            builder.withMetadata(new ClassMetadataImpl(map(DMS_CATEGORY, dmsCategoryType)));
        }

        return builder.build();
    }

    /**
     * Generate a card. *
     *
     * @param cardId
     * @param classe
     * @param attributes
     * @return
     */
    public static Card mockBuildCard(Long cardId, Classe classe, Map<String, Object> attributes) {
        return CardImpl.builder()
                .withId(cardId).withType(classe)
                .withAttributes(attributes)
                .build();
    }

    public static String buildUUID() {
        return UUID.randomUUID().toString();
    }

    public static String fetchAbsoluteTestPath(Path testResourceDir, String filename, boolean checkExistence) {
        return fetchAbsoluteTestPath(Path.of(testResourceDir.toString(), filename).toString(), checkExistence);
    }

    public static String fetchAbsoluteTestPath(String testResourceCompleteFilename, boolean checkExistence) {
        File testFile = new File("src/test/resources/%s".formatted(testResourceCompleteFilename));
        if (checkExistence) {
            assertTrue("Test file %s not found".formatted(testFile.getAbsolutePath()),
                    testFile.exists());
        }

        return testFile.getAbsolutePath();
    }

    // ** DMS **
    public static Classe mockDmsModel() {
        return ClasseImpl.copyOf(mockBuildClasse(DMS_MODEL_PARENT_CLASS, list(
                mockBuildAttributeWithoutOwner(ATTR_CODE),
                mockBuildAttributeWithoutOwner(ATTR_DESCRIPTION)
        )))
                .withMetadata(new ClassMetadataImpl(map(CLASS_SPECIALITY, serializeEnum(ClassSpeciality.CS_DMSMODEL))))
                .build();
    }

    /**
     *
     * @param dmsCategoryName_lookup name used for related {@link LookupType}.
     * @return
     */
    public static Classe mockBaseDocumentDmsModel(String dmsCategoryName_lookup) {
        return ClasseImpl.copyOf(mockBuildClasse(DMS_MODEL_DEFAULT_CLASS,
                list(DMS_MODEL_PARENT_CLASS),
                list(
                        mockBuildAttributeWithoutOwner(ATTR_CODE),
                        mockBuildAttributeWithoutOwner(ATTR_DESCRIPTION)
                )))
                .withMetadata(
                        new ClassMetadataImplBuilder(
                                map(
                                        CLASS_SPECIALITY, serializeEnum(ClassSpeciality.CS_DMSMODEL),
                                        DMS_CATEGORY, dmsCategoryName_lookup
                                ))
                                .build()
                )
                .build();
    }

    /**
     *
     * @param dmsCategoryName name used for <i>dms model</i> {@link Classe}.
     * @param dmsCategoryName_lookup name used for related {@link LookupType}.
     * See how {@link Classe#getDmsCategory() } is a {@link String} and is used
     * to get list of related {@link LookupValue} in
     * {@link #buildFullDetailExtendedResponse()}:      <pre>
     *      String dmsCategory = extendedClass.getClasse().hasDmsCategory() ? extendedClass.getClasse().getDmsCategory() : dmsConfig.getDefaultDmsCategory();
     *      List&lt;LookupValue&gt; allCategoryValues = lookupService.getAllLookup(dmsCategory).stream().filter(c -> c.isActive()).collect(toList());
     * </pre>
     *
     * @param customAttribute a custom string attribute, just to add something
     * specific to this <i>dms model</i>.
     * @return
     */
    public static Classe mockCustomDmsModel(String dmsCategoryName, String dmsCategoryName_lookup, String customAttribute) {
        return ClasseImpl.copyOf(mockBuildClasse(dmsCategoryName,
                list(DMS_MODEL_PARENT_CLASS),
                list(
                        mockBuildAttributeWithoutOwner(ATTR_CODE),
                        mockBuildAttributeWithoutOwner(ATTR_DESCRIPTION),
                        mockBuildAttributeWithoutOwner(customAttribute)
                )))
                .withMetadata(
                        new ClassMetadataImplBuilder(
                                map(
                                        CLASS_SPECIALITY, serializeEnum(ClassSpeciality.CS_DMSMODEL),
                                        DMS_CATEGORY, dmsCategoryName_lookup
                                ))
                                .build()
                )
                .build();

    }

    public static final String A_DMS_CATEGORY_LOOKUP_NAME = "aLookupDmsType";

    public static final LookupType DEFAULT_DMS_LOOKUP_TYPE = LookupTypeImpl.builder().withName(DmsService.DMS_MODEL_ALFRESCO_CATEGORY).withSpeciality(LookupSpeciality.LS_DMSCATEGORY).build();

    public static final LookupType A_DMS_LOOKUP_TYPE = LookupTypeImpl.builder().withName(A_DMS_CATEGORY_LOOKUP_NAME).withSpeciality(LookupSpeciality.LS_DMSCATEGORY).build();

    /**
     * Used by UI to select an available DmsCategory.
     *
     * @param dmsModelName
     * @param aCategoryLookupId
     * @param categoryCode
     * @param categoryDescription
     * @return
     */
    public static LookupValue mockDmsCategoryLookup(String dmsModelName,
            long aCategoryLookupId, String categoryCode, String categoryDescription) {
        return LookupValueImpl.builder()
                .withId(aCategoryLookupId)
                .withType(A_DMS_LOOKUP_TYPE)
                .withCode(categoryCode)
                .withDescription(categoryDescription)
                .withConfig(LookupConfigImpl.builder()
                        .withDmsModel(dmsModelName)
                        .withAllowedExtensions(list("pdf", "txt"))
                        .withMaxFileSize(100)
                        .withCountCheck(DmsAttachmentCountCheck.DAC_AT_LEAST_NUMBER)
                        .withCountCheckNumber(500)
                        .build())
                .withActive(true)
                .build();
    }

    /**
     * Mocks {@link DmsProvider} used by {@link DnsService}.
     *
     * @param dmsService
     * @param isDmsServiceOk
     */
    public static void mockDmsProvider(final DmsService dmsService, boolean isDmsServiceOk) {
        DmsProviderService providerService = mock(DmsProviderService.class);
        when(dmsService.isEnabled()).thenReturn(isDmsServiceOk);
        when(dmsService.getService()).thenReturn(providerService);
        when(providerService.isEnabled()).thenReturn(isDmsServiceOk);
        when(providerService.isServiceOk()).thenReturn(isDmsServiceOk);
        when(providerService.getDmsProviderServiceName()).thenReturn("TEST mock for DmsProviderService");
    }

    private static long uniqueDmsGeneratedCardId = 5000;

    public static Long mockUniqueDocumentMetadataGeneratedCardId() {
        return ++uniqueDmsGeneratedCardId;
    }

    public static void initMockUniqueDocumentMetadataGeneratedCardId(long initValue) {
        uniqueDmsGeneratedCardId = initValue;
    }

    private static long uniqueDmsDocumentId = 9000;

    public static String mockUniqueDmsDocumentId() {
        return "MockedDMS_" + ++uniqueDmsDocumentId;
    }

    public static void initMockUniqueDmsDocumentId(long initValue) {
        uniqueDmsDocumentId = initValue;
    }

    /**
     * Builds a dms metadat {@link Card} (with a <code>id</code> greater than
     * <code>5.000</code>) that contains all document metadata and a generated
     * <i>document id</i> <code>"MockedDMS_&lt;id&gt;</code>"
     *
     *
     * @param aRelatedCard
     * @param dmsModel the same of <code>aRelatedCard</code> {@link Classe}
     * <i>DmsModel</i>.
     * @param aDocMetadataGeneratedCardId a mocked id for generated {@link Card}
     * with {@link Classe} <i>dmsModel</i> that contains document metadata.
     * @param aDocumentId a mocked document id in DMS.
     * @param mimeType
     * @param filename
     * @param fileSize
     * @param dmsCategory
     * @param dummyPeculiarInfo something <i>dummy</i> added to hash,
     * description and author.
     * @return
     */
    public static DocumentInfoAndDetail mockBuildDocumentInfoAndDetail(Card aRelatedCard,
            Classe dmsModel,
            long aDocMetadataGeneratedCardId,
            String aDocumentId, String mimeType,
            String filename, Integer fileSize, String dmsCategory,
            String dummyPeculiarInfo) {
        Card aDocumentMetadataGeneratedCard = null;
        if (aRelatedCard != null) {
            // Full (generated) card with generated infos
            // Use related DmsModel (may contain custom attributes for documents to attach)
            aDocumentMetadataGeneratedCard = mockDocMetadataGeneratedCard(dmsModel,
                    aDocMetadataGeneratedCardId,
                    aRelatedCard,
                    aDocumentId,
                    filename, fileSize,
                    mimeType, dmsCategory);
        }

        return mockBuildDocumentInfoAndDetail(aDocumentId, filename, fileSize, dmsCategory, dummyPeculiarInfo).withMetadata(aDocumentMetadataGeneratedCard).build();
    }

    /**
     *
     * @param dmsModel represents the <i>dms model</i>, that is custom
     * attributes for documents.
     * @param aDocMetadataGeneratedCardId generated by repository at this card
     * insertion.
     * @param aRelatedCard the original {@link Card} that contains the
     * <code>FILE</code> {@link Attribute}
     * @param aGeneratedDmsDocumentId the <i>dms</i> generated unique document
     * id.
     * @param filename
     * @param fileSize
     * @param mimeType
     * @param dmsCategory
     * @return
     */
    public static Card mockDocMetadataGeneratedCard(Classe dmsModel, long aDocMetadataGeneratedCardId,
            Card aRelatedCard,
            String aGeneratedDmsDocumentId,
            String filename, Integer fileSize,
            String mimeType, String dmsCategory) {
        return CardImpl.builder()
                .withType(dmsModel)
                .withId(aDocMetadataGeneratedCardId)
                .withAttributes(
                        map(
                                // See DmsCategoryHelper.setDocumentMetadata()
                                DOCUMENT_ATTR_DOCUMENTID, aGeneratedDmsDocumentId,
                                DOCUMENT_ATTR_CARD, aRelatedCard.getId(),
                                DOCUMENT_ATTR_FILENAME, filename,
                                DOCUMENT_ATTR_MIMETYPE, mimeType,
                                DOCUMENT_ATTR_CATEGORY, dmsCategory,
                                DOCUMENT_ATTR_SIZE, fileSize)
                )
                .build();
    }

    /**
     * Builds a dms metadata {@link Card} (with a <code>id</code> greater than
     * <code>5.000</code>) that contains all document metadata and a generated
     * <i>document id</i> <code>"MockedDMS_&lt;id&gt;</code>"
     *
     *
     * @param dmsModel the same of <code>aRelatedCard</code> {@link Classe}
     * <i>DmsModel</i>.
     * @param aDocMetadataCardId a mocked id for metadata {@link Card} with
     * {@link Classe} <i>dmsModel</i> that contains document metadata.
     * @param aDocumentId a mocked document id in DMS.
     * @param mimeType
     * @param filename
     * @param fileSize
     * @param dmsCategory
     * @param dummyPeculiarInfo something <i>dummy</i> added to hash,
     * description and author.
     * @return
     */
    public static DocumentInfoAndDetail mockBuildDocumentInfoAndDetail_MetadataCard(Classe dmsModel,
            long aDocMetadataCardId,
            String aDocumentId, String mimeType,
            String filename, Integer fileSize, String dmsCategory,
            String dummyPeculiarInfo) {
        Card aDocumentMetadataGeneratedCard = null;
        // Full metadata card with generated infos
        // Use related DmsModel (may contain custom attributes for documents to attach)
        aDocumentMetadataGeneratedCard = mockDocMetadataCard(dmsModel,
                "Code metadata " + aDocMetadataCardId, "Description metadata " + aDocMetadataCardId,
                aDocMetadataCardId,
                aDocumentId,
                filename, fileSize,
                mimeType, dmsCategory);

        return mockBuildDocumentInfoAndDetail(aDocumentId, filename, fileSize, dmsCategory, dummyPeculiarInfo).withMetadata(aDocumentMetadataGeneratedCard).build();
    }

    /**
     *
     * @param dmsModel represents the <i>dms model</i>, that is custom
     * attributes for documents.
     * @param aCode
     * @param aDescription
     * @param aDocMetadataCardId generated by repository at this card insertion.
     * @param aDmsDocumentId the <i>dms</i> generated unique document id.
     * @param filename
     * @param fileSize
     * @param mimeType
     * @param dmsCategory
     * @return
     */
    public static Card mockDocMetadataCard(Classe dmsModel,
            String aCode, String aDescription,
            long aDocMetadataCardId,
            String aDmsDocumentId,
            String filename, Integer fileSize,
            String mimeType, String dmsCategory) {
        return CardImpl.builder()
                .withType(dmsModel)
                .withId(aDocMetadataCardId)
                .withAttributes(
                        map(
                                SystemAttributes.ATTR_CODE, aCode,
                                SystemAttributes.ATTR_DESCRIPTION, aDescription,
                                // See DmsCategoryHelper.setDocumentMetadata()
                                DOCUMENT_ATTR_DOCUMENTID, aDmsDocumentId,
                                DOCUMENT_ATTR_FILENAME, filename,
                                DOCUMENT_ATTR_MIMETYPE, mimeType,
                                DOCUMENT_ATTR_CATEGORY, dmsCategory,
                                DOCUMENT_ATTR_SIZE, fileSize)
                )
                .build();
    }

    /**
     *
     * @param documentId
     * @param filename
     * @param fileSize
     * @param dmsCategory
     * @param dummyPeculiarInfo something <i>dummy</i> added to hash,
     * description and author.
     * @return
     */
    private static DocumentInfoAndDetailImpl.DocumentInfoAndDetailImplBuilder mockBuildDocumentInfoAndDetail(String documentId,
            String filename, Integer fileSize, String dmsCategory,
            String dummyPeculiarInfo) {
        return DocumentInfoAndDetailImpl.builder()
                .withDocumentId(documentId)
                .withCategory(dmsCategory)
                .withFileName(filename).withFileSize(fileSize)
                .withHash(A_KNOWN_DOCUMENT_HASH + "_" + dummyPeculiarInfo)
                .withDescription(A_KNOWN_DOCUMENT_DESCRIPTION + "_" + dummyPeculiarInfo)
                .withMimeType(A_KNOWN_DOCUMENT_MIME_TYPE)
                .withVersion(A_KNOWN_DOCUMENT_VERSION)
                .withAuthor(A_KNOWN_AUTHOR + "_" + dummyPeculiarInfo)
                .withCreated(toDateTime(A_KNOWN_DOCUMENT_CREATION_DATE_STR)).withModified(toDateTime(A_KNOWN_DOCUMENT_MODIFICATION_DATE_STR));
    }

    public static DataHandler mockDocumentContent(DocumentInfoAndDetail aDocumentInfo, File aDocContentFile) {
        assertTrue("TEST: can't find file =< %s >".formatted(aDocContentFile.getAbsolutePath()), aDocContentFile.exists());

        DataHandler result = null;

        try {
            result = CmIoUtils.newDataHandler(
                    Files.readAllBytes(aDocContentFile.toPath()),
                    aDocumentInfo.getMimeType(),
                    aDocumentInfo.getFileName());
        } catch (IOException ex) {
            fail("TEST: error reading test file =< %s > with mime-type =< %s > - %s".formatted(aDocumentInfo.getFileName(), aDocumentInfo.getMimeType(), ex.getMessage()));
        }

        return result;
    }

    // ** PROCESS **
    /**
     *
     * <b>Note</b>: a <i>base default dms category</i> for {@link Process} will
     * result and so will be written in <i>Model</i> json output.
     *
     * @param name
     * @param innerClasse <i>classe</i> with attributes that will be inherited
     * by the process. Must be with <i>class speciality</i> as
     * <code>PROCESS</code> (in its metadata).
     * @return
     */
    public static Process mockBuildProcess(String name, Classe innerClasse) {
        if (innerClasse.hasDmsCategory()) {
            // Can't add the Dms catebory to the process metadata, even if
            // a Process inherits from a Class. So Dms Category can't be set
            // in Process model object, apparently. An bug in existing code?
            // So a <i>base default dms category</i> will result and be written in
            // <i>Model</i> json output.
        }

        return ProcessImpl.builder()
                .withInner(innerClasse)
                .build();
    }

    /**
     * Create a inner <i>classe</i> for a {@link Process}, that is a
     * {@link Classe} with <code>PROCESS</code> <i>speciality</i>.
     *
     * @param classeName
     * @param processAttributes attributes of <code>Flow</code>s are derived
     * from this ones.
     * @param dmsCategoryType the (name of the) {@link LookupType} for <i>dms
     * category</i>, if any.
     *
     * @return
     */
    public static Classe mockBuildClasse_Process(String classeName, List<AttributeWithoutOwner> processAttributes,
            String dmsCategoryType) {
        FluentMap<String, String> metadata = map(CLASS_SPECIALITY, serializeEnum(ClassSpeciality.CS_PROCESS));

        if (isNotBlank(dmsCategoryType)) {
            metadata.with(DMS_CATEGORY, dmsCategoryType);
        }

        return ClasseImpl.builder()
                .withName(classeName)
                .withId(Long.valueOf(UniqueTestIdUtils.tuid())) // Without this the attribute related filter is categorized as EMBEDDED instead of CLASS_ATTRIBUTE
                .withAttributes(processAttributes)
                .withMetadata(new ClassMetadataImpl(metadata)) // Without this, the ProcessImpl constructor will complain
                .build();
    }

    /**
     * Generate a flow.
     *
     * @param flowId a random Id created with {@link CmRandomUtils#randomId() }
     * @param innerCardId
     * @param classeProcess as done in
     * <code>RiverCardToFlowCardWrapperServiceImpl.cardToFlowCard()</code>.
     * @param process
     * @param flowProcessAttributes
     * @param flowStatus
     * @return
     */
    public static Flow mockBuildFlow(String flowId, Long innerCardId, Classe classeProcess, Process process,
            Map<String, Object> flowProcessAttributes, FlowStatus flowStatus, LookupValue flowStatusLookupValue) {
        return FlowImpl.builder()
                .withPlan(process)
                .withCard(mockBuildCard(innerCardId, classeProcess,
                        map(flowProcessAttributes).with(
                                // See WorkflowUpdateHelper.build()
                                ATTR_FLOW_ID, flowId,
                                ATTR_CODE, "ProcessCard_%s_%d".formatted(process.getName(), innerCardId),
                                ATTR_DESCRIPTION, "ProcessCard_%s %d".formatted(process.getName(), innerCardId),
                                // See WorkflowUpdateHelper.copyValuesAndTasksFromFlowData() and ProcessSerializer.serializeFlow()
                                ATTR_FLOW_STATUS, flowStatusLookupValue
                        )
                ))
                .withFlowStatus(flowStatus)
                .build();
    }

    // ** VIEW **
    public static View mockBuildView(String viewName, ViewType viewType, String sqlDef) {
        switch (viewType) {
            case VT_SQL -> {
                return ViewImpl.builder()
                        .withName(viewName)
                        .withDescription("%s Descr".formatted(viewName))
                        .withType(viewType)
                        .withSourceFunction(sqlDef).build();
            }
            case VT_JOIN -> {
                JoinViewConfig jsonConfig = JoinViewConfigImpl.builder()
                        .withMasterClass(sqlDef)
                        .withMasterClassAlias("MyClass_0")
                        .withAttribute(JoinAttributeImpl.builder().withExpr("MyClass_0.Code").withName("Attr1").withDescription("Attr 1 Descr").build())
                        .withAttribute(JoinAttributeImpl.builder().withExpr("MyClass_1.Code").withName("Attr2").withDescription("Attr 2 Descr").build())
                        .withAttribute(JoinAttributeImpl.builder().withExpr("MyClass_2.Code").withName("Attr3").withDescription("Attr 3 Descr").build())
                        //                        .withJoinElement(JoinElementImpl.builder().withDomain(domain1.getName()).withSource("MyClass_0").withDomainAlias("MyDomain_0").withTargetAlias("MyClass_1").withJoinType(JT_INNER_JOIN).withDirection(RD_DIRECT).build())
                        //                        .withJoinElement(JoinElementImpl.builder().withDomain(domain2.getName()).withSource("MyClass_1").withDomainAlias("MyDomain_1").withTargetAlias("MyClass_2").withJoinType(JT_LEFT_JOIN).withDirection(RD_INVERSE).build())
                        .build();

                return ViewImpl.builder()
                        .withName(viewName)
                        .withDescription("%s Descr".formatted(viewName))
                        .withType(viewType)
                        .withJoinConfig(jsonConfig).build();
            }
            default -> {
                fail("TEST: unhandled ViewType =< %s >".formatted(viewType));
                return null;
            }
        }
    }

// ** DOMAIN **
    public static Domain mockBuildDomainDirect(String domainName, String mockUniqueId, Classe sourceClass, Classe targetClass) {
        // Create domain RFCChangeManagertestFetchEcql
        // "_id": "RFCChangeManager",
        // "name": "RFCChangeManager",
        // "description": "RFCChangeManager",
        // "source": "RequestForChange1",
        // "sources": [
        //    "RequestForChange1"
        // ],
        // "sourceProcess": true,
        // "destination": "Employee1",
        // "destinations": [
        //   "Employee1"
        // ],
        // "cardinality": "N:1",
        DomainImpl.DomainImplBuilder domainBuilder = DomainImpl.builder()
                .withName(domainName + mockUniqueId)
                .withClass1(sourceClass) // RequestForChange1
                .withClass2(targetClass); // Employee1
        domainBuilder.withMetadata(b -> b.withCardinality(DomainCardinality.MANY_TO_ONE));

        Domain directDomain = domainBuilder.build();
        return directDomain;
    }

    /**
     * Attribute with type String
     *
     * @param attributeName
     * @param ownerClass
     * @return
     */
    public static Attribute mockBuildAttribute(final String attributeName, final Classe ownerClass) {
        return AttributeImpl.builder()
                .withOwner(ownerClass)
                .withName(attributeName)
                .withType(new StringAttributeType())
                .build();
    }

    public static Attribute mockBuildAttributeInverse(final String attributeName, Domain floorRoomDomain) {
        // Create Floor reference
        // "classId": "Room1"
        // "domain": "FloorRoom1"
        // "targetClass": "Floor1"
        // "direction": "inverse"
        // "name": "Floor",
        // "type": "reference"
        Classe room = floorRoomDomain.getTargetClass();

        return AttributeImpl.builder()
                .withOwner(room)
                .withName(attributeName)
                .withType(new ReferenceAttributeType(floorRoomDomain, RelationDirection.RD_INVERSE))
                .build();
    }

    public static Domain mockBuildDomainInverse(Classe sourceClass, Classe targetClass,
            String domainName, String mockUniqueId) {
        // Create domain FloorRoom1
        // "_id": "FloorRoom1",
        // "name": "FloorRoom1",
        // "description": "Floor room",
        // "source": "Floor1",
        // "sources": [
        //     "Floor1"
        // ],
        // "destination": "Room1",
        // "destinations": [
        //     "Room1"
        // ],
        DomainImpl.DomainImplBuilder domainBuilder = DomainImpl.builder()
                .withName(domainName + mockUniqueId)
                .withClass1(sourceClass) // Floor
                .withClass2(targetClass); // Room

        domainBuilder.withMetadata(b -> b.withCardinality(DomainCardinality.ONE_TO_MANY));
        Domain domain = domainBuilder.build();

        return domain;
    }

    public static void overrideType_Relation(Attribute attribute, Domain domain, final RelationDirection relationDirection) {
        setFieldValue(attribute, "type", new ReferenceAttributeType(domain, relationDirection));
    }

    /**
     * Overwrite private field (through Reflection)
     *
     * @param <T> type of field value
     * @param obj object to apply overwriting of field value on.
     * @param fieldName name of field in object.
     * @param value the new value to assign to the field.
     */
    public static <T> void setFieldValue(Object obj, String fieldName, T value) {

        // Obtain class
        Class<?> theObjClass = obj.getClass();

        java.lang.reflect.Field privateField = null;
        try {
            // get private field by name
            privateField = theObjClass.getDeclaredField(fieldName);

            // set field as accessible
            privateField.setAccessible(true);

            privateField.set(obj, value);
        } catch (IllegalAccessException | NoSuchFieldException exc) {
            fail("test - error accessing field =< %s.%s >, %s".formatted(theObjClass.getName(), fieldName, exc));
        } finally {
            // Restore accessibility
            if (privateField != null) {
                privateField.setAccessible(false);
            }
        }
    } // end setFieldValue method

    /**
     * If a value differs for a key in the two input maps, in the identifying
     * message for the {@link AssertionError} there will be a list of expResult
     * value (<i>left value</i>) and result value (<i>right value</i>).
     *
     *
     * @param expResult
     * @param result
     */
    public static void checkEquals_Map(Map<String, Object> expResult, Map<String, Object> result) {
        MapDifference<String, Object> mapDifference = Maps.difference(expResult, result);
        Map<String, Object> missing = mapDifference.entriesOnlyOnLeft();
        assertTrue(format("Expected but missing: %s", missing), missing.isEmpty());
        Map<String, Object> unexpected = mapDifference.entriesOnlyOnRight();
        assertTrue(format("Actual has unexpected: %s", unexpected), unexpected.isEmpty());
        Map<String, ValueDifference<Object>> differing = mapDifference.entriesDiffering();
        assertTrue(format("Expected and actual differs in: %s", differing),
                differing.isEmpty());
    }

    /**
     * Builds a generics value <code>Answer&lt;X&lt;Y&gt;&gt;</code> that
     * returns given value
     *
     * @param value
     * @return
     */
    public static <T> Answer<T> setupDummyAnswer(T value) {
        return (InvocationOnMock invocation) -> value;
    }

    public static File getTestFile(Class<?> aClass, String resourceFileName) throws URISyntaxException {
        // Get test class package
        String packageName = aClass.getPackage().getName();
        // Convert package name to directory
        String packagePathStr = packageName.replace('.', File.separatorChar);

        // Raises URISyntaxException
        return getTestFile(Path.of(packagePathStr), resourceFileName);
    }

    public static File getTestFile(Path testPackagePath, String resourceFileName) throws URISyntaxException {
        Path testFilePath = Path.of(testPackagePath.toString(), resourceFileName);

        // Raises URISyntaxException
        return getTestFile(testFilePath);
    }

    public static File getTestFile(Path testFilePath) throws URISyntaxException {
        // Raises URISyntaxException
        return new File(TestHelper_Model.class.getResource(testFilePath.toString()).toURI());
    }

} // end TestHelper_Model class

class IdWrapper {

    private Long curId;

    IdWrapper(long initialId) {
        this.curId = initialId;
    }

    long getNext() {
        return curId++;
    }
} // end IdWrapper class
