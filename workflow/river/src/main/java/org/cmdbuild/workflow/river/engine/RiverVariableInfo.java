/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

public interface RiverVariableInfo<T extends Serializable> {

    Optional<T> getDefaultValue();

    String getKey();

    boolean isBasicType();

    Class<T> getJavaType();

    Map<String, String> getAttributes();

}
