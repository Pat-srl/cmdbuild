/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.domain.tree;

import java.nio.file.Path;
import static java.util.Arrays.asList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.DomainDataBase;
import static org.cmdbuild.TestHelper_Model.mockBuildClasse;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.services.serialization.domain.tree.DomainNodeData;
import org.cmdbuild.services.serialization.domain.tree.DomainTreeData;
import org.cmdbuild.sync.ClasseSync;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import org.geotools.metadata.math.Complex;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author afelice
 */
public class DomainTreeBuilderTest extends DomainDataBase {

    private final static String A_KNOWN_DOMAIN_TREE_JSON_FILE = Path.of(TEST_RESOURCE_PATH.toString(), "domain_tree_00_three_paths.json").toString();

    private final static String A_KNOWN_DOMAIN_TREE_WITH_FILTERS_JSON_FILE = Path.of(TEST_RESOURCE_PATH.toString(), "domain_tree_00_path_with_filters.json").toString();
    private final static String A_KNOWN_DOMAIN_COMPLEX = "ComplexBuilding";
    private final static String A_KNOWN_CQL_FILTER_ON_COMPLEX = "a_filter_on_Complex";
    private final static String A_KNOWN_DOMAIN_BUILDING = "BuildingFloor";
    private final static String A_KNOWN_CQL_FILTER_ON_BUILDING = "a_filter_on_Building";
    private final static String A_KNOWN_DOMAIN_FLOOR = "FloorRoom";
    private final static String A_KNOWN_CQL_FILTER_ON_FLOOR = "a_filter_on_Floor";

    private final static String A_KNOWN_DOMAIN_TREE_NO_NODES_JSON_FILE = Path.of(TEST_RESOURCE_PATH.toString(), "domain_tree_13_no_nodes.json").toString();
    private final static String A_KNOWN_DOMAIN_TREE_NO_ROOT_NODE_JSON_FILE = Path.of(TEST_RESOURCE_PATH.toString(), "domain_tree_14_no_root_node.json").toString();
    private final static String A_KNOWN_DOMAIN_TREE_MULTIPLE_ROOT_NODES_JSON_FILE = Path.of(TEST_RESOURCE_PATH.toString(), "domain_tree_15_multiple_root_nodes.json").toString();
    private final static String A_KNOWN_DOMAIN_TREE_DOMAIN_LOOP_JSON_FILE = Path.of(TEST_RESOURCE_PATH.toString(), "domain_tree_16_domain_loop.json").toString();
    private final static String A_KNOWN_DOMAIN_TREE_TWO_LEAF_SUBPATHS_JSON_FILE = Path.of(TEST_RESOURCE_PATH.toString(), "domain_tree_17_two_leaf_subpaths.json").toString();

    protected final ClasseSync classeSync = mock(ClasseSync.class);

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    DomainTreeBuilder instance = new DomainTreeBuilder(classeSync);

