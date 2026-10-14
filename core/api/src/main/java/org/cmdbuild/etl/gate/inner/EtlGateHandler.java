/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.gate.inner;

import static com.google.common.base.Objects.equal;
import com.google.common.base.Preconditions;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.etl.gate.inner.EtlGateHandler.EtlGateHandlerOutputMode.EOM_REPLACE;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import org.cmdbuild.utils.lang.CmNullableUtils;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface EtlGateHandler {

    final String ETL_HANDLER_CONFIG_TYPE = "type",
            ETL_HANDLER_CONFIG_TEMPLATES = "templates",
            ETL_HANDLER_CONFIG_SCRIPT = "script",
            ETL_HANDLER_CONFIG_INPUT_ATTACHMENT_NAME = "input",
            ETL_HANDLER_CONFIG_OUTPUT_ATTACHMENT_NAME = "output",
            ETL_HANDLER_CONFIG_OUTPUT_MODE = "outputMode",
            ETL_HANDLER_CONFIG_SKIP_NEXT_ON_NO_DATA = "skipNextOnNoData";

    String getType();

    List<String> getTemplates();

    @Nullable
    String getScript();

    Map<String, String> getConfig();

    @Nullable
    default String getConfig(String key) {
        return getConfig().get(checkNotBlank(key));
    }

    default String getConfigNotBlank(String key) {
        return checkNotBlank(getConfig(key), "config not found for key =< %s >", key);
    }

    default boolean hasConfigNotBlank(String key) {
        return isNotBlank(getConfig(key));
    }

    default String getTemplate() {
        Preconditions.checkArgument(hasSingleTemplate(), "this gate does not have a single template");
        return Iterables.getOnlyElement(getTemplates());
    }

    default boolean hasScript() {
        return CmNullableUtils.isNotBlank(getScript());
    }

    default boolean hasSingleTemplate() {
        return getTemplates().size() == 1;
    }

    default boolean isOfType(String type) {
        return equal(checkNotNull(type), getType());
    }

    default EtlGateHandlerOutputMode getOutputMode() {
        return parseEnumOrDefault(getConfig(ETL_HANDLER_CONFIG_OUTPUT_MODE), EOM_REPLACE);
    }

    enum EtlGateHandlerOutputMode {
        EOM_REPLACE, EOM_SPLIT, EOM_ATTACH, EOM_APPEND
    }

}
