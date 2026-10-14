/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.test.xpdl;

import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import static org.cmdbuild.workflow.river.engine.utils.FlowGraphUtils.getAllParallelStepIdsConvergingOnThisStep;
import org.cmdbuild.workflow.river.engine.xpdl.XpdlParser;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class FlowGraphUtilsTest {

    private final static String RFC = readToString(FlowGraphUtilsTest.class.getResourceAsStream("/rfc.xpdl"));

    @Test
    public void testGetAllParallelStepsConvergingOnThisStep() {
        RiverPlan plan = XpdlParser.parseXpdlWithDefaultOptions(RFC);

        assertEquals(set("RegisterRFC", "SYS010", "FormalEvaluation"), getAllParallelStepIdsConvergingOnThisStep(plan, "SYS020"));

        assertEquals(set("RegisterRFC", "SYS010", "FormalEvaluation", "check_SYS020", "SYS020", "Process_requestforchange_act4", "Process_requestforchange_act7",
                "Process_requestforchange_act6", "Process_requestforchange_act5", "check_Process_requestforchange_act7",
                "check_Process_requestforchange_act6", "check_Process_requestforchange_act5", "RFCRiskAnalysis", "RFCCostAnalysis", "Process_requestforchange_act10",
                "Process_requestforchange_act9", "RFCImpactAnalysis", "Process_requestforchange_act3", "SYS030.3", "SYS030.2", "SYS030.1"),
                getAllParallelStepIdsConvergingOnThisStep(plan, "Process_requestforchange_act8"));
    }

}
