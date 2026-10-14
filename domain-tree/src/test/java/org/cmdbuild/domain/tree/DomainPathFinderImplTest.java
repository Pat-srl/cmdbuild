/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.domain.tree;

import com.google.common.base.Joiner;
import java.nio.file.Path;
import java.text.NumberFormat;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.DomainDataBase;
import static org.cmdbuild.DomainDataBase.TEST_RESOURCE_PATH;
import static org.cmdbuild.TestHelper_Model.mockBuildAttributeReference;
import static org.cmdbuild.TestHelper_Model.mockBuildCard;
import static org.cmdbuild.TestHelper_Model.mockBuildClasse;
import static org.cmdbuild.TestHelper_Model.mockBuildDomain_1N;
import static org.cmdbuild.UniqueTestIdUtils.id;
import org.cmdbuild.classe.access.UserDomainServiceImpl;
import org.cmdbuild.common.beans.IdAndDescriptionImpl;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.beans.RelationImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.STANDARD_CLASS_INFO_ATTRIBUTES;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.domain.tree.LoggerHelper.buildLog_Short;
import org.cmdbuild.services.serialization.domain.tree.CardData;
import org.cmdbuild.services.serialization.domain.tree.DomainNodeData;
import org.cmdbuild.services.serialization.domain.tree.DomainTreeData;
import org.cmdbuild.sync.CardSync;
import org.cmdbuild.sync.ClasseSync;
import org.cmdbuild.sync.DomainSync;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toPrettyJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author afelice
 */
public class DomainPathFinderImplTest extends DomainDataBase {

    private final static String A_KNOWN_DOMAIN_TREE_ONE_PATH_JSON_FILE = Path.of(TEST_RESOURCE_PATH.toString(), "domain_tree_01_one_path.json").toString();
    private final static String A_KNOWN_DOMAIN_TREE_ONE_PATH_SUPERCLASS_JSON_FILE = Path.of(TEST_RESOURCE_PATH.toString(), "domain_tree_02_one_path_superclass.json").toString();
    private final static String A_KNOWN_DOMAIN_TREE_TWO_PATHS_JSON_FILE = Path.of(TEST_RESOURCE_PATH.toString(), "domain_tree_04_two_paths.json").toString();
    private final static String A_KNOWN_DOMAIN_TREE_TWO_PATHS_USE_CASE_7_JSON_FILE = Path.of(TEST_RESOURCE_PATH.toString(), "domain_tree_07_two_paths.json").toString();

    private final static String A_KNOWN_CQL_FILTER_ON_FLOOR = "a_filter_on_Floor";

    private final static String A_KNOWN_DOMAIN_TREE_CR1_LEAF_FILTER_JSON_FILE = Path.of(TEST_RESOURCE_PATH.toString(), "domain_tree_CR1_leafFilter.json").toString();

    protected final CardSync cardSync = mock(CardSync.class);
    protected final ClasseSync classeSync = mock(ClasseSync.class);
    protected final DomainSync domainSync = mock(DomainSync.class);

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    DomainPathFinderImpl instance = new DomainPathFinderImpl(cardSync, classeSync,
            domainSync);

    static final String A_KNOWN_LEAF_CARD_DATA = """
                                    {
                                        "_id": 20,
                                        "_type": "Room",
                                        "Code": "G01",
                                        "Description": "Room 01 [Tecnoteca headquarters]"
                                    }
                                    """;

    static final String A_KNOWN_LEAF_PATH_TWO_CARD_DATA = """
                                    {
                                        "_id": 70,
                                        "_type": "Room",
                                        "Code": "G01",
                                        "Description": "Room 02 [Tecnoteca headquarters]"
                                    }
                                    """;

    static final String A_NOT_EXISTING_LEAF_CARD_DATA = """
                                    {
                                        "_id": 31,
                                        "_type": "Room",
                                        "Code": "G01",
                                        "Description": "Room 01 [Tecnoteca headquarters]"
                                    }
                                    """;

