/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild;

import java.nio.file.Path;
import static java.util.Arrays.asList;
import static org.cmdbuild.TestHelper_Model.mockBuildAttributeId;
import static org.cmdbuild.TestHelper_Model.mockBuildAttributeIdClasse;
import static org.cmdbuild.TestHelper_Model.mockBuildAttributeReference;
import static org.cmdbuild.TestHelper_Model.mockBuildAttributeWithoutOwner;
import static org.cmdbuild.TestHelper_Model.mockBuildCard;
import static org.cmdbuild.TestHelper_Model.mockBuildClasse;
import static org.cmdbuild.TestHelper_Model.mockBuildDomain_11;
import static org.cmdbuild.TestHelper_Model.mockBuildDomain_1N;
import static org.cmdbuild.TestHelper_Model.mockBuildDomain_N1;
import static org.cmdbuild.TestHelper_Model.mockBuildDomain_NN;
import org.cmdbuild.common.beans.IdAndDescriptionImpl;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.RelationDirection;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.domain.tree.DomainTreeBuilder;
import static org.cmdbuild.domain.tree.DomainTreeBuilder.MODEL_BASE_CLASSE_NAME;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * Creates {@link Classe}s with <code>REFERENCE</code> {@link Attribute}s for {@link Domain}s
 * and {@link Card}s that are related to others to test:
 * <ol>
 * <li>the <code>12</code> <i>use cases</i> for {@link DomainTreeBuilder};
 * <li>the <code>4</code> <i>use cases</i> for {@link DomainPathFinder}.
 * </ol>
 *
 * <p>Similar to <a href="https://gitlab.example.com/cmdbuild-plugins/generic/model-diff-data/-/blob/master/src/test/java/org/cmdbuild/modeldiff/module/data/dataset/data/DatasetDataBase.java?ref_type=heads">modeldiff-data::DatasetDataBase</a>.
 *
 * @author afelice
 */
public class DomainDataBase {

    // (Resource) Folder for artifacts read
    public final static Path TEST_RESOURCE_PATH = Path.of("/org/cmdbuild/test/domain/tree");

    static {
        UniqueTestIdUtils.prepareTuid();
    }

    protected final AttributeWithoutOwner attribWOId;
    protected final AttributeWithoutOwner attribWOType;
    protected final AttributeWithoutOwner attribWOCode;
    protected final AttributeWithoutOwner attribWODescription;
    protected final AttributeWithoutOwner attribRelationToCards_Building_Complex_Inv;
    protected final AttributeWithoutOwner attribRelationToCards_Floor_Building_Inv;
    protected final AttributeWithoutOwner attribRelationToCards_Room_Floor_Inv;
    protected final AttributeWithoutOwner attribRelationToCards_Rack_Room_Inv;
    protected final AttributeWithoutOwner attribRelationToCards_ServerHW_Rack_Inv;

    protected final AttributeWithoutOwner attribRelationToCards_AnotherChild_Complex_Inv;
    protected final AttributeWithoutOwner attribRelationToCards_SuperRoom_AnotherChild_Inv;
    protected final AttributeWithoutOwner attribRelationToCards_SuperRoom_OtherStuff_Inv;

    protected final Classe knownModelBaseClasse;

    protected final static String A_KNOWN_LEAF_CLASS_NAME = "Room";

    protected final Classe complexClasse;
    protected final Classe buildingClasse;
    protected final Classe floorClasse;
    protected final Classe roomClasse;
    protected final Classe superRoomClasse;
    protected final Classe midRoomClasse;
    protected final Classe roomWithSuperclasse;
    protected final Classe rackClasse;
    protected final Classe serverHWClasse;
    // Path two
    protected final Classe anotherRoomBranchClasse;
//    protected final Classe otherStuffClasse;

    protected final Card aCard_Complex50;
    protected final Card aCard_Building40;
    protected final Card aCard_Building41;
    protected final Card aCard_Floor30;
    protected final Card aCard_Floor31;
    protected final Card aCard_Room20;
    protected final Card aCard_Room21;
    protected final Card aCard_AnotherRoomBranchClasse80;
    protected final Card aCard_AnotherRoomBranchClasse81;
    protected final Card aCard_Complex51;
    protected final Card aCard_Room70_path2;
    protected final Card aCard_Room71_path2;

