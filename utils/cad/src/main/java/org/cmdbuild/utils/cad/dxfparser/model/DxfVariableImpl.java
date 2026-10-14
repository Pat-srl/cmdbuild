/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser.model;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableMap;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class DxfVariableImpl implements DxfVariable {

    private final String key;
    private final Map<Integer, DxfValue> values;

    public DxfVariableImpl(String key) {
        this(key, emptyList());
    }

    public DxfVariableImpl(String key, Iterable<DxfValue> values) {
        this.key = checkNotBlank(key);
        this.values = map(values, DxfValue::getGroupCode);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Map<Integer, DxfValue> getValues() {
        return unmodifiableMap(values);
    }

    public void addValue(DxfValueImpl value) {
        checkArgument(values.put(value.getGroupCode(), value) == null, "duplicate value received for groupCode = %s", value.getGroupCode());
    }

    @Override
    public String toString() {
        return "DxfVariable{" + "key=" + key + ", values=" + list(values.values()) + '}';
    }

}