    /**
     * Test of findPathToRoot method, <i>root</i> {@link Card} found, of class
     * DomainPathFinderImpl.
     *
     * <p>
     * f.e. <code>FloorRoom</code> is a <code>1:N</code> {@link Domain} and
     * <code>Room</code> {@link Classe} contains a <i>inverse relation</i>
     * (<code>REFERENCE</code> {@link Attribute} with
     * <code>direction=inverse</code>).
     */
    @Test
    public void testFindPathToRoot_UseCase01_OnePath_RootFound() {
        System.out.println("findPathToRoot_UseCase01_OnePath_RootFound");

        //arrange:
        List<CardMatch> expResult = list(
                new CardMatch("Complex", 50L),
                new CardMatch("Building", 40L),
                new CardMatch("Floor", 30L),
                new CardMatch("Room", 20L)
        );
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_ONE_PATH_JSON_FILE));
        DomainTreeData domainTreeData = fromJson(domainTreeDataJson, DomainTreeData.class);
        assertFalse(domainTreeData.nodes.isEmpty());
        CardData leafCardData = fromJson(A_KNOWN_LEAF_CARD_DATA, CardData.class);
        assertEquals(A_KNOWN_LEAF_CLASS_NAME, leafCardData.className);
        assertEquals(20L, toLong(leafCardData.id));
        // First branch, normal classes
        mockClasseReturn(complexClasse);
        mockClasseReturn(buildingClasse);
        mockClasseReturn(floorClasse);
        mockClasseReturn(roomClasse);
        mockCardReturn(aCard_Room20, roomClasse,
                aCard_Floor30, aCard_Building40, aCard_Complex50);
        mockRelationReturn_Inverse(aDomain_FloorRoom, aCard_Floor30, aCard_Room20);
        mockRelationReturn_Inverse(aDomain_BuildingFloor, aCard_Building40, aCard_Floor30);
        mockRelationReturn_Inverse(aDomain_ComplexBuilding, aCard_Complex50, aCard_Building40);

        //act:
        List<Card> result = instance.findPathToRoot(domainTreeData, leafCardData);

        //assert:
        checkPath(expResult, result);
    }

    /**
     * Test of findPathToRoot method, a <code>N:1</code> {@link Domain} with a
     * <code>direct</code> <code>REFERENCE</code> {@link Attribute} in
     * <code>parent</code> {@link Classe}, <i>root</i> {@link Card} found, of
     * class DomainPathFinderImpl.
     *
     * <p>
     * One branch, <code>N:1</code> {@link Domain} with a <code>direct</code>
     * <code>REFERENCE</code> {@link Attribute} in<i>parent</i>, so multiple
     * {@link Card}s can be found as parent in a <i>path</i> to a <i>root</i>,
     * but only first <i>path</i> is followed:
     * <pre>
     * Room.20
     *     (A) <- Floor.30 -> Building.40 -> Complex.50
     *     (B) -> Floor.31 -> Building.41 -> Complex.51
     * </pre>
     */
    @Test
    public void testFindPathToRoot_UseCase01_b_OnePath_DomainN1Direct_RootFound() {
        System.out.println("findPathToRoot_UseCase01_b_OnePath_DomainN1Direct_RootFound");

        //arrange:
        List<CardMatch> expResult = list(
                new CardMatch("Complex", 50L),
                new CardMatch("Building", 40L),
                new CardMatch("Floor", 30L),
                new CardMatch("Room", 20L)
        );
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_ONE_PATH_JSON_FILE));
        DomainTreeData domainTreeData = fromJson(domainTreeDataJson, DomainTreeData.class);
        assertFalse(domainTreeData.nodes.isEmpty());
        CardData leafCardData = fromJson(A_KNOWN_LEAF_CARD_DATA, CardData.class);
        assertEquals(A_KNOWN_LEAF_CLASS_NAME, leafCardData.className);
        assertEquals(20L, toLong(leafCardData.id));
        // First branch, normal classes
        mockClasseReturn(complexClasse);
        mockClasseReturn(buildingClasse);
        mockClasseReturn(floorClasse_UseCase1_b);
        mockClasseReturn(roomClasse_UseCase1_b);
        mockCardReturn(aCard_Room20_uc1_b, roomClasse_UseCase1_b,
                aCard_Floor30_uc1_b, aCard_Floor31_uc1_b,
                aCard_Building40, aCard_Building41,
                aCard_Complex50, aCard_Complex51);
        // Two relations for Room.20 in brach one:
        // - one inverse from Floor.30;
        // - one inverse from Floor.31;
        mockRelationReturn_Inverse_Multiple(aCard_Room20_uc1_b,
                list(
                        buildPair(aDomain_FloorRoom, aCard_Floor30_uc1_b), // Attribute direct from Floor.30 to Room.20 ==> inverse (as rebuild by UserDomainServiceImpl.getUserRelationsForCard()) Room.20 -> Floor.30
                        buildPair(aDomain_FloorRoom, aCard_Floor31_uc1_b) // Attribute direct from Floor.31 to Room.20 ==> inverse (as rebuild by UserDomainServiceImpl.getUserRelationsForCard()) Room.20 -> Floor.31
                ));
        // Relations for Floor Cards
        mockRelationReturn_Inverse(aDomain_BuildingFloor, aCard_Building40, aCard_Floor30_uc1_b);
        mockRelationReturn_Inverse(aDomain_BuildingFloor, aCard_Building41, aCard_Floor31_uc1_b);
        // Relations for Floor Buildings
        mockRelationReturn_Inverse(aDomain_ComplexBuilding, aCard_Complex50, aCard_Building40);
        mockRelationReturn_Inverse(aDomain_ComplexBuilding, aCard_Complex51, aCard_Building41);

        //act:
        List<Card> result = instance.findPathToRoot(domainTreeData, leafCardData);

        //assert:
        checkPath(expResult, result);
    }

    /**
     * Test of findPathToRoot method, leaf card not existing, of class
     * DomainPathFinderImpl.
     */
    @Test
    public void testFindPathToRoot_UseCase01_OnePath_ErrorNotExistingLeafCard() {
        System.out.println("findPathToRoot_UseCase01_OnePath_Found");

        //arrange:
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_ONE_PATH_JSON_FILE));
        DomainTreeData domainTreeData = fromJson(domainTreeDataJson, DomainTreeData.class);
        assertFalse(domainTreeData.nodes.isEmpty());
        CardData leafCardData = fromJson(A_NOT_EXISTING_LEAF_CARD_DATA, CardData.class);
        assertEquals(A_KNOWN_LEAF_CLASS_NAME, leafCardData.className);
        assertEquals(31L, toLong(leafCardData.id)); // Not existing id
        // First branch, normal classes
        mockClasseReturn(complexClasse);
        mockClasseReturn(buildingClasse);
        mockClasseReturn(floorClasse);
        mockClasseReturn(roomClasse);
        mockCardReturn(aCard_Room20, roomClasse,
                aCard_Building40, aCard_Floor30, aCard_Complex50);
        mockRelationReturn_Inverse(aDomain_FloorRoom, aCard_Floor30, aCard_Room20);
        mockRelationReturn_Inverse(aDomain_BuildingFloor, aCard_Building40, aCard_Floor30);
        mockRelationReturn_Inverse(aDomain_ComplexBuilding, aCard_Complex50, aCard_Building40);

        //act:
        exceptionRule.expect(DomainTreeException.class);
        exceptionRule.expectMessage("Domain tree =< %s > (id =< %s >), leaf classe =< %s > - not found card =< %s >, Classe =< %s > (description =< %s >)".formatted(domainTreeData.name, domainTreeData.id, leafCardData.className, leafCardData.id, leafCardData.className, leafCardData.description));
        instance.findPathToRoot(domainTreeData, leafCardData);

        //assert:
    }

    /**
     * Test of findPathToRoot method, <i>root</i> {@link Card} found through a
     * <i>superclasse</i>, of class DomainPathFinderImpl.
     */
    @Test
    public void testFindPathToRoot_UseCase02_OnePath_RootFound() {
        System.out.println("findPathToRoot_UseCase02_OnePath_RootFound");

        //arrange:
        List<CardMatch> expResult = list(
                new CardMatch("Complex", 50L),
                new CardMatch("Building", 40L),
                new CardMatch("Floor", 30L),
                new CardMatch("Room", 20L)
        );
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_ONE_PATH_SUPERCLASS_JSON_FILE));
        DomainTreeData domainTreeData = fromJson(domainTreeDataJson, DomainTreeData.class);
        assertFalse(domainTreeData.nodes.isEmpty());
        CardData leafCardData = fromJson(A_KNOWN_LEAF_CARD_DATA, CardData.class);
        assertEquals(A_KNOWN_LEAF_CLASS_NAME, leafCardData.className);
        assertEquals(20L, toLong(leafCardData.id));
        // First branch, normal classes
        mockClasseReturn(complexClasse);
        mockClasseReturn(buildingClasse);
        mockClasseReturn(floorClasse);
        mockClasseReturn(superRoomClasse_UseCase2);
        mockClasseReturn(roomClasse_UseCase2); // Has superclasse "SuperRoom"
        mockCardReturn(aCard_Room20_uc2, roomClasse_UseCase2,
                aCard_Floor30, aCard_Building40, aCard_Complex50);
        mockRelationReturn_Inverse(aDomain_FloorRoom_UseCase2, aCard_Floor30, aCard_Room20_uc2);
        mockRelationReturn_Inverse(aDomain_BuildingFloor, aCard_Building40, aCard_Floor30);
        mockRelationReturn_Inverse(aDomain_ComplexBuilding, aCard_Complex50, aCard_Building40);

        //act:
        List<Card> result = instance.findPathToRoot(domainTreeData, leafCardData);

        //assert:
        checkPath(expResult, result);
    }

    /**
     * Test of findPathToRoot method, <i>root</i> {@link Card} <b>not</b> found,
     * of class DomainPathFinderImpl.
     *
     */
    @Test
    public void testFindPathToRoot_UseCase03_OnePath_RootNotFound() {
        System.out.println("findPathToRoot_UseCase03_OnePath_RootNotFound");

        //arrange:
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_ONE_PATH_JSON_FILE));
        DomainTreeData domainTreeData = fromJson(domainTreeDataJson, DomainTreeData.class);
        assertFalse(domainTreeData.nodes.isEmpty());
        CardData leafCardData = fromJson(A_KNOWN_LEAF_CARD_DATA, CardData.class);
        assertEquals(A_KNOWN_LEAF_CLASS_NAME, leafCardData.className);
        assertEquals(20L, toLong(leafCardData.id)); // Not existing id
        // First branch, normal classes
        mockClasseReturn(complexClasse);
        mockClasseReturn(buildingClasse);
        mockClasseReturn(floorClasse);
        mockClasseReturn(roomClasse);
        mockCardReturn(aCard_Room20, roomClasse,
                aCard_Building40, aCard_Floor30, aCard_Complex50);
        mockRelationReturn_Inverse(aDomain_FloorRoom, aCard_Floor30, aCard_Room20);
        /*mockRelationReturn_Inverse(aDomain_BuildingFloor, aCard_Building40, aCard_Floor30);*/ // parent for Building.40 missing
        mockRelationReturn_Inverse(aDomain_ComplexBuilding, aCard_Complex50, aCard_Building40);

        //act:
        List<Card> result = instance.findPathToRoot(domainTreeData, leafCardData);

        //assert:
        assertTrue(result.isEmpty());
    }

    /**
     * Test of findPathToRoot method, <i>root</i> {@link Card} found through a
     * <i>superclasse</i> in a second <i>branch</i>, of class
     * DomainPathFinderImpl.
     *
     * <p>
     * In <b>second branch</b>, the <i>leaf</i> {@link Classe} is a
     * <i>superclass</i>
     * of original <i>leaf</i> {@link Classe} <code>Room</code>. The second
     * <i>path</i>
     * to investigate is cut to <code>SuperRoom</code> and the {@link Card} in
     * <code>Room</code> matches the searched path to <i>root</i>
     * <code>Complex</code>.
     */
    @Test
    public void testFindPathToRoot_UseCase04_TwoPaths_RootFound() {
        System.out.println("findPathToRoot_UseCase04_TwoPaths_RootFound");

        //arrange:
        List<CardMatch> expResult = list(
                new CardMatch("Complex", 51L),
                new CardMatch("AnotherRoomBranchClasse", 80L),
                new CardMatch("Room", 70L)
        );
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_TWO_PATHS_JSON_FILE));
        DomainTreeData domainTreeData = fromJson(domainTreeDataJson, DomainTreeData.class);
        assertFalse(domainTreeData.nodes.isEmpty());
        CardData leafCardData = fromJson(A_KNOWN_LEAF_PATH_TWO_CARD_DATA, CardData.class);
        assertEquals(A_KNOWN_LEAF_CLASS_NAME, leafCardData.className);
        assertEquals(70L, toLong(leafCardData.id));
        // First branch, normal classes
        mockClasseReturn(complexClasse);
        mockClasseReturn(buildingClasse);
        mockClasseReturn(floorClasse);
        mockClasseReturn(superRoomClasse);
        mockClasseReturn(midRoomClasse);
        mockClasseReturn(roomWithSuperclasse);
        mockCardReturn(aCard_Room20, roomWithSuperclasse,
                aCard_Floor30, aCard_Building40, aCard_Complex50);
        mockRelationReturn_Inverse(aDomain_FloorRoom, aCard_Floor30, aCard_Room20);
        mockRelationReturn_Inverse(aDomain_BuildingFloor, aCard_Building40, aCard_Floor30);
        mockRelationReturn_Inverse(aDomain_ComplexBuilding, aCard_Complex50, aCard_Building40);
        // Second branch,
        mockClasseReturn(anotherRoomBranchClasse);
        mockCardReturn(aCard_Room70_path2, roomWithSuperclasse,
                aCard_AnotherRoomBranchClasse80, aCard_Complex51);
        mockRelationReturn_Inverse(aDomain_SuperOtherRoom, aCard_AnotherRoomBranchClasse80, aCard_Room70_path2);
        mockRelationReturn_Inverse(aDomain_AnotherChild, aCard_Complex51, aCard_AnotherRoomBranchClasse80);

        //act:
        List<Card> result = instance.findPathToRoot(domainTreeData, leafCardData);

        //assert:
        checkPath(expResult, result);
    }

    /**
     * Test of findPathToRoot method, <i>root</i> {@link Card} <b>not</b> found
     * because a {@link Card} on path is filtered out, of class
     * DomainPathFinderImpl.
     */
    @Test
    public void testFindPathToRoot_UseCase05_OnePath_CardFilteredOut() {
        System.out.println("findPathToRoot_UseCase05_OnePath_CardFilteredOut");

        //arrange:
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_ONE_PATH_JSON_FILE));
        DomainTreeData domainTreeData = fromJson(domainTreeDataJson, DomainTreeData.class);
        assertFalse(domainTreeData.nodes.isEmpty());

        // Emulate filter for Bulding Classe, in Building node (will be moved by DomainTreeBuilder in the needed child node)
        DomainNodeData buildingNode = domainTreeData.nodes.get(2);
        assertEquals("Floor", buildingNode.targetClass);
        buildingNode.cqlFilter = A_KNOWN_CQL_FILTER_ON_FLOOR;

        CardData leafCardData = fromJson(A_KNOWN_LEAF_CARD_DATA, CardData.class);
        assertEquals(A_KNOWN_LEAF_CLASS_NAME, leafCardData.className);
        assertEquals(20L, toLong(leafCardData.id));

        // First branch, normal classes
        mockClasseReturn(complexClasse);
        mockClasseReturn(buildingClasse);
        mockClasseReturn(floorClasse);
        mockClasseReturn(roomClasse);
        mockCardReturn(aCard_Room20, roomClasse,
                aCard_Floor30,
                /*aCard_Building40,*/ //removed by filter on Building
                aCard_Complex50);
        mockRelationReturn_Inverse(aDomain_FloorRoom, aCard_Floor30, aCard_Room20);
        mockRelationReturn_Inverse(aDomain_BuildingFloor, aCard_Building40, aCard_Floor30);
        mockRelationReturn_Inverse(aDomain_ComplexBuilding, aCard_Complex50, aCard_Building40);

        //act:
        List<Card> result = instance.findPathToRoot(domainTreeData, leafCardData);

        //assert:
        assertTrue(result.isEmpty());
        verify(cardSync, times(1)).read(matchClasse_ByName(floorClasse.getName()), eq(30L), eq(A_KNOWN_CQL_FILTER_ON_FLOOR), eq(STANDARD_CLASS_INFO_ATTRIBUTES));
    }

    /**
     * Test of findPathToRoot method, first valid <i>path</i> chosen if two
     * valid
     * <i>branched</i> found, of class DomainPathFinderImpl.
     */
    @Test
    public void testFindPathToRoot_UseCase06_TwoPaths_FirstPathFound() {
        System.out.println("findPathToRoot_UseCase06_TwoPaths_FirstPathFound");

        //arrange:
        List<CardMatch> expResult = list(
                new CardMatch("Complex", 50L),
                new CardMatch("Building", 40L),
                new CardMatch("Floor", 30L),
                new CardMatch("Room", 20L)
        );
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_TWO_PATHS_JSON_FILE));
        DomainTreeData domainTreeData = fromJson(domainTreeDataJson, DomainTreeData.class);
        assertFalse(domainTreeData.nodes.isEmpty());
        CardData leafCardData = fromJson(A_KNOWN_LEAF_CARD_DATA, CardData.class);
        assertEquals(A_KNOWN_LEAF_CLASS_NAME, leafCardData.className);
        assertEquals(20L, toLong(leafCardData.id));
        // First branch, normal classes
        mockClasseReturn(complexClasse);
        mockClasseReturn(buildingClasse);
        mockClasseReturn(floorClasse);
        mockClasseReturn(superRoomClasse);
        mockClasseReturn(midRoomClasse);
        mockClasseReturn(roomWithSuperclasse);
        mockCardReturn(aCard_Room20, roomWithSuperclasse,
                aCard_Floor30,
                aCard_Building40,
                /*aCard_Building40,*/ //removed by filter on Building
                aCard_Complex50);
        mockRelationReturn_Inverse(aDomain_BuildingFloor, aCard_Building40, aCard_Floor30);
        mockRelationReturn_Inverse(aDomain_ComplexBuilding, aCard_Complex50, aCard_Building40);
        // Second branch,
        mockClasseReturn(anotherRoomBranchClasse);
        mockCardReturn(aCard_Room20, roomWithSuperclasse, // Same leaf of first branch
                aCard_AnotherRoomBranchClasse80, aCard_Complex51);
        mockRelationReturn_Inverse(aDomain_AnotherChild, aCard_Complex51, aCard_AnotherRoomBranchClasse80);

        // Two relations for Room.20:
        // - one in branch one with Floor.30;
        // - one in branch two with AnotherRoomBranchClasse.80
        mockRelationReturn_Inverse_Multiple(aCard_Room20,
                list(
                        buildPair(aDomain_FloorRoom, aCard_Floor30),
                        buildPair(aDomain_SuperOtherRoom, aCard_AnotherRoomBranchClasse80)
                ));

        //act:
        List<Card> result = instance.findPathToRoot(domainTreeData, leafCardData);

        //assert:
        checkPath(expResult, result);
    }

    /**
     * Test of findPathToRoot method, first <i>branch</i> excluded because
     * {@link Card} filtered out so <i>root</i> {@link Card} found through a
     * <i>superclasse</i> in a second <i>branch</i>, of class
     * DomainPathFinderImpl.
     *
     * <p>
     * Combination of <i>use cases</i> <code>4</code> and <code>5</code>.
     * <ol>
     * <li>in <b>first branch</b> a intermediate {@link Card} is <b>filtered
     * out</b>;
     * <li>the <i>root</i> is found in second branch.
     * </ol>
     *
     * <p>
     * a single <i>leaf</i> {@link Card} (on {@link Classe} <code>Room</code>)
     * is related to two different {@link Domain}s (toward {@link Classe}
     * <code>Floor</code> and {@link Classe}
     * <code>AnotherRoomBranchClasse</code>).
     *
     */
    @Test
    public void testFindPathToRoot_UseCase06_TwoPaths_FirstPathExcludedCardFilteredOut() {
        System.out.println("findPathToRoot_UseCase06_TwoPaths_FirstPathExcludedCardFilteredOut");

        //arrange:
        List<CardMatch> expResult = list(
                new CardMatch("Complex", 51L),
                new CardMatch("AnotherRoomBranchClasse", 80L),
                new CardMatch("Room", 20L)
        );
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_TWO_PATHS_JSON_FILE));
        DomainTreeData domainTreeData = fromJson(domainTreeDataJson, DomainTreeData.class);
        assertFalse(domainTreeData.nodes.isEmpty());
        CardData leafCardData = fromJson(A_KNOWN_LEAF_CARD_DATA, CardData.class);
        assertEquals(A_KNOWN_LEAF_CLASS_NAME, leafCardData.className);
        assertEquals(20L, toLong(leafCardData.id));
        // First branch, normal classes
        mockClasseReturn(complexClasse);
        mockClasseReturn(buildingClasse);
        mockClasseReturn(floorClasse);
        mockClasseReturn(superRoomClasse);
        mockClasseReturn(midRoomClasse);
        mockClasseReturn(roomWithSuperclasse);
        mockCardReturn(aCard_Room20, roomWithSuperclasse,
                aCard_Floor30,
                /*aCard_Building40,*/ //removed by filter on Building
                aCard_Complex50);
        mockRelationReturn_Inverse(aDomain_BuildingFloor, aCard_Building40, aCard_Floor30);
        mockRelationReturn_Inverse(aDomain_ComplexBuilding, aCard_Complex50, aCard_Building40);
        // Second branch,
        mockClasseReturn(anotherRoomBranchClasse);
        mockCardReturn(aCard_Room20, roomWithSuperclasse, // Same leaf of first branch
                aCard_AnotherRoomBranchClasse80, aCard_Complex51);
        mockRelationReturn_Inverse(aDomain_AnotherChild, aCard_Complex51, aCard_AnotherRoomBranchClasse80);

        // Two relations for Room.20:
        // - one in branch one with Floor.30;
        // - one in branch two with AnotherRoomBranchClasse.80
        mockRelationReturn_Inverse_Multiple(aCard_Room20,
                list(
                        buildPair(aDomain_FloorRoom, aCard_Floor30),
                        buildPair(aDomain_SuperOtherRoom, aCard_AnotherRoomBranchClasse80)
                ));

        //act:
        List<Card> result = instance.findPathToRoot(domainTreeData, leafCardData);

        //assert:
        checkPath(expResult, result);
    }

    /**
     * Test of findPathToRoot method, first valid <i>path</i> chosen if two
     * valid <i>branched</i> found, of class DomainPathFinderImpl.
     *
     * <p>
     * Two branches, a loop in <code>Floor.30</code> through a
     * <i>superclasse</i> <code>SuperFloor</code>:
     * <pre>
     * Room.20 -> Floor.30
     *              (A) -> Building.40 -> Middle.45 -> (SuperFloor)Floor.30 -> Complex.50
     *              (B) -> Complex.51
     * </pre>
     *
     */
    @Test
    public void testFindPathToRoot_UseCase07_TwoPaths_FirstHasCardLoop() {
        System.out.println("findPathToRoot_UseCase07_TwoPaths_FirstHasCardLoop");

        //arrange:
        List<CardMatch> expResult = list(
                new CardMatch("Complex", 51L),
                new CardMatch("Floor", 30L),
                new CardMatch("Room", 20L)
        );
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_TWO_PATHS_USE_CASE_7_JSON_FILE));
        DomainTreeData domainTreeData = fromJson(domainTreeDataJson, DomainTreeData.class);
        assertFalse(domainTreeData.nodes.isEmpty());
        CardData leafCardData = fromJson(A_KNOWN_LEAF_CARD_DATA, CardData.class);
        assertEquals(A_KNOWN_LEAF_CLASS_NAME, leafCardData.className);
        assertEquals(20L, toLong(leafCardData.id));
        // First branch, normal classes
        mockClasseReturn(complexClasse);
        mockClasseReturn(superFloorClasse_UseCase7);
        mockClasseReturn(middleClasse_UseCase7);
        mockClasseReturn(buildingClasse);
        mockClasseReturn(floorClasse);
        mockClasseReturn(superRoomClasse);
        mockClasseReturn(midRoomClasse);
        mockClasseReturn(roomWithSuperclasse);
        mockCardReturn(aCard_Room20, roomWithSuperclasse,
                aCard_Floor30_uc7,
                aCard_Building40_uc7,
                aCard_Middle45_uc7,
                aCard_Complex50);
        mockRelationReturn_Inverse(aDomain_FloorRoom, aCard_Floor30_uc7, aCard_Room20);
        mockRelationReturn_Inverse(aDomain_BuildingFloor, aCard_Building40_uc7, aCard_Floor30_uc7);
        mockRelationReturn_Inverse(aDomain_MiddleBuilding_UseCase7, aCard_Middle45_uc7, aCard_Building40_uc7);
        mockRelationReturn_Inverse(aDomain_SuperFloorMiddle_UseCase7, aCard_Floor30_uc7, aCard_Middle45_uc7);
        mockRelationReturn_Inverse(aDomain_ComplexSuperFloor_UseCase7, aCard_Complex51, aCard_Floor30_uc7);
        // Second branch,
        mockClasseReturn(anotherRoomBranchClasse);
        mockCardReturn(aCard_Room20, roomWithSuperclasse, // Same leaf of first branch
                aCard_Floor30_uc7, aCard_Complex51);
        // Two relations for Floor.30:
        // - one in branch one with Building.40;
        // - one in branch two with Complex.51
        mockRelationReturn_Inverse_Multiple(aCard_Floor30_uc7,
                list(
                        buildPair(aDomain_BuildingFloor, aCard_Building40_uc7),
                        buildPair(aDomain_ComplexSuperFloor_UseCase7, aCard_Complex51)
                ));

        //act:
        List<Card> result = instance.findPathToRoot(domainTreeData, leafCardData);

        //assert:
        checkPath(expResult, result);
    }

    /**
     * Test of findPathToRoot method, first <i>branch</i> excluded because one
     * of its {@link Domain}s is <code>N:N</code> and filtered out so
     * <i>root</i> {@link Card} found through a
     * <i>superclasse</i> in a second <i>branch</i>, of class
     * DomainPathFinderImpl.
     *
     * <p>
     * Two branches:
     * <ol>
     * <li>in <b>first branch</b> a intermediate {@link Domain} is skipped
     * because <code>N:N</code>;
     * <li>the <i>root</i> is found in second branch.
     * </ol>
     *
     * So, a single <i>leaf</i> {@link Card} (on {@link Classe}
     * <code>Room</code>) is related to two different {@link Domain}s (toward
     * {@link Classe} <code>Floor</code> and {@link Classe}
     * <code>AnotherRoomBranchClasse</code>).
     *
     */
    @Test
    public void testFindPathToRoot_UseCase08_TwoPaths_FirstPathExcludedDomainNN() {
        System.out.println("findPathToRoot_UseCase08_TwoPaths_FirstPathExcludedDomainNN");

        //arrange:
        List<CardMatch> expResult = list(
                new CardMatch("Complex", 51L),
                new CardMatch("AnotherRoomBranchClasse", 80L),
                new CardMatch("Room", 20L)
        );
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_TWO_PATHS_JSON_FILE));
        DomainTreeData domainTreeData = fromJson(domainTreeDataJson, DomainTreeData.class);
        assertFalse(domainTreeData.nodes.isEmpty());
        CardData leafCardData = fromJson(A_KNOWN_LEAF_CARD_DATA, CardData.class);
        assertEquals(A_KNOWN_LEAF_CLASS_NAME, leafCardData.className);
        assertEquals(20L, toLong(leafCardData.id));
        // First branch, normal classes
        mockClasseReturn(complexClasse);
        mockClasseReturn(buildingClasse);
        mockClasseReturn(floorClasse);
        mockClasseReturn(superRoomClasse);
        mockClasseReturn(midRoomClasse);
        mockClasseReturn(roomClasse_UseCase8);
        mockCardReturn(aCard_Room20_uc8, roomClasse_UseCase8,
                aCard_Floor30,
                aCard_Building40,
                aCard_Complex50);
        mockRelationReturn_Inverse(aDomain_BuildingFloor, aCard_Building40, aCard_Floor30);
        mockRelationReturn_Inverse(aDomain_ComplexBuilding, aCard_Complex50, aCard_Building40);
        // Second branch,
        mockClasseReturn(anotherRoomBranchClasse);
        mockCardReturn(aCard_Room20_uc8, roomClasse_UseCase8, // Same leaf of first branch
                aCard_AnotherRoomBranchClasse80, aCard_Complex51);
        mockRelationReturn_Inverse(aDomain_AnotherChild, aCard_Complex51, aCard_AnotherRoomBranchClasse80);

        // Two relations for Room.20:
        // - one in branch one with Floor.30;
        // - one in branch two with AnotherRoomBranchClasse.80
        mockRelationReturn_Inverse_Multiple(aCard_Room20_uc8,
                list(
                        buildPair(aDomain_FloorRoom_UseCase8, aCard_Floor30), // Domain N:N
                        buildPair(aDomain_SuperOtherRoom, aCard_AnotherRoomBranchClasse80)
                ));

        //act:
        List<Card> result = instance.findPathToRoot(domainTreeData, leafCardData);

        //assert:
        checkPath(expResult, result);
    }

    /**
     * Test of findPathToRoot method, first <i>branch</i> excluded because one
     * of its {@link Domain}s is <code>1:1</code> so
     * <i>root</i> {@link Card} found through a
     * <i>superclasse</i> in a second <i>branch</i>, of class
     * DomainPathFinderImpl.
     *
     * <p>
     * Two branches:
     * <ol>
     * <li>in <b>first branch</b> a intermediate {@link Domain} is skipped
     * because <code>1:1</code>;
     * <li>the <i>root</i> is found in second branch.
     * </ol>
     *
     * So, a single <i>leaf</i> {@link Card} (on {@link Classe}
     * <code>Room</code>) is related to two different {@link Domain}s (toward
     * {@link Classe} <code>Floor</code> and {@link Classe}
     * <code>AnotherRoomBranchClasse</code>).
     *
     */
    @Test
    public void testFindPathToRoot_UseCase09_TwoPaths_FirstPathExcludedDomain11() {
        System.out.println("findPathToRoot_UseCase09_TwoPaths_FirstPathExcludedDomain11");

        //arrange:
        List<CardMatch> expResult = list(
                new CardMatch("Complex", 51L),
                new CardMatch("AnotherRoomBranchClasse", 80L),
                new CardMatch("Room", 20L)
        );
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_TWO_PATHS_JSON_FILE));
        DomainTreeData domainTreeData = fromJson(domainTreeDataJson, DomainTreeData.class);
        assertFalse(domainTreeData.nodes.isEmpty());
        CardData leafCardData = fromJson(A_KNOWN_LEAF_CARD_DATA, CardData.class);
        assertEquals(A_KNOWN_LEAF_CLASS_NAME, leafCardData.className);
        assertEquals(20L, toLong(leafCardData.id));
        // First branch, normal classes
        mockClasseReturn(complexClasse);
        mockClasseReturn(buildingClasse);
        mockClasseReturn(floorClasse);
        mockClasseReturn(superRoomClasse);
        mockClasseReturn(midRoomClasse);
        mockClasseReturn(roomClasse_UseCase9);
        mockCardReturn(aCard_Room20_uc9, roomClasse_UseCase9,
                aCard_Floor30,
                aCard_Building40,
                aCard_Complex50);
        mockRelationReturn_Inverse(aDomain_BuildingFloor, aCard_Building40, aCard_Floor30);
        mockRelationReturn_Inverse(aDomain_ComplexBuilding, aCard_Complex50, aCard_Building40);
        // Second branch,
        mockClasseReturn(anotherRoomBranchClasse);
        mockCardReturn(aCard_Room20_uc9, roomClasse_UseCase9, // Same leaf of first branch
                aCard_AnotherRoomBranchClasse80, aCard_Complex51);
        mockRelationReturn_Inverse(aDomain_AnotherChild, aCard_Complex51, aCard_AnotherRoomBranchClasse80);

        // Two relations for Room.20:
        // - one in branch one with Floor.30;
        // - one in branch two with AnotherRoomBranchClasse.80
        mockRelationReturn_Inverse_Multiple(aCard_Room20_uc9,
                list(
                        buildPair(aDomain_FloorRoom_UseCase9, aCard_Floor30), // Domain 1:1
                        buildPair(aDomain_SuperOtherRoom, aCard_AnotherRoomBranchClasse80)
                ));

        //act:
        List<Card> result = instance.findPathToRoot(domainTreeData, leafCardData);

        //assert:
        checkPath(expResult, result);
    }

    /**
     * Test of findPathToRoot method, first <i>branch</i> excluded because one
     * of its {@link Domain}s is <code>N:1</code> with a <code>direct</code>
     * {@link Attribute} in <i>parent</i> {@link Classe} so <i>root</i>
     * {@link Card} found through a
     * <i>superclasse</i> in a second <i>branch</i>, of class
     * DomainPathFinderImpl.
     *
     * <p>
     * Two branches:
     * <ol>
     * <li>in <b>first branch</b> a intermediate {@link Domain} is skipped
     * because <code>N:1</code> (and related <code>REFERENCE</code>
     * {@link Attribute} <i>direct</i>);
     * <li>the <i>root</i> is found in second branch.
     * </ol>
     *
     * So, a single <i>leaf</i> {@link Card} (on {@link Classe}
     * <code>Room</code>) is related to two different {@link Domain}s (toward
     * {@link Classe} <code>Floor</code> <code>inverse</code> and {@link Classe}
     * <code>AnotherRoomBranchClasse</code>).
     *
     */
    @Test
    public void testFindPathToRoot_UseCase10_TwoPaths_FirstPathExcludedDomainN1AttributeDirect() {
        System.out.println("findPathToRoot_UseCase10_TwoPaths_FirstPathExcludedDomainN1AttributeDirect");

        //arrange:
        List<CardMatch> expResult = list(
                new CardMatch("Complex", 51L),
                new CardMatch("AnotherRoomBranchClasse", 80L),
                new CardMatch("Room", 20L)
        );
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_TWO_PATHS_JSON_FILE));
        DomainTreeData domainTreeData = fromJson(domainTreeDataJson, DomainTreeData.class);
        assertFalse(domainTreeData.nodes.isEmpty());
        CardData leafCardData = fromJson(A_KNOWN_LEAF_CARD_DATA, CardData.class);
        assertEquals(A_KNOWN_LEAF_CLASS_NAME, leafCardData.className);
        assertEquals(20L, toLong(leafCardData.id));
        // First branch, normal classes
        mockClasseReturn(complexClasse);
        mockClasseReturn(buildingClasse);
        mockClasseReturn(floorClasse_UseCase10);
        mockClasseReturn(superRoomClasse);
        mockClasseReturn(midRoomClasse);
        mockClasseReturn(roomClasse_UseCase10);
        mockCardReturn(aCard_Room20_uc10, roomClasse_UseCase10,
                aCard_Floor30_uc10,
                aCard_Building40,
                aCard_Complex50);
        mockRelationReturn_Inverse(aDomain_BuildingFloor, aCard_Building40, aCard_Floor30_uc10);
        mockRelationReturn_Inverse(aDomain_ComplexBuilding, aCard_Complex50, aCard_Building40);
        // Second branch,
        mockClasseReturn(anotherRoomBranchClasse);
        mockCardReturn(aCard_Room20_uc10, roomClasse_UseCase10, // Same leaf of first branch, Classe has REFERENCE attribute *inverse*
                aCard_AnotherRoomBranchClasse80, aCard_Complex51);
        mockRelationReturn_Inverse(aDomain_AnotherChild, aCard_Complex51, aCard_AnotherRoomBranchClasse80);

        // Two relations for Room.20:
        // - one in branch one from Floor.30 (FloorRoom is N:1);
        // - one in branch two with AnotherRoomBranchClasse.80
        mockRelationsReturn(aCard_Room20_uc10,
                list(
                        mockBuild_InverseRelation(aDomain_FloorRoom_UseCase10, aCard_Floor30_uc10, aCard_Room20_uc10), // Attribute direct from Floor.30 to Room.20 ==> inverse (as rebuild by UserDomainServiceImpl.getUserRelationsForCard()) Room.20 -> Floor.30
                        mockBuild_InverseRelation(aDomain_SuperOtherRoom, aCard_AnotherRoomBranchClasse80, aCard_Room20_uc10)
                ));

        //act:
        List<Card> result = instance.findPathToRoot(domainTreeData, leafCardData);

        //assert:
        checkPath(expResult, result);
    }

    /**
     * Test of findPathToRoot method, first <i>branch</i> excluded because one
     * of its {@link Domain}s is <code>1:N</code> with a <code>inverse</code>
     * {@link Attribute} in <i>parent</i> {@link Classe} so
     * <i>root</i> {@link Card} found through a
     * <i>superclasse</i> in a second <i>branch</i>, of class
     * DomainPathFinderImpl.
     *
     * <p>
     * Two branches:
     * <ol>
     * <li>in <b>first branch</b> a intermediate {@link Domain} is skipped
     * because <code>1:N</code> (and related <code>REFERENCE</code>
     * {@link Attribute} <i>inverse</i>);
     * <li>the <i>root</i> is found in second branch.
     * </ol>
     *
     * So, a single <i>leaf</i> {@link Card} (on {@link Classe}
     * <code>Room</code>) is related to two different {@link Domain}s (toward
     * {@link Classe} <code>Floor</code> and {@link Classe}
     * <code>AnotherRoomBranchClasse</code> both <code>direct</code>).
     *
     */
    @Test
    public void testFindPathToRoot_UseCase11_TwoPaths_FirstPathExcludedDomain1NAttributeInverse() {
        System.out.println("findPathToRoot_UseCase11_TwoPaths_FirstPathExcludedDomain1NAttributeInverse");

        //arrange:
        List<CardMatch> expResult = list(
                new CardMatch("Complex", 51L),
                new CardMatch("AnotherRoomBranchClasse", 80L),
                new CardMatch("Room", 20L)
        );
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_TWO_PATHS_JSON_FILE));
        DomainTreeData domainTreeData = fromJson(domainTreeDataJson, DomainTreeData.class);
        assertFalse(domainTreeData.nodes.isEmpty());
        CardData leafCardData = fromJson(A_KNOWN_LEAF_CARD_DATA, CardData.class);
        assertEquals(A_KNOWN_LEAF_CLASS_NAME, leafCardData.className);
        assertEquals(20L, toLong(leafCardData.id));
        // First branch, normal classes
        mockClasseReturn(complexClasse);
        mockClasseReturn(buildingClasse);
        mockClasseReturn(floorClasse_UseCase10);
        mockClasseReturn(superRoomClasse);
        mockClasseReturn(midRoomClasse);
        mockClasseReturn(roomClasse_UseCase11);
        mockCardReturn(aCard_Room20_uc11, roomClasse_UseCase10,
                aCard_Floor30,
                aCard_Building40,
                aCard_Complex50);
        mockRelationReturn_Inverse(aDomain_BuildingFloor, aCard_Building40, aCard_Floor30);
        mockRelationReturn_Inverse(aDomain_ComplexBuilding, aCard_Complex50, aCard_Building40);
        // Second branch,
        mockClasseReturn(anotherRoomBranchClasse);
        mockCardReturn(aCard_Room20_uc11, roomClasse_UseCase11, // Same leaf of first branch, Classe has REFERENCE attribute *direct*
                aCard_AnotherRoomBranchClasse80, aCard_Complex51);
        mockRelationReturn_Inverse(aDomain_AnotherChild, aCard_Complex51, aCard_AnotherRoomBranchClasse80);

        // Two relations for Room.20:
        // - one in branch one from Floor.30 (FloorRoom is 1:N);
        // - one in branch two with AnotherRoomBranchClasse.80
        mockRelationsReturn(aCard_Room20_uc11,
                list(
                        mockBuild_DirectRelation(aDomain_FloorRoom_UseCase11, aCard_Floor30, aCard_Room20_uc11), // Attribute inverse from Room.20 to Floor.30 ==> direct (as rebuild by UserDomainServiceImpl.getUserRelationsForCard()) Room.20 -> Floor.30
                        mockBuild_InverseRelation(aDomain_SuperOtherRoom, aCard_AnotherRoomBranchClasse80, aCard_Room20_uc10)
                ));

        //act:
        List<Card> result = instance.findPathToRoot(domainTreeData, leafCardData);

        //assert:
        checkPath(expResult, result);
    }

    static final String A_KNOWN_CR1_LEAF_CARD_DATA = """
                                    {
                                        "_id": 30,
                                        "_type": "Floor",
                                        "Code": "F01",
                                        "Description": "Floor 01 [Tecnoteca headquarters]"
                                    }
                                    """;

    static final String A_KNOWN_LEAF_FILTER = "from Floor where Id in (/( SELECT \"Id\" FROM \"Floor\" WHERE \"Code\" != '01_RO')/)";

    /**
     * Test of findPathToRoot method, <i>filter</i> checked even on <i>leaf</i>
     * {@link Card} found, of class DomainPathFinderImpl.
     *
     * <p>
     * f.e. if <i>node</i> for {@link Classe} <code>Floor</code> has a <i>CQL
     * filter</i>
     * that excludes the <i>leaf</i> {@link Card}. Change Request (#1): an empty
     * list have to be returned.
     */
    @Test
    public void testFindPathToRoot_ChangeRequest01_LeafFilter() {
        System.out.println("findPathToRoot_ChangeRequest01_LeafFilter");

        //arrange:
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_CR1_LEAF_FILTER_JSON_FILE));
        DomainTreeData domainTreeData = fromJson(domainTreeDataJson, DomainTreeData.class);
        assertFalse(domainTreeData.nodes.isEmpty());
        CardData leafCardData = fromJson(A_KNOWN_CR1_LEAF_CARD_DATA, CardData.class);
        assertEquals(floorClasse.getName(), leafCardData.className);
        assertEquals(30L, toLong(leafCardData.id));
        mockClasseReturn(roomClasse);
        mockClasseReturn(buildingClasse);
        mockClasseReturn(floorClasse);
        mockCardReturn(aCard_Floor30, floorClasse,
                aCard_Building40);
        mockRelationReturn_Inverse(aDomain_BuildingFloor, aCard_Building40, aCard_Floor30);

        //act:
        List<Card> result = instance.findPathToRoot(domainTreeData, leafCardData);

        //assert:
        // Read leaf first
        verify(cardSync, times(1)).read(matchClasse(floorClasse), eq(aCard_Floor30.getId()));
        // Check filter for current subpath
        verify(cardSync, times(1)).read(matchClasse(floorClasse), eq(aCard_Floor30.getId()), eq(A_KNOWN_LEAF_FILTER), eq(STANDARD_CLASS_INFO_ATTRIBUTES));

        assertTrue(result.isEmpty());
    }

    /**
     * Builds a <i>domain tree</i> with
     * <ol>
     * <li><code>maxNumOfClasses</code> levels, so {@link Classe}s in a
     * <i>path</i>;
     * <li><code>maxNumOfCards</code> {@link Card}s for each level (so, for each
     * {@link Classe});
     * <li>to augment the number of relations in {@link Card}s, creates for each
     * level <code>maxNumOfCards</code> {@link Domain}s from this level
     * {@link Classe} to the preceding level; for each {@link Domain}, adds * a
     * <code>REFERENCE</code> {@link Attribute} to each {@link Card} in current
     * level.
     * <li>so, each {@link Card} in level <code>L</code> (<code>L</code>
     * different from <code>0</code>, that means the <i>root</i> level of the
     * <i>tree</i>) is in relation to all {@link Card}s in the preceding level.
     * <li>a {@link Card}s in a bottom level is related <b>in first</b>
     * <code>REFERENCE</code> {@link Attribute} (that represents <b>first</b>
     * {@link Domain} in <i>domain tree</i>) to the <i>scaled up</i>
     * {@link Card} in <i>parent</i> level, to move things a little (otherwise
     * <code>Root.0</code> will always be returned as resulting
     * <code>root</code>.
     * </ol>
     *
     * <p>
     * For example: {@link Card} <code>"Card_L1>L0-N0"</code> is a {@link Card}
     * in level <code>1</code> (<code>L1</code>), with <code>curDelta 0</code>
     * (<code>N0</code>, the first in the row) and <code>id = 4</code>. It's
     * parents are in level  <code>0</code> (<code>%gtL0</code>, the <i>root
     * level of the tree</i>) and has <code>REFERENCE</code> {@link Attribute}s
     * (e.g. with <code>maxNumOfCards = 4</code>:
     *
     * <pre>
     * referenceAttr_Domain_L1>L0_N0 => IdAndDescription{id=1, description=Descr for Card_Root-N1}
     * referenceAttr_Domain_L1>L0_N1 => IdAndDescription{id=2, description=Descr for Card_Root-N2}
     * referenceAttr_Domain_L1>L0_N2 => IdAndDescription{id=3, description=Descr for Card_Root-N3}
     * referenceAttr_Domain_L1>L0_N3 => IdAndDescription{id=0, description=Descr for Card_Root-N0}
     * </pre>
     *
     */
    @Test
    public void testFindPathToRoot_UseCase12_ComplexCase() {
        System.out.println("findPathToRoot_UseCase12_ComplexCase");

        //arrange:
        int maxNumOfClasses = 3;
        int maxNumOfCards = 4;

//        // eseguire il test con ex. 10 Classi e 50.000 Card (o 50 Classi per 10.000 Card); tempo build 3'
//        int maxNumOfClasses = 10;
//        int maxNumOfCards = 50000;
        UseCase12Builder builder = new UseCase12Builder(maxNumOfClasses, maxNumOfCards, true);
        Pair<DomainTreeData, CardData> inputs = builder.generateTestFixture();

        // Mocking stub return from SUT invoked services
//            // First branch, normal classes
//            mockClasseReturn(complexClasse);
//            mockClasseReturn(buildingClasse);
//            mockClasseReturn(floorClasse);
//            mockClasseReturn(roomClasse);
//            mockCardReturn(aCard_Room20, roomClasse,
//                    aCard_Floor30, aCard_Building40, aCard_Complex50);
//            mockRelationReturn_Direct(aDomain_FloorRoom, aCard_Floor30, aCard_Room20);
//            mockRelationReturn_Direct(aDomain_BuildingFloor, aCard_Building40, aCard_Floor30);
//            mockRelationReturn_Direct(aDomain_ComplexBuilding, aCard_Complex50, aCard_Building40);
        System.out.println("TEST: mocking return of [%s] Classes...".formatted(maxNumOfClasses));
        for (int curLevel = 0; curLevel < builder.classes.size(); curLevel++) {
            Classe curClasse = builder.classes.get(curLevel);
            mockClasseReturn(curClasse, builder.logDetail(curLevel));
        }
//        System.out.println("TEST: mocking return of [%s] Cards...".formatted(buildIntLog(maxNumOfCards * maxNumOfClasses)));
//        List<Card> allOtherCards = list(builder.cards)
//                .subList(0, builder.cards.size() - 1) // Remove last level of Cards
//                .stream().flatMap(List::stream)
//                .toList();
        List<Card> allOtherCards = builder.getOnlyOtherDiagonalCards();
        System.out.println("TEST: [test optimization]mocking return of [%s] Cards (diagonal Cards [%s])...".formatted(buildIntLog(maxNumOfCards * maxNumOfClasses), buildIntLog(allOtherCards.size() + 1)));
        mockCardReturn(builder.leafCard, builder.leafClasse,
                allOtherCards.toArray(Card[]::new)
        );

        System.out.println("TEST: mocking return of [%s] CMRelations...".formatted(buildIntLog(maxNumOfCards * maxNumOfClasses)));
        for (int curLevel = 1; curLevel < builder.domains.size(); curLevel++) { // skip Root Cards
            List<Domain> curLevelDomains = builder.domains.get(curLevel);

            final int theCurLevel = curLevel; // for lambda awful sake

            IntStream.range(0, builder.maxNumOfCards)
                    .forEach(curCardPos -> {
                        Card curLevelCard = builder.cards.get(theCurLevel).get(curCardPos);
                        // Each Card in currrent level is related with the scaled-up Card in parent level through a Domain
                        List<Pair<Domain, Card>> relatedCards = list();

                // Optimization: skip all not relevant relation mocking
                if (!builder.isDiagonalId(curLevelCard, theCurLevel)) {
                    if (builder.logDetail(theCurLevel, curCardPos)) {
                        System.out.println("TEST: [test optimization]skipping mocking relations for %s".formatted(buildLog_Short(curLevelCard)));
                    }
                    return;
                }
                for (int curDelta = 0; curDelta < builder.maxNumOfCards; curDelta++) {
                    Domain curDomain = curLevelDomains.get(curDelta);
                    int scaledParentCardPos = builder.scaleUp(curDelta + curCardPos, builder.maxNumOfCards);
                    Card relatedParentCard = builder.cards.get(theCurLevel - 1).get(scaledParentCardPos);

                            if (builder.logDetail(theCurLevel, curCardPos + curDelta)) {
                                System.out.println("TEST: mocking relation from %s to parent %s (Domain =< %s >)".formatted(buildLog_Short(curLevelCard), buildLog_Short(relatedParentCard), curDomain.getName()));
                            }

                    relatedCards.add(Pair.of(curDomain, relatedParentCard));
                } // end for

                mockRelationReturn_Inverse_Multiple(curLevelCard, relatedCards);
                    });
        }

        //act:
        System.out.println("TEST: ----< Starting SUT computation >----");
        long start = System.nanoTime();
        List<Card> result = instance.findPathToRoot(inputs.getLeft(), inputs.getRight());
        long elapsedNs = System.nanoTime() - start;
        System.out.println("Elapsed: %.0f ms".formatted(elapsedNs / 1_000_000.0));

        //assert:
        assertEquals(maxNumOfClasses, result.size());
        assertEquals("Root", result.get(0).getClassName());
        // in 3 Classes (levels) and 4 Cards per level, the generated Card Id will be (in square brackets the diagonal Cards)
        // Root:    0    1    2   [3]
        // Classe1: 4    5   [6]   7
        // Classe2: 8   [9]  10   11
        //
        // so, starting  from Classe2.9 the path is Class1.6 (one scale up) and than Root.3 (another scale up)
        assertEquals(maxNumOfCards - 1, (long) result.get(0).getId());
    }

    private void checkPath(List<CardMatch> expCards, List<Card> actualCards) {
        final String errMsg = "expected: [%d] %s%s  actual: [%d] %s%s".formatted(
                expCards.size(), buildLog_Matchers(expCards),
                System.lineSeparator(),
                actualCards.size(), buildLog(actualCards),
                System.lineSeparator()
        );

        assertEquals(errMsg,
                expCards.size(), actualCards.size());

        for (int curPos = 0; curPos < expCards.size(); curPos++) {
            CardMatch expCard = expCards.get(curPos);
            Card actualCard = actualCards.get(curPos);

            assertEquals(errMsg, expCard.classeName, actualCard.getClassName());
            assertEquals(errMsg, expCard.id, (long) actualCard.getId());
        }
    }

    private String buildLog_Matchers(List<CardMatch> expCards) {
        return Joiner.on(", ").join(expCards.stream().map(c -> "%s.%s".formatted(c.classeName, c.id)).collect(toList()));
    }

    private String buildLog(List<Card> expCards) {
        return Joiner.on(", ").join(expCards.stream().map(c -> "%s.%s".formatted(c.getClassName(), c.getId())).collect(toList()));
    }

    static NumberFormat numFormat = NumberFormat.getIntegerInstance(Locale.ITALY);

    /**
     *
     * @param value
     * @return representation with thousands separator
     */
    private String buildIntLog(int value) {
        return numFormat.format(value);
    }

    /**
     * Checks {@link Classe} equality on:
     * <ol>
     * <li>{@link Classe} name;
     * <li>parent name.
     * </ol>
     *
     * @param expClasse
     * @return
     */
    protected Classe matchClasse(Classe expClasse) {
        return argThat(new ClasseMatcher(expClasse));
    }

    /**
     * Checks {@link Classe} equality on {@link Classe} name.
     *
     * @param expClasseName
     * @return
     */
    protected Classe matchClasse_ByName(String expClasseName) {
        return argThat(new ClasseByNameMatcher(expClasseName));
    }

    /**
     * Checks {@link Card} equality on:
     * <ol>
     * <li>{@link Classe} name;
     * <li><code>id</code>.
     * </ol>
     * ClasseMatcher(e
     *
     * @param expCard
     * @return
     */
    protected Card matchCard(Card expCard) {
        return argThat(new CardMatcher(expCard));
    }

    private void mockClasseReturn(Classe classe) {
        mockClasseReturn(classe, true);
    }

    private void mockClasseReturn(Classe classe, boolean detailedLogging) {
        final String classeName = classe.getName();
        if (detailedLogging) {
            System.out.println("TEST: mocking Classe on ClasseSync %s".formatted(classeName));
        }
        when(classeSync.read(eq(classeName), Matchers.anyBoolean())).thenReturn(classe); // A used in DomainTreeBuilder to fetch superclasses
        when(classeSync.read(eq(classeName))).thenReturn(classe); // As used in DmainPathFinderImpl to fetch single Classe
    }

    private void mockCardReturn(Card leafCard, Classe leafCardClasse, Card... otherCardArr) {
        mockCardLeafReturn(leafCard, leafCardClasse);

        // Other cards are read only with (id, type) code and description
        list(otherCardArr).forEach(c -> {
            when(cardSync.read(matchClasse_ByName(c.getClassName()), eq(c.getId()), anyString(), eq(STANDARD_CLASS_INFO_ATTRIBUTES)))
                    .thenReturn(c);
        });
    }

    private void mockCardLeafReturn(Card leafCard, Classe leafCardClasse) {
        mockCardLeafReturn(leafCard, leafCardClasse, false);
    }

    private void mockCardLeafReturn(Card leafCard, Classe leafCardClasse, boolean bFilteredOut) {
        // Leaf card is read full at start...
        when(cardSync.read(matchClasse(leafCardClasse), eq(leafCard.getId()))).thenReturn(leafCard);

        // ...and then (possibly) checked the CQL filter on it
        if (!bFilteredOut) {
            when(cardSync.read(matchClasse_ByName(leafCard.getClassName()), eq(leafCard.getId()), eq(""), eq(STANDARD_CLASS_INFO_ATTRIBUTES)))
                    .thenReturn(leafCard);
        }
    }

    private void mockRelationReturn_Direct(Domain aDomain, Card aCard, Card aRelatedCard) {
        when(domainSync.readRelationsForCard(matchCard(aCard)))
                .thenReturn(list(mockBuild_DirectRelation(aDomain,
                        upcast(aRelatedCard, aDomain.getSourceClass()),
                        upcast(aCard, aDomain.getTargetClass()))
                ));
    }

    private void mockRelationReturn_Direct_Multiple(Card aCard, List<Pair<Domain, Card>> relatedCards) {
        when(domainSync.readRelationsForCard(matchCard(aCard)))
                .thenReturn(relatedCards.stream().map(p -> mockBuild_DirectRelation(p.getKey(),
                upcast(p.getValue(), p.getKey().getSourceClass()),
                upcast(aCard, p.getKey().getTargetClass()))).collect(toList())
                );
    }

    private void mockRelationReturn_Inverse_Multiple(Card aCard, List<Pair<Domain, Card>> inverseRelatedCards) {
        when(domainSync.readRelationsForCard(matchCard(aCard)))
                .thenReturn(inverseRelatedCards.stream()
                        .map(p -> mockBuild_InverseRelation(p.getKey(),
                        p.getValue(),
                        aCard
                )).collect(toList())
                );
    }

    private Pair<Domain, Card> buildPair(Domain aDomain, Card aCard) {
        return Pair.of(aDomain, aCard);
    }

    private void mockRelationReturn_Inverse(Domain aDomain, Card aInverseRelatedCard, Card aCard) {
        when(domainSync.readRelationsForCard(matchCard(aCard)))
                .thenReturn(list(mockBuild_InverseRelation(aDomain,
                        aInverseRelatedCard,
                        aCard)
                ));
    }

    private void mockRelationsReturn(Card aCard, List<CMRelation> relations) {
        when(domainSync.readRelationsForCard(matchCard(aCard)))
                .thenReturn(relations);
    }

    private static CMRelation mockBuild_DirectRelation(Domain aDomain, Card sourceCard, Card targetCard) {
        return RelationImpl.build(aDomain,
                upcast(sourceCard, aDomain.getSourceClass()),
                upcast(targetCard, aDomain.getTargetClass())
        );
    }

    private static CMRelation mockBuild_InverseRelation(Domain aDomain, Card sourceCard, Card targetCard) {
        return RelationImpl.builder().withType(aDomain).withDirection(RelationDirection.RD_INVERSE)
                .withSourceCard(upcast(targetCard, aDomain.getTargetClass()))
                .withTargetCard(upcast(sourceCard, aDomain.getSourceClass()))
                .build();
    }

    /**
     * Handle Domain defined with a superClasse, as done in
     * {@link UserDomainServiceImpl#getUserRelationsForCard()}, getting the
     * relevant
     * <i>source</i> and <i>target</i> {@link Classe} from the once declared in
     * {@link Domain} to build the {@link CMRelation}s.
     *
     * @param aCard
     * @param aSuperclasse
     * @return
     */
    private static Card upcast(Card aCard, Classe aSuperclasse) {
        if (aCard.getClassName().equals(aSuperclasse.getName())) {
            return aCard;
        }

        // Upcast *without* caching
        System.out.println("TEST: upcasting card %s from =< %s > to =< %s >".formatted(LoggerHelper.buildLog_Short(aCard), aCard.getClassName(), aSuperclasse.getName()));
        return CardImpl.builder()
                .withType(aSuperclasse)
                .withAttributes(aCard.getAllValuesAsMap())
                .withId(aCard.getId())
                .build();
    }

    class UseCase12Builder {

        boolean bDetailedLogging;
        int logDetailGrain = 50;

        /**
         * Doesn't create <code>REFERENCE</code> {@link AttributeWithoutOwner},
         * because not used by SUT
         */
        boolean bExtremeLightCards;

        final int maxNumOfClasses; // Levels L
        final int maxNumOfCards; // Cards per level N

        final List<Classe> classes = list();
        final List<List<Domain>> domains = list();
        final List<List<Card>> cards = list();
        Card leafCard;
        Classe leafClasse;

        UseCase12Builder(int maxNumOfClasses, int maxNumOfCards) {
            this(maxNumOfClasses, maxNumOfCards, false);
        }

        /**
         *
         * @param maxNumOfClasses
         * @param maxNumOfCards
         * @param bExtremeLightCards if <code>true</code> doesn't create
         * <code>REFERENCE</code> {@link AttributeWithoutOwner}, because not
         * used by SUT.
         */
        UseCase12Builder(int maxNumOfClasses, int maxNumOfCards, boolean bExtremeLightCards) {
            if (maxNumOfCards < maxNumOfClasses) {
                System.err.println("TEST: expected test bed UseCase12 with numOfCards at least as numOfClasses [%d] - given [%d]".formatted(maxNumOfClasses, maxNumOfCards));
            }

            System.out.println("TEST: Building test bed UseCase12 for [%d] levels (Classes) and [%s] Cards per level".formatted(maxNumOfClasses, buildIntLog(maxNumOfCards)));
            this.maxNumOfClasses = maxNumOfClasses;
            this.maxNumOfCards = maxNumOfCards;

            this.bExtremeLightCards = bExtremeLightCards;

            this.bDetailedLogging = maxNumOfClasses < 4 && maxNumOfCards < 5;

            int numOfData = maxNumOfClasses * maxNumOfCards;
            if (!bDetailedLogging && numOfData > 100) {
                this.logDetailGrain = numOfData / 10;
                System.out.println("TEST: Logging every [%s] item".formatted(buildIntLog(logDetailGrain)));
            }
        }

        Pair<DomainTreeData, CardData> generateTestFixture() {
            System.out.println("TEST: building stubs for [%d] Classes (and [%s] Domains, [%s] REFERENCE Attributes)...".formatted(maxNumOfClasses, buildIntLog(maxNumOfCards * maxNumOfClasses), buildIntLog(maxNumOfCards * maxNumOfClasses)));
            IntStream.range(0, maxNumOfClasses)
                    .forEach(curLevel -> addClasseAndDomains(curLevel));

            System.out.println("TEST: building stubs for [%s] Cards per level [total: %s cards]...".formatted(buildIntLog(maxNumOfCards), buildIntLog(maxNumOfClasses * maxNumOfCards)));
            IntStream.range(0, maxNumOfClasses)
                    .forEach(curLevel -> buildCards(curLevel));

            // Build "input" DomainTreeData
            System.out.println("TEST: building input DomainTreeData...");
            DomainTreeData domainTreeData = new DomainTreeData();
            domainTreeData.id = "UseCase12Id";
            domainTreeData.description = "tree for UseCase12";

            domainTreeData.name = "UseCase12";
            domainTreeData.nodes = IntStream.range(0, maxNumOfClasses)
                    .mapToObj(curLevel -> buildDomainNodeData(curLevel))
                    .flatMap(List::stream)
                    .toList();

            if (maxNumOfClasses < 5) {
                System.out.println("TEST: Input domain tree:%s%s".formatted(System.lineSeparator(), toPrettyJson(domainTreeData)));
            }

            // Build "input" CardData: last level, second Card
            leafCard = getLastDiagonalCard();
            assertTrue(isDiagonalId(leafCard, maxNumOfClasses - 1));
            CardData cardData = new CardData();
            cardData.className = leafCard.getClassName();
            cardData.id = leafCard.getId();

            leafClasse = classes.get(classes.size() - 1);
            assertEquals(cardData.className, leafClasse.getName());

            return Pair.of(domainTreeData, cardData);
        }

        boolean isDiagonalId(Card curLevelCard, int curLevel) {
            return isDiagonalId(curLevelCard.getId(), curLevel);
        }

        boolean isDiagonalId(long curCardId, int curLevel) {
            return curCardId == (maxNumOfCards - 1) * (curLevel + 1);
        }

        Card getLastDiagonalCard() {
            return cards.get(maxNumOfClasses - 1).get(maxNumOfCards - 1 - (maxNumOfClasses - 1));
        }

        /**
         * For <code>maxNum</code> (position from <code>0</code> to
         * <code>maxPos</code> (that is <code>maxNum - 1</code>):
         * <pre>
         *  0 -> 1
         *  1 -> 2
         *  2 -> 3
         *  3 -> 4
         *  ...
         *  maxPos -> 0
         * </pre>
         *
         * @param i
         * @param maxNum
         * @return
         */
        int scaleUp(int i, int maxNum) {
//            if (i > maxNum) {
//                System.err.println("TEST: Unsupported i =< %d > with maxNum =< %d >".formatted(i, maxNum));
//                return -1;
//            }

            return (i + 1) % (maxNum);
        }

        /**
         * <b>Note</b>: build from root (<code>maxNumOfClasses - 1</code>) to 0,
         * to create:
         * <ol>
         * <li><i>direct</i> <code>REFERENCE</code> {@link Attribute} in
         * <i>child</i> {@link Classe};
         * <li>{@link Domain}
         * <li>
         * </ol>
         *
         * @param curLevel
         * @return
         */
        private void addClasseAndDomains(int curLevel) {
            Classe aClasse;

            if (isRootLevel(curLevel)) {
                // the root
                System.out.println("TEST: building Classe =< %s >".formatted("Root"));
                aClasse = mockBuildClasse("Root", list(knownModelBaseClasse.getName()),
                        asList(attribWOId, attribWOType, attribWOCode, attribWODescription));
                domains.add(emptyList());
            } else {
                // A child Class
                String curClasseLevelName = "Classe%d".formatted(curLevel);
                if (logDetail(curLevel)) {
                    System.out.println("TEST: building Classe =< %s >".formatted(curClasseLevelName));
                }

                // Build N references to parent Cards for each child Card
                // AttributeWithoutOwner curattribRelationToCards_SuperRoom_Floor_UseCase2 = mockBuildAttributeReference("referenceAttr_SuperRoomN_Floor1", "FloorRoom", RelationDirection.RD_DIRECT);
                List<AttributeWithoutOwner> refsToParent = buildRefsToParent(curLevel);
                // superRoomClasse_UseCase2 = mockBuildClasse("SuperRoom", list(knownModelBaseClasse.getName()),
                //        asList(attribWOId, attribWOType, attribWOCode, attribWODescription, attribRelationToCards_SuperRoom_Floor_UseCase2));
                aClasse = buildClasse(curClasseLevelName, refsToParent);
                // aDomain_FloorRoom_UseCase2 = mockBuildDomain_1N("FloorRoom", 9L, floorClasse, superRoomClasse_UseCase2);
                Classe parentClasse = classes.get(curLevel - 1);
                List<Domain> refDomains = buildRefDomains(curLevel, parentClasse, aClasse);
                domains.add(refDomains);
            }

            classes.add(aClasse);
        }

        private boolean logDetail(int curLevel) {
            return bDetailedLogging || curLevel % logDetailGrain == 0;
        }

        private boolean logDetail(int curLevel, int curPos) {
            return bDetailedLogging || (((curLevel * maxNumOfCards) + curPos) % logDetailGrain == 0);
        }

        private List<AttributeWithoutOwner> buildRefsToParent(int curLevel) {
            if (logDetail(curLevel)) {
                System.out.println("TEST: building [%s] REFERENCE Attributes for level %d...".formatted(buildIntLog(maxNumOfCards), curLevel));
            }
            return IntStream.range(0, maxNumOfCards)
                    .mapToObj(curPos -> buildAttributeReference(curLevel, curPos))
                    .toList();
        }

        private Classe buildClasse(String classeName, List<AttributeWithoutOwner> refsToParent) {
            return mockBuildClasse(classeName, list(knownModelBaseClasse.getName()),
                    list(attribWOId, attribWOType, attribWOCode, attribWODescription).with(refsToParent)
            );
        }

        private List<Domain> buildRefDomains(int curLevel, Classe parentClasse, Classe childClasse) {
            if (logDetail(curLevel)) {
                System.out.println("TEST: building [%s] Domains for level %d...".formatted(buildIntLog(maxNumOfCards), curLevel));
            }
            return IntStream.range(0, maxNumOfCards)
                    .mapToObj(curPos -> buildRefDomain(curLevel, curPos, parentClasse, childClasse))
                    .toList();
        }

        private Domain buildRefDomain(int curLevel, int curPos, Classe parentClasse, Classe childClasse) {
            String domainName = buildDomainName(curLevel, curPos);
            if (logDetail(curLevel, curPos)) {
                System.out.println("TEST: building Domain =< %s >".formatted(domainName));
            }

            return mockBuildDomain_1N(domainName, id(), parentClasse, childClasse);
        }

        private AttributeWithoutOwner buildAttributeReference(int curLevel, int curPos) {
            String referenceAttrName = buildReferenceAttrName(curLevel, curPos);
            String domainName = buildDomainName(curLevel, curPos);
            if (logDetail(curLevel, curPos)) {
                System.out.println("TEST: building referenceAttr =< %s > [Domain =< %s >]".formatted(referenceAttrName, domainName));
            }

            return mockBuildAttributeReference(referenceAttrName, domainName, RelationDirection.RD_DIRECT);
        }

        private String buildReferenceAttrName(int curLevel, int curPos) {
            return "referenceAttr_%s".formatted(buildDomainName(curLevel, curPos));
        }

        private String buildDomainName(int curLevel, int curPos) {
            return "Domain_L%d>L%d_N%d".formatted(curLevel, curLevel - 1, curPos);
        }

        private void buildCards(int curLevel) {
            List<Card> curLevelCards = list();

            Classe curLevelClasse = classes.get(curLevel);
            if (logDetail(curLevel)) {
                System.out.println("TEST: building [%s] Cards for Classe =< %s > (level [%d])...".formatted(buildIntLog(maxNumOfCards), curLevelClasse.getName(), curLevel));
            }

            IntStream.range(0, maxNumOfCards)
                    .forEach(curCardPos -> {
                        curLevelCards.add(buildCard(curLevelClasse, curLevel, curCardPos));
                    });

            cards.add(curLevelCards);
        }

        private Card buildCard(Classe classe, int curLevel, int curCardPos) {
            String cardName = buildCardName(curLevel, curCardPos);
            if (logDetail(curLevel, curCardPos)) {
                System.out.println("TEST: building Card =< %s >".formatted(cardName));
            }
            long curCardId = buildCardId(curLevel, curCardPos);

//            aCard_Room20 = mockBuildCard(20L, roomClasse,
//                map(
//                        attribWOCode.getName(), "roomCard_20",
//                        attribWODescription.getName(), "room card 20",
//                        attribRelationToCards_Room_Floor.getName(), new IdAndDescriptionImpl(30L, "floor card 30")
//                ));
            Map<String, Object> attrs = map(
                    attribWOCode.getName(), cardName,
                    attribWODescription.getName(), buildCardDscr(curLevel, curCardPos)
            );

            // attribRelationToCards_Room_Floor.getName(), new IdAndDescriptionImpl(30L, "floor card 30")
            if (curLevel > 0
                    && !bExtremeLightCards // Optimization: skip adding REFERENCE values for all cards
                    && isDiagonalId(curCardId, curLevel)) { // Optimization: skip adding REFERENCE values for not diagonal Cards
                // Not the root level: build N REFERENCE Attributes pointing to a level-1 Card
                IntStream.range(0, maxNumOfCards)
                        .forEach(curDelta -> {
                            String curRefAttrName = buildReferenceAttrName(curLevel, curDelta);
                            final int scaledParentCardPos = scaleUp(curDelta + curCardPos, maxNumOfCards);
                            long parentLevelCardId = buildCardId(curLevel - 1, scaledParentCardPos);
                            String parentLevelCardDescr = buildCardDscr(curLevel - 1, scaledParentCardPos);
                            attrs.put(curRefAttrName, new IdAndDescriptionImpl(parentLevelCardId, parentLevelCardDescr));
                        });
            }

            return mockBuildCard(curCardId, classe, attrs);
        }

        private String buildCardDscr(int curLevel, int curCardPos) {
            return "Descr for %s".formatted(buildCardName(curLevel, curCardPos));
        }

        private String buildCardName(int curLevel, int curCardPos) {
            if (isRootLevel(curLevel)) {
                return "Card_Root-N%d".formatted(curCardPos);
            }

            return "Card_L%d>L%d-N%d".formatted(curLevel, curLevel - 1, curCardPos);
        }

        private long buildCardId(int curLevel, int curCardPos) {
            return curLevel * maxNumOfCards + curCardPos;
        }

        private List<DomainNodeData> buildDomainNodeData(int curLevel) {
            List<DomainNodeData> result = list();

            Classe curLevelClasse = classes.get(curLevel);

            if (isRootLevel(curLevel)) {
                DomainNodeData rootNode = new DomainNodeData();
                rootNode.id = buildNodeRootId();
                rootNode.targetClass = curLevelClasse.getName();
                result.add(rootNode);
            } else {
                List<Domain> curLevelDomains = domains.get(curLevel);
                IntStream.range(0, maxNumOfCards)
                        .forEach(curDelta -> {
                            Domain curDomain = curLevelDomains.get(curDelta);

                    DomainNodeData curNode = new DomainNodeData();
                    curNode.id = buildNodeId(curLevel, curDelta);
                    curNode.targetClass = curLevelClasse.getName();
                    curNode.domain = curDomain.getName();
                    curNode.parent = buildNodeId(curLevel - 1, curDelta);

                            result.add(curNode);
                        });
            }

            return result;
        }

        private String buildNodeRootId() {
            return "NodeRoot-0";
        }

        private String buildNodeId(int curLevel, int curPos) {
            if (isRootLevel(curLevel)) {
                return buildNodeRootId();
            }

            return "Node_%d-N%d".formatted(curLevel, curPos);
        }

        private boolean isRootLevel(int curLevel) {
            return curLevel == 0;
        }

        public static String buildUUID() {
            return UUID.randomUUID().toString();
        }

        private List<Card> getOnlyOtherDiagonalCards() {
            List<List<Card>> otherCards = list(cards)
                    .subList(0, cards.size() - 1); // Remove last level of Cards
            List<Card> result = list();
            for (int curLevel = 0; curLevel < otherCards.size(); curLevel++) {
                final int theCurLevel = curLevel; // for lambda awful sake
                otherCards.get(curLevel).stream().filter(c -> isDiagonalId(c, theCurLevel)).forEach(c -> {
                    result.add(c);
                });
            }

            System.out.println("TEST: Other diagonal cards: %s".formatted(buildLog_Short(result)));
            return result;
        }
    } // end UseCase12Builder class

} // end DomainPathFinderImplTest class

