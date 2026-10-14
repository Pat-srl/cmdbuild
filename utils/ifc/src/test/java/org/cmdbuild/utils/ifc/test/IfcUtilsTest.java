/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.ifc.test;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.MoreCollectors.onlyElement;
import static java.lang.String.format;
import org.apache.commons.beanutils.LazyDynaMap;
import org.apache.commons.jxpath.JXPathContext;
import org.bimserver.models.ifc2x3tc1.IfcOrganization;
import org.bimserver.models.ifc4.IfcApplication;
import org.cmdbuild.utils.ifc.IfcEntry;
import org.cmdbuild.utils.ifc.IfcModel;
import org.cmdbuild.utils.ifc.IfcModelEntriesReport;
import static org.cmdbuild.utils.ifc.utils.IfcUtils.emptyModel;
import static org.cmdbuild.utils.ifc.utils.IfcUtils.loadIfc;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.lazyMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import org.cmdbuild.utils.testutils.IgnoreSlowTestRule;
import org.cmdbuild.utils.testutils.Slow;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IfcUtilsTest {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Rule
    public IgnoreSlowTestRule rule = new IgnoreSlowTestRule();

    @Test
    @Slow
    public void testIfcProcessing1() {
        LOGGER.info("testIfcProcessing1");

        //act:
        IfcModel ifc = loadIfc(getClass().getResourceAsStream("/ifc2x3_example.ifc"));
        IfcOrganization ifcOrganization = ifc.getModel().getAll(IfcOrganization.class).stream().filter(o -> isNotBlank(o.getName())).collect(onlyElement());
        IfcEntry ifcOrganizationEntry = ifc.getEntries("IfcOrganization").stream().filter(e -> isNotBlank(e.getString("Name"))).collect(onlyElement());
        IfcModelEntriesReport report = ifc.getReport();
        IfcEntry ifcBuilding = getOnlyElement(ifc.getEntries("IfcBuilding"));

        //assert:
        assertEquals(176810, ifc.getModel().getValues().size());
        assertEquals(16, ifcOrganization.getExpressId());
        assertEquals("Nemetschek AG", ifcOrganization.getName());
        assertEquals("Nemetschek AG", ifcOrganizationEntry.getString("Name"));
        assertEquals(176810, report.getCount());
        assertEquals(1, report.getEntries().get("IfcOrganization").getCount());
        assertEquals("FZK-Haus", ifcBuilding.getString("Name"));
        assertEquals("FZK-Haus", ifcBuilding.queryValue("Name"));
        assertEquals(ifcBuilding.getId(), ifc.queryEntry("IfcBuilding").getId());
        assertEquals("FZK-Haus", ifc.queryValue("IfcBuilding/Name"));
        assertEquals("217973", ifc.queryEntry("IfcSpace[GlobalId='3CgvnK_5f8AxbTWHpnRdyg']").getString("_id"));
        assertEquals("217973", ifc.queryString("IfcSpace[GlobalId='3CgvnK_5f8AxbTWHpnRdyg']/_id"));
        assertEquals("3CgvnK_5f8AxbTWHpnRdyg", ifc.queryEntry("IfcSpace[_id='217973']").getString("GlobalId"));
        assertEquals("3CgvnK_5f8AxbTWHpnRdyg", ifc.queryValue("IfcSpace[_id='217973']/GlobalId"));
    }

    @Test
    @Slow
    public void testIfcProcessing2() {
        LOGGER.info("testIfcProcessing2");

        //act:
        IfcModel ifc = loadIfc(getClass().getResourceAsStream("/ifc4_example.ifc"));
        IfcApplication ifcApplication = getOnlyElement(ifc.getModel().getAll(IfcApplication.class));
        IfcEntry ifcApplicationEntry = getOnlyElement(ifc.getEntries("IfcApplication"));
        IfcModelEntriesReport report = ifc.getReport();
        IfcEntry ifcBuilding = getOnlyElement(ifc.getEntries("IfcBuilding"));

        LOGGER.info("ifc building = \n\n{}\n", mapToLoggableString(map(
                "expressId", ifcBuilding.getInner().getExpressId(),
                "oid", ifcBuilding.getInner().getOid(),
                "pid", ifcBuilding.getInner().getPid(),
                "rid", ifcBuilding.getInner().getRid()
        )));

        //assert:
        assertEquals(44249, ifc.getModel().getValues().size());
        assertEquals(11, ifcApplication.getExpressId());
        assertEquals("ARCHICAD-64", ifcApplication.getApplicationFullName());
        assertEquals("ARCHICAD-64", ifcApplicationEntry.getString("ApplicationFullName"));
        assertEquals(44249, report.getCount());
        assertEquals(2, report.getEntries().get("IfcOrganization").getCount());
        assertEquals("FZK-Haus", ifcBuilding.getString("Name"));
        assertEquals("FZK-Haus", ifcBuilding.queryValue("Name"));
        assertEquals(ifcBuilding.getId(), ifc.queryEntry("IfcBuilding").getId());
        assertEquals("FZK-Haus", ifc.queryValue("IfcBuilding/Name"));
        assertEquals("7", ifc.queryEntry("IfcSpace[GlobalId='2dQFggKBb1fOc1CqZDIDlx']").getString("Name"));
        assertEquals("7", ifc.queryValue("IfcSpace[GlobalId='2dQFggKBb1fOc1CqZDIDlx']/Name"));
        assertEquals("2dQFggKBb1fOc1CqZDIDlx", ifc.queryEntry("IfcSpace[Name='7']").getString("GlobalId"));
        assertEquals("2dQFggKBb1fOc1CqZDIDlx", ifc.queryValue("IfcSpace[Name='7']/GlobalId"));
    }

    @Test
    @Slow
    public void testIfcProcessing3() {
        LOGGER.info("testIfcProcessing3");

        //act:
        IfcModel ifc = loadIfc(getClass().getResourceAsStream("/ifc_compressed_example.ifczip"));
        IfcEntry ifcBuilding = ifc.queryEntry("IfcBuilding");

        //assert:
        assertEquals(27481, ifc.getModel().getValues().size());
        assertEquals(27481, ifc.getReport().getCount());
        assertEquals("Building", ifcBuilding.getString("Name"));
        assertEquals("Building", ifcBuilding.queryValue("Name"));
        assertEquals("Building", ifc.queryValue("IfcBuilding/Name"));
    }

    @Test
    public void testIfcModel() {
        LOGGER.info("testIfcModel");

        //act:
        IfcModel ifc = emptyModel();

        //assert:
        assertEquals(set("Description", "ReferencesElements", "BuildingAddress", "ReferencedBy", "IsDefinedBy", "Decomposes", "Representation", "IsDecomposedBy", "OwnerHistory", "CompositionType", "HasAssignments", "GlobalId", "Name", "ElevationOfRefHeightAsString", "ObjectType", "HasAssociations", "LongName", "ServicedBySystems", "ContainsElements", "ObjectPlacement", "geometry", "ElevationOfTerrainAsString", "ElevationOfTerrain", "ElevationOfRefHeight"),
                ifc.getFeatures("IfcBuilding").keySet());
        assertEquals(817, ifc.getClasses().size());
        assertEquals(0, ifc.getAvailableClasses().size());
    }

    @Test
    @Ignore //TODO
    public void testSurface() {
        IfcModel ifc = loadIfc(getClass().getResourceAsStream("/1679-P0-M3-FM-MAIN_MODEL-AAS-181016.ifc"));
        IfcEntry room = ifc.queryEntry("IfcSpace[Name=146]");
//        IsDefinedBy/RelatingPropertyDefinition[Name='Dimensions']/HasProperties[Name='Area']/NominalValue/wrappedValue
//        ifc.getModel(). TODO
    }

    @Test
    public void testXpath() {
        LOGGER.info("testXpath");

        //act:
        JXPathContext xpath = JXPathContext.newContext(new LazyDynaMap(lazyMap(() -> map(
                "test", "value",
                "IfcOrganization", new LazyDynaMap(lazyMap(() -> map(
                "myAttr", "myValue"
        ))) {
            {
                setReturnNull(true);
                setRestricted(true);
            }
        }))) {
            {
                setReturnNull(true);
                setRestricted(true);
            }
        });

        //assert:
        assertEquals("myValue", getOnlyElement(list(xpath.iterate("//IfcOrganization/myAttr"))));
        assertEquals("myValue", getOnlyElement(xpath.selectNodes("//IfcOrganization/myAttr")));
        assertEquals("value", getOnlyElement(xpath.selectNodes("//test")));
        assertEquals("myValue", getOnlyElement(xpath.selectNodes("/IfcOrganization/myAttr")));
        assertEquals("myValue", getOnlyElement(xpath.selectNodes("IfcOrganization/myAttr")));
        assertEquals("myValue", getOnlyElement(xpath.selectNodes("/IfcOrganization[myAttr='myValue']/myAttr")));
    }

    @Test
    public void testLoadIfcandQueryEntry() {
        LOGGER.info("testLoadIfcandQueryEntry");

        //arrange:
        String spaceName = "761001", featureName = "761001-MEB-001";

        //act:
        IfcModel ifc = loadIfc(getClass().getResourceAsStream("/76.ifc"));

        IfcEntry space = ifc.queryEntry(format("/IfcSpace[@Name='%s']", spaceName));
        LOGGER.info("space = {}", space);
        LOGGER.info("contains = {}", space.getValue("ContainsElements"));
        LOGGER.info("contains = {}", space.getEntry("ContainsElements").getValue("RelatedElements"));

        IfcEntry element2 = ifc.queryEntry(format("/IfcFurnishingElement[@Name='%s']", featureName));
        LOGGER.info("element = {}", element2);
        LOGGER.info("contained = {}", element2.getValue("ContainedInStructure"));

        //assert:
        assertEquals(1, element2.getList("ContainedInStructure").size());
        assertEquals("761001", ifc.queryString("/IfcSpace[@Name='761001']/@Name"));
        assertEquals("3Lj$Q4NoXD0Q6oFcGdsHTI", ifc.queryString("/IfcSpace[@Name='761001']/@GlobalId"));
        assertEquals("761001-MEB-001", ifc.queryString("/IfcSpace[@Name='761001']/ContainsElements/RelatedElements[@Name='761001-MEB-001']/@Name"));
        assertEquals("3kCq1QLU1CkR2ZE1KJW931", ifc.queryString("/IfcSpace[@Name='761001']/ContainsElements/RelatedElements[@Name='761001-MEB-001']/@GlobalId"));
        assertEquals("761001-MEB-001", ifc.queryString("/IfcFurnishingElement[@Name='761001-MEB-001']/@Name"));
        assertEquals("3kCq1QLU1CkR2ZE1KJW931", ifc.queryString("/IfcFurnishingElement[@Name='761001-MEB-001']/@GlobalId"));
        assertEquals("761001", ifc.queryString("/IfcFurnishingElement[@Name='761001-MEB-001']/ContainedInStructure/RelatingStructure/@Name"));
        assertEquals("3Lj$Q4NoXD0Q6oFcGdsHTI", ifc.queryString("/IfcFurnishingElement[@Name='761001-MEB-001']/ContainedInStructure/RelatingStructure/@GlobalId"));
    }
}
