/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.utils;

import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.SorterElementDirection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Splitter;
import java.io.IOException;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.data.filter.SorterElementDirection.ASC;
import org.cmdbuild.data.filter.beans.CmdbSorterImpl;
import org.cmdbuild.data.filter.beans.SorterElementImpl;
import static org.cmdbuild.logic.mapping.json.Constants.DIRECTION_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.PROPERTY_KEY;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.isJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.json.JSONArray;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.illegalArgument;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public class CmSorterUtils {

    private static final ObjectMapper MAPPER = CmFilterUtils.MAPPER;

    private static final CmdbSorter NOOP_SORTER = new CmdbSorterImpl(emptyList());

    private CmSorterUtils() {
        // utility class
    }

    public static CmdbSorter noopSorter() {
        return NOOP_SORTER;
    }

    public static CmdbSorter parseSorter(@Nullable String value) {
        if (isBlank(value)) {
            return NOOP_SORTER;
        } else {
            try {
                if (isJson(value)) {
                    List<Map<String, String>> elements = CmJsonUtils.fromJson(value, CmJsonUtils.LIST_OF_MAP_OF_STRINGS);
                    return new CmdbSorterImpl(list(elements).map(e -> {
                        String property = firstNotBlank(e.get(PROPERTY_KEY), e.get("attribute"));
                        String direction = checkNotBlank(e.get(DIRECTION_KEY)).trim().toLowerCase().replace("ascending", "asc").replace("descending", "desc");
                        return new SorterElementImpl(property, parseEnum(direction, SorterElementDirection.class));
                    }));
                } else {
                    return new CmdbSorterImpl(list(Splitter.on(",").trimResults().omitEmptyStrings().splitToList(value)).map(e -> {
                        Matcher matcher = Pattern.compile("(?i)([^:\\s]+)([:\\s]+(asc|desc))?").matcher(e);
                        checkArgument(matcher.matches(), "invalid sorter pattern");
                        return new SorterElementImpl(matcher.group(1), parseEnumOrDefault(matcher.group(3), ASC));
                    }));
                }
            } catch (Exception ex) {
                throw illegalArgument(ex, "error deserializing sorter =< %s >", abbreviate(value));
            }
        }
    }

    public static CmdbSorter fromJson(JSONArray jsonArray) {
        try {
            String json = MAPPER.writeValueAsString(jsonArray);
            return parseSorter(json);
        } catch (IOException ex) {
            throw illegalArgument(ex);
        }
    }

    public static String serializeSorter(@Nullable CmdbSorter sorter) {
        try {
            return MAPPER.writeValueAsString(firstNotNull(sorter, NOOP_SORTER).getElements().stream().map(e -> map(PROPERTY_KEY, e.getProperty(), DIRECTION_KEY, e.getDirection().name())).toList());
        } catch (JsonProcessingException ex) {
            throw runtime(ex);
        }
    }

}
