/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.base.Strings.emptyToNull;
import java.util.Map;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.modeldiff.core.CmSerializationHelper;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_CODE_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_ID_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_LOOKUP_VALUES_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_LOOKUP_VALUE_DEFAULT_ATTRIBUTE_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_LOOKUP_VALUE_PARENT_TYPE_ATTRIBUTE_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_NAME_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_PARENT_SERIALIZATION;
import org.cmdbuild.modeldiff.schema.SchemaLookupConfiguration;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

/**
 * <b>Concrete placeholder</b> for visitable (to engage polymorphism and choice
 * of visitor in related repository), wrap for a real <i>schema</i>
 * {@link LookupType} stuff, composed of {@link LookupValue} items.
 *
 * @author afelice
 */
public class CmLookupSchemaNode extends CmSchemaItemDataNode {

    /**
     * Computed because may be overwritten when calculating dependency order.
     */
    String parent;

    public CmLookupSchemaNode(CmSchemaItemAttributesData itemData) {
        super(itemData);

        this.parent = emptyToNull( // From UI can arrive "parent": "", see WsLookupType constructor
                toStringOrNull(getModelObj().getAttributesSerialization().get(ATTR_PARENT_SERIALIZATION))
        );
    }

    /**
     *
     * @return name of {@link LookupType}.
     */
    public String getTypeName() {
        return (String) (getModelObj().getAttributesSerialization().get(ATTR_NAME_SERIALIZATION));
    }

    /**
     *
     * @return name of parent; <code>null</code> if none.
     */
    public String getTypeParent() {
        return parent;
    }

    /**
     * Erase parent when calculating dependency order and this is an already
     * existing, untouched, {@link LookupType}: it don't concur to dependency
     * calculation.
     */
    public void eraseTypeParent() {
        this.parent = null;
    }

    public boolean isInherited() {
        return isBlank(getModelObj().getAttributesSerialization().get(ATTR_PARENT_SERIALIZATION));
    }

    /**
     * Used converting from {@link GeneratedDiffSchema_Lookup} to a
     * {@link CmLookupSchemaNode}.
     *
     * @param props
     * @return
     */
    static public String fetchName(Map<String, Object> props) {
        return (String) props.get(ATTR_NAME_SERIALIZATION);
    }

    /**
     * Used converting from {@link GeneratedDiffSchema_Lookup} to a
     * {@link CmLookupSchemaNode}.
     *
     * @param props
     * @return
     */
    static public String fetchCode(Map<String, Object> props) {
        return (String) props.get(ATTR_CODE_SERIALIZATION);
    }

    /**
     * Used converting from {@link GeneratedDiffSchema_Lookup} to a
     * {@link CmLookupSchemaNode}.
     *
     * @param props
     * @return
     */
    static public String fetchTypeParent(Map<String, Object> props) {
        return toStringOrNull(props.get(ATTR_PARENT_SERIALIZATION));
    }

    /**
     * Used converting from {@link GeneratedDiffSchema_Lookup} to a
     * {@link CmLookupSchemaNode}.
     *
     * @param props
     * @return
     */
    static public String fetchValueCode(Map<String, Object> props) {
        return (String) props.get(ATTR_CODE_SERIALIZATION);
    }

    /**
     * Used inserting/updating a {@link LookupValue}.
     *
     * @param props
     * @return
     */
    static public String fetchValueParentType(Map<String, Object> props) {
        return toStringOrNull(props.get(ATTR_LOOKUP_VALUE_PARENT_TYPE_ATTRIBUTE_SERIALIZATION));
    }

    /**
     * @param lookupType
     * @param objectMapper
     * @return
     */
    static public CmLookupSchemaNode buildLookupSchemaNode(SchemaLookupConfiguration lookupType, ObjectMapper objectMapper) {
        CmLookupSchemaNode result = new CmLookupSchemaNode(
                CmSchemaItemAttributesData.from(lookupType.getName(),
                        fetchTypeCmdbSerialization(lookupType, objectMapper) // LookupType properties
                )
        );

        lookupType.getValues()
                .forEach(valueConfiguration
                        -> {
                    Map<String, Object> lookupValueCmdbSerialization = getCmdbSerialization(valueConfiguration, objectMapper); // LookupValue properties
                    // Add synthesized name
                    lookupValueCmdbSerialization.put(ATTR_NAME_SERIALIZATION, fetchCode(lookupValueCmdbSerialization));
                    result.addComponent(
                            new CmSchemaItemAttributesDataNode(
                                    CmSchemaItemAttributesData.from(valueConfiguration.getCode(), lookupValueCmdbSerialization)
                            )
                    );
                });

        return result;
    }

