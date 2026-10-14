/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.etl.job;

import jakarta.activation.DataSource;
import java.util.List;
import java.util.Map;
import org.cmdbuild.etl.EtlException;
import org.cmdbuild.etl.loader.EtlProcessingResult;
import static org.cmdbuild.etl.utils.EtlResultUtils.serializeEtlProcessingResult;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.isCollectionOrMap;
import static org.cmdbuild.utils.lang.CmConvertUtils.isPrimitiveOrWrapper;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

/**
 *
 * @author afelice
 */
public class PayloadUtils {

    public static boolean isValidPayload(Object attachmentPayload) {
        return attachmentPayload == null
                || attachmentPayload instanceof String
                || attachmentPayload instanceof byte[]
                || attachmentPayload instanceof DataSource
                || isPrimitiveOrWrapper(attachmentPayload)
                || isCollectionOrMap(attachmentPayload)
                || attachmentPayload instanceof EtlProcessingResult;
    }

    public static DataSource payloadToDataSource(Object attachmentPayload) {
        try {
            if (attachmentPayload instanceof EtlProcessingResult result) {//TODO improve this
                return payloadToDataSource(serializeEtlProcessingResult(result));
            } else if (attachmentPayload instanceof String string) {
                return newDataSource(string, "text/plain");
            } else if (attachmentPayload instanceof byte[] bs) {
                return newDataSource(bs, "application/octet-stream");
            } else if (attachmentPayload instanceof DataSource dataSource) {
                return dataSource;
            } else {
                return newDataSource(toJson(attachmentPayload), "application/json");//TODO improve this
//                    throw new IllegalArgumentException(format("invalid context data =< %s >", abbreviate(context.getData())));
            }
        } catch (Exception ex) {
            throw new EtlException(ex, "error converting to data source payload =< %s > (%s)", abbreviate(toStringOrNull(attachmentPayload)), getClassOfNullable(attachmentPayload).getName());
        }
    }

    public static List<Map<String, ?>> payloadToRecords(Object attachmentPayload) {
        try {
            if (attachmentPayload instanceof String string) {
                return (List) fromJson(string, CmJsonUtils.LIST_OF_MAP_OF_OBJECTS);
            } else {
                return (List) attachmentPayload;//TODO
            }
        } catch (Exception ex) {
            throw new EtlException(ex, "error reading records from payload =< %s > (%s)", abbreviate(toStringOrNull(attachmentPayload)), getClassOfNullable(attachmentPayload).getName());
        }

    }

}
