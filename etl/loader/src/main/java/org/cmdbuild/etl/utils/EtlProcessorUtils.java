package org.cmdbuild.etl.utils;

import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.util.List;
import jakarta.activation.DataSource;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.cmdbuild.utils.io.CmIoUtils.countBytes;
import static org.cmdbuild.utils.io.CmIoUtils.getContentType;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassNameOfNullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EtlProcessorUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
//    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static Object prepareData(Object data) {
        if (data instanceof List list) {
            LOGGER.info("load import data = LIST[{}]", list.size());
        } else {
            if (data instanceof String string) {
                data = newDataSource(string, "text/plain");
            } else if (data instanceof byte[] bs) {
                data = newDataSource(bs, "application/octet-stream");
            } else if (data instanceof DataSource) {
                //nothing to do
            } else {
                throw new IllegalArgumentException(format("invalid data type =< %s >", getClassNameOfNullable(data)));
            }
            LOGGER.info("load import data = {} {}", byteCountToDisplaySize(countBytes((DataSource) data)), getContentType((DataSource) data));
        }
        return data;
    }

}
