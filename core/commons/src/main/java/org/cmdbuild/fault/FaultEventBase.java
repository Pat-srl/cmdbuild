/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.fault;

import com.fasterxml.jackson.annotation.JsonIgnore;
import static java.lang.String.format;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.fault.FaultLevel.FL_WARNING;
import static org.cmdbuild.fault.FaultLevel.FL_ERROR;

public interface FaultEventBase {

    FaultLevel getLevel();

    String getMessage();

    @JsonIgnore
    default String getMessageAndLevel() {
        return format("%s: %s", serializeEnum(getLevel()).toUpperCase(), getMessage());
    }

    default boolean hasLevel(FaultLevel threshold) {
        return getLevel().getIndex() <= threshold.getIndex();
    }

    @JsonIgnore
    default boolean isError() {
        return hasLevel(FL_ERROR);
    }

    @JsonIgnore
    default boolean isWarning() {
        return hasLevel(FL_WARNING);
    }

}
