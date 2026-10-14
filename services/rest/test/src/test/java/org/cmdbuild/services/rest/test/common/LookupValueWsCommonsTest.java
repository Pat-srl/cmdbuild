/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.services.rest.test.common;

import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupAccessType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.lookup.*;
import org.cmdbuild.service.rest.common.serializationhelpers.LookupSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.WsLookupValue;
import org.cmdbuild.service.rest.v4.wshelpers.LookupValueWsCommons;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.json.CmJsonUtils;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static org.cmdbuild.lookup.LookupConfig.LOOKUP_CONFIG_IS_DEFAULT;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_ID_SERIALIZATION;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_ADMIN;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_SYSTEM;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalAnswers.returnsArgAt;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author afelice
 */
public class LookupValueWsCommonsTest {

    protected final LookupType yesNoUndefLookupType = mockLookupType("YesNoUndef");
    protected final List<LookupValue> yesNoUndefLookupValues = mockLookup(yesNoUndefLookupType, "yes/no/undef", 101L,
            map(
                    "yesCode", "yes",
                    "noCode", "no",
                    "undefCode", "undef"
            ),
            "undefCode"
    );

    private final LookupService lookupService = mock(LookupService.class);
    private final ObjectTranslationService translationService = mock(ObjectTranslationService.class);
    private final LookupSerializationHelper serializationHelper = new LookupSerializationHelper(translationService, lookupService);
    private final LookupValueWsCommons instance;

    public LookupValueWsCommonsTest() {
        // mock description translation for lookup values
        when(translationService.translateLookupDescriptionSafe(anyString(), anyString(), anyString())).then(returnsArgAt(2));

        instance = new LookupValueWsCommons(lookupService, translationService, serializationHelper) {
        };
    }

    /**
     * Test of doRead method, of class LookupValueWsCommons.
     */
    @Test
    public void testDoRead() {
        System.out.println("doRead");

        //arrange:
        LookupType aLookupType = yesNoUndefLookupType;
        String aLookupTypeName = yesNoUndefLookupType.getName();
        LookupValue expLookupValue = yesNoUndefLookupValues.get(0);
        Long aValueId = expLookupValue.getId();
        when(lookupService.getLookup(aValueId)).thenReturn(expLookupValue);
        when(lookupService.getLookupType(eq(aLookupTypeName))).thenReturn(aLookupType);
        FluentMap<String, Object> expLookupValueSerialization = (FluentMap<String, Object>) instance.serializeLookupValue(expLookupValue);

        //act:
        Object result = instance.doRead(aLookupTypeName, aValueId);

        //assert:
        checkResponse(result, expLookupValueSerialization);
        // invoked in doRead()
        verify(lookupService, times(1)).getLookup(eq(aValueId));
        // invoked in toResponse() [called twice: 1) test fixture; 2) sut code]        
        verify(lookupService, times(2)).getLookupType(aLookupTypeName);
        verify(translationService, times(2)).translateLookupDescriptionSafe(eq(aLookupTypeName), eq(expLookupValue.getCode()), eq(expLookupValue.getDescription()));
        verifyNoMoreInteractions(lookupService);
        verifyNoMoreInteractions(translationService);
    }

