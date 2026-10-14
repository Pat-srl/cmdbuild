/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import static java.util.Collections.emptyList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import static java.util.function.Function.identity;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import org.cmdbuild.modeldiff.core.CmSerializationHelper;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_ATTRIBUTE_TYPE_FORMULA_CODE_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_ATTRIBUTE_TYPE_FORMULA_TYPE_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_ATTRIBUTE_VIRTUAL_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_CLASSE_DEFAULT_ORDER_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_INHERITED_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_TYPE_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.VALUE_ATTRIBUTE_TYPE_FORMULA_SCRIPT_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.VALUE_ATTRIBUTE_TYPE_FORMULA_SERIALIZATION;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * Common methods on <i>properties</i> and {@link Attribute}s.
 *
 * @author afelice
 */
public class CmSchemaHelper {

    /**
     * Serialization of <i>attribute type</i> <code>LOOKUP</code> or
     * <code>LOOKUPARRAY</code>.
     *
     * <p>
     * As {@link AttributeTypeName} is serialized in {@link AttributeTypeConversionService#serializeAttributeType(org.cmdbuild.dao.entrytype.Attribute)
     * }.
     */
    public static final Set<String> LOOKUP_TYPES = CmCollectionUtils.set(AttributeTypeConversionService.serializeAttributeType(AttributeTypeName.LOOKUP), AttributeTypeConversionService.serializeAttributeType(AttributeTypeName.LOOKUPARRAY));

    /**
     * Serialization of <i>attribute type</i> <code>FOREIGNKEY</code>.
     *
     * <p>
     * As {@link AttributeTypeName} is serialized in {@link AttributeTypeConversionService#serializeAttributeType(org.cmdbuild.dao.entrytype.Attribute)
     * }.
     */
    public static final String FOREIGN_KEY_TYPE = AttributeTypeConversionService.serializeAttributeType(AttributeTypeName.FOREIGNKEY);

    /**
     * Serialization of <i>attribute type</i> <code>REFERENCE</code>.
     *
     * <p>
     * As {@link AttributeTypeName} is serialized in {@link AttributeTypeConversionService#serializeAttributeType(org.cmdbuild.dao.entrytype.Attribute)
     * }.
     */
    public static final String REFERENCE_TYPE = AttributeTypeConversionService.serializeAttributeType(AttributeTypeName.REFERENCE);

    /**
     * Serialization of <i>attribute type</i> <code>REFERENCEARRAY</code>.
     *
     * <p>
     * As {@link AttributeTypeName} is serialized in {@link AttributeTypeConversionService#serializeAttributeType(org.cmdbuild.dao.entrytype.Attribute)
     * }.
     */
    public static final String REFERENCE_ARRAY_TYPE = AttributeTypeConversionService.serializeAttributeType(AttributeTypeName.REFERENCEARRAY);

    /**
     * Serialization of <i>attribute type</i> <code>REFERENCE</code> or
     * <code>REFERENCEARRAY</code>.
     *
     * <p>
     * As {@link AttributeTypeName} is serialized in {@link AttributeTypeConversionService#serializeAttributeType(org.cmdbuild.dao.entrytype.Attribute)
     * }.
     */
    public static final Set<String> REFERENCE_TYPES = CmCollectionUtils.set(REFERENCE_TYPE, REFERENCE_ARRAY_TYPE);

    /**
     * Serialization of <i>attribute type</i> <code>REFERENCE</code>,
     * <code>REFERENCEARRAY</code> or <code>FOREIGNKEY</code>.
     *
     * <p>
     * As {@link AttributeTypeName} is serialized in {@link AttributeTypeConversionService#serializeAttributeType(org.cmdbuild.dao.entrytype.Attribute)
     * }.
     */
    public static final Set<String> RELATION_TYPE_NAMES = CmCollectionUtils.set(REFERENCE_TYPE, REFERENCE_ARRAY_TYPE, FOREIGN_KEY_TYPE);

    public static final EnumSet<AttributeTypeName> RELATION_TYPES = EnumSet.of(AttributeTypeName.FOREIGNKEY, AttributeTypeName.REFERENCE, AttributeTypeName.REFERENCEARRAY);

