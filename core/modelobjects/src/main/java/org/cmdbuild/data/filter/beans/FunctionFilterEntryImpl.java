/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter.beans;

import org.cmdbuild.data.filter.FunctionFilterEntry;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class FunctionFilterEntryImpl implements FunctionFilterEntry {

    private final String name;

    public FunctionFilterEntryImpl(String name) {
        this.name = checkNotBlank(name);
    }

    @Override
    public String getName() {
        return name;
    }

}
