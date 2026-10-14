/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.activation.DataSource;
import static java.util.Collections.emptyMap;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class WsPart {

    public static final String DEFAULT_PART = "DEFAULT";

    private final DataSource dataSource;
    private final String partName;
    private final Map<String, String> headers;

    public WsPart(String partName, DataSource dataSource) {
        this(dataSource, partName, emptyMap());
    }

    public WsPart(DataSource dataSource, String partName, Map<String, String> headers) {
        this.dataSource = checkNotNull(dataSource);
        this.partName = checkNotBlank(partName);
        this.headers = map(headers).immutable();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public String getPartName() {
        return partName;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

}
