/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.beans;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class XpdlParticipant {

    private final String name, type;

    public XpdlParticipant(String name, String type) {
        this.name = checkNotBlank(name);
        this.type = checkNotBlank(type);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

}
