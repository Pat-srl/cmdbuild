/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import static java.util.Arrays.asList;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.dao.beans.DomainMetadataImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.Domain.DOMAIN_SOURCE_FILTER_SIDE;
import static org.cmdbuild.dao.entrytype.Domain.DOMAIN_TARGET_FILTER_SIDE;
import org.cmdbuild.dao.entrytype.DomainImpl;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;

/**
 *
 * @author afelice
 */
public class DomainSerializationHelperTest {

    private final static String A_KNOWN_CLASS_DOMAIN_FLOOR_FILTER = "from Floor where Id in (/(select \"Id\" from \"Floor\" where \"Building\" = 0{client:Building.Id})/)";
    private final static String A_KNOWN_CLASS_DOMAIN_FLOOR_FILTER_ECQL_ID = "e98o52dg4fam1dy6cp0t83nwafyptxpcr8lg0z4h6vr23"; // EcqlId{source=DOMAIN, id=[FloorRoom, sourceFilter]}

    private final static String A_KNOWN_CLASS_DOMAIN_ROOM_FILTER = "from Room where Id in (/(select \"Id\" from \"Room\" where \"Building\" = 0{client:Building.Id})/)";
    private final static String A_KNOWN_CLASS_DOMAIN_ROOM_FILTER_ECQL_ID = "e98o52dg4fam1dy6cp0t83nwdb8a6hune9o1m65k3xdbv"; // EcqlId{source=DOMAIN, id=[FloorRoom, targetFilter]}

    private final ObjectTranslationService translationService = mock(ObjectTranslationService.class);

    private static DomainSerializationHelper instance;

    @Before
    public void init() {
        UniqueTestIdUtils.prepareTuid();

        instance = new DomainSerializationHelper(mock(DaoService.class), translationService);
    }

    /**
     * Test of serializeDetailedDomain method, for source class domain filter,
     * of class DomainSerializationHelper.
     */
    @Test
    public void testSerializeDetailedDomain_SourceClassDomainFilter() {
        System.out.println("serializeDetailedDomain_SourceClassDomainFilter");

        //arrange:
        Classe floor = mockBuildClass("Floor");
        Classe room = mockBuildClass("Room");
        Domain domain = new DomainImpl.DomainImplBuilder()
                .withName("FloorRoom")
                .withId(1L)
                .withClass1(floor) // source
                .withClass2(room) // target
                .withMetadata(DomainMetadataImpl.builder()
                        .withSourceFilter(A_KNOWN_CLASS_DOMAIN_FLOOR_FILTER).build())
                .build();

        //act:
        Map<String, Object> result = instance.serializeDetailedDomain(domain);

        //assert:
        assertTrue(result.containsKey(DOMAIN_SOURCE_FILTER_SIDE));
        assertEquals(A_KNOWN_CLASS_DOMAIN_FLOOR_FILTER, result.get(DOMAIN_SOURCE_FILTER_SIDE));
        assertTrue(result.containsKey("ecql" + StringUtils.capitalize(DOMAIN_SOURCE_FILTER_SIDE)));
        assertThat(result.get("ecql" + StringUtils.capitalize(DOMAIN_SOURCE_FILTER_SIDE)), instanceOf(Map.class));

        Map<String, Object> resultEcqlFilter = (Map<String, Object>) result.get("ecql" + StringUtils.capitalize(DOMAIN_SOURCE_FILTER_SIDE));
        assertEquals(A_KNOWN_CLASS_DOMAIN_FLOOR_FILTER_ECQL_ID, resultEcqlFilter.get("id"));
        Map<String, Object> resultEcqlFilterBindings = (Map<String, Object>) resultEcqlFilter.get("bindings");
        assertEquals(asList("Building.Id"), resultEcqlFilterBindings.get("client"));
    }

