/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easytemplate;

import static java.util.Collections.emptyMap;
import java.util.Map;
import org.cmdbuild.easytemplate.EasytemplateProcessor;
import org.cmdbuild.easytemplate.TemplateResolver;

public class DummyEasytemplateProcessor implements EasytemplateProcessor {

    private final static DummyEasytemplateProcessor INSTANCE = new DummyEasytemplateProcessor();

    public static DummyEasytemplateProcessor getInstance() {
        return INSTANCE;
    }

    @Override
    public String processExpression(String expression, ExprProcessingMode mode) {
        return expression;
    }

    @Override
    public Map<String, TemplateResolver> getResolvers() {
        return emptyMap();
    }

}
