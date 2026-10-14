/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import static org.junit.Assert.assertEquals;

public class MyClass implements MyInterface {

    @Override
    public String myOverMethod() {
        return "OVER2";
    }

    @Override
    public String myMethod() {
        return "MET";
    }

    @Override
    public MyClass returnMet() {
        return this;
    }

    @Override
    public MyClass returnMetTwo(String value) {
        assertEquals(value, "something");
        return this;
    }

    @Override
    public String myMethodTwo(String value) {
        assertEquals(value, "whatever");
        return "METTWO";
    }

}
