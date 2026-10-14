/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.schema.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.Map;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_DESCRIPTION_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_NAME_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_PARENT_SERIALIZATION;
import org.cmdbuild.modeldiff.core.serializer.ValuesSerializerVisitor;
import org.cmdbuild.modeldiff.diff.schema.CmSchemaItemAttributesData;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.WsAttributeData;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.utils.lang.LambdaExceptionUtils;

/**
 * <p>
 * Similar to CmCardAttributesDeltaSerializer.
 *
 * @author afelice
 */
public class CmSchemaItemAttributesDataSerializerHelper {

    /**
     * To hold a custom {@link SimpleModule} with the custom json serializers.
     */
    private static final ObjectMapper customObjectMapper = getSystemObjectMapper().copy();

    private final JsonGenerator jsonGenerator;
    private final ValuesSerializerVisitor valuesSerializerVisitor;

    public CmSchemaItemAttributesDataSerializerHelper(JsonGenerator jsonGenerator) {
        this.jsonGenerator = jsonGenerator;
        this.valuesSerializerVisitor = new ValuesSerializerVisitor(jsonGenerator, (runnable) -> {
            rethrowExc(runnable);
        });
    }

    public JsonGenerator getJsonGenerator() {
        return jsonGenerator;
    }

    public ValuesSerializerVisitor getValuesSerializerVisitor() {
        return valuesSerializerVisitor;
    }

    /**
     * ObjectMapper hasn't a custom module de-registration: work on a copy            
     * 
     * @return 
     */
    public ObjectMapper getObjectMapper() {
        return customObjectMapper;
    }

    /**
     * Uses a {@link ObjectMapper} copy to register a custom module with given
     * custom json serializers.
     *
     *
     * @param myModuleName
     * @param jsonSerializers
     */
    public synchronized void registerJsonSerializer(String myModuleName,
            Map<Class, JsonSerializer<?>> jsonSerializers) {
        if (!customObjectMapper.getRegisteredModuleIds().contains(myModuleName)
                && !jsonSerializers.isEmpty()) {
            SimpleModule myModule = new SimpleModule(myModuleName);
            jsonSerializers.forEach(myModule::addSerializer);

            // ObjectMapper hasn't a custom module de-registration: work on a copy            
            customObjectMapper.registerModule(myModule);
        }
    }

    public <E extends Exception> void rethrowExc(LambdaExceptionUtils.Runnable_WithExceptions<E> runnable) {
        try {
            runnable.run();
        } catch (Exception ex) {
            throw runtime(ex, "error serializing data diff to json - %s".formatted(ex));
        }
    }

    /**
     * Used in item serialization.
     *
     * @param schemaItemAttributesData
     * @return
     */
    public Map<String, Object> getItemInfo(CmSchemaItemAttributesData schemaItemAttributesData) {
        FluentMap<String, Object> result = map(
                ATTR_NAME_SERIALIZATION, schemaItemAttributesData.getAttributesSerialization().get(ATTR_NAME_SERIALIZATION),
                ATTR_DESCRIPTION_SERIALIZATION, schemaItemAttributesData.getDescription()
        );

        // Used in merge (apply change) algorithm, to easily detect Classes ancestors
        if (schemaItemAttributesData.getAttributesSerialization().containsKey(ATTR_PARENT_SERIALIZATION)) {
            result.with(ATTR_PARENT_SERIALIZATION, schemaItemAttributesData.getAttributesSerialization().get(ATTR_PARENT_SERIALIZATION));
        }

        return result;
    }

    /**
     * {@link Classe} <i>metadata</i> is exploded in CMDBuild serialization and
     * needs to be build again before <i>adding</i>/<i>updating</i>.
     *
     * @param attributeCmdbSerialization
     * @return
     */
    public static ClassSerializationHelper.WsClassData buildClasseData(Map<String, String> attributeCmdbSerialization) {
        // Reconstructs metadata from Classe serialization        
        return customObjectMapper.convertValue(attributeCmdbSerialization, ClassSerializationHelper.WsClassData.class);
    }

    // @todo AFE TBC
    public static WsAttributeData buildAttributeData(Map<String, String> attributeCmdbSerialization) {
        // Reconstructs metadata from Classe serialization        
        return customObjectMapper.convertValue(attributeCmdbSerialization, WsAttributeData.class);
    }

    private static ObjectMapper getSystemObjectMapper() {
        return CmJsonUtils.getObjectMapper();
    }

} // end CmSchemaItemAttributesDataSerializerHelper class

// @todo AFE TBC
//class ClasseMetadataFetcher {
//    FluentMap<String, String> fetch(Map<String, Object> cmdbSerialization) {
//        FluentMap<String, String> result = map();
//        result.with(
//                "defaultFilter", cmdbSerialization.get("defaultFilter"),
//                "defaultImportTemplate", cmdbSerialization.get("defaultImportTemplate"),
//                "defaultExportTemplate", cmdbSerialization.get("defaultExportTemplate"),
//                "_icon", cmdbSerialization.get("_icon"),
//                "uiRouting_mode", cmdbSerialization.get("uiRouting_mode"),
//                "uiRouting_target", cmdbSerialization.get("uiRouting_target"),
//                "uiRouting_custom", cmdbSerialization.get("uiRouting_custom"),
//                "noteInline", cmdbSerialization.get("noteInline"),
//                "noteInlineClosed", cmdbSerialization.get("noteInlineClosed"),
//                "attachmentsInline", cmdbSerialization.get("attachmentsInline"),
//                "attachmentsInlineClosed", cmdbSerialization.get("attachmentsInlineClosed"),
//                "validationRule", cmdbSerialization.get("validationRule"),
//                "stoppableByUser", cmdbSerialization.get("stoppableByUser"),
//                "barcodeSearchAttr", cmdbSerialization.get("barcodeSearchAttr"),
//                "barcodeSearchRegex", cmdbSerialization.get("barcodeSearchRegex"),
//                "domainOrder", cmdbSerialization.get("domainOrder"),
//                "help", cmdbSerialization.get("help"),
//                "autoValue", cmdbSerialization.get("autoValue"),
//                "allowedExtensions", cmdbSerialization.get("allowedExtensions")
//        );
//        return result;
//    }
//}
//
//class DmsModelMetadataFetcher extends ClasseMetadataFetcher {
//
//    FluentMap<String, String> fetch(Map<String, Object> cmdbSerialization) {
//
//        return super.fetch(cmdbSerialization).with(
//                "checkCount", cmdbSerialization.get("checkCount"),
//                "checkCountNumber", cmdbSerialization.get("checkCountNumber"),
//                "maxFileSize", cmdbSerialization.get("maxFileSize")
//        );
//    }
//}
