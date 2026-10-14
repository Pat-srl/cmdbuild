/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.schema.deserializer;

import org.cmdbuild.modeldiff.core.SerializationHandle_String;
import org.cmdbuild.modeldiff.data.deserializer.DataDeserializer;
import org.cmdbuild.modeldiff.diff.schema.GeneratedDiffSchema;
import org.cmdbuild.utils.json.CmJsonUtils;

/**
 *
 * @author afelice
 */
public class CmSchemaDiffDataDeserializerImpl implements DataDeserializer<SerializationHandle_String, GeneratedDiffSchema> {

    @Override
    public GeneratedDiffSchema deserialize(SerializationHandle_String serializedData) {
        return CmJsonUtils.fromJson(serializedData.getSerializationInfo(), GeneratedDiffSchema.class);
    }
    
}