class ClasseMatcher extends ArgumentMatcher<Classe> {

    private final Classe expLeftClasse;

    ClasseMatcher(Classe expLeft) {
        this.expLeftClasse = expLeft;
    }

    @Override
    public boolean matches(Object obj) {
        Classe actualRight = (Classe) obj;

        // @todo AFE: non so perché arriva null qui
        if (actualRight == null) {
            return false;
        }

        return Objects.equals(expLeftClasse.getName(), actualRight.getName())
                && Objects.equals(expLeftClasse.getParentOrNull(), actualRight.getParentOrNull());
    }

} // end ClassMatcher class

class ClasseByNameMatcher extends ArgumentMatcher<Classe> {

    private final String expLeftClasseName;

    ClasseByNameMatcher(String expLeft) {
        this.expLeftClasseName = expLeft;
    }

    @Override
    public boolean matches(Object obj) {
        Classe actualRight = (Classe) obj;

        // @todo AFE: non so perché arriva null qui
        if (actualRight == null) {
            return false;
        }

        return Objects.equals(expLeftClasseName, actualRight.getName());
    }

} // end ClassMatcher class

class CardMatcher extends ArgumentMatcher<Card> {

    private final Card expLeftCard;

    CardMatcher(Card expLeft) {
        this.expLeftCard = expLeft;
    }

    @Override
    public boolean matches(Object obj) {
        Card actualRight = (Card) obj;

        // @todo AFE: non so perché arriva null qui
        if (actualRight == null) {
            return false;
        }

        return Objects.equals(expLeftCard.getClassName(), actualRight.getClassName())
                && Objects.equals(expLeftCard.getId(), actualRight.getId());
    }

} // end CardMatcher class

class CardMatch {

    final String classeName;
    final long id;

    CardMatch(String classeName, long id) {
        this.classeName = classeName;
        this.id = id;
    }

    void checkMatch(Card actual) {
        assertEquals(classeName, actual.getClassName());
        assertEquals(id, (long) actual.getId());
    }

} // end CardMatch class

