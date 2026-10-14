/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.ProcessingException;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import org.apache.commons.io.FilenameUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trim;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.etl.config.WaterwayDescriptor;
import static org.cmdbuild.etl.config.WaterwayDescriptor.WY_DESCRIPTOR_CODE;
import static org.cmdbuild.etl.config.WaterwayDescriptor.WY_DESCRIPTOR_DESCRIPTION;
import org.cmdbuild.etl.config.WaterwayDescriptorMeta;
import org.cmdbuild.etl.config.WaterwayDescriptorMetaImpl;
import static org.cmdbuild.etl.config.WaterwayDescriptorMetaImpl.empty;
import org.cmdbuild.etl.config.WaterwayItem;
import static org.cmdbuild.etl.config.WaterwayItem.WY_ITEM_TAG;
import org.cmdbuild.etl.config.WaterwayItemConfig;
import org.cmdbuild.etl.config.WaterwayItemType;
import static org.cmdbuild.etl.config.WaterwayItemType.NESTED_TYPES;
import static org.cmdbuild.etl.config.WaterwayItemType.SECOND_LEVEL_TYPES;
import static org.cmdbuild.etl.config.WaterwayItemType.TOP_LEVEL_TYPES;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_TRIGGER;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorImpl;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecord;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecordImpl;
import org.cmdbuild.etl.config.inner.WaterwayItemImpl;
import org.cmdbuild.etl.config.itemkey.ItemKey;
import org.cmdbuild.etl.config.itemkey.ItemKeyUtils;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.fromYaml;
import static org.cmdbuild.utils.json.CmJsonUtils.isJson;
import static org.cmdbuild.utils.json.CmJsonUtils.jsonValueToString;
import static org.cmdbuild.utils.json.CmJsonUtils.jsonValueToStringOrEmpty;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toYaml;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrNull;
import org.cmdbuild.utils.lang.CmException;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.parseErrorCode;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.equal;
import static org.cmdbuild.utils.lang.CmStringUtils.*;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.unsafeConsumer;
import static org.quartz.CronExpression.validateExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaterwayDescriptorUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final JsonNode WY_CONFIG_SCHEMA = fromJson(readToString(WaterwayDescriptorUtils.class.getResourceAsStream("/org/cmdbuild/etl/config/schema.json")), JsonNode.class),
            WY_PARAMS_SCHEMA = fromJson(readToString(WaterwayDescriptorUtils.class.getResourceAsStream("/org/cmdbuild/etl/config/params.json")), JsonNode.class);
    private static final String WY_CONFIG_HEADER, WY_PARAMS_HEADER;

    static {
        Matcher matcher = Pattern.compile("#\\s+yaml-language-server.*").matcher(WaterwayDescriptorUtils.getDescriptorTemplate());
        checkArgument(matcher.find());
        WY_CONFIG_HEADER = checkNotBlank(matcher.group());
        WY_PARAMS_HEADER = WY_CONFIG_HEADER.replaceFirst("schema[.]json$", "params.json");
    }

    public static String checkIsValidItemCode(String code) {
        return ItemKeyUtils.checkIsValidItemCode(code);
    }

    public static void checkDescriptorFileName(String name, @Nullable String code) {
        checkArgument(isBlank(code) || Objects.equals(name, buildDescriptorFilename(code)), "invalid bus descriptor file name =< %s > for code =< %s >", name, code);
    }

    public static boolean isDescriptorFilename(@Nullable String name) {
        return isNotBlank(name) && Objects.equals(FilenameUtils.getExtension(name), "yaml");
    }

    public static String buildDescriptorFilename(String code) {
        return format("%s.yaml", checkNotBlank(code));
    }

    public static String getDescriptorCodeFromFilename(String filename) {
        return checkNotBlank(FilenameUtils.getBaseName(filename));
    }

    public static String getDescriptorCodeFromFilename(DataSource dataSource) {
        return checkNotBlank(FilenameUtils.getBaseName(dataSource.getName()));
    }

    public static String buildDescriptorKey(String code, int version) {
        return ItemKeyUtils.buildDescriptorKey(code, version);
    }

    public static String getDescriptorCodeFromKey(String key) {
        return ItemKeyUtils.getDescriptorCodeFromKey(key);
    }

    public static String configItemKey(String descriptorKey, String code) {
        return ItemKeyUtils.configItemKey(descriptorKey, code);
    }

    public static ItemKey parseItemKey(String key) {
        return ItemKeyUtils.parseItemKey(key);
    }

    public static String getItemCodeFromKeyOrCode(String keyOrCode) {
        return ItemKeyUtils.getItemCodeFromKeyOrCode(keyOrCode);
    }

    public static boolean isItemKey(@Nullable String keyOrCode) {
        return ItemKeyUtils.isItemKey(keyOrCode);
    }

    public static String fixDescriptorDataForEdit(WaterwayDescriptorRecord record) {
        return fixDescriptorDataForEdit(record.getData());
    }

    public static String fixDescriptorDataForEdit(String config) {
        if (!isJson(config)) {
            return config;
        } else {
            return WY_CONFIG_HEADER + "\n" + toYaml(fromJson(config, JsonNode.class));
        }
    }

    public static String fixDescriptorDataForEditAndAddParams(WaterwayDescriptorRecord record) {
        return buildDescriptorDataAndParams(fixDescriptorDataForEdit(record.getData()), record);
    }

    public static String checkDescriptorData(String data, WaterwayDescriptorMeta meta) {
        readDescriptor(data, meta);
        return data;
    }

    public static String checkDescriptorData(String data) {
        return WaterwayDescriptorUtils.checkDescriptorData(data, WaterwayDescriptorMetaImpl.empty());
    }

    public static void checkDescriptorCode(String data, String code) {
        String thisCode = getDescriptorCodeOrNull(data);
        checkArgument(isBlank(thisCode) || Objects.equals(thisCode, code), "descriptor code mismatch, expected =< {} > actual =< {} >", code, thisCode);
    }

    @Nullable
    private static String getDescriptorCodeOrNull(String data) {
        if (hasDescriptorDataAndParams(data)) {
            data = splitDescriptorDataAndParams(data).getData();
        }
        ObjectNode node = fromYaml(data, ObjectNode.class);
        return node.has(WY_DESCRIPTOR_CODE) ? jsonValueToString(node.get(WY_DESCRIPTOR_CODE)) : getTypeAndCode(node, false).map(Pair::getRight).orElse(null);
    }

    public static boolean hasDescriptorDataAndParams(@Nullable String data) {
        if (!Pattern.compile("(?m)(?s).*^---.*^---.*").matcher(nullToEmpty(data)).matches()) {
            return false;
        } else {
            try {
                splitDescriptorDataAndParams(data);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    public static Optional<Pair<WaterwayItemType, String>> getTypeAndCode(ObjectNode node, boolean nested) {
        try {
            return (nested ? list(NESTED_TYPES).map(CmConvertUtils::serializeEnum).filter(node::has).collect(toOptional())
                    : list(TOP_LEVEL_TYPES).map(CmConvertUtils::serializeEnum).filter(node::has).collect(toOptional()).or(() -> list(SECOND_LEVEL_TYPES).map(CmConvertUtils::serializeEnum).filter(node::has).collect(toOptional())))
                    .map(k -> Pair.of(parseEnum(k, WaterwayItemType.class), checkNotBlank(jsonValueToString(node.get(k)))));
        } catch (Exception ex) {
            throw runtime(ex, "error getting type and code from node = %s", node);
        }
    }

    public static DescriptorDataAndMeta splitDescriptorDataAndParams(String data) {
        Matcher matcher = Pattern.compile("(?m)(?s).*(^---[^$]*?$.*)^---[^$]*$(.*)").matcher(data);
        checkArgument(matcher.matches(), "invalid config and data file pattern");
        String paramsStr = format("%s\n...", nullToEmpty(matcher.group(1))), dataStr = checkNotBlank(matcher.group(2));
        return new DescriptorDataAndMetaImpl(readDescriptorParams(paramsStr), dataStr);
    }

    public static WaterwayDescriptorMeta readDescriptorParams(String paramsStr) {
        try {
            JsonNode paramsNode = fromYaml(paramsStr, JsonNode.class);
            return WaterwayDescriptorMetaImpl.builder().accept(unsafeConsumer(b -> {
                if (!paramsNode.isNull() && !(paramsNode.isTextual() && isBlank(paramsNode.asText()))) {
                    validate(WY_PARAMS_SCHEMA, paramsNode);
                    Map<String, Object> params = fromJson(paramsNode, MAP_OF_OBJECTS);
                    b.withEnabled(toBooleanOrNull(params.get("enabled")))
                            .withDisabledItems(Splitter.on(",").omitEmptyStrings().trimResults().splitToList(nullToEmpty(configItemValueToString(params.get("disabled")))))//TODO improve this
                            .withParams((Map) map(params).withoutKeys("enabled", "disabled").mapValues(WaterwayDescriptorUtils::configItemValueToString));
                }
            })).build();
        } catch (Exception ex) {
            LOGGER.warn("error processing config file params = \n\n{}\n", paramsStr);
            throw runtime(ex, "error processing config params = %s", abbreviate(paramsStr));
        }
    }

    public static String buildDescriptorDataAndParams(String data, WaterwayDescriptorMeta meta) {
        return format("---\n%s\n# bus descriptor meta and params\n# (note: comments and syntax for params/meta are not preserved)\n\n%s\n---\n%s", WY_PARAMS_HEADER, trim(toYaml(map("enabled", meta.isEnabled(), "disabled", list(meta.getDisabledItems())).with(meta.getParams()))), data);
    }

    public static WaterwayDescriptor readDescriptor(String data) {
        return readDescriptor(data, WaterwayDescriptorMetaImpl.empty());
    }

    @Nullable
    public static String configItemValueToString(Object value) {
        if (value instanceof Iterable iterable) {
            return Joiner.on(",").join(iterable);//TODO improve to string/joiner
        } else {
            return toStringOrNull(value);
        }
    }

    public static WaterwayDescriptor readDescriptor(String data, WaterwayDescriptorMeta meta) {
        Map<String, String> params = meta.getParams();
        try {
            LOGGER.debug("processing bus descriptor data =\n\n{}\n", data);

            if (hasDescriptorDataAndParams(data)) {
                DescriptorDataAndMeta split = splitDescriptorDataAndParams(data);
                data = split.getData();
                params = map(split.getParams()).with(params);
            }

            ObjectNode jsonNode = fromYaml(data, ObjectNode.class);

            LOGGER.debug("loaded data = {}", jsonNode);

            LOGGER.debug("bus descriptor params =\n\n{}\n", mapToLoggableStringLazy(params));
            ObjectNode root = jsonNode;
            Map<String, String> allParams = mapOf(String.class, String.class).accept(m -> list(root.fields()).forEach(e -> {
                if (e.getValue() != null && e.getValue().isValueNode() && !jsonValueToStringOrEmpty(e.getValue()).matches("^[$][^$].*")) {
                    m.put(e.getKey(), jsonValueToString(e.getValue()));
                }
                if (e.getKey().matches("^[$][^$].*")) {
                    root.remove(e.getKey());
                }
            })).with(params);
            Map<String, String> paramVars = map(allParams).filterKeys(k -> !k.matches("^[$][^$].*"));
            paramVars.forEach(root::put);

            Map<String, String> paramExprs = map(allParams).filterKeys(k -> k.matches("^[$][^$].*"));
            DocumentContext jsonPath = JsonPath.parse(toJson(jsonNode));
            paramExprs.forEach((k, v) -> {
                Matcher matcher = Pattern.compile("^[$]([^$].*)[.]([^.]+)$").matcher(k);
                checkArgument(matcher.matches(), "invalid jsonpath expr =< %s >", k);
                String path = checkNotBlank(matcher.group(1)), key = checkNotBlank(matcher.group(2));
                LOGGER.debug("set path = < {}.{} > to value =< {} >", path, key, abbreviate(v));
                jsonPath.put(path, key, v);
            });
            jsonNode = fromJson(jsonPath.jsonString(), ObjectNode.class);

            LOGGER.debug("apply params expressions, params =\n\n{}\n", mapToLoggableStringLazy(paramVars));
            jsonNode = (ObjectNode) applyParamsExpressions(jsonNode, paramVars);

            if (!jsonNode.has("items") && list(TOP_LEVEL_TYPES).map(CmConvertUtils::serializeEnum).filter(jsonNode::has).size() == 1) {
                LOGGER.debug("detected single item format, processing...");
                ObjectNode wrapper = fromJson("{}", ObjectNode.class);//TODO improve this
                Map<String, Object> map = fromJson(jsonNode, MAP_OF_OBJECTS);
                list("description", "notes", "tag").filter(map::containsKey).forEach(key -> wrapper.put(key, toStringOrNull(map.get(key))));
                String type = list(TOP_LEVEL_TYPES).map(CmConvertUtils::serializeEnum).filter(map::containsKey).collect(onlyElement());
                wrapper.put(WY_DESCRIPTOR_CODE, toStringNotBlank(map.get(type)));
                jsonNode.remove("tag");
                ArrayNode items = wrapper.putArray("items");
                items.add(jsonNode);
                jsonNode = wrapper;
            }

            if (isBlank(jsonValueToString(jsonNode.get(WY_DESCRIPTOR_CODE)))) {
                jsonNode.put(WY_DESCRIPTOR_CODE, meta.getCode());
            }
            if (isBlank(jsonValueToString(jsonNode.get(WY_DESCRIPTOR_DESCRIPTION)))) {
                jsonNode.put(WY_DESCRIPTOR_DESCRIPTION, meta.getDescription());
            }

            LOGGER.debug("preprocessed data = {}", jsonNode);

            validate(WY_CONFIG_SCHEMA, jsonNode);

            WaterwayDescriptor waterwayDescriptor = fromJson(jsonNode, WaterwayDescriptorImpl.class);

            validateCronExpression(waterwayDescriptor);

            return waterwayDescriptor;
        } catch (CmException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.warn("error processing config file:\n\n{}\n with params = \n\n{}\n", data, mapToLoggableString(params));
            LOGGER.debug("exception - {}", ex);
            throw runtime(ex, "error processing config file =< %s > with params = %s", abbreviate(data), mapToLoggableStringInline(params));
        }
    }

    public static JsonNode applyParamsExpressions(JsonNode jsonNode, Map<String, String> params) {
        if (jsonNode.isArray()) {
            jsonNode.elements().forEachRemaining(e -> applyParamsExpressions(e, params));
        } else if (jsonNode.isObject()) {
            list(jsonNode.fields()).forEach(e -> {
                String k = e.getKey();
                JsonNode v = e.getValue();
                if (v.isValueNode()) {
                    String s = jsonValueToStringOrEmpty(v);
                    if (s.matches("^[$][^$].*")) {
                        String expr = s.substring(1);
                        if (params.containsKey(expr)) {
                            s = params.get(expr);
                        } else {
                            s = "";
                            LOGGER.warn(marker(), "missing value for param expression =< {} >", expr);
                        }
                        LOGGER.debug("set key = < {} > to value =< {} > for expr =< {} >", k, abbreviate(s), expr);
                        ((ObjectNode) jsonNode).put(k, s);
                    } else if (s.startsWith("$$")) {
                        ((ObjectNode) jsonNode).put(k, s.substring(1));
                    }
                } else {
                    applyParamsExpressions(v, params);
                }
            });
        }
        return jsonNode;
    }

    public static String getDescriptorTemplate() {
        return readToString(WaterwayDescriptorUtils.class.getResourceAsStream("/org/cmdbuild/etl/config/template.yaml"));
    }

    public static String getDescriptorTemplate(String codeOrFile) {
        String code = isDescriptorFilename(codeOrFile) ? getDescriptorCodeFromFilename(codeOrFile) : codeOrFile;
        return getDescriptorTemplate().replace("my_config_file", checkNotBlank(code));
    }

    public static List<WaterwayItem> loadItems(Collection<WaterwayDescriptorRecord> configs) {
        return new WaterwayConfigLoader().loadConfigItems(configs);
    }

    public static void checkItems(Collection<WaterwayDescriptorRecord> configs) {
        loadItems(configs);
    }

    public static WaterwayDescriptorRecord toRecord(String data, WaterwayDescriptorMeta meta, @Nullable WaterwayDescriptorRecord currentConfig) {
        checkArgument(!hasDescriptorDataAndParams(data), "invalid data: cannot convert data with params to record (extract params before)");
        WaterwayDescriptor config = readDescriptor(data, meta);
        checkArgument(currentConfig == null || Objects.equals(config.getCode(), currentConfig.getCode()));
        return (currentConfig == null ? WaterwayDescriptorRecordImpl.builder().withVersion(1).withCode(config.getCode()) : WaterwayDescriptorRecordImpl.copyOf(currentConfig).accept(b -> {
            if (Objects.equals(currentConfig.getData(), data)) {
                LOGGER.debug("config file not changed, keep current version = {}", currentConfig);
                b.withVersion(currentConfig.getVersion());
            } else {
                b.withVersion(currentConfig.getVersion() + 1);
            }
        }))
                .withDescription(config.getDescription())
                .withNotes(config.getNotes())
                .withData(data)
                .withParams(meta.getParams())
                .withEnabled(meta.isEnabled())
                .withDisabledItems(meta.getDisabledItems())
                .build();
    }

    public static String descriptorDataJsonToYaml(String value) {
        return isJson(value) ? toYaml(fromJson(value, JsonNode.class)) : value;
    }

    public static WaterwayDescriptorRecord prepareRecord(DataSource dataSource, @Nullable WaterwayDescriptorMeta meta, Function<String, WaterwayDescriptorRecord> curLoader, Supplier<List<WaterwayDescriptorRecord>> listLoader) {
        String data = readToString(dataSource);
        if (hasDescriptorDataAndParams(data)) {
            checkArgument(meta == null, "cannot set params in config file AND config file meta at the same time");
            DescriptorDataAndMeta split = splitDescriptorDataAndParams(data);
            data = split.getData();
            meta = split.getMeta();
        }
        String code;
        if (isNotBlank(dataSource.getName())) {
            code = getDescriptorCodeFromFilename(dataSource);
            checkArgument(meta == null || !meta.hasCode() || Objects.equals(code, meta.getCode()), "descriptor code mismatch");
            checkDescriptorCode(data, code);
        } else if (meta != null && meta.hasCode()) {
            code = meta.getCode();
            checkDescriptorCode(data, code);
        } else {
            code = checkNotBlank(getDescriptorCodeOrNull(data), "missing descriptor code");
        }
        WaterwayDescriptorRecord currentConfig = curLoader.apply(code);
        meta = WaterwayDescriptorMetaImpl.copyOf(firstNotNull(meta, currentConfig, empty())).withCode(code).build();
        WaterwayDescriptorRecord record = toRecord(data, meta, currentConfig);
        try {
            checkItems(list(listLoader.get()).without(equal(WaterwayDescriptorRecord::getCode, code)).with(record));
        } catch (Exception ex) {
            LOGGER.warn(marker(), "config record set validation error", ex);
        }
        return record;
    }

    public static WaterwayDescriptorRecord validateRecord(WaterwayDescriptorRecord record) {
        return WaterwayDescriptorRecordImpl.copyOf(record).accept(b -> {
            try {
                checkDescriptorData(record.getData(), record);
                b.withValid(true);
            } catch (Exception ex) {
                LOGGER.warn("invalid config record = {}", record, ex);
                b.withValid(false);
            }
        }).build();
    }

    public static List<WaterwayDescriptorRecord> loadSystemDescriptors() {
        try (ScanResult scanResult = new ClassGraph().acceptPackages("org.cmdbuild.etl.waterway.system").scan()) {
            return list(scanResult.getResourcesWithExtension("yaml").getPaths().stream().distinct())
                    .map(r -> toRecord(readToString(WaterwayDescriptorUtils.class.getResourceAsStream("/" + r)), WaterwayDescriptorMetaImpl.builder().withCode(getDescriptorCodeFromFilename(r)).build(), null))
                    .sorted(WaterwayDescriptorRecord::getCode);
        }
    }

    public static List<WaterwayDescriptorRecord> loadPluginDescriptors(Map<String, byte[]> busDescriptors) {
        return list(busDescriptors.entrySet().stream().map(entry -> {
            return toRecord(new String(entry.getValue(), StandardCharsets.UTF_8), WaterwayDescriptorMetaImpl.builder().withCode(getDescriptorCodeFromFilename(entry.getKey())).build(), null);
        })).sorted(WaterwayDescriptorRecord::getCode);
    }

    private static void validate(JsonNode schema, JsonNode jsonNode) throws ProcessingException {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);

        JsonSchema schemaFromNode = factory.getSchema(schema);
        schemaFromNode.initializeValidators();
        Set<ValidationMessage> validate = schemaFromNode.validate(jsonNode);

        LOGGER.debug("validate = {}", validate.isEmpty());
        checkArgument(validate.isEmpty(), "error processing wy config file: %s", list(validate).map(ValidationMessage::getMessage).collect(joining(", ")));
    }

    private static void validateCronExpression(WaterwayDescriptor waterwayDescriptor) {
        waterwayDescriptor.getItems().stream().filter(equal(WaterwayItemConfig::getType, WYCIT_TRIGGER)).forEach(wic -> {
            if (wic.hasConfigNotBlank("cronExpression")) {
                String cronExpression = wic.getConfig("cronExpression");
                if (!toBooleanOrDefault(wic.getConfig("cronExpressionHasSeconds"), false)) {
                    cronExpression = format("0 %s", cronExpression);
                }
                try {
                    LOGGER.debug("cron to validate =< {} >", cronExpression);
                    validateExpression(cronExpression);
                } catch (ParseException e) {
                    throw new CmException(parseErrorCode("801", "non-valid cron expression =< %s >", cronExpression));
                }
            }
        });
    }

    private static class WaterwayConfigLoader {

        private static final String WY_EXTENDS = "wyloader_extends";

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final Map<String, WaterwayItem> itemsByCode = map();
        private final Stack<String> extendsStack = new Stack<>();
        private final Set<String> disabledItems = set();
        private boolean disabledFile = false;
        private final Map<String, Map<String, String>> configsForImport = map();

        private String currentConfigFile;

        private List<WaterwayItem> loadConfigItems(Collection<WaterwayDescriptorRecord> records) {
            list(records).sorted(WaterwayDescriptorRecord::getCode).forEach(r -> {
                try {
                    WaterwayDescriptor configFile = readDescriptor(r.getData(), r);
                    logger.debug("load config file = {}", configFile);
                    String keyForImport = format("wy_configfile_%s", configFile.getCode());
                    extendsStack.push(keyForImport);
                    disabledFile = !r.isEnabled();
                    disabledItems.clear();
                    disabledItems.addAll(r.getDisabledItems());
                    currentConfigFile = r.getKey();
                    configsForImport.put(keyForImport, map(configFile.getConfig()).skipNullValues().with(WY_ITEM_TAG, emptyToNull(configFile.getTag())));
                    configFile.getItems().forEach(this::loadConfigItem);
                    extendsStack.pop();
                } catch (Exception e) {
                    throw runtime(e, "error processing config record = %s", r);
                }
            });
            logger.debug("import config for items");
            return ImmutableList.copyOf(list(itemsByCode.values()).map(i -> WaterwayItemImpl.copyOf(i).withConfig(mapOf(String.class, String.class).accept(b -> {
                List<String> extend = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(nullToEmpty(i.getConfig().get(WY_EXTENDS)));
                extend.forEach(k -> {
                    b.putAll(checkNotNull(configsForImport.get(k), "missing config import for key =< %s >", k));
                });
                b.with(i.getConfig()).withoutKey(WY_EXTENDS);
            })).build()));
        }

        private WaterwayItem loadConfigItem(WaterwayItemConfig i) {
            logger.debug("load config sub-items for item = {}", i);
            extendsStack.push(i.getCode());
            configsForImport.put(i.getCode(), i.getConfig());
            List<String> subItemsIds = list(i.getItems()).map(this::loadConfigItem).map(WaterwayItem::getCode);
            extendsStack.pop();
            logger.debug("load config item = {} (sub items = {})", i, subItemsIds);
            WaterwayItem item = WaterwayItemImpl.builder()
                    .withCode(i.getCode())
                    .withDescription(i.getDescription())
                    .withNotes(i.getNotes())
                    .withType(i.getType())
                    .withSubtype(i.getSubtype())
                    .withConfig(map(i.getConfig()).with(WY_EXTENDS, Joiner.on(",").join(extendsStack)))
                    .withDescriptorKey(currentConfigFile)
                    .withItems(subItemsIds)
                    .withEnabled(!disabledFile && !disabledItems.contains(i.getCode()) && i.isEnabled())
                    .build();
            checkArgument(itemsByCode.put(item.getCode(), item) == null, "duplicate item code =< %s >", item.getCode());
            return item;
        }
    }

    private static class ItemKeyImpl implements ItemKey {

        private final String code, descriptorKey;

        public ItemKeyImpl(String key) {
            Matcher matcher = Pattern.compile("(.+)#(.+)").matcher(checkNotBlank(key));
            checkArgument(matcher.matches(), "invalid item key =< %s >", key);
            this.descriptorKey = checkNotBlank(matcher.group(1));
            this.code = checkIsValidItemCode(matcher.group(2));
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getDescriptorKey() {
            return descriptorKey;
        }

    }

    private static class DescriptorDataAndMetaImpl implements DescriptorDataAndMeta {

        private final WaterwayDescriptorMeta meta;
        private final String dataStr;

        public DescriptorDataAndMetaImpl(WaterwayDescriptorMeta meta, String dataStr) {
            this.dataStr = checkNotBlank(dataStr);
            this.meta = checkNotNull(meta);
        }

        @Override
        public WaterwayDescriptorMeta getMeta() {
            return meta;
        }

        @Override
        public String getData() {
            return dataStr;
        }
    }

}
