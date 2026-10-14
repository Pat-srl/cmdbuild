/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import com.google.common.base.Joiner;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.cmdbuild.auth.multitenant.config.MultitenantConfiguration;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.bim.BimService;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.common.Constants.DMS_MODEL_DEFAULT_CLASS;
import static org.cmdbuild.common.beans.CardIdAndClassNameUtils.serializeTypeAndCode;
import org.cmdbuild.common.beans.TypeAndCode;
import org.cmdbuild.common.beans.TypeAndCodeImpl;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.config.UiConfiguration;
import org.cmdbuild.dao.beans.ClassMetadataImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwnerImpl;
import org.cmdbuild.dao.entrytype.ClassMetadata;
import static org.cmdbuild.dao.entrytype.ClassMetadata.ATTACHMENTS_INLINE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.ATTACHMENTS_INLINE_CLOSED;
import static org.cmdbuild.dao.entrytype.ClassMetadata.AUTO_VALUE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.BARCODE_SEARCH_ATTR;
import static org.cmdbuild.dao.entrytype.ClassMetadata.BARCODE_SEARCH_REGEX;
import static org.cmdbuild.dao.entrytype.ClassMetadata.CLASS_SPECIALITY;
import static org.cmdbuild.dao.entrytype.ClassMetadata.DEFAULT_EXPORT_TEMPLATE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.DEFAULT_FILTER;
import static org.cmdbuild.dao.entrytype.ClassMetadata.DEFAULT_IMPORT_TEMPLATE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.DMS_CATEGORY;
import static org.cmdbuild.dao.entrytype.ClassMetadata.HELP_MESSAGE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.NOTE_INLINE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.NOTE_INLINE_CLOSED;
import static org.cmdbuild.dao.entrytype.ClassMetadata.TEMPLATE_TYPE_TEMPLATE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.UI_ROUTING_MODE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.UI_ROUTING_TARGET;
import static org.cmdbuild.dao.entrytype.ClassMetadata.USER_STOPPABLE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.VALIDATION_RULE;
import org.cmdbuild.dao.entrytype.ClassUiRoutingMode;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.dms.DmsConfiguration;
import org.cmdbuild.easyupload.EasyuploadService;
import org.cmdbuild.lookup.DmsAttachmentCountCheck;
import static org.cmdbuild.lookup.DmsCategoryConfig.DMS_ALLOWED_EXTENSIONS;
import static org.cmdbuild.lookup.DmsCategoryConfig.DMS_CHECK_COUNT;
import static org.cmdbuild.lookup.DmsCategoryConfig.DMS_CHECK_COUNT_NUMBER;
import static org.cmdbuild.lookup.DmsCategoryConfig.DMS_MAX_FILE_SIZE;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import org.cmdbuild.widget.WidgetService;
import org.cmdbuild.workflow.WorkflowConfiguration;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.AdditionalAnswers.returnsArgAt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author afelice
 */
public class ClassSerializationHelperTest {

    // Base
    private final static String A_KNOWN_VALIDATION_RULE = "aValidationRule";
    private final static String A_KNOWN_DMS_CATEGORY = "aDmsCategory";
    private final static long A_KNOWN_DEFAULT_FILTER = 7l;
    private final static TypeAndCode A_KNOWN_IMPORT_TEMPLATE = new TypeAndCodeImpl(TEMPLATE_TYPE_TEMPLATE, "9");
    private static final String A_KNOWN_EXPORT_TEMPLATE_CODE = "11";
    private final static TypeAndCode A_KNOWN_EXPORT_TEMPLATE = new TypeAndCodeImpl(TEMPLATE_TYPE_TEMPLATE, A_KNOWN_EXPORT_TEMPLATE_CODE);

    // INNER CUSTOM METADATA STUFF
    private final static String A_KNOWN_CUSTOM_METADATA_KEY = "aCustomKey";
    private final static String A_KNOWN_CUSTOM_METADATA_VALUE = "aCustomValue";
    private final static Map<String, String> A_KNOWN_CUSTOM_INNER_METADATA = map(A_KNOWN_CUSTOM_METADATA_KEY, A_KNOWN_CUSTOM_METADATA_VALUE);

    // Detailed
    private final static String A_KNOWN_BARCODE_SEARCH_ATTR = "aBarcodeSearchAttr";
    private final static String A_KNOWN_BARCODE_SEARCH_REGEX = "aBarcodeSearchRegex";
    private final static String A_KNOWN_HELP_MESSAGE = "some help message";
    private final static String A_KNOWN_AUTO_VALUE = "aAutoValue";

