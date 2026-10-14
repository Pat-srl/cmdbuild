package org.cmdbuild.workflow.inner;


import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;

import org.cmdbuild.classe.access.UserCardQueryOptions;


public interface FlowCardRepository {

    Flow create(Flow flow);

    Flow update(Flow flow);

    Flow getFlowCardByPlanIdAndFlowId(String planId, String flowId);

    Flow getFlowCard(Flow processInstance);

    Flow getFlowCardByPlanAndCardId(Process processClass, Long cardId);

    Flow getFlowCardByClasseIdAndCardId(String classeName, Long cardId);

    Iterable<? extends Flow> queryOpenAndSuspended(Process processClass);

    PagedElements<Flow> getUserFlows(String classId, UserCardQueryOptions cardQueryOptions);

    PagedElements<Flow> getUserCardsByClassIdAndQueryOptions(String classId, DaoQueryOptions queryOptions);

    Flow getUserFlowCard(String classId, long cardId);

    boolean userCanReadCard(String classId, long cardId);

}
