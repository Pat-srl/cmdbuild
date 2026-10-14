/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config.utils;

import java.util.Map;
import org.cmdbuild.etl.config.WaterwayDescriptorMeta;

public interface DescriptorDataAndMeta {

    WaterwayDescriptorMeta getMeta();

    String getData();

    default Map<String, String> getParams() {
        return getMeta().getParams();
    }
}