    private final static ClassUiRoutingMode A_KNOWN_ROUTING_MODE = ClassUiRoutingMode.CURM_CUSTOMPAGE;
    private final static String A_KNOWN_ROUTING_TARGET = "aRoutingTarget";

    // DMS MODEL STUFF
    private final static Set<String> A_KNOWN_DMS_SUPPORTED_EXTENSIONS = set(".json", ".txt");
    private final static DmsAttachmentCountCheck A_KNOWN_DMS_COUNT_CHECK = DmsAttachmentCountCheck.DAC_AT_LEAST_NUMBER;
    private final static int A_KNOWN_DMS_COUNT_NUMBER = 512;
    private final static int A_KNOWN_DMS_MAX_FILE_SIZE = 1024;

    private static final FluentMap<String, String> A_KNOWN_CLASSE_METADATA_BASE = mapOf(String.class, String.class).with(
            UI_ROUTING_MODE, serializeEnum(A_KNOWN_ROUTING_MODE),
            UI_ROUTING_TARGET, A_KNOWN_ROUTING_TARGET,
            BARCODE_SEARCH_ATTR, A_KNOWN_BARCODE_SEARCH_ATTR,
            BARCODE_SEARCH_REGEX, A_KNOWN_BARCODE_SEARCH_REGEX,
            DEFAULT_FILTER, A_KNOWN_DEFAULT_FILTER,
            DEFAULT_IMPORT_TEMPLATE, A_KNOWN_IMPORT_TEMPLATE.serialize(),
            DEFAULT_EXPORT_TEMPLATE, A_KNOWN_EXPORT_TEMPLATE.serialize()
    ).with(A_KNOWN_CUSTOM_INNER_METADATA);

    private static final FluentMap<String, String> A_KNOWN_CLASSE_METADATA_FULL_DETAIL = map(A_KNOWN_CLASSE_METADATA_BASE).with(
            DMS_CATEGORY, A_KNOWN_DMS_CATEGORY,
            NOTE_INLINE, true,
            NOTE_INLINE_CLOSED, true,
            ATTACHMENTS_INLINE, true,
            ATTACHMENTS_INLINE_CLOSED, true,
            VALIDATION_RULE, A_KNOWN_VALIDATION_RULE,
            USER_STOPPABLE, true,
            BARCODE_SEARCH_ATTR, A_KNOWN_BARCODE_SEARCH_ATTR,
            BARCODE_SEARCH_REGEX, A_KNOWN_BARCODE_SEARCH_REGEX,
            HELP_MESSAGE, A_KNOWN_HELP_MESSAGE,
            AUTO_VALUE, A_KNOWN_AUTO_VALUE
    ).with(A_KNOWN_CUSTOM_INNER_METADATA);

    private static final FluentMap<String, String> A_KNOWN_DMS_MODEL_METADATA_FULL_DETAIL = map(A_KNOWN_CLASSE_METADATA_FULL_DETAIL).with(
            DMS_ALLOWED_EXTENSIONS, mockSerializeDmsExtensions(A_KNOWN_DMS_SUPPORTED_EXTENSIONS),
            DMS_CHECK_COUNT, serializeEnum(A_KNOWN_DMS_COUNT_CHECK),
            DMS_CHECK_COUNT_NUMBER, A_KNOWN_DMS_COUNT_NUMBER,
            DMS_MAX_FILE_SIZE, A_KNOWN_DMS_MAX_FILE_SIZE
    ).with(A_KNOWN_CUSTOM_INNER_METADATA);

    private final static FluentMap<String, String> expCmdbSerialization_Metadata_Base = mapOf(String.class, String.class).with(
            UI_ROUTING_MODE, serializeEnum(A_KNOWN_ROUTING_MODE),
            UI_ROUTING_TARGET, A_KNOWN_ROUTING_TARGET,
            BARCODE_SEARCH_ATTR, A_KNOWN_BARCODE_SEARCH_ATTR,
            BARCODE_SEARCH_REGEX, A_KNOWN_BARCODE_SEARCH_REGEX
    ).with(A_KNOWN_CUSTOM_INNER_METADATA);

    private final static FluentMap<String, Object> expCmdbSerialization_Base = map(
            "defaultFilter", A_KNOWN_DEFAULT_FILTER,
            "defaultImportTemplate", serializeTypeAndCode(A_KNOWN_IMPORT_TEMPLATE),
            "defaultExportTemplate", A_KNOWN_EXPORT_TEMPLATE_CODE,
            "description_attribute_name", ATTR_DESCRIPTION,
            "metadata", expCmdbSerialization_Metadata_Base,
            "_icon", null,
            "uiRouting_mode", serializeEnum(A_KNOWN_ROUTING_MODE),
            "uiRouting_target", A_KNOWN_ROUTING_TARGET,
            "uiRouting_custom", emptyMap()
    //"multitenantMode", serializeClassMultitenantMode(ClassMultitenantMode.CMM_NEVER)
    );

