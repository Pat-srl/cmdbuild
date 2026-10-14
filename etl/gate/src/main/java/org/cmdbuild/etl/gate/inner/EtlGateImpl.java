/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.gate.inner;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.cmdbuild.etl.gate.inner.EtlGateHandlerImpl.EtlGateHandlerImplBuilder;
import static org.cmdbuild.etl.gate.inner.EtlGateHandlerType.ETLHT_SCRIPT;
import static org.cmdbuild.etl.gate.inner.EtlProcessingMode.PM_NOOP;
import static org.cmdbuild.etl.gate.inner.EtlProcessingMode.PM_REALTIME;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenListOfMaps;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public class EtlGateImpl implements EtlGate {

    private final String code, description;
    private final boolean allowPublicAccess, isEnabled;
    private final EtlProcessingMode processingMode;
    private final Map<String, String> config;
    private final List<EtlGateHandler> handlers;

    private EtlGateImpl(EtlGateImplBuilder builder) {
        this.code = checkNotBlank(builder.code);
        this.description = firstNotBlank(builder.description, code);
        this.allowPublicAccess = firstNotNull(builder.allowPublicAccess, false);
        this.isEnabled = firstNotNull(builder.isEnabled, true);
        this.processingMode = firstNotNull(builder.processingMode, PM_REALTIME);
        this.config = map(builder.config).immutable();
        switch (processingMode) {
            case PM_NOOP:
                handlers = emptyList();
                break;
            default:
                this.handlers = ImmutableList.copyOf(nullToEmpty(builder.handlers));
        }
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean getAllowPublicAccess() {
        return allowPublicAccess;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public EtlProcessingMode getProcessingMode() {
        return processingMode;
    }

    @Override
    public Map<String, String> getConfig() {
        return config;
    }

    @Override
    public List<EtlGateHandler> getHandlers() {
        return handlers;
    }

    @Override
    public String toString() {
        return "EtlGate{" + "code=" + code + '}';
    }

    public static EtlGateImplBuilder builder() {
        return new EtlGateImplBuilder();
    }

    public static EtlGateImplBuilder copyOf(EtlGate source) {
        return new EtlGateImplBuilder()
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withAllowPublicAccess(source.getAllowPublicAccess())
                .withProcessingMode(source.getProcessingMode())
                .withConfig(source.getConfig())
                .withEnabled(source.isEnabled())
                .withHandlers(source.getHandlers());
    }

    public static class EtlGateImplBuilder implements Builder<EtlGateImpl, EtlGateImplBuilder> {

        private String code, description;
        private Boolean allowPublicAccess, isEnabled;
        private EtlProcessingMode processingMode;
        private final Map<String, String> config = map();
        private List<EtlGateHandler> handlers;

        public EtlGateImplBuilder withConfig(Object... config) {
            return this.withConfig(map(config));
        }

        public EtlGateImplBuilder withConfig(Map<String, String> config) {
            if (config != null) {
                List<Map<String, String>> handlersFromConfig = unflattenListOfMaps(config, "handlers");
                if (!handlersFromConfig.isEmpty()) {
                    withHandlersConfig(handlersFromConfig);
                }
                this.config.putAll(map(config).withoutKeys(k -> k.startsWith("handlers_")));//TODO improve this
            }
            return this;
        }

        public EtlGateImplBuilder withProcessingMode(EtlProcessingMode processingMode) {
            this.processingMode = processingMode;
            return this;
        }

        public EtlGateImplBuilder withEnabled(Boolean isEnabled) {
            this.isEnabled = isEnabled;
            return this;
        }

        public EtlGateImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public EtlGateImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public EtlGateImplBuilder withHandlers(List<EtlGateHandler> handlers) {
            this.handlers = handlers;
            return this;
        }

        public EtlGateImplBuilder withHandlersConfig(List<Map<String, String>> handlers) {
            this.handlers = handlers.stream().map(EtlGateHandlerImpl::new).collect(toImmutableList());
            return this;
        }

        public EtlGateImplBuilder addHandler(EtlGateHandler handler) {
            this.handlers = list(nullToEmpty(handlers)).with(handler);
            return this;
        }

        public EtlGateImplBuilder withHandler(Consumer<EtlGateHandlerImplBuilder> builder) {
            return this.addHandler(EtlGateHandlerImpl.builder().accept(builder).build());
        }

        public EtlGateImplBuilder withScriptHandler(String script) {
            return this.withHandler(h -> h.withType(ETLHT_SCRIPT).withScript(script));
        }

        public EtlGateImplBuilder withAllowPublicAccess(Boolean allowPublicAccess) {
            this.allowPublicAccess = allowPublicAccess;
            return this;
        }

        public EtlGateImplBuilder withOnlyHandlerScriptContent(String scriptContent) {
            checkArgument(handlers.stream().filter(h -> h.isOfType(ETLHT_SCRIPT)).count() == 1);
            this.handlers = list(handlers).map(h -> {
                if (h.isOfType(ETLHT_SCRIPT)) {
                    return EtlGateHandlerImpl.copyOf(h).withScript(scriptContent).build();
                } else {
                    return h;
                }
            });
            return this;
        }

        @Override
        public EtlGateImpl build() {
            return new EtlGateImpl(this);
        }

    }
}
