/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.postgres;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.List;
import org.apache.commons.compress.compressors.CompressorException;
import static org.cmdbuild.utils.io.CmCompressionUtils.xunzip;
import static org.cmdbuild.utils.io.CmIoUtils.isCompressedWithXzip;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.io.CmNetUtils.getAvailablePort;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.cmdbuild.utils.postgres.PostgresHelperConfigImpl.PostgresHelperBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostgresUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public final static String POSTGRES_VERSION_AUTO = "auto", POSTGRES_VERSION_DEFAULT = "default";

    public static void checkDumpFile(File file) {
        newHelper().buildHelper().checkDumpFile(file);
    }

    public static File getDumpFromFileExtractIfNecessary(File file) throws CompressorException {
        if (isCompressedWithXzip(file)) {
            LOGGER.debug("the dump file is compressed with xz, decompressing before import");
            File res = tempFile("file_", ".dump");
            try (InputStream in = new FileInputStream(file)) {
                byte[] dump = xunzip(in.readAllBytes());
                writeToFile(res, dump);
            } catch (IOException ex) {
                throw runtime(ex);
            }
            return res;
        } else {
            return file;
        }
    }

    public static boolean dumpContainsSchema(File dumpFile, String schema) {
        return newHelper().withSchema(schema).buildHelper().dumpContainsSchema(dumpFile);
    }

    public static List<String> getTablesInDump(File dumpFile, String schema) {
        return newHelper().withSchema(schema).buildHelper().getTablesInDump(dumpFile);
    }

    public static List<String> getSchemasInDump(File dumpFile) {
        return newHelper().buildHelper().getSchemasInDump(dumpFile);
    }

    public static PostgresHelperBuilder newHelper() {
        return PostgresHelperConfigImpl.builder();
    }

    public static PostgresHelperBuilder newHelper(String host, int port, String username, String password) {
        return newHelper()
                .withUsername(username)
                .withPassword(password)
                .withHost(host)
                .withPort(port);
    }

    public static int getPostgresServerAvailablePort() {
        return getAvailablePort(5432);
    }

}