    private final static FluentMap<String, String> expCmdbSerialization_Metadata_FullDetail = map(expCmdbSerialization_Metadata_Base).with(
            HELP_MESSAGE, A_KNOWN_HELP_MESSAGE,
            AUTO_VALUE, A_KNOWN_AUTO_VALUE
    );

    private final static FluentMap<String, Object> expCmdbSerialization_FullDetail = map(expCmdbSerialization_Base).with(
            "dmsCategory", A_KNOWN_DMS_CATEGORY,
            "noteInline", true,
            "noteInlineClosed", true,
            "attachmentsInline", true,
            "attachmentsInlineClosed", true,
            "validationRule", A_KNOWN_VALIDATION_RULE,
            "stoppableByUser", true,
            "barcodeSearchAttr", A_KNOWN_BARCODE_SEARCH_ATTR,
            "barcodeSearchRegex", A_KNOWN_BARCODE_SEARCH_REGEX,
            "help", A_KNOWN_HELP_MESSAGE,
            "autoValue", A_KNOWN_AUTO_VALUE,
            "metadata", expCmdbSerialization_Metadata_FullDetail // Full detail metadata has even help and autovalue
    );

    private final static FluentMap<String, String> expCmdbSerialization_Metadata_DmsModel = map(expCmdbSerialization_Metadata_FullDetail).with(
            DMS_ALLOWED_EXTENSIONS, mockSerializeDmsExtensions(A_KNOWN_DMS_SUPPORTED_EXTENSIONS),
            DMS_CHECK_COUNT, serializeEnum(A_KNOWN_DMS_COUNT_CHECK),
            DMS_CHECK_COUNT_NUMBER, A_KNOWN_DMS_COUNT_NUMBER,
            DMS_MAX_FILE_SIZE, A_KNOWN_DMS_MAX_FILE_SIZE
    );

    private final static FluentMap<String, Object> expCmdbSerialization_DmsModel_FullDetail = map(expCmdbSerialization_FullDetail).with(
            "allowedExtensions", mockSerializeDmsExtensions(A_KNOWN_DMS_SUPPORTED_EXTENSIONS),
            "checkCount", serializeEnum(A_KNOWN_DMS_COUNT_CHECK),
            "checkCountNumber", A_KNOWN_DMS_COUNT_NUMBER,
            "maxFileSize", A_KNOWN_DMS_MAX_FILE_SIZE,
            "metadata", expCmdbSerialization_Metadata_DmsModel // DmsModel metadata has not serialized DMS stuff
    );

    private final LookupService lookupService = mock(LookupService.class);
    private final DmsConfiguration dmsConfiguration = mock(DmsConfiguration.class);
    private final ObjectTranslationService translationService = mock(ObjectTranslationService.class);
    private final UserClassService classeService = mock(UserClassService.class);

    private ClassSerializationHelper instance;

    @Before
    public void setUp() {
        UniqueTestIdUtils.prepareTuid();

        // mock description translation to return unaltered values
        when(translationService.translateLookupDescriptionSafe(anyString(), anyString(), anyString())).then(returnsArgAt(2));

        instance = new ClassSerializationHelper(
                mock(WidgetService.class), translationService,
                mock(BimService.class), mock(EasyuploadService.class),
                classeService,
                mock(MultitenantConfiguration.class), mock(WorkflowConfiguration.class),
                mock(CoreConfiguration.class), mock(UiConfiguration.class),
                mock(ContextMenuSerializationHelper.class),
                lookupService, dmsConfiguration,
                mock(OperationUserSupplier.class), mock(RoleRepository.class));
    }

    /**
     * Test of buildBasicResponse method, of class ClassSerializationHelper.
     */
    @Test
    public void testBuildBasicResponse() {
        System.out.println("buildBasicResponse");

        //arrange:
        String aClasseName = "aClasse";
        Classe classe = mockBuildClasse(aClasseName, A_KNOWN_CLASSE_METADATA_BASE);
        FluentMap<String, Object> expResultSerialization = map(expCmdbSerialization_Base).with(
                "_id", aClasseName,
                "name", aClasseName,
                "description", aClasseName,
                "speciality", serializeEnum(ClassMetadata.ClassSpeciality.CS_DEFAULT)
        );

        //act:
        FluentMap<String, Object> result = instance.buildBasicResponse(classe);

        //assert:
        checkSubset(expResultSerialization, result);
//        System.out.println("TEST: metadata %s".formatted(result.get("metadata")));
    }

