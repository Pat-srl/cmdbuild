/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.utils.lang.CmConvertUtils;
import org.cmdbuild.report.ReportConfigImpl;
import static org.cmdbuild.utils.lang.CmMapUtils.nullToEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WsReportConfigData {

    private final String code;
    private final ReportFormat format;
    private final Map<String, Object> params;

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    public WsReportConfigData(
            @JsonProperty("code") String code,
            @JsonProperty("format") String format,
            @JsonProperty("params") Map<String, Object> params) {
        this.code = code;
        this.format = CmConvertUtils.parseEnum(format, ReportFormat.class);
        
        logger.debug("input report params =< {} >", params);
        this.params = nullToEmpty(params);
        logger.debug("fetched input report params =< {} >", this.params);
    }

    public ReportConfigImpl.ReportConfigImplBuilder buildReportConfig() {
        return ReportConfigImpl.builder()
                .withCode(code)
                .withFormat(format)
                .withParams(params); 
    }

}
