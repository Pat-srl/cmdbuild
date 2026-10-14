/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ifc.inner;

import com.google.common.base.Preconditions;
import com.google.common.collect.Streams;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.annotation.Nullable;
import org.apache.commons.jxpath.JXPathContext;
import org.bimserver.emf.IdEObject;
import org.cmdbuild.utils.ifc.IfcEntry;
import org.cmdbuild.utils.ifc.IfcFeature;
import org.cmdbuild.utils.ifc.utils.IfcUtils;
import static org.cmdbuild.utils.ifc.utils.IfcUtils.buildJXPathContext;
import org.eclipse.emf.ecore.EStructuralFeature;

public class IfcEntryImpl implements IfcEntry {

    private final IdEObject inner;

    public IfcEntryImpl(IdEObject inner) {
        this.inner = Preconditions.checkNotNull(inner);
    }

    @Override
    public IdEObject getInner() {
        return inner;
    }

    @Nullable
    @Override
    public Object getValue(String name) {
        return switch (name) {
            case "_id" ->
                getId(); //TODO improve this
            case "_type" ->
                getType(); //TODO improve this
            default -> {
                EStructuralFeature feature = Preconditions.checkNotNull(inner.eClass().getEStructuralFeature(name), "feature not found for name =< %s >", name);
                Object value = inner.eGet(feature);
                value = convertEValue(value);
                yield value;
            }
        };
    }

    @Nullable
    private Object convertEValue(@Nullable Object value) {
        if (value == null) {
            return value;
        } else if (value instanceof Iterable iterable) {
            return Streams.stream(iterable).map(this::convertEValue).collect(Collectors.toList());
        } else if (value instanceof IdEObject idEObject) {
            return new IfcEntryImpl(idEObject);
        } else if (value instanceof org.eclipse.emf.common.util.Enumerator enumerator) {
            return enumerator.getLiteral();
        } else {
            return value;
        }
    }

    @Override
    public Map<String, IfcFeature> getFeatures() {
        return IfcUtils.getFeatures(inner.eClass());
    }

    public boolean hasFeature(String name) {
        return inner.eClass().getEStructuralFeature(name) != null;
    }

    @Override
    public String toString() {
        return "IfcEntry{" + "id=" + getId() + ", type=" + getType() + (hasFeature("Name") && getString("Name") != null ? (", name=" + getString("Name")) : "") + '}';
    }

    @Override
    public JXPathContext xpath() {
        return buildJXPathContext(this);
    }

}