    /**
     * Test of buildFullDetailResponse method, of class
     * ClassSerializationHelper.
     */
    @Test
    public void testBuildFullDetailResponse() {
        System.out.println("buildFullDetailResponse");

        //arrange:
        String aClasseName = "aClasse";
        Classe classe = mockBuildClasse(aClasseName, A_KNOWN_CLASSE_METADATA_FULL_DETAIL);
        FluentMap<String, Object> expResultSerialization = map(expCmdbSerialization_FullDetail).with(
                "_id", aClasseName,
                "name", aClasseName,
                "description", aClasseName,
                "speciality", serializeEnum(ClassMetadata.ClassSpeciality.CS_DEFAULT)
        );

        //act:
        FluentMap<String, Object> result = instance.buildFullDetailResponse(classe);

        //assert:
        checkSubset(expResultSerialization, result);
    }

    /**
     * Test of buildFullDetailResponse method, of class
     * ClassSerializationHelper.
     */
    @Test
    public void testBuildFullDetailResponse_DmsModel() {
        System.out.println("buildFullDetailResponse_DmsModel");

        //arrange:
        String aDmsModelName = DMS_MODEL_DEFAULT_CLASS;
        Classe dmsModelClasse = mockBuildDmsModel(A_KNOWN_DMS_MODEL_METADATA_FULL_DETAIL);

        CmMapUtils.FluentMap<String, Object> expResultSerialization = map(expCmdbSerialization_DmsModel_FullDetail).with(
                "_id", aDmsModelName,
                "name", aDmsModelName,
                "description", aDmsModelName,
                "speciality", serializeEnum(ClassMetadata.ClassSpeciality.CS_DMSMODEL)
        );

        //act:
        FluentMap<String, Object> result = instance.buildFullDetailResponse(dmsModelClasse);

        //assert:
        checkSubset(expResultSerialization, result);
    }

    // ** DMS **
    private static Classe mockBuildDmsModel(FluentMap<String, String> metadata) {
        return mockBuildClasse(DMS_MODEL_DEFAULT_CLASS,
                metadata.with(CLASS_SPECIALITY, serializeEnum(ClassMetadata.ClassSpeciality.CS_DMSMODEL))
        );
    }

    /**
     * As was in {@link ClassMetadataImpl} constructor.
     *
     * @param extensions
     * @return
     */
    private static String mockDeserializeDmsExtensions(Set<String> extensions) {
        return Joiner.on(",").join(nullToEmpty(extensions));
    }

    /**
     * As was in {@link ClassSerializationHelper#buildFullDetailResponse}.
     *
     * @param extensions
     * @return
     */
    private static String mockSerializeDmsExtensions(Set<String> extensions) {
        return Joiner.on(",").join(nullToEmpty(extensions));
    }

    private static Classe mockBuildClasse(String classeName,
            Map<String, String> metadata) {
        return ClasseImpl.builder()
                .withName(classeName)
                .withId(Long.valueOf(UniqueTestIdUtils.tuid())) // Without this the attribute related filter is categorized as EMBEDDED instead of CLASS_ATTRIBUTE
                .withAttributes(list(
                        mockBuildAttributeWithoutOwner(ATTR_CODE),
                        mockBuildAttributeWithoutOwner(ATTR_DESCRIPTION)
                ))
                .withMetadata(new ClassMetadataImpl(metadata))
                .build();
    }

    /**
     * Attribute with type String
     *
     * @param attributeName
     * @return
     */
    private static AttributeWithoutOwner mockBuildAttributeWithoutOwner(final String attributeName) {
        return AttributeWithoutOwnerImpl.builder()
                .withName(attributeName)
                .withType(new StringAttributeType())
                .build();
    }

    private int downcastToInt(Long id) throws NumberFormatException {
        return id.intValue();
    }

    /**
     * Checks that all entries of <code>subset</code> are contained in
     * <code>superset</code>.
     *
     * @param <K> a {@link Comparable} key, such as a {@link String}
     * @param <V>
     * @param subset
     * @param superset
     */
    private <K extends Comparable<K>, V> void checkSubset(Map<K, V> subset, Map<K, V> superset) {
        assertTrue("expected to be a subset: %s  expected: %s%s    actual: %s".formatted(System.lineSeparator(), toMsg(subset), System.lineSeparator(), toMsg(map(superset).withKeys(subset.keySet()))),
                superset.entrySet().containsAll(subset.entrySet()));
    }

    private <K extends Comparable<K>, V> String toMsg(Map<K, V> aMap) {
        Map<K, V> sortedSubset = new TreeMap<>(aMap);

        // Ordered map to readable string
        return sortedSubset.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(", "));
    }

}
