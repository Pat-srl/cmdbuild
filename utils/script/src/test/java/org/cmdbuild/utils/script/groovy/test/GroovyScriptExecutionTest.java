/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.script.groovy.test;

import static java.util.Collections.emptyMap;
import java.util.Map;
import org.cmdbuild.utils.classpath.MoreClasspathUtils;
import org.cmdbuild.utils.script.groovy.GroovyScriptExecutor;
import org.cmdbuild.utils.script.groovy.GroovyScriptExecutorImpl;
import org.cmdbuild.utils.script.groovy.SimpleGroovyScriptServiceImpl;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.script.CmScriptUtils.SCRIPT_OUTPUT_VAR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class GroovyScriptExecutionTest {

    @Test
    public void testGroovyScript() {
        Map<String, Object> params = map("input", 3);
        GroovyScriptExecutor groovyScriptExecutor = new GroovyScriptExecutorImpl("GroovyScriptDd6ypehc80l0zhp5s8m81rfh", "output = input + 1", null);
        Map<String, Object> res = groovyScriptExecutor.execute(params);
        assertNotNull(res.get("output"));
        assertEquals(4, toInt(res.get("output")));
    }

    @Test
    public void testGroovyScriptService1() {
        assertEquals(3, toInt(new SimpleGroovyScriptServiceImpl().executeScript("output = input + 2", map("input", 1)).get("output")));
    }

    @Test
    public void testGroovyScriptService2() {
        assertEquals(5, toInt(new SimpleGroovyScriptServiceImpl().executeScript("def three = one + two; output = three", map("one", 1, "two", 4)).get("output")));
    }

    @Test
    public void testGroovyScriptService3() {
        Map<String, Object> out = new SimpleGroovyScriptServiceImpl().getScriptExecutor("MyScript", "def localVar = 1; myVar = 1; gClassName = getClass().getName();").execute();

        assertEquals(null, out.get("localVar"));
        assertEquals(1, out.get("myVar"));
        assertEquals("org.cmdbuild.utils.groovy.script.MyScript", out.get("gClassName"));

        out = new SimpleGroovyScriptServiceImpl().getScriptExecutor("MyScript", "aVar = 2; gClassName = getClass().getName();").execute();

        assertEquals(null, out.get("localVar"));
        assertEquals(null, out.get("myVar"));
        assertEquals(2, out.get("aVar"));
        assertEquals("org.cmdbuild.utils.groovy.script.MyScript", out.get("gClassName"));

        out = new SimpleGroovyScriptServiceImpl().getScriptExecutor("org.cmdbuild.test.MyScript", "aVar = 3; gClassName = getClass().getName();").execute();

        assertEquals(null, out.get("localVar"));
        assertEquals(null, out.get("myVar"));
        assertEquals(3, out.get("aVar"));
        assertEquals("org.cmdbuild.test.MyScript", out.get("gClassName"));
    }

    @Test
    public void testGroovyReservedKeyword1() {
        assertEquals("myValue_", new SimpleGroovyScriptServiceImpl().getScriptExecutor("MyScript", "return \"${Categgory}_\"").execute(map("Categgory", "myValue")).get(SCRIPT_OUTPUT_VAR));
//        assertEquals("myValue_", new SimpleGroovyScriptServiceImpl().getScriptExecutor("MyScript", "return \"${getVariable('Category')}_\"").execute(map("Category", "myValue")).get(SCRIPT_OUTPUT_VAR));

        assertEquals("myValue_", new SimpleGroovyScriptServiceImpl().getScriptExecutor("MyScript", "def Category = 'myValue'; return \"${Category}_\"").execute().get(SCRIPT_OUTPUT_VAR));
//        assertEquals("myValue_", new SimpleGroovyScriptServiceImpl().getScriptExecutor("MyScript", "return \"${binding.variables.Category}_\"").execute(map("Category", "myValue")).get(SCRIPT_OUTPUT_VAR));
//        assertEquals("myValue_", new SimpleGroovyScriptServiceImpl().getScriptExecutor("MyScript", "def Category = binding.variables.Category; return \"${Category}_\"").execute(map("Category", "myValue")).get(SCRIPT_OUTPUT_VAR));
        assertEquals("myValue_", new SimpleGroovyScriptServiceImpl().getScriptExecutor("MyScript", "return \"${Category}_\"").execute(map("Category", "myValue")).get(SCRIPT_OUTPUT_VAR));
    }

    @Test
    public void testGroovyReservedKeyword2() {
        assertEquals("myValue_", new SimpleGroovyScriptServiceImpl().getScriptExecutor("MyScript", "return \"${Numbber}_\"").execute(map("Numbber", "myValue")).get(SCRIPT_OUTPUT_VAR));
        assertEquals("myValue_", new SimpleGroovyScriptServiceImpl().getScriptExecutor("MyScript", "def Number = 'myValue'; return \"${Number}_\"").execute().get(SCRIPT_OUTPUT_VAR));
        assertEquals("myValue_", new SimpleGroovyScriptServiceImpl().getScriptExecutor("MyScript", "return \"${Number}_\"").execute(map("Number", "myValue")).get(SCRIPT_OUTPUT_VAR));
    }

    @Test
    public void testGroovyScriptService4() {
        assertEquals("ONE_TWO_THREE", new SimpleGroovyScriptServiceImpl().getScriptExecutor("\"ONE_${due}_${nested.tre}\"").execute(map("due", "TWO", "nested", map("tre", "THREE"))).get("output"));
    }

    @Test(expected = Exception.class)
    public void testGroovyScriptWithCustomClasspath1() {
        assertEquals("ciao", toStringNotBlank(new SimpleGroovyScriptServiceImpl().executeScript("output = (new org.cmdbuild.utils.groovy.test.MyClass()).getMessage()", emptyMap()).get("output")));
    }

    @Test
    public void testGroovyScriptWithCustomClasspath2() {
        assertEquals("ciao", toStringNotBlank(new SimpleGroovyScriptServiceImpl().executeScript("output = (new org.cmdbuild.utils.groovy.test.MyClass()).getMessage()",
                MoreClasspathUtils.buildClassloaderWithJarOverride(getClass().getResourceAsStream("/org/cmdbuild/utils/groovy/test/custom_classpath_test.jar")),
                emptyMap()).get("output")));
    }

    @Test
    public void testGroovyScriptWithCustomClasspath3() {
        assertEquals("ciao", toStringNotBlank(new SimpleGroovyScriptServiceImpl().executeScript("import org.cmdbuild.utils.groovy.test.MyClass; output = new MyClass().getMessage()",
                MoreClasspathUtils.buildClassloaderWithJarOverride(getClass().getResourceAsStream("/org/cmdbuild/utils/groovy/test/custom_classpath_test.jar")),
                emptyMap()).get("output")));
    }

    @Test
    public void testGroovyScriptWithImport() {
        assertEquals("WITH IMPORT", toStringNotBlank(new SimpleGroovyScriptServiceImpl().executeScript("import org.cmdbuild.utils.groovy.test.beans.MyTestBean\n"
                + "import static org.cmdbuild.utils.groovy.test.beans.MyTestBean.getMyFirstString\n"
                + "output = getMyFirstString() + ' ' + new MyTestBean().getMySecondString()",
                emptyMap()).get("output")));
    }
}
