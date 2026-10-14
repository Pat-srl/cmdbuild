/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.orm.test.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonBeanImpl implements JsonBeanInterface {

    private final String value;

    public JsonBeanImpl(@JsonProperty("value") String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

}