    // Path one
    protected final Domain aDomain_ComplexBuilding;
    protected final Domain aDomain_BuildingFloor;
    protected final Domain aDomain_FloorRoom;
    protected final Domain aDomain_RoomCI;
    protected final Domain aDomain_RackServerHW;

    // Path two
    protected final Domain aDomain_AnotherChild;
    protected final Domain aDomain_SuperOtherRoom;
//    protected final Domain aDomain_StuffDomain;

    // UseCase 2
    protected final AttributeWithoutOwner attribRelationToCards_SuperRoom_Floor_Inv_UseCase2;
    protected final Classe superRoomClasse_UseCase2;
    protected final Classe roomClasse_UseCase2;
    protected final Domain aDomain_FloorRoom_UseCase2;
    protected final Card aCard_Room20_uc2;

    // UseCase 7
    protected final AttributeWithoutOwner attribRelationToCards_SuperFloor_Complex_Inv_UseCase7;
    protected final Classe superFloorClasse_UseCase7;
    protected final Domain aDomain_ComplexSuperFloor_UseCase7;
    protected final Card aCard_Floor30_uc7;

    protected final AttributeWithoutOwner attribRelationToCards_Middle_SuperFloor_Inv_UseCase7;
    protected final Classe middleClasse_UseCase7;
    protected final Domain aDomain_SuperFloorMiddle_UseCase7;
    protected final Card aCard_Middle45_uc7;

    protected final AttributeWithoutOwner attribRelationToCards_Building_Middle_Inv_UseCase7;
    protected final Classe buildingClasse_UseCase7;
    protected final Domain aDomain_MiddleBuilding_UseCase7;
    protected final Card aCard_Building40_uc7;

    // UseCase 8
    protected final AttributeWithoutOwner attribRelationToCards_Room_Floor_UseCase8;
    protected final Classe roomClasse_UseCase8;
    protected final Domain aDomain_FloorRoom_UseCase8;
    protected final Card aCard_Room20_uc8;

    // UseCase 9
    protected final AttributeWithoutOwner attribRelationToCards_Room_Floor_UseCase9;
    protected final Classe roomClasse_UseCase9;
    protected final Domain aDomain_FloorRoom_UseCase9;
    protected final Card aCard_Room20_uc9;

    // UseCase 10
    protected final AttributeWithoutOwner attribRelationToCards_Room_Floor_UseCase10;
    protected final Classe roomClasse_UseCase10;
    protected final Classe floorClasse_UseCase10;
    protected final Domain aDomain_FloorRoom_UseCase10;
    protected final Card aCard_Room20_uc10;
    protected final Card aCard_Floor30_uc10;

    // UseCase 11
    protected final AttributeWithoutOwner attribRelationToCards_Room_Floor_Inv_UseCase11;
    protected final Classe roomClasse_UseCase11;
    protected final Domain aDomain_FloorRoom_UseCase11;
    protected final Card aCard_Room20_uc11;

    // UseCase 1_b
    protected final AttributeWithoutOwner attribRelationToCards_Room_Floor_UseCase1_b;
    protected final Classe roomClasse_UseCase1_b;
    protected final Classe floorClasse_UseCase1_b;
    protected final Domain aDomain_FloorRoom_UseCase1_b;
    protected final Card aCard_Room20_uc1_b;
    protected final Card aCard_Floor30_uc1_b;
    protected final Card aCard_Floor31_uc1_b;

    // UseCase Missing Reference in child
    protected final Classe roomClasse_MissingRef;
    protected final Domain aDomain_FloorRoom_MissingRef;
    protected final Card aCard_Room20_MissingRef;

