package org.cmdbuild.config;

import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.etl")
public class EtlConfigurationImpl implements EtlConfiguration {

    @ConfigValue(key = "thousandsSeparator", description = "", defaultValue = "")
    private String thousandsSeparator;

    @ConfigValue(key = "valueArraySeparator", description = "default separator used for value arrays (es: lookup array) for import/export csv etc", defaultValue = "##")
    private String valueArraySeparator;

    @ConfigValue(key = "referenceCacheMaxSize", description = "reference cache max size, default is 10000; increase this if you have more than 10000 references", defaultValue = "10000")
    private Integer referenceCacheMaxSize;

    @ConfigValue(key = "allowDuplicateAttachmentName", description = "rename duplicate attachments, for example from multipart ws", defaultValue = FALSE)
    private Boolean allowDuplicateAttachmentName;

    @Override
    public String getThousandsSeparator() {
        return thousandsSeparator;
    }

    @Override
    public String getValueArraySeparator() {
        return valueArraySeparator;
    }

    @Override
    public Integer getReferenceCacheMaxSize() {
        return referenceCacheMaxSize;
    }

    @Override
    public Boolean allowDuplicateAttachmentName() {
        return allowDuplicateAttachmentName;
    }

}
