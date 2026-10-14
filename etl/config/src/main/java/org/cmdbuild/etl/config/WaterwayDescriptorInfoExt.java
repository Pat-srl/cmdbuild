/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config;

import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.buildDescriptorKey;

public interface WaterwayDescriptorInfoExt extends WaterwayDescriptorInfo, WaterwayDescriptorMeta {

    int getVersion();

    boolean isValid();

    default String getKey() {
        return buildDescriptorKey(getCode(), getVersion());
    }
}
