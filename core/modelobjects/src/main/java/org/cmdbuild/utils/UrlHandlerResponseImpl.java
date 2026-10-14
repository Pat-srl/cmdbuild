/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils;

import java.util.Map;
import jakarta.activation.DataSource;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class UrlHandlerResponseImpl implements UrlHandlerResponse {

    private final DataSource dataSource;
    private final Map<String, String> meta;

    public UrlHandlerResponseImpl(DataSource dataSource, Map<String, String> meta) {
        this.dataSource = dataSource;
        this.meta = map(meta).immutable();
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public Map<String, String> getMeta() {
        return meta;
    }

}
