/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config.inner;

import jakarta.annotation.Nullable;
import org.cmdbuild.etl.config.WaterwayDescriptorInfoExt;

public interface WaterwayDescriptorRecord extends WaterwayDescriptorInfoExt {

    @Nullable
    Long getId();

    String getData();

}
