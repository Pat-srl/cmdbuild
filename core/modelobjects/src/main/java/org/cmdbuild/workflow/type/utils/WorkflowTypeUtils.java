/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.type.utils;

import jakarta.annotation.Nullable;
import org.cmdbuild.workflow.type.LookupType;
import org.cmdbuild.workflow.type.ReferenceType;

public class WorkflowTypeUtils {

    public static boolean isNullOrEmpty(@Nullable ReferenceType referenceType) {
        return referenceType == null || referenceType.getId() <= 0;
    }

    public static ReferenceType emptyToNull(@Nullable ReferenceType referenceType) {
        return isNullOrEmpty(referenceType) ? null : referenceType;
    }

    public static boolean isNullOrEmpty(@Nullable LookupType lookupType) {
        return lookupType == null || lookupType.getId() <= 0;
    }

    public static LookupType emptyToNull(@Nullable LookupType lookupType) {
        return isNullOrEmpty(lookupType) ? null : lookupType;
    }

}
