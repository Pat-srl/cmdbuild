/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

import java.util.List;
import java.util.Map;

import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

/**
 * @author ldare
 */
public class WsUtils {

    public static List<FluentMap<String, Object>> filterSerializations(List<FluentMap<String, Object>> serializationList, String filterStr) {

        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
        if (filter.hasAttributeFilter()) {
            serializationList = AttributeFilterProcessor.<Map<String, Object>>builder().withKeyToValueFunction((k, m) -> toStringOrNull(m.get(k))).withFilter(filter.getAttributeFilter()).filter(serializationList);
        }
        return serializationList;
    }

    public static List<FluentMap<String, Object>> filterSerializations(List<FluentMap<String, Object>> serializationList, WsQueryOptions wsQueryOptions) {
        wsQueryOptions.getQuery().getFilter().checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
        if (wsQueryOptions.getQuery().getFilter().hasAttributeFilter()) {
            serializationList = AttributeFilterProcessor.builder().withFilter(wsQueryOptions.getQuery().getFilter().getAttributeFilter()).withKeyToValueFunction((k, m) -> ((Map) m).get(k)).filter(serializationList);
        }
        return serializationList;
    }
}
