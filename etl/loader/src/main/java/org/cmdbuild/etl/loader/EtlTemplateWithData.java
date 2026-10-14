/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader;

import jakarta.annotation.Nullable;

public interface EtlTemplateWithData {

    EtlTemplate getTemplate();

    Object getData();

    @Nullable
    Object getCallback();

}
