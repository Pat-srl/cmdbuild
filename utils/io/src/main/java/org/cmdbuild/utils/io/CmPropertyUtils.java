/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public class CmPropertyUtils {

    public static String serializeMapAsProperties(Map map) {//TODO keep order
        Properties properties = new Properties();
        map.forEach((k, v) -> properties.put(toStringNotBlank(k), nullToEmpty(toStringOrNull(v))));
        StringWriter writer = new StringWriter();
        try {
            properties.store(writer, null);
        } catch (IOException ex) {
            throw runtime(ex);
        }
        return writer.toString();
    }

    public static Map<String, String> loadProperties(String fileContent) {
        try (InputStream in = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.ISO_8859_1))) {
            return loadProperties(in);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Map<String, String> loadProperties(File file) {
        try (FileInputStream in = new FileInputStream(checkNotNull(file))) {
            return loadProperties(in);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Map<String, String> loadProperties(InputStream in) {
        try {
            Properties properties = new Properties();
            properties.load(checkNotNull(in, "input stream is null"));
            return (Map) properties;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void writeProperties(File out, Map map) {
        writeToFile(out, serializeMapAsProperties(map));
    }

    public static Properties toProperties(Map<String, String> params) {
        Properties properties = new Properties();
        properties.putAll(params);
        return properties;
    }
}
