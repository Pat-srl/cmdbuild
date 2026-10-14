/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.temp;

import java.nio.charset.StandardCharsets;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;

public interface TempService {

    void deleteTempData(String tempId);

    DataHandler getTempData(String tempId);

    BigByteArray getTempDataBigBytes(String tempId);

    TempInfo getTempInfo(String tempId);

    TempServiceHelper helper();

    default String putTempData(DataHandler data) {
        return helper().withData(data).put();
    }

    default String putTempData(DataSource data) {
        return helper().withData(data).put();
    }

    default String putTempData(BigByteArray data) {
        return helper().withData(data).put();
    }

    default String putTempData(DataSource data, TempInfoSource source) {
        return helper().withData(data).withSource(source).put();
    }

//    default String putTempData(String data, Map<String, String> info) {
//        return helper().withData(data).withInfo(info).put();
//    }
    default byte[] getTempDataBytes(String tempId) {
        return getTempDataBigBytes(tempId).toByteArray();
    }

    default String getTempDataAsString(String tempId) {
        return new String(getTempDataBytes(tempId), StandardCharsets.UTF_8);
    }

    default String putTempData(String data) {
        return putTempData(data.getBytes(StandardCharsets.UTF_8));
    }

    default String putTempData(byte[] data) {
        return putTempData(newDataHandler(data));
    }

    interface TempServiceHelper {

        TempServiceHelper withData(DataSource data);

        TempServiceHelper withData(BigByteArray data);

        TempServiceHelper withSource(TempInfoSource source);

        TempServiceHelper withTimeToLive(Long timeToLiveInSeconds);

        String put();

        default TempServiceHelper withData(DataHandler data) {
            return withData(toDataSource(data));
        }

        default TempServiceHelper withData(byte[] data) {
            return withData(new BigByteArray(data));
        }

        default TempServiceHelper withData(String data) {
            return withData(data.getBytes(StandardCharsets.UTF_8));
        }
    }

}