    /**
     * Test of buildDomainTree method, 2 paths cut <code>Room</code> {@Classe} or
     * its superclasse, of class DomainTreeBuilder.
     *
     * <p>From 3 paths in given {@link DomainTreeData}:
     * <pre>
     *	 Complex -> Building -> Floor -> Room -> Rack -> ServerHW
     *	 Complex -> AnotherRoomBranchClasse -> SuperRoom -> OtherStuff
     *	 Complex -> AnotherBranch -> Foo
     * </pre>
     * that starts from the root {@link Classe} {@link Complex}:
     * <ol>
     * <li>cuts the first <i>path</i> to {@link Classe} <code>Room</code>;
     * <li>cuts the second <i>path</i> to {@link Classe} <code>SuperRoom</code>, that is a <code>Room</code> supercclass;
     * <li>discards the third <i>path</i>, that doesn't contain any {@link Classe} <code>Room></code> or its superclasses.
     * </ol>
     * obtaining the 2 following <i>suitable paths</i>:
     * <pre>
     *  Complex -> Building -> Floor -> Room
     *  Complex -> AnotherRoomBranchClasse -> SuperRoom
     * </pre>
     */
    @Test
    public void testBuildDomainTree() {
        System.out.println("buildDomainTree");

        //arrange:
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_JSON_FILE));
        DomainTreeData domainTreeData = CmJsonUtils.fromJson(domainTreeDataJson, DomainTreeData.class);
        assertFalse(domainTreeData.nodes.isEmpty());

        // First branch, normal classes
        mockClasseReturn(complexClasse);
        mockClasseReturn(buildingClasse);
        mockClasseReturn(floorClasse);
        mockClasseReturn(roomClasse);
        // Superclasse for Room in second branch
        mockClasseReturn(superRoomClasse);
        mockClasseReturn(midRoomClasse);
        mockClasseReturn(roomWithSuperclasse);

        //act:
        DomainTree result = instance.buildDomainTree(domainTreeData, A_KNOWN_LEAF_CLASS_NAME);

        //assert:
        assertTrue(result.hasPaths());
        assertEquals(domainTreeData.name, result.name);
        assertEquals(domainTreeData.description, result.description);
        assertEquals(domainTreeData.id, result.id);

         assertEquals(2, result.getPaths().size());
        assertEquals("Complex", result.rootNodeClasseName);
        assertEquals(A_KNOWN_LEAF_CLASS_NAME, result.leafNodeClasseName);
        checkPath(list("Complex", "Building", "Floor", "Room"), result.getPaths().get(0));
        checkPath(list("Complex", "AnotherRoomBranchClasse", "SuperRoom"), result.getPaths().get(1));
    }

    /**
     * Test of buildDomainTree method, 1 path <b>with filters</b> cut
     * <code>Room</code> {@Classe}, of class DomainTreeBuilder.
     *
     * <p>
     * <i>CQL filter</i> to be applied to ({@link Card} of) <i>source</i>
     * {@link Class} is defined, in the original tree, in parent node. To
     * simplify the handling in {@link DomainPathFinder}, move the (parent)
     * filter in the child node. So, the <i>CQL filter</i> to be applied to
     * (parent) <code>Floor</code> {@link Card} is now in
     * <code>"targetClass": "Room"</code>     * node.
     */
    @Test
    public void testBuildDomainTree_Filters() {
        System.out.println("buildDomainTree_Filters");

        //arrange:
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_WITH_FILTERS_JSON_FILE));
        DomainTreeData domainTreeData = CmJsonUtils.fromJson(domainTreeDataJson, DomainTreeData.class);
        assertFalse(domainTreeData.nodes.isEmpty());

        // First branch, normal classes
        mockClasseReturn(complexClasse);
        mockClasseReturn(buildingClasse);
        mockClasseReturn(floorClasse);
        mockClasseReturn(roomClasse);

        //act:
        DomainTree result = instance.buildDomainTree(domainTreeData, A_KNOWN_LEAF_CLASS_NAME);

        //assert:
        assertTrue(result.hasPaths());
        assertEquals(domainTreeData.name, result.name);
        assertEquals(domainTreeData.description, result.description);
        assertEquals(domainTreeData.id, result.id);

        assertEquals(1, result.getPaths().size());
        assertEquals("Complex", result.rootNodeClasseName);
        assertEquals(A_KNOWN_LEAF_CLASS_NAME, result.leafNodeClasseName);
        DomainPath resultPath = result.getPaths().get(0);
        assertEquals(4, resultPath.size());
        checkPath(list("Complex", "Building", "Floor", "Room"), resultPath);

        // Complex node (root)
        final DomainNodeData complexNode = resultPath.get(0);
        assertEquals("Complex", complexNode.targetClass);
        assertTrue(isBlank(complexNode.domain)); // root has no defined root
        assertTrue(isBlank(complexNode.cqlFilter)); // not interested in filters for parents above the root Classe

        // Building node
        final DomainNodeData buildingNode = resultPath.get(1);
        assertEquals("Building", buildingNode.targetClass);
        assertEquals(A_KNOWN_DOMAIN_COMPLEX, buildingNode.domain);
        assertEquals(A_KNOWN_CQL_FILTER_ON_COMPLEX, buildingNode.cqlFilter);

        // Floor node
        final DomainNodeData floorNode = resultPath.get(2);
        assertEquals("Floor", floorNode.targetClass);
        assertEquals(A_KNOWN_DOMAIN_BUILDING, floorNode.domain);
        assertEquals(A_KNOWN_CQL_FILTER_ON_BUILDING, floorNode.cqlFilter);
        // Room node
        final DomainNodeData roomNode = resultPath.get(3);
        assertEquals("Room", roomNode.targetClass);
        assertEquals(A_KNOWN_DOMAIN_FLOOR, roomNode.domain);
        assertEquals(A_KNOWN_CQL_FILTER_ON_FLOOR, roomNode.cqlFilter);
    }

    /**
     * Test of buildDomainTree method, 2 paths cut <code>Room</code> {@Classe} or
     * its superclasse but couldn't load a superclasse {@link Classe}, of class DomainTreeBuilder.
     */
    @Test
    public void testBuildDomainTree_NotExistingSuperclass() {
        System.out.println("buildDomainTree_ErrorNotExistingSuperclass");

        //arrange:
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_JSON_FILE));
        DomainTreeData domainTreeData = CmJsonUtils.fromJson(domainTreeDataJson, DomainTreeData.class);
        assertFalse(domainTreeData.nodes.isEmpty());

        // First branch, normal classes
        mockClasseReturn(complexClasse);
        mockClasseReturn(buildingClasse);
        mockClasseReturn(floorClasse);
        mockClasseReturn(roomClasse);
        // Superclasse for Room in second branch
        mockClasseReturn(superRoomClasse);
        // mockClasseReturn(midRoomClasse); Simulate missing superclass
        mockClasseReturn(roomWithSuperclasse);

        //act:
        exceptionRule.expect(DomainTreeException.class);
        exceptionRule.expectMessage("%s, leaf classe =< %s > couldn't find classe =< %s > searching superclasses for leaf classe =< %s >".formatted(LoggerHelper.buildLog(domainTreeData), A_KNOWN_LEAF_CLASS_NAME, "MidRoom", A_KNOWN_LEAF_CLASS_NAME));
        instance.buildDomainTree(domainTreeData, A_KNOWN_LEAF_CLASS_NAME);
    }

    /**
     * Test of buildDomainTree method, no nodes in tree data, of class
     * DomainTreeBuilder.
     */
    @Test
    public void testBuildDomainTree_UseCase13_ErrorNoNodes() {
        System.out.println("buildDomainTree_UseCase13_ErrorNoNodes");

        //arrange:
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_NO_NODES_JSON_FILE));
        DomainTreeData domainTreeData = CmJsonUtils.fromJson(domainTreeDataJson, DomainTreeData.class);
        assertTrue(domainTreeData.nodes.isEmpty());

        //act:
        exceptionRule.expect(DomainTreeException.class);
        exceptionRule.expectMessage("%s has no nodes".formatted(LoggerHelper.buildLog(domainTreeData)));
        instance.buildDomainTree(domainTreeData, A_KNOWN_LEAF_CLASS_NAME);

        //assert:
    }

    /**
     * Test of buildDomainTree method, no root node in tree data, of class
     * DomainTreeBuilder.
     */
    @Test
    public void testBuildDomainTree_UseCase14_ErrorNoRootNode() {
        System.out.println("buildDomainTree_UseCase14_ErrorNoRootNode");

        //arrange:
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_NO_ROOT_NODE_JSON_FILE));
        DomainTreeData domainTreeData = CmJsonUtils.fromJson(domainTreeDataJson, DomainTreeData.class);
        assertFalse(domainTreeData.nodes.isEmpty());

        //act:
        exceptionRule.expect(DomainTreeException.class);
        exceptionRule.expectMessage("%s has no root node".formatted(LoggerHelper.buildLog(domainTreeData)));
        instance.buildDomainTree(domainTreeData, A_KNOWN_LEAF_CLASS_NAME);

        //assert:
    }

    /**
     * Test of buildDomainTree method, multiple root nodes in tree data, of
     * class DomainTreeBuilder.
     */
    @Test
    public void testBuildDomainTree_UseCase15_ErrorMultipleRootNode() {
        System.out.println("buildDomainTree_UseCase15_ErrorMultipleRootNode");

        //arrange:
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_MULTIPLE_ROOT_NODES_JSON_FILE));
        DomainTreeData domainTreeData = CmJsonUtils.fromJson(domainTreeDataJson, DomainTreeData.class);
        assertFalse(domainTreeData.nodes.isEmpty());

        //act:
        exceptionRule.expect(DomainTreeException.class);
        exceptionRule.expectMessage("%s has multiple root nodes: Complex,AnotherRoot".formatted(LoggerHelper.buildLog(domainTreeData)));
        instance.buildDomainTree(domainTreeData, A_KNOWN_LEAF_CLASS_NAME);

        //assert:
    }

    /**
     * Test of buildDomainTree method, multiple root nodes in tree data, of
     * class DomainTreeBuilder.
     */
    @Test
    public void testBuildDomainTree_UseCase16_DomainLoop() {
        System.out.println("buildDomainTree_UseCase16_ErrorDomainLoop");

        //arrange:
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_DOMAIN_LOOP_JSON_FILE));
        DomainTreeData domainTreeData = CmJsonUtils.fromJson(domainTreeDataJson, DomainTreeData.class);
        assertFalse(domainTreeData.nodes.isEmpty());

        //act:
        exceptionRule.expect(DomainTreeException.class);
        exceptionRule.expectMessage("%s, leaf classe =< %s > has loop on node with domain =< %s >, targetClass =< %s > (node id =< %s >)".formatted(LoggerHelper.buildLog(domainTreeData), A_KNOWN_LEAF_CLASS_NAME, "BuildingFloor", "Floor", "1234b7"));
        instance.buildDomainTree(domainTreeData, A_KNOWN_LEAF_CLASS_NAME);

        //assert:
    }

    /**
     * Test of buildDomainTree method, 2 paths cut <code>Room</code> {@Classe}
     * or its superclasse, of class DomainTreeBuilder.
     *
     * <p>
     * Two paths in given {@link DomainTreeData}:
     * <pre>
     *	 Complex -> Building -> Floor -> Room
     *      -> (Domain RoomCI) Rack -> ServerHW
     *      -> (Domain RoomCI, with filter) Hardware
     * </pre>
     *
     * that starts from the root {@link Classe} {@link Complex}; {@link Classe}
     * <code>Hardware</code> is a
     * <p>
     * superclasse</p> of <code>Rack</code>
     *
     * <p>
     * obtaining the 2 following <i>suitable paths</i>:
     * <pre>
     *  Complex -> Building -> Floor -> Room -> Rack
     *  Complex -> Building -> Floor -> Room -> Hardare
     * </pre>
     */
    @Test
    public void testBuildDomainTree_TwoLeafSubpath() {
        System.out.println("buildDomainTree_TwoLeafSubpath");

        //arrange:
        String aLeafClasseName = "Rack";
        String domainTreeDataJson = readToString(getClass().getResourceAsStream(A_KNOWN_DOMAIN_TREE_TWO_LEAF_SUBPATHS_JSON_FILE));
        DomainTreeData domainTreeData = CmJsonUtils.fromJson(domainTreeDataJson, DomainTreeData.class);
        assertFalse(domainTreeData.nodes.isEmpty());

        // First branch, normal classes
        mockClasseReturn(complexClasse);
        mockClasseReturn(buildingClasse);
        mockClasseReturn(floorClasse);
        mockClasseReturn(roomClasse);
        // Superclasse in leaf subpath of second branch
        Classe hardwareClasse = mockBuildClasse("Hardware", list(knownModelBaseClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription, attribRelationToCards_Rack_Room_Inv));
        Classe rackClasse_WithSuperclasse = mockBuildClasse(aLeafClasseName, list(hardwareClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription, attribRelationToCards_Rack_Room_Inv));
        mockClasseReturn(rackClasse_WithSuperclasse);
        mockClasseReturn(serverHWClasse);
        mockClasseReturn(hardwareClasse);

        //act:
        DomainTree result = instance.buildDomainTree(domainTreeData,
                aLeafClasseName); // Matches both "Rack" and its superclass "Hardware"

        //assert:
        assertTrue(result.hasPaths());
        assertEquals(domainTreeData.name, result.name);
        assertEquals(domainTreeData.description, result.description);
        assertEquals(domainTreeData.id, result.id);

        assertEquals(2, result.getPaths().size());
        assertEquals("Complex", result.rootNodeClasseName);
        assertEquals(aLeafClasseName, result.leafNodeClasseName);
        checkPath(list("Complex", "Building", "Floor", "Room", "Rack"), result.getPaths().get(0));
        checkPath(list("Complex", "Building", "Floor", "Room", "Hardware"), result.getPaths().get(1));
    }

    private void checkPath(List<String> expDomains, DomainPath actualPath) {
        assertEquals(expDomains, actualPath.pathData.stream().map(d -> d.targetClass).collect(toList()));
    }

    private void mockClasseReturn(Classe classe) {
        final String classeName = classe.getName();
        System.out.println("TEST: Mocking Classe on ClasseSync %s".formatted(classeName));
        when(classeSync.read(eq(classeName), Matchers.anyBoolean())).thenReturn(classe);
    }

}
