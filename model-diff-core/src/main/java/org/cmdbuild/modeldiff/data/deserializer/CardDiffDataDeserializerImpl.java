/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.data.deserializer;

import org.cmdbuild.modeldiff.core.SerializationHandle_String;
import org.cmdbuild.modeldiff.diff.data.GeneratedDiffData;
import org.cmdbuild.utils.json.CmJsonUtils;

/**
 *
 * @author afelice
 */
public class CardDiffDataDeserializerImpl implements DataDeserializer<SerializationHandle_String, GeneratedDiffData> {

    @Override
    public GeneratedDiffData deserialize(SerializationHandle_String serializedData) {
        return CmJsonUtils.fromJson(serializedData.getSerializationInfo(), GeneratedDiffData.class);
    }
    
}
