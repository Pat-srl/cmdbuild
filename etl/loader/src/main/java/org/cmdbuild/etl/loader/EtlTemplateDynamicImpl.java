package org.cmdbuild.etl.loader;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class EtlTemplateDynamicImpl implements EtlTemplateDynamic {

    private final String code, description;
    private final boolean isActive;
    private final Object data;

// processed vars:[[Ljava.lang.String;@1f0a77c2, [Ljava.lang.String;@2717dac3, [Ljava.lang.String;@7f1549b6, [Ljava.lang.String;@2ef6b51f]
    private EtlTemplateDynamicImpl(EtlTemplateDynamicImplBuilder builder) {
        this.code = checkNotBlank(builder.code);
        this.description = nullToEmpty(builder.description);
        this.isActive = firstNotNull(builder.isActive, true);
        this.data = checkNotNull(builder.data);
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
    public boolean isActive() {
        return isActive;
    }

    @Override

    public Object getDynamicTemplate() {
        return data;
    }

    @Override
    public String toString() {
        return "EtlTemplateDynamic{" + "code=" + code + '}';
    }

    public static EtlTemplateDynamicImplBuilder builder() {
        return new EtlTemplateDynamicImplBuilder();
    }

    public static EtlTemplateDynamicImplBuilder copyOf(EtlTemplateDynamic source) {
        return new EtlTemplateDynamicImplBuilder()
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withActive(source.isActive())
                .withData(source.getDynamicTemplate());
    }

    public static class EtlTemplateDynamicImplBuilder implements Builder<EtlTemplateDynamicImpl, EtlTemplateDynamicImplBuilder> {

        private String code;
        private String description;
        private boolean isActive;
        private Object data;

        public EtlTemplateDynamicImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public EtlTemplateDynamicImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public EtlTemplateDynamicImplBuilder withActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public EtlTemplateDynamicImplBuilder withData(Object data) {
            this.data = data;
            return this;
        }

        @Override
        public EtlTemplateDynamicImpl build() {
            return new EtlTemplateDynamicImpl(this);
        }

    }
}
