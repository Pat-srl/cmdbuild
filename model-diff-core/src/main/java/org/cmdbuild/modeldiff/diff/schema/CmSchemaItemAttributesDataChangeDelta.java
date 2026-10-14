/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import com.google.common.collect.MapDifference;
import java.util.Map;
import org.cmdbuild.modeldiff.diff.patch.AbstractCmDelta;
import org.cmdbuild.modeldiff.diff.patch.CmChangeDelta;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * This specialization is needed to handle diff between
 * {@link CmSchemaItemAttributesDataNode}.
 *
 * @author afelice
 */
public class CmSchemaItemAttributesDataChangeDelta extends CmChangeDelta<CmSchemaItemAttributesDataNode, CmSchemaItemAttributesData> {

    private final FluentMap<String, Object> changedAattributesSerialization;
    private Map<String, MapDifference.ValueDifference<Object>> differingAttribValues = map();
    private Map<String, Object> addedAttribValues = map();
    private Map<String, Object> removedAttribValues = map();

    public CmSchemaItemAttributesDataChangeDelta(Class modelNodeClass, String distinguishingName,
            CmSchemaItemAttributesDataNode sourceItemAttributesDataNode, CmSchemaItemAttributesDataNode targetSchemaItemAttributesDataNode,
            FluentMap<String, Object> changedAttributesSerialization) {
        super(modelNodeClass, distinguishingName, sourceItemAttributesDataNode, targetSchemaItemAttributesDataNode);

        this.changedAattributesSerialization = changedAttributesSerialization;
    }

    public FluentMap<String, Object> getChangedAttributesSerialization() {
        return changedAattributesSerialization;
    }

    public void addAll(CmSchemaItemAttributesDataChangeDelta otherChanges) {
        changedAattributesSerialization.with(otherChanges.getChangedAttributesSerialization());
        differingAttribValues.putAll(otherChanges.differingAttribValues);
        addedAttribValues.putAll(otherChanges.addedAttribValues);
        removedAttribValues.putAll(otherChanges.removedAttribValues);
    }

    public AbstractCmDelta setChanged(Map<String, MapDifference.ValueDifference<Object>> differingAttribValues) {
        this.differingAttribValues = map(differingAttribValues);
        return this;
    }

    public Map<String, MapDifference.ValueDifference<Object>> getChanged() {
        return this.differingAttribValues;
    }

    public AbstractCmDelta setAdded(Map<String, Object> addedAttribValues) {
        this.addedAttribValues = map(addedAttribValues);
        return this;
    }

    public Map<String, Object> getAdded() {
        return this.addedAttribValues;
    }

    public AbstractCmDelta setRemoved(Map<String, Object> removedAttribValues) {
        this.removedAttribValues = map(removedAttribValues);
        return this;
    }

    public Map<String, Object> getRemoved() {
        return this.removedAttribValues;
    }

    /**
     * Used whan a change delta was emptied because programmatically equivalent 
     * data contained (such as <code>null</code> VS <code>"null"</code> due to 
     * <code>NullNode</code> in some Json content).
     * @return 
     */
    @Override
    public boolean isSomethingChanged() {
        return !(addedAttribValues.isEmpty() && removedAttribValues.isEmpty() && differingAttribValues.isEmpty());
    }

}
