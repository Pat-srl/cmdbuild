package org.cmdbuild.etl.loader;

import java.util.Map;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public interface EtlTemplateDynamicProcessor {

    EtlTemplate prepareTemplate(EtlTemplateReference dynamicTemplate, Map<String, Object> context);

    default EtlTemplate prepareTemplate(EtlTemplateReference dynamicTemplate, Object... context) {
        return prepareTemplate(dynamicTemplate, map(context));
    }

}
