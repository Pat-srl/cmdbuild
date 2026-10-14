/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import java.util.Map;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.modeldiff.core.CmSerializationHelper;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_LOOKUP_VALUES_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_NAME_SERIALIZATION;
import static org.cmdbuild.modeldiff.diff.schema.CmLookupSchemaNode.fetchCode;
import org.cmdbuild.modeldiff.schema.DmsCategoryTypeConfiguration;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;

/**
 * <b>Concrete placeholder</b> for visitable (to engage polymorphism and choice
 * of visitor in related repository), wrap for a real <i>schema</i>
 * <i>dms category</i> (a special type of {@link LookupType}) stuff, composed of
 * value items.
 *
 * @author afelice
 */
public class CmDmsCategoryTypeSchemaNode extends CmLookupSchemaNode {

    public CmDmsCategoryTypeSchemaNode(CmSchemaItemAttributesData itemData) {
        super(itemData);
    }

    // @todo AFE TBC
//    /**
//     * Used in <i>compare</i>.
//     *
//     * @param origLookupSchemaNode
//     * @return
//     */
//    public static CmDmsCategoryTypeSchemaNode buildDmsCategorySchemaNode(CmLookupSchemaNode origLookupSchemaNode) {
//        CmDmsCategoryTypeSchemaNode result = new CmDmsCategoryTypeSchemaNode(origLookupSchemaNode.itemData);
//        result.parent = origLookupSchemaNode.parent;
//        origLookupSchemaNode.getComponents().forEach(c -> {
//            result.addComponent(c);
//        });
//
//        return result;
//    }
    /**
     * Used in <b><i>compare</i> step</b> only.
     *
     * <ul>
     * <li>Remove <code>values</code> found in the <i>dms category type</i> (a
     * special type of {@link LookupType}) <i>CMDBuild serialization</i>, that
     * represents the {@link LookupValue}s but is not needed to make the
     * <i>diff</i>, that is done on:
     * <ol>
     * <li>other {@link LookupType} <i>properties</i>;
     * <li>compound <i>attributes</i>, that are the list of {@link LookupValue}.
     * </ol>
     * <li>for {@link LookupValue}s:
     * <ol>
     * <li>removes the {@link CmSerializationHelper#ATTR_ID_SERIALIZATION},
     * because different CMDBuild system installations use different
     * <code>id</code>, so to skip in <i>diff step</i>;
     * <li>artificially adds <code>name</code>, to be used as <i>distinguishing
     * name</i>
     * while <i>compare</i> and then <i>apply diff</i>: <i>CMDBuild
     * serialization</i> for {@link LookupValue} contains a <code>code</code>
     * (and <code>_id</code>) but not a <code>name</code>.
     * </ol>
     * </ul>
     *
     * @param genDmsCategoryType
     * @return
     */
    public static CmDmsCategoryTypeSchemaNode buildDmsCategoryTypeNode(DmsCategoryTypeConfiguration genDmsCategoryType) {
        // Lookup properties
        Map<String, Object> dmsCategoryTypeCmdbSerialization = fromJson(toJson(genDmsCategoryType), MAP_OF_OBJECTS);
        dmsCategoryTypeCmdbSerialization.remove(ATTR_LOOKUP_VALUES_SERIALIZATION); // don't serialize again the LookupValues in Lookup properties, them will be compound nodes, see forward
        CmDmsCategoryTypeSchemaNode dmsCategoryNode = new CmDmsCategoryTypeSchemaNode(
                CmSchemaItemAttributesData.from(genDmsCategoryType.getName(), dmsCategoryTypeCmdbSerialization)
        );

        // Values
        genDmsCategoryType.getValues().forEach(
                genDmsCategoryValue -> {
                    Map<String, Object> dmsCategoryValueCmdbSerialization = removeId(fromJson(toJson(genDmsCategoryValue), MAP_OF_OBJECTS));
                    // Remove Id (not comparable between systems)
                    dmsCategoryValueCmdbSerialization = removeId(dmsCategoryValueCmdbSerialization);
                    // Add synthesized name
                    dmsCategoryValueCmdbSerialization.put(ATTR_NAME_SERIALIZATION, fetchCode(dmsCategoryValueCmdbSerialization));
                    dmsCategoryNode.addComponent(
                            CmSchemaItemDeltaBuilder.buildSchemaItemAttributesDataNode_Unique(
                                    genDmsCategoryType.getName(),
                                    genDmsCategoryValue.getCode(), dmsCategoryValueCmdbSerialization));
                }
        );

        return dmsCategoryNode;
    }

}
