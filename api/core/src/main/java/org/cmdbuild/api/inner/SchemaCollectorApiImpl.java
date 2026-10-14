/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.api.inner;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.cmdbuild.api.SchemaCollectorApi;
import org.cmdbuild.modeldiff.core.SerializationHandle_String;
import org.cmdbuild.modeldiff.schema.SchemaCollector;
import org.cmdbuild.modeldiff.schema.SchemaConfiguration;
import static org.cmdbuild.utils.io.CmIoUtils.cmTmpDir;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.getJsonBeanTargetClass;
import static org.cmdbuild.utils.json.CmJsonUtils.hasJsonBeanAnnotation;
import static org.cmdbuild.utils.json.CmJsonUtils.toPrettyJson;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation to be used inside CMDBuild.
 *
 * @author afelice
 */
public class SchemaCollectorApiImpl implements SchemaCollectorApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static ObjectMapper OBJECT_MAPPER_WITH_INCLUDE_SOURCE_ON_ERROR;

    static { // Enable INCLUDE_SOURCE_IN_LOCATION when raising a deserialization error
        // Original ObjectMapper (with custom modules for serialization)
        ObjectMapper originalMapper = getSystemObjectMapper();

        // Create a new JsonFactory with INCLUDE_SOURCE_IN_LOCATION enabled (no sensible data will be logged)
        JsonFactory customJsonFactory = JsonFactory.builder()
                .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
                .build();

        OBJECT_MAPPER_WITH_INCLUDE_SOURCE_ON_ERROR = originalMapper.copyWith(customJsonFactory);
    }

    /**
     * Used when {@link SchemaCollector} service is not available.
     *
     * <p>
     * throws {@link UnsupportedOperationException} for each method invoked.
     */
    public static final SchemaCollectorApi DUMMY = new SchemaCollectorApi() {
        private static final String UNSUPPORTED_MESSAGE = "service SchemaCollector not available";

        @Override
        public String collectSchema(String curSystemMnemoniName, String curSystemId) {
            throw unsupported(UNSUPPORTED_MESSAGE);
        }

        @Override
        public String compareSchema(String otherSchemaSerialization, String curSystemMnemonicName) {
            throw unsupported(UNSUPPORTED_MESSAGE);
        }

        @Override
        public String compareSchemaBetween(String aSchemaSerialization, String otherSchemaSerialization) {
            throw unsupported(UNSUPPORTED_MESSAGE);
        }

        @Override
        public String applySchemaDiff(String diffSchemaSerialization) {
            throw unsupported(UNSUPPORTED_MESSAGE);
        }

        @Override
        public String test(String any) {
            return "(dummy)%s ; temp dir =< %s >".formatted(any, getTmpPath());
        }
    };

    private final SchemaCollector schemaCollector;

    /**
     * Initialized in {@link org.cmdbuild.api.inner.SystemApiImpl}
     *
     * @param schemaCollector
     */
    SchemaCollectorApiImpl(SchemaCollector schemaCollector) {
        this.schemaCollector = checkNotNull(schemaCollector, "Not instantiated implementation of " + SchemaCollectorApi.class.getName());
        logger.info("Implementation of {}: {}", SchemaCollectorApi.class.getName(), this.schemaCollector.toString());
    }

    @Override
    public String test(String any) {
        return "%s ; temp dir =< %s >".formatted(SchemaCollectorApi.super.test(any), getTmpPath());
    }

    @Override
    public String collectSchema(String curSystemMnemonicName, String curSystemId) {
        logger.info("collectSchema(\"{}\",\"{}\") -- invoking...", curSystemMnemonicName, curSystemId);
        SchemaConfiguration schemaConfiguration = schemaCollector.collectSchema(curSystemId, curSystemMnemonicName);

        final String resultPrettyJson = toPrettyJson(schemaConfiguration);
        logger.info("collectSchema(\"{}\",\"{}\") -- result json: size [{}]. ", curSystemMnemonicName, curSystemId, getReadableSize(resultPrettyJson));

        File tmpFile = writeJsonToTemp(resultPrettyJson, "%s_%s_%s".formatted("schemaCollect", curSystemMnemonicName, curSystemId));
        logger.info("collectSchema(\"{}\",\"{}\") -- result json: stored to temp file =< {} >).", curSystemMnemonicName, curSystemId, tmpFile.getAbsolutePath());

        return tmpFile.getAbsolutePath();
    }

    @Override
    public String compareSchema(String otherSchemaSerialization, String curSystemMnemonicName) {
        logger.info("compareSchema(\"{}\", (db)\"{}\") -- invoking...", otherSchemaSerialization, curSystemMnemonicName);
        String otherSchemaJson = readFromTmp(otherSchemaSerialization);
        SchemaConfiguration leftSchemaConfiguration = fromJson_IncludeSourceOnError(otherSchemaJson, SchemaConfiguration.class);

        SerializationHandle_String result = schemaCollector.compareSchema(leftSchemaConfiguration, curSystemMnemonicName);

        File tmpFile = writeJsonToTemp(result.getSerializationInfo(), "%s_%s_%s_VS_%s".formatted("schemaCompare", leftSchemaConfiguration.name, leftSchemaConfiguration.id, curSystemMnemonicName));
        logger.info("compareSchema(\"{}\", (db)\"{}\") -- result json: stored to temp file =< {} >).", otherSchemaSerialization, curSystemMnemonicName, tmpFile.getAbsolutePath());

        return tmpFile.getAbsolutePath();
    }

    @Override
    public String compareSchemaBetween(String newSchemaSerialization, String aSchemaSerialization) {
        logger.info("compareSchemaBetween((new)\"{}\", \"{}\") -- invoking...", newSchemaSerialization, aSchemaSerialization);
        String newSchemaJson = readFromTmp(newSchemaSerialization);
        SchemaConfiguration leftSchema = fromJson_IncludeSourceOnError(newSchemaJson, SchemaConfiguration.class);

        String aSchemaJson = readFromTmp(aSchemaSerialization);
        SchemaConfiguration rightSchema = fromJson_IncludeSourceOnError(aSchemaJson, SchemaConfiguration.class);  
        SerializationHandle_String result = schemaCollector.compareSchema(leftSchema, rightSchema);

        File tmpFile = writeJsonToTemp(result.getSerializationInfo(), "%s_%s_%s_VS_%s_%s".formatted("schemaCompare", leftSchema.name, leftSchema.id, rightSchema.name, rightSchema.id));
        logger.info("compareSchemaBetween((new)\"{}\", \"{}\") -- result json: stored to temp file =< {} >).", newSchemaSerialization, aSchemaSerialization, tmpFile.getAbsolutePath());

        return tmpFile.getAbsolutePath();
    }

    @Override
    public String applySchemaDiff(String diffSchemaSerialization) {
        logger.info("applySchema(\"{}\") -- invoking...", diffSchemaSerialization); 
        String schemaDiffJson = readFromTmp(diffSchemaSerialization);

        SchemaConfiguration schemaConfiguration = schemaCollector.applySchemaDiff(new SerializationHandle_String(schemaDiffJson));
        final String resultPrettyJson = toPrettyJson(schemaConfiguration);

        File tmpFile = writeJsonToTemp(resultPrettyJson, "%s_%s_%s".formatted("schemaUpdated", schemaConfiguration.name, schemaConfiguration.id));

        return tmpFile.getAbsolutePath();
    }

    private File writeJsonToTemp(String content, String filename) {
        File tmpFile = tempFile(filename, "json", false);
        writeToFile(content, tmpFile);

        if (!tmpFile.exists()) {
            throw runtime("Unable to create file %s".formatted(tmpFile.getAbsolutePath()));
        }

        return tmpFile;
    }

    private static String getTmpPath() {
        return cmTmpDir().getAbsolutePath();
    }

    private String readFromTmp(String tempFilename) {
        File tmpFile = new File(cmTmpDir(), tempFilename);
        if (!tmpFile.exists()) {
            throw runtime("couldn't find temp file =< %s >=".formatted(tmpFile.getAbsolutePath()));
        }
        return readToString(tmpFile);
    }

    private static String getReadableSize(String content) {
        if (content == null) {
            return "0 B";
        }

        byte[] utf8Bytes = content.getBytes(StandardCharsets.UTF_8);
        long sizeInBytes = utf8Bytes.length; // Dimensione effettiva in byte

        if (sizeInBytes >= 1024 * 1024) {
            double sizeInMB = sizeInBytes / (1024.0 * 1024);
            return String.format("%.1f MB", sizeInMB);
        } else if (sizeInBytes >= 1024) {
            double sizeInKB = sizeInBytes / 1024.0;
            return String.format("%.1f KB", sizeInKB);
        } else {
            return sizeInBytes + " B";
        }
    }

    public static <T> T fromJson_IncludeSourceOnError(String json, Class<T> classe) {
        try {
            if (hasJsonBeanAnnotation(classe)) {
                classe = getJsonBeanTargetClass(classe);
            }
            return OBJECT_MAPPER_WITH_INCLUDE_SOURCE_ON_ERROR.readValue(json, classe);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static ObjectMapper getSystemObjectMapper() {
        return CmJsonUtils.getObjectMapper();
    }

}
