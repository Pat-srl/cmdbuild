package org.cmdbuild.minions;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import jakarta.annotation.Nullable;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_ERROR;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_NOTRUNNING;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinionHandlerImpl implements MinionHandlerExt {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String name, description, configEnabler;
    private final Supplier<Boolean> enabledChecker;
    private final boolean isHidden, isRestartable;
    private final Set<String> requires;
    private final List<Object> reloadOnConfigs;
    private final Supplier<MinionRuntimeStatus> statusChecker;
    private final int order;

    private MinionRuntimeStatus status;

    private MinionHandlerImpl(MinionHandlerImplBuilder builder) {
        this.name = checkNotBlank(builder.name);
        this.description = firstNotNull(builder.description, builder.name);
        this.configEnabler = builder.configEnabler;
        this.statusChecker = firstNotNull(builder.statusChecker, () -> this.status);
        this.enabledChecker = firstNotNull(builder.enabledChecker, Suppliers.ofInstance(true));
        this.isHidden = firstNotNull(builder.isHidden, false);
        this.isRestartable = firstNotNull(builder.isRestartable, true);
        this.order = firstNotNull(builder.order, 0);
        this.requires = ImmutableSet.copyOf(builder.requires);
        this.reloadOnConfigs = ImmutableList.copyOf(builder.reloadOnConfigs);
        this.status = firstNotNull(builder.status, MRS_NOTRUNNING);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getConfigEnabler() {
        return configEnabler;
    }

    @Override
    public MinionRuntimeStatus getRuntimeStatus() {
        return status;
    }

    @Override
    public MinionRuntimeStatus checkRuntimeStatus() {
        try {
            status = statusChecker.get();
        } catch (Exception ex) {
            logger.debug("service =< {} > is NOT OK", name, ex);
            logger.error("service =< {} > is NOT OK: {}", name, ex.toString());
            status = MRS_ERROR;
        }
        return status;
    }

    @Override
    public boolean isEnabled() {
        return enabledChecker.get();
    }

    @Override
    public boolean isHidden() {
        return isHidden;
    }

    @Override
    public boolean isRestartable() {
        return isRestartable;
    }

    @Override
    public Set<String> getRequires() {
        return requires;
    }

    @Override
    public List<Object> getReloadOnConfigs() {
        return reloadOnConfigs;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return "MinionHandler{" + "service=" + name + '}';
    }

    public static MinionHandlerImplBuilder builder() {
        return new MinionHandlerImplBuilder();
    }

    @Override
    public void setStatus(MinionRuntimeStatus status) {
        this.status = status;
    }

    public static class MinionHandlerImplBuilder implements Builder<MinionHandlerImpl, MinionHandlerImplBuilder> {

        private String name, description, configEnabler;
        private Supplier<MinionRuntimeStatus> statusChecker;
        private Supplier<Boolean> enabledChecker;
        private Boolean isHidden, isRestartable;
        private MinionRuntimeStatus status;
        private final Set<String> requires = set();
        private final List<Object> reloadOnConfigs = list();
        private Integer order;

        public MinionHandlerImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public MinionHandlerImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        @Nullable
        public MinionHandlerImplBuilder withConfigEnabler(String configEnabler) {
            this.configEnabler = configEnabler;
            return this;
        }

        public MinionHandlerImplBuilder withOrder(Integer order) {
            this.order = order;
            return this;
        }

        public MinionHandlerImplBuilder reloadOnConfigs(Object... configs) {
            this.reloadOnConfigs.addAll(list(configs));
            return this;
        }

        public MinionHandlerImplBuilder withRequires(String... requires) {
            this.requires.addAll(set(requires));
            return this;
        }

        public MinionHandlerImplBuilder withRequires(SystemStatus... requires) {
            this.requires.addAll(list(requires).map(s -> serializeEnum(s)));
            return this;
        }

        public MinionHandlerImplBuilder withStatusChecker(Supplier<MinionRuntimeStatus> statusChecker) {
            this.statusChecker = statusChecker;
            return this;
        }

        public MinionHandlerImplBuilder withStatus(MinionRuntimeStatus status) {
            this.status = status;
            return this;
        }

        public MinionHandlerImplBuilder withEnabledChecker(Supplier<Boolean> enabledChecker) {
            this.enabledChecker = enabledChecker;
            return this;
        }

        public MinionHandlerImplBuilder withEnabledChecker(boolean isEnabled) {
            return this.withEnabledChecker(Suppliers.ofInstance(isEnabled));
        }

        public MinionHandlerImplBuilder withHidden(boolean isHidden) {
            this.isHidden = isHidden;
            return this;
        }

        public MinionHandlerImplBuilder withRestartable(boolean isRestartable) {
            this.isRestartable = isRestartable;
            return this;
        }

        @Override
        public MinionHandlerImpl build() {
            return new MinionHandlerImpl(this);
        }

    }
}
