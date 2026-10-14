/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmduild.etl.loader.inner;

import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmduild.etl.loader.inner.TestHelper_Model.mockBuildAttribute;
import static org.cmduild.etl.loader.inner.TestHelper_Model.mockBuildClasse;
import static org.cmduild.etl.loader.inner.TestHelper_Model.mockBuildDomainInverse;
import static org.cmduild.etl.loader.inner.TestHelper_Model.overrideType_Relation;
import static org.cmduild.etl.loader.inner.UniqueTestIdUtils.tuid;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author afelice
 */
public class EtlTemplateProcessorServiceImplTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ProcessingTerminatorToggle mockProcessingTerminatorToggle = mock(ProcessingTerminatorToggle.class);

//    @BeforeClass
//    public static void setUpClass() {
//    }
//    
//    @AfterClass
//    public static void tearDownClass() {
//    }
    @Before
    public void setUp() {
        UniqueTestIdUtils.prepareTuid();
    }

//    @After
//    public void tearDown() {
//    }
    @Test
    public void testCreateRecord_MultiThread() {
        System.out.println("createRecord_MultiThread");

        //arrange:     
        String mockUniqueId = tuid("");
        Classe floor = TestHelper_Model.mockBuildClasse(tuid("FloorAttr"));
        Classe room = mockBuildClasse(tuid("Room"));
        Attribute floorAttribute = mockBuildAttribute("Floor", room); // with type: string
        Domain floorRoomDomain = mockBuildDomainInverse(floor, room, "FloorRoom", mockUniqueId);
        overrideType_Relation(floorAttribute, floorRoomDomain, RelationDirection.RD_INVERSE);

        //act:
        //assert:
    } // end testCreateRecord method    

    @Test
    public void testUpdateRecord_MultiThread() {
        System.out.println("updateRecord_MultiThread");

        //arrange:
        //act:
        //assert:
    }

} // end EtlTemplateProcessorServiceImplTest class

