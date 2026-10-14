/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import static java.util.Collections.emptyMap;
import java.util.Map;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.lookup.LookupValue;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_CLASSE_CONTEXT_MENU_ITEMS_JS_COMPONENT_ID_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_CLASSE_CONTEXT_MENU_ITEMS_JS_COMPONENT_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_CLASSE_CONTEXT_MENU_ITEMS_JS_DESKTOP_COMPONENT_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_CLASSE_CONTEXT_MENU_ITEMS_JS_MOBILE_COMPONENT_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_NAME_SERIALIZATION;
import org.cmdbuild.modeldiff.schema.SchemaContextMenuComponentConfiguration;
import org.cmdbuild.modeldiff.schema.SchemaContextMenuItemConfiguration;
import static org.cmdbuild.sync.ContextMenuComponentHandler.isRunningUnderUnitTest;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.cmdbuild.uicomponents.data.UiComponentData;
import org.cmdbuild.uicomponents.data.UiComponentDataImpl;
import org.cmdbuild.uicomponents.data.UiComponentDataImpl.UiComponentDataImplBuilder;
import static org.cmdbuild.uicomponents.data.UiComponentType.UCT_CONTEXTMENU;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>Concrete placeholder</b> for visitable (to engage polymorphism and choice
 * of visitor in related repository), wrap for a real <i>schema</i>
 * <i>context menu items</i> stuff, composed of {@link LookupValue} items.
 *
 * @author afelice
 */
public class CmContextMenuItemsSchemaNode extends CmSchemaItemDataNode {

    protected static final Logger logger = LoggerFactory.getLogger(CmContextMenuItemsSchemaNode.class);

    private static final Md5Helper md5Helper = Md5Helper.getInstance();

    public CmContextMenuItemsSchemaNode(CmSchemaItemAttributesData itemData) {
        super(itemData);
    }

    @Override
    public void addComponent(CmSchemaItemAttributesDataNode component) {
        super.addComponent(component);
        logComponentJS("", component);
    }

    /**
     * Used converting from {@link GeneratedDiffSchema_Component} to a
     * {@link CmContextMenuItemsSchemaNode}.
     *
     * @param props
     * @return
     */
    public static String fetchComponentId(Map<String, Object> props) {
        return (String) props.get(ATTR_CLASSE_CONTEXT_MENU_ITEMS_JS_COMPONENT_ID_SERIALIZATION);
    }

    /**
     * Used in <i><b>compare</> step</i>.
     *
     * <p>
     * Handles from base64 (to represent a Zip file JS script in json
     * serialization) to MD5 (to easily compare the JS script content)
     *
     * <p>
     * <b>Notes</b>:
     * <ol>
     * <li>for {@link UiComponentData}s containing a Zip file JS script, handles
     * from base64 (to represent a Zip file JS script in json serialization) to
     * MD5 (to easily compare the JS script content).
     * <li>artificially adds <code>name</code>, to be used as
     * <i>distinguishing name</i> while <i>compare</i> and then <i>apply
     * diff</i>: <i>CMDBuild serialization</i> for {@link UiComponentData}
     * contains a <code>componentId</code> but not a <code>name</code>.
     * <li>contains:
     * <ul>
     * <li>a list of {@link CmSchemaItemAttributesDataNode} for each of
     * contained {@link UiComponentData}s. <b>note</b>:
     * <li>{@link UiComponentInfo} <code>componentId</code> is used as
     * <code>distinguishingName</code>, because <i>CMDBuild serialization</i>
     * for {@link UiComponentInfo} (in {@link Classe} <i>property</i>
     * <code>contextMenuItems</code> contains a <code>componentId</code> but not
     * a <code>name</code>.
     * </ul>
     * </ol>
     *
     * @param genSchemaContextMenuItem
     * @return
     */
    public static CmContextMenuItemsSchemaNode toContextMenuItemNode(SchemaContextMenuItemConfiguration genSchemaContextMenuItem) {

        CmContextMenuItemsSchemaNode contextMenuItemNode = new CmContextMenuItemsSchemaNode(
                CmSchemaItemAttributesData.from("components", emptyMap())
        );

        // JS components
        genSchemaContextMenuItem.components.forEach(
                genSchemaJsComponent -> {
                    Map<String, Object> jsComponentSerialization = fromJson(toJson(genSchemaJsComponent), MAP_OF_OBJECTS);
                    final String componentId = fetchComponentId(jsComponentSerialization);

                    // "scripts" with base64 stuff
                    // Add synthesized name
                    jsComponentSerialization.put(ATTR_NAME_SERIALIZATION, componentId);
                    // Use md5 for JS script and store base64 as originalJson
                    Map<String, Object> originalJsonSerialization = map(jsComponentSerialization);
                    Map<String, String> originalJsScriptsSerialization = ((Map<String, String>) jsComponentSerialization.get(ATTR_CLASSE_CONTEXT_MENU_ITEMS_JS_COMPONENT_SERIALIZATION));

                    logger.info("JS Component =< {} >, building Md5 representation", componentId);
                    jsComponentSerialization.put(ATTR_CLASSE_CONTEXT_MENU_ITEMS_JS_COMPONENT_SERIALIZATION, toMd5Str(originalJsScriptsSerialization));
                    contextMenuItemNode.addComponent(new CmSchemaItemAttributesDataNode(
                            CmSchemaItemAttributesData.from(
                                    genSchemaJsComponent.getComponentId(),
                                    jsComponentSerialization, // md5 of base64, used for comparison
                                    originalJsonSerialization) // serializable base64, used to re-hydrate back to Json compare output
                    ));
                }
        );

        return contextMenuItemNode;
    }

