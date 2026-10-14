/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_CODE_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_NAME_SERIALIZATION;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrDefault;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

/**
 * Represents a generic model configuration, for:
 * <ul>
 * <li>{@link Classe};
 * <li>{@link Process};
 * </ul>
 *
 * @author afelice
 */
public class SchemaItemConfiguration {

    private Map<String, Object> cmdbSerialization;

    /**
     * Used for properties that are json parts or collections, like (in
     * {@link Classe}
     * <i>CMDBuild serialization</i>) <code>defaultOrder</code>,
     * <code>formTrigger</code> and so on.
     */
    private Map<String, Object> jsonCmdbSerializationValues;

    public List<AttributeConfiguration> attributes = list();

    @JsonIgnore
    public String name;

    @JsonCreator
    public SchemaItemConfiguration() {
    }

    /**
     *
     * @param name. Is <i>Json ignored</i>, can't be in <i>Json creator</i>.
     */
    public SchemaItemConfiguration(String name) {
        this.name = name;
    }

    @JsonIgnore // to be able to change name in inherited classes
    public String getName() {
        return this.name;
    }

    /**
     * Override this method in a derived class, changing
     * <code>@JsonProperty</code> string, to change name used for <i>schema
     * item</i>.
     *
     * @param cmdbSerialization
     */
    @JsonProperty("item")
    public void setCmdbSerialization(Map<String, Object> cmdbSerialization) {
        this.cmdbSerialization = cmdbSerialization;

        if (cmdbSerialization.containsKey(ATTR_NAME_SERIALIZATION) || cmdbSerialization.containsKey(ATTR_CODE_SERIALIZATION)) {
            this.name = getName(cmdbSerialization);
        }
    }

    /**
     * Override this method in a derived class, changing
     * <code>@JsonProperty</code> string, to change name used for <i>schema
     * item</i>.
     *
     * @return
     */
    @JsonProperty("item")
    public Map<String, Object> getCmdbSerialization() {
        return cmdbSerialization;
    }

    /**
     * Used for properties that are json parts or collections, like
     * <code>defaultOrder</code>, <code>formTrigger</code> and so on.
     *
     * @see {@link ClassSerializationHelper#WsClassData} constructor, looking
     * for {@link List} with <i>CMDBuild internal objects</i>.
     * @param jsonValues
     */
    @JsonIgnore
    public void setJsonCmdbSerializationValues(Map<String, Object> jsonValues) {
        jsonCmdbSerializationValues = jsonValues;
    }

    /**
     * Returns <i>CMDBuild serialization</i> but with some properties that are
     * json parts.
     *
     * @return
     */
    @JsonIgnore
    public Map<String, Object> getSerializableValues() {
        return map(cmdbSerialization).with(jsonCmdbSerializationValues);
    }

    /**
     * Override this method in a derived class, changing
     * <code>@JsonProperty</code> string, to change name used for list of
     * attributes.
     *
     * @param attributes
     */
    @JsonProperty("attributes")
    @JsonDeserialize(using = CustomAttributeListDeserializer.class)
    public void setAttributes(List<AttributeConfiguration> attributes) {
        this.attributes = attributes;
    }

    /**
     * Override this method in a derived class, changing
     * <code>@JsonProperty</code> string, to change name used for list of
     * attributes.
     *
     * @return
     */
    @JsonProperty("attributes")
    @JsonSerialize(using = CustomAttributeListSerializer.class)
    public synchronized List<AttributeConfiguration> getAttributes() {
        return this.attributes;
    }

    public boolean hasAttrubute(String attribName) {
        return this.attributes.stream().filter(a -> attribName.equals(a.getName())).findFirst().isPresent();
    }

    public synchronized void addAttribute(AttributeConfiguration attribConf) {
        attributes.add(attribConf);
    }

    public synchronized void updateAttribute(AttributeConfiguration attribConf) {
        String attribName = attribConf.getName();
        // Substitute AttributeConfiguration having matching name
        int curPos = 0;
        for (; curPos < attributes.size(); curPos++) {
            if (attributes.get(curPos).getName().equals(attribName)) {
                attributes.set(curPos, attribConf);
                break; // Exit from loop after substitution
            }
        }

        if (curPos == attributes.size()) {
            // Attribute with given name not found. Simply add it.
            attributes.add(attribConf);
        }
    }

    public synchronized void removeAttribute(String attribName) {
        // Remove AttributeConfiguration having matching name
        int curPos = 0;
        for (; curPos < attributes.size(); curPos++) {
            if (attributes.get(curPos).getName().equals(attribName)) {
                attributes.remove(curPos);
                break; // Exit from loop after removing
            }
        }
    }

    public static String getName(Map<String, Object> cmdbSerialization1) {
        return toStringOrDefault(cmdbSerialization1.get(ATTR_NAME_SERIALIZATION), toStringOrNull(cmdbSerialization1.get(ATTR_CODE_SERIALIZATION)));
    }

    @Override
    public String toString() {
        return "%s<%s>([%d]props, [%d]attribs)".formatted(this.getClass().getName(),
                getName(getCmdbSerialization()),
                getCmdbSerialization().size(), attributes.size());
    }

} // end SchemaItemConfiguration class

// @todo AFE tbc
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

class CustomAttributeListDeserializer extends JsonDeserializer<List<AttributeConfiguration>> {

    @Override
    public List<AttributeConfiguration> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        // Ottieni l'albero JSON (JSON Array)
        JsonNode node = p.getCodec().readTree(p);

        List<AttributeConfiguration> result = list();
        ObjectMapper mapper = (ObjectMapper) p.getCodec();

        // Itera sugli elementi dell'array JSON e deserializza ciascun elemento come oggetto AttributeConfiguration
        for (JsonNode elementNode : node) {
            AttributeConfiguration curAttribConfig = mapper.treeToValue(elementNode, AttributeConfiguration.class); // Carica solo name
            if (elementNode != null && elementNode.isObject()) {
                // Converte il nodo direttamente in una Map<String, Object>
                Map<String, Object> childrenMap = mapper.convertValue(elementNode, Map.class);
                curAttribConfig.setCmdbSerialization(childrenMap);
            }

            result.add(curAttribConfig);
        }

        return result;
    }
} // end CustomAttributeListDeserializer class
