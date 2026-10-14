/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.utils;

import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Set;
import org.cmdbuild.easytemplate.EasytemplateProcessor;
import org.cmdbuild.utils.script.groovy.GroovyScriptService;

public class TaskPerformerExpressionProcessorUtils {

    public static Set<String> getPerformersFromExpression(EasytemplateProcessor templateResolver, GroovyScriptService groovyScriptService, String expression) {
        return getPerformersFromExpression(templateResolver, groovyScriptService, emptyMap(), expression);
    }

    public static Set<String> getPerformersFromExpression(EasytemplateProcessor templateResolver, GroovyScriptService groovyScriptService, Map<String, Object> rawWorkflowVars, String expression) {
        String resolvedExpression = templateResolver.processExpression(expression);

        TaskPerformerExpressionProcessor evaluator = new TaskPerformerExpressionProcessor(groovyScriptService, resolvedExpression);
        evaluator.setVariables(rawWorkflowVars);

        return evaluator.getNames();
    }
}
