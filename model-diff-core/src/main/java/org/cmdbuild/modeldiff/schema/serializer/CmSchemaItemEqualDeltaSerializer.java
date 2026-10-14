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
import org.cmdbuild.modeldiff.core.serializer.SerializerVisitable;
import org.cmdbuild.modeldiff.core.serializer.ValuesSerializerVisitor;
import org.cmdbuild.modeldiff.diff.patch.CmEqualDelta;
import org.cmdbuild.modeldiff.diff.schema.CmSchemaItemAttributesData;
import org.cmdbuild.modeldiff.diff.schema.CmSchemaItemAttributesDataNode;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * JSON serializer for {@link CmEqualDelta}, used for <i>schema item
 * properties</i>
 * when some <i>schema item attribute</i> is modified (insert/remove/change) and
 * that <i>schema item</i> is serialized in <i>schema diff</i>.
 *
 * @author afelice
 */
public class CmSchemaItemEqualDeltaSerializer extends JsonSerializer<CmEqualDelta<CmSchemaItemAttributesDataNode, CmSchemaItemAttributesData>> {

    @Override
    public void serialize(CmEqualDelta<CmSchemaItemAttributesDataNode, CmSchemaItemAttributesData> delta, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        CmSchemaItemAttributesDataSerializerHelper serializerHelper = new CmSchemaItemAttributesDataSerializerHelper(gen);
        ValuesSerializerVisitor serializerVisitor = serializerHelper.getValuesSerializerVisitor();

//        if (delta.getTargetModelNode().getModelObj().getClass().equals(CmSchemaItemAttributesData.class)) {
//            System.out.println("Schema Item Attributes Node =< %s > (Equal)".formatted(delta.getTargetModelNode().getModelObj().getName()));
//        }

        CmSchemaItemAttributesData schemaItemAttributesData = delta.getSourceModelNode().getModelObj();
        SerializerVisitable equalVisitable = serializerVisitor.getVisitableFactory()
                .create(null, extractBasicInfo(schemaItemAttributesData.getAttributesSerialization()));
        serializerHelper.rethrowExc(() -> {
            equalVisitable.accept(serializerVisitor);
        });
    }

    private Object extractBasicInfo(Map<String, Object> attributesSerialization) {
        return map(attributesSerialization).withKeys(ATTR_NAME_SERIALIZATION, ATTR_DESCRIPTION_SERIALIZATION);
    }

}