    public static boolean isActive(Map<String, Object> itemAttribCmdbSerialization) {
        return Boolean.TRUE.equals(itemAttribCmdbSerialization.get(CmSerializationHelper.ATTR_ACTIVE_SERIALIZATION));
    }

    public static String getReferenceTarget(Map<String, Object> props) {
        return (String) props.get(CmSerializationHelper.ATTR_REFERENCED_CLASSE_SERIALIZATION);
    }

    public static boolean hasDefaultOrder(Map<String, Object> classeCmdbSerialization) {
        return fetchDefaultOrder(classeCmdbSerialization) != null;
    }

    public static boolean hasDefaultOrder(GeneratedDiffSchema_ChangedItemProps classeChangedProps) {
        return !fetchDefaultOrder(classeChangedProps).isEmpty(); // if not changed property, returns null
    }

    public static List fetchDefaultOrder(Map<String, Object> classeCmdbSerialization) {
        return (List) classeCmdbSerialization.get(ATTR_CLASSE_DEFAULT_ORDER_SERIALIZATION);
    }

    public static List fetchDefaultOrder(GeneratedDiffSchema_ChangedItemProps classeChangedProps) {
        Optional<GeneratedDiffSchema_ChangedAttributeValue> result = classeChangedProps.changedProps.stream().filter(p -> p.attribName.equals(ATTR_CLASSE_DEFAULT_ORDER_SERIALIZATION)).findFirst();
        if (result.isPresent()) {
            return (List) result.get().newValue;
        }

        return emptyList();
    }

    public static boolean isInherited(CmSchemaItemAttributesDataNode itemAttribNode) {
        return isInherited(itemAttribNode.getModelObj().getAttributesSerialization());
    }

    public static boolean isInherited(Map<String, Object> itemAttribCmdbSerialization) {
        return Boolean.TRUE.equals(itemAttribCmdbSerialization.get(ATTR_INHERITED_SERIALIZATION));
    }

    /**
     *
     * @param itemAttribNode
     * @return <code>true</code> if a virtual {@Attribute}; works even if a
     * <code>FORMULA</code> {@link Attribute}, without
     * {@link #ATTR_ATTRIBUTE_VIRTUAL_SERIALIZATION} attribute.
     */
    public static boolean isVirtualAttribute(CmSchemaItemAttributesDataNode itemAttribNode) {
        return isVirtualAttribute(itemAttribNode.getModelObj().getAttributesSerialization());
    }

    public static boolean isVirtualAttribute(Map<String, Object> itemAttribCmdbSerialization) {
        return Boolean.TRUE.equals(itemAttribCmdbSerialization.get(ATTR_ATTRIBUTE_VIRTUAL_SERIALIZATION)) || isFormulaAttribute(itemAttribCmdbSerialization);
    }

    public static boolean isFormulaAttribute(Map<String, Object> itemAttribCmdbSerialization) {
        return VALUE_ATTRIBUTE_TYPE_FORMULA_SERIALIZATION.equals(itemAttribCmdbSerialization.get(ATTR_TYPE_SERIALIZATION));
    }

    public static boolean isGroovyScriptFormulaAttribute(Map<String, Object> itemAttribCmdbSerialization) {
        return isFormulaAttribute(itemAttribCmdbSerialization)
                && VALUE_ATTRIBUTE_TYPE_FORMULA_SCRIPT_SERIALIZATION.equals(itemAttribCmdbSerialization.get(ATTR_ATTRIBUTE_TYPE_FORMULA_TYPE_SERIALIZATION));
    }

    public static String fetchGroovyScriptFormula(Map<String, Object> itemAttribCmdbSerialization) {
        return (String) itemAttribCmdbSerialization.get(ATTR_ATTRIBUTE_TYPE_FORMULA_CODE_SERIALIZATION);
    }

    public static String getReferenceDomain(Map<String, Object> props) {
        return (String) props.get(CmSerializationHelper.ATTR_REFERENCE_DOMAIN_SERIALIZATION);
    }

    public static Map<String, Object> toDeactivated(Map<String, Object> itemAttribCmdbSerialization) {
        return map(itemAttribCmdbSerialization).with(CmSerializationHelper.ATTR_ACTIVE_SERIALIZATION, false);
    }

