/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader.data;

import jakarta.annotation.Nullable;
import org.cmdbuild.etl.loader.EtlTemplateConfig;

public interface EtlTemplateData {

    @Nullable
    Long getId();

    String getCode();

    String getDescription();

    boolean isActive();

    EtlTemplateConfig getConfig();
}
