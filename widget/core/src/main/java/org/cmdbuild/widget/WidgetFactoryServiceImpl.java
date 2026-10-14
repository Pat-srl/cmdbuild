package org.cmdbuild.widget;

import org.cmdbuild.widget.model.Widget;
import org.cmdbuild.widget.model.WidgetData;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import static java.util.function.Function.identity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.commons.lang3.math.NumberUtils;
import static org.cmdbuild.cql.utils.CqlUtils.getFrom;
import org.cmdbuild.easytemplate.EasytemplateResolverNames;
import org.cmdbuild.easytemplate.store.EasytemplateRepository;
import org.cmdbuild.ecql.EcqlBindingInfo;
import org.cmdbuild.ecql.utils.EcqlUtils;
import org.cmdbuild.exception.WidgetException;
import org.cmdbuild.script.ScriptService;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackIfPacked;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import org.cmdbuild.widget.model.WidgetImpl;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_ATTR_KEYS_FOR_CQL_PROCESSING;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_BUTTON_LABEL_KEY;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_CLASS_NAME;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_FILTER_KEY;
import static org.cmdbuild.widget.utils.WidgetValueUtils.buildWidgetStringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.checkIsPrimitive;
import static org.cmdbuild.utils.lang.CmConvertUtils.isBoolean;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBoolean;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassNameOfNullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringInline;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.widget.WidgetFactoryService.WIDGET_CLASSIDORPROCESSID;
import org.springframework.context.annotation.Primary;

@Component
@Primary
public final class WidgetFactoryServiceImpl implements WidgetFactoryService {

    private static final String WIDGET_ECQL_FROM = "from";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EasytemplateRepository templateRespository;
    private final ScriptService scriptService;

    public WidgetFactoryServiceImpl(EasytemplateRepository templateRespository, ScriptService scriptService) {
        this.templateRespository = checkNotNull(templateRespository);
        this.scriptService = checkNotNull(scriptService);
    }

    @Override
    public Widget widgetDataToWidget(WidgetData widgetData, Map<String, Object> context) {
        try {
            return new WidgetDataProcessor(widgetData, context).processData();
        } catch (Exception ex) {
            throw new WidgetException(ex, "error processing widget data = %s with context = %s", widgetData, mapToLoggableStringInline(context));
        }
    }

    private class WidgetDataProcessor {

        private static final String CLIENT_PREFIX = EasytemplateResolverNames.CLIENT + ":";
        private static final String DB_TEMPLATE_PREFIX = EasytemplateResolverNames.DB_TEMPLATE + ":";

        private final WidgetData widgetData;
        private final Map<String, Object> contextData;
        private final Map<String, Object> sourceData;
        private final Map<String, Object> processedData = map();

        public WidgetDataProcessor(WidgetData widgetData, Map<String, Object> context) {
            this.widgetData = checkNotNull(widgetData);
            this.sourceData = map(widgetData.getData()).accept(m -> {
                if (isNotBlank(widgetData.getLabel())) {
                    m.put(WIDGET_BUTTON_LABEL_KEY, addQuotesIfMissing(widgetData.getLabel()));
                }
            });
            this.contextData = checkNotNull(context);
        }

        public Widget processData() {
            logger.debug("process data for widget = {}", widgetData);
            logger.trace("context for widget processing = \n\n{}\n", mapToLoggableStringLazy(contextData));
            map(sourceData).mapValues((k, v) -> {
                logger.trace("convert widget keyValue {} =< {} > ( {} )", k, v, getClassOfNullable(v));
                Object value = convertValue(k, v);
                logger.trace("set processed widget keyValue {} =< {} > ( {} )", k, value, getClassOfNullable(v));
                return value;
            }).forEach(processedData::put);
            map(processedData).filterKeys(this::shouldProcessCqlValue).forEach((k, v) -> processWidgetCqlValue(k, v, processedData::put));

            Optional.ofNullable(processedData.get(ecqlProcessedKey(WIDGET_FILTER_KEY))).ifPresent((m) -> {
                Optional.ofNullable(emptyToNull((String) ((Map) m).get(WIDGET_ECQL_FROM))).ifPresent((classId) -> processedData.put(WIDGET_CLASS_NAME, buildWidgetStringValue(classId)));
            });

            Widget widget = WidgetImpl.builder()
                    .withContext(contextData)
                    .withData(processedData)
                    .withId(widgetData.getId())
                    .withActive(widgetData.isActive())
                    .withLabel((String) processedData.get(WIDGET_BUTTON_LABEL_KEY))
                    .withType(widgetData.getType())
                    .build();

            return widget;
        }

