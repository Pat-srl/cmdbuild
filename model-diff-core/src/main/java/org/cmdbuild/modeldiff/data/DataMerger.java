/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.data;

import java.util.List;
import org.cmdbuild.modeldiff.core.SerializationHandle;
import org.cmdbuild.modeldiff.core.SerializationHandle_FileSystem_Zipped;

/**
 * Merge a <i>data diff </i> to CMDBuild system.
 *
 * @author afelice
 */
public interface DataMerger {

    List<CmCardAttributesData> mergeData(SerializationHandle serializedData, SerializationHandle_FileSystem_Zipped modifiedDataWithDms);

    default List<CmCardAttributesData> mergeData(SerializationHandle serializedData) {
        return mergeData(serializedData, null);
    }
}
