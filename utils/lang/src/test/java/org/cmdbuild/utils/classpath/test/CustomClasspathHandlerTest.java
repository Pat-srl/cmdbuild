/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.classpath.test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import org.cmdbuild.utils.classpath.ClasspathUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class CustomClasspathHandlerTest {

    @Test
    public void testClassOverride1() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        String value = MyClassForClasspathOverrideTest.myTestMethod();
        assertEquals("two", value);
        Class c1 = MyClassForClasspathOverrideTest.class;
        value = (String) c1.getMethod("myTestMethod").invoke(null);
        assertEquals("two", value);

        ClassLoader handler = ClasspathUtils.buildClassloaderWithJarOverride(list(new File("src/test/resources/org/cmdbuild/utils/classpath/test/my_override_jar.jar")), MyClassForClasspathOverrideTest.class.getClassLoader());
        Class c2 = handler.loadClass(MyClassForClasspathOverrideTest.class.getName());
        assertNotEquals(c1, c2);
        value = (String) c2.getMethod("myTestMethod").invoke(null);
        assertEquals("one", value);

        ClassLoader handler2 = ClasspathUtils.buildClassloaderWithJarOverride(list(new File("src/test/resources/org/cmdbuild/utils/classpath/test/my_override_jar.jar")), MyClassForClasspathOverrideTest.class.getClassLoader());
        Class c3 = handler2.loadClass(MyClassForClasspathOverrideTest.class.getName());
        assertNotEquals(c1, c3);
        assertNotEquals(c2, c3);
        value = (String) c3.getMethod("myTestMethod").invoke(null);
        assertEquals("one", value);

        Class c4 = handler.loadClass(MyClassForClasspathOverrideTest.class.getName());
        assertEquals(c2, c4);
        value = (String) c4.getMethod("myTestMethod").invoke(null);
        assertEquals("one", value);
    }

    @Test
    public void testClassOverride2() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, InstantiationException {
        ClassLoader handler = ClasspathUtils.buildClassloaderWithJarOverride(list(new File("src/test/resources/org/cmdbuild/utils/classpath/test/my_override_jar.jar")), MyClassForClasspathOverrideTest.class.getClassLoader());
        Class c2 = handler.loadClass("org.cmdbuild.utils.classpath.test.MyOtherClassForClasspathOverrideTest");
        Object instance = c2.getConstructor(String.class).newInstance("test");
        String value = (String) c2.getMethod("myTestMethod").invoke(instance);
        assertEquals("other test", value);
    }

    @Test
    public void testClassOverride3() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, InstantiationException {
        ClassLoader handler = ClasspathUtils.buildClassloaderWithJarOverride(list(new File("src/test/resources/org/cmdbuild/utils/classpath/test/my_other_jar.something")), MyClassForClasspathOverrideTest.class.getClassLoader());
        Class c2 = handler.loadClass("org.cmdbuild.utils.classpath.test.MyOtherClassForClasspathOverrideTest");
        Object instance = c2.getConstructor(String.class).newInstance("test");
        String value = (String) c2.getMethod("myTestMethod").invoke(instance);
        assertEquals("other test", value);
    }

}
