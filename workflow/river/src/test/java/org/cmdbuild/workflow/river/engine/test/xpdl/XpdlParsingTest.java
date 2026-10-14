package org.cmdbuild.workflow.river.engine.test.xpdl;

import static com.google.common.base.Strings.emptyToNull;
import java.io.IOException;
import static java.lang.String.format;
import java.nio.charset.Charset;
import static java.util.stream.Collectors.joining;
import org.apache.commons.io.IOUtils;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.RiverTaskType;
import org.cmdbuild.workflow.river.engine.core.Step;
import org.cmdbuild.workflow.river.engine.utils.PlanToDotGraphPlotter;
import org.cmdbuild.workflow.river.engine.xpdl.XpdlParser;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XpdlParsingTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String requestForChangeXpdlContent,
            assetMgtXpdlContent,
            complexProcessOneXpdlContent,
            incidentMgtXpdlContent,
            processWithDataFieldInPackage;

    @Before
    public void init() throws IOException {
        requestForChangeXpdlContent = IOUtils.toString(getClass().getResourceAsStream("/RequestForChange.xpdl"), Charset.defaultCharset());
        assetMgtXpdlContent = IOUtils.toString(getClass().getResourceAsStream("/AssetMgt.xpdl"), Charset.defaultCharset());
        complexProcessOneXpdlContent = IOUtils.toString(getClass().getResourceAsStream("/ComplexProcessOne.xpdl"), Charset.defaultCharset());
        incidentMgtXpdlContent = IOUtils.toString(getClass().getResourceAsStream("/IncidentMgt.xpdl"), Charset.defaultCharset());
        processWithDataFieldInPackage = IOUtils.toString(getClass().getResourceAsStream("/ProcessForTest.xpdl"), Charset.defaultCharset());
    }

    @Test
    public void testXpdlParsingPackageDataFields() {
        RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(processWithDataFieldInPackage);
        assertNotNull(plan);
        assertTrue(plan.getDefaultValues().toString().contains("PackageFieldString"));
        assertTrue(plan.getGlobalVariables().entrySet().toString().contains("PackageFieldString"));
    }

    @Test
    public void testRequestForChangeParsing() {
        RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(requestForChangeXpdlContent);
        assertNotNull(plan);
        assertEquals("RequestForChange", plan.attributes().get("cmdbuildBindToClass"));
    }

    @Test
    public void testAssetMgtParsing() {
        RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(assetMgtXpdlContent);
        assertNotNull(plan);
        assertEquals("AssetMgt", plan.attributes().get("cmdbuildBindToClass"));
    }

    @Test
    public void testGestioneTickerParsing() {
        RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(readToString(getClass().getResourceAsStream("/GestioneTicket_44.xpdl")));
        assertNotNull(plan);
        assertEquals("GestioneTicket", plan.attributes().get("cmdbuildBindToClass"));
    }

    @Test
    public void testIncidentMgtParsing() {
        RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(incidentMgtXpdlContent);
        assertNotNull(plan);
        assertEquals("IncidentMgt", plan.attributes().get("cmdbuildBindToClass"));

        logger.debug("entry point steps = {}", plan.getEntryPointStepIds());
        assertFalse(plan.getEntryPointStepIds().contains("Process_incidentmgt_act1"));
        assertTrue(plan.getEntryPointStepIds().contains("IM02-HDOpening"));
    }

    @Test
    public void testBlockActivityProcParsing() { //test nested activity blocks
        RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(readToString(getClass().getResourceAsStream("/BlockActivityProc.xpdl")));
        assertNotNull(plan);
        assertEquals("BlockActivityProc", plan.attributes().get("cmdbuildBindToClass"));

        logger.debug("all steps = \n\n{}\n", plan.getSteps().stream().map(s -> format(" %-40s %s", s.getId(), s.getTask().getId())).sorted().collect(joining("\n")));

        assertThat(list(plan.getSteps()).map(Step::getId), hasItems("StepA", "BlockAct1_StepB", "BlockAct1_BlockAct2_StepC"));
    }

//    @Test
//    public void testAssetMgtNestedParsing() { //test nested activity blocks
//        RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(readToString(getClass().getResourceAsStream("/AssetMgt_nested.xpdl")));
//        assertNotNull(plan);
//        assertEquals("AssetMgt", plan.attributes().get("cmdbuildBindToClass"));
//
//        logger.debug("all steps = \n\n{}\n", plan.getSteps().stream().map(s -> format(" %-60s %s", s.getId(), s.getTask().getId())).sorted().collect(joining("\n")));
////		assertFalse(plan.getEntryPointStepIds().contains("Process_incidentmgt_act1"));
////		assertTrue(plan.getEntryPointStepIds().contains("IM02-HDOpening"));
//    }
    @Test
    public void testAssetMgtInlineFieldAttrParsing() {
        RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(assetMgtXpdlContent);
        assertNotNull(plan);
        Step step = plan.getStepById("SYS01-SetOpeningData");
        assertEquals(RiverTaskType.SCRIPT_INLINE, step.getTask().getTaskType());
    }

    @Test
    public void testComplexProcessOneParsing() {
        RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(complexProcessOneXpdlContent);
        assertNotNull(plan);
    }

    @Test
    public void testAssetMgtPlotting() {
        RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(assetMgtXpdlContent);
        assertNotNull(plan);

        String dotGraph = PlanToDotGraphPlotter.planToDotGraph(plan);
        assertNotNull(emptyToNull(dotGraph));

        logger.debug("dot graph = \n\n{}\n", dotGraph);
    }

    @Test
    public void testRequestForChangePlotting() {
        RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(requestForChangeXpdlContent);
        assertNotNull(plan);

        String dotGraph = PlanToDotGraphPlotter.planToDotGraph(plan);
        assertNotNull(emptyToNull(dotGraph));

        logger.debug("dot graph = \n\n{}\n", dotGraph);
    }

    @Test
    public void testComplexProcessOnePlotting() {
        RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(complexProcessOneXpdlContent);
        assertNotNull(plan);

        String dotGraph = PlanToDotGraphPlotter.planToDotGraph(plan);
        assertNotNull(emptyToNull(dotGraph));

        logger.debug("dot graph = \n\n{}\n", dotGraph);
    }

}
