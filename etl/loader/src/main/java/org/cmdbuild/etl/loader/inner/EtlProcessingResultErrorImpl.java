/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import org.cmdbuild.etl.loader.EtlRecordInfo;
import org.cmdbuild.utils.lang.CmPreconditions;
import org.cmdbuild.etl.loader.EtlProcessingResultFault;

public class EtlProcessingResultErrorImpl implements EtlProcessingResultFault {

    private final EtlRecordInfo recordInfo;
    private final String userErrorMessage;
    private final String techErrorMessage;

    public EtlProcessingResultErrorImpl(long recordIndex, long recordLineNumber, Map<String, Object> recordData, String userErrorMessage, String techErrorMessage) {
        this(new EtlRecordInfoImpl(recordIndex, recordLineNumber, recordData), userErrorMessage, techErrorMessage);
    }

    public EtlProcessingResultErrorImpl(EtlRecordInfo recordInfo, String userErrorMessage, String techErrorMessage) {
        this.recordInfo = checkNotNull(recordInfo);
        this.userErrorMessage = CmPreconditions.checkNotBlank(userErrorMessage);
        this.techErrorMessage = CmPreconditions.checkNotBlank(techErrorMessage);
    }

    @Override
    public long getRecordIndex() {
        return recordInfo.getRecordIndex();
    }

    @Override
    public long getRecordLineNumber() {
        return recordInfo.getRecordLineNumber();
    }

    @Override
    public List<Map.Entry<String, String>> getRecordData() {
        return recordInfo.getRecordData();
    }

    @Override
    public String getUserErrorMessage() {
        return userErrorMessage;
    }

    @Override
    public String getTechErrorMessage() {
        return techErrorMessage;
    }

    @Override
    public Map<String, Object> getRawRecord() {
        return recordInfo.getRawRecord();
    }

}
