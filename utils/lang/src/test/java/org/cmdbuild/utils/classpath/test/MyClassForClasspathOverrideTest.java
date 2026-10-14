/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.classpath.test;

public class MyClassForClasspathOverrideTest {

    private final static MyClassThree INNER = new MyClassThree();

    public static String myTestMethod() {
        return "two";
    }

}
