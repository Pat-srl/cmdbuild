/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.etl.loader.EtlTemplateColumnConfig;
import org.cmdbuild.etl.loader.EtlTemplateColumnConfigImpl;
import org.cmdbuild.etl.loader.EtlTemplateColumnMode;

import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;

/**
 * @author ldare
 */
public class WsEtlColumnData {

    private final String attribute, columnName, defaultValue, dateFormat, timeFormat, decimalSeparator, dateTimeFormat, thousandsSeparator;
    private final EtlTemplateColumnMode mode;

    public WsEtlColumnData(
            @JsonProperty("attribute") String attribute,
            @JsonProperty("columnName") String columnName,
            @JsonProperty("default") String defaultValue,
            @JsonProperty("dateFormat") String dateFormat,
            @JsonProperty("timeFormat") String timeFormat,
            @JsonProperty("decimalSeparator") String decimalSeparator,
            @JsonProperty("dateTimeFormat") String dateTimeFormat,
            @JsonProperty("thousandsSeparator") String thousandsSeparator,
            @JsonProperty("mode") String mode) {
        this.attribute = attribute;
        this.columnName = columnName;
        this.defaultValue = defaultValue;
        this.dateFormat = dateFormat;
        this.timeFormat = timeFormat;
        this.decimalSeparator = decimalSeparator;
        this.thousandsSeparator = thousandsSeparator;
        this.dateTimeFormat = dateTimeFormat;
        this.mode = parseEnumOrNull(mode, EtlTemplateColumnMode.class);
    }

    public EtlTemplateColumnConfig toColumnConfig() {
        return EtlTemplateColumnConfigImpl.builder()
                .withAttributeName(attribute)
                .withColumnName(columnName)
                .withDefault(defaultValue)
                .withMode(mode)
                .withTimeFormat(timeFormat)
                .withDateFormat(dateFormat)
                .withDecimalSeparator(decimalSeparator)
                .withDateTimeFormat(dateTimeFormat)
                .withThousandsSeparator(thousandsSeparator)
                .build();
    }
}
