/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api;

import static java.util.Collections.emptyMap;
import java.util.Map;
import jakarta.activation.DataSource;
import static org.cmdbuild.utils.io.CmIoUtils.isUrl;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSourceFromUrl;

public interface EtlApi {

    EtlGateJobApi load(String gate, DataSource data, Map<String, String> meta);

    default EtlGateJobApi load(String gate) {
        return load(gate, "", emptyMap());
    }

    default EtlGateJobApi load(String gate, Map<String, String> meta) {
        return load(gate, "", meta);
    }

    default EtlGateJobApi load(String gate, DataSource data) {
        return load(gate, data, emptyMap());
    }

    default EtlGateJobApi load(String gate, byte[] data, Map<String, String> meta) {
        return load(gate, newDataSource(data), meta);
    }

    default EtlGateJobApi load(String gate, byte[] data) {
        return load(gate, newDataSource(data));
    }

    default EtlGateJobApi load(String gate, String data) {
        return load(gate, data, emptyMap());
    }

    default EtlGateJobApi load(String gate, String data, Map<String, String> meta) {
        if (isUrl(data)) {
            return load(gate, newDataSourceFromUrl(data), meta);
        } else {
            return load(gate, newDataSource(data), meta);
        }
    }

}
