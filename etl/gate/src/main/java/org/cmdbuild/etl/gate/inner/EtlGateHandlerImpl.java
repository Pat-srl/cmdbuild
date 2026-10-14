/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.gate.inner;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.etl.gate.inner.EtlGateHandlerType.ETLHT_SCRIPT;
import static org.cmdbuild.utils.encode.CmPackUtils.packIfNotBlankOrNull;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackIfPacked;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.toListOfStrings;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class EtlGateHandlerImpl implements EtlGateHandler {

    private final Map<String, String> config;

    private final String type;
    private final List<String> templates;
    private final String script;

    public EtlGateHandlerImpl(Map<String, String> config) {
        this.config = map(config).immutable();
        type = checkNotBlank(config.get(ETL_HANDLER_CONFIG_TYPE));
        templates = toListOfStrings(config.get(ETL_HANDLER_CONFIG_TEMPLATES));
        script = unpackIfPacked(config.get(ETL_HANDLER_CONFIG_SCRIPT));
        switch (type) {
            case ETLHT_SCRIPT ->
                checkArgument(hasScript(), "missing script for `script` handler");
        }
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public List<String> getTemplates() {
        return templates;
    }

    @Override
    @Nullable
    public String getScript() {
        return script;
    }

    @Override
    public Map<String, String> getConfig() {
        return config;
    }

    @Override
    public String toString() {
        return "EtlGateHandler{" + "type=" + type + '}';
    }

    public static EtlGateHandlerImplBuilder builder() {
        return new EtlGateHandlerImplBuilder();
    }

    public static EtlGateHandlerImplBuilder copyOf(EtlGateHandler source) {
        return builder().withConfig(source.getConfig());
    }

    public static class EtlGateHandlerImplBuilder implements Builder<EtlGateHandlerImpl, EtlGateHandlerImplBuilder> {

        private final Map<String, String> config = map();

        public EtlGateHandlerImplBuilder withConfig(String... config) {
            return this.withConfig(map(config));
        }

        public EtlGateHandlerImplBuilder withConfig(Map<String, String> config) {
            this.config.putAll(firstNotNull(config, emptyMap()));
            return this;
        }

        public EtlGateHandlerImplBuilder withScript(@Nullable String script) {
            this.config.put(ETL_HANDLER_CONFIG_SCRIPT, packIfNotBlankOrNull(script));
            return this;
        }

        public EtlGateHandlerImplBuilder withType(String type) {
            this.config.put(ETL_HANDLER_CONFIG_TYPE, type);
            return this;
        }

        public EtlGateHandlerImplBuilder withTemplate(@Nullable String template) {
            this.config.put(ETL_HANDLER_CONFIG_TEMPLATES, template);
            return this;
        }

        public EtlGateHandlerImplBuilder withTemplates(Collection<String> templates) {
            this.config.put(ETL_HANDLER_CONFIG_TEMPLATES, Joiner.on(",").join(templates));//TODO improve this, use common utils
            return this;
        }

        public EtlGateHandlerImplBuilder withTemplates(String... templates) {
            return this.withTemplates(list(templates));
        }

        public EtlGateHandlerImplBuilder withTemplatesAsString(String value) {
            this.config.put(ETL_HANDLER_CONFIG_TEMPLATES, value);
            return this;
        }

        @Override
        public EtlGateHandlerImpl build() {
            return new EtlGateHandlerImpl(config);
        }

    }

}
