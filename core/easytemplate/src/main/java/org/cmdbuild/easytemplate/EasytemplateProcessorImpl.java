package org.cmdbuild.easytemplate;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableMap;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import java.util.Map;
import java.util.regex.Matcher;
import static java.util.regex.Matcher.quoteReplacement;
import java.util.regex.Pattern;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.commons.lang3.math.NumberUtils;
import org.cmdbuild.easytemplate.TemplateResolver;
import org.cmdbuild.easytemplate.TemplateResolverImpl;
import static org.apache.commons.text.StringEscapeUtils.escapeJson;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasytemplateProcessorImpl implements EasytemplateProcessor {

    private final static String OPEN_GRAPH_MARK = randomId(), CLOSE_GRAPH_MARK = randomId();
    private final static int MAX_PROCESSING_LOOP_COUNT = 1000;

    private final static Map<String, TemplateResolver> DEFAULT_RESOLVERS = ImmutableMap.of("symbol", new TemplateResolverImpl((Function<String, Object>) (x) -> {
        return switch (x) {
            case "open" ->
                OPEN_GRAPH_MARK;
            case "close" ->
                CLOSE_GRAPH_MARK;
            default ->
                throw runtime("unsupported value for `symbol` resolver =< %s >", x);
        };
    }, false));

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, TemplateResolver> templateResolvers;
    private final Pattern pattern;
    private final boolean hasResolverForDefaultExpr;
    private final int resGroup, exprGroup;

    private EasytemplateProcessorImpl(EasytemplateProcessorImplBuilder builder) {
        this.templateResolvers = builder.templateResolvers;
        this.hasResolverForDefaultExpr = templateResolvers.containsKey("");
        String patternStr;
        if (hasResolverForDefaultExpr) {
            patternStr = format("[{]((%s):)?([^{}]+)[}]", Joiner.on("|").join(templateResolvers.keySet()));
            resGroup = 2;
            exprGroup = 3;
        } else {
            patternStr = format("[{](%s):([^{}]+)[}]", Joiner.on("|").join(templateResolvers.keySet()));
            resGroup = 1;
            exprGroup = 2;
        }
        logger.trace("start template processor, using pattern = {}", patternStr);
        pattern = Pattern.compile(patternStr);
    }

    @Override
    @Nullable
    public String processExpression(@Nullable String expression, ExprProcessingMode mode) {
        return isBlank(expression) ? expression : new ResolverHelper(expression, mode).resolve();
    }

    @Override
    public Map<String, TemplateResolver> getResolvers() {
        return templateResolvers;
    }

    private class ResolverHelper {

        private final String input;
        private final ExprProcessingMode mode;

        public ResolverHelper(String input, ExprProcessingMode mode) {
            this.input = checkNotBlank(input);
            this.mode = checkNotNull(mode);
        }

        public String resolve() {
            return unescapeGraphs(resolveTemplate(input));
        }

        private String resolveTemplate(String template) {
            logger.trace("trying to resolve template = {}", template);
            if (Pattern.compile(Pattern.quote("{easytemplate:disable}")).matcher(template).find()) {
                logger.trace("easytemplate disabled for this expr, skipping");
                return template.replaceFirst(Pattern.quote("{easytemplate:disable}"), "");
            } else {
                Matcher matcher = pattern.matcher(template);
                String resolved = template;
                int loopCount = 0;
                while (matcher.find()) {
                    loopCount++;
                    checkArgument(loopCount < MAX_PROCESSING_LOOP_COUNT, "expr stack overflow error: too many processing loops while resolving expr =< %s >", abbreviate(template));
                    logger.trace("found match = {}", matcher.group(0));
                    String resolver = nullToEmpty(matcher.group(resGroup));
                    String expr = matcher.group(exprGroup);
                    String replacement = resolveExpression(resolver, expr);
                    logger.trace("replacing match with string = {}", replacement);
                    resolved = matcher.replaceFirst(quoteReplacement(replacement));
                    matcher = pattern.matcher(resolved);
                }
                return resolved;
            }
        }

        @Nullable
        private String resolveExpression(String resolver, String expression) {
            expression = unescapeGraphs(expression);
            logger.trace("evaluating expr = {} with resolver = {} processing mode = {}", expression, resolver, mode);
            TemplateResolver engine = templateResolvers.get(resolver);
            if (engine != null) {
                Object value = engine.getFunction().apply(expression);
                if (value == null) {
                    logger.warn("expr =< {} > resolved to null with resolver =< {} >", expression, resolver);
                } else {
                    logger.trace("expr =< {} > resolved to value =< {} > ({}) with resolver =< {} >", expression, value, getClassOfNullable(value).getName(), resolver);
                    if (!engine.isRecursive()) {
                        value = escapeGraphs(toStringOrEmpty(value));
                    }
                }
                return valueToString(value, mode);
            } else {
                logger.warn(marker(), "expr resolver =< {} > not found (return null)", resolver);
                return valueToString(null, mode);
            }
        }
    }

    private static String escapeGraphs(String value) {
        return nullToEmpty(value)
                .replace("{", OPEN_GRAPH_MARK)//TODO improve this
                .replace("}", CLOSE_GRAPH_MARK);
    }

    private static String unescapeGraphs(String value) {
        return nullToEmpty(value)
                .replace(OPEN_GRAPH_MARK, "{")
                .replace(CLOSE_GRAPH_MARK, "}");
    }

    private String valueToString(@Nullable Object value, ExprProcessingMode mode) {
        return switch (mode) {
            case EPM_JAVASCRIPT -> {
                if (value == null) {
                    yield "null";
                } else {
                    String strValue = toStringOrEmpty(value);
                    if (NumberUtils.isCreatable(strValue)) {
                        yield strValue;
                    } else {
                        yield format("\"%s\"", escapeJson(strValue));
                    }
                }
            }
            default ->
                toStringOrEmpty(value);
        };
    }

    public static EasytemplateProcessorImplBuilder builder() {
        return new EasytemplateProcessorImplBuilder();
    }

    public static EasytemplateProcessorImplBuilder copyOf(EasytemplateProcessor processor) {
        return builder().withResolvers(processor.getResolvers());
    }

    public static class EasytemplateProcessorImplBuilder implements Builder<EasytemplateProcessorImpl, EasytemplateProcessorImplBuilder> {

        protected final Map<String, TemplateResolver> templateResolvers = map(DEFAULT_RESOLVERS);

        private EasytemplateProcessorImplBuilder() {
        }

        public EasytemplateProcessorImplBuilder withResolver(Function<String, Object> engine, String... prefixes) {
            return EasytemplateProcessorImplBuilder.this.withResolver(engine, asList(prefixes));
        }

        public EasytemplateProcessorImplBuilder withResolver(Function<String, Object> engine, Iterable<String> prefixes) {
            for (String p : prefixes) {
                templateResolvers.put(p, new TemplateResolverImpl(engine, true));
            }
            return this;
        }

        public EasytemplateProcessorImplBuilder withResolver(String prefix, Function<String, Object> engine) {
            templateResolvers.put(prefix, new TemplateResolverImpl(engine, true));
            return this;
        }

        public EasytemplateProcessorImplBuilder withResolver(String prefix, Function<String, Object> engine, boolean isRecursive) {
            templateResolvers.put(prefix, new TemplateResolverImpl(engine, isRecursive));
            return this;
        }

        public EasytemplateProcessorImplBuilder withResolvers(Map<String, TemplateResolver> resolvers) {
            templateResolvers.putAll(resolvers);
            return this;
        }

        @Override
        public EasytemplateProcessorImpl build() {
            return new EasytemplateProcessorImpl(this);
        }

    }

}
