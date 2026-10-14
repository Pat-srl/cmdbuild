/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io.test;

import static org.cmdbuild.utils.exec.CmProcessUtils.executeBashScript;
import static org.cmdbuild.utils.exec.CmProcessUtils.executeProcess;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CmProcessUtilsTest {

    @Test
    public void testProcessExec() {
        String res = executeProcess("/bin/bash", "-c", "echo -n ciao");
        assertEquals("ciao", res);
    }

    @Test
    public void testBashExec() {
        String res = executeBashScript("echo -n ciao");
        assertEquals("ciao", res);
    }

}
