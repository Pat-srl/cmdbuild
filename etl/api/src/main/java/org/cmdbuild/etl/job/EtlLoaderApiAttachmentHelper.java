/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.job;

import java.util.Map;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public interface EtlLoaderApiAttachmentHelper {

    EtlLoaderApiAttachmentHelper withData(Object data);

    EtlLoaderApiAttachmentHelper withMeta(String key, String value);

    default EtlLoaderApiAttachmentHelper withMeta(String... meta) {
        return this.withMeta(map(meta));
    }

    default EtlLoaderApiAttachmentHelper withMeta(Map<String, String> meta) {
        meta.forEach(this::withMeta);
        return this;
    }

}
