package org.cmdbuild.easytemplate;

import static com.google.common.base.Strings.nullToEmpty;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.easytemplate.EasytemplateImpl.TEMPLATES_TABLE;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping(TEMPLATES_TABLE)
public class EasytemplateImpl implements Easytemplate {

    public static final String TEMPLATES_TABLE = "_Templates";
    public static final String TEMPLATE_NAME = "Name";
    public static final String TEMPLATE_DEFINITION = "Template";

    private final Long id;
    private final String key;
    private final String value;

    private EasytemplateImpl(EasytemplateImplBuilder builder) {
        this.id = builder.id;
        this.key = checkNotBlank(builder.key);
        this.value = nullToEmpty(builder.value);
    }

    private EasytemplateImpl(String key, String value) {
        this.id = null;
        this.key = key;
        this.value = value;
    }

    public static EasytemplateImpl of(String key) {
        return new EasytemplateImpl(key, null);
    }

    public static EasytemplateImpl of(String key, String value) {
        return new EasytemplateImpl(key, value);
    }

    @CardAttr(ATTR_ID)
    @Override
    @Nullable
    public Long getId() {
        return id;
    }

    @CardAttr(TEMPLATE_NAME)
    @Override
    public String getKey() {
        return key;
    }

    @CardAttr(TEMPLATE_DEFINITION)
    @Override
    public String getValue() {
        return value;
    }

    public static EasytemplateImplBuilder builder() {
        return new EasytemplateImplBuilder();
    }

    public static EasytemplateImplBuilder copyOf(EasytemplateImpl source) {
        return new EasytemplateImplBuilder()
                .withId(source.getId())
                .withKey(source.getKey())
                .withValue(source.getValue());
    }

    public static class EasytemplateImplBuilder implements Builder<EasytemplateImpl, EasytemplateImplBuilder> {

        private Long id;
        private String key;
        private String value;

        public EasytemplateImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public EasytemplateImplBuilder withKey(String key) {
            this.key = key;
            return this;
        }

        public EasytemplateImplBuilder withValue(String value) {
            this.value = value;
            return this;
        }

        @Override
        public EasytemplateImpl build() {
            return new EasytemplateImpl(this);
        }

    }
}
