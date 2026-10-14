package org.cmdbuild.utils.script.groovy.test;

import static java.util.Collections.emptyMap;
import org.cmdbuild.utils.classpath.SingleClassLoader;
import static org.cmdbuild.utils.classpath.ClasspathUtils.doWithCustomClassLoader;
import static org.cmdbuild.utils.lang.CmReflectionUtils.executeMethod;
import static org.cmdbuild.utils.lang.CmReflectionUtils.newInstance;
import static org.cmdbuild.utils.script.CmScriptUtils.SCRIPT_OUTPUT_VAR;
import org.cmdbuild.utils.script.groovy.GroovyScriptExecutorImpl;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CustomClassloaderTest {

    @Test
    public void testClassloaderFromComponents1() throws ClassNotFoundException {

        Class groovyClass = new GroovyScriptExecutorImpl("org.cmdbuild.customclassloader.test.MyClass", """
                        def myFun(text){ 
                            return "hello ${text}".toString();
                        }
            """, null).getGroovyClass();

        Object instance = newInstance(groovyClass);
        assertEquals("hello something", executeMethod(instance, "myFun", "something"));

        SingleClassLoader classLoader = new SingleClassLoader(groovyClass, null);

        groovyClass = classLoader.loadClass("org.cmdbuild.customclassloader.test.MyClass");

        instance = newInstance(groovyClass);

        assertEquals("hello something", executeMethod(instance, "myFun", "something"));

        doWithCustomClassLoader(classLoader, () -> {
//            Class classe = Class.forName("org.cmdbuild.customclassloader.test.MyClass");
            Class classe = Thread.currentThread().getContextClassLoader().loadClass("org.cmdbuild.customclassloader.test.MyClass");
            Object thisInstance = newInstance(classe);
            assertEquals("hello something", executeMethod(thisInstance, "myFun", "something"));
        });
    }

    @Test
    public void testClassloaderFromComponents2() throws ClassNotFoundException {

        Class groovyClass = new GroovyScriptExecutorImpl("org.cmdbuild.customclassloader.test.MyClass", """
                        def myFun(text){ 
                            return "hello ${text}".toString();
                        }
            """, null).getGroovyClass();

        SingleClassLoader classLoader = new SingleClassLoader(groovyClass, null);

        doWithCustomClassLoader(classLoader, () -> {
            assertEquals("hello something", new GroovyScriptExecutorImpl("org.cmdbuild.customclassloader.test.TestClass", """                                                                     
                        return (new org.cmdbuild.customclassloader.test.MyClass()).myFun("something")                                                                     
                                     """, classLoader).execute(emptyMap()).get(SCRIPT_OUTPUT_VAR));
        });
    }

    @Test
    public void testClassloaderFromComponents3() throws ClassNotFoundException {

        Class groovyClass = new GroovyScriptExecutorImpl("org.cmdbuild.customclassloader.test.MyClass", """
                        def myFun(text){ 
                            return "hello ${text}".toString();
                        }
            """, null).getGroovyClass();

        SingleClassLoader classLoader = new SingleClassLoader(groovyClass, null);

        assertEquals("hello something", new GroovyScriptExecutorImpl("org.cmdbuild.customclassloader.test.TestClass", """                                                                     
                        return (new org.cmdbuild.customclassloader.test.MyClass()).myFun("something")                                                                     
                                     """, classLoader).execute(emptyMap()).get(SCRIPT_OUTPUT_VAR));
    }

}
