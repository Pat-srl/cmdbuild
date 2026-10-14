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
import org.cmdbuild.modeldiff.diff.patch.CmRemoveDelta;
import org.cmdbuild.modeldiff.diff.schema.CmSchemaItemAttributesData;
import org.cmdbuild.modeldiff.diff.schema.CmSchemaItemAttributesDataNode;

/**
 * JSON serializer for {@link CmRemoveDelta}
 *
 * <p>
 * As CmCardRemoveDeltaSerializer.
 *
 * @author afelice
 */
public class CmSchemaItemRemoveDeltaSerializer extends JsonSerializer<CmRemoveDelta<CmSchemaItemAttributesDataNode, CmSchemaItemAttributesData>> {
    
    @Override
    public void serialize(CmRemoveDelta<CmSchemaItemAttributesDataNode, CmSchemaItemAttributesData> delta, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        CmSchemaItemAttributesDataSerializerHelper serializerHelper = new CmSchemaItemAttributesDataSerializerHelper(gen);
        ValuesSerializerVisitor serializerVisitor = serializerHelper.getValuesSerializerVisitor();

//        if (delta.getSourceModelNode().getModelObj().getClass().equals(CmSchemaItemAttributesData.class)) {
//            System.out.println("Schema Item Attributes Node =< %s > (Remove)".formatted(delta.getSourceModelNode().getModelObj().getName()));
//        }

        CmSchemaItemAttributesData removedItemAttributesData = delta.getSourceModelNode().getModelObj();
        SerializerVisitable addedVisitable = serializerVisitor.getVisitableFactory()
                .create(null, removedItemAttributesData.getAttributesSerialization());
        serializerHelper.rethrowExc(() -> {
            addedVisitable.accept(serializerVisitor);
        });
    }
}
