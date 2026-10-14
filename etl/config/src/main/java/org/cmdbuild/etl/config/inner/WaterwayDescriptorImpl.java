/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config.inner;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import static java.util.Collections.unmodifiableMap;
import java.util.List;
import java.util.Map;
import org.cmdbuild.etl.config.WaterwayDescriptor;
import org.cmdbuild.etl.config.WaterwayItemConfig;
import static org.cmdbuild.utils.json.CmJsonUtils.collectionType;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.nullToEmpty;

public class WaterwayDescriptorImpl implements WaterwayDescriptor {

    private final String code, description, notes, tag;
    private final List<WaterwayItemConfig> items;
    @JsonAnySetter
    private final Map<String, String> config = CmMapUtils.map();

    public WaterwayDescriptorImpl(@JsonProperty(WY_DESCRIPTOR_CODE) String code, @JsonProperty(WY_DESCRIPTOR_DESCRIPTION) String description, @JsonProperty("notes") String notes, @JsonProperty("tag") String tag, @JsonProperty("items") ArrayNode items) {
        this.code = checkNotBlank(code);
        this.description = Strings.nullToEmpty(description);
        this.notes = Strings.nullToEmpty(notes);
        this.tag = nullToEmpty(tag);
        this.items = List.copyOf((List) fromJson(items, collectionType(WaterwayItemConfigImpl.class)));
        Preconditions.checkArgument(list(this.items).map(WaterwayItemConfig::getCode).duplicates().isEmpty(), "invalid config: duplicate item codes");
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
    public String getNotes() {
        return notes;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public List<WaterwayItemConfig> getItems() {
        return items;
    }

    @Override
    public Map<String, String> getConfig() {
        return unmodifiableMap(config);
    }

    @Override
    public String toString() {
        return "WaterwayDescriptor{" + "code=" + code + '}';
    }

}