    /**
     * <b>Used in <i>collect step</i></b>.
     *
     * <p>
     * Converts JS script <code>byte[]</code> representation to
     * <code>base64</code> {@link String}.
     *
     * @param contextMsg current <i>step</i>
     * (<code>collect</code>/<code>compare</code>/<code>applyDiff</code>).
     * @param componentData component data, containing <i>JS scripts</i> for
     * each supported target devices.
     * @return
     */
    public static SchemaContextMenuComponentConfiguration buildComponentConfiguration(String contextMsg, UiComponentData componentData) {
        SchemaContextMenuComponentConfiguration componentConfiguration = new SchemaContextMenuComponentConfiguration(componentData.getName(), componentData.getActive());

        // JS: from byte[] to base64 representation
        Map<TargetDevice, String> scriptItems = toScriptItemsStr(contextMsg, componentData);

        componentConfiguration.setScriptByTargetDevice(scriptItems);

        return componentConfiguration;
    }

    /**
     * Used in <i><b>applyDiff</b> step</i>.
     *
     * <p>
     * Reverts JS script <code>base64</code> representation to
     * <code>byte[]</code>.
     *
     * <p>
     * If in unit-testing, skip zip check because adding a
     * {@link UiComponentData} based on data found in the <i>schema
     * configuration</i>.
     *
     * @param contextMsg current <i>step</i>
     * (<code>collect</code>/<code>compare</code>/<code>applyDiff</code>).
     * @param jsComponentSchemaSerialization
     * @return
     */
    public static UiComponentData buildUiComponentData_JSComponent(String contextMsg, Map<String, Object> jsComponentSchemaSerialization) {
        UiComponentDataImplBuilder builder = buildUiComponentDataBuilder();

        String componentId = fetchComponentId(jsComponentSchemaSerialization);
        return buildUiComponentData_JSComponent(componentId, contextMsg, builder, jsComponentSchemaSerialization);
    }

    /**
     * Used in <i><b>applyDiff</b> step</i>, <b>without Zip normalization</b>
     * done in {@link UiComponentUtils#normalizeComponentData(data)}.
     *
     * <p>
     * Reverts JS script <code>base64</code> representation to
     * <code>byte[]</code>.
     *
     * <p>
     * If in unit-testing, skip zip check because adding a
     * {@link UiComponentData} based on data found in the <i>schema
     * configuration</i>.
     *
     * @param contextMsg current <i>step</i>
     * (<code>collect</code>/<code>compare</code>/<code>applyDiff</code>).
     * @param jsComponentSchemaSerialization
     * @return
     */
    public static UiComponentData buildUiComponentData_JSComponent_Synthesized(String contextMsg, Map<String, Object> jsComponentSchemaSerialization) {
        UiComponentDataImplBuilder builder = buildUiComponentDataBuilder(true);

        String componentId = fetchComponentId(jsComponentSchemaSerialization);
        return buildUiComponentData_JSComponent(componentId, contextMsg, builder, jsComponentSchemaSerialization);
    }

