package org.cmdbuild.easytemplate.store;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import org.cmdbuild.easytemplate.Easytemplate;

public interface EasytemplateRepository {

    Easytemplate create(Easytemplate template);

    @Nullable
    String getTemplateOrNull(String name);

    default String getTemplate(String name) {
        return checkNotNull(getTemplateOrNull(name), "template not found for name =< %s >", name);
    }

}
