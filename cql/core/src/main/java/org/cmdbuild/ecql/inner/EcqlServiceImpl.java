/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.ecql.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import org.cmdbuild.cql.EcqlException;
import org.cmdbuild.easytemplate.EasytemplateProcessor;
import static org.cmdbuild.easytemplate.EasytemplateProcessor.ExprProcessingMode.EPM_JAVASCRIPT;
import org.cmdbuild.easytemplate.EasytemplateProcessorImpl;
import org.cmdbuild.easytemplate.EasytemplateService;
import org.cmdbuild.ecql.EcqlExpression;
import org.cmdbuild.ecql.EcqlRepository;
import org.cmdbuild.ecql.EcqlService;
import static org.cmdbuild.ecql.utils.EcqlUtils.getJsExprFromContext;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.cmdbuild.utils.script.js.CmJsUtils.RhinoJsEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.script.js.CmJsUtils.getRhinoJsEngine;

@Component
public class EcqlServiceImpl implements EcqlService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EcqlRepository repository;
    private final EasytemplateService easytemplateService;

    public EcqlServiceImpl(EcqlRepository repository, EasytemplateService easytemplateService) {
        this.repository = checkNotNull(repository);
        this.easytemplateService = checkNotNull(easytemplateService);
    }

    @Override
    public String prepareCqlExpression(String ecqlId, String jsContext) {
        EcqlExpression ecql = repository.getById(ecqlId);
        try {
            String cql = new EcqlProcessor(ecql, jsContext).processEcqlExpression();
            return cql;
        } catch (Exception ex) {
            throw new EcqlException(ex, "error preparing cql expression for ecqlId = %s", ecqlId);
        }
    }

    private class EcqlProcessor {

        private final RhinoJsEngine engine = getRhinoJsEngine();

        private final EcqlExpression ecql;
        private final String jsContext;

        public EcqlProcessor(EcqlExpression ecql, String suppliedJsContext) {
            this.ecql = checkNotNull(ecql);
            this.jsContext = checkNotBlank(suppliedJsContext, "js context for ecql processing is null or blank");
        }

        public String processEcqlExpression() {
            try {
                return getEasytemplateProcessor().processExpression(ecql.getEcql());
            } catch (Exception ex) {
                throw new EcqlException(ex, "error processing ecql expression = '%s'", abbreviate(ecql.getEcql()));
            }
        }

        private EasytemplateProcessor getEasytemplateProcessor() {
            return EasytemplateProcessorImpl.copyOf(easytemplateService.getDefaultProcessorWithJsContext(jsContext))
                    .withResolver("xa", (key) -> ecql.getContext().get(key))//TODO check not null ??
                    .withResolver("js", this::evalJs)
                    .build();

            //TODO dbtmpl
        }

        private Object evalJs(String expr) {
            String jsCode = getJsExprFromContext(expr, ecql.getContext());
            logger.debug("eval js code = {} expr = '{}'", expr, abbreviate(jsCode));
            jsCode = getEasytemplateProcessor().processExpression(jsCode, EPM_JAVASCRIPT);
            return evalJavascriptCode(format("var context = %s;\n\nvar result;\n\nwith(context){\n\tresult = %s;\n};\n\nresult;", jsContext, jsCode));
        }

        private Object evalJavascriptCode(String jsScript) {
            logger.debug("execute js script = \n\n{}\n", jsScript);
            try {
                return engine.eval(jsScript);
            } catch (Exception ex) {
                throw new EcqlException(ex, "error processing js script = '%s'", abbreviate(jsScript));
            }
        }
    }

}
