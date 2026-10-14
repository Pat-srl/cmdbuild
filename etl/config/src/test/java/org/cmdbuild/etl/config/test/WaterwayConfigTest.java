/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config.test;

import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.uniqueIndex;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Set;
import static org.apache.commons.lang3.StringUtils.trim;
import org.cmdbuild.etl.config.WaterwayDescriptor;
import org.cmdbuild.etl.config.WaterwayDescriptorMetaImpl;
import static org.cmdbuild.etl.config.WaterwayDescriptorMetaImpl.empty;
import org.cmdbuild.etl.config.WaterwayItem;
import org.cmdbuild.etl.config.WaterwayItemConfig;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_FUNCTION;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_GATE;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_HANDLER;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_TEMPLATE;
import org.cmdbuild.etl.config.utils.DescriptorDataAndMeta;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.applyParamsExpressions;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.buildDescriptorDataAndParams;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.checkDescriptorData;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.hasDescriptorDataAndParams;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.loadItems;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.readDescriptor;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.splitDescriptorDataAndParams;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.toRecord;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.fromYaml;
import static org.cmdbuild.utils.json.CmJsonUtils.isYaml;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import org.cmdbuild.utils.lang.CmException;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaterwayConfigTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testConfigProcessing1() throws Exception {
        String configFile = readToString(getClass().getResourceAsStream("/test.yaml"));
        Map<String, Object> data = fromYaml(configFile, MAP_OF_OBJECTS);

        logger.info("loaded data = {}", data);

        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);

        JsonSchema schemaFromNode = factory.getSchema(fromJson(readToString(getClass().getResourceAsStream("/org/cmdbuild/etl/config/schema.json")), JsonNode.class));
        schemaFromNode.initializeValidators();
        Set<ValidationMessage> validate = schemaFromNode.validate(fromJson(toJson(data), JsonNode.class));

        logger.info("validate = {}", list(validate).map(v -> v.getMessage()));
        assertTrue(validate.stream().allMatch(ValidationMessage::isValid));
    }

    @Test
    public void testSingleItemProcessing() {
        String configFile = readToString(getClass().getResourceAsStream("/test_single.yaml"));
        WaterwayDescriptor config = readDescriptor(configFile, empty());

        assertEquals("test", config.getCode());
        assertEquals("My Test Config", config.getDescription());
        assertEquals("my notes !!!", trim(config.getNotes()));
        assertEquals("mtag", config.getTag());

        WaterwayItemConfig configItem = config.getItem("MyItem");
        assertEquals("MyItem", configItem.getCode());
        assertEquals(WYCIT_GATE, configItem.getType());
        assertEquals("My Item", configItem.getDescription());
        assertEquals("something", configItem.getNotes());

        Map<String, WaterwayItem> items = uniqueIndex(loadItems(list(toRecord(configFile, empty(), null))), WaterwayItem::getCode);

        WaterwayItem item = items.get("MyItem");
        assertEquals("MyItem", item.getCode());
        assertEquals(WYCIT_GATE, item.getType());
        assertEquals("My Item", item.getDescription());
        assertEquals("something", item.getNotes());
        assertEquals("mtag", item.getConfig("tag"));
    }

    @Test
    public void testConfigProcessing2() {
        String configFile = readToString(getClass().getResourceAsStream("/test.yaml"));
        WaterwayDescriptor config = readDescriptor(configFile, empty());

        assertEquals("test", config.getCode());
        assertEquals("My Test Config", config.getDescription());
        assertEquals("my notes !!!", trim(config.getNotes()));

        assertEquals(map("tt_customConfig1", "CONF_1", "tt_customConfig2", "CONF_2"), config.getConfig());

        assertEquals(list("MyItem", "MyOtherItem"), list(config.getItems()).map(WaterwayItemConfig::getCode));

        WaterwayItemConfig configItem = config.getItem("MyItem");
        assertEquals("MyItem", configItem.getCode());
        assertEquals(WYCIT_GATE, configItem.getType());
        assertEquals("My Item", configItem.getDescription());
        assertEquals("something", configItem.getNotes());

        assertEquals(map("tt_customConfig1", "CONF_1_OVERRIDE", "tt_customConfig3", "CONF_3", "tt_customConfig4", "CONF_4", "templates", "three"), configItem.getConfig());

        assertEquals(2, configItem.getItems().size());

        WaterwayItemConfig configSubItem = configItem.getItems().get(0);

        assertEquals("MyItem_1", configSubItem.getCode());
        assertEquals(WYCIT_HANDLER, configSubItem.getType());
        assertEquals("script", configSubItem.getSubtype());

        assertEquals(map("tt_customConfig3", "CONF_3_OVERRIDE", "tt_customConfig5", "CONF_5", "script", "something"), configSubItem.getConfig());

        configSubItem = configItem.getItems().get(1);

        assertEquals("MyItem_2", configSubItem.getCode());
        assertEquals(WYCIT_HANDLER, configSubItem.getType());
        assertEquals("template", configSubItem.getSubtype());

        assertEquals(map("templates", "one,two"), configSubItem.getConfig());

        Map<String, WaterwayItem> items = uniqueIndex(loadItems(list(toRecord(configFile, empty(), null))), WaterwayItem::getCode);

        assertEquals(set("MyItem", "MyItem_1", "MyItem_2", "MyOtherItem"), items.keySet());

        WaterwayItem item = checkNotNull(items.get("MyItem"));
        assertEquals("MyItem", item.getCode());
        assertEquals(WYCIT_GATE, item.getType());
        assertEquals("My Item", item.getDescription());
        assertEquals("something", item.getNotes());
        assertEquals(map("tt_customConfig1", "CONF_1_OVERRIDE", "tt_customConfig2", "CONF_2", "tt_customConfig3", "CONF_3", "tt_customConfig4", "CONF_4", "templates", "three"), item.getConfig());

        assertEquals(2, item.getItems().size());

        WaterwayItem subItem = checkNotNull(items.get(item.getItems().get(0)));

        assertEquals("MyItem_1", subItem.getCode());
        assertEquals(WYCIT_HANDLER, subItem.getType());

        assertEquals(map("tt_customConfig1", "CONF_1_OVERRIDE", "tt_customConfig2", "CONF_2", "tt_customConfig3", "CONF_3_OVERRIDE", "tt_customConfig4", "CONF_4", "tt_customConfig5", "CONF_5", "script", "something", "templates", "three"), subItem.getConfig());
        assertEquals("script", subItem.getSubtype());

        subItem = checkNotNull(items.get(item.getItems().get(1)));

        assertEquals("MyItem_2", subItem.getCode());
        assertEquals(WYCIT_HANDLER, subItem.getType());

        assertEquals(map("tt_customConfig1", "CONF_1_OVERRIDE", "tt_customConfig2", "CONF_2", "tt_customConfig3", "CONF_3", "tt_customConfig4", "CONF_4", "templates", "one,two"), subItem.getConfig());
        assertEquals("template", subItem.getSubtype());
    }

    @Test
    public void testFunConfigProcessing() {
        String configFile = readToString(getClass().getResourceAsStream("/test2.yaml"));
        WaterwayDescriptor config = readDescriptor(configFile, empty());

        assertEquals(list("MyItem", "MyOtherItem", "_cm3_dashboard_model_stats", "MyScheduledTrigger", "MyEventTrigger"), list(config.getItems()).map(WaterwayItemConfig::getCode));

        Map<String, WaterwayItem> items = uniqueIndex(loadItems(list(toRecord(configFile, empty(), null))), WaterwayItem::getCode);

        WaterwayItem item = checkNotNull(items.get("_cm3_dashboard_model_stats"));
        assertEquals("_cm3_dashboard_model_stats", item.getCode());
        assertEquals(WYCIT_FUNCTION, item.getType());
        assertEquals("Fun", item.getDescription());
        assertEquals("fu fu", item.getNotes());
        assertEquals(map("data", "...").keySet(), item.getConfig().keySet());
    }

    @Test
    public void testIfcImportConfig() {
        String configFile = readToString(getClass().getResourceAsStream("/test3.yaml"));
        WaterwayDescriptor config = readDescriptor(configFile, empty());
    }

    @Test
    public void testTemplateConfig1() {
        String configFile = readToString(getClass().getResourceAsStream("/test_template_1.yaml"));
        WaterwayDescriptor config = readDescriptor(configFile, empty());

        assertEquals("MyTemplate", config.getCode());
        assertEquals("My Template", config.getDescription());
        assertEquals("my notes !!!", trim(config.getNotes()));
        assertTrue(config.hasSingleItem());

        WaterwayItemConfig item = config.getSingleItem();

        assertEquals("MyTemplate", item.getCode());
        assertEquals("My Template", item.getDescription());
        assertEquals("my notes !!!", trim(item.getNotes()));

        assertEquals(WYCIT_TEMPLATE, item.getType());

        logger.info("template config = \n\n{}\n", mapToLoggableString(item.getConfig()));

        assertTrue(item.getConfig().containsKey("firstCol"));
        assertNull(item.getConfig("firstCol"));

        assertEquals("tmpl1", item.getConfig("errorTemplate"));
        assertEquals("acc1", item.getConfig("errorAccount"));

        assertEquals("tmpl2", item.getConfig("notificationTemplate"));
        assertEquals("acc2", item.getConfig("notificationAccount"));
    }

    @Test
    public void testTemplateConfig2() {
        String configFile = readToString(getClass().getResourceAsStream("/test_template_2.yaml"));
        WaterwayDescriptor config = readDescriptor(configFile, empty());

        assertEquals("MyTemplate", config.getCode());
        assertEquals("My Template", config.getDescription());
        assertEquals("my notes !!!", trim(config.getNotes()));
        assertTrue(config.hasSingleItem());

        WaterwayItemConfig item = config.getSingleItem();

        assertEquals("MyTemplate", item.getCode());
        assertEquals("My Template", item.getDescription());
        assertEquals("my notes !!!", trim(item.getNotes()));

        assertEquals(WYCIT_TEMPLATE, item.getType());

        logger.info("template config = \n\n{}\n", mapToLoggableString(item.getConfig()));

        assertTrue(item.getConfig().containsKey("firstCol"));
        assertNull(item.getConfig("firstCol"));

        assertTrue(item.getConfig().containsKey("dateTimeMode"));
        assertTrue(item.getConfig("dateTimeMode").equals("java"));

        assertEquals("tmpl1", item.getConfig("errorTemplate"));
        assertEquals("acc1", item.getConfig("errorAccount"));

        assertEquals("tmpl2", item.getConfig("notificationTemplate"));
        assertEquals("acc2", item.getConfig("notificationAccount"));
    }

    @Test
    public void testJsonpath1() {
        DocumentContext jsonPath = JsonPath.parse("{\"items\":[{\"key\":\"value\"}]}");
        jsonPath.put("items[0]", "key", "other");
        assertEquals("{\"items\":[{\"key\":\"other\"}]}", jsonPath.jsonString());
    }

    @Test
    public void testParamExpressions() {
        JsonNode jsonNode = applyParamsExpressions(fromJson("{\"items\":[{\"key\":\"$var\"}]}", JsonNode.class), map("var", "something"));
        assertEquals("{\"items\":[{\"key\":\"something\"}]}", toJson(jsonNode));
    }

    @Test
    public void testConfigProcessingWithJsonpath() {
        String configFile = readToString(getClass().getResourceAsStream("/test_jsonpath.yaml"));
        Map<String, String> configParams = map("var_1", "CONF_2", "var_2", "CONF_4", "$items[0].handlers[0].tt_customConfig5", "CONF_5");
        WaterwayDescriptor config = readDescriptor(configFile, WaterwayDescriptorMetaImpl.build(configParams));

        assertEquals("test", config.getCode());
        assertEquals("My Test Config", config.getDescription());
        assertEquals("my notes !!!", trim(config.getNotes()));

        assertEquals(map("var_1", "CONF_2", "var_2", "CONF_4", "tt_customConfig1", "CONF_1", "tt_customConfig2", "CONF_2"), config.getConfig());

        assertEquals(list("MyItem", "MyOtherItem"), list(config.getItems()).map(WaterwayItemConfig::getCode));

        WaterwayItemConfig configItem = config.getItem("MyItem");
        assertEquals("MyItem", configItem.getCode());
        assertEquals(WYCIT_GATE, configItem.getType());
        assertEquals("My Item", configItem.getDescription());
        assertEquals("something", configItem.getNotes());

        assertEquals(map("tt_customConfig1", "CONF_1_OVERRIDE", "tt_customConfig3", "CONF_3", "tt_customConfig4", "CONF_4", "templates", "three"), configItem.getConfig());

        assertEquals(2, configItem.getItems().size());

        WaterwayItemConfig configSubItem = configItem.getItems().get(0);

        assertEquals("MyItem_1", configSubItem.getCode());
        assertEquals(WYCIT_HANDLER, configSubItem.getType());
        assertEquals("script", configSubItem.getSubtype());

        assertEquals(map("tt_customConfig3", "CONF_3_OVERRIDE", "tt_customConfig5", "CONF_5", "script", "something"), configSubItem.getConfig());

        configSubItem = configItem.getItems().get(1);

        assertEquals("MyItem_2", configSubItem.getCode());
        assertEquals(WYCIT_HANDLER, configSubItem.getType());
        assertEquals("template", configSubItem.getSubtype());

        assertEquals(map("templates", "one,two"), configSubItem.getConfig());

        Map<String, WaterwayItem> items = uniqueIndex(loadItems(list(toRecord(configFile, WaterwayDescriptorMetaImpl.build(configParams), null))), WaterwayItem::getCode);

        assertEquals(set("MyItem", "MyItem_1", "MyItem_2", "MyOtherItem"), items.keySet());

        WaterwayItem item = checkNotNull(items.get("MyItem"));
        assertEquals("MyItem", item.getCode());
        assertEquals(WYCIT_GATE, item.getType());
        assertEquals("My Item", item.getDescription());
        assertEquals("something", item.getNotes());
        assertEquals(map("var_1", "CONF_2", "var_2", "CONF_4", "tt_customConfig1", "CONF_1_OVERRIDE", "tt_customConfig2", "CONF_2", "tt_customConfig3", "CONF_3", "tt_customConfig4", "CONF_4", "templates", "three"), item.getConfig());

        assertEquals(2, item.getItems().size());

        WaterwayItem subItem = checkNotNull(items.get(item.getItems().get(0)));

        assertEquals("MyItem_1", subItem.getCode());
        assertEquals(WYCIT_HANDLER, subItem.getType());

        assertEquals(map("var_1", "CONF_2", "var_2", "CONF_4", "tt_customConfig1", "CONF_1_OVERRIDE", "tt_customConfig2", "CONF_2", "tt_customConfig3", "CONF_3_OVERRIDE", "tt_customConfig4", "CONF_4", "tt_customConfig5", "CONF_5", "script", "something", "templates", "three"), subItem.getConfig());
        assertEquals("script", subItem.getSubtype());

        subItem = checkNotNull(items.get(item.getItems().get(1)));

        assertEquals("MyItem_2", subItem.getCode());
        assertEquals(WYCIT_HANDLER, subItem.getType());

        assertEquals(map("var_1", "CONF_2", "var_2", "CONF_4", "tt_customConfig1", "CONF_1_OVERRIDE", "tt_customConfig2", "CONF_2", "tt_customConfig3", "CONF_3", "tt_customConfig4", "CONF_4", "templates", "one,two"), subItem.getConfig());
        assertEquals("template", subItem.getSubtype());
    }

    @Test
    public void testConfigWithParams() {
        String configFile = readToString(getClass().getResourceAsStream("/test.yaml")),
                configWithParams = readToString(getClass().getResourceAsStream("/test_withparams.yaml"));

        assertTrue(isYaml(configFile));
        assertTrue(isYaml(configWithParams));

        assertFalse(hasDescriptorDataAndParams(configFile));
        assertTrue(hasDescriptorDataAndParams(configWithParams));

        DescriptorDataAndMeta pair = splitDescriptorDataAndParams(configWithParams);

        assertEquals(map("var1", "something", "var2", "else"), pair.getParams());
        assertTrue(isYaml(pair.getData()));

        checkDescriptorData(configFile);
        checkDescriptorData(pair.getData());

        WaterwayDescriptor config = readDescriptor(pair.getData(), WaterwayDescriptorMetaImpl.build(pair.getParams()));
        assertEquals("test", config.getCode());
        assertEquals("something", config.getConfig().get("tt_customConfig1"));

        configWithParams = buildDescriptorDataAndParams(pair.getData(), WaterwayDescriptorMetaImpl.build(pair.getParams()));

        assertTrue(hasDescriptorDataAndParams(configWithParams));

        pair = splitDescriptorDataAndParams(configWithParams);

        assertEquals(map("var1", "something", "var2", "else"), pair.getParams());
        assertTrue(isYaml(pair.getData()));

        checkDescriptorData(pair.getData());

        config = readDescriptor(pair.getData(), WaterwayDescriptorMetaImpl.build(pair.getParams()));
        assertEquals("test", config.getCode());
        assertEquals("something", config.getConfig().get("tt_customConfig1"));

        configWithParams = buildDescriptorDataAndParams(pair.getData(), WaterwayDescriptorMetaImpl.build(map("var1", "something2", "var2", "else2")));

        assertTrue(hasDescriptorDataAndParams(configWithParams));

        pair = splitDescriptorDataAndParams(configWithParams);

        assertEquals(map("var1", "something2", "var2", "else2"), pair.getParams());
        assertTrue(isYaml(pair.getData()));

        checkDescriptorData(pair.getData());

        config = readDescriptor(pair.getData(), WaterwayDescriptorMetaImpl.build(pair.getParams()));
        assertEquals("test", config.getCode());
        assertEquals("something2", config.getConfig().get("tt_customConfig1"));

        configWithParams = buildDescriptorDataAndParams(pair.getData(), WaterwayDescriptorMetaImpl.build(emptyMap()));

        assertTrue(hasDescriptorDataAndParams(configWithParams));

        pair = splitDescriptorDataAndParams(configWithParams);

        assertEquals(emptyMap(), pair.getParams());
        assertTrue(isYaml(pair.getData()));

        checkDescriptorData(pair.getData());

        config = readDescriptor(pair.getData(), WaterwayDescriptorMetaImpl.build(pair.getParams())); //warning here, missing config param
        assertEquals("test", config.getCode());
        assertEquals("", config.getConfig().get("tt_customConfig1"));
    }

    @Test
    public void testConfigWithParams2() {
        String configFile = readToString(getClass().getResourceAsStream("/test_withparams_2.yaml"));
        assertTrue(isYaml(configFile));
        assertTrue(hasDescriptorDataAndParams(configFile));
        checkDescriptorData(configFile);
        WaterwayDescriptor config = readDescriptor(configFile);
        assertEquals("test_param", config.getCode());
    }

    @Test
    public void testEmptyYaml() {
        assertTrue(fromYaml("---\n# comment\n   \n...", JsonNode.class).isNull());
        assertTrue(fromYaml("---\n# comment\n  \n\n  \n \n...", JsonNode.class).isNull());
    }

    @Test(expected = Exception.class)
    public void testConfigProcessingFail() {
        String configFile = readToString(getClass().getResourceAsStream("/test_invalid.yaml"));
        readDescriptor(configFile, empty());
        fail();
    }

    @Test
    public void testCronExpressionValid() {
        String configFile = readToString(getClass().getResourceAsStream("/test_cron_valid.yaml"));
        readDescriptor(configFile, empty());
    }

    @Test(expected = CmException.class)
    public void testCronExpressionInvalid() {
        String configFile = readToString(getClass().getResourceAsStream("/test_cron_invalid.yaml"));
        readDescriptor(configFile, empty());
    }
}
