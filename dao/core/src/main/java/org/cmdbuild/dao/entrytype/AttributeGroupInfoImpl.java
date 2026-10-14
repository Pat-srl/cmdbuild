/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import static java.util.Collections.emptyMap;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.entrytype.AttributeGroupData.ATTRIBUTE_GROUP_DEFAULT_DISPLAY_MODE;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public class AttributeGroupInfoImpl implements AttributeGroupInfo {

    private final String name, description;
    private final Map<String, String> config;

    public AttributeGroupInfoImpl(String name, @Nullable String description) {
        this(name, description, emptyMap());
    }

    public AttributeGroupInfoImpl(String name, @Nullable String description, Map<String, String> config) {
        this.name = checkNotBlank(name);
        this.description = firstNotBlank(description, name);
        this.config = map(config).immutable();
        parseEnumOrNull(config.get(ATTRIBUTE_GROUP_DEFAULT_DISPLAY_MODE), AttributeGroupDefaultDisplayMode.class);//TODO improve this
    }

    public AttributeGroupInfoImpl(String name) {
        this(name, null);
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
    public Map<String, String> getConfig() {
        return config;
    }

    @Override
    public String toString() {
        return "AttributeGroupInfo{" + "name=" + name + '}';
    }

}
