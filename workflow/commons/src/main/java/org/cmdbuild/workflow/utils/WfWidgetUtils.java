/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.utils;

import java.util.Optional;
import jakarta.annotation.Nullable;
import org.cmdbuild.workflow.type.LookupType;
import org.cmdbuild.workflow.type.ReferenceType;
import static org.cmdbuild.workflow.type.utils.WorkflowTypeUtils.emptyToNull;

public class WfWidgetUtils {

    @Nullable
    public static Object convertValueForWidget(@Nullable Object value) {//note: this is also used by flow fluent api
        if (value == null) {
            return null;
        } else if (value instanceof LookupType) {
            LookupType lookupType = LookupType.class.cast(value);
            return Optional.ofNullable(emptyToNull(lookupType)).map(LookupType::getId).orElse(null);
        } else if (value instanceof ReferenceType) {
            ReferenceType refeference = ReferenceType.class.cast(value);
            return Optional.ofNullable(emptyToNull(refeference)).map(ReferenceType::getId).orElse(null);
        } else {
            return value;
        }
    }

}
