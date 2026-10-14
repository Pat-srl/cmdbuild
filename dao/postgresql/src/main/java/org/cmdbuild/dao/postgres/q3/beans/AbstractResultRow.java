/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3.beans;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDDOMAIN;
import org.cmdbuild.dao.core.q3.ResultRow;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;

public abstract class AbstractResultRow implements ResultRow {

    @Override
    @Nullable
    public <T> T get(Attribute outputParameter) {
        Map<String, Object> map = asMap();
        checkArgument(map.containsKey(outputParameter.getName()), "output value not found for key = %s", outputParameter.getName());
        return (T) rawToSystem(outputParameter.getType(), map.get(outputParameter.getName()));
    }

    @Override
    public String toString() {
        return "ResultRow{Type=" + firstNotBlankOrNull(get(ATTR_IDCLASS), get(ATTR_IDDOMAIN)) + ",Id=" + get(ATTR_ID) + '}';
    }

}
