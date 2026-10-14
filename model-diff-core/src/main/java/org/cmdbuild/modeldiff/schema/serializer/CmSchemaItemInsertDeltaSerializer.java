/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.schema.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.cmdbuild.modeldiff.core.serializer.SerializerVisitable;
import org.cmdbuild.modeldiff.core.serializer.ValuesSerializerVisitor;
import org.cmdbuild.modeldiff.diff.patch.CmInsertDelta;
import org.cmdbuild.modeldiff.diff.schema.CmSchemaItemAttributesData;
import org.cmdbuild.modeldiff.diff.schema.CmSchemaItemAttributesDataNode;

/**
 * JSON serializer for {@link CmInsertDelta}
 *
 * <p>
 * As CmCardRemoveDeltaSerializer.
 *
 * @author afelice
 */
public class CmSchemaItemInsertDeltaSerializer extends JsonSerializer<CmInsertDelta<CmSchemaItemAttributesDataNode, CmSchemaItemAttributesData>> {

    @Override
    public void serialize(CmInsertDelta<CmSchemaItemAttributesDataNode, CmSchemaItemAttributesData> delta, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        CmSchemaItemAttributesDataSerializerHelper serializerHelper = new CmSchemaItemAttributesDataSerializerHelper(gen);
        ValuesSerializerVisitor serializerVisitor = serializerHelper.getValuesSerializerVisitor();

//        if (delta.getTargetModelNode().getModelObj().getClass().equals(CmSchemaItemAttributesData.class)) {
//            System.out.println("Schema Item Attributes Node =< %s > (Insert)".formatted(delta.getTargetModelNode().getModelObj().getName()));
//        }

        CmSchemaItemAttributesData insertedSchemaItemAttributesData = delta.getTargetModelNode().getModelObj();
        SerializerVisitable addedVisitable = serializerVisitor.getVisitableFactory()
                .create(null, insertedSchemaItemAttributesData.getAttributesSerialization());
        serializerHelper.rethrowExc(() -> {
            addedVisitable.accept(serializerVisitor);
        });
    }
}