    /**
     * Test of doReadAll method, ADMIN mode, of class LookupValueWsCommons.
     */
    @Test
    public void testDoReadAll_Admin() {
        System.out.println("doReadAll_Admin");

        //arrange
        Integer aLimit = 1;
        Integer aOffset = 0;
        String aFilter = "";
        String viewMode = VIEW_MODE_ADMIN;
        LookupType aLookupType = yesNoUndefLookupType;
        String aLookupTypeName = yesNoUndefLookupType.getName();
        LookupValue expLookupValue_1 = yesNoUndefLookupValues.get(0);
        LookupValue expLookupValue_2 = yesNoUndefLookupValues.get(1);
        LookupValue expLookupValue_3 = yesNoUndefLookupValues.get(2);
        final PagedElements<LookupValue> pagedValues = toPaged(yesNoUndefLookupValues);
        when(lookupService.getAllLookup(eq(aLookupTypeName), eq(aOffset), eq(aLimit), any(CmdbFilter.class))).thenReturn(pagedValues);
        when(lookupService.getLookupType(eq(aLookupTypeName))).thenReturn(aLookupType);
        FluentMap<String, Object> expLookupValueSerialization_1 = (FluentMap<String, Object>) instance.serializeLookupValue(expLookupValue_1);
        FluentMap<String, Object> expLookupValueSerialization_2 = (FluentMap<String, Object>) instance.serializeLookupValue(expLookupValue_2);
        FluentMap<String, Object> expLookupValueSerialization_3 = (FluentMap<String, Object>) instance.serializeLookupValue(expLookupValue_3);

        //act:
        Object result = instance.doReadAll(aLookupTypeName, aLimit, aOffset, aFilter, viewMode);

        //assert:
        checkResponse(result, list(
                expLookupValueSerialization_1,
                expLookupValueSerialization_2,
                expLookupValueSerialization_3
        ));
        // invoked in doReadAll()
        verify(lookupService, times(1)).getAllLookup(eq(aLookupTypeName), eq(aOffset), eq(aLimit), any(CmdbFilter.class));
        // invoked in toResponse() [called 6 times: 1) #3 test fixture; 2) #3 sut code]        
        verify(lookupService, times(6)).getLookupType(aLookupTypeName);
        verify(translationService, times(2)).translateLookupDescriptionSafe(eq(aLookupTypeName), eq(expLookupValue_1.getCode()), eq(expLookupValue_1.getDescription()));
        verify(translationService, times(2)).translateLookupDescriptionSafe(eq(aLookupTypeName), eq(expLookupValue_2.getCode()), eq(expLookupValue_2.getDescription()));
        verify(translationService, times(2)).translateLookupDescriptionSafe(eq(aLookupTypeName), eq(expLookupValue_3.getCode()), eq(expLookupValue_3.getDescription()));
        verifyNoMoreInteractions(lookupService);
        verifyNoMoreInteractions(translationService);
    }

    /**
     * Test of doReadAll method, <b>not</b> ADMIN mode, of class LookupValueWsCommons.
     */
    @Test
    public void testDoReadAll_NotAdmin() {
        System.out.println("doReadAll_NotAdmin");

        //arrange
        Integer aLimit = 1;
        Integer aOffset = 0;
        String aFilter = "";
        String viewMode = VIEW_MODE_SYSTEM;
        LookupType aLookupType = yesNoUndefLookupType;
        String aLookupTypeName = yesNoUndefLookupType.getName();
        LookupValue expLookupValue_1 = yesNoUndefLookupValues.get(0);
        LookupValue expLookupValue_2 = yesNoUndefLookupValues.get(1);
        LookupValue expLookupValue_3 = yesNoUndefLookupValues.get(2);
        final PagedElements<LookupValue> pagedValues = toPaged(yesNoUndefLookupValues);
        when(lookupService.getActiveLookup(eq(aLookupTypeName), eq(aOffset), eq(aLimit), any(CmdbFilter.class))).thenReturn(pagedValues);
        when(lookupService.getLookupType(eq(aLookupTypeName))).thenReturn(aLookupType);
        FluentMap<String, Object> expLookupValueSerialization_1 = (FluentMap<String, Object>) instance.serializeLookupValue(expLookupValue_1);
        FluentMap<String, Object> expLookupValueSerialization_2 = (FluentMap<String, Object>) instance.serializeLookupValue(expLookupValue_2);
        FluentMap<String, Object> expLookupValueSerialization_3 = (FluentMap<String, Object>) instance.serializeLookupValue(expLookupValue_3);

        //act:
        Object result = instance.doReadAll(aLookupTypeName, aLimit, aOffset, aFilter, viewMode);

        //assert:
        checkResponse(result, list(
                expLookupValueSerialization_1,
                expLookupValueSerialization_2,
                expLookupValueSerialization_3
        ));
        // invoked in doReadAll()
        verify(lookupService, times(1)).getActiveLookup(eq(aLookupTypeName), eq(aOffset), eq(aLimit), any(CmdbFilter.class));
        // invoked in toResponse() [called 6 times: 1) #3 test fixture; 2) #3 sut code]        
        verify(lookupService, times(6)).getLookupType(aLookupTypeName);
        verify(translationService, times(2)).translateLookupDescriptionSafe(eq(aLookupTypeName), eq(expLookupValue_1.getCode()), eq(expLookupValue_1.getDescription()));
        verify(translationService, times(2)).translateLookupDescriptionSafe(eq(aLookupTypeName), eq(expLookupValue_2.getCode()), eq(expLookupValue_2.getDescription()));
        verify(translationService, times(2)).translateLookupDescriptionSafe(eq(aLookupTypeName), eq(expLookupValue_3.getCode()), eq(expLookupValue_3.getDescription()));
        verifyNoMoreInteractions(lookupService);
        verifyNoMoreInteractions(translationService);
    }

