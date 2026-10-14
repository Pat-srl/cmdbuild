/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import com.google.common.collect.MapDifference;
import java.util.Map;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.modeldiff.diff.JsonDiffHelper;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * Builder for deltas between:
 * <ul>
 * <li>{@link CmSchemaItemDataNode};
 * <li>{@link CmSchemaItemAttributesDataNode}.
 * </ul>
 *
 * @author afelice
 */
public class CmSchemaItemDeltaBuilder extends JsonDiffHelper {

    /**
     *
     * @param itemContainerName used to create a unique name for the attribute,
     * if this name is unique, as a {@link Classe} name.
     * @param attributeName
     * @param attributeSerialization
     * @return
     */
    public static CmSchemaItemAttributesDataNode buildSchemaItemAttributesDataNode_Unique(String itemContainerName,
            String attributeName, Map<String, Object> attributeSerialization) {
        return new CmSchemaItemAttributesDataNode(
                buildSchemaItemAttributesData_Unique(itemContainerName, attributeName, attributeSerialization)
        );
    }

    public static CmSchemaItemAttributesData buildSchemaItemAttributesData_Unique(String itemName, String attributeName, Map<String, Object> attributeSerialization) {
        return new CmSchemaItemAttributesData(
                "%s>>-->%s".formatted(itemName, attributeName), // A unique name
                attributeSerialization
        );
    }

    /**
     * Built while making <i>schema item</i> attributes diff.
     *
     * @param rightSchemaItemAttributesDataNode
     * @param leftSchemaItemAttributesDataNode
     * @param addedAttribValues
     * @return
     */
    public static CmSchemaItemAttributesDataChangeDelta buildChangeDelta_Added_Attrib(CmSchemaItemAttributesDataNode rightSchemaItemAttributesDataNode, CmSchemaItemAttributesDataNode leftSchemaItemAttributesDataNode, CmMapUtils.FluentMap<String, Object> addedAttribValues) {
        CmSchemaItemAttributesDataChangeDelta result = new CmSchemaItemAttributesDataChangeDelta(CmSchemaItemAttributesDataNode.class, rightSchemaItemAttributesDataNode.getDistinguishingName(), rightSchemaItemAttributesDataNode, leftSchemaItemAttributesDataNode, addedAttribValues);
        result.setAdded(addedAttribValues);

        return result;
    }

    /**
     * Built while making <i>schema item</i> attributes diff.
     *
     * @param rightSchemaItemAttributesDataNode
     * @param leftSchemaItemAttributesDataNode
     * @param removedAttribValues
     * @return
     */
    public static CmSchemaItemAttributesDataChangeDelta buildChangeDelta_Removed_Attrib(CmSchemaItemAttributesDataNode rightSchemaItemAttributesDataNode, CmSchemaItemAttributesDataNode leftSchemaItemAttributesDataNode, CmMapUtils.FluentMap<String, Object> removedAttribValues) {
        CmSchemaItemAttributesDataChangeDelta result = new CmSchemaItemAttributesDataChangeDelta(CmSchemaItemAttributesDataNode.class, rightSchemaItemAttributesDataNode.getDistinguishingName(), rightSchemaItemAttributesDataNode, leftSchemaItemAttributesDataNode, removedAttribValues);
        result.setRemoved(removedAttribValues);
        return result;
    }

    /**
     * Built while:
     * <ul>
     * <li>making {@link Card} attributes diff;
     * <li>rebuilding diff from <i>data diff</i> serialization, while merging.
     * </ul>
     * 
     * @param leftSchemaItemDataNode
     * @param rightSchemaItemDataNode
     * @param mapDifference
     * @return 
     */
    public static CmSchemaItemAttributesDataChangeDelta buildChangeDelta_Attrib(CmSchemaItemAttributesDataNode leftSchemaItemDataNode, CmSchemaItemAttributesDataNode rightSchemaItemDataNode, MapDifference<String, Object> mapDifference) {
        Map<String, MapDifference.ValueDifference<Object>> differingAttribValues = mapDifference.entriesDiffering();

        differingAttribValues = normalizeDiff_Schema(differingAttribValues);
        if (!differingAttribValues.isEmpty()) {
            // Changes in schema item attributes map
            CmMapUtils.FluentMap<String, Object> changedAttribValues = map(differingAttribValues).mapValues(v -> v.leftValue()); // left (changed) value
            CmSchemaItemAttributesDataChangeDelta changeDelta = new CmSchemaItemAttributesDataChangeDelta(CmSchemaItemAttributesDataNode.class, rightSchemaItemDataNode.getDistinguishingName(), rightSchemaItemDataNode, leftSchemaItemDataNode, changedAttribValues);
            changeDelta.setChanged(differingAttribValues);
            return changeDelta;
        }

        return null;
    }    

}
