package org.cmdbuild.workflow.test;

import static junit.framework.Assert.assertEquals;
import org.cmdbuild.workflow.model.PlanInfo;
import org.cmdbuild.workflow.model.PlanInfoImpl;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
public class PlanIdTest {

    @Test
    public void testWithVersionZero() {
        PlanInfo info = PlanInfoImpl.deserialize("package#0#definition");

        assertEquals("package", info.getPackageId());
        assertEquals("0", info.getVersion());
        assertEquals("definition", info.getDefinitionId());
        assertEquals("package#0#definition", info.getPlanId());
    }

    @Test
    public void testWithVersionNum() {
        PlanInfo info = PlanInfoImpl.deserialize("package#12#definition");

        assertEquals("package", info.getPackageId());
        assertEquals("12", info.getVersion());
        assertEquals("definition", info.getDefinitionId());
        assertEquals("package#12#definition", info.getPlanId());
    }

}
