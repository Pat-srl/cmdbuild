package org.cmdbuild.workflow.utils;

import com.google.common.base.Splitter;
import static java.lang.String.format;
import java.util.Collections;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Set;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrDefault;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.script.CmScriptUtils.SCRIPT_OUTPUT_VAR;
import org.cmdbuild.utils.script.groovy.GroovyScriptService;

public class TaskPerformerExpressionProcessor {

    private static final String PERFORMERS_SEPARATOR = ",";

    private static final Map<String, Object> MISSING_VARIABLES = emptyMap();

    private final GroovyScriptService groovyScriptService;
    private final String expression;
    private Map<String, Object> variables;

    public TaskPerformerExpressionProcessor(GroovyScriptService groovyScriptService, String expression) {
        this.groovyScriptService = checkNotNull(groovyScriptService);
        this.expression = expression;
        this.variables = MISSING_VARIABLES;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = applyOrDefault(variables, Collections::unmodifiableMap, MISSING_VARIABLES);
    }

    /**
     * Returns the names contained in the expression.
     *
     * @return the names.
     */
    public Set<String> getNames() {
        final String evaluated = safeEvaluate();
        return extractNames(evaluated);
    }

    /**
     * Extracts names from specified string.
     *
     * @param str is the string containing the names.
     *
     * @return the extracted names.
     */
    private Set<String> extractNames(String str) {
        return set(Splitter.on(PERFORMERS_SEPARATOR).omitEmptyStrings().trimResults().splitToList(str));
    }

    /**
     * Safe evaluates expression.
     *
     * @return the evaluated expression or an empty string on error.
     */
    private String safeEvaluate() {
        try {
            return evaluate();
        } catch (Exception e) {
            return EMPTY;
        }
    }

    /**
     * Evaluates the expression.
     *
     * @return the evaluated expression.
     *
     * @throws Exception if an error occurs during evaluation.
     */
    private String evaluate() {
        return groovyScriptService.getScriptExecutor(format("output = %s", expression)).executeQuietly(variables).get(SCRIPT_OUTPUT_VAR).toString();
    }
}