    public static boolean isRelationAttribute(Map<String, Object> attribCmdbSerialization) {
        String attributeTypeStr = getAttributeType(attribCmdbSerialization);
        return RELATION_TYPE_NAMES.contains(attributeTypeStr);
    }

    public static String getName(Map<String, Object> props) {
        return (String) props.get(CmSerializationHelper.ATTR_NAME_SERIALIZATION);
    }

    public static String getAttributeType(Map<String, Object> props) {
        return (String) props.get(CmSerializationHelper.ATTR_TYPE_SERIALIZATION);
    }

    public static String getReferencedType(CmSchemaItemAttributesDataNode attribDataNode) {
        return getReferencedType(attribDataNode.getModelObj().getAttributesSerialization());
    }

    public static String getReferencedType(Map<String, Object> props) {
        return (String) props.get(CmSerializationHelper.ATTR_REFERENCED_CLASSE_SERIALIZATION);
    }

    public static String getDomain(Map<String, Object> props) {
        return (String) props.get(CmSerializationHelper.ATTR_REFERENCE_DOMAIN_SERIALIZATION);
    }

    public static boolean isReferenceIn(Map<String, Object> attribCmdbSerialization, Set<String> domainNames) {
        String attributeTypeStr = getAttributeType(attribCmdbSerialization);
        return REFERENCE_TYPES.contains(attributeTypeStr) && domainNames.contains(getDomain(attribCmdbSerialization));
    }

    public static boolean isReferenceTo(CmSchemaItemAttributesDataNode attribDataNode, Set<String> referredClasseNames) {
        Map<String, Object> attribCmdbSerialization = attribDataNode.getModelObj().getAttributesSerialization();
        String attributeTypeStr = getAttributeType(attribCmdbSerialization);
        return REFERENCE_TYPES.contains(attributeTypeStr) && referredClasseNames.contains(getReferencedType(attribCmdbSerialization));
    }

    public static boolean isForeignKeyTo(CmSchemaItemAttributesDataNode attribDataNode, Set<String> referredClasseNames) {
        Map<String, Object> attribCmdbSerialization = attribDataNode.getModelObj().getAttributesSerialization();
        String attributeTypeStr = getAttributeType(attribCmdbSerialization);
        return FOREIGN_KEY_TYPE.contains(attributeTypeStr) && referredClasseNames.contains(getReferencedType(attribCmdbSerialization));
    }

    public static boolean isForeignKeyAttribute(CmSchemaItemAttributesDataNode attribDataNode) {
        return isForeignKeyAttribute(attribDataNode.getModelObj().getAttributesSerialization());
    }

    public static boolean isForeignKeyAttribute(Map<String, Object> attribCmdbSerialization) {
        String attributeTypeStr = getAttributeType(attribCmdbSerialization);
        return FOREIGN_KEY_TYPE.equals(attributeTypeStr);
    }

    public static boolean isInverseReferenceAttribute(Map<String, Object> attribCmdbSerialization) {
        return isReferenceAttribute(attribCmdbSerialization) && isInverseReference(attribCmdbSerialization);
    }

    public static boolean isInverseReferenceAttribute(CmSchemaItemAttributesDataNode attribDataNode) {
        Map<String, Object> attribCmdbSerialization = attribDataNode.getModelObj().getAttributesSerialization();
        return isInverseReferenceAttribute(attribCmdbSerialization);
    }

    public static boolean isDirectReferenceAttribute(Map<String, Object> attribCmdbSerialization) {
        return isReferenceAttribute(attribCmdbSerialization) && isDirectReference(attribCmdbSerialization);
    }

    public static boolean isDirectReferenceAttribute(CmSchemaItemAttributesDataNode attribDataNode) {
        Map<String, Object> attribCmdbSerialization = attribDataNode.getModelObj().getAttributesSerialization();
        return isDirectReferenceAttribute(attribCmdbSerialization);
    }

    public static boolean isRelationAttribute(Attribute attrib) {
        return RELATION_TYPES.contains(attrib.getType().getName());
    }

