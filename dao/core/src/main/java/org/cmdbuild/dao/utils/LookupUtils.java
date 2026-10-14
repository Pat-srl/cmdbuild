/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.utils;

import static com.google.common.base.Preconditions.checkArgument;
import jakarta.annotation.Nullable;
import org.cmdbuild.common.beans.LookupValue;

public class LookupUtils {

    public static LookupValue checkLookupNotNull(@Nullable LookupValue value) {
        checkArgument(value != null && value.hasIdOrCode());
        return value;
    }

}
