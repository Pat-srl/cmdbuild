/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config.inner;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.nullToEmpty;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.etl.config.WaterwayItemType;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_HANDLER;
import org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.LIST_OF_MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmInlineUtils.flattenMaps;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.etl.config.WaterwayItemConfig;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.checkIsValidItemCode;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.configItemValueToString;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.getTypeAndCode;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;

public class WaterwayItemConfigImpl implements WaterwayItemConfig {

    private final String code, description, notes, subtype;
    private final WaterwayItemType type;
    private final List<WaterwayItemConfig> items;
    private final Map<String, String> config;
    private final boolean enabled;

    @JsonCreator
    public WaterwayItemConfigImpl(ObjectNode node) {
        Map<String, Object> map = map(fromJson(node, MAP_OF_OBJECTS));

        boolean nested = toBooleanOrDefault(map.remove("wy_nested"), false);

        Pair<WaterwayItemType, String> typeCode = getTypeAndCode(node, nested).get();
        map.remove(serializeEnum(typeCode.getKey()));

        this.type = typeCode.getKey();
        this.code = checkIsValidItemCode(typeCode.getValue());

        this.description = nullToEmpty(toStringOrNull(map.get("description")));
        this.notes = nullToEmpty(toStringOrNull(map.get("notes")));
        this.enabled = toBooleanOrDefault(map.get("enabled"), true);

        this.subtype = switch (type) {
            case WYCIT_HANDLER ->
                toStringNotBlank(map.get("type"));
            default ->
                null;
        };

        ArrayNode handlers = (ArrayNode) node.get("handlers");
        this.items = listOf(WaterwayItemConfig.class).accept(l -> {
            if (handlers != null && !handlers.isNull()) {
                AtomicInteger i = new AtomicInteger(1);
                list(handlers.elements()).map(e -> {
                    ((ObjectNode) e).put(serializeEnum(WYCIT_HANDLER), format("%s_%s", code, i.getAndIncrement()));
                    ((ObjectNode) e).put("wy_nested", true);
                    return fromJson(e, WaterwayItemConfigImpl.class);
                }).forEach(l::add);
            }
        }).immutableCopy();

        config = map(map).withoutKeys("code", "type", "description", "notes", "handlers", "columns", "enabled").mapValues(WaterwayDescriptorUtils::configItemValueToString).accept(m -> {
            JsonNode columns = node.get("columns");
            if (columns != null) {
                if (columns.isArray()) {
                    List<Map<String, String>> list = fromJson(columns, LIST_OF_MAP_OF_STRINGS);
                    m.putAll(flattenMaps(map("columns", list)));
                } else {
                    m.put("columns", columns.asText());
                }
            }
        }).immutable();
        config.values().forEach((v) -> checkArgument(v == null || v instanceof String));
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getNotes() {
        return notes;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public WaterwayItemType getType() {
        return type;
    }

    @Override
    @Nullable
    public String getSubtype() {
        return subtype;
    }

    @Override
    public Map<String, String> getConfig() {
        return config;
    }

    @Override
    public List<WaterwayItemConfig> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "WaterwayItemConfig{" + "code=" + code + ", type=" + type + '}';
    }

}
