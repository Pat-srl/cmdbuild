/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.common.serializationhelpers;


import com.fasterxml.jackson.databind.JsonNode;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.utils.lang.CmMapUtils;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.cmdbuild.dao.utils.CmFilterProcessingUtils.mapFilter;
import static org.cmdbuild.dao.utils.CmFilterUtils.serializeFilter;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
public class EtlTemplateSerializationHelper {

    public static CmMapUtils.FluentMap serializeBasicTemplate(EtlTemplate template) {
        return map(
                "_id", template.getCode(),
                "code", template.getCode(),
                "description", template.getDescription(),
                "targetType", serializeEnum(template.getTargetType()),
                "targetName", template.getTargetName(),
                "active", template.isActive(),
                "type", serializeEnum(template.getType()),
                "_export", template.isExportTemplate(),
                "_import", template.isImportTemplate()
        );
    }

    public static CmMapUtils.FluentMap serializeDetailedTemplate(EtlTemplate template) {
        return serializeBasicTemplate(template).with(
                "fileFormat", serializeEnum(template.getFileFormat()),
                "errorTemplate", template.getErrorTemplate(),
                "notificationTemplate", template.getNotificationTemplate(),
                "errorAccount", template.getErrorAccount(),
                "notificationAccount", template.getNotificationAccount(),
                "exportFilter", serializeFilter(template.getExportFilter()),//TODO improve this, see below
                "importFilter", serializeFilter(template.getImportFilter()),
                "filter", fromJson(serializeFilter(template.getFilter()), JsonNode.class),
                "mergeMode", serializeEnum(template.getMergeMode()),
                "mergeMode_when_missing_update_attr", template.getAttributeNameForUpdateAttrOnMissing(),
                "mergeMode_when_missing_update_value", template.getAttributeValueForUpdateAttrOnMissing(),
                "csv_separator", template.getCsvSeparator(),
                "importKeyAttributes", template.getImportKeyAttributes(),
                "handleMissingRecordsOnError", template.getHandleMissingRecordsOnError(),
                "useHeader", template.getUseHeader(),
                "ignoreColumnOrder", template.getIgnoreColumnOrder(),
                "headerRow", template.getHeaderRow(),
                "dataRow", template.getDataRow(),
                "firstCol", template.getFirstCol(),
                "source", template.getSource(),
                "charset", template.getCharset(),
                "decimalSeparator", template.getDecimalSeparator(),
                "dateFormat", template.getDateFormat(),
                "timeFormat", template.getTimeFormat(),
                "dateTimeFormat", template.getDateTimeFormat(),
                "thousandsSeparator", template.getThousandsSeparator(),
                "enableCreate", serializeEnum(template.getEnableCreate()),
                "columns", template.getColumns().stream().map(c -> map(
                        "attribute", c.getAttributeName(),
                        "columnName", c.getColumnName(),
                        "default", c.getDefault(),
                        "mode", serializeEnum(c.getMode())
                ).skipNullValues().with(
                        "decimalSeparator", c.getDecimalSeparator(),
                        "dateFormat", c.getDateFormat(),
                        "timeFormat", c.getTimeFormat(),
                        "dateTimeFormat", c.getDateTimeFormat(),
                        "thousandsSeparator", c.getThousandsSeparator()
                )).collect(toList()));
    }

    public static List<Map<String, Object>> filterAndApplySerialization(List<EtlTemplate> templates, WsQueryOptions wsQueryOptions) {
        return list(templates)
                .map(wsQueryOptions.isDetailed() ? EtlTemplateSerializationHelper::serializeDetailedTemplate : EtlTemplateSerializationHelper::serializeBasicTemplate)
                .withOnly(mapFilter(wsQueryOptions.getQuery().getFilter()));
    }
}