    /**
     * Test of doReadDistinct method, of class LookupValueWsCommons.
     */
    @Test
    public void testDoReadDistinct() {
        System.out.println("doReadDistinct");

        //arrange
        Integer aLimit = 1;
        Integer aOffset = 0;
        String aClasseName = "aClass";
        String aLookupAttributeName = "aLookupAttribute";
        String viewMode = VIEW_MODE_SYSTEM;
        LookupType aLookupType = yesNoUndefLookupType;
        String aLookupTypeName = yesNoUndefLookupType.getName();
        LookupValue expLookupValue_1 = yesNoUndefLookupValues.get(0);
        LookupValue expLookupValue_2 = yesNoUndefLookupValues.get(1);
        LookupValue expLookupValue_3 = yesNoUndefLookupValues.get(2);
        final PagedElements<LookupValue> pagedValues = toPaged(yesNoUndefLookupValues);
        when(lookupService.getDistinctActiveLookup(eq(aLookupTypeName), eq(aOffset), eq(aLimit), anyString(), anyString())).thenReturn(pagedValues);
        when(lookupService.getLookupType(eq(aLookupTypeName))).thenReturn(aLookupType);
        FluentMap<String, Object> expLookupValueSerialization_1 = (FluentMap<String, Object>) instance.serializeLookupValue(expLookupValue_1);
        FluentMap<String, Object> expLookupValueSerialization_2 = (FluentMap<String, Object>) instance.serializeLookupValue(expLookupValue_2);
        FluentMap<String, Object> expLookupValueSerialization_3 = (FluentMap<String, Object>) instance.serializeLookupValue(expLookupValue_3);

        //act:
        Object result = instance.doReadDistinct(aLookupTypeName, aLimit, aOffset, viewMode, aClasseName, aLookupAttributeName);

        //assert:
        checkResponse(result, list(
                expLookupValueSerialization_1,
                expLookupValueSerialization_2,
                expLookupValueSerialization_3
        ));
        // invoked in doReadDistinct()
        verify(lookupService, times(1)).getDistinctActiveLookup(eq(aLookupTypeName), eq(aOffset), eq(aLimit), anyString(), anyString());
        // invoked in toResponse() [called 6 times: 1) #3 test fixture; 2) #3 sut code]        
        verify(lookupService, times(6)).getLookupType(aLookupTypeName);
        verify(translationService, times(2)).translateLookupDescriptionSafe(eq(aLookupTypeName), eq(expLookupValue_1.getCode()), eq(expLookupValue_1.getDescription()));
        verify(translationService, times(2)).translateLookupDescriptionSafe(eq(aLookupTypeName), eq(expLookupValue_2.getCode()), eq(expLookupValue_2.getDescription()));
        verify(translationService, times(2)).translateLookupDescriptionSafe(eq(aLookupTypeName), eq(expLookupValue_3.getCode()), eq(expLookupValue_3.getDescription()));
        verifyNoMoreInteractions(lookupService);
        verifyNoMoreInteractions(translationService);
    }


