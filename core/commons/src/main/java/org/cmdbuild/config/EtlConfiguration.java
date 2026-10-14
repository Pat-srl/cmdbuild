package org.cmdbuild.config;

public interface EtlConfiguration {

    String getThousandsSeparator();

    String getValueArraySeparator();

    Integer getReferenceCacheMaxSize();

    Boolean allowDuplicateAttachmentName();

}
