/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.corecomponents;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
 
public class CoreComponentImpl implements CoreComponent {
 
    private final boolean isActive;
    private final String code, description, data;
    private final CoreComponentType type;

    private CoreComponentImpl(CoreComponentImplBuilder builder) { 
        this.isActive = firstNotNull(builder.isActive, true);
        this.code = checkNotBlank(builder.code);
        this.description = nullToEmpty(builder.description);
        this.type = checkNotNull(builder.type);
        this.data = builder.data;
    } 

    @Override 
    public boolean isActive() {
        return isActive;
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
    public String getData() {
        return data;
    }

    @Override
    @CardAttr
    public CoreComponentType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "CoreComponent{" + "code=" + code + ", type=" + type + '}';
    }

    public static CoreComponentImplBuilder builder() {
        return new CoreComponentImplBuilder();
    }

    public static CoreComponentImplBuilder copyOf(CoreComponent source) {
        return new CoreComponentImplBuilder() 
                .withActive(source.isActive())
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withData(source.getData())
                .withType(source.getType());
    }

    public static class CoreComponentImplBuilder implements Builder<CoreComponentImpl, CoreComponentImplBuilder> {
 
        private Boolean isActive;
        private String code;
        private String description;
        private String data;
        private CoreComponentType type;
 
        public CoreComponentImplBuilder withActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public CoreComponentImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public CoreComponentImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public CoreComponentImplBuilder withData(String data) {
            this.data = data;
            return this;
        }

        public CoreComponentImplBuilder withType(CoreComponentType type) {
            this.type = type;
            return this;
        }

        @Override
        public CoreComponentImpl build() {
            return new CoreComponentImpl(this);
        }

    }
}