    /**
     * Test of serializeDetailedDomain method, for destination class domain
     * filter, of class DomainSerializationHelper.
     */
    @Test
    public void testSerializeDetailedDomain_DestinationClassDomainFilter() {
        System.out.println("serializeDetailedDomain_DestinationClassDomainFilter");

        //arrange:
        Classe floor = mockBuildClass("Floor");
        Classe room = mockBuildClass("Room");
        Domain domain = new DomainImpl.DomainImplBuilder()
                .withName("FloorRoom")
                .withId(1L)
                .withClass1(floor) // source
                .withClass2(room) // target
                .withMetadata(DomainMetadataImpl.builder()
                        .withTargetFilter(A_KNOWN_CLASS_DOMAIN_ROOM_FILTER).build())
                .build();

        //act:
        Map<String, Object> result = instance.serializeDetailedDomain(domain);

        //assert:
        assertTrue(result.containsKey(DOMAIN_TARGET_FILTER_SIDE));
        assertEquals(A_KNOWN_CLASS_DOMAIN_ROOM_FILTER, result.get(DOMAIN_TARGET_FILTER_SIDE));
        assertTrue(result.containsKey("ecql" + StringUtils.capitalize(DOMAIN_TARGET_FILTER_SIDE)));
        assertThat(result.get("ecql" + StringUtils.capitalize(DOMAIN_TARGET_FILTER_SIDE)), instanceOf(Map.class));

        Map<String, Object> resultEcqlFilter = (Map<String, Object>) result.get("ecql" + StringUtils.capitalize(DOMAIN_TARGET_FILTER_SIDE));
        assertEquals(A_KNOWN_CLASS_DOMAIN_ROOM_FILTER_ECQL_ID, resultEcqlFilter.get("id"));
        Map<String, Object> resultEcqlFilterBindings = (Map<String, Object>) resultEcqlFilter.get("bindings");
        assertEquals(asList("Building.Id"), resultEcqlFilterBindings.get("client"));
    }

    /**
     * Test of serializeDetailedDomain method, for both (source and target)
     * class domain filter, of class DomainSerializationHelper.
     */
    @Test
    public void testSerializeDetailedDomain_BothClassDomainFilter() {
        System.out.println("serializeDetailedDomain_BothClassDomainFilter");

        //arrange:
        Classe floor = mockBuildClass("Floor");
        Classe room = mockBuildClass("Room");
        Domain domain = new DomainImpl.DomainImplBuilder()
                .withName("FloorRoom")
                .withId(1L)
                .withClass1(floor) // source
                .withClass2(room) // target
                .withMetadata(DomainMetadataImpl.builder()
                        .withSourceFilter(A_KNOWN_CLASS_DOMAIN_FLOOR_FILTER)
                        .withTargetFilter(A_KNOWN_CLASS_DOMAIN_ROOM_FILTER)
                        .build()
                ).build();

        //act:
        Map<String, Object> result = instance.serializeDetailedDomain(domain);

        //assert:
        Map<String, Object> resultEcqlSourceFilter = (Map<String, Object>) result.get("ecql" + StringUtils.capitalize(DOMAIN_SOURCE_FILTER_SIDE));
        assertEquals(A_KNOWN_CLASS_DOMAIN_FLOOR_FILTER_ECQL_ID, resultEcqlSourceFilter.get("id"));
        Map<String, Object> resultEcqlSourceFilterBindings = (Map<String, Object>) resultEcqlSourceFilter.get("bindings");
        assertEquals(asList("Building.Id"), resultEcqlSourceFilterBindings.get("client"));

        Map<String, Object> resultEcqlTargetFilter = (Map<String, Object>) result.get("ecql" + StringUtils.capitalize(DOMAIN_TARGET_FILTER_SIDE));
        assertEquals(A_KNOWN_CLASS_DOMAIN_ROOM_FILTER_ECQL_ID, resultEcqlTargetFilter.get("id"));
        Map<String, Object> resultEcqlTargetFilterBindings = (Map<String, Object>) resultEcqlTargetFilter.get("bindings");
        assertEquals(asList("Building.Id"), resultEcqlTargetFilterBindings.get("client"));
    }

    private static ClasseImpl mockBuildClass(String classeName) {
        return ClasseImpl.builder()
                .withName(classeName)
                .build();
    }
} // end DomainSerializationHelperTest class
