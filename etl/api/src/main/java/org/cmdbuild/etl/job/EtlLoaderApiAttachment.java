package org.cmdbuild.etl.job;

import java.util.Map;
import jakarta.activation.DataSource;

public interface EtlLoaderApiAttachment {

    String getCode();

    String getDataAsString();

    DataSource getData();

    Object getObject();

    Map<String, String> getMeta();

}
