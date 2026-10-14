/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser;

import static java.lang.String.format;
import static org.cmdbuild.utils.lang.CmConvertUtils.toDouble;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;

public interface DxfStreamEvent {

    int getLineNumber();

    int getGroupCode();

    String getValue();

    default String getGroupCodeDashValue() {
        return format("%s-%s", getGroupCode(), getValue().trim());
    }

    default double getValueAsDouble() {
        return toDouble(getValue());
    }

    default int getValueAsInt() {
        return toInt(getValue());
    }

    default boolean getValueAsBoolean() {
        return getValueAsInt() == 0;
    }
}