    private static final String A_KNOWN_LOOKUP_VALUE_SERIALIZATION = """
            {
              "_id" : 102,
              "_type" : "YesNoUndef",
              "code" : "noCode",
              "description" : "no",
              "_description_translation" : "no",
              "index" : 1,
              "active" : true,
              "default" : false,
              "icon_type" : "image",
              "icon_image" : "a_icon_image",
              "icon_font" : "a_icon_font",
              "icon_color" : "a_icon_color",
              "text_color" : "a_text_color",
              "note" : "a_note",
              "parent_type" : null
            }""";

    /**
     * Test of doCreate method, of class LookupValueWsCommons.
     */
    @Test
    public void testDoCreate() {
        System.out.println("doCreate");

        //arrange:
        LookupType aLookupType = yesNoUndefLookupType;
        String aLookupTypeName = aLookupType.getName();
        LookupValue expLookupValue_2 = yesNoUndefLookupValues.get(1);
        Map<String, Object> lookupValueDataMap = CmJsonUtils.fromJson(A_KNOWN_LOOKUP_VALUE_SERIALIZATION, MAP_OF_OBJECTS);
        // When creating a LookupValue, id hasn't to be set
        lookupValueDataMap.put(ATTR_ID_SERIALIZATION, null);
        WsLookupValue wsLookupValue = buildLookupValueData(lookupValueDataMap);
        when(lookupService.createOrUpdateLookup(any(LookupValue.class))).thenReturn(expLookupValue_2);
        when(lookupService.getLookupType(eq(aLookupTypeName))).thenReturn(aLookupType);
        FluentMap<String, Object> expLookupValueSerialization_2 = (FluentMap<String, Object>) instance.serializeLookupValue(expLookupValue_2);

        //act:
        Object result = instance.doCreate(aLookupTypeName, wsLookupValue);

        //assert:
        checkResponse(result, expLookupValueSerialization_2);
        // invoked in doCreate()
        verify(lookupService, times(1)).createOrUpdateLookup(matchLookupValue(expLookupValue_2));
        // invoked in toResponse() [called 6 times: 1) #2 test fixture; 2) #1 sut code]        
        verify(lookupService, times(3)).getLookupType(aLookupTypeName);
        verify(translationService, times(2)).translateLookupDescriptionSafe(eq(aLookupTypeName), eq(expLookupValue_2.getCode()), eq(expLookupValue_2.getDescription()));
        verifyNoMoreInteractions(lookupService);
        verifyNoMoreInteractions(translationService);
    }

    /**
     * Test of doUpdate method, of class LookupValueWsCommons.
     */
    @Test
    public void testDoUpdate() {
        System.out.println("doUpdate");

        //arrange:
        LookupType aLookupType = yesNoUndefLookupType;
        String aLookupTypeName = aLookupType.getName();
        LookupValue expLookupValue_2 = yesNoUndefLookupValues.get(1);
        Map<String, Object> lookupValueDataMap = CmJsonUtils.fromJson(A_KNOWN_LOOKUP_VALUE_SERIALIZATION, MAP_OF_OBJECTS);
        Long aKnownLookupValueId = toLong(lookupValueDataMap.get(ATTR_ID_SERIALIZATION));
        WsLookupValue wsLookupValue = buildLookupValueData(lookupValueDataMap);
        when(lookupService.createOrUpdateLookup(any(LookupValue.class))).thenReturn(expLookupValue_2);
        when(lookupService.getLookupType(eq(aLookupTypeName))).thenReturn(aLookupType);
        FluentMap<String, Object> expLookupValueSerialization_2 = (FluentMap<String, Object>) instance.serializeLookupValue(expLookupValue_2);

        //act:
        Object result = instance.doUpdate(aLookupTypeName, aKnownLookupValueId, wsLookupValue);

        //assert:
        checkResponse(result, expLookupValueSerialization_2);
        // invoked in doUpdate()
        verify(lookupService, times(1)).createOrUpdateLookup(matchLookupValue(expLookupValue_2));
        // invoked in toResponse() [called 6 times: 1) #2 test fixture; 2) #1 sut code]        
        verify(lookupService, times(3)).getLookupType(aLookupTypeName);
        verify(translationService, times(2)).translateLookupDescriptionSafe(eq(aLookupTypeName), eq(expLookupValue_2.getCode()), eq(expLookupValue_2.getDescription()));
        verifyNoMoreInteractions(lookupService);
        verifyNoMoreInteractions(translationService);
    }

