/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.core.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.lang.CmExceptionUtils.illegalArgument;
import org.cmdbuild.utils.lang.LambdaExceptionUtils;

/**
 * Visitor, the json serialization for each supported type of value.
 *
 * <p>
 * Specific serialization for each supported value type.
 *
 * @author afelice
 */
public class ValuesSerializerVisitor implements SerializerVisitor {

    private final JsonGenerator jsonGenerator;
    private final ObjectMapper customObjectMapper = CmJsonUtils.getObjectMapper();
    private final Consumer<LambdaExceptionUtils.Runnable_WithExceptions<?>> errorHandling;
    private final static ValuesSerializerVisitableFactory VALUES_VISITABLE_FACTORY = new ValuesSerializerVisitableFactory();

    public ValuesSerializerVisitor(JsonGenerator jsonGenerator, Consumer<LambdaExceptionUtils.Runnable_WithExceptions<?>> errorHandling) {
        this.jsonGenerator = checkNotNull(jsonGenerator);
        this.errorHandling = errorHandling;
    }

    @Override
    public void visitNull(String fieldName) {
        errorHandling.accept(() -> {
            if (fieldName == null) {
                jsonGenerator.writeNull();
            } else {
                jsonGenerator.writeNullField(fieldName);
            }
        });
    }

    @Override
    public void visit(String fieldName, Number value) {
        errorHandling.accept(() -> {
            if (fieldName == null) {
                writeNumber(value);
            } else {
                writeNumberField(fieldName, value);
            }
        });
    }

    @Override
    public void visit(String fieldName, Boolean value) {
        errorHandling.accept(() -> {
            if (fieldName == null) {
                jsonGenerator.writeBoolean(value);
            } else {
                jsonGenerator.writeBooleanField(fieldName, value);
            }
        });
    }

    @Override
    public void visit(String fieldName, String value) {
        errorHandling.accept(() -> {
            if (fieldName == null) {
                jsonGenerator.writeString(value);
            } else {
                jsonGenerator.writeStringField(fieldName, value);
            }
        });
    }

    @Override
    public void visit(String fieldName, Enum<?> value) {
        errorHandling.accept(() -> {
            if (fieldName != null) {
                jsonGenerator.writeFieldName(fieldName);
            }

            // Use ObjectMapper to write enum JSON serialization with provided serializers
            customObjectMapper.writeValue(jsonGenerator, value);
        });
    }

    @Override
    public void visit(String fieldName, Map<String, Object> value) {
        errorHandling.accept(() -> {
            if (fieldName != null) {
                jsonGenerator.writeFieldName(fieldName);
            }
            jsonGenerator.writeStartObject();

            value.entrySet().forEach(e -> {
                // Creator of Visistable
                SerializerVisitable itemVisitable = getVisitableFactory().create(e.getKey(), e.getValue());

                // Self invocation on visitor
                itemVisitable.accept(this);
            });

            jsonGenerator.writeEndObject();
        });
    }

    @Override
    public void visit(String fieldName, List value) {
        errorHandling.accept(() -> {
            if (fieldName != null) {
                jsonGenerator.writeFieldName(fieldName);
            }

            jsonGenerator.writeStartArray(); // Starts JSON array

            value.forEach(itemValue -> {
                // Creator of Visistable
                SerializerVisitable itemVisitable = getVisitableFactory().create(null, itemValue);

                // Self invocation on visitor
                itemVisitable.accept(this);
            });

            jsonGenerator.writeEndArray(); // Ends JSON array
        });
    }

    @Override
    public SerializerVisitableFactory getVisitableFactory() {
        return VALUES_VISITABLE_FACTORY;
    }

    /**
     * Can't use Number as is with {@link JsonGenerator#writeNumber(long) },
     * have to explicitly cast t it.
     *
     * @param value
     * @throws java.io.IOException
     */
    private void writeNumber(Number value) throws IOException {
        if (value instanceof Integer integer) {
            jsonGenerator.writeNumber(integer);
        } else if (value instanceof Long aLong) {
            jsonGenerator.writeNumber(aLong);
        } else if (value instanceof Double aDouble) {
            jsonGenerator.writeNumber(aDouble);
        } else if (value instanceof BigDecimal bigDecimal) {
            jsonGenerator.writeNumber(bigDecimal);
        } else if (value instanceof Float aFloat) {
            jsonGenerator.writeNumber(aFloat);
        } else if (value instanceof Short aShort) {
            jsonGenerator.writeNumber(aShort);
        } else if (value instanceof Byte aByte) {
            jsonGenerator.writeNumber(aByte);
        } else {
            throw illegalArgument("Unsupported Number type =< %s >", value.getClass().getName());
        }
    }

    /**
     * Can't use Number as is with {@link JsonGenerator#writeNumber(long) },
     * have to explicitly cast t it.
     *
     * @param fieldName
     * @param value
     * @throws java.io.IOException
     */
    private void writeNumberField(String fieldName, Number value) throws IOException {
        jsonGenerator.writeFieldName(fieldName);
        writeNumber(value);
    }

} // end ValuesSerializerVisitor class
