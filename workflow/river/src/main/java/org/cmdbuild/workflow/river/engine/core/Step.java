package org.cmdbuild.workflow.river.engine.core;

import java.util.Collection;
import org.cmdbuild.workflow.river.engine.RiverTask;
import org.cmdbuild.workflow.river.engine.RiverTaskCompleted;

public interface Step {

    String getId();

    IncomingHandler getIncomingHandler();

    RiverTask getTask();

    OutgoingHandler getOutgoingHandler();

    default Collection<String> getOutgoingStepTransitionIds(RiverTaskCompleted completedTask) {
        return getOutgoingHandler().getOutgoingStepTransitionIdsForTask(completedTask);
    }

    enum IncomingHandler {

        ACTIVATE_WHEN_ANY_INCOMING_STEP_HAVE_COMPLETED, ACTIVATE_WHEN_ALL_INCOMING_STEPS_HAVE_COMPLETED
    }

    interface OutgoingHandler {

        Collection<String> getOutgoingStepTransitionIdsForTask(RiverTaskCompleted completedTask);

        Collection<String> getAllOutgoingStepTransitionIds();
    }

}
