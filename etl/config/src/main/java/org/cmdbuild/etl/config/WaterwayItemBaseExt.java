package org.cmdbuild.etl.config;

import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface WaterwayItemBaseExt extends WaterwayItemBase {

    Map<String, String> getConfig();

    @Nullable
    default String getConfig(String key) {
        return getConfig().get(checkNotBlank(key));
    }

    default boolean hasConfigNotBlank(String key) {
        return isNotBlank(getConfig(key));
    }
}
