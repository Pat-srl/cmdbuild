/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader.inner;

import static com.google.common.collect.Maps.transformValues;
import java.util.List;
import java.util.Map;
import org.cmdbuild.etl.loader.EtlRecordInfo;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.utils.lang.CmStringUtils;

public class EtlRecordInfoImpl implements EtlRecordInfo {

    private final long recordIndex;
    private final long recordLineNumber;
    private final Map<String, String> recordData;
    private final Map<String, Object> rawRecord;

    public EtlRecordInfoImpl(long recordIndex, long recordLineNumber, Map<String, Object> recordData) {
        this.recordIndex = recordIndex;
        this.recordLineNumber = recordLineNumber;
        this.rawRecord = map(recordData).immutable();
        this.recordData = map(transformValues(recordData, CmStringUtils::toStringOrEmpty)).immutable();
    }

    @Override
    public long getRecordIndex() {
        return recordIndex;
    }

    @Override
    public long getRecordLineNumber() {
        return recordLineNumber;
    }

    @Override
    public List<Map.Entry<String, String>> getRecordData() {
        return CmCollectionUtils.list(recordData.entrySet());
    }

    @Override
    public String toString() {
        return "EtlRecordInfo{" + "recordIndex=" + recordIndex + ", recordLineNumber=" + recordLineNumber + ", recordData=" + recordData + '}';
    }

    @Override
    public Map<String, Object> getRawRecord() {
        return rawRecord;
    }

}
