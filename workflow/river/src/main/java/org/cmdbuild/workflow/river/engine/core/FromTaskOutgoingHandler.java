package org.cmdbuild.workflow.river.engine.core;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import org.cmdbuild.workflow.river.engine.RiverTaskCompleted;
import org.cmdbuild.workflow.river.engine.core.Step.OutgoingHandler;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.NEXT_FLAGS_TO_ACTIVATE_SCRIPT_VAR;

public class FromTaskOutgoingHandler implements OutgoingHandler {

    private final Collection<String> allOutgoingFlags;

    public FromTaskOutgoingHandler(Collection<String> allOutgoingFlags) {
        this.allOutgoingFlags = allOutgoingFlags;
    }

    @Override
    public Collection<String> getAllOutgoingStepTransitionIds() {
        return allOutgoingFlags;
    }

    @Override
    public Collection<String> getOutgoingStepTransitionIdsForTask(RiverTaskCompleted completedTask) {
        return ImmutableSet.copyOf((Collection<String>) completedTask.getLocalVariables().get(NEXT_FLAGS_TO_ACTIVATE_SCRIPT_VAR));
    }

}
