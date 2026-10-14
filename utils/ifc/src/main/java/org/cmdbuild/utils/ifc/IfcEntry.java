/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ifc;

import com.google.common.collect.Iterables;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.bimserver.emf.IdEObject;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.isPrimitiveOrWrapper;

public interface IfcEntry extends XpathQuery {

    final String IFC_ATTR_GLOBAL_ID = "GlobalId";

    IdEObject getInner();

    @Nullable
    Object getValue(String name);

    Map<String, IfcFeature> getFeatures();

    @Nullable
    default String getString(String name) {
        return CmStringUtils.toStringOrNull(getValue(name));
    }

    @Nullable
    default IfcEntry getEntry(String name) {
        Object value = getValue(name);
        if (value == null) {
            return null;
        } else if (value instanceof Iterable && (Iterables.size((Iterable) value)) == 1) {
            return (IfcEntry) getOnlyElement((Iterable) value);
        } else {
            return (IfcEntry) value;
        }
    }

    default List<IfcEntry> getList(String name) {
        return (List) getValue(name);
    }

    default Map<String, Object> asMap() {
        return (Map) map("_id", getId(), "_type", getType()).accept(m -> getFeatures().keySet().forEach(k -> m.put(k, getValue(k))));
    }

    default Map<String, Object> asMapOfPrimitives() {
        return map(asMap()).withoutValues(v -> !isPrimitiveOrWrapper(v));
    }

    default String getType() {
        return getInner().eClass().getName();
    }

    default long getId() {
        return getInner().getExpressId();
    }

    @Nullable
    default String getGlobalId() {
        return getString(IFC_ATTR_GLOBAL_ID);
    }
}