    /**
     * Used in <b><i>compare</i> step</b> only.
     *
     * <ul>
     * <li>Remove <code>values</code> found in the {@link LookupType}
     * <i>CMDBuild serialization</i>, that represents the {@link LookupValue}s
     * but is not needed to make the <i>diff</i>, that is done on:
     * <ol>
     * <li>other {@link LookupType} <i>properties</i>;
     * <li>compound <i>attributes</i>, that are the list of {@link LookupValue}.
     * </ol>
     * <li>for {@link LookupValue}s:
     * <ol>
     * <li>removes the {@link CmSerializationHelper#ATTR_ID_SERIALIZATION},
     * because different CMDBuild system installations use different
     * <code>id</code>, so to skip in a <i>compare</i>;
     * <li>artificially adds <code>name</code>, to be used as <i>distinguishing
     * name</i>
     * while <i>compare</i> and then <i>apply diff</i>: <i>CMDBuild
     * serialization</code> for {@link LookupValue} contains a <code>code</code>
     * (and <code>_id</code>) but not a <code>name</code>.
     * </ul>
     *
     * @param genSchemaLookup
     * @return
     */
    static public CmLookupSchemaNode toLookupNode(SchemaLookupConfiguration genSchemaLookup) {
        // Lookup properties
        Map<String, Object> lookupTypeCmdbSerialization = fromJson(toJson(genSchemaLookup), MAP_OF_OBJECTS);
        lookupTypeCmdbSerialization.remove(ATTR_LOOKUP_VALUES_SERIALIZATION); // don't serialize again the LookupValues in Lookup properties, them will be compound nodes, see forward
        CmLookupSchemaNode lookupNode = new CmLookupSchemaNode(
                CmSchemaItemAttributesData.from(genSchemaLookup.getName(), lookupTypeCmdbSerialization)
        );

        // Values
        genSchemaLookup.getValues().forEach(
                genSchemaLookupValue -> {
                    Map<String, Object> lookupValueCmdbSerialization = removeId(fromJson(toJson(genSchemaLookupValue), MAP_OF_OBJECTS));
                    // Add synthesized name
                    lookupValueCmdbSerialization.put(ATTR_NAME_SERIALIZATION, fetchCode(lookupValueCmdbSerialization));
                    lookupNode.addComponent(
                            CmSchemaItemDeltaBuilder.buildSchemaItemAttributesDataNode_Unique(
                                    genSchemaLookup.getName(),
                                    genSchemaLookupValue.getCode(), lookupValueCmdbSerialization));
                }
        );

        return lookupNode;
    }

    /**
     * Gets the <code>id</code> from given
     * <code>origLookupValueCmdbSerialization</code>, the current system
     * {@link LookupValue} <i>CMDBuild serialization</i>.
     *
     * @param lookupValueCmdbSerialization <b>note</b>: modified by current
     * method.
     * @param origLookupValueCmdbSerialization
     */
    public static Long fixId(Map<String, Object> lookupValueCmdbSerialization, Map<String, Object> origLookupValueCmdbSerialization) {
        Long curOrigLookupValueId = (Long) origLookupValueCmdbSerialization.get(ATTR_ID_SERIALIZATION);
        lookupValueCmdbSerialization.put(ATTR_ID_SERIALIZATION, curOrigLookupValueId);

        return curOrigLookupValueId;
    }

    public static Map<String, Object> removeId(Map<String, Object> cmdbSerialization) {
        return map(cmdbSerialization).withoutKey(ATTR_ID_SERIALIZATION);
    }

    public static boolean isDefault(Map<String, Object> lookupValueCmdbSerialization) {
        return Boolean.TRUE.equals(ATTR_LOOKUP_VALUE_DEFAULT_ATTRIBUTE_SERIALIZATION);
    }

    public static void unsetDefault(Map<String, Object> lookupValueCmdbSerialization) {
        if (isDefault(lookupValueCmdbSerialization)) {
            lookupValueCmdbSerialization.put(ATTR_LOOKUP_VALUE_DEFAULT_ATTRIBUTE_SERIALIZATION, false);
        }
    }

    private static Map<String, Object> fetchTypeCmdbSerialization(SchemaLookupConfiguration lookupType, ObjectMapper objectMapper) {
        Map<String, Object> cmdbSerialization = getCmdbSerialization(lookupType, objectMapper);
        cmdbSerialization.remove(ATTR_LOOKUP_VALUES_SERIALIZATION);

        return cmdbSerialization;
    }

    private static Map<String, Object> getCmdbSerialization(Object obj, ObjectMapper objectMapper) {
        return objectMapper.convertValue(obj, new TypeReference<Map<String, Object>>() {
        });
    }

}
