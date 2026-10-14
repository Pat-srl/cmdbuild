/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.script.python.test;

import java.util.Map;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.utils.script.python.PythonScriptExecutor;
import org.cmdbuild.utils.script.python.PythonScriptExecutorImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class PythonScriptExecutionTest {

    @Test
    public void testPythonScript() {
        Map<String, Object> params = map("input", 3);
        PythonScriptExecutor pythonScriptExecutor = new PythonScriptExecutorImpl("output = input + 1");
        Map<String, Object> res = pythonScriptExecutor.execute(params);
        assertNotNull(res.get("output"));
        assertEquals(4, toInt(res.get("output")));
    }

    @Test
    public void testPythonScriptService1() {
        assertEquals(3, toInt(new PythonScriptExecutorImpl("output = input + 2").execute(map("input", 1)).get("output")));
    }

    @Test
    public void testPythonScriptService2() {
        assertEquals(5, toInt(new PythonScriptExecutorImpl("three = one + two; output = three").execute(map("one", 1, "two", 4)).get("output")));
    }

    @Test
    public void testPythonScriptService3() {
        String script = """
                        def employee_details(ID):
                            switcher = {
                                "1004": "Employee Name: Mario Rossi",
                                "1009": "Employee Name: Luigi Verdi",
                                "1010": "Employee Name: Matteo Neri",
                            }
                            '''The first argument will be returned if the match found and
                                nothing will be returned if no match found'''
                            return switcher.get(ID, "nothing")

                        # Take the employee ID
                        # Print the output
                        description = employee_details(input)
                        """;

        assertEquals("Employee Name: Mario Rossi", new PythonScriptExecutorImpl(script).execute(map("input", "1004")).get("description"));
        assertEquals("Employee Name: Luigi Verdi", new PythonScriptExecutorImpl(script).execute(map("input", "1009")).get("description"));
        assertEquals("Employee Name: Matteo Neri", new PythonScriptExecutorImpl(script).execute(map("input", "1010")).get("description"));

    }
}
