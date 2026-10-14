/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import java.util.Map;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_DESCRIPTION_SERIALIZATION;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * Represents:
 * <ul>
 * <li>{@link Attribute} in {@link Classe} data;
 * <li>{@link Attribute} in {@link Process} data;
 * <li>values in {@link Domain} data;
 * <li>values in {@link LookupType} data;
 * </ul>
 *
 * @author afelice
 */
public class CmSchemaItemAttributesData extends CmSchemaItemData {

    private final String description;

    private final Map<String, Object> attributesSerialization;

    /**
     * <i>Json properties</i> values.
     */
    private final Map<String, Object> origJsonValues;

    /**
     * Used while deserializing <i>other</i> found data.
     *
     * <p>
     * See
     * {@link AttributeTypeConversionService.AttrSerializerHelper#serializeAttributeType()},
     * the <code>name</code> value is used for <code>name</code> and
     * <code>id></code> serialization key.
     *
     * @param itemAttributeName
     * @param attributeSerialization
     */
    public CmSchemaItemAttributesData(String itemAttributeName, Map<String, Object> attributeSerialization) {
        this(itemAttributeName, attributeSerialization, map());
    }

    /**
     * Used while deserializing <i>other</i> found data.
     *
     * <p>
     * See
     * {@link AttributeTypeConversionService.AttrSerializerHelper#serializeAttributeType()},
     * the <code>name</code> value is used for <code>name</code> and
     * <code>id></code> serialization key.
     *
     * @param itemAttributeName
     * @param attributeSerialization
     * @param origSerializationJsonValues values in attribute serialization that
     * were recognized as <i>Json content</i> and treated differently while in
     * <i>diff</i>:
     * <ol>
     * <li><b>stringified</b> for comparison;
     * <li>reverted back to <i>Json content</i> for <i>diff serialization</i>.
     * </ol>
     */
    public CmSchemaItemAttributesData(String itemAttributeName,
            Map<String, Object> attributeSerialization, Map<String, Object> origSerializationJsonValues) {
        super(itemAttributeName);

        this.attributesSerialization = map(attributeSerialization);
        this.origJsonValues = origSerializationJsonValues; 

        // As in CardImpl constructor
        this.description = get(ATTR_DESCRIPTION_SERIALIZATION, String.class);
    }

//    /**
//     * Copy constructor
//     *
//     * @param origData
//     * @param newAttributeSerialization
//     */
//    public CmSchemaItemAttributesData(CmSchemaItemAttributesData origData, Map<String, Object> newAttributeSerialization) {
//        this(origData.getName(), newAttributeSerialization, origData.origJsonValues);
//    }    
    
    /**
     * Copy constructor, but with specified attributes.
     *
     * @param origData
     * @param newAttributeSerialization
     */
    public CmSchemaItemAttributesData(CmSchemaItemAttributesData origData, Map<String, Object> newAttributeSerialization) {
        this(origData.getName(), newAttributeSerialization, origData.origJsonValues);
    }

    static public CmSchemaItemAttributesData from(Attribute aAttribute, Map<String, Object> attributeSerialization) {
        return from(aAttribute.getName(), attributeSerialization);
    }

    static public CmSchemaItemAttributesData from(CmSchemaItemData aItemData, Map<String, Object> itemSerialization) {
        return from(aItemData.getName(), itemSerialization);
    }

    static public CmSchemaItemAttributesData from(String aName, Map<String, Object> attributeSerialization) {
        return new CmSchemaItemAttributesData(aName, attributeSerialization);
    }

    static public CmSchemaItemAttributesData from(String aName, Map<String, Object> attributeSerialization, Map<String, Object> origSerializationJsonValues) {
        return new CmSchemaItemAttributesData(aName, attributeSerialization, origSerializationJsonValues);
    }

    public String getDescription() {
        return description;
    }

    public Map<String, Object> getAttributesSerialization() {
        return attributesSerialization;
    }

    /**
     * The <i>Json properties</i> as were found in <i>CMDB
     * original serialization</i>.
     * @return 
     */
    public Map<String, Object> getOrigJsonValues() {
        return origJsonValues;
    }
    
    /**
     * Re-hydrates the <i>Json properties</i> as were found in <i>CMDB
     * original serialization</i>: is re-hydrated in <i>attribute serialization</i>..
     */
    public void rehydrateAttributesSerialization_WithJsonValues() {
        attributesSerialization.putAll(origJsonValues);
    }

    /**
     * As in {@link Card#get(java.lang.String, java.lang.Class) .
     *
     * @return
     * @param name
     * @param clazz
     * @param <T>
     */
    private <T> T get(String name, Class<T> clazz) {
        return convert(attributesSerialization.get(name), clazz);
    }

    @Override
    public String toString() {
        return "CmCardAttributesData{name =< %s >=, descr =< %s >, ([%d] attribute values; [%d] Json values)}".formatted(name, description, attributesSerialization.size(), origJsonValues.size());
    }

}
