/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ifc.inner;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.List;
import java.util.Map;
import org.cmdbuild.utils.ifc.IfcModel;
import static org.cmdbuild.utils.ifc.IfcModel.RECORD_SOURCE_ENTRY;
import static org.cmdbuild.utils.lang.CmConvertUtils.isPrimitiveOrWrapper;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IfcMapper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final IfcModel ifc;

    public IfcMapper(IfcModel ifc) {
        this.ifc = checkNotNull(ifc);
    }

    public List<Map<String, Object>> extractRecords(String xpathSelector, Map<String, String> attributeFromXpathMapping) {
        logger.debug("extract ifc records for context =< {} > with mapping = \n\n{}\n", xpathSelector, mapToLoggableStringLazy(attributeFromXpathMapping));
        return ifc.queryEntries(xpathSelector).stream().map(e -> {
            logger.debug("processing record = {}", e);
            Map<String, Object> record = map();
            attributeFromXpathMapping.forEach((attribute, xpath) -> {
                logger.trace("attribute: {}, xpath: {}", attribute, xpath);
                Object value = e.queryValue(xpath);
                logger.trace("query value: {}", value);
                checkArgument(value == null || isPrimitiveOrWrapper(value), "invalid value type found for selector =< {} > context = {}, value =< {} > {}", xpath, e, value, getClassOfNullable(value).getSimpleName());
                record.put(attribute, value);
            });
            record.put(RECORD_SOURCE_ENTRY, e);
            logger.trace("processed record = {} extracted values = \n\n{}\n", e, mapToLoggableStringLazy(record));
            return record;
        }).collect(toImmutableList());
    }

}
