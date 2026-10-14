package org.cmdbuild.utils.lang;

import static org.cmdbuild.utils.lang.CmReflectionUtils.executeMethod;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionUtilsTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testReflectionUtils1() throws Exception {
        MyClass bean = new MyClass();
        assertEquals("MET", executeMethod(bean, "myMethod"));
        assertEquals("METTWO", executeMethod(bean, "myMethodTwo", "whatever"));
        assertEquals("DEF", executeMethod(bean, "myDefMethod"));
        assertEquals("OVER2", executeMethod(bean, "myOverMethod"));
        assertEquals(bean, executeMethod(bean, "returnMet"));
        assertEquals(bean, executeMethod(bean, "returnMetTwo", "something"));
    }

    @Test
    public void testExecmethodWithArgs() {
        MyClassWithMethods myClassWithMethods = new MyClassWithMethods();
        assertTrue(executeMethod(myClassWithMethods, "myMethodWithOneArg", (Object) null));
    }

    public static class MyBeanOne {

    }

    public static interface MyBeanTwo {

    }

    public static class MyBeanThree implements MyBeanTwo {

    }

    public static class MyClassWithMethods {

        public boolean myMethodWithOneArg(String arg) {
            return true;
        }

    }
}