    public static boolean isRelationAttribute(CmSchemaItemAttributesDataNode attribDataNode) {
        Map<String, Object> attribCmdbSerialization = attribDataNode.getModelObj().getAttributesSerialization();
        return isRelationAttribute(attribCmdbSerialization);
    }

    public static boolean isReferenceAttribute(Attribute attrib) {
        return attrib.getType().getName() == AttributeTypeName.REFERENCE;
    }

    public static boolean isReferenceAttribute(CmSchemaItemAttributesDataNode attribDataNode) {
        Map<String, Object> attribCmdbSerialization = attribDataNode.getModelObj().getAttributesSerialization();
        return isReferenceAttribute(attribCmdbSerialization);
    }

    public static boolean isReferenceAttribute(Map<String, Object> attribCmdbSerialization) {
        String attributeTypeStr = getAttributeType(attribCmdbSerialization);
        return REFERENCE_TYPE.equals(attributeTypeStr) || REFERENCE_ARRAY_TYPE.equals(attributeTypeStr);
    }

    /**
     * Used before making <i>topological sort</i> while inserting
     * {@link Classe}s.
     *
     * @param cmClasseSchemaNode
     * @return
     */
    public static List<CmSchemaItemAttributesDataNode> getAllInverseReferences(CmClasseSchemaNode cmClasseSchemaNode) {
        return cmClasseSchemaNode.getComponents().stream().filter(attribDataNode -> isInverseReferenceAttribute(attribDataNode)).collect(toList());
    }

    /**
     * Used before making <i>topological sort</i> while inserting
     * {@link Classe}s.
     *
     * @param cmClasseSchemaNode
     * @return
     */
    public static List<CmSchemaItemAttributesDataNode> getAllDirectReferences(CmClasseSchemaNode cmClasseSchemaNode) {
        return cmClasseSchemaNode.getComponents().stream().filter(attribDataNode -> isDirectReferenceAttribute(attribDataNode)).collect(toList());
    }

    public static boolean isLookup(CmSchemaItemAttributesDataNode attribDataNode, Set<String> lookupTypeNames) {
        Map<String, Object> attribCmdbSerialization = attribDataNode.getModelObj().getAttributesSerialization();
        String attributeTypeStr = getAttributeType(attribCmdbSerialization);
        return LOOKUP_TYPES.contains(attributeTypeStr) && lookupTypeNames.contains(getLookupType(attribCmdbSerialization));
    }

    /**
     * Used before making <i>topological sort</i> while inserting
     * {@link Classe}s.
     *
     * @param cmClasseSchemaNode
     * @return
     */
    public static List<CmSchemaItemAttributesDataNode> getForeignKeys(CmClasseSchemaNode cmClasseSchemaNode) {
        return cmClasseSchemaNode.getComponents().stream().filter(attribDataNode -> isForeignKeyAttribute(attribDataNode)).collect(toList());
    }

    public static boolean isLookup(Map<String, Object> attribCmdbSerialization) {
        String attributeTypeStr = getAttributeType(attribCmdbSerialization);
        return LOOKUP_TYPES.contains(attributeTypeStr);
    }

    public static String getLookupType(Map<String, Object> props) {
        return (String) props.get(CmSerializationHelper.ATTR_LOOKUP_ATTRIBUTE_SERIALIZATION);
    }

    private static boolean isDirectReference(Map<String, Object> props) {
        return CmSerializationHelper.ATTR_REFERENCE_DIRECTION_DIRECT_SERIALIZATION.equals(getReferencedDirection(props));
    }

    private static String getReferencedDirection(Map<String, Object> props) {
        return (String) props.get(CmSerializationHelper.ATTR_REFERENCE_DIRECTION_SERIALIZATION);
    }

    private static boolean isInverseReference(Map<String, Object> props) {
        return CmSerializationHelper.ATTR_REFERENCE_DIRECTION_INVERSE_SERIALIZATION.equals(getReferencedDirection(props));
    }

    public static <T> Map<String, T> toMapByName(Stream<T> stream, Function<? super T, String> nameExtractor) {
        return stream.collect(Collectors.toMap(
                nameExtractor,
                identity()
        ));
    }

}
