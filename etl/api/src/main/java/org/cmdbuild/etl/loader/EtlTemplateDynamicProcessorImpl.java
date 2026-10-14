package org.cmdbuild.etl.loader;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.DOTALL;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import org.cmdbuild.etl.EtlException;
import org.cmdbuild.script.ScriptService;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class EtlTemplateDynamicProcessorImpl implements EtlTemplateDynamicProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ScriptService scriptService;
    private final EtlTemplateLoaderHelper helper;

    public EtlTemplateDynamicProcessorImpl(ScriptService scriptService, EtlTemplateLoaderHelper helper) {
        this.scriptService = checkNotNull(scriptService);
        this.helper = checkNotNull(helper);
    }

    @Override
    public EtlTemplate prepareTemplate(EtlTemplateReference template, Map<String, Object> context) {
        if (template.isDynamic()) {
            Map<String, Object> model = fromJson(toJson(((EtlTemplateDynamic) template).getDynamicTemplate()), MAP_OF_OBJECTS);
            model = (Map<String, Object>) processModel(model, context);
            return helper.jsonToTemplate(toJson(model));
        } else {
            return (EtlTemplate) template;
        }
    }

    private Object processModel(Object model, Map<String, Object> context) {
        if (model instanceof Map map) {
            return map(map).mapValues(v -> processModel(v, context));
        } else if (model instanceof Iterable i) {
            return list(i).map(v -> processModel(v, context));
        } else if (model instanceof String s) {
            Matcher matcher = Pattern.compile("^DYN[{](.+)[}]$", DOTALL).matcher(trimToEmpty(s));
            if (matcher.matches()) {
                String expr = matcher.group(1);
                return processExpr(expr, context);
            } else {
                return s;
            }
        } else {
            return model;
        }
    }

    private Object processExpr(String expr, Map<String, Object> context) {
        try {
            return scriptService.helper(getClass(), expr).executeForOutput(context);
        } catch (Exception ex) {
            throw new EtlException(ex, "error processing expression for dynamic template =< %s >", expr);
        }
    }

}
