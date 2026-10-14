/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.core;

import java.util.Collection;
import static java.util.Collections.emptyList;
import org.cmdbuild.workflow.river.engine.RiverTaskCompleted;
import org.cmdbuild.workflow.river.engine.core.Step.OutgoingHandler;

/**
 *
 * @author davide
 */
public class EmptyOutgoingHandler implements OutgoingHandler {

    private final static EmptyOutgoingHandler INSTANCE = new EmptyOutgoingHandler();

    @Override
    public Collection<String> getOutgoingStepTransitionIdsForTask(RiverTaskCompleted completedTask) {
        return emptyList();
    }

    @Override
    public Collection<String> getAllOutgoingStepTransitionIds() {
        return emptyList();
    }

    public static OutgoingHandler goingNowere() {
        return INSTANCE;
    }

}
