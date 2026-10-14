/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.beans;

import static com.google.common.base.Predicates.not;
import com.google.common.base.Splitter;
import static com.google.common.collect.Maps.filterKeys;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.defaultString;
import org.cmdbuild.dao.function.FunctionMetadata;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import org.cmdbuild.dao.function.StoredFunctionCategory;
import static org.cmdbuild.dao.function.StoredFunctionCategory.UNDEFINED;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public class FunctionMetadataImpl extends EntryTypeMetadataImpl implements FunctionMetadata {

    private final static Set<String> FUNCTION_METADATA_ATTRS = set(MASTERTABLE, CATEGORIES, TAGS, SOURCE).immutable();

    private final Set<StoredFunctionCategory> categories;
    private final Set<String> tags;
//	private final Map<String, Object> metadata;
    private final String masterTable, source;

    public FunctionMetadataImpl(Map<String, String> map) {
        super(map, filterKeys(map, not(FUNCTION_METADATA_ATTRS::contains)));

        masterTable = defaultString(map.get(MASTERTABLE));
        source = toStringOrNull(map.get(SOURCE));

        tags = set(Splitter.on(",")
                .trimResults()
                .omitEmptyStrings()
                .split(defaultString(map.get(TAGS))))
                .immutable();

        categories = set(Splitter.on(",").trimResults().omitEmptyStrings().splitToList(defaultString(map.get(CATEGORIES))).stream().map(v -> parseEnumOrDefault(v, UNDEFINED)).collect(toList()));

//		metadata = map(CATEGORIES, categories, MASTERTABLE, masterTable, TAGS, tags);
    }

    @Override
    public Collection<StoredFunctionCategory> getCategories() {
        return categories;
    }

    @Override
    public Set<String> getTags() {
        return tags;
    }

    @Override
    public String getMasterTable() {
        return masterTable;
    }

    @Override
    @Nullable
    public String getSource() {
        return source;
    }

}
