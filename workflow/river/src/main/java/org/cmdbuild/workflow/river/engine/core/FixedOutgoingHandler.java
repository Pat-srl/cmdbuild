package org.cmdbuild.workflow.river.engine.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.copyOf;
import java.util.Collection;
import java.util.List;
import org.cmdbuild.workflow.river.engine.RiverTaskCompleted;
import org.cmdbuild.workflow.river.engine.core.Step.OutgoingHandler;

public class FixedOutgoingHandler implements OutgoingHandler {

    private final List<String> outgoingFlags;

    public FixedOutgoingHandler(Collection<String> outgoingFlags) {
        this.outgoingFlags = copyOf(checkNotNull(outgoingFlags));
    }

    @Override
    public List<String> getOutgoingStepTransitionIdsForTask(RiverTaskCompleted completedTask) {
        return outgoingFlags;
    }

    public List<String> getOutgoingStepTransitionIds() {
        return outgoingFlags;
    }

    @Override
    public Collection<String> getAllOutgoingStepTransitionIds() {
        return outgoingFlags;
    }

}