    /**
     * Test of doDelete method, of class LookupValueWsCommons.
     */
    @Test
    public void testDoDelete() {
        System.out.println("doDelete");

        //arrange:
        LookupType aLookupType = yesNoUndefLookupType;
        String aLookupTypeName = aLookupType.getName();
        Long aKnownLookupValueId = 102L;

        //act:
        Object result = instance.doDelete(aLookupTypeName, aKnownLookupValueId);

        //assert:
        checkSuccess(result);
    }

    /**
     * Test of doReorder method, of class LookupValueWsCommons.
     */
    @Test
    public void testDoReorder() {
        System.out.println("doReorder");

        //arrange:
        String viewMode = VIEW_MODE_ADMIN;
        LookupType aLookupType = yesNoUndefLookupType;
        String aLookupTypeName = yesNoUndefLookupType.getName();
        List<Long> lookupValueIds = list(101L, 102L, 103L);
        LookupValue expLookupValue_1 = addId(yesNoUndefLookupValues.get(0), 101L);
        LookupValue expLookupValue_2 = addId(yesNoUndefLookupValues.get(1), 102L);
        LookupValue expLookupValue_3 = addId(yesNoUndefLookupValues.get(2), 103L);
        final PagedElements<LookupValue> pagedValues = toPaged(yesNoUndefLookupValues);
        // Invoked in doReorder()
        when(lookupService.getAllLookup(eq(aLookupTypeName))).thenReturn(pagedValues);
        // Invoked in doReorder() -> doReadAll()
        when(lookupService.getAllLookup(eq(aLookupTypeName), any(Integer.class), any(Integer.class), any(CmdbFilter.class))).thenReturn(pagedValues);
        when(lookupService.getLookupType(eq(aLookupTypeName))).thenReturn(aLookupType);
        FluentMap<String, Object> expLookupValueSerialization_1 = (FluentMap<String, Object>) instance.serializeLookupValue(expLookupValue_1);
        FluentMap<String, Object> expLookupValueSerialization_2 = (FluentMap<String, Object>) instance.serializeLookupValue(expLookupValue_2);
        FluentMap<String, Object> expLookupValueSerialization_3 = (FluentMap<String, Object>) instance.serializeLookupValue(expLookupValue_3);


        //act:
        Object result = instance.doReorder(aLookupTypeName, lookupValueIds, viewMode);

        //assert:
        checkResponse(result, list(
                expLookupValueSerialization_1,
                expLookupValueSerialization_2,
                expLookupValueSerialization_3
        ));
        // invoked in doReadAll()
        verify(lookupService, times(1)).getAllLookup(eq(aLookupTypeName));
        verify(lookupService, times(1)).getAllLookup(eq(aLookupTypeName), any(Integer.class), any(Integer.class), any(CmdbFilter.class));
        // invoked in toResponse() [called 6 times: 1) #3 test fixture; 2) #3 sut code]        
        verify(lookupService, times(6)).getLookupType(aLookupTypeName);
        verify(translationService, times(2)).translateLookupDescriptionSafe(eq(aLookupTypeName), eq(expLookupValue_1.getCode()), eq(expLookupValue_1.getDescription()));
        verify(translationService, times(2)).translateLookupDescriptionSafe(eq(aLookupTypeName), eq(expLookupValue_2.getCode()), eq(expLookupValue_2.getDescription()));
        verify(translationService, times(2)).translateLookupDescriptionSafe(eq(aLookupTypeName), eq(expLookupValue_3.getCode()), eq(expLookupValue_3.getDescription()));
//        verifyNoMoreInteractions(lookupService);
//        verifyNoMoreInteractions(translationService);     
    }

