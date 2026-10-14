/*
 * CMDBuild has been developed and is managed by PAT srl
  You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.schema.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Map;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_DESCRIPTION_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_NAME_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_PARENT_SERIALIZATION;
import org.cmdbuild.modeldiff.core.serializer.SerializerVisitable;
import org.cmdbuild.modeldiff.core.serializer.ValuesSerializerVisitor;
import org.cmdbuild.modeldiff.diff.schema.CmSchemaItemAttributesDataChangeDelta;

/**
 * JSON serializer for {@link CmSchemaItemAttributesDataChangeDelta}.
 *
 * <p>
 * Similar to
 * <code>org.cmdbuild.model.dataset.data.serializer.CmCardAttributesDataChangeDeltaSerializer</code>
 *
 * @author afelice
 */
public class CmSchemaItemAttributesDataChangeDeltaSerializer extends JsonSerializer<CmSchemaItemAttributesDataChangeDelta> {

    @Override
    public void serialize(CmSchemaItemAttributesDataChangeDelta delta, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {       
        CmSchemaItemAttributesDataSerializerHelper serializerHelper = new CmSchemaItemAttributesDataSerializerHelper(jsonGenerator);
        ValuesSerializerVisitor serializerVisitor = serializerHelper.getValuesSerializerVisitor();

        jsonGenerator.writeStartObject(); // Starts "changed" JSON object

        boolean bWriteItemInfo = true;
        if (!delta.getAdded().isEmpty()) {
            if (bWriteItemInfo) {
                serializeSchemaItemInfo(serializerHelper, delta);
                bWriteItemInfo = false; // write item info only once
            }

            jsonGenerator.writeFieldName("added");
            SerializerVisitable addedVisitable = serializerVisitor.getVisitableFactory()
                    .create(null, delta.getAdded());
            addedVisitable.accept(serializerVisitor);
        }

        if (!delta.getRemoved().isEmpty()) {
            if (bWriteItemInfo) {
                serializeSchemaItemInfo(serializerHelper, delta);
                bWriteItemInfo = false; // write item info only once
            }

            jsonGenerator.writeFieldName("removed");
            SerializerVisitable removedVisitable = serializerVisitor.getVisitableFactory()
                    .create(null, delta.getRemoved());
            removedVisitable.accept(serializerVisitor);
        }

        if (!delta.getChanged().isEmpty()) {
            if (bWriteItemInfo) {
                serializeSchemaItemInfo(serializerHelper, delta);
                bWriteItemInfo = false; // write item info only once
            }

            jsonGenerator.writeFieldName("changed");
            jsonGenerator.writeStartArray(); // Starts changed JSON array            

            delta.getChanged().entrySet().forEach(e -> serializerHelper.rethrowExc(() -> {
                jsonGenerator.writeStartObject(); // Starts "fieldName" item JSON object
                jsonGenerator.writeStringField("attribute", e.getKey());

                SerializerVisitable changedItemVisitable_OldValue = serializerVisitor.getVisitableFactory()
                        .create("oldValue", e.getValue().rightValue());
                changedItemVisitable_OldValue.accept(serializerVisitor);

                SerializerVisitable changedItemVisitable_NewValue = serializerVisitor.getVisitableFactory()
                        .create("newValue", e.getValue().leftValue());
                changedItemVisitable_NewValue.accept(serializerVisitor);

                jsonGenerator.writeEndObject(); // Ends "fieldName" item JSON object
            }));

            jsonGenerator.writeEndArray(); // Ends "changed" JSON array        
        }

        jsonGenerator.writeEndObject(); // Ends "changed" JSON object        
    }

    /**
     * Used for each <i>schema item</i> serialization, to identify the <b>schema
     * item</b>.
     *
     * @param serializerHelper
     * @param delta
     */
    protected void serializeSchemaItemInfo(CmSchemaItemAttributesDataSerializerHelper serializerHelper, CmSchemaItemAttributesDataChangeDelta delta) {
        ValuesSerializerVisitor serializerVisitor = serializerHelper.getValuesSerializerVisitor();

        Map<String, Object> schemaItemInfo = serializerHelper.getItemInfo(delta.getSourceModelNode().getModelObj());
        SerializerVisitable itemNameVisitable = serializerVisitor.getVisitableFactory()
                .create(ATTR_NAME_SERIALIZATION, schemaItemInfo.get(ATTR_NAME_SERIALIZATION));
        itemNameVisitable.accept(serializerVisitor);
        SerializerVisitable itemDescrVisitable = serializerVisitor.getVisitableFactory()
                .create(ATTR_DESCRIPTION_SERIALIZATION, schemaItemInfo.get(ATTR_DESCRIPTION_SERIALIZATION));
        itemDescrVisitable.accept(serializerVisitor);

        if (schemaItemInfo.containsKey(ATTR_PARENT_SERIALIZATION)) {
            SerializerVisitable itemParentVisitable = serializerVisitor.getVisitableFactory()
                    .create(ATTR_PARENT_SERIALIZATION, schemaItemInfo.get(ATTR_PARENT_SERIALIZATION)); // Needed in merge (apply changes) algorithm, to find Classe ancestors
            itemParentVisitable.accept(serializerVisitor);
        }
    }

}
