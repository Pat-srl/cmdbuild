/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.ws.rs.core.MediaType;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.cmdbuild.report.ReportInfoImpl;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.MoreCollectors.onlyElement;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmMapUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 *
 * @author ldare
 */
public class WsReportData {


//TODO write custom report config !!

    private final String description, code, iconFont;
    private final boolean isActive;
    private final Map<String, String> config;

    public WsReportData(
            @JsonProperty("description") String description,
            @JsonProperty("code") String code,
            @JsonProperty("active") Boolean isActive,
            @JsonProperty("config") Map<String, String> config,
            @JsonProperty("icon_font") String iconFont
    ) {
        this.description = description;
        this.code = checkNotBlank(code, "missing code param");
        this.isActive = isActive;
        this.config = nullToEmpty(config);
        this.iconFont = iconFont;
    }

    public ReportInfoImpl.ReportInfoImplBuilder toReportInfo() {
        return ReportInfoImpl.builder()
                .withActive(isActive)
                .withCode(code)
                .withDescription(description)
                .withConfig(config)
                .withIconFont(iconFont);
    }

    public static WsReportData getData(List<Attachment> attachments) {
        return fromJson(readToString(attachments.stream().filter(a -> a.getContentType().isCompatible(MediaType.APPLICATION_JSON_TYPE)).collect(onlyElement()).getDataHandler()), WsReportData.class);
    }
}