    public DomainDataBase() {
        attribWOId = mockBuildAttributeId();
        attribWOType = mockBuildAttributeIdClasse();
        attribWOCode = mockBuildAttributeWithoutOwner(ATTR_CODE);
        attribWODescription = mockBuildAttributeWithoutOwner(ATTR_DESCRIPTION);

        knownModelBaseClasse = mockBuildClasse(MODEL_BASE_CLASSE_NAME, list(MODEL_BASE_CLASSE_NAME),
                list(attribWOId, attribWOType, attribWOCode, attribWODescription));

        complexClasse = mockBuild_NormalClasse("Complex");

        attribRelationToCards_Building_Complex_Inv = mockBuildAttributeReference("referenceAttr_BuildingN_Complex1", "ComplexBuilding", RelationDirection.RD_INVERSE);
        buildingClasse = mockBuildClasse("Building", list(knownModelBaseClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription, attribRelationToCards_Building_Complex_Inv));

        attribRelationToCards_Floor_Building_Inv = mockBuildAttributeReference("referenceAttr_FloorN_Building1", "BuildingFloor", RelationDirection.RD_INVERSE);
        floorClasse = mockBuildClasse("Floor", list(knownModelBaseClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription, attribRelationToCards_Floor_Building_Inv));

        attribRelationToCards_Room_Floor_Inv = mockBuildAttributeReference("referenceAttr_RoomN_Floor1", "FloorRoom", RelationDirection.RD_INVERSE);
        roomClasse = mockBuildClasse("Room", list(knownModelBaseClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription, attribRelationToCards_Room_Floor_Inv));

        attribRelationToCards_Rack_Room_Inv = mockBuildAttributeReference("referenceAttr_RackN_Room1", "RoomCI", RelationDirection.RD_INVERSE);
        rackClasse = mockBuildClasse("Rack", list(knownModelBaseClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription, attribRelationToCards_Rack_Room_Inv));

        attribRelationToCards_ServerHW_Rack_Inv = mockBuildAttributeReference("referenceAttr_ServerHWN_Rack1", "RackServiceHW", RelationDirection.RD_INVERSE);
        serverHWClasse = mockBuildClasse("ServerHW", list(knownModelBaseClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription, attribRelationToCards_ServerHW_Rack_Inv));

        attribRelationToCards_AnotherChild_Complex_Inv = mockBuildAttributeReference("referenceAttr_AnotherChildN_Complex1", "AnotherChild", RelationDirection.RD_INVERSE);
        anotherRoomBranchClasse = mockBuildClasse("AnotherRoomBranchClasse", list(knownModelBaseClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription, attribRelationToCards_AnotherChild_Complex_Inv));

        attribRelationToCards_SuperRoom_AnotherChild_Inv = mockBuildAttributeReference("referenceAttr_SuperRoomN_AnotherChild1", "SuperOtherRoom", RelationDirection.RD_INVERSE);
        superRoomClasse = mockBuildClasse("SuperRoom", list(knownModelBaseClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription, attribRelationToCards_SuperRoom_AnotherChild_Inv));

        attribRelationToCards_SuperRoom_OtherStuff_Inv = mockBuildAttributeReference("referenceAttr_OtherStuffN_SuperRoom1", "StuffDomain", RelationDirection.RD_INVERSE);
//        otherStuffClasse = mockBuildClasse("OtherStuff", list(knownModelBaseClasse.getName()),
//                asList(attribWOId, attribWOType, attribWOCode, attribWODescription, attribRelationToCards_SuperRoom_AnotherChild));

        midRoomClasse = mockBuildClasse("MidRoom", list(superRoomClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription));
        roomWithSuperclasse = mockBuildClasse(A_KNOWN_LEAF_CLASS_NAME, list(midRoomClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription));

        aDomain_ComplexBuilding = mockBuildDomain_1N("ComplexBuilding", 1L, complexClasse, buildingClasse);
        aDomain_BuildingFloor = mockBuildDomain_1N("BuildingFloor", 2L, buildingClasse, floorClasse);
        aDomain_FloorRoom = mockBuildDomain_1N("FloorRoom", 3L, floorClasse, roomClasse);
        aDomain_RoomCI = mockBuildDomain_1N("RoomCI", 4L, roomClasse, rackClasse);
        aDomain_RackServerHW = mockBuildDomain_1N("RackServerHW", 5L, rackClasse, serverHWClasse);

        aDomain_AnotherChild = mockBuildDomain_1N("AnotherChild", 6L, complexClasse, anotherRoomBranchClasse);
        aDomain_SuperOtherRoom = mockBuildDomain_1N("SuperOtherRoom", 7L, anotherRoomBranchClasse, superRoomClasse);
//        aDomain_StuffDomain = mockBuildDomain_1N("StuffDomain", 8L, superRoomClasse, otherStuffClasse);

        // ----<Path one>----
        // Building -> Complex
        // Building.40 -> Complex.50
        aCard_Building40 = mockBuildCard(40L, buildingClasse,
                map(attribWOCode.getName(), "bulidingCard_40",
                        attribWODescription.getName(), "building card 40",
                        attribRelationToCards_Building_Complex_Inv.getName(), new IdAndDescriptionImpl(50L, "complex card 50")
                ));
        // Building.41 -> Complex.51
        aCard_Building41 = mockBuildCard(41L, buildingClasse,
                map(attribWOCode.getName(), "buildingCard_41",
                        attribWODescription.getName(), "building card 41",
                        attribRelationToCards_Building_Complex_Inv.getName(), new IdAndDescriptionImpl(51L, "complex card 51")
                ));
        aCard_Complex50 = mockBuildCard(50L, complexClasse,
                map(
                        attribWOCode.getName(), "complexCard_50",
                        attribWODescription.getName(), "commplex card 50"
                ));
        aCard_Complex51 = mockBuildCard(51L, complexClasse,
                map(
                        attribWOCode.getName(), "complexCard_51",
                        attribWODescription.getName(), "commplex card 51"
                ));

        // Floor -> Building
        // Floor.30 -> Building.40
        aCard_Floor30 = mockBuildCard(30L, floorClasse,
                map(attribWOCode.getName(), "floorCard_30",
                        attribWODescription.getName(), "floor card 30",
                        attribRelationToCards_Floor_Building_Inv.getName(), new IdAndDescriptionImpl(40L, "building card 40")
                ));
        aCard_Floor31 = mockBuildCard(31L, floorClasse,
                map(attribWOCode.getName(), "floorCard_31",
                        attribWODescription.getName(), "floor card 31",
                        attribRelationToCards_Floor_Building_Inv.getName(), new IdAndDescriptionImpl(41L, "building card 41")
                ));

        // Room -> Floor
        // Room.20 -> Floor.30
        aCard_Room20 = mockBuildCard(20L, roomWithSuperclasse,
                map(attribWOCode.getName(), "roomCard_20",
                        attribWODescription.getName(), "room card 20",
                        attribRelationToCards_Room_Floor_Inv.getName(), new IdAndDescriptionImpl(30L, "floor card 30")
                ));
        aCard_Room21 = mockBuildCard(21L, roomWithSuperclasse,
                map(attribWOCode.getName(), "roomCard_21",
                        attribWODescription.getName(), "room card 21",
                        attribRelationToCards_Room_Floor_Inv.getName(), new IdAndDescriptionImpl(31L, "floor card 31")
                ));

        // ----<Path two>----
        // AnotherRoomBranchClasse -> Complex
        // AnotherRoomBranchClasse.80 -> Complex.51
        aCard_AnotherRoomBranchClasse80 = mockBuildCard(80L, anotherRoomBranchClasse,
                map(attribWOCode.getName(), "anotherRoomBranchClasseCard_80",
                        attribWODescription.getName(), "anotherRoomBranchClasse card 80",
                        attribRelationToCards_AnotherChild_Complex_Inv.getName(), new IdAndDescriptionImpl(51L, "complex card 51")
                ));
        // AnotherRoomBranchClasse.81 -> Complex.51
        aCard_AnotherRoomBranchClasse81 = mockBuildCard(81L, anotherRoomBranchClasse,
                map(attribWOCode.getName(), "anotherRoomBranchCard_81",
                        attribWODescription.getName(), "anotherRoomBranch card 81",
                        attribRelationToCards_AnotherChild_Complex_Inv.getName(), new IdAndDescriptionImpl(51L, "complex card 51")
                ));

        // Room (through SuperRoom) -> AnotherRoomBranchClasse
        // Room.70 -> AnotherRoomBranchClasse.80
        aCard_Room70_path2 = mockBuildCard(70L, roomWithSuperclasse,
                map(attribWOCode.getName(), "roomCard_70",
                        attribWODescription.getName(), "room card 70",
                        attribRelationToCards_Room_Floor_Inv.getName(), new IdAndDescriptionImpl(80L, "anotherRoomBranchClasse card 80")
                ));
        // Room.71  -> AnotherRoomBranchClasse.81
        aCard_Room71_path2 = mockBuildCard(71L, roomWithSuperclasse,
                map(attribWOCode.getName(), "roomCard_71",
                        attribWODescription.getName(), "room card 71",
                        attribRelationToCards_Room_Floor_Inv.getName(), new IdAndDescriptionImpl(81L, "anotherRoomBranchClasse card 80")
                ));

        // ==== Use case 2:
        // only one branch, a Superclass of destination class
        attribRelationToCards_SuperRoom_Floor_Inv_UseCase2 = mockBuildAttributeReference("referenceAttr_SuperRoomN_Floor1", "FloorRoom", RelationDirection.RD_INVERSE);
        superRoomClasse_UseCase2 = mockBuildClasse("SuperRoom", list(knownModelBaseClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription, attribRelationToCards_SuperRoom_Floor_Inv_UseCase2));
        roomClasse_UseCase2 = mockBuildClasse("Room", list(superRoomClasse_UseCase2.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription, attribRelationToCards_SuperRoom_Floor_Inv_UseCase2));
        aDomain_FloorRoom_UseCase2 = mockBuildDomain_1N("FloorRoom", 9L, floorClasse, superRoomClasse_UseCase2);

        // Room -> Floor
        // Room20_uc2 -> Floor30
        aCard_Room20_uc2 = mockBuildCard(20L, roomClasse_UseCase2,
                map(attribWOCode.getName(), "roomCard_20_uc2",
                        attribWODescription.getName(), "room card 20 uc2",
                        attribRelationToCards_SuperRoom_Floor_Inv_UseCase2.getName(), new IdAndDescriptionImpl(30L, "floor card 30")
                ));

        // ==== Use case 7:
        // two branches, first branch has a Card loop (through a Seperclass belonging)
        // Room.20 -> Floor.30
        //              (A) -> Building.40 -> Middle.45 -> (SuperFloor)Floor.30 -> Complex.50
        //              (B) -> Complex.51
        //
        // (A) Building.40 -> Middle.45
        attribRelationToCards_Building_Middle_Inv_UseCase7 = mockBuildAttributeReference("referenceAttr_BuildingN_Middle1", "MiddleBuilding", RelationDirection.RD_INVERSE);
        buildingClasse_UseCase7 = mockBuildClasse("Building", list(knownModelBaseClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription,
                        attribRelationToCards_Building_Middle_Inv_UseCase7
                ));
        attribRelationToCards_Middle_SuperFloor_Inv_UseCase7 = mockBuildAttributeReference("referenceAttr_MiddleN_SuperFloor1", "SuperFloorMiddle", RelationDirection.RD_INVERSE);
        middleClasse_UseCase7 = mockBuildClasse("Middle", list(knownModelBaseClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription,
                        attribRelationToCards_Middle_SuperFloor_Inv_UseCase7
                ));
        aDomain_MiddleBuilding_UseCase7 = mockBuildDomain_1N("MiddleBuilding", 10L, middleClasse_UseCase7, buildingClasse_UseCase7);
        aCard_Building40_uc7 = mockBuildCard(40L, buildingClasse_UseCase7,
                map(attribWOCode.getName(), "buildingCard_40_uc7",
                        attribWODescription.getName(), "building card 40 uc7",
                        attribRelationToCards_Building_Middle_Inv_UseCase7.getName(), new IdAndDescriptionImpl(45L, "middle card 45")
                ));

        // (A) Middle.45 -> (SuperFloor)Floor.30
        attribRelationToCards_SuperFloor_Complex_Inv_UseCase7 = mockBuildAttributeReference("referenceAttr_SuperFloorN_Complex1", "ComplexSuperFloor", RelationDirection.RD_INVERSE);
        superFloorClasse_UseCase7 = mockBuildClasse("Floor", list(knownModelBaseClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription,
                        attribRelationToCards_Floor_Building_Inv,
                        attribRelationToCards_SuperFloor_Complex_Inv_UseCase7
                ));
        aDomain_SuperFloorMiddle_UseCase7 = mockBuildDomain_1N("SuperFloorMiddle", 11L, superFloorClasse_UseCase7, middleClasse_UseCase7);
        aCard_Middle45_uc7 = mockBuildCard(45L, middleClasse_UseCase7,
                map(attribWOCode.getName(), "middleCard_45_uc7",
                        attribWODescription.getName(), "middle card 45 uc7",
                        attribRelationToCards_Middle_SuperFloor_Inv_UseCase7.getName(), new IdAndDescriptionImpl(30L, "floor card 30")
                ));

        // Floor.30 uc7 is related to both (A) Building.40 and (B) Complex.51
        aDomain_ComplexSuperFloor_UseCase7 = mockBuildDomain_1N("ComplexSuperFloor", 12L, complexClasse, superFloorClasse_UseCase7);
        aCard_Floor30_uc7 = mockBuildCard(30L, superFloorClasse_UseCase7,
                map(attribWOCode.getName(), "floorCard_30_uc7",
                        attribWODescription.getName(), "floor card 30 uc7",
                        attribRelationToCards_Floor_Building_Inv.getName(), new IdAndDescriptionImpl(40L, "building card 40"),
                        attribRelationToCards_SuperFloor_Complex_Inv_UseCase7.getName(), new IdAndDescriptionImpl(51L, "complex card 51")
                ));

        // ==== Use case 8:
        // two branches, first branch has a N:N Domain
        attribRelationToCards_Room_Floor_UseCase8 = mockBuildAttributeReference("referenceAttr_RoomN_FloorN", "FloorRoom", RelationDirection.RD_DIRECT);
        roomClasse_UseCase8 = mockBuildClasse("Room", list(midRoomClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription, attribRelationToCards_Room_Floor_UseCase8, attribRelationToCards_SuperRoom_AnotherChild_Inv));
        aDomain_FloorRoom_UseCase8 = mockBuildDomain_NN("FloorRoom", 13L, floorClasse, roomClasse_UseCase8);
        aCard_Room20_uc8 = mockBuildCard(20L, roomClasse_UseCase8,
                map(attribWOCode.getName(), "roomCard_20_uc8",
                        attribWODescription.getName(), "room card 20 uc8",
                        attribRelationToCards_Room_Floor_UseCase8.getName(), new IdAndDescriptionImpl(30L, "floor card 30"),
                        attribRelationToCards_SuperRoom_AnotherChild_Inv.getName(), new IdAndDescriptionImpl(80L, "anotherRoomBranch card card 80") // reference in second branch
                ));

        // ==== Use case 9:
        // two branches, first branch has a 1:1 Domain
        attribRelationToCards_Room_Floor_UseCase9 = mockBuildAttributeReference("referenceAttr_Room1_Floor1", "FloorRoom", RelationDirection.RD_DIRECT);
        roomClasse_UseCase9 = mockBuildClasse("Room", list(midRoomClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription, attribRelationToCards_Room_Floor_UseCase9, attribRelationToCards_SuperRoom_AnotherChild_Inv));
        aDomain_FloorRoom_UseCase9 = mockBuildDomain_11("FloorRoom", 14L, floorClasse, roomClasse_UseCase9);
        aCard_Room20_uc9 = mockBuildCard(20L, roomClasse_UseCase9,
                map(attribWOCode.getName(), "roomCard_20_uc9",
                        attribWODescription.getName(), "room card 20 uc9",
                        attribRelationToCards_Room_Floor_UseCase9.getName(), new IdAndDescriptionImpl(30L, "floor card 30"),
                        attribRelationToCards_SuperRoom_AnotherChild_Inv.getName(), new IdAndDescriptionImpl(80L, "anotherRoomBranch card card 80") // reference in second branch
                ));

        // ==== Use case 10:
        // two branches, first branch has a N:1 Domain with direct Reference Attribute in parent Class
        roomClasse_UseCase10 = mockBuildClasse("Room", list(midRoomClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription, attribRelationToCards_SuperRoom_AnotherChild_Inv));
        attribRelationToCards_Room_Floor_UseCase10 = mockBuildAttributeReference("referenceAttr_Room1_FloorN", "FloorRoom", RelationDirection.RD_DIRECT);
        floorClasse_UseCase10 = mockBuildClasse("Floor", list(knownModelBaseClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription, attribRelationToCards_Room_Floor_UseCase10));
        aDomain_FloorRoom_UseCase10 = mockBuildDomain_N1("FloorRoom", 15L, floorClasse_UseCase10, roomClasse_UseCase10);
        aCard_Room20_uc10 = mockBuildCard(20L, roomClasse_UseCase10,
                map(attribWOCode.getName(), "roomCard_20_uc10",
                        attribWODescription.getName(), "room card 20 uc10",
                        attribRelationToCards_SuperRoom_AnotherChild_Inv.getName(), new IdAndDescriptionImpl(80L, "anotherRoomBranch card card 80") // reference in second branch
                ));
        // Attribute direct for Domain N:1
        aCard_Floor30_uc10 = mockBuildCard(30L, floorClasse_UseCase10,
                map(attribWOCode.getName(), "floorCard_30_uc10",
                        attribWODescription.getName(), "floor card 30 uc10",
                        attribRelationToCards_Room_Floor_UseCase10.getName(), new IdAndDescriptionImpl(20L, "room card 20")
                ));

        // ==== Use case 11:
        // two branches, first branch has a 1:N Domain with direct Reference Attribute in child Class
        attribRelationToCards_Room_Floor_Inv_UseCase11 = mockBuildAttributeReference("referenceAttr_RoomN_Floor1", "FloorRoom", RelationDirection.RD_INVERSE);
        roomClasse_UseCase11 = mockBuildClasse("Room", list(midRoomClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription, attribRelationToCards_Room_Floor_Inv_UseCase11));
        aDomain_FloorRoom_UseCase11 = mockBuildDomain_1N("FloorRoom", 16L, floorClasse, roomClasse_UseCase11);
        aCard_Room20_uc11 = mockBuildCard(20L, roomClasse_UseCase11,
                map(attribWOCode.getName(), "roomCard_20_uc11",
                        attribWODescription.getName(), "room card 20 uc11",
                        attribRelationToCards_Room_Floor_Inv_UseCase11.getName(), new IdAndDescriptionImpl(30L, "floor card 30"),
                        attribRelationToCards_SuperRoom_AnotherChild_Inv.getName(), new IdAndDescriptionImpl(80L, "anotherRoomBranch card card 80") // reference in second branch
                ));

        // ==== Use case 1_b:
        // one branch, with a N:1 Domain with direct Reference Attribute in parent Class, that connects to two different roots
        roomClasse_UseCase1_b = mockBuildClasse("Room", list(knownModelBaseClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription));
        attribRelationToCards_Room_Floor_UseCase1_b = mockBuildAttributeReference("referenceAttr_Room1_FloorN", "FloorRoom", RelationDirection.RD_DIRECT);
        floorClasse_UseCase1_b = mockBuildClasse("Floor", list(knownModelBaseClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription, attribRelationToCards_Room_Floor_UseCase1_b));
        aDomain_FloorRoom_UseCase1_b = mockBuildDomain_N1("FloorRoom", 15L, floorClasse_UseCase10, roomClasse_UseCase1_b);
        aCard_Room20_uc1_b = mockBuildCard(20L, roomClasse_UseCase1_b,
                map(
                        attribWOCode.getName(), "roomCard_20_uc1_b",
                        attribWODescription.getName(), "room card 20 uc1_b"
                ));
        // Attribute inverse for Domain N:1
        aCard_Floor30_uc1_b = mockBuildCard(30L, floorClasse_UseCase1_b,
                map(
                        attribWOCode.getName(), "floorCard_30_uc1_b",
                        attribWODescription.getName(), "floor card 30 uc1_b",
                        attribRelationToCards_Room_Floor_UseCase1_b.getName(), new IdAndDescriptionImpl(20L, "room card 20")
                ));
        // Second Card Floor.31 pointing to Room.20
        aCard_Floor31_uc1_b = mockBuildCard(31L, floorClasse_UseCase1_b,
                map(
                        attribWOCode.getName(), "floorCard_31_uc1_b",
                        attribWODescription.getName(), "floor card 31 uc1_b",
                        attribRelationToCards_Room_Floor_UseCase1_b.getName(), new IdAndDescriptionImpl(20L, "room card 20")
                ));

        // ==== Use case MissingRef:
        // In child Classe, missing the REFERENCE Attribute that points to the parent
        roomClasse_MissingRef = mockBuildClasse("Room", list(knownModelBaseClasse.getName()),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription/**
                 * , attribRelationToCards_Room_Floor_Inv_UseCase11
                 */
                ));
        aDomain_FloorRoom_MissingRef = mockBuildDomain_1N("FloorRoom", 17L, floorClasse, roomClasse_MissingRef);
        aCard_Room20_MissingRef = mockBuildCard(20L, roomClasse_MissingRef,
                map(
                        attribWOCode.getName(), "roomCard_20_uc11",
                        attribWODescription.getName(), "room card 20 uc11"/**,
                        attribRelationToCards_Room_Floor_Inv_UseCase11.getName(), new
                 * IdAndDescriptionImpl(30L, "floor card 30")
                 */
                // the missing REFERENCE
                ));
    }

    protected Classe mockBuild_NormalClasse(String classeName) {
        return mockBuildClasse(classeName, list(DomainTreeBuilder.MODEL_BASE_CLASSE_NAME),
                asList(attribWOId, attribWOType, attribWOCode, attribWODescription));
    }
}
