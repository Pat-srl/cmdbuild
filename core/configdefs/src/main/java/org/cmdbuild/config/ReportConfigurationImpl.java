/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackIfPacked;
import static org.cmdbuild.utils.json.CmJsonUtils.LIST_OF_MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.report")
public class ReportConfigurationImpl implements ReportConfiguration {

    @ConfigValue(key = "fonts", description = "custom font config (see report docs)", defaultValue = "packj3ghbsh2d9srf6eed470gxtp7vrspq9pqx07dyfyyd58wnvuqxrlk0newh9cq3tcs91jbx29gmk6scvmvqkxrgxxqdlwuoarxdpen7l5900g250is6gje2efr3bj0o0n4dvb4rkyexto9ksy2dv37fft0y10wpw11xhthdnebjgk9cingarafl2rwwd4vam3c85id6zp4vdh9htf24tipw3ukqbom3x4zqk0e4lzhs6pbtga6ukarksh297iceezgypkcap")
    private String fontConfigs;

    @ConfigValue(key = "ignoreMissingFont", description = "set jasper report config `net.sf.jasperreports.awt.ignore.missing.font`", defaultValue = TRUE)
    private Boolean ignoreMissingFont;

    @ConfigValue(key = "defaultPdfEncoding", description = "set jasper report config `net.sf.jasperreports.default.pdf.encoding`", defaultValue = "Identity-H")
    private String defaultPdfEncoding;

    @ConfigValue(key = "defaultPdfEmbedded", description = "set jasper report config `net.sf.jasperreports.default.pdf.embedded`", defaultValue = FALSE)
    private Boolean defaultPdfEmbedded;

    @ConfigValue(key = "defaultPdfFont", description = "set jasper report config `net.sf.jasperreports.default.pdf.font.name` and `net.sf.jasperreports.default.font.name`", defaultValue = "SansSerif")
    private String defaultPdfFont;

    @ConfigValue(key = "otherConfigs", description = "other (custom) jasper report configs (optionally-packed json map)", defaultValue = "{}")
    private String otherConfigs;

    @Override
    public List<Map<String, String>> getFontConfigs() {
        return fromJson(unpackIfPacked(fontConfigs), LIST_OF_MAP_OF_STRINGS);
    }

    @Override
    public Map<String, String> getOtherReportConfigs() {
        return isBlank(otherConfigs) ? emptyMap() : fromJson(unpackIfPacked(otherConfigs), MAP_OF_STRINGS);
    }

    @Override
    @Nullable
    public Boolean ignoreMissingFont() {
        return ignoreMissingFont;
    }

    @Override
    @Nullable
    public String getDefaultPdfEncoding() {
        return defaultPdfEncoding;
    }

    @Override
    @Nullable
    public String getDefaultPdfFont() {
        return defaultPdfFont;
    }

    @Override
    @Nullable
    public Boolean getDefaultPdfEmbedded() {
        return defaultPdfEmbedded;
    }
}
