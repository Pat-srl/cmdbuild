/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.temp;

import static com.google.common.base.Preconditions.checkArgument;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;

public interface TempData {

    final String TEMP_DATA_ATTR_USER = "User";

    @Nullable
    Long getId();

    String getTempId();

    byte[] getData();

    boolean isComposite();

    TempInfo getInfo();

    Long getTimeToLive();

    default CompositionInfo getCompositionInfo() {
        checkArgument(isComposite());
        return fromJson(getData(), CompositionInfoImpl.class);
    }
}
