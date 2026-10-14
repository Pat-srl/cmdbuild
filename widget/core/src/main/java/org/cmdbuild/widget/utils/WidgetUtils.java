/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.widget.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import java.io.IOException;
import java.io.StringReader;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.cql.CqlUtils.compileWithTemplateParams;
import org.cmdbuild.cql.compiler.impl.CqlQueryImpl;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.normalizeId;
import org.cmdbuild.widget.model.WidgetData;
import org.cmdbuild.widget.model.WidgetDataImpl;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGETS_FOR_WORKFLOW;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_BUTTON_LABEL_KEY;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_ID;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_OUTPUT_KEY;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WidgetUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String readClassNameFromCQLFilter(String filter) {
        try {
            CqlQueryImpl queryImpl = compileWithTemplateParams(filter);
            return queryImpl.getFrom().mainClass().getName();
        } catch (Exception e) {
            throw runtime(e, "error reading class name from cql filter = %s", filter);
        }
    }

    public static Map<String, String> parseSerializedWidgetData(String serializedWidgetData) {
        return new WidgetDeserializer(serializedWidgetData).deserialize();
    }

    public static WidgetData toWidgetData(String widgetType, String serializedWidgetData) {
        return toWidgetData(widgetType, true, serializedWidgetData, emptyMap());
    }

    public static WidgetData toWidgetData(String widgetType, @Nullable Boolean isActive, String serializedWidgetData, String label) {
        return toWidgetData(widgetType, isActive, serializedWidgetData, map(WIDGET_BUTTON_LABEL_KEY, checkNotNull(label)));//TODO owner info
    }

    public static WidgetData toWidgetData(String widgetType, @Nullable Boolean isActive, String serializedWidgetData, Map<String, String> extraData) {
        widgetType = checkNotBlank(widgetType);
        Map<String, String> map = map(parseSerializedWidgetData(serializedWidgetData)).with(extraData);
        String label = map.get(WIDGET_BUTTON_LABEL_KEY);
        map = map(map).withoutKey(WIDGET_BUTTON_LABEL_KEY);
        String id;
        if (isBlank(map.get(WIDGET_ID))) {
            id = hash(widgetType + serializedWidgetData);
            map.put(WIDGET_ID, "\"" + id + "\"");
        } else {
            id = normalizeId(map.get(WIDGET_ID));
        }
//        String id = firstNotBlank(normalizeId(map.get(WIDGET_ID)), hash(widgetType + serializedWidgetData));
        return WidgetDataImpl.builder()
                .withData(map)
                .withId(id)
                .withIsActive(isActive)
                .withLabel(label)
                .withType(widgetType)
                .build();
    }

    public static String serializeWidgetDataToString(Map<String, Object> widgetData) {
        return widgetData.entrySet().stream().map((entry) -> format("%s=%s", entry.getKey(), entry.getValue())).collect(Collectors.joining("\n"));
    }

    public static boolean isWorkflowWidgetType(String key) {
        return WIDGETS_FOR_WORKFLOW.contains(key);
    }

    private static class WidgetDeserializer {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private static final String VALUE_SEPARATOR = "=";

        private String serializedWidget;

        private final Map<String, String> widgetData = map();
        private String outVar;

        public WidgetDeserializer(String serializedWidget) {
            this.serializedWidget = checkNotNull(serializedWidget);
        }

        private Map<String, String> deserialize() {
            try {
                logger.trace("deserializing widget data= \n\n{}\n", serializedWidget);
                normalizeEoln();
                parseSerializedWidget();
                return widgetData;
            } catch (Exception ex) {
                logger.error("error deserializing widget data = \n\n{}\n", serializedWidget);
                logger.error("error deserializing widget data", ex);
                throw runtime(ex, "error deserializing widget data = %s", abbreviate(widgetData));
            }
        }

        private void normalizeEoln() {
            serializedWidget = serializedWidget.replaceAll("\r?\n\r?", "\n").replaceAll("\r", "");
            logger.trace("normalized eoln for widget = \n\n{}\n", serializedWidget);
        }

        private void parseSerializedWidget() throws IOException {
            IOUtils.readLines(new StringReader(serializedWidget)).forEach((line) -> {
                parseSerializedWidgetLine(line);
            });
        }

        private void parseSerializedWidgetLine(String line) {
            if (!isBlank(line)) {
                logger.trace("parse widget line = {}", line);
                List<String> split = Splitter.on(VALUE_SEPARATOR).limit(2).trimResults().splitToList(line);
                if (split.size() == 1 || isBlank(split.get(1))) {
                    processSingleValue(split.get(0));
                } else {
                    widgetData.put(split.get(0), split.get(1));
                }
            }
        }

        private void processSingleValue(String value) {
            logger.trace("prcess single widget value = {}", value);
            checkNotBlank(value);
            checkArgument(outVar == null, "duplicate output variable found, values = %s and %s", outVar, value);
            outVar = value;
            widgetData.put(WIDGET_OUTPUT_KEY, format("\"%s\"", outVar));
        }

    }

}
