/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.etl.config.WaterwayDescriptorMeta;
import org.cmdbuild.etl.config.WaterwayDescriptorMetaImpl;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.toListOfStrings;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

/**
 * @author ldare
 */
public class WsConfigMeta {

    private final List<String> disabled;
    private final Map<String, String> params;
    private final boolean enabled;
    private final String data, code, description;

    public WsConfigMeta(@JsonProperty("disabled") String disabled,
                        @JsonProperty("enabled") Boolean enabled,
                        @JsonProperty("params") Map<String, String> params,
                        @JsonProperty("data") String data,
                        @JsonProperty("code") String code,
                        @JsonProperty("description") String description) {
        this.disabled = toListOfStrings(disabled);
        this.enabled = firstNotNull(enabled, true);
        this.params = map(checkNotNull(params)).immutable();
        this.data = data;
        this.code = code;
        this.description = description;
    }

    public WaterwayDescriptorMeta toMeta() {
        return WaterwayDescriptorMetaImpl.builder().withEnabled(enabled).withDisabledItems(disabled).withParams(params).withCode(code).withDescription(description).build();
    }

    public String getData() {
        return this.data;
    }

}