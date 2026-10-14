/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.data.deserializer;

import org.cmdbuild.modeldiff.core.SerializationHandle_String;
import org.cmdbuild.modeldiff.dataset.data.GeneratedData;
import org.cmdbuild.utils.json.CmJsonUtils;

/**
 *
 * @author afelice
 */
public class CardDataDeserializerImpl implements DataDeserializer<SerializationHandle_String, GeneratedData> {

    @Override
    public GeneratedData deserialize(SerializationHandle_String serializedData) {
        return CmJsonUtils.fromJson(serializedData.getSerializationInfo(), GeneratedData.class);
    }
    
}
