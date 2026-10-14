/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.temp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import static com.google.common.base.Objects.equal;
import jakarta.annotation.Nullable;
import org.cmdbuild.common.CmContentInfo;

import static org.cmdbuild.temp.TempInfoSource.TS_SECURE;

public interface TempInfo extends CmContentInfo {

    @Nullable
    Long getSize();

    String getContentType();

    @Nullable
    String getFileName();

    TempInfoSource getSource();

    @Nullable
    Long getTimeToLive();

    @JsonIgnore
    default boolean isSourceSecure() {
        return equal(getSource(), TS_SECURE);
    }

}
