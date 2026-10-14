/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.common.utils;

import java.util.Collection;
import static java.util.Collections.emptyMap;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class WsResponseUtils {

    public static FluentMap<String, Object> response(Stream pagedData, long totalSize) {
        return response((Collection) pagedData.collect(toList()), totalSize);
    }

    public static FluentMap<String, Object> response(Collection pagedData, long totalSize) {
        return response(pagedData, totalSize, emptyMap());
    }

    public static FluentMap<String, Object> response(Collection pagedData, long totalSize, Map meta) {
        return simpleResponse(pagedData).with("meta", map("total", totalSize).with(meta));
    }

    public static FluentMap<String, Object> response(PagedElements paged, Map meta) {
        return response(paged.elements(), paged.totalSize(), meta);
    }

    public static FluentMap<String, Object> response(PagedElements paged) {
        return response(paged.elements(), paged.totalSize());
    }

    public static FluentMap<String, Object> response(Object value) {
        if (value instanceof Stream stream) {
            value = stream.collect(toList());
        }
        if (value instanceof Iterable && !(value instanceof Collection)) {
            value = list((Iterable) value);
        }
        if (value instanceof Collection collection) {
            return response(collection, collection.size());
        } else {
            return simpleResponse(value);
        }
    }

    private static FluentMap<String, Object> simpleResponse(Object value) {
        return success().with("data", value);
    }

    public static FluentMap<String, Object> success() {
        return map("success", true);
    }

    public static FluentMap<String, Object> failure() {
        return map("success", false);
    }
}