    /**
     * Test of toResponse method, of class LookupValueWsCommons.
     */
    @Test
    public void testToResponse() {
        System.out.println("toResponse");

        //arrange:
        Map<String, Object> lookupValueDataMap = CmJsonUtils.fromJson(A_KNOWN_LOOKUP_VALUE_SERIALIZATION, MAP_OF_OBJECTS);
        WsLookupValue wsLookupValue = buildLookupValueData(lookupValueDataMap);
        LookupType aLookupType = yesNoUndefLookupType;
        String aLookupTypeName = yesNoUndefLookupType.getName();
        LookupValue expLookupValue_2 = yesNoUndefLookupValues.get(1);
        when(lookupService.getLookupType(eq(aLookupTypeName))).thenReturn(aLookupType);
        FluentMap<String, Object> expLookupValueSerialization_2 = (FluentMap<String, Object>) instance.serializeLookupValue(expLookupValue_2);

        //act:
        Object result = instance.serializeLookupValue(expLookupValue_2);

        //assert:
        assertEquals(expLookupValueSerialization_2, result);
        // invoked in toResponse() [called 2 times: 1) #1 test fixture; 2) #1 sut code]        
        verify(lookupService, times(2)).getLookupType(aLookupTypeName);
        verify(translationService, times(2)).translateLookupDescriptionSafe(eq(aLookupTypeName), eq(expLookupValue_2.getCode()), eq(expLookupValue_2.getDescription()));
        verifyNoMoreInteractions(lookupService);
        verifyNoMoreInteractions(translationService);
    }

    private void checkResponse(Object result, Map<String, Object> expResultDataSerialization) {
        //assert:
        checkSuccess(result);
        assertEquals(expResultDataSerialization, ((Map) result).get("data"));
    }

    private void checkResponse(Object result, List<?> expResultDataSerialization) {
        //assert:
        checkSuccess(result);
        assertEquals(expResultDataSerialization, ((Map) result).get("data"));
    }

    private void checkSuccess(Object result) {
        //assert:
        assertEquals(true, ((Map) result).get("success"));
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
                .withAccessType(LookupAccessType.LT_DEFAULT)
//                .withId(1L)
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

    private static LookupValue addId(LookupValue lookupValue, Long aKnownId) {
        return LookupValueImpl.copyOf(lookupValue).withId(aKnownId).build();
    }

    protected LookupValue matchLookupValue(LookupValue expLookupValue) {
        return argThat(new LookupValueMatcher(expLookupValue));
    }

    private <T> Map<String, Object> getCmdbSerialization(T lookupTypeDiff, ObjectMapper objectMapper) {
        return objectMapper.convertValue(lookupTypeDiff, new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * Build a <i>serialization data</i> for a {@link LookupValue}.
     *
     * @param lookupValueCmdbSerialization
     * @return
     */
    private WsLookupValue buildLookupValueData(Map<String, Object> lookupValueCmdbSerialization) {
        return CmJsonUtils.getObjectMapper().convertValue(lookupValueCmdbSerialization, WsLookupValue.class);
    }

    private <T> PagedElements<T> toPaged(List<T> values) {
        return new PagedElements(values);
    }

} // end LookupValueWsCommonsTest class

class IdWrapper {

    private Long curId;

    IdWrapper(long initialId) {
        this.curId = initialId;
    }

    long getNext() {
        return curId++;
    }
} // end IdWrapper class

class LookupValueMatcher extends ArgumentMatcher<LookupValue> {

    private final LookupValue expLeftLookupValue;

    LookupValueMatcher(LookupValue expLeft) {
        this.expLeftLookupValue = expLeft;
    }

    @Override
    public boolean matches(Object obj) {
        LookupValue actualRight = (LookupValue) obj;

        // @todo AFE: non so perché arriva null qui
        if (actualRight == null) {
            return false;
        }

        return Objects.equals(expLeftLookupValue.getLookupType(), actualRight.getLookupType()) &&
                Objects.equals(expLeftLookupValue.getCode(), actualRight.getCode());
    }

} // end LookupValueMatcher class