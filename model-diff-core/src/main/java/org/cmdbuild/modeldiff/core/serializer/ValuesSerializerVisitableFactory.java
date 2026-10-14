/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.core.serializer;

import com.fasterxml.jackson.databind.node.NullNode;
import java.util.List;
import java.util.Map;

/**
 * Factory of {@link SerializerVisitable} for supported (attribute) values.
 * 
 * <p>Handled even {@link NullNode} for <i>json objects</i>.
 *
 * @author afelice
 */
public class ValuesSerializerVisitableFactory implements SerializerVisitableFactory {
    
    public final static String NULL_NODE_SERIALIZATION = "null";
    
    /**
     * From raw value type to visitable.
     *
     * @param fieldName null if no enwrapping field name, for example for compound 
     *    values (map item or list item);
     * @param value
     * @return
     */
    @Override
    public SerializerVisitable create(String fieldName, Object value) {
        if (value == null) {
            return new NullVisitable(fieldName);
        } else if (value instanceof Number aNumber) {
            return new NumberVisitable(fieldName, aNumber);
        } else if (value instanceof Boolean aBool) {
            return new BooleanVisitable(fieldName, aBool);
        } else if (value instanceof String aString) {
            return new StringVisitable(fieldName, aString);
        } else if (value instanceof Enum) {
            return new EnumVisitable(fieldName, (Enum<?>) value);
        } else if (value instanceof Map) {
            return new MapVisitable(fieldName, (Map<String, Object>) value);
        } else if (value instanceof List) {
            return new ListVisitable(fieldName, (List<Object>) value);
        } else if (value instanceof NullNode) {
            return new StringVisitable(fieldName, NULL_NODE_SERIALIZATION); // the explicit representation of NullNode in a Json
        }
        
        throw new RuntimeException("unhandled data type =< %s > while serializing data to json, field name =< %s >.".formatted(value.getClass().getName(), fieldName));
    }    
}
