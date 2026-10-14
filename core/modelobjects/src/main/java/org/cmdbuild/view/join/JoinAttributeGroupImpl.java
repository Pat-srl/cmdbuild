/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.view.join;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Map;
import java.util.Objects;
import org.cmdbuild.dao.entrytype.AttributeGroupDefaultDisplayMode;
import static org.cmdbuild.dao.entrytype.AttributeGroupDefaultDisplayMode.AGDDM_OPEN;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public class JoinAttributeGroupImpl implements JoinAttributeGroup {

    private final String name, description;
    private final AttributeGroupDefaultDisplayMode defaultDisplayMode;

    private JoinAttributeGroupImpl(JoinAttributeGroupImplBuilder builder) {
        this(builder.toMap());
    }

    @JsonCreator
    public JoinAttributeGroupImpl(Map<String, ?> config) {
        name = toStringNotBlank(config.get("name"));
        description = toStringNotBlank(firstNotBlank(config.get("description"), name));
        defaultDisplayMode = parseEnumOrDefault(toStringOrNull(config.get("defaultDisplayMode")), AGDDM_OPEN);
    }

    @JsonAnyGetter
    public Map<String, String> toMap() {
        return copyOf(this).toMap();
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
    public String getDefaultDisplayMode() {
        return serializeEnum(defaultDisplayMode);
    }

    @Override
    public String toString() {
        return "JoinAttributeGroup{" + "name=" + name + '}';
    }

    public static JoinAttributeGroupImplBuilder builder() {
        return new JoinAttributeGroupImplBuilder();
    }

    public static JoinAttributeGroupImplBuilder copyOf(JoinAttributeGroup source) {
        return new JoinAttributeGroupImplBuilder()
                .withName(source.getName())
                .withDescription(source.getDescription())
                .withDefaultDisplayMode(source.getDefaultDisplayMode());
    }

    public static class JoinAttributeGroupImplBuilder implements Builder<JoinAttributeGroupImpl, JoinAttributeGroupImplBuilder> {

        private String name, description, defaultDisplayMode;

        public Map<String, String> toMap() {
            return (Map) map("name", name, "description", description, "defaultDisplayMode", defaultDisplayMode).withoutValues(Objects::isNull);
        }

        public JoinAttributeGroupImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public JoinAttributeGroupImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public JoinAttributeGroupImplBuilder withDefaultDisplayMode(String defaultDisplayMode) {
            this.defaultDisplayMode = defaultDisplayMode;
            return this;
        }

        @Override
        public JoinAttributeGroupImpl build() {
            return new JoinAttributeGroupImpl(this);
        }

    }
}
