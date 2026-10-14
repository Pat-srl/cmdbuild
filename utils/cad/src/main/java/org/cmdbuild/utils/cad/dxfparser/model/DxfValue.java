/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser.model;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public interface DxfValue {

    String getValue();

    int getGroupCode();

    default int getValueAsInt() {
        return parseInt(getValue());
    }

    default double getValueAsDouble() {
        return parseDouble(getValue());
    }
}