        private Object convertValue(String key, @Nullable Object rawValue) {
            String stringValue = unpackIfPacked(toStringOrEmpty(rawValue).trim());
            if (isBlank(stringValue)) {
                return "";
            } else if (isBetweenQuotes(stringValue)) {
                return removeQuotes(stringValue);
            } else if (stringValue.startsWith(CLIENT_PREFIX)) {
                return format("{%s}", stringValue);
            } else if (stringValue.startsWith(DB_TEMPLATE_PREFIX)) {
                String templateName = stringValue.substring(DB_TEMPLATE_PREFIX.length());
                return templateRespository.getTemplateOrNull(templateName);
            } else if (NumberUtils.isCreatable(stringValue)) {
                return Integer.valueOf(stringValue);
            } else if (isBoolean(stringValue)) {
                return toBoolean(stringValue);
            } else if (WIDGET_FILTER_KEY.equals(key)) {//TODO check this
                return stringValue;
            } else if (stringValue.matches("[a-zA-Z0-9_]+") || contextData.containsKey(stringValue)) {
                return contextData.get(stringValue);
            } else {
                logger.debug("processing widget value expr script =< {} >", abbreviate(stringValue));
                Object value = scriptService.helper(getClass(), stringValue).withData(map(sourceData).with(contextData).with("data", map(sourceData).with(contextData))).executeForOutput();
                return checkIsPrimitive(value, "invalid output value =< %s > ( %s ) for widget value expr =< %s >", abbreviate(value), getClassNameOfNullable(value), abbreviate(stringValue));
            }
        }

        private void processWidgetCqlValue(String key, @Nullable Object value, BiConsumer<String, Object> callback) {
            if (isNotBlank(value)) {
                EcqlBindingInfo ecqlBindingInfo = EcqlUtils.getEcqlBindingInfoForExpr(toStringNotBlank(value), emptyMap());//TODO check context
                Map<String, Object> xaContext = map(ecqlBindingInfo.getXaBindings(), identity(), processedData::get);
                String ecqlId = isNullOrBlank(contextData.get(WIDGET_CLASSIDORPROCESSID)) ? EcqlUtils.buildEmbeddedEcqlId(toStringNotBlank(value)) : EcqlUtils.buildWidgetEcqlId(toStringNotBlank(contextData.get(WIDGET_CLASSIDORPROCESSID)), toStringOrNull(contextData.get(WIDGET_TASKDEFINITIONID)), widgetData.getId(), key, xaContext);
                String classId = getFrom(toStringNotBlank(value));
                value = map("id", ecqlId, "bindings", map("server", list(ecqlBindingInfo.getServerBindings()), "client", list(ecqlBindingInfo.getClientBindings())), WIDGET_ECQL_FROM, classId);
                key = ecqlProcessedKey(key);
                logger.trace("set processed widget cql value {} =< {} >", key, value);
                callback.accept(key, value);
            }
        }

        private String ecqlProcessedKey(String key) {
            return format("_%s_ecql", key);
        }

        private boolean isBetweenQuotes(String value) {
            return value.matches("^\\s*(['].*[']|[\"].*[\"])\\s*$");
        }

        private String addQuotesIfMissing(String value) {
            if (isBetweenQuotes(value)) {
                return value;
            } else {
                return format("\"%s\"", value);
            }
        }

        private String removeQuotes(String value) {
            Matcher matcher = Pattern.compile("^\\s*['\"](.*)['\"]\\s*$").matcher(value);
            checkArgument(matcher.find());
            return matcher.group(1);
        }

        private boolean shouldProcessCqlValue(String key) {
            return WIDGET_ATTR_KEYS_FOR_CQL_PROCESSING.contains(key);
        }

    }

}
