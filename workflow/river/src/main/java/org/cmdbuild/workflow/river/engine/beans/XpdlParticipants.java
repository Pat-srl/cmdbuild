/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.beans;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import jakarta.annotation.Nullable;

public class XpdlParticipants {

    private final Map<String, XpdlParticipant> xpdlParticipants;

    public XpdlParticipants(Map<String, XpdlParticipant> xpdlParticipants) {
        this.xpdlParticipants = ImmutableMap.copyOf(xpdlParticipants);
    }

    @Nullable
    public XpdlParticipant getXpdlParticipant(String name) {
        return xpdlParticipants.get(name);
    }
}
