package org.cmdbuild.sysparam;

import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface SysparamRepository {

    @Nullable
    String getParamOrNull(String key);

    default String getParam(String key) {
        return checkNotBlank(getParamOrNull(key), "param not found for key =< %s >", key);
    }
}
