/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.utils;

import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

public interface PagingInfo {

    long getOffset();

    @Nullable
    Long getLimit();

    default long getLimitOrMaxValue() {
        return firstNotNull(getLimit(), Long.MAX_VALUE);
    }
}