    /**
     * Used in <i><b>applyDiff</b> step</i>.
     *
     * <p>
     * Reverts JS script <code>base64</code> representation to
     * <code>byte[]</code>.
     *
     * @param contextMsg current <i>step</i>
     * (<code>collect</code>/<code>compare</code>/<code>applyDiff</code>).
     * @param builder
     * @param componentId
     * @param jsComponentSchemaSerialization
     * @return
     */
    public static UiComponentData buildUiComponentData_JSComponent(String componentId, String contextMsg, UiComponentDataImplBuilder builder, Map<String, Object> jsComponentSchemaSerialization) {
        builder.withName(componentId).withType(UCT_CONTEXTMENU);

        // Reverts JS script base64 representation to byte[]
        Map<TargetDevice, byte[]> jsData = map();
        if (jsComponentSchemaSerialization.containsKey(ATTR_CLASSE_CONTEXT_MENU_ITEMS_JS_COMPONENT_SERIALIZATION)) {
            Map<String, Object> scriptBytesColl = (Map<String, Object>) jsComponentSchemaSerialization.get(ATTR_CLASSE_CONTEXT_MENU_ITEMS_JS_COMPONENT_SERIALIZATION);
            fetchScript(componentId, contextMsg, jsData, scriptBytesColl, ATTR_CLASSE_CONTEXT_MENU_ITEMS_JS_DESKTOP_COMPONENT_SERIALIZATION, TargetDevice.TD_DEFAULT);
            fetchScript(componentId, contextMsg, jsData, scriptBytesColl, ATTR_CLASSE_CONTEXT_MENU_ITEMS_JS_MOBILE_COMPONENT_SERIALIZATION, TargetDevice.TD_MOBILE);
            builder.withData(jsData);
        }

        return builder.build();
    }

    /**
     * Adds the <code>byte[]</code> JS script for:
     * <ol>
     * <li>{@link TargetDevice#TD_DEFAULT} -- the desktop JS script;
     * <li>{@link TargetDevice#TD_MOBILE} -- the mobile JS script.
     * </ol>
     *
     * @param resultJsData <b>Note</b>: modified by current method
     * @param fromJsonJsData
     * @param jsonAttrNameSerialization key in <code>"script"</code> Json
     * attribute to fetch (<code>base64</code> JS from.
     * @param targetDevice
     */
    private static void fetchScript(String componentId, String contextMsg, Map<TargetDevice, byte[]> resultJsData, Map<String, Object> fromJsonJsData, String jsonAttrNameSerialization, final TargetDevice targetDevice) {
        if (fromJsonJsData.containsKey(jsonAttrNameSerialization)) {
            String scriptBase64Str = (String) fromJsonJsData.get(jsonAttrNameSerialization);
            byte[] scriptBytes = fromScriptStr_WithLog(componentId, contextMsg, targetDevice, scriptBase64Str);
            resultJsData.put(targetDevice, scriptBytes);
        }
    }

    /**
     * A <code>byte[]</code> is not storable in a Json. So a <code>Base64</code>
     * encoding is applied.
     *
     * @param scriptBytes
     * @return a <code>base64</code> representation of input <code>bytes</code>.
     */
    public static String toScriptStr(byte[] scriptBytes) {
        return Base64.getEncoder().encodeToString(scriptBytes);
    }

    /**
     * Fetch a script from a Json stored <code>Base64</code> encoding.
     *
     * @param aBase64ScriptStr
     * @return
     */
    public static byte[] fromScriptStr(String aBase64ScriptStr) {
        return Base64.getDecoder().decode(aBase64ScriptStr);
    }

    /**
     * <i>MD5</i> is an efficient way to compare a (possibly) long array of
     * bytes.
     *
     * @param originBytes
     * @return
     */
    private static byte[] md5(byte[] originBytes) {
        return md5Helper.md5(originBytes);
    }

    /**
     * <b>Used in <i>compare step</i></b>.
     *
     * Applies <i>MD5</i> to all given map values, and returns a map with a
     * String (easily and lightly comparable) JS script representation.
     *
     * @param originalJsScriptsSerialization serialization containing
     * <code>base64</code> JS Script serialization.
     * @return
     */
    public static Map<String, String> toMd5Str(Map<String, String> originalJsScriptsSerialization) {
        Map<String, String> result = map(originalJsScriptsSerialization);
        result.replaceAll((key, scriptBase64Str) -> toMd5Str(scriptBase64Str.getBytes(), "target =< %s>, base64".formatted(key)));

        return result;
    }

