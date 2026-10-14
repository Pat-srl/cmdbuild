/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.beans;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public interface LookupValue extends IdAndDescription {

    @Nullable
    String getLookupType();

    default boolean hasLookupType() {
        return isNotBlank(getLookupType());
    }

    default void checkLookupType(String type) {
        checkArgument(equal(getLookupType(), type), "invalid lookup type: expected =< %s > but found =< %s >", type, getLookupType());
    }
}
