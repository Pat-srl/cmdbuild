/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import static java.util.Objects.isNull;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 * Represents {@link Classe} model configuration.
 *
 * <p>
 * create/delete and modification of related {link @Card}s can be disabled by
 * {@link Dataset} specification.
 *
 * @author afelice
 */
public class ClasseConfiguration {

    /**
     * Represent grant to create a card for this class.
     */
    public static final String CLASS_CAN_CREATE_CARDS_GRANT = "_can_create";
    /**
     * Represent grant to update a card for this class.
     */
    public static final String CLASS_CAN_UPDATE_CARDS_GRANT = "_can_update";
    /**
     * Represent grant to clone a card for this class.
     */
    public static final String CLASS_CAN_CLONE_CARDS_GRANT = "_can_clone";
    /**
     * Represent grant to delete a card for this class.
     */
    public static final String CLASS_CAN_DELETE_CARDS_GRANT = "_can_delete";

    /**
     * Represent structural modification of class
     */
    public static final String CLASS_CAN_MODIFY_CARDS_GRANT = "_can_modify";

    private Map<String, Object> cmdbSerialization;

    @JsonSerialize(using = CustomAttributeListSerializer.class)
    public List<AttributeConfiguration> attributes = list();
    private String filter;

    @JsonIgnore
    public String name;

    /**
     *
     * @param name {@link Classe} name.
     */
    public ClasseConfiguration(String name) {
        this.name = name;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @JsonProperty("class")
    public void setCmdbSerialization(Map<String, Object> cmdbSerialization) {
        this.cmdbSerialization = cmdbSerialization;
    }

    @JsonProperty("class")
    public Map<String, Object> getCmdbSerialization() {
        return cmdbSerialization;
    }

    public void addAttribute(AttributeConfiguration attribConf) {
        attributes.add(attribConf);
    }

    public void overrideCanWriteCards(boolean writable) {
        overrideCanDoOnCard(CLASS_CAN_CREATE_CARDS_GRANT, writable);
        overrideCanDoOnCard(CLASS_CAN_UPDATE_CARDS_GRANT, writable);
        overrideCanDoOnCard(CLASS_CAN_CLONE_CARDS_GRANT, writable);
        overrideCanDoOnCard(CLASS_CAN_DELETE_CARDS_GRANT, writable);
        // CLASS_CAN_MODIFY_CARDS_GRANT represents structural modification, not allowed here to alter it
    }

    protected void overrideCanDoOnCard(String grant, boolean writable) {
        if (!cmdbSerialization.containsKey(grant) || isNull(cmdbSerialization.get(grant))) {
            // If not found, get new value
            cmdbSerialization.put(grant, writable);
        } else {
            // Remains writable or turns to not writable
            cmdbSerialization.put(grant,
                    (boolean) cmdbSerialization.get(grant) && writable);
        }
    }

} // end ClasseConfiguration class

class CustomAttributeListSerializer extends JsonSerializer<List<AttributeConfiguration>> {

    @Override
    public void serialize(List<AttributeConfiguration> attribConfigList, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        for (AttributeConfiguration attribConfig : attribConfigList) {
            // Raises IOException
            gen.writeObject(attribConfig.getCmdbSerialization());
        }
        gen.writeEndArray();
    }

} // end CustomAttributeListSerializer class
