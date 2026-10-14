/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.ecql.inner;

import jakarta.inject.Provider;
import org.cmdbuild.dao.beans.DomainMetadataImpl;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.entrytype.*;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dashboard.DashboardRepository;
import org.cmdbuild.easytemplate.store.EasytemplateRepository;
import org.cmdbuild.ecql.EcqlExpression;
import org.cmdbuild.ecql.EcqlRepository;
import org.cmdbuild.homepage.HomepageRepository;
import org.cmdbuild.navtree.NavTreeService;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.sysparam.SysparamRepository;
import org.cmdbuild.widget.WidgetService;
import org.cmdbuild.workflow.WorkflowService;
import org.junit.Before;
import org.junit.Test;

import static java.lang.Boolean.TRUE;
import static org.cmdbuild.dao.entrytype.AttributeMetadata.FILTER;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author afelice
 */
public class EcqlRepositoryImplTest {

    private final static String A_KNOWN_FLOOR_FILTER = "from Floor where Id in (/(select \"Id\" from \"Floor\" where \"Building\" = 0{client:Building.Id})/)";
    private final static String A_KNOWN_DOMAIN_CLASS_FILTER_ECQL_ID = "e98o52dg4fam1dy6cp0t83nwafyptxpcr8lg0z4h6vr23";
    private final static String A_KNOWN_DOMAIN_CLASS_MASTER_DETAIL_FILTER_ECQL_ID = "3aujtzmrp32naspn";
    private static final String A_KNOWN_DOMAIN_SOURCE_CLASS_NAME = "Floor";
    private static final String A_KNOWN_DOMAIN_NAME = "FloorRoom";

    private final ClasseRepository classeRepository = mock(ClasseRepository.class);
    private final DomainRepository domainRepository = mock(DomainRepository.class);

    private EcqlRepository instance;

    @Before
    public void setUp() {
        //sessionService.createAndSet(LoginDataImpl.buildNoPasswordRequired(SYSTEM_USER));

        instance = new EcqlRepositoryImpl(classeRepository, domainRepository,
                mockProvider(ReportService.class), mockProvider(EasytemplateRepository.class),
                mockProvider(SysparamRepository.class), mockProvider(NavTreeService.class),
                mockProvider(DashboardRepository.class), mockProvider(WorkflowService.class),
                mockProvider(WidgetService.class), mockProvider(HomepageRepository.class));
    }

    /**
     * Test of getById method, class attribute, of class EcqlRepositoryImpl.
     */
    @Test
    public void testGetById_ClassAttributeFilter() {
        System.out.println("getById_ClassAttributeFilter");

        //arrange:
        Classe room = mockClasse("Room");
        Classe floor = mockBuildClasse("Floor");
        final String floorRoom_ClassAttributefilterEcqlId = "1kpo50n3y230ezume2pywttnt8b";

        // Create domain FloorRoom2
        Domain floorRoomDomain = DomainImpl.builder()
                .withName(A_KNOWN_DOMAIN_NAME)
                .withId(1L)
                .withClass1(floor) // source
                .withClass2(room) // target
                .build();
        // Create Floor reference
        Attribute floorAttribute = AttributeImpl.builder()
                .withOwner(room)
                .withName("Floor")
                .withType(new ReferenceAttributeType(floorRoomDomain, RelationDirection.RD_INVERSE))
                .withMeta(FILTER, A_KNOWN_FLOOR_FILTER)
                .build();
        //when(room.getAllAttributesAsMap()).thenReturn(map(floorAttribute.getName(), floorAttribute));
        when(room.getAttribute(eq(floorAttribute.getName()))).thenReturn(floorAttribute);

        when(classeRepository.getClasse(eq(room.getName()))).thenReturn(room);
        when(domainRepository.getDomain(eq(floorRoomDomain.getName()))).thenReturn(floorRoomDomain);

        //act:
        EcqlExpression ecql = instance.getById(floorRoom_ClassAttributefilterEcqlId);

        //assert:
        assertEquals(A_KNOWN_FLOOR_FILTER, ecql.getEcql());
    }

    /**
     * Test of getById method, domain class, of class EcqlRepositoryImpl.
     */
    @Test
    public void testGetById_DomainClassFilter() {
        System.out.println("getById_DomainClassFilter");

        //arrange:
        Classe room = mockBuildClasse("Room"),
                floor = mockBuildClasse(A_KNOWN_DOMAIN_SOURCE_CLASS_NAME);
        final String floorRoom_filterEcqlId = A_KNOWN_DOMAIN_CLASS_FILTER_ECQL_ID;

        // Create domain FloorRoom2
        Domain floorRoomDomain = DomainImpl.builder()
                .withName(A_KNOWN_DOMAIN_NAME)
                .withId(1L)
                .withClass1(floor) // source
                .withClass2(room) // target
                .withMetadata(DomainMetadataImpl.builder()
                        .withSourceFilter(A_KNOWN_FLOOR_FILTER)
                        .build())
                .build();

        when(domainRepository.getDomain(eq(floorRoomDomain.getName()))).thenReturn(floorRoomDomain);

        //act:
        EcqlExpression ecql = instance.getById(floorRoom_filterEcqlId);

        //assert:
        assertEquals(A_KNOWN_FLOOR_FILTER, ecql.getEcql());
    }

    /**
     * Test of getById method, of class EcqlRepositoryImpl.
     */
    @Test
    public void testGetById_DomainClassMasterDetailFilter() {
        System.out.println("getById_DomainClassMasterDetailFilter");

        //arrange:
        Classe room = mockBuildClasse("Room"),
                floor = mockBuildClasse(A_KNOWN_DOMAIN_SOURCE_CLASS_NAME);
        final String floorRoom_masterDetailFilterEcqlId = A_KNOWN_DOMAIN_CLASS_MASTER_DETAIL_FILTER_ECQL_ID;

        // Create domain FloorRoom2
        Domain floorRoomDomain = DomainImpl.builder()
                .withName(A_KNOWN_DOMAIN_NAME)
                .withId(1L)
                .withClass1(floor) // source
                .withClass2(room) // target
                .withMetadata(DomainMetadataImpl.builder()
                        .withIsMasterDetail(TRUE)
                        .withMasterDetailFilter(A_KNOWN_FLOOR_FILTER)
                        .build())
                .build();

        when(domainRepository.getDomain(eq(floorRoomDomain.getName()))).thenReturn(floorRoomDomain);

        //act:
        EcqlExpression ecql = instance.getById(floorRoom_masterDetailFilterEcqlId);

        //assert:
        assertEquals(A_KNOWN_FLOOR_FILTER, ecql.getEcql());
    }

    private static Classe mockClasse(String classeName) {
        Classe mock = mock(Classe.class);
        when(mock.getName()).thenReturn(classeName);

        return mock;
    }

    private static ClasseImpl mockBuildClasse(String classeName) {
        return mockClasseBuilder(classeName)
                .build();
    }

    private static ClasseImpl.ClasseBuilder mockClasseBuilder(String classeName) {
        return ClasseImpl.builder()
                .withName(classeName);
    }

    private <T> Provider<T> mockProvider(Class<T> anyToStub) {
        T stub = (T) mock(anyToStub);

        return directProvider(stub);
    }

    private <T> Provider<T> directProvider(T injected) {
        return () -> injected;
    }

}
