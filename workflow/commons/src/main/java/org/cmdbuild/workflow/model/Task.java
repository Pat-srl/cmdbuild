package org.cmdbuild.workflow.model;

import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.widget.model.Widget;

public interface Task {

    String getId();

    String getFlowId();

    Flow getProcessInstance();

    TaskDefinition getDefinition();

    String getPerformerName();

    @Nullable
    Object getDescriptionValue();

    @Nullable
    Object getActivitySubsetId();

    /**
     * Returns the activity widgets for this process instance, with expansion of
     * "server" variables.
     *
     * @return ordered list of widgets for this activity instance
     * @
     */
    List<Widget> getWidgets();

    boolean isWritable();

}
