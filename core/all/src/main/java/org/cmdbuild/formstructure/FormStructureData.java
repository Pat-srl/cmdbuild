/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.formstructure;

import jakarta.annotation.Nullable;

public interface FormStructureData {

    @Nullable
    Long getId();

    String getCode();

    String getData();
}
