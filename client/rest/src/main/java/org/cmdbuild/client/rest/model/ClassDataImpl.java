/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

@JsonDeserialize(builder = ClassDataImpl.SimpleClassDataBuilder.class)
public class ClassDataImpl implements ClassData {

    private final String name, description;
    private final String type;//TODO try enum
    private final String parentId;
    private final boolean isActive, isSuperclass;

    private ClassDataImpl(SimpleClassDataBuilder builder) {
        this.name = checkNotBlank(builder.name);
        this.description = firstNotBlank(builder.description, name);
        this.type = checkNotBlank(builder.type);
        this.parentId = builder.parentId;
        this.isActive = builder.isActive;
        this.isSuperclass = builder.isSuperclass;
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
    public String getType() {
        return type;
    }

    @Override
    @Nullable
    public String getParentId() {
        return parentId;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean isSuperclass() {
        return isSuperclass;
    }

    public static SimpleClassDataBuilder builder() {
        return new SimpleClassDataBuilder();
    }

    public static SimpleClassDataBuilder copyOf(ClassDataImpl source) {
        return new SimpleClassDataBuilder()
                .withName(source.getName())
                .withDescription(source.getDescription())
                .withType(source.getType())
                .withParentId(source.getParentId())
                .withActive(source.isActive())
                .withSuperclass(source.isSuperclass());
    }

    public static class SimpleClassDataBuilder implements Builder<ClassDataImpl, SimpleClassDataBuilder> {

        private String name;
        private String description;
        private String type;
        private String parentId;
        private Boolean isActive, isSuperclass;

        public SimpleClassDataBuilder withDefaults() {
            this.type = "class";
            this.isActive = true;
            this.isSuperclass = false;
            return this;
        }

        public SimpleClassDataBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public SimpleClassDataBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public SimpleClassDataBuilder withType(String type) {
            this.type = type;
            return this;
        }

        @JsonProperty("parent")
        public SimpleClassDataBuilder withParentId(String parentId) {
            this.parentId = parentId;
            return this;
        }

        public SimpleClassDataBuilder withActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        @JsonProperty("prototype")
        public SimpleClassDataBuilder withSuperclass(Boolean isSuperclass) {
            this.isSuperclass = isSuperclass;
            return this;
        }

        @Override
        public ClassDataImpl build() {
            return new ClassDataImpl(this);
        }

    }
}
