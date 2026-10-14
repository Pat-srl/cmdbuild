/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

public interface MyInterface {

    String myMethod();

    String myMethodTwo(String value);

    default String myDefMethod() {
        return "DEF";
    }

    default String myOverMethod() {
        return "OVER";
    }

    MyInterface returnMet();

    MyInterface returnMetTwo(String value);

}
