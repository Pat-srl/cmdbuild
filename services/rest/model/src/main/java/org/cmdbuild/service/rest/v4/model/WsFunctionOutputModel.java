/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ldare
 */
public class WsFunctionOutputModel {

    private final List<WsFunctionOutputParameter> output;

    public WsFunctionOutputModel(@JsonProperty("output") List<WsFunctionOutputParameter> output) {
        this.output = checkNotNull(output);
    }

    @Override
    public String toString() {
        return "WsFunctionOutputModel{" + "output=" + output + '}';
    }

    public List<WsFunctionOutputParameter> getOutput() {
        return this.output;
    }

}