    public static String toMd5Str(byte[] inputBytes, String msg) {
        final String resultHex = md5Helper.toMd5Str(inputBytes);
        logger.info("MD5: from {} to md5 (hex) =< {} >", buildLogBytes(inputBytes, msg), resultHex);

        return resultHex;
    }

//    /**
//     * Compare string representation (<code>base64</code>) of
//     * <code>byte[]</code> using their respective <code>MD5</code> hashing.
//     *
//     * @param aBase64Str
//     * @param anotherBase64Str
//     * @return
//     */
//    boolean equalsBytes(String aBase64Str, String anotherBase64Str) {
//        // @todo AFE tbc
//        Decoder base64Decoder = Base64.getDecoder();
//
//        byte[] aBytes = base64Decoder.decode(aBase64Str);
//        byte[] anotherBytes = base64Decoder.decode(anotherBase64Str);
//
//        return Arrays.equals(md5Helper.md5(aBytes), md5Helper.md5(anotherBytes));
//    }
    /**
     * Used in <i><b>collect</b> step</i>.
     *
     * <p>
     * From <code>byte[]</code> to <code>base64</code> representation.
     *
     * @param componentData
     * @return
     */
    private static Map<TargetDevice, String> toScriptItemsStr(String contextMsg, UiComponentData componentData) {
        Map<TargetDevice, String> scriptItems = map();

        String componentId = componentData.getName();
        // UiComponentData contains both JS scripts: default (desktop UI) and mobile
        // Defauilt (UI Desktop)
        if (!isNullOrEmpty(componentData.getDataDefault())) {
            final String scriptStr = toScriptStr_WithLog(componentId, contextMsg, TargetDevice.TD_DEFAULT, componentData.getDataDefault());
            scriptItems.put(TargetDevice.TD_DEFAULT, scriptStr);
        }
        // Mobile
        if (!isNullOrEmpty(componentData.getDataMobile())) {
            final String scriptStr = toScriptStr_WithLog(componentId, contextMsg, TargetDevice.TD_MOBILE, componentData.getDataMobile());
            scriptItems.put(TargetDevice.TD_MOBILE, scriptStr);
        }
        return scriptItems;
    }

    /**
     * Used in <i><b>collect</b> step</i>.
     *
     * <p>
     * From <code>byte[]</code> to <code>base64</code> representation.
     *
     * @param targetDevice
     * @param scriptBytes
     * @return
     */
    private static String toScriptStr_WithLog(String componentId, String contextMsg, TargetDevice targetDevice, byte[] scriptBytes) {
        String scriptBase64Str = toScriptStr(scriptBytes);
        String scriptMd5Str = toMd5Str(scriptBase64Str.getBytes(), "base64");
        logComponentJS(componentId, contextMsg, targetDevice, scriptBase64Str, scriptMd5Str);
        return scriptBase64Str;
    }

    /**
     * Used in <i><b>applyDiff</b> step</i>.
     *
     * <p>
     * From <code>base64</code> to <code>byte[]</code> representation.
     *
     * @param targetDevice
     * @param scriptStr
     * @return
     */
    private static byte[] fromScriptStr_WithLog(String componentId, String contextMsg, TargetDevice targetDevice, String scriptBase64Str) {
        String scriptMd5Str = toMd5Str(scriptBase64Str.getBytes(), "base64");
        byte[] scriptBytes = logComponentJS(componentId, contextMsg, targetDevice, scriptBase64Str, scriptMd5Str);
        return scriptBytes;
    }

    public final static int SCRIPT_PREVIEW_LENGTH = 10;

