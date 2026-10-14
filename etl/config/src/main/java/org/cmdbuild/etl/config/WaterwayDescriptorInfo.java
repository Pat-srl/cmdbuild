/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config;

import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.buildDescriptorFilename;

public interface WaterwayDescriptorInfo {

    String getCode();

    String getDescription();

    String getNotes();

    String getTag();

    default String getFileName() {
        return buildDescriptorFilename(getCode());
    }
}
