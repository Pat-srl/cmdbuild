/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.fault;

import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public interface FaultMessage extends FaultEventBase {

    @Nullable
    String getCode();

    boolean showUser();

    default boolean hasCode() {
        return StringUtils.isNotBlank(getCode());
    }

}