    /**
     * @param targetDevice
     * @param scriptBase64Str
     * @param toCompareScriptMd5Str
     */
    private static byte[] logComponentJS(String componentId, String contextMsg, TargetDevice targetDevice, String scriptBase64Str, String toCompareScriptMd5Str) {
        byte[] origScriptBytes = fromScriptStr(scriptBase64Str);

//        // Write to temporary binary file
//        if (!isRunningUnderUnitTest()) {
//            CmIoUtils.tempFile("JSComponent_%s_%s_%s".formatted(contextMsg, componentId, serializeEnum(targetDevice)), "zip", origScriptBytes);
//        }

        String scriptStrLog = buildLogBytes(origScriptBytes, "bytes");
        String scriptBase64StrLog = buildLogBytes(scriptBase64Str.getBytes(), "base64");
        logger.info("JS Component: target =< {} > {}, {}, md5 (hex) =< {} >",
                serializeEnum(targetDevice),
                scriptStrLog,
                scriptBase64StrLog,
                toCompareScriptMd5Str);

        return origScriptBytes;
    }

    private static String buildLogBytes(byte[] scriptBytes, String msg) {
        String scriptStr = new String(scriptBytes, StandardCharsets.UTF_8);
        String scriptStrLog = scriptStr.length() < 2 * SCRIPT_PREVIEW_LENGTH
                ? " is =< %s >".formatted(scriptStr)
                : " starts with =< %s >, ends with =< %s >".formatted(scriptStr.substring(0, SCRIPT_PREVIEW_LENGTH), scriptStr.substring(scriptStr.length() - SCRIPT_PREVIEW_LENGTH));
        return "%s [%d]%s".formatted(msg, scriptBytes.length, scriptStrLog);
    }

    /**
     * Used in <i><b>compare</b> step</i>.
     *
     * @param dataNode
     */
    private void logComponentJS(String contextMsg, CmSchemaItemAttributesDataNode dataNode) {
        final CmSchemaItemAttributesData modelObj = dataNode.getModelObj();
        logComponentJS(contextMsg, modelObj.getAttributesSerialization(), modelObj.getOrigJsonValues());
    }

    /**
     * @param toCompareJsComponentSerialization md5 of base64, used for
     * comparison
     * @param originalJsonSerialization serializable base64, used to re-hydrate
     * back to Json compare output
     */
    private static void logComponentJS(String contextMsg, Map<String, Object> toCompareJsComponentSerialization, Map<String, Object> originalJsonSerialization) {
        String componentId = fetchComponentId(originalJsonSerialization);

        logComponentJS(componentId, contextMsg, TargetDevice.TD_DEFAULT, toCompareJsComponentSerialization, originalJsonSerialization);
        logComponentJS(componentId, contextMsg, TargetDevice.TD_MOBILE, toCompareJsComponentSerialization, originalJsonSerialization);
    }

    private static void logComponentJS(String componentId, String contextMsg, TargetDevice targetDevice, Map<String, Object> toCompareJsComponentSerialization, Map<String, Object> originalJsonSerialization) {
        final String key = serializeEnum(targetDevice);
        if (toCompareJsComponentSerialization.containsKey(key) && originalJsonSerialization.containsKey(key)) {
            final String actualToCompareJsonMd5Str = (String) toCompareJsComponentSerialization.get(key);
            String originalBase64ScriptStr = (String) originalJsonSerialization.get(key);
            byte[] originalScriptBytes = logComponentJS(componentId, contextMsg, targetDevice, originalBase64ScriptStr, actualToCompareJsonMd5Str);
        }
    }

    /**
     *
     * @return a builder; if in unit-testing, skips the zip format * stuff done
     * in {@link UiComponentDataImpl#normalizeComponentData(java.util.Map)}.
     */
    static private UiComponentDataImplBuilder buildUiComponentDataBuilder() {
        UiComponentDataImplBuilder builder = UiComponentDataImpl.builder();
        builder.withSynthesized(isRunningUnderUnitTest());

        return builder;
    }

    /**
     *
     * @return a builder; if in unit-testing, skips the zip format * stuff done
     * in {@link UiComponentDataImpl#normalizeComponentData(java.util.Map)}.
     */
    static private UiComponentDataImplBuilder buildUiComponentDataBuilder(boolean bSynthesized) {
        UiComponentDataImplBuilder builder = UiComponentDataImpl.builder();
        builder.withSynthesized(bSynthesized || isRunningUnderUnitTest());

        return builder;
    }

    static private Map<String, Object> getCmdbSerialization(Object obj, ObjectMapper objectMapper) {
        return objectMapper.convertValue(obj, new TypeReference<Map<String, Object>>() {
        });
    }

} // end CmContextMenuItemsSchemaNode class

