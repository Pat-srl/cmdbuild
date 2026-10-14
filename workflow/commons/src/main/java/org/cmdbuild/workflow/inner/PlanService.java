package org.cmdbuild.workflow.inner;

import com.google.common.eventbus.EventBus;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.dao.entrytype.Classe;

public interface PlanService {

    @Nullable
    String getPlanIdOrNull(Classe classe);

    @Nullable
    String getClassNameOrNull(String planId);

    List<TaskDefinition> getEntryTasks(String planId);

    List<TaskDefinition> getAllTasks(String planId);

    boolean hasPlanId(String suggestedPlanId);

    EventBus getEventBus();

}
