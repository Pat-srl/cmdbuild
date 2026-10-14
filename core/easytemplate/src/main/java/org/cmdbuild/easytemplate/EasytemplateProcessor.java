package org.cmdbuild.easytemplate;

import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.easytemplate.TemplateResolver;
import static org.cmdbuild.easytemplate.EasytemplateProcessor.ExprProcessingMode.EPM_DEFAULT;

public interface EasytemplateProcessor {

    @Nullable
    String processExpression(@Nullable String expression, ExprProcessingMode mode);

    Map<String, TemplateResolver> getResolvers();

    @Nullable
    default String processExpression(@Nullable String expression) {
        return processExpression(expression, EPM_DEFAULT);
    }

    enum ExprProcessingMode {
        EPM_JAVASCRIPT, EPM_DEFAULT
    }

}