///**
// * Used when merging from <i>schema</i>, skips the zip format stuff done in
// * {@link UiComponentDataImpl#normalizeComponentData(java.util.Map)}.
// *
// * @author afelice
// */
//// @todo AFE Errore: con discovery dei mapping fatto in CardMapperServiceImpl, si ottiene "duplicate mapper found for interface" all'avvio di CMDBuild
////@CardMapping(UI_COMPONENT_TABLE_NAME)
//@Primary
//class UiComponentDataImpl_Schema extends UiComponentDataImpl {
//
//    protected UiComponentDataImpl_Schema(UiComponentDataImplBuilder builder) {
//        super(builder);
//    }
//
//    protected UiComponentDataImpl_Schema(UiComponentDataImpl_SchemaBuilder builder) {
//        super(toParentBuilder(builder));
//    }
//
//    private static UiComponentDataImplBuilder toParentBuilder(UiComponentDataImpl_SchemaBuilder thisBuilder) {
//                return new UiComponentDataImplBuilder()
//                .withId(thisBuilder.id)
//                .withName(thisBuilder.name)
//                .withDescription(thisBuilder.description)
//                .withLastUpdated(thisBuilder.lastUpdated)
//                .withActive(thisBuilder.isActive)
//                .withDataDefault(thisBuilder.dataDefault)
//                .withDataMobile(thisBuilder.dataMobile)
//                .withType(thisBuilder.type);
//    }
//
//    @Override
//    protected Map<TargetDevice, byte[]> normalizeComponentData(Map<TargetDevice, byte[]> data) {
//        // Skip the zip format stuff done in UiComponentDataImpl.normalizeComponentData()
//        return data;
//    }
//
//    public static UiComponentDataImpl_SchemaBuilder schemaBuilder() {
//        return new UiComponentDataImpl_SchemaBuilder();
//    }
//
//    /**
//     * Same as {UiComponentDataImpl_SchemaBuilder}
//     */
//    public static class UiComponentDataImpl_SchemaBuilder implements Builder<UiComponentDataImpl_Schema, UiComponentDataImpl_SchemaBuilder> {
//
//        private Long id;
//        private String name;
//        private String description;
//        private ZonedDateTime lastUpdated;
//        private byte[] dataMobile, dataDefault;
//        private Boolean isActive;
//        private UiComponentType type;
//
//        public UiComponentDataImpl_SchemaBuilder withId(Long id) {
//            this.id = id;
//            return this;
//        }
//
//        public UiComponentDataImpl_SchemaBuilder withActive(Boolean isActive) {
//            this.isActive = isActive;
//            return this;
//        }
//
//        public UiComponentDataImpl_SchemaBuilder withName(String name) {
//            this.name = name;
//            return this;
//        }
//
//        public UiComponentDataImpl_SchemaBuilder withDescription(String description) {
//            this.description = description;
//            return this;
//        }
//
//        public UiComponentDataImpl_SchemaBuilder withLastUpdated(ZonedDateTime lastUpdated) {
//            this.lastUpdated = checkNotNull(lastUpdated);
//            return this;
//        }
//
//        public UiComponentDataImpl_SchemaBuilder withDataDefault(byte[] data) {
//            this.dataDefault = data;
//            return this;
//        }
//
//        public UiComponentDataImpl_SchemaBuilder withDataMobile(byte[] data) {
//            this.dataMobile = data;
//            return this;
//        }
//
//        public UiComponentDataImpl_SchemaBuilder withoutDataForTargetDevice(TargetDevice targetDevice) {
//            switch (checkNotNull(targetDevice)) {
//                case TD_DEFAULT ->
//                    dataDefault = null;
//                case TD_MOBILE ->
//                    dataMobile = null;
//            }
//            return this;
//        }
//
//        public UiComponentDataImpl_SchemaBuilder withData(Map<TargetDevice, byte[]> data) {
//            return this.withDataDefault(data.get(TD_DEFAULT)).withDataMobile(data.get(TD_MOBILE));
//        }
//
//        public UiComponentDataImpl_SchemaBuilder withType(UiComponentType type) {
//            this.type = type;
//            return this;
//        }
//
//        @Override
//        public UiComponentDataImpl_Schema build() {
//            return new UiComponentDataImpl_Schema(this);
//        }
//
//    } // end UiComponentDataImpl_SchemaBuilder class
//} // end UiComponentDataImpl_Schema class

