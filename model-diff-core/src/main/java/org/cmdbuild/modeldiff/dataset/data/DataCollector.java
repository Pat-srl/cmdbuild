/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dataset.data;

import jakarta.activation.DataHandler;
import java.util.Map;
import org.cmdbuild.modeldiff.core.JsonSerializationMode;
import org.cmdbuild.modeldiff.core.SerializationHandle;
import org.cmdbuild.modeldiff.core.SerializationHandle_FileSystem_Zipped;

/**
 * From a dataset (definition), build a configuration (real data to handle)
 *
 * @author afelice
 */
public interface DataCollector {

    SerializationHandle collectData(DataDataset dataset, Map<String, String> bindedFilters, boolean cardInfoOnly, JsonSerializationMode jsonSerializationMode);

    SerializationHandle_FileSystem_Zipped collectData(DataDataset dataset, Map<String, String> bindedFilters);

    SerializationHandle compareData(DataDataset dataset, Map<String, String> bindedFilters, SerializationHandle serializedData);

    DataHandler extractDmsDocument(DataDataset dataset, SerializationHandle_FileSystem_Zipped zippedFileSerializationHandle, String classeName, String cardId, String docZippedName);
}
