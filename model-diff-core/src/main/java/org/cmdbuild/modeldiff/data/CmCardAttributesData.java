/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.data;

import java.util.HashMap;
import java.util.Map;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;

/**
 * Represents {@link Card} data, with attribute values.
 *
 * @author afelice
 */
public class CmCardAttributesData extends CmCardData {

    private final String code;
    private final String description;

    private final Map<String, Object> attributesSerialization;

    /**
     * Used while deserializing <i>Mobile</i> received data: given {@link Card}
     * id may be a String (a UUID), if newly added.
     *
     * @param classeName
     * @param id
     * @param attributeSerialization
     */
    public CmCardAttributesData(String classeName, Object id, Map<String, Object> attributeSerialization) {
        this(classeName, String.valueOf(id), attributeSerialization);
    }

    public CmCardAttributesData(String classeName, Long id, Map<String, Object> attributeSerialization) {
        this(classeName, String.valueOf(id), attributeSerialization);
    }

    public CmCardAttributesData(String classeName, String id, Map<String, Object> attributeSerialization) {
        super(classeName, id);

        this.attributesSerialization = new HashMap(attributeSerialization);

        // As in Card.getDescription()
        this.code = get(ATTR_CODE, String.class);

        // As in Card.getCode()
        this.description = get(ATTR_DESCRIPTION, String.class);
    }

    /**
     * Copy constructor, but with specified attributes.
     *
     * @param origData
     * @param newAttributeSerialization
     */
    public CmCardAttributesData(CmCardAttributesData origData, Map<String, Object> newAttributeSerialization) {
        this(origData.getClasseName(), origData.getId(), newAttributeSerialization);
    }

    static public CmCardAttributesData from(Card aCard) {
        return new CmCardAttributesData(aCard.getClassName(), aCard.getId(), aCard.getAllValuesAsMap());
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, Object> getAttributesSerialization() {
        return attributesSerialization;
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
        return "CmCardAttributesData{id =< %s >=, descr =< %s >, ([%d] attribute values)}".formatted(id, description, attributesSerialization.size());
    }

}
