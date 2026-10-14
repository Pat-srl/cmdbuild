/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser.model;

import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.List;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;

public interface DxfGenericObject extends DxfObject {

    String getType();

    List<DxfValue> getValues();

    default List<DxfValue> getValues(int groupCode) {
        return getValues().stream().filter(v -> v.getGroupCode() == groupCode).collect(toImmutableList());
    }

    default DxfValue getValue(int groupCode) {
        return getValues().stream().filter(v -> v.getGroupCode() == groupCode).collect(onlyElement("value not found for group code = %s", groupCode));
    }

    default DxfValue getFirstValue(int groupCode) {
        return getValues().stream().filter(v -> v.getGroupCode() == groupCode).limit(1).collect(onlyElement("value not found for group code = %s", groupCode));
    }
}
