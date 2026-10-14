/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.formstructure;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class FormStructureImpl implements FormStructure {

    private final String data;

    public FormStructureImpl(String data) {
        this.data = checkNotBlank(data);
    }

    @Override
    public String getData() {
        return data;
    }

}
